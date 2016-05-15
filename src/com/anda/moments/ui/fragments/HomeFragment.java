package com.anda.moments.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anda.gson.reflect.TypeToken;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.HomeAdapter;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.entity.CircleMessage;
import com.anda.moments.entity.CommentConfig;
import com.anda.moments.entity.CommentInfo;
import com.anda.moments.entity.CommentUser;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.listener.SwpipeListViewOnScrollListener;
import com.anda.moments.ui.CircleDetailActivity;
import com.anda.moments.ui.MainActivity;
import com.anda.moments.ui.SkinsActivity;
import com.anda.moments.ui.publish.PublishActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.ui.publish.PublishTextActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.InputMethodUtils;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.CommentListView;
import com.anda.moments.views.XListView;
import com.anda.moments.views.XListView.IXListViewListener;
import com.squareup.picasso.Picasso;

/**
 * 首页
 * @author will
 *
 */
@SuppressLint("NewApi") 
public class HomeFragment extends BaseFragment implements OnRefreshListener,IXListViewListener{

	private View mContentView;
	private ActionBar mActionBar;
	private XListView mListView;
	private List<CircleMessage> circleMessageList = new ArrayList<CircleMessage>();

//	public LinearLayout mEditTextBody;
//	private EditText mEditTextComment;//评论文本框
//	private ImageView sendIv;//发送评论


