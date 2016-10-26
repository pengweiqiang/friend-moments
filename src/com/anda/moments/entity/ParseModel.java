package com.anda.moments.entity;

import com.anda.gson.JsonElement;

import java.util.List;

public class ParseModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object apiResult;
	
	private int resultCount; //总数
	
	private String jsessionid;

	private String validateCode;

	private JsonElement results;

	private JsonElement result;

	private int reqCount;//好友请求个数
	
	private User user;

	private int isCanChat;//0不可聊天 1不可以聊天

	private User publishUser;
	private List<Infos> infos;

	private int newMsgNum;//最新评论信息数量
	private String icon;//最新消息评论人的头像

	private List<NewMessage> msgInfo;


	public Object getApiResult() {
		return apiResult;
	}

	public void setApiResult(Object apiResult) {
		this.apiResult = apiResult;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public String getJsessionid() {
		return jsessionid;
	}

	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JsonElement getResults() {
		return results;
	}

	public void setResults(JsonElement results) {
		this.results = results;
	}

	public int getReqCount() {
		return reqCount;
	}

	public void setReqCount(int reqCount) {
		reqCount = reqCount;
	}

	public JsonElement getResult() {
		return result;
	}

	public void setResult(JsonElement result) {
		this.result = result;
	}

	public User getPublishUser() {
		return publishUser;
	}

	public void setPublishUser(User publishUser) {
		this.publishUser = publishUser;
	}

	public List<Infos> getInfos() {
		return infos;
	}

	public void setInfos(List<Infos> infos) {
		this.infos = infos;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public int getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum) {
		this.newMsgNum = newMsgNum;
	}

	public List<NewMessage> getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(List<NewMessage> msgInfo) {
		this.msgInfo = msgInfo;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getIsCanChat() {
		return isCanChat;
	}

	public void setIsCanChat(int isCanChat) {
		this.isCanChat = isCanChat;
	}
}
