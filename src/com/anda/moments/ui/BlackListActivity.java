package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.anda.gson.reflect.TypeToken;
import com.anda.moments.R;
import com.anda.moments.apdater.FriendsListAdapter;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
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
import io.rong.imlib.model.UserInfo;

/**
 * 黑名单
 * @author pengweiqiang
 *
 */
public class BlackListActivity extends BaseActivity implements SideBar.OnTouchingLetterChangedListener, TextWatcher,SwipeRefreshLayout.OnRefreshListener{

	ActionBar mActionbar;
	LoadingDialog mLoadingDialog;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListView mListView;

	private List<User> friendLists = new ArrayList<User>();//好友列表
	FriendsListAdapter mFriendListAdapter;

	private CharacterParser characterParser;// 汉字转拼音

	private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类

	private SideBar mSideBar;

	private TextView mDialog;

	private EditText mEtSearchFriends;//搜索好友



	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_black_list);
		super.onCreate(savedInstanceState);


		mFriendListAdapter = new FriendsListAdapter(this,friendLists,true);
		mListView.setAdapter(mFriendListAdapter);


	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("黑名单");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		mSideBar = (SideBar) findViewById(R.id.friend_sidrbar);
		mDialog = (TextView) findViewById(R.id.friend_dialog);
		mSideBar.setTextView(mDialog);
		mSideBar.setOnTouchingLetterChangedListener(this);

		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);


		mSwipeRefreshLayout.setColorSchemeResources(R.color.main_tab_text_color_selected);

		mSwipeRefreshLayout.setOnRefreshListener(this);


		mListView = (ListView)findViewById(R.id.listView);

		mEtSearchFriends = (EditText) findViewById(R.id.et_search_friend);


	}

	@Override
	public void initListener() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		getFriendsList();
	}


	/**
	 * 获取好友列表
	 */
	private void getFriendsList(){
		User user = getUser();
		if(user==null){
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();



		ApiUserUtils.getMyBlackList(mContext, user.getPhoneNum(),String.valueOf(1), new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(mLoadingDialog!=null && mLoadingDialog.isShowing()){
					mLoadingDialog.cancel();
				}
				mSwipeRefreshLayout.setRefreshing(false);
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					Log.e("FriendsFragment","获取好友列表："+parseModel.getResults().toString());

					List<User> friends = JsonUtils.fromJson(parseModel.getResults().toString(),new TypeToken<List<User>>(){});
					initData(friends);
				}else{
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}


	private void initData(List<User> friends){

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		friendLists.clear();
		if(friends!=null && !friends.isEmpty()){
			for(User user:friends){
//				int flag = user.getFlag();// flag—0表示已添加，flag-1表示接受好友请求，flag-2表示拒绝好友邀请,flag-4未添加
				String userName = StringUtils.isEmpty(user.getDescTag())?user.getUserName():user.getDescTag();
//				if(flag == 1) {
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
				String phoneNum = sortModel.getPhoneNum();
				String userId = sortModel.getUserId();
				if ((name!=null && name.indexOf(filterStr.toString()) != -1 )||
						(suoxie!=null && suoxie.indexOf(filterStr.toString()) != -1) ||
						(name!=null && characterParser.getSelling(name).startsWith(filterStr.toString()))||
						(userId!=null && userId.contains(filterStr))||
						(phoneNum!=null && phoneNum.contains(filterStr))
						) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		mFriendListAdapter.updateListView(filterDateList);
	}

	@Override
	public void onRefresh() {
		getFriendsList();
	}
}
