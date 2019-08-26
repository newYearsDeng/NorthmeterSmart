package com.northmeter.northmetersmart.activity;

import java.util.List;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;

/**
 * 管理主界面上所有设备显示与隐藏
 * */
public class DeviceDisplayAllAty extends BaseActivity implements OnClickListener{

	private ToggleButton show_togbtn_air;
	private String ip_state;//用来表示是否显示在主界面
	private boolean flag = true;//checkbox的状态设置
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_display_all_aty);
		init_view();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.show_togbtn_air:
			if(show_togbtn_air.isChecked()){
				ip_state = "show_on";
			}else{
				ip_state = "show_off";
			}	
			dialog_show_hide(ip_state);
			break;
		case R.id.button2:
			finish();
			break;
		}
	}

	private void init_view(){
		try{
			show_togbtn_air = (ToggleButton) findViewById(R.id.show_togbtn_air);
			DBDevice db_d = new DBDevice(DeviceDisplayAllAty.this);
			List<MyDevice> devices = db_d.GetMyDevices();
			for(int i=0;i<devices.size();i++){
				if(devices.get(i).getIp().equals("show_off")){
					flag = false;
				}
	 
			 }
			show_togbtn_air.setChecked(flag);
			show_togbtn_air.setOnClickListener(this);// 添加监听事件
			
			findViewById(R.id.button2).setOnClickListener(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**打开弹出提示跳合闸*/
	private void dialog_show_hide(final String ip_state){
		try{
			final Dialog dialogSex = new Dialog(DeviceDisplayAllAty.this, R.style.dialog);
			View viewSex = getLayoutInflater().inflate(R.layout.thz_point, null);
			// 设置dialog没有title
			dialogSex.setContentView(viewSex, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			Window window = dialogSex.getWindow();
//			window.setGravity(Gravity.TOP);
			//dialogSex.setCanceledOnTouchOutside(true); 
			dialogSex.setCancelable(true);
			
			// 可以在此设置显示动画
			window.setWindowAnimations(R.style.AnimBottom_Dialog);
			WindowManager.LayoutParams wl = window.getAttributes();
//			wl.x = 0;
//			wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
			// 以下这两句是为了保证按钮可以水平满屏
			wl.width = LayoutParams.WRAP_CONTENT;
			wl.height = LayoutParams.WRAP_CONTENT;
			// 设置显示位置
			dialogSex.onWindowAttributesChanged(wl);
			
			TextView point_title_01 = (TextView) viewSex.findViewById(R.id.point_title_01);
			TextView point_title_02 = (TextView) viewSex.findViewById(R.id.point_title_02);
			if(ip_state.equals("show_off")){
				point_title_01.setText("隐藏");
				point_title_02.setText("是否隐藏所有设备?");
			}else{
				point_title_01.setText("显示");
				point_title_02.setText("是否显示所有设备?");
			}
			
		    viewSex.findViewById(R.id.button_cancel).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					show_togbtn_air.setChecked(flag);
					dialogSex.cancel();

				}
			});//取消
		    
			viewSex.findViewById(R.id.button_submit).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialogSex.cancel();
					 new Thread(){
						 public void run(){
							 try{
								 DBDevice db_d = new DBDevice(DeviceDisplayAllAty.this); 
								 List<MyDevice> devices = db_d.GetMyDevices();
								 for(int i=0;i<devices.size();i++){
									 MyDevice device = devices.get(i);
									 device.setIp(ip_state);
									 db_d.Update(device);		 
								 }
							 
							 }catch(Exception e){
								 e.printStackTrace();
							 }
						 }
						 
					 }.start();
							
				}
			});//确定
			
			
			
			dialogSex.show();
			
			viewSex.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					show_togbtn_air.setChecked(flag);
					dialogSex.cancel();
					
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	
}
