package com.anda.moments.ui.publish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.anda.GlobalConfig;
import com.anda.gson.Gson;
import com.anda.gson.JsonArray;
import com.anda.gson.JsonObject;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.MainActivity;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.utils.publish.Bimp;
import com.anda.moments.utils.publish.FileUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sz.itguy.utils.FileUtil;

/**
 * 发布图片
 * @author pengweiqiang
 *
 */
public class PublishPictureActivity extends BaseActivity {

	private EditText mEtContent;
	private GridView mGridView;
	private GridAdapter adapter;
	ActionBar mActionBar;



	LoadingDialog mLoadingDialog;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_publish_picture);
		super.onCreate(savedInstanceState);

		InitGridView();

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
//				sendPictureByXutils();
				sendPicture();
			}
		},R.color.main_tab_text_color_selected);
		mEtContent = (EditText)findViewById(R.id.et_content);
		mGridView = (GridView) findViewById(R.id.noScrollgridview);

	}

	@Override
	public void initListener() {

	}


	private OkHttpClient client = new OkHttpClient();

//	//参数类型
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("images/png");

//	private void sendPictureByXutils(){
//		final String content = mEtContent.getText().toString().trim();
//		if(StringUtils.isEmpty(content)){
//			ToastUtils.showToast(mContext,"请输入内容");
//			mEtContent.requestFocus();
//			return;
//		}
//		mLoadingDialog = new LoadingDialog(mContext);
//		mLoadingDialog.show();
//
//		RequestParams params = new RequestParams();
//		String url = ReqUrls.DEFAULT_REQ_HOST_IP+ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;
//
//
//		List<File> fileList = new ArrayList<File>();
//		JsonArray fileMetaInfo = new JsonArray();
//		for (int i = 0; i < Bimp.drr.size(); i++) {
//			String Str = Bimp.drr.get(i).substring(
//					Bimp.drr.get(i).lastIndexOf("/") + 1,
//					Bimp.drr.get(i).lastIndexOf("."));
//			File file = new File(FileUtils.SDPATH+Str+".png");
//			fileList.add(file);
////			fileList.add(FileUtils.SDPATH+Str+".JPEG");
//			JsonObject jsonObject = new JsonObject();
//			jsonObject.addProperty("name",Str+".png");
//			jsonObject.addProperty("type","1");
//			fileMetaInfo.add(jsonObject);
//
//			if(file.exists()){
//				params.setBodyEntity(new FileUploadEntity(file,"binary/octet-stream"));
//			}
//
//		}
////添加一个文本表单参数
//		String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
//
//
////		params.setContentType("multipart/form-data");
//		params.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID);
//		params.addQueryStringParameter("fileMetaInfo", fileMetaInfoStr);
//		params.addQueryStringParameter("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
//		params.addQueryStringParameter("infoText",content);
//		params.addQueryStringParameter("isPublic","1");
////		params.addBodyParameter("file",fileList,"image/jpeg");
//
//// 只包含字符串参数时默认使用BodyParamsEntity，
//// 类似于UrlEncodedFormEntity（"application/x-www-form-urlencoded"）。
////		params.addBodyParameter("name", "value");
//
//// 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
//// 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
//// 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
//// MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
//// 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
//
//
//
//
//
//
//
//
//		HttpUtils http = new HttpUtils();
//		http.send(HttpRequest.HttpMethod.POST,
//				url,
//				params,
//				new RequestCallBack<String>() {
//
//					@Override
//					public void onStart() {
//
//					}
//
//					@Override
//					public void onLoading(long total, long current, boolean isUploading) {
//						if (isUploading) {
//							ToastUtils.showToast(mContext,"upload: " + current + "/" + total);
//						} else {
//							ToastUtils.showToast(mContext,"reply: " + current + "/" + total);
//						}
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						ToastUtils.showToast(mContext,responseInfo.result);
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						ToastUtils.showToast(mContext,error.getExceptionCode() + ":" + msg);
//					}
//				});
//
//	}

	/**
	 * 发布图片
	 */
