package com.northmeter.northmetersmart.I;

public interface IRequestInterface {
	public void getHttpRequestPost(String url, String para);
	public void getHttpRequestGet(String url, String para);
	public void getTcp_Help(String para);
	
	/**灯控获取列表时需要登录获取cookie*/
	public void getHttpRequest_LoginSet_Cookie(String url, String para);
	
	public void getHttpRequestGetSet_Cookie(String url, String para, String cookie);
	public void getHttpRequestPostSet_Cookie(String url, String para, String cookie);
}
