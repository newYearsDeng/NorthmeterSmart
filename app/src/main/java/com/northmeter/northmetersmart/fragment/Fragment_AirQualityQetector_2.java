package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.AirQualityQetectorAty;
import com.northmeter.northmetersmart.helper.PopupHelper;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**空气质量检测仪曲线 30个点*/
public class Fragment_AirQualityQetector_2 extends Fragment implements OnClickListener,OnChartValueSelectedListener,AirQualityQetectorAty.DataChange {
	private View view;
	private String str_mac,str_name,str_type;
	private TextView text_action;
	private PopupWindow popupWindow;
	private LineChart lineChar;
	private int[] mColors = ColorTemplate.VORDIPLOM_COLORS;
	private int dataSelect=3;//选择显示数据项
	private static String result;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_airqualityqetector_2, container, false);
		init_view();
		init_popupWindow();
		((AirQualityQetectorAty) getActivity()).setData(this);
		
		init_lineChar();
		String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
		String [] zdnVals = { "0" }; // 避免数据库没数据时，图表出错
		init_totalChartData(zdnVals,xVals);
		
		

		return view;
	}
	
	private void init_view(){
		Intent intent = getActivity().getIntent();
		str_mac = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		
		text_action = (TextView) view.findViewById(R.id.text_action);
		text_action.setOnClickListener(this);
		
		lineChar = (LineChart) view.findViewById(R.id.linechar);
		
	}
	
	/**初始化图表*/
	private void init_lineChar(){
		// 图表
		lineChar.setOnChartValueSelectedListener(this);
		lineChar.setDrawGridBackground(false);
		lineChar.setDescription("");
		//mChart.setDrawYValues(true);
		lineChar.setDragEnabled(true);//设置是否可以拖拽；
		lineChar.setScaleEnabled(true);
		lineChar.animateY(1500); // 立即执行的动画,x轴
		lineChar.setDescriptionColor(mColors[3]);//曲线上标签字体颜色
		lineChar.setGridBackgroundColor(mColors[3] & 0x70FFFFFF);
		lineChar.setAlpha(1.0f);
		lineChar.getXAxis().setDrawGridLines(false); 
		//mChart.setDrawYValues(false);//设置是否显示y轴的值的数据

		YAxis y = lineChar.getAxisLeft(); // y轴的标示
	    y.setTextColor(mColors[3]);
	    
	    YAxis y1 = lineChar.getAxisRight(); // 右侧y轴的标示
	    y1.setTextColor(mColors[3]);
	    //y1.setEnabled(false);

	   	XAxis x = lineChar.getXAxis(); // x轴显示的标签
		x.setTextColor(mColors[3]);
		x.setPosition(XAxisPosition.BOTTOM);
	}
	
	/**12月用电曲线数据*/
	private void init_totalChartData(String[] zdn_list,String[] time_list){
		try{
			// 设置 x轴 数据
			String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
			float[] zdnVals = { 0f }; // 避免数据库没数据时，图表出错
			
			xVals = new String[time_list.length];
			zdnVals = new float[zdn_list.length];
			
			for (int i = 0; i < time_list.length; i++) {
				// 时间，作为 x轴
				String time = time_list[i];
				String t = time; // 取出格式为 xx:xx 的时间段
				xVals[i] = t;
				// zdn，作为y轴
				zdnVals[i] = Float.valueOf(zdn_list[i]);
			}

			// create a chartdata object that contains only the x-axis labels (no
			// entries or datasets)
			LineData data1 = new LineData(xVals);

			lineChar.setData(data1);

			lineChar.invalidate();
			LineData data = lineChar.getData();

			if (data != null) {

				int count = (data.getDataSetCount() + 1);

				// create 10 y-vals
				ArrayList<Entry> yVals = new ArrayList<Entry>();

				// 设置 y轴 数据
				for (int i = 0; i < data.getXValCount(); i++)
					yVals.add(new Entry(zdnVals[i], i));

				LineDataSet set = new LineDataSet(yVals, null);
				set.setLineWidth(1f);
				set.setCircleSize(1f);
				set.setDrawValues(false);//设置曲线上不显示数值
				// 颜色
				int color = mColors[count % mColors.length + 2];

				set.setColor(color);
				set.setCircleColor(color);
				set.setHighLightColor(color);
				
				// 填充曲线下方的区域，红色，半透明。
				set.setDrawFilled(true);
			     // 数值越小 透明度越大
				set.setFillAlpha(50);
				set.setFillColor(color);
				
				Legend mLegend = lineChar.getLegend();
				mLegend.setEnabled(false);//表示线条类别的小方块不显示
				if (mLegend != null) {
			        mLegend.setFormSize(6f);// 字体    
			        mLegend.setTextColor(color);// 颜色    
				}

				data.addDataSet(set);
				lineChar.notifyDataSetChanged();
				lineChar.invalidate();  
				}		
			}catch(Exception e){
				e.printStackTrace();
			 }
		
	}
	
	
	private void get_AirQuality(){
		new Thread(){
			public void run(){
				MyOrder odToSend;
				switch (str_type) {
				case Type_Entity.Air_Quality_Qetector://空气质量检测仪
					odToSend = OrderList.getSendByDeviceType(str_type,
							str_mac, OrderList.Air_Quality_Qetector_Read,"");	
					break;

				default:
					odToSend = null;
					break;
				}
				//发送mqtt推送消息;
				String send_msg = Utils.sendOrder(odToSend);
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler,
							str_type+"/"+str_mac,send_msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message message = new Message();
					message.what = 2;
					message.obj = "exception"; 
					handler.sendMessage(message);
				}
			}
			
		}.start();
		
	}
	
	private void init_popupWindow(){
		View view_ = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_airqualityqetector, null);
		popupWindow = new PopupHelper().getWindow_ALLWRAP(view_,getActivity());
		view_.findViewById(R.id.menu_btn_1).setOnClickListener(this);//PM1.0
		view_.findViewById(R.id.menu_btn_2).setOnClickListener(this);//PM2.5
		view_.findViewById(R.id.menu_btn_3).setOnClickListener(this);//PM10
		view_.findViewById(R.id.menu_btn_4).setOnClickListener(this);//温度
		view_.findViewById(R.id.menu_btn_5).setOnClickListener(this);//湿度
		view_.findViewById(R.id.menu_btn_6).setOnClickListener(this);//甲醛
		view_.findViewById(R.id.menu_btn_7).setOnClickListener(this);//CO2
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.text_action:
			popupWindow.showAsDropDown(v,-10, 10);
			break;
		case R.id.menu_btn_1:
			dataSelect=2;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("PM1.0");
			break;
		case R.id.menu_btn_2:
			dataSelect=3;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("PM2.5");
			break;
		case R.id.menu_btn_3:
			dataSelect=4;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("PM10");
			break;
		case R.id.menu_btn_4:
			dataSelect=5;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("温度");
			break;
		case R.id.menu_btn_5:
			dataSelect=6;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("湿度");
			break;
		case R.id.menu_btn_6:
			dataSelect=7;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("甲醛");
			break;
		case R.id.menu_btn_7:
			dataSelect=8;
			popupWindow.dismiss();
			showChar(result,dataSelect);
			text_action.setText("CO2");
			break;
		}
		
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), e.getVal()+"", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void setDataChange(String receive) {
		// TODO Auto-generated method stub
		System.out.println("空气质量检测仪30个点: "+receive);
		Message message = new Message();
		message.what=1;
		message.obj = receive;
		handler.sendMessage(message);
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
				super.handleMessage(msg);		
				switch(msg.what){
				case 1:
					result = (String) msg.obj;
					showChar(result,3);
					break;
				}
		}
	};
	
	private void showChar(String result,int dataSelect){
		String[] dataList = result.split("\n");
		String[] zdn_list = new String[dataList.length]; 
		String[] time_list = new String[dataList.length]; 

		for(int i = 0 ; i < dataList.length ; i++){
			zdn_list[i] = dataList[i].split(" ")[dataSelect];
			time_list[i] = (dataList[i].split(" ")[0]+" "+dataList[i].split(" ")[1]).substring(5,16);
		}
		init_totalChartData(zdn_list,time_list);
		lineChar.invalidate();
	}
	

	/**
	 *  1100    PM1.0 xxxxug/m3
		1500	PM2.5 xxxxug/m3
		1700	PM10  xxxxug/m3
		3000	温度	℃
		45		湿度  xx %
		2700	甲醛 xxxx ug/m3
		4012	co2 xxxx ug/m3

		pm1.0/pm2.5/pm10/温度/湿度/甲醛/co2
	 * 2017-07-28 04:58:49 0006 0009 0010 31 61 0043 0400
	 * 2017-07-28 05:09:06 0006 0009 0010 31 61 0043 0410*/
	
}
