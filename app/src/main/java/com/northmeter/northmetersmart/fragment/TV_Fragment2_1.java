package com.northmeter.northmetersmart.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.device.MyDiagramData;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;

/**电能曲线*/
public class TV_Fragment2_1 extends Fragment implements OnClickListener,
		OnChartValueSelectedListener {
	protected String[] mMonths = new String[] { "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };

	protected String[] mParties = new String[] { "Party A", "Party B",
			"Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
			"Party I", "Party J", "Party K", "Party L", "Party M", "Party N",
			"Party O", "Party P", "Party Q", "Party R", "Party S", "Party T",
			"Party U", "Party V", "Party W", "Party X", "Party Y", "Party Z" };

	private View view;
	private LineChart mChart;
	int[] mColors = ColorTemplate.VORDIPLOM_COLORS;

	private String str_mac;
	
	private ReceiveTool receiver;
	private Handler handler;
	private SharedPreferences spf;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_f2_1, container, false);
		try{
			mChart = (LineChart) view.findViewById(R.id.chart1);
			// 获取该页面mac值
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			
			RegisterBroad();
			//get_Form_Data();
			
			spf = getActivity().getSharedPreferences("form_48h_socket", getActivity().MODE_PRIVATE);
			String rec_str = spf.getString(str_mac, "0000-00-00 00:00:00 0000-00-00 00:00:00 0");
			String [] receive=rec_str.split("\n");
			
			
			
			String [] time_list = new String[receive.length];
			String [] dn_list = new String[receive.length];
			for(int i=0;i<receive.length;i++){//获取到48小时用电曲线，用空格来获取数组；
				time_list[i] = receive[i].split(" ")[2]+" "+receive[i].split(" ")[3];
				dn_list[i] = receive[i].split(" ")[4];
			}
			init_view( dn_list, time_list);
			
			
			handler = new Handler(){
				public void handleMessage(Message msg){
					try{
						super.handleMessage(msg);
						Bundle bundle=msg.getData();
						String [] data = bundle.getStringArray("read48h");
						if(data[0].equals("success")){
//							spf = getActivity().getSharedPreferences("form_48h_socket", getActivity().MODE_PRIVATE);
//							String rec_str = spf.getString(str_mac, "0000-00-00 00:00:00 0000-00-00 00:00:00 0");
							String rec_str = data[1];
							String [] receive=rec_str.split("\n");
							
							String [] time_list = new String[receive.length];
							String [] dn_list = new String[receive.length];
							for(int i=0;i<receive.length;i++){//获取到48小时用电曲线，用空格来获取数组；
								time_list[i] = receive[i].split(" ")[2]+" "+receive[i].split(" ")[3];
								dn_list[i] = receive[i].split(" ")[4];
//								System.out.println(" msg_list[年月]:"+ receive[i].split(" ")[0]);  //年月
//								System.out.println(" msg_list[时间]:"+ receive[i].split(" ")[1]);//时间
//								System.out.println(" msg_list[年月]:"+ receive[i].split(" ")[2]);//年月
//								System.out.println(" msg_list[时间]:"+ receive[i].split(" ")[3]);//时间
//								System.out.println(" msg_list[电能]:"+ receive[i].split(" ")[4]);//电能
							}
							init_view( dn_list, time_list);
							}
					 	}catch(Exception e){
					 		e.printStackTrace();
					 	}
					}
						
			};
			
			}catch(Exception e){
				e.printStackTrace();
		}
		return view;
	}
	
	
	/**48小时用电曲线*/
	private void init_view(String[] dn_list,String[] time_list){
		
		mChart.setOnChartValueSelectedListener(this);
		mChart.setDrawGridBackground(false);// 是否显示表格颜色    
		mChart.setDescription("");
		//mChart.setDrawYValues(true);
		mChart.setDragEnabled(true);//设置是否可以拖拽；
		mChart.setScaleEnabled(true);
		mChart.animateX(1000); // 立即执行的动画,x轴
		mChart.setDescriptionColor(mColors[3]);//曲线上标签字体颜色
		mChart.setGridBackgroundColor(mColors[3] & 0x70FFFFFF);
		mChart.setAlpha(1f);
		//mChart.setDrawYValues(false) ;//设置是否显示y轴的值的数据

		YAxis y = mChart.getAxisLeft(); // y轴的标示
	    y.setTextColor(mColors[3]);
	    //y.setLabelCount(5); // y轴上的标签的显示的个数
	    
	    YAxis y1 = mChart.getAxisRight(); // 右侧y轴的标示
	    y1.setTextColor(mColors[3]);


	    XAxis x = mChart.getXAxis(); // x轴显示的标签
		x.setTextColor(mColors[3]);
		    
		// 数据
		DBDiagramData db_diagd = new DBDiagramData(getActivity());
		List<MyDiagramData> diagramData = db_diagd
				.GetMyDiagramDataByMacTop(str_mac);

		// 设置 x轴 数据
		String[] xVals = { "0" }; // 避免数据库没数据时，图表出错
		float[] zdnVals = { 0f }; // 避免数据库没数据时，图表出错
//		if (!diagramData.isEmpty()) {
//			int len = diagramData.size();
//			xVals = new String[len];
//			zdnVals = new float[len];
//			for (int i = 0; i < len; i++) {
//				MyDiagramData dd = diagramData.get(i);
//				// 时间，作为 x轴
//				Str ing time = dd.getTimeNow();
//				String t = time.substring(5, 16); // 取出格式为 xx:xx 的时间段
//				xVals[i] = t;
//				// zdn，作为y轴
//				zdnVals[i] = Float.valueOf(receive[i]);
//			}
//		}

			xVals = new String[time_list.length];
			zdnVals = new float[dn_list.length];
			for (int i = 0; i < time_list.length; i++) {
				//MyDiagramData dd = diagramData.get(i);
				// 时间，作为 x轴
				String time = time_list[i];
				String t = time.substring(5, 16); // 取出格式为 xx:xx 的时间段
				xVals[i] = t;
				// zdn，作为y轴
				zdnVals[i] = Float.valueOf(dn_list[i]);
			}

		
		// create a chartdata object that contains only the x-axis labels (no
		// entries or datasets)
		LineData data1 = new LineData(xVals);
		
		mChart.setData(data1);

		mChart.invalidate();
		LineData data = mChart.getData();

		if (data != null) {

			int count = (data.getDataSetCount() + 1);

			// create 10 y-vals
			ArrayList<Entry> yVals = new ArrayList<Entry>();

			// 设置 y轴 数据
			for (int i = 0; i < data.getXValCount(); i++)
				yVals.add(new Entry(zdnVals[i], i));

			LineDataSet set = new LineDataSet(yVals, "小时用电(kWh) ");
			set.setLineWidth(2.5f);
			set.setCircleSize(3.0f);
			set.setDrawValues(false);//设置曲线上不显示数值
			// 颜色
			int color = mColors[count % mColors.length + 1];
			set.setColor(color);
			set.setCircleColor(color);
			set.setHighLightColor(color);
			
			Legend mLegend = mChart.getLegend();
				if (mLegend != null) {
					mLegend.setFormSize(6f);// 字体    
			        mLegend.setTextColor(color);// 颜色    
				}
			

			data.addDataSet(set);
			mChart.notifyDataSetChanged();
			mChart.invalidate();
			}
	}
	

	@Override
	public void onClick(View v) {
	}


	@Override
	public void onNothingSelected() {

	}
	
	/**读取48小时用电数据*/
	public void get_Form_Data(){
		new Thread(){
			public void run(){
				try {
					System.out.println("读取48小时用电数据");
					Thread.sleep(1500);
					PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
							"0a0001a820/"+str_mac,"realtimedata/"+"0a0001a820/"+str_mac+"/read48h");	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a820/"+str_mac,"realtimedata/"+"0a0001a820/"+str_mac+"/read48h");
				} catch (MqttException e) {//realtimedata/0a0001aa7k/002014110119/read48h
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();	
	}
	
	
	/**注册接收广播*/
	public void RegisterBroad(){
		//界面更新广播接收;
		new Thread(){
			public void run(){
				receiver = new ReceiveTool();
				IntentFilter intentfilter = new IntentFilter("Intent.SELETC");
				getActivity().registerReceiver(receiver, intentfilter);
				
			}
		}.start();	
	}
	
	class ReceiveTool extends BroadcastReceiver{//selected/(注：前面这些数据不传递过来)  0a0001aa7k/002014110119/read48h/data

		@Override
		public void onReceive(Context context, Intent intent) {//selected/0a0001aa7k/002014110119/read30h/data
			// TODO Auto-generated method stub
			try{
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "read48h":
						System.out.println("************************收到广播，这是插座界面48小时曲线************："+intent.getAction());
						final String rec_str = intent.getStringExtra("msg").split("/")[3];
						new Thread(){
							public void run(){
								try{
									//把48小时用电数据存储到sharedpreference
									if(!rec_str.equals("null")){
//										spf = getActivity().getSharedPreferences("form_48h_socket", getActivity().MODE_PRIVATE);
//										Editor editor = spf.edit();
//										editor.putString(str_mac, rec_str);
//										editor.commit();
										
										Message msg=new Message();
										Bundle bundle=new Bundle();
										bundle.putStringArray("read48h", new String[]{"success",rec_str});
										msg.setData(bundle);	
										TV_Fragment2_1.this.handler.sendMessage(msg);
									}
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}.start();
						
						break;
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
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), e.getVal()+" kWh", Toast.LENGTH_SHORT).show();
	}


}
