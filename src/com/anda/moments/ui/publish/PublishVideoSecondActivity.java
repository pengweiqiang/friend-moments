package com.anda.moments.ui.publish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import com.anda.GlobalConfig;
import com.anda.gson.JsonArray;
import com.anda.gson.JsonObject;
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
import com.anda.moments.utils.publish.FileUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.Headers;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.MultipartBuilder;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
import com.yqritc.scalablevideoview.ScalableVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;
import sz.itguy.utils.FileUtil;

/**
 * 发布视频
 * @author pengweiqiang
 *
 */
public class PublishVideoSecondActivity extends BaseActivity {

	private EditText mEtContent;
	ActionBar mActionBar;

	LoadingDialog mLoadingDialog;

	public static final String KEY_FILE_PATH = "file_path";

	private String filePath;
	private String firstPicturePath;//缩略图

	private ScalableVideoView mScalableVideoView;
	private ImageView mPlayImageView;
	private ImageView mThumbnailImageView;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_publish_video_second);
		super.onCreate(savedInstanceState);

		filePath = getIntent().getStringExtra(KEY_FILE_PATH);
		Log.d(TAG, filePath);
		if (TextUtils.isEmpty(filePath)) {
			Toast.makeText(this, "视频路径错误", Toast.LENGTH_SHORT).show();
			AppManager.getAppManager().finishActivity();
			return;
		}

		try {
			// 这个调用是为了初始化mediaplayer并让它能及时和surface绑定
			mScalableVideoView.setDataSource("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		mThumbnailImageView.setImageBitmap(getVideoThumbnail(filePath));
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
//				sendVideoByXutils();
				sendVideo();
			}
		},R.color.main_tab_text_color_selected);
		mEtContent = (EditText)findViewById(R.id.et_content);


		mScalableVideoView = (ScalableVideoView) findViewById(R.id.video_view);


		mPlayImageView = (ImageView) findViewById(R.id.playImageView);

		mThumbnailImageView = (ImageView) findViewById(R.id.thumbnailImageView);

	}

	/**
	 * 获取视频缩略图（这里获取第一帧）
	 * @param filePath
	 * @return
	 */
	public Bitmap getVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(1));
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		finally {
			try {
				retriever.release();
			}
			catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		firstPicturePath = FileUtil.saveMediaFirstPicture(bitmap,System.currentTimeMillis()+"");
		return bitmap;
	}

	private boolean isVolume = true;
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.video_view:
				mScalableVideoView.stop();
				mPlayImageView.setVisibility(View.VISIBLE);
				mThumbnailImageView.setVisibility(View.VISIBLE);
				break;
			case R.id.playImageView:
				try {
					mScalableVideoView.setDataSource(filePath);
					mScalableVideoView.setVolume(0,0);
					mScalableVideoView.setLooping(true);
					mScalableVideoView.prepare(new MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							mScalableVideoView.start();
//							while(isVolume){
//								System.out.println(mp.getCurrentPosition()+"  ");
//								if(mp.getCurrentPosition()>100) {
//									mp.setVolume(10, 10);
//									isVolume = false;
//								}
//							}
						}
					});

					mPlayImageView.setVisibility(View.GONE);
					mThumbnailImageView.setVisibility(View.GONE);
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());
					Toast.makeText(this, "播放视频异常", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	@Override
	public void initListener() {

	}



//	private OkHttpClient client = new OkHttpClient();
//	//参数类型
//	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

	private void sendVideoByXutils(){
		final String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content)){
			ToastUtils.showToast(mContext,"请输入内容");
			mEtContent.requestFocus();
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();

		String url = ReqUrls.DEFAULT_REQ_HOST_IP+ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;

		JsonArray fileMetaInfo = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name","file_video.mp4");
		jsonObject.addProperty("type","2");
		fileMetaInfo.add(jsonObject);

//添加一个文本表单参数
		String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);

		RequestParams params = new RequestParams();
//		params.setContentType("multipart/form-data");
		params.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID);
//		params.addBodyParameter("fileMetaInfo", fileMetaInfoStr);
		params.addBodyParameter("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
		params.addBodyParameter("infoText",content);
		params.addBodyParameter("isPublic","1");

		File fileVideo = new File(filePath);
		InputStream inputStreamVideo = null;
		try {
			inputStreamVideo = new FileInputStream(fileVideo);

//			params.addBodyParameter("file", inputStreamVideo, fileVideo.length(), fileVideo.getName(), "video/mp4");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST,
				url,
				params,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						if (isUploading) {
							ToastUtils.showToast(mContext,"upload: " + current + "/" + total);
						} else {
							ToastUtils.showToast(mContext,"reply: " + current + "/" + total);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						ToastUtils.showToast(mContext,responseInfo.result);
						mLoadingDialog.cancel();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mLoadingDialog.cancel();
						ToastUtils.showToast(mContext,error.getExceptionCode() + ":" + msg);
					}
				});

	}

	/**
	 * 发布视频
	 */
	public void sendVideo(){
		final String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content)){
			ToastUtils.showToast(mContext,"请输入内容");
			mEtContent.requestFocus();
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext,"上传中...");
		mLoadingDialog.show();

		File file = new File(filePath);
		final File firstPictureFile = new File(firstPicturePath);
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;

		JsonArray fileMetaInfo = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", file.getName());
		jsonObject.addProperty("type", ReqUrls.MEDIA_TYPE_VIDEO+"");