	private int mScreenHeight;
	private int mEditTextBodyHeight;
	private int mCurrentKeyboardH;
	private int mSelectCircleItemH;
	private int mSelectCommentItemOffset;


	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ImageView mIvHeadBg;//背景
	private ImageView mIvUserHead;//头像
	private TextView mTvUserName;//昵称
	

	
	private HomeAdapter mHomeAdapter;
	private CommentConfig mCommentConfig;
	private int page = 1;
	
	


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.home, container,false);

		initView();
		initListener();


		setViewTreeObserver();

		
		return mContentView;
	}

	private void setViewTreeObserver() {

		final ViewTreeObserver swipeRefreshLayoutVTO = mSwipeRefreshLayout.getViewTreeObserver();
		swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				Rect r = new Rect();
				mSwipeRefreshLayout.getWindowVisibleDisplayFrame(r);
				int statusBarH =  getStatusBarHeight();//状态栏高度
				int screenH = mSwipeRefreshLayout.getRootView().getHeight();
				if(r.top != statusBarH ){
					//在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
					r.top = statusBarH;
				}
				int keyboardH = screenH - (r.bottom - r.top);
				android.util.Log.d("HOME_FRAGMENT", "screenH＝ "+ screenH +" &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

				if(keyboardH == mCurrentKeyboardH){//有变化时才处理，否则会陷入死循环
					return;
				}

				mCurrentKeyboardH = keyboardH;
				mScreenHeight = screenH;//应用屏幕的高度
				mEditTextBodyHeight = ((MainActivity)mActivity).mEditTextBody.getHeight();

				//偏移listview
				if(mListView!=null && mCommentConfig != null){
					int index = mCommentConfig.circlePosition==0?mCommentConfig.circlePosition:(mCommentConfig.circlePosition+mListView.getHeaderViewsCount());
					mListView.setSelectionFromTop(index, getListviewOffset(mCommentConfig));
				}
			}
		});
	}

	/**
	 * 获取状态栏高度
	 * @return
	 */
	private int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private int width ,height;
	private void initView(){
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mListView = (XListView)mContentView.findViewById(R.id.listView);
//		mEditTextComment = (EditText) mContentView.findViewById(R.id.circleEt);
//		sendIv = (ImageView)mContentView.findViewById(R.id.sendIv);
//		mEditTextBody = (LinearLayout)mContentView.findViewById(R.id.editTextBodyLl);

		//头部
		View mHeadView = View.inflate(mActivity,R.layout.header_index,null);
		mListView.addHeaderView(mHeadView);

		mIvHeadBg = (ImageView)mContentView.findViewById(R.id.iv_bg_head);//背景
		mIvUserHead = (ImageView)mHeadView.findViewById(R.id.iv_user_head);//头像
		mTvUserName = (TextView)mHeadView.findViewById(R.id.tv_user_name_head);

		//背景适配
		width = DeviceInfo.getDisplayMetricsWidth(mActivity);
		FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mIvHeadBg.getLayoutParams();
		params1.width = width;
		params1.height = (int) (params1.width * 1.0 / 1080 * 480);
		height = params1.height;
		mIvHeadBg.setLayoutParams(params1);
		Picasso.with(mActivity).load(getUser().getSkinPath()).resize(width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);

		//头像距离顶部距离
		FrameLayout.LayoutParams paramsUserHead = (FrameLayout.LayoutParams) mIvUserHead.getLayoutParams();
		paramsUserHead.setMargins(0,width/3,DeviceInfo.dp2px(mActivity,15),0);
		mIvUserHead.setLayoutParams(paramsUserHead);


		mSwipeRefreshLayout = (SwipeRefreshLayout)mContentView.findViewById(R.id.swipe_container);


		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		mActionBar.setTitle(R.string.tab_part_time);
		mActionBar.hideLeftActionButton();
//		mActionBar.hideBottonLine();
		mActionBar.setRightActionButton(publishOnclickListener);
		mActionBar.setRightActionButtonLongClickListener(publishOnLongListener);
		mSwipeRefreshLayout.setOnRefreshListener(this);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		mHomeAdapter = new HomeAdapter(mActivity,circleMessageList);
		mHomeAdapter.setFragment(HomeFragment.this);
		mListView.setAdapter(mHomeAdapter);

		mListView.setOnScrollListener(new SwpipeListViewOnScrollListener(mSwipeRefreshLayout));

	}


	/**
	 * 获取朋友圈列表
	 */
	private void getCircleData(){

		String phoneNum = MyApplication.getInstance().getCurrentUser().getPhoneNum();
		ApiMyUtils.getFriendsInfos(mActivity, phoneNum,ReqUrls.LIMIT_DEFAULT_NUM + "", String.valueOf(page), "0", new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mSwipeRefreshLayout.setRefreshing(false);
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					List<CircleMessage> circleMessages = JsonUtils.fromJson(parseModel.getResults().toString(),new TypeToken<List<CircleMessage>>(){});

					showData2View(circleMessages);
				}else{
					if(page!=1){
						page --;
					}
					ToastUtils.showToast(mActivity,parseModel.getInfo());
				}
			}
		});

	}

	/**
	 * 数据展示在ListView上
	 * @param circleMessages
     */
	private void showData2View(List<CircleMessage> circleMessages){
		if(circleMessages!=null && !circleMessages.isEmpty()){
			if(page == 1){
				circleMessageList.clear();
			}
			circleMessageList.addAll(circleMessages);




			mHomeAdapter.notifyDataSetChanged();
			if(circleMessages.size()<ReqUrls.LIMIT_DEFAULT_NUM){//少于请求条数，隐藏底部栏
				mListView.hideFooterView();
			}else {
				mListView.onLoadFinish(page,circleMessages.size(),"加载更多");
			}



		}else{
			mListView.hideFooterView();
		}
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getCircleData();

	}

	@Override
	public void onResume() {
		super.onResume();
		initMyData();
	}

//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		super.onHiddenChanged(hidden);
//		if(!hidden){
//			showMyInfo2Cache();
//		}
//	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onRefresh() {
		page = 1;
		getCircleData();
	}

	/**
	 * 刷新界面
	 */
	public void onRefreshing(){
		page = 1;
		mSwipeRefreshLayout.setRefreshing(true);
		getCircleData();
	}

	@Override
	public void onLoadMore() {
		page++;
		getCircleData();
	}
	private void initListener(){

		mIvHeadBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showChangeBackground();
			}
		});

		mActionBar.setTitleOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				topListViewFirst();

				return false;
			}
		});

