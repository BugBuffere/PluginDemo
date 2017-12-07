package com.leng.plugin.library.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.util.Log;

public class WriteLog {
	
	private static long logFileSize = 5 * 1024 * 1024;
	
	private static PrintStream ps;
	
	private static File currentLogFile;
	
	private static boolean isCreateNewStream;
	/**
	 * 写log到文件
	 * @param log
	 * @param thr
	 */
	public static void write(String log,Throwable thr){
		getOutputStream();
		if (ps != null) {
			ps.println(log);
			if (thr != null) {
				thr.printStackTrace(ps);
			}
		}
	}
	/**
	 * 获取log输出流
	 */
	private static void getOutputStream(){
		checkLogFile();
		if (ps == null || isCreateNewStream) {
			try {
				ps = new PrintStream(new FileOutputStream(currentLogFile,true),true);
			} catch (Exception e) {
				Log.e("Leng", "初始LogUtils失败!!!", e);
			}
		}
	}
	/**
	 * 检查log文件是否存在
	 */
	private static void checkLogFile(){
		isCreateNewStream = false;
		if (currentLogFile == null) {
			File logDir = new File(Config.DEFAULT_LOG_PATH);
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
			currentLogFile = new File(logDir, "log_all");
			return;
		}
		if (currentLogFile.length() >= logFileSize) {
			currentLogFile = createNewLogFile();
			isCreateNewStream = true;
		}
	}
	/**
	 * 创建新的log文件
	 * @return
	 */
	private static File createNewLogFile(){
		File logDir = new File(Config.DEFAULT_LOG_PATH);
		File logFile = null;
		for (File file : logDir.listFiles()) {
			if (file.isFile()) {
				String fileName = file.getName();
				Log.d("Leng", "fileName::" + fileName);
				if (fileName.startsWith("log_all_")) {
                    int count = Integer.parseInt(fileName.substring(fileName.lastIndexOf("_") + 1));
                    if (count >= 10) {
						file.delete();//删除过旧的文件
						continue;
					}
                    fileName = fileName.substring(0, fileName.lastIndexOf("_") + 1) + ++count;
                    //修改文件名
                    file.renameTo(new File(Config.DEFAULT_LOG_PATH+ "/" + fileName));
                }else if (fileName.equals("log_all")) {
                	 fileName += "_1";
                     file.renameTo(new File(Config.DEFAULT_LOG_PATH + "/" + fileName));
                     logFile = new File(logDir,"log_all");
                }
			}
		}
		return logFile;
	}

}
