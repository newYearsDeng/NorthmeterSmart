package com.northmeter.northmetersmart.wxPresenter;

public interface I_WXRequestHttp {
	/**带msg.what的请求*/
	public void getHttpRequestPost_withWhatMsg(String url, String para, int what);
	public void getHttpRequestGet_withWhatMsg(String url, String para, int what);
}
