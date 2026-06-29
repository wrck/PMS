package com.dp.plat.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    
    /**
     * 获取当前月的第一天
     * 
     * @return
     */
    public static Date getFirstDay() {
        Calendar cal_1 = Calendar.getInstance();// 获取当前日期
        cal_1.set(Calendar.SECOND, 0);
        cal_1.set(Calendar.MINUTE, 0);
        cal_1.set(Calendar.HOUR_OF_DAY, 0);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        return cal_1.getTime();
    }

    /**
     * 获取传入日期的季度第一天
     * 
     * @param date
     * @return
     */
    public static Date getQuarterFirstDay(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        int month = getQuarterInMonth(c.get(Calendar.MONTH), true);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 获取传入日期的季度最后一天
     * 
     * @param date
     * @return
     */
    public static Date getQuarterLastDay(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        int month = getQuarterInMonth(c.get(Calendar.MONTH), false);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 0);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 当前月是否是季度末月
     * 
     * @return
     */
    public static boolean isQuarterLastMonth() {
        Calendar ca = Calendar.getInstance();
        int month = ca.get(Calendar.MONTH);
        int months[] = { 2, 5, 8, 11 };
        boolean bool = false;
        for (int m : months) {
            if (m == month) {
                bool = true;
                break;
            }
        }
        return bool;
    }
    
    public static Date[] getNearlyYearDates(Date nowDate, Date rangeStart, Date rangeEnd) {
    	return getNearlyYearDates(nowDate, rangeStart, rangeEnd, false);
//    	Calendar date = Calendar.getInstance();
//		date.setTime(nowDate);
//		date.set(Calendar.HOUR_OF_DAY, 0);
//		date.set(Calendar.MINUTE, 0);
//		date.set(Calendar.SECOND, 0);
//		date.set(Calendar.MILLISECOND, 0);
//		Date now = new Date();
//		Calendar begin = Calendar.getInstance();
//		begin.setTime(rangeStart != null ? rangeStart : now);
//		Calendar end = Calendar.getInstance();
//		end.setTime(rangeEnd != null ? rangeEnd : now);
//		
//		// 不在区间内的，找匹配最近的一段时间
//		if (date.before(begin)) {
//			date.setTime(begin.getTime());;
//		} else if (date.after(end)) {
//			date.setTime(end.getTime());;
//		}
//		
//		Calendar startDate = Calendar.getInstance();
//		startDate.setTime(date.getTime());
//		Calendar endDate = Calendar.getInstance();
//		endDate.setTime(date.getTime());
//		
//    	while (isBetween(date, begin, end)) {
//    		endDate.setTime(end.getTime());
//    		end.add(Calendar.YEAR, -1);
//    		startDate.setTime(end.getTime());
//    	}
//    	startDate.add(Calendar.DATE, 1);
//    	if (startDate.before(begin)) {
//    		startDate = begin;
//    	}
//		return new Date[] {startDate.getTime(), endDate.getTime()};
    }
    
    public static Date[] getNearlyYearDates(Date nowDate, Date rangeStart, Date rangeEnd, boolean reverse) {
    	Calendar date = Calendar.getInstance();
		date.setTime(nowDate);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		Date now = new Date();
		Calendar begin = Calendar.getInstance();
		begin.setTime(rangeStart != null ? rangeStart : now);
		Calendar end = Calendar.getInstance();
		end.setTime(rangeEnd != null ? rangeEnd : now);
		
		// 不在区间内的，找匹配最近的一段时间
		if (date.before(begin)) {
			date.setTime(begin.getTime());;
		} else if (date.after(end)) {
			date.setTime(end.getTime());;
		}
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(date.getTime());
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(date.getTime());
		
		if (reverse) {
			while (isBetween(date, begin, end)) {
				endDate.setTime(end.getTime());
				end.add(Calendar.YEAR, -1);
				startDate.setTime(end.getTime());
			}
			startDate.add(Calendar.DATE, 1);
			if (startDate.before(begin)) {
				startDate = begin;
			}
		} else {
			while (isBetween(date, begin, end)) {
				startDate.setTime(begin.getTime());
				begin.add(Calendar.YEAR, 1);
				endDate.setTime(begin.getTime());
			}
			endDate.add(Calendar.DATE, -1);
			if (endDate.after(end)) {
				endDate = end;
			}
		}
		return new Date[] {startDate.getTime(), endDate.getTime()};
    }
    
	/***
	 * 比较某一时间是否在两个日期之间
	 * 
	 * @param nowTime
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isBetween(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
			return true;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);
		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}
	
	/***
	 * 比较某一时间是否在两个日期之间
	 * 
	 * @param nowTime
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isBetween(Calendar nowTime, Calendar startTime, Calendar endTime) {
		if (startTime.before(endTime) && (nowTime.getTimeInMillis() == startTime.getTimeInMillis() || nowTime.getTimeInMillis() == endTime.getTimeInMillis())) {
			return true;
		}
		if (nowTime.after(startTime) && nowTime.before(endTime)) {
			return true;
		} else {
			return false;
		}
	}

    /**
     * 获取格式化后的时间间隔
     * 
     * @param endTime
     * @param startTime
     * @return formatedDateInterval
     */
    public static String getFormatedDateIntervalByType(Date startTime, Date endTime, int type) {
        if (startTime == null || endTime == null) {
            return "";
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endTime);
        return getFormatedDateIntervalByType(startCalendar, endCalendar, type);
    }

    /**
     * 获取格式化后的时间间隔
     * 
     * @param endTime
     * @param startTime
     * @return formatedDateInterval
     */
    public static String getFormatedDateIntervalByType(Calendar startTime, Calendar endTime, int type) {
        if (startTime == null || endTime == null) {
            return "";
        }
        double diff = 0;
        String sign = "";
        if (endTime.compareTo(startTime) < 0) {
            sign = "-";
            Calendar temp = endTime;
            endTime = startTime;
            startTime = temp;
        }

        long time1 = startTime.getTimeInMillis();
        long time2 = endTime.getTimeInMillis();
        
        double yearDiff = endTime.get(Calendar.YEAR) - startTime.get(Calendar.YEAR);
        double monthDiff = endTime.get(Calendar.MONTH) - startTime.get(Calendar.MONTH);
        double day = endTime.get(Calendar.DAY_OF_MONTH) - startTime.get(Calendar.DAY_OF_MONTH);
        if(day < 0) {
            int fMonth = endTime.get(Calendar.MONTH);
            // 找前一个月
            fMonth = fMonth > 0 ? fMonth : fMonth + 12;
            int daysOfMonth = getDaysByYearMonth(startTime.get(Calendar.YEAR), fMonth);
            day +=daysOfMonth;
            monthDiff--;
        }
        if(monthDiff < 0) {
            monthDiff += 12;
            yearDiff--;
        }
        double month =  monthDiff + yearDiff * 12;
        double diffTime = time2 - time1;
        double interval = 1;
        switch (type) {
            case Calendar.YEAR:
                diff = yearDiff + monthDiff / 12 + day / 30 / 12;
                break;
            case Calendar.MONTH:
                interval = interval * 30;// 按30天来计算
            case Calendar.DATE:
                interval = interval * 24;
            case Calendar.HOUR:
                interval = interval * 60;
            case Calendar.MINUTE:
                interval = interval * 60;
            case Calendar.SECOND:
                interval = interval * 1000;
                diff = type != Calendar.MONTH ? diffTime / interval : month + (day / endTime.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            default:
                break;
        }
        //返回2位小数，并且四舍五入
        DecimalFormat df = new DecimalFormat("######0.##");
        return sign + df.format(diff);
    }

    /**
     * 获取格式化后的时间间隔
     * 
     * @param endTime
     * @param startTime
     * @return formatedDateInterval
     */
    public static String getFormatedDateInterval(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return "";
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endTime);

        return getFormatedDateInterval(startCalendar, endCalendar);
    }

    /**
     * 获取格式化后的时间间隔
     * 
     * @param endTime
     * @param startTime
     * @return formatedDateInterval
     */
    public static String getFormatedDateInterval(Calendar startTime, Calendar endTime) {
        if (startTime == null || endTime == null) {
            return "";
        }
        String sign = "";
        if (endTime.compareTo(startTime) == 0) {
            return "0秒";
        } else if (endTime.compareTo(startTime) < 0) {
            sign = "-";
            Calendar temp = endTime;
            endTime = startTime;
            startTime = temp;
        }

        int year = endTime.get(Calendar.YEAR) - startTime.get(Calendar.YEAR);
        int month = endTime.get(Calendar.MONTH) - startTime.get(Calendar.MONTH);
        int day = endTime.get(Calendar.DAY_OF_MONTH) - startTime.get(Calendar.DAY_OF_MONTH);
        int hour = endTime.get(Calendar.HOUR_OF_DAY) - startTime.get(Calendar.HOUR_OF_DAY);
        int minute = endTime.get(Calendar.MINUTE) - startTime.get(Calendar.MINUTE);
        int second = endTime.get(Calendar.SECOND) - startTime.get(Calendar.SECOND);

        StringBuilder str = new StringBuilder();
        if (second > 0) {
            str.insert(0, "秒").insert(0, second);
        } else if (second < 0) {
            minute--;
            str.insert(0, "秒").insert(0, second + 60);
        }
        if (minute > 0) {
            str.insert(0, "分").insert(0, minute);
        } else if (minute < 0) {
            hour--;
            str.insert(0, "分").insert(0, minute + 60);
        }
        if (hour > 0) {
            str.insert(0, "小时").insert(0, hour);
        } else if (hour < 0) {
            day--;
            str.insert(0, "小时").insert(0, hour + 24);
        }
        if (day > 0) {
            str.insert(0, "天").insert(0, day);
        } else if (day < 0) {
            month--;
            int fMonth = endTime.get(Calendar.MONTH);
            // 找前一个月
            fMonth = fMonth > 0 ? fMonth : fMonth + 12;
            int daysOfMonth = getDaysByYearMonth(startTime.get(Calendar.YEAR), fMonth);
            str.insert(0, "天").insert(0, day + daysOfMonth);
        }

        if (month > 0) {
            str.insert(0, "个月").insert(0, month);
        } else if (month < 0) {
            year--;
            str.insert(0, "个月").insert(0, month + 12);
        }
        if (year > 0) {
            str.insert(0, "年").insert(0, year);
        }
        str.insert(0, sign);
        return str.toString();
    }

    // 返回第几个月份，不是几月
    // 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
    private static int getQuarterInMonth(int month, boolean isQuarterStart) {
        int months[] = { 1, 4, 7, 10 };
        if (!isQuarterStart) {
            months = new int[] { 3, 6, 9, 12 };
        }
        if (month >= 0 && month <= 2) return months[0];
        else if (month >= 3 && month <= 5) return months[1];
        else if (month >= 6 && month <= 8) return months[2];
        else return months[3];
    }

    /**
     * 获取某年某月有多少天
     * 
     * @param year
     * @param month
     * @return
     */
    private static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
    
    public static void main(String[] args) throws ParseException {
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date rangeStart = dateFormat.parse("2018-09-28");
		Date rangeEnd = dateFormat.parse("2020-10-28");
		Date[] nearlyYearDates = getNearlyYearDates(nowDate, rangeStart, rangeEnd, true);
		System.out.println(dateFormat.format(nearlyYearDates[0]));
		System.out.println(dateFormat.format(nearlyYearDates[1]));
		
		System.out.println();
		nearlyYearDates = getNearlyYearDates(nowDate, rangeStart, rangeEnd, false);
		System.out.println(dateFormat.format(nearlyYearDates[0]));
		System.out.println(dateFormat.format(nearlyYearDates[1]));
	}
}
