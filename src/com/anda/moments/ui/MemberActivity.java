package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
/**
 * 会员
 * @author pengweiqiang
 *
 */
public class MemberActivity extends BaseActivity {

	ActionBar mActionbar;
	
//	private View mBtnBecomeMember,mBtnBuyKuihuazi,mBtnAddSystem;
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.member);
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("葵花籽的作用");
		mActionbar.setLeftActionButtonListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
//		mBtnBecomeMember = findViewById(R.id.become_member);
//		mBtnBuyKuihuazi = findViewById(R.id.buy_kuihuazi);
//		mBtnAddSystem = findViewById(R.id.add_system);
	}

	@Override
	public void initListener() {
//		mBtnBecomeMember.setOnClickListener(onClickListener);
//		mBtnBuyKuihuazi.setOnClickListener(onClickListener);
//		mBtnAddSystem.setOnClickListener(onClickListener);
	}
	
//	OnClickListener onClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			
//		}
//	};

}
