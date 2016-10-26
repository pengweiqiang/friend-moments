package com.anda.moments.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.anda.moments.MyApplication;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.utils.HttpConnectionUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author will on 16/10/12 16:56
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */
public class MessageService extends Service{

    public Timer mTimer = new Timer();// 定时器
    private static final int PERIOD_TIME = 5*1000;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ObservableNewMessage observableNewMessage;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        observableNewMessage = ObservableNewMessage.getInstance();
        timerTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void timerTask() {
        //创建定时线程执行更新任务
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                hanlder.sendEmptyMessage(1);
            }
        }, 2000, PERIOD_TIME);// 定时任务
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();// 程序退出时cancel timer
    }

    private Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                findLatestCommInfo();

                getNewReqFriend();
            }
        }
    };

    /**
     * 获取最新评论数量
     */
    private void findLatestCommInfo(){
        User user = MyApplication.getInstance().getCurrentUser();
        if(user!=null){
            final String phoneNum = user.getPhoneNum();
            ApiMomentsUtils.findLatestCommInfo(this, phoneNum, "2","1","1", new HttpConnectionUtil.RequestCallback() {
                @Override
                public void execute(ParseModel parseModel) {
                    if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
                        int newMsgNum = parseModel.getNewMsgNum();

                        //发送消息
                        observableNewMessage.setNewMessage(newMsgNum,parseModel.getIcon());
                    }
                }
            });
        }

    }

    /**
     * 获取新好友请求
     */
    private void getNewReqFriend(){
        User user = MyApplication.getInstance().getCurrentUser();
        if(user!=null) {
            final String phoneNum = user.getPhoneNum();

            ApiMomentsUtils.getMyFriendsList(this, phoneNum,1, new HttpConnectionUtil.RequestCallback() {
                @Override
                public void execute(ParseModel parseModel) {
                    if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
                        int reqCount = parseModel.getReqCount();//好友请求个数
                        if(reqCount>0) {
                            //发送消息
                            observableNewMessage.setNewReqFriendCount(reqCount);
                        }else{
                        }

                    }
                }
            });
        }

    }
}
