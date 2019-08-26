package com.northmeter.northmetersmart.adp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.model.Center_LvModel;

public class Center_listviewAdp extends BaseAdapter {
	private Context context;
	private List<Center_LvModel> models;

	public Center_listviewAdp(Context context, List<Center_LvModel> models) {
		super();
		this.context = context;
		this.models = models;
	}

	@Override
	public int getCount() {
		return models.size();
	}

	@Override
	public Object getItem(int position) {
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.center_listviewitem, null);
		view.getBackground().setAlpha(180);
		TextView title = (TextView) view.findViewById(R.id.textView1);
		TextView version = (TextView) view.findViewById(R.id.textView2);
		ImageView ico = (ImageView) view.findViewById(R.id.imageView1);

		title.setText(models.get(position).getTitle());
		version.setText(models.get(position).getVersion());
		if (models.get(position).getImgs() != null) {
			ico.setImageResource(models.get(position).getImgs());
		} else {
			// ico.setVisibility(View.GONE);
		}
		return view;
	}

}
