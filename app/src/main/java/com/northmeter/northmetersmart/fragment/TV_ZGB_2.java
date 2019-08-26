package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;
import android.R.integer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.order.Type_Entity;

public class TV_ZGB_2 extends Fragment {
	private View view;

	private TextView txt_name;
	private TextView txt_mac;
	private TextView txt_resule;
	private TextView txt_type;
	private String sx_Time,times;
	
	private String str_mac,str_name,str_type;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_zgb_2, container, false);

		txt_name = (TextView) view.findViewById(R.id.txt_name);
		txt_mac = (TextView) view.findViewById(R.id.txt_mac);
		txt_type = (TextView) view.findViewById(R.id.txt_type);
		txt_resule = (TextView) view.findViewById(R.id.txt_resule);
		
		// 获取该页面mac值
		Intent intent = getActivity().getIntent();
		str_mac  = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");

		SharedPreferences sp=getActivity().getSharedPreferences("status", 0);
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
		
		
		txt_name.setText(str_name);
		txt_type.setText(getTypeName(str_type));
		txt_mac.setText(str_mac);

		return view;
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
