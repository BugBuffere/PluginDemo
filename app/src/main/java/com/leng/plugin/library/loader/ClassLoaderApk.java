package com.leng.plugin.library.loader;

import java.lang.reflect.Method;


import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.leng.plugin.library.log.LogUtil;

public class ClassLoaderApk extends ClassLoaderJar {
	
	private static final String TAG = ClassLoaderApk.class.getSimpleName();
	private Resources mRes;
	private String defPackage;
	
	/**
	 * 加载apk
	 * @param app
	 * @param path
	 * @param defPackage
	 */
	public ClassLoaderApk(Application app,String path,String defPackage) {
		super(app, path);
		this.defPackage = defPackage;
	}

	public ClassLoaderApk(Application app,String path,String dexPath,String defPackage){
		super(app,path,dexPath);
		this.defPackage = defPackage;
	}
	
	// *****************资源ID类型*******************//
	public static final String LAYOUT = "layout";
	public static final String ID = "id";
	public static final String DRAWABLE = "drawable";
	public static final String STYLE = "style";
	public static final String STRING = "string";
	public static final String COLOR = "color";
	public static final String DIMEN = "dimen";
	public static final String BOOL = "bool";
	public static final String ARRAY = "array";
	public static final String ANIMATION = "anim";
	
	@Override
	protected void loader() {
		// TODO Auto-generated method stub
		super.loader();
		//加载插件apk下面的res资源
		try {
			AssetManager asset = AssetManager.class.newInstance();
			Method addAssetPath = asset.getClass().getMethod("addAssetPath", String.class);
			addAssetPath.invoke(asset, mPath);
			Resources res = mApp.getResources();
			mRes = new Resources(asset, res.getDisplayMetrics(), res.getConfiguration());
		} catch (Exception e) {
			LogUtil.e(TAG,"", e);
		}
		LogUtil.d(TAG , " loader....");
	} 
	
	public XmlResourceParser getAnimation(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, ANIMATION);
			if (id != 0) {
				return mRes.getAnimation(id);
			}	
		}
		return null;
	}
	
	public XmlResourceParser getLayout(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, LAYOUT);
			if (id!=0) {
				return mRes.getLayout(id);
			}
		}
		return null;
	}
	
	public Drawable getDrawable(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, DRAWABLE);
			if (id!=0) {
				return mRes.getDrawable(id);
			}
		}
		return null;
	}
	
	public String[] getStringAry(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, ARRAY);
			if (id!=0) {
				return mRes.getStringArray(id);
			}
		}
		return null;
	}
	
	public String getString(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, STRING);
			if (id!=0) {
				return mRes.getString(id);
			}
		}
		return null;
	}
	
	public Boolean getBoolean(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, BOOL);
			if (id!=0) {
				return mRes.getBoolean(id);
			}
		}
		return null;
	}
	
	public int getColor(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, COLOR);
			if (id!=0) {
				return mRes.getColor(id);
			}
		}
		return 0;
	}
	
	public ColorStateList getColorStateList(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, DRAWABLE);
			if (id!=0) {
				return mRes.getColorStateList(id);
			}
		}
		return null;
	}
	
	public float getDimension(String name){
		if (mRes == null) {
			LogUtil.e(TAG , " resource is null !!!");
		}else{
			int id = getIdByName(name, DIMEN);
			if (id!=0) {
				return mRes.getDimension(id);
			}
		}
		return 0f;
	}
	
	/**
	 * 根据控件名获取控件id
	 * @param name
	 * @param defType
	 * @return
	 */
	private int getIdByName(String name,String defType){
		if (TextUtils.isEmpty(defPackage)) {
			LogUtil.e(TAG , " defPackage is empty!!");
			return 0;
		}
		LogUtil.d(TAG , " name::"+name + " defType::" + defType);
		return mRes.getIdentifier(name, defType, defPackage);
	}

}