//	private void sendPicture(){
//		final String content = mEtContent.getText().toString().trim();
//		if(StringUtils.isEmpty(content)){
//			ToastUtils.showToast(mContext,"请输入内容");
//			mEtContent.requestFocus();
//			return;
//		}
//		mLoadingDialog = new LoadingDialog(mContext);
//		mLoadingDialog.show();
//
//		PostFormBuilder postFormBuilder = OkHttpUtils.post();
//
//
//		List<String> list = new ArrayList<String>();
//		JsonArray fileMetaInfo = new JsonArray();
//		if(Bimp.drr==null || Bimp.drr.isEmpty()){
//			postFormBuilder.addFile("","",null);
//		}else {
//			for (int i = 0; i < Bimp.drr.size(); i++) {
//				String Str = Bimp.drr.get(i).substring(
//						Bimp.drr.get(i).lastIndexOf("/") + 1,
//						Bimp.drr.get(i).lastIndexOf("."));
//				list.add(FileUtils.SDPATH + Str + ".png");
//
//
//				File file = new File(Bimp.drr.get(i));
//				if (file.exists()) {
//
//					JsonObject jsonObject = new JsonObject();
//					jsonObject.addProperty("name", file.getName());
//					jsonObject.addProperty("type", "1");
//					fileMetaInfo.add(jsonObject);
//
//					postFormBuilder.addFile(file.getName(), file.getName(), file);
//				}
//			}
//		}
//
//		Map<String,String> params = new HashMap<String, String>();
//		params.put("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
//		params.put("isPublic","1");
//		params.put("infoText",content);
//		params.put("fileMetaInfo",JsonUtils.toJson(fileMetaInfo));
//
//		String url = ReqUrls.DEFAULT_REQ_HOST_IP+ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;
//		postFormBuilder.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID).url(url).params(params)
//				.build().execute(new StringCallback() {
//			@Override
//			public void onError(Call call, Exception e) {
//				mLoadingDialog.cancel();
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						ToastUtils.showToast(mContext,"发布失败");
//					}
//				});
//			}
//
//			@Override
//			public void onResponse(String response) {
//				mLoadingDialog.cancel();
//				JSONObject jsonResult = null;
//				try {
//					jsonResult = new JSONObject(response);
//					int retFlag = jsonResult.getInt("retFlag");
//					if(ApiConstants.RESULT_SUCCESS.equals(""+retFlag)){
//						// 完成上传服务器后 .........
//						FileUtils.deleteDir();
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.showToast(mContext,"发布成功");
//								sendSuccess();
//							}
//						});
//
//					}else{
//						final String info = jsonResult.getString("info");
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.showToast(mContext,info);
//							}
//						});
//
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//
//			}
//		});
//
//	}
	/**
	 * 发布图片
	 */
	private void sendPicture(){
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
				// 高清的压缩图片全部就在  list 路径里面了
				// 高清的压缩过的 bmp 对象  都在 Bimp.bmp里面
				//多文件表单上传构造器
				MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				JsonArray fileMetaInfo = new JsonArray();
				for (int i = 0; i < Bimp.drr.size(); i++) {
					String filePath = FileUtils.SDPATH+Bimp.drr.get(i).substring(
							Bimp.drr.get(i).lastIndexOf("/"));
					File file = new File(filePath);
					if(file.exists()){
						JsonObject jsonObject = new JsonObject();
						jsonObject.addProperty("name",file.getName());
						jsonObject.addProperty("type",ReqUrls.MEDIA_TYPE_PICTURE+"");
						fileMetaInfo.add(jsonObject);
						RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG,file);
						multipartBuilder.addFormDataPart(file.getName(), file.getName(), fileBody);
					}
				}

				//添加表单参数
				multipartBuilder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());
				String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
				multipartBuilder.addFormDataPart("fileMetaInfo",fileMetaInfoStr);
				multipartBuilder.addFormDataPart("infoText",content);//动态内容
				multipartBuilder.addFormDataPart("isPublic","1");//是否公开 0：私有 1：公开（必填）

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
								// 完成上传服务器后 .........
								FileUtils.deletePictures();
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


	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置


		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {

			return (Bimp.drr.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
//			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				ImageView image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}

			if (position == Bimp.drr.size()) {
				image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.add_picture));
				if (position == 9) {
					image.setVisibility(View.GONE);
				}
			} else {
				Picasso.with(mContext).load(new File(Bimp.drr.get(position))).resize(DeviceInfo.dp2px(mContext,68),DeviceInfo.dp2px(mContext,68)).centerCrop().into(image);
//				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						adapter.notifyDataSetChanged();
						break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
//								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								if(bm!=null) {
									bm.recycle();
									bm = null;
								}
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public void InitGridView() {

		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				if (arg2 == Bimp.drr.size()) {
					new PopupWindows(PublishPictureActivity.this, mGridView);
				} else {
					Intent intent = new Intent(PublishPictureActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	protected void onRestart() {
		super.onRestart();
		adapter.update();
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(ViewGroup.LayoutParams.FILL_PARENT);
			setHeight(ViewGroup.LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(PublishPictureActivity.this,
							SelectPicActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private static final int SELECT_PICTURE = 0x000001;
	private String path = "";
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		FileUtil.createFile(FileUtil.PICTURE_FILE_DIR+"/camrea");
		File file = new File(Environment.getExternalStorageDirectory()+FileUtil.PICTURE_FILE_DIR+"/camrea", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case TAKE_PICTURE:

					try {
						System.out.println(path);
						Bitmap bm = Bimp.revitionImageSize(path);

						String newStr = path.substring(
								path.lastIndexOf("/") + 1,
								path.lastIndexOf("."));
						String filePath = FileUtils.saveBitmap(bm, "" + newStr);

						if(bm!=null){
							bm.recycle();
							bm = null;
							FileUtil.deleteFile(path);
						}
						Bimp.max += 1;
						Bimp.drr.add(filePath);
//						adapter.notifyDataSetChanged();
					}catch (IOException e){

					}

					break;
				case SELECT_PICTURE:

					break;
				default:
					break;
			}
		}

	}
}
