package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anda.GlobalConfig;
import com.anda.gson.JsonArray;
import com.anda.gson.JsonObject;
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
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sz.itguy.utils.FileUtil;

/**
 * 个人中心
 * @author pengweiqiang
 *
 */
public class PersonalInfoActivity extends BaseActivity {

	ActionBar mActionbar;

	private ImageView mIvHead;//头像

	private View mBtnSign;
	private TextView mTvUserName,//昵称
			mTvUserId,//ID
			mTvSex,mTvAddressDetail,mTvAddressArea,mTvUserSign;

	private View mBtnSex;//性别
	private View mBtnSettings;//设置
	private View mBtnUserHead;//修改头像
	private View mBtnUpdateUserName;//昵称
	private View mBtnUserId;//userId
	private View mBtnAddress;//地址
	private View mBtnDistrict;//地区

	private View mBtnLoginOut;//退出登陆

	LoadingDialog mLoadingDialog;

	String phoneNum = "";
	String sex = "";

	User user;
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_personal_info);
		super.onCreate(savedInstanceState);


		phoneNum = this.getIntent().getStringExtra("phoneNum");

		getData();
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("个人中心");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mBtnSign = findViewById(R.id.rl_user_sign);

		mIvHead = (ImageView)findViewById(R.id.iv_user_head);
		mBtnUserHead = findViewById(R.id.rl_user_head);

		mTvUserName = (TextView) findViewById(R.id.tv_user_name);
		mTvUserId = (TextView)findViewById(R.id.tv_user_id);
		mTvSex = (TextView)findViewById(R.id.tv_sex);
		mTvAddressDetail = (TextView)findViewById(R.id.tv_address_detail);
		mTvAddressArea = (TextView)findViewById(R.id.tv_address_area);
		mTvUserSign = (TextView)findViewById(R.id.tv_user_sign);

		mBtnSettings = findViewById(R.id.rl_setting);
		mBtnLoginOut = findViewById(R.id.rl_login_out);
		mBtnSex = findViewById(R.id.rl_sex);
		mBtnUpdateUserName = findViewById(R.id.rl_update_username);
		mBtnUserId = findViewById(R.id.rl_user_id);
		mBtnAddress = findViewById(R.id.rl_address);
		mBtnDistrict = findViewById(R.id.rl_distrct);

	}

	@Override
	public void initListener() {
		mBtnSign.setOnClickListener(onClickListener);
		mBtnLoginOut.setOnClickListener(onClickListener);
		mBtnSettings.setOnClickListener(onClickListener);
		mBtnSex.setOnClickListener(onClickListener);
		mBtnUserHead.setOnClickListener(onClickListener);
		mBtnUpdateUserName.setOnClickListener(onClickListener);
		mBtnUserId.setOnClickListener(onClickListener);
		mBtnAddress.setOnClickListener(onClickListener);
		mBtnDistrict.setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(user==null){
				getData();
				return;
			}
			switch (v.getId()){
				case R.id.rl_address:
					startUpdateInfoActivity(4,"地址",user.getAddr());
					break;
				case R.id.rl_user_id://userId
					startUpdateInfoActivity(3,"ID",user.getUserId());
					break;
				case R.id.rl_user_sign://个性签名
					startUpdateInfoActivity(1,"签名",user.getSummary());
					break;
				case R.id.rl_update_username://昵称
					startUpdateInfoActivity(0,"昵称",user.getUserName());
					break;
				case R.id.rl_user_head://头像
					showPhotoDialog();
					break;
				case R.id.rl_sex://性别
					showSexDialog();
					break;
				case R.id.rl_distrct://地区
					startUpdateInfoActivity(5,"地区",user.getDistrict());
					break;
//				case R.id.rl_remarks://备注
//					startUpdateInfoActivity(2,"备注");
//					break;
				case R.id.rl_login_out://登出
					logOut();
					Intent intent = new Intent(mContext,LoginActivity.class);
					startActivity(intent);
					break;
				case R.id.rl_setting://设置
					startActivity(SettingActivity.class);
					break;

			}
		}
	};

	private void startUpdateInfoActivity(int type,String title,String content){
		Intent intent = new Intent(mContext,UpdateInfoActivity.class);
		intent.putExtra("type",type);
		intent.putExtra("title",title);
		intent.putExtra("content",content);
		startActivity(intent);
	}

	/**
	 * 获取数据
	 */
	private void getData(){
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();
		ApiMyUtils.getMyInformations(mContext, phoneNum, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					user = parseModel.getUser();
					showData();
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
				mLoadingDialog.cancel();
			}
		});
	}

	private void showData(){
		if(user!=null) {
			mTvUserName.setText(StringUtils.isEmpty(user.getUserName())?"":user.getUserName());
			mTvUserId.setText(user.getUserId());
			mTvSex.setText(StringUtils.isEmpty(user.getGender())?"":user.getGender());
			mTvAddressDetail.setText(StringUtils.isEmpty(user.getAddr())?"":user.getAddr());
			mTvAddressArea.setText(StringUtils.isEmpty(user.getDistrict())?"":user.getDistrict());
			mTvUserSign.setText(StringUtils.isEmpty(user.getSummary())?"":user.getSummary());


//			Picasso.with(mContext).load(user.getIcon()).placeholder(R.drawable.default_useravatar).into(mIvHead);
			int width = DeviceInfo.dp2px(mContext,70);
			Picasso.with(mContext).load(user.getIcon()).resize(width,width).centerCrop().placeholder(mContext.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvHead);
			MyApplication.getInstance().setUser(user);
			SharePreferenceManager.saveBatchSharedPreference(mContext,Constant.FILE_NAME,"user",JsonUtils.toJson(user));
		}
	}


	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private static final int UPDATE_FXID = 4;// 结果
	private static final int UPDATE_NICK = 5;// 结果
	String imageName = "";
	private void showPhotoDialog() {
		File file = new File(Constant.MyAvatarDir);
		if(!file.exists()){
			file.mkdirs();
		}

		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		// 为确认按钮添加事件,执行退出应用操作
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("拍照");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {

				imageName = DateUtils.getNowTime() + ".png";
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Constant.MyAvatarDir, imageName)));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
				dlg.cancel();
			}
		});
		TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
		tv_xiangce.setText("相册");
		tv_xiangce.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				imageName = DateUtils.getNowTime() + ".png";
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

				dlg.cancel();
			}
		});

	}

	@SuppressLint("SdCardPath")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PHOTO_REQUEST_TAKEPHOTO:

					startPhotoZoom(
							Uri.fromFile(new File(Constant.MyAvatarDir, imageName)),
							480);
					break;

				case PHOTO_REQUEST_GALLERY:
					if (data != null)
						startPhotoZoom(data.getData(), 100);
					break;

				case PHOTO_REQUEST_CUT:
					// BitmapFactory.Options options = new BitmapFactory.Options();
					//
					// /**
					// * 最关键在此，把options.inJustDecodeBounds = true;
					// * 这里再decodeFile()，返回的bitmap为空
					// * ，但此时调用options.outHeight时，已经包含了图片的高了
					// */
					// options.inJustDecodeBounds = true;
					String imagePath = Constant.MyAvatarDir+imageName;
