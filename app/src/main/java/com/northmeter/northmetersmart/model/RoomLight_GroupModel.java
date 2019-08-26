package com.northmeter.northmetersmart.model;

/**场景群组model*/
public class RoomLight_GroupModel {

	public boolean isCheck;
	public String id;//自增长标记符 
	public String groupId;//群组编号
	public String groupName;//群组名称
	public String buildingId;//所属建筑ID
	public String buildingName;//所属建筑名称 
	public String createTime;//创建时间 
	public boolean getCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public RoomLight_GroupModel(){};
	
}
