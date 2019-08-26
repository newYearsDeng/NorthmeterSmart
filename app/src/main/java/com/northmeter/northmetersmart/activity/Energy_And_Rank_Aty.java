package com.northmeter.northmetersmart.activity;

import java.util.ArrayList;
import java.util.List;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.EnergyAndRankAdp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.model.Energy_And_Rank_Model;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.widget.ListView;

/**
 * 节能天数和用电排名的显示类；
 * */
public class Energy_And_Rank_Aty extends BaseActivity{
	private ListView listview;
	private Energy_And_Rank_Model model;
	private List<Energy_And_Rank_Model> models;
	private ReceiveTool receiver ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.energy_and_rank_aty);
		listview = (ListView) findViewById(R.id.listview_energy);
		//注册监听广播
		//RegisterBroad();
		//发送命令获取排名数据
		String send_msg = get_subscribe_meter();
		get_Form_Rank(send_msg);
		//初始化界面
		//init_listview();

	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			try{
				super.handleMessage(msg);
				if (msg.what == 1) {//selected/(注：前面这些数据不传递过来)    Type/Meter/get_ranking/002014110119 2 2 1.00 1 1.00
					String bundle_msg = msg.getData().getString("content");
					switch(bundle_msg.split("/")[2]){
					case "get_ranking":
						String receive =bundle_msg.split("/")[3];
						String [] rank_list = receive.split("\n");
						models = new ArrayList<Energy_And_Rank_Model>();
						for(int i=0; i<1; i++ ){
							Energy_And_Rank_Model model = new Energy_And_Rank_Model();
							model.setText_name("表号");
							model.setText_energy("用电(天)");
							model.setText_energy_save("节能(天)");
							model.setText_percent("比例");
							model.setText_rank("战胜(人)");
							model.setText_beat("");
							model.setBackground(R.drawable.back_ground_color);
							models.add(model);
						}
						
						for(int i=0; i<rank_list.length; i++ ){
							System.out.println(" msg_list[i*6+0]:"+ rank_list[i].split(" ")[0]);//表号
							System.out.println(" msg_list[i*6+1]:"+ rank_list[i].split(" ")[1]);//用电天数
							System.out.println(" msg_list[i*6+2]:"+ rank_list[i].split(" ")[2]);//节能天数
							System.out.println(" msg_list[i*6+3]:"+ rank_list[i].split(" ")[3]);//用电比例
							System.out.println(" msg_list[i*6+4]:"+ rank_list[i].split(" ")[4]);//排名
							System.out.println(" msg_list[i*6+5]:"+ rank_list[i].split(" ")[5]);//打败多少对手
							Energy_And_Rank_Model model = new Energy_And_Rank_Model();	
							model.setText_name(rank_list[i].split(" ")[0]);
							model.setText_energy(rank_list[i].split(" ")[1]);
							model.setText_energy_save(rank_list[i].split(" ")[2]);
							model.setText_percent(rank_list[i].split(" ")[3]);
							model.setText_rank(rank_list[i].split(" ")[4]);
							model.setText_beat(rank_list[i].split(" ")[5]);
							model.setBackground(R.drawable.back_ground_color);
							models.add(model);
						}
						EnergyAndRankAdp adp = new EnergyAndRankAdp(Energy_And_Rank_Aty.this,models);
						listview.setAdapter(adp);
						break;
						}
				}
						
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
	
	/**读取能耗排行*/
	private void get_Form_Rank(final String meters){
			new Thread(){
				public void run(){
					try {
						Time t = new Time();
						t.setToNow();
						String time = String.valueOf(t.year)+"/"+(t.month+1);
						
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Energy_And_Rank_Aty.this,handler,
								"select_form;select_form_back","type/"+meters+"/get_ranking");	
						//new PublishMqttAddMRecord("select_form","type/"+meters+"/get_ranking");
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}.start();
	
	}
	
	
	private void init_listview(){
		try{
			listview = (ListView) findViewById(R.id.listview_energy);
			models = new ArrayList<Energy_And_Rank_Model>();
			for(int i=0;i<1;i++){
				model = new Energy_And_Rank_Model();
				model.setText_name("名字");
				model.setText_energy("用电天数");
				model.setText_energy_save("节能天数");
				model.setText_percent("用电比例");
				model.setText_rank("排名");
				models.add(model);
			}
			for(int i=0;i<10;i++){
				model = new Energy_And_Rank_Model();
				model.setText_name("空调"+(i+1));
				model.setText_energy("52"+i);
				model.setText_energy_save("42"+i);
				model.setText_percent("15");
				model.setText_rank("0"+(i+1));
				models.add(model);
			}
			EnergyAndRankAdp adp = new EnergyAndRankAdp(getApplicationContext(),models);
			listview.setAdapter(adp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**注册接收广播*/
	public void RegisterBroad(){
		try{
		//界面更新广播接收;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter_1 = new IntentFilter("Intent.SELETC");
				registerReceiver(receiver, intentfilter_1);
			}
		}.start();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    class ReceiveTool extends BroadcastReceiver{//selected/(注：前面这些数据不传递过来)    Type/Meter/get_ranking/002014110119 2 2 1.00 1 1.00

		@Override
		public void onReceive(Context context, final Intent intent) {
			// TODO Auto-generated method stub
			try{
				System.out.println("排名数据广播"+intent.getStringExtra("msg"));
				if(intent.getStringExtra("msg").split("/")[1].equals("Meter")){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "get_ranking":
						String receive = intent.getStringExtra("msg").split("/")[3];
						System.out.println("receive:"+receive);
						final String [] rec_list = receive.split("\n");
						new Thread(){
							public void run(){
								try{
//									for(int i=0; i<rec_list.length/6; i++ ){
//										System.out.println(" msg_list[i*6+0]:"+ rec_list[i*5]);//年月
//										System.out.println(" msg_list[i*6+1]:"+ rec_list[i*5+1]);//时间
//										System.out.println(" msg_list[i*6+2]:"+ rec_list[i*5+2]);//电能
//										System.out.println(" msg_list[i*6+3]:"+ rec_list[i*5+3]);//功率
//										System.out.println(" msg_list[i*6+4]:"+ rec_list[i*5+4]);//温度
//									}
									Message msg = new Message();
									Bundle bundle = new Bundle();
									bundle.putStringArray("ranking", rec_list);
									msg.setData(bundle);
									Energy_And_Rank_Aty.this.handler.sendMessage(msg);
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}.start();
						
						break;
						
						default:
							break;
					}
					
				}
	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
    
    /**获取本机中存储的空调控制器*/
    private String get_subscribe_meter(){
    	String result_str = null;
    	try{
    		DBDevice dbd = new DBDevice(getApplicationContext());
        	List<MyDevice> mydevices = dbd.GetMyDevicesByType(4);
        	if(!mydevices.isEmpty()){
        		String[] device_list = new String[mydevices.size()];
            	for(int i=0;i<mydevices.size();i++){
            		device_list[i] = mydevices.get(i).getMac();
            	}
            	result_str = Utils.join(",",device_list);
        	}   		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result_str;
    }
   
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try{
			super.onDestroy();
			this.unregisterReceiver(receiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
