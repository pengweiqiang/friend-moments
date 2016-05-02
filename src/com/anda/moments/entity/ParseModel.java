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

	private JsonElement results;

	private JsonElement result;

	private int ReqCount;//好友请求个数
	
	private User user;

	private User publishUser;
	private List<Infos> infos;


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
		return ReqCount;
	}

	public void setReqCount(int reqCount) {
		ReqCount = reqCount;
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
}
