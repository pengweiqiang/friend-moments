package com.anda.moments.ui.publish;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.LoadingDialog;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import sz.itguy.utils.FileUtil;
import sz.itguy.wxlikevideo.camera.CameraHelper;
import sz.itguy.wxlikevideo.recorder.WXLikeVideoRecorder;
import sz.itguy.wxlikevideo.views.CameraPreviewView;

/**
 * 录制视频
 * @author pengweiqiang
 *
 */
public class PublishVideoActivity extends BaseActivity implements View.OnTouchListener {

	private static final String TAG = "NewRecordVideoActivity";

	// 输出宽度
	private static final int OUTPUT_WIDTH = 320;
	// 输出高度
	private static final int OUTPUT_HEIGHT = 240;
	// 宽高比
	private static final float RATIO = 1f * OUTPUT_WIDTH / OUTPUT_HEIGHT;

	private Camera mCamera;

	private WXLikeVideoRecorder mRecorder;

	private static final int CANCEL_RECORD_OFFSET = -100;
	private float mDownX, mDownY;
	private boolean isCancelRecord = false;

	private ProgressBar recordProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int cameraId = CameraHelper.getDefaultCameraID();
		// Create an instance of Camera
		mCamera = CameraHelper.getCameraInstance(cameraId);
		if (null == mCamera) {
			ToastUtils.showToast(mContext,"打开相机失败!");
			finish();
			return;
		}
		// 初始化录像机
		mRecorder = new WXLikeVideoRecorder(this, FileUtil.MEDIA_FILE_DIR);
		mRecorder.setOutputSize(OUTPUT_WIDTH, OUTPUT_HEIGHT);

		setContentView(R.layout.activity_publish_video);
		CameraPreviewView preview = (CameraPreviewView) findViewById(R.id.camera_preview);
		preview.setCamera(mCamera, cameraId);

		recordProgressBar = (ProgressBar) findViewById(R.id.record_progress_bar);


		mRecorder.setCameraPreviewView(preview);

		findViewById(R.id.button_start).setOnTouchListener(this);

//		((TextView) findViewById(R.id.filePathTextView)).setText("请在" + FileUtil.MEDIA_FILE_DIR + "查看录制的视频文件");
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mRecorder != null) {
			boolean recording = mRecorder.isRecording();
			// 页面不可见就要停止录制
			mRecorder.stopRecording();
			// 录制时退出，直接舍弃视频
			if (recording) {
				FileUtil.deleteFile(mRecorder.getFilePath());
			}
		}
		releaseCamera();              // release the camera immediately on pause event
		AppManager.getAppManager().finishActivity();
	}

	private void releaseCamera() {
		if (mCamera != null){
			mCamera.setPreviewCallback(null);
			// 释放前先停止预览
			mCamera.stopPreview();
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}

	/**
	 * 开始录制
	 */
	private void startRecord() {

		if (mRecorder.isRecording()) {
			Toast.makeText(this, "正在录制中…", Toast.LENGTH_SHORT).show();

			return;
		}

		// initialize video camera
		if (prepareVideoRecorder()) {
			// 录制视频
			if (!mRecorder.startRecording()) {
				Toast.makeText(this, "录制失败…", Toast.LENGTH_SHORT).show();
			}else{
				startTime = System.currentTimeMillis();
				showRecordProgress();
				Log.e("NewRecord","开始录制.....1111111111");
			}
		}
	}


	/**
	 * 准备视频录制器
	 * @return
	 */
	private boolean prepareVideoRecorder(){
		if (!FileUtil.isSDCardMounted()) {
			Toast.makeText(this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private Timer timer;// 计时器
	private int time = 0;//

	private void showRecordProgress() {
		time = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(time++);
			}
		}, 0, 15);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what >1000) {
				stopRecord();//停止录像
			} else {
				if(mRecorder.isRecording()) {
					recordProgressBar.setProgress(msg.what);
				}
			}
		};
	};

	private long startTime = 0;
	/**
	 * 停止录制
	 */
	private void stopRecord() {
		mRecorder.stopRecording();
		if(timer!=null) {
			timer.cancel();
		}
		String videoPath = mRecorder.getFilePath();

		if(System.currentTimeMillis()-startTime<2000){
			ToastUtils.showToast(mContext,"录制时间太短");
			recordProgressBar.setProgress(0);
			FileUtil.deleteFile(videoPath);
			return;
		}
		Log.e("NewRecord",((System.currentTimeMillis()-startTime)/1000)+"s 结束录制.....222222222");


		// 没有录制视频
		if (null == videoPath) {
			return;
		}
		// 若取消录制，则删除文件，否则通知宿主页面发送视频
		if (isCancelRecord) {
			FileUtil.deleteFile(videoPath);
		} else {
			// 告诉宿主页面录制视频的路径
			startActivity(new Intent(this, PublishVideoSecondActivity.class).putExtra(PublishVideoSecondActivity.KEY_FILE_PATH, videoPath));
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isCancelRecord = false;
				mDownX = event.getX();
				mDownY = event.getY();
				startRecord();

				break;
			case MotionEvent.ACTION_MOVE:
				if (!mRecorder.isRecording())
					return false;

				float y = event.getY();
				if (y - mDownY < CANCEL_RECORD_OFFSET) {
					if (!isCancelRecord) {
						// cancel record
						isCancelRecord = true;
						Toast.makeText(this, "cancel record", Toast.LENGTH_SHORT).show();
					}
				} else {
					isCancelRecord = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				stopRecord();
				break;
		}

		return true;
	}

	/**
	 * 开始录制失败回调任务
	 *
	 * @author Martin
	 */
	public static class StartRecordFailCallbackRunnable implements Runnable {

		private WeakReference<PublishVideoActivity> mNewRecordVideoActivityWeakReference;

		public StartRecordFailCallbackRunnable(PublishVideoActivity activity) {
			mNewRecordVideoActivityWeakReference = new WeakReference<PublishVideoActivity>(activity);
		}

		@Override
		public void run() {
			PublishVideoActivity activity;
			if (null == (activity = mNewRecordVideoActivityWeakReference.get()))
				return;

			String filePath = activity.mRecorder.getFilePath();
			if (!TextUtils.isEmpty(filePath)) {
				FileUtil.deleteFile(filePath);
				Toast.makeText(activity, "Start record failed.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 停止录制回调任务
	 *
	 * @author Martin
	 */
	public static class StopRecordCallbackRunnable implements Runnable {

		private WeakReference<PublishVideoActivity> mNewRecordVideoActivityWeakReference;

		public StopRecordCallbackRunnable(PublishVideoActivity activity) {
			mNewRecordVideoActivityWeakReference = new WeakReference<PublishVideoActivity>(activity);
		}

		@Override
		public void run() {
			PublishVideoActivity activity;
			if (null == (activity = mNewRecordVideoActivityWeakReference.get()))
				return;

			String filePath = activity.mRecorder.getFilePath();
			if (!TextUtils.isEmpty(filePath)) {
				if (activity.isCancelRecord) {
					FileUtil.deleteFile(filePath);
				} else {
					Toast.makeText(activity, "Video file path: " + filePath, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null){
			timer.cancel();
		}
	}

	@Override
	public void initView() {

	}

	@Override
	public void initListener() {

	}
}
