package com.northmeter.northmetersmart.activity_build;

/**
 * 显示房间设备界面的model*/
public class RoomModel {
	//0001000100010015/115#0a0001aa7k#150721023211#0a0003ahup#5a4ce2e221e7;
	//0001000100010001/150721023218#0a0001aa7k#150721023218#0a0003ahup#5a4ce2e221e7;
	/**id*/
	public String id;
	/**名字*/
	public String name;
	/**类型*/
	public String type;
	/**表号*/
	public String tableNum;
	/**主节点类型*/
	public String masternode_type;
	/**主节点表号*/
	public String masternode_num;
	/**图片资源*/
	public int resource;
	/**图标宽度*/
	public int width;
	
	
	
	
	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTableNum() {
		return tableNum;
	}

	public void setTableNum(String tableNum) {
		this.tableNum = tableNum;
	}

	public String getMasternode_type() {
		return masternode_type;
	}

	public void setMasternode_type(String masternode_type) {
		this.masternode_type = masternode_type;
	}

	public String getMasternode_num() {
		return masternode_num;
	}

	public void setMasternode_num(String masternode_num) {
		this.masternode_num = masternode_num;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public RoomModel(String id, String name, String type, String tableNum, String masternode_type, String masternode_num,int resource, int width){
		this.id = id;
		this.name = name;
		this.type = type;
		this.tableNum = tableNum;
		this.masternode_type = masternode_type;
		this.masternode_num = masternode_num;
		this.resource = resource;
		this.width = width;
	}
	
	public RoomModel(){};
	
}
