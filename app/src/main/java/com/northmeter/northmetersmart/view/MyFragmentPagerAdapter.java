package com.northmeter.northmetersmart.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;


/*tvaty界面里面第二个*/
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;
    private FragmentManager fm;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
        this.fm=fm;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
    
    public void setFragments(ArrayList<Fragment> fragmentsList) {
    	   if(this.fragmentsList != null){
    	      FragmentTransaction ft = fm.beginTransaction();
    	      for(Fragment f:this.fragmentsList){
    	        ft.remove(f);
    	      }
    	      ft.commit();
    	      ft=null;
    	      fm.executePendingTransactions();
    	   }
    	  this.fragmentsList = fragmentsList;
    	  notifyDataSetChanged();
    	}

}
