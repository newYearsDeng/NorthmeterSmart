package com.northmeter.northmetersmart.fragment;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
	1100    PM1.0 xxxxug/m3
	1500	PM2.5 xxxxug/m3
	1700	PM10  xxxxug/m3
	3000	温度	℃
	45	湿度  xx %
	2700	甲醛 xxxx ug/m3
	ffff	tvoc xxxx ppm
	4012	co2 xxxx ug/m3
	ffff	co xxxx ug/m3
	ffff	o2 xxxx ug/m3
	ffff	预留
	0607270100 时间 yymmddhhmm*/
/**空气质量检测仪*/
public class Fragment_AirQualityQetector_1 extends Fragment implements OnClickListener{
	private View view;
	private String str_mac,str_name,str_type;
	private TextView text_pm2_5,text_temputer,text_wet,text_jq,text_pm1_0,text_pm10,text_co2;
	private CheckBox checkbox1;
	private ImageView image_socket;
	private TextView textview_socket;
	private CustomProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_airqualityqetector_1, container, false);
		init_view();
		get_AirQuality();
		return view;
	}
	
	private void init_view(){
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		
		view.findViewById(R.id.img_refresh).setOnClickListener(this);
		checkbox1 = (CheckBox) view.findViewById(R.id.checkbox1);
		checkbox1.setOnClickListener(this);
		image_socket = (ImageView) view.findViewById(R.id.image_socket);
		textview_socket = (TextView) view.findViewById(R.id.textview_socket);
		
		text_pm2_5 = (TextView) view.findViewById(R.id.text_pm2_5);
		text_temputer = (TextView) view.findViewById(R.id.text_temputer);
		text_wet = (TextView) view.findViewById(R.id.text_wet);
		text_jq = (TextView) view.findViewById(R.id.text_jq);
		text_pm1_0 = (TextView) view.findViewById(R.id.text_pm1_0);
		text_pm10 = (TextView) view.findViewById(R.id.text_pm10);
		text_co2 = (TextView) view.findViewById(R.id.text_co2);
		//text_kqzl = (TextView) view.findViewById(R.id.text_kqzl);//空气质量指标
		
		progressDialog = CustomProgressDialog.createDialog(getActivity());
		
	}
	
	Handler handler_1 = new Handler(){
		@Override
		public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				switch(msg.what){
				case 1:
					String bundle_msg = msg.getData().getString("content");
					if(bundle_msg.split("/")[1].equals(str_mac)){
						switch(bundle_msg.split("/")[2]){
						case "zigbee_zdsb2":
							get_cq_of_meter();
							String zdsb2 = bundle_msg.split("/")[3];	
							if(zdsb2.equals("FAIL")){
								String failMsg = bundle_msg.split("/")[4];//抄表失败时反馈的数据
								Toast.makeText(getActivity(), failMsg, Toast.LENGTH_LONG).show();
								break;
							}else{
								show_message(zdsb2);
							}
							break;
						case "get_cq_of_meter"://设备通讯质量 selected/0a0001aa7k/002014110119/get_cq_of_meter/002014119119  100 100 
							try{
								//通讯质量显示
								//Toast.makeText(getActivity(), bundle_msg.split("/")[3], 3000).show();
								init_cp();
//								get_nowTime_data();
							}catch(Exception e){
								e.printStackTrace();
							}
							break;	
						}
					}
					break;
				case 2:
					Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();
					break;
				}
		}
	};
	/**
	1100    PM1.0 xxxxug/m3
	1500	PM2.5 xxxxug/m3
	1700	PM10  xxxxug/m3
	3000	温度	℃
	45	湿度  xx %
	2700	甲醛 xxxx ug/m3
	ffff	tvoc xxxx ppm
	4012	co2 xxxx ug/m3
	ffff	co xxxx ug/m3
	ffff	o2 xxxx ug/m3
	ffff	预留
	0607270100 时间 yymmddhhmm*/ //"6,8,9,30.0,0,3741,NULL,NULL,NULL,NULL,ffff,NULL
	private void show_message(String data){
		try{
			String[] dataList = data.split(",");
			String pm1_0 = dataList[0]+"ug/m³";
			int pm2_5 = Integer.valueOf(dataList[1]);
			String pm10 = dataList[2]+"ug/m³";
			String temp = dataList[3]+"℃";
			String wet = dataList[4]+"%";
			String jiaquan = dataList[5]+"ug/m³";
			String co2 = dataList[7]+"ppm";
			
			text_pm2_5.setText(pm2_5+"ug/m³"); 
			text_temputer.setText(temp); 
			text_wet.setText(wet); 
			text_jq.setText(jiaquan); 
			text_pm1_0.setText(pm1_0); 
			text_pm10.setText(pm10); 
			text_co2.setText(co2); 
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void get_AirQuality(){
		new Thread(){
			public void run(){
				MyOrder odToSend;
				switch (str_type) {
				case Type_Entity.Air_Quality_Qetector://空气质量检测仪
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.Air_Quality_Qetector_Read,"");	
					break;
				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				String send_msg = Utils.sendOrder(odToSend);
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
							str_type+"/"+str_mac,send_msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message message = new Message();
					message.what = 2;
					message.obj = "exception"; 
					handler_1.sendMessage(message);
				}
			}
			
		}.start();
		
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
							str_type+"/"+str_mac,
							"realtimedata/"+str_type+"/"+str_mac+"/get_cq_of_meter");	
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.img_refresh:
			progressDialog.show();
			get_AirQuality();
			break;
		case R.id.checkbox1:
			break;
		}
		
	}
	
	
	
	/**显示通讯质量*/
	private void init_cp(){
		try{
			SharedPreferences sp = getActivity().getSharedPreferences("cp_state", getActivity().MODE_PRIVATE);
			String cancellation_msg = sp.getString(str_mac,str_mac+" null null");
			
			if(!cancellation_msg.equals(str_mac+" null null")){
				String[] cancellation_list = cancellation_msg.split(" ");
				if(!cancellation_list[2].equals("0")){
					textview_socket.setTextColor(Color.WHITE);
		            int cancellation = Integer.parseInt(cancellation_list[1]);
		            if(cancellation==0){
		            	image_socket.setImageResource(R.drawable.signal_0);
		            	textview_socket.setTextColor(Color.RED);
		            }else if(cancellation>0&&cancellation<20){
		            	image_socket.setImageResource(R.drawable.signal_1);
		            }else if(cancellation>19&&cancellation<40){
		            	image_socket.setImageResource(R.drawable.signal_2);
		            }else if(cancellation>39&&cancellation<60){
		            	image_socket.setImageResource(R.drawable.signal_3);
		            }else if(cancellation>59&&cancellation<80){
		            	image_socket.setImageResource(R.drawable.signal_4);
		            }else if(cancellation>79&&cancellation<=100){
		            	image_socket.setImageResource(R.drawable.signal_5);
		            }else{
		            	textview_socket.setTextColor(Color.RED);
		            	 image_socket.setImageResource(R.drawable.signal_0);
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
					
					if(progressDialog!=null){
						progressDialog.dismiss();
					}
	            }
            }else{
  			   image_socket.setImageResource(R.drawable.signal_0);
  			   textview_socket.setTextColor(Color.RED);
  			   if(progressDialog!=null){
  				 progressDialog.dismiss();
  			   }
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
}
