package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.Kt_Task_OneAdp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.model.Kt_Task_OneModel;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;

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
 * 显示一次性定时任务的界面
 * */
public class Kt_Frag_Task_one_View  extends BaseActivity implements OnClickListener,OnItemClickListener{

	private String str_mac,str_name,str_type;
	private TextView edit_view;
	private RelativeLayout relativelayout3;//最下方编辑栏
	private boolean visibility = false;
	private ListView listview_adp;
	private Kt_Task_OneAdp adapter ;
	private List<Kt_Task_OneModel> models;
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
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			RegisterBroad();
			init_view();
			init_listview(visibility);
			
			read_Task_One();
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**初始化listview*/
	private void init_listview(boolean visibility){
		try{
			listview_adp = (ListView) findViewById(R.id.listview1);
			models = new ArrayList<Kt_Task_OneModel>();
			
			sp = getSharedPreferences("get_one_tasks",MODE_PRIVATE);
			String task_msg = sp.getString(str_mac, null);//0a0001aa7k/150721023750/get_one_tasks/0802021611081728289SXA#
							//#0#2016-11-15 09:54:00#2016-11-18 12:50:00#hz
			String type = task_msg.split("/")[0];
			String mac = task_msg.split("/")[1];
			String data = task_msg.split("/")[3];

			if(!data.equals("null")){					
				String[] Task_list = data.split("\n");
				for(int i=0;i<Task_list.length;i++){
					String[] one_task_list = Task_list[i].split("#");	
					Kt_Task_OneModel model = new Kt_Task_OneModel();
					model.setTask_id(one_task_list[0]);
					model.setTime(one_task_list[2]);
					model.setContinue_time(one_task_list[3]);
					model.setControl_id(one_task_list[4]);
					model.setMeter(mac);
					model.setControl_type(type);
					model.setVisibility(visibility);
					models.add(model);
				}	
			}else{
				//收到数据为"null"时，清除点models中缓存数据；
				models.clear();
			}
			adapter = new Kt_Task_OneAdp(this,models);
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
				Intent intent = new Intent(this,Kt_Frag_Task_one_Add.class);
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
				read_Task_One();
				break;
		    	
		    case R.id.img_choose://全选
		    	for (int i = 0; i < models.size(); i++) {
		    		Kt_Task_OneModel model = models.get(i);
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
				List<Kt_Task_OneModel> modelsToDelete = new ArrayList<Kt_Task_OneModel>();
				for(int i=0;i<models.size();i++){
					if(models.get(i).isCheck()){
						modelsToDelete.add(models.get(i));
					}
				}
				//String[] delete_list = new String[modelsToDelete.size()];
				for(int j=0;j<modelsToDelete.size();j++){
//					String delete_data = modelsToDelete.get(j).getTime()+"#"+modelsToDelete.get(j).getContinue_time()+"#"+
//							modelsToDelete.get(j).getControl_id()+"#"+modelsToDelete.get(j).getControl_type()+"#"+modelsToDelete.get(j).getMeter();
					delete_Task_One(modelsToDelete.get(j).getTask_id());
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
			Kt_Task_OneModel model1=models.get(arg2);
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
	
	  /**注册接收广播*/
		public void RegisterBroad(){
			try{
			//一次性定时任务广播接收;
			new Thread(){
				public void run(){
					receiver = new ReceiveTool();
					IntentFilter intentfilter = new IntentFilter("NOTIFY_ONE_TASK");
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
						case "get_one_tasks"://一次性定时任务数据
							sp = getSharedPreferences("get_one_tasks",MODE_PRIVATE);
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
		
		/**
		 * 发送定时任务*/
		/**发送以设置好的定时任务到服务器*/
		private void read_Task_One(){//addOneoffTask_of_meter/2016-09-28 13:50:23#2016-09-28 20:50:23#hz#0a0001aa7k#150721023750
									//delOneoffTask_of_meter/2016-09-28 13:50:23#2016-09-28 20:50:23#hz#0a0001aa7k#150721023750
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(Kt_Frag_Task_one_View.this,
								str_type+"/"+str_mac,"onetask/read");	
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
		private void delete_Task_One(final String data){//addOneoffTask_of_meter/2016-09-28 13:50:23#2016-09-28 20:50:23#hz#0a0001aa7k#150721023750
														//delOneoffTask_of_meter/2016-09-28 13:50:23#2016-09-28 20:50:23#hz#0a0001aa7k#150721023750
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(Kt_Frag_Task_one_View.this,
								str_type+"/"+str_mac,"onetask/delBatchTask/"+data);	
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/delOneoffTask_of_meter/"+data);
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();	
		}
	
	
}
