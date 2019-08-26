package com.northmeter.northmetersmart.fragment;

import java.text.DecimalFormat;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.ToastUtil;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/*四路灯控界面*/
public class TV_Fragment_Four extends Fragment implements OnClickListener {
	private boolean SOCKET_STATE_HEZHA = true;
	private boolean SOCKET_STATE_TIAOZHA = false;
	
	private String SHUAXIN="SHUXIN";//刷新标示

	private View view;
	private CheckBox checkBox1,checkBox_1,checkBox_2,checkBox_3,checkBox_4;
	private TextView tv_item0;
	private TextView tv_item1;
	private TextView tv_item2;
	private TextView tv_item3;
	private TextView tv_item4;
	private TextView tv_item5;
	private TextView text_item6;
	private TextView textview2_socket;//通讯网络字样
	private ImageView image_refash,image0_socket;
	private String URL_PATH;
	
	// 传进来的数据(mac)
	private String str_mac,str_name,str_type;
	private CustomProgressDialog progressDialog_1;
	private SharedPreferences sp;
	private ReceiveTool receiver;
	private String dwcssjk;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_four_street, container, false);
		try{
			URL_PATH = URL_help.getInstance().getUrl_address();
			checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
			checkBox1.setOnClickListener(this);
			checkBox_1 = (CheckBox) view.findViewById(R.id.checkBox_1);
			checkBox_1.setOnClickListener(this);
			checkBox_2 = (CheckBox) view.findViewById(R.id.checkBox_2);
			checkBox_2.setOnClickListener(this);
			checkBox_3 = (CheckBox) view.findViewById(R.id.checkBox_3);
			checkBox_3.setOnClickListener(this);
			checkBox_4 = (CheckBox) view.findViewById(R.id.checkBox_4);
			checkBox_4.setOnClickListener(this);
			image_refash= (ImageView) view.findViewById(R.id.img_refresh);
			// 获取传进来的数据 mac
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			// 初始化数据项视图
			initDataView();
			//注册接收广播
		    //RegisterBroad();
		    //读取通讯质量
			get_cq_of_meter();

			//刷新数据
			image_refash.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try{
					    sp = getActivity().getSharedPreferences("cp_state", getActivity().MODE_PRIVATE);
					    String cp_str = sp.getString(str_mac,str_mac+" null null");
					    if(!cp_str.split(" ")[2].equals("0")){
				    		progressDialog_1=CustomProgressDialog.createDialog(getActivity());
						    progressDialog_1.setMessage("正在读取电能信息");
							progressDialog_1.show();					
							new Thread(){
								public void run(){
									get_nowTime_data();
								}
							}.start();
					    }else{
					    	ToastUtil.showToast(getActivity(), "网关不在线", 5000);
					    }	
					}catch(Exception e){
						e.printStackTrace();
					}      
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
			Message msg=new Message();
			Bundle data=new Bundle();
			String return_data="设备不在线";
			String [] dnData=new String[]{SHUAXIN,return_data};
			data.putStringArray("THZ", dnData);
			msg.setData(data);	
			TV_Fragment_Four.this.handler.sendMessage(msg);
			//progressDialog_1.dismiss();
			}
		return view;
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if(getActivity().isFinishing()){
				return;
			}
			
			Bundle data=msg.getData();
			String[] thz=data.getStringArray("THZ");

			if(thz[0].equals("MQTT")){
				switch(thz[1]){
				case "HZ_ALL":
					checkBox1.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_ALL":
					checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
					break;
				case "HZ_1":
					checkBox_1.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_1":
					checkBox_1.setChecked(SOCKET_STATE_TIAOZHA);
					break;
				case "HZ_2":
					checkBox_2.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_2":
					checkBox_2.setChecked(SOCKET_STATE_TIAOZHA);
					break;
				case "HZ_3":
					checkBox_3.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_3":
					checkBox_3.setChecked(SOCKET_STATE_TIAOZHA);
					break;
				case "HZ_4":
					checkBox_4.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_4":
					checkBox_4.setChecked(SOCKET_STATE_TIAOZHA);
					break;
					default:
						break;
				}
				sp = getActivity().getSharedPreferences("status", 0);
				Editor editor = sp.edit();
				editor.putString(str_mac,"100");
				editor.commit();

				progressDialog_1.dismiss();
				Toast toast=Toast.makeText(getActivity(), "连接异常，请检查网络！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			if(thz[0].equals(SHUAXIN)){
				if(thz[1].equals("SUCCESS")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("FAIL")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "刷新失败！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("CONTROL_SUCCESS")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				else if(thz[1].equals("连接异常")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "连接不上服务器了-_-", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				else if(thz[1].equals("TIMEOUT")){
					initDataView();
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(),"连接超时！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				else{
					SharedPreferences spfs = getActivity().getSharedPreferences("THZAN", 0);
					String thzanniu = spfs.getString("THZAN", "TZ0");
					switch(thzanniu){
					case "HZ0":
						checkBox1.setChecked(SOCKET_STATE_HEZHA);
						break;
					case "TZ0":
						checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
						break;
					case "HZ1":
						checkBox_1.setChecked(SOCKET_STATE_HEZHA);
						break;
					case "TZ1":
						checkBox_1.setChecked(SOCKET_STATE_TIAOZHA);
						break;
					case "HZ2":
						checkBox_2.setChecked(SOCKET_STATE_HEZHA);
						break;
					case "TZ2":
						checkBox_2.setChecked(SOCKET_STATE_TIAOZHA);
						break;
					case "HZ3":
						checkBox_3.setChecked(SOCKET_STATE_HEZHA);
						break;
					case "TZ3":
						checkBox_3.setChecked(SOCKET_STATE_TIAOZHA);
						break;
					case "HZ4":
						checkBox_4.setChecked(SOCKET_STATE_HEZHA);
						break;
					case "TZ4":
						checkBox_4.setChecked(SOCKET_STATE_TIAOZHA);
						break;
						default:
							break;
					}
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(),"控制失败！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			}
			
			if(thz[0].equals("SOCKET_STATE")&&thz[1].equals("FAIL")){
				progressDialog_1.dismiss();
				//handler.removeCallbacks(runnable_2);
				Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
				return;
			}
			
			progressDialog_1.dismiss();
			checkBox1.setClickable(true);
				}catch(Exception e){
					e.printStackTrace();
			}
		}
	};
	
