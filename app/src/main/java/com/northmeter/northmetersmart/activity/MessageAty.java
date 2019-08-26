package com.northmeter.northmetersmart.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.fragment.Kt_Frag1;
import com.northmeter.northmetersmart.fragment.Kt_Frag1_Midea;
import com.northmeter.northmetersmart.fragment.Kt_Frag4;
import com.northmeter.northmetersmart.fragment.Kt_Frag_Serve;
import com.northmeter.northmetersmart.fragment.TV_Fragment2;
import com.northmeter.northmetersmart.fragment.TV_Fragment5;
import com.northmeter.northmetersmart.view.MyFragmentPagerAdapter;

/** 空调 */
@SuppressLint("NewApi")
public class MessageAty extends FragmentActivity implements
		OnCheckedChangeListener, OnClickListener {

//	private LinearLayout Linearlayout_val;
	private Kt_Frag1_Midea fragment1_1;
	private Kt_Frag1 fragment1;
	private Kt_Frag_Serve fragment2;
//	private Kt_Frag2 fragment2_1;
//	private Kt_Frag3 fragment3;
//	private Change_Message_Air fragment4;
	private Kt_Frag4 fragment5;
	private TV_Fragment2 fragment6;
//	private Kt_Frag_Other other_fragment;
	private Fragment SelectFragment = null;
	private String str_mac,str_name,str_type,buildid,roleid;
	private TextView title;
	private TV_Fragment5 fragment5_1;
	ViewPager mPager;
	private RadioButton radio_0,radio_1,radio_2,radio_3;

	private RadioGroup radioGroup1;
	private int currIndex = 0;
	
	/**定义接口对象*/
	public DataChange dataChange;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{ 
		setContentView(R.layout.message_aty);
		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
		init_firstview();

		fragment1 = new Kt_Frag1();
		fragment1_1 = new Kt_Frag1_Midea();
		fragment2 = new Kt_Frag_Serve();
		
		//fragment2_1 = new Kt_Frag2();//定时任务
		//fragment3 = new Kt_Frag3();//定时任务
		//fragment4 = new Change_Message_Air();
		fragment5 = new Kt_Frag4();
		fragment5_1 = new TV_Fragment5();//月报表
		
		fragment6 = new TV_Fragment2();//曲线
		
		//other_fragment = new Kt_Frag_Other();
		
		InitViewPager();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init_firstview(){
		Intent intent = getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		buildid = intent.getStringExtra("buildid");
		roleid = intent.getStringExtra("roleid");
		
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup_air);
		radioGroup1.setOnCheckedChangeListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.put_button_2).setOnClickListener(this);
		
		
		radio_0 = (RadioButton) findViewById(R.id.radio0);
		radio_1 = (RadioButton) findViewById(R.id.radio1);
		radio_2 = (RadioButton) findViewById(R.id.radio2);
		radio_3 = (RadioButton) findViewById(R.id.radio3);
		title= (TextView) findViewById(R.id.textView_title_2);
		title.setText(str_name);
	}
	

	@Override
	public void onClick(View v) {
		try{
			switch (v.getId()) {
			case R.id.button1:
				finish();
				break;
			case R.id.put_button_2://右上角设置按钮
				//radioGroup1.check(-1);
				Intent intent = new Intent(MessageAty.this,Change_Message.class);
				intent.putExtra("mac", str_mac);
				intent.putExtra("type", str_type);
				intent.putExtra("name", str_name);
				intent.putExtra("buildid", buildid);
				intent.putExtra("roleid", roleid);
				startActivity(intent);
				
				//loadFragment(fragment4);
				break;
	
			default:
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void ShowAdd() {
//		SelectFragment = FragmentHelper.switchFragment(SelectFragment,
//				fragment3, this);
		//loadFragment(fragment3);
	}
	
	public void showkt_frg2(){
	//	loadFragment(fragment2_1);
	}
	
	public void show_OtherAty(){
		//loadFragment(other_fragment);
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
		case R.id.radio2:
			 mPager.setCurrentItem(2);
			break;
		case R.id.radio3:
			 mPager.setCurrentItem(3);
			 break;
		default:
			break;
		}
	}


	
    private void InitViewPager() {
       mPager = (ViewPager) findViewById(R.id.Linearlayout_val);
       ArrayList<Fragment>  fragmentsList = new ArrayList<Fragment>();
       if(str_name.indexOf("美的")>=0){
			fragmentsList.add(fragment1_1);
		} else{
	        fragmentsList.add(fragment1);
		}
        
        fragmentsList.add(fragment5_1);
        fragmentsList.add(fragment6);
        fragmentsList.add(fragment2);
        
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//返回时引发异常
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(3);//不预存页面
        
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
	            	new RequestHelp(MessageAty.this).get_cq_of_meter(str_mac, str_type);
	                break;
	            case 1:
	            	radio_1.setChecked(true);
	            	new RequestHelp(MessageAty.this).get_work_activities(handler_1,str_mac, str_type);
	            	//new RequestHelp(MessageAty.this).get_report_year_month_list(handler_1,str_mac,str_type);
	            	break;
	            case 2:
	            	radio_2.setChecked(true);
	            	new RequestHelp(MessageAty.this).get_Form_Data30point_1(handler_1,str_mac,str_type);
	            	//new RequestHelp(MessageAty.this).get_Form_Data48h_1(handler_1,str_mac,str_type);
	                break;
	            case 3:
	            	radio_3.setChecked(true);
	            	break;

	            }
	            currIndex = arg0;
        	}catch(Exception e){
	            e.printStackTrace();
	            return;
	            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    
    //解决两次进入viewpage的fragment时造成程序奔溃或者viewpage部分没有图像的问题
    public void onDetach() {
    	//参数是固定写法
		System.out.println("调用方法onDetach");	
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
    
    public void setData(DataChange dataChange){
    	this.dataChange = dataChange;
    }
    
    /**
     * 创建一个接口*/
    public interface DataChange{
    	public void setDataChange(String msg);
    }
    
    Handler handler_1 = new Handler(){
  		@Override
  		public void handleMessage(Message msg) {
  			try{
  				super.handleMessage(msg);
  				if (msg.what == 1) {
  					Message message_msg=new Message();
  					Bundle bundle =new Bundle();
  					String bundle_msg = msg.getData().getString("content");
  					if(bundle_msg.split("/")[1].equals(str_mac)){
  						switch(bundle_msg.split("/")[2]){
  						case "read30point"://selected/(注：前面这些数据不传递过来)     0a0001a820/160127001282/read30point/data
  							try{
  								//Toast.makeText(MessageAty.this, "接口数据read30point："+bundle_msg.split("/")[2], 3000).show();
  								sendBroad("Intent.MessageAty_Read30point", bundle_msg.split("/")[0]+"/"+bundle_msg.split("/")[1]+"/"+
  										bundle_msg.split("/")[2]+"/"+bundle_msg.split("/")[3]);
  								new RequestHelp(MessageAty.this).get_Form_Data48h_1(handler_1,str_mac,str_type);
  							}catch(Exception e){
  								e.printStackTrace();
  							}
  							break;	
  						case "read48h"://selected/(注：前面这些数据不传递过来)     0a0001a820/160127001282/read48h/data
  							try{
  								sendBroad("Intent.MessageAty_Read48h", bundle_msg.split("/")[0]+"/"+bundle_msg.split("/")[1]+"/"+
  										bundle_msg.split("/")[2]+"/"+bundle_msg.split("/")[3]);
  							}catch(Exception e){
  								e.printStackTrace();
  							}
  							break;	
  						case "get_work_activities"://活动数据 selected/(没发送过来)   0a0001aa7k/002014110119/get_work_activities/data
  							try{
  								sendBroad("Intent.get_work_activities", bundle_msg.split("/")[0]+"/"+bundle_msg.split("/")[1]+"/"+
  										bundle_msg.split("/")[2]+"/"+bundle_msg.split("/")[3]);
  								new RequestHelp(MessageAty.this).get_report_year_month_list(handler_1,str_mac,str_type);
  							}catch(Exception e){
  								e.printStackTrace();
  							}
  							break;
  						case "get_report_year_month_list"://报表月份 selected/(没发送过来)   0a0001aa7k/002014110119/get_report_year_month_list/data
  							try{
  								sendBroad("Intent.get_report_year_month_list", bundle_msg.split("/")[0]+"/"+bundle_msg.split("/")[1]+"/"+
  										bundle_msg.split("/")[2]+"/"+bundle_msg.split("/")[3]);
  							}catch(Exception e){
  								e.printStackTrace();
  							}
  							break;
  						}
  					
  					}
  				}
  			}catch(Exception e){
  				e.printStackTrace();
  			}
  		}
  	};
  	
  	
	//接收到来自mqtt推送的消息后发出广播通知；
	private void sendBroad(String action,String str){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra("msg", str);
		sendBroadcast(intent);
	}
	

}
