package com.northmeter.northmetersmart.activity_build.I;


import com.northmeter.northmetersmart.http.HttpRequest;
import com.northmeter.northmetersmart.http.Tcp_Help;

public class RequestInterface implements IRequestInterface{
	IRequestShow IRequestShow;
	
	public RequestInterface(IRequestShow IRequestShow){
		this.IRequestShow = IRequestShow;
	}

	@Override
	public void getHttpRequestPost(final String url, final String para) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  System.out.println(para);
	    			  String result = HttpRequest.sendPost(url, para);
	    			  System.out.println("result======"+result);
	    			  IRequestShow.requestShow(result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  IRequestShow.requestShow("exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

	@Override
	public void getHttpRequestGet(final String url, final String para) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendGet(url, para);
	    			  System.out.println("result======"+result);
	    			  IRequestShow.requestShow(result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  IRequestShow.requestShow("exception");
	    		  }
	    		 
	    	  }
	      }.start();
		
	}

	@Override
	public void getTcp_Help(final String para ) {
		// TODO Auto-generated method stub
		 new Thread(){
	   	  public void run(){
	   		  try{
	   			  String result = Tcp_Help.getTcpMsg(para);
	   			  System.out.println("result======"+result);
	   			  IRequestShow.requestShow(result);
	   		  }catch(Exception e){
	   			  e.printStackTrace();
	   			  IRequestShow.requestShow("exception");
	   		  }
	   		 
	   	  }
	     }.start();
	}

	
	@Override
	public void getHttpRequest_LoginSet_Cookie(final String url, final String para) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendPost_getSet_Cookie(url, para);
	    			  System.out.println("result======"+result);
	    			  IRequestShow.requestShow(result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  IRequestShow.requestShow("exception");
	    		  }
	    		 
	    	  }
	      }.start();
		
	}

	@Override
	public void getHttpRequestGetSet_Cookie(final String url, final String para ,final String cookie) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendGet2(url, para,cookie);
	    			  System.out.println("result======"+result);
	    			  IRequestShow.requestShow(result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  IRequestShow.requestShow("exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

	@Override
	public void getHttpRequestPostSet_Cookie(final String url, final String para,final String cookie) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendPost2(url, para, cookie);
	    			  System.out.println("result======"+result);
	    			  IRequestShow.requestShow(result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  IRequestShow.requestShow("exception");
	    		  }
	    		 
	    	  }
	      }.start();
	}

}
