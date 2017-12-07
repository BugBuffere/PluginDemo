package com.leng.plugin;

import android.app.Application;
import android.content.Context;

import com.leng.plugin.library.crash.CrashHandler;
import com.leng.plugin.library.log.LogUtil;
import com.leng.plugin.library.other.VersionUtils;
import com.leng.plugin.library.plugin.ActivityProxy;

/**
 * Created by carry on 2017/12/5.
 */

public class MyApplication extends Application {

    private static final String TAG = "[MyApplication]";

    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG,"VersionInfo::" + VersionUtils.getVersionName(this));
        CrashHandler.getInstance().init(this);
        //开启hook
        ActivityProxy.hook(this);
        application = this;
    }

    public static Application application(){
        return application;
    }

}
