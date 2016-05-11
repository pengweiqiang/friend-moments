package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.anda.moments.ui.publish.PublishActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yqritc.scalablevideoview.ScalableVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import sz.itguy.utils.FileUtil;


/**
 * 发布视频
 * @author pengweiqiang
 *
 */
public class VideoDetailActivity extends BaseActivity {

	ActionBar mActionBar;

	LoadingDialog mLoadingDialog;

	public static final String KEY_FILE_PATH = "file_path";

	private String filePath;
	private String firstPicturePath;

	private ScalableVideoView mScalableVideoView;
	private ImageView mPlayImageView;
	private ImageView mThumbnailImageView;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_video_detail);
		super.onCreate(savedInstanceState);

		filePath = getIntent().getStringExtra(KEY_FILE_PATH);
		firstPicturePath = getIntent().getStringExtra("firstPicture");
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

//		mThumbnailImageView.setImageBitmap(getVideoThumbnail(filePath));


		try {

			String subfolder =  FileUtil.createFile(FileUtil.DOWNLOAD_MEDIA_FILE_DIR);
			String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
			File videoPath = new File(subfolder,fileName);
			if(videoPath.exists()){
				filePath = videoPath.getAbsolutePath();
			}
			mScalableVideoView.setDataSource(filePath);
//					mScalableVideoView.setVolume(0,0);
			mScalableVideoView.setLooping(true);
			mScalableVideoView.prepare(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mScalableVideoView.start();
				}
			});

			mPlayImageView.setVisibility(View.GONE);
			mThumbnailImageView.setVisibility(View.GONE);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
			Toast.makeText(this, "播放视频异常", Toast.LENGTH_SHORT).show();
		}

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
		mActionBar.setTitle("视频详情");



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
		return bitmap;
	}


	@Override
	protected void onStop() {
		super.onStop();
		mScalableVideoView.stop();
		mScalableVideoView.release();
	}

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
//					mScalableVideoView.setVolume(0,0);
					mScalableVideoView.setLooping(true);
					mScalableVideoView.prepare(new MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							mScalableVideoView.start();
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action){
			case MotionEvent.ACTION_UP:
				AppManager.getAppManager().finishActivity();
			break;
		}
		return super.onTouchEvent(event);
	}
}
