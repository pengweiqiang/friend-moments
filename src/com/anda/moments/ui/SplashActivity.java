package com.anda.moments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;


public class SplashActivity extends BaseActivity {

	private ImageView mIvImage;
    private long delayTime = 1800;


    Handler handler;
    Runnable runnable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.splash);
		super.onCreate(savedInstanceState);

        DeviceInfo.setContext(SplashActivity.this);
        String nowVersionName = DeviceInfo.getVersionName();
        String appVersionName = (String) SharePreferenceManager.getSharePreferenceValue(
                this, Constant.FILE_NAME, "appVersionName", "");
        GlobalConfig.VERSION_NAME_V = nowVersionName;

        if (StringUtils.isEmpty(appVersionName) || !nowVersionName.equals(appVersionName)) {// 首次打开app,进入引导页面
            SharePreferenceManager.saveBatchSharedPreference(SplashActivity.this,
                    Constant.FILE_NAME, "appVersionName", nowVersionName);

            Intent intent = new Intent(SplashActivity.this,WelcomePageActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishActivity(SplashActivity.this);
            return;
        }else{
            handler = new Handler();
            if (MyApplication.getInstance().getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        startNextActivity(MainActivity.class);
                    }
                };
                handler.postDelayed(runnable, delayTime);
            }else{
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        startNextActivity(LoginActivity.class);
                    }
                };
                handler.postDelayed(runnable, delayTime);
            }
        }




        int width = DeviceInfo.getDisplayMetricsWidth(SplashActivity.this);
        int height = (int) (width * 1.0 / 1080 * 1920);
        LayoutParams params = mIvImage.getLayoutParams();
        params.width = width;
        params.height = height;
        mIvImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mIvImage.setLayoutParams(params);
	}

    private void startNextActivity(Class<?> cls) {
        Intent intent = new Intent(SplashActivity.this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in,
                R.anim.activity_left_out);
        AppManager.getAppManager().finishActivity(SplashActivity.class);
    }
	/**
	 * 启动主页面
	 */
	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		AppManager.getAppManager().finishActivity(SplashActivity.this);
	}

	@Override
	public void initView() {
		mIvImage = (ImageView) findViewById(R.id.splash);
	}

	@Override
	public void initListener() {
		
	}
	
}
