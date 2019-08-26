package com.northmeter.northmetersmart.adp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.model.Kt_Task_OneModel;
import com.northmeter.northmetersmart.utils.Utils;

/** 空调一次性定时ListView Adapter */
public class Kt_Task_OneAdp extends BaseAdapter {
	
	//private Map<Integer,Integer> selected;
	private Context context;
	private List<Kt_Task_OneModel> models;
	private Kt_Task_OneModel model;
	public Kt_Task_OneAdp(Context context, List<Kt_Task_OneModel> models) {
		super();
		this.context = context;
		this.models = models;
		//selected=new HashMap<Integer,Integer>();
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
		try{
		ViewHolder viewholder = null;
		View view;
		if(viewholder==null){
			 convertView = LayoutInflater.from(context).inflate(R.layout.kt_task_one_item, null);
			 viewholder = new ViewHolder();
			 viewholder.txt_action=(TextView) convertView.findViewById(R.id.txt_action);
			 viewholder.txt_time=(TextView) convertView.findViewById(R.id.txt_time);
			 viewholder.txt_continue_time = (TextView) convertView.findViewById(R.id.txt_continue_time);
			 viewholder.chk_select = (CheckBox) convertView.findViewById(R.id.chk_select);
			 //viewholder.chk_onoff  = (CheckBox) convertView.findViewById(R.id.chk_onoff);
			 convertView.setTag(viewholder);
		}else{
			viewholder=(ViewHolder) convertView.getTag();
		}
		model = models.get(position);
		if(model.getControl_id().equals("tz")){
			viewholder.txt_action.setText( "跳闸");
		}else{
			viewholder.txt_action.setText( "合闸");
		}
		
		viewholder.txt_time.setText(model.getTime());
		viewholder.txt_continue_time.setText(getContinueHour(model.getTime(),model.getContinue_time())+" 小时");

		viewholder.chk_select.setChecked(false);
		if(model.isCheck()==true){
			viewholder.chk_select.setChecked(true);
		}else{
			viewholder.chk_select.setChecked(false);
		}
		if(model.isVisibility()){
			viewholder.chk_select.setVisibility(View.VISIBLE);
		}else{
			viewholder.chk_select.setVisibility(View.GONE);
		}
		
		addListener(viewholder,position);//添加事件响应
		final int pos = position;
		}catch(Exception e){
			e.printStackTrace();
			}
		return convertView;
	}
	
	 private void addListener(ViewHolder holder,final int position){
		 holder.chk_select.setOnCheckedChangeListener(new OnCheckedChangeListener(){
               @Override
               public void onCheckedChanged(CompoundButton buttonView,
                           boolean isChecked) {
            	   System.out.println("点击选中的checkBox");
                    // TODO Auto-generated method stub
                     if(isChecked==false){
                    	 model.setCheck(false);
                    	 System.out.println("model.setcheck:"+model.isCheck());
//                        if(!selected.containsKey(buttonView.getTag()))
//                        	selected.put((Integer) buttonView.getTag(),position);
                     }else{
                    	 model.setCheck(true);
                    	 System.out.println("model.setcheck:"+model.isCheck());
                    	 //selected.remove((Integer) buttonView.getTag());
                     }
               }           
         });
		 
//		 holder.chk_onoff.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//             @Override
//             public void onCheckedChanged(CompoundButton buttonView,
//                         boolean isChecked) {
//          	   System.out.println("点击选中的chk_onoff");
//                  // TODO Auto-generated method stub
//                   if(isChecked==false){
//                  	 model.setOpen(false);
//                  	 System.out.println("model.isopen:"+model.isOpen());                  	 
//                   }else{
//                  	 model.setOpen(true);
//                  	 System.out.println("model.isopen:"+model.isOpen());
//                   }
//             }           
//       });

   }

	
	public final class ViewHolder{
		TextView txt_action;//执行动作
		TextView txt_time;//执行时间
		TextView txt_continue_time;//持续时长
		CheckBox chk_select;//选择项
	}
	
	/**把持续时间 转换为时间数*/
	private static String getContinueHour(String time,String continue_time_1){
		String continue_hour = null;
		try{
			String now_time = Utils.dateToStamp(time);
			String continue_time = Utils.dateToStamp(continue_time_1);
			long result_time = Long.parseLong(continue_time)-Long.parseLong(now_time);		
			continue_hour = result_time/3600000+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return continue_hour;
	}
	


}
