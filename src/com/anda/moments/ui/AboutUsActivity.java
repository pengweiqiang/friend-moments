package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.views.ActionBar;

/**
 * 关于我们
 * @author pengweiqiang
 *
 */
public class AboutUsActivity extends BaseActivity {

	ActionBar mActionbar;

	TextView mTvAppName;





	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_about_us);
		super.onCreate(savedInstanceState);

		mTvAppName.setText("微萌 V"+ DeviceInfo.getVersionName());
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("关于我们");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		mTvAppName = (TextView)findViewById(R.id.tv_app_name);

	}

	@Override
	public void initListener() {

	}

}
