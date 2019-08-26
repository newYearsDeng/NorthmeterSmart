package com.northmeter.northmetersmart.helper;

import java.util.Calendar;

public class TimeHelper {
	public static Calendar d = Calendar.getInstance();
	public static int year = d.get(Calendar.YEAR);
	public static int month = d.get(Calendar.MONTH);
	public static int day = d.get(Calendar.DAY_OF_MONTH);

	public static String getTime() {
		return d.get(Calendar.YEAR)
				+ "-"
				+ (d.get(Calendar.MONTH) < 9 ? "0"
						+ (d.get(Calendar.MONTH) + 1)
						: d.get(Calendar.MONTH) + 1)
				+ "-"
				+ (d.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
						+ d.get(Calendar.DAY_OF_MONTH) : d
						.get(Calendar.DAY_OF_MONTH)) + " ";
	}
}
