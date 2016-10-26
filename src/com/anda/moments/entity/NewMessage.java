package com.anda.moments.entity;

import java.io.Serializable;

/**
 * 新消息
 * @author pengweiqiang
 *
 */
public class NewMessage implements Serializable{


	/**
	 * id : 819
	 * infoId : 1106
	 * commentId : 824
	 * commentText : 刚刚好
	 * phoneNum : 13975139573
	 * isRead : 0
	 * createTime : 1476237448000
	 */

	private int id;
	private long infoId;
	private int commentId;
	private String commentText;
	private String phoneNum;
	private String isRead;
	private long createTime;
	/**
	 * msgType : 1
	 * myIcon : http://ob9qgxnlb.bkt.clouddn.com/images/13041018655/icon/1465025897552.png
	 * commUserIcon :
	 * commPhoneNum : 13651363843
	 * attaIcon : null
	 * newsText : null
	 */

	private String msgType;
	private String myIcon;
	private String commUserIcon;
	private String commPhoneNum;
	private String attaIcon;
	private String newsText;
	/**
	 * myUserName : (⊙o⊙)
	 * commUserName : We're12345
	 * attaType : 1
	 */

	private String myUserName;//我的名字
	private String commUserName;//评论人名称
	private String attaType;//attaType-1-图片，2-音频，3-视频


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getInfoId() {
		return infoId;
	}

	public void setInfoId(long infoId) {
		this.infoId = infoId;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCommentText() {
		return commentText==null?"":commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType==null?"":msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMyIcon() {
		return myIcon;
	}

	public void setMyIcon(String myIcon) {
		this.myIcon = myIcon;
	}

	public String getCommUserIcon() {
		return commUserIcon;
	}

	public void setCommUserIcon(String commUserIcon) {
		this.commUserIcon = commUserIcon;
	}

	public String getCommPhoneNum() {
		return commPhoneNum;
	}

	public void setCommPhoneNum(String commPhoneNum) {
		this.commPhoneNum = commPhoneNum;
	}

	public String getAttaIcon() {
		return attaIcon;
	}

	public void setAttaIcon(String attaIcon) {
		this.attaIcon = attaIcon;
	}

	public String getNewsText() {
		return newsText;
	}

	public void setNewsText(String newsText) {
		this.newsText = newsText;
	}

	public String getMyUserName() {
		return myUserName;
	}

	public void setMyUserName(String myUserName) {
		this.myUserName = myUserName;
	}

	public String getCommUserName() {
		return commUserName;
	}

	public void setCommUserName(String commUserName) {
		this.commUserName = commUserName;
	}

	public String getAttaType() {
		return attaType;
	}

	public void setAttaType(String attaType) {
		this.attaType = attaType;
	}
}
