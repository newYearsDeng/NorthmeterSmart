package com.northmeter.northmetersmart.activity;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.northmetersmart.I.IRequestShow;
import com.northmeter.northmetersmart.I.RequestInterface;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Change_Message extends BaseActivity implements OnClickListener,IRequestShow {

	private TextView text_type,text_deviceid,text_energyid;
	private EditText edittext_name,edittext_buildid;
	private String str_type,str_name,str_mac,buildid,roleid;
	private CustomProgressDialog progressDialog;
	private RequestInterface requestInterface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.put_session);
		init_view();
	}
	
	private void init_view(){
		try{
			requestInterface = new RequestInterface(this);
			
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			buildid = intent.getStringExtra("buildid");
			roleid = intent.getStringExtra("roleid");
				
			text_type = (TextView)findViewById(R.id.text_type);//设备类型
			text_deviceid = (TextView)findViewById(R.id.text_deviceid);//设备id
			text_energyid = (TextView)findViewById(R.id.text_energyid);//能源类型
			
			edittext_name = (EditText)findViewById(R.id.edittext_name);//设备名字
			edittext_buildid = (EditText)findViewById(R.id.edittext_buildid);//建筑id
			
			Button button_change = (Button) findViewById(R.id.button_change);
			button_change.setOnClickListener(this);//确认修改
			
			findViewById(R.id.button_back).setOnClickListener(this);//返回
			
			if(!roleid.equals(Type_Entity.manager)){
				button_change.setVisibility(View.GONE);
			}
			
			switch(str_type){
			case Type_Entity.Gateway_type:
				text_type.setText("网关");
				break;
			case Type_Entity.Socket_type:
				text_type.setText("ZigBee插座");
				break;
			case Type_Entity.Four_street_control:
				text_type.setText("ZigBee灯控面板");
				break;
			case Type_Entity.Split_air_conditioning:
				text_type.setText("分体空调控制器");
				break;
			case Type_Entity.Central_air_conditioning:
				text_type.setText("中央空调控制器");
				break;
				default:
					text_type.setText(str_type);
					break;
			}
			
			text_deviceid.setText(str_mac);
			edittext_name.setText(str_name);
			edittext_buildid.setText(buildid);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.button_back:
				finish();
				break;
			case R.id.button_change:
				progressDialog = CustomProgressDialog.createDialog(this);
				progressDialog.show();
				changeRoomDevice();
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				progressDialog.dismiss();
				String bundle_msg = msg.getData().getString("buildMsg");
				if(bundle_msg.equals("exception")){
					Toast.makeText(Change_Message.this, "网络连接异常", Toast.LENGTH_LONG).show();
				}else{
					//{"returnCode":"300","returnMsg":"bad parameter type"}
					JSONObject obj = JSONObject.parseObject(bundle_msg);
					Object detail = obj.get("returnCode");
					if(detail.equals("200")){
						Toast.makeText(Change_Message.this, "修改成功", Toast.LENGTH_LONG).show();
						finish();
					}else{
						Object errmsg = obj.get("returnMsg");
						Toast.makeText(Change_Message.this, errmsg.toString(), Toast.LENGTH_LONG).show();					
						return;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	

	
	private void changeRoomDevice(){
		new Thread(){ 
			public void run(){
				try{
					String URL = URL_help.getInstance().getUrl_address();
					String jsonStr = getSendJson();
					String url_msg = null;
					if(str_type.equals(Type_Entity.Gateway_type)){
						url_msg = "updateCollector";
					}else{
						url_msg = "updateMeter";
					}
					requestInterface.getHttpRequestPost(URL+"/"+url_msg, jsonStr);//http://218.17.157.121:8821/url_msg	
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}.start();
	}
	
	/**组装json格式的请求数据*/
	/**deviceId：计量设备编号
	deviceName：计量表名称
	deviceLabel：能源标签（ac, socket等）
	buildingId：下挂所在的建筑编号
	deviceTab：设备其他标签属性如办公，宿舍设备
	baudrate：波特率
	commport：通讯端口号
	*/
	
	/**deviceId：采集器编号
	deviceName：采集器名称
	deviceType：采集器类别
	buildingId：下挂所在的建筑编号
	*/
	private String getSendJson(){
		String jsonStr = null;
		try{
			JSONObject jsonObj = new JSONObject();
			if(str_type.equals(Type_Entity.Gateway_type)){//采集器的添加信息
				jsonObj.put("deviceId",  str_mac);//采集器编号
				jsonObj.put("deviceName", edittext_name.getText().toString());//采集器名称
				jsonObj.put("deviceType", str_type);//采集器类别
				jsonObj.put("buildingId", edittext_buildid.getText().toString());//下挂所在的建筑编号
			}else{
				jsonObj.put("deviceId", str_mac);//计量设备编号  
				jsonObj.put("deviceName", edittext_name.getText().toString());//计量表名称
				jsonObj.put("deviceLaber","ac");//能源标签（ac, socket等）//deviceLabel
				jsonObj.put("buildingId", edittext_buildid.getText().toString());//下挂所在的建筑编号
				jsonObj.put("deviceTab", "null");//设备其他标签属性如办公，宿舍设备
				jsonObj.put("baudrate", "2400bps");//波特率
				jsonObj.put("commport", "1F");//通讯端口号
			}
			jsonStr = jsonObj.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonStr;
	}

	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		Message message = new Message();
	    Bundle bundle = new Bundle();
	    bundle.putString("buildMsg", msg);
	    message.setData(bundle);
	    Change_Message.this.handler.sendMessage(message);
	}
	
}
