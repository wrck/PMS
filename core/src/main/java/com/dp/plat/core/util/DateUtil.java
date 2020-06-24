package com.dp.plat.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtil {
	private static Log log = LogFactory.getLog(DateUtil.class);

	/**
	 * 获取今天的日期
	 * 
	 * @return
	 */
	public static String getTodayDate() {
		Date today = new Date();
		return getDateTime("yyyy-MM-dd", today);
	}

	public static String getTodayDateTime() {
		Date today = new Date();
		return getDateTime("yyyy-MM-dd HH:mm:ss", today);
	}

	public static final String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
			log.error("aDate is null!");
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * 两个日期比较
	 * 
	 * @param format  转换类型
	 * @param maxdate 最大的日期
	 * @param mindate 最小的日期
	 * @return maxdate>mindate
	 */
	public static boolean compare_date(String format, String maxdate, String mindate) {
		boolean fag = true;
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
		try {
			Date ddate = sdf.parse(maxdate);
			Date toddate = sdf.parse(mindate);
			fag = ddate.after(toddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fag;
	}

	/** 获得指定日期 上 n 周第一天 周一 */
	public static Date getWeekStartDay(Date time) {
		return getWeekStartDay(time, 0);
	}
	
	/** 获得指定日期 上 n 周第一天 周一*/
	public static Date getWeekStartDay(Date time, int n) {
		if (time == null) {
			time = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_WEEK, 2);
		c.add(Calendar.DATE, -7 * n);
		return c.getTime();
	}
	
	/** 获得指定日期 上 n周周日*/
	public static Date getWeekEndDay(Date time) {
		return getWeekEndDay(time, 0);
	}
	
	/** 获得指定日期 上 n周周日*/
	public static Date getWeekEndDay(Date time, int n) {
		if (time == null) {
			time = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_WEEK, 2);
		c.add(Calendar.DATE, -7 * n);
		c.add(Calendar.DATE, 6);
		return c.getTime();
	}
	
}
