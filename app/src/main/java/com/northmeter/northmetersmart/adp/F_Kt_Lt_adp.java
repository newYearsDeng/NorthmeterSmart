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
import com.northmeter.northmetersmart.model.F_Kt_Lv_Model;

/** 空调定时ListView Adapter */
public class F_Kt_Lt_adp extends BaseAdapter {
	
	//private Map<Integer,Integer> selected;
	private Context context;
	private List<F_Kt_Lv_Model> models;
	private List<Boolean> mChecked;
	private F_Kt_Lv_Model model;
	public F_Kt_Lt_adp(Context context, List<F_Kt_Lv_Model> models) {
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
			 convertView = LayoutInflater.from(context).inflate(R.layout.f_dingshi_kt_item, null);
			 viewholder = new ViewHolder();
			 viewholder.txt_time=(TextView) convertView.findViewById(R.id.txt_time);
			 viewholder.txt_weekday=(TextView) convertView.findViewById(R.id.txt_weekday);
			 viewholder.txt_1 = (TextView) convertView.findViewById(R.id.txt_1);
			 viewholder.txt_2 = (TextView) convertView.findViewById(R.id.txt_2);
			 viewholder.txt_3 = (TextView) convertView.findViewById(R.id.txt_3);
			 viewholder.txt_4 = (TextView) convertView.findViewById(R.id.txt_4);
			 viewholder.txt_5 = (TextView) convertView.findViewById(R.id.txt_5);
			 viewholder.txt_6 = (TextView) convertView.findViewById(R.id.txt_6);
			 viewholder.chk_select = (CheckBox) convertView.findViewById(R.id.chk_select);
			 //viewholder.chk_onoff  = (CheckBox) convertView.findViewById(R.id.chk_onoff);
			 convertView.setTag(viewholder);
		}else{
			viewholder=(ViewHolder) convertView.getTag();
		}
		model = models.get(position);
		viewholder.txt_time.setText( model.getTime());
		viewholder.txt_weekday.setText(model.getWeekday());
		viewholder.txt_1.setText(model.getTxt1());
		viewholder.txt_2.setText(model.getTxt2());
		viewholder.txt_3.setText(model.getTxt3());
		viewholder.txt_4.setText(model.getTxt4());
		viewholder.txt_5.setText(model.getTxt5());
		viewholder.txt_6.setText(model.getTxt6());
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
		
//		if(model.isOpen()==true){
//			viewholder.chk_onoff.setChecked(true);
//		}else{
//			viewholder.chk_onoff.setChecked(false);
//		}
//		 if(selected.containsKey(position))
//			 viewholder.chk_select.setChecked(true);
//         else
//        	 viewholder.chk_select.setChecked(false);
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
		TextView txt_time;
		TextView txt_weekday;
		TextView txt_1;
		TextView txt_2; 
		TextView txt_3;
		TextView txt_4;
		TextView txt_5;
		TextView txt_6;
		CheckBox chk_select;
	//	CheckBox chk_onoff;	
	}


}
