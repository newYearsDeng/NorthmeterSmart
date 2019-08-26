package com.northmeter.northmetersmart.I;


import com.northmeter.northmetersmart.http.HttpRequest;

public class RequestInterfaceSet implements IRequestInterfaceSet{
	IRequestSet iRequestSet;
	
	public RequestInterfaceSet(IRequestSet iRequestSet){
		this.iRequestSet = iRequestSet;
	}

	@Override
	public void getHttpRequestPostSet_Cookie(final String url, final String para,
			final String cookie) {
		// TODO Auto-generated method stub
		 new Thread(){
	    	  public void run(){
	    		  try{
	    			  String result = HttpRequest.sendPost2(url, para, cookie);
	    			  System.out.println("result======"+result);
	    			  iRequestSet.showRequestSet(result);
	    		  }catch(Exception e){
	    			  e.printStackTrace();
	    			  iRequestSet.showRequestSet("exception");
	    		  }
	    		 
	    	  }
	      }.start();
		
	}

}
