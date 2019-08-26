package com.northmeter.northmetersmart.device;

public class MyDiagramData {
	private String mac;
	private String timeNow;
	private String zdn;
	private String gl;
	private String temp;//温度

	public MyDiagramData() {

	}

	public MyDiagramData(String mac, String timeNow, String zdn,String gl,String temp) {
		this.mac = mac;
		this.timeNow = timeNow;
		this.zdn = zdn;
		this.gl=gl;
		this.temp=temp;
	}
	
	
	
	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getTimeNow() {
		return timeNow;
	}

	public void setTimeNow(String timeNow) {
		this.timeNow = timeNow;
	}

	public String getZdn() {
		return zdn;
	}

	public void setZdn(String zdn) {
		this.zdn = zdn;
	}

	public String getGl() {
		return gl;
	}

	public void setGl(String gl) {
		this.gl = gl;
	}
	
}
