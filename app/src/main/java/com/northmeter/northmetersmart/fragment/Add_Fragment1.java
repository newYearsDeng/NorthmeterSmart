package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;

public class Add_Fragment1 extends Fragment implements OnClickListener {
	private View view, f1_p1View;
	private PopupWindow popupWindowf1_p1;
	private Integer f1_p1_selecttxt_id;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		loadView(inflater, container);
		loadPopupWindow(inflater);

		// 默认选择项为 DEVICE_WIFI_SMART_SOCKET
		DeviceManager.getInstance().getTempDevice()
				.setType(MyDevice.DEVICE_WIFI_SMART_SOCKET);

		return view;
	}

	public void loadView(LayoutInflater inflater, ViewGroup container) {
		view = inflater.inflate(R.layout.f_add_1, container, false);
		view.findViewById(R.id.button_next0).setOnClickListener(this);
		view.findViewById(R.id.Layout_selecttype_f1).setOnClickListener(this);

	}

	private void loadPopupWindow(LayoutInflater inflater) {
		f1_p1View = inflater.inflate(R.layout.f_add_1p1, null);
		popupWindowf1_p1 = new PopupHelper().getWindow_ALLWRAP(f1_p1View,
				this.getActivity());
		f1_p1View.findViewById(R.id.btn1).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn2).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn3).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn4).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn5).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn6).setOnClickListener(this);
		f1_p1View.findViewById(R.id.btn7).setOnClickListener(this);
		//f1_p1View.findViewById(R.id.btn7).setOnClickListener(this);
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
		case R.id.button_next0:
			String eypt_text=((TextView) view.findViewById(R.id.f1_txt_1)).getText().toString();
			if(eypt_text.equals("WiFi智能插座 W8001")){
				DeviceManager.getInstance().getTempDevice()
				.setContact("NONE");
				FragmentHelper.loadFragment(FragmentHelper.fragments.get(1));//通过Fragment设置选中
				((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
			}else if(eypt_text.equals("以太网智能网关")){
				FragmentHelper.loadFragment(FragmentHelper.fragments.get(2));//通过Fragment设置选中
				((RadioButton) RadioHelper.radioGroup .getChildAt(1)).setChecked(true);
//				FragmentHelper.loadFragment(FragmentHelper.fragments.get(13));//通过Fragment设置选中
//				((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
			}else if(eypt_text.equals("Zigbee智能插座")){
				DBDevice db_d = new DBDevice(getActivity());
				List<MyDevice> devices = db_d.GetMyDevices();
				final List dev= new ArrayList();//用于保存符合条件的设备mac地址
				if(devices!=null){
				for(int i=0;i<devices.size();i++){
					if(devices.get(i).getType()==MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY | devices.get(i).getType()==MyDevice.DEVICE_WIFI_SMART_GATEWAY){
						dev.add(devices.get(i).getMac());
						}
					}
				}
				if(dev.size()==0){
					Toast.makeText(getActivity(), "网关不存在", Toast.LENGTH_SHORT).show();
				}else{
					FragmentHelper.loadFragment(FragmentHelper.fragments.get(5));//通过Fragment设置选中
					((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
				}
				
			}
			else if(eypt_text.equals("Zigbee四路灯控")){
				DBDevice db_d = new DBDevice(getActivity()); 
				List<MyDevice> devices = db_d.GetMyDevices();
				final List dev= new ArrayList();//用于保存符合条件的设备mac地址
				if(devices!=null){
				for(int i=0;i<devices.size();i++){
					if(devices.get(i).getType()==MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY | devices.get(i).getType()==MyDevice.DEVICE_WIFI_SMART_GATEWAY){
						dev.add(devices.get(i).getMac());
						}
					}
				}
				if(dev.size()==0){
					Toast.makeText(getActivity(), "网关不存在", Toast.LENGTH_SHORT).show();
				}else{
					FragmentHelper.loadFragment(FragmentHelper.fragments.get(15));//通过Fragment设置选中
					((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
				}
				
			}
				else if(eypt_text.equals("Zigbee智能空调控制器")){
				DBDevice db_d = new DBDevice(getActivity()); 
				List<MyDevice> devices = db_d.GetMyDevices();
				final List dev= new ArrayList();//用于保存符合条件的设备mac地址
				if(devices!=null){
				for(int i=0;i<devices.size();i++){
					if(devices.get(i).getType()==MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY | devices.get(i).getType()==MyDevice.DEVICE_WIFI_SMART_GATEWAY){
						dev.add(devices.get(i).getMac());
						}
					}
				}
				if(dev.size()==0){
					Toast.makeText(getActivity(), "网关不存在", Toast.LENGTH_SHORT).show();
				}else{
					FragmentHelper.loadFragment(FragmentHelper.fragments.get(7));//通过Fragment设置选中
					((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
				}
			}else if(eypt_text.equals("无线智能网关")){
				FragmentHelper.loadFragment(FragmentHelper.fragments.get(9));//通过Fragment设置选中
				((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
			}else if(eypt_text.equals("WiFi智能空调控制器")){
				FragmentHelper.loadFragment(FragmentHelper.fragments.get(10));//通过Fragment设置选中
				((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
			}
//			else if(eypt_text.equals("Ⅱ型集中器")){
//				FragmentHelper.loadFragment(FragmentHelper.fragments.get(13));//通过Fragment设置选中
//				((RadioButton) RadioHelper.radioGroup.getChildAt(1)).setChecked(true);
//			}
			// FragmentHelper.loadFragment(FragmentHelper.fragments.get(1));//通过Fragment设置选中
			
			break;

		// 更改设备类型
		case R.id.btn1:/*wifi智能插座*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_WIFI_SMART_SOCKET);
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();
			break;

		case R.id.btn2:/*以太网智能网关*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY);
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();
			break;

		case R.id.btn3:/*zigbee智能插座*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_ZIGBEE_SMART_SOCKET);
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();	
			break;

		case R.id.btn4:/*zigbee空调控制器*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_ZIGBEE_SMART_AIR);
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();
			break;
		case R.id.btn5:/*无线智能网关*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_WIFI_SMART_GATEWAY);
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();
			break;
		case R.id.btn6:/*wifi智能空调控制器*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_WIFI_SMART_AIR );
			DeviceManager.getInstance().getTempDevice()
			.setContact("NONE");
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();
			break;
			
		case R.id.btn7:/*zigbee四路灯控*/
			DeviceManager.getInstance().getTempDevice()
					.setType(MyDevice.DEVICE_ZIGBEE_SOCKET_FOUR);
			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
					.getText());
			f1_p1_selecttxt_id = v.getId();
			popupWindowf1_p1.dismiss();	
			break;
//		case R.id.btn7:/*二型集中器*/
//			DeviceManager.getInstance().getTempDevice()
//					.setType(MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY);
//			((TextView) view.findViewById(R.id.f1_txt_1)).setText(((Button) v)
//					.getText());
//			f1_p1_selecttxt_id = v.getId();
//			popupWindowf1_p1.dismiss();
//			break;

		case R.id.Layout_selecttype_f1:
			popupWindowf1_p1.showAsDropDown(v);
			break;
		}
	}

}
