package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.anda.moments.R;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.HttpConnectionUtil.RequestCallback;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
/**
 * 兼职详情
 * @author pengweiqiang
 *
 */
public class PartTimeDetailActivity extends BaseActivity {

	ActionBar mActionbar;
	
	LoadingDialog loadingDialog;
	
	View mBtnComplaint,mBtnApply,mBtnCollect;
	View mBtnCompanyDetail;
	
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.part_time_detail);
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("");
		mActionbar.setLeftActionButtonListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mBtnComplaint = findViewById(R.id.btn_complaint);
		mBtnCompanyDetail = findViewById(R.id.company_detail);
		mBtnApply = findViewById(R.id.btn_apply);
		mBtnCollect = findViewById(R.id.btn_collect);
	}

	@Override
	public void initListener() {
		mBtnComplaint.setOnClickListener(onClickListener);
		mBtnCompanyDetail.setOnClickListener(onClickListener);
		mBtnApply.setOnClickListener(onClickListener);
		mBtnCollect.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_collect:
				ToastUtils.showToast(mContext, "收藏成功");
				break;
			case R.id.btn_apply:
				ToastUtils.showToast(mContext, "请先验证手机号");
				startActivity(VefityPhoneActivity.class);
				break;
			case R.id.btn_complaint:
				startActivity(ComplaintActivity.class);
				break;
			case R.id.company_detail:
				startActivity(CompanyDetailActivity.class);
				break;

			default:
				break;
			}
		}
	};
	
	private void getPartTimeDetail(){
//		String schoolName = mEtSchoolName.getText().toString().trim();
//		if(StringUtils.isEmpty(schoolName)){
//			mEtSchoolName.requestFocus();
//			ToastUtils.showToast(mContext, "请选择学校");
//			return ;
//		}
//		String studentId = mEtStudentId.getText().toString().trim();
//		if(StringUtils.isEmpty(studentId)){
//			mEtStudentId.requestFocus();
//			ToastUtils.showToast(mContext, "请选择学号");
//			return ;
//		}
//		String password = mEtPassword.getText().toString().trim();
//		if(StringUtils.isEmpty(password)){
//			mEtPassword.requestFocus();
//			ToastUtils.showToast(mContext, "请选择密码");
//			return ;
//		}
//		
//		loadingDialog = new LoadingDialog(mContext, "登录中...");
//		loadingDialog.show();
//		ApiUserUtils.login(mContext, studentId, schoolName, password, new RequestCallback() {
//			
//			@Override
//			public void execute(ParseModel parseModel) {
//				loadingDialog.cancel();
//				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getState())){
//					User user = JsonUtils.fromJson(parseModel.getUserMessage().toString(), User.class);
//					String userRandom = parseModel.getUserRandom();
//					logined(userRandom, user);
//					ToastUtils.showToast(mContext, "登录成功");
//					AppManager.getAppManager().finishActivity();
//				}else{
//					ToastUtils.showToast(mContext, StringUtils.isEmpty(parseModel.getErrorMessage())?"登录失败，稍后请重试！":parseModel.getErrorMessage());
//				}
//			}
//		});
	}

}
