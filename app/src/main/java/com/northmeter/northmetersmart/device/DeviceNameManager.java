package com.northmeter.northmetersmart.device;

public class DeviceNameManager {
	private static DeviceNameManager uniqueInstance = null;

	private DeviceNameManager() {
	}

	public static DeviceNameManager getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DeviceNameManager();
		}
		return uniqueInstance;
	}

	/**
	 * 根据电器的类型返回 activity名字
	 * 
	 * @param elec_type
	 *            电器类型
	 * @return String 相应的 activity
	 */
	public String getAtyNameByElecType(int elec_type) {
		String rtn = "";
		switch (elec_type) {
		// 电视
		case MyDevice.ELEC_TELEVISION:
			rtn = "TVAty";
			break;

		case MyDevice.ELEC_COMPUTER:
			rtn = "TVAty";
			break;

		case MyDevice.ELEC_LIGHT:
			rtn = "TVAty";
			break;

		case MyDevice.ELEC_OTHERS:
			rtn = "TVAty";
			break;

		default:
			rtn = "TVAty";
			break;
		}

		return rtn;
	}
}
