package com.northmeter.northmetersmart.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceDataDN;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.order.MyResultDN;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

/*延时控制*/
public class TV_Fragment4_3 extends BaseActivity implements OnClickListener{
	private Button button_return,time_send;
	private Spinner mspinner;
	private String str_mac,thzzt;
	private EditText time_set;
	private boolean SOCKET_STATE_HEZHA = true;
	private boolean SOCKET_STATE_TIAOZHA = false;
	private CustomProgressDialog progressDialog;
	private Handler handler;  
	private Runnable runnable;
	private String URL_PATH;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tv_f4_3);
		try{
		URL_PATH = URL_help.getInstance().getUrl_address();
		button_return=(Button)findViewById(R.id.yskz_back);
		button_return.setOnClickListener(this);
		time_send = (Button)findViewById(R.id.tv_button_4);
		time_send.setOnClickListener(this);
		//获取mac地址
		Intent intent=getIntent();
		str_mac=intent.getStringExtra("mac");
		progressDialog= CustomProgressDialog.createDialog(this);
		progressDialog.setMessage("正在读取延时时间");
		progressDialog.show();
		mspinner=(Spinner) findViewById(R.id.spinner);
		//设置时间框
		time_set = (EditText) findViewById(R.id.tv_input_3);
		//默认按钮不可点击，收到数据后再设置可以点击
		time_send.setEnabled(false);



		//下拉框
		String [] array=new String[]{"跳闸","合闸"};
		SharedPreferences getsp=getSharedPreferences(str_mac, 0);
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, array);
		_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mspinner.setAdapter(_Adapter);
		mspinner.setSelection(getsp.getInt(str_mac, 0));
		mspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view,
		            int position, long id) {
		        thzzt=parent.getItemAtPosition(position).toString();
		        SharedPreferences sp=getSharedPreferences(str_mac, 0);
		        Editor edit=sp.edit();
		        if(thzzt.equals("跳闸")){
		        	edit.putInt(str_mac, 0);
		        }else{
		        	edit.putInt(str_mac, 1);
		        }
		        edit.commit();
		       // Toast.makeText(getActivity(), "跳合闸状态:"+thzzt, 2000).show();
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {
		        // TODO Auto-generated method stub
		    }
		});
		
		
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
					time_send.setEnabled(true);
				}		
				TV_Fragment4_3.this.time_set.setText(Integer.parseInt(tzyssj)+"");
				handler.removeCallbacks(runnable);
			}catch(Exception e){
				e.printStackTrace();
				}
				}
		};	
		
		runnable = new Runnable(){
			public void run(){
				DBDevice db_d = new DBDevice(TV_Fragment4_3.this);
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
				// 发送命令并得到返回结果
				String rst_raw = OrderManager.getInstance().sendOrder(
						OrderList.getOrderByDeviceType(
								Type_Entity.Wifi_socket,
								md.getTableNum(), OrderList.ORDER_SOCKET_READ_DN),
						OrderList.USER_PASSWORD, URL_PATH, "utf-8");
				System.out.println("...................rst_raw..."+rst_raw);
				if(rst_raw ==null){
					Message msg=new Message();
					Bundle data=new Bundle();
					String [] dnData=new String[]{"0","0","0","0","策略读取失败"};
					data.putStringArray("dnxx", dnData);
					msg.setData(data);		
					TV_Fragment4_3.this.handler.sendMessage(msg) ;
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
					TV_Fragment4_3.this.handler.sendMessage(msg) ;
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
					String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
							"result", -1);
					// 将结果存到MyResultdn对象中
				if(rst.equals("fail")==false){
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
					TV_Fragment4_3.this.handler.sendMessage(msg);

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
						TV_Fragment4_3.this.handler.sendMessage(msg) ;
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
		//设置跳闸延时时间	
		case R.id.tv_button_4:
			String ys_1=time_set.getText().toString();
			for(int i=0;i<ys_1.length();i++){
				if(Character.isDigit(ys_1.charAt(i))==false){
					Toast.makeText(this, "延时参数必须为数字哟，请您重新输入", 2000).show();
					break;
				}
			}
			if(ys_1.indexOf(".")!=-1){
				Toast.makeText(this, "延时参数不能输入小数，请重新输入", 2000).show();
				break;
			}
			if(ys_1.length()<4){
				String str ="0000";
				ys_1=str.substring(0, 4-ys_1.length())+ys_1;
			}if(ys_1.length()>4){
				Toast.makeText(this, "延迟时间不能大于10000", 1000).show();
				break;
			}
			String tays=MyResultDN.inverseData(ys_1);
			String rews_3 = null;
			if(thzzt.equals("跳闸")){
				 rews_3 = sendMsgByUserCommand(OrderList.ORDER_SOCKET_SEND_DN_tzyssj,tays);
			}else{
				 rews_3 = sendMsgByUserCommand(OrderList.ORDER_SOCKET_SEND_DN_hzyssj,tays);
			}
			if(rews_3=="fail"){
				Toast.makeText(this, "连接超时",Toast.LENGTH_SHORT).show();
				break;
			}if(rews_3.equals("success")){
				Toast.makeText(this, "设置成功",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "设置失败",Toast.LENGTH_SHORT).show();
			}
			break;

		//结束当前页面
		case R.id.yskz_back:
			finish();
			break;
		}
		// TODO Auto-generated method stub
		
	}
	
	
	//发送设置的参数
	public String sendMsgByUserCommand(int order_type,String cmd_data){
		DBDevice db_d = new DBDevice(TV_Fragment4_3.this);
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
		// 发送命令并得到返回结果
		String rst_raw = OrderManager.getInstance().sendOrder(
				OrderList.getSendByDeviceType(
						Type_Entity.Wifi_socket,
						md.getTableNum(),order_type,cmd_data),
						OrderList.USER_PASSWORD, URL_PATH, "utf-8");
		if(rst_raw==null){ 
			return "fail";
		}
		String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
				"msg", -1);
		if(rst_error.equals("没有找到这个采集器")){
			return null;
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
	
	
	
}

