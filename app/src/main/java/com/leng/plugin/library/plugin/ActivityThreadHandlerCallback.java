package com.leng.plugin.library.plugin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.leng.plugin.library.log.LogUtil;

import java.lang.reflect.Field;

/**
 * Created by carry on 2017/12/5.
 */

public class ActivityThreadHandlerCallback implements Handler.Callback {

    private static final String TAG = "ActivityThreadHandlerCallback";

    private Handler mBase;

    private String targetIntent;

    public ActivityThreadHandlerCallback(Handler base){
        mBase = base;
    }

    public void setTargetIntent(String targetIntent){
        this.targetIntent = targetIntent;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case 100:
                if (!TextUtils.isEmpty(targetIntent)) {
                    handleLauchActivity(message);
                }
                break;
        }
        mBase.handleMessage(message);
        return true;
    }

    private void handleLauchActivity(Message message) {
        Object obj = message.obj;
        try {
            //把替身恢复成真身
            Field intent = obj.getClass().getDeclaredField("intent");
            intent.setAccessible(true);
            Intent raw = (Intent) intent.get(obj);

            Intent targetIntent = raw.getParcelableExtra(this.targetIntent);
            raw.setComponent(targetIntent.getComponent());
            this.targetIntent = null;
        }catch (Exception e){
            LogUtil.w(TAG,"",e);
        }
    }


}
