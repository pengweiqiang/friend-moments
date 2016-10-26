package com.anda.moments.ui.publish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.anda.moments.views.ToggleButton;
import com.anda.moments.views.audio.AudioRecordButton;
import com.anda.moments.views.audio.MediaManager;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
	private ToggleButton mToggleButtonIsPublic;//是否公开
	String isPublic = "0";//是否公开 1不公开 0 公开

	LoadingDialog mLoadingDialog;

	private View mViewRecordPlay;
	private View mViewRecordAnim;
	private TextView mTvSeconds;//时间

	private GridView mGridView;
	private GridAdapter adapter;


	public String filePath = "";//音频路径
	public float second = 0;//音频时间
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_publish_voice);
		super.onCreate(savedInstanceState);


		InitGridView();

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
//				sendAudio();
				send();
			}
		},R.color.main_tab_text_color_selected);
		mBtnAudio = (AudioRecordButton) findViewById(R.id.btn_record);
		mViewRecordPlay = findViewById(R.id.view_record);
		mViewRecordAnim = findViewById(R.id.voice_anim);
		mTvSeconds = (TextView)findViewById(R.id.tv_audio_second);
		mEtContent = (EditText)findViewById(R.id.et_content);
		mToggleButtonIsPublic = (ToggleButton)findViewById(R.id.toggle_is_public);
		mToggleButtonIsPublic.setToggleOn();

		mGridView = (GridView) findViewById(R.id.noScrollgridview);


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

		mToggleButtonIsPublic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				isPublic = on?"0":"1";
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
		}
//		else{
//			ToastUtils.showToast(mContext,"请录入语音");
//			return;
//		}

		mLoadingDialog = new LoadingDialog(mContext,"上传中...");
		mLoadingDialog.show();


		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;



		Map<String,String> params = new HashMap<String, String>();
		params.put("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
		try {
			String contentStr = URLEncoder.encode(URLEncoder.encode(content, "UTF-8"),"UTF-8");

			params.put("infoText",contentStr);//动态内容
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		params.put("isPublic",isPublic);

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


	private OkHttpClient client = new OkHttpClient();

	//	//参数类型
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("images/png");
	/**
	 * 发布图片+音频
	 */
	private void send(){
		client = client.newBuilder().writeTimeout(30, TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build();
		final String content = mEtContent.getText().toString().trim();
		if(StringUtils.isEmpty(content)&& (Bimp.drr == null || Bimp.drr.isEmpty())){
//			ToastUtils.showToast(mContext,"请输入内容");
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
							Bimp.drr.get(i).lastIndexOf("/"),Bimp.drr.get(i).lastIndexOf("."))+".jpg";
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

				File file = new File(filePath);

				if(file.exists()) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("name", file.getName());
					jsonObject.addProperty("type", ReqUrls.MEDIA_TYPE_AUDIO+"");//1-图片，2-音频，3-视频
					jsonObject.addProperty("audioTime",Math.round(second)+"");//音频时长
					fileMetaInfo.add(jsonObject);
					RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
					multipartBuilder.addFormDataPart(file.getName(), file.getName(), fileBody);
				}

				//添加表单参数
				multipartBuilder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());
				String fileMetaInfoStr = JsonUtils.toJson(fileMetaInfo);
				multipartBuilder.addFormDataPart("fileMetaInfo",fileMetaInfoStr);
				try {
					String contentStr = URLEncoder.encode(URLEncoder.encode(content, "UTF-8"),"UTF-8");

					multipartBuilder.addFormDataPart("infoText",contentStr);//动态内容
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				multipartBuilder.addFormDataPart("isPublic",isPublic);//是否公开 0：私有 1：公开（必填）

				RequestBody requestBody = multipartBuilder.build();

				//构造文件上传时的请求对象Request
				String url = ReqUrls.DEFAULT_REQ_HOST_IP+ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION;
				Request request = new Request.Builder().url(url)
						.post(requestBody)
						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
						.build();

				client.newCall(request).enqueue(new okhttp3.Callback() {
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
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ToastUtils.showToast(mContext,"发布成功");
										// 完成上传服务器后 .........
										FileUtils.deletePictures();
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


	/**
	 * 发送成功跳转
	 */
	private void sendSuccess(){

		if(Bimp.drr!=null) {
			Bimp.drr.clear();
			Bimp.max = 0;
//			Bimp.drr = null;
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
			if(Bimp.drr==null){
				Bimp.drr = new ArrayList<String>();
			}
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

				String filePath = FileUtils.SDPATH+Bimp.drr.get(position).substring(
						Bimp.drr.get(position).lastIndexOf("/"),Bimp.drr.get(position).lastIndexOf("."))+".jpg";
				Picasso.with(mContext).load(new File(filePath)).resize(DeviceInfo.dp2px(mContext,68),DeviceInfo.dp2px(mContext,68)).centerCrop().into(image);
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
					hideInputMethod();
					new PopupWindows(PublishVoiceActivity.this, mGridView);
				} else {
					Intent intent = new Intent(PublishVoiceActivity.this,
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
					Intent intent = new Intent(PublishVoiceActivity.this,
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
