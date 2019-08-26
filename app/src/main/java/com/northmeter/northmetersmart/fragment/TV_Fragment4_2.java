package com.northmeter.northmetersmart.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;

/*定时控制*/
public class TV_Fragment4_2 extends BaseActivity implements  OnClickListener{
	private Button button_return;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tv_f4_2);
		button_return=(Button)findViewById(R.id.dskz_back);
		button_return.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.dskz_back:
			finish();
			break;
		}
	}

	
}
