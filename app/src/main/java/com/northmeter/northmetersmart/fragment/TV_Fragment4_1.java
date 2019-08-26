package com.northmeter.northmetersmart.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.db.DBDeviceData;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceData;
import com.northmeter.northmetersmart.device.MyDeviceDataDN;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.MyResultDN;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

public class TV_Fragment4_1 extends BaseActivity implements OnClickListener {
	private boolean SOCKET_STATE_HEZHA = true;
	private boolean SOCKET_STATE_TIAOZHA = false;
	private Handler handler;                   
	private TextView textview_gl,textview_dqdl;
	private TextView textview_clzt;//策略读取状态；
	private EditText edittext1,edittext2,edittext4;
	private Button button1,button3,button4;
	private ImageView button_return;
	private Spinner mspinner;
	private  String str_mac,dqdl;
	private String thzzt;//跳合闸状态
	private CustomProgressDialog progressDialog;
	private AlertDialog dialog=null;
	private Runnable runnable;
	private String URL_PATH;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.tv_f4_1);
	
		URL_PATH = URL_help.getInstance().getUrl_address();
		Intent intent = getIntent();
		str_mac = intent.getStringExtra("mac");

		progressDialog= CustomProgressDialog.createDialog(this);
		progressDialog.setMessage("正在读取设备策略");
		progressDialog.show();
		mspinner=(Spinner) findViewById(R.id.spinner);
		edittext1 = (EditText) findViewById(R.id.tv_input_1);
		edittext2 = (EditText) findViewById(R.id.tv_input_2);
		edittext4 = (EditText) findViewById(R.id.tv_input_4);
		textview_clzt = (TextView) findViewById(R.id.tv_zhuangtai);
		button1=(Button) findViewById(R.id.tv_button_1);
		button3=(Button) findViewById(R.id.tv_button_3);
		button4=(Button) findViewById(R.id.tv_button_4);
		//结束当前页面
		button_return=(ImageView) findViewById(R.id.ydcl_back);
		button_return.setOnClickListener(this);

		button1.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		//默认按钮不可点击，收到数据后再设置可以点击
		button1.setEnabled(false);;
		button3.setEnabled(false);
		button4.setEnabled(false);


		textview_dqdl = (TextView) findViewById(R.id.item0);//获取当前电量
		dqdl = readDataDQXX();
		System.out.println("***** *****当前电量："+dqdl);


		 handler=new Handler(){
			public void handleMessage(Message msg){
			try{
				super.handleMessage(msg);
				Bundle data=msg.getData();
				if(isFinishing()){
					return;
				}
				progressDialog.dismiss();
				String[] dlrcveive=data.getStringArray("dnxx");
				String glxx = dlrcveive[0];
				String glsx = dlrcveive[1];
				String dlsx = dlrcveive[2]; 
				String tzyssj = dlrcveive[3];
				String cldqzt = dlrcveive[4];
				if(cldqzt.equals("策略读取成功")){//设置按钮可点击
					button1.setEnabled(true);
					button3.setEnabled(true);
					button4.setEnabled(true);
				}	
				String glxx_1=(Double.parseDouble(glxx))*1000+"";
				String glsx_1=(Double.parseDouble(glsx))*1000+"";		
				TV_Fragment4_1.this.edittext1.setText(glxx_1);
				TV_Fragment4_1.this.edittext2.setText(glsx_1);
				TV_Fragment4_1.this.edittext4.setText(Double.valueOf(dlsx)+"");
				TV_Fragment4_1.this.textview_clzt.setText(cldqzt);	
				System.out.println("----------glsx_1-"+glsx+" "+glsx_1  +" "+Double.valueOf(glsx_1));
				handler.removeCallbacks(runnable);
			}catch(Exception e){
				e.printStackTrace();
				}
				}
		};	
		
		runnable = new Runnable(){
			public void run(){
				DBDevice db_d = new DBDevice(TV_Fragment4_1.this);
				List<MyDevice> devices = db_d.GetMyDevices();
				MyDeviceDataDN myDeviceDataDN=null;
				MyDevice md = null;
				if (!devices.isEmpty()) {
					for (int i = 0; i < devices.size(); i++) {
						if (devices.get(i).getMac().equals(str_mac)) {
							md = devices.get(i);
							break;
						}
					}
				}
				// =================================================
				MyOrder odToSend;
				switch (md.getType()) {
				// 智能插座
				case MyDevice.DEVICE_WIFI_SMART_SOCKET: 
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(Type_Entity.Wifi_socket,
							md.getTableNum(), OrderList.ORDER_SOCKET_READ_DN);
					break;  
				case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET://zigbee插座
					odToSend = OrderList.getSendByDeviceType(Type_Entity.Socket_type,
							md.getTableNum(), OrderList.ORDER_SOCKET_READ_DN,"");			
					break;

				default:
					odToSend = null;
					break;
				}
				
				// 发送命令并得到返回的结果
				String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
						OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
				
				// 发送命令并得到返回结果
//				String rst_raw = OrderManager.getInstance().sendOrder(
//						OrderList.getOrderByDeviceType(
//								MyDevice.DEVICE_WIFI_SMART_SOCKET,
//								md.getTableNum(), OrderList.ORDER_SOCKET_READ_DN),
//						OrderList.USER_PASSWORD, OrderList.URL_PATH, "utf-8");
				System.out.println("...................rst_raw..."+rst_raw);
				if(rst_raw ==null){
					Message msg=new Message();
					Bundle data=new Bundle();
					String [] dnData=new String[]{"0","0","0","0","策略读取失败"};
					data.putStringArray("dnxx", dnData);
					msg.setData(data);		
					TV_Fragment4_1.this.handler.sendMessage(msg) ;
					progressDialog.dismiss();
					return;
				}
				String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
						"msg", -1);
				if(rst_error.equals("没有找到这个采集器")){
					Message msg=new Message();
					Bundle data=new Bundle();
					String [] dnData=new String[]{"0","0","0","0","策略读取失败"};
					data.putStringArray("dnxx", dnData);
					msg.setData(data);		
					TV_Fragment4_1.this.handler.sendMessage(msg) ;
					return;
				}
				if (rst_raw != null) {
					// 获取当前时间
					Time t = new Time();
					t.setToNow();
					String timeNow = String.valueOf(t.year) + "年"
							+ String.valueOf(t.month + 1) + "月" + t.monthDay + "日"
							+ t.hour + "时" + t.minute + "分" + t.second + "秒"; // month是从0开始计算的

					// 解析返回的结果
					String status = OrderManager.getInstance().getItemByOrder(rst_raw,
							"status", -1);
					// 将结果存到MyResultdn对象中
				if(status.equals("200")){
					// 解析返回的结果
					String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
							"result", -1);
					System.out.println("********RST结果*********"+rst);
					MyResultDN myResultDN = new MyResultDN(rst);
					
				    myDeviceDataDN = new MyDeviceDataDN(str_mac, timeNow,
							"on", myResultDN.getGlxx(), myResultDN.getGlsx(),
							myResultDN.getDlsx(),myResultDN.getTzyssj());
				    
				    Message msg=new Message();
					Bundle data=new Bundle();
					String [] dnData=new String[]{myDeviceDataDN.getGlxx(),myDeviceDataDN.getGlsx(),
							myDeviceDataDN.getDlsx(),myDeviceDataDN.getTzyssj(),"策略读取成功"};
					data.putStringArray("dnxx", dnData);
					msg.setData(data);
					TV_Fragment4_1.this.handler.sendMessage(msg);

					System.out.println("=======================================================================");
					System.out.println("*******功率下限*****"+myDeviceDataDN.getGlxx());
					System.out.println("*******功率上限*****"+myDeviceDataDN.getGlsx());
					System.out.println("*******电量上限*****"+myDeviceDataDN.getDlsx());
					System.out.println("*******延时跳闸时间**"+myDeviceDataDN.getTzyssj());
					}
					else{
						Message msg=new Message();
						Bundle data=new Bundle();
						String [] dnData=new String[]{"0","0","0","0","策略读取失败"};
						data.putStringArray("dnxx", dnData);
						msg.setData(data);		
						TV_Fragment4_1.this.handler.sendMessage(msg) ;
						}		
					}	
			}
	};
		new Thread(runnable).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		
}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		//设置功率上下限
		case R.id.tv_button_1:
			try{
			String gl_1=edittext1.getText().toString();
			String gl_2=edittext2.getText().toString();
			if(Double.parseDouble(gl_2)>2500){
				Toast.makeText(this, "功率上限不能大于2500w", 2000).show();
				break;
			}
			if( Double.parseDouble(gl_1)>Double.parseDouble(gl_2)){
				Toast.makeText(this, "功率下限不能大于功率上限", 2000).show();
					break;
				}
			if( Double.parseDouble(gl_1)<0){
					Toast.makeText(this, "功率下限不可设为负数", 2000).show();
					break;
				}
			String gl_1_1=Double.parseDouble(gl_1)/1000+"";
			String gl_2_1=Double.parseDouble(gl_2)/1000+"";
			System.out.println("********************gl_2_1: "+gl_2_1+" / "+gl_2);
			String str_gl ="000000";
			gl_1=str_gl.substring(0, 6-gl_1_1.length())+gl_1_1;
			gl_2=str_gl.substring(0, 6-gl_2_1.length())+gl_2_1;
			//开始发送命令请求
			String rews_1= sendMsgByUserCommand(OrderList.ORDER_SOCKET_SEND_DN_glxx,gl_1);
			String rews_2= sendMsgByUserCommand(OrderList.ORDER_SOCKET_SEND_DN_glsx,gl_2);
			System.out.println("gl下限/////"+gl_1+" " +gl_2);
				if(rews_1=="fail" ||rews_2=="fail"){
					Toast.makeText(this, "连接超时",Toast.LENGTH_SHORT).show();
					break;
				}if(rews_1.equals("success") & rews_2.equals("success")){
					Toast.makeText(this, "设置成功",Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "设置失败",Toast.LENGTH_SHORT).show();
				}

			break;
				}catch(Exception e){
				e.printStackTrace();
				}
			
		
			
		//设置电量上限,输入不要字节反向
		case R.id.tv_button_3:
		try{
			String dl_1=edittext4.getText().toString();
			if(Double.parseDouble(dl_1)<Double.parseDouble(dqdl)){
				Toast.makeText(this, "电量上限不能小于当前电量！",2000).show();
				break;
			}
			
			if(Double.parseDouble(dl_1)>5000){
				Toast.makeText(this, "电量上限不能超过5000kwh", 2000).show();
				break;
			}
			
			if(dl_1.length()<6){
				String str ="000000";
				dl_1=str.substring(0, 6-dl_1.length())+dl_1;
				System.out.println("************"+dl_1);
			}
			String rews_4 = sendMsgByUserCommand(OrderList.ORDER_SOCKET_SEND_DN_dlsx,dl_1);
				if(rews_4=="fail"){
					Toast.makeText(this, "连接超时",Toast.LENGTH_SHORT).show();
					break;
				}if(rews_4.equals("success")){
					Toast.makeText(this, "设置成功",Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "设置失败",Toast.LENGTH_SHORT).show();
				}
			
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
			
		//电量清零按钮
		case R.id.tv_button_4:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			//提示信息
			//String s=(String) this.getResources().getText(R.string.login);
			builder.setTitle("电量清零");
			builder.setMessage("是否清零?");
			builder.setCancelable(false);	
			//是按钮   
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String clean = sendMsgDNclear();
					System.out.println("电量清零"+clean);
					if(clean.equals("success")){
						Toast.makeText(TV_Fragment4_1.this, "电能清零成功",Toast.LENGTH_SHORT).show();
						return;
					}if(clean=="fail"){
						Toast.makeText(TV_Fragment4_1.this, "连接超时",Toast.LENGTH_SHORT).show();
						return;
					}else{
						Toast.makeText(TV_Fragment4_1.this, "电能清零失败",Toast.LENGTH_SHORT).show();
						return;
					}
					
				}				
			}).create();
			
			//否按钮
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}		
			}).create();
			dialog=builder.show();
	
			break;
			
			//结束当前页面
		case R.id.ydcl_back:
			finish();
			break;
		}
		// TODO Auto-generated method stub
		
	}
	
	
	//发送设置的参数
	public String sendMsgByUserCommand(int order_type,String cmd_data){
		DBDevice db_d = new DBDevice(TV_Fragment4_1.this);
		List<MyDevice> devices = db_d.GetMyDevices();
		MyDeviceDataDN myDeviceDataDN=null;
		MyDevice md = null;
		String rst_rce = null;
		if (!devices.isEmpty()) {
			for (int i = 0; i < devices.size(); i++) {
				if (devices.get(i).getMac().equals(str_mac)) {
					md = devices.get(i);
					break;
				}
			}
		}
		// =================================================
		MyOrder odToSend;
		switch (md.getType()) {
		// 智能插座
		case MyDevice.DEVICE_WIFI_SMART_SOCKET: 
			// 根据设备类型，获取需要发送的命令
			odToSend = OrderList.getSendByDeviceType(Type_Entity.Wifi_socket,
					md.getTableNum(), order_type,cmd_data);
			break;  
		case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET://zigbee插座
			odToSend = OrderList.getSendByDeviceType(Type_Entity.Socket_type,
					md.getTableNum(), order_type,cmd_data);			
			break;

		default:
			odToSend = null;
			break;
		}
		
		// 发送命令并得到返回的结果
		String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
				OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
		
//		// 发送命令并得到返回结果
//		String rst_raw = OrderManager.getInstance().sendOrder(
//				OrderList.getSendByDeviceType(
//						MyDevice.DEVICE_WIFI_SMART_SOCKET,
//						md.getTableNum(),order_type,cmd_data),
//						OrderList.USER_PASSWORD, OrderList.URL_PATH, "utf-8");
		if(rst_raw==null){ 
			return "fail";
		}
		String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
				"msg", -1);
		if(rst_error.equals("没有找到这个采集器")|rst_error.equals("没有找到该设备的组网信息！")|rst_error.equals("无效流水！")){
			return "fail";
		}

		if (rst_raw != null) {
			// 解析返回的结果
			String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
					"result", -1);
			System.out.println("********RST结果*********"+rst);
			rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
						"result", -1);

			if (rst_rce != null) {
				if (rst_rce.equals("success")) {
					System.out.println("====================设置成功=== ");
				} else {
					System.out.println("====================设置失败=== ");
					}
				}
		}
		return rst_rce;
	}
	
	
	//电量清零
	public String sendMsgDNclear(){
		DBDevice db_d = new DBDevice(TV_Fragment4_1.this);
		List<MyDevice> devices = db_d.GetMyDevices();
		MyDeviceDataDN myDeviceDataDN=null;
		MyDevice md = null;
		String rst_rce = null;
		if (!devices.isEmpty()) {
			for (int i = 0; i < devices.size(); i++) {
				if (devices.get(i).getMac().equals(str_mac)) {
					md = devices.get(i);
					break;
				}
			}
		}
		// =================================================
		MyOrder odToSend;
		switch (md.getType()) {
		// 智能插座
		case MyDevice.DEVICE_WIFI_SMART_SOCKET: 
			// 根据设备类型，获取需要发送的命令
			odToSend = OrderList.getOrderByDeviceType(Type_Entity.Wifi_socket,
					md.getTableNum(), OrderList.ORDER_SOCKET_CLEAR);
			break;  
		case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET://zigbee插座
			odToSend = OrderList.getSendByDeviceType(Type_Entity.Socket_type,
					md.getTableNum(), OrderList.ORDER_SOCKET_CLEAR,"");			
			break;

		default:
			odToSend = null;
			break;
		}
		
		// 发送命令并得到返回的结果
		String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
				OrderList.USER_PASSWORD, URL_PATH, "UTF-8");
