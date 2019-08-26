package com.northmeter.northmetersmart.mqtt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.activity.MainActivity;
import com.northmeter.northmetersmart.db.DBReportData;
import com.northmeter.northmetersmart.model.ReportData_Model;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PushService extends Service {
	// this is the log tag
	public static final String TAG = "DemoPushService";

	//public static final String MQTT_HOST = "218.17.157.121";
	public static final String MQTT_HOST = "218.17.157.121";

	public static int MQTT_BROKER_PORT_NUM = 1883;

	private static MqttPersistence MQTT_PERSISTENCE = null;
	//设置为false会接收离线消息，设置为true则不会接收离线消息；
	private static boolean MQTT_CLEAN_START = true;

	private static short MQTT_KEEP_ALIVE = 60 * 15;

	private static int[] MQTT_QUALITIES_OF_SERVICE;
	
	private static int MQTT_QUALITY_OF_SERVICE = 0;
	// The broker should not retain any messages.
	private static boolean MQTT_RETAINED_PUBLISH = false;

	public static String MQTT_CLIENT_ID = "smart";

	// These are the actions for the service (name are descriptive enough)
	private static final String ACTION_START = MQTT_CLIENT_ID + ".START";
	private static final String ACTION_STOP = MQTT_CLIENT_ID + ".STOP";
	private static final String ACTION_KEEPALIVE = MQTT_CLIENT_ID
			+ ".KEEP_ALIVE";
	private static final String ACTION_RECONNECT = MQTT_CLIENT_ID
			+ ".RECONNECT";

	// Connection log for the push service. Good for debugging.
	private ConnectionLog mLog;

	// Connectivity manager to determining, when the phone loses connection
	private ConnectivityManager mConnMan;
	// Notification manager to displaying arrived push notifications
	private NotificationManager mNotifMan;

	// Whether or not the service has been started.
	private boolean mStarted;

	// This the application level keep-alive interval, that is used by the
	// AlarmManager
	// to keep the connection active, even when the device goes to sleep.
	private static final long KEEP_ALIVE_INTERVAL = 1000 * 60;

	// Retry intervals, when the connection is lost.
	private static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
	private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;

	// Preferences instance
	private SharedPreferences mPrefs;
	// We store in the preferences, whether or not the service has been started
	public static final String PREF_STARTED = "isStarted";
	// We also store the deviceID (target)
	public static final String PREF_DEVICE_ID = "deviceID";
	// We store the last retry interval
	public static final String PREF_RETRY = "retryInterval";

	// Notification title
	public static String NOTIF_TITLE = "微建筑能源";
	// Notification id
	private static int NOTIF_CONNECTED = 0;

	// This is the instance of an MQTT connection.
	private MQTTConnection mConnection;
	private long mStartTime;
								   
	private String Process_Name = "org.dyd.smart.activity:service2";
	
	// Static method to start the service
	public static void actionStart(Context ctx) {
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	// Static method to send a keep alive message
	public static void actionPing(Context ctx) {
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(ACTION_KEEPALIVE);
		ctx.startService(i);
	}
	

	@Override
	public void onCreate() {
		try{
			super.onCreate();
			timetesk();
			System.out.println("这是mqtt1.0版本的启动服务。。。。。");
			log("Creating service");
			mStartTime = System.currentTimeMillis();
	
			try {
				mLog = new ConnectionLog();
				Log.i(TAG, "Opened log at " + mLog.getPath());
			} catch (IOException e) {
				Log.e(TAG, "Failed to open log", e);
			}
	
			// Get instances of preferences, connectivity manager and notification
			// manager
			mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
			mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
			/*
			 * If our process was reaped by the system for any reason we need to
			 * restore our state with merely a call to onCreate. We record the last
			 * "started" value and restore it here if necessary.
			 */
			handleCrashedService();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// This method does any necessary clean-up need in case the server has been
	// destroyed by the system
	// and then restarted
	private void handleCrashedService() {
		if (wasStarted() == true) {
			log("Handling crashed service...");
			// stop the keep alives
			stopKeepAlives();

			// Do a clean start
			start();
		}
	}

	@Override
	public void onDestroy() {
		log("Service destroyed (started=" + mStarted + ")");

		// Stop the services, if it has been started
		if (mStarted == true) {
			stop();
		}else{
			actionStart(getApplicationContext());
		}
		
		try {
			if (mLog != null)
				mLog.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		try{
			super.onStart(intent, startId);
			log("Service started with intent=" + intent);

			// Do an appropriate action based on the intent.
			if (intent.getAction().equals(ACTION_STOP) == true) {
				stop();
				stopSelf();
			} else if (intent.getAction().equals(ACTION_START) == true) {
				start();
			} else if (intent.getAction().equals(ACTION_KEEPALIVE) == true) {
				keepAlive();
			} else if (intent.getAction().equals(ACTION_RECONNECT) == true) {
				if (isNetworkAvailable()) {
					reconnectIfNecessary();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
							}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// log helper function
	private void log(String message) {
		log(message, null);
	}

	private void log(String message, Throwable e) {
		if (e != null) {
			Log.e(TAG, message, e);

		} else {
			Log.i(TAG, message);
		}

		if (mLog != null) {
			try {
				  mLog.println(message);
			} catch (IOException ex) {
			}
		}
	}

	// Reads whether or not the service has been started from the preferences
	private boolean wasStarted() {
		return mPrefs.getBoolean(PREF_STARTED, false);
	}

	// Sets whether or not the services has been started in the preferences.
	private void setStarted(boolean started) {
		mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
		mStarted = started;
	}

	private synchronized void start() {
		log("Starting service...");

		// Do nothing, if the service is already running.
		if (mStarted == true) {
			Log.w(TAG, "Attempt to start connection that is already active");
			return;
		}

		// Establish an MQTT connection
		connect();

		// Register a connectivity listener
		registerReceiver(mConnectivityChanged, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private synchronized void stop() {
		// Do nothing, if the service is not running.
		if (mStarted == false) {
			Log.w(TAG, "Attempt to stop connection not active.");
			return;
		}

		// Save stopped state in the preferences
		setStarted(false);

		// Remove the connectivity receiver
		unregisterReceiver(mConnectivityChanged);
		// stopping the service.
		cancelReconnect();

		// Destroy the MQTT connection if there is one
		if (mConnection != null) {
			mConnection.disconnect();
			mConnection = null;
		}
	}

	//
	private synchronized void connect() {
		log("Connecting...");
		// fetch the device ID from the preferences.
		String deviceID = mPrefs.getString(PREF_DEVICE_ID, null);
		// Create a new connection only if the device id is not NULL
		if (deviceID == null) {
			log("Device ID not found.");
		} else {
			try {
				mConnection = new MQTTConnection(MQTT_HOST, deviceID);
			} catch (MqttException e) {
				e.printStackTrace();
				// Schedule a reconnect, if we failed to connect
				log("MqttException: "
						+ (e.getMessage() != null ? e.getMessage() : "NULL"));
				if (isNetworkAvailable()) {
					scheduleReconnect(mStartTime);
				}
			}
			setStarted(true);
		}
	}

	private synchronized void keepAlive() {
		try {
			// Send a keep alive, if there is a connection.
			if (mStarted == true && mConnection != null) {
				mConnection.sendKeepAlive();
			}
		} catch (MqttException e) {
			log("MqttException: " 
					+ (e.getMessage() != null ? e.getMessage() : "NULL"), e);

			mConnection.disconnect();
			mConnection = null;
			cancelReconnect();
		}
	}

	// Schedule application level keep-alives using the AlarmManager
	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + KEEP_ALIVE_INTERVAL,
				KEEP_ALIVE_INTERVAL, pi);
	}

	// Remove all scheduled keep alives
	private void stopKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	// We schedule a reconnect based on the starttime of the service
	public void scheduleReconnect(long startTime) {
		// the last keep-alive interval
		long interval = mPrefs.getLong(PREF_RETRY, INITIAL_RETRY_INTERVAL);

		// Calculate the elapsed time since the start
		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		// Set an appropriate interval based on the elapsed time since start
		if (elapsed < interval) {
			interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
		} else {
			interval = INITIAL_RETRY_INTERVAL;
		}

		log("Rescheduling connection in " + interval + "ms.");

		// Save the new internval
		mPrefs.edit().putLong(PREF_RETRY, interval).commit();

		// Schedule a reconnect using the alarm manager.
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
	}

	// Remove the scheduled reconnect
	public void cancelReconnect() {
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void reconnectIfNecessary() {
		if (mStarted == true && mConnection == null) {
			log("Reconnecting...");
			connect();
		}
	}

	// This receiver listeners for network changes and updates the MQTT
	// connection
	// accordingly
	private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get network info
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			// Is there connectivity?
			boolean hasConnectivity = (info != null && info.isConnected()) ? true
					: false;

			log("Connectivity changed: connected=" + hasConnectivity);

			if (hasConnectivity) {
				reconnectIfNecessary();
			} else if (mConnection != null) {
				// if there no connectivity, make sure MQTT connection is
				// destroyed
				mConnection.disconnect();
				cancelReconnect();
				mConnection = null;
			}
		}
	};

	// 发出消息通知到前台;
	@SuppressLint("NewApi")
	private void showNotification(String text) {
		 NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
		 Notification myNotify = new Notification();  
         myNotify.icon = R.drawable.apk_icon;  
         myNotify.tickerText = "您有新消息，请注意查收！";  
         myNotify.when = System.currentTimeMillis();
         myNotify.defaults=Notification.DEFAULT_SOUND;
         myNotify.flags = Notification.FLAG_ONLY_ALERT_ONCE;// 够自动清除  
         myNotify.defaults = Notification.DEFAULT_ALL;
         myNotify.visibility=Notification.VISIBILITY_PUBLIC;//在任何情况下都显示，不受锁屏影响。
         //myNotify.priority = Notification.PRIORITY_HIGH;
         
         RemoteViews rv = new RemoteViews(getPackageName(),  
                 R.layout.notification_layout);
         rv.setTextViewText(R.id.text_content_1, "微建筑能源");  
         rv.setTextViewText(R.id.text_content_2, text);
         myNotify.contentView = rv;  
         Intent intent = new Intent(this,MainActivity.class);
         PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  
                 intent, 0);  
         myNotify.contentIntent = contentIntent;  
         manager.notify(NOTIF_CONNECTED++, myNotify);  
		
	}

	// Check if we are online
	private boolean isNetworkAvailable() {
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}

	// This inner class is a wrapper on top of MQTT client.
	private class MQTTConnection implements MqttSimpleCallback {
		IMqttClient mqttClient = null;

		// Creates a new connection given the broker address and initial topic
		public MQTTConnection(String brokerHostName, String initTopic)
				throws MqttException {
			// Create connection spec
			SharedPreferences spf=getSharedPreferences("URL_ADD", MODE_PRIVATE);
			String mqtt_add = spf.getString("MQTT_ADD", "218.17.157.121:1883");

			String mqttConnSpec = "tcp://" + mqtt_add.split(":")[0] + "@"
					+ mqtt_add.split(":")[1];
			// Create the client and connect
			mqttClient = MqttClient.createMqttClient(mqttConnSpec,
					MQTT_PERSISTENCE);
			String clientID = MQTT_CLIENT_ID + "/"
					+ mPrefs.getString(PREF_DEVICE_ID, "");
			mqttClient.connect(clientID, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);

			// register this client app has being able to receive messages
			mqttClient.registerSimpleHandler(this);

			// Subscribe to an initial topic, which is combination of client ID
			// and device ID.
			initTopic = MQTT_CLIENT_ID + "/" + initTopic;
			subscribeToTopic();
			//publishToTopic("TopicA","发送消息到mqttd");
			log("Connection established to " + brokerHostName + " on topic "
					+ initTopic);

			// Save start time
			mStartTime = System.currentTimeMillis();
			// Star the keep-alives
			startKeepAlives();
		}

		// Disconnect
		public void disconnect() {
			try {
				stopKeepAlives();
				mqttClient.disconnect();
			} catch (MqttPersistenceException e) {
				log("MqttException"
						+ (e.getMessage() != null ? e.getMessage() : " NULL"),
						e);
			}
		}

		/*
		 * Send a request to the message broker to be sent messages published
		 * with the specified topic name. Wildcards are allowed.
		 */
		private void subscribeToTopic() throws MqttException {

			if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
				// quick sanity check - don't try and subscribe if we don't have
				log("Connection error" + "No connection");
			} else {
				try{
				List<String> topiclist = new HandleMqttMessage(getApplicationContext()).getDeviceTopic();
				if(topiclist!=null){
					String[] topics = new String[topiclist.size()];
					MQTT_QUALITIES_OF_SERVICE = new int[topiclist.size()];
					for(int i=0;i<topiclist.size();i++){
						topics[i] = topiclist.get(i);
						MQTT_QUALITIES_OF_SERVICE[i] = 0;
					}
					
					mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);
				}
//				String[] topics = { "0a0001a03c/160127001285","0a0003ahup/5a4ce2e92136"};
//				MQTT_QUALITIES_OF_SERVICE= new int[]{ 0,0 };
//				mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		/*
		 * Sends a message to the message broker, requesting that it be
		 * published to the specified topic.
		 * 发送定义的topic消息到代理器
		 */
		private void publishToTopic(String topicName, String message)
				throws MqttException {
			if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
				// quick sanity check - don't try and publish if we don't have
				// a connection
				log("No connection to public to");
			} else {
				mqttClient.publish(topicName, message.getBytes(),
						MQTT_QUALITY_OF_SERVICE, MQTT_RETAINED_PUBLISH);
			}
		}

		/*
		 * Called if the application loses it's connection to the message
		 * broker.
		 */
		public void connectionLost() throws Exception {
			log("Loss of connection" + "connection downed");
			stopKeepAlives();
			// null itself
			mConnection = null;
			if (isNetworkAvailable() == true) {
				reconnectIfNecessary();
			}
		}

		/*
		 * Called when we receive a message from the message broker.
		 */
		public synchronized void publishArrived(String topicName, final byte[] payload, int qos,
				boolean retained) {
//			log("Get message: " + receive);
//			showNotification(receive);
			new Thread(){
				public void run(){
					try{
						DBReportData dbd = new DBReportData(PushService.this);
						String receive = new String(payload);
						System.out.println("********************pushService****************"+receive);
						String[] result = new HandleMessageToService(getApplicationContext()).handleMqttM(receive);
						if(result==null){
							//sendBroad("Intent.UPDATA_ZIGBEE",receive);
							//showNotification(new HandleMqttMessage(getApplicationContext()).handleMqttM(receive));
						}else{
							switch(result[0]){
							case "STATUSMSG"://statusMsg/北电以太网-Zigbee采集器#测试办公物联#系统部#online#时间 
								sendBroad("Intent.UPDATA",result[1]);
								String msg = result[1].split("/")[1];	
								ReportData_Model rdm = new ReportData_Model();
								switch(msg.split("#")[3]){
								case "online":
									showNotification(msg.split("#")[4]+": "+msg.split("#")[2]+msg.split("#")[1]+"("+msg.split("#")[0]+")上线");
									rdm.setTime(msg.split("#")[4]);
									rdm.setReportData(msg.split("#")[2]+msg.split("#")[1]+"("+msg.split("#")[0]+")上线");
									break;
								case "offline":
									showNotification(msg.split("#")[4]+": "+msg.split("#")[2]+msg.split("#")[1]+"("+msg.split("#")[0]+")下线");
									rdm.setTime(msg.split("#")[4]);
									rdm.setReportData(msg.split("#")[2]+msg.split("#")[1]+"("+msg.split("#")[0]+")下线");
									break;
								}
								dbd.Insert(rdm);
								sendBroad("Intent.ReportData",rdm.getTime()+"#"+rdm.getReportData());
								break;
							case "UPDATA_NOTIFY"://设备数据主动上报
								sendBroad("Intent.UPDATA",result[1]);
								break;
							case "WARNINGMSG"://报警信息上报
								sendBroad("Intent.UPDATA","warningmsg");
								showNotification(result[1].split("/")[2]+"功率超限!");
								break;
							case "warnpush"://warn#分体空调#150721023750#电量已使用0.05kWh,接近定额值:0.2
								showNotification("设备"+result[1].split("#")[2]+result[1].split("#")[3]);
								break;
							case "alarm"://alarm/AlarmType#on(off)#Type#meter#建筑#时间#内容   alarm/missing_data#on#北电分体空调控制器#空调1#系统部#2017-06-01#????? 
								String alarm = result[1].split("/")[1];	
								ReportData_Model rdm1 = new ReportData_Model();
								switch(alarm.split("#")[1]){
								case "on":
									showNotification(alarm.split("#")[5]+": "+alarm.split("#")[4]+alarm.split("#")[3]+"("+alarm.split("#")[2]+")"
											+alarm.split("#")[6]);
									rdm1.setTime(alarm.split("#")[5]);
									rdm1.setReportData(alarm.split("#")[4]+alarm.split("#")[3]+"("+alarm.split("#")[2]+")"
											+alarm.split("#")[6]);
									break;
								case "off":
									showNotification(alarm.split("#")[5]+": "+alarm.split("#")[4]+alarm.split("#")[3]+"("+alarm.split("#")[2]+")"
											+alarm.split("#")[6]);
									rdm1.setTime(alarm.split("#")[5]);
									rdm1.setReportData(alarm.split("#")[4]+alarm.split("#")[3]+"("+alarm.split("#")[2]+")"
											+alarm.split("#")[6]);
									break;
								}
								dbd.Insert(rdm1);
								sendBroad("Intent.ReportData",rdm1.getTime()+"#"+rdm1.getReportData());
								break;
							
							}
							
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}.start();
			
			
		}

		public void sendKeepAlive() throws MqttException {
			log("Sending keep alive");
			// publish to a keep-alive topic
			publishToTopic(MQTT_CLIENT_ID + "/keepalive", 
					mPrefs.getString(PREF_DEVICE_ID, ""));
		}
	}
	
	
	int count=0;
	boolean isServiceRunning=false;
	public void timetesk(){
		Timer mtime = new Timer();
		
		TimerTask timetask = new TimerTask() {
			@Override  
			public void run() {
				// TODO Auto-generated method stub
				//判断推送服务是否在运行，如果不在运行则重新启动;
				ActivityManager manager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
				for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) { 
					System.out.println("service:"+service.service.getClassName());
					if("org.dyd.smart.activity:pushService".equals(service.service.getClassName())) { 
						isServiceRunning = true;
						break;
					} 
				}
				if(!isServiceRunning){
					System.out.println("重启service!");
					actionStart(getApplicationContext());
				}
				actionPing(getApplicationContext());
				count++;
				System.out.println("time:"+count);
			}
		};
		mtime.schedule(timetask,10000,60000);
	}
	
	//接收到来自mqtt推送的消息后发出广播通知；
	private void sendBroad(String action,String str){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra("msg", str);
		sendBroadcast(intent);
	}

}