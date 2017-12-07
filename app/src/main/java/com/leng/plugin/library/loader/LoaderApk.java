package com.leng.plugin.library.loader;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.leng.plugin.library.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carry on 2017/12/6.
 */

public class LoaderApk {

    private static final String TAG = "[LoaderApk]";

    private String apk;

    private String versionName;

    private int versionCode;

    private String packageName;

    private ActivityInfo[] mainActivityInfo;

    private ActivityInfo[] activityInfos;

    private ActivityInfo[] receiverInfos;

    private ServiceInfo[] serviceInfos;

    private ProviderInfo[] providerInfos;

    private PermissionInfo[] permissionInfos;

    private String[] requestedPermissions;

    private int[] requestedPermissionsFlags;

    private ApplicationInfo applicationInfo;

    private Drawable icon;

    private ClassLoaderApk classLoader;

    /**
     * 可以是apk路径或者apk包名
     * @param apk
     */
    public LoaderApk(Application application,String apk) {
        this.apk = apk;
        if (TextUtils.isEmpty(this.apk)) {
            throw new RuntimeException("The parameters can not be null!!!");
        }
        loader(application);
    }

    /**
     * 获取apk基本信息
     */
    private void loader(Application context){
        if (TextUtils.isEmpty(versionName) || versionCode == 0
                || activityInfos == null || applicationInfo == null || receiverInfos == null
                || serviceInfos == null || providerInfos == null || permissionInfos == null
                || icon == null || requestedPermissions == null || requestedPermissionsFlags == null) {
            LogUtil.d(TAG,"loader apk " + apk);
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = null;
            if (apk.contains(".")){
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                for (PackageInfo info : packageInfos) {
                    if (apk.equals(info.packageName)) {
                        packageInfo = info;
                        break;
                    }
                }
                classLoader = new ClassLoaderApk(context,packageInfo.applicationInfo.sourceDir,packageInfo.packageName);
            }else {
                packageInfo = pm.getPackageArchiveInfo(apk,PackageManager.GET_ACTIVITIES);
                classLoader = new ClassLoaderApk(context,apk,packageInfo.packageName);
            }
            LogUtil.d(TAG,"packageInfo::" + packageInfo);
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
                versionCode = packageInfo.versionCode;
                packageName = packageInfo.packageName;
                activityInfos = packageInfo.activities;
                receiverInfos = packageInfo.receivers;
                serviceInfos = packageInfo.services;
                providerInfos = packageInfo.providers;
                permissionInfos = packageInfo.permissions;
                requestedPermissions = packageInfo.requestedPermissions;
                requestedPermissionsFlags = packageInfo.requestedPermissionsFlags;
                applicationInfo = packageInfo.applicationInfo;
                applicationInfo.sourceDir = apk;
                applicationInfo.publicSourceDir = apk;
                icon = applicationInfo.loadIcon(pm);

                Intent intent = new Intent(Intent.ACTION_MAIN,null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,0);
                LogUtil.d(TAG,"resolveInfos::" + resolveInfos);
                List<ActivityInfo> mainActivityList = new ArrayList<ActivityInfo>();
                for (ResolveInfo info: resolveInfos) {
                    LogUtil.d(TAG,"packageName::" + info.activityInfo.packageName
                        +" ----- mainActivity::" + info.activityInfo.name);
                    if (info.activityInfo.packageName.equals(packageName)){
                        mainActivityList.add(info.activityInfo);
                    }
                }
                mainActivityInfo = mainActivityList.toArray(new ActivityInfo[mainActivityList.size()]);
            }
        }
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public ActivityInfo[] getActivityInfos() {
        return activityInfos;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public Drawable getIcon() {
        return icon;
    }

    public ActivityInfo[] getReceiver() {
        return receiverInfos;
    }

    public ServiceInfo[] getService() {
        return serviceInfos;
    }

    public ProviderInfo[] getProvider() {
        return providerInfos;
    }

    public PermissionInfo[] getPermission() {
        return permissionInfos;
    }

    public String[] getRequestedPermissions() {
        return requestedPermissions;
    }

    public int[] getRequestedPermissionsFlags() {
        return requestedPermissionsFlags;
    }

    public ActivityInfo[] getMainActivityInfo() {
        return mainActivityInfo;
    }

    public ClassLoaderApk getClassLoader(){
        return classLoader;
    }

}
