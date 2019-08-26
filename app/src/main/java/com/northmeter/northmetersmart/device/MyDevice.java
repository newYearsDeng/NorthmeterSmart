package com.northmeter.northmetersmart.device;

import java.util.ArrayList;
import java.util.List;

public class MyDevice {

	// 设备类型
	public static final int DEVICE_NULL = -1;
	/**wifi插座*/
	public static final int DEVICE_WIFI_SMART_SOCKET = 0;  
	
	/**zigbee插座*/
	public static final int DEVICE_ZIGBEE_SMART_SOCKET = 1; 
	
	/**Wifi转Zigbee智能用电网关/无线智能网关*/
	public static final int DEVICE_WIFI_SMART_GATEWAY = 2; 
	
	/**以太网转Zigbee智能用电网关*/
	public static final int DEVICE_ZIGBEE_SMART_GATEWAY = 3;
	
	/**zigbee空调控制器*/
	public static final int DEVICE_ZIGBEE_SMART_AIR = 4;  
	
	/**wifi空调控制器*/
	public static final int DEVICE_WIFI_SMART_AIR = 5;   
	
	/**智能网关*/
	public static final int DEVICE_Translation_GATEWAY = 6; 
	
	/**zigbee中央空调控制器*/
	public static final int DEVICE_CENTER_SMART_AIR = 7; 
	
	/**zigbee四路灯控面板*/
	public static final int DEVICE_ZIGBEE_SOCKET_FOUR = 8; 
	
	/**空气质量检车仪*/
	public static final int DEVICE_Air_Quality_Qetector = 9; 
	
	// 电器类型
	public static final int ELEC_NULL = -1;
	public static final int ELEC_TELEVISION = 0;// 电视
	public static final int ELEC_COMPUTER = 1;// 电脑
	public static final int ELEC_LIGHT = 2;// 电灯
	public static final int ELEC_RICE_cooker = 3;// 电饭煲
	public static final int ELEC_FAN = 4;// 电风扇
	public static final int ELEC_HEATER = 5;// 热水器
	public static final int ELEC_WATER_dispenser = 6;// 饮水机
	public static final int ELEC_LIVINGTV = 7;// 客厅电视机
	public static final int ELEC_OTHERS = 8;// 其他
	public static final int ELEC_ZIGBEE_SOCKET = 9;//zigbee 网关
	public static final int ELEC_AIR = 10 ; //空调
	public static final int ELEC_CENTER_AIR = 11 ; //中央空调
	public static final int ELEC_FOUR_CONTROL = 12;//四路灯控

	private static final int TOTAL_ARTTIBUTES = 8;
	/**设备类型*/
	private int type; 
	/**wifi插座的表号，可为null*/
	private String tableNum; 
	/**mac地址，唯一身份识别*/
	private String mac;
	private String ip;
	/**电器类型*/
	private int elec_type;
	private String name;
	/**版本号*/
	private String version;
	/**网关和zigbee设备的内在联系*/
	private String contact;

	public MyDevice() {

	}

	public MyDevice(int type, String tableNum, String mac, String ip,
			int elec_type, String name, String version , String contact) {
		this.type = type;
		this.tableNum = tableNum;
		this.mac = mac;
		this.ip = ip;
		this.elec_type = elec_type;
		this.name = name;
		this.version = version;
		this.contact = contact;
	}

	
	
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTableNum() {
		return tableNum;
	}

	public void setTableNum(String tableNum) {
		this.tableNum = tableNum;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getElec_type() {
		return elec_type;
	}
	
	public void setElec_type(int elec_type) {
		this.elec_type = elec_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/*
	 * huzenan
	 */
	// 生成设备信息的字符串序列
	// 每个数据之间用 分号; 隔开，结尾用多一个分号; 做标记
	public String createDeviceInformation() {
		String rtn = null;

		rtn = String.valueOf(this.type) + ";" + this.tableNum + ";" + this.mac
				+ ";" + this.ip + ";" + String.valueOf(this.elec_type) + ";"
				+ this.name + ";" + this.version + ";" + this.contact+";";

		return rtn;
	}

	// 解析设备信息的字符串序列
	public static List<MyDevice> getDevicesWithString(String info) {
		List<MyDevice> devices = new ArrayList<MyDevice>();
		try{
		if(!info.equals(null)){
			String [] datalist = info.split(";");
			for (int i = 0; i < datalist.length/TOTAL_ARTTIBUTES; i++) {
				// 添加一个设备到 devices 中
				MyDevice md = new MyDevice();
				md.type = Integer.parseInt(datalist[i*8]);
				md.tableNum = datalist[i*8+1];
				md.mac = datalist[i*8+2];
				md.ip = datalist[i*8+3];
				md.elec_type = Integer.parseInt(datalist[i*8+4]);
				md.name = datalist[i*8+5];
				md.version = datalist[i*8+6];
				md.contact = datalist[i*8+7];
				devices.add(md);
			}

		}
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		return devices;
	}
	/*
	 * end huzenan
	 */
}
