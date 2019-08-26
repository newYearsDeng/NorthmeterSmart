package com.northmeter.northmetersmart.activity;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.camera.activity.CaptureActivity;

/** 登录 */
public class LoginAty extends BaseActivity implements OnClickListener {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private ImageView imageView1;
	private EditText editText_name;
	private SharedPreferences spf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_aty);

		spf = getSharedPreferences("Root_Name", MODE_PRIVATE);
		if(spf.getString("Root_Name", null)!=null){
			startActivity(new Intent(this,MainActivity.class));
			finish();
		}else{
			findViewById(R.id.button_zing).setOnClickListener(this);
			findViewById(R.id.button_login).setOnClickListener(this);
			imageView1 = (ImageView) findViewById(R.id.imageView1);
			editText_name = (EditText) findViewById(R.id.editText_name);
			
			Intent intent = new Intent();
			intent.setClass(LoginAty.this, CaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			
			AnimationSet am = new AnimationSet(true);
			AlphaAnimation ap = new AlphaAnimation(1, 0);
			ap.setDuration(1000);
			am.addAnimation(ap);
			imageView1.setAnimation(am);
		}

	}
	/**存储扫面的权限到sharedpreference*/
	private void save_shared_name(String str_name){
		spf = getSharedPreferences("Root_Name", MODE_PRIVATE);
		Editor editor = spf.edit();
		editor.putString("Root_Name", str_name);
		editor.commit();
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_zing:
			Intent intent = new Intent();
			intent.setClass(LoginAty.this, CaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		case R.id.button_login:
			if(editText_name.getText().toString().equals("6ccpug")){
				save_shared_name("6ccpug");
				startActivity(new Intent(this,MainActivity.class));
				finish();
			}else{
				Toast.makeText(getApplication(), "权限码有误，请重新输入！", Toast.LENGTH_SHORT).show();
			}	
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
						 editText_name.setText(data.getStringExtra("result").toString());//显示到输入编辑框；
						 save_shared_name(data.getStringExtra("result").toString());//保存到SharedPreferences；
						 System.out.println("==="+data.getStringExtra("result"));
							return;
						}
				  }
			}
			break;
			}
		}

}
