package com.northmeter.northmetersmart.helper;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.northmeter.northmetersmart.R;

public class FragmentHelper {
	public static Context F_aty;
	public static List<Fragment> fragments = new ArrayList<Fragment>();

	public static void loadFragment(Fragment fragment) {

		FragmentManager _fragmentManager;
		FragmentTransaction _transaction;
		_fragmentManager = ((FragmentActivity) F_aty)
				.getSupportFragmentManager();
		_transaction = _fragmentManager.beginTransaction();
		_transaction.replace(R.id.Linearlayout_val, fragment);
		// _transaction.addToBackStack(null);//是否加入返回键栈中
		_transaction.commit();
	}
	/** 切换Fragment优化不卡版本 */
	public static Fragment switchFragment(Fragment from, Fragment to, FragmentActivity activity) {
		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		if (from == null)
			transaction.add(R.id.Linearlayout_val, to).commit();
		else if (!to.isAdded())
			// 隐藏当前的fragment，add下一个到Activity中
			transaction.hide(from).add(R.id.Linearlayout_val, to).commit();
		else
			// 隐藏当前的fragment，显示下一个
			transaction.hide(from).show(to).commit();
		return to;
	}

}
