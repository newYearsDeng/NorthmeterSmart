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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**zigbee插座主控界面*/
public class TV_Fragment1 extends Fragment implements OnClickListener {
	private boolean SOCKET_STATE_HEZHA = true;
	private boolean SOCKET_STATE_TIAOZHA = false;
	private String SHUAXIN="SHUXIN";//刷新标示

	private View view;
	private ImageView im_imageView1;
	private CheckBox checkBox1;
	private TextView tv_item0;
	private TextView tv_item1;
	private TextView tv_item3;
	private TextView textview2_socket;//通讯网络字样
	private ImageView image_refash,image0_socket;
	/**活动流ui*/
	private TextView time_on,time_continue,time_off;
	private ImageView activ_img_1,activ_img_2,activ_img_3,activ_img_4,activ_img_5;
	private LinearLayout linear_action_1;
	
	// 传进来的数据(mac)
	private String str_mac,str_name,str_type;
	private CustomProgressDialog progressDialog_1;
	private SharedPreferences sp;
	private String URL_PATH;
	private ReceiveTool receiver;
	private String dwcssjk=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_f1, container, false);
		try{
		checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
		checkBox1.setOnClickListener(this);
		image_refash= (ImageView) view.findViewById(R.id.img_refresh);
		URL_PATH = URL_help.getInstance().getUrl_address();
		
		// 获取传进来的数据 mac
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		
		//初始化数据项视图
		initDataView();
		//注册接收广播
		//RegisterBroad();
		//读取通讯质量
		get_cq_of_meter();
// 		//活动流
//		get_socket_readtime_workflow();

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
//					   
					}catch(Exception e){
						e.printStackTrace();
					}  
				}
			});
		
		}catch(Exception e){
			e.printStackTrace();
			}
		return view;
	}
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
						System.out.println("mqtt数据mqtt数据mqtt数据mqtt数据::::"+msg.getData().getString("content"));
						switch(bundle_msg.split("/")[2]){
						case "get_cq_of_meter"://设备通讯质量 selected/0a0001aa7k/002014110119/get_cq_of_meter/002014119119  100 100 
							try{
								//通讯质量显示
								//Toast.makeText(getActivity(), bundle_msg.split("/")[3], 3000).show();
								init_cp();
								//活动流
								get_socket_readtime_workflow();
								get_nowTime_data();
							}catch(Exception e){
								e.printStackTrace();
							}
							break;
						
						case "get_socket_readtime_workflow"://活动流
							String receive = bundle_msg.split("/")[3];
							SharedPreferences sp = getActivity().getSharedPreferences("socket_workflow", getActivity().MODE_PRIVATE);
							Editor edit = sp.edit();
							edit.putString(str_mac, receive);
							edit.commit();
							
							init_workflow(receive);
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
								tv_item3.setText(Double.valueOf(dwcssjk.split(",")[2]) + "A");
								if(dwcssjk.split(",")[7].equals("00")){
									checkBox1.setChecked(SOCKET_STATE_HEZHA);
								}if(dwcssjk.split(",")[7].equals("01")){
									checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
								}
								System.out.println("刷新完成------------");
								Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
							}
							progressDialog_1.dismiss();
							break;
								
						}
					
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	
    Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if(getActivity().isFinishing()){
				return;
			}
			
			Bundle data=msg.getData();
			String[] thz=data.getStringArray("THZ");
			
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
			(ViewGroup) view.findViewById(R.id.toast_layout));
			TextView toast_text = (TextView) layout.findViewById(R.id.toast_text);
			
			if(thz[0].equals("MQTT_CANCLE")){
				switch(thz[1]){
				case "HZ_ALL":
					checkBox1.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_ALL":
					checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
					break;
					default:
						break;
				}
				if(progressDialog_1!=null){
					progressDialog_1.dismiss();
				}
				return;
			}
			
			if(thz[0].equals("MQTT")){
				switch(thz[1]){
				case "HZ_ALL":
					checkBox1.setChecked(SOCKET_STATE_HEZHA);
					break;
				case "TZ_ALL":
					checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
					break;
					default:
						break;
				}
				sp = getActivity().getSharedPreferences("status", getActivity().MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString(str_mac,"100");
				editor.commit();
				
				if(progressDialog_1!=null){
					progressDialog_1.dismiss();
				}				
				ToastUtil.showToast(getActivity(), "连接不上服务器了-_-", 5000);
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
				}else if(thz[1].equals("CONTROL_SUCCESS_TZ")){
					initDataView();
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("CONTROL_SUCCESS_HZ")){
					initDataView();
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
					}
				else if(thz[1].equals("TIMEOUT")){
					initDataView();
					progressDialog_1.dismiss();
					toast_text.setText("服务器无响应");
					Toast toast = new Toast(getActivity());
					toast.setView(layout);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				else{//操作失败原因
					switch(thz[1].split("/")[3]){
					case "hz":
						checkBox1.setChecked(true);
						break;
					case "tz":
						checkBox1.setChecked(false);
						break;
					}
					initDataView();
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(),thz[1].split("/")[4], Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			}
			progressDialog_1.dismiss();
			checkBox1.setClickable(true);
				}catch(Exception e){
					e.printStackTrace();
			}
		}
	};
	
