package com.northmeter.northmetersmart.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;

/**网络地址设置*/
public class UrlSettinAty extends BaseActivity implements OnClickListener{
	
	private EditText edit_http,edit_mqtt,edit_usreUrl,edit_tcp;
	private Button button_set;
	private SharedPreferences sp;

	private String network_address = "218.17.157.121:8821";
	private String mqtt_address = "218.17.157.121:1883";
	private String user_address = "218.17.157.121:1531";
	private String tcp_address = "218.17.157.121:8822";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.url_setting_aty);
		init_view();

	}
	
	private void init_view(){
		edit_http = (EditText) findViewById(R.id.edit_http);
		edit_mqtt = (EditText) findViewById(R.id.edit_mqtt);
		edit_usreUrl = (EditText) findViewById(R.id.edit_usreUrl);
		edit_tcp = (EditText) findViewById(R.id.edit_tcp);
		
		sp = getSharedPreferences("URL_ADD", MODE_PRIVATE);
		edit_http.setText(sp.getString("URL_ADD", network_address));
		edit_mqtt.setText(sp.getString("MQTT_ADD", mqtt_address));
		edit_usreUrl.setText(sp.getString("USER_ADD", user_address));
		edit_tcp.setText(sp.getString("TCP_ADD", tcp_address));
		
		findViewById(R.id.button_back).setOnClickListener(this);
		findViewById(R.id.button_set).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.button_back:
				finish();
				break;
			case R.id.button_set:
				String msg = edit_http.getText().toString();
				Editor edit = sp.edit();
				if(msg.equals("")){
					edit.putString("URL_ADD",network_address);
					URL_help.getInstance().setUrl_address("http://"+network_address);
				}else{
					edit.putString("URL_ADD", msg);	
					URL_help.getInstance().setUrl_address("http://"+msg);
				}	
				
				String mqtt_msg = edit_mqtt.getText().toString();
				if(mqtt_msg.equals("")){
					edit.putString("MQTT_ADD",mqtt_address);
					URL_help.getInstance().setEmqtt_address(mqtt_address);
				}else{
					edit.putString("MQTT_ADD", mqtt_msg);
					URL_help.getInstance().setEmqtt_address(mqtt_msg);
				}	
				
				String user_msg = edit_usreUrl.getText().toString();
				if(user_msg.equals("")){
					edit.putString("USER_ADD",user_address);
					URL_help.getInstance().setUser_address("http://"+user_address);
				}else{
					edit.putString("USER_ADD", user_msg);
					URL_help.getInstance().setUser_address("http://"+user_msg);
				}	
				
				String tcp_msg = edit_tcp.getText().toString();
				if(user_msg.equals("")){
					edit.putString("TCP_ADD",tcp_address);
					URL_help.getInstance().setTcp_address(tcp_address);
				}else{
					edit.putString("TCP_ADD", tcp_msg);
					URL_help.getInstance().setTcp_address(tcp_msg);
				}	
				
				edit.commit();
				finish();
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
}
