package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.anda.gson.reflect.TypeToken;
import com.anda.moments.R;
import com.anda.moments.apdater.NewFriendsListAdapter;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
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
import com.anda.moments.views.swipe.SwipeMenu;
import com.anda.moments.views.swipe.SwipeMenuCreator;
import com.anda.moments.views.swipe.SwipeMenuItem;
import com.anda.moments.views.swipe.SwipeMenuListView;

import java.util.List;

/**
 * 新好友请求
 * @author pengweiqiang
 *
 */
public class NewFriendsListActivity extends BaseActivity {

	ActionBar mActionbar;


	private SwipeMenuListView mListView;//新好友列表
	NewFriendsListAdapter mFriendListAdapter;
	private List<User> newUsers;


	LoadingDialog mLoadingDialog;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_friends_list);
		super.onCreate(savedInstanceState);
		initSwipeListView();

		getData();
	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("新好友请求");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});


		mListView = (SwipeMenuListView)findViewById(R.id.listView);


	}

	@Override
	public void initListener() {
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User user = newUsers.get(position);
				Intent intent = new Intent(mContext, UserInfoActivity.class);
				intent.putExtra("user",user);
				mContext.startActivity(intent);
			}
		});

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.rl_add_friends_send_message://打开通讯录发送短信
					Intent it = new Intent(Intent.ACTION_VIEW);
					it.putExtra("sms_body", "发送短信文案");
					it.setType("vnd.android-dir/mms-sms");
					startActivity(it);
//					Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//					startActivity(intent);
					break;
				case R.id.rl_add_friends_weixin:

					break;

			}
		}
	};





	/**
	 * 获取新好友列表
     */
	private void getData(){
		User user = getUser();
		if(user==null){
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();
		ApiMomentsUtils.getMyFriendsList(mContext, user.getPhoneNum(),-1, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					newUsers = JsonUtils.fromJson(parseModel.getResults().toString(),new TypeToken<List<User>>(){});

					if(newUsers!=null && !newUsers.isEmpty()){
						mFriendListAdapter = new NewFriendsListAdapter(mContext,newUsers);
						mListView.setAdapter(mFriendListAdapter);
					}else{
						ToastUtils.showToast(mContext,"没有新好友请求信息");
					}
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}

	private void initSwipeListView(){
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				if(menu.getViewType()==0){//download
					// create "open" item
					SwipeMenuItem deleteItem = new SwipeMenuItem(
							getApplicationContext());
					// set item background
					deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
							0x3F, 0x25)));
					// set item width
					deleteItem.setWidth(DeviceInfo.dp2px(mContext, 90));
					// set item title
					deleteItem.setTitle("删除");
					// set item title fontsize
					deleteItem.setTitleSize(18);
					// set item title font color
					deleteItem.setTitleColor(Color.WHITE);
					// add to menu
					menu.addMenuItem(deleteItem);

					// create "delete" item
					//				SwipeMenuItem deleteItem = new SwipeMenuItem(
					//						getApplicationContext());
					//				// set item background
					//				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
					//						0x3F, 0x25)));
					//				// set item width
					//				deleteItem.setWidth(DeviceInfo.dp2px(mContext, 90));
					//				// set a icon
					//				deleteItem.setIcon(R.drawable.ic_delete);
					// add to menu
					//				menu.addMenuItem(deleteItem);
				}
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				User user = newUsers.get(position);
				int flag = user.getFlag();

					switch (index) {
						case 0:
//							if(flag == 0) {
//								addFriend(position, String.valueOf(user.getRelationId()));
//							}else {
							deleteItem(position,String.valueOf(user.getRelationId()));

//							}
							break;
					}
			}
		});
		// set SwipeListener
		mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

	}


	/**
	 * 添加好友 flag 2 拒绝好友
	 * @param friendId
	 */
//	private void addFriend(final int position, String friendId){
//		User user = getUser();
//		if(user==null){
//			return;
//		}
//		if(mLoadingDialog==null){
//			mLoadingDialog = new LoadingDialog(mContext);
//
//		}
//		int flag = 2;//拒绝添加好友
//		mLoadingDialog.show();
//		ApiUserUtils.dealFriendsRequest(mContext, user.getPhoneNum(), friendId,flag, new HttpConnectionUtil.RequestCallback() {
//			@Override
//			public void execute(ParseModel parseModel) {
//				mLoadingDialog.cancel();
//				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
//					newUsers.get(position).setFlag(2);
//					newUsers.remove(position);
//					mFriendListAdapter.notifyDataSetChanged();
//					ToastUtils.showToast(mContext,parseModel.getInfo());
//				}else{
//					ToastUtils.showToast(mContext,parseModel.getInfo());
//				}
//			}
//		});
//	}

	private void deleteItem(final int position, String friendId){
		User user = getUser();
		if(user==null){
			return;
		}
		if(mLoadingDialog==null){
			mLoadingDialog = new LoadingDialog(mContext);

		}
		mLoadingDialog.show();
		ApiUserUtils.deleteFriendRequest(mContext, String.valueOf(user.getId()), new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					newUsers.remove(position);
					mFriendListAdapter.notifyDataSetChanged();
				}else{
					ToastUtils.showToast(mContext, StringUtils.isEmpty(parseModel.getInfo())?"删除失败":parseModel.getInfo());
				}
			}
		});
	}





}
