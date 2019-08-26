package com.northmeter.northmetersmart.model;

public class Main_GvModel {
	private String id;
	private int img;
	private String building_id;//id
	private String build_name;//名字
	private String build_roleid;//权限
	private int width;
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public String getBuilding_id() {
		return building_id;
	}

	public void setBuilding_id(String building_id) {
		this.building_id = building_id;
	}

	public String getBuild_name() {
		return build_name;
	}

	public void setBuild_name(String build_name) {
		this.build_name = build_name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getBuild_roleid() {
		return build_roleid;
	}

	public void setBuild_roleid(String build_roleid) {
		this.build_roleid = build_roleid;
	}

	public Main_GvModel() {
		super();
	}

	public Main_GvModel(String id,int img,String building_id,String build_name,String build_roleid,int width) {
		super();
		this.id = id;
		this.img = img;
		this.building_id = building_id;
		this.build_name = build_name;
		this.build_roleid = build_roleid;
		this.width = width;
	}

}
