package com.anda.moments.ui.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.BannerPagerAdapter;
import com.anda.moments.ui.OutSouceReadActivity;
import com.anda.moments.ui.OutSourceEntranceActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.PageIndicator;
import com.anda.universalimageloader.core.ImageLoader;

/**
 * 外包
 * @author will
 *
 */
public class OutsourceFragment extends BaseFragment{

	private View mContentView;
	private ActionBar mActionBar;
	
	View mOutSourceRead,mOutSourcing,mOutSourceEntrance;
	
	
	private List<String> ads;
	FrameLayout bannerView;
	ViewPager bannerViewPager;
	PageIndicator mIndicator;
	ArrayList<View> bannerListView;
	private BannerPagerAdapter bannerPageAdapter;
	private FrameLayout progressBbanner;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.out_source, container,false);
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
//		mActionBar.setTitle(R.string.tab_out_source);
		mActionBar.hideLeftActionButton();
		
		mOutSourceRead = mContentView.findViewById(R.id.out_source_read);
		mOutSourcing = mContentView.findViewById(R.id.outSourceing);
		mOutSourceEntrance = mContentView.findViewById(R.id.out_source_entrance);
		
		
		bannerView = (FrameLayout) mContentView.findViewById(R.id.banner);

		bannerViewPager = (ViewPager) bannerView
				.findViewById(R.id.banner_viewpager);
		progressBbanner = (FrameLayout)mContentView.findViewById(R.id.progress_banner);

		LayoutParams params1 = bannerViewPager.getLayoutParams();
		params1.width = DeviceInfo.getDisplayMetricsWidth(mActivity);
		params1.height = (int) (params1.width * 1.0 / 700 * 340);
		bannerViewPager.setLayoutParams(params1);
		progressBbanner.setLayoutParams(params1);
		bannerListView = new ArrayList<View>();

		bannerPageAdapter = new BannerPagerAdapter(bannerListView);

		bannerViewPager.setAdapter(bannerPageAdapter);
		bannerViewPager.setCurrentItem(0);

		bannerViewPager.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					startTimer();
				} else {
					stopTimer();
				}
				return false;
			}
		});
		mIndicator = (PageIndicator) bannerView.findViewById(R.id.indicator);
		mIndicator.setViewPager(bannerViewPager);
		
		
		return mContentView;
	}

	private void initListener(){
		mOutSourceRead.setOnClickListener(onClickListener);
		mOutSourcing.setOnClickListener(onClickListener);
		mOutSourceEntrance.setOnClickListener(onClickListener);
	}
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.out_source_read:
				startActivity(OutSouceReadActivity.class);
				break;
			case R.id.outSourceing:
				
				break;
			case R.id.out_source_entrance:
				startActivity(OutSourceEntranceActivity.class);
				break;

			default:
				break;
			}
		}
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initListener();
		getAdLists();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	Timer timer;
	public void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						if(!ads.isEmpty()){
							int currentIndex = bannerViewPager.getCurrentItem();
							if(currentIndex<ads.size()-1){
								currentIndex++;
							}else{
								currentIndex = 0;
							}
							
							bannerViewPager.setCurrentItem(currentIndex,true);
						}
					}
				});
			}
		}, 5000, 6000);
	}
	
	public void stopTimer(){
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
	}
	
	public void addBannerView() {
		bannerListView.clear();
		for (int i = 0; i < ads.size(); i++) {
			final int position = i;
			final String ad = ads.get(i);
			// ImageView viewOne = (ImageView) LayoutInflater.from(mActivity)
			// .inflate(R.layout.b0_index_banner_cell, null);
			View viewOne = LayoutInflater.from(mActivity).inflate(
					R.layout.b0_index_banner_cell, null);
			ImageView bannerImageView = (ImageView) viewOne
					.findViewById(R.id.banner_imageView);
			ImageLoader.getInstance().displayImage(
					ad,
					bannerImageView,
					MyApplication.getInstance().getOptions(
							R.drawable.default_image));
			bannerListView.add(viewOne);

//			viewOne.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//				}
//			});
		}
		if (ads != null && !ads.isEmpty()) {
			startTimer();
			bannerPageAdapter = new BannerPagerAdapter(bannerListView);

			bannerViewPager.setAdapter(bannerPageAdapter);
			bannerViewPager.setCurrentItem(0);
			bannerPageAdapter.notifyDataSetChanged();

			mIndicator.setViewPager(bannerViewPager);
		}
	}
	
	/**
	 * 获取首页广告数据
	 */
	private void getAdLists() {
		ads = new ArrayList<String>();
		

		ads.add("drawable://" + R.drawable.default_image);
		ads.add("http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg");
		ads.add("http://pic18.nipic.com/20111215/577405_080531548148_2.jpg");
		ads.add("drawable://" + R.drawable.default_image);
		addBannerView();
	}
	
}
