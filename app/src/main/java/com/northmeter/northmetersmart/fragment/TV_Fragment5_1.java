package com.northmeter.northmetersmart.fragment;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.Active_History_Aty;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * 活动流界面
 * */
public class TV_Fragment5_1 extends Fragment implements OnClickListener {
	
	private View view;
	//今日
	/**今日时长*/
	private TextView today_textview_time;
	/**今日用电量*/
	private TextView today_textview_electricity;
	/**非工作室温*/
	private TextView today_textview_temp_1;
	/**工作平均室温*/
	private TextView today_textview_temp_2;
	

	/**今日非工作室温图标*/
	private ImageView today_image_temp_1;
	/**今日工作平均室温图标*/
	private ImageView today_image_temp_2;
	
	
	//昨日
	/**昨日时长*/
	private TextView yesterday_textview_time;
	/**昨日用电量*/
	private TextView yesterday_textview_electricity;
	/**非工作室温*/
	private TextView yesterday_textview_temp_1;
	/**工作平均室温*/
	private TextView yesterday_textview_temp_2;
	

	/**昨日非工作室温图标*/
	private ImageView yesterday_image_temp_1;
	/**昨日工作平均室温图标*/
	private ImageView yesterday_image_temp_2;
	
	
	//前天
	/**前日时长*/
	private TextView daybefore_textview_time;
	/**前日用电量*/
	private TextView daybefore_textview_electricity;
	/**非工作室温*/
	private TextView daybefore_textview_temp_1;
	/**工作平均室温*/
	private TextView daybefore_textview_temp_2;
	

	/**前日非工作室温图标*/
	private ImageView daybefore_image_temp_1;
	/**前日工作平均室温图标*/
	private ImageView daybefore_image_temp_2;
	
