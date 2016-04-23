package com.anda.moments.entity;

import com.anda.gson.JsonElement;

public class ParseModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object apiResult;
	
	private int resultCount; //总数
	
	private String jsessionid;
	
	private String userRandom;
	
	private JsonElement userMessage;
	
	private String url;
	
	private int ishfuser;//1代表开通汇付  0代表未开通汇付
	
	private String ordId;
	
	private String otherStr;
	
	
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

	public String getUserRandom() {
		return userRandom;
	}

	public void setUserRandom(String userRandom) {
		this.userRandom = userRandom;
	}

	public JsonElement getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(JsonElement userMessage) {
		this.userMessage = userMessage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIshfuser() {
		return ishfuser;
	}

	public void setIshfuser(int ishfuser) {
		this.ishfuser = ishfuser;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	public String getOtherStr() {
		return otherStr;
	}

	public void setOtherStr(String otherStr) {
		this.otherStr = otherStr;
	}
	
	
	
	
}
