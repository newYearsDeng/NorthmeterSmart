package com.northmeter.northmetersmart.device;

public class MyDeviceData {
	private String mac;
	private String timeNow;
	private String stateLastTime;

	// 总电能 8
	private String zdn;
	// 电压 4
	private String dy;
	// 电流 6
	private String dl;
	// 功率 6
	private String gl;
	// 无功???? 6
	private String wg;
	// 频率 4
	private String pl;
	// 功率因数 4
	private String glys;
	// 跳合闸状态 2
	private String thzzt;

	public String getZdn() {
		return zdn;
	}

	public MyDeviceData() {

	}

	public MyDeviceData(String mac, String timeNow, String stateLastTime,
			String zdn, String dy, String dl, String gl, String wg, String pl,
			String glys, String thzzt) {

		this.setMac(mac);
		this.setTimeNow(timeNow);
		this.stateLastTime = stateLastTime; // on/off

		// 总电能 8
		this.zdn = zdn;
		// 电压 4
		this.dy = dy;
		// 电流 6
		this.dl = dl;
		// 功率 6
		this.gl = gl;
		// 无功???? 6
		this.wg = wg;
		// 频率 4
		this.pl = pl;
		// 功率因数 4
		this.glys = glys;
		// 跳合闸状态 2
		this.thzzt = thzzt;
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

	public void setZdn(String zdn) {
		this.zdn = zdn;
	}

	public String getDy() {
		return dy;
	}

	public void setDy(String dy) {
		this.dy = dy;
	}

	public String getDl() {
		return dl;
	}

	public void setDl(String dl) {
		this.dl = dl;
	}

	public String getGl() {
		return gl;
	}

	public void setGl(String gl) {
		this.gl = gl;
	}

	public String getWg() {
		return wg;
	}

	public void setWg(String wg) {
		this.wg = wg;
	}

	public String getPl() {
		return pl;
	}

	public void setPl(String pl) {
		this.pl = pl;
	}

	public String getGlys() {
		return glys;
	}

	public void setGlys(String glys) {
		this.glys = glys;
	}

	public String getThzzt() {
		return thzzt;
	}

	public void setThzzt(String thzzt) {
		this.thzzt = thzzt;
	}
}
