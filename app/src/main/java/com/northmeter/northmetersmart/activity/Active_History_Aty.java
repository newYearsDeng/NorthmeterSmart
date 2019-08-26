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
import android.widget.LinearLayout.LayoutParams;

/**
 *活动流历史记录
 * */
public class Active_History_Aty extends BaseActivity implements OnClickListener{
	private ReceiveTool receiver;
	private String str_mac;
	private TextView hitory_text;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.active_history_aty);
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			
			hitory_text = (TextView) findViewById(R.id.hitory_text);
			findViewById(R.id.but_back_1).setOnClickListener(this);//返回
			//注册接收广播
			//RegisterBroad();
			
			get_Form_Data();
			
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	/**
	 *  selected/0a0001a820/160127001282/get_socket_workflow_history/2016-09-29/11:25:47 active#10:40:09 active#09:55:45 
	 *  active#08:36:50 starting up\n2016-09-28/18:03:29
	 *  power off#17:41:39 active#17:07:42 active#16:24:26 active#16:01:51 active#12:42:40 active#11:46:36 
	 *  active#11:35:38 active#10:39:21 active#10:27:41 active#10:15:59 active#09:43:11 active#09:32:44 
	 *  standby#08:37:08 starting up\n2016-09-27/18:10:52 power off#17:49:55 active#17:27:39 standby#17:16:42
	 *  active#16:54:22 standby#16:12:08 active#16:01:29 active#15:27:02 active#15:16:36 standby#14:22:33 active#13:40:08
	 *  active#12:33:57 standby#12:11:52 active#09:49:55 active#09:16:14 standby#08:32:45 starting up
	 */
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if (msg.what == 1) {
				String bundle_msg = msg.getData().getString("content");
				if(bundle_msg.split("/")[1].equals(str_mac)){//selected/(没发送过来)   0a0001aa7k/002014110119/get_socket_readtime_workflow/data
					switch(bundle_msg.split("/")[2]){//selected/(没发送过来)   0a0001aa7k/002014110119/get_socket_workflow_history/data
					case "get_socket_workflow_history"://活动流历史记
						int flag = bundle_msg.indexOf("get_socket_workflow_history/")+"get_socket_workflow_history/".length();
						String receive= bundle_msg.substring(flag,bundle_msg.length());

						LinearLayout linear_textout = (LinearLayout) findViewById(R.id.linear_textout);
						linear_textout.setOrientation(LinearLayout.VERTICAL);
						linear_textout.removeAllViews();
						Bundle data=msg.getData();
						
						String[] flow_day_list = receive.split("\n");//天与天之间的数据用\n隔开 

						for(int i=0;i<flow_day_list.length;i++){//2016-09-29/11:25:47 active#10:40:09 active#09:55:45
							String[] flow_list = flow_day_list[i].split("/");

							TextView text_1 = new TextView(Active_History_Aty.this);
							text_1.setText(flow_list[0]);
							text_1.setTextSize(17);                               
							text_1.setTextColor(Color.WHITE);
							text_1.setPadding(10, 20, 10, 10);
							linear_textout.addView(text_1);                        
																		
							
							
							//设置权重
						    LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
						    LayoutParams llp = new LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT, 1.0f);
							llp.setMargins(0, 1, 0, 1);
							
							String[] flow_msg = flow_list[1].split("#");
							for(int j=0;j<flow_msg.length;j++){
								LinearLayout linear_detailed_child = new LinearLayout(Active_History_Aty.this);
								linear_detailed_child.setOrientation(LinearLayout.HORIZONTAL);
								linear_detailed_child.setBackgroundResource(R.drawable.active_back);								
								linear_detailed_child.setLayoutParams(llp);
								
								TextView textview_1 = new TextView(Active_History_Aty.this);
								textview_1.setLayoutParams(lp);
								textview_1.setText(flow_msg[j].split(" ")[0]);
								textview_1.setTextSize(15);  
								textview_1.setTextColor(Color.WHITE);
								textview_1.setGravity(Gravity.CENTER);
								textview_1.setPadding(0, 20, 0, 20);
								linear_detailed_child.addView(textview_1);
								
								TextView textview_2 = new TextView(Active_History_Aty.this);
								textview_2.setLayoutParams(lp);
								if(flow_msg[j].split(" ").length==3){
									textview_2.setText(flow_msg[j].split(" ")[1]+" "+flow_msg[j].split(" ")[2]);
								}else{
									textview_2.setText(flow_msg[j].split(" ")[1]);
								}
								
								textview_2.setTextSize(15);                               
								textview_2.setTextColor(Color.WHITE);
								textview_2.setGravity(Gravity.CENTER);
								textview_2.setPadding(0, 20, 0, 20);
								linear_detailed_child.addView(textview_2);
								
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
	
	/**读取活动流数据
    发送：realtimedata/类型/表号/get_socket_workflow_history
    返回: get_socket_workflow_history/0a0001aa7k/002014110119/data
	 */
	private void get_Form_Data(){
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Active_History_Aty.this,handler,
							"0a0001a820/"+str_mac,"realtimedata/0a0001a820/"+str_mac+"/get_socket_workflow_history");	
					
//					PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a820/"+str_mac,"realtimedata/0a0001a820/"+str_mac+"/get_socket_workflow_history");
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
				IntentFilter intentfilter = new IntentFilter("Intent.SELETC");
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
			if(intent.getStringExtra("msg").split("/")[0].equals("0a0001a820")|intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
				switch(intent.getStringExtra("msg").split("/")[2]){//selected/(没发送过来)   0a0001aa7k/002014110119/get_socket_readtime_workflow/data
				case "get_socket_workflow_history"://活动流历史记
					int flag = intent.getStringExtra("msg").indexOf("get_socket_workflow_history/")+"get_socket_workflow_history/".length();
					String receive= intent.getStringExtra("msg").substring(flag,intent.getStringExtra("msg").length());
					String [] dnData=new String[]{"readtime_workflow",receive};
					data.putStringArray("READ", dnData);
					msg.setData(data);	
					Active_History_Aty.this.handler.sendMessage(msg);
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
