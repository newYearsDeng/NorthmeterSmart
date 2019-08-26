package com.northmeter.northmetersmart.mqtt;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.Time;
import android.widget.Toast;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;
import com.northmeter.northmetersmart.control.URL_help;

/**
 * 发送mqtt消息到broke代理并且接受数据
 * */
public class PublishMqttMessageAndReceive implements MqttSimpleCallback{

	IMqttClient mqttClient = null;
	private String mDeviceID = "smart125";
	private static MqttPersistence MQTT_PERSISTENCE = null;
	public static String MQTT_CLIENT_ID = "smart";
	public static final String PREF_DEVICE_ID = "deviceID";
	private static short MQTT_KEEP_ALIVE = 60 * 15;
	private long mStartTime;
	private String SUPER_SIGN = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
	private Context context;

	// The broker should not retain any messages.

	private PublishMqttMessageAndReceive mConnection;
	
	private static PublishMqttMessageAndReceive uniqueInstance = null;
	public synchronized static PublishMqttMessageAndReceive getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new PublishMqttMessageAndReceive();
		}
		return uniqueInstance;
	}
	public static PublishMqttMessageAndReceive getUniqueInstance() {
		return uniqueInstance;
	}
	public static void setPublishMqttMessageAndReceive(PublishMqttMessageAndReceive uniqueInstance) {
		PublishMqttMessageAndReceive.uniqueInstance = uniqueInstance;
	}

	public PublishMqttMessageAndReceive(){}
	
	// Creates a new connection given the broker address and initial topic
	@SuppressLint("NewApi")
	public synchronized void PublishMessage(Context context,String TopicName,String msg)
			throws MqttException {
			this.context = context;
			//获取设备唯一id,可能有些设备获取为unknow，在一般情况下暂时先使用这个id
			String SerialNumber = android.os.Build.SERIAL; 
				
			String mqtt_add = URL_help.getInstance().getEmqtt_address();
			String mqttConnSpec = "tcp://" + mqtt_add.split(":")[0] + "@"
					+ mqtt_add.split(":")[1];
	
			mqttClient = MqttClient.createMqttClient(mqttConnSpec,
					MQTT_PERSISTENCE);
			
			
//			String [] msg_list = msg.split("/");
//			String clientID = msg_list[msg_list.length-1];
			String clientID = MQTT_CLIENT_ID + "_"+SerialNumber;
			
			mqttClient.connect(clientID, true,MQTT_KEEP_ALIVE);
	
			mqttClient.registerSimpleHandler(this);
	
			//subscribeToTopic("TopicA");
			String [] topics = TopicName.split(";");
			int [] arg = new int[topics.length];
			for(int i=0;i<topics.length;i++){
				arg[i]=0;
			}
			
			mqttClient.subscribe(topics, arg);
			
			publishToTopic(topics[0],msg);
			
			System.out.println("发送数据到mqtt==:"+msg);
			System.out.println("Connection established to " + PushService.MQTT_HOST + " on topic ");
			
			mStartTime = System.currentTimeMillis();
			//mqttClient.disconnect();

	}
	@Override
	public void connectionLost() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public synchronized void publishArrived(String topicName, byte[] payload, int qos,
			boolean retained) throws Exception{
		// Show a notification
		final String receive = new String(payload);
		
		new Thread(){
			public synchronized void run(){
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("接收到mqtt推送的消息，新的通讯框架********"+receive);
				String[] result = new HandleMqttMessage(context).handleMqttM(receive);
				if(result==null){
					sendBroad("Intent.UPDATA_ZIGBEE",receive);
					//showNotification(new HandleMqttMessage(getApplicationContext()).handleMqttM(receive));
				}else{
					switch(result[0]){
					case "STATUSMSG"://登录包
						sendBroad("Intent.UPDATA",result[1]);
						break;
					case "UPDATA_NOTIFY"://设备数据主动上报
						sendBroad("Intent.UPDATA",result[1]);
						break;
					case "NOTIFY_ZIGBEE"://手动抄表数据广播
						sendBroad("Intent.NOTIFY_ZIGBEE",result[1]);
						break;
					case "WARNINGMSG"://报警信息上报
						sendBroad("Intent.UPDATA","warningmsg");
						//showNotification(result[1].split("/")[2]+"功率超限!");
						break;
					case "AddManageRecord"://添加档案时收到的数据
						sendBroad("Intent.AddManageRecord",result[1]);
						break;
					case "NOTIFY_AIR"://空调红外发送等收到的数据
						sendBroad("Intent.NOTIFY_AIR",result[1]);
						break;
					case "SELETC"://报表，能耗数据查询
						sendBroad("Intent.SELETC",result[1]);
						break;
					case "NOTIFY_AIR_TASK"://空调定时任务更新
						sendBroad("NOTIFY_AIR_TASK", result[1]);
						break;
					case "NOTIFY_ONE_TASK"://一次性定时任务
						sendBroad("NOTIFY_ONE_TASK", result[1]);
						break;
					case "NOTIFY_QUOTA_TASK"://月度定额任务
						sendBroad("NOTIFY_QUOTA_TASK", result[1]);
						break;
					case "SELECT_CENTER"://中央空调数据块查询
						sendBroad("Intent.SELECT_CENTER", result[1]);
						break;
					case "SELECT_cq"://通讯质量数据查询
						sendBroad("Intent.SELECT_cq", result[1]);
						break;
					case "SELECT_REPORT"://月报表数据
						sendBroad("Intent.SELECT_REPORT", result[1]);
						break;
					
					}
					
				}
//				System.out.println("Get message: " + receive);
			}
		}.start();
		
	}
	
	
	private synchronized void publishToTopic(String topicName, String message)
			throws MqttException {
		if ((mqttClient == null) || (mqttClient.isConnected() == false)) {

		} else {
			System.out.println("PublishMqttMessageAndReceive发送请求------");
			mqttClient.publish(topicName, message.getBytes(),0, false);
		}
	}
	
	//接收到来自mqtt推送的消息后发出广播通知；
	private void sendBroad(String action,String str){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra("msg", str);
		context.sendBroadcast(intent);
	}
	
	
}

