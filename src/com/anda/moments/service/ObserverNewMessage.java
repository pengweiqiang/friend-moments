package com.anda.moments.service;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author will on 16/10/12 17:04
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */
public class ObserverNewMessage implements Observer{

    NewMessageListener newMessageListener;

    public ObserverNewMessage(Observable observable){
        observable.addObserver(this);
    }


    @Override
    public void update(Observable observable, Object data) {
        if(newMessageListener==null){
            return;
        }
        if(data instanceof Integer) {//请求好友
            newMessageListener.getNewReqFriendCount((Integer)data);
        }else{
            List<String> messages = (List<String>) data;
            newMessageListener.getNewMessage(messages);
        }
    }

    public interface NewMessageListener{
        public void getNewMessage(List<String> messages);

        public void getNewReqFriendCount(int reqFriendCount);
    }

    public void setNewMessageListener(NewMessageListener newMessageListener){
        this.newMessageListener = newMessageListener;
    }
}
