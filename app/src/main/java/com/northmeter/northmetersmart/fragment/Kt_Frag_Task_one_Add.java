package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adapter.NumericWheelAdapter;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.utils.Utils;
import com.northmeter.northmetersmart.widget.WheelView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 设置空调一次性定时任务*/
public class Kt_Frag_Task_one_Add extends BaseActivity implements OnClickListener{
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private WheelView hour;
	private WheelView mins;
	private WheelView second;
	
	
	private String str_mac,str_type;

	private TextView txt_data,txt_time,txt_continue_hour,action_1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.kt_frag_task_one);
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_type = intent.getStringExtra("type");
			
			init_view();
		}catch(Exception e){
			e.printStackTrace();
			}
	}

	
	/**初始化界面*/
	private void init_view(){
		findViewById(R.id.button_back).setOnClickListener(this);//返回
		findViewById(R.id.but_set).setOnClickListener(this);//发送
		
		txt_data = (TextView) findViewById(R.id.txt_data);//日期
		txt_data.setOnClickListener(this);
		
		txt_time = (TextView) findViewById(R.id.txt_time);//时间
		txt_time.setOnClickListener(this);
		
		txt_continue_hour = (TextView) findViewById(R.id.txt_continue_hour);//持续时间
		txt_continue_hour.setOnClickListener(this);
		
		action_1 = (TextView) findViewById(R.id.action_1);//执行动作
		action_1.setOnClickListener(this);
		
		/**初始化时间选择器的时间为当前时间*/
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		int curDate = c.get(Calendar.DATE);
		txt_data.setText(curYear+"-"+curMonth+"-"+curDate);
		System.out.println("------------"+curYear+"-"+curMonth+"-"+curDate);
		
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second= c.get(Calendar.SECOND);
		txt_time.setText(hour+":"+minute+":"+second);
		System.out.println("------------"+hour+":"+minute+":"+second);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.but_set://发送
				add_Task_One(sendOneTaskMsg());
				finish();
				break;
			case R.id.button_back://返回
				finish();
				break;
			case R.id.txt_data://日期
				showDateDialog();
				break;
			case R.id.txt_time://时间
				showTimeDialog();
				break;
			case R.id.action_1://跳合闸选择
				showActionDialog();
				break;
			case R.id.txt_continue_hour:
				showContinueTimeDialog();//持续时间
				break;
				
				default:
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 发送定时任务*/
	/**发送以设置好的定时任务到服务器*/
	private void add_Task_One(final String data){//onetask/addBatchTask/346BD3D72B911494329840280#2017-5-9 11:12:3#2017-05-09 16:12:03#hz# #building=*&meter=150721023813&eqpt_type=0a0001aa7k
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(Kt_Frag_Task_one_Add.this,
							str_type+"/"+str_mac,"onetask/"+data);	
//					PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	/**
	 * 组合一次性定时任务数据*/
	@SuppressLint("NewApi")
	private String sendOneTaskMsg(){
		//onetask/addBatchTask/0802021611081728289SXA#2016-11-08 17:30:00#2016-11-08 18:00:00#tz# #building=0001000100010001&meter=*&eqpt_type=0a0001aa7k	
		String send_msg = "";
		try{
			String thz = null;
			if(action_1.getText().toString().equals("跳闸")){
				thz = "tz";
			}else{
				thz = "hz";
			}
			String data = txt_data.getText().toString();//日期
			String time = txt_time.getText().toString();//时间
			String data_time = data+" "+time;
			String continue_time = getContinueTime(data,time,txt_continue_hour.getText().toString());
			
			//计算任务编号
			long task_time = System.currentTimeMillis();//系统时间戳
			//获取设备唯一id,可能有些设备获取为unknow，在一般情况下暂时先使用这个id
			String SerialNumber = android.os.Build.SERIAL; 
			String task_id = SerialNumber+task_time;
			send_msg = "addBatchTask/"+task_id+"#"+data_time+"#"+continue_time+"#"+thz+"# #building=*&meter="+str_mac+"&eqpt_type="+str_type;
		}catch(Exception e){
			e.printStackTrace();
		}
		return send_msg;
	}
	
	/**在设置时间的基础上加上持续时间*/
	/**在设置时间的基础上加上持续时间*/
	private static String getContinueTime(String data, String time,String continue_time_1){
		String continue_time = null;
		try{
			String res = Utils.dateToStamp(data+" "+time);
			long result_time = Long.parseLong(res)+Integer.parseInt(continue_time_1)*3600*1000;		
			continue_time = Utils.stampToDate(String.valueOf(result_time));
		}catch(Exception e){
			e.printStackTrace();
		}
		return continue_time;
	}
	
	/**
	 * 选择持续时间
	 * */
	private void showContinueTimeDialog(){
		final AlertDialog dialog = new AlertDialog.Builder(Kt_Frag_Task_one_Add.this)
				.create();
		dialog.show();
		Window window = dialog.getWindow();
		// 设置布局
		window.setContentView(R.layout.task_one_continue_time);
		// 设置宽高
		window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		// 设置弹出的动画效果
		window.setWindowAnimations(R.style.AnimBottom);
		
		ListView listview_val = (ListView) window.findViewById(R.id.listview_val);
		
		final String [] arr_data = new String[72];
		for(int i=0;i<72;i++){
			arr_data[i] = (i+1)+"";
		}
		ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(this,R.layout.task_one_continue_item,R.id.item_textview,arr_data);
		listview_val.setAdapter(arrayadapter);
		listview_val.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				txt_continue_hour.setText(arr_data[arg2]);
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
	
	
	/**
	 * 选择跳合闸
	 */
	private void showActionDialog(){
		final AlertDialog dialog = new AlertDialog.Builder(Kt_Frag_Task_one_Add.this)
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
		
		action_on.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				action_1.setText("合闸");
				dialog.cancel();
			}
		});
		action_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				action_1.setText("跳闸");
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
	
	
	
	/**
	 * 显示时间
	 */
	private void showTimeDialog(){
		final AlertDialog dialog = new AlertDialog.Builder(Kt_Frag_Task_one_Add.this)
				.create();
		dialog.show();
		Window window = dialog.getWindow();
		// 设置布局
		window.setContentView(R.layout.timepick);
		// 设置宽高
		window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		// 设置弹出的动画效果
		window.setWindowAnimations(R.style.AnimBottom);

		hour = (WheelView) window.findViewById(R.id.hour);
		initHour();
		mins = (WheelView) window.findViewById(R.id.mins);
		initMins();
		second = (WheelView) window.findViewById(R.id.second);
		initSecond();
		
		Calendar c = Calendar.getInstance();
		// 设置当前时间
		hour.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
		mins.setCurrentItem(c.get(Calendar.MINUTE));
		second.setCurrentItem(c.get(Calendar.SECOND));
		
		hour.setVisibleItems(7);
		mins.setVisibleItems(7);
		second.setVisibleItems(7);

		// 设置监听
		Button ok = (Button) window.findViewById(R.id.set);
		Button cancel = (Button) window.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = hour.getCurrentItem() + ":"+ mins.getCurrentItem()+":"+second.getCurrentItem();
				txt_time.setText(str);
				Toast.makeText(Kt_Frag_Task_one_Add.this, str, Toast.LENGTH_LONG).show();
				dialog.cancel();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

	/**
	 * 显示日期
	 */
	private void showDateDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(Kt_Frag_Task_one_Add.this)
				.create();
		dialog.show();
		Window window = dialog.getWindow();
		// 设置布局
		window.setContentView(R.layout.datapick);
		// 设置宽高
		window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		// 设置弹出的动画效果
		window.setWindowAnimations(R.style.AnimBottom);


		Calendar c = Calendar.getInstance();
		final int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		int curDate = c.get(Calendar.DATE);
		year = (WheelView) window.findViewById(R.id.year);
		initYear(curYear);
		month = (WheelView) window.findViewById(R.id.month);
		initMonth();
		day = (WheelView) window.findViewById(R.id.day);
		initDay(curYear,curMonth);


		year.setCurrentItem(curYear-curYear);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);
		year.setVisibleItems(7);
		month.setVisibleItems(7);
		day.setVisibleItems(7);

		// 设置监听
		Button ok = (Button) window.findViewById(R.id.set);
		Button cancel = (Button) window.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = (year.getCurrentItem()+curYear) + "-"+ (month.getCurrentItem()+1)+"-"+(day.getCurrentItem()+1);
				Toast.makeText(Kt_Frag_Task_one_Add.this, str, Toast.LENGTH_LONG).show();
				txt_data.setText(str);
				dialog.cancel();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
		// 因为我们用的是windows的方法，所以不管ok活cancel都要加上“dialog.cancel()”这句话，
		// 不然有程序崩溃的可能，仅仅是一种可能，但我们还是要排除这一点，对吧？
		// 用AlertDialog的两个Button，即使监听里什么也不写，点击后也是会吧dialog关掉的，不信的同学可以去试下

	}
	
	/**
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}
	
	
	/**
	 * 初始化年
	 */
	private void initYear(int curYear) {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,curYear, curYear+50);
		numericWheelAdapter.setLabel(" 年");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		year.setViewAdapter(numericWheelAdapter);
		year.setCyclic(true);
	}

	/**
	 * 初始化月
	 */
	private void initMonth() {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,1, 12, "%02d");
		numericWheelAdapter.setLabel(" 月");
		//numericWheelAdapter.setTextSize(15);  设置字体大小
		month.setViewAdapter(numericWheelAdapter);
		month.setCyclic(true);
	}

	/**
	 * 初始化天
	 */
	private void initDay(int arg1, int arg2) {
		NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(this,1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel(" 日");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		day.setViewAdapter(numericWheelAdapter);
		day.setCyclic(true);
	}

	/**
	 * 初始化时
	 */
	private void initHour() {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,0, 23, "%02d");
		numericWheelAdapter.setLabel(" 时");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		hour.setViewAdapter(numericWheelAdapter);
		hour.setCyclic(true);
	}

	/**
	 * 初始化分
	 */
	private void initMins() {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,0, 59, "%02d");
		numericWheelAdapter.setLabel(" 分");
//		numericWheelAdapter.setTextSize(15);  设置字体大小
		mins.setViewAdapter(numericWheelAdapter);
		mins.setCyclic(true);
	}
	
	/**
	 * 初始化秒*/
	private void initSecond(){
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,0, 59, "%02d");
		numericWheelAdapter.setLabel(" 秒");
//		numericWheelAdapter.setTextSize(15);  设置字体大小
		second.setViewAdapter(numericWheelAdapter);
		second.setCyclic(true);
		
	}

	
	
}
