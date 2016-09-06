package com.anda.moments.ui.my;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
	String phoneNum = "";


	String userId = "";
	String username = "";
	String summary = "";
	String descTag = "";
	String address = "";//地址
	String district = "";//地区

	private int type ;//0昵称  1 个性签名  2 备注 3 userId  4 地址  5地区  6举报
	String relationId = "";//好友关系主键




	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_info);
		super.onCreate(savedInstanceState);

		title = this.getIntent().getStringExtra("title");
		type = this.getIntent().getIntExtra("type",-1);
		content = this.getIntent().getStringExtra("content");
		relationId = this.getIntent().getStringExtra("relationId");
		phoneNum = this.getIntent().getStringExtra("phoneNum");

		if(type == 6){
			mActionbar.setTitle(title);
			mEtContent.setHint("请输入举报内容");
		}else {
			mActionbar.setTitle("修改" + title);
			mEtContent.setHint("请输入" + title);
		}
		if(!StringUtils.isEmpty(content)) {
			mEtContent.setText(content);
		}
		mEtContent.requestFocus();


		int maxLength = 50;
		if(type == 0){//昵称
			maxLength = 10;
		}else if(type == 1){//个性签名
			maxLength = 100;
		}else if(type == 2){//备注
			maxLength = 10;
		}else if(type ==3){//userId
			maxLength = 20;
//			limitEditText();
		}else if(type == 4){//地址
			maxLength = 60;
		}else if(type == 5){//地区
			maxLength = 60;
		}else if(type == 6){
			maxLength = 400;
		}

		if(type != 3){
			limitEditText();
		}
		checkEditTextLength(maxLength);

	}

	private void checkEditTextLength(int maxLength){
		mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
	}

	private void limitEditText(){
		mEtContent.setInputType(InputType.TYPE_CLASS_TEXT);
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
//				if(StringUtils.isEmpty(content)){
//					ToastUtils.showToast(mContext,"请输入"+title);
//					return;
//				}

				if(type == 0){//昵称
					username = content;

				}else if(type == 1){//个性签名
					summary = content;
				}else if(type == 2){//备注
					descTag = content;
					updateFriendTags();
					return;
				}else if(type ==3){//userId
					userId = content;
					checkExistUserId();
					return;
				}else if(type == 4){//地址
					address = content;
				}else if(type == 5){//地区
					district = content;
				}else if(type == 6){
					addReport();
					return;
				}

				updateInfoByOkHttp();
			}
		},R.color.main_tab_text_color_selected);


	}

	@Override
	public void initListener() {

	}

	LoadingDialog mLoadingDialog;
	/**
	 * 修改好友备注
	 */
	private void updateFriendTags(){
		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();
		ApiUserUtils.updateFriendTags(mContext, MyApplication.getInstance().getCurrentUser().getPhoneNum(),relationId , descTag, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					ToastUtils.showToast(mContext,parseModel.getInfo());
					AppManager.getAppManager().finishActivity();
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}

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

				setParams(builder);
//				//添加一个文本表单参数
//				builder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());
//
//				if(type == 0){//昵称
//					builder.addFormDataPart("userName", username);
//				}else if(type == 1){//个性签名
//					builder.addFormDataPart("summary", summary);
//				}else if(type == 2){//备注
//					builder.addFormDataPart("descTag", descTag);
//				}else if(type ==3){//userId
//					builder.addFormDataPart("userId", userId);
//				}else if(type == 4){//地址
//					builder.addFormDataPart("address", address);
//				}else if(type == 5){//地区
//					builder.addFormDataPart("district",district);
//				}


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

		if(type == 0){//昵称
			user.setUserName(username);
			String headIcon = user.getIcon();
			Uri uri = null;
			if(!StringUtils.isEmpty(user.getIcon())){
				uri = Uri.parse(headIcon);
			}
			String rongUserName = StringUtils.isEmpty(username)?user.getDescTag():username;
			if(StringUtils.isEmpty(rongUserName)){
				rongUserName = user.getPhoneNum();
			}
			//刷新融云用户信息
			RongIM.getInstance().refreshUserInfoCache(new UserInfo(user.getPhoneNum(), rongUserName, uri));
		}else if(type == 1){//个性签名
			user.setSummary(summary);
		}else if(type == 2){//备注
			user.setDescTag(descTag);
		}else if(type ==3){//userId
			user.setUserId(userId);
		}else if(type == 4){//地址
			user.setAddr(address);
		}else if(type == 5){//地区
			user.setDistrict(district);
		}

		MyApplication.getInstance().setUser(user);
		SharePreferenceManager.saveBatchSharedPreference(mContext, Constant.FILE_NAME,"user", JsonUtils.toJson(user));
	}


	private void checkExistUserId(){
		if(!StringUtils.isEmpty(content)){
			ToastUtils.showToast(mContext,"ID只能修改一次");
			return;
		}
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
					mLoadingDialog.cancel();
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});

	}


	private void setParams(MultipartBody.Builder builder){
		User user = MyApplication.getInstance().getCurrentUser();
		//添加一个文本表单参数
		builder.addFormDataPart("phoneNum", user.getPhoneNum());

		if(type == 0){//昵称
			builder.addFormDataPart("userName", username);
			builder.addFormDataPart("summary", user.getSummary());
			builder.addFormDataPart("descTag", user.getDescTag());
			builder.addFormDataPart("userId", user.getUserId());
			builder.addFormDataPart("address", user.getAddr());
			builder.addFormDataPart("district",user.getDistrict());

		}else if(type == 1){//个性签名
			builder.addFormDataPart("userName", user.getUserName());
			builder.addFormDataPart("summary", summary);
			builder.addFormDataPart("descTag", user.getDescTag());
			builder.addFormDataPart("userId", user.getUserId());
			builder.addFormDataPart("address", user.getAddr());
			builder.addFormDataPart("district",user.getDistrict());
		}else if(type == 2){//备注
			builder.addFormDataPart("userName", user.getUserName());
			builder.addFormDataPart("summary", user.getSummary());
			builder.addFormDataPart("descTag", descTag);
			builder.addFormDataPart("userId", user.getUserId());
			builder.addFormDataPart("address", user.getAddr());
			builder.addFormDataPart("district",user.getDistrict());
		}else if(type ==3){//userId
			builder.addFormDataPart("userName", user.getUserName());
			builder.addFormDataPart("summary", user.getSummary());
			builder.addFormDataPart("descTag", user.getDescTag());
			builder.addFormDataPart("userId", userId);
			builder.addFormDataPart("address", user.getAddr());
			builder.addFormDataPart("district",user.getDistrict());
		}else if(type == 4){//地址
			builder.addFormDataPart("userName", user.getUserName());
			builder.addFormDataPart("summary", user.getSummary());
			builder.addFormDataPart("descTag", user.getDescTag());
			builder.addFormDataPart("userId", user.getUserId());
			builder.addFormDataPart("address", address);
			builder.addFormDataPart("district",user.getDistrict());
		}else if(type == 5){//地区
			builder.addFormDataPart("userName", user.getUserName());
			builder.addFormDataPart("summary", user.getSummary());
			builder.addFormDataPart("descTag", user.getDescTag());
			builder.addFormDataPart("userId", user.getUserId());
			builder.addFormDataPart("address", user.getAddr());
			builder.addFormDataPart("district",district);
		}

		builder.addFormDataPart("gender",user.getGender());
		builder.addFormDataPart("isNeedValidate",user.getIsNeedValidate());
		builder.addFormDataPart("isLookMyInfo",user.getIsLookMyInfo());
		builder.addFormDataPart("isLookOtherInfo",user.getIsLookOtherInfo());
	}

	private void addReport(){
		String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content)){
			ToastUtils.showToast(mContext,"请输入内容");
			return;
		}
		if(mLoadingDialog == null){
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();

		ApiUserUtils.addReport(mContext, content,MyApplication.getInstance().getCurrentUser().getPhoneNum(), phoneNum, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					ToastUtils.showToast(mContext,"举报成功");
					AppManager.getAppManager().finishActivity();
				}else {
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}





}