//					File file = new File(imagePath);
//					Picasso.with(mContext).load(file).placeholder(R.drawable.default_useravatar).into(mIvHead);
					Bitmap bitmap = BitmapFactory.decodeFile(Constant.MyAvatarDir
							+ imageName);
					mIvHead.setImageBitmap(bitmap);
					updateMyInfo(imagePath);
					break;

			}
			super.onActivityResult(requestCode, resultCode, data);

		}
	}

	@SuppressLint("SdCardPath")
	private void startPhotoZoom(Uri uri1, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri1, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", false);


		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Constant.MyAvatarDir, imageName)));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * 弹出性别选择框
	 */
	private void showSexDialog() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		LinearLayout ll_title = (LinearLayout) window
				.findViewById(R.id.ll_title);
		ll_title.setVisibility(View.VISIBLE);
		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_title.setText("性别");
		// 为确认按钮添加事件,执行退出应用操作
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("男");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				if (!sex.equals("1")) {
					updateSex();
					sex = "男";
				}

				dlg.cancel();
			}
		});
		TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
		tv_xiangce.setText("女");
		tv_xiangce.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!sex.equals("2")) {
					updateSex();
					sex = "女";
				}
				dlg.cancel();
			}
		});

	}

	/**
	 * 修改性别
     */
	private void updateSex(){
		updateMyInfo("");
	}

