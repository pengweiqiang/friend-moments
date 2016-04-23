package com.anda.moments.entity;

import java.io.Serializable;

/**
 * 兼职dto
 * @author pengweiqiang
 *
 */
public class PartTimeJob implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2212240942328035052L;
	
	private long id;
	private String name;
	private String time;
	private String money;
	private String distance;
	
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	
}