//		sendIv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//发布评论
//				String content = mEditTextComment.getText().toString().trim();
//				if(TextUtils.isEmpty(content)){
//					ToastUtils.showToast(mActivity,"评论内容不能为空...");
//					return;
//				}
//				addComment(content,mCommentConfig);
//
//				updateEditTextBodyVisible(View.GONE,null);
//			}
//		});

		mListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (((MainActivity)mActivity).mEditTextBody.getVisibility() == View.VISIBLE) {
					//mEditTextBody.setVisibility(View.GONE);
					//CommonUtils.hideSoftInput(MainActivity.this, mEditText);
					updateEditTextBodyVisible(View.GONE, null);
					return true;
				}
				return false;
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){
					return;
				}
				CircleMessage circleMessage = circleMessageList.get(position-1);
				Intent intent = new Intent(mActivity, CircleDetailActivity.class);
				intent.putExtra("circleMessage",circleMessage);
				startActivity(intent);
			}
		});
	}

	/**
	 * 评论
	 * @param content
     */
	public void addComment(final String content){
		final User user = MyApplication.getInstance().getCurrentUser();
		if(user==null){
			ToastUtils.showToast(mActivity,"请先登录");
			return;
		}
		String infoId = circleMessageList.get(mCommentConfig.circlePosition).getInfoId()+"";
		ApiMomentsUtils.addComment(mActivity,infoId,content,user.getPhoneNum(),new HttpConnectionUtil.RequestCallback(){

			@Override
			public void execute(ParseModel parseModel) {
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){//评论成功
//					ToastUtils.showToast(mActivity,parseModel.getInfo());
					CommentUser commentUser = new CommentUser();

					commentUser.setText(content);
					if(mCommentConfig!= null && mCommentConfig.replyUser!=null) {
						commentUser.setUserId(mCommentConfig.replyUser.getText());
						commentUser.setIcon(mCommentConfig.replyUser.getIcon());
						commentUser.setUserName(mCommentConfig.replyUser.getUserName());
						commentUser.setPhoneNum(mCommentConfig.replyUser.getPhoneNum());
					}else{
						commentUser.setUserId(user.getUserId());
						commentUser.setIcon(user.getIcon());
						commentUser.setUserName(user.getUserName());
						commentUser.setPhoneNum(user.getPhoneNum());
					}
					commentUser.setPublishTime(System.currentTimeMillis());

					update2AddComment(mCommentConfig.circlePosition,commentUser);
				}else{
					ToastUtils.showToast(mActivity,parseModel.getInfo());
				}
			}
		});
	}

	/**
	 * 更新评论列表
	 * @param circlePosition
	 * @param commentUser
     */
	private void update2AddComment(int circlePosition,CommentUser commentUser){
		CommentInfo commentInfo = mHomeAdapter.getDatas().get(circlePosition).getCommentInfo();

		commentInfo.setCommentNum(commentInfo.getCommentNum()+1);
		commentInfo.getCommentUsers().add(commentUser);

//		int visibleFirstPosi = mListView.getFirstVisiblePosition();
//		int visibleLastPosi = mListView.getLastVisiblePosition();
//		if (circlePosition >= visibleFirstPosi && circlePosition <= visibleLastPosi) {
//			View view = mListView.getChildAt(circlePosition - visibleFirstPosi);
//			HomeAdapter.ViewHolder holder = (HomeAdapter.ViewHolder) view.getTag();
//			holder.commentAdapter.add(0,commentUser);
//		}

		mHomeAdapter.notifyDataSetChanged();

		//清空评论文本
		((MainActivity)mActivity).mEditTextComment.setText("");
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
	
	
	private android.view.View.OnClickListener screenOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_classification:
//				openRotation(mContentView.findViewById(R.id.down1));
				break;
			case R.id.btn_distance:
//				openRotation(mContentView.findViewById(R.id.down2));
				break;
			case R.id.btn_capacity_sort:
//				openRotation(mContentView.findViewById(R.id.down3));
				break;

			default:
				break;
			}
			
		}
	};
	

	
	/**
	 * 发布
	 */
	OnClickListener publishOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, PublishActivity.class);
			startActivity(intent);
		}
	};
	View.OnLongClickListener publishOnLongListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			startActivity(PublishTextActivity.class);
			return false;
		}
	};


	private void initMyData(){
		User user = getUser();
		if(user!=null){//个人信息
			int width = DeviceInfo.dp2px(mActivity,70);
			Picasso.with(mActivity).load(user.getIcon()).resize(width,width).centerCrop().placeholder(mActivity.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
			mTvUserName.setText(user.getUserName());
			Picasso.with(mActivity).load(user.getSkinPath()).placeholder(R.drawable.bg_head).resize(this.width,height).centerCrop().error(R.drawable.bg_head).into(mIvHeadBg);
		}
	}




	/**
	 * 展示评论输入框
	 * @param visibility
     */
	public void updateEditTextBodyVisible(int visibility,CommentConfig commentConfig) {
		mCommentConfig = commentConfig;
		((MainActivity)mActivity).mEditTextBody.setVisibility(visibility);

		measureCircleItemHighAndCommentItemOffset(commentConfig);

		if(View.VISIBLE==visibility){
			((MainActivity)mActivity).mEditTextComment.requestFocus();
			//弹出键盘
			try {
				InputMethodUtils.showSoftInput(((MainActivity)mActivity).mEditTextComment.getContext(), ((MainActivity)mActivity).mEditTextComment);
			}catch (Exception e){

			}
		}else if(View.GONE==visibility){
			//隐藏键盘
			try {
				InputMethodUtils.hideSoftInput(((MainActivity)mActivity).mEditTextComment.getContext(), ((MainActivity)mActivity).mEditTextComment);
			}catch (Exception e){

			}
		}
	}




	/**
	 * 测量偏移量
	 * @param commentConfig
	 * @return
	 */
	private int getListviewOffset(CommentConfig commentConfig) {
		if(commentConfig == null)
			return 0;
		//这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
		int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
		if(commentConfig.commentType == CommentConfig.Type.REPLY){
			//回复评论的情况
			listviewOffset = listviewOffset + mSelectCommentItemOffset;
		}
		return listviewOffset;
	}

	private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig){
		if(commentConfig == null)
			return;

		int headViewCount = mListView.getHeaderViewsCount();
		int firstPosition = mListView.getFirstVisiblePosition();
		//只能返回当前可见区域（列表可滚动）的子项
		View selectCircleItem = mListView.getChildAt(headViewCount + commentConfig.circlePosition - firstPosition);
		if(selectCircleItem != null){
			mSelectCircleItemH = selectCircleItem.getHeight();
			if(headViewCount >0 && firstPosition <headViewCount && commentConfig.circlePosition == 0){
				//如果有headView，而且head是可见的，并且处理偏移的位置是第一条动态，则将显示的headView的高度合并到第一条动态上
				for(int i=firstPosition; i<headViewCount; i++){
					mSelectCircleItemH += mListView.getChildAt(i).getHeight();
				}
			}
		}

		if(commentConfig.commentType == CommentConfig.Type.REPLY){
			//回复评论的情况
			CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
			if(commentLv!=null){
				//找到要回复的评论view,计算出该view距离所属动态底部的距离
				View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
				if(selectCommentItem != null){
					//选择的commentItem距选择的CircleItem底部的距离
					mSelectCommentItemOffset = 0;
					View parentView = selectCommentItem;
					do {
						int subItemBottom = parentView.getBottom();
						parentView = (View) parentView.getParent();
						if(parentView != null){
							mSelectCommentItemOffset += (parentView.getHeight() - subItemBottom);
						}
					} while (parentView != null && parentView != selectCircleItem);
				}
			}
		}
	}


	public void updateView(int position,String text) {
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int lastVisiblePosition = mListView.getLastVisiblePosition();
		if(position>=firstVisiblePosition && position<=lastVisiblePosition){
			View view = mListView.getChildAt(position - firstVisiblePosition);
			if(view.getTag() instanceof HomeAdapter.ViewHolder){
				HomeAdapter.ViewHolder vh = (HomeAdapter.ViewHolder)view.getTag();
				vh.mTvPraiseCount.setText(text);
			}
		}

	}
//	public void updataView(int position,String text) {
//
//		int visibleFirstPosi = mListView.getFirstVisiblePosition();
//		int visibleLastPosi = mListView.getLastVisiblePosition();
//		if (position >= visibleFirstPosi && position <= visibleLastPosi) {
//			View view = mListView.getChildAt(position - visibleFirstPosi);
//			HomeAdapter.ViewHolder holder = (HomeAdapter.ViewHolder) view.getTag();
//
//			holder.mTvPraiseCount.setText(text);
//		} else {
//
//		}
//
//	}

	/**
	 * 更换封面
	 *
	 */
	private void showChangeBackground(){
		final AlertDialog dlg = new AlertDialog.Builder(mActivity).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.alertdialog);
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("更换封面");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				startActivity(SkinsActivity.class);
				dlg.cancel();
			}
		});
		window.findViewById(R.id.ll_content2).setVisibility(View.GONE);


	}


}
