package com.anda.moments.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.anda.moments.R;
import com.anda.moments.ui.LoginActivity;
import com.anda.moments.ui.MemberActivity;
import com.anda.moments.ui.MyAdviceActivity;
import com.anda.moments.ui.MyCollectActivity;
import com.anda.moments.ui.MyMessageActivity;
import com.anda.moments.ui.MyOrderActivity;
import com.anda.moments.ui.VefityPhoneActivity;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 我的
 * @author will
 *
 */
public class MyFragment extends BaseFragment{

	private View mContentView;
	private ActionBar mActionBar;
	
	private View mCallCustom,mCheckVersion,mBuyMember,mMyOrder,mMyTeam,mMyCollect,mMyAdvice,mMyMessage;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.my, container,false);
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
		mActionBar.setTitle(R.string.tab_my);
		mActionBar.hideLeftActionButton();
		
		
		mCallCustom = mContentView.findViewById(R.id.call_custom);
		mCheckVersion = mContentView.findViewById(R.id.check_version);
		mBuyMember = mContentView.findViewById(R.id.buy_member);
		mMyOrder = mContentView.findViewById(R.id.my_order);
		mMyTeam = mContentView.findViewById(R.id.my_team);
		mMyCollect = mContentView.findViewById(R.id.my_collect);
		mMyAdvice = mContentView.findViewById(R.id.my_advice);
		mMyMessage = mContentView.findViewById(R.id.my_message);
		
		//View层级上把硬件加速关掉
		mContentView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
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
	
	private void initListener(){
		mCallCustom.setOnClickListener(myOnclickListener);
		mCheckVersion.setOnClickListener(myOnclickListener);
		mBuyMember.setOnClickListener(myOnclickListener);
		mMyOrder.setOnClickListener(myOnclickListener);
		mMyTeam.setOnClickListener(myOnclickListener);
		mMyCollect.setOnClickListener(myOnclickListener);
		mMyAdvice.setOnClickListener(myOnclickListener);
		mMyMessage.setOnClickListener(myOnclickListener);
	}
	private void startLoginActivity(){
		Intent intent = new Intent(mActivity,LoginActivity.class);
		startActivity(intent);
	}
	OnClickListener myOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.my_advice:
				startActivity(MyAdviceActivity.class);
				break;
			case R.id.my_collect:
				if(isLogined()){
					startActivity(MyCollectActivity.class);
				}else{
					startLoginActivity();
				}
				
				break;
			case R.id.my_message:
				if(isLogined()){
					startActivity(MyMessageActivity.class);
				}else{
					startLoginActivity();
				}
				
				break;
			case R.id.my_team:
				startLoginActivity();
				break;
			case R.id.my_order:
				Intent intentMyOrder = new Intent(mActivity,MyOrderActivity.class);
				startActivity(intentMyOrder);
				break;
			case R.id.buy_member:
				Intent intentMember = new Intent(mActivity,MemberActivity.class);
				startActivity(intentMember);
				break;
			case R.id.call_custom://拨打电话
				Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-5232123"));
				startActivity(intentCall);
				break;
			case R.id.check_version://检测新版本

			    
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				    @Override
				    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				        switch (updateStatus) {
				        case UpdateStatus.Yes: // has update
				            UmengUpdateAgent.showUpdateDialog(mActivity, updateInfo);
				            break;
				        case UpdateStatus.No: // has no update
				        	ToastUtils.showToast(mActivity, "已经是最新版本");
				            break;
				        case UpdateStatus.NoneWifi: // none wifi
				        	ToastUtils.showToast(mActivity, "没有wifi连接， 只在wifi下更新");
				            break;
				        case UpdateStatus.Timeout: // time out
				        	ToastUtils.showToast(mActivity, "请求超时，稍后请重试!");
				            break;
				        }
				    }
				});
			    UmengUpdateAgent.forceUpdate(mActivity);
				break;
			default:
				break;
			}
		}
	};
	
	
}
