package com.northmeter.northmetersmart.fragment;

import java.text.DecimalFormat;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.control.AuxHW;
import com.northmeter.northmetersmart.control.GreeFrequency;
import com.northmeter.northmetersmart.control.GreeFrequencyComplement;
import com.northmeter.northmetersmart.control.GreeKTHW;
import com.northmeter.northmetersmart.control.HuaLingHW;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.ToastUtil;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.utils.Utils;

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
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
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
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * 格力空调遥控页面
 */
@SuppressLint("NewApi")
public class Kt_Frag1 extends Fragment implements OnClickListener {
	private View view;
	private int temputer=26;
	private ImageView model_change,speed_change,open_down,img_Refresh,air_left_right,air_up_down,img_send;
	private CheckBox checkBox_upDown,checkBox_leftRight,checkBox1;
	private String[] model={"制冷","自动","抽湿","送风","制热"};
	private String[] speed={"自动","一级","二级","三级"};
	private String[] leftRightList = {"左右","左右关"};
	private String[] upDownList = {"上下","上下关"};
 	private int model_id=0,speed_id=0,onoff=1,leftright=0,updown=0;
	private String upDown,leftRight;
	private String str_mac,str_name,str_type,str_ver;
	private TextView tv_item0,tv_item1,textView_temp,txt_temp;
	private boolean SOCKET_STATE_HEZHA = true;
	private boolean SOCKET_STATE_TIAOZHA = false;
	private CustomProgressDialog progressDialog_1;
	private SharedPreferences sp;
	private Runnable thread,runnable_1,runnable_2,runnable_3;
	private String SHUAXIN="SHUXIN";//刷新标示
	private String FSHWBM="FSHWBM";//发送红外标示
	private String hwa_1;
	private String URL_PATH;
	private ReceiveTool receiver;
	private LinearLayout layout_dialog;
	private ImageView image0_air;//通讯网关显示
	private TextView textview2_air;//通讯网关字样
	
	private LinearLayout linearlayout_air0,linearlayout_air1,linearlayout_air2,linearlayout_air3;
	
	private String dwcssjk;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try{
			view = inflater.inflate(R.layout.f_yaokong_kongtiao, container, false);
			
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			
			if(str_name.indexOf("格力定频")>=0){
				str_ver = "0003";
			}
			if(str_name.indexOf("格力变频")>=0){
				str_ver = "0003_1";
			}
			if(str_name.indexOf("华凌")>=0){
				str_ver = "0003_2";
			}
			if(str_name.indexOf("奥克斯")>=0||str_name.indexOf("AUX")>=0){
                str_ver = "0003_3";
			}
			
			URL_PATH = URL_help.getInstance().getUrl_address();
			//注册接收广播
			//RegisterBroad();
			initDataView();
			init_first();
			//通讯质量
			get_cq_of_meter();
			
			
		
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
	
	//----------------------- 
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if(getActivity().isFinishing()){
				return;
			}
			
			Bundle data=msg.getData();
			String[] thz=data.getStringArray("THZ");		
			
			//环境温度
			if(thz[0].equals("HJWD")){
				textView_temp.setText(thz[1]);
				return;
			}
			
			//发送红外
			if(thz[0].equals("FSHWBM")&&thz[1].equals("FAIL")){
				Toast.makeText(getActivity(), "发送失败", Toast.LENGTH_SHORT).show();
				upDown="上下关";
				leftRight="左右关";
				checkBox_upDown.setChecked(false);
				checkBox_leftRight.setChecked(false);
			}
			if(thz[0].equals("FSHWBM")&&thz[1].equals("SUCCESS")){	
				SharedPreferences sp=getActivity().getSharedPreferences("LastHwZl_gree", 0);
				Editor editor=sp.edit();
				editor.putString(str_mac, txt_temp.getText()+","+model_id+","+speed_id+","+leftright+","+updown);
				editor.commit();
				Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT).show();
			}
			if(thz.length==8&&thz[1].equals("SUCCESS")){
			try{		
				tv_item0.setText(Double.valueOf(thz[2]) + "kWh");
				tv_item1.setText(Double.valueOf(thz[3]) + "W");
				}catch(Exception e){
					e.printStackTrace();
					}
				}
			
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
				progressDialog_1.dismiss();
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

