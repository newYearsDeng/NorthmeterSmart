package com.northmeter.northmetersmart.activity_build;

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
import com.northmeter.northmetersmart.order.Type_Entity;

public class Room_GridviewAdp extends BaseAdapter {

	private Context context;
	private List<RoomModel> models;

	public Room_GridviewAdp(Context context) {
		super();
		this.context = context;
		
	}
	
	public void setRoom_GridviewAdp(List<RoomModel> models){
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
			
			viewholder.imageView.setImageResource(models.get(position).getResource());
		   
			viewholder.text_type.setText(Type_Entity.getTypeName(models.get(position).getType()));
			
			viewholder.text_name.setText(models.get(position).getName());
			if(viewholder.text_name.getMaxLines()>1){
				viewholder.text_name.setTextSize(8);
			}

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
