package com.northmeter.northmetersmart.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加定额任务的界面
 * */
public class Task_Quota_Add extends BaseActivity implements OnClickListener{
	/**定额数值*/
	private EditText quota_data;
	private TextView warring_action;
	private String str_mac,str_name,str_type,roleid;
	private String action_number="0";//报警方式表示0警告不跳闸   1警告和跳闸
	private ReceiveTool receiver;
	private CustomProgressDialog progressDialog_1;
	private LinearLayout task_sure;
	
	private View layout;
	private TextView toast_text,quota_text,unit_text_1,unit_text_2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.task_quota_add);
			
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			roleid = intent.getStringExtra("roleid");
			/**注册接收广播*/
			//RegisterBroad();
			init_view();
			read_Quota();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void init_view(){
		quota_data = (EditText) findViewById(R.id.quota_data);//设定额度
		quota_text = (TextView) findViewById(R.id.quota_text);//剩余额度
		unit_text_1 = (TextView) findViewById(R.id.unit_text_1);//设定值的单位
		unit_text_2 = (TextView) findViewById(R.id.unit_text_2);//设定值的单位
		
		switch(str_type){
		case Type_Entity.Central_air_conditioning:
			unit_text_1.setText("h");
			unit_text_2.setText("h");
			break;
		}
		
		task_sure = (LinearLayout) findViewById(R.id.task_sure);
		if(!roleid.equals(Type_Entity.manager)){
			task_sure.setVisibility(View.GONE);
		}
		
		findViewById(R.id.but_set).setOnClickListener(this);//设置
		findViewById(R.id.but_delete).setOnClickListener(this);//删除
		findViewById(R.id.button_back).setOnClickListener(this);//返回
		warring_action = (TextView) findViewById(R.id.warring_action);
		warring_action.setOnClickListener(this);
		
		LayoutInflater inflater = getLayoutInflater();
		layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toast_layout));
		toast_text = (TextView) layout.findViewById(R.id.toast_text);
	}
	
	/**
	 * 选择提醒方式
	 */
	private void showActionDialog(){
		final AlertDialog dialog = new AlertDialog.Builder(Task_Quota_Add.this)
				.create();
		dialog.show();
		Window window = dialog.getWindow();
		// 设置布局
		window.setContentView(R.layout.task_one_action_pick);
		// 设置宽高
		window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		// 设置弹出的动画效果
		window.setWindowAnimations(R.style.AnimBottom);
		
		LinearLayout action_on = (LinearLayout) window.findViewById(R.id.action_on);
		LinearLayout action_off = (LinearLayout) window.findViewById(R.id.action_off);
		
		TextView action_on_text = (TextView) window.findViewById(R.id.action_on_text);
		TextView action_off_text = (TextView) window.findViewById(R.id.action_off_text);
		action_on_text.setText("只报警");
		action_off_text.setText("报警并跳闸");
		
		action_on.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				warring_action.setText("只报警");
				action_number = "0";
				dialog.cancel();
			}
		});
		action_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				warring_action.setText("报警并跳闸");
				action_number = "1";
				dialog.cancel();
			}
		});

		
		LinearLayout cancelLayout = (LinearLayout) window.findViewById(R.id.view_none);
		cancelLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				dialog.cancel();
				return false;
			}
		});
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.but_set://设定额度
//				Pattern pattern = Pattern.compile("[0-9]{1,}");
//				Matcher matcher = pattern.matcher((CharSequence)quota_data.getText());
//				boolean result=matcher.matches();
//				if (result != true) {
//					toast_text.setText("输入的数字格式有误");
//					Toast toast = new Toast(this);
//					toast.setView(layout);
//					toast.setDuration(5000);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
//					break;
//				}else{
				progressDialog_1=CustomProgressDialog.createDialog(this);
				progressDialog_1.show();

				String data = quota_data.getText().toString();
				String send_data = data+"#"+action_number+"#building=*&meter="+str_mac+"&eqpt_type="+str_type;
				addQuota(send_data);
				//}
				break;
			case R.id.but_delete://删除额度
				progressDialog_1=CustomProgressDialog.createDialog(this);
				progressDialog_1.show();
				delete_Quota(str_mac,str_type);
				break;
			case R.id.button_back://返回
				finish();
				break;
			case R.id.warring_action://选择报警方式
				showActionDialog();
				break;
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	/**读取任务信息*/
	private void read_Quota(){//quota/read
		new Thread(){
			public void run(){
				try {
					Thread.sleep(1000);
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Task_Quota_Add.this,handler,
							str_type+"/"+str_mac,"quota/read");	
//					PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/read");
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	
	/**
	 * 定额量的：quota/addQuota/99.9#0#building=*&meter=150721023750&eqpt_type=0a0001aa7k
	 * 返回》selected/type/Meter/get_quota/data
	 */
	/**发送以设置好的定额任务到服务器*/
	private void addQuota(final String data){//addQuota/99.9#0#building=*&meter=150721023750&eqpt_type=0a0001aa7k
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Task_Quota_Add.this,handler,
							str_type+"/"+str_mac,"quota/addQuota/"+data);	
//					PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	/**
	 * 删除任务*/
	private void delete_Quota(final String mac,final String str_type){//定额量的：quota/addQuota/99.9#0#building=*&meter=150721023750&eqpt_type=0a0001aa7k
		//delQuota/0a0001aa7k#150721023750
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(Task_Quota_Add.this,handler,
							str_type+"/"+mac,"quota/delQuota/"+str_type+"#"+mac);	
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
	class ReceiveTool extends BroadcastReceiver{/**返回》selected/ type/Meter/get_quota/99.9#0*/
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			System.out.println("************************收到广播，一次性定时任务界面************："+intent.getAction());
			try{
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "get_quota"://一次性定时任务数据
						String data = intent.getStringExtra("msg").split("/")[3];
						if(!data.equals("null")){
							String[] quota_list = data.split("#");
							quota_data.setText(quota_list[0]);
							quota_text.setText(quota_list[2]);
							action_number = quota_list[1];
							if(action_number.equals("0")){
								warring_action.setText("只报警");
							}else{
								warring_action.setText("报警并跳闸");
							}	
						}else{
							quota_data.setText("");
						}
						if(progressDialog_1.isShowing()){
							progressDialog_1.dismiss();
						}
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
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if (msg.what == 1) {
				String bundle_msg = msg.getData().getString("content");
				if(bundle_msg.split("/")[1].equals(str_mac)){
					switch(bundle_msg.split("/")[2]){
					case "get_quota"://一次性定时任务数据
						String data = bundle_msg.split("/")[3];
						if(!data.equals("null")){
							String[] quota_list = data.split("#");
							quota_data.setText(quota_list[0]);
							quota_text.setText(quota_list[2]);
							action_number = quota_list[1];
							if(action_number.equals("0")){
								warring_action.setText("只报警");
							}else{
								warring_action.setText("报警并跳闸");
							}	
						}else{
							quota_data.setText("");
						}
						if(progressDialog_1!=null){
							progressDialog_1.dismiss();
						}
						break;
					}
				}
			}
		
			
			
		}catch(Exception e){
			e.printStackTrace();
			}
		}
	};

	
	
}
