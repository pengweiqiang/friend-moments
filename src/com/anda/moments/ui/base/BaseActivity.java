package com.anda.moments.ui.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.constant.api.ReqUrls;
import com.anda.moments.entity.User;
import com.anda.moments.ui.LoginActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity implements OnClickListener {

	protected static final String TAG = "BaseActivity";
	protected MyApplication application;
	protected Context mContext;
	public InputMethodManager manager;
//	protected SystemBarTintManager tintManager;
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);

		initView();
		mContext = this;
		initListener();
		application = MyApplication.getInstance();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
//
//		tintManager = new SystemBarTintManager(this);
//		tintManager.setStatusBarTintEnabled(true);
////		tintManager.setStatusBarTintResource(R.color.app_title_color);
//		tintManager.setTintColor(getResources().getColor(R.color.app_bg_color));
////		SystemBarConfig config = tintManager.getConfig();
////		map.setPadding(0, config.getPixelInsetTop(), config.getPixelInsetRight(), config.getPixelInsetBottom());
	}

	/**
	 * 左返回按钮点击事件
	 */
	protected void onLeftBackClick() {
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
		
		
	}

	@Override
	protected void onDestroy() {
		dismissWaitingDialog();
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

	protected void startActivity(Class<?> aTargetClass, Bundle aBundle) {
		Intent i = new Intent(this, aTargetClass);
		if (aBundle != null) {
			i.putExtras(aBundle);
		}
		startActivity(i);
	}

	protected void dismissWaitingDialog() {
	}

	public void showToast(String aMessage) {
		Toast.makeText(this, aMessage, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT)
				.show();
	}

	public void startActivity(Class<?> cls) {
		Intent intent = new Intent(mContext, cls);
		startActivity(intent);
	}

	/**
	 * 隐藏键盘
	 */
	protected void hideInputMethod() {
		View view = getCurrentFocus();
		if (view != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	@Override
	public void onClick(View v) {
	}

	/**
	 * 初始化控件
	 */
	public abstract void initView();

	/**
	 * 初始化控件的事件
	 */
	public abstract void initListener();

	/**
	 * 展示空白页面
	 * 
	 * @param view
	 * @param tipStr
	 * @param goStr
	 * @param imageId
	 * @param emptyClick
	 */
	/*
	 * public void showEmpty(View view,int tipId,int goId,int
	 * imageId,OnClickListener emptyClick){ ImageView imageView =
	 * (ImageView)view.findViewById(R.id.empty_imageView);
	 * imageView.setImageDrawable(this.getResources().getDrawable(imageId));
	 * TextView textTip = (TextView)view.findViewById(R.id.empty_text_tip);
	 * textTip.setText(getResources().getString(tipId)); TextView textGo =
	 * (TextView)view.findViewById(R.id.empty_text_go);
	 * textGo.setText(getResources().getString(goId));
	 * 
	 * textGo.setOnClickListener(emptyClick); }
	 */

	/*
	 * public void showEmpty(View view,String tip,int goId,int
	 * imageId,OnClickListener emptyClick){ ImageView imageView =
	 * (ImageView)view.findViewById(R.id.empty_imageView);
	 * imageView.setImageDrawable(this.getResources().getDrawable(imageId));
	 * TextView textTip = (TextView)view.findViewById(R.id.empty_text_tip);
	 * textTip.setText(tip); TextView textGo =
	 * (TextView)view.findViewById(R.id.empty_text_go);
	 * textGo.setText(getResources().getString(goId));
	 * 
	 * textGo.setOnClickListener(emptyClick); }
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			if (getCurrentFocus() != null
//					&& getCurrentFocus().getWindowToken() != null) {
//				manager.hideSoftInputFromWindow(getCurrentFocus()
//						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//			}
//		}
		return super.onTouchEvent(event);
	}
	
	

	/**
	 * 退出登录
	 */
	public void logOut() {

		GlobalConfig.JSESSION_ID = "";
		SharePreferenceManager.saveBatchSharedPreference(mContext,Constant.FILE_NAME,ReqUrls.JSESSION_ID,"");
		application.setUser(null);
		SharePreferenceManager.saveBatchSharedPreference(mContext,Constant.FILE_NAME,"user","");
		AppManager.getAppManager().finishAllActivity(LoginActivity.class);
	}

	/**
	 * 登录成功
	 * 
	 * @param user
	 */
	public void logined(User user) {
//		if (!StringUtils.isEmpty(userRandom)) {
//			GlobalConfig.USER_RANDOM = userRandom;
////			long currentTime = System.currentTimeMillis();
//			SharePreferenceManager.saveBatchSharedPreference(mContext,
//					Constant.FILE_NAME, ReqUrls.USER_RANDOM, userRandom);
//		}
		if (user != null ) {
			application.setUser(user);
			SharePreferenceManager.saveBatchSharedPreference(mContext,
					Constant.FILE_NAME, "user", JsonUtils.toJson(user));
		}

	}

	public boolean isLogined() {
		if (application.getCurrentUser() != null) {
			return true;
		}
		return false;

	}

	public User getUser(){
		User user = application.getCurrentUser();
		if(user == null){
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
			return null;
		}
		return user;
	}
	
	
	
    
    @TargetApi(19) 
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

}
