package com.leng.plugin.library.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by Admin on 2017/9/9.
 */

@SuppressLint("SimpleDateFormat")
public class LogUtil {

    private static int currentLevel = Log.VERBOSE;

    private static Object mLock = new Object();

    private static String TAG = "Leng";

    private static SimpleDateFormat sdf;

    private static Date mDate;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        mDate = new Date();
    }

    public static void setLevel(int level){
        currentLevel = level;
    }

    public static void v(String tag,String msg){
        v(tag,msg,null);
    }

    public static void v(String tag,String msg,Throwable thr){
        msg = tag + " " + msg;
        _log("V",msg,thr);
    }

    public static void d(String tag,String msg){
        d(tag,msg,null);
    }

    public static void d(String tag,String msg,Throwable thr){
        msg = tag + " " + msg;
        _log("D",msg,thr);
    }

    public static void i(String tag,String msg){
        i(tag,msg,null);
    }

    public static void i(String tag,String msg,Throwable thr){
        msg = tag + " " + msg;
        _log("I",msg,thr);
    }

    public static void w(String tag,String msg){
        w(tag,msg,null);
    }

    public static void w(String tag,String msg,Throwable thr){
        msg = tag + " " + msg;
        _log("W",msg,thr);
    }

    public static void e(String tag,String msg){
        e(tag,msg,null);
    }

    public static void e(String tag,String msg,Throwable thr){
        msg = tag + " " + msg;
        _log("E",msg,thr);
    }

    /**
     * 打印log到文件
     * @param level
     * @param msg
     * @param thr
     */
    private static void _log(String level,String msg,Throwable thr){
        if (currentLevel < Log.ASSERT  || Config.IS_OPEN_LOG) {
            synchronized (mLock) {
                mDate.setTime(System.currentTimeMillis());
                String log = new StringBuilder()
                        .append(level).append("/")
                        .append(sdf.format(mDate))
                        .append(" ").append(TAG)
                        .append(" ").append(msg).toString();
                LogMsg.obtain(log,thr).sendToTarget();
                println(level,msg,thr);
            }
        }
    }
    /**
     * 打印log到控制台
     * @param level
     * @param msg
     * @param thr
     */
    private static void println(String level,String msg,Throwable thr){
        int priority = Log.VERBOSE;
        if ("V".equals(level)) {
            priority = Log.VERBOSE;
        }else if ("D".equals(level)){
            priority = Log.DEBUG;
        }else if ("I".equals(level)) {
            priority = Log.INFO;
        }else if ("W".equals(level)) {
            priority = Log.WARN;
        }else if ("E".equals(level)) {
            priority = Log.ERROR;
        }
        if (thr != null) {
            msg = msg + "\n" + Log.getStackTraceString(thr);
        }
        Log.println(priority,TAG,msg);
    }

}
