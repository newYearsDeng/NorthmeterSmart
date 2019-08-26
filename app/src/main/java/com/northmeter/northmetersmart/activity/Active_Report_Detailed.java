package com.northmeter.northmetersmart.activity;

import java.util.ArrayList;
import java.util.List;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.R.color;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 月报表——一个月内每天详细用电
 * */
public class Active_Report_Detailed extends BaseActivity implements OnClickListener{
	private ReceiveTool receiver;
	private String str_mac,str_type,title;
	private TextView title_name;//使用项的标题
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.active_report_detailed);
			
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_type = intent.getStringExtra("type");
			title = intent.getStringExtra("title");
			
			title_name = (TextView) findViewById(R.id.title_name);
			switch(str_type){
			case Type_Entity.Split_air_conditioning:
				title_name.setText("用电量");
				break;
			case Type_Entity.Central_air_conditioning:
				title_name.setText("使用时长");
				break;
			}
			
			findViewById(R.id.but_back_1).setOnClickListener(this);//返回		
			//注册接收广播
			//RegisterBroad();
			get_meter_used_ele_list_of_day();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_view(){
		
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//RegisterBroad();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.but_back_1:
				finish();
				break;
				
				default:
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			
			if (msg.what == 1) {
				String bundle_msg = msg.getData().getString("content");
				if(bundle_msg.split("/")[1].equals(str_mac)){
					
					LinearLayout linear_textout = (LinearLayout) findViewById(R.id.linear_detailed);
					linear_textout.setOrientation(LinearLayout.VERTICAL);
					linear_textout.removeAllViews();
					//linear_textout.setBackgroundColor(R.drawable.active_back_text);		
					switch(bundle_msg.split("/")[2]){//selected/ 表类型/表号/get_meter_used_ele_list_of_day/data
					case "get_meter_used_ele_list_of_day":
						if(!bundle_msg.split("/")[3].equals("null")){
							String [] detaildeList = bundle_msg.split("/")[3].split("\n");
							for(int i=0;i<detaildeList.length;i++){
								LinearLayout linear_detailed_child = new LinearLayout(Active_Report_Detailed.this);
								linear_detailed_child.setOrientation(LinearLayout.HORIZONTAL);
								linear_detailed_child.setBackgroundResource(R.drawable.active_back);
								LayoutParams llp = new LayoutParams(LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f);
								llp.setMargins(0, 1, 0, 1);
								linear_detailed_child.setLayoutParams(llp);
								//设置权重
							    LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
								
								TextView text_1 = new TextView(Active_Report_Detailed.this);
								text_1.setLayoutParams(lp);
								text_1.setText(detaildeList[i].split(" ")[0]);
								text_1.setTextSize(15);  
								text_1.setTextColor(Color.WHITE);
								text_1.setPadding(30, 30, 10, 30);
								linear_detailed_child.addView(text_1);
								
								TextView text_2 = new TextView(Active_Report_Detailed.this);
								text_2.setLayoutParams(lp);
								text_2.setText(detaildeList[i].split(" ")[1]+" kWh");
								text_2.setTextSize(15);                               
								text_2.setTextColor(Color.WHITE);
								text_2.setGravity(Gravity.CENTER);
								linear_detailed_child.addView(text_2);
								
								linear_textout.addView(linear_detailed_child);
							}
						}
						break;
					case "get_meter_used_time_list_of_day":
						if(!bundle_msg.split("/")[3].equals("null")){
							String [] detaildeList = bundle_msg.split("/")[3].split("\n");
							for(int i=0;i<detaildeList.length;i++){
								LinearLayout linear_detailed_child = new LinearLayout(Active_Report_Detailed.this);
								linear_detailed_child.setOrientation(LinearLayout.HORIZONTAL);
								linear_detailed_child.setBackgroundResource(R.drawable.active_back);
								LayoutParams llp = new LayoutParams(LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f);
								llp.setMargins(0, 1, 0, 1);
								linear_detailed_child.setLayoutParams(llp);
								//设置权重
							    LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
								
								TextView text_1 = new TextView(Active_Report_Detailed.this);
								text_1.setLayoutParams(lp);
								text_1.setText(detaildeList[i].split(" ")[0]);
								text_1.setTextSize(15);  
								text_1.setTextColor(Color.WHITE);
								text_1.setPadding(30, 30, 10, 30);
								linear_detailed_child.addView(text_1);
								
								TextView text_2 = new TextView(Active_Report_Detailed.this);
								text_2.setLayoutParams(lp);
								text_2.setText(detaildeList[i].split(" ")[1]+" h");
								text_2.setTextSize(15);                               
								text_2.setTextColor(Color.WHITE);
								text_2.setGravity(Gravity.CENTER);
								linear_detailed_child.addView(text_2);
								
								linear_textout.addView(linear_detailed_child);
							}
						}
						break;
					}
				
				}
			}
			
			

			
		}catch(Exception e){
			e.printStackTrace();
			}
		}
	};
	
	/** realtimedata/表类型/表号/get_meter_used_ele_list_of_day/月份
		get_meter_used_ele_list_of_day("0a0001aa7k", "150721023750", "2016-10")

		get_meter_used_time_list_of_day("0a0001a4r5", "150907000125", "2016-10")
		realtimedata/表类型/表号/get_meter_used_time_list_of_day/月份
	 */

	private void get_meter_used_ele_list_of_day(){
		new Thread(){
			public void run(){
				try {					
					switch(str_type){
					case Type_Entity.Split_air_conditioning://分体空调电量历史数据
						System.out.println("str_type:;"+Type_Entity.Split_air_conditioning);
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Active_Report_Detailed.this,handler,
								str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_meter_used_ele_list_of_day/"+title);
						break;
					case Type_Entity.Central_air_conditioning://中央空调历史使用时长数据
						System.out.println("str_type:;"+Type_Entity.Central_air_conditioning);
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Active_Report_Detailed.this,handler,
								str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_meter_used_time_list_of_day/"+title);
						break;
						default:
							System.out.println("str_type:ss;"+Type_Entity.Split_air_conditioning);
							PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Active_Report_Detailed.this,handler,
									str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_meter_used_ele_list_of_day/"+title);
							break;
					}
	
					} catch (MqttException e)
						{
				// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}.start();
	
	}
	
	
	
	/**注册接收广播*/
	public void RegisterBroad(){
		//界面更新广播接收;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter("Intent.SELECT_REPORT");
				registerReceiver(receiver, intentfilter);
			}
		}.start();	
	}
	
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("************************收到广播,这是活动流历史界面"+intent.getAction());
			Message msg=new Message();
			Bundle data=new Bundle();
			if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
				String receive= intent.getStringExtra("msg").split("/")[3];
				switch(intent.getStringExtra("msg").split("/")[2]){//selected/ 表类型/表号/get_meter_used_ele_list_of_day/data
				case "get_meter_used_ele_list_of_day"://分体空调用电量历史记录
					String [] dnData=new String[]{"get_meter_used_ele_list_of_day",receive};
					data.putStringArray("REPORT_LIST", dnData);
					msg.setData(data);	
					Active_Report_Detailed.this.handler.sendMessage(msg);
					break;
				case "get_meter_used_time_list_of_day"://中央空调使用时长历史记录
					String [] dnData_1=new String[]{"get_meter_used_time_list_of_day",receive};
					data.putStringArray("REPORT_LIST", dnData_1);
					msg.setData(data);	
					Active_Report_Detailed.this.handler.sendMessage(msg);
					break;
				}
			}
	
			
		}
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
		//unregisterReceiver(receiver);
		}catch(Exception e){
			e.printStackTrace();
			}
	}



	
}
