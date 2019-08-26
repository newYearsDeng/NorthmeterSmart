package com.northmeter.northmetersmart.activity_build.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity_build.I.IRequestShow;
import com.northmeter.northmetersmart.activity_build.I.RequestInterface;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.view.MyFragmentPagerAdapter;

/**
 * 灯光场景和策略的容器*/
public class Fragment_RoomLight extends Fragment implements IRequestShow {
	Resources resources;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView tvTab_1, tvTab_2,tvTab_3;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int avg;
    public final static int num = 3 ; 
    private Fragment tv_f1;
    private Fragment tv_f2;
    private Fragment tv_f3;
    private View view;
    
    private RequestInterface requestInterface;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		// TODO Auto-generated method stub
	    view = inflater.inflate(R.layout.fragment_energy, null);
		try{
			Intent intent = getActivity().getIntent();
			requestInterface = new RequestInterface(this);
			requestInterface.getHttpRequest_LoginSet_Cookie("http://10.168.1.165:8081/user/login", "{\"userId\":\"admin\",\"pwd\":\"public\"}");
			
			resources = getResources();
	        InitWidth(view);
	        InitTextView(view);
	        InitViewPager(view);
	        TranslateAnimation animation = new TranslateAnimation(position_one,offset,0, 0);
	        tvTab_1.setTextColor(resources.getColor(R.color.royalblue));
	        animation.setFillAfter(true);
	        animation.setDuration(300);
	        ivBottomLine.startAnimation(animation);
        
        /*注册广播*/
      //  RegisterBroad();   
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	
	  @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	private void InitTextView(View parentView) {
		tvTab_1 = (TextView) parentView.findViewById(R.id.tv_tab_1);
		tvTab_2 = (TextView) parentView.findViewById(R.id.tv_tab_2);
		tvTab_3 = (TextView) parentView.findViewById(R.id.tv_tab_3);

		tvTab_1.setOnClickListener(new MyOnClickListener(0));
		tvTab_2.setOnClickListener(new MyOnClickListener(1));
		tvTab_3.setOnClickListener(new MyOnClickListener(2));
	        
        tvTab_1.setText("场景");//tactics
        tvTab_2.setText("群组");//group
        tvTab_3.setText("单灯");
	        
	        
	 }

    private void InitViewPager(View parentView) {
        mPager = (ViewPager) parentView.findViewById(R.id.viewPager);
        fragmentsList = new ArrayList<Fragment>();
        
        tv_f1 = new Fragment_RoomLight_1();
        tv_f2 = new Fragment_RoomLight_2();
        tv_f3 = new Fragment_RoomLight_3();
    	
        fragmentsList.add(tv_f1);
        fragmentsList.add(tv_f2);
        fragmentsList.add(tv_f3);


        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//返回时引发异常
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(3);//预存页面
        
    }

    private void InitWidth(View parentView) {
        ivBottomLine = (ImageView) parentView.findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / num - bottomLineWidth) / 2);
        avg = (int) (screenW / num);
        position_one = avg + offset;
        
        
    }

    public class MyOnClickListener implements View.OnClickListener {
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
	            int position_two = position_one*2-offset;
	            switch (arg0) {
	            case 0:
	            	if (currIndex == 1) {   
		            	animation = new TranslateAnimation(position_one, offset, 0, 0);
		            		
	            	} else if (currIndex == 2) {   
	            		animation = new TranslateAnimation(position_two,offset, 0, 0);  
	            			
	            	}   
	            	tvTab_1.setTextColor(resources.getColor(R.color.royalblue));
            		tvTab_2.setTextColor(resources.getColor(R.color.white));
            		tvTab_3.setTextColor(resources.getColor(R.color.white));
	                break;
	            case 1:
	            	if (currIndex == 0) {   
		            	animation = new TranslateAnimation(offset, position_one, 0, 0);  
	            	} else if (currIndex == 2) {   
	            		animation = new TranslateAnimation(position_two, position_one, 0, 0);   	
	            	} 
	            	tvTab_1.setTextColor(resources.getColor(R.color.white));
            		tvTab_2.setTextColor(resources.getColor(R.color.royalblue));
            		tvTab_3.setTextColor(resources.getColor(R.color.white));
	                break;
	            case 2:   
	            	if (currIndex == 0) {   
	            		animation = new TranslateAnimation(offset, position_two, 0, 0); 
	            	} else if (currIndex == 1) {   
	            		animation = new TranslateAnimation(position_one, position_two, 0, 0);  
	            	} 
	            	tvTab_1.setTextColor(resources.getColor(R.color.white));
            		tvTab_2.setTextColor(resources.getColor(R.color.white));
            		tvTab_3.setTextColor(resources.getColor(R.color.royalblue));
	            	break; 
	            }
	            currIndex = arg0;
	            animation.setFillAfter(true);
	            animation.setDuration(300);
	            ivBottomLine.startAnimation(animation);   
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
    	super.onDetach();
    
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
    

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
			super.onDestroy();
		}

	@Override
	public void requestShow(String msg) {
		// TODO Auto-generated method stub
		URL_help.getInstance().setSet_Cookie(msg);
	}
		
	    
}