//		jsonObject.addProperty("icon",firstPictureFile.getName());//视频第一帧
		fileMetaInfo.add(jsonObject);

		Map<String,String> params = new HashMap<String, String>();
		params.put("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
		params.put("infoText",content);
		params.put("isPublic","1");

		String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
		params.put("fileMetaInfo",fileMetaInfoStr);

		OkHttpUtils.post().addFile(file.getName(),file.getName(),file)
//				.addFile(firstPictureFile.getName(),firstPictureFile.getName(),firstPictureFile)
				.url(url)
				.params(params)
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.build()
				.connTimeOut(20000)
				.readTimeOut(20000)
				.writeTimeOut(20000)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						mLoadingDialog.cancel();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtils.showToast(mContext, "发布失败");
							}
						});
					}

//					@Override
//					public void inProgress(float progress) {
//						super.inProgress(progress);
////						Log.i(TAG,"p:"+progress);
//						mLoadingDialog.setText(progress+"%");
//					}

					@Override
					public void onResponse(String response) {
						mLoadingDialog.cancel();
						try {
							JSONObject jsonResult = new JSONObject(response);
							int retFlag = jsonResult.getInt("retFlag");
							if (ApiConstants.RESULT_SUCCESS.equals("" + retFlag)) {
								deleteFile();
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ToastUtils.showToast(mContext, "发布成功");
										sendSuccess();
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

				});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		deleteFile();
	}

	private void deleteFile(){
		// 完成上传服务器后 .........
		FileUtil.deleteFile(filePath);
		FileUtil.deleteFile(firstPicturePath);
	}

	/**
	 * 发布视频
	 */
//	private void sendVideo(){
//		final String content = mEtContent.getText().toString().trim();
//		if(StringUtils.isEmpty(content)){
//			ToastUtils.showToast(mContext,"请输入内容");
//			mEtContent.requestFocus();
//			return;
//		}
//		mLoadingDialog = new LoadingDialog(mContext);
//		mLoadingDialog.show();
//		ThreadUtil.getTheadPool(true).submit(new Runnable() {
//			@Override
//			public void run() {
//
//				File file = new File(filePath);
//
//				JsonArray fileMetaInfo = new JsonArray();
//				JsonObject jsonObject = new JsonObject();
//				jsonObject.addProperty("name", file.getName());
//				jsonObject.addProperty("type", "3");
//				fileMetaInfo.add(jsonObject);
//
//				//多文件表单上传构造器
//				MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
//
//
//				RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
////						multipartBuilder.addPart(Headers.of("Content-Disposition","form-data;name=file_"+i+";filename="+file.getName()),fileBody);
////						multipartBuilder.addFormDataPart("file_"+i,file.getName(), fileBody);
//				multipartBuilder.addFormDataPart(file.getName(), file.getName(), fileBody);
//
//				//添加一个文本表单参数
//				multipartBuilder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());
//				String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
//				multipartBuilder.addFormDataPart("fileMetaInfo", fileMetaInfoStr);
//				multipartBuilder.addFormDataPart("infoText", content);//动态内容
//				multipartBuilder.addFormDataPart("isPublic", "1");//是否公开 0：私有 1：公开（必填）
//
//				RequestBody requestBody = multipartBuilder.build();
//				//构造文件上传时的请求对象Request
//				String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;
//				Request request = new Request.Builder().url(url)
//						.post(requestBody)
//						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
//						.build();
//				Call call = client.newCall(request);
//				call.enqueue(new Callback() {
//
//					@Override
//					public void onFailure(Request request, IOException e) {
//						mLoadingDialog.cancel();
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.showToast(mContext, "发布失败");
//							}
//						});
//					}
//
//					@Override
//					public void onResponse(Response response) throws IOException {
//						mLoadingDialog.cancel();
//						try {
//							if (!response.isSuccessful()) {
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										ToastUtils.showToast(mContext, "发布失败");
//									}
//								});
//
//							} else {
//								try {
//									JSONObject jsonResult = new JSONObject(response.body().string());
//									int retFlag = jsonResult.getInt("retFlag");
//									if (ApiConstants.RESULT_SUCCESS.equals("" + retFlag)) {
//
//										// 完成上传服务器后 .........
//										FileUtil.deleteFile(filePath);
//										runOnUiThread(new Runnable() {
//											@Override
//											public void run() {
//												ToastUtils.showToast(mContext, "发布成功");
//												sendSuccess();
//											}
//										});
//
//									} else {
//										final String info = jsonResult.getString("info");
//										runOnUiThread(new Runnable() {
//											@Override
//											public void run() {
//												ToastUtils.showToast(mContext, info);
//											}
//										});
//
//									}
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//
//							}
//						} catch (IOException e) {
//							mLoadingDialog.cancel();
//							e.printStackTrace();
//						}
//
//					}
//				});
//
//			}
//
//
//		});
//
//
//	}

	/**
	 * 发送成功跳转
	 */
	private void sendSuccess(){

		AppManager.getAppManager().finishActivity(PublishActivity.class);
		AppManager.getAppManager().finishActivity();
		Intent intent = new Intent(mContext, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("refresh",true);
		startActivity(intent);
	}






}
