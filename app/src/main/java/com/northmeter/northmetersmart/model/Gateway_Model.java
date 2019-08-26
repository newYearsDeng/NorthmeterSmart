package com.northmeter.northmetersmart.model;


import com.northmeter.northmetersmart.androidmenutoabhost.SlideView;

/**网关内档案model*/
public class Gateway_Model {

	private String table_num;
	private String name;
	private String type;
	private String online;//是否在线
	private String cancellation;//通讯质量
    public SlideView slideView;
    
    
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCancellation() {
		return cancellation;
	}
	public void setCancellation(String cancellation) {
		this.cancellation = cancellation;
	}
	public String getTable_num() {
		return table_num;
	}
	public void setTable_num(String table_num) {
		this.table_num = table_num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	
	public Gateway_Model(){		
	};
	
	public Gateway_Model(String table_num,String name,String type,String online,String cancellation){
		this.table_num = table_num;
		this.name = name;
		this.type = type;
		this.online = online;
		this.cancellation = cancellation;
		
	}
}
