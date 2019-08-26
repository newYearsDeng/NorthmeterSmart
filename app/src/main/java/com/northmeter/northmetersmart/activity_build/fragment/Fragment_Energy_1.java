package com.northmeter.northmetersmart.activity_build.fragment;

import java.util.ArrayList;
import java.util.List;


import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.helper.PromptHelper;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/*
 *  realtimedata/建筑编号/get_building_used_ele_of_month    12个月数据
	realtimedata/建筑编号/get_building_subentry_used_ele_of_month/时间              具体月份数据

	返回：
	selected/建筑编号/get_building_used_ele_of_month/数据
	selected/建筑编号/get_building_subentry_used_ele_of_month/数据
*/

/**
 * 能源页面子页面*/
public class Fragment_Energy_1 extends Fragment implements OnChartValueSelectedListener,OnClickListener,DialogInterface.OnDismissListener{
	private View view;
	private LineChart total_chart;//最近12个月用电情况
	private PieChart now_piechart;//本月用电情况
	private TextView title_now,time_action;
	private List<String> devicesName;
	private List<Float> items;
	
	private int[] mColors = ColorTemplate.VORDIPLOM_COLORS;
	private String buildid,buildname,roleid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_energy_1, null);
		try{
			init_view();
			init_total_chart();
			String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
			String [] zdnVals = { "0" }; // 避免数据库没数据时，图表出错
			init_totalChartData(zdnVals,xVals);
			
			get_building_used_ele_of_month(buildid);

			init_pieChart();
			initChartItems();
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	
	
	
	private void init_view(){
		Intent intent = getActivity().getIntent();
		buildid = intent.getStringExtra("buildid");
		buildname = intent.getStringExtra("buildname");	
		roleid = intent.getStringExtra("roleid");
		
		total_chart = (LineChart) view.findViewById(R.id.total_chart);
		now_piechart = (PieChart) view.findViewById(R.id.now_piechart);
		
		title_now = (TextView) view.findViewById(R.id.title_now);
		time_action = (TextView) view.findViewById(R.id.time_action);//选择日期
		time_action.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
			switch(v.getId()){
			case R.id.time_action:
				 PromptHelper.createTimePicker(getActivity(), false, true, true, new DatePickerDialog.OnDateSetListener() {
			            @Override
			            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			            	int month = monthOfYear+1;
			            	String mon = month+"";
			            	if(month<10){
			            		mon = "0"+month;
			            	}
			                String startDate = year + "-" + mon;
			                time_action.setText(startDate);
			                get_building_subentry_used_ele_of_month(false,buildid,startDate);
			            }
			        }, this);
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onDismiss(DialogInterface arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**初始化图表*/
	private void init_total_chart(){
		// 图表
		total_chart.setOnChartValueSelectedListener(this);
		total_chart.setDrawGridBackground(false);
		total_chart.setDescription("");
		//mChart.setDrawYValues(true);
		total_chart.setDragEnabled(true);//设置是否可以拖拽；
		total_chart.setScaleEnabled(true);
		total_chart.animateY(1500); // 立即执行的动画,x轴
		total_chart.setDescriptionColor(mColors[3]);//曲线上标签字体颜色
		total_chart.setGridBackgroundColor(mColors[3] & 0x70FFFFFF);
		total_chart.setAlpha(255);
		total_chart.getXAxis().setDrawGridLines(false); 
		//mChart.setDrawYValues(false);//设置是否显示y轴的值的数据

		YAxis y = total_chart.getAxisLeft(); // y轴的标示
	    y.setTextColor(mColors[3]);
	    
	    YAxis y1 = total_chart.getAxisRight(); // 右侧y轴的标示
	    y1.setTextColor(mColors[3]);
	    //y1.setEnabled(false);

	   	XAxis x = total_chart.getXAxis(); // x轴显示的标签
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
				System.out.println(zdn_list.length+"**------***************---"+zdn_list[i]);
				zdnVals[i] = Float.valueOf(zdn_list[i]);
			}

			// create a chartdata object that contains only the x-axis labels (no
			// entries or datasets)
			LineData data1 = new LineData(xVals);

			total_chart.setData(data1);

			total_chart.invalidate();
			LineData data = total_chart.getData();

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
				
				Legend mLegend = total_chart.getLegend();
				mLegend.setEnabled(false);//表示线条类别的小方块不显示
				if (mLegend != null) {
			        mLegend.setFormSize(6f);// 字体    
			        mLegend.setTextColor(color);// 颜色    
				}

				data.addDataSet(set);
				total_chart.notifyDataSetChanged();
				total_chart.invalidate();  
				}		
			}catch(Exception e){
				e.printStackTrace();
			 }
		
	}
	
	/**初始化饼图*/
	private void init_pieChart(){
		// change the color of the center-hole
		now_piechart.setHoleColor(Color.rgb(235, 235, 235));

		now_piechart.setHoleRadius(60f);
		
		//now_piechart.setValueTextColor(Color.rgb(0, 0, 0));

		now_piechart.setDescription("");

		now_piechart.setDrawCenterText(true);

		now_piechart.setDrawHoleEnabled(true);

		now_piechart.setRotationAngle(0);
		
		now_piechart.animateY(1400);
		
		now_piechart.animateXY(1500, 1500);

		// enable rotation of the chart by touch
		now_piechart.setRotationEnabled(true);

		// display percentage values
		now_piechart.setUsePercentValues(true);
		// mChart.setUnit(" €");
		// mChart.setDrawUnitsInChart(true);

		// add a selection listener
		now_piechart.setOnChartValueSelectedListener(this);
		// mChart.setTouchEnabled(false);

		//now_piechart.setCenterText("我的设备\n总电能");
		now_piechart.setDrawHoleEnabled(false);
		
		now_piechart.setDrawSliceText(false);//设置不显示文字
		
		
		// 设置饼图数据
		initChartItems();
		setChartData(items.size(), 100);
		
		Legend legend = now_piechart.getLegend();
		if (legend != null) {
			legend.setPosition(LegendPosition.LEFT_OF_CHART_CENTER);
			legend.setXEntrySpace(2f);
			legend.setYEntrySpace(2f);//设置 比例块距离饼图的距离
			legend.setTextColor(Color.WHITE);
			
		}
	}
	
	/**
	 * 初始化饼图的数据源
	 */
	private void initChartItems() {

			int [] temp_items = new int[]{0};
			devicesName = new ArrayList<String>();
			items = new ArrayList<Float>();
			for (int i = 0; i < temp_items.length; i++) {
				items.add((float) temp_items[i]);
				devicesName.add("");
			}

	}

	/**
	 * 
	 * @param count
	 *            设备总数
	 * @param range
	 *            百分比总额，一般为100
	 */
	private void setChartData(int count, float range) {

		System.out.println("setData start!");

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();

		for (int i = 0; i < count; i++) {
			yVals1.add(new Entry(items.get(i), i));
		}

		ArrayList<String> xVals = new ArrayList<String>();

		for (int i = 0; i < count; i++) {
			System.out.println("xVals添加一个值：" + devicesName.get(i));
			xVals.add(devicesName.get(i));
		}

		// PieDataSet set1 = new PieDataSet(yVals1, "我的设备");
		PieDataSet pieDataSet = new PieDataSet(yVals1, "");
		pieDataSet.setSliceSpace(0f);           //设置饼状Item之间的间隙  
		pieDataSet.setSelectionShift(3f);      //设置饼状Item被选中时变化的距离  
		pieDataSet.setDrawValues(true);

		ArrayList<Integer> colors = new ArrayList<Integer>();

		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.LIBERTY_COLORS )
			colors.add(c);

		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.PASTEL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);

		colors.add(ColorTemplate.getHoloBlue());

		pieDataSet.setColors(colors);
		
		    
		PieData pieData = new PieData(xVals, pieDataSet);
		pieData.setDrawValues(true);            //设置是否显示数据实体(百分比，true:以下属性才有意义)  
	    pieData.setValueTextColor(Color.BLUE);  //设置所有DataSet内数据实体（百分比）的文本颜色  
	    pieData.setValueTextSize(10f);          //设置所有DataSet内数据实体（百分比）的文本字体大小  
	    pieData.setValueFormatter(new PercentFormatter());//设置所有DataSet内数据实体（百分比）的文本字体格式  
		now_piechart.setData(pieData);
		
