package com.northmeter.northmetersmart.model;
/*保存udp数据用于传递到下一个界面*/
public class UDP_model_trance {
	private static UDP_model_trance uniqueInstance = null;
	public static UDP_model_trance getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new UDP_model_trance();
		}
		return uniqueInstance;
	}
	private UDP_model model;
	public UDP_model getModel() {
		return model;
	}
	public void setModel(UDP_model model) {
		this.model = model;
	}
	public UDP_model_trance(){
		
	};
}
