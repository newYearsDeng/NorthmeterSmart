package com.northmeter.northmetersmart.I;

public interface IRoomLightDevicePresenter {
	public void getHttpRequestPostSet_Cookie(String url, String para);
	public void getHttpRequestGetSet_Cookie(int xRefreshType, String url, String para, String cookie);
}
