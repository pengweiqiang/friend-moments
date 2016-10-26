package com.anda.moments.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * @author will on 16/10/12 17:01
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */
public class ObservableNewMessage extends Observable{
    private static ObservableNewMessage observableNewMessage = null;


    private ObservableNewMessage(){

    }

    public static ObservableNewMessage getInstance(){
        if(observableNewMessage == null){
            observableNewMessage = new ObservableNewMessage();
        }
        return observableNewMessage;
    }

    public void setNewMessage(int newMessageCount,String iconUrl){
        setChanged();
        List<String> messages = new ArrayList<String>();
        messages.add(String.valueOf(newMessageCount));
        messages.add(iconUrl);
        notifyObservers(messages);
    }


    public void setNewReqFriendCount(int newReqFriendCount){
        setChanged();

        notifyObservers(newReqFriendCount);
    }
}
