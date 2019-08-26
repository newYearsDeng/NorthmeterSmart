package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;


import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.db.DBDeviceData;
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceData;
import com.northmeter.northmetersmart.device.MyDiagramData;

/**
 * 中央空调信息页面*/
public class Kt_Frag_Center_2 extends Activity {
	private TextView tv_status;
	private TextView tv_mac;
	private TextView tv_tableName;
	private TextView tv_time;
	private TextView tv_result;
	private TextView tv_version;
	private TextView txt_type;
	private String sx_Time,times;
	private String str_mac;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.tv_f3_1);
			tv_status = (TextView) findViewById(R.id.txt_status);
			tv_mac = (TextView) findViewById(R.id.txt_mac);
			tv_tableName = (TextView) findViewById(R.id.txt_tabname);
			tv_time = (TextView) findViewById(R.id.txt_time);
			tv_result = (TextView) findViewById(R.id.txt_resule);
			tv_version = (TextView) findViewById(R.id.txt_version);
			
			findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			
			// 获取该页面mac值
			Intent intent = getIntent();
		    str_mac = intent.getStringExtra("mac");

			SharedPreferences sp = getSharedPreferences("status", 0);
			String resu=sp.getString(str_mac,"100");
			if(resu.equals("200")){
				tv_result.setText("执行成功");
			}if(resu.equals("300")){
				tv_result.setText("执行失败");
			}if(resu.equals("250")){
				tv_result.setText("执行中...");
			}if(resu.equals("400")){
				tv_result.setText("执行超时");
			}if(resu.equals("350")){
				tv_result.setText("服务系统拒绝执行指令");
			}if(resu.equals("100")){
				tv_result.setText("连接超时");
			}
			
			txt_type = (TextView) findViewById(R.id.txt_type);
			txt_type.setText(getTypeName(str_mac));
			// 根据mac获取相应的信息

			// ========================================================
			DBDeviceData db_dd = new DBDeviceData(Kt_Frag_Center_2.this);
			// MyDeviceData mdd = db_dd.Query(str_mac);
			// =========================================================
			MyDeviceData mdd = null;
			List<MyDeviceData> devicesData = db_dd.GetMyDeviceData();
			for (int i = 0; i < devicesData.size(); i++) {
				if (!devicesData.isEmpty()) {
					mdd = devicesData.get(i);
					if (mdd.getMac().equals(str_mac))
						break;
				}
			}
			// =========================================================
			if (mdd != null) {
				//tv_time.setText(mdd.getTimeNow());
				sx_Time=mdd.getTimeNow();
				if (mdd.getStateLastTime().equals("on")){
					tv_status.setText("已连接");
					}
				else if (mdd.getStateLastTime().equals("off")){
					tv_status.setText("未连接");
					}
			} else{
				tv_status.setText("未读取到数据");
				}

			tv_mac.setText(str_mac);

			DBDevice db_d = new DBDevice(Kt_Frag_Center_2.this);
			List<MyDevice> devices = db_d.GetMyDevices();
			MyDevice md = null;
			for (int i = 0; i < devices.size(); i++) {
				md = devices.get(i);
				if (md.getMac().equals(str_mac)) {
					break;
				}
			}
			tv_tableName.setText(md.getTableNum());
			tv_version.setText(md.getVersion());
			//tv_time.setText(mdd.getTimeNow());
				
			// ========================================================
			//获取MyDiagramData里面的时间跟MyDeviceData的时间比较
			try{
				List<MyDiagramData> diagr=new ArrayList<MyDiagramData>();
				DBDiagramData dbdiagr=new DBDiagramData(Kt_Frag_Center_2.this);
				MyDiagramData myd=new MyDiagramData();
				diagr=dbdiagr.GetMyDiagramDataByMac(str_mac);
				if(diagr!=null){
				for(int i=(diagr.size()-1);i<diagr.size();i++){
					myd=diagr.get(i);
				}
				times=myd.getTimeNow();
				System.out.println("times*********"+times+" sx_Time"+sx_Time);
				String NEW_time=getTime(times,sx_Time);
				tv_time.setText(NEW_time);
				}
			}catch(Exception e){
				tv_time.setText(sx_Time);
				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*比较两个时间哪一个是最新的*/
	private String getTime(String time1_1,String time2_1){
		String time1=time1_1;
		String time2=time2_1;
		time1=time1.replaceAll("-", " ");
		time1=time1.replaceAll(":", " ");
		time1=time1.replaceAll(" ", "");
		System.out.println("---"+time1);
		
		time2=time2.replace("年", "");
		time2=time2.replace("月", "");
		time2=time2.replace("日", "");
		time2=time2.replace("时", "");
		time2=time2.replace("分", "");
		time2=time2.replace("秒", "");
		time2=time2.replace(" ", "");
		System.out.println("---"+time2);
		
		if((Long.parseLong(time1)-Long.parseLong(time2))>0){
			time1_1=time1_1.replaceFirst("-", "年");
			time1_1=time1_1.replaceFirst("-", "月");
			time1_1=time1_1.replaceFirst(" ", "日");
			time1_1=time1_1.replaceFirst(":", "时");
			time1_1=time1_1.replaceFirst(":", "分");
			time1_1=time1_1.replaceFirst(" ", "秒");
			time1_1=time1_1.replaceAll(" ", "");
			return time1_1;
		}
		if((Long.parseLong(time1)-Long.parseLong(time2))<0){
			return time2_1;
		}else{
			return time2_1;
		}
	}
	
	private String getTypeName(String mac){
		int type;
		String typename = null;
		DBDevice db_d = new DBDevice(Kt_Frag_Center_2.this);
		MyDevice mdevice = db_d.Query(mac);
		type = mdevice.getType();
		switch(mdevice.getType()){
		case MyDevice.DEVICE_WIFI_SMART_SOCKET:
			typename="wifi智能插座8001";
			break;
		case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET:
			typename="zigbee智能插座";
			break;
		case MyDevice.DEVICE_WIFI_SMART_GATEWAY:
			typename="无线智能网关";
			break;
		case MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY:
			typename="以太网智能网关";
			break;
		case MyDevice.DEVICE_ZIGBEE_SMART_AIR:
			typename="zigbee空调控制器";
			break;
		case MyDevice.DEVICE_CENTER_SMART_AIR:
			typename="zigbee中央空调控制器";
			break;
		case MyDevice.DEVICE_WIFI_SMART_AIR:
			typename="wifi空调控制器";
			break;
		}
		return typename;
	}
}
