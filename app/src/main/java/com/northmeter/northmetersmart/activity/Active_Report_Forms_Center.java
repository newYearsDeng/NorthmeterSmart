package com.northmeter.northmetersmart.activity;

import java.util.ArrayList;
import java.util.List;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *中央空调用电报表
 * */
public class Active_Report_Forms_Center extends BaseActivity implements OnClickListener{
	private ReceiveTool receiver;
	private String str_mac,str_type,title;
	/**月使用时长*/
	private TextView textview_sc;
	/**通宵使用时长*/
	private TextView textview_txsysc;
	/**月使用率*/
	private TextView textview_ysyl;
	
	/**工作日总使用时长*/
	private TextView textview_gzrsc;
	/**工作日工作时段使用时长*/
	private TextView textview_gzrgzsdsc;
	/**工作日非工作时段使用时长*/
	private TextView textview_gzrfgzsdsc;
	/**非工作日总使用时长*/
	private TextView textview_fgzrsc;
	
	/**在线率*/
	private TextView textview_online;
	/**通讯成功率*/
	private TextView textview_txcgl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.active_report_forms_center);
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_type = intent.getStringExtra("type");
			title = intent.getStringExtra("title");
			init_view();
			//注册接收广播
			//RegisterBroad();
			
			TextView textview_title = (TextView) findViewById(R.id.textview_title);
			textview_title.setText(title.substring(title.length()-2,title.length())+"月报表数据");
			
			findViewById(R.id.but_back_1).setOnClickListener(this);//返回
			findViewById(R.id.detailed).setOnClickListener(this);//用电明细
			
			get_report_of_month();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_view(){
		textview_sc = (TextView) findViewById(R.id.textview_sc);
		textview_txsysc = (TextView) findViewById(R.id.textview_txsysc);
		textview_ysyl = (TextView) findViewById(R.id.textview_ysyl);
		
		textview_gzrsc = (TextView) findViewById(R.id.textview_gzrsc);
		textview_gzrgzsdsc = (TextView) findViewById(R.id.textview_gzrgzsdsc);
		textview_gzrfgzsdsc = (TextView) findViewById(R.id.textview_gzrfgzsdsc);
		textview_fgzrsc = (TextView) findViewById(R.id.textview_fgzrsc);
		
		textview_online = (TextView) findViewById(R.id.textview_online);
		textview_txcgl = (TextView) findViewById(R.id.textview_txcgl);
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
			case R.id.detailed:
				Intent intent = new Intent(this,Active_Report_Detailed.class);
				intent.putExtra("mac", str_mac);
				intent.putExtra("type", str_type);
				intent.putExtra("title", title);
				startActivity(intent);
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
					switch(bundle_msg.split("/")[2]){//selected/(没发送过来)   表类型/表号/get_report_of_month/data
					case "get_report_of_month"://具体月报表数据
						int flag = bundle_msg.indexOf("get_report_of_month/")+"get_report_of_month/".length();
						String report= bundle_msg.substring(flag,bundle_msg.length());
						
						if(!report.equals("null")){					
							String [] report_list = report.split(" ");
							textview_sc.setText(report_list[1]+" h");
							textview_txsysc.setText(report_list[2]+" h");
							textview_ysyl.setText(report_list[3]);
							
							textview_gzrsc.setText(report_list[4]+" h"); 
							textview_gzrgzsdsc.setText(report_list[5]+" h"); 
							textview_gzrfgzsdsc.setText(report_list[6]+" h");
							textview_fgzrsc.setText(report_list[7]+" h"); 
							
							textview_online.setText(report_list[8]); 
							textview_txcgl.setText(report_list[9]); 
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
	
	/**realtimedata/表类型/表号/get_report_of_month/月份
	 * get_report_of_month("0a0001aa7k", "150721023750", "2016-10")*/
	/**获取月报表月份数据*/
	private void get_report_of_month(){
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Active_Report_Forms_Center.this,handler,
								str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_report_of_month/"+title);	
					} catch (MqttException e) {
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
				switch(intent.getStringExtra("msg").split("/")[2]){//selected/(没发送过来)   realtimedata/表类型/表号/get_report_of_month/data
				case "get_report_of_month"://具体月报表数据
					int flag = intent.getStringExtra("msg").indexOf("get_report_of_month/")+"get_report_of_month/".length();
					String receive= intent.getStringExtra("msg").substring(flag,intent.getStringExtra("msg").length());
					String [] dnData=new String[]{"get_report_of_month",receive};
					data.putStringArray("REPORT_LIST", dnData);
					msg.setData(data);	
					Active_Report_Forms_Center.this.handler.sendMessage(msg);
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
