package com.anda.moments.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.anda.gson.reflect.TypeToken;
import com.anda.moments.R;
import com.anda.moments.apdater.FriendsListAdapter;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.AddFriendsActivity;
import com.anda.moments.ui.MainActivity;
import com.anda.moments.ui.NewFriendsListActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.widget.slide.CharacterParser;
import com.anda.moments.views.widget.slide.PinyinComparator;
import com.anda.moments.views.widget.slide.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 好友
 * @author will
 *
 */
public class FriendsFragment extends BaseFragment implements SideBar.OnTouchingLetterChangedListener, TextWatcher {

	private View mContentView;
	private ActionBar mActionBar;

	private ListView mListView;
	private View mBtnNewFriendsRequest;//新好友请求
	private View mBtnFriendsMessage;//好友消息
	private EditText mEtSearchFriends;//搜索好友

	private List<User> friendLists = new ArrayList<User>();//好友列表
	FriendsListAdapter mFriendListAdapter;

	private CharacterParser characterParser;// 汉字转拼音

	private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类

	private SideBar mSideBar;

	private TextView mDialog;

	LoadingDialog mLoadingDialog;
	private TextView mTvReqCount;//新用户请求个数
	private TextView mTvUnReadMsg;//未读消息


	private int reqCount;//好友请求个数
	private int unReadMsgCount;//未读消息数

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_friends, container,false);

		initView();
		initListener();

		isPrepared = true;//控件初始化完成

		pushMsgReceiver();


		return mContentView;
	}

	/**
	 * 未读消息改变提醒
	 */
	private void pushMsgReceiver(){
		final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION,
				Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
				Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE};
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
//				RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener1, Conversation.ConversationType.APP_PUBLIC_SERVICE);
			}
		}, 500);
	}

	private void initView(){
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mActionBar.setTitle(R.string.tab_friends_source);
		mActionBar.hideLeftActionButton();
//		mActionBar.hideBottonLine();
		mActionBar.setRightActionButton(R.drawable.btn_add_friends, "", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, AddFriendsActivity.class);
				startActivity(intent);
			}
		});

		mSideBar = (SideBar) mContentView.findViewById(R.id.friend_sidrbar);
		mDialog = (TextView) mContentView.findViewById(R.id.friend_dialog);
		mSideBar.setTextView(mDialog);
		mSideBar.setOnTouchingLetterChangedListener(this);


		mListView = (ListView)mContentView.findViewById(R.id.listView);

		addHeader();
		mFriendListAdapter = new FriendsListAdapter(mActivity,friendLists);
		mListView.setAdapter(mFriendListAdapter);
	}
	private void addHeader(){
		View mViewFriendHeader = View.inflate(mActivity,R.layout.friends_header,null);
		mBtnNewFriendsRequest = mViewFriendHeader.findViewById(R.id.rl_new_friend_request);
		mBtnFriendsMessage = mViewFriendHeader.findViewById(R.id.rl_friend_message);
		mEtSearchFriends = (EditText) mViewFriendHeader.findViewById(R.id.et_search_friend);
		mTvReqCount = (TextView)mViewFriendHeader.findViewById(R.id.unread_address_number);
		mTvUnReadMsg = (TextView)mViewFriendHeader.findViewById(R.id.unread_msg_count);

		mListView.addHeaderView(mViewFriendHeader);

	}

	private void initListener(){
		mBtnNewFriendsRequest.setOnClickListener(onClickListener);
		mBtnFriendsMessage.setOnClickListener(onClickListener);

//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				if(position ==0){
//					return;
//				}
////				User user = friendLists.get(position-1);
//				User user = mFriendListAdapter.getItem(position);
//				Intent intent = new Intent(mActivity, UserInfoActivity.class);
//				intent.putExtra("user",user);
//				startActivity(intent);
//			}
//		});
	}
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.rl_new_friend_request://好友请求
					Intent intent = new Intent(mActivity, NewFriendsListActivity.class);
					startActivity(intent);
					break;
				case R.id.rl_friend_message://新消息
//					Intent intentConversationList = new Intent(mActivity, ConversationListActivity.class);
//					startActivity(intentConversationList);
					if(RongIM.getInstance() != null)
						RongIM.getInstance().startConversationList(mActivity);
					break;


			default:
				break;
			}
		}
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);



	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		getFriendsList();
