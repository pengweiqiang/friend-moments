package com.anda.moments.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.MyAdapter;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.entity.Infos;
import com.anda.moments.entity.MyInfo;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.my.CircleDetailActivity;
import com.anda.moments.ui.my.PersonalInfoActivity;
import com.anda.moments.ui.my.SkinsActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.XListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 * @author will
 *
 */
public class MyFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,XListView.IXListViewListener {

	public static int REQUEST_CODE_DETAIL = 0x102;
	private View mContentView;
	private ActionBar mActionBar;
	private XListView mListView;
	private List<Infos> infosList = new ArrayList<Infos>();



	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ImageView mIvHeadBg;//背景
	private ImageView mIvUserHead;//头像
	private View mViewUserHead;
	private TextView mTvUserSign;//个人签名
	private TextView mTvUserName;//昵称

	private MyInfo myInfo;



	private MyAdapter mMyAdapter;
	private int page = 1;




	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.my, container,false);

		initView();
		initListener();


		return mContentView;
	}
	private int width,height;
	private void initView(){
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mListView = (XListView)mContentView.findViewById(R.id.listView);

		View mHeadView = View.inflate(mActivity,R.layout.header_my_index,null);

		mListView.addHeaderView(mHeadView);

		mIvHeadBg = (ImageView)mContentView.findViewById(R.id.iv_bg_head);//背景
		mIvUserHead = (ImageView)mHeadView.findViewById(R.id.iv_user_head);//头像
		mViewUserHead = mHeadView.findViewById(R.id.rl_user_head);
		mTvUserSign = (TextView)mHeadView.findViewById(R.id.tv_user_sign);//个人签名
		mTvUserName = (TextView)mHeadView.findViewById(R.id.tv_user_name_head);//昵称

		//背景适配
		width = DeviceInfo.getDisplayMetricsWidth(mActivity);
		FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mIvHeadBg.getLayoutParams();
		params1.width = width;
		params1.height = (int) (params1.width * 1.0 / 1080 * 480);
		height = params1.height;
		mIvHeadBg.setLayoutParams(params1);
		Picasso.with(mActivity).load(getUser().getSkinPath()).resize(width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);

		//头像距离顶部距离
//		FrameLayout.LayoutParams paramsUserHead = (FrameLayout.LayoutParams) mIvUserHead.getLayoutParams();
//		paramsUserHead.setMargins(0,width/3,DeviceInfo.dp2px(mActivity,15),0);
//		mIvUserHead.setLayoutParams(paramsUserHead);

		FrameLayout.LayoutParams paramsUserHead = (FrameLayout
				.LayoutParams) mViewUserHead.getLayoutParams();
		paramsUserHead.setMargins(0,width/3,DeviceInfo.dp2px(mActivity,15),0);
		mViewUserHead.setLayoutParams(paramsUserHead);


		//个性签名
//		FrameLayout.LayoutParams paramsUserSign = (FrameLayout.LayoutParams)mTvUserSign.getLayoutParams();
//		paramsUserSign.setMargins(0,width/3+DeviceInfo.dp2px(mActivity,15),DeviceInfo.dp2px(mActivity,15),0);
		mTvUserSign.setVisibility(View.VISIBLE);
//		mTvUserSign.setLayoutParams(paramsUserSign);
//
		mSwipeRefreshLayout = (SwipeRefreshLayout)mContentView.findViewById(R.id.swipe_container);


		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		mActionBar.setTitle(R.string.tab_my);
		mActionBar.hideLeftActionButton();
//		mActionBar.hideBottonLine();
		mSwipeRefreshLayout.setOnRefreshListener(this);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		mMyAdapter = new MyAdapter(mActivity,infosList);
		mListView.setAdapter(mMyAdapter);



	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getData();

	}

	@Override
	public void onResume() {
		super.onResume();
		showMyInfo2Cache();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMyAdapter.stopCurrentAnimAudio();
		mMyAdapter.playingAudioIndex = -1;

	}
	@Override
	public void onRefresh() {
		page = 1;
		getData();
	}

	@Override
	public void onLoadMore() {
		page++;
		getData();
	}
	private void initListener(){
		mIvHeadBg.setOnClickListener(onClickListener);
		mIvUserHead.setOnClickListener(onClickListener);
		mActionBar.setTitleOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				topListViewFirst();
				return false;
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){
					return;
				}
				Infos infos = infosList.get(position-1);
				Intent intent = new Intent(mActivity, CircleDetailActivity.class);
				intent.putExtra("position",position-1);
				intent.putExtra("id",infos.getInfoId());
				startActivityForResult(intent,REQUEST_CODE_DETAIL);

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//刷新进入动态详情的数据
		if(requestCode == REQUEST_CODE_DETAIL && data!=null){
			if(infosList!=null && !infosList.isEmpty()){
				boolean isDeleted = data.getBooleanExtra("isDeleted",false);
				int position = data.getIntExtra("position", -1);
				if(isDeleted){
					infosList.remove(position);
					mMyAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	/**
	 * listView置顶
	 */
	public void topListViewFirst(){
		if(mListView!=null){
			if(mListView.getFirstVisiblePosition()<ReqUrls.LIMIT_DEFAULT_NUM){
				mListView.smoothScrollToPosition(0);
			}else{
				mListView.setSelection(0);
			}
		}
	}


	private android.view.View.OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.iv_bg_head://更换封面
					showChangeBackground();
					break;
				case R.id.iv_user_head://头像跳入个人中心
					Intent intent = new Intent(mActivity,PersonalInfoActivity.class);
					intent.putExtra("phoneNum",MyApplication.getInstance().getCurrentUser().getPhoneNum());
					startActivity(intent);
					break;
				case R.id.btn_capacity_sort:
//				openRotation(mContentView.findViewById(R.id.down3));
					break;

				default:
					break;
			}

		}
	};





	private void getData(){
		User user = getUser();
		if(user==null){
			return;
		}
		ApiMyUtils.getInfoDetails(mActivity, user.getPhoneNum(), ReqUrls.LIMIT_DEFAULT_NUM+"",String.valueOf(page),"2", new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mSwipeRefreshLayout.setRefreshing(false);
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					myInfo = new MyInfo();
					myInfo.setPublishUser(parseModel.getPublishUser());
					myInfo.setInfos(parseModel.getInfos());
					initData();

				}else{
					if(page!=1){
						page --;
					}
					ToastUtils.showToast(mActivity,parseModel.getInfo());
				}
			}
		});
	}

	private void initData(){

		if(myInfo!=null ){
			if(myInfo.getPublishUser()!=null) {//个人信息
				User user = myInfo.getPublishUser();
				mTvUserSign.setText(user.getSummary());
				int width = DeviceInfo.dp2px(mActivity,70);
				Picasso.with(mActivity).load(user.getIcon()).resize(width,width).centerCrop().placeholder(mActivity.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
//				Picasso.with(mActivity).load(user.getIcon()).placeholder(mActivity.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
				mTvUserName.setText(user.getUserName());
				Picasso.with(mActivity).load(user.getSkinPath()).resize(this.width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);

			}
			if(myInfo.getInfos()!=null){
				if(page == 1){
					infosList.clear();
				}
				infosList.addAll(myInfo.getInfos());
				mMyAdapter.notifyDataSetChanged();
				if(myInfo.getInfos().size()<ReqUrls.LIMIT_DEFAULT_NUM){//少于请求条数，隐藏底部栏
					mListView.hideFooterView();
				}else {
					mListView.stopLoadMore();
				}
			}else{
				mListView.hideFooterView();
//				mListView.onLoadFinish(page,0,"没有更多");
			}

		}
	}

	private void showMyInfo2Cache(){
		User user = getUser();
		if(user!=null){//个人信息
			Picasso.with(mActivity).load(user.getIcon()).placeholder(mActivity.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
			mTvUserName.setText(user.getUserName());
			mTvUserSign.setText(user.getSummary());
			Picasso.with(mActivity).load(getUser().getSkinPath()).resize(width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);
		}
	}

	/**
	 * 更换封面
	 *
	 */
	private void showChangeBackground(){
		final AlertDialog dlg = new AlertDialog.Builder(mActivity).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.alertdialog);
		TextView tvContent = (TextView) window.findViewById(R.id.tv_content1);
		tvContent.setText("更换封面");
		tvContent.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				startActivity(SkinsActivity.class);
				dlg.cancel();
			}
		});
		window.findViewById(R.id.ll_content2).setVisibility(View.GONE);

	}



}