	/**三天界面布局*/
	private LinearLayout today_layout_1;
	private LinearLayout today_layout_2;
	private LinearLayout yestoday_layout_1;
	private LinearLayout yestoday_layout_2;
	private LinearLayout daybefore_layout_1;
	private LinearLayout daybefore_layout_2;
	
	
	private ImageView activ_img_1,activ_img_2,activ_img_3,activ_img_4,activ_img_5;
	private String str_mac,str_name,str_type;
	private ReceiveTool receiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_f5_1, container, false);
		try{
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			
			init_view();
			//注册接收广播
			RegisterBroad();
			//get_work_activities();
			
			switch(str_type){
			case Type_Entity.Split_air_conditioning:
				break;
			case Type_Entity.Central_air_conditioning:
				break;
			case Type_Entity.Socket_type:
				break;
			case Type_Entity.Four_street_control:
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	
	/**初始化界面*/
	private void init_view(){
		LinearLayout active_flow_but = (LinearLayout) view.findViewById(R.id.active_flow_but);//历史记录跳转
		active_flow_but.setOnClickListener(this);
		/**今日时长*/
		today_textview_time = (TextView) view.findViewById(R.id.today_textview_time);
		/**今日用电量*/
		today_textview_electricity = (TextView) view.findViewById(R.id.today_textview_electricity);
		/**非工作室温*/
		today_textview_temp_1 = (TextView) view.findViewById(R.id.today_textview_temp_1);
		/**工作平均室温*/
		today_textview_temp_2 = (TextView) view.findViewById(R.id.today_textview_temp_2);
		
		/**今日非工作室温图标*/
		today_image_temp_1 = (ImageView) view.findViewById(R.id.today_image_temp_1);
		/**今日工作平均室温图标*/
		today_image_temp_2 = (ImageView) view.findViewById(R.id.today_image_temp_2);
		
		
		//昨日
		/**昨日时长*/
		yesterday_textview_time = (TextView) view.findViewById(R.id.yesterday_textview_time);
		/**昨日用电量*/
		yesterday_textview_electricity = (TextView) view.findViewById(R.id.yesterday_textview_electricity);
		/**非工作室温*/
		yesterday_textview_temp_1 = (TextView) view.findViewById(R.id.yesterday_textview_temp_1);
		/**工作平均室温*/
		yesterday_textview_temp_2 = (TextView) view.findViewById(R.id.yesterday_textview_temp_2);
		
		/**昨日非工作室温图标*/
		yesterday_image_temp_1 = (ImageView) view.findViewById(R.id.yesterday_image_temp_1);
		/**昨日工作平均室温图标*/
		yesterday_image_temp_2 = (ImageView) view.findViewById(R.id.yesterday_image_temp_2);
		
		
		//前天
		/**前日时长*/
		daybefore_textview_time = (TextView) view.findViewById(R.id.daybefore_textview_time);
		/**前日用电量*/
		daybefore_textview_electricity = (TextView) view.findViewById(R.id.daybefore_textview_electricity);
		/**非工作室温*/
		daybefore_textview_temp_1 = (TextView) view.findViewById(R.id.daybefore_textview_temp_1);
		/**工作平均室温*/
		daybefore_textview_temp_2 = (TextView) view.findViewById(R.id.daybefore_textview_temp_2);
		
		/**前日非工作室温图标*/
		daybefore_image_temp_1 = (ImageView) view.findViewById(R.id.daybefore_image_temp_1);
		/**前日工作平均室温图标*/
		daybefore_image_temp_2 = (ImageView) view.findViewById(R.id.daybefore_image_temp_2);
		
		
		today_layout_1 = (LinearLayout) view.findViewById(R.id.today_layout_1);
		today_layout_2 = (LinearLayout) view.findViewById(R.id.today_layout_2);
		yestoday_layout_1 = (LinearLayout) view.findViewById(R.id.yestoday_layout_1);
		yestoday_layout_2 = (LinearLayout) view.findViewById(R.id.yestoday_layout_2);
		daybefore_layout_1 = (LinearLayout) view.findViewById(R.id.daybefore_layout_1);
		daybefore_layout_2 = (LinearLayout) view.findViewById(R.id.daybefore_layout_2);
		
		/**每一天子布局*/
		LinearLayout today_layout_child = (LinearLayout) view.findViewById(R.id.today_layout_child);
		LinearLayout yesterday_layout_child = (LinearLayout) view.findViewById(R.id.yesterday_layout_child);
		LinearLayout daybefore_layout_child = (LinearLayout) view.findViewById(R.id.daybefore_layout_child);
		
		if(str_type.equals(Type_Entity.Central_air_conditioning)){//中央空调
			today_layout_child.setVisibility(view.GONE);
			yesterday_layout_child.setVisibility(view.GONE);
			daybefore_layout_child.setVisibility(view.GONE);
			active_flow_but.setVisibility(view.GONE);
		}
		else if(str_type.equals(Type_Entity.Split_air_conditioning)){//分体空调
			active_flow_but.setVisibility(view.GONE);
		}
		else if(str_type.equals(Type_Entity.Socket_type)){//插座
			today_layout_1.setVisibility(view.GONE);
			today_layout_2.setVisibility(view.GONE);
			yestoday_layout_1.setVisibility(view.GONE);
			yestoday_layout_2.setVisibility(view.GONE);
			daybefore_layout_1.setVisibility(view.GONE);
			daybefore_layout_2.setVisibility(view.GONE);
		}
		
	}
	
	/**读取活动流数据
	        发送：realtimedata/类型/表号/get_work_activities
	        返回: get_work_activities/0a0001aa7k/002014110119/data
	        数据结果：*/ //2016-11-17 1h31min#1.34#27.5#27.07  2016-11-16 3h48min#3.41#28.0#28.37  2016-11-15 3h48min#3.41#28.0#28.37
	private void get_work_activities(){
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
								str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_work_activities");	
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a820/"+str_mac,"realtimedata/0a0001a820/"+str_mac+"/get_socket_readtime_workflow");
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
		try{
			switch(v.getId()){
			case R.id.active_flow_but://历史活动流
				Intent intent = new Intent(getActivity(),Active_History_Aty.class);
				intent.putExtra("mac", str_mac);
				startActivity(intent);
				break;
				
				default:
					break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 *  0℃以下
		0-10
		10-16
		16-24
		24-30
		30-37
		37以上
	 */
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if(getActivity().isFinishing()){
				return;
			}
			
			Bundle data=msg.getData();
			String[] receive = data.getStringArray("READ");
			switch(receive[0]){
		
			
			case "get_work_activities":
				String active_msg = receive[1];//2016-11-17 1h31min#1.34#27.5#27.07  2016-11-16 3h48min#3.41#28.0#28.37  2016-11-15 3h48min#3.41#28.0#28.37
				String [] activitie_list = active_msg.split("\n");
				String active_1 = activitie_list[0];//今天数据
				String active_2 = activitie_list[1];//昨天数据
				String active_3 = activitie_list[2];//前天数据
				
				String active_1_msg = active_1.split(" ")[1];
				if(active_1_msg.equals("null")){
					today_layout_1.setVisibility(view.GONE);
					today_layout_2.setVisibility(view.GONE);
				}else{
					switch(str_type){
					case"0a0001aa7k":
						/**时长*/
						today_textview_time.setText(active_1_msg.split("#")[0]);
						/**今日用电量*/
						today_textview_electricity.setText(active_1_msg.split("#")[1]+"kWh");
						/**非工作室温*/
						today_textview_temp_1.setText(active_1_msg.split("#")[2]+"℃");
						/**工作平均室温*/
						today_textview_temp_2.setText(active_1_msg.split("#")[3]+"℃"); 
						
						today_image_temp_1.setImageResource(getImageReourse(active_1_msg.split("#")[2]));
						today_image_temp_2.setImageResource(getImageReourse(active_1_msg.split("#")[3]));
						break;
					case "0a0001a4r5":
						/**时长*/
						today_textview_time.setText(active_1_msg.split("#")[0]);
						/**非工作室温*/
						today_textview_temp_1.setText(active_1_msg.split("#")[1]+"℃");
						/**工作平均室温*/
						today_textview_temp_2.setText(active_1_msg.split("#")[2]+"℃"); 
						
						today_image_temp_1.setImageResource(getImageReourse(active_1_msg.split("#")[1]));
						today_image_temp_2.setImageResource(getImageReourse(active_1_msg.split("#")[2]));

						break;
					}
					
				}
				
				String active_2_msg = active_2.split(" ")[1];
				if(active_2_msg.equals("null")){
					yestoday_layout_1.setVisibility(view.GONE);
					yestoday_layout_2.setVisibility(view.GONE);
				}else{
					switch(str_type){
					case"0a0001aa7k":
						/**时长*/
						yesterday_textview_time.setText(active_2_msg.split("#")[0]);
						/**昨日用电量*/
						yesterday_textview_electricity.setText(active_2_msg.split("#")[1]+"kWh");
						/**非工作室温*/
						yesterday_textview_temp_1.setText(active_2_msg.split("#")[2]+"℃");
						/**工作平均室温*/
						yesterday_textview_temp_2.setText(active_2_msg.split("#")[3]+"℃");
						
						yesterday_image_temp_1.setImageResource(getImageReourse(active_2_msg.split("#")[2]));
						yesterday_image_temp_2.setImageResource(getImageReourse(active_2_msg.split("#")[3]));
						break;
					case"0a0001a4r5":
						/**时长*/
						yesterday_textview_time.setText(active_2_msg.split("#")[0]);
						/**非工作室温*/
						yesterday_textview_temp_1.setText(active_2_msg.split("#")[1]+"℃");
						/**工作平均室温*/
						yesterday_textview_temp_2.setText(active_2_msg.split("#")[2]+"℃");
						
						yesterday_image_temp_1.setImageResource(getImageReourse(active_2_msg.split("#")[1]));
						yesterday_image_temp_2.setImageResource(getImageReourse(active_2_msg.split("#")[2]));
						break;
					}
					
				}
				
				String active_3_msg = active_3.split(" ")[1];
				if(active_3_msg.equals("null")){
					daybefore_layout_1.setVisibility(view.GONE);
					daybefore_layout_2.setVisibility(view.GONE);
				}else{
					switch(str_type){
					case"0a0001aa7k":
						/**时长*/
						daybefore_textview_time.setText(active_3_msg.split("#")[0]);
						/**前日用电量*/
						daybefore_textview_electricity.setText(active_3_msg.split("#")[1]+"kWh"); 
						/**非工作室温*/
						daybefore_textview_temp_1.setText(active_3_msg.split("#")[2]+"℃"); 
						/**工作平均室温*/
						daybefore_textview_temp_2.setText(active_3_msg.split("#")[3]+"℃"); 
						
						daybefore_image_temp_1.setImageResource(getImageReourse(active_3_msg.split("#")[2]));
						daybefore_image_temp_2.setImageResource(getImageReourse(active_3_msg.split("#")[3]));
						break;
					case"0a0001a4r5":
						/**时长*/
						daybefore_textview_time.setText(active_3_msg.split("#")[0]);
						/**非工作室温*/
						daybefore_textview_temp_1.setText(active_3_msg.split("#")[1]+"℃"); 
						/**工作平均室温*/
						daybefore_textview_temp_2.setText(active_3_msg.split("#")[2]+"℃"); 
						
						daybefore_image_temp_1.setImageResource(getImageReourse(active_3_msg.split("#")[1]));
						daybefore_image_temp_2.setImageResource(getImageReourse(active_3_msg.split("#")[2]));
						break;
					}
				}
				
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			}
		}
	};
	
	/**计算当前温度加载图片*/
	private static int getImageReourse(String temp_str){
		int resource = R.drawable.active_temp_1;
		try{
			if(!temp_str.equals("--")){
				float temp = Float.parseFloat(temp_str);
				if(temp<1){
					resource = R.drawable.active_temp_1;
				}else if(0<temp && temp<10){
					resource = R.drawable.active_temp_2;
				}else if(9<temp && temp<16){
					resource = R.drawable.active_temp_3;
				}else if(15<temp && temp<24){
					resource = R.drawable.active_temp_4;
				}else if(23<temp && temp<30){
					resource = R.drawable.active_temp_5;
				}else if(29<temp && temp<37){
					resource = R.drawable.active_temp_6;
				}else{
					resource = R.drawable.active_temp_7;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return resource;
		
	}
		

	/**注册接收广播*/
	public void RegisterBroad(){
		//界面更新广播接收;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter("Intent.get_work_activities");
				getActivity().registerReceiver(receiver, intentfilter);
			
			}
		}.start();	
	}
	
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("************************收到广播,这是zigbee界面"+intent.getAction());
			Message msg=new Message();
			Bundle data=new Bundle();
			if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
				switch(intent.getStringExtra("msg").split("/")[2]){//selected/(没发送过来)   0a0001aa7k/002014110119/get_socket_readtime_workflow/data
				case "get_work_activities"://活动流
					String receive= intent.getStringExtra("msg").split("/")[3];
					if(!receive.equals("null")){
						String [] dnData=new String[]{"get_work_activities",receive};
						data.putStringArray("READ", dnData);
						msg.setData(data);	
						TV_Fragment5_1.this.handler.sendMessage(msg);
					}
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
		getActivity().unregisterReceiver(receiver);
		}catch(Exception e){
			e.printStackTrace();
			}
	}
	

}
