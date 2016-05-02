package com.anda.moments.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.HomeAapter;
import com.anda.moments.apdater.HomeAdapter;
import com.anda.moments.api.ApiMyUtils;
import com.anda.moments.entity.Image;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.PartTimeJob;
import com.anda.moments.ui.publish.PublishActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.Log;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.XListView;
import com.anda.moments.views.XListView.IXListViewListener;

/**
 * 兼职
 * @author will
 *
 */
@SuppressLint("NewApi") 
public class HomeFragment extends BaseFragment implements OnRefreshListener,IXListViewListener{

	private View mContentView;
	private ActionBar mActionBar;
	private XListView mListView;
	private List<List<String>> imagesList=new ArrayList<List<String>>();;
	private String[] images=new String[]{
			"http://pic4.zhongsou.com/image/4808b8d0191adf4bcde.jpg"
			,"file:///android_asset/img2.jpg"
			,"file:///android_asset/img3.jpg"
			,"file:///android_asset/img4.jpg"
			,"file:///android_asset/img5.jpg"
			,"file:///android_asset/img6.jpg"
			,"file:///android_asset/img7.jpg"
			,"file:///android_asset/img8.jpg"
			,"http://pic32.nipic.com/20130715/13232606_164243348120_2.jpg"};


	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ImageView mIvHeadBg;//背景
	private ImageView mIvUserHead;//头像
	

	
	private HomeAdapter mHomeAdapter;
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

		
		return mContentView;
	}

	private void initView(){
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mListView = (XListView)mContentView.findViewById(R.id.listView);

		//头部
		View mHeadView = View.inflate(mActivity,R.layout.header_index,null);
		mListView.addHeaderView(mHeadView);

		mIvHeadBg = (ImageView)mContentView.findViewById(R.id.iv_bg_head);//背景
		mIvUserHead = (ImageView)mContentView.findViewById(R.id.iv_user_head);//头像

		//背景适配
		int width = DeviceInfo.getDisplayMetricsWidth(mActivity);
		FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mIvHeadBg.getLayoutParams();
		params1.width = width;
		params1.height = (int) (params1.width * 1.0 / 1080 * 480);
		mIvHeadBg.setLayoutParams(params1);

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
		mActionBar.hideBottonLine();
		mActionBar.setRightActionButton(publishOnclickListener);
		mSwipeRefreshLayout.setOnRefreshListener(this);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		mHomeAdapter = new HomeAdapter(mActivity,imagesList);
		mListView.setAdapter(mHomeAdapter);



	}

	private void getData() {

		//这里单独添加一条单条的测试数据，用来测试单张的时候横竖图片的效果
		ArrayList<String> singleList=new ArrayList<String>();
		singleList.add(images[8]);
		imagesList.add(singleList);
		//从一到9生成9条朋友圈内容，分别是1~9张图片
		for(int i=0;i<9;i++){
			ArrayList<String> itemList=new ArrayList<String>();
			for(int j=0;j<=i;j++){
				itemList.add(images[j]);
			}
			imagesList.add(itemList);
		}
		mHomeAdapter.notifyDataSetChanged();
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getData();

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

	


	



	
	
}
