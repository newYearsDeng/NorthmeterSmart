package com.northmeter.northmetersmart.fragment;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.Active_Report_Forms;
import com.northmeter.northmetersmart.activity.Active_Report_Forms_Center;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.simplecache.ACache;
import com.northmeter.northmetersmart.utils.Utils;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.fragment.app.Fragment;

/**
 * 月报表界面
 * */
public class TV_Fragment5_2 extends Fragment {
	
	private View view;
	private ListView listview;
	private ArrayAdapter<String> arrayadapter;
	private String str_mac,str_name,str_type;
	private ReceiveTool receiver;
	private ACache mCache;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_f5_2, container, false);
		try{
			//注册接收广播
			RegisterBroad();
			
			listview  = (ListView) view.findViewById(R.id.listview);
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			
			mCache = ACache.get(getActivity());
			//get_report_year_month_list();
			
			String str_Cache = mCache.getAsString("report_year_month_list"+str_mac)+"";
			if(!str_Cache.equals("null")){
				init_view(mCache.getAsString("report_year_month_list"+str_mac));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	
	/**初始化界面*/
	private void init_view(String list){
		try{
			String[] arr_data = list.split(" ");
			arrayadapter = new ArrayAdapter<String>(getActivity(),R.layout.report_form_item,R.id.textview1,arr_data);
			listview.setAdapter(arrayadapter);
			listview.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					TextView text=(TextView)view.findViewById(R.id.textview1);
					String text_title = text.getText().toString();
					
					switch(str_type){
					case Type_Entity.Split_air_conditioning://分体空调月报
						Intent intent_1 = new Intent(getActivity(),Active_Report_Forms.class);
						intent_1.putExtra("mac",str_mac);
						intent_1.putExtra("type", str_type);
						intent_1.putExtra("title", text_title);
						startActivity(intent_1);
						break;
					case Type_Entity.Central_air_conditioning://中央空调月报
						Intent intent_2 = new Intent(getActivity(),Active_Report_Forms_Center.class);
						intent_2.putExtra("mac",str_mac);
						intent_2.putExtra("type", str_type);
						intent_2.putExtra("title", text_title);
						startActivity(intent_2);
						break;
						default:
							Intent intent_0 = new Intent(getActivity(),Active_Report_Forms.class);
							intent_0.putExtra("mac",str_mac);
							intent_0.putExtra("type", str_type);
							intent_0.putExtra("title", text_title);
							startActivity(intent_0);
							break;
					}
					
				}
				
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** get_report_year_month_list(Meter_type, Meter)
		realtimedata/表类型/表号/get_report_year_month_list*/
	/**获取月报表月份列表*/
	private void get_report_year_month_list(){
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
								str_type+"/"+str_mac,
								"realtimedata/"+str_type+"/"+str_mac+"/get_report_year_month_list");	
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
				IntentFilter intentfilter = new IntentFilter("Intent.get_report_year_month_list");
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
			//selected/0a0001aa7k/150721023750/get_report_of_month/2016-10 119.71 0.0 1/2 100% 27.11 50.53 19.41 5.86 0.93 0.82
			if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
				switch(intent.getStringExtra("msg").split("/")[2]){//selected/(没发送过来)   0a0001aa7k/002014110119/get_socket_readtime_workflow/data
				case "get_report_year_month_list"://报表月报表  realtimedata/   表类型/表号/get_report_year_month_list/data
					String receive= intent.getStringExtra("msg").split("/")[3];
					String result = Utils.join(" ", receive.split("\n"));//带换行符的字符串转换为带空格的字符串
					init_view(result);
					mCache.put("report_year_month_list"+intent.getStringExtra("msg").split("/")[1], result,86400);

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
