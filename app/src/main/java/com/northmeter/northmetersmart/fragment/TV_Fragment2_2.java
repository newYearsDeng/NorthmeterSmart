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
import com.northmeter.northmetersmart.activity.TVAty;
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.device.MyDiagramData;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;

/**插座实时功率曲线，30个点*/
public class TV_Fragment2_2 extends Fragment implements OnClickListener,
		OnChartValueSelectedListener , TVAty.DataChange {
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
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tv_f2_2, container, false);
		try{
			// 获取该页面mac值
			Intent intent = getActivity().getIntent();
			str_mac = intent.getStringExtra("mac");
			
			((TVAty) getActivity()).setData(this);
			
			/*注册广播*/
	        RegisterBroad();
			//获取30个点的实时数据
			//get_Form_Data();
			
			mChart = (LineChart) view.findViewById(R.id.chart1);
		
	        /*界面初始化*/
			init_view();
			
			//handler消息处理
			handler = new Handler(){
				public void handleMessage(Message msg){
					try{
						super.handleMessage(msg);
						Bundle bundle = msg.getData();
						String[] msg_list = bundle.getStringArray("read30point");
						if(msg_list[0].equals("success")){
							init_view();
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
	
	private void init_view(){
		try{
			// 图表		
			mChart.setOnChartValueSelectedListener(this);
			mChart.setDrawGridBackground(false);// 是否显示表格颜色    
			mChart.setDescription("");
			//mChart.setDrawYValues(true);
			mChart.setDragEnabled(true);//设置是否可以拖拽；
			mChart.setScaleEnabled(true);
			mChart.animateX(1000); // 立即执行的动画,x轴
			mChart.setDescriptionColor(mColors[3]);//曲线上标签字体颜色
			mChart.setGridBackgroundColor(mColors[3] & 0x70FFFFFF);
			mChart.setAlpha(1.0f);
			//mChart.setDrawYValues(false) ;//设置是否显示y轴的值的数据

			YAxis y = mChart.getAxisLeft(); // y轴的标示
		    y.setTextColor(mColors[3]);
		   // y.setLabelCount(5); // y轴上的标签的显示的个数
		    
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

				LineDataSet set = new LineDataSet(yVals, " 功率(w) ");
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
			
			
			
			
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}
	
	

	@Override
	public void onClick(View v) {
	
	}


	@Override
	public void onNothingSelected() {

	}
	
	/**读取30个点的实时用电功率与温度*/
	public void get_Form_Data(){
			new Thread(){
				public void run(){
					try {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						PublishMqttMessageAndReceive.getInstance().PublishMessage(getActivity(),
								"0a0001a820/"+str_mac,"realtimedata/"+"0a0001a820/"+str_mac+"/read30point");
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a820/"+str_mac,"realtimedata/"+"0a0001a820/"+str_mac+"/read30point");
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
				intentfilter.addAction("Intent.SELETC");
				getActivity().registerReceiver(receiver, intentfilter);
			}
		}.start();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    class ReceiveTool extends BroadcastReceiver{//selected/(注：前面这些数据不传递过来)     0a0001aa7k/002014110119/read30h/data

		@Override
		public void onReceive(Context context, final Intent intent) {
			// TODO Auto-generated method stub
			try{
				if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
					switch(intent.getStringExtra("msg").split("/")[2]){
					case "UPDATA":
						init_view();
						break;
					case "read30point":
						System.out.println("插座30小时曲线:"+intent.getAction());
						final String receive = intent.getStringExtra("msg").split("/")[3];
						new Thread(){
							public void run(){
								try{
									if(!receive.equals("null")){
										String [] rec_list = receive.split("\n");
										DBDiagramData db_diagd = new DBDiagramData(getActivity());
										db_diagd.Delete(str_mac);
										String temp = "0";
										for(int i=0; i<rec_list.length; i++ ){
											System.out.println(" msg_list[i*3+0]:"+ rec_list[i].split(" ")[0]);//年月
											System.out.println(" msg_list[i*3+1]:"+ rec_list[i].split(" ")[1]);//时间
											System.out.println(" msg_list[i*3+2]:"+ rec_list[i].split(" ")[2]);//功率

											MyDiagramData mydiagramdata = new MyDiagramData(str_mac, rec_list[i].split(" ")[0]+" "+rec_list[i].split(" ")[1], "0",rec_list[i].split(" ")[2],"0");
											db_diagd.Insert(mydiagramdata);
											
											if(i==rec_list.length-1){
												Message msg = new Message();
												Bundle bundle = new Bundle();
												bundle.putStringArray("read30point", new String[]{"success"});
												msg.setData(bundle);
												TV_Fragment2_2.this.handler.sendMessage(msg);
											}
										}
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

	 /**
     * 实现接口之后必须实现接口里的抽象方法
     */
	@Override
	public void setDataChange(String receive) {//selected/0a0001aa7k/150721023750/read30point/data
		// TODO Auto-generated method stub
		final String receive_msg = receive;
		new Thread(){
			public void run(){
				try{
					if(!receive_msg.equals("null")){
						String [] rec_list = receive_msg.split("\n");
						DBDiagramData db_diagd = new DBDiagramData(getActivity());
						db_diagd.Delete(str_mac);
						String temp = "0";
						for(int i=0; i<rec_list.length; i++ ){
							System.out.println(" msg_list[i*3+0]:"+ rec_list[i].split(" ")[0]);//年月
							System.out.println(" msg_list[i*3+1]:"+ rec_list[i].split(" ")[1]);//时间
							System.out.println(" msg_list[i*3+2]:"+ rec_list[i].split(" ")[2]);//功率

							MyDiagramData mydiagramdata = new MyDiagramData(str_mac, rec_list[i].split(" ")[0]+" "+rec_list[i].split(" ")[1], "0",rec_list[i].split(" ")[2],"0");
							db_diagd.Insert(mydiagramdata);
							
							if(i==rec_list.length-1){
								Message msg = new Message();
								Bundle bundle = new Bundle();
								bundle.putStringArray("read30point", new String[]{"success"});
								msg.setData(bundle);
								TV_Fragment2_2.this.handler.sendMessage(msg);
							}
						}
					}

					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), e.getVal()+" w", Toast.LENGTH_SHORT).show();
	}

}
