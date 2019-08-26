package com.northmeter.northmetersmart.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MessageAty_Center;
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.device.MyDiagramData;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.order.Type_Entity;
import com.northmeter.northmetersmart.view.DrawGeometryView_1;

/**中央空调的用电时长方波图 和实时温度和功率曲线*/
public class Kt_Frag_Chart_3 extends Fragment implements OnChartValueSelectedListener,MessageAty_Center.DataChange {
	private View view;
	/**温度曲线*/
	private LineChart mChart_tem;
	/**中央空调开关机用电方波图*/
	private LinearLayout time_chart_1;
	private int[] mColors = ColorTemplate.VORDIPLOM_COLORS;

	private String str_mac,str_name,str_type;
	private String URL_PATH;
	private ReceiveTool receiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try{
			view = inflater.inflate(R.layout.kt_frag_chart_3, container, false);
			// 获取该页面mac值
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			str_name = intent.getStringExtra("name");
			str_type = intent.getStringExtra("type");
			
			((MessageAty_Center) getActivity()).setData(this);
			
			time_chart_1 = (LinearLayout) view.findViewById(R.id.time_chart_1);
						
			mChart_tem = (LineChart) view.findViewById(R.id.energy_chart_tem_2);
			/*注册广播*/
	        RegisterBroad();

	        /*界面初始化*/
			init_view();
			init_view_temp();
			
			//获取30个点的实时数据
			//get_Form_Data();
						
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	/**功率曲线*/
	private void init_view(){
		try{
			// 图表		    
			// 数据
			time_chart_1.removeAllViews();
			DBDiagramData db_diagd = new DBDiagramData(getActivity());
			List<MyDiagramData> diagramData = db_diagd
					.GetMyDiagramDataByMacTop(str_mac);

			// 设置 x轴 数据
			String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
			float[] zdnVals = { 0f }; // 避免数据库没数据时，图表出错
			if (!diagramData.isEmpty()) {
				int len = diagramData.size();
				xVals = new String[len];
				zdnVals = new float[len];
				for (int i = 0; i < len; i++) {
					MyDiagramData dd = diagramData.get(i);
					// 时间，作为 x轴
					String time = dd.getTimeNow();
					String t = time.substring(5, 16); // 取出格式为 xx:xx 的时间段
					xVals[i] = t;
					// zdn，作为y轴
					zdnVals[i] = Float.valueOf(dd.getGl());
				}
			}
			DrawGeometryView_1 draw = new DrawGeometryView_1(getActivity(),zdnVals,xVals);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(zdnVals.length*100, ViewGroup.LayoutParams.MATCH_PARENT);
			draw.setLayoutParams(params);
			time_chart_1.addView(draw);
			
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}
	/**温度曲线*/
	private void init_view_temp(){
		try{
		mChart_tem.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
		mChart_tem.setDrawGridBackground(false);
		mChart_tem.setDescription("");
		//mChart_tem.setDrawYValues(true);
		mChart_tem.setDragEnabled(true);//设置是否可以拖拽；
		mChart_tem.setScaleEnabled(true);
		mChart_tem.animateX(1000); // 立即执行的动画,x轴
		mChart_tem.setDescriptionColor(mColors[3]);//曲线上标签字体颜色
		mChart_tem.setGridBackgroundColor(mColors[3] & 0x70FFFFFF);
		mChart_tem.setAlpha(1f);
		//mChart_tem.setDrawYValues(false) ;//设置是否显示y轴的值的数据
		
//		mChart_tem.setYRange(0, 40, false);//设置y轴数据的最大值和最小值，设置true,调用此方法后图标将会重绘；
//		mChart_tem.resetYRange(true);
		
		YAxis y = mChart_tem.getAxisLeft(); // y轴的标示
	    y.setTextColor(mColors[3]);
	    
	    YAxis y1 = mChart_tem.getAxisRight(); // 右侧y轴的标示
	    y1.setTextColor(mColors[3]);
	    
	   	XAxis x = mChart_tem.getXAxis(); // x轴显示的标签
		x.setTextColor(mColors[3]);
		    
	
		//mChart_tem.setYRange(0, 40, false);//设置y轴数据的最大值和最小值，设置true,调用此方法后图标将会重绘；
		//mChart_tem.resetYRange(true);
		
		// 数据
		DBDiagramData db_diagd = new DBDiagramData(getActivity());
		List<MyDiagramData> diagramData = db_diagd
				.GetMyDiagramDataByMacTop(str_mac);

		// 设置 x轴 数据
		String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
		float[] tempVals = { 0f }; // 避免数据库没数据时，图表出错
		if (!diagramData.isEmpty()) {
			int len = diagramData.size();
			xVals = new String[len];
			tempVals = new float[len];
			for (int i = 0; i < len; i++) {
				MyDiagramData dd = diagramData.get(i);
				// 时间，作为 x轴
				String time = dd.getTimeNow();
				String t = time.substring(5, 16); // 取出格式为 xx:xx 的时间段
				xVals[i] = t;
				// zdn，作为y轴
				tempVals[i] = Float.valueOf(dd.getTemp());
			}
		}

		// create a chartdata object that contains only the x-axis labels (no
		// entries or datasets)
		LineData data1 = new LineData(xVals);

		mChart_tem.setData(data1);

		mChart_tem.invalidate();
		LineData data = mChart_tem.getData();

		if (data != null) {

			int count = (data.getDataSetCount() + 1);

			// create 10 y-vals
			ArrayList<Entry> yVals = new ArrayList<Entry>();

			// 设置 y轴 数据
			for (int i = 0; i < data.getXValCount(); i++){
				yVals.add(new Entry(tempVals[i], i));
			}
			LineDataSet set = new LineDataSet(yVals, " 温度(℃) ");
			set.setLineWidth(2.5f);
			set.setCircleSize(3.0f);
			set.setDrawValues(false);//设置曲线上不显示数值
			
			// 颜色
			int color = mColors[3];

			set.setColor(color);
			set.setCircleColor(color);
			set.setHighLightColor(color);
			
		    Legend mLegend = mChart_tem.getLegend();
			    if (mLegend != null) {
		           mLegend.setFormSize(6f);// 字体    
		           mLegend.setTextColor(color);// 颜色    
			}

			data.addDataSet(set);
			mChart_tem.notifyDataSetChanged();
			mChart_tem.invalidate();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	

	@Override
	public void onNothingSelected() {

	}
	/**读取30个点的实时用电功率与温度*/
	private void get_Form_Data(){
			new Thread(){
				public void run(){
					try {
						String type;
						if(str_type.equals(Type_Entity.Central_air_conditioning)){
							type = "0a0001a4r5";
						}else{
							type = "0a0001aa7k";
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("*******--------------------------------"+type+"/"+str_mac+"/read30point");
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
								type+"/"+str_mac,"realtimedata/"+type+"/"+str_mac+"/read30point");
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(type+"/"+str_mac,"realtimedata/"+type+"/"+str_mac+"/read30point");
					} catch (MqttException e) {//realtimedata/0a0001aa7k/002014110119/read30point
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}.start();
	
	}
	
    /**注册接收广播*/
	public void RegisterBroad(){
		try{
		//界面更新广播接收;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter();
				intentfilter.addAction("Intent.UPDATA");
				intentfilter.addAction("Intent.MessageAty_Read30point");
				getActivity().registerReceiver(receiver, intentfilter);
			}
		}.start();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    											//表号+"/UPDATA"
    class ReceiveTool extends BroadcastReceiver{//selected/(注：前面这些数据不传递过来)     0a0001aa7k/002014110119/read30h/data

		@Override
		public void onReceive(Context context, final Intent intent) {
			// TODO Auto-generated method stub
			try{
				String receive_msg = intent.getStringExtra("msg");
				if(receive_msg.split("/").length==4){
					if(receive_msg.split("/")[1].equals(str_mac)){
						switch(receive_msg.split("/")[2]){
						case "read30point":
							final String receive = receive_msg.split("/")[3];
							new Thread(){
								public void run(){
									try{
										if(!receive.equals("null")){
											String [] rec_list = receive.split("\n");
											DBDiagramData db_diagd = new DBDiagramData(getActivity());
											db_diagd.Delete(str_mac);
											String temp = "0";
											for(int i=0; i<rec_list.length; i++ ){
												if(rec_list[i].split(" ")[3].equals("FFF.F")|rec_list[i].split(" ")[3].equals("fff.f")|rec_list[i].split(" ")[3].equals("-ff.f")|rec_list[i].split(" ")[3].equals("-FF.F")){
													temp = "0";
												}else{
													temp = rec_list[i].split(" ")[3];
												}
												MyDiagramData mydiagramdata = new MyDiagramData(str_mac, rec_list[i].split(" ")[0]+" "+rec_list[i].split(" ")[1], 
														"0",rec_list[i].split(" ")[2],temp);
												db_diagd.Insert(mydiagramdata);
												
											}
											Message msg = new Message();
											Bundle bundle = new Bundle();
											bundle.putStringArray("read30point", rec_list);
											msg.setData(bundle);
											Kt_Frag_Chart_3.this.handler.sendMessage(msg);
										}
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}.start();
							
							break;
							
							default:
								break;
						}
						
					}
					
				}else if(receive_msg.split("/").length==2){
					if(receive_msg.split("/")[0].equals(str_mac)){
						switch(intent.getStringExtra("msg").split("/")[1]){
						case "UPDATA":
							init_view();
							init_view_temp();
							break;
							}
				}
			}

			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
    
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try{
			super.onDestroy();
			getActivity().unregisterReceiver(receiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void setDataChange(String msg) {
		// TODO Auto-generated method stub
		System.out.println("++++++++++++++++++++dd++中央空调66666666666666");
		final String receive = msg.split("/")[3];
		if(msg.split("/")[2].equals("read30point")){
			new Thread(){
				public void run(){
					try{
						if(!receive.equals("null")){
							String [] rec_list = receive.split("\n");
							DBDiagramData db_diagd = new DBDiagramData(getActivity());
							db_diagd.Delete(str_mac);
							String temp = "0";
							for(int i=0; i<rec_list.length; i++ ){
								if(rec_list[i].split(" ")[3].equals("FFF.F")|rec_list[i].split(" ")[3].equals("fff.f")|rec_list[i].split(" ")[3].equals("-ff.f")|rec_list[i].split(" ")[3].equals("-FF.F")){
									temp = "0";
								}else{
									temp = rec_list[i].split(" ")[3];
								}
								MyDiagramData mydiagramdata = new MyDiagramData(str_mac, rec_list[i].split(" ")[0]+" "+rec_list[i].split(" ")[1], 
										"0",rec_list[i].split(" ")[2],temp);
								db_diagd.Insert(mydiagramdata);
								
							}
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putStringArray("read30point", rec_list);
							msg.setData(bundle);
							Kt_Frag_Chart_3.this.handler.sendMessage(msg);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	//handler消息处理
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			try{
				super.handleMessage(msg);
				Bundle bundle = msg.getData();
				String[] msg_list = bundle.getStringArray("read30point");
				if(msg_list[0].equals("success")){
					init_view();
					init_view_temp();
				}else{
					String[] zdn_list = new String[msg_list.length];
					String[] temp_list = new String[msg_list.length];
					String[] time_list = new String[msg_list.length];
					
					for(int i = 0;i < msg_list.length; i++){
						zdn_list[i] = msg_list[i].split(" ")[2];
						if(msg_list[i].split(" ")[3].equals("FFF.F")||msg_list[i].split(" ")[3].equals("fff.f")||msg_list[i].split(" ")[3].equals("-ff.f")||msg_list[i].split(" ")[3].equals("-FF.F")){
							temp_list[i] = "0";
						}else{
							temp_list[i] = msg_list[i].split(" ")[3];
						}
						time_list[i] = msg_list[i].split(" ")[0]+" "+msg_list[i].split(" ")[1];
					}
					
					init_viewByMqtt(zdn_list, time_list);
					init_view_tempByMqtt(temp_list, time_list);
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
	
	/**功率曲线*/
	private void init_viewByMqtt(String[] zdn_list,String[] time_list){
		try{
			// 图表		    
			// 数据
			time_chart_1.removeAllViews();
			// 设置 x轴 数据
			String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
			float[] zdnVals = { 0f }; // 避免数据库没数据时，图表出错

			int len = time_list.length;
			xVals = new String[len];
			zdnVals = new float[len];
			for (int i = 0; i < time_list.length; i++) {
				//MyDiagramData dd = diagramData.get(i);
				// 时间，作为 x轴
				String time = time_list[i];
				String t = time.substring(5, 16); // 取出格式为 xx:xx 的时间段
				xVals[i] = t;
				// zdn，作为y轴
				zdnVals[i] = Float.valueOf(zdn_list[i]);
			}
			
			DrawGeometryView_1 draw = new DrawGeometryView_1(getActivity(),zdnVals,xVals);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(zdnVals.length*100, ViewGroup.LayoutParams.MATCH_PARENT);
			draw.setLayoutParams(params);
			time_chart_1.addView(draw);
			
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}
	/**温度曲线*/
	private void init_view_tempByMqtt(String[] temp_list,String[] time_list){
		try{
		mChart_tem.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
		mChart_tem.setDrawGridBackground(false);
		mChart_tem.setDescription("");
		//mChart_tem.setDrawYValues(true);
		mChart_tem.setDragEnabled(true);//设置是否可以拖拽；
		mChart_tem.setScaleEnabled(true);
		mChart_tem.animateX(1000); // 立即执行的动画,x轴
		mChart_tem.setDescriptionColor(mColors[3]);//曲线上标签字体颜色
		mChart_tem.setGridBackgroundColor(mColors[3] & 0x70FFFFFF);
		mChart_tem.setAlpha(1f);
	//	mChart_tem.setDrawYValues(false) ;//设置是否显示y轴的值的数据
		
//		mChart_tem.setYRange(0, 40, false);//设置y轴数据的最大值和最小值，设置true,调用此方法后图标将会重绘；
//		mChart_tem.resetYRange(true);
		
		YAxis y = mChart_tem.getAxisLeft(); // y轴的标示
	    y.setTextColor(mColors[3]);
	    
	    YAxis y1 = mChart_tem.getAxisRight(); // 右侧y轴的标示
	    y1.setTextColor(mColors[3]);
	    
	   	XAxis x = mChart_tem.getXAxis(); // x轴显示的标签
		x.setTextColor(mColors[3]);
		    
		//mChart_tem.setYRange(0, 40, false);//设置y轴数据的最大值和最小值，设置true,调用此方法后图标将会重绘；
		//mChart_tem.resetYRange(true);

		// 设置 x轴 数据
		String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
		float[] tempVals = { 0f }; // 避免数据库没数据时，图表出错
		xVals = new String[time_list.length];
		tempVals = new float[time_list.length];
		for (int i = 0; i < time_list.length; i++) {
			//MyDiagramData dd = diagramData.get(i);
			// 时间，作为 x轴
			String time = time_list[i];
			String t = time.substring(5, 16); // 取出格式为 xx:xx 的时间段
			xVals[i] = t;
			// zdn，作为y轴
			tempVals[i] = Float.valueOf(temp_list[i]);
		}
		

		// create a chartdata object that contains only the x-axis labels (no
		// entries or datasets)
		LineData data1 = new LineData(xVals);

		mChart_tem.setData(data1);

		mChart_tem.invalidate();
		LineData data = mChart_tem.getData();

		if (data != null) {

			int count = (data.getDataSetCount() + 1);

			// create 10 y-vals
			ArrayList<Entry> yVals = new ArrayList<Entry>();

			// 设置 y轴 数据
			for (int i = 0; i < data.getXValCount(); i++){
				yVals.add(new Entry(tempVals[i], i));
			}
			LineDataSet set = new LineDataSet(yVals, " 温度(℃) ");
			set.setLineWidth(2.5f);
			set.setCircleSize(3.0f);
			set.setDrawValues(false);//设置曲线上不显示数值
			
			// 颜色
			int color = mColors[3];

			set.setColor(color);
			set.setCircleColor(color);
			set.setHighLightColor(color);
			
		    Legend mLegend = mChart_tem.getLegend();
			    if (mLegend != null) {
		           mLegend.setFormSize(6f);// 字体    
		           mLegend.setTextColor(color);// 颜色    
			}

			data.addDataSet(set);
			mChart_tem.notifyDataSetChanged();
			mChart_tem.invalidate();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), e.getVal()+"", Toast.LENGTH_SHORT).show();
	}

}
