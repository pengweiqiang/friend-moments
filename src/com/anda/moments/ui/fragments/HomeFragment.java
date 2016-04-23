package com.anda.moments.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.apdater.HomeAapter;
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.PartTimeJob;
import com.anda.moments.ui.CityListActivity;
import com.anda.moments.ui.PartTimeDetailActivity;
import com.anda.moments.ui.base.BaseFragment;
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
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ImageView mRefreshLocation;
	private TextView mTxtLocation;
	
	//分类条件
	private View mClassification,mDistance,mCapacitySort;
	
	
	private HomeAapter mHomeAdapter;
	private List<PartTimeJob> mData;
	private int page = 1;
	
	
	
	ObjectAnimator rotationLocation ;
	
	double lat;
	static double lon;
	static String city = "";// 当前定位城市
	
	ObjectAnimator rotationParam;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.home, container,false);
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mListView = (XListView)mContentView.findViewById(R.id.listView);
		mSwipeRefreshLayout = (SwipeRefreshLayout)mContentView.findViewById(R.id.swipe_container);
		mRefreshLocation = (ImageView)mContentView.findViewById(R.id.refresh_location);
		mTxtLocation = (TextView)mContentView.findViewById(R.id.txt_location);
		mClassification = mContentView.findViewById(R.id.btn_classification);
		mDistance = mContentView.findViewById(R.id.btn_distance);
		mCapacitySort = mContentView.findViewById(R.id.btn_capacity_sort);
		
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		
		mActionBar.setTitle(R.string.app_name);
		mActionBar.setLeftActionButton(R.drawable.down, "北京", cityOnclickListener);
		mActionBar.setRightActionButton(searchOnclickListener);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		
		
		return mContentView;
	}
	
	private void getData(){
		PartTimeJob partTime = new PartTimeJob();
		partTime.setDistance("13m");
		partTime.setMoney("15元/小时");
		partTime.setName("百度招聘");
		partTime.setTime("30分钟前");
		
		PartTimeJob partTime2 = new PartTimeJob();
		partTime2.setDistance("13m");
		partTime2.setMoney("15元/小时");
		partTime2.setName("百度招聘");
		partTime2.setTime("30分钟前");
		
		PartTimeJob partTime3 = new PartTimeJob();
		partTime3.setDistance("13m");
		partTime3.setMoney("15元/小时");
		partTime3.setName("百度招聘");
		partTime3.setTime("30分钟前");
		
		PartTimeJob partTime4 = new PartTimeJob();
		partTime4.setDistance("13m");
		partTime4.setMoney("15元/小时");
		partTime4.setName("百度招聘");
		partTime4.setTime("30分钟前");
		
		PartTimeJob partTime5 = new PartTimeJob();
		partTime5.setDistance("13m");
		partTime5.setMoney("15元/小时");
		partTime5.setName("百度招聘");
		partTime5.setTime("30分钟前");
		
		PartTimeJob partTime6 = new PartTimeJob();
		partTime6.setDistance("13m");
		partTime6.setMoney("15元/小时");
		partTime6.setName("百度招聘");
		partTime6.setTime("30分钟前");
		
		PartTimeJob partTime7 = new PartTimeJob();
		partTime7.setDistance("13m");
		partTime7.setMoney("15元/小时");
		partTime7.setName("百度招聘");
		partTime7.setTime("30分钟前");
		
		mData.add(partTime);
		mData.add(partTime2);
		mData.add(partTime3);
		mData.add(partTime4);
		mData.add(partTime5);
		mData.add(partTime6);
		mData.add(partTime7);

		mHomeAdapter.notifyDataSetChanged();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				mListView.onLoadFinish(page, 0, "加载完毕");
				//mListView.stopLoadMore();
//				mHomeAdapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setRefreshing(false);				
			}
		}, 3000);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mData = new ArrayList<PartTimeJob>();
		mHomeAdapter = new HomeAapter(mActivity, mData);
		mListView.setAdapter(mHomeAdapter);
		
		
//		getData();
		
		
		initListener();
		
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
	Animation operatingAnim;
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
	
	private void openRotation(View view){
		rotationParam = ObjectAnimator.ofFloat(view, "rotation", 0,180);
		rotationParam.setInterpolator(new LinearInterpolator());
		rotationParam.setDuration(300);
		rotationParam.start();
	}
	
	/**
	 * 搜索
	 */
	OnClickListener searchOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	/**
	 * 城市选择
	 */
	OnClickListener cityOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity,CityListActivity.class);
			startActivityForResult(intent, Constant.ACTION_CITY);
		}
	};
	
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null){
			return ;
		}
		if(requestCode == Constant.ACTION_CITY){
			String city = data.getStringExtra("city");
			mActionBar.setLeftText(city);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	
	
}
