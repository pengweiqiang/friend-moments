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
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.ToggleButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

		String isNeed = MyApplication.getInstance().getCurrentUser().getIsNeedValidate();
		if("yes".equals(isNeed)){
			mToggleAddFriendCheck.setToggleOn();
		}else{
			mToggleAddFriendCheck.setToggleOff();
		}
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

	private LoadingDialog mLoadingDialog;
	private OkHttpClient client = new OkHttpClient();
	private void addMyIscheck(final String ischeck){

		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();

		ThreadUtil.getTheadPool(true).submit(new Runnable() {
			@Override
			public void run() {

				//多文件表单上传构造器
				MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

				//添加一个文本表单参数
				builder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());

				builder.addFormDataPart("isNeedValidate",ischeck);


				RequestBody requestBody = builder.build();
				//构造文件上传时的请求对象Request
				String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_UPDATE_USER_INFO;
				Request request = new Request.Builder().url(url)
						.post(requestBody)
						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
						.build();

				Call call = client.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						mLoadingDialog.cancel();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtils.showToast(mContext, "更新失败");
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						mLoadingDialog.cancel();
						try {
							if (!response.isSuccessful()) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ToastUtils.showToast(mContext, "更新失败");
									}
								});

							} else {
								try {
									JSONObject jsonResult = new JSONObject(response.body().string());
									int retFlag = jsonResult.getInt("retFlag");
									if (ApiConstants.RESULT_SUCCESS.equals("" + retFlag)) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ToastUtils.showToast(mContext,"修改成功");
												updateSuccessRefreshCache(ischeck);
											}
										});

									} else {
										final String info = jsonResult.getString("info");
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ToastUtils.showToast(mContext, info);
											}
										});

									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						} catch (IOException e) {
							mLoadingDialog.cancel();
							e.printStackTrace();
						}
					}
				});
			}


		});


	}

	private void updateSuccessRefreshCache(String isCheck){
		User user = MyApplication.getInstance().getCurrentUser();

		user.setIsNeedValidate(isCheck);

		MyApplication.getInstance().setUser(user);
		SharePreferenceManager.saveBatchSharedPreference(mContext, Constant.FILE_NAME,"user", JsonUtils.toJson(user));
	}






}
