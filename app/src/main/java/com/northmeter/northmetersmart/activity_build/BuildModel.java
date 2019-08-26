package com.northmeter.northmetersmart.activity_build;

/**楼宇关系model*/
public class BuildModel {

	public String build_id;
	public String build_name;
	public String getBuild_id() {
		return build_id;
	}
	public void setBuild_id(String build_id) {
		this.build_id = build_id;
	}
	public String getBuild_name() {
		return build_name;
	}
	public void setBuild_name(String build_name) {
		this.build_name = build_name;
	}
	
	
	public BuildModel(){
		
	};
	
	public BuildModel(String build_id,String build_name){
		this.build_id = build_id;
		this.build_name = build_name;
	}
}
