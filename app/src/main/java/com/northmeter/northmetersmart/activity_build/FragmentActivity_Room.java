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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.UrlSettinAty;
import com.northmeter.northmetersmart.activity.UserCenterAty;
import com.northmeter.northmetersmart.activity_build.I.IRoomDeviceShow;
import com.northmeter.northmetersmart.activity_build.fragment.Fragment_RoomLight;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.view.MyFragmentPagerAdapter;

/**
 * 房间中设备和灯光页面的容器
 * */
public class FragmentActivity_Room extends FragmentActivity implements OnCheckedChangeListener,OnClickListener,IRoomDeviceShow {
	private PopupWindow popupWindow_1;
	final static String TAG = "TV_LOGCAT";
	private Activity_Room fragment1;
	private Fragment_RoomLight fragment2;
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
	private static String bundle_msg;
	
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.fragmentactivity_room);
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
			init_popupWindow_1();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	private void initDataView() {
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup1.setOnCheckedChangeListener(this);
		radio_0 = (RadioButton) findViewById(R.id.radio0);
		radio_1 = (RadioButton) findViewById(R.id.radio1);
		
		ImageView imageview_add = (ImageView) findViewById(R.id.imageview_add);
		imageview_add.setOnClickListener(this);
		findViewById(R.id.img_back).setOnClickListener(this);
		
		if(!roleid.equals(Type_Entity.manager)){
			imageview_add.setVisibility(View.GONE);
		}
	
		TextView room_title = (TextView) findViewById(R.id.room_title);
		room_title.setText(buildname);
	
	}
	
	private void init_popupWindow_1(){
		try{
			View view_1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_room_popup, null);
			popupWindow_1 = new PopupHelper().getWindow_ALLWRAP(view_1,getApplicationContext());
			
			view_1.findViewById(R.id.menu_btn_1).setOnClickListener(this);//系统设置
			view_1.findViewById(R.id.menu_btn_2).setOnClickListener(this);//用户中心
			LinearLayout menu_btn_3 = (LinearLayout) view_1.findViewById(R.id.menu_btn_3);//添加档案
			menu_btn_3.setOnClickListener(this);
			LinearLayout menu_btn_4 = (LinearLayout) view_1.findViewById(R.id.menu_btn_4);//设备管理
			menu_btn_4.setOnClickListener(this);
			if(!roleid.equals(Type_Entity.manager)){
				menu_btn_3.setVisibility(View.GONE);
				menu_btn_4.setVisibility(View.GONE);
		 		
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;	
		case R.id.imageview_add:
			popupWindow_1.showAtLocation(v, Gravity.TOP|Gravity.RIGHT, 0, 0);
			break;
		case R.id.menu_btn_1:
			startActivity(new Intent(FragmentActivity_Room.this,UrlSettinAty.class));
			break;
		case R.id.menu_btn_2:
			startActivity(new Intent(FragmentActivity_Room.this,UserCenterAty.class));
			popupWindow_1.dismiss();
			break;
		case R.id.menu_btn_3://添加档案
			popupWindow_1.dismiss();
			Intent intent = new Intent(FragmentActivity_Room.this,Add_BuildAty_1.class);
			intent.putExtra("buildid", buildid);
			intent.putExtra("buildname", buildname);
			startActivity(intent);
			break;
		case R.id.menu_btn_4:
			popupWindow_1.dismiss();
			Intent intent1 = new Intent(FragmentActivity_Room.this,DeviceDisplayAty.class);
			intent1.putExtra("bundle_msg", bundle_msg);
			intent1.putExtra("buildid", buildid);
			intent1.putExtra("buildname", buildname);
			startActivity(intent1);
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
		    
		    fragment1 = new Activity_Room();
		    fragment2 = new Fragment_RoomLight();
		    
		    fragmentsList.add(fragment1);
			//fragmentsList.add(fragment2);
	
		    
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



	@Override
	public void showRoomDevice(String data) {
		// TODO Auto-generated method stub
		bundle_msg = data;
		
	}
	
	
	
	
	
}
	