//	private void updateMyInfo(final String filePath){
//		if(mLoadingDialog==null) {
//			mLoadingDialog = new LoadingDialog(mContext);
//		}
//		mLoadingDialog.show();
//
////		ThreadUtil.getTheadPool(true).submit(new Runnable() {
////			@Override
////			public void run() {
//
//				PostFormBuilder postFormBuilder = OkHttpUtils.post();
//				File file = new File(filePath);
//				if (file.exists()) {//有头像的情况
//					postFormBuilder.addFile(file.getName(), file.getName(), file);
//				}
//				Map<String, String> params = new HashMap<String, String>();
//				if (!StringUtils.isEmpty(sex)) {
//					params.put("gender", sex);
//				}
//				params.put("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
//
//
//				String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_UPDATE_USER_INFO;
//				postFormBuilder.url(url);
//				postFormBuilder.params(params);
//				postFormBuilder.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID);
//				postFormBuilder.build().execute(new StringCallback() {
//					@Override
//					public void onError(Call call, Exception e) {
//						mLoadingDialog.cancel();
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.showToast(mContext, "更新失败");
//							}
//						});
//					}
//
//					@Override
//					public void onResponse(String response) {
//						mLoadingDialog.cancel();
//						try {
//							JSONObject jsonResult = new JSONObject(response);
//							int retFlag = jsonResult.getInt("retFlag");
//							if (ApiConstants.RESULT_SUCCESS.equals("" + retFlag)) {
//
//								// 完成上传服务器后 .........
//								FileUtil.deleteFile(filePath);
//								String imgPath = "";//返回的头像路径
//								if (!StringUtils.isEmpty(filePath)) {
//									imgPath = jsonResult.getString("imgPath");
//								}
//								uploadUserHeadSuccess(imgPath);
//							} else {
//								final String info = jsonResult.getString("info");
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										ToastUtils.showToast(mContext, info);
//									}
//								});
//
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//					}
//				});
////			}
////		});
//	}
	/**
	 * 上传头像
	 * @param image
     */
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
	private OkHttpClient client = new OkHttpClient();
	private void updateMyInfo(final String filePath){
		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();

		ThreadUtil.getTheadPool(true).submit(new Runnable() {
			@Override
			public void run() {

				//多文件表单上传构造器
				MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

				File file = new File(filePath);
				if(file.exists()){//有头像的情况
					RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
					multipartBuilder.addFormDataPart(file.getName(), file.getName(), fileBody);
				}
				//添加一个文本表单参数
				multipartBuilder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());
				if(!StringUtils.isEmpty(sex)) {
					multipartBuilder.addFormDataPart("gender", sex);
				}

				RequestBody requestBody = multipartBuilder.build();
				//构造文件上传时的请求对象Request
				String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_UPDATE_USER_INFO;
				Request request = new Request.Builder().url(url)
						.post(requestBody)
						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
						.build();
				Call call = client.newCall(request);
				call.enqueue(new okhttp3.Callback() {
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

										// 完成上传服务器后 .........
										FileUtil.deleteFile(filePath);
										String imgPath = "";//返回的头像路径
										if(!StringUtils.isEmpty(filePath)) {
											imgPath = jsonResult.getString("imgPath");
										}
										uploadUserHeadSuccess(imgPath);
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


	/**
	 * 上传头像成功刷新用户缓存数据。
	 *
	 *  需要更新的用户缓存数据。
	 */
	private void uploadUserHeadSuccess(String newHeadUrl){


		final User user = MyApplication.getInstance().getCurrentUser();
		if(!StringUtils.isEmpty(newHeadUrl)) {
			user.setIcon(newHeadUrl);
			RongIM.getInstance().refreshUserInfoCache(new UserInfo(user.getPhoneNum(), user.getUserName(), Uri.parse(newHeadUrl)));
		}
		if(!StringUtils.isEmpty(sex)) {
			user.setGender(sex);
			sex = "";
		}
		SharePreferenceManager.saveBatchSharedPreference(mContext,Constant.FILE_NAME,"user",JsonUtils.toJson(user));
		MyApplication.getInstance().setUser(user);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mTvSex.setText(user.getGender());
				ToastUtils.showToast(mContext, "更新成功");
			}
		});



	}

	@Override
	protected void onRestart() {
		super.onRestart();
		User user = MyApplication.getInstance().getCurrentUser();
		if(user!=null) {
			this.user = user;

			mTvUserName.setText(StringUtils.isEmpty(user.getUserName())?"":user.getUserName());
			mTvUserId.setText(user.getUserId());
			mTvSex.setText(StringUtils.isEmpty(user.getGender())?"":user.getGender());
			mTvAddressDetail.setText(StringUtils.isEmpty(user.getAddr())?"":user.getAddr());
			mTvAddressArea.setText(StringUtils.isEmpty(user.getDistrict())?"":user.getDistrict());
			mTvUserSign.setText(StringUtils.isEmpty(user.getSummary())?"":user.getSummary());

		}
	}
}
