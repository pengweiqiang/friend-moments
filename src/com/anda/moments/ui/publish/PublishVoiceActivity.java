package com.anda.moments.ui.publish;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

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
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.utils.publish.Bimp;
import com.anda.moments.utils.publish.FileUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.audio.AudioRecordButton;
import com.anda.moments.views.audio.MediaManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sz.itguy.utils.FileUtil;

/**
 * 发布语音
 * @author pengweiqiang
 *
 */
public class PublishVoiceActivity extends BaseActivity {

	private AudioRecordButton mBtnAudio;//录制语音
	ActionBar mActionBar;
	EditText mEtContent;

	LoadingDialog mLoadingDialog;

	private View mViewRecordPlay;
	private TextView mTvSeconds;//时间


	public String filePath = "";//音频路径
	public float second = 0;//音频时间
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_publish_voice);
		super.onCreate(savedInstanceState);




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
				sendAudio();
			}
		},R.color.main_tab_text_color_selected);
		mBtnAudio = (AudioRecordButton) findViewById(R.id.btn_record);
		mViewRecordPlay = findViewById(R.id.view_record);
		mTvSeconds = (TextView)findViewById(R.id.tv_audio_second);
		mEtContent = (EditText)findViewById(R.id.et_content);
	}


	@Override
	public void initListener() {
		mBtnAudio.setAudioRecordFinishListener(new MyAudioRecordFinishListener());
		mViewRecordPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playAudioRecord();
			}
		});
	}

	class MyAudioRecordFinishListener implements AudioRecordButton.AudioRecordFinishListener {

		@Override
		public void onFinish(float second, String filePath) {
			//录制结束
			PublishVoiceActivity.this.second = second;
			PublishVoiceActivity.this.filePath = filePath;
			mTvSeconds.setText(Math.round(second)+"''");
		}

	}

	/**
	 * 播放录音
	 */
	private void playAudioRecord(){
		if(StringUtils.isEmpty(filePath) || !new File(filePath).exists()){
			return;
		}
		MediaManager.playSound(filePath,
				new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
//						voiceAnim
//								.setBackgroundResource(R.drawable.icon_voice_ripple);
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MediaManager.release();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MediaManager.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
	}

	private OkHttpClient client = new OkHttpClient();
	/**
	 * 发布录音
	 */
	private void sendAudio(){
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

				File file = new File(filePath);

				JsonArray fileMetaInfo = new JsonArray();
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name",file.getName());
				jsonObject.addProperty("type","2");//1-图片，2-音频，3-视频
				fileMetaInfo.add(jsonObject);

				//多文件表单上传构造器
				MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);


				RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
				multipartBuilder.addFormDataPart(file.getName(), file.getName(), fileBody);

				//添加一个文本表单参数
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


				try {
					Response response = client.newCall(request).execute();

					mLoadingDialog.cancel();
					if(!response.isSuccessful()){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtils.showToast(mContext,"发布失败");
							}
						});

					}else{
						try {
							JSONObject jsonResult = new JSONObject(response.body().string());
							int retFlag = jsonResult.getInt("retFlag");
							if(ApiConstants.RESULT_SUCCESS.equals(""+retFlag)){
								// 完成上传服务器后 .........
//								FileUtils.deleteAudioDir();
								FileUtils.delFile(filePath);
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
				} catch (IOException e) {
					mLoadingDialog.cancel();
					e.printStackTrace();
				}

			}

		});


	}

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
