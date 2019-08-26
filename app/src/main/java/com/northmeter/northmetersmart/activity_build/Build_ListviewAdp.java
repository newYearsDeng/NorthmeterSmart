package com.northmeter.northmetersmart.activity_build;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;

public class Build_ListviewAdp extends BaseAdapter {

	private Context context;
	private List<BuildModel> models;

	public Build_ListviewAdp(Context context) {
		super();
		this.context = context;
		
	}
	public void setListviewAdp(List<BuildModel> models){
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
		try{
			ViewHolder viewholder = null;
			if(viewholder == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.report_form_item, null);
				
				//convertView.setLayoutParams(new GridView.LayoutParams(width/3, width/3/(426/408)));//设置宽高
				
				viewholder = new ViewHolder();
				
				viewholder.textview1 =(TextView) convertView.findViewById(R.id.textview1);

				convertView.setTag(viewholder);
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			
			String buildName = models.get(position).getBuild_name();

			viewholder.textview1.setText(buildName);
			


			}catch(Exception e){
				e.printStackTrace();
			}
		
		return convertView;
	}
	
	public final class ViewHolder{
		TextView textview1;
	}
}
