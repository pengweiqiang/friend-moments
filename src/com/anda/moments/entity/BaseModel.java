package com.anda.moments.entity;

import java.io.Serializable;

import com.anda.gson.JsonElement;

public class BaseModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8998410069756234518L;
	
	//{"state":"OK","userMessage":{"id":1,"name":"1","password":"1","schoolName":"1","studentId":"1","telephone":"13811658342"},"userRandom":"pv1jn92jvosoifx"}
	private String retFlag;//接口状态码
	
	private String info;//接口提示信息
	
	private JsonElement data;
	

	


	public String getRetFlag() {
		return retFlag;
	}
	public void setRetFlag(String retFlag) {
		this.retFlag = retFlag;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}
}
