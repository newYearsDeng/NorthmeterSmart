package com.northmeter.northmetersmart.model;



public class Fragment_mac_trance {
	
	private static Fragment_mac_trance uniqueInstance = null;
	public static Fragment_mac_trance getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Fragment_mac_trance();
		}
		return uniqueInstance;
	}

	private String mac;
	private String type_select;
	
	

	public String getType_select() {
		return type_select;
	}

	public void setType_select(String type_slect) {
		this.type_select = type_slect;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public Fragment_mac_trance(){
		
	};
}
