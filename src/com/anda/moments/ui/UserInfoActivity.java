package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.ToggleButton;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;

/**
 * 个人信息(自己、以及好友信息)
 * @author pengweiqiang
 *
 */
public class UserInfoActivity extends BaseActivity {

	ActionBar mActionbar;
	
	private View mBtnSign,mBtnRemarks;
	private TextView mTvUserName,mTvUserNameHead,mTvUserId,mTvSex,mTvAddressDetail,mTvAddressArea,mTvUserSign;
	private TextView mTvUserDesc;

	//好友的其他信息
	public ToggleButton mTogglePublic,mToggleFriendsPublic;

	private Button mBtnAddFriends;//添加好友
	private Button mBtnSendMsg;//发送消息


	private ImageView mIvHeadBg;
	private ImageView mIvUserHead;//头像

	LoadingDialog mLoadingDialog;

	String phoneNum = "";
	int flag = 0;//0未添加好友 1已接受好友
	User user;

	private int width,height;
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_info);
		super.onCreate(savedInstanceState);

		ViewGroup.LayoutParams paramsImageView = mIvHeadBg.getLayoutParams();
		paramsImageView.width = DeviceInfo.getDisplayMetricsWidth(UserInfoActivity.this);
		paramsImageView.height = (int) (paramsImageView.width * 1.0 / 1080 * 480);
		width = paramsImageView.width;
		height = paramsImageView.height;
		mIvHeadBg.setLayoutParams(paramsImageView);

		user = (User)this.getIntent().getSerializableExtra("user");
		phoneNum = user.getPhoneNum();
		flag = user.getFlag();
		showData();

		getData();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		getData();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		user = (User)intent.getSerializableExtra("user");
		phoneNum = user.getPhoneNum();
		flag = user.getFlag();
		showData();
		getData();
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("详细资料");
		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mBtnSign = findViewById(R.id.rl_user_sign);
		mBtnRemarks = findViewById(R.id.rl_remarks);
//		mBtnAddSystem = findViewById(R.id.add_system);

		mIvHeadBg = (ImageView)findViewById(R.id.iv_bg_head);
		mIvUserHead = (ImageView)findViewById(R.id.iv_user_head);

		mTvUserName = (TextView) findViewById(R.id.tv_user_name);
		mTvUserId = (TextView)findViewById(R.id.tv_user_id);
		mTvSex = (TextView)findViewById(R.id.tv_sex);
		mTvAddressDetail = (TextView)findViewById(R.id.tv_address_detail);
		mTvAddressArea = (TextView)findViewById(R.id.tv_address_area);
		mTvUserSign = (TextView)findViewById(R.id.tv_user_sign);
		mTvUserNameHead = (TextView)findViewById(R.id.tv_user_name_head);
		mTvUserDesc = (TextView)findViewById(R.id.tv_user_desc);

		mBtnAddFriends = (Button)findViewById(R.id.btn_add_friend);
		mBtnSendMsg = (Button)findViewById(R.id.btn_send_msg);


		mTogglePublic = (ToggleButton) findViewById(R.id.toggle_is_public);
		mToggleFriendsPublic = (ToggleButton) findViewById(R.id.toggle_friend_public);

	}

	@Override
	public void initListener() {
		mBtnSign.setOnClickListener(onClickListener);
		mBtnRemarks.setOnClickListener(onClickListener);
		mBtnAddFriends.setOnClickListener(onClickListener);
		mBtnSendMsg.setOnClickListener(onClickListener);
//		mBtnAddSystem.setOnClickListener(onClickListener);
		mIvUserHead.setOnClickListener(onClickListener);

		//朋友圈公开
		mTogglePublic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				String isPermi = "";
				if(on){
					isPermi = "no";
				}else{
					isPermi = "yes";
				}
				addNotPermLookPerson(isPermi);

			}
		});

		mToggleFriendsPublic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				String isLook = "";
				if(on){
					isLook = "no";
				}else{
					isLook = "yes";
				}
				addNotNoticePerson(isLook);

			}
		});
	}
	
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.iv_user_head://点击头像进入用户主页
					//点击头像进入个人主页
					Intent intentUserHome = new Intent(mContext,UserHomeActivity.class);
					user.setFlag(1);//已接受好友
					intentUserHome.putExtra("user",user);
					startActivity(intentUserHome);
					break;
				case R.id.rl_remarks://备注
					Intent intent = new Intent(mContext,UpdateInfoActivity.class);
					intent.putExtra("type",2);
					intent.putExtra("title","备注");
					intent.putExtra("relationId",user.getRelationId()+"");
					intent.putExtra("content",user.getDescTag());
					startActivity(intent);
					break;
				case R.id.rl_user_sign://个性签名

					break;
				case R.id.btn_add_friend://添加好友
					addFriend();
					break;
				case R.id.btn_send_msg://发送消息
					/**
					 * 启动单聊
					 * context - 应用上下文。
					 * targetUserId - 要与之聊天的用户 Id。
					 * title - 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
					 */
