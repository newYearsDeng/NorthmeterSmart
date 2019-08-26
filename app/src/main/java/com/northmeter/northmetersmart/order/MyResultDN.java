package com.northmeter.northmetersmart.order;

public class MyResultDN {

	// 各个参数的位数
	private int[] dataLen = { 6, 6, 8, 4 };

	/**
	 * 以下均为原始返回数据，要通过 get 方法处理才能得到正确的数据
	 */
		// 功率下限 6
		private String glxx;
		// 功率上限 6
		private String glsx;
		// 电量上限 8
		private String dlsx;
		// 跳合闸延时时间 4
		private String tzyssj;

	public MyResultDN() {

	}

	public MyResultDN(String result) {
		int i = 0;
		int start = 0;
		int end = dataLen[i];

		// 功率下限 6
		glxx = result.substring(start, end);
		// 功率下限 6
		start = end;
		end += dataLen[++i];
		glsx = result.substring(start, end);
		// 电量上限 8
		start = end;
		end += dataLen[++i];
		dlsx = result.substring(start, end);
		// 跳合闸延时时间 4
		start = end;
		end += dataLen[++i];
		tzyssj = result.substring(start, end);
		
	}

	/**
	 * 对数据进行两位两位的反转 如：01 20 -> 20 01
	 * 
	 * @param data
	 *            需要反转的数据
	 * @return String 反转后的数据
	 */
	public static String inverseData(String data) {
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

	// 功率下限 8
	public String getGlxx() {
		String rtn = "";
		rtn = inverseData(glxx);
		rtn = rtn.substring(0, 2) + "." + rtn.substring(2, 6);
		return rtn;
	}

	// 功率上限 4
	public String getGlsx() {
		String rtn = "";
		rtn = inverseData(glsx);
		rtn = rtn.substring(0, 2) + "." + rtn.substring(2, 6);
		return rtn;
	}

	// 电量上限 6
	public String getDlsx() {
		String rtn = "";
		rtn = inverseData(dlsx);
		rtn = rtn.substring(0, 6) + "." + rtn.substring(6, 8);
		return rtn;
	}

	// 跳合闸延时时间 4
	public String getTzyssj() {
		String rtn = "";
		rtn = inverseData(tzyssj);
		return rtn;
	}

	
}
