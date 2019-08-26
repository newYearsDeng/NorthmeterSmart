package com.northmeter.northmetersmart.fragment;

import java.util.List;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 空调其他设置，如红外条数查询和初始化，空调控制器校时
 * */
public class Kt_Frag_Other extends Activity implements OnClickListener{
	
	private Button but_refash_time,but_correct_time,but_refash_hwmts,but_correct_hwmts,but_correct_rcb;
	private TextView txt_hwmts,txt_refash_time,txt_rcb;
	private String str_mac,str_name,str_type,URL_PATH;
	private Handler handler;
	private SharedPreferences spf;
	private String timeNow,rqxq;
	private ReceiveTool receiver;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.kt_frag_other);
			/**注册广播监听*/
			RegisterBroad();
			init_view();
			refash_time();//刷新一下时间
			handler  = new Handler(){
				public void handleMessage(Message msg){
					try{
					super.handleMessage(msg);
					Bundle data=msg.getData();
					String []airData=data.getStringArray("zigbeeAir");
					if(airData[0].equals("refash_Time")){
						txt_refash_time.setText(airData[1]);
						Toast.makeText(Kt_Frag_Other.this, "刷新完成！", Toast.LENGTH_SHORT).show();
					}else if(airData[0].equals("correct_Time")){
						Toast.makeText(Kt_Frag_Other.this, "校时完成！", Toast.LENGTH_SHORT).show();
					}else if(airData[0].equals("refash_Hwmts")){
						spf=getSharedPreferences("HWMTS", 0);
						Editor editor=spf.edit();
						editor.putString(str_mac, airData[1]);
						editor.commit();
						txt_hwmts.setText(airData[1]);
						Toast.makeText(Kt_Frag_Other.this, "刷新完成！", Toast.LENGTH_SHORT).show();
					}else if(airData[0].equals("correct_Hwmts")){
						txt_hwmts.setText(0);
						Toast.makeText(Kt_Frag_Other.this, "初始化完成！", Toast.LENGTH_SHORT).show();
					}else if(airData[0].equals("correct_Rcb")){
						delAllCycleTask(str_mac);
						txt_rcb.setText("初始化完成");
						Toast.makeText(Kt_Frag_Other.this, "日程表初始化完成！", Toast.LENGTH_SHORT).show();
					}
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**页面初始化*/
	private void init_view(){
		try{
		// 获取该页面mac值
		Intent intent = getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		System.out.println("mac**************"+str_mac);
		URL_PATH = URL_help.getInstance().getUrl_address();
		
		findViewById(R.id.button2).setOnClickListener(this);//返回
		
		but_refash_hwmts = (Button) findViewById(R.id.but_refash_hwmts);//刷新特定红外码条数
		but_refash_hwmts.setOnClickListener(this);
		but_correct_hwmts = (Button) findViewById(R.id.but_correct_hwmts);//初始化红外码条数
		but_correct_hwmts.setOnClickListener(this);
		but_refash_time = (Button) findViewById(R.id.but_refash_time);//刷新时间
		but_refash_time.setOnClickListener(this);
		but_correct_time = (Button) findViewById(R.id.but_correct_time);//校时
		but_correct_time.setOnClickListener(this);
		but_correct_rcb = (Button) findViewById(R.id.but_correct_rcb);//初始化日程表
		but_correct_rcb.setOnClickListener(this);
		
		txt_refash_time = (TextView) findViewById(R.id.txt_refash_time);//时间显示
		txt_hwmts = (TextView) findViewById(R.id.txt_hwmts);//红外码条数显示
		spf=getSharedPreferences("HWMTS", 0);
		txt_hwmts.setText(spf.getString(str_mac,"0"));
		
		txt_rcb = (TextView) findViewById(R.id.txt_rcb);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.but_refash_time://刷新时间
			refash_time();
			break;
		case R.id.but_correct_time://校时
			correct_time();
			break;
		case R.id.but_refash_hwmts://刷新红外编码条数
			refash_hwmts();
			break;
		case R.id.but_correct_hwmts://初始化红外编码条数
			correct_hwmts();
			break;
		case R.id.but_correct_rcb://日程表初始化
			correct_rcb();
			break;
		case R.id.button2:
			finish();
			break;
		}
		
	}
	/**
	 * 将单个字符的日期（时间） 转换成两个字符 如：1 -> 01
	 */
	private String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
	
	/**注册接收广播*/
	public void RegisterBroad(){
		//界面更新广播接收;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter("Intent.NOTIFY_ZIGBEE");
				registerReceiver(receiver, intentfilter);

			}
		}.start();	
	}
	
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("************************收到广播"+intent.getAction());
			Message msg=new Message();
			Bundle data=new Bundle();
			if(intent.getStringExtra("msg").split("/")[0].equals(str_mac)){
				switch(intent.getStringExtra("msg").split("/")[1]){
				
				}
			}
	
			
		}
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			unregisterReceiver(receiver);
		}catch(Exception e){
			e.printStackTrace();
			}
	}
	/**刷新时间*/
	private void refash_time(){
		try{
			new Thread(){
				public void run(){
					DBDevice db_d = new DBDevice(Kt_Frag_Other.this);
					List<MyDevice> devices = db_d.GetMyDevices();
					MyDevice md = null;
					String rst = null,rst_rq = null;
					for (int i = 0; i < devices.size(); i++) {	
						if (devices.get(i).getMac().equals(str_mac)) {
							md = devices.get(i);
							break;
						}
					}
		
					String rst_raw = OrderManager.getInstance().sendOrder(
							OrderList.Send_Zigbee_ByDeviceType(
									str_type,str_mac,OrderList.READ_ZIGBEE_TIME,"","",""),
									OrderList.USER_PASSWORD, URL_PATH, "utf-8");				
					if(rst_raw!=null){
						String status_time = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1); 
						if(!status_time.equals("200")){
							return;
						}
						rst = OrderManager.getInstance().getItemByOrder(rst_raw,
								"result", -1);
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] airData=new String[]{"refash_Time",rst};
						data.putStringArray("zigbeeAir", airData);
						msg.setData(data);		
						Kt_Frag_Other.this.handler.sendMessage(msg);
		
					}
	
				}
			}.start();
			}catch(Exception e){
				e.printStackTrace();
				}
	}
	
	/**校时*/
	private void correct_time(){
		try{
			new Thread(){
				public void run(){
					//-----
					MyOrder odToSend;
					switch (str_type) { 
					case Type_Entity.Split_air_conditioning://zigbee空调控制器
						odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
								str_mac, OrderList.WRITE_ZIGBEE_TIME,"","","");			
						break;
					default:
						odToSend = null;
						break;
					}
					
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
									OrderList.USER_PASSWORD, URL_PATH, "utf-8");
					if(rst_raw!=null){
						String status_time = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1); 
						if(status_time.equals("200")){
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] airData=new String[]{"correct_Time",};
							data.putStringArray("zigbeeAir", airData);
							msg.setData(data);		
							Kt_Frag_Other.this.handler.sendMessage(msg) ;		
						}else{
							return;
						}
					}else{
						return;
					}
					
				}
			}.start();
			}catch(Exception e){
				e.printStackTrace();
				}
	}
	
	
	/**刷新红外码条数*/
	private void refash_hwmts(){
		try{
			new Thread(){
				public void run(){
					MyOrder odToSend;
					switch (str_type) {
					case Type_Entity.Split_air_conditioning://zigbee空调控制器
						odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
								str_mac, OrderList.READ_ZIGBEE_AIR_HWM,"","","");			
						break;
					default:
						odToSend = null;
						break;
					}
					
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
									OrderList.USER_PASSWORD, URL_PATH, "utf-8");
					if(rst_raw!=null){
						String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1); 
						if(rst_error.equals("200")){
							String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
									"result", -1);
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] airData=new String[]{"refash_Hwmts",rst};
							data.putStringArray("zigbeeAir", airData);
							msg.setData(data);		
							Kt_Frag_Other.this.handler.sendMessage(msg) ;
						}else{
							return;
						}
					}
				}
			}.start();
			}catch(Exception e){
				e.printStackTrace();
				}
	}
	
	/**初始化红外码编码条数*/
	private void correct_hwmts(){
		try{
			new Thread(){
				public void run(){
					
					MyOrder odToSend;
					switch (str_type) {
					case Type_Entity.Split_air_conditioning://zigbee空调控制器
						odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
								str_mac, OrderList.CLEAR_ZIGBEE_HWM,"","","");			
						break;
					default:
						odToSend = null;
						break;
					}
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
									OrderList.USER_PASSWORD, URL_PATH, "utf-8");
					if(rst_raw!=null){
						String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1); 
						if(rst_error.equals("200")){
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] airData=new String[]{"correct_Hwmts"};
							data.putStringArray("zigbeeAir", airData);
							msg.setData(data);		
							Kt_Frag_Other.this.handler.sendMessage(msg) ;
						}else{
							return;
						}
					}
				}
			}.start();
			}catch(Exception e){
				e.printStackTrace();
				}
	}
	
	/**日程表初始化*/
	private void correct_rcb(){
		try{
			new Thread(){
				public void run(){
					MyOrder odToSend;
					switch (str_type) {
					case Type_Entity.Split_air_conditioning://zigbee空调控制器
						odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
								str_mac, OrderList.CSH_ZIGBEE_AIR_RCB,"","","");			
						break;
					default:
						odToSend = null;
						break;
					}
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
									OrderList.USER_PASSWORD, URL_PATH, "utf-8");
					if(rst_raw!=null){
						String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1); 
						if(rst_error.equals("200")){
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] airData=new String[]{"correct_Rcb"};
							data.putStringArray("zigbeeAir", airData);
							msg.setData(data);		
							Kt_Frag_Other.this.handler.sendMessage(msg) ;
							}else{
								return;
							}
						}
					}
				}.start();
			}catch(Exception e){
				e.printStackTrace();
				}
	}		
	
	/**删除以设置好的所有定时任务到服务器*/
	private void delAllCycleTask(final String str_mac){//tasks_/delAllCycleTask/002014110119

		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
							"0a0001aa7k/"+str_mac,"tasks_/delAllCycleTask/"+str_mac);	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001aa7k/"+str_mac,"tasks_/delAllCycleTask/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
}
