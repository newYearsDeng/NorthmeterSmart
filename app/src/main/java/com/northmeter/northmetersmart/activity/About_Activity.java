package com.northmeter.northmetersmart.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;

/**关于*/
public class About_Activity extends BaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_aty);
		findViewById(R.id.button_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button_back :
			finish();
			break;
		}
		
	}
	
}

