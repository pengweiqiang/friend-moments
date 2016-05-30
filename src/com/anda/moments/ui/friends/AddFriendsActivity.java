package com.anda.moments.ui.friends;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.anda.gson.reflect.TypeToken;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.SearchFriendsListAdapter;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.ShareUtil;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

import java.util.List;

/**
 * 添加好友
 * @author pengweiqiang
 *
 */
public class AddFriendsActivity extends BaseActivity {

	ActionBar mActionbar;

	private View mBtnAddWeixin;
	private View mBtnAddPhone;

	private EditText mEtSearchId;

	private View mViewPhoneAndWeixin;//通讯录和微信好友
	private ListView mListView;//搜索的结果
	SearchFriendsListAdapter mFriendListAdapter;

	private ShareUtil shareUtil;




	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_friends);
		super.onCreate(savedInstanceState);

		shareUtil = new ShareUtil(this);
		shareUtil.initWX();
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("添加好友");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		mBtnAddWeixin = findViewById(R.id.rl_add_friends_weixin);
		mBtnAddPhone = findViewById(R.id.rl_add_friends_send_message);
		mEtSearchId = (EditText)findViewById(R.id.et_search_friend);
		mViewPhoneAndWeixin = findViewById(R.id.ll_phone_weixin);
		mListView = (ListView)findViewById(R.id.listView);
		mListView.setVisibility(View.GONE);
	}

	@Override
	public void initListener() {
		mBtnAddWeixin.setOnClickListener(onClickListener);
		mBtnAddPhone.setOnClickListener(onClickListener);
//		mEtSearchId.setOnKeyListener(onKeyListener);

		mEtSearchId.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(StringUtils.isEmpty(s.toString())){
					hideSearchListView(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mEtSearchId.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				/*判断是否是“SEARCH”键*/
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					String userId = mEtSearchId.getText().toString().trim();
					if(StringUtils.isEmpty(userId)){
						ToastUtils.showToast(mContext,"请输入用户ID");
						return false;
					}
					/*隐藏软键盘*/
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
					}

					searchFriendById(userId);


					return true;
				}
				return false;
			}
		});

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.rl_add_friends_send_message://打开通讯录发送短信
					Intent it = new Intent(Intent.ACTION_VIEW);
					it.putExtra("sms_body", ApiConstants.SHARE_CONTENT);
					it.setType("vnd.android-dir/mms-sms");
					startActivity(it);
//					Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//					startActivity(intent);
					break;
				case R.id.rl_add_friends_weixin:
					showShareDialog();
					break;

			}
		}
	};

	private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_SEARCH){

				String userId = mEtSearchId.getText().toString().trim();
				if(StringUtils.isEmpty(userId)){
					ToastUtils.showToast(mContext,"请输入用户ID");
					return false;
				}

				/*隐藏软键盘*/
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if(inputMethodManager.isActive()){
					inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				}


				searchFriendById(userId);

				return true;
			}
			return false;
		}
	};

	private List<User> searchUsers;

	/**
	 * 根据昵称、备注、ID、手机号进行模糊查询自己的好友
	 * @param userId
     */
	private void searchFriendById(final String userId){
		User user = MyApplication.getInstance().getCurrentUser();
		ApiMyUtils.searchFriendByUserID(mContext, userId,user.getPhoneNum(), new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					searchUsers = JsonUtils.fromJson(parseModel.getResult().toString(),new TypeToken<List<User>>(){});

					if(searchUsers!=null && !searchUsers.isEmpty()){
						hideSearchListView(false);
					}else{
						ToastUtils.showToast(mContext,"没有"+userId+"用户");
						hideSearchListView(true);
					}
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}

	private void hideSearchListView(boolean isHide){
		if(isHide){
			mListView.setVisibility(View.GONE);
			mViewPhoneAndWeixin.setVisibility(View.VISIBLE);
		}else{
			mListView.setVisibility(View.VISIBLE);
			mViewPhoneAndWeixin.setVisibility(View.GONE);
			mFriendListAdapter = new SearchFriendsListAdapter(AddFriendsActivity.this,searchUsers);
			mListView.setAdapter(mFriendListAdapter);
		}
	}


	private void showShareDialog() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		// 为确认按钮添加事件,执行退出应用操作
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("好友");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				shareUtil.sendWebPageToWX(ApiConstants.SHARE_CONTENT, SendMessageToWX.Req.WXSceneSession);
				dlg.cancel();
			}
		});
		TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
		tv_xiangce.setText("朋友圈");
		tv_xiangce.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				shareUtil.sendWebPageToWX(ApiConstants.SHARE_CONTENT, SendMessageToWX.Req.WXSceneTimeline);

				dlg.cancel();
			}
		});

	}



}
