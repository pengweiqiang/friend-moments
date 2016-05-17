package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.ToggleButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

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
				String ischeck = "";
				if(on){
					ischeck = "yes";
				}else{
					ischeck = "no";
				}
				addMyIscheck(ischeck);
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

	/**
	 * 加我是否要校验
	 * @param ischeck yes需要 no不需要
	 */
	private void addMyIscheck(String ischeck){
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_ADD_NOT_NOTICE_PERSON;
		User myUser = MyApplication.getInstance().getCurrentUser();
		OkHttpUtils//
				.get()//
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.addParams("phoneNum",myUser.getPhoneNum())
				.addParams("isLook",ischeck)
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						ToastUtils.showToast(mContext,"保存失败.");
					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {

							}else{
								ToastUtils.showToast(mContext,jsonObject.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showToast(mContext,"保存失败");
						}

					}
				});


	}






}
