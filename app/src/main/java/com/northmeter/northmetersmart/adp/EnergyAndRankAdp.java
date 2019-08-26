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
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.model.Energy_And_Rank_Model;

/**节能天数和排名显示的适配器*/
public class EnergyAndRankAdp  extends BaseAdapter{
	private Context context;
	private List<Energy_And_Rank_Model> models;
	private Energy_And_Rank_Model model;

	public EnergyAndRankAdp(Context context, List<Energy_And_Rank_Model> models) {
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
				convertView = LayoutInflater.from(context).inflate(R.layout.energy_and_rank,
						null);
				viewholder = new ViewHolder();
				viewholder.textview_1 = (TextView) convertView.findViewById(R.id.textView_rank_1);
				viewholder.textview_2 = (TextView) convertView.findViewById(R.id.textView_rank_2);
				viewholder.textview_3 = (TextView) convertView.findViewById(R.id.textView_rank_3);
				viewholder.textview_4 = (TextView) convertView.findViewById(R.id.textView_rank_4);
				viewholder.textview_5 = (TextView) convertView.findViewById(R.id.textView_rank_5);
				viewholder.layout_back = (LinearLayout) convertView.findViewById(R.id.layout_back);
				convertView.setTag(viewholder);
				
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			model = models.get(position);
			String flag = check_name(model.getText_name());
			System.out.println("*********"+model.getText_name()+"/"+flag);
			if(flag.equals("")){
				viewholder.textview_1.setText(model.getText_name());
				viewholder.layout_back.setBackgroundResource(model.getBackground());
			}else{
				viewholder.textview_1.setText(flag);
				viewholder.layout_back.setBackgroundResource(R.drawable.layout_background);
			}
			viewholder.textview_2.setText(model.getText_energy());
			viewholder.textview_3.setText(model.getText_energy_save());
			viewholder.textview_4.setText(model.getText_percent());
			viewholder.textview_5.setText(model.getText_rank());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return convertView;
	}
	
	public final class ViewHolder{
		TextView textview_1;
		TextView textview_2;
		TextView textview_3;
		TextView textview_4;
		TextView textview_5;
		LinearLayout layout_back;
		
	}
	
	/**检验该表号是不是存在本地数据库里面*/
	private String check_name(String mac){
		String  flag = "";
		try{
			DBDevice device = new DBDevice(context);
			List<MyDevice> mydevice= device.GetMyDevices();
			if(!mydevice.isEmpty()){
				for(int i = 0;i < mydevice.size();i++){
					if(mydevice.get(i).getMac().equals(mac)){
						flag = mydevice.get(i).getName();
					}
				}
			}else{
				flag = "";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
}
