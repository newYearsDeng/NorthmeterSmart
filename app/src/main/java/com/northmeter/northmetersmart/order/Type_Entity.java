package com.northmeter.northmetersmart.order;


import com.northmeter.northmetersmart.R;

public class Type_Entity {
	/**分体空调*/
	public static final String Split_air_conditioning = "0a0001aa7k";
	
	/**中央空调*/
	public static final String Central_air_conditioning = "0a0001a4r5";
	
	/**zigbee插座*/
	public static final String Socket_type = "0a0001a820";
	
	/**四路灯控*/
	public static final String Four_street_control = "0a0001a830";
	
	/**zigbee以太网采集器*/
	public static final String Gateway_type = "0a0003ahup";
	
	/**wifi插座*/
	public static final String Wifi_socket = "0a0003a8nq";
	
	/**Wifi转Zigbee智能用电网关/无线智能网关*/
	public static final String Wifi_gateway = "0a0003ahu"; 
	
	/**照明灯控*/
	public static final String Light_control = "0a0001a840";
	
	/**触摸面板*/
	public static final String Touch_panel = "0a0001a850";
	
	/**空气质量检测仪*/
	public static final String Air_Quality_Qetector = "0a0001a880";
	
	
	
	/**建筑权限*/
	public static final String manager="manager";
	public static final String analyser="analyser";
	public static final String controller="controller";
	public static final String observer="observer";
	public static final String visitor="visitor";	

	/**微信登录时web后台服务地址*/
	public static final String URL_getUser = "http://10.168.1.211:4000/users/getUser";//登录  "flag=tel&tel=17876148387"
	public static final String URL_getUserP= "http://218.17.157.121:4003/users/get User";//公网登录
	public static final String URL_registerUser = "http://10.168.1.211:4000/users/registerUser";//用户绑定 "{\"tel\":\"18679932627\",\"unionid\":\"o0bzZv7RkOov7DN8xsicoaM7jdkQ\"}"
	
	
	public static int getResource(String type){
		int resource = 0;
		switch(type){
		case Type_Entity.Split_air_conditioning:
			resource = R.drawable.new_air;
			break;
		case Type_Entity.Central_air_conditioning:
			resource = R.drawable.new_center_air;
			break;
		case Type_Entity.Four_street_control:
			resource = R.drawable.new_siludengk;
			break;
		case Type_Entity.Socket_type:
			resource = R.drawable.new_chazuo;
			break;
		case Type_Entity.Gateway_type:
			resource = R.drawable.new_socket;
			break;
		case Type_Entity.Light_control:
			resource = R.drawable.new_diandeng;
			break;
		case Type_Entity.Touch_panel:
			resource = R.drawable.new_siludengk;
			break;
		case Type_Entity.Air_Quality_Qetector:
			resource = R.drawable.new_nenghaofenxi;
			break;
		}
		
		return resource;
		
	}
	
	public static String getTypeName(String type){
		String typename = null;
		switch(type){
		case Type_Entity.Gateway_type:
			typename="智能网关";
			break;
		case Type_Entity.Socket_type:
			typename="智能插座";
			break;
		case Type_Entity.Wifi_gateway:
			typename="无线智能网关";
			break;
		case Type_Entity.Split_air_conditioning:
			typename="分体空调";
			break;
		case Type_Entity.Central_air_conditioning:
			typename="中央空调";
			break;
		case Type_Entity.Four_street_control:
			typename="灯控面板";
			break;
		case Type_Entity.Light_control:
			typename="照明灯控";
			break;
		case Type_Entity.Touch_panel:
			typename="触摸面板";
			break;
		case Type_Entity.Air_Quality_Qetector:
			typename="空气检测仪";
			break;
			default:
				typename="未识别设备";
				break;
		}
		return typename;
	}
	
	
	/**表号反向*/
	public static String reverseRst(String rst){
		//String newRst=rst.substring(2, rst.length()-2);
		String lastrst = "";
		for(int i=rst.length()/2;i>0;i--){
			lastrst=lastrst+rst.substring(i*2-2, i*2);
			
		}
		return lastrst;
	}
	/**获取校验码。总加和*/
	public static String get_sum(String num){
		 int sum=0;
			for(int i=0;i<num.length()/2;i++){
				 sum=sum+Integer.valueOf(num.substring(i*2,i*2+2),16);
			}
			String check_str=Integer.toHexString(sum);
			if(check_str.length()<2){
				check_str="0"+check_str;
			}else{
				System.out.println("总加和："+check_str);
				check_str=check_str.substring(check_str.length()-2,check_str.length());
			}
		 return check_str;
	 }
}
