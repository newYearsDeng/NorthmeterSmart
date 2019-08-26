package com.northmeter.northmetersmart.activity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import androidx.fragment.app.FragmentActivity;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.device.DeviceManager;
import com.northmeter.northmetersmart.fragment.Add_Fragment1;
import com.northmeter.northmetersmart.fragment.Add_Fragment2;
import com.northmeter.northmetersmart.fragment.Add_Fragment2_1;
import com.northmeter.northmetersmart.fragment.Add_Fragment2_2;
import com.northmeter.northmetersmart.fragment.Add_Fragment2_3;
import com.northmeter.northmetersmart.fragment.Add_Fragment2_4;
import com.northmeter.northmetersmart.fragment.Add_Fragment2_5;
import com.northmeter.northmetersmart.fragment.Add_Fragment2_6;
import com.northmeter.northmetersmart.fragment.Add_Fragment3;
import com.northmeter.northmetersmart.fragment.Add_Fragment4;
import com.northmeter.northmetersmart.fragment.Add_Fragment4_1;
import com.northmeter.northmetersmart.fragment.Add_Fragment4_2;
import com.northmeter.northmetersmart.fragment.Add_Fragment4_3;
import com.northmeter.northmetersmart.fragment.Add_Fragment4_4;
import com.northmeter.northmetersmart.fragment.Add_Fragment4_5;
import com.northmeter.northmetersmart.fragment.Add_Fragment4_6;
import com.northmeter.northmetersmart.fragment.Add_Fragment_udp_1;
import com.northmeter.northmetersmart.fragment.Add_Fragment_udp_2;
import com.northmeter.northmetersmart.helper.FragmentHelper;
import com.northmeter.northmetersmart.helper.RadioHelper;

/** 添加设备 */
public class AddAty extends FragmentActivity implements
		OnCheckedChangeListener, OnClickListener {

	private Add_Fragment1 fragment1;
	private Add_Fragment2 fragment2;
	private Add_Fragment2_1 fragment2_1;
	private Add_Fragment3 fragment3;
	private Add_Fragment4 fragment4;
	private Add_Fragment4_1 fragment4_1;
	private Add_Fragment2_2 fragment2_2;
	private Add_Fragment4_2 fragment4_2;
	private Add_Fragment2_3 fragment2_3;
	private Add_Fragment4_3 fragment4_3;
	private Add_Fragment2_4 fragment2_4;
	private Add_Fragment2_5 fragment2_5;
	private Add_Fragment4_4 fragment4_4;
	private Add_Fragment4_5 fragment4_5;
	private Add_Fragment2_6 fragment2_6;
	private Add_Fragment4_6 fragment4_6;
	
	private Add_Fragment_udp_1 fragment_udp_1;
	private Add_Fragment_udp_2 fragment_udp_2;

	private View view1, view2, view3, view4, f1_p1View;
	private Button btn1, btn2, btn3, btn4;

	// scan
	private final static int SCANNIN_GREQUEST_CODE = 1;
	public static String str_scan_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_aty);
//		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
				
		loadViews();
		loadAddToManager();

		// init
		DeviceManager.getInstance().init(this);
		str_scan_result = null;
	}

	private void loadViews() {
		((RadioGroup) findViewById(R.id.radioGroup1))
				.setOnCheckedChangeListener(this);
		RadioHelper.radioGroup = ((RadioGroup) findViewById(R.id.radioGroup1));
		findViewById(R.id.button1).setOnClickListener(this);

		fragment1 = new Add_Fragment1();
		fragment2 = new Add_Fragment2();
		fragment2_1 = new Add_Fragment2_1();
		//fragment3 = new Add_Fragment3();
		fragment4 = new Add_Fragment4();
		fragment4_1=new Add_Fragment4_1();
		fragment2_2=new Add_Fragment2_2();
		fragment4_2=new Add_Fragment4_2();
		fragment2_3=new Add_Fragment2_3();
		fragment4_3=new Add_Fragment4_3();
		fragment2_4=new Add_Fragment2_4();
		fragment2_5=new Add_Fragment2_5();
		fragment4_4=new Add_Fragment4_4();
		fragment4_5=new Add_Fragment4_5();
		fragment2_6=new Add_Fragment2_6();
		fragment4_6=new Add_Fragment4_6();
		
	    fragment_udp_1=new Add_Fragment_udp_1();
		fragment_udp_2=new Add_Fragment_udp_2();
	}

	public void loadAddToManager() {
		FragmentHelper.F_aty = this;
		FragmentHelper.fragments.add( fragment1 );//wifi插座                      0
		FragmentHelper.fragments.add( fragment2 );//wifi插座			1
		FragmentHelper.fragments.add( fragment2_1);//以太网转zigbee	2
		FragmentHelper.fragments.add( fragment4 );//wifi插座			3
		FragmentHelper.fragments.add( fragment4_1);//以太网转zigbee	4
		FragmentHelper.fragments.add( fragment2_2);//zigbee插座		5
		FragmentHelper.fragments.add( fragment4_2);//zigbee插座		6
		FragmentHelper.fragments.add( fragment2_3);//zigbee空调控制器	7
		FragmentHelper.fragments.add( fragment4_3);//zigbee空调控制器	8
		FragmentHelper.fragments.add( fragment2_4);//无线智能网关       	9
		FragmentHelper.fragments.add( fragment2_5);//wifi空调控制器	10
		FragmentHelper.fragments.add( fragment4_4);//无线智能网关	11
		FragmentHelper.fragments.add( fragment4_5);//wifi空调控制器	12
		
		FragmentHelper.fragments.add( fragment_udp_1);//udp搜索添加二型集中器（网关）13
		FragmentHelper.fragments.add( fragment_udp_2);//udp搜索添加二型集中器（网关）14
		
		FragmentHelper.fragments.add( fragment2_6);//四路灯控	15
		FragmentHelper.fragments.add( fragment4_6);//四路灯控	16
		
		//FragmentHelper.fragments.add(fragment3);
		FragmentHelper.loadFragment(fragment1);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next0:
			FragmentHelper.loadFragment(fragment2);
			break;
		case R.id.button_next1:
			FragmentHelper.loadFragment(fragment4);
			break;
		case R.id.button_next2:
			// FragmentHelper.loadFragment(fragment4);
			break;
		case R.id.button_next3: // finish
			finish();
			break;
		case R.id.button1: // back
			finish();
			break;

		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		loadCheckid(checkedId);
	}

	public void loadCheckid(int checkedId) {
		switch (checkedId) {
		case R.id.radio0:
			//FragmentHelper.loadFragment(fragment1);
			break;
		case R.id.radio1:
			//FragmentHelper.loadFragment(fragment2);
			break;
		case R.id.radio2:
			//FragmentHelper.loadFragment(fragment4);
			
			break;
		}
	}

	private View loadViewBy(int layout) {
		return LayoutInflater.from(getApplicationContext()).inflate(layout,
				null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// tv_result.setText("相机回调！ requestCode = " + requestCode);

		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			// tv_result.setText("SCANNIN_GREQUEST_CODE");

			if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
				if (data != null) {
					if (data.hasExtra("result")) {

						// tv_result.setText("RESULT_FIRST_USER！");
						// Bundle bundle = data.getExtras();
						// 显示扫描到的内容

						str_scan_result = data.getStringExtra("result");
					}
				}
			}
		default:
			// str_scan_result = "default!";
		}
	}
}
