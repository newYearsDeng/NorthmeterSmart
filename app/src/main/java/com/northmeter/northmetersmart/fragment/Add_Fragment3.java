package com.northmeter.northmetersmart.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.helper.FragmentHelper;

public class Add_Fragment3 extends Fragment implements OnClickListener {
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_add_3, container, false);
		view.findViewById(R.id.button_next2).setOnClickListener(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		viewGroup.removeView(view);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_next2:
			FragmentHelper.loadFragment(FragmentHelper.fragments.get(3));
			break;

		default:
			break;
		}
	}

}
