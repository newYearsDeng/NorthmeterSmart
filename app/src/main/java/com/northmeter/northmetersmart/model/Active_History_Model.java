package com.northmeter.northmetersmart.model;

/**
 * 活动流历史记录model*/
public class Active_History_Model {

	/**日期*/
	public String data;
	/**时间*/
	public String time;
	/**动作*/
	public String active;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
	
}