//		Legend legend = now_piechart.getLegend();
//		legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
//		

		// undo all highlights
		now_piechart.highlightValues(null);

		now_piechart.invalidate();
		
	
	}
	
	

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), e.getVal()+"kWh", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub
		
	}
	
	//realtimedata/建筑编号/get_building_used_ele_of_month    12个月数据
	private void get_building_used_ele_of_month(final String buildid){
		//selected/00010002/get_building_used_ele_of_month/2017-06 1207.8 2017-05 1937.45 2017-04 1698.27 2017-03 835.39 2017-02 702.37 2017-01 460.13 2016-12 875.08 2016-11 1061.06 2016-10 260.24 2016-09 0.0 2016-08 0.0 2016-07 0.0
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler,
							buildid,"realtimedata/"+buildid+"/get_building_used_ele_of_month");	
//					PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	//realtimedata/建筑编号/get_building_subentry_used_ele_of_month/时间              具体月份数据
	private void get_building_subentry_used_ele_of_month(final boolean flag,final String buildid,final String timeNow){
		//selected/00010002/get_building_subentry_used_ele_of_month/2017-06 {"all":1399.3700000000001,"ac":1256.41,"socket":24.48,"lighting":118.47999999999999}
		new Thread(){
			public void run(){
				try {
					String time;
					if(flag){
						Time t = new Time();
						t.setToNow();
						time = String.valueOf(t.year) + "-"
								+  toDoubleDate(t.month + 1); // month是从0开始计算的
						time_action.setText(time);
					}else{
						time = timeNow;
					}
					
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler,
							buildid,"realtimedata/"+buildid+"/get_building_subentry_used_ele_of_month/"+time);	
//					PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(str_type+"/"+str_mac,"onetask/"+data);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	
	private static String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
	
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
		try{
			super.handleMessage(msg);
			if (msg.what == 1) {
				String bundle_msg = msg.getData().getString("content");
				if(bundle_msg.split("/")[1].equals(buildid)){
					switch(bundle_msg.split("/")[2]){
					case "get_building_used_ele_of_month":
						get_building_subentry_used_ele_of_month(true,buildid,"");
						
						String data = bundle_msg.split("/")[3];
						if(!data.equals("null")){
							String [] dataList = data.split("\n");
							String[] zdn_list = new String[dataList.length]; 
							String[] time_list = new String[dataList.length]; 
							for(int i = 0;i<dataList.length;i++){
								zdn_list[i] = dataList[i].split(" ")[1];
								time_list[i] = dataList[i].split(" ")[0];
							}
							init_totalChartData(zdn_list,time_list);
							total_chart.invalidate();
						}
						break;
					case "get_building_subentry_used_ele_of_month"://2017-06 {"all":1399.3700000000001,"ac":1256.41,"socket":24.48,"lighting":118.47999999999999}
						String pie_data = bundle_msg.split("/")[3];
						List list  = new ArrayList();
						if(!pie_data.equals("null")){
							int state = pie_data.indexOf("{");
							String time = pie_data.substring(0,state);
							title_now.setText(time+"用电");
							
							JSONObject jsonobject = JSONObject.parseObject(pie_data.substring(state,pie_data.length()));
							devicesName.clear();
							items.clear();
							for(Object k : jsonobject.keySet()){
				    	        Object v = jsonobject.get(k);  
				    	        if(!k.equals("all")){
				    	        	switch(k.toString()){
				    	        	case "ac":
				    	        		devicesName.add("空调用电");
				    	        		break;
				    	        	case "socket":
				    	        		devicesName.add("插座用电");
				    	        		break;
				    	        	case "lighting":
				    	        		devicesName.add("照明用电");
				    	        		break;
				    	        	}
				    	        	
					    	        items.add(Float.parseFloat(v.toString()));
				    	        } 
				    	    } 
							setChartData(items.size(), 100);
							now_piechart.animateXY(1500, 1500);
							now_piechart.invalidate();
							
						}
						break;
					}
				}
			}
		
			
			
		}catch(Exception e){
			e.printStackTrace();
			}
		}
	};

	

}
