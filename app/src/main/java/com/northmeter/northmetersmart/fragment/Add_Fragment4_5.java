package com.northmeter.northmetersmart.fragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;


import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;

/**wifi空调控制器*/
public class Add_Fragment4_5 extends Fragment implements OnClickListener {
	private View view;
	private View f4_p1View;
	private TextView macNow;
	private TextView ipNow;
	// private TextView elecTypeNow;
	private EditText nameNow;
	private TextView biaoHao;
	private Button button_next3;
	
	private PopupWindow popupWindowf1_p1;
	private Integer f4_p1_selecttxt_id;
	private Context context;
	private static String sbiaohao;
	private Handler handler;
	private static String receiv=null;
	static StringBuffer sbu;
	private CustomProgressDialog progressDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_add_4_5, container, false);
		
		progressDialog= CustomProgressDialog.createDialog(getActivity());
		progressDialog.setMessage("读取表号");
		progressDialog.show();
		
		button_next3 = (Button) view.findViewById(R.id.button_next3);
		button_next3.setOnClickListener(this);
		button_next3.setEnabled(false);
		
		macNow = (TextView) view.findViewById(R.id.tv_mac);
		ipNow = (TextView) view.findViewById(R.id.tv_ip);

		view.findViewById(R.id.linear_select).setOnClickListener(this);

		macNow.setText(DeviceManager.getInstance().getTempDevice().getMac());
		ipNow.setText(DeviceManager.getInstance().getTempDevice().getIp());
		loadPopupWindow(inflater);

		// elecTypeNow =
		nameNow = (EditText) view.findViewById(R.id.et_input_name);
		biaoHao =(TextView)view.findViewById(R.id.et_input_biao);
		//biaoHao.setText("141113000580");
		
	    handler = new Handler() {  
	        
            public void handleMessage(Message msg) {  
            	super.handleMessage(msg);  
                Bundle data = msg.getData();  
                String val = data.getString("receive");
                if(val.equals("FANHUI")||val.equals(null)){
                	progressDialog.dismiss();
                	Toast.makeText(getActivity(), "读取表号失败,请重新配置", 3000).show(); 
                	DeviceManager.getInstance().isFoundDevice=false;
                	((RadioButton) RadioHelper.radioGroup.getChildAt(1))
					.setChecked(true);
                	FragmentHelper.loadFragment(FragmentHelper.fragments.get(1));//通过Fragment设置选
                	return;
                }else{ //此处更新ui界面
                	progressDialog.dismiss();
                    Add_Fragment4_5.this.biaoHao.append(val);
                    button_next3.setEnabled(true);
                 }
            };  
        };  
		
	    new Thread(){  
            @Override  
            public void run() 
            {  

            	String s_biao=getsocketsAndNumber();
        	    Message msg = new Message();  
              	Bundle data = new Bundle();
             	data.putString("receive", s_biao);  
             	msg.setData(data);  
             	System.out.println("UI更新......"); 
             	Add_Fragment4_5.this.handler.sendMessage(msg);
             	        
        	       
                }                             
        }.start();  
	

		
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
			// 完成设备的命名
			DeviceManager.getInstance().getTempDevice()
					.setName(nameNow.getText().toString());
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
					if(md.getMac()!=null||md.getMac().equals(null)) {
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

		// 更改电器类型
		case R.id.btn1:
			DeviceManager.getInstance().getTempDevice()
					.setElec_type(MyDevice.ELEC_TELEVISION);
			((TextView) view.findViewById(R.id.tv_select_1))
					.setText(((Button) v).getText());
			f4_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();
			break;

		case R.id.btn2:
			DeviceManager.getInstance().getTempDevice()
					.setElec_type(MyDevice.ELEC_COMPUTER);
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
	//socket连接获取表号
	private String getsocketsAndNumber(){
		String FANHUI="FANHUI";
		Socket socket =null;
    	//DeviceManager.getInstance().getTempDevice().getIp()//获取ip
        try {
			DataOutputStream mBufferedReader = null;
    		DataInputStream  mPrintWriter = null;  
    		 byte [] receive=null;
    		 byte [] receive_1=null;
    		 byte [] receive_2=null;
    	        try {// 创建一个Socket对象，并指定服务端的IP及端口号  
    	        	System.out.println("开始连接......");
    	        	Thread.sleep(3000);
    				socket = getsockets();
    	        	//DeviceManager.getInstance().getTempDevice().getIp()
    				//如果没连上，再进行两次重连
    	        	if(socket==null){
    	        		socket = getsockets();
    	        		if(socket==null){
    	        			socket = getsockets();
    	        			}if(socket==null){
    	        				return FANHUI;
    	        		}	
    	        	}
    	        	
    	            System.out.println("socket.isClosed()+socket.isConnected()"+socket.isClosed()+""+socket.isConnected());
    	            if(socket.isClosed() == true && socket.isConnected() == false){
//    	            	System.out.println("第一次检查");
//    	            	Message msg = new Message();  
//              	        Bundle data = new Bundle(); 
//             	        data.putString("receive", "FANHUI");  
//             	        msg.setData(data);  
//             	        Add_Fragment4.this.handler.sendMessage(msg);
             	        return FANHUI;
    	            }
    	            
    	            System.out.println("连接成功......");
    	            mBufferedReader = new DataOutputStream(socket.getOutputStream());
    	            mPrintWriter    = new DataInputStream(socket.getInputStream()); 
    	            mBufferedReader.flush();
    	            System.out.println("开始发送......"); 
    	            try {
						Thread.sleep(2000);						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 	
//    	            receive_1=new byte[14]; 
//    	            mPrintWriter.readFully(receive_1,0,14);
//    	            
//    	            String strr=bytesToHexString(receive_1);//转换为字符串  
//    	            System.out.println("*******登录包*****"+strr);
//    	            if(receive_1==null){
//    	            	mPrintWriter.readFully(receive_1,0,14);
//    	            	strr=bytesToHexString(receive_1);
//    	            }  
//         
//    	            System.out.println("+++++++str======="+strr.toUpperCase());
    	            //String smsg="68 AA AA AA AA AA AA 68 13 00 DF 16"; 
    	            byte[] sendData= new byte[12];
    	            sendData[0]= (byte)0x68;  sendData[6] = (byte)0xAA;
    	            sendData[1]= (byte)0xAA;  sendData[7] = (byte)0x68;
    	            sendData[2]= (byte)0xAA;  sendData[8] = (byte)0x13;
    	            sendData[3]= (byte)0xAA;  sendData[9] = (byte)0x00;
    	            sendData[4]= (byte)0xAA;  sendData[10]= (byte)0xDF;
    	            sendData[5]= (byte)0xAA;  sendData[11]= (byte)0x16;
    	            mBufferedReader.write(sendData);
    	            mBufferedReader.flush();
    	            System.out.println("发送表号成功......"); 
    	            receive=new byte[21];
    	            sbu=new StringBuffer();
    	            try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	            System.out.println("开始接受表号......"); 
    	            //阻塞式读取数据
    	            mPrintWriter.readFully(receive,0,18);
    	            String str=bytesToHexString(receive);//转换为字符串    
    	            int len_1 = str.indexOf("68");
    	            int len_2=str.indexOf("689306", len_1+7);
    	            if (len_2 < 0){
    	            	len_2=str.indexOf("6822", len_1+7);
    	            	}
    	            
    	            if ((len_1>= 0) && (len_2 < 0))
    	           {
    	        	   mPrintWriter.read(receive);
	    	           str=bytesToHexString(receive);//转换为字符串    
	    	           len_1 = str.indexOf("68");
	    	           len_2 = str.indexOf("689306", len_1+7);
	    	           if (len_2 < 0){
	    	            	len_2=str.indexOf("6822", len_1+7);
	    	            	}
    	            
    	           }
    	            
					System.out.println("+++++++str======="+str.toUpperCase());
    	            System.out.println("+++++++++sbu==========*********"+sbu);
    	            String code;
    	            System.out.println("********"+len_1+" "+len_2);
    	            if ((len_1>= 0) && (len_2 >=0)){
    	            	sbu.append(str);
    	            	code=sbu.substring(len_1+2, len_2);
    	            	}
    	            else
    	            {
    	            	mBufferedReader.write(sendData);
	    	            System.out.println("发送表号成功......"); 
	    	            //receive=new byte[100];
	    	            //sbu=new StringBuffer();
	    	            try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    	            System.out.println("开始接受表号......"); 
	    	            //阻塞式读取数据
	    	            byte [] recs= new byte[100];
	    	            mPrintWriter.read(recs);
	    	            str=bytesToHexString(recs);//转换为字符串    
	    	            len_1 = str.indexOf("68");
	    	            len_2=str.indexOf("689306", len_1+7);
	    	            if (len_2 < 0){
	    	            	len_2=str.indexOf("6822", len_1+7);
	    	            	}
						System.out.println("+++++++str======="+str.toUpperCase());
	    	            System.out.println("+++++++++sbu==========*********"+sbu);
	    	            //String code;
	    	            System.out.println("********"+len_1+" "+len_2);
	    	            if ((len_1>= 0) && (len_2 >=0))
	    	            {
	    	            	sbu.replace(0, 100, str);
	    	            	code=sbu.substring(len_1+2, len_2);
	    	            }   	            		
	    	            else{
	    	            	return FANHUI;
	    	            }
    	            }
    	            
    	        	String rec="";
    	        	for(int i=code.length()/2;i>0;i--){
    	        		rec=rec+code.substring(i*2-2, i*2);
    	        	}
    	        	System.out.println("接受表号完毕......"); 
    	        	sbiaohao=rec;
	    
    	        	

    	        //接收版本号：68 AA AA AA AA AA AA 68 01 02 D5 C1 65 16
	            System.out.println("开始发送......"); 
	        	byte[] sendVersion= new byte[14];
	            	sendVersion[0]=(byte)0x68;  sendVersion[8]= (byte)0x01;
	            	sendVersion[1]=(byte)0xAA;  sendVersion[9]= (byte)0x02;
	            	sendVersion[2]=(byte)0xAA;  sendVersion[10]=(byte)0xD5;
	            	sendVersion[3]=(byte)0xAA;  sendVersion[11]=(byte)0xC1;
	            	sendVersion[4]=(byte)0xAA;  sendVersion[12]=(byte)0x65;
	            	sendVersion[5]=(byte)0xAA;  sendVersion[13]=(byte)0x16;
	            	sendVersion[6]=(byte)0xAA;
	            	sendVersion[7]=(byte)0x68;
	        	mBufferedReader.flush();
	        	mBufferedReader.write(sendVersion);
	        	mBufferedReader.flush();
 	            System.out.println("发送成功......"); 
 	            receive_2=new byte[30];
 	            StringBuffer sbu=new StringBuffer();
 	            try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 	            System.out.println("开始接受......"); 
 	            //阻塞式读取数据
 	            mPrintWriter.read(receive_2);
 	            String ver=bytesToHexString(receive_2);//转换为字符串    
 	            System.out.println("版本号====="+ver);
 	            //68 00 06 00 13 11 14 68 81 05 D5 C1 3C 43 3C E5 16
 	            int start=ver.indexOf("688105d5c1");
 	            //第二次读取版本号
 	            if(start<0){
 	            	try {
 	            		mBufferedReader.flush();
	 		        	mBufferedReader.write(sendVersion);
	 		        	mBufferedReader.flush(); 	 
 						Thread.sleep(2000);
 						mPrintWriter.read(receive_2);
 						ver=bytesToHexString(receive_2);
 					} catch (InterruptedException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 		        	
 	            }
 	           //第三次读取版本号
 	            start=ver.indexOf("688105d5c1");
 	            if(start<0){
 	            	try {
 	            		mBufferedReader.flush();
	 		        	mBufferedReader.write(sendVersion);
	 		        	mBufferedReader.flush(); 	
 						Thread.sleep(2000);
 						mPrintWriter.read(receive_2);
 						ver=bytesToHexString(receive_2);
 					} catch (InterruptedException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 		        	
 	            }
 	            
 	           if(start<0){
 	        	  return FANHUI;
 	        	  }
 	           
 	            String ver_1=ver.substring(start+10, start+16);
 	            String version=manageVersion(ver_1);
 	            System.out.println("版本号====="+ver_1.toUpperCase()+" "+version);
 	            DeviceManager.getInstance().getTempDevice()
				.setVersion(version);
 	            System.out.println("版本号保存成功..........."+version);
 	            
    	        //保存表号
    	        DeviceManager.getInstance().getTempDevice()
						.setTableNum(sbiaohao);
    	        System.out.println("保存成功..........."+sbiaohao);
    	        mBufferedReader.flush();
    			mBufferedReader.close();
    			mPrintWriter.close();
    	        socket.close();
    	        return sbiaohao;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e){
			e.printStackTrace();
		}  		
        }catch(Exception e){
        	e.printStackTrace();
        }
        return FANHUI;
        }
	//版本号数据-33，再反向，补零
	private String manageVersion(String str){
		String str_1=(Integer.parseInt(str.substring(0,2),16)-51)+"";
		String str_2=(Integer.parseInt(str.substring(2,4),16)-51)+"";
		String str_3=(Integer.parseInt(str.substring(4,6),16)-51)+"";	
		//格式化为00类型
		String bzms ="00";
		if(str_1.length()<2){
			str_1=bzms.substring(0, 2-str_1.length())+str_1;
		}
		if(str_2.length()<2){
			str_2=bzms.substring(0, 2-str_2.length())+str_2;
		}
		if(str_3.length()<2){
			str_3=bzms.substring(0, 2-str_3.length())+str_3;
		}
		String version=str_1+"."+str_2+"."+str_3;
		return version;
	}
	
	
	//获取socket连接
		private Socket getsockets(){
			Socket socket =null;
			System.out.println("建立连接......");
	    	//DeviceManager.getInstance().getTempDevice().getIp()//获取ip
	        try {// 创建一个Socket对象，并指定服务端的IP及端口号  
				socket = new Socket(DeviceManager.getInstance().getTempDevice().getIp(), 5000);
				socket.setSoTimeout(15000);
				return socket;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        	
	        	return null;	
		}
		
		
}