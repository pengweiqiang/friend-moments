package com.anda.moments.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.RongCloudEvent;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.CircleMessage;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseFragmentActivity;
import com.anda.moments.ui.fragments.HomeFragment;
import com.anda.moments.ui.fragments.MyFragment;
import com.anda.moments.ui.fragments.FriendsFragment;
import com.anda.moments.utils.AESEncryptor;
import com.anda.moments.utils.FileUtil;
import com.anda.moments.utils.InputMethodUtils;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.audio.MediaManager;
import com.umeng.update.UmengUpdateAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;

public class MainActivity extends BaseFragmentActivity {
	
	//首页
	private HomeFragment mHomeFragment;
	//好友
	private FriendsFragment mFriendsFragment;
	//我的
	private MyFragment mMyFragment;
	
	private FragmentPagerAdapter mFragmentAdapter;
	private FragmentManager mFragmentManager;
	
	private RadioGroup mTabIndicators ;
	
	
	
	int[] tabIds = {R.id.home,R.id.friends,R.id.my};
	
	private int checkId = tabIds[0];

	private View mViewMessage;//消息提醒


	public LinearLayout mEditTextBody;
	public EditText mEditTextComment;//评论文本框
	private ImageView sendIv;//发送评论

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();

		initListener();

		getRongToken();
//		initRong();

		setUserInfo();
    }

	@Override
	protected void onStart() {
		super.onStart();
		UmengUpdateAgent.update(this);
	}




	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean isRefresh = intent.getBooleanExtra("refresh",false);
		if(isRefresh){
			mHomeFragment.onRefreshing();
		}
	}

	private void initView(){
    	mTabIndicators = (RadioGroup) findViewById(R.id.tabIndicators);
    	
    	mFragmentManager = getSupportFragmentManager();
    	mFragmentAdapter = new FragmentPagerAdapter(mFragmentManager) {
			
			@Override
			public int getCount() {
				return tabIds.length;
			}
			
			@Override
			public Fragment getItem(int position) {
				if (position == 0) {
					return mHomeFragment = new HomeFragment();
				} else if (position == 1) {
					return mFriendsFragment = new FriendsFragment();
				}else if (position == 2) {
					return mMyFragment = new MyFragment();
				}
				return null;
			}
		};
    	
		
		mTabIndicators
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Fragment fragment = getInstanceById(checkedId);
				hideFragment(checkedId, fragment);
				checkId = checkedId;
			}
		});
		Fragment fragment = getInstanceById(checkId);
		hideFragment(checkId, fragment);


		mEditTextComment = (EditText) findViewById(R.id.circleEt);
		sendIv = (ImageView)findViewById(R.id.sendIv);
		mEditTextBody = (LinearLayout)findViewById(R.id.editTextBodyLl);
		mViewMessage = findViewById(R.id.ll_message);
		mViewMessage.setVisibility(View.GONE);
    	
    }
	public void showMessage(int visibily){
		mViewMessage.setVisibility(visibily);
	}

	private void initListener(){
		sendIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//发布评论
				String content = mEditTextComment.getText().toString().trim();
				if(TextUtils.isEmpty(content)){
					ToastUtils.showToast(MainActivity.this,"评论内容不能为空...");
					return;
				}
				mHomeFragment.addComment(content);

				//隐藏键盘
				try {
					InputMethodUtils.hideSoftInput(mEditTextComment.getContext(), mEditTextComment);
				}catch (Exception e){

				}
				mEditTextBody.setVisibility(View.GONE);

			}
		});
	}

    public Fragment getInstanceById(int id){
    	switch (id) {
		case R.id.home:
			if(mHomeFragment == null){
				mHomeFragment = new HomeFragment();
			}
			return mHomeFragment;
		case R.id.friends:
			if(mFriendsFragment == null){
				mFriendsFragment = new FriendsFragment();
			}
			return mFriendsFragment;
		
		case R.id.my:
			if(mMyFragment == null){
				mMyFragment = new MyFragment();
			}
			return mMyFragment;

		default:
			break;
		}
    	return null;
    }

    private void hideFragment(int checkId, Fragment fragment) {
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		// 试用fragment替换activity中的main组件
		for (int i = 0; i < tabIds.length; i++) {
			if (tabIds[i] == checkId) {
				if (mFragmentManager.getFragments() != null
						&& mFragmentManager.getFragments().contains(fragment)) {
					fragmentTransaction.show(fragment);
				} else {
					fragmentTransaction.add(R.id.frameLayout, fragment);
				}
				break;
			}
		}
		if (this.checkId != checkId) {
			fragmentTransaction.hide(getInstanceById(this.checkId));
		}
		// 提交事物
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    private boolean isExit = false;

	// 退出操作
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if(mEditTextBody != null && mEditTextBody.getVisibility() == View.VISIBLE){
				mEditTextBody.setVisibility(View.GONE);
				return true;
			}
			if (isExit == false) {
				isExit = true;
				handler.sendEmptyMessageDelayed(0, 3000);
				ToastUtils.showToast(this, "再按一次退出朋友圈");
				return true;
			} else {
				FileUtil.deleteCache();
				SharePreferenceManager.saveBatchSharedPreference(MainActivity.this,Constant.FILE_NAME, "lastTime","");
				AppManager.getAppManager().finishAllActivity();
				return false;
			}
		}
		return true;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};


