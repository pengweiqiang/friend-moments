package com.anda.moments.ui.publish;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.views.LoadingDialog;
import com.squareup.okhttp.OkHttpClient;

/**
 * 发布视频
 * @author pengweiqiang
 *
 */
public class PublishVideoActivity extends BaseActivity {

	private View mViewPicture,mViewVideo,mViewVoice;

	LoadingDialog mLoadingDialog;

	private View mViewRoot;
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_publish_video);
		super.onCreate(savedInstanceState);


		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showAnim(true);
			}
		},300);


	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void initView() {
		mViewPicture = findViewById(R.id.rl_publish_picture);
		mViewVideo  = findViewById(R.id.rl_publish_video);
		mViewVoice = findViewById(R.id.rl_publish_vioce);
		mViewRoot = findViewById(R.id.rl_root);

		mViewPicture.setVisibility(View.GONE);
		mViewVideo.setVisibility(View.GONE);
		mViewVoice.setVisibility(View.GONE);
	}

	@Override
	public void initListener() {
		mViewPicture.setOnClickListener(onClickListener);
		mViewVideo.setOnClickListener(onClickListener);
		mViewVoice.setOnClickListener(onClickListener);

		mViewRoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.rl_publish_picture://图片

					break;
				case R.id.rl_publish_video://视频

					break;
				case R.id.rl_publish_vioce://语音

					break;

			}
		}
	};

	/**
	 * 获取数据
	 */
	private void getData(){
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();

	}



	private void showAnim(final boolean isdown){
		ObjectAnimator animator,animator2,animator3;
		int height = DeviceInfo.getScreenHeight(mContext);
		int heightImageView = DeviceInfo.dp2px(mContext,100);
		if(isdown) {
			animator = ObjectAnimator.ofFloat(mViewPicture, "translationY",100 , 0);
			animator2 = ObjectAnimator.ofFloat(mViewVideo, "translationY",150 , 0);
			animator3 = ObjectAnimator.ofFloat(mViewVoice, "translationY",200 , 0);

//			animator2 = ObjectAnimator.ofFloat(mViewVideo, "translationY", height , -30,50,0f);
//			animator3 = ObjectAnimator.ofFloat(mViewVoice, "translationY", height , -20,50,0f);
		}else{
			animator = ObjectAnimator.ofFloat(mViewPicture, "translationY", 0, -1000f);
			animator2 = ObjectAnimator.ofFloat(mViewVideo, "translationY", 0, -1000f);
			animator3 = ObjectAnimator.ofFloat(mViewVoice, "translationY", 0, -1000f);
		}
		animator.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				if(isdown) {
					mViewPicture.setVisibility(View.VISIBLE);
					mViewVideo.setVisibility(View.VISIBLE);
					mViewVoice.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				if(!isdown){
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});

		AnimatorSet animSet = new AnimatorSet();
		animator.setDuration(400);
		animator.setInterpolator(new BounceInterpolator());
		animator2.setDuration(500);
		animator2.setInterpolator(new BounceInterpolator());
		animator3.setDuration(600);
		animator3.setInterpolator(new BounceInterpolator());
//		animSet.play(animator).after(animator2).after(animator3);

		animator.start();
//		animSet.start();
//		animator2.setStartDelay(100);
		animator2.start();
//		animator3.setStartDelay(180);
		animator3.start();
	}

	private void sendPicture(){
		OkHttpClient okHttpClient  = new OkHttpClient();

	}

}
