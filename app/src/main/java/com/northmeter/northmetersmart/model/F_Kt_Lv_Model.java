package com.northmeter.northmetersmart.model;

public class F_Kt_Lv_Model {
	private boolean check;
	private int id;
	private String time;
	private String ampm;
	private String weekday;
	private String txt1;
	private String txt2;
	private String txt3;
	private String txt4;
	private String txt5;
	private String txt6;
	private boolean visibility;//是否显示选择按钮
	
	
	
	public boolean isVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public String getTxt6() {
		return txt6;
	}

	public void setTxt6(String txt6) {
		this.txt6 = txt6;
	}

	public String getTxt5() {
		return txt5;
	}

	public void setTxt5(String txt5) {
		this.txt5 = txt5;
	}

	public String getTxt4() {
		return txt4;
	}

	public void setTxt4(String txt4) {
		this.txt4 = txt4;
	}


	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getAmpm() {
		return ampm;
	}
	
	public void setAmpm(String ampm) {
		this.ampm = ampm;
	}
	
	public String getWeekday() {
		return weekday;
	}
	
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}
	
	public String getTxt1() {
		return txt1;
	}
	
	public void setTxt1(String txt1) {
		this.txt1 = txt1;
	}
	
	public String getTxt2() {
		return txt2;
	}
	
	public void setTxt2(String txt2) {
		this.txt2 = txt2;
	}
	
	public String getTxt3() {
		return txt3;
	}
	
	public void setTxt3(String txt3) {
		this.txt3 = txt3;
	}

	
	public boolean isCheck() {
		return check;
	}
	
	public void setCheck(boolean check) {
		this.check = check;
	}
	
	public F_Kt_Lv_Model(){
		super();
		}
	
	public F_Kt_Lv_Model(boolean check,int id,String time,String ampm,String txt1,String txt2,String txt3,String txt4,String txt5,String txt6){
		this.check=check;
		this.id=id;
		this.time=time;
		this.ampm=ampm;
		this.txt1=txt1;
		this.txt2=txt2;
		this.txt3=txt3;
		this.txt4=txt4;
		this.txt5=txt5;
		this.txt6=txt6;
	}
	
}
