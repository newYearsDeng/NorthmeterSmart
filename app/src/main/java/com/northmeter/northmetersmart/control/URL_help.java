package com.northmeter.northmetersmart.control;

import com.northmeter.northmetersmart.activity_build.RoomModel;
import com.northmeter.northmetersmart.model.Main_GvModel;

import java.util.ArrayList;
import java.util.List;



/**获取自定义保存的url地址进行传递到下一个界面*/
public class URL_help {

	private static URL_help uniqueInstance=null;
	public static URL_help getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new URL_help();
		}
		return uniqueInstance;
	}
	private String url_address;//http地址
	private String root_name;
	private String emqtt_address;//mqtt地址
	private String user_address;//账户系统地址
	private String tcp_address;//tcp地址
	private long tab_time;//选项卡时间，根据时间确认是否加载数据；
	private List<RoomModel> RoomModels = new ArrayList<RoomModel>();//这里先初始化list，否者访客房间生成获取list时会出异常
	private List<Main_GvModel> Main_GvModels = new ArrayList<Main_GvModel>();//存储mainactivity的界面图标model，主要是存储建筑权限用于房间中判断
																			//房间里面是管理员而在整个楼里面是观察之类的；
	
	private String Set_Cookie;//登录灯控获取列表需要的http请求头文件
	
	
	

	public static URL_help getUniqueInstance() {
		return uniqueInstance;
	}
	public static void setUniqueInstance(URL_help uniqueInstance) {
		URL_help.uniqueInstance = uniqueInstance;
	}
	public String getRoot_name() {
		return root_name;
	}
	public void setRoot_name(String root_name) {
		this.root_name = root_name;
	}
	public String getUrl_address() {
		return url_address;
	}
	public void setUrl_address(String url_address) {
		this.url_address = url_address;
	}
	
	public String getEmqtt_address() {
		return emqtt_address;
	}
	public void setEmqtt_address(String emqtt_address) {
		this.emqtt_address = emqtt_address;
	}
	
	
	public String getUser_address() {
		return user_address;
	}
	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}
	
	
	public String getTcp_address() {
		return tcp_address;
	}
	public void setTcp_address(String tcp_address) {
		this.tcp_address = tcp_address;
	}
	public List<RoomModel> getRoomModels() {
		return RoomModels;
	}
	public void setRoomModels(List<RoomModel> roomModels) {
		RoomModels = roomModels;
	}
	
	public List<Main_GvModel> getMain_GvModels() {
		return Main_GvModels;
	}
	public void setMain_GvModels(List<Main_GvModel> main_GvModels) {
		Main_GvModels = main_GvModels;
	}
	public long getTab_time() {
		return tab_time;
	}
	public void setTab_time(long tab_time) {
		this.tab_time = tab_time;
	}
	
	public String getSet_Cookie() {
		return Set_Cookie;
	}
	public void setSet_Cookie(String set_Cookie) {
		Set_Cookie = set_Cookie;
	}
	private URL_help(){};
}
