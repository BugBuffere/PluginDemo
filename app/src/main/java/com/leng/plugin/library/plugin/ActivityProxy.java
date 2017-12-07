package com.leng.plugin.library.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.leng.plugin.library.log.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by carry on 2017/12/5.
 */

public class ActivityProxy {

    private static final String TAG = "ActivityProxy";

    private static IActivityManagerHandler handler;

    private static ActivityThreadHandlerCallback callback;

    public static void startAcivity(Context context, Intent startAcivity, Class<? extends Activity> stubActivity,String targetIntent){
        if (handler != null && callback != null && context != null
                && startAcivity != null && startAcivity != null
                && !TextUtils.isEmpty(targetIntent)) {
            handler.setStubActivity(stubActivity);
            handler.setTargetIntent(targetIntent);
            callback.setTargetIntent(targetIntent);
            context.startActivity(startAcivity);
            LogUtil.d(TAG,"startAcivity::" + startAcivity.getComponent().getClassName()
                    + " stubActvity::" + stubActivity.getClass().getSimpleName()
                    + " targetIntent::" + targetIntent);
        }
    }

    public static void hook(Context context){
        if (context == null){
            throw new RuntimeException("The parameters can not be null!!!");
        }
        try {
            hookActivityManagerNative(context);
            hookActivityThreadHandler();
        } catch (Exception e) {
            LogUtil.w(TAG,"",e);
        }
    }

    private static void hookActivityManagerNative(Context context) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
        Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true);

        Object gDefault = gDefaultField.get(null);

        //gDefault是一个android.util.Singleton对象；我们取出这个单例里面的字段
        Class<?> singleton = Class.forName("android.util.Singleton");
        Field mInstanceField = singleton.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        //ActvityManagerNative 的gDefault对象里面原始的IActivityManager对象
        Object rawIActivityManager = mInstanceField.get(gDefault);

        //创建一个这个对象的代理对象，然后替换这个字段，让我们的代理对象帮忙干活
        Class<?> iActvityManagerInterface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActvityManagerInterface},
                handler = new IActivityManagerHandler(rawIActivityManager,context));
        mInstanceField.set(gDefault,proxy);
    }

    private static void hookActivityThreadHandler() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        //先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.setAccessible(true);
        Object currentActivityThread = currentActivityThreadField.get(null);

        //由于ActivityThread一个进程只有一个，我们获取这个对象的mH
        Field mHField = activityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mH = (Handler) mHField.get(currentActivityThread);
        //设置它的回调，根据源码:我们自己给他设置一个回调，就会替代之前的回调

        Field mCallBackField = Handler.class.getDeclaredField("mCallback");
        mCallBackField.setAccessible(true);

        mCallBackField.set(mH,callback = new ActivityThreadHandlerCallback(mH));

    }

}
