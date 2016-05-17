package com.anda.moments.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.RongCloudEvent;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.constant.api.ReqUrls;
import com.anda.moments.ui.base.BaseFragmentActivity;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.views.ActionBar;

import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by pengweiqiang on 16/5/4.
 * 会话列表
 */
public class ConversationListActivity extends BaseFragmentActivity{

	ActionBar mActionbar;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_list);

		setActionBar();

		isReconnect();
	}

	/**
	 * 加载 会话列表 ConversationListFragment
	 */
	private void enterFragment() {

		ConversationListFragment fragment = (ConversationListFragment) getSupportFragmentManager().findFragmentById(R.id.conversationlist);

		Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
				.appendPath("conversationlist")
				.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
				.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
				.appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
				.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
				.build();

		fragment.setUri(uri);

	}

	/**
	 * 设置 actionbar 事件
	 */
	private void setActionBar() {

		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("消息列表");
//		mActionbar.hideBottonLine();
		mActionbar.setLeftActionButtonListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});
	}

	/**
	 * 判断消息是否是 push 消息
	 */
	private void isReconnect() {

		Intent intent = getIntent();
		String token = (String) SharePreferenceManager.getSharePreferenceValue(ConversationListActivity.this, Constant.FILE_NAME, ReqUrls.TOKEN_RONG,"");


		//push，通知或新消息过来
		if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

			//通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
			if (intent.getData().getQueryParameter("push") != null
					&& intent.getData().getQueryParameter("push").equals("true")) {

				reconnect(token);
			} else {
				//程序切到后台，收到消息后点击进入,会执行这里
				if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

					reconnect(token);
				} else {
					enterFragment();
				}
			}
		}
	}

	/**
	 * 重连
	 *
	 * @param token
	 */
	private void reconnect(String token) {

		if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

			RongIM.connect(token, new RongIMClient.ConnectCallback() {
				@Override
				public void onTokenIncorrect() {

				}

				@Override
				public void onSuccess(String s) {
					if (RongCloudEvent.getInstance() != null)
						RongCloudEvent.getInstance().setOtherListener();
					enterFragment();
				}

				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {

				}
			});
		}
	}

}
