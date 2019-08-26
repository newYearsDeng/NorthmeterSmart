package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.Task_Quota_Adp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.model.Task_Quota_Model;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.utils.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 显示定额任务的界面
 * */
public class Task_Quota_View  extends BaseActivity implements OnClickListener,OnItemClickListener{

	private String str_mac,str_type;
	private TextView edit_view;
	private RelativeLayout relativelayout3;//最下方编辑栏
	private boolean visibility = false;
	private ListView listview_adp;
	private Task_Quota_Adp adapter ;
	private List<Task_Quota_Model> models;
	private ReceiveTool receiver;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.kt_frag_task_one_view);
			
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_type = Utils.getDeviceType(this,str_mac);
			RegisterBroad();
			init_view();
			init_listview(visibility);
			
			read_Quota();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**初始化界面*/
	private void init_view(){
		try{
			relativelayout3 = (RelativeLayout) findViewById(R.id.relativelayout3);
			findViewById(R.id.relativeLayout2).setOnClickListener(this);//添加定时任务
			findViewById(R.id.ima_remove).setOnClickListener(this);//删除按钮
			findViewById(R.id.img_choose).setOnClickListener(this);//全选按钮
			findViewById(R.id.ima_freshen).setOnClickListener(this);//刷新按钮
			findViewById(R.id.button2).setOnClickListener(this);//返回
			edit_view = (TextView)findViewById(R.id.edit_view);//编辑按钮
			edit_view.setOnClickListener(this);
			
			TextView txt_2 = (TextView) findViewById(R.id.txt_2);
			txt_2.setText("添加定额任务");
			
			TextView textView_title_2 = (TextView) findViewById(R.id.textView_title_2);
			textView_title_2.setText("定额任务");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**初始化listview*/
	private void init_listview(boolean visibility){
		try{
			listview_adp = (ListView) findViewById(R.id.listview1);
			models = new ArrayList<Task_Quota_Model>();
			
			sp = getSharedPreferences("get_quota",MODE_PRIVATE);
			String task_msg = sp.getString(str_mac, null);//0a0001aa7k/150721023750/quota/99.9#0#building=*&meter=150721023750&eqpt_type=0a0001aa7k

			String type = task_msg.split("/")[0];
			String mac = task_msg.split("/")[1];
			String data = task_msg.split("/")[3];

			if(!data.equals("null")){					
				String[] Task_list = data.split("\n");
				for(int i=0;i<Task_list.length;i++){
					String[] one_task_list = Task_list[i].split("#");	
					Task_Quota_Model model = new Task_Quota_Model();
					model.setMeter(mac);
					model.setType(type);
					model.setQuota_data(data.split("#")[0]);
					model.setQuota_control(data.split("#")[1]);
					model.setVisibility(visibility);
					models.add(model);
				}	
			}else{
				//收到数据为"null"时，清除点models中缓存数据；
				models.clear();
			}
			adapter = new Task_Quota_Adp(this,models);
			listview_adp.setAdapter(adapter);
			listview_adp.setOnItemClickListener(this);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.relativeLayout2:
				Intent intent = new Intent(this,Task_Quota_Add.class);
				intent.putExtra("mac", str_mac);
				intent.putExtra("type", str_type);
				startActivity(intent);
				break;
			
			case R.id.edit_view:
		    	if(edit_view.getText().equals("编辑")){
		    		edit_view.setText("取消");
		    		visibility = true;
		    		init_listview(visibility);
		    		relativelayout3.setVisibility(View.VISIBLE);
		    	}else{
		    		edit_view.setText("编辑");
		    		visibility = false;
		    		init_listview(visibility);
		    		relativelayout3.setVisibility(View.GONE);
		    	}
		    	break;
			case R.id.ima_freshen://刷新
				read_Quota();
				break;
		    	
		    case R.id.img_choose://全选
		    	for (int i = 0; i < models.size(); i++) {
		    		Task_Quota_Model model = models.get(i);
					if (model.isCheck()){
						model.setCheck(false);
					}else{
						model.setCheck(true);
					}
						
				}
		    	adapter.notifyDataSetChanged();
		    	break;		    	
			case R.id.button2://返回
				finish();
				break;
				
			case R.id.ima_remove://删除  delOneoffTask_of_meter/2016-09-28 13:50:23#2016-09-28 20:50:23#hz#0a0001aa7k#150721023750
				List<Task_Quota_Model> modelsToDelete = new ArrayList<Task_Quota_Model>();
				for(int i=0;i<models.size();i++){
					if(models.get(i).isCheck()){
						modelsToDelete.add(models.get(i));
					}
				}
				//String[] delete_list = new String[modelsToDelete.size()];
				for(int j=0;j<modelsToDelete.size();j++){
//					String delete_data = modelsToDelete.get(j).getTime()+"#"+modelsToDelete.get(j).getContinue_time()+"#"+
//							modelsToDelete.get(j).getControl_id()+"#"+modelsToDelete.get(j).getControl_type()+"#"+modelsToDelete.get(j).getMeter();
					delete_Quota(modelsToDelete.get(j).getMeter(),modelsToDelete.get(j).getType());
				}
				break;
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		RegisterBroad();
		init_listview(visibility);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		try{
			Task_Quota_Model model1=models.get(arg2);
			//Toast.makeText(getActivity(), model1.getId()+"", Toast.LENGTH_SHORT).show();
			if(model1.isCheck()==false){
				model1.setCheck(true);
			}else{
				model1.setCheck(false);
			}   
			adapter.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

		
		/**
		 * 发送定时任务*/
		/**发送以设置好的定时任务到服务器*/
		private void read_Quota(){//定额量的：quota/addQuota/99.9#0#building=*&meter=150721023750&eqpt_type=0a0001aa7k

			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
								str_type+"/"+str_mac,"quota/read");	
//						PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/read");
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();	
		}

		/**
		 * 删除定时任务*/
		/**发送以设置好的定时任务到服务器*/
		private void delete_Quota(final String data,final String str_type){//定额量的：quota/addQuota/99.9#0#building=*&meter=150721023750&eqpt_type=0a0001aa7k
			//delQuota/0a0001aa7k#150721023750
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
								str_type+"/"+str_mac,"quota/delQuota#"+data);	
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/delOneoffTask_of_meter/"+data);
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();	
		}
		
		
	  /**注册接收广播*/
	  public void RegisterBroad(){
		  try{
			  //一次性定时任务广播接收;
			  new Thread(){
			  public void run(){
				  receiver = new ReceiveTool();
				  IntentFilter intentfilter = new IntentFilter("NOTIFY_QUOTA_TASK");
				  registerReceiver(receiver, intentfilter);
				
			  }
			  }.start();	
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
		
		/**接收定时任务更新广播类*/
		/** selected/0a0001a820/160127001286/get_one_tasks/data
			getreceive = new String[]{"NOTIFY_ONE_TASK",receive.split("/")[2]+"/get_one_tasks/"+receive.split("/")[1]+"/"+receive.split("/")[4]};};*/
		class ReceiveTool extends BroadcastReceiver{
			//selected/0a0001aa7k/150721023750/get_one_tasks/2016-09-28 13:50:23#2016-09-28 19:50:23#hz 
			@Override
			public void onReceive(Context context, Intent intent) {//selected/0a0001aa7k/002014110119/read30h/data
				// TODO Auto-generated method stub
				
				System.out.println("************************收到广播，一次性定时任务界面************："+intent.getAction());
				try{
					if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
						switch(intent.getStringExtra("msg").split("/")[2]){
						case "get_quota"://一次性定时任务数据
							sp = getSharedPreferences("get_quota",MODE_PRIVATE);
							Editor editor = sp.edit();
							editor.putString(str_mac, intent.getStringExtra("msg"));
							editor.commit();
							
							init_listview(visibility);
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
				super.onDestroy();
				unregisterReceiver(receiver);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	
}
