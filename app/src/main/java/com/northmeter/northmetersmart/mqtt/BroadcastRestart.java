package com.northmeter.northmetersmart.mqtt;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 监听系统广播启动service*/
public class BroadcastRestart extends BroadcastReceiver{
	private boolean isServiceRunning = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("收到的广播intent.getAction():"+intent.getAction());
		if(intent.getAction().equals("Intent.ACTION_CLOSE_SYSTEM_DIALOGS")){
			ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) { 
				System.out.println("service:"+service.service.getClassName());
				if("org.mh.smart.mqtt.PushService".equals(service.service.getClassName())) { 
					isServiceRunning = true; 
				} 
			}
			if(!isServiceRunning){
				//重新启动
				System.out.println("重新启动service");
				PushService.actionStart(context);
			}
		
		}
	}

		
}
