package com.northmeter.northmetersmart.adp;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.model.Machine_LvModel;

public class Machine_ListviewAdp extends BaseAdapter {

	private Context context;
	private List<Machine_LvModel> models;

	public Machine_ListviewAdp(Context context, List<Machine_LvModel> models) {
		super();
		this.context = context;
		this.models = models;
	}

	@Override
	public int getCount() {
		return models.size();
	}

	@Override
	public Object getItem(int i) {
		return models.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.machine_listviewitem, null);
		CheckBox box = (CheckBox) view.findViewById(R.id.isCheck);
		TextView textView1 = (TextView) view.findViewById(R.id.textView1);
		TextView textView2 = (TextView) view.findViewById(R.id.textView2);
		ImageView imageView = (ImageView) view.findViewById(R.id.img_status);
		box.setFocusable(false);
		box.setEnabled(false);
		if (models.get(position).getIscheck() == "false"  )
			box.setChecked(false);
		else
			box.setChecked(true);

		textView1.setText(models.get(position).getTitle());
		textView2.setText(models.get(position).getType());
		imageView.setImageResource(models.get(position).getStatus());
		return view;
	}

}
