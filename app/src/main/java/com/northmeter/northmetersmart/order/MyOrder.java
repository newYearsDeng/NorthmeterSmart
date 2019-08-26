package com.northmeter.northmetersmart.order;

public class MyOrder {

	private String partner;
	private String eqpt_type;
	private String eqpt_id_code;
	private String eqpt_pwd;
	private String cmd_type;
	private String cmd_id;
	private String cmd_data; // 读数据时可为空

	public MyOrder() {

	}

	public MyOrder(String partner, String eqpt_type, String eqpt_id_code,
			String eqpt_pwd, String cmd_type, String cmd_id, String cmd_data) {
		this.partner = partner;
		this.eqpt_type = eqpt_type;
		this.eqpt_id_code = eqpt_id_code;
		this.eqpt_pwd = eqpt_pwd;
		this.cmd_type = cmd_type;
		this.cmd_id = cmd_id;
		this.cmd_data = cmd_data;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getEqpt_type() {
		return eqpt_type;
	}

	public void setEqpt_type(String eqpt_type) {
		this.eqpt_type = eqpt_type;
	}

	public String getEqpt_id_code() {
		return eqpt_id_code;
	}

	public void setEqpt_id_code(String eqpt_id_code) {
		this.eqpt_id_code = eqpt_id_code;
	}

	public String getEqpt_pwd() {
		return eqpt_pwd;
	}

	public void setEqpt_pwd(String eqpt_pwd) {
		this.eqpt_pwd = eqpt_pwd;
	}

	public String getCmd_type() {
		return cmd_type;
	}

	public void setCmd_type(String cmd_type) {
		this.cmd_type = cmd_type;
	}

	public String getCmd_id() {
		return cmd_id;
	}

	public void setCmd_id(String cmd_id) {
		this.cmd_id = cmd_id;
	}

	public String getCmd_data() {
		return cmd_data;
	}

	public void setCmd_data(String cmd_data) {
		this.cmd_data = cmd_data;
	}
}
