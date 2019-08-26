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
import com.northmeter.northmetersmart.model.ReportData_Model;

/**报警消息上报记录*/
public class ReportData_ListviewAdp  extends BaseAdapter{
	private Context context;
	private List<ReportData_Model> models;
	private ReportData_Model model;

	public ReportData_ListviewAdp(Context context, List<ReportData_Model> models) {
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
				convertView = LayoutInflater.from(context).inflate(R.layout.reportdata_item,
						null);
				viewholder = new ViewHolder();
				viewholder.text_report_1 = (TextView) convertView.findViewById(R.id.text_report_1);
				viewholder.text_report_2 = (TextView) convertView.findViewById(R.id.text_report_2);
				convertView.setTag(viewholder);
				
			}else{
				viewholder = (ViewHolder) convertView.getTag();
			}
			model = models.get(position);
			System.out.println(model.getTime()+"/"+model.getReportData());
			viewholder.text_report_1.setText(model.getTime());
			viewholder.text_report_2.setText(model.getReportData());
		}catch(Exception e){
			e.printStackTrace();
		}
		return convertView;
	}
	
	public final class ViewHolder{
		TextView text_report_1;
		TextView text_report_2;		
	}
}
