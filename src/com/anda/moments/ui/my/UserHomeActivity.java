package com.anda.moments.ui.my;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.Infos;
import com.anda.moments.entity.MyInfo;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.XListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人主页
 * @author pengweiqiang
 *
 */
public class UserHomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,XListView.IXListViewListener{

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


	private User user ;


	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_home);
		super.onCreate(savedInstanceState);

		user = (User)this.getIntent().getSerializableExtra("user");
		initViewAfter();



		getData();

	}

	@Override
	public void initView() {
		mActionBar = (ActionBar)findViewById(R.id.actionBar);
		mListView = (XListView)findViewById(R.id.listView);


		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);


		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);


	}
	private int width ,height;
	private void initViewAfter(){

		View mHeadView = View.inflate(mContext,R.layout.header_my_index,null);

		mIvHeadBg = (ImageView)mHeadView.findViewById(R.id.iv_bg_head);//背景
		mIvUserHead = (ImageView)mHeadView.findViewById(R.id.iv_user_head);//头像
		mViewUserHead = mHeadView.findViewById(R.id.rl_user_head);
		mTvUserSign = (TextView)mHeadView.findViewById(R.id.tv_user_sign);//个人签名
		mTvUserName = (TextView)mHeadView.findViewById(R.id.tv_user_name_head);//昵称


		//背景适配
		width = DeviceInfo.getScreenWidth(mContext);
		FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mIvHeadBg.getLayoutParams();
		params1.width = width;
		params1.height = (int) (params1.width * 1.0 / 1080 * 480);
		height = params1.height;
		mIvHeadBg.setLayoutParams(params1);
//		Picasso.with(mContext).load(getUser().getSkinPath()).resize(width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);


		mListView.addHeaderView(mHeadView);

		//头像距离顶部距离
//		FrameLayout.LayoutParams paramsUserHead = (FrameLayout.LayoutParams) mIvUserHead.getLayoutParams();
//		paramsUserHead.setMargins(0,width/3,DeviceInfo.dp2px(mActivity,15),0);
//		mIvUserHead.setLayoutParams(paramsUserHead);


		FrameLayout.LayoutParams paramsUserHead = (FrameLayout
				.LayoutParams) mViewUserHead.getLayoutParams();
		paramsUserHead.setMargins(0,width/3,DeviceInfo.dp2px(mContext,15),0);
		mViewUserHead.setLayoutParams(paramsUserHead);

		//个性签名
//		FrameLayout.LayoutParams paramsUserSign = (FrameLayout.LayoutParams)mTvUserSign.getLayoutParams();
//		paramsUserSign.setMargins(0,width/3+DeviceInfo.dp2px(mActivity,15),DeviceInfo.dp2px(mActivity,15),0);
		mTvUserSign.setVisibility(View.VISIBLE);
//		mTvUserSign.setLayoutParams(paramsUserSign);
//


//		mActionBar.hideBottonLine();
		mActionBar.setTitle(StringUtils.isEmpty(user.getUserName())?user.getPhoneNum():user.getUserName());
		mActionBar.setLeftActionButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mMyAdapter = new MyAdapter(mContext,infosList);
		mListView.setAdapter(mMyAdapter);

		mIvUserHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserInfoActivity.class);
				intent.putExtra("user",user);
				startActivity(intent);
			}
		});

	}

	@Override
	public void initListener() {
		mSwipeRefreshLayout.setOnRefreshListener(this);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){
					return;
				}
				Infos infos = infosList.get(position-1);
				Intent intent = new Intent(mContext, CircleDetailActivity.class);
				intent.putExtra("position",position-1);
				intent.putExtra("id",infos.getInfoId());
				startActivity(intent);
			}
		});

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.rl_sercet_setting://私密设置

					break;
				case R.id.rl_about_us://关于我们

					break;

			}
		}
	};

	/**
	 * listView置顶
	 */
	public void topListViewFirst(){
		if(mListView!=null){
			if(mListView.getFirstVisiblePosition()< ReqUrls.LIMIT_DEFAULT_NUM){
				mListView.smoothScrollToPosition(0);
			}else{
				mListView.setSelection(0);
			}
		}
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

	private LoadingDialog loadingDialog;
	private void getData(){
		if(user==null){
			return;
		}
		if((infosList==null || infosList.isEmpty()) && page==1) {
			loadingDialog = new LoadingDialog(mContext);
			loadingDialog.show();
		}
		String myPhoneNum = MyApplication.getInstance().getCurrentUser().getPhoneNum();
		ApiMyUtils.getInfoDetails(mContext, user.getPhoneNum(), ReqUrls.LIMIT_DEFAULT_NUM+"",String.valueOf(page),"2",myPhoneNum, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mSwipeRefreshLayout.setRefreshing(false);
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
//					Log.e("MyFragment",parseModel.getResults().getAsString());
					myInfo = new MyInfo();
					myInfo.setPublishUser(parseModel.getPublishUser());
					myInfo.setInfos(parseModel.getInfos());
					initData();

				}else{
					if(page!=1){
						page --;
					}
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
				if(loadingDialog!=null && loadingDialog.isShowing()) {
					loadingDialog.cancel();
				}
			}
		});
	}

	private void initData(){

		if(myInfo!=null ){
			if(myInfo.getPublishUser()!=null) {//个人信息
				User user = myInfo.getPublishUser();
				mTvUserSign.setText(user.getSummary());
//				Picasso.with(mContext).load(user.getIcon()).placeholder(mContext.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
				int width = DeviceInfo.dp2px(mContext,70);
				Picasso.with(mContext).load(user.getIcon()).resize(width,width).centerCrop().placeholder(mContext.getResources().getDrawable(R.drawable.default_useravatar)).into(mIvUserHead);
				Picasso.with(mContext).load(user.getSkinPath()).resize(this.width,height).centerCrop().placeholder(R.drawable.bg_head).error(R.drawable.bg_head).into(mIvHeadBg);
				mTvUserName.setText(StringUtils.isEmpty(user.getDescTag())?user.getDescTag():user.getUserName());
			}
			if(myInfo.getInfos()!=null && !myInfo.getInfos().isEmpty()){
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



}