//	private void initRong(){
//		String rongToken = (String)SharePreferenceManager.getSharePreferenceValue(MainActivity.this,Constant.FILE_NAME, com.anda.moments.constant.api.ReqUrls.TOKEN_RONG,"");
//		if(!StringUtils.isEmpty(rongToken)){
//			connect(rongToken);
//		}
//	}

	/**
	 * 建立与融云服务器的连接
	 *
	 * @param token
	 */
	private void connect(String token) {

		if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

			/**
			 * IMKit SDK调用第二步,建立与服务器的连接
			 */
			RongIM.connect(token, new RongIMClient.ConnectCallback() {

				/**
				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
				 */
				@Override
				public void onTokenIncorrect() {

					Log.d("LoginActivity", "--onTokenIncorrect");
				}

				/**
				 * 连接融云成功
				 * @param userid 当前 token
				 */
				@Override
				public void onSuccess(String userid) {
					RongCloudEvent.getInstance().setOtherListener();
					Log.d("LoginActivity", "--onSuccess" + userid);

				}

				/**
				 * 连接融云失败
				 * @param errorCode 错误码，可到官网 查看错误码对应的注释
				 * http://www.rongcloud.cn/docs/android.html#常见错误码
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {

					Log.d("LoginActivity", "--onError" + errorCode);
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//刷新进入动态详情的数据
//		if(requestCode == HomeFragment.REQUEST_CODE_DETAIL && resultCode == RESULT_OK && data!=null){
//			mHomeFragment.onActivityResult(requestCode,resultCode,data);
//		}
	}

	/**
	 * 获取融云token
	 */
	public void getRongToken(){

		if(!StringUtils.isEmpty(GlobalConfig.TOKEN_RONG)){
			connect(GlobalConfig.TOKEN_RONG);
			return;
		}
		User user = MyApplication.getInstance().getCurrentUser();
		if(user==null){
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return;
		}
		final String userId = user.getPhoneNum();//手机号做userId
		final String name = StringUtils.isEmpty(user.getUserName())?userId:user.getUserName();
		final String portraitUri = StringUtils.isEmpty(user.getIcon())?"":user.getIcon();

		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_GET_RONGYUN_TOKEN;
		OkHttpUtils
				.get()//
				.addHeader("JSESSIONID",GlobalConfig.JSESSION_ID)
				.addParams("phoneNum",user.getPhoneNum())
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {

					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {
								String rongToken = new JSONObject(jsonObject.getString("tokenInfo")).getString("token");
								SharePreferenceManager.saveBatchSharedPreference(MainActivity.this,Constant.FILE_NAME,
										com.anda.moments.constant.api.ReqUrls.TOKEN_RONG+"_"+userId,rongToken+"_&_"
										+ System.currentTimeMillis());
								Log.e("MainActivity_GET_TOKEN",rongToken);
								connect(rongToken);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});

//		//获取token
//		ThreadUtil.getTheadPool(true).submit(new Runnable() {
//			@Override
//			public void run() {
//				SdkHttpResult result = null;
//				try {
//					result = HttpUtil.getToken(com.anda.moments.api.constant.ReqUrls.APPKEY_RONG, com.anda.moments.api.constant.ReqUrls.APPSERCERT_RONG, userId, name,
//							portraitUri);
//					if(result.getHttpCode()==200){
//						JSONObject resultJson = new JSONObject(result.getResult());
//						if(resultJson.getInt("code")==200){
//							String token = resultJson.getString("token");
//							SharePreferenceManager.saveBatchSharedPreference(MainActivity.this,Constant.FILE_NAME,com.anda.moments.constant.api.ReqUrls.TOKEN_RONG+"_"+userId,token+"_&_"
//									+ System.currentTimeMillis());
//							Log.e("MainActivity_GET_TOKEN",token);
//							connect(token);
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//		});

	}

	private void setUserInfo(){
		RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

			@Override
			public UserInfo getUserInfo(String userId) {

				User user = MyApplication.getInstance().getCurrentUser();
				final String portraitUri = StringUtils.isEmpty(user.getIcon())?"":user.getIcon();
				UserInfo userInfo = new UserInfo(user.getPhoneNum(),user.getUserName(), Uri.parse(portraitUri));
				return userInfo;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
			}

		}, true);
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
		mHomeFragment.resetVideoList();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MediaManager.resume();
	}





}
