package com.northmeter.northmetersmart.activity_build.I;


import com.northmeter.northmetersmart.http.HttpRequest;

public class RoomLightDevicePresenter implements IRoomLightDevicePresenter{
	ILightDevieShow iLightDevieShow;
	
	public RoomLightDevicePresenter(ILightDevieShow iLightDevieShow){
		this.iLightDevieShow = iLightDevieShow;
	}

	@Override
	public void getHttpRequestPostSet_Cookie(final String url, final String para) {
		// TODO Auto-generated method stub
		new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendPost_getSet_Cookie(url, para);
	    			  System.out.println("result======"+result);
	    			  iLightDevieShow.showLightDevice(0, result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  iLightDevieShow.showLightDevice(0, "exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

	@Override
	public void getHttpRequestGetSet_Cookie(final int xRefreshType,final String url, final String para,
			final String cookie) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendGet2(url, para,cookie);
	    			  System.out.println("result======"+result);
	    			  iLightDevieShow.showLightDevice(xRefreshType, result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  iLightDevieShow.showLightDevice(xRefreshType, "exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

}
