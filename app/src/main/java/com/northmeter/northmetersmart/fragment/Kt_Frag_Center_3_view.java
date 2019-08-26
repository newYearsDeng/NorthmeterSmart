package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.Center_Task_viewAdp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBInfraredCode;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.model.F_Kt_Lv_Model;
import com.northmeter.northmetersmart.model.MyInfraredCode;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

/** 显示中央空调定时任务*/
public class Kt_Frag_Center_3_view extends BaseActivity implements OnClickListener,OnItemClickListener {
	
	private View view;
	ListView listView;
	List<F_Kt_Lv_Model> models = new ArrayList<F_Kt_Lv_Model>();;
	private CustomProgressDialog progressDialog;

	private CheckBox check_onoff;
	private F_Kt_Lv_Model model;
	private String str_mac,str_type,str_name,str_ver;
	Center_Task_viewAdp adp ;
	private Handler handler;
	private String URL_PATH;
	private ReceiveTool receiver;
	private TextView edit_view;
	private RelativeLayout relativelayout3;//最下方编辑栏
	private boolean visibility = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.f_kongtiao_1);

			Intent intent = getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			str_ver = intent.getStringExtra("ver");
			
			listView = (ListView) findViewById(R.id.listView1);
			relativelayout3 = (RelativeLayout) findViewById(R.id.relativelayout3);
			relativelayout3.setOnClickListener(this);
			findViewById(R.id.relativeLayout2).setOnClickListener(this);//添加定时任务
			findViewById(R.id.ima_remove).setOnClickListener(this);//删除按钮
			findViewById(R.id.img_choose).setOnClickListener(this);//全选按钮
			findViewById(R.id.button2).setOnClickListener(this);//返回
			findViewById(R.id.ima_freshen).setOnClickListener(this);//刷新
			findViewById(R.id.img_sdcscsh).setOnClickListener(this);//日程表初始化
			edit_view = (TextView)findViewById(R.id.edit_view);//编辑按钮
			
			edit_view.setOnClickListener(this);
			URL_PATH = URL_help.getInstance().getUrl_address();
			read_Task_Data();//刷新定时任务列表
			RegisterBroad();//注册接收广播
		
			init_view(visibility);
			
			handler=new Handler(){
				public void handleMessage(Message mesg){
					super.handleMessage(mesg);
					Bundle bundle=mesg.getData();
					String result=bundle.getString("DETrcb");
					switch(result){
					case "delete_fail":
						Toast.makeText(Kt_Frag_Center_3_view.this, "删除失败", Toast.LENGTH_SHORT).show();
						break;
					case "delete_success":
						Toast.makeText(Kt_Frag_Center_3_view.this, "删除成功", Toast.LENGTH_SHORT).show();
						adp.notifyDataSetChanged();
						break;
					case "correct_Rcb_fail":
						Toast.makeText(Kt_Frag_Center_3_view.this, "日程表初始化失败", Toast.LENGTH_SHORT).show();
						break;
					case "correct_Rcb_success":
						Toast.makeText(Kt_Frag_Center_3_view.this, "日程表初始化完成", Toast.LENGTH_SHORT).show();
						break;
						default:
							break;
					}
					
	 				progressDialog.cancel();
				}
			};
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private void init_view(boolean visibility){
		try{			
			models.clear();
			DBInfraredCode dbinfrared=new DBInfraredCode(Kt_Frag_Center_3_view.this);
			List<MyInfraredCode> infrared=dbinfrared.Query(str_mac);
			if(infrared!=null){
				for(int i=0;i<infrared.size();i++){
					model=new F_Kt_Lv_Model();
					model.setTime(infrared.get(i).getTime());
					String ampm=timetoint(infrared.get(i).getTime());
					model.setAmpm(ampm);
					model.setWeekday(infrared.get(i).getWeeks());
					model.setTxt1(infrared.get(i).getSwitchs());//开关机
					model.setTxt2(infrared.get(i).getTemputer()); 
					model.setTxt3(infrared.get(i).getModel());
					model.setTxt4(infrared.get(i).getSpeed());
					model.setTxt5(infrared.get(i).getUpdown());
					model.setTxt6(infrared.get(i).getLeftright());
					model.setId(infrared.get(i).getRand());
					model.setVisibility(visibility);
					models.add(model);
				}
			}
			adp = new Center_Task_viewAdp(Kt_Frag_Center_3_view.this,models);
			adp.notifyDataSetChanged();
			listView.setAdapter(adp);
			listView.setOnItemClickListener(this);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
//	@Override
//	public void onDestroyView() {
//		ViewGroup viewGroup = (ViewGroup) view.getParent();
//		viewGroup.removeView(view);
//		super.onDestroyView();
//	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init_view(visibility);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		    case R.id.edit_view:
		    	if(edit_view.getText().equals("编辑")){
		    		edit_view.setText("取消");
		    		visibility = true;
		    		init_view(visibility);
		    		relativelayout3.setVisibility(View.VISIBLE);
		    	}else{
		    		edit_view.setText("编辑");
		    		visibility = false;
		    		init_view(visibility);
		    		relativelayout3.setVisibility(View.GONE);
		    	}
		    	break;
		    case R.id.ima_freshen:
		    	read_Task_Data();//刷新定时任务列表
		    	break;
		    	
		    case R.id.img_choose://全选
		    	for (int i = 0; i < models.size(); i++) {
		    		F_Kt_Lv_Model model = models.get(i);
					if (model.isCheck()){
						model.setCheck(false);
					}else{
						model.setCheck(true);
					}
						
				}
		    	adp.notifyDataSetChanged();
		    	break;		    	
			case R.id.button2:
				finish();
				break;
			case R.id.relativeLayout2://添加
				Intent intent_1 = new Intent(Kt_Frag_Center_3_view.this,Kt_Frag_Center_3_add.class);
				intent_1.putExtra("mac", str_mac);
				intent_1.putExtra("type", str_type);
				intent_1.putExtra("name", str_name);
				intent_1.putExtra("ver", str_ver);
				startActivity(intent_1);
				//((MessageAty) this.getActivity()).ShowAdd();
				break;	 
			case R.id.ima_remove://删除
				try{	
				boolean flag = false;
				for(int j=0;j<models.size();j++){
					if(models.get(j).isCheck()==true){
						flag =true;
					}
				}
				if(flag==false){
					Toast.makeText(Kt_Frag_Center_3_view.this, "未选择任何任务删除！", Toast.LENGTH_SHORT).show();
					return;
				}
				progressDialog=CustomProgressDialog.createDialog(Kt_Frag_Center_3_view.this);
				progressDialog.setMessage("正在删除日程表");
				progressDialog.show();
				new Thread(){
					public void run(){
						try{
							List<F_Kt_Lv_Model> modelsToDelete = new ArrayList<F_Kt_Lv_Model>();
							boolean flag = false;
							for (int i = 0; i < models.size(); i++){
								System.out.println("设备总数量=" + models.get(i).getId());
								if(models.get(i).isCheck()==true){
									//添加一个需要删除的设备到modelsToDelete
									modelsToDelete.add(models.get(i));
							
								}
							}
							for (int i = 0; i < modelsToDelete.size(); i++){
								System.out.println("设备总数量=" + modelsToDelete.get(i).getId());
								if(modelsToDelete.get(i).isCheck()==true){
									//添加一个需要删除的设备到modelsToDelete
									String week_1=modelsToDelete.get(i).getWeekday().toString().replaceAll(" ","");
									week_1=week_1.replace("[", "");
									week_1=week_1.replace("]", "");
									String[] week=week_1.split(",");
									flag=deleteRcb(modelsToDelete.get(i).getId(),str_mac,week,modelsToDelete.get(i).getTime());
									
									System.out.println("flag---------"+flag);
									if(flag==false){
										Message mesg=new Message();
										Bundle data=new Bundle();
										data.putString("DETrcb", "delete_fail");
										mesg.setData(data);
										Kt_Frag_Center_3_view.this.handler.sendMessage(mesg);
										break;
									}else{
										int code = modelsToDelete.get(i).getId();
										String fs_code;
										if(code<10){
											fs_code = "0"+code;
										}else{
											fs_code = code+"";
										}
										
										//表号  + 开关机编号（01，02）+ 表号 + 时间 + 空调类型 + 天数
										String task_str = "delCycleTask/"+str_mac+"#"+fs_code+"&"+str_mac+"&"+modelsToDelete.get(i).getTime()+"&"+"0a0001a4r5"+"&"+modelsToDelete.get(i).getWeekday();
										delete_Task_Data(task_str);
										
										System.out.println("添加需要删除的设备："+modelsToDelete.get(i).getId());
										
										}
									} 
								}
							
							if(flag==true){
								Message mesg=new Message();
								Bundle data=new Bundle();
								data.putString("DETrcb", "delete_success");
								mesg.setData(data);
								Kt_Frag_Center_3_view.this.handler.sendMessage(mesg);
							}
							
						}catch(Exception e){
							e.printStackTrace();
						}	
					}
				}.start();
						
				}catch(Exception e){
					e.printStackTrace();
					}
				break;  
			case R.id.img_sdcscsh://日程表初始化
				progressDialog=CustomProgressDialog.createDialog(Kt_Frag_Center_3_view.this);
				progressDialog.setMessage("初始化日程表");
				progressDialog.show();
				correct_rcb();
				break;
				
			default:
				break;
		}
	}     
	
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try{
			F_Kt_Lv_Model model1=models.get(arg2);
			//Toast.makeText(getActivity(), model1.getId()+"", Toast.LENGTH_SHORT).show();
			if(model1.isCheck()==false){
				model1.setCheck(true);
			}else{
				model1.setCheck(false);
			}   
			adp.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String timetoint(String time){
		time=time.replace(":", "");
		int timenew=Integer.parseInt(time);
		if(timenew<1200){
			return "上午";
		}else{
			return "下午";
		}
	}
	
	/**日程表初始化*/
	private void correct_rcb(){
		try{
			new Thread(){
				public void run(){
					//-----
					MyOrder odToSend;
					switch (str_type) {
					case Type_Entity.Central_air_conditioning://zigbee中央空调控制器
						odToSend = OrderList.getSendByDeviceType(str_type,
								str_mac, OrderList.CSH_ZIGBEE_AIR_RCB,"");			
						break;
					default:
						odToSend = null;
						break;
					}
		
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,OrderList.USER_PASSWORD, URL_PATH, "utf-8");
					if(rst_raw!=null){
						String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1); 
						if(rst_error.equals("200")){
							//删除所有定时任务
							delAllCycleTask(str_mac);
							
							Message msg=new Message();
							Bundle data=new Bundle();
							data.putString("DETrcb", "correct_Rcb_success");
							msg.setData(data);		
							Kt_Frag_Center_3_view.this.handler.sendMessage(msg) ;
							}else{
								Message msg=new Message();
								Bundle data=new Bundle();
								data.putString("DETrcb", "correct_Rcb_fail");
								msg.setData(data);		
								Kt_Frag_Center_3_view.this.handler.sendMessage(msg) ;
							}
						}else{
							Message msg=new Message();
							Bundle data=new Bundle();
							data.putString("DETrcb", "correct_Rcb_fail");
							msg.setData(data);		
							Kt_Frag_Center_3_view.this.handler.sendMessage(msg) ;
						}
					}
				}.start();
			}catch(Exception e){
				e.printStackTrace();
				}
	}		
	
	/**删除以设置好的所有定时任务到服务器*/
	private void delAllCycleTask(final String str_mac){//tasks_/delAllCycleTask/002014110119

		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
							"0a0001a4r5/"+str_mac,"tasks_/delAllCycleTask/"+str_mac);	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001aa7k/"+str_mac,"tasks_/delAllCycleTask/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	
	/**删除以设置好的定时任务到服务器*/
	private void delete_Task_Data(final String data){//delCycleTask/表号  + 开关机编号（01，02）+ 表号 + 时间 + 空调类型 + 天数
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
	
	/**刷新定时任务列表*/
	private void read_Task_Data(){
		new Thread(){
			public void run(){
				try {
					System.out.println("刷新定时任务列表"+str_mac);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getApplicationContext(),
							"0a0001a4r5/"+str_mac,"tasks_/read");	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a4r5/"+str_mac,"tasks_/read");
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	private boolean deleteRcb(int id,String mac,String[] weekdays,String time){
		try{	
			String rcb = null;
			//**********************************************
			for(int i=0;i<weekdays.length;i++){
				System.out.println("*********weekdays[i].toString()"+weekdays[i]);
				switch(weekdays[i].toString()){
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
			MyOrder odToSend_1=OrderList.getSendByDeviceType(str_type, 
					str_mac, OrderList.READ_CENTER_WEEK_TASK,rcb);
			String rst_raw_1 = OrderManager.getInstance().sendOrder(odToSend_1,
					OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
			if(rst_raw_1==null){
				Message mesg=new Message();
				Bundle data=new Bundle();
				data.putString("DETrcb", "delete_fail");
				mesg.setData(data);
				Kt_Frag_Center_3_view.this.handler.sendMessage(mesg);
				return false;
			}
			if(rst_raw_1!=null){
				String msg_status = OrderManager.getInstance().getItemByOrder(rst_raw_1,
						"status", -1);
				if(!msg_status.equals("200")){
					return false;
				}
				String rst_1 = OrderManager.getInstance().getItemByOrder(rst_raw_1,
						"result", -1);
				if(rst_1!=null){ 
					rst_1=getRCB_tesk(rst_1,id,time);
					//-*---------写日程表
					MyOrder odToSend=OrderList.getSendByDeviceType(str_type, 
							str_mac, OrderList.WRITE_CENTER_WEEK_TASK,rcb+rst_1);	
					
					String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
							OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
					
					System.out.println("rst_raw: "+rst_raw);
					
					if(rst_raw==null){
						Message mesg=new Message();
						Bundle data=new Bundle();
						data.putString("DETrcb", "delete_fail");
						mesg.setData(data);
						Kt_Frag_Center_3_view.this.handler.sendMessage(mesg);
						System.out.println("删除失败");

					}
					
					if(rst_raw!=null){					
						String status = OrderManager.getInstance().getItemByOrder(rst_raw,
								"status", -1);
						if(status.equals("200")){
							if(i==weekdays.length-1){

								return true;
							}
							continue;
							}
						}else{
							return false;
						}
					//----------
					
				
			}else{
				Message mesg=new Message();
				Bundle data=new Bundle();
				data.putString("DETrcb", "delete_fail");
				mesg.setData(data);
				Kt_Frag_Center_3_view.this.handler.sendMessage(mesg);
				return false;
				}
			}		
			
			}
		}catch(Exception e){
			e.printStackTrace();
			Message mesg=new Message();
			Bundle data=new Bundle();
			data.putString("DETrcb", "delete_fail");
			mesg.setData(data);
			Kt_Frag_Center_3_view.this.handler.sendMessage(mesg);
			return false;
		}
		return false;
	}

	private String getRCB_tesk(String result,int hw_code,String time){
		int s=0,e=6; 
		String str_hw_code;
		if(hw_code<10){
			str_hw_code="0"+hw_code;
		}else{
			str_hw_code=hw_code+"";
		}
		if(result.equals("000000000000000000000000000000000000000000000000000000000000")){
			return result;
		}
		String [] time_list=time.split(":");//replace(":", "");
		String check=str_hw_code+time_list[1]+time_list[0];
		System.out.println("需要校验的字符串========="+check);
		for(int i=0;i<result.length()/6;i++){
			if(result.substring(s, e).equals(check)){//截取其中需要删除的日程表的3个字节段，把前后的日程表连接起来，在后面在补上3个自己的00
				result=result.substring(0, s)+result.substring(e, result.length())+"000000";
				break;
			}
			s=s+6;
			e=e+6;
		}
		return result;
	}
	
	  /**注册接收广播*/
		public void RegisterBroad(){
			try{
			//界面更新广播接收;
			new Thread(){
				public void run(){
					receiver = new ReceiveTool();
					IntentFilter intentfilter = new IntentFilter("NOTIFY_AIR_TASK");
					registerReceiver(receiver, intentfilter);
					
				}
			}.start();	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	/**接收定时任务更新广播类*/
	class ReceiveTool extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {//selected/0a0001aa7k/002014110119/read30h/data
			// TODO Auto-generated method stub
			System.out.println("************************收到广播，这是空调界面************："+intent.getAction());
			try{
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "TASK_UPDATA":
						init_view(visibility);
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
