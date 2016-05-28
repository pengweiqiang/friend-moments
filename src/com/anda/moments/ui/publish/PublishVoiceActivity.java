package com.anda.moments.ui.publish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.audio.AudioRecordButton;
import com.anda.moments.views.audio.MediaManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
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
	private View mViewRecordAnim;
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
		mViewRecordAnim = findViewById(R.id.voice_anim);
		mTvSeconds = (TextView)findViewById(R.id.tv_audio_second);
		mEtContent = (EditText)findViewById(R.id.et_content);


	}


	AnimationDrawable animation;
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
			second = Math.round(second);
			mTvSeconds.setText(second+"''");
		}

	}

	/**
	 * 播放录音
	 */
	private void playAudioRecord(){
		if(StringUtils.isEmpty(filePath) || !new File(filePath).exists()){
			ToastUtils.showToast(mContext,"请先录入语音");
			return;
		}
		mViewRecordAnim.setBackgroundResource(R.drawable.anim_play_audio);
//		if(animation==null) {
			animation = (AnimationDrawable) mViewRecordAnim.getBackground();
//		}

		animation.start();
		MediaManager.playSound(filePath,
				new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						animation.stop();
						mViewRecordAnim
								.setBackgroundResource(R.drawable.icon_voice_anim_3);
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MediaManager.release();
		FileUtil.deleteFile(filePath);
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



	/**
	 * 发布音频
	 */
	private void sendAudio(){
		final String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content) && TextUtils.isEmpty(filePath)){
			mEtContent.requestFocus();
			return;
		}
		File file = new File(filePath);
		PostFormBuilder postFormBuilder = OkHttpUtils.post();

		JsonArray fileMetaInfo = new JsonArray();
		if(file.exists()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", file.getName());
			jsonObject.addProperty("type", ReqUrls.MEDIA_TYPE_AUDIO+"");//1-图片，2-音频，3-视频
			jsonObject.addProperty("audioTime",Math.round(second)+"");//音频时长
			fileMetaInfo.add(jsonObject);
			postFormBuilder.addFile(file.getName(),file.getName(),file);
		}else{
			ToastUtils.showToast(mContext,"请录入语音");
			return;
		}

		mLoadingDialog = new LoadingDialog(mContext,"上传中...");
		mLoadingDialog.show();


		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;



		Map<String,String> params = new HashMap<String, String>();
		params.put("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
		params.put("infoText",content);
		params.put("isPublic","1");

		String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
		params.put("fileMetaInfo",fileMetaInfoStr);

		postFormBuilder.url(url)
				.params(params)
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.build()
				.connTimeOut(20000)
				.readTimeOut(20000)
				.writeTimeOut(20000)
				.execute(new Callback() {
					@Override
					public String parseNetworkResponse(Response response) throws Exception {
						return response.body().string();
					}

//					@Override
//					public void inProgress(float progress) {
//						mLoadingDialog.setText(progress+"%");
//						super.inProgress(progress);
//					}

					@Override
					public void onError(Call call, Exception e) {
						mLoadingDialog.cancel();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtils.showToast(mContext, "发布失败,稍后请重试");
							}
						});
					}

					@Override
					public void onResponse(Object response) {
						mLoadingDialog.cancel();
						try {
							JSONObject jsonResult = new JSONObject((String)response);
							int retFlag = jsonResult.getInt("retFlag");
							if(ApiConstants.RESULT_SUCCESS.equals(""+retFlag)){
								// 完成上传服务器后 .........
//								FileUtils.deleteAudioDir();
								FileUtil.deleteFile(filePath);
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
//				.execute(new StringCallback() {
//					@Override
//					public void onError(Call call, Exception e) {
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
//					public void onResponse(String response) {
//						mLoadingDialog.cancel();
//						try {
//							JSONObject jsonResult = new JSONObject(response);
//							int retFlag = jsonResult.getInt("retFlag");
//							if(ApiConstants.RESULT_SUCCESS.equals(""+retFlag)){
//								// 完成上传服务器后 .........
////								FileUtils.deleteAudioDir();
//								FileUtils.delFile(filePath);
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										ToastUtils.showToast(mContext,"发布成功");
//										sendSuccess();
//									}
//								});
//
//							}else{
//								final String info = jsonResult.getString("info");
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										ToastUtils.showToast(mContext,info);
//									}
//								});
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//					}
//
//				});
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
