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
public class PublishMqttMessageAndReceive_1{

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

	private PublishMqttMessageAndReceive_1 mConnection;
	
	private static PublishMqttMessageAndReceive_1 uniqueInstance = null;
	public synchronized static PublishMqttMessageAndReceive_1 getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new PublishMqttMessageAndReceive_1();
		}
		return uniqueInstance;
	}
	public static PublishMqttMessageAndReceive_1 getUniqueInstance() {
		return uniqueInstance;
	}
	public static void setPublishMqttMessageAndReceive(PublishMqttMessageAndReceive_1 uniqueInstance) {
		PublishMqttMessageAndReceive_1.uniqueInstance = uniqueInstance;
	}

	public PublishMqttMessageAndReceive_1(){}
	
	// Creates a new connection given the broker address and initial topic
	@SuppressLint("NewApi")
	public synchronized void PublishMessage(Context context,Handler handler,String TopicName,String msg)
			throws MqttException {
			this.context = context;
			//获取设备唯一id,可能有些设备获取为unknow，在一般情况下暂时先使用这个id
			String SerialNumber = android.os.Build.SERIAL; 
				
			String mqtt_add = URL_help.getInstance().getEmqtt_address();
			String mqttConnSpec = "tcp://" + mqtt_add.split(":")[0] + "@"
					+ mqtt_add.split(":")[1];
			System.out.println("mqttConnSpec************"+mqttConnSpec);
	
			mqttClient = MqttClient.createMqttClient(mqttConnSpec,
					MQTT_PERSISTENCE);
			
			
//			String [] msg_list = msg.split("/");
//			String clientID = msg_list[msg_list.length-1];
			String clientID = MQTT_CLIENT_ID + "_"+SerialNumber;
			
			mqttClient.connect(clientID, true,MQTT_KEEP_ALIVE);
			SimpleCallbackHandler callback = new SimpleCallbackHandler(context,clientID, handler);
			mqttClient.registerSimpleHandler(callback);
	
			//subscribeToTopic("TopicA");
			String [] topics = TopicName.split(";");
			int [] arg = new int[topics.length];
			for(int i=0;i<topics.length;i++){
				arg[i]=0;
			}
			
			mqttClient.subscribe(topics, arg);
			
			publishToTopic(topics[0],msg);
			
			System.out.println("发送数据到mqtt__1==:"+msg);
			System.out.println("Connection established to " + mqtt_add + " on topic ");
			
			mStartTime = System.currentTimeMillis();
			//mqttClient.disconnect();

	}


	private synchronized void publishToTopic(String topicName, String message)
			throws MqttException {
		if ((mqttClient == null) || (mqttClient.isConnected() == false)) {

		} else {
			System.out.println("PublishMqttMessageAndReceive_1发送请求------");
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

