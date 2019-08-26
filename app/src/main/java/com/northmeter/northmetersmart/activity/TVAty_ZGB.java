package com.northmeter.northmetersmart.activity;

import java.util.List;


import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.fragment.TV_ZGB_1;
import com.northmeter.northmetersmart.fragment.TV_ZGB_2;

//以太网转zigbee网关的信息显示
public class TVAty_ZGB extends FragmentActivity implements OnClickListener, OnCheckedChangeListener{
	private TextView title;
	private String str_mac,str_name,str_type,buildid,roleid;
	private RadioGroup radiogroup_zgb;
	private TV_ZGB_1 fragment_zgb1;
	private TV_ZGB_2 fragment_zgb2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
		setContentView(R.layout.tv_aty_zigbee);
		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}		
		init_zgbview();
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.put_button_1).setOnClickListener(this);
		radiogroup_zgb=(RadioGroup) findViewById(R.id.radio_Group2);
		radiogroup_zgb.setOnCheckedChangeListener(this);
		
		fragment_zgb1=new TV_ZGB_1();
		fragment_zgb2=new TV_ZGB_2();
				
		loadFragment(fragment_zgb1);
		}catch(Exception e){
			e.printStackTrace();}
	}
	
	private void init_zgbview(){
		title= (TextView) findViewById(R.id.textView_title_2);
		Intent intent = getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		buildid = intent.getStringExtra("buildid");
		roleid = intent.getStringExtra("roleid");
		
		title.setText(str_name);
	}
	
	private void loadFragment(Fragment fragment) {
		 FragmentManager _fragmentManager;
		 FragmentTransaction _transaction;
		_fragmentManager = getSupportFragmentManager();
		_transaction = _fragmentManager.beginTransaction();
		_transaction.replace(R.id.Linearlayout_zgb_fragment, fragment);
		_transaction.commit();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button1:
			finish();
			break;
		case R.id.put_button_1:
			Intent intent = new Intent(TVAty_ZGB.this,Change_Message.class);
			intent.putExtra("mac", str_mac);
			intent.putExtra("type", str_type);
			intent.putExtra("name", str_name);
			intent.putExtra("buildid", buildid);
			intent.putExtra("roleid", roleid);
			startActivity(intent);
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_0:
			loadFragment(fragment_zgb1);
			break;
		case R.id.radio_1:
			loadFragment(fragment_zgb2);
			break;
		
		default:
			break;
		}
	}
	
	
}
