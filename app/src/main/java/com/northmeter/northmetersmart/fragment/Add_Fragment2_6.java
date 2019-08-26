package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.camera.activity.CaptureActivity;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;
import com.northmeter.northmetersmart.model.Fragment_mac_trance;

/**zigbee四路灯控*/
public class Add_Fragment2_6 extends Fragment implements OnClickListener {
	private View view;
	private EditText zgb_socket1_tabnum;
	private Spinner zgb_Spinner;
	private String mac;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	@SuppressWarnings("null")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
		Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_add_2_2, container, false);
		try{
		zgb_socket1_tabnum = (EditText) view.findViewById(R.id.zgb_socket1);
		zgb_Spinner = (Spinner) view.findViewById(R.id.spinner_zgb);
		view.findViewById(R.id.zgb_socket_next).setOnClickListener(this);
		view.findViewById(R.id.zgb_socket_saomiao).setOnClickListener(this);
		//下拉列表
		String[] mItems =null;
		String data="";
		DBDevice db_d = new DBDevice(getActivity());
		List<MyDevice> devices = db_d.GetMyDevices();
		final List dev= new ArrayList();//用于保存符合条件的设备mac地址
		if(devices!=null){
		for(int i=0;i<devices.size();i++){
			if(devices.get(i).getType()==MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY | devices.get(i).getType()==MyDevice.DEVICE_WIFI_SMART_GATEWAY){
				System.out.println("mItems[] :"+devices.get(i).getType());
				System.out.println(devices.get(i).getName());
				data=data+devices.get(i).getName()+",";
				dev.add(devices.get(i).getMac());
				}
			}
		}
		// 建立数据源
		mItems=data.split(",");
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems);
		_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		zgb_Spinner.setAdapter(_Adapter);
		zgb_Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view,
		            int position, long id) { 
		        System.out.println("选择了 "+ position);
		        zgb_Spinner.setSelection(position);
		        mac = (String) dev.get(position);
		    }
		    @Override 
		    public void onNothingSelected(AdapterView<?> parent) {
		        // TODO Auto-generated method stub
		    }
		});
		zgb_Spinner.setSelection(0);
		mac = (String) dev.get(0);
		
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
		switch(v.getId()){
		case R.id.zgb_socket_saomiao:
			// 开始扫描二维码，得到的信息会存储在 str_scan_result
			Intent intent = new Intent();
			intent.setClass(getActivity(), CaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		
		case R.id.zgb_socket_next:
			if(zgb_socket1_tabnum.getText().length()!=12){
				Toast.makeText(getActivity(), "zigbee四路灯控表号为12位数字组成！", Toast.LENGTH_SHORT).show();
				break;
			}
			Pattern pattern = Pattern.compile("[0-9]{1,}");
			Matcher matcher = pattern.matcher((CharSequence)zgb_socket1_tabnum.getText());
			boolean result=matcher.matches();
			if (result != true) {
				Toast.makeText(getActivity(), "zigbee四路灯控表号为12位数字组成！", Toast.LENGTH_SHORT).show();
				break;
			}
			
//			Add_Fragment4_2 fragment = new Add_Fragment4_2();
//            Bundle bundle = new Bundle();
//            bundle.putString("order", mac);
//            fragment.setArguments(bundle);
//            FragmentManager fragmentManager = getFragmentManager();
//            //开始Fragment事务
//            FragmentTransaction fTransaction = fragmentManager.beginTransaction();
//            //将Fragment添加到事务中，并指定一个TAG 
//            fTransaction.add(fragment, "179521");
//            //提交Fragment事务
//            fTransaction.commit();
			Fragment_mac_trance.getInstance().setMac(mac);
            DeviceManager.getInstance().getTempDevice().setContact(mac);
			DeviceManager.getInstance().getTempDevice().setTableNum(zgb_socket1_tabnum.getText().toString());
			DeviceManager.getInstance().getTempDevice().setMac(zgb_socket1_tabnum.getText().toString());
			DeviceManager.getInstance().getTempDevice().setIp("192.168.1.0");
			DeviceManager.getInstance().getTempDevice().setVersion("0002_1");
			DeviceManager.getInstance().getTempDevice().setElec_type(MyDevice.ELEC_FOUR_CONTROL);//灯控的类型图片，暂时用电灯
			FragmentHelper.loadFragment(FragmentHelper.fragments.get(16));//通过Fragment设置选中
			((RadioButton) RadioHelper.radioGroup.getChildAt(2)).setChecked(true);
			break;
		}
		
	}
	
	/*扫面完成后关闭扫面界面返回时接收传回的数据*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
				if (data != null) {
					if (data.hasExtra("result")) {
						 data.getStringExtra("result");
						 zgb_socket1_tabnum.setText(data.getStringExtra("result").toString());
						 System.out.println("==="+data.getStringExtra("result"));
							return;
						}
				    }
				}
			break;
			}
	}

}
