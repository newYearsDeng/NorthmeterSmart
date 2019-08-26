package com.northmeter.northmetersmart.model;

/**灯控单灯model*/
public class RoomLight_DeviceModel{

	public String eqptIdCode;//	设备ID 
	public String eqptName;//设备名称
	public String eqptType;//设备类型代码
	public String eqptTypeName;//设备类型名称
	public String eqptBuildingId;//建筑ID
	public String eqptBuildingName;//建筑名称
	public String eqptShortNum;//设备短号
	public String eqptStatus;//设备档案状态
	public String commport;//通讯端口号
	public String baudrate;//波特率
	public String collectorId;//上行采集设备
	public String collectorType;//采集设备类型代码
	public String label;//标签
	public String tab;//能源类型
	public String createTime;//创建时间
	public int width;
	
	public RoomLight_DeviceModel(){}

	public String getEqptIdCode() {
		return eqptIdCode;
	}

	public void setEqptIdCode(String eqptIdCode) {
		this.eqptIdCode = eqptIdCode;
	}

	public String getEqptName() {
		return eqptName;
	}

	public void setEqptName(String eqptName) {
		this.eqptName = eqptName;
	}

	public String getEqptType() {
		return eqptType;
	}

	public void setEqptType(String eqptType) {
		this.eqptType = eqptType;
	}

	public String getEqptTypeName() {
		return eqptTypeName;
	}

	public void setEqptTypeName(String eqptTypeName) {
		this.eqptTypeName = eqptTypeName;
	}

	public String getEqptBuildingId() {
		return eqptBuildingId;
	}

	public void setEqptBuildingId(String eqptBuildingId) {
		this.eqptBuildingId = eqptBuildingId;
	}

	public String getEqptBuildingName() {
		return eqptBuildingName;
	}

	public void setEqptBuildingName(String eqptBuildingName) {
		this.eqptBuildingName = eqptBuildingName;
	}

	public String getEqptShortNum() {
		return eqptShortNum;
	}

	public void setEqptShortNum(String eqptShortNum) {
		this.eqptShortNum = eqptShortNum;
	}

	public String getEqptStatus() {
		return eqptStatus;
	}

	public void setEqptStatus(String eqptStatus) {
		this.eqptStatus = eqptStatus;
	}

	public String getCommport() {
		return commport;
	}

	public void setCommport(String commport) {
		this.commport = commport;
	}

	public String getBaudrate() {
		return baudrate;
	}

	public void setBaudrate(String baudrate) {
		this.baudrate = baudrate;
	}

	public String getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(String collectorId) {
		this.collectorId = collectorId;
	}

	public String getCollectorType() {
		return collectorType;
	}

	public void setCollectorType(String collectorType) {
		this.collectorType = collectorType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	};
	
	
	
}