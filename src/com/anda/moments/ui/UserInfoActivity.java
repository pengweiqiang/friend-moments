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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * 个人信息(自己、以及好友信息)
 * @author pengweiqiang
 *
 */
public class UserInfoActivity extends BaseActivity {

	ActionBar mActionbar;
	
	private View mBtnSign,mBtnRemarks;
	private TextView mTvUserName,mTvUserNameHead,mTvUserId,mTvSex,mTvAddressDetail,mTvAddressArea,mTvUserSign;

	private Button mBtnAddFriends;//添加好友
	private Button mBtnSendMsg;//发送消息


	private ImageView mIvHeadBg;
	private ImageView mIvUserHead;//头像

	LoadingDialog mLoadingDialog;

	String phoneNum = "";
	int flag = 0;//0未添加好友 1已接受好友
	User user;
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_info);
		super.onCreate(savedInstanceState);

		ViewGroup.LayoutParams paramsImageView = mIvHeadBg.getLayoutParams();
		paramsImageView.width = DeviceInfo.getDisplayMetricsWidth(UserInfoActivity.this);
		paramsImageView.height = (int) (paramsImageView.width * 1.0 / 1080 * 480);
		mIvHeadBg.setLayoutParams(paramsImageView);

		user = (User)this.getIntent().getSerializableExtra("user");
		phoneNum = user.getPhoneNum();
		flag = user.getFlag();
		showData();

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

		mBtnAddFriends = (Button)findViewById(R.id.btn_add_friend);
		mBtnSendMsg = (Button)findViewById(R.id.btn_send_msg);


	}

	@Override
	public void initListener() {
		mBtnSign.setOnClickListener(onClickListener);
		mBtnRemarks.setOnClickListener(onClickListener);
		mBtnAddFriends.setOnClickListener(onClickListener);
		mBtnSendMsg.setOnClickListener(onClickListener);
//		mBtnAddSystem.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.rl_remarks://备注

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
		ApiMyUtils.getMyInformations(mContext, phoneNum, new HttpConnectionUtil.RequestCallback() {
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

//			Picasso.with(mContext).load(user.getIcon()).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(mIvUserHead);
			Picasso.with(mContext).load(user.getIcon()).centerCrop().placeholder(getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
			if(flag==1){
				mBtnAddFriends.setVisibility(View.GONE);
			}else{
				mBtnAddFriends.setVisibility(View.VISIBLE);
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

}
