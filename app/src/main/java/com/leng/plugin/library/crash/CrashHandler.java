package com.leng.plugin.library.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.leng.plugin.library.log.LogUtil;
import com.leng.plugin.library.other.VersionUtils;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressLint("SimpleDateFormat")
public class CrashHandler implements UncaughtExceptionHandler {
	
	private static final String TAG = "[crashHanlder]";
	//系统默认的UncaughtException处理类
	private UncaughtExceptionHandler mDefaultHandler;
	//CrachHandler实例
	private static CrashHandler instance = new CrashHandler();
	//程序的Context对象
	private Context context;
	//用来储存设备信息和异常信息

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	
	private String defPath = Environment.getExternalStorageDirectory().getPath() + "/Leng/report";
	
	private CrashHandler(){}
	
	public static CrashHandler getInstance(){
		return instance;
	}
	
	public void init(Context context){
		this.context = context;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	/**
	 * 当UncaughtException发生时会转入该函数处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			Log.e(TAG, "测试");
			mDefaultHandler.uncaughtException(thread, ex);
		}else{
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Process.killProcess(Process.myPid());
			System.exit(0);
		}
	}
	/**
	 * 自定义错误处理，收集错误信息，发生错误报告等操作
	 * @param ex
	 * @return true：如果处理了该异常信息，
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		//Toast提示程序停止运行
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, "很抱歉,程序出现异常,即将退出.",Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}).start();
		LogUtil.e(TAG, "crash::", ex);
		saveCrashInfo2File(ex);
		return true;
	}
	/**
	 * 保存异常信息
	 * @param ex
	 */
	private void saveCrashInfo2File(Throwable ex) {
		try {
			long milliseconds = System.currentTimeMillis();
			String time = format.format(new Date(milliseconds ));
			File dir = new File(defPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}else{
				clearOldCrash(dir);
			}
			PrintWriter pw = new PrintWriter(new File(dir,"crash_" + context.getPackageName() + "_" + time + ".txt"));
			pw.println(time);
			//手机手机信息
			savePoneInfo(pw);
			ex.printStackTrace(pw);
			pw.close();
		} catch (Exception e) {
			LogUtil.e(TAG,"error::",e);
		}
	}
	/**
	 * 清除旧的日志
	 * @param dir
	 */
	private void clearOldCrash(File dir) {
		File[] listFiles = dir.listFiles();
		if (listFiles != null && listFiles.length >= 50) {
			for (int i = 0; i < listFiles.length; i++) {
				File f = listFiles[i];
				f.delete();
			}
		}
	}

	/**
	 * 保存设备信息
	 * @param pw
	 */
	@SuppressWarnings("deprecation")
	private void savePoneInfo(PrintWriter pw) {
		pw.print("App Name: ");
		pw.println(context.getPackageName());
		pw.print("App Version: ");
		pw.println(VersionUtils.getVersionName(context)+"-"+ VersionUtils.getVersionCode(context));
		//android系统版本信息
		pw.print("Android Version: ");
		pw.println(Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT);
		//手机制造商
		pw.print("Vendor: ");
		pw.println(Build.MANUFACTURER);
		// 手机型号
		pw.print("Model: ");
		pw.println(Build.MODEL);
		// cpu架构
		pw.print("CPU ABI: ");
		pw.println(Build.CPU_ABI);
	}

}
