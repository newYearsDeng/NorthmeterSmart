package com.northmeter.northmetersmart.order;
/**
 * 中央空调上报数据解析
 * 	02 41 01 00 (电能)
	00 00 00 （功率）
	70 02 （环境温度）
	01（开关） 
	00 20（设定温度及模式）
	03（风机档位）
	37 28 01 00
	21 82 00 00
	81 84 03 00
	85 77 00 05（剩余金额）
	00 00 00 （状态字）
	19 14 13 10 16（时间）
 * */
public class MyResultCentral {

	// 各个参数的位数
	private int[] dataLen = { 8, 6, 4, 2, 4, 2, 8, 8, 8, 8, 6, 10};
	/**
	 * 以下均为原始返回数据，要通过 get 方法处理才能得到正确的数据
	 */
	/**电能  8*/
	private String zdn;
	/**功率  6*/
	private String gl;
	/**环境温度  4*/
	private String hjwd;
	/**开关状态  2*/
	private String kgzt;
	/**设定温度及模式  4*/
	private String sdwdjms;
	/**风机档位  2*/
	private String fsdw;
	/**空调低速使用时长  8*/
	private String low_speed_time;
	/**空调中速使用时长  8*/
	private String middle_speed_time;
	/**空调高速使用时长  8*/
	private String high_speed_time;
	/**剩余金额  8*/
	private String syje;
	/**状态字  6*/
	private String ztz;
	/**时间  10*/
	private String sj;

	public MyResultCentral() {

	}

	public MyResultCentral(String result) {
		int i = 0;
		int start = 0;
		int end = dataLen[i];

		// 总电能 8
		zdn = result.substring(start, end);
		// 功率 6
		start = end;
		end += dataLen[++i];
		gl = result.substring(start, end);
		// 环境温度 4
		start = end;
		end += dataLen[++i];
		hjwd = result.substring(start, end);
		// 开关状态 2
		start = end;
		end += dataLen[++i];
		kgzt = result.substring(start, end);
		// 设定温度及模式  4
		start = end;
		end += dataLen[++i];
		sdwdjms = result.substring(start, end);
		// 风机档位  2
		start = end;
		end += dataLen[++i];
		fsdw = result.substring(start, end);
		// 空调低速使用时长  8
		start = end;
		end += dataLen[++i];
		low_speed_time = result.substring(start, end);
		// 空调中速使用时长  8
		start = end;
		end += dataLen[++i];
		middle_speed_time = result.substring(start, end);
		
		// 空调高速使用时长  8
		start = end;
		end += dataLen[++i];
		high_speed_time = result.substring(start, end);
		// 剩余金额  8
		start = end;
		end += dataLen[++i];
		syje = result.substring(start, end);
		
		// 状态字  6
		start = end;
		end += dataLen[++i];
		ztz = result.substring(start, end);
		
		//时间  10
		start = end;
		end += dataLen[++i];
		sj = result.substring(start, end);
	}

	/**
	 * 对数据进行两位两位的反转 如：01 20 -> 20 01
	 * 
	 * @param data
	 *            需要反转的数据
	 * @return String 反转后的数据
	 */
	public String inverseData(String data) {
		String rtn = "";
		int start = 0;

		int len = data.length();
		String next = data.substring(start, start + 2);
		while (next.length() > 0) {
			rtn = next + rtn;

			start += 2;
			if (start < len)
				next = data.substring(start, start + 2);
			else
				break;
		}

		return rtn;
	}

	// 总电能 8
	public String getZdn() {
		String rtn = "";
		rtn = inverseData(zdn);
		rtn = rtn.substring(0, 6) + "." + rtn.substring(6, 8);
		return rtn;
	}

	// 功率 6
	public String getGl() {
		String rtn = "";
		rtn = inverseData(gl);
		rtn = rtn.substring(0, 5) + "." + rtn.substring(5, 6);
		return rtn;
	}

	// 环境温度 4
	public String getHjwd() {
		String rtn = "";
		rtn = inverseData(hjwd);
		rtn = rtn.substring(0, 3) + "." + rtn.substring(3, 4);
		return rtn;
	}

	// 开关状态 2
	public String getKgzt() {
		String rtn = "";
		rtn = inverseData(kgzt);
		return rtn;
	}

	// 设定温度及模式  4
	public String getSdwdjms() {
		String rtn = "";
		rtn = inverseData(sdwdjms);
		return rtn;
	}

	// 风机档位  2
	public String getFsdw() {
		String rtn = "";
		rtn = inverseData(fsdw);
		return rtn;
	}

	// 空调低速使用时长(单位：分钟)  8
	public String getLow_speed_time() {
		String rtn = "";
		rtn = inverseData(low_speed_time);
		return rtn;
	}
	// 空调中速使用时长(单位：分钟)  8
	public String getMiddle_speed_time() {
		String rtn = "";
		rtn = inverseData(middle_speed_time);
		return rtn;
	}
	// 空调高速使用时长 (单位：分钟)  8
	public String getHigh_speed_time() {
		String rtn = "";
		rtn = inverseData(high_speed_time);
		return rtn;
	}
	//剩余金额 (单位：元) 8
	public String getSyje() {
		String rtn = "";
		rtn = inverseData(syje);
		switch(rtn.substring(0,1)){
		case "8":
			rtn = "-"+rtn.substring(1, 6) + "." + rtn.substring(6, 8);
			break;
		case "9":
			rtn = "-1"+rtn.substring(1, 6) + "." + rtn.substring(6, 8);
			break;
			default:
				rtn = rtn.substring(0, 6) + "." + rtn.substring(6, 8);
				break;
		
		}
		return rtn;
	}

	// 状态字  6
	public String getZtz() {
		String rtn = "";
		rtn = inverseData(ztz);
		return rtn;
	}
	// 时间  10
	public String getSj() {
		String rtn = "";
		rtn = inverseData(sj);
		return rtn;
	}
}