//					RongIM.getInstance().startPrivateChat(mContext, "2642", "hello");
					if(user==null){
						return;
					}
					RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.PRIVATE, String.valueOf(user.getPhoneNum()), user.getUserName());
					break;
			}
		}
	};

	/**
	 * 获取数据
	 */
	private void getData(){
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();
		String queryPhoneNum = MyApplication.getInstance().getCurrentUser().getPhoneNum();
		ApiMyUtils.getMyInformations(mContext, phoneNum,queryPhoneNum, new HttpConnectionUtil.RequestCallback() {
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
			mTvUserNameHead.setText(StringUtils.isEmpty(user.getUserName())?"":user.getUserName());
			mTvUserId.setText(user.getUserId());
			mTvSex.setText(StringUtils.isEmpty(user.getGender())?"":user.getGender());
			mTvAddressDetail.setText(StringUtils.isEmpty(user.getAddr())?"":user.getAddr());
			mTvAddressArea.setText(StringUtils.isEmpty(user.getDistrict())?"":user.getDistrict());
			mTvUserSign.setText(StringUtils.isEmpty(user.getSummary())?"":user.getSummary());
			mTvUserDesc.setText(user.getDescTag());

//			Picasso.with(mContext).load(user.getIcon()).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(mIvUserHead);
			if(!StringUtils.isEmpty(user.getIcon())) {
				int width = DeviceInfo.dp2px(mContext,70);
				Picasso.with(mContext).load(user.getIcon()).resize(width,width).centerCrop().placeholder(mContext.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
			}
			Picasso.with(mContext).load(user.getSkinPath()).resize(this.width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);
			if(flag==1){
				mBtnAddFriends.setVisibility(View.GONE);
			}else{
				mBtnAddFriends.setVisibility(View.VISIBLE);
			}

			//如果是自己的信息不需要展示添加好友按钮
			User myInfo = MyApplication.getInstance().getCurrentUser();
			if(user.getPhoneNum().equals(myInfo.getPhoneNum())){
				findViewById(R.id.ll_friends_info).setVisibility(View.GONE);
			}

			String isLookMyInfo = user.getIsLookMyInfo();//不让好友看我的朋友圈,
			if(!StringUtils.isEmpty(isLookMyInfo)) {
				if ("yes".equalsIgnoreCase(isLookMyInfo)) {
					mTogglePublic.setToggleOff();
				} else {
					mTogglePublic.setToggleOn();
				}
			}
			String isLookOtherInfo = user.getIsLookOtherInfo();//是否看别人动态
			if(!StringUtils.isEmpty(isLookOtherInfo)) {
				if ("yes".equalsIgnoreCase(isLookOtherInfo)) {
					mToggleFriendsPublic.setToggleOff();
				} else {
					mToggleFriendsPublic.setToggleOn();
				}
			}



		}
	}

	private void addFriend(){
		User user = getUser();
		if(user ==null){
			return;
		}
		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();
		ApiUserUtils.addFriends(mContext, user.getPhoneNum(), phoneNum, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					ToastUtils.showToast(mContext,"邀请好友成功");
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}

	/**
	 * 保存我是否看他的动态
	 * @param isLook yes看 no不看
     */
	private void addNotNoticePerson(String isLook){
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_ADD_NOT_NOTICE_PERSON;
		User myUser = MyApplication.getInstance().getCurrentUser();
		OkHttpUtils//
				.get()//
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.addParams("myPhoneNum",myUser.getPhoneNum())
				.addParams("otherPhoneNum",user.getPhoneNum())
				.addParams("relationId",String.valueOf(user.getRelationId()))
				.addParams("isLook",isLook)
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						ToastUtils.showToast(mContext,"保存失败.");
					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {

							}else{
								ToastUtils.showToast(mContext,jsonObject.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showToast(mContext,"保存失败");
						}

					}
				});


	}

	/**
	 * 是否允许查看我的朋友圈
	 * @param isPermi
     */
	private void addNotPermLookPerson(String isPermi){
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_ADD_NOT_PERM_LOOK_PERSON;
		User myUser = MyApplication.getInstance().getCurrentUser();
		OkHttpUtils//
				.get()//
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.addParams("myPhoneNum",myUser.getPhoneNum())
				.addParams("otherPhoneNum",user.getPhoneNum())
				.addParams("relationId",String.valueOf(user.getRelationId()))
				.addParams("isPermi",isPermi)
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						ToastUtils.showToast(mContext,"保存失败.");
					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {

							}else{
								ToastUtils.showToast(mContext,jsonObject.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showToast(mContext,"保存失败");
						}

					}
				});
	}


}
