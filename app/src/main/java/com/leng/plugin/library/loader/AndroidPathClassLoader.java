package com.leng.plugin.library.loader;

import dalvik.system.PathClassLoader;

/**
 * Created by carry on 2017/12/5.
 */

public class AndroidPathClassLoader extends PathClassLoader {

    /**
     *
     * @param dexPath 文件或者目录的列表
     * @param parent 父类加载器
     */
    public AndroidPathClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, parent);
    }

    /**
     *
     * @param dexPath 文件或者目录的列表
     * @param librarySearchPath 包含lib库的目录列表
     * @param parent 父类加载器
     */
    public AndroidPathClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }

}
