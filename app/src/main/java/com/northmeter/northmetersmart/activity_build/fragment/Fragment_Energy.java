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
import com.northmeter.northmetersmart.view.MyFragmentPagerAdapter;

/**
 * 建筑能源显示*/
public class Fragment_Energy extends Fragment {
	Resources resources;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView tvTabNew, tvTabHot,tv_tab_1;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int avg;
    public final static int num = 3 ; 
    private Fragment tv_f1;
    private Fragment tv_f2;
    private View view;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		// TODO Auto-generated method stub
	    view = inflater.inflate(R.layout.fragment_energy, null);
		try{
			Intent intent = getActivity().getIntent();
			
			resources = getResources();
	        InitWidth(view);
	        InitTextView(view);
	        InitViewPager(view);
	        TranslateAnimation animation = new TranslateAnimation(position_one,offset,0, 0);
	        tvTabNew.setTextColor(resources.getColor(R.color.royalblue));
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
	        tvTabNew = (TextView) parentView.findViewById(R.id.tv_tab_1);
	        tvTabHot = (TextView) parentView.findViewById(R.id.tv_tab_2);

	        tvTabNew.setOnClickListener(new MyOnClickListener(0));
	        tvTabHot.setOnClickListener(new MyOnClickListener(1));
	    }

	    private void InitViewPager(View parentView) {
	        mPager = (ViewPager) parentView.findViewById(R.id.viewPager);
	        fragmentsList = new ArrayList<Fragment>();
	        
	        tv_f1 = new Fragment_Energy_1();
        
        	
	        fragmentsList.add(tv_f1);
	        
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
		            switch (arg0) {
		            case 0:
		                if (currIndex == 1) {
		                    animation = new TranslateAnimation(position_one, offset, 0, 0);
		                    tvTabHot.setTextColor(resources.getColor(R.color.royalblue));
		                } 
		                tvTabNew.setTextColor(resources.getColor(R.color.white));
		                break;
		            case 1:
		                if (currIndex == 0) {
		                    animation = new TranslateAnimation(offset, position_one, 0, 0);
		                    tvTabNew.setTextColor(resources.getColor(R.color.royalblue));
		                } 
		                tvTabHot.setTextColor(resources.getColor(R.color.white));
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
		
	    
}
