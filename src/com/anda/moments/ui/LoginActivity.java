package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.anda.GlobalConfig;
import com.anda.moments.R;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.CheckInputUtil;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.HttpConnectionUtil.RequestCallback;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity {

	ActionBar mActionbar;
	
	LoadingDialog loadingDialog;
	Button mBtnLogin;
	EditText mEtPhone,mEtCode;
	private TextView mGetCode;//获取验证码
	
	String sessionId = "";

	CheckBox checkBox;
	TextView mTvAgreement;
	
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.login);
		super.onCreate(savedInstanceState);

		if(!StringUtils.isEmpty(GlobalConfig.JSESSION_ID) && application.getCurrentUser()!=null){
			loginSuccess();
			return;
		}
		
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("登录");
		mActionbar.hideLeftActionButton();
		
		mBtnLogin = (Button)findViewById(R.id.btn_login);
		mEtPhone = (EditText)findViewById(R.id.et_phone);
		mEtCode = (EditText)findViewById(R.id.et_code);
		mGetCode = (TextView)findViewById(R.id.tv_get_code);
		checkBox = (CheckBox)findViewById(R.id.rc_checkbox);
		mTvAgreement = (TextView)findViewById(R.id.tv_to_agreement);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppManager.getAppManager().finishActivity(SplashActivity.class);
	}

	@Override
	public void initListener() {
		mBtnLogin.setOnClickListener(onClickListener);
		mGetCode.setOnClickListener(onClickListener);
		mTvAgreement.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_get_code:
					getCode();
				break;
				case R.id.btn_login:
					login();
				break;
				case R.id.tv_to_agreement:
					Intent intent = new Intent(mContext,AgreementActivity.class);
					startActivity(intent);
					break;

			default:
				break;
			}
		}
	};
	private void getCode(){
		String tel = mEtPhone.getText().toString().trim();
		if(StringUtils.isEmpty(tel)){
			ToastUtils.showToast(mContext, "请输入手机号");
			mEtPhone.requestFocus();
			return;
		}
		
		if(CheckInputUtil.checkPhone(tel, mContext)){
			if(mGetCode.isEnabled()){
				mGetCode.setEnabled(false);
				ApiUserUtils.getValidateCode(mContext, tel, new RequestCallback() {
					
					@Override
					public void execute(ParseModel parseModel) {
						if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){//发送成功
							regainCode();
							mEtCode.requestFocus();
							sessionId = parseModel.getJsessionid();
							String validateCode = parseModel.getValidateCode();
							if(ApiConstants.ISDEBUG) {
								mEtCode.setText(validateCode);
							}
						}else{
							mGetCode.setEnabled(true);
							ToastUtils.showToast(mContext, parseModel.getInfo());
						}
					}
				});
			}
		}
	}
	//登陆
	private void login(){
		final String phone = mEtPhone.getText().toString().trim();
		if(StringUtils.isEmpty(phone)){
			mEtPhone.requestFocus();
			ToastUtils.showToast(mContext, "请输入手机号");
			return ;
		}
//		if(phone.equals("15652265841")){
//			loginSuccess();
//			return;
//		}
		String code = mEtCode.getText().toString().trim();
		if(StringUtils.isEmpty(code)){
			mEtCode.requestFocus();
			ToastUtils.showToast(mContext, "请输入验证码");
			return ;
		}
		if(StringUtils.isEmpty(sessionId)){
			mEtCode.requestFocus();
			ToastUtils.showToast(mContext, "请先获取验证码");
			return;
		}

		if(!checkBox.isChecked()){
			ToastUtils.showToast(mContext, "请先阅读并同意登录注册协议");
			return;
		}


		
		loadingDialog = new LoadingDialog(mContext, "登录中...");
		loadingDialog.show();
		ApiUserUtils.login(mContext, phone, code, new RequestCallback() {
			
			@Override
			public void execute(ParseModel parseModel) {
				loadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					User user = parseModel.getUser();
					logined(user);
//					setUserInfo();
					loginSuccess();
//					getUserInfo(phone);

				}else{
					ToastUtils.showToast(mContext, StringUtils.isEmpty(parseModel.getInfo())?"登录失败，稍后请重试！":parseModel.getInfo());
				}
			}
		});
	}
	//登陆成功
	private void loginSuccess(){
		Intent intent = new Intent(mContext,MainActivity.class);
		startActivity(intent);
		AppManager.getAppManager().finishActivity();
	}

	private void getUserInfo(String phoneNum){
		ApiMyUtils.getMyInformations(mContext, phoneNum,phoneNum, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					User user = parseModel.getUser();

					logined(user);
//					setUserInfo();
					loginSuccess();
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
				if(loadingDialog!=null && loadingDialog.isShowing()) {
					loadingDialog.cancel();
				}
			}
		});
	}

	
	private Timer timer;// 计时器
	private int time = 60;//倒计时60秒

	private void regainCode() {
		time = 60;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(time--);
			}
		}, 0, 1000);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				mGetCode.setEnabled(true);
				mGetCode.setText("获取验证码");
				timer.cancel();
			} else {
				mGetCode.setText(msg.what + "秒重发");
			}
		};
	};
	protected void onDestroy() {
		super.onDestroy();
		if(timer != null){
			timer.cancel();
		}
	};





}
