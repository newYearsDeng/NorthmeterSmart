package com.northmeter.northmetersmart.adp;

import java.util.List;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.model.RoomLight_DeviceModel;
import com.northmeter.northmetersmart.order.Type_Entity;

/**
 * 灯控单灯adapter*/
public class RoomLight_DeviceAdapter extends BaseAdapter{
	private Context context;
	private List<RoomLight_DeviceModel> models;

	public RoomLight_DeviceAdapter(Context context) {
		super();
		this.context = context;
		
	}
	
	public void setRoomLight_DeviceAdapter(List<RoomLight_DeviceModel> models){
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
			int width = models.get(position).getWidth();
			ViewHolder viewholder = null;
			if(viewholder == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.room_griditem, null);
				
				convertView.setLayoutParams(new GridView.LayoutParams(width/3, width/3/(426/408)));//设置宽高
				
				viewholder = new ViewHolder();
				viewholder.imageView  = (ImageView) convertView.findViewById(R.id.imageview_val);
				viewholder.imageView1 = (ImageView) convertView.findViewById(R.id.img_status);
				viewholder.text_type =(TextView) convertView.findViewById(R.id.text_type);
				viewholder.text_name = (TextView) convertView.findViewById(R.id.text_name);
				viewholder.textView = (TextView) convertView.findViewById(R.id.text_title);
				convertView.setTag(viewholder);
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			
			viewholder.imageView.setImageResource(Type_Entity.getResource(models.get(position).getEqptType()));

			viewholder.text_type.setText(Type_Entity.getTypeName(models.get(position).getEqptType()));
			viewholder.text_name.setText(models.get(position).getEqptName());

			}catch(Exception e){
				e.printStackTrace();
			}
		
		return convertView;
	}
	
	public final class ViewHolder{
		ImageView imageView ;
		ImageView imageView1;
		TextView text_type;
		TextView text_name;
		TextView textView;
	}
	
}

