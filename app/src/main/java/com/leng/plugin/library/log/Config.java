package com.leng.plugin.library.log;

import java.io.File;

import android.os.Environment;

/**
 * Created by Admin on 2017/9/9.
 */

public class Config {

    public static final String DEFAULT_PATH = Environment.getExternalStorageDirectory().getPath() + "/Leng";

    public static final String DEFAULT_LOG_PATH = DEFAULT_PATH + "/log";

    public static final boolean IS_OPEN_LOG = exist(DEFAULT_LOG_PATH + "/txz/log_enable_file");

    private static boolean exist(String filePath){
        return new File(filePath).exists();
    }

}
