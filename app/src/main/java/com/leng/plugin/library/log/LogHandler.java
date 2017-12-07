package com.leng.plugin.library.log;

import android.os.Message;

;import com.leng.plugin.library.handler.BaseHandler;

/**
 * Created by Admin on 2017/9/9.
 */

public class LogHandler extends BaseHandler {

    private static LogHandler mHandler;

    private LogHandler() {
        super("log-handler");
    }

    public static LogHandler getInstance() {
        if (mHandler == null) {
            synchronized (LogHandler.class) {
                if (mHandler == null) {
                    mHandler = new LogHandler();
                }
            }
        }
        return mHandler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        LogMsg lm = (LogMsg) msg.obj;
        String log = lm.log;
        Throwable thr = lm.thr;
        WriteLog.write(log, thr);
        lm.log = null;
        lm.thr = null;
        lm.insert();
        return false;
    }
}
