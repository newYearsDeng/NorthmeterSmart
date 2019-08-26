package com.northmeter.northmetersmart.model;
/*udp报文数据*/
public class UDP_model {
	private String ischeck;
	private String head;//报文头
	private String terminal_add;//终端地址
	private String master_IP_port;//主站IP地址和端口
	private String terminal_IP_mask_gateway;//终端IP地址、掩码、网关
	private String terminal_time;//終端时间
	private String passmode_onoff;//透传模式开关
	private String device_name;//集中器设备名称
	private String version;//软件版本，版本时间，硬件版本 
	private String point_capacity;//測量點容量
	private String TCP_connection_state;//TCP连接主站状态
	
	public UDP_model( String ischeck,String head,String terminal_add,String master_IP_port,
	 String terminal_IP_mask_gateway,String terminal_time,String passmode_onoff,String device_name,
	 String version,String point_capacity,String TCP_connection_state) {
		super();
		this.ischeck = ischeck;
		this.head = head;
		this.terminal_add = terminal_add;
		this.master_IP_port = master_IP_port;
		this.terminal_IP_mask_gateway = terminal_IP_mask_gateway;
		this.terminal_time = terminal_time;
		this.passmode_onoff = passmode_onoff;
		this.device_name = device_name;
		this.version = version;
		this.point_capacity = point_capacity;
		this.TCP_connection_state = TCP_connection_state;
	}
	public UDP_model() {
			super();	
			}
	
	public String getIscheck() {
		return ischeck;
	}
	public void setIscheck(String ischeck) {
		this.ischeck = ischeck;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getTerminal_add() {
		return terminal_add;
	}
	public void setTerminal_add(String terminal_add) {
		this.terminal_add = terminal_add;
	}
	public String getMaster_IP_port() {
		return master_IP_port;
	}
	public void setMaster_IP_port(String master_IP_port) {
		this.master_IP_port = master_IP_port;
	}
	public String getTerminal_IP_mask_gateway() {
		return terminal_IP_mask_gateway;
	}
	public void setTerminal_IP_mask_gateway(String terminal_IP_mask_gateway) {
		this.terminal_IP_mask_gateway = terminal_IP_mask_gateway;
	}
	public String getTerminal_time() {
		return terminal_time;
	}
	public void setTerminal_time(String terminal_time) {
		this.terminal_time = terminal_time;
	}
	public String getPassmode_onoff() {
		return passmode_onoff;
	}
	public void setPassmode_onoff(String passmode_onoff) {
		this.passmode_onoff = passmode_onoff;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPoint_capacity() {
		return point_capacity;
	}
	public void setPoint_capacity(String point_capacity) {
		this.point_capacity = point_capacity;
	}
	public String getTCP_connection_state() {
		return TCP_connection_state;
	}
	public void setTCP_connection_state(String tCP_connection_state) {
		TCP_connection_state = tCP_connection_state;
	}
	
	
}
