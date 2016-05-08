package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sz.itguy.utils.FileUtil;

/**
 * 修改个人信息
 * @author pengweiqiang
 *
 */
public class UpdateInfoActivity extends BaseActivity {

	ActionBar mActionbar;

	EditText mEtContent;
	String title = "";
	String content = "";


	String userId = "";
	String username = "";
	String summary = "";
	String descTag = "";
	String address = "";//地址

	private int type ;//0昵称  1 个性签名  2 备注 3 userId  4 地址




	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_info);
		super.onCreate(savedInstanceState);

		title = this.getIntent().getStringExtra("title");
		type = this.getIntent().getIntExtra("type",-1);
		content = this.getIntent().getStringExtra("content");
		mActionbar.setTitle("修改"+title);
		mEtContent.setHint("请输入"+title);
		if(!StringUtils.isEmpty(content)) {
			mEtContent.setText(content);
		}
		mEtContent.requestFocus();
	}


	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);

//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mEtContent = (EditText)findViewById(R.id.et_content);

		mActionbar.setRightActionButton(0, "保存", new OnClickListener() {
			@Override
			public void onClick(View v) {

				String content = mEtContent.getText().toString().trim();
				if(StringUtils.isEmpty(content)){
					ToastUtils.showToast(mContext,"请输入"+title);
					return;
				}

				if(type == 0){//昵称
					username = content;
				}else if(type == 1){//个性签名
					summary = content;
				}else if(type == 2){//备注
					descTag = content;
				}else if(type ==3){//userId
					userId = content;
					checkExistUserId();
					return;
				}else if(type == 4){//地址
					address = content;
				}
				updateInfoByOkHttp();
			}
		},R.color.main_tab_text_color_selected);

	}

	@Override
	public void initListener() {

	}

	LoadingDialog mLoadingDialog;

//	private void updateInfoByOkHttp(){
//		if(mLoadingDialog==null) {
//			mLoadingDialog = new LoadingDialog(mContext);
//		}
//		mLoadingDialog.show();
//
//		Map<String,String> params = new HashMap<String, String>();
//		params.put("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
//
//		if(!StringUtils.isEmpty(username)) {
//			params.put("userName", username);
//		}
//		if(!StringUtils.isEmpty(descTag)) {
//			params.put("descTag", descTag);
//		}
//		if(!StringUtils.isEmpty(summary)) {
//			params.put("summary", summary);
//		}
//		if(!StringUtils.isEmpty(userId)){
//			params.put("userId", userId);
//		}
//
//
//		if(!StringUtils.isEmpty(address)){
//			params.put("address", address);
//		}
//		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_UPDATE_USER_INFO;
//
//		OkHttpUtils.post().url(url).params(params).addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
//				.build().execute(new StringCallback() {
//			@Override
//			public void onError(Call call, Exception e) {
//				mLoadingDialog.cancel();
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						ToastUtils.showToast(mContext, "更新失败");
//					}
//				});
//			}
//
//			@Override
//			public void onResponse(String response) {
//				mLoadingDialog.cancel();
//				try {
//					JSONObject jsonResult = new JSONObject(response);
//					int retFlag = jsonResult.getInt("retFlag");
//					if (ApiConstants.RESULT_SUCCESS.equals("" + retFlag)) {
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.showToast(mContext,"修改成功");
//								updateSuccessRefreshCache();
//								AppManager.getAppManager().finishActivity();
//							}
//						});
//
//					} else {
//						final String info = jsonResult.getString("info");
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.showToast(mContext, info);
//							}
//						});
//
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//
//
//			}
//		});
//	}


	private OkHttpClient client = new OkHttpClient();
	private void updateInfoByOkHttp(){

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
				if(!StringUtils.isEmpty(username)) {
					builder.addFormDataPart("userName", username);
				}
				if(!StringUtils.isEmpty(descTag)) {
					builder.addFormDataPart("descTag", descTag);
				}
				if(!StringUtils.isEmpty(summary)) {
					builder.addFormDataPart("summary", summary);
				}
				if(!StringUtils.isEmpty(userId)){
					builder.addFormDataPart("userId", userId);
				}

				if(!StringUtils.isEmpty(address)){
					builder.addFormDataPart("address", address);
				}


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
												updateSuccessRefreshCache();
												AppManager.getAppManager().finishActivity();
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


	private void updateSuccessRefreshCache(){
		User user = MyApplication.getInstance().getCurrentUser();
		if(!StringUtils.isEmpty(username)){
			user.setUserName(username);
		}
		if(!StringUtils.isEmpty(descTag)){
			user.setDescTag(descTag);
		}
		if(!StringUtils.isEmpty(summary)){
			user.setSummary(summary);
		}
		if(!StringUtils.isEmpty(userId)){
			user.setUserId(userId);
		}
		if(!StringUtils.isEmpty(address)){
			user.setAddr(address);
		}

		MyApplication.getInstance().setUser(user);
		SharePreferenceManager.saveBatchSharedPreference(mContext, Constant.FILE_NAME,"user", JsonUtils.toJson(user));
	}


	private void checkExistUserId(){
		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();;
		ApiMyUtils.checkExistUserId(mContext, userId, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					updateInfoByOkHttp();
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});

	}






}
