package com.northmeter.northmetersmart.fragment;

import java.util.List;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceDataDN;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;
import com.northmeter.northmetersmart.model.Fragment_mac_trance;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.mqtt.PushService;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**zigbee插座*/
public class Add_Fragment4_2 extends Fragment implements OnClickListener{
	private View view;
	private View f4_p1View;
	private Integer f4_p1_selecttxt_id;
	private EditText zgb_socket_name;
	private TextView txt_addrecord;
	private Context context;
	private PopupWindow popupWindowf1_p1;
	private String tabnum,mac;
	private CustomProgressDialog progressDialog;
	private Handler handler;
	private String URL_PATH;
	private ReceiveTool receiver_add,receiver;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.f_add_4_2, container, false);
		view.findViewById(R.id.button_next4_2).setOnClickListener(this);
		RegisterBroad();//注册广播
		URL_PATH = URL_help.getInstance().getUrl_address();
		txt_addrecord=(TextView) view.findViewById(R.id.txt_addrecord);
		loadPopupWindow(inflater);
		//zigbee名字输入
		zgb_socket_name=(EditText) view.findViewById(R.id.zgb_socket_name);
		zgb_socket_name.setOnClickListener(this);
		view.findViewById(R.id.linear_select).setOnClickListener(this);
		tabnum= DeviceManager.getInstance().getTempDevice().getTableNum();
		//mac=getFragmentManager().findFragmentByTag("179521").getArguments().getString("order");
		mac= Fragment_mac_trance.getInstance().getMac();
		System.out.println("网关mac地址"+mac);
		progressDialog=CustomProgressDialog.createDialog(getActivity()); 
		progressDialog.setMessage("正在下载档案");
		progressDialog.show();
		
		handler=new Handler(){
			public void handleMessage(Message msg){
				try{
				super.handleMessage(msg);
				Bundle data=msg.getData();
//				progressDialog.dismiss();
				String[] mqttreceive = data.getStringArray("ADDMANAGER");
				if(mqttreceive[0] == "MQTT"){
					switch(mqttreceive[1]){
					case "MQTTerr":
						progressDialog.dismiss();
						Toast.makeText(getActivity(), "网络连接异常！", Toast.LENGTH_SHORT).show();
						((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(5));//通过Fragment设置选
	                	return;
					case "TIMEOUT":
						progressDialog.dismiss();
						Toast.makeText(getActivity(), "连接超时！", Toast.LENGTH_SHORT).show();
						((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(5));//通过Fragment设置选
	                	return;
					case "AddManageRecord":
						progressDialog.dismiss();
						Toast.makeText(getActivity(), "数据库添加成功！", Toast.LENGTH_SHORT).show();
						break;
					case "AddManageRecordFail":
						progressDialog.dismiss();
						Toast.makeText(getActivity(), "添加失败！", Toast.LENGTH_SHORT).show();
						((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(5));//通过Fragment设置选
	                	return;
					}
				}else{
					txt_addrecord.setText(mqttreceive[0]);
					if(mqttreceive[0].equals("ADD_Fail")){
						progressDialog.dismiss();
						Toast.makeText(getActivity(), "添加档案失败！", Toast.LENGTH_SHORT).show();
	                	((RadioButton) RadioHelper.radioGroup.getChildAt(1))
						.setChecked(true);
	                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(5));//通过Fragment设置选
	                	return;
					}
				}
				}catch(Exception e){
					progressDialog.dismiss();
					e.printStackTrace();
				}
				}
			};	
			
		new Thread(){
			public void run(){
				DownANXX_zigbee();//往采集器下载档案
//					try {
//						PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("addmanagerecord","0a0001a820/"+
//								DeviceManager.getInstance().getTempDevice().getMac()+"/wallsocket/0a0003ahup/"+
//								DeviceManager.getInstance().getTempDevice().getContact()+"/ethnet");
//					} catch (MqttException e1) {
//						// TODO Auto-generated catch block
//						Message msg  = new Message();
//						Bundle data = new Bundle();
//						String [] Data=new String[]{"MQTT","MQTTerr"};
//						data.putStringArray("ADDMANAGER", Data);
//						msg.setData(data);	
//						Add_Fragment4_2.this.handler.sendMessage(msg);
//						e1.printStackTrace();
//					}

			}
		}.start();

		return view;
	}
	
	private void loadPopupWindow(LayoutInflater inflater) {
		f4_p1View = inflater.inflate(R.layout.f_add_4p1, null);
		popupWindowf1_p1 = new PopupHelper().getWindow_ALLWRAP(f4_p1View,
				this.getActivity());
		f4_p1View.findViewById(R.id.btn1).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn2).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn3).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn4).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn5).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn6).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn7).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn8).setOnClickListener(this);
		f4_p1View.findViewById(R.id.btn9).setOnClickListener(this);
	}
	
	//发送设置的参数
		public String sendMsgByUserCommand(int order_type,String cmd_data){
			DBDevice db_d = new DBDevice(getActivity());
			List<MyDevice> devices = db_d.GetMyDevices();
			MyDeviceDataDN myDeviceDataDN=null;
			MyDevice md = null;
			String rst_rce = null;
			if (!devices.isEmpty()) {
				for (int i = 0; i < devices.size(); i++) {
					if (devices.get(i).getTableNum().equals(tabnum)) {
						md = devices.get(i);
						break;
					}
				}
			}
			
			// =================================================
			// 发送命令并得到返回结果,中间件1.0下载档案时"01"+字节反向的表号+"00",而中间件2.0改为"01"+表号;
			String rst_raw = OrderManager.getInstance().sendOrder(
					OrderList.getSendByDeviceType(
							Type_Entity.Socket_type,
							mac,order_type,"01"+tabnum),
							OrderList.USER_PASSWORD, URL_PATH, "utf-8");
			if(rst_raw==null){  
				return null;
			}
//			String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
//					"msg", -1);
//			if(rst_error.equals("没有找到这个采集器")|rst_error.equals("没有找到该设备的组网信息！")|rst_error.equals("无效流水！")){
//				return null;
//			}

			if (rst_raw != null){
				// 解析返回的结果
				rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
							"status", -1);

				if (rst_rce != null) {
					if (rst_rce.equals("200")) {
						System.out.println("====================设置成功=== ");
					} else {
						System.out.println("====================设置失败=== ");
						}
					}
			}
			return rst_rce;
		}
		/**往采集器下载档案*/
		private void DownANXX_zigbee(){
			try{	
				int stay = getDAnum();
				System.out.println("/////////-----------stay"+reverseRst(tabnum)+"/"+stay);
				Message msg=new Message();
				Bundle data=new Bundle();
				if(stay>=0){
					try {
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
								"addmanagerecord;managerecord","0a0001a820/"+
										DeviceManager.getInstance().getTempDevice().getMac()+"/wallsocket/0a0003ahup/"+
										DeviceManager.getInstance().getTempDevice().getContact()+"/ethnet");
						
//						PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("addmanagerecord","0a0001a820/"+
//								DeviceManager.getInstance().getTempDevice().getMac()+"/wallsocket/0a0003ahup/"+
//								DeviceManager.getInstance().getTempDevice().getContact()+"/ethnet");
					} catch (MqttException e1) {
						// TODO Auto-generated catch block
						Message message  = new Message();
						Bundle bundle = new Bundle();
						String [] Data=new String[]{"MQTT","MQTTerr"};
						bundle.putStringArray("ADDMANAGER", Data);
						message.setData(bundle);	
						Add_Fragment4_2.this.handler.sendMessage(message);
						e1.printStackTrace();
					}
					String[] repert=new String[]{"网关内已存在设备档案,添加成功！"};
					data.putStringArray("ADDMANAGER", repert);
					msg.setData(data);		
					Add_Fragment4_2.this.handler.sendMessage(msg);
				}else{
					String res=sendMsgByUserCommand(OrderList.ADD_ZIGBEE_RECORD,"");
					if(!res.equals("200")){
						String[] repert=new String[]{"ADD_Fail"};
						data.putStringArray("ADDMANAGER", repert);
						msg.setData(data);		
						Add_Fragment4_2.this.handler.sendMessage(msg);
					}else{
						try {
							PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
									"addmanagerecord;managerecord","0a0001a820/"+
											DeviceManager.getInstance().getTempDevice().getMac()+"/wallsocket/0a0003ahup/"+
											DeviceManager.getInstance().getTempDevice().getContact()+"/ethnet");
						} catch (MqttException e1) {
							// TODO Auto-generated catch block
							Message message  = new Message();
							Bundle bundle = new Bundle();
							String [] Data=new String[]{"MQTT","MQTTerr"};
							bundle.putStringArray("ADDMANAGER", Data);
							message.setData(bundle);	
							Add_Fragment4_2.this.handler.sendMessage(message);
							e1.printStackTrace();
						}
						String[] repert=new String[]{"添加档案成功！"};
						data.putStringArray("ADDMANAGER", repert);
						msg.setData(data);		
						Add_Fragment4_2.this.handler.sendMessage(msg);
						
						}
				}
				
				}catch(Exception e){
					Message msg=new Message();
					Bundle data=new Bundle();
					String[] repert=new String[]{"ADD_Fail"};
					data.putStringArray("ADDMANAGER", repert);
					msg.setData(data);		
					Add_Fragment4_2.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_next4_2:
				DeviceManager.getInstance().getTempDevice().setName(zgb_socket_name.getText().toString());
				//DeviceManager.getInstance().update(getActivity());
				//检查是否存在相同的设备
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
					}}catch(Exception e){
						e.printStackTrace();
					}
				
				
				
				context = getActivity().getApplicationContext();
				PushService.actionStop(context);
				/**启动mqtt后台监听*/
				PushService.actionStart(context);
				
				Intent intent=new Intent(context,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.btn1:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_COMPUTER);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;

			case R.id.btn2:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_TELEVISION);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;

			case R.id.btn3:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_LIGHT);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;

			case R.id.btn4:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_LIVINGTV);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn5:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_HEATER);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn6:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_WATER_dispenser);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn7:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_RICE_cooker);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn8:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_FAN);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;
			case R.id.btn9:
				DeviceManager.getInstance().getTempDevice()
						.setElec_type(MyDevice.ELEC_OTHERS);
				((TextView) view.findViewById(R.id.tv_select_1))
						.setText(((Button) v).getText());
				f4_p1_selecttxt_id = v.getId();
				popupWindowf1_p1.dismiss();
				break;

			case R.id.linear_select:
				popupWindowf1_p1.showAsDropDown(v);
				break;	
			}
			
		}
		
		
		/*发送读取网关档案信息列表,检查是否存在相同的设备档案*/
		private int getDAnum(){
			int stay=-1;
			try{
				DBDevice db_d = new DBDevice(getActivity());
				//DBDeviceData db_dd = new DBDeviceData(getActivity());
				List<MyDevice> devices = db_d.GetMyDevices();
				//List<MyDeviceData> devicesData = db_dd.GetMyDeviceData();
				MyDevice md = null;
				String timeNow = null;
				for (int i = 0; i < devices.size(); i++) {
					md = devices.get(i);
					if (md.getMac().equals(mac))
						break;
				}
			
				// 发送命令并得到返回的结果
				String rst_raw = OrderManager.getInstance().sendOrder(
						OrderList.getSendByDeviceType(
								Type_Entity.Gateway_type,
								md.getTableNum(),OrderList.READ_SOCKET_RECORD,""),
								OrderList.USER_PASSWORD, URL_PATH, "utf-8");
		
				if(rst_raw==null){
					return stay;
				}
				String status = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
				if(status.equals("200")){
					String rst_1 = OrderManager.getInstance().getItemByOrder(rst_raw,
							"result", -1);
					 stay = rst_1.indexOf(tabnum);//(reverseRst(tabnum));
				}else{
					return stay;
				}
				return stay;
			}catch(Exception e){
				e.printStackTrace();
				return stay;
				}
		}
		
		
		/*读取到的档案号解析为电表号*/
		private String reverseRst(String rst){
			//String newRst=rst.substring(2, rst.length()-2);
			String lastrst = "";
			for(int i=rst.length()/2;i>0;i--){
				lastrst=lastrst+rst.substring(i*2-2, i*2);
				
			}
			return lastrst;
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

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				System.out.println("************************收到广播"+intent.getAction());
				Message msg=new Message();
				Bundle data=new Bundle();
				if(intent.getStringExtra("msg").split("/")[0].equals(DeviceManager.getInstance().getTempDevice().getMac())){
					switch(intent.getStringExtra("msg").split("/")[1]){
					case "AddManageRecord"://计量设备数据库添加成功或者数据库已存在；
						if(intent.getStringExtra("msg").split("/")[2].equals("添加失败")){
							String [] Data_2=new String[]{"MQTT","AddManageRecordFail"};
							data.putStringArray("ADDMANAGER", Data_2);
							msg.setData(data);	
							Add_Fragment4_2.this.handler.sendMessage(msg);
							break;
						}
						String data_0=intent.getStringExtra("msg").split("/")[2];//截取到数据：“计量设备数据库添加成功或者计量设备数据库已存再”
//							new Thread(){
//								public void run(){
//									DownANXX_zigbee();
//								}
//							}.start();
							String [] Data_0=new String[]{"MQTT","AddManageRecord"};
							data.putStringArray("ADDMANAGER", Data_0);
							msg.setData(data);	
							Add_Fragment4_2.this.handler.sendMessage(msg);
						break;

					case "TIMEOUT":
						String data_3="TIMEOUT";
						String [] Data_1=new String[]{"MQTT",data_3};
						data.putStringArray("ADDMANAGER", Data_1);
						msg.setData(data);	
						Add_Fragment4_2.this.handler.sendMessage(msg);
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
