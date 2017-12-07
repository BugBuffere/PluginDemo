package com.leng.plugin.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leng.plugin.MyApplication;
import com.leng.plugin.R;
import com.leng.plugin.activity.stub.StubStandardUnspecifiedActivity1;
import com.leng.plugin.library.loader.ClassLoaderApk;
import com.leng.plugin.library.loader.LoaderApk;
import com.leng.plugin.library.log.LogUtil;
import com.leng.plugin.library.plugin.ActivityProxy;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "[main-activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(TAG,"startActivity");
                LoaderApk loaderApk = new LoaderApk(MyApplication.application(),"com.leng.demo");
                ActivityInfo[] mainActivityInfo = loaderApk.getMainActivityInfo();
                LogUtil.d(TAG,"mainActivityInfo::" + mainActivityInfo);
                if (mainActivityInfo != null) {
                    for (ActivityInfo info: mainActivityInfo) {
                        LogUtil.d(TAG,"activityName::" + info);
                        if (info.name.contains("MainActivity")){
                            ClassLoaderApk classLoaderApk = loaderApk.getClassLoader();
                            Class<?> cls = classLoaderApk.getClass(info.name);
                            LogUtil.d(TAG,"class::" + cls.getName());
                            Intent intent = new Intent(MainActivity.this,cls);
                            //ActivityProxy.startAcivity(MainActivity.this,intent, StubStandardUnspecifiedActivity1.class,"test");
                        }
                    }
                }
            }
        });
    }
}
