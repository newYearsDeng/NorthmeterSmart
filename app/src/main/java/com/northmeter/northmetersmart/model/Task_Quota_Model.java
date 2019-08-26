package com.northmeter.northmetersmart.model;

/**
 * 定额任务models*/
public class Task_Quota_Model {
	private String task_id;//任务编号
	private String quota_data;//定额值
	private String quota_control;//操作类型
	private String meter;//表号
	private String type;//设备类型
	private boolean visibility;//是否显示选择按钮
	private boolean check;//是否选择框
	

	
	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getTask_id() {
		return task_id;
	}



	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}



	public String getQuota_data() {
		return quota_data;
	}



	public void setQuota_data(String quota_data) {
		this.quota_data = quota_data;
	}



	public String getQuota_control() {
		return quota_control;
	}



	public void setQuota_control(String quota_control) {
		this.quota_control = quota_control;
	}



	public String getMeter() {
		return meter;
	}



	public void setMeter(String meter) {
		this.meter = meter;
	}



	public boolean isVisibility() {
		return visibility;
	}



	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}



	public boolean isCheck() {
		return check;
	}



	public void setCheck(boolean check) {
		this.check = check;
	}



	public Task_Quota_Model(){
		
	}
	
	
}
