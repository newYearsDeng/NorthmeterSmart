package com.northmeter.northmetersmart.wxPresenter;


import com.northmeter.northmetersmart.http.HttpRequest;

public class WXRequestHttp implements I_WXRequestHttp{
	I_WXRequestShow requestShow;
	
	public WXRequestHttp(I_WXRequestShow requestShow){
		this.requestShow = requestShow;
	}
	
	@Override
	public void getHttpRequestPost_withWhatMsg(final String url, final String para, final int what) {
		// TODO Auto-generated method stub
		new Thread(){
	    	  public void run(){
	    		  try{
	    			  System.out.println(para);
	    			  String result = HttpRequest.sendPost(url, para);
	    			  System.out.println("result======"+result);
	    			  requestShow.showWXRequestData(what,result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  requestShow.showWXRequestData(what,"exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

	@Override
	public void getHttpRequestGet_withWhatMsg(final String url, final String para, final int what) {
		// TODO Auto-generated method stub
		new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendGet(url, para);
	    			  System.out.println("result======"+result);
	    			  requestShow.showWXRequestData(what,result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  requestShow.showWXRequestData(what,"exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

}
