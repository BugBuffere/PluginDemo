package com.leng.plugin.library.loader;

import java.io.File;
import java.lang.reflect.Method;


import android.app.Application;
import android.text.TextUtils;

import com.leng.plugin.library.log.LogUtil;
import com.leng.plugin.library.other.ZipUtils;

import dalvik.system.DexClassLoader;

public class ClassLoaderJar {
	
	private static final String TAG = ClassLoaderJar.class.getSimpleName();
	
	private DexClassLoader mDex;
	protected Application mApp;
	protected String mPath;
	protected String dexPath;
	
	/**
	 * 加载jar
	 * @param app
	 * @param path
	 */
	public ClassLoaderJar(Application app,String path) {
		this(app,path,null);
	}

	public ClassLoaderJar(Application app,String path,String dexPath){
		mApp = app;
		mPath = path;
		this.dexPath = dexPath;
		loader();
	}

	protected void loader() {
		File resFile = new File(mPath);
		if (resFile.exists()) {
			if(TextUtils.isEmpty(dexPath)){
				dexPath = mApp.getApplicationInfo().dataDir + "/dexfiles";
				File dexFile = new File(dexPath);
				if (!dexFile.exists()) {
					dexFile.mkdir();
				}
				/*try{
					//解压apk文件
					ZipUtils.unzip(mPath,dexPath);
				}catch (Exception e){
					LogUtil.w(TAG,"unzip apk failed!!",e);
				}*/
			}
			mPath = resFile.getAbsolutePath();
			LogUtil.d(TAG,"mPath::" + mPath + " dexPath::" + dexPath);
			mDex = new DexClassLoader(mPath, dexPath, null, this.getClass().getClassLoader());
		}
		LogUtil.d(TAG , " loader....");
	}
	
	public Class<?> getClass(String className){
		if (mDex == null) {
			LogUtil.e(TAG , " classLoader is null!!");
			return null;
		}
		try {
			return mDex.loadClass(className);
		} catch (Exception e) {
			LogUtil.e(TAG,"", e);
			return  null;
		}
	}
	
	public Object newInstance(String className){	
		try {
			return getClass(className).newInstance();
		} catch (Exception e) {
			LogUtil.e(TAG,"",e);
			return null;
		}
	}
	
	public Object invokeStaticMethod(String className,String methodName){
		return invokeStaticMethod(className, methodName, new Class<?>[]{}, new Object[]{});
	}
	
	public Object invokeStaticMethod(String className,String methodName,Class<?>[] parameterTypes,Object[] parameters){
		try {
			Method method = getClass(className).getMethod(methodName, parameterTypes);
			return method.invoke(null, parameters);
		} catch (Exception e) {
			LogUtil.e(TAG,"", e);
		}
		return null;
	}
	
	public Object invokeMethod(String className,String methodName){
		Object obj = newInstance(className);
		return invokeMethod(obj, methodName,new Class<?>[]{}, new Object[]{});
	}
	
	public Object invokeMethod(Object obj,String methodName){
		return invokeMethod(obj, methodName, new Class<?>[]{}, new Object[]{});
	}
	
	public Object invokeMethod(Object obj,String methodName,Class<?>[] parameterTypes,Object[] parameters){
		if (obj == null) {
			LogUtil.d(TAG , " params obj is null!!!");
			return null;
		}
		try {
			Method method = obj.getClass().getMethod(methodName, parameterTypes);
			return method.invoke(obj, parameters);
		} catch (Exception e) {
			LogUtil.e(TAG,"", e);
			return null;
		}
	}


}
