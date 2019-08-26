package com.northmeter.northmetersmart.model;

/**灯控场景model*/
public class RoomLight_TacticsModel{
		public String tacticsId;//策略编号 
		public String tacticsName;//策略名称
		public String buildingId;//建筑id
		public String buildingName;//建筑名称
		public String startDate;//生命周期起始日期
		public String endDate;//声明周期终止日期
		public boolean isEffective;//是否生效
		public int width;
		
		public RoomLight_TacticsModel(){};
		
		public String getTacticsId() {
			return tacticsId;
		}
		public void setTacticsId(String tacticsId) {
			this.tacticsId = tacticsId;
		}
		public String getTacticsName() {
			return tacticsName;
		}
		public void setTacticsName(String tacticsName) {
			this.tacticsName = tacticsName;
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
		public String getStartDate() {
			return startDate;
		}
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public boolean isEffective() {
			return isEffective;
		}
		public void setEffective(boolean isEffective) {
			this.isEffective = isEffective;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}
			
		
	}