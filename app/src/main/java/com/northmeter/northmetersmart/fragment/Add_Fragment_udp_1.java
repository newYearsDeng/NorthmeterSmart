package com.northmeter.northmetersmart.fragment;
/**
 * 添加二型集中器时，发送报文搜寻设备，并列表显示搜寻到的设备
 * */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.UDP_listviewAdp;
import com.northmeter.northmetersmart.control.UDP_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;
import com.northmeter.northmetersmart.model.UDP_model;
import com.northmeter.northmetersmart.model.UDP_model_trance;

/**二型集中器*/
public class Add_Fragment_udp_1 extends Fragment implements OnClickListener,OnItemClickListener {
	private View view;
	private WifiManager manager;
	private static WifiManager.MulticastLock lock;
	private Handler handle;
	List<UDP_model> models;
	UDP_listviewAdp listViewAdp;
	private ListView listview_val;
	private CustomProgressDialog progressDialog;
	private Button udp_next0;

	@SuppressLint("WifiManagerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
		Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.add_fragment_udp_1, container, false);
		try{
		view.findViewById(R.id.send_udp).setOnClickListener(this);
		udp_next0 = (Button) view.findViewById(R.id.udp_next0);
		udp_next0.setOnClickListener(this);
		udp_next0.setClickable(false);
		listview_val = (ListView) view.findViewById(R.id.listview_udp);
		listview_val.setOnItemClickListener(this);
		manager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
				 lock= manager.createMulticastLock("test wifi");
				 
		 handle = new Handler(){
				public void handleMessage(Message msg){
					try{
					Bundle data=msg.getData();
					String rec_data=data.getString("udp_data");
					progressDialog.dismiss();
					System.out.println("rce:"+rec_data);
					if(rec_data.equals("NOWIFI")){
						Toast.makeText(getActivity(), "wifi已关闭,请连接wifi再点击搜索！", Toast.LENGTH_SHORT).show();
						return;
					}
					String str="68575C5573FE0109002057982401DA119D795A1B0000000000000000000000000000000002C0A801A8FFFFFF00C0A801010346050901020004856B580201054E4F4E454E414D450000063130303422041542312E3507C70801C116";
					if(rec_data!=""&rec_data.indexOf("6806ffffffff8100eb16")<0&rec_data.length()>=182){
					models=UDP_help.getudp_text(rec_data);
//					models_out = new ArrayList<UDP_model>();
//					for (int i = 0; i <models.size(); i++) {
//						UDP_model model = new UDP_model();
//						model.setHead(models.get(i).getHead());
//						model.setDevice_name(models.get(i).getDevice_name());
//						model.setIscheck(models.get(i).getIscheck());
//						model.setMaster_IP_port(models.get(i).getMaster_IP_port());
//						model.setPassmode_onoff(models.get(i).getPassmode_onoff());
//						model.setPoint_capacity(models.get(i).getPoint_capacity());
//						model.setTCP_connection_state(models.get(i).getTCP_connection_state());
//						model.setTerminal_add(models.get(i).getTerminal_add());
//						model.setTerminal_time(models.get(i).getTerminal_time());
//						model.setVersion(models.get(i).getVersion());
//						model.setTerminal_IP_mask_gateway(models.get(i).getTerminal_IP_mask_gateway());
//						models_out.add(model);
//					}

					listViewAdp = new UDP_listviewAdp(getActivity(), models);
					listview_val.setAdapter(listViewAdp);
					
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				}
			};		
			
		
			progressDialog=CustomProgressDialog.createDialog(getActivity());
			progressDialog.show();
			new Thread(){
				public void run(){	
					if (manager.isWifiEnabled() == false) {
						String rec_udp="NOWIFI";
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("udp_data", rec_udp);
						message.setData(bundle);
						Add_Fragment_udp_1.this.handle.sendMessage(message);
						return;
					}
					String rec_udp=getServerData();
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("udp_data", rec_udp);
					message.setData(bundle);
					Add_Fragment_udp_1.this.handle.sendMessage(message);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_udp:/*发送报文*/
			if (manager.isWifiEnabled() == false) {
				Toast.makeText(getActivity(), "wifi已关闭,请连接wifi再点击搜索！", Toast.LENGTH_SHORT).show();
				break;
			}
			progressDialog=CustomProgressDialog.createDialog(getActivity());
			//progressDialog.setMessage("搜索设备中");
			progressDialog.show();
			new Thread(){
				public void run(){	
					String rec_udp=getServerData();
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("udp_data", rec_udp);
					message.setData(bundle);
					Add_Fragment_udp_1.this.handle.sendMessage(message);
				}
			}.start();
			break;
		
		case R.id.udp_next0://下一步
			for(int i=0;i<models.size();i++){
				if(models.get(i).getIscheck()=="true"){
					UDP_model_trance.getInstance().setModel(models.get(i));
				}
			}
			FragmentHelper.loadFragment(FragmentHelper.fragments.get(14));//通过Fragment设置选
			((RadioButton) RadioHelper.radioGroup.getChildAt(2)).setChecked(true);
			break;

		default:
			break;
		}
	}
	
	 public static String getServerData() {  
	        StringBuffer acceptStr = new StringBuffer();  
	        try {  
	            InetAddress serverAddr = InetAddress.getByName(UDP_help.SERVERIP);  
	            DatagramSocket socket =  null;
	            if(socket==null){
	            	socket = new DatagramSocket(null);
	            	socket.setReuseAddress(true);
	            	socket.bind(new InetSocketAddress(UDP_help.SERVERPORT));
	            	socket.setSoTimeout(5000);
	            	}
	            
	            /*68 06 FF FF FF FF 81 00 EB 16*/
	            byte[] buf = new byte[10];  
	            buf[0]= (byte)0x68;  buf[5] = (byte)0xFF;
	            buf[1]= (byte)0x06;  buf[6] = (byte)0x81;
	            buf[2]= (byte)0xFF;  buf[7] = (byte)0x00;
	            buf[3]= (byte)0xFF;  buf[8] = (byte)0xEB;
	            buf[4]= (byte)0xFF;  buf[9]=  (byte)0x16;
	            System.out.println("开始广播");
				lock.acquire();
	            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, UDP_help.SERVERPORT);  
	            socket.send(packet);  

	            while(true){
	                System.out.println("接收数据");
	                byte[] buffer = new byte[91]; 
	                DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);  
	                socket.receive(recvPacket);
	                System.out.println(" recvPacket.getLength()"+ recvPacket.getLength());
	                InetAddress ad = recvPacket.getAddress();  
	                String s = ad.getHostAddress();  
	                //acceptStr = bytesToHexString(buffer); //new String(recvPacket.getData());  
	                System.out.println("acceptStr"+bytesToHexString(buffer));             
	                if(bytesToHexString(buffer).indexOf("6806ffffffff8100eb16")<0){
	                	acceptStr.append(bytesToHexString(buffer));
	                }
	                if(socket.isClosed()){
	                	break;
	                	}
	                }
	            socket.close();
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally{
	        	 lock.release();
	        } 
	        return acceptStr.toString();  
	  
	    }  
	    
	    
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
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			UDP_model model=models.get(arg2);
			for(int i=0;i<models.size();i++){
				if(models.get(i)!=model){
					UDP_model model_1 = models.get(i);
					model_1.setIscheck("false");
					}
				}
			if(model.getIscheck()=="false"){
				model.setIscheck("true");
				
			}else{
				model.setIscheck("false");
			}
			listViewAdp.notifyDataSetChanged();
			udp_next0.setClickable(true);
		}

}

