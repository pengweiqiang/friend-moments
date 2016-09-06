package com.anda.moments.entity;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String phoneNum;
	private String icon;
	private String userName;
	private String userId;
	private String gender;
	private String addr;
//	private String address;
	private String district;
	private String summary;
	private String descTag;
	private int flag;// flag—0表示已添加，flag-1表示接受好友请求，flag-2表示拒绝好友邀请,flag-4未添加
	private long createTime;
	private long relationId;
	private String skinPath;//皮肤地址
	private String birthdayTime;//出生年月
	private String isNeedValidate;//加好友是否需要验证  默认yes需要  no为不需要
	private String isLookMyInfo; //不让某人看我的朋友圈  默认no,yes为指定用户看我的朋友圈
	private String isLookOtherInfo;//是否看别人的动态，默认yes,查看任何好友动态
	private String isBlock;//0 不拉黑 1拉黑

	private String sortLetters; // 显示数据拼音的首字母
	private String suoxie;// 姓名缩写

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

	public String getUserName() {
		return userName==null?"":userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId==null?"":userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGender() {
		return gender==null?"":gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}

	public String getDistrict() {
		return district==null?"":district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getSummary() {
		return summary==null?"":summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescTag() {
		return descTag==null?"":descTag;
	}

	public void setDescTag(String descTag) {
		this.descTag = descTag;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getSuoxie() {
		return suoxie;
	}

	public void setSuoxie(String suoxie) {
		this.suoxie = suoxie;
	}

	public String getAddr() {
		return addr==null?"":addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getRelationId() {
		return relationId;
	}

	public void setRelationId(long relationId) {
		this.relationId = relationId;
	}

	public String getSkinPath() {
		return skinPath;
	}

	public void setSkinPath(String skinPath) {
		this.skinPath = skinPath;
	}

	public String getBirthdayTime() {
		return birthdayTime;
	}

	public void setBirthdayTime(String birthdayTime) {
		this.birthdayTime = birthdayTime;
	}

	public String getIsNeedValidate() {
		return isNeedValidate==null?"":isNeedValidate;
	}

	public void setIsNeedValidate(String isNeedValidate) {
		this.isNeedValidate = isNeedValidate;
	}

	public String getIsLookMyInfo() {
		return isLookMyInfo==null?"":isLookMyInfo;
	}

	public void setIsLookMyInfo(String isLookMyInfo) {
		this.isLookMyInfo = isLookMyInfo;
	}

	public String getIsLookOtherInfo() {
		return isLookOtherInfo==null?"":isLookOtherInfo;
	}

	public void setIsLookOtherInfo(String isLookOtherInfo) {
		this.isLookOtherInfo = isLookOtherInfo;
	}

	public String getIsBlock() {
		return isBlock;
	}

	public void setIsBlock(String isBlock) {
		this.isBlock = isBlock;
	}
}
