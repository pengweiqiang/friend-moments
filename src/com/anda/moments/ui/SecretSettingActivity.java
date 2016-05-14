package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.ToggleButton;

/**
 * 私密设置
 * @author pengweiqiang
 *
 */
public class SecretSettingActivity extends BaseActivity {

	ActionBar mActionbar;

	private ToggleButton mToggleAddFriendCheck;
			//,mTogglePublic,mToggleFriendsPublic;





	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_secret_setting);
		super.onCreate(savedInstanceState);

	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("私密设置");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

//		mTogglePublic = (ToggleButton) findViewById(R.id.toggle_is_public);
		mToggleAddFriendCheck = (ToggleButton) findViewById(R.id.toggle_add_friend_check);
//		mToggleFriendsPublic = (ToggleButton) findViewById(R.id.toggle_friend_public);

	}

	@Override
	public void initListener() {
//		//朋友圈公开
//		mTogglePublic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
//			@Override
//			public void onToggle(boolean on) {
//
//			}
//		});
		//添加好友验证
		mToggleAddFriendCheck.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {

			}
		});
//		//不看ta的朋友圈
//		mToggleFriendsPublic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
//			@Override
//			public void onToggle(boolean on) {
//
//			}
//		});
	}






}
