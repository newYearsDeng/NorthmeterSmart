package com.northmeter.northmetersmart.order;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.text.format.Time;

import com.northmeter.northmetersmart.http.HttpUtils;

public class OrderManager {

	private String SUPER_SIGN = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

	private static OrderManager uniqueInstance = null;

	private OrderManager() {
	}

	public static OrderManager getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new OrderManager();
		}
		return uniqueInstance;
	}

	/**
	 * 发送命令到中间件系统
	 * 
	 * @param order
	 *            MyOrder对象，存放命令内容
	 * @param url_path
	 *            URL地址
	 * @param encode
	 *            编码类型(UTF-8)
	 * @return String 服务器返回的结果
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") 
	public String sendOrder(MyOrder od, String pswd, String url_path,
			String encode) {
		// 强制使用Internet
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		System.out.println("#######################sending order!");
		// 指令流水号
		// 有一个问题，这里使用的是用户手机的时间
		Time t = new Time();
		t.setToNow();
		String time = String.valueOf(t.year).substring(2, 4)
				+ this.toDoubleDate(t.month + 1)
				+ this.toDoubleDate(t.monthDay) + this.toDoubleDate(t.hour)
				+ this.toDoubleDate(t.minute); // month是从0开始计算的
		String order_no = time + new RandomGUID();
		System.out.println("#######################order_no = " + order_no);

		// objs
		String objs = "[{\"eqpt_type\":\"" + od.getEqpt_type()
				+ "\",\"eqpt_id_code\":\"" + od.getEqpt_id_code()
				+ "\",\"eqpt_pwd\":\"" + od.getEqpt_pwd()
				+ "\",\"cmd_type\":\"" + od.getCmd_type() + "\",\"cmd_id\":\""
				+ od.getCmd_id() + "\",\"cmd_data\":\"" + od.getCmd_data()
				+ "\"}]";
		System.out.println("#######################objs = " + objs);

		// sign
		// Create_direct_opt_by_user create = new Create_direct_opt_by_user();
		// String sign = create.get_sign(objs, pswd);
		String sign = SUPER_SIGN;
		System.out.println("#######################sign = " + sign);

		// order string
		String orderStr = "order_no=" + order_no + "&partner="
				+ od.getPartner() + "&objs=" + objs + "&sign=" + sign;
		System.out.println("#######################orderStr = " + orderStr);

		
		String result = null;
		result = HttpUtils.submitPostData(orderStr, url_path, encode);

		return result;
	}

	/**
	 * 解析返回的命令，得到命令中相应的项
	 * 
	 * @param od
	 *            需要解析的命令
	 * @param item
	 *            需要从命令中取出的项的名称
	 * @param rstLen
	 *            取出项的值的长度，为-1时，自动判断最后一个引号 (")
	 * @return 解析完后，该项的值
	 */
	public String getItemByOrder(String od, String item, int rstLen) {
		String result = "";
		
		// 找到 "result" 在命令中出现的位置
		int start = 0;
		int len = item.length();

		// substring 不包括end位置的字符
		// 会引发溢出的异常????
		while (!od.substring(start, start + len).equals(item))
			++start;

		start = start + len + 3; // "anyItem":" 此后 rstLen 位跟着的是结果
		if (rstLen != -1)
			result = od.substring(start, start + rstLen);
		else {
			// 算出 rstLen，以结尾的引号为标志
			int end = start;
			while (od.charAt(end) != '"')
				++end;
			result = od.substring(start, end);
		}

		return result;
	}

	public String getItemByOrder_(String od, String item, int rstLen) {
		String result = "";

		// 找到 "result" 在命令中出现的位置
		int start = 0;
		int len = item.length();

		// substring 不包括end位置的字符
		// 会引发溢出的异常????
		while (!od.substring(start, start + len).equals(item))
			++start;

		start = start + len + 2; // "anyItem":" 此后 rstLen 位跟着的是结果
		if (rstLen != -1)
			result = od.substring(start, start + rstLen);
		else {
			// 算出 rstLen，以结尾的引号为标志
			int end = start;
			while (od.charAt(end) != ',')
				++end;
			result = od.substring(start, end);
		}

		return result;
	}

	/**
	 * 将单个字符的日期（时间） 转换成两个字符 如：1 -> 01
	 */
	private String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
}
