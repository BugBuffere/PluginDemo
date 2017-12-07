package com.leng.plugin.library.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;

public abstract class BaseHandler implements Callback {
	
	protected Handler mH;
	
	public BaseHandler(String threadName){
		HandlerThread ht = new HandlerThread(threadName);
		ht.start();
		mH = new Handler(ht.getLooper(), this);
	}
	
	public BaseHandler(Looper looper){
		mH = new Handler(looper, this);
	}
	
	public Message obtainMessage(){
		return mH.obtainMessage();
	}
	
	public void sendMessage(Object obj){
		mH.obtainMessage(0, obj).sendToTarget();
	}
	
	public void runOnUserThread(Runnable r,long delay){
		mH.postDelayed(r, delay);
	}
	
	public void removeOnUserThreadCallback(Runnable r){
		mH.removeCallbacks(r);
	}
	
	public boolean isMainThread(){
		return Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId();
	}
	
}
