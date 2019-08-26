package com.northmeter.northmetersmart.model;

/**
 * 一次性跳合闸任务models*/
public class Kt_Task_OneModel {
//addOneoffTask/2016,9,1,13,50,23#6#tz#meter=150721023750&eqpt_type=0a0001aa7k
	private String task_id;//任务编号
	private String time;//执行时间
	private String continue_time;//持续时间
	private String control_id;//具体操作
	private String control_type;//操作类型
	private String meter;//表号
	private boolean visibility;//是否显示选择按钮
	private boolean check;//是否选择框
	
	
	
	
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public boolean isVisibility() {
		return visibility;
	}
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContinue_time() {
		return continue_time;
	}
	public void setContinue_time(String continue_time) {
		this.continue_time = continue_time;
	}
	public String getControl_type() {
		return control_type;
	}
	public void setControl_type(String control_type) {
		this.control_type = control_type;
	}
	public String getControl_id() {
		return control_id;
	}
	public void setControl_id(String control_id) {
		this.control_id = control_id;
	}
	public String getMeter() {
		return meter;
	}
	public void setMeter(String meter) {
		this.meter = meter;
	}
	
	public Kt_Task_OneModel(){
		
	}
	
	
}
