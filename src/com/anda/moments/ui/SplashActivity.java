package com.anda.moments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.xiaomi.ad.SplashAd;
import com.xiaomi.ad.SplashAdListener;


public class SplashActivity extends BaseActivity{

	private ImageView mIvImage;
    private long delayTime = 1100;

    //以下的POSITION_ID 需要使用您申请的值替换下面内容
    private static final String POSITION_ID = "d2d57beefa3897d21d88fe45a3282ffb";
    private Handler mHandler = new Handler(Looper.getMainLooper());


    Handler handler;

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
//            handler = new Handler();
//            if (MyApplication.getInstance().getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {
//
//                runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        startNextActivity(MainActivity.class);
//                    }
//                };
//                handler.postDelayed(runnable, delayTime);
//            }else{
//                runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        startNextActivity(LoginActivity.class);
//                    }
//                };
//                handler.postDelayed(runnable, delayTime);
//            }
        }




        int width = DeviceInfo.getDisplayMetricsWidth(SplashActivity.this);
        int height = (int) (width * 1.0 / 1080 * 1920);
        LayoutParams params = mIvImage.getLayoutParams();
        params.width = width;
        params.height = height;
        mIvImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mIvImage.setLayoutParams(params);


        initAd();
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


        if (MyApplication.getInstance().getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishActivity(SplashActivity.this);
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishActivity(SplashActivity.this);
        }
	}


    private Class getNextClass(){
        if (MyApplication.getInstance().getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {

            return MainActivity.class;
        }else{
            return LoginActivity.class;
        }
    }






	@Override
	public void initView() {
		mIvImage = (ImageView) findViewById(R.id.splash);
	}

	@Override
	public void initListener() {
		
	}

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }



    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


    private void initAd(){
        ViewGroup container = (ViewGroup) findViewById(R.id.splash_ad_container);
        SplashAd splashAd = new SplashAd(this, container, new SplashAdListener() {
            @Override
            public void onAdPresent() {
                // 开屏广告展示
                Log.d(TAG, "onAdPresent");
                mHandler.postDelayed(mRemoveDefaultPicture, 500);
            }

            @Override
            public void onAdClick() {
                //用户点击了开屏广告
                Log.d(TAG, "onAdClick");
                startMainActivity();
            }

            @Override
            public void onAdDismissed() {
                //这个方法被调用时，表示从开屏广告消失。
                Log.d(TAG, "onAdDismissed");
                startMainActivity();
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "onAdFailed, message: " + s);
                //这个方法被调用时，表示从服务器端请求开屏广告时，出现错误。
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startMainActivity();
                    }
                }, 500);
            }
        });
        splashAd.requestAd(POSITION_ID);

    }


    private Runnable mRemoveDefaultPicture = new Runnable() {
        @Override
        public void run() {
            View logo = findViewById(R.id.splash);
            AlphaAnimation alpha = new AlphaAnimation(1, 0);
            alpha.setDuration(200);
            logo.startAnimation(alpha);
            logo.setVisibility(View.GONE);
        }
    };




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 跑应用的逻辑
     */
    private void runApp() {

    }

    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 创建开屏容器
//        RelativeLayout.LayoutParams params =
//                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.addRule(RelativeLayout.ABOVE, R.id.ad_bottom);




    }

    @Override
    protected void onStop() {
        super.onStop();
//        SpotManager.getInstance(mContext).onStop();
    }





}
