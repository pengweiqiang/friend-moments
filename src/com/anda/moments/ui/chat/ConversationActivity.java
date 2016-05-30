package com.anda.moments.ui.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by pengweiqiang on 16/5/4.
 * 聊天界面
 */
public class ConversationActivity extends BaseFragmentActivity{

    ActionBar mActionbar;



    private String mTargetId;

    String title = "";

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        Intent intent = getIntent();


//        RongIM.getInstance().refreshUserInfoCache(new UserInfo("userId", "啊明", Uri.parse("http://rongcloud-web.qiniudn.com/docs_demo_rongcloud_logo.png")));

        getIntentDate(intent);

        setActionBar();

        isReconnect(intent);
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        title = intent.getData().getQueryParameter("title");

        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
    }


    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }


    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {

        String token = (String)SharePreferenceManager.getSharePreferenceValue(ConversationActivity.this, Constant.FILE_NAME, ReqUrls.TOKEN_RONG,"");

//        if (DemoContext.getInstance() != null) {
//
//            token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
//        }

        //push或通知过来
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
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }

    /**
     * 设置 actionbar 事件
     */
    private void setActionBar() {

        mActionbar = (ActionBar)findViewById(R.id.actionBar);
        mActionbar.setTitle(title);
//		mActionbar.hideBottonLine();
        mActionbar.setLeftActionButtonListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
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

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

}
