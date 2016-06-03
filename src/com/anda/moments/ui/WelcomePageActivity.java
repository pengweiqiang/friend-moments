package com.anda.moments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.anda.GlobalConfig;
import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


/**
 * 欢迎页面
 * 
 * @author will
 * 
 */
public class WelcomePageActivity extends BaseActivity {

	private long delayTime = 3660;
	private ImageView mImageView;

//	GifImageView gifImageView;
//	GifDrawable gifDrawable;
	Handler handler;
	Runnable runnable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.welcome);
		super.onCreate(savedInstanceState);
		handler = new Handler();
		LayoutParams imageLayoutParams = mImageView.getLayoutParams();
		imageLayoutParams.width = DeviceInfo.getScreenWidth(this);
		imageLayoutParams.height = (int) (imageLayoutParams.width * 1.0 / 1080 * 1920);
		mImageView.setScaleType(ScaleType.FIT_XY);
		mImageView.setLayoutParams(imageLayoutParams);

		Glide.with(mContext).load(R.drawable.splash2).skipMemoryCache(true).
				diskCacheStrategy( DiskCacheStrategy.NONE).into(mImageView);
		init();
		//asset file
//		try {
//			gifDrawable = new GifDrawable( getAssets(), "splash.gif" );
//			gifImageView.setImageDrawable(gifDrawable);
//
////			delayTime = gifDrawable.getDuration();
//
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}




	}

	private void init(){

		if (application.getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {

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

	private void startNextActivity(Class<?> cls) {
		Intent intent = new Intent(WelcomePageActivity.this, cls);
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
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.removeCallbacks(runnable);
				if (application.getCurrentUser() != null && !StringUtils.isEmpty(GlobalConfig.JSESSION_ID)) {
					startNextActivity(MainActivity.class);
				}else{
					startNextActivity(LoginActivity.class);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		recycle();
	}

	private void recycle(){
//		if(gifDrawable!=null){
//			gifDrawable.stop();
//			gifDrawable.recycle();
//			gifDrawable = null;
//		}
	}
}
