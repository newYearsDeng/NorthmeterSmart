package com.northmeter.northmetersmart.control;

import java.util.ArrayList;
import java.util.List;


import android.R.integer;

import com.northmeter.northmetersmart.model.UDP_model;

/**广播帮助类*/
public class UDP_help {
	public static final String SERVERIP = "255.255.255.255"; // 广播地址  
    public static final int SERVERPORT = 1093; // 端口号  
    
	public static List<UDP_model> getudp_text(String receice){
		List<UDP_model> models = new ArrayList<UDP_model>();
		try{
		for(int i=0;i<receice.length()/182;i++){
			UDP_model model = new UDP_model();
			model.setIscheck("false");
			model.setHead(receice.substring(i*182, i*182+16));
			model.setTerminal_add(receice.substring(i*182+16, i*182+26));//终端地址
			model.setMaster_IP_port(receice.substring(i*182+26, i*182+72));//主站IP地址和端口
		    model.setTerminal_IP_mask_gateway(receice.substring(i*182+72, i*182+98));//终端IP地址、掩码、网关
		    model.setTerminal_time(receice.substring(i*182+98, i*182+112));//終端时间
		    model.setPassmode_onoff(receice.substring(i*182+112, i*182+124));;//透传模式开关
		    model.setDevice_name(receice.substring(i*182+124, i*182+146));//集中器设备名称
		    model.setVersion(receice.substring(i*182+146, i*182+170));//软件版本，版本时间，硬件版本 
		    model.setPoint_capacity(receice.substring(i*182+170, i*182+174));//測量點容量
		    model.setTCP_connection_state(receice.substring(i*182+174, i*182+182));
		    models.add(model);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return models;
	} 
	public static String Ascii_To_String(String ascii){//ASCII转换为字符串
		  StringBuffer s=new StringBuffer();
		  String[] chars = new String[ascii.length()/2];
		  for(int j=0;j<ascii.length()/2;j++){
			  chars[j]=ascii.substring(j*2,j*2+2);
		  }
		  for(int i=0;i<chars.length;i++){
      	
			  System.out.println(chars[i]+" "+(char)Integer.parseInt(Integer.valueOf(chars[i],16).toString()));
			  s=s.append(String.valueOf((char)Integer.parseInt(Integer.valueOf(chars[i],16).toString())));
         
		  }
		  return s.toString();
		 }
	
	 public static String String_To_Ascii(String str){//字符串转换为ASCII码
		  StringBuffer s=new StringBuffer();
		  char[] chars=str.toCharArray(); //把字符中转换为字符数组
	
		  for(int i=0;i<chars.length;i++){//输出结果
	
		         System.out.println(" "+chars[i]+" "+Integer.toHexString((int)chars[i]));
		         s.append(Integer.toHexString((int)chars[i]));
		        }
		  return s.toString();
	 }
	 
	 public static String get_inter_add(String add){//16进制转10进制,接收到的报文显示是处理
		 StringBuffer sb=new StringBuffer();
		 String[] chars=new String[add.length()/2];
		 for(int i=0;i<add.length()/2;i++){
			 chars[i]=add.substring(i*2,i*2+2);
			 sb.append(Integer.valueOf(chars[i],16)+".");
		 }
		 return sb.toString().substring(0,sb.toString().length()-1);
	 }

	 public static String to_HexString(String add){//10进制转16进制,发送修改的报文时处理
		 StringBuffer sb=new StringBuffer();
		 add=add.replace(".", " ");
		 String[] chars=add.split(" ");
		 for(int i=0;i<chars.length;i++){
			 String hex=Integer.toHexString(Integer.parseInt(chars[i]));
			 if(hex.length()<2){
				 hex="0"+hex;
			 }
			 sb.append(hex);
		 }
		 return sb.toString();
	 }
	 public static String get_inter_port(String add){//16进制转10进制,转端口，接收时
		 StringBuffer sb=new StringBuffer();
		 for(int i=add.length()/2;i>0;i--){
			 sb.append(add.substring(i*2-2,i*2));
		 }
		 int data=Integer.valueOf(sb.toString(),16);
		 return data+"";
	 }
	 public static String to_HexString_port(String add){//10进制转16进制,转端口，发送时
		 StringBuffer sb=new StringBuffer();
		 String hstr=Integer.toHexString(Integer.parseInt(add));
		 for(int i=hstr.length()/2;i>0;i--){
			 sb.append(hstr.substring(i*2-2,i*2));
		 }
		 String str="0000";
		 String send=sb.toString();
		 if(send.length()<4){
			 send=str.substring(send.length(),str.length())+send;
		 }
		 return send;
	 }
	 
	 /*16进制字符串转换成byte数组进行广播*/
	 public static byte[] strtoByteArray(String hexString) {
	    hexString = hexString.toLowerCase();
	    final byte[] byteArray = new byte[hexString.length() / 2];
	    int k = 0;
	    for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
	    	byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
	    	byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
	    	byteArray[i] = (byte) (high << 4 | low);
	    	k += 2;
	    	 }
	    return byteArray;    
	 }
	 
	 public static String get_sum(String num){/*获取校验码。总加和*/
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
