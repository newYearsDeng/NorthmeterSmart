package com.northmeter.northmetersmart.fragment;

import java.util.List;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.fragment.app.Fragment;

/**以太网转zigbee网关*/
public class Add_Fragment4_1 extends Fragment implements OnClickListener{
	private View view;
	private Context context;
	private String tabnum;
	private CustomProgressDialog progressDialog;
	private ArrayAdapter<String> arr_adapter;
	private Handler handler;
	private ListView listview;
	private TextView txt_result;
	private String URL_PATH;
	private ReceiveTool receiver_add,receiver;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.f_add_4_1, container, false);
		RegisterBroad();//注册广播
		try{
		view.findViewById(R.id.button_next4_1).setOnClickListener(this);
		txt_result = (TextView) view.findViewById(R.id.txt_result);
		tabnum= DeviceManager.getInstance().getTempDevice().getTableNum();
		progressDialog= CustomProgressDialog.createDialog(getActivity());

		progressDialog.setMessage("正在读取档案");
		progressDialog.show();
		
		URL_PATH = URL_help.getInstance().getUrl_address();
		handler=new Handler(){
			public void handleMessage(Message msg){
				try{
				super.handleMessage(msg);
				Bundle data=msg.getData();
				progressDialog.dismiss();
				String[] mqttrcveive = data.getStringArray("ADDMANAGER");
				if(mqttrcveive[0] == "MQTT"){
					System.out.println("mqttrcveive[1]******"+mqttrcveive[1]);
					switch(mqttrcveive[1]){
					case "MQTTerr":
						Toast.makeText(getActivity(), "网络连接异常！", Toast.LENGTH_SHORT).show();
						((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(2));//通过Fragment设置选
	                	return;
					case "TIMEOUT":
						Toast.makeText(getActivity(), "连接超时！", Toast.LENGTH_SHORT).show();
						((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(2));//通过Fragment设置选
	                	return;
					case "采集设备添加成功":
						Toast.makeText(getActivity(), "采集设备添加成功!", Toast.LENGTH_SHORT).show();
						String[] mqttmsg = new String[]{mqttrcveive[1]};
						listview=(ListView) view.findViewById(R.id.listView1);
						arr_adapter=new ArrayAdapter<String>(getActivity(),R.layout.add_f4_item,R.id.textView1,mqttmsg);
						listview.setAdapter(arr_adapter);
						break;
					case "ADD_ERROR":
						Toast.makeText(getActivity(), "添加失败！", Toast.LENGTH_SHORT).show();
						((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(2));//通过Fragment设置选
						break;
					 
					}
					
				}else{
					if(mqttrcveive[0].equals("档案读取失败！")){
	                	((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(2));//通过Fragment设置选
	                	return;
					}
					
					//列表显示档案信息
					listview=(ListView) view.findViewById(R.id.listView1);
					arr_adapter=new ArrayAdapter<String>(getActivity(),R.layout.add_f4_item,R.id.textView1,mqttrcveive);
					listview.setAdapter(arr_adapter);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				}
			};	
			
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
								"addmanagerecord;managerecord","0a0003ahup"+"/"+DeviceManager.getInstance().getTempDevice().getMac()+"/ethnet");	
						
//						PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("addmanagerecord","0a0003ahup"+"/"+
//								DeviceManager.getInstance().getTempDevice().getMac()+"/ethnet");
					} catch (MqttException e1) {
						// TODO Auto-generated catch block
						Message msg  = new Message();
						Bundle data = new Bundle();
						String [] Data=new String[]{"MQTT","MQTTerr"};
						data.putStringArray("ADDMANAGER", Data);
						msg.setData(data);	
						Add_Fragment4_1.this.handler.sendMessage(msg);
						e1.printStackTrace();
					}
					
				}
			}.start();
		
			}catch(Exception e){
				e.printStackTrace();
			}
		return view;
	}
	
	@Override
	public void onDestroyView() {
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		viewGroup.removeView(view);
		super.onDestroyView();
	}
	
		//发送设置的参数
		public String sendMsgByUserCommand(String mac_str,int order_type,String cmd_data){
			String rst_rce = null;
			// =================================================
			// 发送命令并得到返回结果
			String rst_raw = OrderManager.getInstance().sendOrder(
					OrderList.getSendByDeviceType(
							Type_Entity.Gateway_type,
							mac_str,order_type,cmd_data),
							OrderList.USER_PASSWORD, URL_PATH, "utf-8");
			if(rst_raw==null){ 
				return null;
			} 
			String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
					"msg", -1);
			if(rst_error.equals("没有找到这个采集器")|rst_error.equals("没有找到该设备的组网信息！")|rst_error.equals("无效流水！")){
				return null;
			}
			
			if (rst_raw != null){
				// 解析返回的结果
				String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);  
				System.out.println("********RST结果*********"+rst);
				if(!rst.equals("200")){
					return "fail";
				}else{
					rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
							"result", -1);
				}
				
			}
			return rst_rce;
		}
		/*读取到的档案号解析为电表号*/
		private String reverseRst(String rst){
			String newRst=rst.substring(2, rst.length()-2);
			String lastrst = "";
			for(int i=newRst.length()/2;i>0;i--){
				lastrst=lastrst+newRst.substring(i*2-2, i*2);
				
			}
			return lastrst;
		}
		private String RstOnline(String rst){
			String lastrst = null;
			String newRst=rst.substring(rst.length()-2, rst.length());
			if(newRst=="00"|newRst.equals("00")){
				lastrst="    不在线";
			}else{
				lastrst="    在线";
			}
			return lastrst;
		}
		/**发送抄表命令查询网关内档案*/
		private void getDAmsg(){
			String [] dnData = null;
			try{
				String result=sendMsgByUserCommand(tabnum,OrderList.READ_SOCKET_RECORD,"");
				System.out.println("result******"+result);
				Message msg=new Message();
				Bundle data=new Bundle();
				if(result==null){
					 dnData=new String[]{"档案读取失败！"};
				}else if(result.equals("fail")){
					dnData=new String[]{"档案读取失败！"};
				}else if(result.equals("")){
					dnData=new String[]{"网关内档案为空！"};
				}
				else{
					String[] resultStr = result.split(",");
					int length=resultStr.length;
					dnData=new String[length];
					for(int i=0;i<length;i++){
						dnData[i] = resultStr[i].substring(2,resultStr[i].length()-2)+RstOnline(resultStr[i]);
						}
	
					}
					data.putStringArray("ADDMANAGER", dnData);
					msg.setData(data);		
					Add_Fragment4_1.this.handler.sendMessage(msg);
				}catch(Exception e){
					Message msg=new Message();
					Bundle data=new Bundle();
					dnData=new String[]{"档案读取失败！"};
					data.putStringArray("ADDMANAGER", dnData);
					msg.setData(data);		
					Add_Fragment4_1.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_next4_1:
				DBDevice db_d = new DBDevice(getActivity());
				List<MyDevice> devices = db_d.GetMyDevices();
				MyDevice md = new MyDevice();
				try{
				if(!devices.isEmpty()) {
					md=db_d.Query(DeviceManager.getInstance().getTempDevice().getMac());
					System.out.println("md****"+md);
					if(md.getMac()==null){
						System.out.println("不存在相同设备，添加");
						DeviceManager.getInstance().update(getActivity());
					}else{
						if(md.getMac()!=null||!md.getMac().equals(null)) {
						    System.out.println("存在相同设备，只更新"+DeviceManager.getInstance().getTempDevice().getMac());
							DeviceManager.getInstance().upDataOld(getActivity());
							Toast toast=Toast.makeText(getActivity(), "存在相同设备，原有图标将被覆盖", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show(); 
						}else{
							System.out.println("不存在相同设备，添加");
							DeviceManager.getInstance().update(getActivity());
							}
					
						}
					
				}else{
					System.out.println("不存在设备，添加");
					DeviceManager.getInstance().update(getActivity());
					}
				}catch(Exception e){
						e.printStackTrace();
					}	
				context = getActivity().getApplicationContext();
				Intent intent=new Intent(context,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			}
			
		}
		
		/**注册接收广播*/
		public void RegisterBroad(){
			//界面更新广播接收;
			new Thread(){
				public void run(){
					receiver = new ReceiveTool();
					IntentFilter intentfilter = new IntentFilter("Intent.NOTIFY_ZIGBEE");
					getActivity().registerReceiver(receiver, intentfilter);
					
					receiver_add = new ReceiveTool();
					IntentFilter intentfilter_1 = new IntentFilter("Intent.AddManageRecord");
					getActivity().registerReceiver(receiver_add, intentfilter_1);
					
				}
			}.start();	
		}
		
		class ReceiveTool extends BroadcastReceiver{
//receive.split("/")[2]+"/AddManageRecord/"+receive.split("/")[receive.split("/").length-1]};
			@Override  //addmanagerecord/0a0003ahup/000090600011/添加失败
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				System.out.println("************************收到广播"+intent.getAction());
				Message msg=new Message();
				Bundle data=new Bundle();
				if(intent.getStringExtra("msg").split("/")[0].equals(DeviceManager.getInstance().getTempDevice().getMac())){
					switch(intent.getStringExtra("msg").split("/")[1]){
					case "AddManageRecord":
						String data_0=intent.getStringExtra("msg").split("/")[2];
						if(data_0.equals("采集设备数据库已存在")){//采集设备数据库已存再
							new Thread(){
								public void run(){
									getDAmsg();
								}
							}.start();
						}else if(data_0.equals("采集设备添加成功")){//采集设备数据库添加成功
							String [] Data_0=new String[]{"MQTT",data_0};
							data.putStringArray("ADDMANAGER", Data_0);
							msg.setData(data);	
							Add_Fragment4_1.this.handler.sendMessage(msg);
						}else{
							String data_1="ADD_ERROR";
							String [] Data_1=new String[]{"MQTT",data_1};
							data.putStringArray("ADDMANAGER", Data_1);
							msg.setData(data);	
							Add_Fragment4_1.this.handler.sendMessage(msg);
						}
						break;

					case "TIMEOUT":
						String data_3="TIMEOUT";
						String [] Data_1=new String[]{"MQTT",data_3};
						data.putStringArray("ADDMANAGER", Data_1);
						msg.setData(data);	
						Add_Fragment4_1.this.handler.sendMessage(msg);
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
			getActivity().unregisterReceiver(receiver_add);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	
}
