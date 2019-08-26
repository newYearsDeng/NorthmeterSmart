package com.northmeter.northmetersmart.model;


import com.northmeter.northmetersmart.androidmenutoabhost.SlideView_toDisplay;

/**设备显示与隐藏管理 model*/
public class Device_Display_Model {
	/**名字*/
	public String device_name;
	/**类型*/
	public String device_type;
	/**表号*/
	public String deivce_meter;
	/**主节点类型*/
	public String masternode_type;
	/**主节点表号*/
	public String masternode_num;
	/**是否显示选择按钮*/
	private boolean visibility;
	/**是否选择框*/
	private boolean check;
	
	public SlideView_toDisplay slideView;
	
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getDevice_type() {
		return device_type;
	}
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	public String getDeivce_meter() {
		return deivce_meter;
	}
	public void setDeivce_meter(String deivce_meter) {
		this.deivce_meter = deivce_meter;
	}
	public String getMasternode_type() {
		return masternode_type;
	}
	public void setMasternode_type(String masternode_type) {
		this.masternode_type = masternode_type;
	}
	public String getMasternode_num() {
		return masternode_num;
	}
	public void setMasternode_num(String masternode_num) {
		this.masternode_num = masternode_num;
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
	
	
	
	
}
