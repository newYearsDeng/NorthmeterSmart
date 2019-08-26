package com.northmeter.northmetersmart.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.northmeter.northmetersmart.db.DBDevice;
import com.xlwtech.util.XlwDevice;


public class DeviceManager {

	private static DeviceManager uniqueInstance = null;
	private Context context;

	private List<MyDevice> mDeviceList;
	// private List<MyDevice> mNewDeviceList; // smartconfig时 一次只返回一个设备
	private MyDevice tempDevice;
	public Boolean isFoundDevice;

	DBDevice db;

	private DeviceManager() {
	}

	public static DeviceManager getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DeviceManager();
		}
		return uniqueInstance;
	}

	/**
	 * 初始化设备数据：读取本地数据，将设备信息加到 mDeviceList 中
	 */
	public boolean init(Context c) {

		this.context = c;

		mDeviceList = new ArrayList<MyDevice>();
		// mNewDeviceList = new ArrayList<MyDevice>();
		tempDevice = new MyDevice();
		isFoundDevice = false;

		// 初始化数据库=====================================================
		db = new DBDevice(c);
		mDeviceList = db.GetMyDevices();

		// 初始化 XLW 的监听 =====================================================
		// OUR OUI EUI-48Address Block 78-9C-E7 00-00-00 through FF-FF-FF
		// 78-9C-E7 is the first 3 byte of our mac address, it is register
		// in
		// IEEE
		XlwDevice.getInstance().SetXlwDeviceListener(new XlwDevice.XlwDeviceListener() {
			@Override
			
			public boolean onSmartFound(String mac, String ip, String version,
					String capability) {
				// 搜索到设备时，将设备的mac和ip存储到 tempDevice中
				tempDevice.setMac(mac);
				tempDevice.setIp(ip);
				isFoundDevice = true;

				XlwDevice.getInstance().SmartConfigStop();

				return true;
			}
			
			@Override
			public void onReceive(String mac, byte[] data, int length) {
				String rsp = new String(data, 0, length);

			}

			@Override
			public void onSendError(String mac, int sn, int err) {

				switch (err) {
				case XlwDevice.ERR_BUSY:

					break;

				case XlwDevice.ERR_TIMER_OUT:

					break;

				case XlwDevice.ERR_MAC_INVALID:

					break;

				case XlwDevice.ERR_DEVICE_OFFLINE:

					break;

				default:
					break;
				}
			}

			@Override
			public boolean onSearchFound(String arg0, String arg1, String arg2,
					String arg3, String arg4) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onStatusChange(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

		}); // XLW DeviceListener End

		//XlwDevice.getInstance().SetUerId("18665891976");
		//XlwDevice.getInstance().SetTimeZone(8);
		MySetTimer();
		return true;
	}
	
	  private Timer myTimer;    
	    private void MySetTimer()
	    {
	    	myTimer = new Timer();
	    	myTimer.schedule( 
					new TimerTask() 
					{
						@Override
						public void run() 
						{	

							 XlwDevice.getInstance().SmartConfigProgressGet();
						}
					} , 0, 1000);    		
	    }

	public String getPhoneSsid() {
		boolean isClosed = false;
		boolean noName = false;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled() == false) {
			Toast.makeText(context, "wifi已关闭", Toast.LENGTH_SHORT).show();
			isClosed = true;
		}

		if (!isClosed) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String ssidNow = wifiInfo.getSSID();
			String strHead = ssidNow.substring(0, 1);
			if ((ssidNow == null) || (ssidNow.length() <= 0)) {
				Toast.makeText(context, "无法得到wifi名称", Toast.LENGTH_SHORT)
						.show();
				noName = true;
			}

			if (!noName) {
				if (strHead.equals("\"") == true)
					ssidNow = ssidNow.substring(1, ssidNow.length() - 1);
				return ssidNow;
			}
		}

		return null;
	}

	/**
	 * 用于对 tempDevice 的设置 因为每次配置都是顺序执行 所以每次完成时都能保证 tempDevice 完整初始化
	 */
	public MyDevice getTempDevice() {
		return tempDevice;
	}

	/**
	 * 开始一键配置wifi
	 */
	public void doSmartCongfigStart(String ssid, String pswd) {
		XlwDevice.getInstance().SmartConfigStart(ssid, pswd, 120000);
	}

	/**
	 * 停止一键配置wifi
	 */
	public void doSmartConfigStop() {
		XlwDevice.getInstance().SmartConfigStop();
	}

	/**
	 * 清除设备信息
	 */
	public void doDeviceClear() {
		XlwDevice.getInstance().DeviceClear();
	}

	/**
	 * 搜索附近的设备：搜索到的设备添将会通过 onDeviceFound 方法，被添加到 mNewDeviceList 中
	 */
	// public void searchDevice() {
	// XlwDevice.getInstance().DeviceSearch(5000);
	// }

	/**
	 * 更新设备数据：将tempDevice的数据存到mDeviceList中，并修改isFound=false
	 */
	public void update(Context c) {

		// 将 tempDevice 添加到 mDeviceList 中
		mDeviceList.add(tempDevice);

		// 将 tempDevice 保存到数据库
		db.Insert(tempDevice);

		// 保存完毕，更新isFound=false，无需清除 tempDevice 的数据，因为每次对 tempDevice 的设置都是顺序执行的
		isFoundDevice = false;
	}
	/**
	 * 更新已有设备数据：将tempDevice的数据存到mDeviceList中，并修改isFound=false
	 */
	public void upDataOld(Context c) {

		// 将 tempDevice 添加到 mDeviceList 中
		mDeviceList.add(tempDevice);

		// 将 tempDevice 保存到数据库
		db.Update(tempDevice);

		// 保存完毕，更新isFound=false，无需清除 tempDevice 的数据，因为每次对 tempDevice 的设置都是顺序执行的
		isFoundDevice = false;
	}
}
