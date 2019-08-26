package com.northmeter.northmetersmart.activity_build.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.model.RoomLight_DeviceModel;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.utils.Utils;

/**单灯控制页面*/
public class Activity_LightControl extends BaseActivity implements OnClickListener,Fragment_RoomLight_3.IShowIntentData,IRequestShow {
	private SeekBar seekbar;
	private TextView textview_title,light_num,text_dy,text_dl,text_gl;
	private CheckBox checkbox;
	private String thz="03",str_mac;
	private int light=0,first_light;//亮度 
	
	
	private CustomProgressDialog progressDialog;
	public static RoomLight_DeviceModel deviceModel = new RoomLight_DeviceModel();

	private RequestInterface requestInterface;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.activity_light_control);
			init_view();
			init_seekBar();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_view(){
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		
		requestInterface = new RequestInterface(this);
		
		textview_title = (TextView) findViewById(R.id.textview_title);
		textview_title.setText(title);
		light_num = (TextView) findViewById(R.id.light_num);
		findViewById(R.id.but_control).setOnClickListener(this);//控制按钮
		findViewById(R.id.but_back).setOnClickListener(this);//返回按钮
		findViewById(R.id.img_refresh).setOnClickListener(this);//刷新按钮
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		checkbox.setOnClickListener(this);
		
		text_dy = (TextView) findViewById(R.id.text_dy);
		text_dl = (TextView) findViewById(R.id.text_dl);
		text_gl = (TextView) findViewById(R.id.text_gl);
	}
	
	private void init_seekBar(){
		seekbar = (SeekBar) findViewById(R.id.seekbar_1);
		seekbar.setProgress(50);
		light = 50;
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				 float pro = seekbar.getProgress();
		         float num = seekbar.getMax();
		         int result = Math.round((pro / num) * 100);
		         light_num.setText("亮度"+result+"%");
		         light = Math.round(result);
		         
			}
		});
	}

    private void controlLight(){
    	try{
	    	progressDialog = CustomProgressDialog.createDialog(this);
	 	    progressDialog.show();
	 	    new Thread(){
	 	    	public void run(){
	 	    		MyOrder odToSend;
	 				switch (deviceModel.getEqptType()) {  
	 				case Type_Entity.Light_control:// 照明灯控
	 					odToSend = OrderList.getSendByDeviceType(Type_Entity.Light_control,
	 							deviceModel.getEqptIdCode(), OrderList.Control_Light,getLedControl(deviceModel.getEqptIdCode(),thz,light));
	 					break;
	 				default:
	 					odToSend = null;
	 					break;
	 				}
	 				String send_msg = Utils.sendOrder(odToSend);
	 				System.out.println("+++++++++++++++++"+ URL_help.getUniqueInstance().getUrl_address());
	 				requestInterface.getHttpRequestPost(URL_help.getUniqueInstance().getUrl_address(), send_msg);
	 	    	}	
	 	    }.start();
	    	
	    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.but_control://执行
			controlLight();
			break;
		case R.id.but_back://返回
			finish();
			break;
		case R.id.checkbox:
			if(checkbox.isChecked()){
				thz = "03";
			}else{
				thz = "13";
			}
			break;
		case R.id.img_refresh://刷新
			break;
		}
	}
	
	/**																							  //46（46-33 = 13 （1表示合闸，3解析为2进制为0011，pwm两路启用））
	/**led控制																						52(52-33 = 1f (1表示合闸，f为1111，全部启用)  )
	 * //6806002405160368 14(控制码) 15（长度） 3343EFEF（标示符，0001bcbc）  33333333(密码) AB896745（操作者代码）  46（合闸，3路）  3338333833333333 2916
	 * */
	private String getLedControl(String mac,String thz,int light){
		String sent_str="";
		try{
			sent_str = "68"+Type_Entity.reverseRst(mac)+"6814153343EFEF"+"33333333AB896745"+Integer.toHexString(Integer.valueOf(thz,16)+51);
 

			//计算亮度值
			StringBuffer sb = new StringBuffer();
			
			String num1  = Integer.toHexString(light*60);
			String flag = "0000";
			num1 = flag.substring(num1.length(),4)+num1;
			
			System.out.println(num1);
			for(int i=0;i<num1.length()/2;i++){
				String t = Integer.toHexString(Integer.valueOf(num1.substring(i*2, i*2+2),16)+51);
				if(t.length()>2){
					t= t.substring(t.length()-2,t.length());
				}
				sb.append(t);
			}
			
			sent_str = sent_str+Type_Entity.reverseRst("33333333"+sb.toString()+sb.toString());
			//计算检验核
			String check_code = Type_Entity.get_sum(sent_str);
			sent_str = sent_str+check_code+"16";
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("调光："+sent_str);
		return sent_str;
	}
	

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Object object =  msg.obj;
        	switch(msg.what){
        	case 0:
        		deviceModel = (RoomLight_DeviceModel) object;
        		System.out.println(deviceModel.getEqptType());
        		break;
        	case 1:
        		if(object.toString().equals("success")){
        			Toast.makeText(Activity_LightControl.this, "操作成功", Toast.LENGTH_SHORT).show();
        		}else{
        			Toast.makeText(Activity_LightControl.this, "操作失败", Toast.LENGTH_SHORT).show();
        		}
        		if(progressDialog.isShowing()){
        			progressDialog.dismiss();
        		}
        		break;
        	}
        }
	};
	
	@Override
	public void showIntentData(RoomLight_DeviceModel deviceModel) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 0;
		message.obj = deviceModel; 
		mHandler.sendMessage(message);
	}

	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 1;
		if(!msg.equals("exception")||msg!=null){
			String status = OrderManager.getInstance().getItemByOrder(msg,
					"status", -1);
			if(status.equals("200")){
				message.obj = "success"; 
			}else{
				message.obj = "fail";
			}
		}else{
			message.obj = "exception";
		}
		mHandler.sendMessage(message);
		
	}
	

}
