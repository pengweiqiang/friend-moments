package com.anda.moments.ui;

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
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.ui.publish.PublishActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.utils.publish.Bimp;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
 * 设置
 * @author pengweiqiang
 *
 */
public class FeedBackActivity extends BaseActivity {

	private EditText mEtContent;
	ActionBar mActionBar;



	LoadingDialog mLoadingDialog;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_feed_back);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void initView() {
		mActionBar = (ActionBar)findViewById(R.id.actionBar);
		mActionBar.setLeftActionButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mActionBar.setTitle("意见反馈");
		mActionBar.setRightActionButton(0, "提交", new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendText();
			}
		},R.color.main_tab_text_color_selected);
		mEtContent = (EditText)findViewById(R.id.et_content);

	}

	@Override
	public void initListener() {

	}



	/**
	 * 发布意见
	 */
	private void sendText(){

		String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content)){
			ToastUtils.showToast(mContext,"请输入内容");
			mEtContent.requestFocus();
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_SUBMIT_IDEA;
		String phoneNum = MyApplication.getInstance().getCurrentUser().getPhoneNum();
		OkHttpUtils//
				.get()//
				.addHeader("JSESSIONID",GlobalConfig.JSESSION_ID)
				.addParams("respIdea",content)
				.addParams("phoneNum",phoneNum)
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						mLoadingDialog.cancel();
						ToastUtils.showToast(mContext,"发送意见反馈失败.");
					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {
								ToastUtils.showToast(mContext,"发送成功，我们会对您的意见进行处理.");
								AppManager.getAppManager().finishActivity();
							}else{
								ToastUtils.showToast(mContext,jsonObject.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showToast(mContext,"发送意见反馈失败");
						}
						mLoadingDialog.cancel();

					}
				});







	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



}
