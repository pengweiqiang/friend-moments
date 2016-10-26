package com.anda.moments.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pengweiqiang on 16/5/8.
 */
public class CommentUser implements Serializable,Cloneable {

    public CommentUser(){}

    private long commentId;//评论id
    private int type;//评论类型  1是评论 2是萌化了
    private String userName;//userName-评论人
    private String userId;
    private String phoneNum;
    private String icon;
    private String text;
    private long publishTime;

    private String replyText;
    private String commUserName;//commUserName-被评论人。
    private String commPhoneNum;

    private List<CommentUser> subCommUsers;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<CommentUser> getSubCommUsers() {
        return subCommUsers;
    }

    public void setSubCommUsers(List<CommentUser> subCommUsers) {
        this.subCommUsers = subCommUsers;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public String getCommUserName() {
        return commUserName;
    }

    public void setCommUserName(String commUserName) {
        this.commUserName = commUserName;
    }

    public String getCommPhoneNum() {
        return commPhoneNum;
    }

    public void setCommPhoneNum(String commPhoneNum) {
        this.commPhoneNum = commPhoneNum;
    }

    public CommentUser clone(){
        CommentUser commentUser = null;
        try{
            commentUser = (CommentUser) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return commentUser;
    }

}
