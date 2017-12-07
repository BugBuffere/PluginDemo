package com.leng.plugin.library.loader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by carry on 2017/12/5.
 */

public class AndroidDexClassLoader extends DexClassLoader {

    /**
     *
     * @param dexPath 需要装载的APK或者Jar文件的路径。包含多个路径用File.pathSeparator间隔开，在Android上默认是":"
     * @param optimizedDirectory 优化后的dex文件放目录，不能为null
     * @param librarySearchPath 目标类中使用的C/C++库的列表，每个目录用File.pathSeparator间隔开；可以为null
     * @param parent 该类装载器的父装载器，一般用当前执行类的装载器
     */
    public AndroidDexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    public static AndroidDexClassLoader loaderApkByPackageName(Context context,String packageName){
        //创建一个意图，用来找到指定的apk
        Intent intent = new Intent(packageName,null);
        //获得包管理器
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,0);
        //获得指定的activity的信息
        ActivityInfo activityInfo = resolveInfos.get(0).activityInfo;
        //获得包名
        String packagename = activityInfo.packageName;
        //获得apk的目录或者jar的目录
        String apkPath = activityInfo.applicationInfo.sourceDir;
        //dex解压后的目录，注意，这个用宿主程序的目录，android中只允许程序读写自己目录下的文件
        String dexOutputDir = context.getApplicationInfo().dataDir;
        //native代码的目录
        String libPath = activityInfo.applicationInfo.nativeLibraryDir;
        //创建类加载器，把dex加载到虚拟机中
        AndroidDexClassLoader classLoader = new AndroidDexClassLoader(apkPath,dexOutputDir,libPath,
                AndroidDexClassLoader.class.getClassLoader());
        return classLoader;
    }

    public static AndroidDexClassLoader loaderApkByPath(Context context,String apkPath){
        AndroidDexClassLoader classLoader = new AndroidDexClassLoader(apkPath,getDexOutputDir(context).getAbsolutePath(),null,
                AndroidDexClassLoader.class.getClassLoader());
        return classLoader;
    }

    public static File getDexOutputDir(Context context){
        return context.getDir("dex",0);
    }

}
