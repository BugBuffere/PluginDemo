package com.leng.plugin.library.plugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.leng.plugin.library.log.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by carry on 2017/12/5.
 */

public class IActivityManagerHandler implements InvocationHandler {

    private static final String TAG = "IActivityManagerHandler";

    private Object mBase;

    private Context context;

    private Class<? extends Activity> stubActivity;

    private String targetIntent;

    public IActivityManagerHandler(Object base, Context context){
        mBase = base;
        this.context = context;
    }

    public void setStubActivity(Class< ? extends Activity> stubActivity){
        this.stubActivity = stubActivity;
    }

    public void setTargetIntent(String targetIntent){
        this.targetIntent = targetIntent;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        //LogUtil.d(TAG,"invoke object::" + o + " method::" + method.getName());
        if ("startActivity".equals(method.getName()) && this.stubActivity != null
                && !TextUtils.isEmpty(this.targetIntent)){
            //只拦截这个方法 替换参数，任你所为；甚至替换原始Activity启动别的Activity偷梁换柱
            //API 23:
            /*
                public final Activity startActivityNow(Activity parent, String id,
                Intent intent, ActivityInfo activityInfo, IBinder token, Bundle state,
                Activity.NonConfigurationInstances lastNonConfigurationInstances)
             */
            //找到参数里面的第一个Intent对象
            LogUtil.d(TAG,"invoke object::" + o + " method::" + method.getName());
            Intent raw = null;
            int index = 0;
            for (int i = 0; i < objects.length;i++){
                LogUtil.d(TAG,"invoke objects " + i + " " + objects[i]);
                if (objects[i] instanceof Intent && raw == null) {
                    raw = (Intent) objects[i];
                    index = i;
                }
            }

            Intent newIntent = new Intent();

            //替身Activity的包名，也就是我们自己的包名
            String stubPackage = context.getPackageName();

            //这里我们把启动的Activity临时替换为StubActivity
            ComponentName componentName = new ComponentName(stubPackage,stubActivity.getName());
            newIntent.setComponent(componentName);

            //把我们原始要启动的TargetActivity先存起来
            newIntent.putExtra(targetIntent,raw);

            //替换掉Intent,达到欺骗AMS的目的
            objects[index] = newIntent;
            LogUtil.d(TAG,"invoke hook success");
            stubActivity = null;
            targetIntent = null;
            return method.invoke(mBase,objects);
        }
        return method.invoke(mBase,objects);
    }

}