//		getUnReadCount();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 获取好友列表
	 */
	private void getFriendsList(){
		User user = getUser();
		if(user==null){
			return;
		}
		if(!isLoadData) {
			mLoadingDialog = new LoadingDialog(mActivity);
			mLoadingDialog.show();
		}
		ApiMomentsUtils.getMyFriendsList(mActivity, user.getPhoneNum(),1, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(mLoadingDialog!=null && mLoadingDialog.isShowing()){
					mLoadingDialog.cancel();
				}
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					isLoadData = true;
					Log.e("FriendsFragment","获取好友列表："+parseModel.getResults().toString());
					reqCount = parseModel.getReqCount();//好友请求个数
					if(reqCount>0) {
						mTvReqCount.setVisibility(View.VISIBLE);
						mTvReqCount.setText(String.valueOf(reqCount));
					}else{
						mTvReqCount.setVisibility(View.GONE);
					}
					List<User> friends = JsonUtils.fromJson(parseModel.getResults().toString(),new TypeToken<List<User>>(){});
					initData(friends);
					showFriendMsg();
				}else{
					ToastUtils.showToast(mActivity,parseModel.getInfo());
				}
			}
		});
	}


	private void initData(List<User> friends){

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		if(friends!=null && !friends.isEmpty()){
			friendLists.clear();
			for(User user:friends){
				int flag = user.getFlag();// flag—0表示已添加，flag-1表示接受好友请求，flag-2表示拒绝好友邀请,flag-4未添加
				String userName = StringUtils.isEmpty(user.getDescTag())?user.getUserName():user.getDescTag();
				if(flag == 1) {
					if(StringUtils.isEmpty(userName)){
						user.setSuoxie("");
						user.setSortLetters("#");
					}else {
						String pinyin = characterParser.getSelling(userName);
						String suoxie = CharacterParser.getFirstSpell(userName);

						user.setSuoxie(suoxie);
						String sortString = pinyin.substring(0, 1).toUpperCase();

						if (sortString.matches("[A-Z]")) {// 正则表达式，判断首字母是否是英文字母
							user.setSortLetters(sortString);
						} else {
							user.setSortLetters("#");
						}
					}
					friendLists.add(user);
				}else{
					continue;
				}
				//刷新用户信息头像
				Uri headUri = Uri.parse(StringUtils.isEmpty(user.getIcon())?"":user.getIcon());
				RongContext.getInstance().getUserInfoCache().put(user.getPhoneNum(),new UserInfo(user.getPhoneNum(),userName, headUri));



			}
		}else{

		}
		// 根据a-z进行排序
		Collections.sort(friendLists, pinyinComparator);
		mFriendListAdapter.updateListView(friendLists);
		mEtSearchFriends.addTextChangedListener(this);

	}


	@Override
	public void onTouchingLetterChanged(String s) {
		int position = 0;
		// 该字母首次出现的位置
		if(mFriendListAdapter != null){
			position = mFriendListAdapter.getPositionForSection(s.charAt(0));
		}
		if (position != -1) {
			mListView.setSelection(position);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString(), friendLists);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 *
	 * @param filterStr
	 */
	private void filterData(String filterStr, List<User> list) {
		List<User> filterDateList = new ArrayList<User>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = list;

		} else {
			filterDateList.clear();
			for (User sortModel : list) {
				String name = StringUtils.isEmpty(sortModel.getDescTag())?sortModel.getUserName():sortModel.getDescTag();
				String suoxie = sortModel.getSuoxie();
				if (name.indexOf(filterStr.toString()) != -1 || suoxie.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		mFriendListAdapter.updateListView(filterDateList);
	}

	/**
	 * 获取未读消息
	 */
	private void getUnReadCount(){
		RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
			@Override
			public void onSuccess(Integer integer) {
				int totalUnreadCount = integer;
				if(totalUnreadCount>0){
					String totalCount = totalUnreadCount>99?"...":totalUnreadCount+"";
					mTvUnReadMsg.setText(totalCount);
					mTvUnReadMsg.setVisibility(View.VISIBLE);
				}else{
					mTvUnReadMsg.setVisibility(View.GONE);
				}

			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {

			}
		});
	}

	public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
		@Override
		public void onMessageIncreased(int count) {
			unReadMsgCount = count;
			if (count == 0) {
				mTvUnReadMsg.setVisibility(View.GONE);
			} else if (count > 0 && count < 100) {
				mTvUnReadMsg.setVisibility(View.VISIBLE);
				mTvUnReadMsg.setText(count + "");
			} else {
				mTvUnReadMsg.setVisibility(View.VISIBLE);
				mTvUnReadMsg.setText("...");
			}
			showFriendMsg();

		}
	};

	private void showFriendMsg(){
		if(unReadMsgCount==0 && reqCount==0){
			((MainActivity)mActivity).showMessage(View.GONE);
		}else if(unReadMsgCount!=0 || reqCount !=0){
			((MainActivity)mActivity).showMessage(View.VISIBLE);
		}
	}


	private boolean isLoadData = false;//是否已经加载数据
	private boolean isPrepared = false;//标志已经初始化完成

//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if(isPrepared && isVisibleToUser){
//			getFriendsList();
//		}
//	}

//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		super.onHiddenChanged(hidden);
//		if(!hidden){
//			getFriendsList();
//		}
//	}


	/**
	 * 填充数据
	 *
	 * @param list
	 */
//	private void fillData(List<User> list) {
//		for (User user : list) {
//			if (user != null && user.getUserName() != null) {
//				String pinyin = characterParser.getSelling(user.getUserName());
//				String suoxie = CharacterParser.getFirstSpell(user.getUserName());
//
//				user.setSuoxie(suoxie);
//				String sortString = pinyin.substring(0, 1).toUpperCase();
//
//				if (sortString.matches("[A-Z]")) {// 正则表达式，判断首字母是否是英文字母
//					user.setSortLetters(sortString);
//				} else {
//					user.setSortLetters("☆");
//				}
//			}
//		}
//	}
}
