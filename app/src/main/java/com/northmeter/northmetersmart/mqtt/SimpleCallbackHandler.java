package com.northmeter.northmetersmart.mqtt;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.inputmethod.InputMethodSession.EventCallback;

import com.ibm.mqtt.MqttSimpleCallback;

/**
 * 使用EventBus分发事件
 */
public class SimpleCallbackHandler implements MqttSimpleCallback {
	private String instanceData = "";
	private Handler handler;
	private Context context;

	public SimpleCallbackHandler(Context context , String instance, Handler handler) {
		instanceData = instance;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void connectionLost() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishArrived(String topicName, byte[] payload, int Qos, boolean retained)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			final String receive = new String(payload);
			new Thread(){
				public synchronized void run(){
					String bundle_msg = null;
					String[] result = new HandleMqttMessage(context).handleMqttM(receive);
					if(result==null){
						return;
						//bundle_msg = receive;
						//showNotification(new HandleMqttMessage(getApplicationContext()).handleMqttM(receive));
					}else{
//						switch(result[0]){
//						case "STATUSMSG"://登录包
//							bundle_msg = result[1];
//							break;
//						case "UPDATA_NOTIFY"://设备数据主动上报
//							bundle_msg = result[1];
//							break;
//						case "NOTIFY_ZIGBEE"://手动抄表数据广播
//							bundle_msg = result[1];
//							break;
//						case "WARNINGMSG"://报警信息上报
//							bundle_msg = result[1];
//							//showNotification(result[1].split("/")[2]+"功率超限!");
//							break;
//						case "AddManageRecord"://添加档案时收到的数据
//							bundle_msg = result[1];
//							break;
//						case "NOTIFY_AIR"://空调红外发送等收到的数据
//							bundle_msg = result[1];
//							break;
//						case "SELETC"://报表，能耗数据查询
//							bundle_msg = result[1];
//							break;
//						case "NOTIFY_AIR_TASK"://空调定时任务更新
//							bundle_msg = result[1];
//							break;
//						case "NOTIFY_ONE_TASK"://一次性定时任务
//							bundle_msg = result[1];
//							break;
//						case "NOTIFY_QUOTA_TASK"://月度定额任务
//							bundle_msg = result[1];
//							break;
//						case "SELECT_CENTER"://中央空调数据块查询
//							bundle_msg = result[1];
//							break;
//						case "SELECT_cq"://通讯质量数据查询
//							bundle_msg = result[1];
//							break;
//						case "SELECT_REPORT"://月报表数据
//							bundle_msg = result[1];
//							break;
//						case "Zigbee_dwcssjk"://读取电网参数数据块
//							bundle_msg = result[1];
//							break;
//						
//						}
						bundle_msg = result[1];
						
					}
					System.out.println("Get message: " + receive);
					System.out.println("Get message: " + bundle_msg);
					Message msg = Message.obtain();
					Bundle bundle = new Bundle();
					bundle.putString("content", bundle_msg);
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			}.start();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}




}