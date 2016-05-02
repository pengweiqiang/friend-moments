package com.anda.moments.ui.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anda.gson.reflect.TypeToken;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.BannerPagerAdapter;
import com.anda.moments.apdater.FriendsListAdapter;
import com.anda.moments.apdater.HomeAdapter;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.AddFriendsActivity;
import com.anda.moments.ui.LoginActivity;
import com.anda.moments.ui.NewFriendsListActivity;
import com.anda.moments.ui.OutSouceReadActivity;
import com.anda.moments.ui.OutSourceEntranceActivity;
import com.anda.moments.ui.UserInfoActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.PageIndicator;
import com.anda.moments.views.widget.pinyin.PinYin;
import com.anda.moments.views.widget.slide.CharacterParser;
import com.anda.moments.views.widget.slide.PinyinComparator;
import com.anda.moments.views.widget.slide.SideBar;
import com.anda.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

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

	private List<User> friendLists;//好友列表
	FriendsListAdapter mFriendListAdapter;

	private CharacterParser characterParser;// 汉字转拼音

	private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类

	private SideBar mSideBar;

	private TextView mDialog;

	LoadingDialog mLoadingDialog;
	private TextView mTvReqCount;//新用户请求个数

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
		return mContentView;
	}
	private void initView(){
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mActionBar.setTitle(R.string.tab_friends_source);
		mActionBar.hideLeftActionButton();
		mActionBar.hideBottonLine();
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

					break;


			default:
				break;
			}
		}
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getFriendsList();

	}

	@Override
	public void onResume() {
		super.onResume();
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
		ApiMomentsUtils.getMyFriendsList(mActivity, user.getPhoneNum(), new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					Log.e("FriendsFragment","获取好友列表："+parseModel.getData());
					int reqCount = parseModel.getReqCount();//好友请求个数
					if(reqCount>0) {
						mTvReqCount.setText(reqCount);
					}else{
						mTvReqCount.setVisibility(View.GONE);
					}
					friendLists = JsonUtils.fromJson(parseModel.getResults().toString(),new TypeToken<List<User>>(){});
					initData();
				}else{
					ToastUtils.showToast(mActivity,parseModel.getInfo());
				}
			}
		});
	}

	private void initData(){
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		if(friendLists!=null && !friendLists.isEmpty()){
			for(User user:friendLists){
				String pinyin = characterParser.getSelling(user.getUserName());
				String suoxie = CharacterParser.getFirstSpell(user.getUserName());

				user.setSuoxie(suoxie);
				String sortString = pinyin.substring(0, 1).toUpperCase();

				if (sortString.matches("[A-Z]")) {// 正则表达式，判断首字母是否是英文字母
					user.setSortLetters(sortString);
				} else {
					user.setSortLetters("☆");
				}
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
				String name = sortModel.getUserName();
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
