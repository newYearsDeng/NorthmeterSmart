package com.northmeter.northmetersmart.model;
/*空调红外码的对象类*/
public class MyInfraredCode {
	private String mac;
	private String time;
	private String version;
	private String switchs;
	private String model;
	private String temputer;
	private String speed;
	private String updown;
	private String leftright;
	private String weeks;
	private int id;
	private int rand;
	
	
	
	public String getWeeks() {
		return weeks;
	}
	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}
	public int getRand() {
		return rand;
	}
	public void setRand(int rand) {
		this.rand = rand;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSwitchs() {
		return switchs;
	}
	public void setSwitchs(String switchs) {
		this.switchs = switchs;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getTemputer() {
		return temputer;
	}
	public void setTemputer(String temputer) {
		this.temputer = temputer;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getUpdown() {
		return updown;
	}
	public void setUpdown(String updown) {
		this.updown = updown;
	}
	public String getLeftright() {
		return leftright;
	}
	public void setLeftright(String leftright) {
		this.leftright = leftright;
	}
	public MyInfraredCode(){
		
	}
	
	public MyInfraredCode(int rand,String mac, String time,String version,String switchs,String model,String temputer,String speed,String updown,String leftright,String weeks){
		this.rand=rand;
		this.mac=mac;
		this.time=time;
		this.version=version;
		this.switchs=switchs;
		this.model=model;
		this.temputer=temputer;
		this.speed=speed;
		this.updown=updown;
		this.leftright=leftright;
		this.weeks=weeks;
	}
}