/* --------------------------------------------------------------------------------------  */
	Handler handler_1 = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try{
				super.handleMessage(msg);
				if (msg.what == 1) {
					Message message_msg=new Message();
					Bundle bundle =new Bundle();
					String bundle_msg = msg.getData().getString("content");
					if(bundle_msg.split("/")[1].equals(str_mac)){
						switch(bundle_msg.split("/")[2]){
						case "get_cq_of_meter"://设备通讯质量 selected/0a0001aa7k/002014110119/get_cq_of_meter/002014119119  100 100 
							try{
								//通讯质量显示
								//Toast.makeText(getActivity(), bundle_msg.split("/")[3], 3000).show();
								init_cp();
								get_nowTime_data();
							}catch(Exception e){
								e.printStackTrace();
							}
							break;	
							
						case "Zigbee_dwcssjk":
							dwcssjk = bundle_msg.split("/")[3];	
							if(dwcssjk.equals("FAIL")){
								String failMsg = bundle_msg.split("/")[4];//抄表失败时反馈的数据
								Toast.makeText(getActivity(), failMsg, Toast.LENGTH_LONG).show();
							}else{
								float gl = Float.parseFloat(dwcssjk.split(",")[3])*1000;
								DecimalFormat df = new DecimalFormat("#.00");
								tv_item0.setText(Double.valueOf(dwcssjk.split(",")[0]) + "kWh");
								tv_item1.setText(Double.valueOf(df.format(gl)) + "W");
								tv_item2.setText(Double.valueOf(dwcssjk.split(",")[1]) + "V");
								tv_item3.setText(Double.valueOf(dwcssjk.split(",")[2]) + "A");
								tv_item4.setText(Double.valueOf(dwcssjk.split(",")[6]) + "");
								tv_item5.setText(Double.valueOf(dwcssjk.split(",")[5])+"Hz");
								String thzztsj = getBinaryString(dwcssjk.split(",")[7]);
								if(thzztsj.indexOf("1")>=0){//thzztsj.equals("1111")
									text_item6.setText("合闸");
									checkBox1.setChecked(SOCKET_STATE_HEZHA);
								}else{
									text_item6.setText("跳闸");
									checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
								}
								
								if(thzztsj.substring(0,1).equals("1")){
									checkBox_1.setChecked(SOCKET_STATE_HEZHA);
								}else{
									checkBox_1.setChecked(SOCKET_STATE_TIAOZHA);
								}
								if(thzztsj.substring(1,2).equals("1")){
									checkBox_2.setChecked(SOCKET_STATE_HEZHA);
								}else{
									checkBox_2.setChecked(SOCKET_STATE_TIAOZHA);
								}
								if(thzztsj.substring(2,3).equals("1")){
									checkBox_3.setChecked(SOCKET_STATE_HEZHA);
								}else{
									checkBox_3.setChecked(SOCKET_STATE_TIAOZHA);
								}
								if(thzztsj.substring(3,4).equals("1")){
									checkBox_4.setChecked(SOCKET_STATE_HEZHA);
								}else{
									checkBox_4.setChecked(SOCKET_STATE_TIAOZHA);
								}
								
							}
							progressDialog_1.dismiss();
							Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
							break;	
							
						}
					
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	/**刷新数据*/
	private void get_nowTime_data(){
		new Thread(){
			public void run(){
				try{
							
					MyOrder odToSend;
					switch (str_type) {
					case Type_Entity.Four_street_control://zigbee四路灯控面板
						odToSend = OrderList.getSendByDeviceType(str_type,
								str_mac, OrderList.READ_ZIGBEE_SHUJU,"");			
						break;
					case Type_Entity.Socket_type://zigbee插座
						odToSend = OrderList.getSendByDeviceType(str_type,
								str_mac, OrderList.READ_ZIGBEE_SHUJU,"");
						break;
					default:
						odToSend = null;
						break;
					}
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					// =================================================
						
				}catch(Exception e){
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="连接异常";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					System.out.println("发送信息----");
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg) ;
					e.printStackTrace();
					}
				    }
				}.start();
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//注册接收广播
		RegisterBroad();
		
		checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
		// 获取传进来的数据 mac
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		
		initDataView();
	
	}




	@Override
	public void onClick(View v) {
		try{
		SharedPreferences spf = getActivity().getSharedPreferences("THZAN", 0);
		final Editor edit = spf.edit();
		switch (v.getId()) {
		case R.id.checkBox1:
			// 如果不是isSelected就是isChecked

			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
			//((CheckBox) v).setClickable(false);
			if (((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA) {
				// 之前状态为合闸状态，因此要执行
				// “跳闸”
				progressDialog_1.setMessage("正在跳闸");//设置进度条信息为合闸
				progressDialog_1.show();

			 new Thread(){
				public void run(){	
					try{
				//Toast.makeText(getActivity(), "跳闸", Toast.LENGTH_SHORT).show();
				
				// =================================================
				// 发送命令并得到返回结果，此处应该是跳闸
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF,"");
					break;

				default:
					odToSend = null;
					break;
				}
					
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "HZ0");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					System.out.println("mqtt连接异常，请检查网络！*********************");
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="HZ_ALL";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
					}catch(Exception e){
						e.printStackTrace();
					}
				}

			}.start();
			} else {
				// 之前状态为跳闸状态，因此要执行
				// “合闸”
				//Toast.makeText(getActivity(), "合闸", Toast.LENGTH_SHORT).show();
				
				// =================================================
				progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
				progressDialog_1.show();
				
			new Thread(){
				public void run(){ 
					try{
						
				// 发送命令并得到返回结果，此处应该是合闸
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON,"");
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON,"");
					break;

				default:
					odToSend = null;
					break;
				}
				
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "TZ0");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="TZ_ALL";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
					}catch(Exception e){
						e.printStackTrace();
					}
			}
				
			}.start();
			}
			break;
			
		case R.id.checkBox_1:
			// 如果不是isSelected就是isChecked
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
			if (((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA) {
				// 之前状态为合闸状态，因此要执行
				// “跳闸”
				progressDialog_1.setMessage("正在跳闸");//设置进度条信息为合闸
				progressDialog_1.show();

			 new Thread(){
				public void run(){			
				//Toast.makeText(getActivity(), "跳闸", Toast.LENGTH_SHORT).show();
				// =================================================
				// 发送命令并得到返回结果，此处应该是跳闸
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_1,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_1,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "HZ1");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="HZ_1";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
					}
				}
			}.start();
			
			} else {
				// 之前状态为跳闸状态，因此要执行
				// “合闸”
				// =================================================
				progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
				progressDialog_1.show();
				
			 new Thread(){
				public void run(){ 
				// =================================================
				// 发送命令并得到返回结果，此处应该是合闸
				
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_1,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_1,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "TZ1");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="TZ_1";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
				
			}
			}.start();
			}
			break;
			
		case R.id.checkBox_2:
			// 如果不是isSelected就是isChecked
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
			if (((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA) {
				// 之前状态为合闸状态，因此要执行
				// “跳闸”
				progressDialog_1.setMessage("正在跳闸");//设置进度条信息为合闸
				progressDialog_1.show();

			new Thread(){
				public void run(){			
				//Toast.makeText(getActivity(), "跳闸", Toast.LENGTH_SHORT).show();
				// =================================================
				// 发送命令并得到返回结果，此处应该是跳闸
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_2,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_2,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "HZ2");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="HZ_2";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				}

				}
			}.start();
			} else {
				// 之前状态为跳闸状态，因此要执行
				// “合闸”
				//Toast.makeText(getActivity(), "合闸", Toast.LENGTH_SHORT).show();
				
				// =================================================
				progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
				progressDialog_1.show();
				
			new Thread(){
				public void run(){ 
				// =================================================
				// 发送命令并得到返回结果，此处应该是合闸
				
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_2,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_2,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "TZ2");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="TZ_2";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
					}
				
				}
			}.start();
			}
			break;
			
		case R.id.checkBox_3: 
			// 如果不是isSelected就是isChecked
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
			if (((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA) {
				// 之前状态为合闸状态，因此要执行
				// “跳闸”
				progressDialog_1.setMessage("正在跳闸");//设置进度条信息为合闸
				progressDialog_1.show();

			new Thread(){
				public void run(){			
				//Toast.makeText(getActivity(), "跳闸", Toast.LENGTH_SHORT).show();
				// =================================================
				// 发送命令并得到返回结果，此处应该是跳闸
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_3,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_3,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "HZ3");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="HZ_3";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				}

			  }
			}.start();
			} else {
				// 之前状态为跳闸状态，因此要执行
				// “合闸”
				//Toast.makeText(getActivity(), "合闸", Toast.LENGTH_SHORT).show();
				
				// =================================================
				progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
				progressDialog_1.show();
				
			new Thread(){
				public void run(){ 
				// =================================================
				// 发送命令并得到返回结果，此处应该是合闸
				
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_3,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_3,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "TZ3");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="TZ_3";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
			  }
			}.start();
			}
			break;
			
	    case R.id.checkBox_4:
			// 如果不是isSelected就是isChecked
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
			if (((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA) {
				// 之前状态为合闸状态，因此要执行
				// “跳闸”
				progressDialog_1.setMessage("正在跳闸");//设置进度条信息为合闸
				progressDialog_1.show();

			new Thread(){
				public void run(){			
				//Toast.makeText(getActivity(), "跳闸", Toast.LENGTH_SHORT).show();
				// 发送命令并得到返回结果，此处应该是跳闸
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_4,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_OFF_4,"");
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "HZ4");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="HZ_4";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
				  }
				}
			}.start();
			} else {
				// 之前状态为跳闸状态，因此要执行
				// “合闸”
				
				// =================================================
				progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
				progressDialog_1.show();
				
			new Thread(){
				public void run(){ 
				// =================================================
				// 发送命令并得到返回结果，此处应该是合闸
				
				MyOrder odToSend;
				switch (str_type) {
				// 智能插座
				case Type_Entity.Wifi_socket: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON);
					break;  
				case Type_Entity.Four_street_control://zigbee四路灯控面板
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_4,"");			
					break;
				case Type_Entity.Socket_type://zigbee插座
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.ORDER_SOCKET_ON_4,"");
					break;

				default:
					odToSend = null;
					break;
				}
				
				//发送mqtt推送消息;
				try {
					//发送mqtt推送消息;
					String send_msg = Utils.sendOrder(odToSend);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
												
					edit.putString("THZAN", "TZ4");
					edit.commit();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="TZ_4";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					TV_Fragment_Four.this.handler.sendMessage(msg);
					e.printStackTrace();
					}
	
				}
			}.start();
			}
			break;
				
			//--------------------------------------------------------
			
	    case R.id.layout_dialog:
	    	init_dialog_view();
	    	break;
	
		}

	}catch(Exception e){
		e.printStackTrace();
		Message msg=new Message();
		Bundle data=new Bundle();
		String [] thzxx=new String[]{"SOCKET_STATE","FAIL"};
		data.putStringArray("THZ", thzxx);	
		msg.setData(data);		
		TV_Fragment_Four.this.handler.sendMessage(msg) ;
	} 
				 
	}
	
	
	/**打开弹出时初始化数据视图*/
	private void init_dialog_view(){
		try{
			final AlertDialog dialogSex = new AlertDialog.Builder(getActivity()).create();  
			dialogSex.show();  
			Window window = dialogSex.getWindow();  
			window.setContentView(R.layout.dialog_switch_message);  
			dialogSex.setCanceledOnTouchOutside(true); 
			dialogSex.setCancelable(true);			
			// 可以在此设置显示动画
			window.setWindowAnimations(R.style.AnimBottom_Dialog);
			
			TextView dialog_item0 = (TextView) window.findViewById(R.id.item0);
			TextView dialog_item1 = (TextView) window.findViewById(R.id.item1);
			TextView dialog_item2 = (TextView) window.findViewById(R.id.item2);
			TextView dialog_item3 = (TextView) window.findViewById(R.id.item3);
			TextView dialog_item4 = (TextView) window.findViewById(R.id.item4);
			TextView dialog_item5 = (TextView) window.findViewById(R.id.item5);
			TextView dialog_item6 = (TextView) window.findViewById(R.id.item6);
			
			
			if(dwcssjk!=null){
				float gl = Float.parseFloat(dwcssjk.split(",")[3])*1000;
				DecimalFormat df = new DecimalFormat("#.00");
				dialog_item0.setText(Double.valueOf(dwcssjk.split(",")[0]) + "kWh");
				dialog_item1.setText(Double.valueOf(df.format(gl)) + "W");
				dialog_item2.setText(Double.valueOf(dwcssjk.split(",")[1]) + "V");
				dialog_item3.setText(Double.valueOf(dwcssjk.split(",")[2]) + "A");
				dialog_item4.setText(Double.valueOf(dwcssjk.split(",")[6]) + "");
				dialog_item5.setText(Double.valueOf(dwcssjk.split(",")[5])+"Hz");
				String thzztsj = getBinaryString(dwcssjk.split(",")[7]);
				if(thzztsj.indexOf("1")>=0){//thzztsj.equals("1111")
					dialog_item6.setText("合闸");
				}else{
					dialog_item6.setText("跳闸");
				}
			}else{
				dialog_item0.setText("0");
				dialog_item1.setText("0");
				dialog_item2.setText("0");
				dialog_item3.setText("0");
				dialog_item4.setText("0");
				dialog_item5.setText("0");
				dialog_item6.setText("未知");	
				}
			window.findViewById(R.id.relativeLayout2).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialogSex.cancel();
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/*进入界面时初始化数据*/
	private void initDataView() {
		try{
		System.out.println("开始初始化View！");
		view.findViewById(R.id.layout_dialog).setOnClickListener(this);
		
		tv_item0 = (TextView) view.findViewById(R.id.item0);
		tv_item1 = (TextView) view.findViewById(R.id.item1);
		tv_item2 = (TextView) view.findViewById(R.id.item2);
		tv_item3 = (TextView) view.findViewById(R.id.item3);
		tv_item4 = (TextView) view.findViewById(R.id.item4);
		tv_item5 = (TextView) view.findViewById(R.id.item5);
		text_item6 = (TextView) view.findViewById(R.id.item6);
		textview2_socket = (TextView) view.findViewById(R.id.textview2_socket);
		
		image0_socket = (ImageView) view.findViewById(R.id.image0_socket);//通讯网络显示
		//设备通讯质量
		init_cp();
		
		}catch(Exception e){
			e.printStackTrace();
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
	/**handle异常*/
	public void handleException(){
		Message msg=new Message();
		Bundle data=new Bundle();
		String return_data="连接异常";
		String [] dnData=new String[]{"MQTT",return_data};
		data.putStringArray("THZ", dnData);
		msg.setData(data);	
		TV_Fragment_Four.this.handler.sendMessage(msg) ;
	}
	/**把16进制转换为2进制数据*/
	/**因为手机device数据库里面存的跳合闸信息是1个字节的16进制数，这里转换为2进制才是4路灯控的的跳合闸信息*/
	private String getBinaryString(String thzzt){
		int intformer=Integer.parseInt(Integer.valueOf(thzzt,16).toString());//16进制转换为10进制
		String intform=Integer.toBinaryString(intformer);//10进制转换为2进制
		//补齐4位
		if(intform.length()<4){
			String str ="0000";
			intform=str.substring(0, 4-intform.length())+intform;
		}
		System.out.println("++++++++++++------*****"+intform);
		return intform;
	}
	 /**读取设备通讯质量
     * realtimedata/表类型/表号/get_cq_of_meter
	   realtimedata/类型/采集器表号/get_cq_of_gateway
	*/
	private void get_cq_of_meter(){
		new Thread(){
			public void run(){
				try {
					//realtimedata/类型/表号/get_cq_of_meter
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
							Type_Entity.Four_street_control+"/"+str_mac,
							"realtimedata/"+Type_Entity.Four_street_control+"/"+str_mac+"/get_cq_of_meter");	
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handleException();
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
				IntentFilter intentfilter = new IntentFilter();  
				intentfilter.addAction("Intent.NOTIFY_ZIGBEE");  
				intentfilter.addAction("Intent.UPDATA"); 
				intentfilter.addAction("Intent.SELECT_cq");  
				getActivity().registerReceiver(receiver, intentfilter); 
				
			}
		}.start();	
	}
	
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("************************收到广播，这是四路灯控界面："+intent.getAction()+"/:"+intent.getStringExtra("msg"));
			try{
				Message msg=new Message();
				Bundle data=new Bundle();
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "UPDATA"://数据上报时如果是该设备的数据这里也会更新一次数据
						initDataView();
						break;
					case "CONTROL_SUCCESS_HZ"://跳合闸成功
						get_nowTime_data();
						String data_0="CONTROL_SUCCESS";
						String [] Data_0=new String[]{SHUAXIN,data_0};
						data.putStringArray("THZ", Data_0);
						msg.setData(data);	
						TV_Fragment_Four.this.handler.sendMessage(msg);
						break;
					case "CONTROL_SUCCESS_TZ"://跳合闸成功
						get_nowTime_data();
						String data_="CONTROL_SUCCESS";
						String [] Data_=new String[]{SHUAXIN,data_};
						data.putStringArray("THZ", Data_);
						msg.setData(data);	
						TV_Fragment_Four.this.handler.sendMessage(msg);
						break;
					case "TIMEOUT":
						String data_3="TIMEOUT";
						String [] Data_2=new String[]{SHUAXIN,data_3};
						data.putStringArray("THZ", Data_2);
						msg.setData(data);	
						TV_Fragment_Four.this.handler.sendMessage(msg);
						break;
					
					case "CONTROL"://跳合闸失败
						String data_1=intent.getStringExtra("msg").toString();
						String [] Data_1=new String[]{SHUAXIN,data_1};
						data.putStringArray("THZ", Data_1);
						msg.setData(data);	
						TV_Fragment_Four.this.handler.sendMessage(msg);
						break;
					}
				}
	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
		getActivity().unregisterReceiver(receiver);
		}catch(Exception e){
			e.printStackTrace();
			}
	}
	

	/**显示通讯质量*/
	private void init_cp(){
		try{
			sp = getActivity().getSharedPreferences("cp_state", getActivity().MODE_PRIVATE);
			String cancellation_msg = sp.getString(str_mac,str_mac+" null null");
			
			if(!cancellation_msg.equals(str_mac+" null null")){
				String[] cancellation_list = cancellation_msg.split(" ");
				if(!cancellation_list[2].equals("0")){
					textview2_socket.setTextColor(Color.WHITE);
		            int cancellation = Integer.parseInt(cancellation_list[1]);
		            if(cancellation==0){
		            	textview2_socket.setTextColor(Color.RED);
		            	image0_socket.setImageResource(R.drawable.signal_0);
		            }else if(cancellation>0&&cancellation<20){
		            	image0_socket.setImageResource(R.drawable.signal_1);
		            }else if(cancellation>19&&cancellation<40){
		            	image0_socket.setImageResource(R.drawable.signal_2);
		            }else if(cancellation>39&&cancellation<60){
		            	image0_socket.setImageResource(R.drawable.signal_3);
		            }else if(cancellation>59&&cancellation<80){
		            	image0_socket.setImageResource(R.drawable.signal_4);
		            }else if(cancellation>79&&cancellation<=100){
		            	image0_socket.setImageResource(R.drawable.signal_5);
		            }else{
		            	image0_socket.setImageResource(R.drawable.signal_0);
		            	textview2_socket.setTextColor(Color.RED);
		            }
				}else{							
					LayoutInflater inflater = getActivity().getLayoutInflater();
					View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) view.findViewById(R.id.toast_layout));
					TextView toast_text = (TextView) layout.findViewById(R.id.toast_text);
					toast_text.setText("网关不在线");
					Toast toast = new Toast(getActivity());
					toast.setView(layout);
					toast.setDuration(5000);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					
					if(progressDialog_1!=null){
						progressDialog_1.dismiss();
					}
				}

			}else{
				image0_socket.setImageResource(R.drawable.signal_0);
				textview2_socket.setTextColor(Color.RED);
				
				if(progressDialog_1!=null){
					progressDialog_1.dismiss();
				}
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
