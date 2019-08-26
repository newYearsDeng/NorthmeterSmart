package com.northmeter.northmetersmart.activity_build;

import java.lang.reflect.Field;
import java.util.ArrayList;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.fragment.Fragment_Energy;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.view.MyFragmentPagerAdapter;

/**
 * 建筑页面和能源页面的容器
 * */
public class FragmentActivity_Build extends FragmentActivity implements OnCheckedChangeListener,OnClickListener {
	
	final static String TAG = "TV_LOGCAT";
	private Activity_Build fragment1;
	private Fragment_Energy fragment2;
	private RadioGroup radioGroup1;
	// 传进来的数据(mac)     
	private String buildid,buildname,roleid;
	private SharedPreferences sp;
	private CustomProgressDialog progressDialog;
	private RadioButton radio_0,radio_1;
	private Handler handler;
	private Runnable thread;
	private ViewPager mPager;
	private int currIndex = 0;
	
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmentactivity_build);
		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		
		// 获取activity传入的数据
		Intent intent = getIntent();
		buildid = intent.getStringExtra("buildid");
		buildname = intent.getStringExtra("buildname");	
		roleid = intent.getStringExtra("roleid");
		
		// 初始化数据项视图
		initDataView();
		InitViewPager();
	}
	
	
	
	private void initDataView() {
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup1.setOnCheckedChangeListener(this);
		radio_0 = (RadioButton) findViewById(R.id.radio0);
		radio_1 = (RadioButton) findViewById(R.id.radio1);
		
		TextView imageview_add = (TextView) findViewById(R.id.imageview_add);
		imageview_add.setOnClickListener(this);
		findViewById(R.id.img_back).setOnClickListener(this);
		
		if(!roleid.equals(Type_Entity.manager)){
			imageview_add.setVisibility(View.GONE);
		}
		
		TextView build_title = (TextView) findViewById(R.id.build_title);
	    build_title.setText(buildname);
	
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;	
		case R.id.imageview_add:
			Intent intent = new Intent(this,Add_BuildAty_1.class);
			intent.putExtra("buildid", buildid);
			intent.putExtra("buildname", buildname);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio0:
			 mPager.setCurrentItem(0);
			break;
		case R.id.radio1:
			 mPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}
	
	
	
	private void InitViewPager() {
		try{
		    mPager = (ViewPager) findViewById(R.id.linearlayout_val);
		    ArrayList<Fragment>  fragmentsList = new ArrayList<Fragment>();
		    
		    fragment1 = new Activity_Build();
		    fragment2 = new Fragment_Energy();
		    if((buildid.length()==8 && roleid.equals(Type_Entity.manager))||(buildid.length()==8&&roleid.equals(Type_Entity.analyser))){
		    	fragmentsList.add(fragment1);
			    fragmentsList.add(fragment2);
		    }else{
		    	fragmentsList.add(fragment1);
		    	radioGroup1.setVisibility(View.GONE);
		    }
		    
		    mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
		    mPager.setOnPageChangeListener(new MyOnPageChangeListener());//返回时引发异常
		    mPager.setCurrentItem(0);
		    mPager.setOffscreenPageLimit(1);//不预存页面
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public class MyOnClickListener implements OnClickListener {
		private int index = 0;
		
		public MyOnClickListener(int i) {
		    index = i;
		}
		
		@Override
		public void onClick(View v) {
		    mPager.setCurrentItem(index);
		}
	};
	
	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
	
	@Override
	public void onPageSelected(int arg0) {
		try{ 
	        Animation animation = null;
	        switch (arg0) {
	        case 0:
	        	radio_0.setChecked(true);
	            break;
	        case 1:
	        	radio_1.setChecked(true);
	        	break;
	       
	        }
	        currIndex = arg0;
		}catch(Exception e){
	        e.printStackTrace();
	        return;
	        }
		}
	
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {}
	}
	
	//解决两次进入viewpage的fragment时造成程序奔溃或者viewpage部分没有图像的问题
	public void onDetach(){
		try {
			Field childFragmentManager;
			childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	
}
	
