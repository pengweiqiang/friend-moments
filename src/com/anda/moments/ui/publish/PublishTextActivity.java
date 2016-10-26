package com.anda.moments.ui.publish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.anda.GlobalConfig;
import com.anda.gson.JsonArray;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.MainActivity;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.utils.publish.Bimp;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 发布文字
 * @author pengweiqiang
 *
 */
public class PublishTextActivity extends BaseActivity {

	private EditText mEtContent;
	ActionBar mActionBar;
	private ToggleButton mToggleButtonIsPublic;//是否公开
	String isPublic = "0";//是否公开 1不公开 0 公开



	LoadingDialog mLoadingDialog;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_publish_text);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void initView() {
		mActionBar = (ActionBar)findViewById(R.id.actionBar);
		mActionBar.setLeftActionButton(0, "取消", new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mActionBar.setTitle("发布动态");
		mActionBar.setRightActionButton(0, "发布", new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendText();
			}
		},R.color.main_tab_text_color_selected);
		mEtContent = (EditText)findViewById(R.id.et_content);
		mToggleButtonIsPublic = (ToggleButton)findViewById(R.id.toggle_is_public);
		mToggleButtonIsPublic.setToggleOn();

	}

	@Override
	public void initListener() {
		mToggleButtonIsPublic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				isPublic = on?"0":"1";
			}
		});
	}


	private OkHttpClient client = new OkHttpClient();

//	//参数类型
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("images/png");
	/**
	 * 发布文字
	 */
	private void sendText(){
		client = client.newBuilder().writeTimeout(30,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build();
		final String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content)){
			ToastUtils.showToast(mContext,"请输入内容");
			mEtContent.requestFocus();
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();
		ThreadUtil.getTheadPool(true).submit(new Runnable() {
			@Override
			public void run() {
				//多文件表单上传构造器
				MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				JsonArray fileMetaInfo = new JsonArray();

				//添加表单参数
				multipartBuilder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());
				String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
				multipartBuilder.addFormDataPart("fileMetaInfo",fileMetaInfoStr);

				try {
					String contentStr = URLEncoder.encode(URLEncoder.encode(content, "UTF-8"),"UTF-8");

					multipartBuilder.addFormDataPart("infoText",contentStr);//动态内容
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				multipartBuilder.addFormDataPart("isPublic",isPublic);

				RequestBody requestBody = multipartBuilder.build();

				//构造文件上传时的请求对象Request
				String url = ReqUrls.DEFAULT_REQ_HOST_IP+ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;
				Request request = new Request.Builder().url(url)
						.post(requestBody)
						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
						.build();

				client.newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						mLoadingDialog.cancel();
						e.printStackTrace();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtils.showToast(mContext,"发布失败");
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						mLoadingDialog.cancel();

						try {

							JSONObject jsonResult = new JSONObject(response.body().string());
							int retFlag = jsonResult.getInt("retFlag");
							if(ApiConstants.RESULT_SUCCESS.equals(""+retFlag)){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ToastUtils.showToast(mContext,"发布成功");
										sendSuccess();
									}
								});

							}else{
								final String info = jsonResult.getString("info");
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ToastUtils.showToast(mContext,info);
									}
								});

							}
						} catch (JSONException e) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ToastUtils.showToast(mContext,"发布失败.");
								}
							});
							e.printStackTrace();
						}

					}
				});


			}

		});







	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 发送成功跳转
	 */
	private void sendSuccess(){

//		if(Bimp.bmp!=null) {
//			Bimp.bmp.clear();
//			Bimp.bmp = null;
//		}
		if(Bimp.drr!=null) {
			Bimp.drr.clear();
			Bimp.drr = null;
		}
		AppManager.getAppManager().finishActivity(PublishActivity.class);
		AppManager.getAppManager().finishActivity();
		Intent intent = new Intent(mContext, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("refresh",true);
		startActivity(intent);
	}

}
