package com.northmeter.northmetersmart.adp;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.model.RoomLight_GroupModel;

public class RoomLight_GroupAdapter extends BaseAdapter {

	private Context context;
	private List<RoomLight_GroupModel> models;

	public RoomLight_GroupAdapter(Context context) {
		super();
		this.context = context;
		
	}
	public void setRoomLight_GroupAdapter(List<RoomLight_GroupModel> models){
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
				convertView = LayoutInflater.from(context).inflate(R.layout.roomlight_adp_item, null);
				
				
				viewholder = new ViewHolder();
				
				viewholder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
				viewholder.textview_name =(TextView) convertView.findViewById(R.id.textview_name);

				convertView.setTag(viewholder);
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			
			boolean check = models.get(position).getCheck();
			if(check){
				viewholder.checkbox.setChecked(true);
			}else{
				viewholder.checkbox.setChecked(false);
			}
			viewholder.textview_name.setText(models.get(position).getGroupName());
			
			}catch(Exception e){
				e.printStackTrace();
			}
		
		return convertView;
	}
	
	public final class ViewHolder{
		CheckBox checkbox;
		TextView textview_name;
	}
}
