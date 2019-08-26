package com.northmeter.northmetersmart.model;

public class Machine_LvModel {
	private String id;
	private String ischeck;
	private String title;
	private String type;
	private int status;
	private String contact;
	
	

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIscheck() {
		return ischeck;
	}

	public void setIscheck(String ischeck) {
		this.ischeck = ischeck;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Machine_LvModel(String id, String ischeck, String title,
			String type, int status , String contact) {
		super();
		this.id = id;
		this.ischeck = ischeck;
		this.title = title;
		this.type = type;
		this.status = status;
		this.contact = contact;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Machine_LvModel() {
		super();
		// TODO 自动生成的构造函数存根
	}

}
