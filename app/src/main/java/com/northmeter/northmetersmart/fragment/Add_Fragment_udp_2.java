package com.northmeter.northmetersmart.fragment;

/**
 * 添加二型集中器时，根据选择的设备列出详细信息
 * */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.control.UDP_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.model.UDP_model;
import com.northmeter.northmetersmart.model.UDP_model_trance;

/**二型集中器*/
public class Add_Fragment_udp_2 extends Fragment implements OnClickListener {
	private View view;
	private Context context;
	private static UDP_model models;
	private WifiManager manager;
	private static WifiManager.MulticastLock lock;
	private EditText master_ip,master_port,terminal_ip,mask,gateway,soft_version,version_data,hard_version;

	@SuppressLint("WifiManagerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
		Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.add_fragment_udp_2, container, false);
		try{
		manager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
				 lock= manager.createMulticastLock("test wifi");
		view.findViewById(R.id.udp_next1).setOnClickListener(this);
		view.findViewById(R.id.udp_change_first).setOnClickListener(this);
		models= UDP_model_trance.getInstance().getModel();
		for(int i=0;i<1;i++){
			System.out.println(models.getHead());
			System.out.println(models.getTerminal_add());
			System.out.println(models.getMaster_IP_port());
			System.out.println(models.getTerminal_IP_mask_gateway());
			System.out.println(models.getTerminal_time());
			System.out.println(models.getPassmode_onoff());
			System.out.println(models.getDevice_name());
			System.out.println(models.getVersion());
			System.out.println(models.getPoint_capacity());
			System.out.println(models.getTCP_connection_state());
		}
		init();
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	public  void init(){
		master_ip = (EditText) view.findViewById(R.id.master_ip);
		master_ip.setText(UDP_help.get_inter_add(models.getMaster_IP_port().substring(2,10)));
		master_port = (EditText) view.findViewById(R.id.master_port);
		master_port.setText(UDP_help.get_inter_port(models.getMaster_IP_port().substring(10,14)));
		terminal_ip = (EditText) view.findViewById(R.id.terminal_ip);
		terminal_ip.setText(UDP_help.get_inter_add(models.getTerminal_IP_mask_gateway().substring(2,10)));
		mask = (EditText) view.findViewById(R.id.mask);
		mask.setText(UDP_help.get_inter_add(models.getTerminal_IP_mask_gateway().substring(10,18)));
		gateway = (EditText) view.findViewById(R.id.gateway);
		gateway.setText(UDP_help.get_inter_add(models.getTerminal_IP_mask_gateway().substring(18,26)));
		soft_version = (EditText) view.findViewById(R.id.soft_version);
		soft_version.setText(UDP_help.Ascii_To_String(models.getVersion().substring(2,10)));
		version_data = (EditText) view.findViewById(R.id.version_data);
		version_data.setText(models.getVersion().substring(10,16));
		hard_version = (EditText) view.findViewById(R.id.hard_version);
		hard_version.setText(UDP_help.Ascii_To_String(models.getVersion().substring(16,24)));
	}

	@Override
	public void onDestroyView() {
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		viewGroup.removeView(view);
		super.onDestroyView();
	}
	/*获取设备终端地址进行处理得到表号*/
	public static String getID(String address){
		String add_return = null;
		try{
		String add_ = address.substring(0,2);
		String add_0 = address.substring(2,4);
		String add_1 = address.substring(4,6);
		String add_2 = address.substring(6,8);
		String add_3 = add_2+add_1;
		String add_4 = Integer.valueOf(add_3,16)+"";
	    String str="00000";
	    if(add_4.length()<5){
		   add_4=str.substring(add_4.length(),str.length())+add_4;
	    }
	    add_return=add_0+add_+add_4;
		}catch(Exception e){
			e.printStackTrace();
		}
		return add_return;	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.udp_next1:
			try{
			new Thread(){
				public void run(){
					String udp_text=get_send_udp_str();
					sendServerData(udp_text);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					rstar();
				}
			}.start();
			
			DeviceManager.getInstance().getTempDevice().setTableNum("000"+getID(models.getTerminal_add().substring(2,10)));
			DeviceManager.getInstance().getTempDevice().setName("000"+getID(models.getTerminal_add().substring(2,10)));
			DeviceManager.getInstance().getTempDevice().setElec_type(MyDevice.ELEC_ZIGBEE_SOCKET);
			DeviceManager.getInstance().getTempDevice().setMac("000"+getID(models.getTerminal_add().substring(2,10)));
			DeviceManager.getInstance().getTempDevice().setContact("000"+getID(models.getTerminal_add().substring(2,10)));
			DeviceManager.getInstance().getTempDevice().setIp(models.getTerminal_add());
			DeviceManager.getInstance().getTempDevice().setVersion("0001");
			System.out.println("get_send_udp_str():"+get_send_udp_str());;
//			FragmentHelper.loadFragment(FragmentHelper.fragments.get(4));//通过Fragment设置选
//			((RadioButton) RadioHelper.radioGroup.getChildAt(2)).setChecked(true);
			DBDevice db_d = new DBDevice(getActivity());
			List<MyDevice> devices = db_d.GetMyDevices();
			MyDevice md = new MyDevice();
			try{
			if(!devices.isEmpty()) {
				md=db_d.Query("000"+getID(models.getTerminal_add().substring(2,10)));
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
			Intent intent=new Intent(context,MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case R.id.udp_change_first:
			master_ip.setText(UDP_help.get_inter_add(models.getMaster_IP_port().substring(2,10)));
			master_port.setText(UDP_help.get_inter_port(models.getMaster_IP_port().substring(10,14)));
			terminal_ip.setText(UDP_help.get_inter_add(models.getTerminal_IP_mask_gateway().substring(2,10)));
			mask.setText(UDP_help.get_inter_add(models.getTerminal_IP_mask_gateway().substring(10,18)));
			gateway.setText(UDP_help.get_inter_add(models.getTerminal_IP_mask_gateway().substring(18,26)));
//			new Thread(){
//				public void run(){
//					rstar();
//				}
//			}.start();
			break;

		default:
			break;
		}
	}



	public String get_send_udp_str(){//获取修改的报文广播出去
		String udp_send=null;
		models.getHead();//0
		models.getTerminal_add();//1
		UDP_help.to_HexString(master_ip.getText().toString());
		UDP_help.to_HexString_port(master_port.getText().toString());
		String str="00000000000000000000000000000000000000000000";
		String send_1=UDP_help.to_HexString(master_ip.getText().toString())+UDP_help.to_HexString_port(master_port.getText().toString());
		str="01"+send_1+str.substring(send_1.length(),str.length());
		
		UDP_help.to_HexString(terminal_ip.getText().toString());
		UDP_help.to_HexString(mask.getText().toString());
		UDP_help.to_HexString(gateway.getText().toString());

		models.getTerminal_time();//4
		models.getPassmode_onoff();//5
		models.getDevice_name();//6
		models.getVersion();//7
		models.getPoint_capacity();//8
		models.getTCP_connection_state();//9
	    udp_send=models.getHead().substring(0,12)+"82"+models.getHead().substring(14,16)+models.getTerminal_add()+str+"02"+UDP_help.to_HexString(terminal_ip.getText().toString())+
				UDP_help.to_HexString(mask.getText().toString())+UDP_help.to_HexString(gateway.getText().toString())+models.getTerminal_time()+
				models.getPassmode_onoff()+models.getDevice_name()+models.getVersion()+models.getPoint_capacity()+models.getTCP_connection_state().substring(0,4);
		
	    String check_str=UDP_help.get_sum(udp_send);/*计算检验码*/
		udp_send=udp_send+check_str+"16";
		System.out.println("发送的报文："+udp_send);
		return udp_send.toUpperCase();
	}
	
	public static String sendServerData(String udp_text) { /*发送修改的报文*/ 
        String acceptStr = null;  
        try {  
            InetAddress serverAddr = InetAddress.getByName(UDP_help.SERVERIP);  
            DatagramSocket socket =  null;
            if(socket==null){
            	socket = new DatagramSocket(null);
            	socket.setReuseAddress(true);
            	socket.bind(new InetSocketAddress(UDP_help.SERVERPORT));
            	socket.setSoTimeout(20000);
            	}
            
            byte[] buf = UDP_help.strtoByteArray(udp_text);;  
            System.out.println("开始广播");
			lock.acquire();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, UDP_help.SERVERPORT);  
            socket.send(packet);  
            socket.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally{
        	 lock.release();
        } 
        return acceptStr;  
  
    }  
	public static void rstar() {  /*重启设备*/
        String acceptStr = null;  
        try {  
            InetAddress serverAddr = InetAddress.getByName(UDP_help.SERVERIP);  
            DatagramSocket socket =  null;
            if(socket==null){
            	socket = new DatagramSocket(null);
            	socket.setReuseAddress(true);
            	socket.bind(new InetSocketAddress(UDP_help.SERVERPORT));
            	socket.setSoTimeout(20000);
            	}
            /*68 06 5C 55 73 FE 83 00 13 16*/
            String rstar_str="6806"+models.getHead().substring(4,12)+"8300";
    		String check_str=UDP_help.get_sum(rstar_str);
    		rstar_str=rstar_str+check_str+"16";
    		System.out.println("广播报文："+rstar_str);
            byte[] buf = UDP_help.strtoByteArray(rstar_str);//获取包含需要重启设备表号的字符串转换成byte数组进行广播
            System.out.println("开始广播");
			lock.acquire();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, UDP_help.SERVERPORT);  
            socket.send(packet);  
            socket.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally{
        	 lock.release();
        } 
  
    }  
	
}

