package com.leng.plugin.library.other;



import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.leng.plugin.library.log.LogUtil;


public class VersionUtils {
	
	private static final String TAG = "[version-util]";
	
	private static int versionCode = -1203;
	
	private static String versionName = null;
	
	public static String getVersionName(Context context){
		if (versionName == null) {
			versionName = getVersionName(context, context.getPackageName());
		}
		return versionName;
	}
	
	public static int getVersionCode(Context context){
		if (versionCode == -1203) {
			versionCode = getVersionCode(context, context.getPackageName());
		}
		return versionCode;
	}

	private static int getVersionCode(Context context,String packageName){
		int versionCode = -1203;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
			versionCode = info.versionCode;
		} catch (Exception e) {
			LogUtil.w(TAG, "get " + packageName + " versionCode failed!!" , e);
		}
		return versionCode;
	}
	
	private static String getVersionName(Context context,String packageName){
		String versionName = "error";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
			versionName = info.versionName;
		} catch (Exception e) {
			LogUtil.w(TAG, "get " + packageName + " versionName failed!!" , e);
		}
		return versionName;
	}

}
