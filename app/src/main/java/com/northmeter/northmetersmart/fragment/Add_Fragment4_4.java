package com.northmeter.northmetersmart.fragment;

import java.util.List;


import android.content.Context;
import android.content.Intent;
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

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

/**无线智能网关*/
public class Add_Fragment4_4 extends Fragment implements OnClickListener{
	private View view;
	private View f4_p1View;
	private TextView macNow;
	private TextView ipNow;
	// private TextView elecTypeNow;
	private EditText nameNow;
	private Button button_next3;
	
	private PopupWindow popupWindowf1_p1;
	private Integer f4_p1_selecttxt_id;
	private Context context;
	private static String sbiaohao;
	private Handler handler;
	private static String receiv=null;
	static StringBuffer sbu;
	private CustomProgressDialog progressDialog;
	private ArrayAdapter<String> arr_adapter;
	private ListView listview;
	private String URL_PATH;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_add_4_4, container, false);
		try{
		URL_PATH = URL_help.getInstance().getUrl_address();
		progressDialog= CustomProgressDialog.createDialog(getActivity());
		progressDialog.setMessage("正在读取档案...");
		progressDialog.show();
		
		button_next3 = (Button) view.findViewById(R.id.button_next3);
		button_next3.setOnClickListener(this);
		
		macNow = (TextView) view.findViewById(R.id.tv_mac);
		ipNow = (TextView) view.findViewById(R.id.tv_ip);

		macNow.setText(DeviceManager.getInstance().getTempDevice().getMac());
		ipNow.setText(DeviceManager.getInstance().getTempDevice().getIp());
		System.out.println("***********"+DeviceManager.getInstance().getTempDevice().getMac());
		System.out.println("***********"+DeviceManager.getInstance().getTempDevice().getIp());
		// elecTypeNow =
		nameNow = (EditText) view.findViewById(R.id.et_input_name);
		
		handler=new Handler(){
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				Bundle data=msg.getData();
				progressDialog.dismiss();
				String[] dlrcveive=data.getStringArray("dnxx");
				if(dlrcveive[0]=="档案读取失败！"){
                	((RadioButton) RadioHelper.radioGroup.getChildAt(1))
					.setChecked(true);
                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(9));//通过Fragment设置选
                	return;
				}
				
				//列表显示档案信息
				listview=(ListView) view.findViewById(R.id.listView1);
				arr_adapter=new ArrayAdapter<String>(getActivity(),R.layout.add_f4_item,R.id.textView1,dlrcveive);
				listview.setAdapter(arr_adapter);
				//txt_result.setText(dlrcveive[0]);
				}
			};	
			
		new Thread(){
			public void run(){
				try{
				String [] dnData = null;
				String result=sendMsgByUserCommand(macNow.getText().toString(),OrderList.READ_SOCKET_RECORD,"");
				System.out.println("result******"+result);
				Message msg=new Message();
				Bundle data=new Bundle();
				if(result==null){
					 dnData=new String[]{"档案读取失败！"};
				}else if(result=="fail"){
					dnData=new String[]{"网关内档案为空！"};
				}
				else{
					int i=result.length()/16;
					dnData=new String[i];
					int s=0;
					for(int j=0;j<i;j++){
						dnData[j]=reverseRst(result.substring(s, s+16))+RstOnline(result.substring(s, s+16));
						s=s+17;
						}
	
					}
					data.putStringArray("dnxx", dnData);
					msg.setData(data);		
					Add_Fragment4_4.this.handler.sendMessage(msg);
				}catch(Exception e){
					Message msg=new Message();
					Bundle data=new Bundle();
					String []dnData=new String[]{"档案读取失败！"};
					data.putStringArray("dnxx", dnData);
					msg.setData(data);		
					Add_Fragment4_4.this.handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return view;
	}
	
	//转换为字符串
	public static String bytesToHexString(byte[] src){       

        StringBuilder stringBuilder = new StringBuilder();       
        if (src == null || src.length <= 0) {       
            return null;       
        }       
        for (int i = 0; i < src.length; i++) {       
            int v = src[i] & 0xFF;       
            String hv = Integer.toHexString(v);       
            if (hv.length() < 2) {       
                stringBuilder.append(0);       
            }       
            stringBuilder.append(hv);       
        }       
        return stringBuilder.toString();       
    }


	
	
	@Override
	public void onDestroyView() {
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		viewGroup.removeView(view);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next3: // finish
			//设备名
			DeviceManager.getInstance().getTempDevice().setName(nameNow.getText().toString());
			//设备mac地址
			DeviceManager.getInstance().getTempDevice().setMac(macNow.getText().toString());
			//作为网关和zigbee设备关联的标识
			DeviceManager.getInstance().getTempDevice().setContact(macNow.getText().toString());
			System.out.println("保存设备联系的concent");
			//设备表号，这里网关的表号设置为跟mac地址一样
			DeviceManager.getInstance().getTempDevice().setTableNum(macNow.getText().toString());
			//设备版本号，网关的版本默认为0001
			DeviceManager.getInstance().getTempDevice().setVersion("0001");
			//设备的电器类型
			DeviceManager.getInstance().getTempDevice().setElec_type(MyDevice.ELEC_ZIGBEE_SOCKET);
			
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

			// 停止smartconfig
			//DeviceManager.getInstance().doSmartConfigStop();
			// 清除 xlw 中所找到的设备
			//DeviceManager.getInstance().doDeviceClear();

			// 结束设备的添加
			context = getActivity().getApplicationContext();
			Intent intent=new Intent(context,MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			//getActivity().finish();
			break;

		}
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
					"result", -1);  
			System.out.println("********RST结果*********"+rst);
			if(rst.equals("none")){
				return "fail";
			}
			rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
						"result", -1);
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
	

		
		
}
