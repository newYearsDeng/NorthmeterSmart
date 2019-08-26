package com.northmeter.northmetersmart.fragment;

import java.text.DecimalFormat;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.db.DBDeviceData;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceData;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.ToastUtil;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.MyResultCentral;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.utils.Utils;

import android.R.integer;
import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * 中央空调遥控页面
 */
@SuppressLint("NewApi")
public class Kt_Frag_Center_1 extends Fragment implements OnClickListener {
	private View view;


	private ImageView speed_change,open_down,img_Refresh,imageView_up,imageView_down,img_send;
	private String[] speed={"自动","一级","二级","三级"};
	private int speed_id=0,onoff=1,temputer=20;
	private String str_mac,str_name,str_type;
	private TextView tv_item0,tv_item1,tv_item2,tv_item3,tv_item4,tv_item5,text_item6;
	private TextView textview2_air;//通讯网关字样
	/**设定温度*/
	private TextView txt_Temp;
	/**环境温度*/
	private TextView item_temp_center;
	private CustomProgressDialog progressDialog_1;
	private SharedPreferences sp;
	private Runnable thread,thread_ktxx,runnable_1,runnable_2,runnable_3;
	private String SHUAXIN="SHUXIN";//刷新标示
	private String FSHWBM="FSHWBM";//发送红外标示
	private String hwa_1;
	private ImageView img_speed_low,img_speed_middle,img_speed_high,img_speed_auto;
	private String URL_PATH;
	private ReceiveTool receiver;
	private ImageView image0_air;
	private String dwcssjk;
	private String [] dw_list;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try{
		view = inflater.inflate(R.layout.kt_frag_center, container, false);
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		
		URL_PATH = URL_help.getInstance().getUrl_address();
		init_first();
		initDataView();
		//读取通讯质量
		get_cq_of_meter();
		//get_Center_Data();

		//刷新数据
		img_Refresh.setOnClickListener(new OnClickListener() {
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
								get_cq_of_meter();//读取通讯质量
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
		}
		return view;
	}
	
//*********************************************************************************************************
	Handler handler_1 = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try{
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
							//后台数据块
							get_Center_Data();
						}catch(Exception e){
							e.printStackTrace();
						}
						break;	
					case "get_meter_current_blob_Success"://中央空调数据块读取
//						Toast.makeText(getActivity(), "数据更新完成", Toast.LENGTH_SHORT).show();
						String receive = bundle_msg.split("/")[3];
						if(receive!=null){
							String[] _list = receive.split("#");
							String[] dw_list_curr = new String[_list.length-1];
							for(int i=0;i<dw_list_curr.length;i++){
								dw_list_curr[i] = _list[i+1];
							}
							show_meter_current_blob(false,dw_list_curr);
							dw_list = dw_list_curr;
						}
						break;
					case "get_meter_current_blob_Fail":
						Toast.makeText(getActivity(), "数据更新失败", Toast.LENGTH_SHORT).show();
						break;
					case "Zigbee_dwcssjk":
//						000141.21#电能  1
//						138.2#功率  2
//						027.5#环境温度  3
//						00#开关状态 4
//						2000#温度及模式  5
//						83#风机档位  6
//						00012837#低风速时长  7 
//						00008245#中风速时长 8
//						00038540#高风速时长 9
//						050077.85#剩余金额 10 
//						800C00#状态字 11
//						2016-10-17 11:19:00上报时间
//						161.41,0.0000,26.5,01,2200,03,00014401,00010101,00043750,49612.25,000000,1704071032						
						dwcssjk = bundle_msg.split("/")[3];	
						System.out.println("中央空调电能数据块+"+dwcssjk);
						if(dwcssjk.equals("FAIL")){
							String failMsg = bundle_msg.split("/")[4];//抄表失败时反馈的数据
							Toast.makeText(getActivity(), failMsg, Toast.LENGTH_LONG).show();
							progressDialog_1.dismiss();
							break;
						}
						if(dwcssjk!=null){
							dw_list = dwcssjk.split(",");
							show_meter_current_blob(true,dw_list);
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
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if(getActivity().isFinishing()){
				return;
			}
			
			Bundle data=msg.getData();
			String[] thz=data.getStringArray("THZ");
			
			if(thz[0].equals("KTXX")&thz[1]=="fail"){
				Toast.makeText(getActivity(), "读取失败", Toast.LENGTH_SHORT).show();
				progressDialog_1.dismiss();
				return;
			}
			/*远程开关*/
			if(thz[0].equals("YCKG")){
				switch(thz[1]){
				case "success":
					initDataView();
					progressDialog_1.dismiss();
					Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
					break;
				case "fail":
					if(thz[2]=="HEZHA"|thz[2].equals("HEZHA")){
						onoff=0;
						open_down.setImageResource(getOnOffResource(0));
					}else{
						onoff=1;
						open_down.setImageResource(getOnOffResource(1));
					}
					if(thz.length==4){
						switch(thz[3].toString()){
						case "20":
							Toast.makeText(getActivity(), "操作失败,编程按键未打开", Toast.LENGTH_SHORT).show();
							break;
						case "21":
							Toast.makeText(getActivity(), "操作失败,温度不允许", Toast.LENGTH_SHORT).show();
							break;
						case "22":
							Toast.makeText(getActivity(), "操作失败,时段不允许", Toast.LENGTH_SHORT).show();
							break;
						case "23":
							Toast.makeText(getActivity(), "操作失败,禁止使用", Toast.LENGTH_SHORT).show();
							break;
						case "24":
							Toast.makeText(getActivity(), "操作失败,余额不足", Toast.LENGTH_SHORT).show();
							break;
							default:
								Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
								break;
						}
					}else{
						Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					break;	
				}
				progressDialog_1.dismiss();
			}
			
			/*发送风速档位*/
			if(thz[0].equals("FSDW")){
				switch(thz[1]){
				case "success":
					Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
					break;
				case "fail":
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					break;
				}
				progressDialog_1.dismiss();
			}
			/*设定温度*/
			if(thz[0].equals("SDWD")){
				try{
				switch(thz[1]){
				case "success":
					Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
					progressDialog_1.dismiss();
					break;
				case "fail":
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					progressDialog_1.dismiss();
					break;
				case "star":
					progressDialog_1=CustomProgressDialog.createDialog(getActivity());
				    progressDialog_1.setMessage("设定温度");
					progressDialog_1.show();
					break;
				default:
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					progressDialog_1.dismiss();
					break;					
				}
				/**网络异常*/
				if(thz[0].equals("MQTT")){
					
					sp = getActivity().getSharedPreferences("status", 0);
					Editor editor = sp.edit();
					editor.putString(str_mac,"100");
					editor.commit();
					
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(),"连接不上服务器了-_-", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			
			/*刷新设备时的信息*/
			if(thz[0].equals(SHUAXIN)){
				if(thz[1].equals("SUCCESS")){
					initDataView();
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("FAIL")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("FAIL")){
					Toast toast=Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					progressDialog_1.dismiss();
					return;
				}
				
			}
			//progressDialog_1.dismiss();
				}catch(Exception e){
					progressDialog_1.dismiss();
					e.printStackTrace();
			}
		}
	};

	
	private void show_meter_current_blob(boolean flag,String[] dw_list){
			if(flag){//到设备查询的实时数据
				double data = Double.valueOf(dw_list[1])*1000;
				DecimalFormat df  = new DecimalFormat("###.0");
				tv_item1.setText( df.format(data)+ "w");
			}else{
				tv_item1.setText(Double.valueOf(dw_list[1]) + "w");
			}
			
			
			switch(dw_list[3]){
			case "00":
				tv_item3.setText("开");
				onoff=0;
				open_down.setImageResource(getOnOffResource(0));
				break;
			case "01":
				tv_item3.setText("关");
				onoff=1;
				open_down.setImageResource(getOnOffResource(1));
				break;
			default:
				tv_item3.setText("关");
				onoff=1;
				open_down.setImageResource(getOnOffResource(1));
				break;
			}
		
			switch(dw_list[5]){//风速档位
			case "01":
				getSpeedResource("01");
				break;
			case "02":
				getSpeedResource("02");
				break;
			case "03":
				getSpeedResource("03");
				break;
			case "81":
				getSpeedResource("83");
				break;
			case "82":
				getSpeedResource("83");
				break;
			case "83":
				getSpeedResource("83");
				break;
			default:
				getSpeedResource("other");
				break;
			}	
			if(dw_list[4].length()==4){
				txt_Temp.setText(dw_list[4].substring(0, 2));
			}else{
				txt_Temp.setText(dw_list[4]);
			}
			
			item_temp_center.setText(Double.valueOf(dw_list[2])+"");
		
	}

	
	
	/**刷新数据,读取网关内的数据，http*/
	private void get_nowTime_data(){
		new Thread(){
			public void run(){
				try{
					
					MyOrder odToSend;
					switch (str_type) {
					// 智能插座
					case Type_Entity.Wifi_socket:
						// 根据设备类型，获取需要发送的命令
						odToSend = OrderList.getOrderByDeviceType(str_type,
								str_mac, OrderList.ORDER_SOCKET_READ);
						break;  
					case Type_Entity.Split_air_conditioning://zigbee空调控制器
						odToSend = OrderList.getSendByDeviceType(str_type,
								str_mac, OrderList.READ_ZIGBEE_AIR_SHUJU,"");	
						break;
					case Type_Entity.Central_air_conditioning://zigbee中央空调控制器
						odToSend = OrderList.getSendByDeviceType(str_type,
								str_mac, OrderList.READ_CENTER_DNSJK,"");	
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
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="网络异常";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					msg.setData(data);	
					Kt_Frag_Center_1.this.handler.sendMessage(msg) ;
					e.printStackTrace();
					}
				}
			}.start();
	}
	
	//初始化界面视图
	public void init_first(){
		img_Refresh =(ImageView) view.findViewById(R.id.img_refresh);
		open_down=(ImageView) view.findViewById(R.id.img_onoff);//开关
		open_down.setOnClickListener(this);
		
		img_speed_low = (ImageView) view.findViewById(R.id.img_speed_low);
		img_speed_low.setOnClickListener(this);
		
		img_speed_middle = (ImageView) view.findViewById(R.id.img_speed_middle);
		img_speed_middle.setOnClickListener(this);
		
		img_speed_high = (ImageView) view.findViewById(R.id.img_speed_high);
		img_speed_high.setOnClickListener(this);
		
		img_speed_auto = (ImageView) view.findViewById(R.id.img_speed_auto);
		img_speed_auto.setOnClickListener(this);
		
		imageView_up = (ImageView) view.findViewById(R.id.imageview_up);
		imageView_up.setOnClickListener(this);
		
		imageView_down = (ImageView) view.findViewById(R.id.imageview_down);
		imageView_down.setOnClickListener(this);
		
		txt_Temp = (TextView) view.findViewById(R.id.txt_temp);
		txt_Temp.setOnClickListener(this);
		
		img_send = (ImageView) view.findViewById(R.id.img_send);
		img_send.setOnClickListener(this);
 	}
	
	
	//点击风速图标循环选择风速大小
	private void getSpeedResource(String speeds){
		//String[] speed={"自动","低风","中风","高风"};
		switch(speeds){
		case "01":
			img_speed_low.setImageResource(R.drawable.air_speed_difeng);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			break;
		case "02":
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			break;
		case "03":
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			break;
		case "83":
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self);
			break;
		case "other":
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			break;
		}
	}
	//获取开关对应图标
	private int getOnOffResource(int onoff){
		int imgs = 0;
		switch(onoff){
		case 0:
			imgs=R.drawable.new_air_on;
			break;
		case 1:
			imgs=R.drawable.new_air_off;
			break;
		}
		return imgs;
	}
	
	@Override
	public void onDestroyView() {
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		viewGroup.removeView(view);
		super.onDestroyView();
	}
	
	
	/**打开弹出时初始化数据视图*/
	private void init_dialog_view(){
		try{
			final AlertDialog dialogSex = new AlertDialog.Builder(getActivity()).create();  
			dialogSex.show();  
			Window window = dialogSex.getWindow();  
			window.setContentView(R.layout.dialog_center_message);  
			dialogSex.setCanceledOnTouchOutside(true); 
			dialogSex.setCancelable(true);			
			// 可以在此设置显示动画
			window.setWindowAnimations(R.style.AnimBottom_Dialog);
			
			TextView dialog_item0 = (TextView) window.findViewById(R.id.item0);//电量
			TextView dialog_item1 = (TextView) window.findViewById(R.id.item4);//功率
			TextView dialog_item2 = (TextView) window.findViewById(R.id.item1);//空调低速累积使用时长
			TextView dialog_item3 = (TextView) window.findViewById(R.id.item2);//空调中速累积使用时长
			TextView dialog_item4 = (TextView) window.findViewById(R.id.item3);//空调高速累积使用时长
			TextView dialog_item5 = (TextView) window.findViewById(R.id.item5);//室温
			TextView dialog_item6 = (TextView) window.findViewById(R.id.item6);//跳合闸状态
			TextView dialog_syje = (TextView) window.findViewById(R.id.item_syje);//剩余金额
			TextView dialog_speed = (TextView) window.findViewById(R.id.item_speed);//风速档位
			
//  		000141.21#电能  1
//			138.2#功率  2
//			027.5#环境温度  3
//			00#开关状态 4
//			2000#温度及模式  5
//			83#风机档位  6
//			00012837#低风速时长  7 
//			00008245#中风速时长 8
//			00038540#高风速时长 9
//			050077.85#剩余金额 10 
//			800C00#状态字 11
//			2016-10-17 11:19:00上报时间
//			161.41,0.0000,26.5,01,2200,03,00014401,00010101,00043750,49612.25,000000,1704071032						
			if (dw_list != null) {
				dialog_item0.setText(Double.valueOf(dw_list[0]) + "kWh");
				dialog_item1.setText(Double.valueOf(dw_list[1]) + "W");
				
				//使用时长
				dialog_item2.setText(Double.valueOf(dw_list[6]) + "分钟");
				dialog_item3.setText(Double.valueOf(dw_list[7]) + "分钟");
				dialog_item4.setText(Double.valueOf(dw_list[8]) + "分钟");
				
				//室温
				dialog_item5.setText(Double.valueOf(dw_list[2])+"℃");
				//跳合闸状态					
				if(dw_list[3].equals("00")){
					dialog_item6.setText("开");
				}if(dw_list[3].equals("01")){
					dialog_item6.setText("关");
				}
				
				//剩余金额
				dialog_syje.setText(Double.valueOf(dw_list[9])+"元");
				
				//风机档位
				switch(dw_list[5]){
				case "01":
					dialog_speed.setText("一级");
					break;
				case "02":
					dialog_speed.setText("二级");
					break;
				case "03":
					dialog_speed.setText("三级");
					break;
				default:
					dialog_speed.setText("自动");
					break;
				}	
				
			} else {
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
	
		
	@Override
	public void onClick(View v) {
		try{
		switch(v.getId()){
		case R.id.layout_dialog://弹窗显示设备用电具体信息
			init_dialog_view();
			break;
			
		case R.id.imageview_down://降低温度			
			temputer=Integer.parseInt((String) txt_Temp.getText());
			temputer=temputer-1;
			if(temputer<20){
				temputer=30;
			}
			txt_Temp.setText(temputer+"");
			
			new Thread(){
				public void run(){
					try {
						int send_temp = temputer;
						Thread.sleep(2000);
						if(send_temp == temputer){
							System.out.println("降低设定温度值************"+send_temp);
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] star=new String[]{"SDWD","star"};
							data.putStringArray("THZ", star);
							msg.setData(data);	
							Kt_Frag_Center_1.this.handler.sendMessage(msg);
							String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_WDJMS,txt_Temp.getText().toString()+"00");
							if(rest=="success"|rest.equals("success")){	
								Message msg_1=new Message();
								Bundle data_1=new Bundle();
								String [] dnData=new String[]{"SDWD","success"};
								data_1.putStringArray("THZ", dnData);
								msg_1.setData(data_1);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg_1);
							}else{
								Message msg_2=new Message();
								Bundle data_2=new Bundle();
								String [] dnData=new String[]{"SDWD","fail"};
								data_2.putStringArray("THZ", dnData);
								msg_2.setData(data_2);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg_2);
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			break;
		case R.id.imageview_up://增加温度

			temputer=Integer.parseInt((String) txt_Temp.getText());
			temputer=temputer+1;
			if(temputer>30){
				temputer=20;
			}
			txt_Temp.setText(temputer+"");
			
			new Thread(){
				public void run(){
					try {
						int send_temp = temputer;
						Thread.sleep(2000);
						if(send_temp == temputer){
							System.out.println("增加设定温度值************"+send_temp);
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] star=new String[]{"SDWD","star"};
							data.putStringArray("THZ", star);
							msg.setData(data);	
							Kt_Frag_Center_1.this.handler.sendMessage(msg);
							
							String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_WDJMS,txt_Temp.getText().toString()+"00");
					
							if(rest=="success"|rest.equals("success")){	
								Message msg_1=new Message();
								Bundle data_1=new Bundle();
								String [] dnData=new String[]{"SDWD","success"};
								data_1.putStringArray("THZ", dnData);
								msg_1.setData(data_1);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg_1);
							}else{
								Message msg_2=new Message();
								Bundle data_2=new Bundle();
								String [] dnData=new String[]{"SDWD","fail"};
								data_2.putStringArray("THZ", dnData);
								msg_2.setData(data_2);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg_2);
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						progressDialog_1.dismiss();
					}
				}
			}.start();
			break;
		
		case R.id.img_onoff://开关
			try{
			onoff=onoff+1;
			if(onoff>1){
				onoff=0;
			}
			if(onoff==0){
				Toast.makeText(getActivity(), "开机中,请稍后...", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(), "关机中,请稍后...", Toast.LENGTH_SHORT).show();
			}
			open_down.setImageResource(getOnOffResource(onoff));
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("远程开关");
			progressDialog_1.show();
			new Thread(){
				public void run(){
					Message msg=new Message();
					Bundle data=new Bundle();
					if(onoff==0){
						String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_ON,"");
						if(rest=="success"|rest.equals("success")){
							String rece = readOnce();
							if(rece.equals("success")){
								String [] dnData=new String[]{"YCKG","success"};
								data.putStringArray("THZ", dnData);
								msg.setData(data);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg);
							}
							
						}else{
							String [] dnData=new String[]{"YCKG","fail","TIAOZHA",rest};
							data.putStringArray("THZ", dnData);
							msg.setData(data);	
							Kt_Frag_Center_1.this.handler.sendMessage(msg);
						}
						
					}else{
						String rest = sendMsgByUserCommand(OrderList.SEND_CENTER_OFF,"");
						if(rest=="success"|rest.equals("success")){
							String  rece=readOnce();
							if(rece.equals("success")){
								String [] dnData=new String[]{"YCKG","success"};
								data.putStringArray("THZ", dnData);
								msg.setData(data);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg);
							}else{
								String [] dnData=new String[]{"YCKG","success"};
								data.putStringArray("THZ", dnData);
								msg.setData(data);	
								Kt_Frag_Center_1.this.handler.sendMessage(msg);
							}	
						}else{
							String [] dnData=new String[]{"YCKG","fail","HEZHA",rest};
							data.putStringArray("THZ", dnData);
							msg.setData(data);	
							Kt_Frag_Center_1.this.handler.sendMessage(msg);
						}
					}
				}
			}.start();
			}catch(Exception e){
				e.printStackTrace();
				progressDialog_1.dismiss();
			}
			break;
		case R.id.img_speed_low://低风速
			img_speed_low.setImageResource(R.drawable.air_speed_difeng);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("调节风速");
			progressDialog_1.show();
			new Thread(){
				public void run(){
					String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_SPEED,"01");
					if(rest=="success"|rest.equals("success")){
						Message msg=new Message();
						Bundle data=new Bundle(); 
						String [] dnData=new String[]{"FSDW","success"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}else{
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","fail"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}
				}
			}.start();
			
			break;
		case R.id.img_speed_middle://中风速
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("调节风速");
			progressDialog_1.show();
			new Thread(){
				public void run(){
					String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_SPEED,"02");
					if(rest=="success"|rest.equals("success")){
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","success"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}else{
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","fail"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}
				}
			}.start();
			
			break;
		case R.id.img_speed_high://高风速
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng);
			img_speed_auto.setImageResource(R.drawable.air_self_p);
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("调节风速");
			progressDialog_1.show();
			new Thread(){
				public void run(){
					String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_SPEED,"03");
					if(rest=="success"|rest.equals("success")){
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","success"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}else{
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","fail"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}
				}
			}.start();
			
			break;
		case R.id.img_speed_auto://自动风速
			img_speed_low.setImageResource(R.drawable.air_speed_difeng_p);
			img_speed_middle.setImageResource(R.drawable.air_speed_zhongfeng_p);
			img_speed_high.setImageResource(R.drawable.air_speed_gaofeng_p);
			img_speed_auto.setImageResource(R.drawable.air_self);
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("调节风速");
			progressDialog_1.show();
			new Thread(){
				public void run(){
					String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_SPEED,"83");
					if(rest=="success"|rest.equals("success")){
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","success"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}else{
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"FSDW","fail"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}
				}
			}.start();
			
			break;
			
		case R.id.img_send:
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("正在设定温度");
			progressDialog_1.show();
			new Thread(){
				public void run(){
					String rest=sendMsgByUserCommand(OrderList.SEND_CENTER_WDJMS,txt_Temp.getText().toString()+"00");
					Message msg=new Message();
					Bundle data=new Bundle();
					if(rest=="success"|rest.equals("success")){					
						String [] dnData=new String[]{"SDWD","success"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}else{
						String [] dnData=new String[]{"SDWD","fail"};
						data.putStringArray("THZ", dnData);
						msg.setData(data);	
						Kt_Frag_Center_1.this.handler.sendMessage(msg);
					}
					
				}
			}.start();
			break;
			
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	
	/**zdn->zdn, dy->sdwdjms, dl->fsdw, gl->gl, wg->hjwd, pl->Low_speed_time/Low_speed_time/Low_speed_time , glys->ysje/ztz , thzzt ->kgzt*/
	/**进入界面时初始化数据*/
	private void initDataView() {
		
		view.findViewById(R.id.layout_dialog).setOnClickListener(this);//弹窗具体用电信息
		//tv_item0 = (TextView) view.findViewById(R.id.item0_air);
		tv_item1 = (TextView) view.findViewById(R.id.item1_air);
		tv_item2 = (TextView) view.findViewById(R.id.item2);
		tv_item3 = (TextView) view.findViewById(R.id.item3_air);//跳合闸状态
		tv_item4 = (TextView) view.findViewById(R.id.item4);
		tv_item5 = (TextView) view.findViewById(R.id.item5);
		text_item6 = (TextView) view.findViewById(R.id.item6);
		txt_Temp = (TextView) view.findViewById(R.id.txt_temp);//设定温度及模式
		item_temp_center = (TextView) view.findViewById(R.id.item_temp_center);//环境温度
		textview2_air = (TextView) view.findViewById(R.id.textview2_air);
		
		image0_air = (ImageView) view.findViewById(R.id.image0_air);//通讯质量显示
		//设备通讯质量
		init_cp();

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
	
	private String readOnce() {
		String src = null;
		try{
		DBDevice db_d = new DBDevice(getActivity());
		List<MyDevice> devices = db_d.GetMyDevices();
		MyDevice md = null;
		for (int i = 0; i < devices.size(); i++) {
			md = devices.get(i);
			if (md.getMac().equals(str_mac))
				break;
		}
		
		// =================================================
		// 发送命令并得到返回结果，此处应该是读取
		String rst_raw = OrderManager.getInstance().sendOrder(
				OrderList.getSendByDeviceType(
						Type_Entity.Central_air_conditioning, str_mac,
						OrderList.READ_CENTER_DNSJK,""), OrderList.USER_PASSWORD,
				URL_PATH, "utf-8");
		
		if (!rst_raw.equals(null)) {
			String rst_check=OrderManager.getInstance().getItemByOrder(rst_raw,
					"status", -1);
			if(rst_check.equals("200")){
				// 获取当前时间
				Time t = new Time();
				t.setToNow();
				String timeNow = String.valueOf(t.year) + "年"
						+ toDoubleDate(t.month + 1) + "月" + toDoubleDate(t.monthDay) + "日"
						+ toDoubleDate(t.hour) + "时" + toDoubleDate(t.minute) + "分" + toDoubleDate(t.second) + "秒"; // month是从0开始计算的
	
				// 解析返回的结果
				String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
						"result", -1);
				System.out.println("rst======="+rst);
				if(!rst.equals("")){
					System.out.println("rst======="+rst);
					
					//有功总电能，电压，电流，总有功功率，无功功率（ff代替），电网频率，功率因素，空调开关状态（00-开，01-关）
					String [] sjk_list = rst.split(",");
					
					
					
					
					// 更新数据库
					MyDeviceData mdd = null;
					DBDeviceData db_dd = new DBDeviceData(getActivity());
					List<MyDeviceData> devicesData = db_dd.GetMyDeviceData();
					if (!devicesData.isEmpty()) {
						for (int i = 0; i < devicesData.size(); i++) {
							if (devicesData.get(i).getMac().equals(str_mac)) {
								mdd = devicesData.get(i);
								break;
							}
						}
					}
					float gl = Float.parseFloat(sjk_list[1])*1000;
					DecimalFormat df = new DecimalFormat("#.00"); 
					MyDeviceData myDeviceData = new MyDeviceData(str_mac,timeNow,
							"on", sjk_list[0], sjk_list[4],
							sjk_list[5], df.format(gl), sjk_list[2],
							sjk_list[6]+"/"+sjk_list[7]+"/"+sjk_list[8],
							sjk_list[9]+"/"+sjk_list[10], sjk_list[3]);
					
					if (mdd != null) {
						db_dd.Update(myDeviceData);
					} else {
						db_dd.Insert(myDeviceData);
					}
					
				    src= "success";
				}
			}
		}   
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return src;
	}
	
	
	
		//发送设置的参数
		public String sendMsgByUserCommand(int order_type,String cmd_data){
			String rst_rce = null;
			try{
			
			// =================================================
			// 发送命令并得到返回结果
			String rst_raw = OrderManager.getInstance().sendOrder(
					OrderList.getSendByDeviceType(
							Type_Entity.Central_air_conditioning,
							str_mac,order_type,cmd_data),
							OrderList.USER_PASSWORD, URL_PATH, "utf-8");
			if(rst_raw==null){ 
				return "fail";
			}else{
				String status = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
				if(status.equals("200")){
					rst_rce = "success";// OrderManager.getInstance().getItemByOrder(rst_raw,"result", -1);
				}else{
					return "fail";
				}
			}

			}catch(Exception e){
				e.printStackTrace();
				return "fail";
			}
			System.out.println("开机结果：：："+rst_rce);
			return rst_rce.toLowerCase();
		}
				
		
		private String getReverseHwm(String hwm){
			String res_hwm ="";
			try{
				for (int i = hwm.length()/2; i > 0; i--)
		        {
				 res_hwm=res_hwm+hwm.substring(i * 2-2, i * 2);       	   
		        }
			}catch(Exception e){
				e.printStackTrace();
			}
			return res_hwm;
		}
		 /**handle异常*/
		 public void handleException(){
				Message msg  = new Message();
				Bundle data = new Bundle();
				String _data="网络异常";
				String [] Data=new String[]{"MQTT",_data};
				data.putStringArray("THZ", Data);
				msg.setData(data);	
				Kt_Frag_Center_1.this.handler.sendMessage(msg);
			}
		
		
		/**读取中央空调mqtt端 用电数据块*/
		private void get_Center_Data(){
			new Thread(){
				public void run(){
					try {
						//realtimedata/类型/表号/get_meter_current_blob
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
								Type_Entity.Central_air_conditioning+"/"+str_mac,
								"realtimedata/"+Type_Entity.Central_air_conditioning+"/"+str_mac+"/get_meter_current_blob");	
						
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handleException();
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
								Type_Entity.Central_air_conditioning+"/"+str_mac,
								"realtimedata/"+Type_Entity.Central_air_conditioning+"/"+str_mac+"/get_cq_of_meter");	
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handleException();
					}

				}
			}.start();	
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			try{
				if(progressDialog_1 != null) {  
			    	progressDialog_1.dismiss();  
			    }  
				getActivity().unregisterReceiver(receiver);
			}catch(Exception e){
				e.printStackTrace();
				}
		}
		
		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			try{
				init_first();
				initDataView();
				//注册接收广播
				RegisterBroad();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		/**注册接收广播*/
		public void RegisterBroad(){
			//界面更新广播接收;
			new Thread(){
				public void run(){
					 receiver = new ReceiveTool();
					 IntentFilter intentfilter = new IntentFilter();  
					 intentfilter.addAction("Intent.UPDATA");  
					 intentfilter.addAction("Intent.SELECT_CENTER"); 
					 intentfilter.addAction("Intent.SELECT_cq");  
					 getActivity().registerReceiver(receiver, intentfilter); 
									
				}
			}.start();	
		}
		
	class ReceiveTool extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("收到数据============="+intent.getStringExtra("msg"));
			// TODO Auto-generated method stub
			try{
				Message msg=new Message();
				Bundle data=new Bundle();
//				000141.21#电能  1
//				138.2#功率  2
//				027.5#环境温度  3
//				00#开关状态 4
//				2000#温度及模式  5
//				83#风机档位  6
//				00012837#低风速时长  7 
//				00008245#中风速时长 8
//				00038540#高风速时长 9
//				050077.85#剩余金额 10 
//				800C00#状态字 11
//				2016-10-17 11:19:00上报时间
				//receive.split("/")[1]+"/"+receive.split("/")[2]+"/CENTER_UPDATA/"+last_cent_msg
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "CENTER_UPDATA"://中央空调的上报数据
						MyResultCentral myResult = new MyResultCentral(intent.getStringExtra("msg").split("/")[3]);
						String [] list = new String[12];
						list[0] = myResult.getZdn();
						list[1] = myResult.getGl();
						list[2] = myResult.getHjwd();
						list[3] = myResult.getKgzt();
						list[4] = myResult.getSdwdjms();
						list[5] = myResult.getFsdw();
						list[6] = myResult.getLow_speed_time();
						list[7] = myResult.getMiddle_speed_time();
						list[8] = myResult.getHigh_speed_time();
						list[9] = myResult.getSyje();
						list[10] = myResult.getZtz();
						list[11] = myResult.getSj();
						dw_list = list;
						show_meter_current_blob(false,dw_list);
						break;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
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
						textview2_air.setTextColor(Color.WHITE);
			            int cancellation = Integer.parseInt(cancellation_list[1]);
			            if(cancellation==0){
			            	 image0_air.setImageResource(R.drawable.signal_0);
			            	 textview2_air.setTextColor(Color.RED);
			            }else if(cancellation>0&&cancellation<20){
			            	 image0_air.setImageResource(R.drawable.signal_1);
			            }else if(cancellation>19&&cancellation<40){
			            	 image0_air.setImageResource(R.drawable.signal_2);
			            }else if(cancellation>39&&cancellation<60){
			            	 image0_air.setImageResource(R.drawable.signal_3);
			            }else if(cancellation>59&&cancellation<80){
			            	 image0_air.setImageResource(R.drawable.signal_4);
			            }else if(cancellation>79&&cancellation<=100){
			            	 image0_air.setImageResource(R.drawable.signal_5);
			            }else{
			            	 textview2_air.setTextColor(Color.RED);
			            	 image0_air.setImageResource(R.drawable.signal_0);
			            }
		            }else{			            	
		            	LayoutInflater inflater = getActivity().getLayoutInflater();
						View layout = inflater.inflate(R.layout.toast_layout,
						(ViewGroup) view.findViewById(R.id.toast_layout));
						TextView toast_text = (TextView) layout.findViewById(R.id.toast_text);
						toast_text.setText("网关不在线");
						Toast toast = new Toast(getActivity());
						toast.setView(layout);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						
		            	if(progressDialog_1!=null){
							progressDialog_1.dismiss();
						}	
		            }
				}else{	
	            	image0_air.setImageResource(R.drawable.signal_0);
	            	textview2_air.setTextColor(Color.RED);
	            	
					if(progressDialog_1!=null){
						progressDialog_1.dismiss();
					}
	            }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
}

