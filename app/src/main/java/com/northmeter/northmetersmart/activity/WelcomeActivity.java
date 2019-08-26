package com.northmeter.northmetersmart.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.WindowManager;
import android.widget.Toast;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.wxapi.WXEntryActivity;


/**
 *欢迎页
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	try{
            		startActivity(new Intent(WelcomeActivity.this,WXEntryActivity.class));
                    finish();
            	}catch(Exception e){
            		startActivity(new Intent(WelcomeActivity.this,WXEntryActivity.class));
            		e.printStackTrace();
            	}
               
            }
        }, 2000);
        
    }
    

}
