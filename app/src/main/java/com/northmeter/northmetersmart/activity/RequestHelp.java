package com.northmeter.northmetersmart.activity;


import com.ibm.mqtt.MqttException;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive;
import com.northmeter.northmetersmart.mqtt.PublishMqttMessageAndReceive_1;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RequestHelp {
	private Context context;

	public RequestHelp(Context context){
		this.context = context;
	}
	/**读取48小时用电数据*/
	public void get_Form_Data48h(final String str_mac,final String str_type){
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(context,
							str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/read48h");
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(type+"/"+str_mac,"realtimedata/"+type+"/"+str_mac+"/read48h");
				} catch (MqttException e) {//realtimedata/0a0001aa7k/002014110119/read48h
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

			}
		}.start();	
	}
	
	/**读取48小时用电数据*/
	public void get_Form_Data48h_1(final Handler handler,final String str_mac,final String str_type){
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(context,handler,
							str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/read48h");
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(type+"/"+str_mac,"realtimedata/"+type+"/"+str_mac+"/read48h");
				} catch (MqttException e) {//realtimedata/0a0001aa7k/002014110119/read48h
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

			}
		}.start();	
	}
	
	/**读取30个点的实时用电功率与温度*/
	public void get_Form_Data30point(final String str_mac,final String str_type){
			new Thread(){
				public void run(){
					try {
						System.out.println("*******--------------------------------"+str_type+"/"+str_mac+"/read30point");
						PublishMqttMessageAndReceive.getInstance().PublishMessage(context,
								str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/read30point");
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(type+"/"+str_mac,"realtimedata/"+type+"/"+str_mac+"/read30point");
					} catch (MqttException e) {//realtimedata/0a0001aa7k/002014110119/read30point
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 	
				}
			}.start();
	
	}
	
	/**读取30个点的实时用电功率与温度*/
	public void get_Form_Data30point_1(final Handler handler,final String str_mac,final String str_type){
			new Thread(){
				public void run(){
					try {
						System.out.println("*******--------------------------------"+str_type+"/"+str_mac+"/read30point");
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(context,handler,
								str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/read30point");
						//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord(type+"/"+str_mac,"realtimedata/"+type+"/"+str_mac+"/read30point");
					} catch (MqttException e) {//realtimedata/0a0001aa7k/002014110119/read30point
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 	
				}
			}.start();
	
	}
	
	
		/**读取活动流数据
	    发送：realtimedata/类型/表号/get_socket_readtime_workflow
	    返回: get_socket_readtime_workflow/0a0001aa7k/002014110119/data
	    数据结果：active#none#0h15min#none*/  //common一般  starting up开机  power off关机   standby 待机
	 public  void get_socket_readtime_workflow(final String str_mac,final String str_type){
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive.getInstance().PublishMessage(context,
							str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_socket_readtime_workflow");	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a820/"+str_mac,"realtimedata/0a0001a820/"+str_mac+"/get_socket_readtime_workflow");
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 	
			}
		}.start();

	 }
	 
	 /**读取设备通讯质量
	     * realtimedata/表类型/表号/get_cq_of_meter
		   realtimedata/类型/采集器表号/get_cq_of_gateway
		*/
		public void get_cq_of_meter(final String str_mac,final String str_type){
			new Thread(){
				public void run(){
					try {
						//realtimedata/类型/表号/get_cq_of_meter
						PublishMqttMessageAndReceive.getInstance().PublishMessage(context,
								str_type+"/"+str_mac,
								"realtimedata/"+str_type+"/"+str_mac+"/get_cq_of_meter");	
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();	
		}
		
		/** get_report_year_month_list(Meter_type, Meter)
		realtimedata/表类型/表号/get_report_year_month_list*/
		/**获取月报表月份列表*/
		public void get_report_year_month_list(final Handler handler,final String str_mac,final String str_type){
			new Thread(){
				public void run(){
					try {
						PublishMqttMessageAndReceive_1.getInstance().PublishMessage(context,handler,
								str_type+"/"+str_mac,
								"realtimedata/"+str_type+"/"+str_mac+"/get_report_year_month_list");	
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}.start();

		}
		
		/**读取活动数据
        	发送：realtimedata/类型/表号/get_work_activities
        	返回: get_work_activities/0a0001aa7k/002014110119/data
        	数据结果：active#none#0h15min#none*/  
		public void get_work_activities(final Handler handler,final String str_mac,final String str_type){
		new Thread(){
			public void run(){
				try {
					PublishMqttMessageAndReceive_1.getInstance().PublishMessage(context,handler,
							str_type+"/"+str_mac,"realtimedata/"+str_type+"/"+str_mac+"/get_work_activities");	
					//PublishMqttAddMRecord pushMR = new PublishMqttAddMRecord("0a0001a820/"+str_mac,"realtimedata/0a0001a820/"+str_mac+"/get_socket_readtime_workflow");
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

		}
		
	
}
