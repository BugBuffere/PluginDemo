package com.leng.plugin.library.log;

/**
 * Created by Admin on 2017/9/9.
 */

public class LogMsg {

    public String log;

    public Throwable thr;

    private LogMsg nextLog;

    private static final Object sPoolSync = new Object();

    private static LogMsg sPool;

    private static LogMsg obtain(){
        synchronized (sPoolSync) {
            if (sPool != null) {
                LogMsg lm = sPool;
                sPool = lm.nextLog;
                lm.nextLog = null;
                return lm;
            }
        }
        return new LogMsg();
    }

    public static LogMsg obtain(String log,Throwable thr){
        LogMsg lm = obtain();
        lm.log = log;
        lm.thr = thr;
        return lm;
    }

    public void sendToTarget(){
        LogHandler.getInstance().sendMessage(this);
    }

    public void insert(){
        synchronized (sPoolSync) {
            if (sPool == null) {
                sPool = this;
            }else {
                LogMsg prevLm = sPool;
                LogMsg nextLm = null;
                for (;;){
                    nextLm = prevLm.nextLog;
                    if (nextLm == null) {
                        break;
                    }
                    prevLm = nextLm;
                }
                prevLm.nextLog = this;
            }
        }
    }

}
