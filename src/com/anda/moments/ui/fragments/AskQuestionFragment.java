package com.anda.moments.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.anda.moments.R;
import com.anda.moments.apdater.AskShareAapter;
import com.anda.moments.entity.PartTimeJob;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.XListView;
import com.anda.moments.views.XListView.IXListViewListener;

/**
 * 有问必答
 * 
 * @author pengweiqiang
 * 
 */
public class AskQuestionFragment extends BaseFragment implements
		OnRefreshListener, IXListViewListener {

	private View mContentView;
	ActionBar mActionBar;
	private XListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private AskShareAapter mListAdapter;
	private List<PartTimeJob> mData;
	private int page = 1;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater
				.inflate(R.layout.ask_question, container, false);
		mActionBar = (ActionBar) mContentView.findViewById(R.id.actionBar);
		mActionBar.setTitle("有问必答");
		mActionBar.setRightActionButton(searchListener);
		mActionBar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});

		mListView = (XListView) mContentView.findViewById(R.id.listView);
		mSwipeRefreshLayout = (SwipeRefreshLayout) mContentView
				.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		mSwipeRefreshLayout.setOnRefreshListener(this);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		return mContentView;
	}

	private void getData() {
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

		mData.add(partTime);
		mData.add(partTime2);
		mData.add(partTime3);
		mData.add(partTime4);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// mListView.onLoadFinish(page, 0, "加载完毕");
				// mListView.stopLoadMore();
				mSwipeRefreshLayout.setRefreshing(false);
				mListAdapter.notifyDataSetChanged();
			}
		}, 2000);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mData = new ArrayList<PartTimeJob>();
		mListAdapter = new AskShareAapter(mActivity, mData);
		mListView.setAdapter(mListAdapter);

		getData();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
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

	OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

}
