package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBInfraredCode;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.model.MyInfraredCode;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.timerpicker.PickerView;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;
/**添加中央空调定时任务
 * 时间+01：开机     时间+02：关机
 * rand integer,mac text,timeNow text,version text,switch text,model text,temputer text,speed text,updown text,leftright text,weeks text
 * */
@SuppressLint("NewApi")
public class Kt_Frag_Center_3_add extends Activity implements OnClickListener,
		OnValueChangeListener, Formatter {
	private int onoff=0;
	private String switch_onoff = "01";
	private ImageView imge_onoff ;
	private CheckBox check_0,check_1,check_2,check_3,check_4,check_5,check_6;
	private String week_0,week_1,week_2,week_3,week_4,week_5,week_6;
	private int time_1=8,time_2=30;
	private String str_mac,str_type,str_name,str_ver;
	private String hwa_1 = null;
	private Handler handler;
	private CustomProgressDialog progressDialog;
	private List weekdays=new ArrayList();
	private String rcb;
	private String URL_PATH;
	private PickerView minute_pv;
	private PickerView second_pv;
	/**定时任务编号+时间*/
	private String time_num;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.kt_frag_center3_add);
			//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
			if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
	            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	        }
			
			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			str_ver = intent.getStringExtra("ver");
			URL_PATH = URL_help.getInstance().getUrl_address();
			init_PickerView();
			init_first();
			
			handler=new Handler(){
				public void handleMessage(Message mesg){
					try{
						super.handleMessage(mesg);
						Bundle bundle=mesg.getData();
						String result=bundle.getString("SD");
						inintData();
						System.out.println("result"+result);
						if(result.equals("success")){
							progressDialog.cancel();
							finish();
							//((MessageAty)getActivity()).showkt_frg2();
							Toast.makeText(Kt_Frag_Center_3_add.this, "添加成功！", Toast.LENGTH_SHORT).show();
						}else if(result.equals("fail")){
							progressDialog.cancel();
							//((MessageAty)getActivity()).showkt_frg2();
							finish();
							Toast.makeText(Kt_Frag_Center_3_add.this, "添加失败！", Toast.LENGTH_SHORT).show();
						}else if(result.equals("Full_or_Same")){
							progressDialog.cancel();
							finish();
							Toast.makeText(Kt_Frag_Center_3_add.this, "定时任务存储已达上限或存在相同时间段任务！", Toast.LENGTH_LONG).show();
						}
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
					
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**初始化时间控件 */
	private void init_PickerView(){
		minute_pv = (PickerView) findViewById(R.id.minute_pv);
		second_pv = (PickerView) findViewById(R.id.second_pv);
		List<String> data = new ArrayList<String>();
		List<String> seconds = new ArrayList<String>();
		for (int i = 0; i < 24; i++)
		{
			data.add(i < 10? "0" + i : ""+i);
		}
		for (int i = 0; i < 60; i++)
		{
			seconds.add(i < 10 ? "0" + i : "" + i);
		}
		minute_pv.setData(data);
		minute_pv.setOnSelectListener(new PickerView.onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				time_1 = Integer.parseInt(text);
			}
		});
		second_pv.setData(seconds);
		second_pv.setOnSelectListener(new PickerView.onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				time_2 = Integer.parseInt(text);
			}
		});
		minute_pv.setSelected(8);
		second_pv.setSelected(30);
	}

	
	/**初始化界面控件*/
	private void init_first(){
	
		//设置，取消按钮
		findViewById(R.id.btn_submit).setOnClickListener(this);
		findViewById(R.id.button_back).setOnClickListener(this);
		//findViewById(R.id.btn_cancel).setOnClickListener(this);
		
		//开关选择
		imge_onoff=(ImageView) findViewById(R.id.imge_onoff);
		imge_onoff.setOnClickListener(this);
		imge_onoff.setImageResource(getOnOffResource(onoff));
		
		
		//选择每周的天数按钮
		check_0=(CheckBox) findViewById(R.id.chk_0);
		check_0.setOnClickListener(this);
		check_1=(CheckBox) findViewById(R.id.chk_1);
		check_1.setOnClickListener(this);
		check_2=(CheckBox) findViewById(R.id.chk_2);
		check_2.setOnClickListener(this);
		check_3=(CheckBox) findViewById(R.id.chk_3);
		check_3.setOnClickListener(this);
		check_4=(CheckBox) findViewById(R.id.chk_4);
		check_4.setOnClickListener(this);
		check_5=(CheckBox) findViewById(R.id.chk_5);
		check_5.setOnClickListener(this);
		check_6=(CheckBox) findViewById(R.id.chk_6);
		check_6.setOnClickListener(this);
		
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
	
	@Override
	public void onClick(View v) {
		try{
		int rand=0;
		switch(v.getId()){
		case R.id.btn_submit:
		try{		
			if(weekdays.size()==0){
				Toast.makeText(Kt_Frag_Center_3_add.this, "您还未选择日时段！", Toast.LENGTH_SHORT).show();
				return;
			}
			progressDialog=CustomProgressDialog.createDialog(Kt_Frag_Center_3_add.this);
			progressDialog.setMessage("正在下载红外码");
			progressDialog.show();
			
			if(onoff==0){
				switch_onoff="01";
			}else{
				switch_onoff="02";
			}
			
			System.out.println("weekdays------------"+weekdays.toString());
			new  Thread(){
				public void run(){
					//读取设备信息
					try{
						runThread();

				}catch(Exception e){
					e.printStackTrace();
					progressDialog.cancel();	
				}
				}
			}.start();
			
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
			
		case R.id.button_back:
			finish();
			break;
		case R.id.chk_0:
			if(check_0.isChecked()==true){
				weekdays.add("七");
				System.out.println("remove weekdays.add(7)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("七")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_1:
			if(check_1.isChecked()==true){
				weekdays.add("一");
				System.out.println("remove weekdays.add(1)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("一")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_2:
			if(check_2.isChecked()==true){
				weekdays.add("二");
				System.out.println("remove weekdays.add(2)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_3:
			if(check_3.isChecked()==true){
				weekdays.add("三");
				System.out.println("remove weekdays.add(3)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("三")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_4:
			if(check_4.isChecked()==true){
				weekdays.add("四");
				System.out.println("remove weekdays.add(4)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("四")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_5:
			if(check_5.isChecked()==true){
				weekdays.add("五");
				System.out.println("remove weekdays.add(5)");
			}else{
				if(weekdays!=null){ 
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("五")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
		case R.id.chk_6:
			if(check_6.isChecked()==true){
				weekdays.add("六");
				System.out.println("remove weekdays.add(6)");
			}else{
				if(weekdays!=null){
				for(int i=0;i<weekdays.size();i++){
					if(weekdays.get(i).equals("六")){
						weekdays.remove(i);
						--i;
							}
						}
					}
				}
			break;
			
		case R.id.imge_onoff:
			onoff=onoff+1;
			if(onoff>1){
				onoff=0;
			}
			if(onoff==0){
				Toast.makeText(Kt_Frag_Center_3_add.this, "开机", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(Kt_Frag_Center_3_add.this, "关机", Toast.LENGTH_SHORT).show();
			}
			imge_onoff.setImageResource(getOnOffResource(onoff));
			break;

		}}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
		//获取开关对应图标
		private int getOnOffResource(int onoff){
			int imgs = 0;
			switch(onoff){
			case 0:
				imgs=R.drawable.cycle_air_on;
				break;
			case 1:
				imgs=R.drawable.cycle_air_off;
				break;
			}
			return imgs;
		}
		
		
		

		//设置完后重新初始化定义的数据
		private void inintData(){
			onoff=0;
			time_1=8;
			time_2=30;
			weekdays.clear();
			check_0.setChecked(false);
			check_1.setChecked(false);
			check_2.setChecked(false);
			check_3.setChecked(false);
			check_4.setChecked(false);
			check_5.setChecked(false);
			check_6.setChecked(false);
		}

	@Override
	public String format(int value) {
		String tmpStr = String.valueOf(value);
		if (value < 10)
			tmpStr = "0" + tmpStr;
		return tmpStr;
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		Toast.makeText(Kt_Frag_Center_3_add.this, "" + newVal, Toast.LENGTH_SHORT)
				.show();
	}
	class onvalchange_1 implements OnValueChangeListener{

		@Override
		public void onValueChange(NumberPicker arg0, int arg1, int newVal) {
			// TODO Auto-generated method stub
			time_1=newVal;	
		}
		
	}

	class onvalchange_2 implements OnValueChangeListener{
		@Override
		public void onValueChange(NumberPicker arg0, int arg1, int newVal) {
			// TODO Auto-generated method stub
			time_2=newVal;
		}
		
	}
	
	public void runThread(){//发送定时任务到控制器；
		try{
			//**********************************************
			for(int i=0;i<weekdays.size();i++){
				switch(weekdays.get(i).toString()){
				case "七":
					rcb="07";
					break;
				case "一":
					rcb="01";
					break;
				case "二":
					rcb="02";
					break;
				case "三":
					rcb="03";
					break;
				case "四":
					rcb="04";
					break;
				case "五":
					rcb="05";
					break;
				case "六":
					rcb="06";
					break;
				}
			//读取日程
			String rst_hw = null;
			MyOrder odToSend_1= OrderList.getSendByDeviceType(str_type,
					str_mac, OrderList.READ_CENTER_WEEK_TASK,rcb);
			String rst_raw_1 = OrderManager.getInstance().sendOrder(odToSend_1,
					OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
			if(rst_raw_1==null){
				System.out.println("发送失败");
				Message mesg=new Message();
				Bundle data=new Bundle();
				data.putString("SD", "fail");
				mesg.setData(data);
				Kt_Frag_Center_3_add.this.handler.sendMessage(mesg);
			}else{
				String rst_status = OrderManager.getInstance().getItemByOrder(rst_raw_1,
						"status", -1);
				Message mesg=new Message();
				Bundle data=new Bundle();
				
				if(rst_status.equals("200")){
					String rst_1 = OrderManager.getInstance().getItemByOrder(rst_raw_1,
							"result", -1);
					if(rst_1!=null){ 
						//红外码编号，加分钟，小时； 新版为 nn(小时 分钟 编号)+hhmm
						time_num=switch_onoff+format(time_2)+format(time_1);
						int[] rcb_list=getRCB_tesk(rst_1,time_num);
						if(rcb_list==null){//定时任务存储达上限或存在相同时间段命令；
							data.putString("SD", "Full_or_Same");
							mesg.setData(data);
							Kt_Frag_Center_3_add.this.handler.sendMessage(mesg);  
							return;
						}
						System.out.println("/*/*/*/*/*/*/*定时任务编号："+"/"+format(time_1)+":"+format(time_2));
					    //截取日程表内一段数据替换为当前设置的日程表数据;(最新的控制器的日程表数据需要把时间按从小到大的顺序排列好)
						rst_hw=rst_1.substring(0,(int) rcb_list[0])+time_num+rst_1.substring((int) rcb_list[1],rst_1.length());
					}else{
						data.putString("SD", "fail");
						mesg.setData(data);
						Kt_Frag_Center_3_add.this.handler.sendMessage(mesg); 
						return;
					}
				}else{
					data.putString("SD", "fail");
					mesg.setData(data);
					Kt_Frag_Center_3_add.this.handler.sendMessage(mesg);  
					return;
				}
				
				
			}
			
				
			System.out.println("rst_hw//////////////"+rst_hw);
			/****************************************************/
			System.out.println("运行到此处1");
			rst_hw = rcb_sort(rst_hw);
			//发送任务日程 ：时间+编号
			MyOrder odToSend=OrderList.getSendByDeviceType(str_type, 
					str_mac, OrderList.WRITE_CENTER_WEEK_TASK,rcb+rst_hw);	
			
			String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
					OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
			
			System.out.println("rst_raw: "+rst_raw);
			
			if(rst_raw==null){
				System.out.println("发送失败");
				Message mesg=new Message();
				Bundle data=new Bundle();
				data.putString("SD", "fail");
				mesg.setData(data);
				Kt_Frag_Center_3_add.this.handler.sendMessage(mesg);
			}else{
				Message mesg=new Message();
				Bundle data=new Bundle();			
				String status = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
				if(status.equals("200")){
					try{
					System.out.println("运行到此处2");
					if(i==weekdays.size()-1){
						//把红外码字段存储到数据库
						DBInfraredCode dbinfrared=new DBInfraredCode(Kt_Frag_Center_3_add.this);
						MyInfraredCode myinfrared=new MyInfraredCode(Integer.parseInt(switch_onoff),str_mac,format(time_1)+":"+format(time_2), str_ver,switch_onoff,null,null,null,null,null,weekdays.toString());
						dbinfrared.Insert(myinfrared);
						//表号  + 开关机编号（01，02）+ 表号 + 时间 + 空调类型
						String task_str = "addCycleTask/"+str_mac+"#"+switch_onoff+"&"+str_mac+"&"+format(time_1)+":"+format(time_2)+"&"+"0a0001a4r5"+"&"+weekdays.toString();

						add_Task_Data(task_str);
						
						data.putString("SD", "success");
						mesg.setData(data);
						Kt_Frag_Center_3_add.this.handler.sendMessage(mesg);
					}else{
						continue;
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					System.out.println("运行到此处3");
					data.putString("SD", "fail");
					mesg.setData(data);
					Kt_Frag_Center_3_add.this.handler.sendMessage(mesg);
					return;
	
				}
			  }
			}//循环发送
		}catch(Exception e){
			e.printStackTrace();
			progressDialog.cancel();
		}
	}
	
	/**发送以设置好的定时任务到服务器*/
	private void add_Task_Data(final String data){//addCycleTask/str_mac#3（红外码编号）&002014110119&08:15&0003（空调类型）&开&制冷（模式）&24（温度）&三级（风速）&上下（上下扫风开）&左右（左右扫风开）&[一, 二, 三, 四, 五
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
							"0a0001a4r5/"+str_mac,"tasks_/"+data);		
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a4r5/"+str_mac,"tasks_/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	
	
	
	/**读取回来的日程表数据为一串的000000，3个字节为一个日程表*/                                                                                                                                                                                                        
	public int[] getRCB_tesk(String result,String rcb_task){
		System.out.println("--------++++++++++-----"+rcb_task);
		int[] rcb_data=new int[]{0,6};
		int s=0,e=6; 
		for(int i=0;i<result.length()/6;i++){
			if(result.substring(s, e).equals(rcb_task)){//如果日程表中存在相同的任务则返回null
				return null;
			}
			
			if(result.substring(s, e).equals("000000")){
				rcb_data[0] = s;
				rcb_data[1] = e;
				break;
			}
			s=s+6;
			e=e+6;
		}
		if(result.indexOf("000000")<0){
//			rcb_data[0] = 60;
//			rcb_data[1] = 66;
			return null;
		}
		return rcb_data;
	}
	
	/**读取回来的日程表数据为一串的000000，3个字节为一个日程表*/                                                                                                                                                                                                        
	public int[] getRCB_tesk_list(String result){
		int[] rcb_data=new int[]{0,6};
		int s=0,e=6; 
		for(int i=0;i<result.length()/6;i++){
			if(result.substring(s, e).equals("000000")){
				rcb_data[0] = s;
				rcb_data[1] = e;
				break;
			}
			s=s+6;
			e=e+6;
		}
		if(result.indexOf("000000")<0){
			rcb_data[0] = 60;
			rcb_data[1] = 66;
		}
		return rcb_data;
	}
	
	/**把日程表数据按从小到大的顺序进行排列*/
	private String rcb_sort(String rcb_str){
		String[] rcb_list = new String[rcb_str.length()/6];
		String result_rcb="";
		int[] list = getRCB_tesk_list(rcb_str);
		System.out.println("list[0]:"+list);
		if( (int) list[0]>6 ){
			for(int i=0 ; i<rcb_str.length()/6 ; i++){
				rcb_list[i] = rcb_str.substring(i*6, i*6+6);
			}
			for(int x=0;x<rcb_list.length;x++){
				System.out.println("======================================"+rcb_list[x]);
			}
			for(int j=0;j<list[1]/6;j++){
				for(int k=j+1;k<list[1]/6-1;k++){
					String str_ = rcb_list[j];
					int l1 = Integer.parseInt(receive_rcb_str(rcb_list[j]));
					int l2 = Integer.parseInt(receive_rcb_str(rcb_list[k]));
					if(l1>l2){
						rcb_list[j] = rcb_list[k];
						rcb_list[k] = str_;
					}
				}
					
			}
		}else{
			return rcb_str;
		}
		for(int i=0;i<rcb_list.length;i++){
			result_rcb =result_rcb+rcb_list[i];
		}
		return result_rcb;
	}
	
	private String receive_rcb_str(String rcb_str){
		String rcb_rece="";
		try{
			rcb_rece = rcb_str.substring(2, 6);
			rcb_rece = rcb_rece.substring(2, 4)+rcb_rece.substring(0, 2);
		}catch(Exception e){
			e.printStackTrace();
		}
		return rcb_rece;
	} 
	
}
