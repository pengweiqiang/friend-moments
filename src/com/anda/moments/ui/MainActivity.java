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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseFragmentActivity;
import com.anda.moments.ui.fragments.HomeFragment;
import com.anda.moments.ui.fragments.MyFragment;
import com.anda.moments.ui.fragments.FriendsFragment;
import com.anda.moments.utils.AESEncryptor;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.utils.rong.HttpUtil;
import com.anda.moments.utils.rong.SdkHttpResult;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.FormEncodingBuilder;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
        UmengUpdateAgent.update(this);


		getRongToken();

		setUserInfo();
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

			if(mHomeFragment.mEditTextBody != null && mHomeFragment.mEditTextBody.getVisibility() == View.VISIBLE){
				mHomeFragment.mEditTextBody.setVisibility(View.GONE);
				return true;
			}
			if (isExit == false) {
				isExit = true;
				handler.sendEmptyMessageDelayed(0, 3000);
				ToastUtils.showToast(this, "再按一次退出朋友圈");
				return true;
			} else {
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

					Log.d("LoginActivity", "--onSuccess" + userid);
//					startActivity(new Intent(LoginActivity.this, MainActivity.class));
//					AppManager.getAppManager().finishActivity();



				}

				/**
				 * 连接融云失败
				 * @param errorCode 错误码，可到官网 查看错误码对应的注释
				 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {

					Log.d("LoginActivity", "--onError" + errorCode);
				}
			});
		}
	}



	/**
	 * 获取融云token
	 */
	public void getRongToken(){

//		String token = "yGajkmQC9/DeLpIfz1XZvGNmRv3vVKUm5Pd3X59B3Zrjb4e72wACJNgFlqmA/Pmn/WKLLppfBIugP+UQYLzgjc6lsSvZ3KbZ";
//		connect(token);
		User user = MyApplication.getInstance().getCurrentUser();
		final String userId = user.getPhoneNum();//手机号做userId
		final String name = StringUtils.isEmpty(user.getUserName())?userId:user.getUserName();
		final String portraitUri = StringUtils.isEmpty(user.getIcon())?"":user.getIcon();

		//获取token
			ThreadUtil.getTheadPool(true).submit(new Runnable() {
				@Override
				public void run() {
					SdkHttpResult result = null;
					try {
						result = HttpUtil.getToken(ReqUrls.APPKEY_RONG, ReqUrls.APPSERCERT_RONG, userId, name,
                                portraitUri);
						if(result.getHttpCode()==200){
							JSONObject resultJson = new JSONObject(result.getResult());
							if(resultJson.getInt("code")==200){
								String token = resultJson.getString("token");
								SharePreferenceManager.saveBatchSharedPreference(MainActivity.this,Constant.FILE_NAME,com.anda.moments.constant.api.ReqUrls.TOKEN_RONG,token);
								Log.e("MainActivity_GET_TOKEN",token);
								connect(token);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});



//		User user = MyApplication.getInstance().getCurrentUser();
//		//测试token
//		String token = "5wLzR6P9/EpPd2wW9qmjIyauN5QPjImrHh1qCPp60JMDp1kwaSDEq2SM0YgN5kYZ0tn7uWz3mqcVhoxLgAvk2g==";
//
//		if(user.getUserId()==null){
//			token = "y1XeKpFw6q6lE5oY2upqGmNmRv3vVKUm5Pd3X59B3Zrjb4e72wACJHTJ02W4Du7ikJC1Yc2oVfmUFT8Da0IZdw==";
//		}else {
//			if ("richard1".equals(user.getUserId())) {
//				token = "5wLzR6P9/EpPd2wW9qmjIyauN5QPjImrHh1qCPp60JMDp1kwaSDEq2SM0YgN5kYZ0tn7uWz3mqcVhoxLgAvk2g==";
//			} else if ("2642".equals(user.getUserId())) {
//				token = "y1XeKpFw6q6lE5oY2upqGmNmRv3vVKUm5Pd3X59B3Zrjb4e72wACJHTJ02W4Du7ikJC1Yc2oVfmUFT8Da0IZdw==";
//			}
//		}

//		SharePreferenceManager.saveBatchSharedPreference(MainActivity.this,Constant.FILE_NAME,com.anda.moments.constant.api.ReqUrls.TOKEN_RONG,token);
//
//		connect(token);
//		OkHttpClient client = new OkHttpClient();
//
//		JSONObject requestJson = new JSONObject();
//
//		try {
//			requestJson.put("userId",user.getId());
//			requestJson.put("name",);
//			requestJson.put("portraitUri", StringUtils.isEmpty(user.getIcon())?"http://ww2.sinaimg.cn/crop.0.0.1080.1080.1024/d773ebfajw8eum57eobkwj20u00u075w.jpg":user.getIcon());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
////		requestJson.put();
////		requestJson.put("RC-Nonce",nonce);
////		requestJson.put("RC-Timestamp",timestamp);
////		requestJson.put("RC-Signature",sign);
////		requestJson.put("Content-Type",contentType);
//
//		String nonce = String.valueOf(Math.random() * 1000000);
//		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
//		StringBuilder toSign = new StringBuilder(appSecret).append(nonce)
//				.append(timestamp);
//		String sign = AESEncryptor.hexSHA1(toSign.toString());
//
//		RequestBody body = RequestBody.create(JSON,requestJson.toString());
//
//		Request request = new Request.Builder()
//		.url("https://api.cn.ronghub.com/user/getToken.json")
//		.addHeader("RC-App-Key",appKey).addHeader("RC-Nonce",nonce)
//				.addHeader("RC-Timestamp",timestamp).addHeader("RC-Signature",sign).addHeader("Content-Type",contentType)
//		.post(body)
//		.build();
//		client.newCall(request).enqueue(new Callback() {
//			@Override
//			public void onFailure(Request request, IOException e) {
//				e.printStackTrace();
//			}
//
//			@Override
//			public void onResponse(Response response) throws IOException {
//				if (!response.isSuccessful()) {
//					String token = "5wLzR6P9/EpPd2wW9qmjIyauN5QPjImrHh1qCPp60JMDp1kwaSDEq2SM0YgN5kYZ0tn7uWz3mqcVhoxLgAvk2g==";
//					connect(token);
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							ToastUtils.showToast(MainActivity.this, "获取融云Token失败");
//						}
//					});
//					return;
//				}
//				String result = response.body().toString();
//				System.out.println(response.body().string());
//				JSONObject object = null;
//				try {
//					object = new JSONObject(result);
//					JSONObject jobj = object.getJSONObject("result");
//
//					if (object.getInt("code") == 200) {
//						String token = jobj.getString("token");
//						SharePreferenceManager.saveBatchSharedPreference(MainActivity.this,Constant.FILE_NAME,com.anda.moments.constant.api.ReqUrls.TOKEN_RONG,token);
//						connect(token);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

}
