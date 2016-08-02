package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.views.ActionBar;

/**
 * 设置
 * @author pengweiqiang
 *
 */
public class SettingActivity extends BaseActivity {

	ActionBar mActionbar;

	private View mBtnSercetSetting;
	private View mBtnAboutUs;




	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		super.onCreate(savedInstanceState);

	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("设置");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		mBtnSercetSetting = findViewById(R.id.rl_sercet_setting);
		mBtnAboutUs = findViewById(R.id.rl_about_us);

	}

	@Override
	public void initListener() {
		mBtnSercetSetting.setOnClickListener(onClickListener);
		mBtnAboutUs.setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.rl_sercet_setting://私密设置
					startActivity(SecretSettingActivity.class);
					break;
				case R.id.rl_about_us://关于我们
					startActivity(AboutUsActivity.class);
					break;

			}
		}
	};



}
