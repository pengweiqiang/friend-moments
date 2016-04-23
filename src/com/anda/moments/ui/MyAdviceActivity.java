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
 * 意见反馈
 * @author pengweiqiang
 *
 */
public class MyAdviceActivity extends BaseActivity {

	ActionBar mActionbar;
	
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.my_advice);
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("意见反馈");
		mActionbar.setLeftActionButtonListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mActionbar.setRightActionButton(0, "发送", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	@Override
	public void initListener() {
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};

}
