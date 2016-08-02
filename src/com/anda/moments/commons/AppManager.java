package com.anda.moments.commons;

import android.app.Activity;

import java.util.Iterator;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序
 */
public class AppManager {
	
	private static Stack<Activity> activityStack;
	private static AppManager instance;
	
	private AppManager() {
	}
	
	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}
	
	/**
	 * 添加Activity到堆中
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	/**
	 * 获取当前Activity（堆栈中压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}
	
	/**
	 * 根据字节码找到对应的Activity实例
	 * 
	 * @param cls
	 * @return
	 */
	public Activity getActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}
	
	/**
	 * 结束当前Activity（堆栈中�?���?��压入的）
	 */
	public void finishActivity() {
		try{
			Activity activity = activityStack.lastElement();
			finishActivity(activity);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null && activityStack!=null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}
	
	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
//		for (Activity activity : activityStack) {
//			if (activity.getClass().equals(cls)) {
//				finishActivity(activity);
//			}
//		}
		 Iterator<Activity> itr = activityStack.iterator();  
		 while(itr.hasNext()){
			 Activity activity = itr.next();
			 if(activity.getClass().equals(cls)){
				 itr.remove();
				 activity.finish();
				 activity = null;
			 }
		 }
	}
	
	/**
	 * 结束所有的Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i) && !activityStack.get(i).isFinishing()) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
		System.exit(0);
	}

	/**
	 * 结束所有的Activity 除指定的activity
	 */
	public void finishAllActivity(Class unCloseClass) {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			Activity activity = activityStack.get(i);
			if(activity.getClass().getName().equals(unCloseClass.getName())){
				continue;
			}else {
				if (null != activityStack.get(i) && !activityStack.get(i).isFinishing()) {
					activityStack.get(i).finish();
				}
			}
		}
	}

	public boolean isOnlyOne(Activity currentAcvitity){
		if(activityStack!=null && !activityStack.isEmpty()){
			if(activityStack.contains(currentAcvitity) && activityStack.size() ==1){
				return true;
			}
		}
		return false;
	}
}