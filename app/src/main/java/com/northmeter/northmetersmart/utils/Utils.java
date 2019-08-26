package com.northmeter.northmetersmart.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.content.Context;
import android.net.ParseException;
import android.text.format.Time;

import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.RandomGUID;
import com.northmeter.northmetersmart.order.Type_Entity;

/**
 * 工具类
 * */
public class Utils {
	private static String SUPER_SIGN = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

	/**使用自定义的符号拼接字符串*/
    public static String join(String join,String[] strAry){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<strAry.length;i++){
             if(i==(strAry.length-1)){
                 sb.append(strAry[i]);
             }else{
                 sb.append(strAry[i]).append(join);
             }
        }
       
        return new String(sb);
    }
    
	/**
	 * 根据表号检查设备的类型*/
	public static String getDeviceType(Context context,String mac){
		String type = null;
		try{
			DBDevice db = new DBDevice(context);
			MyDevice mydevice = db.Query(mac);
			switch(mydevice.getType()){
			case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET:
				type = Type_Entity.Socket_type;
				break;
			case MyDevice.DEVICE_ZIGBEE_SMART_AIR:
				type = Type_Entity.Split_air_conditioning;
				break;
			case MyDevice.DEVICE_CENTER_SMART_AIR:
				type = Type_Entity.Central_air_conditioning;
				break;
			case MyDevice.DEVICE_ZIGBEE_SOCKET_FOUR:
				type = Type_Entity.Four_street_control;
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return type;
	}
	
	
	 /**
     * 将时间转换为时间戳
     */    
    public static String dateToStamp(String s) throws ParseException{
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }
	
    /** 
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    
    /**
     * 将抄表数据进行组合
     * */
    public static String sendOrder(MyOrder od) {
		// 指令流水号
		// 有一个问题，这里使用的是用户手机的时间
		Time t = new Time();
		t.setToNow();
		String time = String.valueOf(t.year).substring(2, 4)
				+ toDoubleDate(t.month + 1)
				+ toDoubleDate(t.monthDay) + toDoubleDate(t.hour)
				+ toDoubleDate(t.minute); // month是从0开始计算的
		String order_no = time + new RandomGUID();
		System.out.println("#######################order_no = " + order_no);

		// objs
		String objs = "[{\"eqpt_type\":\"" + od.getEqpt_type()
				+ "\",\"eqpt_id_code\":\"" + od.getEqpt_id_code()
				+ "\",\"eqpt_pwd\":\"" + od.getEqpt_pwd()
				+ "\",\"cmd_type\":\"" + od.getCmd_type() + "\",\"cmd_id\":\""
				+ od.getCmd_id() + "\",\"cmd_data\":\"" + od.getCmd_data()
				+ "\"}]";
		System.out.println("#######################objs = " + objs);

		// sign
		// Create_direct_opt_by_user create = new Create_direct_opt_by_user();
		// String sign = create.get_sign(objs, pswd);
		String sign = SUPER_SIGN;
		System.out.println("#######################sign = " + sign);

		// order string
		String orderStr = "order_no=" + order_no + "&partner="
				+ od.getPartner() + "&objs=" + objs + "&sign=" + sign;
		System.out.println("#######################orderStr = " + orderStr);

//		String result = null;
//		result = HttpUtils.submitPostData(orderStr, url_path, encode);

		return orderStr;
	}
    
    /**
	 * 将单个字符的日期（时间） 转换成两个字符 如：1 -> 01
	 */
	private static String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
	
	
	 /**
	  * byte数组转换为字符串*/
	 public static String bytesToHexString(byte[] src){       
        StringBuilder stringBuilder = new StringBuilder();       
        if (src == null || src.length <= 0) {       
            return null;       
        }       
        for (int i = 0; i < src.length; i++) {       
            int v = src[i] & 0xFF;       
            String hv = Integer.toHexString(v);       
            if (hv.length() < 2) {       
                stringBuilder.append(0);       
            }       
            stringBuilder.append(hv);       
        }       
        return stringBuilder.toString();       
    }
	 
	 /**ASCII转换为字符串*/
	 public static String Ascii_To_String(String ascii){//ASCII转换为字符串
		 StringBuffer s=new StringBuffer();
		 String[] chars = new String[ascii.length()/2];
		 for(int j=0;j<ascii.length()/2;j++){
			 chars[j]=ascii.substring(j*2,j*2+2);
		 	}
		 for(int i=0;i<chars.length;i++){

			 //System.out.println(chars[i]+" "+(char)Integer.parseInt(Integer.valueOf(chars[i],16).toString()));
			 s=s.append(String.valueOf((char)Integer.parseInt(Integer.valueOf(chars[i],16).toString())));
  
		 }
		 return s.toString();
	 	}
}
