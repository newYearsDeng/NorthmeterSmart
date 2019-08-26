package com.northmeter.northmetersmart.service;

import java.util.List;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Time;

import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDiagramData;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

public class ReadService extends Service {
	private IBinder binder = new LocalBinder();
	private int fixedtime;
	private int READ_TIME = 300; // 5分钟读取一次数据
	private String URL_PATH;
	@Override
	public void onCreate() {
		System.out.println("onCreate");
		// 这里可以启动媒体播放器
		super.onCreate();
		SharedPreferences sp = getSharedPreferences("URL_ADD", MODE_PRIVATE);
		URL_PATH = sp.getString("URL_ADD", "http://218.17.157.121:7001/Action");
		new Thread1().start();
	}

	class Thread1 extends Thread {

		@Override
		public void run() {
			super.run();
			int i = 0;
			while (true) {
				try {
					System.out.println(i++);
					Thread.sleep(1000);
					SharedPreferences spd=getSharedPreferences("TimeSet", MODE_PRIVATE);
					String time=spd.getString("TimeSet", "10");
					if(time.equals("")){
						time="10";
					}
					int ReadTime=Integer.parseInt(time)*60;
					if(i>ReadTime){
						i=0;
					}
					//System.out.println(ReadTime+"时间间隔===================================================");
					if (i == ReadTime) {
						i = 0;
						System.out.println("拉取数据！");
						sendReadOrder();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sendReadOrder() {
	try{
		DBDevice db_d = new DBDevice(this);
		List<MyDevice> devices = db_d.GetMyDevices();
		if (!devices.isEmpty()) {

			DBDiagramData db_diagd = new DBDiagramData(this);

			for (int i = 0; i < devices.size(); i++) {
				try{
				MyDevice md = devices.get(i);

				// POST读取一次信息
				MyOrder odToSend;
				switch (md.getType()) {
				// 智能插座
				case MyDevice.DEVICE_WIFI_SMART_SOCKET://wifi插座
					// 根据设备类型，获取需要发送的命令
					odToSend = OrderList.getOrderByDeviceType(Type_Entity.Wifi_socket,
							md.getTableNum(), OrderList.ORDER_SOCKET_READ);
					break;
				case MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY://zigbee网关
					odToSend = OrderList.getSendByDeviceType(Type_Entity.Gateway_type,
							md.getTableNum(), OrderList.READ_SOCKET_RECORD,"");
					break;  
				case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET://zigbee插座
					odToSend = OrderList.getSendByDeviceType(Type_Entity.Socket_type,
							md.getTableNum(), OrderList.READ_ZIGBEE_SHUJU,"");
					break;
				case MyDevice.DEVICE_ZIGBEE_SMART_AIR://zigbee空调控制器
					odToSend = OrderList.getSendByDeviceType(Type_Entity.Split_air_conditioning,
							md.getTableNum(), OrderList.READ_ZIGBEE_AIR_SHUJU,"");
					break;
				case MyDevice.DEVICE_CENTER_SMART_AIR://zigbee中央空调控制器
					odToSend = OrderList.getSendByDeviceType(Type_Entity.Central_air_conditioning,
							md.getTableNum(), OrderList.READ_ZIGBEE_AIR_SHUJU,"");	
					break;

				default:
					odToSend = null;
					break;
				}			
				// 发送命令并得到返回的结果
				String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
						OrderList.USER_PASSWORD, URL_PATH, "UTF-8");

				// 服务器返回数据成功
				if (rst_raw != null) {
					//判断是否返回正确的信息
					String rst_error = OrderManager.getInstance().getItemByOrder(
							rst_raw, "msg", -1);
					if(rst_error.equals("没有找到这个采集器")){
						continue;
					}
					// 解析返回的结果
					String rst = OrderManager.getInstance().getItemByOrder(
							rst_raw, "result", -1);
					// 将结果存到MyResult对象中
					
					//MyResult myResult = new MyResult(rst);
					
					// 获取当前时间，并生成格式为 xxxx-xx-xx xx:xx:xx 的 String  this.toDoubleDate
					Time t = new Time();
					t.setToNow();
					String timeNow = String.valueOf(t.year) + "-"
							+  this.toDoubleDate(t.month + 1) + "-"
							+  this.toDoubleDate(t.monthDay) + " "
							+  this.toDoubleDate(t.hour) + ":"
							+  this.toDoubleDate(t.minute) + ":"
							+  this.toDoubleDate(t.second); // month是从0开始计算的
					

					// 将获取的数据插入到 MyDiagramData 表中
					MyDiagramData dd = new MyDiagramData(md.getMac(), timeNow,
							rst.split(",")[0],rst.split(",")[3],"");
					db_diagd.Insert(dd);

					System.out.println("insert result: mac = " + dd.getMac()
							+ " timeNow = " + dd.getTimeNow() + "zdn = "
							+ dd.getZdn()+" gl"+dd.getGl());

				} else{
					continue;
				}
			}catch(Exception e){
					continue;			}
					; // 服务器返回数据失败
			} // end for
		} else
			; // MyDevice 表为空
		}catch(Exception e){
			e.printStackTrace();
			}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class LocalBinder extends Binder {
		ReadService getService() {
			return ReadService.this;
		}
	}

	@Override
	public void onDestroy() {
		System.out.println("onDestroy");
		super.onDestroy();
	}

	/**
	 * 将单个字符的日期（时间） 转换成两个字符 如：1 -> 01
	 */
	private String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
}
