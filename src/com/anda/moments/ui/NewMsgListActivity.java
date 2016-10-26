package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.anda.moments.R;
import com.anda.moments.apdater.NewMessageListAdapter;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.NewMessage;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.listener.SwpipeListViewOnScrollListener;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.ui.my.CircleDetailActivity;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.anda.moments.views.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 最新消息
 * @author pengweiqiang
 *
 */
public class NewMsgListActivity extends BaseActivity implements XListView.IXListViewListener,SwipeRefreshLayout.OnRefreshListener{

	ActionBar mActionbar;
	LoadingDialog mLoadingDialog;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private XListView mListView;

	private List<NewMessage> newMessageList = new ArrayList<NewMessage>();
	NewMessageListAdapter mNewMessageListAdapter;

	int pageNo = 1;

	int pageSize = 10;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_msg_list);
		super.onCreate(savedInstanceState);

		pageSize = this.getIntent().getIntExtra("pageSize",10);

		mNewMessageListAdapter = new NewMessageListAdapter(this,newMessageList);
		mListView.setAdapter(mNewMessageListAdapter);




	}


	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("消息");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});


		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);


		mSwipeRefreshLayout.setColorSchemeResources(R.color.main_tab_text_color_selected);

		mSwipeRefreshLayout.setOnRefreshListener(this);


		mListView = (XListView)findViewById(R.id.listView);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		mListView.setOnScrollListener(new SwpipeListViewOnScrollListener(mSwipeRefreshLayout));

	}

	@Override
	public void initListener() {
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NewMessage newMessage = newMessageList.get(position);
				Intent intent = new Intent(mContext, CircleDetailActivity.class);
				intent.putExtra("position",-1);
				intent.putExtra("id",newMessage.getInfoId());
				intent.putExtra("commentId",newMessage.getId());
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getNewMsgList();
	}


	/**
	 * 获取好友列表
	 */
	private void getNewMsgList(){
		User user = getUser();
		if(user==null){
			return;
		}
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.show();



		ApiMomentsUtils.findLatestCommInfo(mContext,user.getPhoneNum(),"1",String.valueOf(pageNo),String.valueOf(pageSize), new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				if(mLoadingDialog!=null && mLoadingDialog.isShowing()){
					mLoadingDialog.cancel();
				}
				mSwipeRefreshLayout.setRefreshing(false);
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){

					List<NewMessage> newMessages = parseModel.getMsgInfo();
					showData2View(newMessages);


				}else{
					if(pageNo!=1){
						pageNo --;
					}
					ToastUtils.showToast(mContext,parseModel.getInfo());
				}
			}
		});
	}

	private void showData2View(List<NewMessage> newMessages){
		if(newMessages!=null ){

			if(pageNo == 1){
				newMessageList.clear();
			}
			newMessageList.addAll(newMessages);

			mNewMessageListAdapter.notifyDataSetChanged();
			if(newMessages.isEmpty()){
				mListView.hideFooterView();
			}
			if(pageNo>1 && newMessages.size()< ReqUrls.LIMIT_DEFAULT_NUM){
				mListView.hideFooterView();
			}else {
				if (newMessages.size() < ReqUrls.LIMIT_DEFAULT_NUM) {//少于请求条数，隐藏底部栏
					mListView.onLoadFinish(pageNo, newMessages.size(), "查看更早消息...", "#F29c9F");
//				mListView.hideFooterView();
				} else {
					mListView.onLoadFinish(pageNo, newMessages.size(), "加载更多");
				}
			}

		}else{
			mListView.hideFooterView();
		}
	}




	@Override
	public void onRefresh() {
		pageNo = 1;
		getNewMsgList();
	}

	@Override
	public void onLoadMore() {
		pageNo ++;
		pageSize = 10;
		getNewMsgList();
	}
}
