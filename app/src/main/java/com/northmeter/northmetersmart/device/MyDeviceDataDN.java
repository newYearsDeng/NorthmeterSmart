package com.northmeter.northmetersmart.device;

public class MyDeviceDataDN {
	private String mac;
	private String timeNow;
	private String stateLastTime;

	// 功率下限 6
	private String glxx;
	// 功率上限 6
	private String glsx;
	// 电量上限 8
	private String dlsx;
	// 跳合闸延时时间 4
	private String tzyssj;
	
	

	public MyDeviceDataDN(String mac, String timeNow, String stateLastTime,
			String glxx, String glsx, String dlsx, String tzyssj) {

		this.setMac(mac);
		this.setTimeNow(timeNow);
		this.stateLastTime = stateLastTime; // on/off

		// 功率下限 6
		this.glxx = glxx;
		// 功率上限 6
		this.glsx = glsx;
		// 电量上限 8
		this.dlsx = dlsx;
		// 跳合闸延时时间 4
		this.tzyssj = tzyssj;
		
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

	public String getStateLastTime() {
		return stateLastTime;
	}

	public void setStateLastTime(String stateLastTime) {
		this.stateLastTime = stateLastTime;
	}

	public String getGlxx() {
		return glxx;
	}

	public void setGlxx(String glxx) {
		this.glxx = glxx;
	}

	public String getGlsx() {
		return glsx;
	}

	public void setGlsx(String glsx) {
		this.glsx = glsx;
	}

	public String getDlsx() {
		return dlsx;
	}

	public void setDlsx(String dlsx) {
		this.dlsx = dlsx;
	}

	public String getTzyssj() {
		return tzyssj;
	}

	public void setTzyssj(String tzyssj) {
		this.tzyssj = tzyssj;
	}

}