//		// 发送命令并得到返回结果
//		
//		String rst_raw = OrderManager.getInstance().sendOrder(
//				OrderList.getOrderByDeviceType(
//						MyDevice.DEVICE_WIFI_SMART_SOCKET,
//						md.getTableNum(),OrderList.ORDER_SOCKET_CLEAR),
//						OrderList.USER_PASSWORD, OrderList.URL_PATH, "utf-8");
		if( rst_raw==null){
			return "fail";
		}
		String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
				"msg", -1);
		if(rst_error.equals("没有找到这个采集器")|rst_error.equals("没有找到该设备的组网信息！")|rst_error.equals("无效流水！")){
			return "fail";
		}
		if (rst_raw != null) {
			rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
						"result", -1);
			System.out.println("********RST_1结果*********"+rst_raw);
			System.out.println("********RST_2结果*********"+rst_rce);
			if (rst_rce != null) {
				if (rst_rce.equals("success")) {
					System.out.println("====================设置成功=== ");
				} else {
					System.out.println("====================设置失败=== ");
					}
				}
		}
		return rst_rce;
	}
	
	/*从数据库读取当前电量*/
	private  String readDataDQXX() {
		String dqdl=null;
		DBDeviceData db_dd = new DBDeviceData(TV_Fragment4_1.this);
		List<MyDeviceData> devicesData = db_dd.GetMyDeviceData();
		DBDevice db_d = new DBDevice(TV_Fragment4_1.this);
		List<MyDevice> devices = db_d.GetMyDevices();
		MyDevice md = null;
		if (!devices.isEmpty()) {
			for (int i = 0; i < devices.size(); i++) {
				if (devices.get(i).getMac().equals(str_mac)) {
					md = devices.get(i);
					break;
				}
			}
		}
		if (md != null)
			System.out.println("找到设备 mac=" + md.getMac() + " str_mac="
					+ str_mac);
		// ===========================================================		
			MyDeviceData mdd = null;
			if (!devicesData.isEmpty()) {
				for (int i = 0; i < devicesData.size(); i++) {
					if (devicesData.get(i).getMac().equals(str_mac)) {
						mdd = devicesData.get(i);
						break;
					}
				}
			}
			if (mdd != null)
				System.out.println("找到设备信息 mac=" + mdd.getMac() + " str_mac="
						+ str_mac);
			
			if (mdd != null) {
				dqdl=mdd.getZdn();	
			} else {
				dqdl="0";
			}
			return dqdl;
		}
	
	
	
	
}
