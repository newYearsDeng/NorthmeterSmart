package com.northmeter.northmetersmart.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.fragment.Fragment_AirQualityQetector_1;
import com.northmeter.northmetersmart.fragment.Fragment_AirQualityQetector_2;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.view.MyFragmentPagerAdapter;

/** 空气质量检测仪*/
@SuppressLint("NewApi")
public class AirQualityQetectorAty extends FragmentActivity implements OnCheckedChangeListener,
		OnClickListener {
	
	final static String TAG = "TV_LOGCAT";

	private Fragment_AirQualityQetector_1 fragment1;
	private Fragment_AirQualityQetector_2 fragment2;
	private RadioGroup radioGroup1;
 
	// 数据项视图
	private ImageView im_imageView1;
	private TextView tv_title;
	
	// 传进来的数据(mac)     
	private String str_mac,str_name,str_type,buildid,roleid;
	private SharedPreferences sp;
	private CustomProgressDialog progressDialog;
	private RadioButton radio_0,radio_1,radio_2,radio_3;
	private Handler handler;
	private Runnable thread;
	private ViewPager mPager;
	private int currIndex = 0;
	
	/**定义接口对象*/
	public static DataChange dataChange;
	    
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tv_aty);
		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		
		URL_help.getInstance().setTab_time(System.currentTimeMillis());
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup1.setOnCheckedChangeListener(this);
		radio_0 = (RadioButton) findViewById(R.id.radio0);
		radio_1 = (RadioButton) findViewById(R.id.radio1);
		radio_2 = (RadioButton) findViewById(R.id.radio2);
		radio_3 = (RadioButton) findViewById(R.id.radio3);
		
		radio_1.setVisibility(View.GONE);
		radio_3.setVisibility(View.GONE);
		
		findViewById(R.id.put_button).setOnClickListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		
	  
//----------------------------------------------------------------------------------
		
		// 获取activity传入的数据
		Intent intent = getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		buildid = intent.getStringExtra("buildid");
		roleid = intent.getStringExtra("roleid");

		// 初始化数据项视图
		initDataView();
		InitViewPager();
	}



	private void initDataView() {

		System.out.println("开始初始化View！");

		tv_title = (TextView) findViewById(R.id.textView1);
		im_imageView1 = (ImageView) findViewById(R.id.imageView_icon);
	
		tv_title.setText(str_name);

		}
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			finish();
			break;	
		case R.id.put_button:
			Intent intent = new Intent(AirQualityQetectorAty.this,Change_Message.class);
			intent.putExtra("mac", str_mac);
			intent.putExtra("type", str_type);
			intent.putExtra("name", str_name);
			intent.putExtra("buildid", buildid);
			intent.putExtra("roleid", roleid);
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
	/**
	 * 将单个字符的日期（时间） 转换成两个字符 如：1 -> 01
	 */
	private String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio0:
			 mPager.setCurrentItem(0);
			break;
		case R.id.radio1:
			 //mPager.setCurrentItem(1);
			break;
		case R.id.radio2:
			 mPager.setCurrentItem(1);
			break;
		case R.id.radio3:
			//mPager.setCurrentItem(3);
			break;
		default:
			break;
		}
	}


	
    private void InitViewPager() {
    	try{
	        mPager = (ViewPager) findViewById(R.id.linearlayout_val);
	        ArrayList<Fragment>  fragmentsList = new ArrayList<Fragment>();
	        
	        fragment1 = new Fragment_AirQualityQetector_1();
	        fragment2 = new Fragment_AirQualityQetector_2();

	        fragmentsList.add(fragment1);
	        fragmentsList.add(fragment2);

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
	            	new RequestHelp(AirQualityQetectorAty.this).get_cq_of_meter(str_mac, str_type);
	            	new RequestHelp(AirQualityQetectorAty.this).get_socket_readtime_workflow(str_mac, str_type);
	                break;
	            case 1:
	            	radio_2.setChecked(true);
	            	new RequestHelp(AirQualityQetectorAty.this).get_Form_Data30point_1(handler_1,str_mac,str_type);
	            	break;
	            case 2:
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
    public void onDetach(){
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
     * 创建一个接口
     */
    public interface DataChange {
        void setDataChange(String receive);
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
						case "read30point"://selected/(注：前面这些数据不传递过来)    /0a0001a880/131701090026/read30point/2017-07-28 04:47:04 0007 0010 0011 31 60 0046 0410
							try{
								//通讯质量显示
								//Toast.makeText(TVAty.this, "接口数据："+bundle_msg.split("/")[2], 3000).show();
								dataChange.setDataChange(bundle_msg.split("/")[3]);
							}catch(Exception e){
								e.printStackTrace();
							}
							break;	
						case "get_work_activities"://活动数据 selected/(没发送过来)   0a0001aa7k/002014110119/get_work_activities/data
  							try{
  								sendBroad("Intent.get_work_activities", bundle_msg.split("/")[0]+"/"+bundle_msg.split("/")[1]+"/"+
  										bundle_msg.split("/")[2]+"/"+bundle_msg.split("/")[3]);
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
