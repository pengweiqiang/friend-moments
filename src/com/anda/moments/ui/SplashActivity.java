package com.anda.moments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.anda.GlobalConfig;
import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.umeng.update.UmengUpdateAgent;


/**
 * 加载页
 * 
 * @author will
 * 
 */
public class SplashActivity extends BaseActivity {

	private long delayTime = 1500;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.splash);
		super.onCreate(savedInstanceState);
		

		DeviceInfo.setContext(mContext);
		String nowVersionName = DeviceInfo.getVersionName();
		String appVersionName = (String) SharePreferenceManager.getSharePreferenceValue(
				this, Constant.FILE_NAME, "appVersionName", "");
		GlobalConfig.VERSION_NAME_V = nowVersionName;
		
		LayoutParams imageLayoutParams = mImageView.getLayoutParams();
		imageLayoutParams.width = DeviceInfo.getScreenWidth(this);
		imageLayoutParams.height = (int) (imageLayoutParams.width * 1.0 / 720 * 1280);
		mImageView.setScaleType(ScaleType.FIT_XY);
		mImageView.setLayoutParams(imageLayoutParams);
		if (StringUtils.isEmpty(appVersionName) || !nowVersionName.equals(appVersionName)) {// 首次打开app,进入引导页面
			// findViewById(R.id.loading).setVisibility(View.VISIBLE);
			// application.getBoot(handler);
//			SharePreferenceManager.saveBatchSharedPreference(application,
//					Constant.FILE_NAME, "isFirst", false);
			SharePreferenceManager.saveBatchSharedPreference(application,
					Constant.FILE_NAME, "appVersionName", nowVersionName);

//			new Handler().postDelayed(new Runnable() {
//				public void run() {
//					startNextActivity(WelcomeViewPageActivity.class);
//				}
//			}, delayTime);

			if (application.getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {

				// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
				new Handler().postDelayed(new Runnable() {
					public void run() {
						startNextActivity(MainActivity.class);
					}
				}, 1000);
			}else{
				// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
				new Handler().postDelayed(new Runnable() {
					public void run() {
						startNextActivity(LoginActivity.class);
						// startNextActivity(MainActivity.class);
					}
				}, delayTime);
			}

		} else {
			if (application.getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {

					// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
					new Handler().postDelayed(new Runnable() {
						public void run() {
							startNextActivity(MainActivity.class);
						}
					}, 1000);
			}else{
				// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
				new Handler().postDelayed(new Runnable() {
					public void run() {
						startNextActivity(LoginActivity.class);
						// startNextActivity(MainActivity.class);
					}
				}, delayTime);
			}

		}


	}

	private void startNextActivity(Class<?> cls) {
		Intent intent = new Intent(SplashActivity.this, cls);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_right_in,
				R.anim.activity_left_out);
		AppManager.getAppManager().finishActivity();
	}

	@Override
	public void initView() {
		mImageView = (ImageView) findViewById(R.id.splash);
	}

	@Override
	public void initListener() {
		
	}


}
