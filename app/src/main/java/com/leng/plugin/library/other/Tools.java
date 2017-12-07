package com.leng.plugin.library.other;

import com.leng.plugin.library.log.LogUtil;

import java.io.Closeable;

/**
 * Created by carry on 2017/12/7.
 */

public class Tools {

    private static final String TAG = "[Tools]";

    public static void close(Closeable close){
        try {
            close.close();
        }catch (Exception e){
            LogUtil.w(TAG,"close failed!!!",e);
        }
    }

}
