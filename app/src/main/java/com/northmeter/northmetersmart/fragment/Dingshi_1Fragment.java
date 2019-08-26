package com.northmeter.northmetersmart.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.northmeter.northmetersmart.R;

public class Dingshi_1Fragment extends Fragment implements OnClickListener {

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f_dingshi_1, container, false);
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

	}

}
