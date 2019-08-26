package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.order.Type_Entity;

/**zigbee插座信息界面*/
public class TV_Fragment3_1 extends BaseActivity {
	private TextView txt_name,txt_type,txt_mac,txt_resule;

	private String str_mac,str_type,str_name;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tv_f3_1);
		try{
			txt_name = (TextView) findViewById(R.id.txt_name);
			txt_type = (TextView) findViewById(R.id.txt_type);
			txt_mac = (TextView) findViewById(R.id.txt_mac);
			txt_resule = (TextView) findViewById(R.id.txt_resule);

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
			str_type = intent.getStringExtra("type");
			str_name = intent.getStringExtra("name");
			
			
			txt_name.setText(str_name);
			txt_type.setText(getTypeName(str_type));
			txt_mac.setText(str_mac);
	
			SharedPreferences sp=getSharedPreferences("status", 0);
			String resu=sp.getString(str_mac,"100");
			if(resu.equals("200")){
				txt_resule.setText("执行成功");
			}if(resu.equals("300")){
				txt_resule.setText("执行失败");
			}if(resu.equals("250")){
				txt_resule.setText("执行中...");
			}if(resu.equals("400")){
				txt_resule.setText("执行超时");
			}if(resu.equals("350")){
				txt_resule.setText("服务系统拒绝执行指令");
			}if(resu.equals("100")){
				txt_resule.setText("连接超时");
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
	
	private String getTypeName(String type){
		String typename = null;
		switch(type){
		case Type_Entity.Gateway_type:
			typename="以太网智能网关";
			break;
		case Type_Entity.Socket_type:
			typename="Zigbee智能插座";
			break;
		case Type_Entity.Wifi_gateway:
			typename="无线智能网关";
			break;
		case Type_Entity.Split_air_conditioning:
			typename="Zigbee分体空调控制器";
			break;
		case Type_Entity.Central_air_conditioning:
			typename="Zigbee中央空调控制器";
			break;
		case Type_Entity.Four_street_control:
			typename="Zigbee灯控面板";
			break;
		}
		return typename;
	}
}
