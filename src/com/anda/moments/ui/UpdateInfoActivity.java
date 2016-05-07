package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

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

	private int type ;//0昵称  1 个性签名




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

	String username = "";
	String summary = "";
	String descTag = "";
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
				}
//				updateInfo();
				updateInfoByOkHttp();
			}
		},R.color.main_tab_text_color_selected);

	}

	@Override
	public void initListener() {

	}

	LoadingDialog mLoadingDialog;

	/**
	 * 修改个人资料
	 */
	private void updateInfo(){
		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();
		String phoneNum = MyApplication.getInstance().getCurrentUser().getPhoneNum();
		ApiUserUtils.updateUserInfo(mContext,phoneNum,username,"","","","",summary,descTag,new HttpConnectionUtil.RequestCallback(){

			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					ToastUtils.showToast(mContext,"修改成功");
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}

	private void updateInfoByOkHttp(){

		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();

		ThreadUtil.getTheadPool(true).submit(new Runnable() {
			@Override
			public void run() {
				OkHttpClient client = new OkHttpClient();
				RequestBody formBody = new FormEncodingBuilder()
						.add("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum())
						.add("userName",username)
						.add("userId","")
						.add("gender","")
						.add("address","")
						.add("district","")
						.add("summary",summary)
						.add("descTag",descTag)
						.add("icon","")
						.build();
				String url = ReqUrls.DEFAULT_REQ_HOST_IP+ReqUrls.REQUEST_UPDATE_USER_INFO;
				Request request = new Request.Builder()
						.url(url)
						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
						.post(formBody)
						.build();

				Response response = null;
				try {
					response = client.newCall(request).execute();

					if (!response.isSuccessful())
						throw new IOException("Unexpected code " + response);

					System.out.println(response.body().string());
					mLoadingDialog.cancel();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});


	}







}
