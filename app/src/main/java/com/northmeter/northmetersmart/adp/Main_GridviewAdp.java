package com.northmeter.northmetersmart.adp;

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
import com.northmeter.northmetersmart.model.Main_GvModel;

public class Main_GridviewAdp extends BaseAdapter {

	private Context context;
	private List<Main_GvModel> models;

	public Main_GridviewAdp(Context context) {
		super();
		this.context = context;
		
	}
	public void setGridviewAdp(List<Main_GvModel> models){
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
			int width = models.get(position).getWidth();
			ViewHolder viewholder = null;
			if(viewholder == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.main_gvitem, null);
				
				convertView.setLayoutParams(new GridView.LayoutParams(width/2, width*2/5));//设置宽高
				
				viewholder = new ViewHolder();
				viewholder.imageView  = (ImageView) convertView.findViewById(R.id.imageview_val);
				viewholder.textview_name =(TextView) convertView.findViewById(R.id.text_name);
				viewholder.textview_role = (TextView) convertView.findViewById(R.id.text_role);
				viewholder.textView = (TextView) convertView.findViewById(R.id.text_title);
				convertView.setTag(viewholder);
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			
			viewholder.imageView.setImageResource(models.get(position).getImg());
			
			viewholder.textview_name.setText(models.get(position).getBuild_name());
			switch(models.get(position).getBuild_roleid()){
			case "manager":
				viewholder.textview_role.setText("权限:管理者");
				break;
			case "analyser":
				viewholder.textview_role.setText("权限:分析者");
				break;
			case "controller":
				viewholder.textview_role.setText("权限:控制者");
				break;
			case "observer":
				viewholder.textview_role.setText("权限:观察者");
				break;
			case "visitor":
				viewholder.textview_role.setText("权限:访客权限");
				break;
				
			}
			
			
	
			//viewholder.textView.setText(models.get(position).getBuilding_id());
			}catch(Exception e){
				e.printStackTrace();
			}
		
		return convertView;
	}
	
	public final class ViewHolder{
		ImageView imageView ;
		TextView textview_name;
		TextView textview_role;
		TextView textView;
	}
}
