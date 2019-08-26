package com.northmeter.northmetersmart.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;

/**以太网转zigbee网关*/
public class Add_Fragment2_1 extends Fragment implements OnClickListener {
	private View view;
	private EditText zigbeeEdit1,zigbeeEdit2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
		Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_add_2_1, container, false);
		view.findViewById(R.id.next_zigbee).setOnClickListener(this);
		zigbeeEdit1=(EditText) view.findViewById(R.id.zigbeeEdit1);//网关号
		zigbeeEdit2=(EditText) view.findViewById(R.id.zigbeeEdit2);//网关名称
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
		case R.id.next_zigbee:
			DeviceManager.getInstance().getTempDevice().setTableNum(zigbeeEdit1.getText().toString());
			DeviceManager.getInstance().getTempDevice().setName(zigbeeEdit2.getText().toString());
			DeviceManager.getInstance().getTempDevice().setElec_type(MyDevice.ELEC_ZIGBEE_SOCKET);
			DeviceManager.getInstance().getTempDevice().setMac(zigbeeEdit1.getText().toString());
			DeviceManager.getInstance().getTempDevice().setContact(zigbeeEdit1.getText().toString());
			DeviceManager.getInstance().getTempDevice().setIp("192.168.1.0");
			DeviceManager.getInstance().getTempDevice().setVersion("0001");
			FragmentHelper.loadFragment(FragmentHelper.fragments.get(4));//通过Fragment设置选
			((RadioButton) RadioHelper.radioGroup.getChildAt(2)).setChecked(true);
			break;

		default:
			break;
		}
	}

}
