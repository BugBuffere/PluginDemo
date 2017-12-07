package com.leng.plugin.library.other;

import com.leng.plugin.library.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	private static final String TAG = "[zip]";
	//压缩文件
	public static void zip(String zipFileName,String... sourceFileName){
		LogUtil.d(TAG, "压缩中……");
		try {
			//创建zip输出流
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(zipFileName)));
			//创建缓冲区输出流
			BufferedOutputStream bos = new BufferedOutputStream(out);
			for (String string : sourceFileName) {
				File sourceFile = new File(string);
				//调用函数
				compress(out,bos,sourceFile,sourceFile.getName());
			}
			bos.close();
			out.close();
			LogUtil.d(TAG, "压缩完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//压缩文件
	public static void zip(File zipFile,File... sourceFiles){
		LogUtil.d(TAG, "压缩中……");
		try {
			//创建zip输出流
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
			//创建缓冲区输出流
			BufferedOutputStream bos = new BufferedOutputStream(out);
			for (File sourceFile : sourceFiles) {
				//调用函数
				compress(out,bos,sourceFile,sourceFile.getName());
			}
			bos.close();
			out.close();
			LogUtil.d(TAG, "压缩完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void compress(ZipOutputStream out, BufferedOutputStream bos,
			File sourceFile, String name) throws Exception {
		//如果路径为目录（文件夹）
		if (sourceFile.isDirectory()) {
			File[] fileList = sourceFile.listFiles();
			if (fileList.length == 0) {
				//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
				LogUtil.d(TAG, "zip dir name::" + name);
				out.putNextEntry(new ZipEntry(name + "/"));
			}else{
				//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
				for (File file : fileList) {
					compress(out, bos, file, name + "/" + file.getName());
				}
			}
		}else{
			//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
			out.putNextEntry(new ZipEntry(name));
			LogUtil.d(TAG, "zip name::" + name);
			FileInputStream fis = new FileInputStream(sourceFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
				bos.flush();
			}
			bis.close();
			fis.close();
		}
	}

	public static void unzip(String zipPath,String targetPath) throws IOException {
		File file = new File(zipPath);
		File target = new File(targetPath);
		unzip(file,target);
	}

	public static void unzip(File zipFile,File targetFile) throws IOException {
		if (!zipFile.isFile()) {
			throw new FileNotFoundException("file not exist!!");
		}
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> files = zip.entries();
		File outFile = null;
		ZipEntry entry = null;
		BufferedInputStream in = null;
		BufferedOutputStream os = null;
		while (files.hasMoreElements()) {
			entry = files.nextElement();
			outFile = new File(targetFile,entry.getName());
			//如果条目为目录，则跳向下一个
			if (entry.isDirectory()){
				outFile.mkdirs();
				continue;
			}
			//创建目录
			if (!outFile.getParentFile().exists()){
				outFile.getParentFile().mkdirs();
			}
			//创建新文件
			outFile.createNewFile();
			//如果不可写，则跳向下一个条目
			if (!outFile.canWrite()){
				continue;
			}
			try {
				in = new BufferedInputStream(zip.getInputStream(entry));
				os = new BufferedOutputStream(new FileOutputStream(outFile));
				byte[] buffer = new byte[1024];
				int readCount = -1;
				while ((readCount = in.read(buffer)) != -1) {
					os.write(buffer,0,readCount);
				}
			}catch (Exception e) {
				LogUtil.w(TAG,"unzip file::" + zipFile.getAbsolutePath() + " failed!!",e);
			}finally {
				Tools.close(in);
				Tools.close(os);
				in = null;
				os = null;
			}
		}
	}

}
