package com.northmeter.northmetersmart.adp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.model.Gateway_Model;

/**网关内档案列表显示*/
public class Gateway_ListviewAdp  extends BaseAdapter{
	private Context context;
	private List<Gateway_Model> models;
	private Gateway_Model model;

	public Gateway_ListviewAdp(Context context, List<Gateway_Model> models) {
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
		try{
			ViewHolder viewholder = null;
			if(viewholder == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.gateway_item,
						null);
				viewholder = new ViewHolder();
				viewholder.text_gateway_1 = (TextView) convertView.findViewById(R.id.text_gateway_1);
				viewholder.text_gateway_2 = (TextView) convertView.findViewById(R.id.text_gateway_2);
				viewholder.text_gateway_3 = (TextView) convertView.findViewById(R.id.text_gateway_3);
				convertView.setTag(viewholder);
				
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			model = models.get(position);
			viewholder.text_gateway_1.setText(model.getTable_num());
			viewholder.text_gateway_2.setText(model.getName());
			viewholder.text_gateway_3.setText(model.getOnline());
		}catch(Exception e){
			e.printStackTrace();
		}
		return convertView;
	}
	
	public final class ViewHolder{
		TextView text_gateway_1;
		TextView text_gateway_2;
		TextView text_gateway_3;		
	}
}
