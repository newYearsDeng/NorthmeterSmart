package com.northmeter.northmetersmart.fragment;

import java.util.ArrayList;
import java.util.List;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.I.IRequestShow;
import com.northmeter.northmetersmart.I.RequestInterface;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.androidmenutoabhost.ListViewCompat;
import com.northmeter.northmetersmart.androidmenutoabhost.SlideView;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.http.HttpRequest;
import com.northmeter.northmetersmart.model.Gateway_Model;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class TV_ZGB_1 extends Fragment implements OnItemClickListener,OnClickListener,IRequestShow {
	private View view;
	private ListViewCompat mListView;
	private ImageView image_refash;
	private ArrayAdapter<String> arr_adapter;
	private SlideAdapter listview_adp;
	private List<Gateway_Model> models;
	CustomProgressDialog progressDialog_1;
	private String str_mac,str_name,str_type,roleid,buildid;
	private TextView toatl;
	private SharedPreferences spf;
	private ReceiveTool receiver;
	private RequestInterface requestInterface;
	private String URL;

    private SlideView mLastSlideViewWithStatusOn;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.tv_zgb_1, container, false);
		try{
			requestInterface = new RequestInterface(this);
			
			spf = getActivity().getSharedPreferences("status", 0);
			
		toatl=(TextView)view.findViewById(R.id.total);
		
    	mListView = (ListViewCompat) view.findViewById(R.id.listView_zgb_1);
    	URL = URL_help.getInstance().getUrl_address();

		Intent intent = getActivity().getIntent();
		str_mac  = intent.getStringExtra("mac");
		str_name = intent.getStringExtra("name");
		str_type = intent.getStringExtra("type");
		roleid = intent.getStringExtra("roleid");
		buildid = intent.getStringExtra("buildid");
		progressDialog_1=CustomProgressDialog.createDialog(getActivity());
	    progressDialog_1.setMessage("正在读取档案信息");
		progressDialog_1.show();
		//注册接收广播
		//RegisterBroad();

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
					get_Msg_readRecord();
			}
		}.start();
		
		
		
		image_refash = (ImageView) view.findViewById(R.id.img_refresh_zgb);
		image_refash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				    progressDialog_1.setMessage("正在读取档案信息");
					progressDialog_1.show();
					
					new Thread(){
						public void run(){
							get_Msg_readRecord();
						}
					}.start();
					
				
			}
		});
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	
	Handler handler=new Handler(){
		public void handleMessage(Message message){
			try{
			super.handleMessage(message);
			Bundle budle=message.getData();
			if(progressDialog_1.isShowing()){
				progressDialog_1.dismiss();
			}
			String[] dlrcveive=budle.getStringArray("DAXX");
			String[] dlnum=budle.getStringArray("DA_NUM");
			
			switch(message.what){
			case 0:
				if(dlrcveive[0].equals("ReadyFail")){
					Toast.makeText(getActivity(),dlrcveive[1] , Toast.LENGTH_SHORT).show();
				}else{
					if(dlrcveive.length>0){
						toatl.setText(dlnum[1]);
						models = new ArrayList<Gateway_Model>();
						for(int i=0;i<dlrcveive.length;i++){
							System.out.println("-----"+dlrcveive[i]);
							Gateway_Model model = new Gateway_Model();
							model.setTable_num(dlrcveive[i].substring(2,dlrcveive[i].length()-2));
							model.setOnline(RstOnline(dlrcveive[i]));
							model.setCancellation("0");
							models.add(model);
							
							requestInterface.getHttpRequestPost(URL+"/getMeterById","{\"meterid\":\""+dlrcveive[i].substring(2,dlrcveive[i].length()-2)+"\"}");
						}
						listview_adp = new SlideAdapter(models); 
						mListView.setAdapter(listview_adp);
						mListView.setOnItemClickListener(TV_ZGB_1.this);
					}
				}
				break;
			}
			

			

			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
	
	/**查询网关内档案*/
	private void get_Msg_readRecord(){
		try{
			MyOrder odToSend;
			switch (str_type) {  
			case Type_Entity.Gateway_type:// zigbee智能网关
				odToSend = OrderList.getSendByDeviceType(Type_Entity.Gateway_type,
						str_mac, OrderList.READ_SOCKET_RECORD,"");
				break;
			default:
				odToSend = null;
				break;
			}
			
			// 发送命令并得到返回的结果
			String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
					OrderList.USER_PASSWORD, URL, "UTF-8");							
			if(rst_raw==null){
				saveShared("100");
				String []setdata={"ReadyFail","查询档案失败"};
				Message msg=new Message();
				msg.what=0;
				Bundle bundle=new Bundle();
				bundle.putStringArray("DAXX", setdata);
				msg.setData(bundle);
				TV_ZGB_1.this.handler.sendMessage(msg);
				return;
			}else{
				String status = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
				saveShared(status);
				if(status.equals("200")){
					String rst = OrderManager.getInstance().getItemByOrder(rst_raw,
							"result", -1);
				
					String[] dnData=rst.split(",");
					
					String number=getDA_num(rst);//网关内档案个数	
					String num_online=getDA_online(rst);//网关内档案在线个数							
					String[] dnData_num={number,num_online};	
					//查询网关通讯质量
					get_cq_of_gateway();
					
					Message msg=new Message();
					msg.what=0;
					Bundle bundle=new Bundle();
					bundle.putStringArray("DA_NUM", dnData_num);
					bundle.putStringArray("DAXX", dnData);
					msg.setData(bundle);		
					TV_ZGB_1.this.handler.sendMessage(msg);
					return;
				}else{/**
				{"order_no":"1703291554D868AADA3E33A1A15649A987B1A45AD1","result":"网关不在线","status":"300","msg":"任务执行失败"}*/
					String result = OrderManager.getInstance().getItemByOrder(rst_raw,
							"result", -1);
					String [] setdata={"ReadyFail",result};
					Message msg=new Message();
					msg.what=0;
					Bundle bundle=new Bundle();
					bundle.putStringArray("DAXX", setdata);
					msg.setData(bundle);
					TV_ZGB_1.this.handler.sendMessage(msg);
					return;
				}
			}												
		}catch(Exception e){
			saveShared("100");
			Message msg=new Message();
			msg.what=0;
			Bundle bundle=new Bundle();
			String []setdata={"ReadyFail","查询档案失败"};
			bundle.putStringArray("DAXX", setdata);
			msg.setData(bundle);
			TV_ZGB_1.this.handler.sendMessage(msg);
			e.printStackTrace();
			}
	}
	
	/**存储操作结果的回复状态*/
	private void saveShared(String status){
		try{			
			Editor editor = spf.edit();
			editor.putString(str_mac,status);
			editor.commit();
			}catch(Exception e){
				e.printStackTrace();
		}
	}

	
	
	/**检查是否在线*/
	private String RstOnline(String rst){
		String lastrst = null;
		try{
			String newRst=rst.substring(rst.length()-2, rst.length());
			if(newRst=="01"|newRst.equals("01")){
				lastrst="在线";
			}else{
				lastrst="不在线";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return lastrst;
	}
	
	/*当查询的是网关内档案的，计算出网关内档案数以及在线个数*/
	/**网关内档案个数*/
	private String getDA_num(String str){
		int number = 0;
		try{
			if(str.length()/16>=1){
				number = str.split(",").length;
			}
			System.out.println("网关内个数------"+number);
		}catch(Exception e){
			e.printStackTrace();
		}
		return number+"";
	}
	/**在线个数*/
	private String getDA_online(String str){
		int number = 0;
		String str_num;
		try{
			String[] list = str.split(",");
			for(int i=0;i<list.length;i++){
				if(list[i].substring(list[i].length()-2, list[i].length()).equals("01")){
					number=number+1;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("网关内在线个数----"+number);
		return number+"";
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try{
			System.out.println("点击了"+models.get(arg2).getTable_num());
			Gateway_Model model = models.get(arg2);		
			switch(model.getType()){
				case Type_Entity.Gateway_type://zigbee网关
					Intent intent1 = new Intent(".dyd.smart.activity.TVAty_ZGB");
					startActivity(intent1);
					break;
				case Type_Entity.Split_air_conditioning://分体空调控制器
					Intent intent2 = new Intent(".dyd.smart.activity.MessageAty");
					intent2.putExtra("name", model.getName());
					intent2.putExtra("mac", model.getTable_num());
					intent2.putExtra("type", model.getType());
					intent2.putExtra("buildid",buildid);
					intent2.putExtra("roleid", roleid);
					startActivity(intent2);
					break;
				
				case Type_Entity.Central_air_conditioning://中央空调控制器
					Intent intent5 = new Intent(".dyd.smart.activity.MessageAty_Center");
					intent5.putExtra("name", model.getName());
					intent5.putExtra("mac", model.getTable_num());
					intent5.putExtra("type", model.getType());
					intent5.putExtra("buildid",buildid);
					intent5.putExtra("roleid", roleid);
					
					startActivity(intent5);
					break;
				case Type_Entity.Socket_type://zigbee插座
					Intent intent3 = new Intent(".dyd.smart.activity.TVAty");
					intent3.putExtra("name", model.getName());
					intent3.putExtra("mac", model.getTable_num());
					intent3.putExtra("type", model.getType());
					intent3.putExtra("buildid",buildid);
					intent3.putExtra("roleid", roleid);
					
					startActivity(intent3);
					break;
				case Type_Entity.Four_street_control://zigbee四路灯控
					Intent intent3_1 = new Intent(".dyd.smart.activity.TVAty_Four_street");
					intent3_1.putExtra("name", model.getName());
					intent3_1.putExtra("mac", model.getTable_num());
					intent3_1.putExtra("type", model.getType());
					intent3_1.putExtra("buildid",buildid);
					intent3_1.putExtra("roleid", roleid);
					
					startActivity(intent3_1);
					break;
					default:
						Intent intent3_2 = new Intent(".dyd.smart.activity.TVAty");
						intent3_2.putExtra("name", model.getName());
						intent3_2.putExtra("mac", model.getTable_num());
						intent3_2.putExtra("type", model.getType());
						intent3_2.putExtra("buildid",buildid);
						intent3_2.putExtra("roleid", roleid);
						startActivity(intent3_2);
						break;
				}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	
	
	    private class SlideAdapter extends BaseAdapter implements SlideView.OnSlideListener {
	    	private List<Gateway_Model> models;
	        private LayoutInflater mInflater;

	        SlideAdapter(List<Gateway_Model> models) {
	            super();
	            mInflater = getActivity().getLayoutInflater();
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
	        	
	            ViewHolder holder;
	            SlideView slideView = (SlideView) convertView;
	            try{
		            if (slideView == null) {
		                View itemView = mInflater.inflate(R.layout.gateway_item, null);
	
		                slideView = new SlideView(getActivity());
		                slideView.setContentView(itemView);
	
		                holder = new ViewHolder(slideView);
		                slideView.setOnSlideListener(this);
		                slideView.setTag(holder);
		            } else {
		                holder = (ViewHolder) slideView.getTag();
		            }
		            
		            final Gateway_Model model = models.get(position);
		            model.slideView = slideView;
		            model.slideView.shrink();
		            
		            holder.text_gateway_1.setText(model.getTable_num());
		            holder.text_gateway_2.setText(model.getName());
		            holder.text_gateway_3.setText(model.getOnline());
		            
		            if(model.getOnline().equals("不在线")){
		            	holder.text_gateway_4.setImageResource(R.drawable.signal_0);
		            }else{
			            int cancellation = Integer.parseInt(model.getCancellation());
			            if(cancellation==0){
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_0);
			            }else if(cancellation>0&&cancellation<20){
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_1);
			            }else if(cancellation>19&&cancellation<40){
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_2);
			            }else if(cancellation>39&&cancellation<60){
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_3);
			            }else if(cancellation>59&&cancellation<80){
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_4);
			            }else if(cancellation>79&&cancellation<=100){
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_5);
			            }else{
			            	 holder.text_gateway_4.setImageResource(R.drawable.signal_0);
			            }
		            
		            }
		            holder.deleteHolder.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
						    progressDialog_1.setMessage("正在删除档案");
							progressDialog_1.show();
							System.out.println("点击了删除按钮::::::::::"+model.getTable_num());
							/*删除网关内档案*/
							new Thread(new Runnable() {
								@Override
								public void run(){
										try{
											String URL =  URL_help.getInstance().getUrl_address();
											//String receive_code = MachineAty.sendMsgByUserCommand(model.getTable_num(),OrderList.DELETE_ZIGBEE_RECORD,str_mac);
											 String para = "{\"collectorId\":\""+str_mac+"\",\"collectorType\":\""+str_type+"\",\"meterCount\":\""+1+"\",\"meterId\":\"\'"+model.getTable_num()+"\'\"}";
												String result = HttpRequest.sendPost(URL+"/deleteArchive", para);
												JSONArray jsonarray = JSONArray.parseArray(result);
												JSONObject obj = JSONObject.parseObject(jsonarray.get(0).toString());
												Object detail = obj.get("errcode");
												if(detail.equals("0")){
													get_Msg_readRecord();
												}else{
													Object errmsg = obj.get("errmsg");
													Message message = new Message();
													message.what=2;
												    Bundle bundle = new Bundle();
												    bundle.putString("buildMsg", errmsg.toString());
												    message.setData(bundle);
												    TV_ZGB_1.this.handler_buildMsg.sendMessage(message);

												}
										}catch(Exception e){
											e.printStackTrace();
											}
										
									}
							}).start();
						}
					});
		        }catch(Exception e){
		        	e.printStackTrace();
		        }

	            return slideView;
	        }

	        @Override
	        public void onSlide(View view, int status) {
	            if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
	                mLastSlideViewWithStatusOn.shrink();
	            }

	            if (status == SLIDE_STATUS_ON) {
	                mLastSlideViewWithStatusOn = (SlideView) view;
	            }
	        }

	    }


	    private static class ViewHolder {
	        public TextView text_gateway_1;
	        public TextView text_gateway_2;
	        public TextView text_gateway_3;
	        public ImageView text_gateway_4;
	        public ViewGroup deleteHolder;

	        ViewHolder(View view) {
	        	text_gateway_1 = (TextView) view.findViewById(R.id.text_gateway_1);
	        	text_gateway_2 = (TextView) view.findViewById(R.id.text_gateway_2);
	        	text_gateway_3 = (TextView) view.findViewById(R.id.text_gateway_3);
	        	text_gateway_4 = (ImageView) view.findViewById(R.id.text_gateway_4);
	            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
	        }
	    }



	    @Override
	    public void onClick(View v) {
	        if (v.getId() == R.id.holder) {            
	        }
	    }
	    
		
		/**注册接收广播*/
		public void RegisterBroad(){
			//界面更新广播接收;
			new Thread(){
				public void run(){
					receiver = new ReceiveTool();
					IntentFilter intentfilter_1 = new IntentFilter("Intent.SELECT_cq");
					getActivity().registerReceiver(receiver, intentfilter_1);
				}
			}.start();	
		}
		
		Handler handler_1 = new Handler(){
			public void handleMessage(Message msg){
			try{
				super.handleMessage(msg);
				
				if (msg.what == 1) {
					String bundle_msg = msg.getData().getString("content");
					if(bundle_msg.split("/")[1].equals(str_mac)){
						switch(bundle_msg.split("/")[2]){//selected/(没发送过来)   表类型/表号/get_cq_of_gateway/data
						case "get_cq_of_gateway"://网关内档案通讯质量查询
							String get_msg = bundle_msg.split("/")[3];
							if(get_msg!="null"){
								String [] data_list = get_msg.split("\n");
								for(int i=0;i<data_list.length;i++){
									System.out.println("data_list:"+data_list[i]);
									for(int j=0;j<models.size();j++){
										if(data_list[i].split(" ")[0].equals(models.get(j).getTable_num())){
											Gateway_Model model = models.get(j);
											model.setCancellation(data_list[i].split(" ")[1]);
										}
									}
								}
								listview_adp.notifyDataSetChanged();
							}

							break;
						case "get_cq_of_gateway_Fail":
							Toast.makeText(getActivity(), "数据更新失败", Toast.LENGTH_SHORT).show();
							break;
						}
					
					}
				}
				
			
			}catch(Exception e){
				e.printStackTrace();
				}
			}
		};
		
	    
	    
	    /**读取网关内档案在线率
	     * realtimedata/表类型/表号/get_cq_of_meter
		   realtimedata/类型/采集器表号/get_cq_of_gateway
		*/
		private void get_cq_of_gateway(){
			new Thread(){
				public void run(){
					try {
						//realtimedata/类型/表号/get_meter_current_blob
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(getActivity(),handler_1,
								Type_Entity.Gateway_type+"/"+str_mac,
								"realtimedata/"+Type_Entity.Gateway_type+"/"+str_mac+"/get_cq_of_gateway");	
						
//						PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(Type_Entity.Central_air_conditioning+"/"+str_mac,
//								"realtimedata/"+Type_Entity.Central_air_conditioning+"/"+str_mac+"/get_meter_current_blob");
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();	
		}
		
		class ReceiveTool extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				System.out.println("************************收到广播，这是中央空调界面："+intent.getAction());
				try{
					if(intent.getStringExtra("msg").split("/")[1].equals(str_mac)){
						switch(intent.getStringExtra("msg").split("/")[2]){
						case "get_cq_of_gateway"://网关内档案通讯质量查询
							String get_msg = intent.getStringExtra("msg").split("/")[3];
							if(get_msg!="null"){
								String [] data_list = get_msg.split("\n");
								for(int i=0;i<data_list.length;i++){
									System.out.println("data_list:"+data_list[i]);
									for(int j=0;j<models.size();j++){
										if(data_list[i].split(" ")[0].equals(models.get(j).getTable_num())){
											Gateway_Model model = models.get(j);
											model.setCancellation(data_list[i].split(" ")[1]);
										}
									}
								}
								listview_adp.notifyDataSetChanged();
							}

							break;
						case "get_cq_of_gateway_Fail":
							Toast.makeText(getActivity(), "数据更新失败", Toast.LENGTH_SHORT).show();
							break;
						}
					}
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}


		
		/**
		 * [{"eqpt_id_code":"002014110119","eqpt_name":"格力定频-总监分体","eqpt_type":"0a0001aa7k","eqpt_type_name":"北电分体空调控制器",
		 * "collect":"42435c5573fe","eqpt_building_name":"技术总监办公室","create_time":"2016-05-12 10:16:32","laber":"ac","tab":"null",
		 * "eqpt_status":"1","baudrate":"2400bps","commport":"1F"}]*/

		Handler handler_buildMsg = new Handler(){
			public void handleMessage(Message message){
				try{
				super.handleMessage(message);
				String msg = message.getData().getString("buildMsg");
				switch(message.what){
				case 1:
					if(msg!=null && !msg.equals("exception")){
						JSONArray jsonarray = JSONArray.parseArray(msg);
						JSONObject json = (JSONObject) jsonarray.get(0);
						String tablenum = (String) json.get("eqpt_id_code");
						String eqpt_type = (String) json.get("eqpt_type");
						String eqpt_name = (String) json.get("eqpt_name");
						for(int i=0;i<models.size();i++){
							if(models.get(i).getTable_num().equals(tablenum)){
								models.get(i).setName(eqpt_name);
								models.get(i).setType(eqpt_type);
								listview_adp.notifyDataSetChanged();
								return;
							}
						}
					}
					break;
				case 2:
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					break;
				}
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		};

		@Override
		public void requestShow(String msg) {
			// TODO Auto-generated method stub
			System.out.println("查询方案数据：    ---------------"+msg);
			Message message = new Message();
			message.what = 1;
		    Bundle bundle = new Bundle();
		    bundle.putString("buildMsg", msg.split(";")[0]);
		    message.setData(bundle);
		    TV_ZGB_1.this.handler_buildMsg.sendMessage(message);
		}
	
	
}
