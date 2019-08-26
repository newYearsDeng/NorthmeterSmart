package com.northmeter.northmetersmart.work;


import android.app.Application;


/**
 * 
 * The Class WApplication.
 * 
 * Application类
 * 
 * @author Lien
 */
public class XpgApplication extends Application {

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	public void onCreate() {
		super.onCreate();
//		CrashHandler crashHandler = CrashHandler.getInstance();  
//        // 注册crashHandler  
//        crashHandler.init(getApplicationContext());  
//        // 发送以前没发送的报告(可选)  
//        crashHandler.sendPreviousReportsToServer();  
		
//		PushAgent mPushAgent = PushAgent.getInstance(this);
//		//注册推送服务，每次调用register方法都会回调该接口
//		mPushAgent.register(new IUmengRegisterCallback() {
//
//		    @Override
//		    public void onSuccess(String deviceToken) {
//		        //注册成功会返回device token
//		    }
//
//		    @Override
//		    public void onFailure(String s, String s1) {
//
//		    }
//		});
	}
}