/* --------------------------------------------------------------------------------------  */
	
	/**刷新数据*/
	private void get_nowTime_data(){
		new Thread(){
		public void run(){
			try{

				MyOrder odToSend;
				switch (str_type) {
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
											
					
			}catch(Exception e){
				System.out.println("mqtt传递的异常");
				e.printStackTrace();
				Message msg  = new Message();
				Bundle data = new Bundle();
				String _data="连接异常";
				String [] Data=new String[]{"MQTT",_data};
				data.putStringArray("THZ", Data);
				msg.setData(data);	
				TV_Fragment1.this.handler.sendMessage(msg);
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
		switch (v.getId()) {
		case R.id.layout_dialog:
			init_dialog_view();
			break;
		
		case R.id.checkBox1:
			try{
				// 如果不是isSelected就是isChecked
				//((CheckBox) v).setClickable(false);
				progressDialog_1=CustomProgressDialog.createDialog(getActivity());
				
				if(((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA){
					dialog_thz_point("tz");
				}else{
					dialog_thz_point("hz");
													
				}
			
			}catch(Exception e){
				Message msg  = new Message();
				Bundle data = new Bundle();
				String _data="连接异常";
				String [] Data=new String[]{"MQTT",_data};
				data.putStringArray("THZ", Data);
				msg.setData(data);	
				TV_Fragment1.this.handler.sendMessage(msg);
				e.printStackTrace();
			}
			break;
							
		}
		  
				 
	}
	
	/**打开弹出提示跳合闸*/
	private void dialog_thz_point(final String thz_msg){
		try{
			final Dialog dialogSex = new Dialog(getActivity(), R.style.dialog);
			View viewSex = getActivity().getLayoutInflater().inflate(R.layout.thz_point, null);
			// 设置dialog没有title
			dialogSex.setContentView(viewSex, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			Window window = dialogSex.getWindow();
//			window.setGravity(Gravity.TOP);
			//dialogSex.setCanceledOnTouchOutside(true); 
			dialogSex.setCancelable(true);
			
			// 可以在此设置显示动画
			window.setWindowAnimations(R.style.AnimBottom_Dialog);
			WindowManager.LayoutParams wl = window.getAttributes();
//			wl.x = 0;
//			wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
			// 以下这两句是为了保证按钮可以水平满屏
			wl.width = LayoutParams.WRAP_CONTENT;
			wl.height = LayoutParams.WRAP_CONTENT;
			// 设置显示位置
			dialogSex.onWindowAttributesChanged(wl);
			
			TextView point_title_01 = (TextView) viewSex.findViewById(R.id.point_title_01);
			TextView point_title_02 = (TextView) viewSex.findViewById(R.id.point_title_02);
			if(thz_msg.equals("tz")){
				point_title_01.setText("跳闸");
			}else{
				point_title_01.setText("合闸");
			}
			
		    viewSex.findViewById(R.id.button_cancel).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialogSex.cancel();
					String _data = null;
					if(thz_msg.equals("tz")){
						 _data="HZ_ALL";
					}else{
						 _data="TZ_ALL";
					}
					Message msg  = new Message();
					Bundle data = new Bundle();
					String [] Data=new String[]{"MQTT_CANCLE",_data};
					data.putStringArray("THZ", Data);
					msg.setData(data);	
					TV_Fragment1.this.handler.sendMessage(msg);
				}
			});//取消
		    
			viewSex.findViewById(R.id.button_submit).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialogSex.cancel();
					progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
					progressDialog_1.show();
					new Thread(){
						public void run(){
							try{

								MyOrder odToSend;
								if(thz_msg.equals("hz")){
									switch (str_type) {
									// 智能插座
									case Type_Entity.Wifi_socket: //wifi插座
										// 根据设备类型，获取需要发送的命令
										odToSend = OrderList.getOrderByDeviceType(str_type,
												str_mac, OrderList.ORDER_SOCKET_ON);
										break;  
									case Type_Entity.Socket_type://zigbee插座
										odToSend = OrderList.getSendByDeviceType(str_type,
												str_mac, OrderList.ORDER_SOCKET_ON,"");			
										break;
						
									default:
										odToSend = null;
										break;
									}
								}else{
									switch (str_type) {
									// 智能插座
									case Type_Entity.Wifi_socket: //wifi插座
										// 根据设备类型，获取需要发送的命令
										odToSend = OrderList.getOrderByDeviceType(str_type,
												str_mac, OrderList.ORDER_SOCKET_OFF);
										break;  
									case Type_Entity.Socket_type://zigbee插座
										odToSend = OrderList.getSendByDeviceType(str_type,
												str_mac, OrderList.ORDER_SOCKET_OFF,"");			
										break;
						
									default:
										odToSend = null;
										break;
									}
								}
				
								
							   //发送mqtt推送消息;
								String send_msg = Utils.sendOrder(odToSend);
								PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
										odToSend.getEqpt_type()+"/"+odToSend.getEqpt_id_code(),send_msg);
							}catch(MqttException e){
								String _data = null;
								if(thz_msg.equals("tz")){
									 _data="HZ_ALL";
								}else{
									 _data="TZ_ALL";
								}
								Message msg  = new Message();
								Bundle data = new Bundle();
								String [] Data=new String[]{"MQTT",_data};
								data.putStringArray("THZ", Data);
								msg.setData(data);	
								TV_Fragment1.this.handler.sendMessage(msg);
								e.printStackTrace();
							}
						}
					}.start();
							
				}
			});//确定
			
			
			
			dialogSex.show();
			
			viewSex.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSex.cancel();
					String _data = null;
					if(thz_msg.equals("tz")){
						 _data="HZ_ALL";
					}else{
						 _data="TZ_ALL";
					}
					Message msg  = new Message();
					Bundle data = new Bundle();
					String [] Data=new String[]{"MQTT_CANCLE",_data};
					data.putStringArray("THZ", Data);
					msg.setData(data);	
					TV_Fragment1.this.handler.sendMessage(msg);
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
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
				if(dwcssjk.split(",")[7].equals("00")){
					dialog_item6.setText("合闸");
				}if(dwcssjk.split(",")[7].equals("01")){
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
			im_imageView1=(ImageView) view.findViewById(R.id.img_view_icon);
			view.findViewById(R.id.layout_dialog).setOnClickListener(this);
			tv_item0 = (TextView) view.findViewById(R.id.item0);
			tv_item1 = (TextView) view.findViewById(R.id.item1);
			tv_item3 = (TextView) view.findViewById(R.id.item3);
			
			image0_socket = (ImageView) view.findViewById(R.id.image0_socket);//通讯网络显示
			textview2_socket = (TextView) view.findViewById(R.id.textview2_socket);
			
			init_cp();
			
			/**活动流显示*/
			linear_action_1  = (LinearLayout) view.findViewById(R.id.linear_action_1);
			time_on = (TextView) view.findViewById(R.id.time_on);//开机时间
			time_continue = (TextView) view.findViewById(R.id.time_continue);//工作时长
			time_off = (TextView) view.findViewById(R.id.time_off);//关机时间
			
			activ_img_1 = (ImageView) view.findViewById(R.id.activ_img_1);
			activ_img_2 = (ImageView) view.findViewById(R.id.activ_img_2);
			activ_img_3 = (ImageView) view.findViewById(R.id.activ_img_3);
			activ_img_4 = (ImageView) view.findViewById(R.id.activ_img_4);
			activ_img_5 = (ImageView) view.findViewById(R.id.activ_img_5);
			
			sp = getActivity().getSharedPreferences("socket_workflow", getActivity().MODE_PRIVATE);
			String receive = sp.getString(str_mac, "null");		
			init_workflow(receive);

			linear_action_1.setVisibility(View.VISIBLE);
	
			im_imageView1.setImageResource(R.drawable.img_chazuo_icon);	

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
		Message msg  = new Message();
		Bundle data = new Bundle();
		String _data="连接异常";
		String [] Data=new String[]{"MQTT",_data};
		data.putStringArray("THZ", Data);
		msg.setData(data);	
		TV_Fragment1.this.handler.sendMessage(msg);
	}
	
	/**读取设备通讯质量
     * realtimedata/表类型/表号/get_cq_of_meter
	   realtimedata/类型/采集器表号/get_cq_of_gateway
	*/
	private void get_cq_of_meter(){
		new Thread(){
			public void run(){
				try {
//					MqttV3Service.getInstance().connectionMqttServer(getActivity(),handler_1, Type_Entity.Socket_type+"/"+str_mac,
//							"realtimedata/"+Type_Entity.Socket_type+"/"+str_mac+"/get_cq_of_meter");
//				} catch (com.ibm.micro.client.mqttv3.MqttException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					handleException();
//				}
					//realtimedata/类型/表号/get_cq_of_meter
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
							Type_Entity.Socket_type+"/"+str_mac,
							"realtimedata/"+Type_Entity.Socket_type+"/"+str_mac+"/get_cq_of_meter");	
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handleException();
				} 

			}
		}.start();	
	}
	
	/**读取活动流数据
	    发送：realtimedata/类型/表号/get_socket_readtime_workflow
	    返回: get_socket_readtime_workflow/0a0001aa7k/002014110119/data
	    数据结果：active#none#0h15min#none*/  //common一般  starting up开机  power off关机   standby 待机
	  private void get_socket_readtime_workflow(){
		new Thread(){
			public void run(){
					//Thread.sleep(1800);
//					try {
//						MqttV3Service.getInstance().connectionMqttServer(getActivity(),handler_1, Type_Entity.Socket_type+"/"+str_mac,
//								"realtimedata/"+Type_Entity.Socket_type+"/"+str_mac+"/get_socket_readtime_workflow");
//					} catch (com.ibm.micro.client.mqttv3.MqttException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						handleException();
//					}
					
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
								"0a0001a820/"+str_mac,"realtimedata/0a0001a820/"+str_mac+"/get_socket_readtime_workflow");
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
		//注册接收广播;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter();  
				intentfilter.addAction("Intent.NOTIFY_ZIGBEE");  
				intentfilter.addAction("Intent.UPDATA"); 
				intentfilter.addAction("Intent.SELECT_cq");  
				intentfilter.addAction("Intent.SELETC");
				getActivity().registerReceiver(receiver, intentfilter); 
			}
		}.start();
		
	}
	
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("************************收到广播,这是zigbee界面"+intent.getAction());
			try{
				Message msg=new Message();
				Bundle data=new Bundle();
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "UPDATA"://数据上报时如果是该设备的数据这里也会更新一次数据
						initDataView();
						break;
					case "CONTROL_SUCCESS_HZ"://跳合闸成功
						String data_2="CONTROL_SUCCESS_HZ";
						String [] Data_0=new String[]{SHUAXIN,data_2};
						data.putStringArray("THZ", Data_0);
						msg.setData(data);	
						TV_Fragment1.this.handler.sendMessage(msg);
						break;
					case "CONTROL_SUCCESS_TZ"://跳合闸成功
						String data_="CONTROL_SUCCESS_TZ";
						String [] Data_=new String[]{SHUAXIN,data_};
						data.putStringArray("THZ", Data_);
						msg.setData(data);	
						TV_Fragment1.this.handler.sendMessage(msg);
						break;
					case "TIMEOUT":
						String data_3="TIMEOUT";
						String [] Data_1=new String[]{SHUAXIN,data_3};
						data.putStringArray("THZ", Data_1);
						msg.setData(data);	
						TV_Fragment1.this.handler.sendMessage(msg);
						break;
					case "get_socket_readtime_workflow"://活动流
						String receive= intent.getStringExtra("msg").split("/")[3];
						SharedPreferences sp = getActivity().getSharedPreferences("socket_workflow", getActivity().MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putString(str_mac, receive);
						edit.commit();
						
						init_workflow(receive);
						break;
					
					case "CONTROL"://跳合闸失败
						String data_4=intent.getStringExtra("msg").toString();
						String [] Data_2=new String[]{SHUAXIN,data_4};
						data.putStringArray("THZ", Data_2);
						msg.setData(data);	
						TV_Fragment1.this.handler.sendMessage(msg);
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
		try{
			System.out.println("触发onDestroy--------------------");
			super.onDestroy();
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
					ToastUtil.showToast(getActivity(), "网关不在线", 5000);
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
	

	/**显示活动流数据*/
	private void init_workflow(String receive){
		try{
			if(!receive.equals("null")){
				String[] active_list = receive.split("#");
				switch(active_list[0]){
				case "active":// active活跃 usual一般  starting up开机  power off关机   standby 待机
					activ_img_1.setImageResource(R.drawable.active_on);//开机
					activ_img_2.setImageResource(R.drawable.active_normal);//一般
					activ_img_3.setImageResource(R.drawable.active_activity_p);//活跃
					activ_img_4.setImageResource(R.drawable.active_stand);//待机
					activ_img_5.setImageResource(R.drawable.active_off);//关机 
					break;
				case "usual"://一般
					activ_img_1.setImageResource(R.drawable.active_on);//开机
					activ_img_2.setImageResource(R.drawable.active_normal_p);//一般
					activ_img_3.setImageResource(R.drawable.active_activity);//活跃
					activ_img_4.setImageResource(R.drawable.active_stand);//待机
					activ_img_5.setImageResource(R.drawable.active_off);//关机 
					break;
				case "starting up"://开机
					activ_img_1.setImageResource(R.drawable.active_on_p);//开机
					activ_img_2.setImageResource(R.drawable.active_normal);//一般
					activ_img_3.setImageResource(R.drawable.active_activity);//活跃
					activ_img_4.setImageResource(R.drawable.active_stand);//待机
					activ_img_5.setImageResource(R.drawable.active_off);//关机 
					break;
				case "power off"://关机
					activ_img_1.setImageResource(R.drawable.active_on);//开机
					activ_img_2.setImageResource(R.drawable.active_normal);//一般
					activ_img_3.setImageResource(R.drawable.active_activity);//活跃
					activ_img_4.setImageResource(R.drawable.active_stand);//待机
					activ_img_5.setImageResource(R.drawable.active_off_p);//关机 
					break;
				case "standby"://待机
					activ_img_1.setImageResource(R.drawable.active_on);//开机
					activ_img_2.setImageResource(R.drawable.active_normal);//一般
					activ_img_3.setImageResource(R.drawable.active_activity);//活跃
					activ_img_4.setImageResource(R.drawable.active_stand_p);//待机
					activ_img_5.setImageResource(R.drawable.active_off);//关机 
					break;
				}
				
				time_on.setText(active_list[1]);
				time_continue.setText(active_list[2]);
				time_off.setText(active_list[3]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
}
