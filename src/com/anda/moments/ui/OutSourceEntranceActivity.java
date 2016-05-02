package com.anda.moments.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.anda.moments.R;
import com.anda.moments.apdater.HomeAapter;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.PartTimeJob;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.XListView;
import com.anda.moments.views.XListView.IXListViewListener;

public class OutSourceEntranceActivity extends BaseActivity implements OnRefreshListener,IXListViewListener{

	private ActionBar mActionBar;
	private XListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private TextView mTxtLocation;
	
	//分类条件
	private View mClassification,mCity,mCapacitySort;
	
	
	private HomeAapter mHomeAdapter;
	private List<PartTimeJob> mData;
	private int page = 1;
	
	@Override
	public void initView() {
		mActionBar = (ActionBar)findViewById(R.id.actionBar);
		mListView = (XListView)findViewById(R.id.listView);
		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
		mClassification = findViewById(R.id.btn_classification);
		mCity = findViewById(R.id.btn_city);
		mCapacitySort = findViewById(R.id.btn_capacity_sort);
		
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		
		mActionBar = (ActionBar)findViewById(R.id.actionBar);
		mActionBar.setTitle("外包入口");
		mActionBar.setLeftActionButtonListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
		mSwipeRefreshLayout.setOnRefreshListener(this);
		
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
	}

	@Override
	public void initListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mContext,PartTimeDetailActivity.class);
				startActivity(intent);
			}
			
		});
		mClassification.setOnClickListener(screenOnClickListener);
		mCity.setOnClickListener(screenOnClickListener);
		mCapacitySort.setOnClickListener(screenOnClickListener);
	}

	
	private android.view.View.OnClickListener screenOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_classification:
//				openRotation(mContentView.findViewById(R.id.down1));
				break;
			case R.id.btn_city:
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
	
	
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.out_source_entrance);
		super.onCreate(savedInstanceState);
		
		mData = new ArrayList<PartTimeJob>();
		mHomeAdapter = new HomeAapter(mContext, mData);
		mListView.setAdapter(mHomeAdapter);
		
		
		getData();
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	public void onRefresh() {
		page = 1;
		getData();
	}

	@Override
	public void onLoadMore() {
		page++;
		getData();
	}

}