			if(thz[0].equals(SHUAXIN)){
				if(thz[1].equals("SUCCESS")){
					get_cq_of_meter();//通讯质量
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
				}else if(thz[1].equals("CONTROL_SUCCESS_HZ")){
					checkBox1.setChecked(SOCKET_STATE_HEZHA);
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				else if(thz[1].equals("CONTROL_SUCCESS_TZ")){
					checkBox1.setChecked(SOCKET_STATE_TIAOZHA);
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				else if(thz[1].equals("TIMEOUT")){
					initDataView();
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(),"连接服务器超时", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("HWFS_SUCCESS")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}else if(thz[1].equals("HWFS_FAIL")){
					progressDialog_1.dismiss();
					Toast toast=Toast.makeText(getActivity(), "操作失败！", Toast.LENGTH_SHORT);
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
//						refashTHZZT();
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

	//-----------------------------------------------------------------------------------------------	
	
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
								get_nowTime_data();
							}catch(Exception e){
								e.printStackTrace();
							}
							break;	
							
						case "Zigbee_dwcssjk":
							read_hjwd();//读取环境温度
							dwcssjk = bundle_msg.split("/")[3];	
							if(dwcssjk.equals("FAIL")){
								String failMsg = bundle_msg.split("/")[4];//抄表失败时反馈的数据
								Toast.makeText(getActivity(), failMsg, Toast.LENGTH_LONG).show();
								progressDialog_1.dismiss();
								break;
							}
							
							if (dwcssjk != null) {
                                float gl = 0;
							    if(isNumber(dwcssjk.split(",")[3])){
                                    gl = Float.parseFloat(dwcssjk.split(",")[3])*1000;
                                }
								DecimalFormat df = new DecimalFormat("#.00");
                                tv_item0.setText(dwcssjk.split(",")[0] + "kWh");
								tv_item1.setText(df.format(gl) + "W");
								if(Double.valueOf(df.format(gl))>5){
									onoff = 0;
								}else{
									onoff = 1;
								}
								open_down.setImageResource(getOnOffResource(onoff));
								String wg = dwcssjk.split(",")[4];
								
								//textView_temp.setText(wg +"");
								if(dwcssjk.split(",")[7].equals("00")){
									checkBox1.setChecked(true);
									linearlayout_air0.setVisibility(View.GONE);
									linearlayout_air1.setVisibility(View.VISIBLE);
									linearlayout_air2.setVisibility(View.VISIBLE);
									linearlayout_air3.setVisibility(View.VISIBLE);
								}if(dwcssjk.split(",")[7].equals("01")){
									checkBox1.setChecked(false);
									linearlayout_air0.setVisibility(View.VISIBLE);
									linearlayout_air1.setVisibility(View.VISIBLE);
									linearlayout_air2.setVisibility(View.GONE);
									linearlayout_air3.setVisibility(View.GONE);
								}
								
							} else {
								tv_item0.setText("0");
								textView_temp.setText("--");
								}	
							if(progressDialog_1.isShowing()){
								progressDialog_1.dismiss();
							}
							Toast toast=Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
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
					// 智能插座
					case Type_Entity.Wifi_socket:
						// 根据设备类型，获取需要发送的命令
						odToSend = OrderList.getOrderByDeviceType(str_type,
								str_mac, OrderList.ORDER_SOCKET_READ);
						break;  
					case Type_Entity.Split_air_conditioning://zigbee空调控制器
						odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
								str_mac, OrderList.READ_ZIGBEE_AIR_SHUJU,"","","");	
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
					System.out.println("发送信息----");
					msg.setData(data);	
					Kt_Frag1.this.handler.sendMessage(msg) ;
					e.printStackTrace();
					}
				    }
				}.start();
			
		}
	
	
	/**读取环境温度*/
	private void read_hjwd(){
		new Thread(){
			public void run(){
				try{
					String timeNow = null;
					MyOrder odToSend;
					switch (str_type) {
					case Type_Entity.Split_air_conditioning://zigbee空调控制器环境温度
						odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
								str_mac, OrderList.ZIGBEE_AIR_HJWD,"","","");	
						break;

					default:
						odToSend = null;
						break;
					}
					// 发送命令并得到返回的结果
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
							OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
					System.out.println("************hjwd"+rst_raw);
					if (rst_raw != null){
						// 解析返回的结果
						String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1);  
						if(!rst.equals("200")){
							return;
						}else{
							String rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
									"result", -1);
							Message msg=new Message();
							Bundle data=new Bundle();
							String [] dnData=new String[]{"HJWD",rst_rce};
							data.putStringArray("THZ", dnData);
							System.out.println("发送信息----");
							msg.setData(data);	
							Kt_Frag1.this.handler.sendMessage(msg) ;
						}
						
					}
					
												
				}catch(Exception e){
					Message msg=new Message();
					Bundle data=new Bundle();
					String return_data="网络异常";
					String [] dnData=new String[]{"MQTT",return_data};
					data.putStringArray("THZ", dnData);
					System.out.println("发送信息----");
					msg.setData(data);	
					Kt_Frag1.this.handler.sendMessage(msg) ;
					e.printStackTrace();
					}
				    }
		}.start();
	}
	
	/**设置发送按钮的抖动*/
	private void setAnim(){
		img_send.setImageResource(R.drawable.air_sending);
		TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);  
        animation.setInterpolator(new OvershootInterpolator());  
        animation.setDuration(50);  
        animation.setRepeatCount(20);  
        animation.setRepeatMode(Animation.REVERSE);  
        img_send.startAnimation(animation); 
	}
	
	//初始化界面视图
	public void init_first(){
		layout_dialog = (LinearLayout) view.findViewById(R.id.layout_dialog);
		layout_dialog.setOnClickListener(this);
		
		view.findViewById(R.id.imageview1).setOnClickListener(this);//温度降低
		view.findViewById(R.id.imageview2).setOnClickListener(this);//温度增加
		img_send = (ImageView) view.findViewById(R.id.img_send);//发送
		img_send.setOnClickListener(this);
		
		img_Refresh =(ImageView) view.findViewById(R.id.img_refresh);//刷新
		
		model_change=(ImageView) view.findViewById(R.id.img_model_change);//模式
		model_change.setOnClickListener(this);
		
		speed_change=(ImageView) view.findViewById(R.id.img_speed_change);//风速
		speed_change.setOnClickListener(this);
		
		open_down=(ImageView) view.findViewById(R.id.img_onoff);//开关
		open_down.setOnClickListener(this);
		
		air_left_right = (ImageView) view.findViewById(R.id.air_left_right);//左右扫风
		air_left_right.setOnClickListener(this);
		
		air_up_down = (ImageView) view.findViewById(R.id.air_up_down);//上下扫风
		air_up_down.setOnClickListener(this);
		
		open_down.setImageResource(getOnOffResource(onoff));
		
		SharedPreferences sp = getActivity().getSharedPreferences("LastHwZl_gree", 0);
		String lastZl = sp.getString(str_mac, 26+","+0+","+0+","+0+","+0);
		String [] lastHwmZl=lastZl.split(",");
		txt_temp.setText(lastHwmZl[0]);
		model_change.setImageResource(getModelResource(model[Integer.parseInt(lastHwmZl[1])]));
		speed_change.setImageResource(getSpeedResource(speed[Integer.parseInt(lastHwmZl[2])]));
		air_left_right.setImageResource(getLeftRightResource(Integer.parseInt(lastHwmZl[3])));
		air_up_down.setImageResource(getUpDownResource(Integer.parseInt(lastHwmZl[4])));
		
		model_id = Integer.parseInt(lastHwmZl[1]);
		speed_id = Integer.parseInt(lastHwmZl[2]);
		leftright= Integer.parseInt(lastHwmZl[3]);
		updown=Integer.parseInt(lastHwmZl[4]);
	}
	
	//点击模式图标循环选择模式
	private int getModelResource(String models){
		//String[] model={"自动","制冷","抽湿","送风","制热"};
		int img = 0;
		switch(models){
		case "自动":
			img=R.drawable.air_self_style;
			break;
		case "制冷":
			img=R.drawable.air_model_cold_style;
			break;
		case "抽湿":
			img=R.drawable.air_model_chushi_style;
			break;
		case "送风":
			img=R.drawable.air_model_songfeng_style;
			break;
		case "制热":
			img=R.drawable.air_model_hot_style;
			break;
		}
		return img;
	}
	
	//点击风速图标循环选择风速大小
	private int getSpeedResource(String speeds){
		//String[] speed={"自动","低风","中风","高风"};
		int img = 0;
		switch(speeds){
		case "自动":
			img= R.drawable.air_self_style;
			break;
		case "一级":
			img= R.drawable.air_speed_difeng_style;
			break;
		case "二级":
			img= R.drawable.air_speed_zhongfeng_style;
			break;
		case "三级":
			img= R.drawable.air_speed_gaofeng_style;
			break;
		}
		return img;
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
	//获取左右扫风对应图标
	private int getLeftRightResource(int onoff){
		int imgs = 0;
		switch(onoff){
		case 0:
			imgs=R.drawable.air_leftright;
			break;
		case 1:
			imgs=R.drawable.air_leftright_p;
			break;
		}
		return imgs;
	}
	//获取上下扫风对应图标
	private int getUpDownResource(int onoff){
		int imgs = 0;
		switch(onoff){
		case 0:
			imgs=R.drawable.air_updown;
			break;
		case 1:
			imgs=R.drawable.air_updown_p;
			break;
		}
		return imgs;
	}
	
//	@Override
//	public void onDestroyView() {
//		ViewGroup viewGroup = (ViewGroup) view.getParent();
//		viewGroup.removeView(view);
//		super.onDestroyView();
//	}
	/**打开弹出时初始化数据视图*/
	private void init_dialog_view(){
		try{
			final AlertDialog dialogSex = new AlertDialog.Builder(getActivity()).create();  
			dialogSex.show();  
			Window window = dialogSex.getWindow();  
			window.setContentView(R.layout.dialog_air_message);  
			
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
			TextView dialog_temp  = (TextView) window.findViewById(R.id.item_temp);
			
			if (dwcssjk != null) {
                float gl = 0;
			    if(isNumber(dwcssjk.split(",")[3])){
                    gl = Float.parseFloat(dwcssjk.split(",")[3])*1000;
                }
				DecimalFormat df = new DecimalFormat("#.00");
				dialog_item0.setText(dwcssjk.split(",")[0] + "kWh");
				dialog_item1.setText(df.format(gl) + "W");
				dialog_item2.setText(dwcssjk.split(",")[1] + "V");
				dialog_item3.setText(dwcssjk.split(",")[2] + "A");
				dialog_item4.setText(dwcssjk.split(",")[6] + "");
				dialog_item5.setText(dwcssjk.split(",")[5]+"Hz");
				String wg = dwcssjk.split(",")[4];
				dialog_temp.setText(textView_temp.getText());
				if(dwcssjk.split(",")[7].equals("00")){
					dialog_item6.setText("合闸");
				}if(dwcssjk.split(",")[7].equals("01")){
					dialog_item6.setText("跳闸");
				}
				
			}else {
				dialog_item0.setText("0");
				dialog_item1.setText("0");
				dialog_item2.setText("0");
				dialog_item3.setText("0");
				dialog_item4.setText("0");
				dialog_item5.setText("0");
				dialog_temp.setText("0");
				dialog_item6.setText("未知");	
				}
			
			
			dialogSex.show();
			
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
		
		case R.id.img_onoff://开关
			onoff=onoff+1;
			String open_close_toast = null;
			if(onoff>1){
				onoff=0;
			}
			if(onoff==0){
				Toast.makeText(getActivity(), "开机", Toast.LENGTH_SHORT).show();
				open_close_toast = "开机";
			}else{
				Toast.makeText(getActivity(), "关机", Toast.LENGTH_SHORT).show();
				open_close_toast = "关机";
			}
			open_down.setImageResource(getOnOffResource(onoff));
			
			temputer=Integer.parseInt((String) txt_temp.getText());
			String kaiguan_1="开";
			if(onoff==0){
				kaiguan_1="开";
			}else{
				kaiguan_1="关";
			}
			
			leftRight = leftRightList[leftright];
			upDown = upDownList[updown];
			
			SharedPreferences sp_1=getActivity().getSharedPreferences("LastHwZl_gree", 0);
			Editor editor_1=sp_1.edit();
			editor_1.putString(str_mac, txt_temp.getText()+","+model_id+","+speed_id+","+updown+","+leftright);
			editor_1.commit();
			
			String hwm_zl_1=kaiguan_1+","+model[model_id]+","+temputer+","+speed[speed_id]+","+upDown+","+leftRight;
			if(str_ver.equals("0003")){
				hwa_1 =get_reverse_String(GreeKTHW.getKTHWM("GREE",hwm_zl_1));
				System.out.println("hwa_1: "+hwa_1);
			}else if(str_ver.equals("0003_1")){
				if(model[model_id].equals("制冷")||model[model_id].equals("送风")){
					hwa_1 = GreeFrequency.getGreeFrequency(hwm_zl_1);
				}else{
					hwa_1 = GreeFrequencyComplement.getGreeFrequencyComplement(hwm_zl_1);
				}
				System.out.println("hwa_1变频: "+hwa_1);
			}else if(str_ver.equals("0003_2")){
				hwa_1 =HuaLingHW.getHuaLinHw(hwm_zl_1);
				System.out.println("hwa_1: "+hwa_1);
			}else if(str_ver.equals("0003_3")){
                hwa_1 = new AuxHW().getControlAux(hwm_zl_1);
            }
			/*发送红外编码--------------------------------------------------------*/
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage(open_close_toast);
			progressDialog_1.show();
			try{
			 new Thread(){
				public void run(){			
				//=================================================
				MyOrder odToSend;
				switch (str_type) {  
				case Type_Entity.Split_air_conditioning://zigbee空调控制器
					odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
							str_mac, OrderList.SEND_ZIGBEE_AIR_HWM,"","",hwa_1);	
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
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg  = new Message();
					Bundle data = new Bundle();
					String _data="网络异常";
					String [] Data=new String[]{"MQTT",_data};
					data.putStringArray("THZ", Data);
					msg.setData(data);	
					Kt_Frag1.this.handler.sendMessage(msg);
					e.printStackTrace();
					}
											
				}
			}.start();
			}catch(Exception e){
				progressDialog_1.dismiss();
				e.printStackTrace();
				}
			break;
		case R.id.img_model_change://模式
			model_id=model_id+1;
			if(model_id>4){
				model_id=0;
			}
			System.out.println(model_id);
			model_change.setImageResource(getModelResource(model[model_id]));
			
			setAnim();
			break;
		case R.id.img_speed_change://风速
			speed_id=speed_id+1;
			if(speed_id>3){
				speed_id=0;
			}
			speed_change.setImageResource(getSpeedResource(speed[speed_id]));

			setAnim();
			break;
			
		case R.id.imageview1://降低温度
			temputer=Integer.parseInt((String) txt_temp.getText());
			temputer=temputer-1;
			if(temputer<16){
				temputer=30;
			}
			txt_temp.setText(temputer+"");

			setAnim();
			break;
		case R.id.imageview2://增加温度
			temputer=Integer.parseInt((String) txt_temp.getText());
			temputer=temputer+1;
			if(temputer>30){
				temputer=16;
			}
			txt_temp.setText(temputer+"");

			setAnim();
			break;
		case R.id.air_left_right://左右扫风
			leftright = leftright+1;
			if(leftright>1){
				leftright=0;
			}
			//leftRight = leftRightList[leftright];
			air_left_right.setImageResource(getLeftRightResource(leftright));
			
			setAnim();
			break;
			
		case R.id.air_up_down://上下扫风
			updown = updown+1;
			if(updown>1){
				updown=0;
			}
			//upDown = upDownList[updown];
			air_up_down.setImageResource(getUpDownResource(updown));

			setAnim();
			break;
				
		case R.id.img_send://发送按钮
			img_send.setImageResource(R.drawable.air_send_style);
			temputer=Integer.parseInt((String) txt_temp.getText());
			String kaiguan="开";
			if(onoff==0){
				kaiguan="开";
			}else{
				kaiguan="关";
			}
			
			leftRight = leftRightList[leftright];
			upDown = upDownList[updown];
			
			SharedPreferences sp=getActivity().getSharedPreferences("LastHwZl_gree", 0);
			Editor editor=sp.edit();
			editor.putString(str_mac, txt_temp.getText()+","+model_id+","+speed_id+","+updown+","+leftright);
			editor.commit();
			
			System.out.println(leftRight+"//"+upDown);
			
			String hwm_zl=kaiguan+","+model[model_id]+","+temputer+","+speed[speed_id]+","+upDown+","+leftRight;
			if(str_ver.equals("0003")){
				hwa_1 =get_reverse_String(GreeKTHW.getKTHWM("GREE",hwm_zl));
				System.out.println("hwa_1: "+hwa_1);
			}else if(str_ver.equals("0003_1")){
				if(model[model_id].equals("制冷")||model[model_id].equals("送风")){
					hwa_1 = GreeFrequency.getGreeFrequency(hwm_zl);
				}else{
					hwa_1 = GreeFrequencyComplement.getGreeFrequencyComplement(hwm_zl);
				}
				System.out.println("hwa_1变频: "+hwa_1);
			}else if(str_ver.equals("0003_2")){
				hwa_1 = HuaLingHW.getHuaLinHw(hwm_zl);
				System.out.println("hwa_1: "+hwa_1);
			}else if(str_ver.equals("0003_3")){
                hwa_1 = new AuxHW().getControlAux(hwm_zl);
            }
			/*发送红外编码--------------------------------------------------------*/
			progressDialog_1=CustomProgressDialog.createDialog(getActivity());
		    progressDialog_1.setMessage("正在发送红外码");
			progressDialog_1.show();
			try{
			runnable_3= new Runnable(){
				public void run(){			
				// =================================================
				MyOrder odToSend;
				switch (str_type) {  
				case Type_Entity.Split_air_conditioning://zigbee空调控制器
					odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
							str_mac, OrderList.SEND_ZIGBEE_AIR_HWM,"","",hwa_1);	
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
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					Message msg  = new Message();
					Bundle data = new Bundle();
					String _data="网络异常";
					String [] Data=new String[]{"MQTT",_data};
					data.putStringArray("THZ", Data);
					msg.setData(data);	
					Kt_Frag1.this.handler.sendMessage(msg);
					e.printStackTrace();
					}
											
				}
			};
			new Thread(runnable_3).start();
			}catch(Exception e){
				progressDialog_1.dismiss();
				e.printStackTrace();
				}
		
			
			/*发送红外编码---------------------------------------------------------*/
			break;
			
		case R.id.chk_hetiaobtn:
			//----------------------------------
			// 如果不是isSelected就是isChecked
			try{
				//progressDialog_1=CustomProgressDialog.createDialog(getActivity());
				//((CheckBox) v).setClickable(false);
				if (((CheckBox) v).isChecked() == SOCKET_STATE_TIAOZHA) {
					// 之前状态为合闸状态，因此要执行跳闸		
					// =================================================
					//发送mqtt推送消息;
					dialog_thz_point("tz");
				} else {
					// 之前状态为跳闸状态，因此要执行 “合闸”
					dialog_thz_point("hz");
			
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
					
			//------------------------------------
			break;
			}
			}catch(Exception e){
				e.printStackTrace();
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
					Kt_Frag1.this.handler.sendMessage(msg);
				}
			});//取消
		    
			viewSex.findViewById(R.id.button_submit).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialogSex.cancel();
					progressDialog_1=CustomProgressDialog.createDialog(getActivity());
					progressDialog_1.setMessage("正在合闸");//设置进度条信息为合闸
					progressDialog_1.show();
					new Thread(){
						public void run(){
							try{
								MyOrder odToSend;
								if(thz_msg.equals("hz")){
									switch (str_type) {
									// 智能插座
									case Type_Entity.Wifi_socket: 
										// 根据设备类型，获取需要发送的命令
										odToSend = OrderList.getOrderByDeviceType(str_type,
												str_mac, OrderList.ORDER_SOCKET_READ);
										break;  
									case Type_Entity.Split_air_conditioning://zigbee空调控制器
										odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
												str_mac, OrderList.ZIGBEE_AIR_ON,"","","");			
										break;
									default:
										odToSend = null;
										break;
									}
								}else{
									switch (str_type) {
									// 智能插座
									case Type_Entity.Wifi_socket: 
										// 根据设备类型，获取需要发送的命令
										odToSend = OrderList.getOrderByDeviceType(str_type,
												str_mac, OrderList.ORDER_SOCKET_READ);
										break;  
									case Type_Entity.Split_air_conditioning://zigbee空调控制器
										odToSend = OrderList.Send_Zigbee_ByDeviceType(str_type,
												str_mac, OrderList.ZIGBEE_AIR_OFF,"","","");			
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
								Kt_Frag1.this.handler.sendMessage(msg);
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
					Kt_Frag1.this.handler.sendMessage(msg);
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
		tv_item0 = (TextView) view.findViewById(R.id.item0_air);
		tv_item1 = (TextView) view.findViewById(R.id.item1_air);
		txt_temp = (TextView) view.findViewById(R.id.txt_temp);//设定温度显示
		textView_temp = (TextView) view.findViewById(R.id.item_temp_air);//环境温度显示
		
		image0_air = (ImageView) view.findViewById(R.id.image0_air);//通讯网络显示
		textview2_air = (TextView) view.findViewById(R.id.textview2_air);
		//设备通讯质量
		init_cp();	
		
		checkBox1 = (CheckBox) view.findViewById(R.id.chk_hetiaobtn);
		checkBox1.setOnClickListener(this);
		
		
		txt_temp.setOnClickListener(this);
		
		linearlayout_air0 = (LinearLayout) view.findViewById(R.id.linearlayout_air0);
		linearlayout_air1 = (LinearLayout) view.findViewById(R.id.linearlayout_air1);
		linearlayout_air2 = (LinearLayout) view.findViewById(R.id.linearlayout_air2);
		linearlayout_air3 = (LinearLayout) view.findViewById(R.id.linearlayout_air3);
		

	
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
		/**注册接收广播*/
		public void RegisterBroad(){
			//界面更新广播接收;
			new Thread(){
				public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter();  
				intentfilter.addAction("Intent.NOTIFY_ZIGBEE");  
				intentfilter.addAction("Intent.UPDATA"); 
				intentfilter.addAction("Intent.NOTIFY_AIR");
				intentfilter.addAction("Intent.SELECT_cq");
				getActivity().registerReceiver(receiver, intentfilter); 
				}
			}.start();	
	
		}
		
		/**handle异常*/
		public void handleException(){
			Message msg=new Message();
			Bundle data=new Bundle();
			String return_data="网络异常";
			String [] dnData=new String[]{"MQTT",return_data};
			data.putStringArray("THZ", dnData);
			msg.setData(data);	
			Kt_Frag1.this.handler.sendMessage(msg) ;
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
								Type_Entity.Split_air_conditioning+"/"+str_mac,
								"realtimedata/"+Type_Entity.Split_air_conditioning+"/"+str_mac+"/get_cq_of_meter");	
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handleException();
					}
				}
			}.start();	
		}

		
		class ReceiveTool extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				System.out.println("************************收到广播，这是空调界面："+intent.getAction());
				System.out.println("intent.getStringExtraL::::"+intent.getStringExtra("msg"));
				try{
				Message msg=new Message();
				Bundle data=new Bundle();
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "UPDATA"://数据上报时如果是该设备的数据这里也会更新一次数据
						initDataView();
						break;
					case "CONTROL_SUCCESS_HZ"://跳合闸成功
						String data_0="CONTROL_SUCCESS_HZ";
						String [] Data_0=new String[]{SHUAXIN,data_0};
						data.putStringArray("THZ", Data_0);
						msg.setData(data);	
						Kt_Frag1.this.handler.sendMessage(msg);
						break;
					case "CONTROL_SUCCESS_TZ"://跳合闸成功
						String data_="CONTROL_SUCCESS_TZ";
						String [] Data_=new String[]{SHUAXIN,data_};
						data.putStringArray("THZ", Data_);
						msg.setData(data);	
						Kt_Frag1.this.handler.sendMessage(msg);
						break;
					case "TIMEOUT":
						String data_2="TIMEOUT";
						String [] Data_2=new String[]{SHUAXIN,data_2};
						data.putStringArray("THZ", Data_2);
						msg.setData(data);	
						Kt_Frag1.this.handler.sendMessage(msg);
						break;
					case "HWFS_SUCCESS"://红外发送成功
						String data_3="HWFS_SUCCESS";
						String [] Data_3=new String[]{SHUAXIN,data_3};
						data.putStringArray("THZ", Data_3);
						msg.setData(data);	
						Kt_Frag1.this.handler.sendMessage(msg);
						break;
					
					case "HWFS":////操作失败（包括空调控制器的跳合闸和红外发送）
						String data_1="HWFS_FAIL";
						String [] Data_1=new String[]{SHUAXIN,data_1};
						data.putStringArray("THZ", Data_1);
						msg.setData(data);	
						Kt_Frag1.this.handler.sendMessage(msg);
						break;
					case "CONTROL":////操作失败（包括空调控制器的跳合闸和红外发送）
						String data_4=intent.getStringExtra("msg").toString();
						String [] Data_4=new String[]{SHUAXIN,data_4};
						data.putStringArray("THZ", Data_4);
						msg.setData(data);	
						Kt_Frag1.this.handler.sendMessage(msg);
						break;
					}
				}
		
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
		
		
		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			try{
				System.out.println("********************onResume******************----------*ghjhfjhsdb");
				//注册接收广播
				RegisterBroad();
				init_first();
				initDataView();
				
			}catch(Exception e){
				e.printStackTrace();
			}
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
		/**字符串进行字节反向*/
		private static String get_reverse_String(String str){
			StringBuffer strbuf = new StringBuffer();
			for(int i = str.length()/2;i > 0;i--){
				strbuf.append(str.substring(i*2-2,i*2));
			}
			
			return strbuf.toString();
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

    public static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }
	
}

