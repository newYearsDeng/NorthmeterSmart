package com.northmeter.northmetersmart.model;

public class Center_LvModel {
	private String title;
	private Integer imgs;
	private String version;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getImgs() {
		return imgs;
	}

	public void setImgs(Integer imgs) {
		this.imgs = imgs;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Center_LvModel(String title, Integer imgs, String version) {
		super();
		this.title = title;
		this.imgs = imgs;
		this.version = version;
	}

	public Center_LvModel() {
		super();
	}

}
