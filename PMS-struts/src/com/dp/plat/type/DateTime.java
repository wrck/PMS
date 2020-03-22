package com.dp.plat.type;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime extends Date implements UserParamInterface
{
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public DateTime()
	{
		super();
		Calendar cal = Calendar.getInstance();
		cal.setTime(this);
		cal.set(Calendar.MILLISECOND, 0);
		this.setTime(cal.getTime().getTime());
	}

	public DateTime(String stime)
	{
		this.parseFrom(stime);
	}

	public DateTime(Integer time)
	{
		this.setTime((long) time * 1000);
	}

	public DateTime(Long unixtime)
	{
		this.setTime(unixtime * 1000);
	}

	public DateTime(Date date)
	{
		if (date == null)
		{
			this.setTime(0);
		}
		else
		{
			this.setTime(date.getTime());
		}

	}

	public void setUnixTime(long unixtime)
	{
		this.setTime(unixtime * 1000);
	}

	public boolean parseFrom(String input)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			Date date = formatter.parse(input);
			this.setTime(date.getTime());
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public String formatTo()
	{
		if (this.getTime() == 0)
		{
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(this);
	}

	public String formatToFileSuffix()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(this);
	}

	public long longValue()
	{
		return this.getTime() / 1000;
	}

	public long lastValue()
	{
		return this.longValue() + 3600 * 24 - 1;
	}

	public boolean ltNow()
	{
		return (this.longValue() < (new Date()).getTime() / 1000);
	}

	public boolean gtNow()
	{
		return (this.lastValue() > (new Date()).getTime() / 1000);
	}

	public static DateTime lastOnehour()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.HOUR, -1);
		return new DateTime(cal.getTime());
	}

	public static DateTime lastOnehour(DateTime datetime)
	{
		if (datetime != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(datetime);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.HOUR, -1);
			return new DateTime(cal.getTime());
		}
		return datetime;
	}

	public static DateTime nextOnehour(DateTime datetime)
	{
		if (datetime != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(datetime);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.HOUR, 1);
			return new DateTime(cal.getTime());
		}
		return datetime;
	}

	@Override
	public String toString()
	{
		return formatTo();
	}

	public String toDateString()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(this);
	}

	// 系统当前时间的零点
	public static DateTime timeToday()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间下一天的零点
	public static DateTime timeTomorrow()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间前一天的零点
	public static DateTime timeYesterday()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在周的周日，一周的开始是周日
	public static DateTime thisWeek()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在周的下一周的周日，一周的开始是周日
	public static DateTime nextWeek()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在周的上一周的周日，一周的开始是周日
	public static DateTime beforeWeek()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在月的开始，即一号
	public static DateTime thisMonth()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在月的下个月的开始，即下月一号
	public static DateTime nextMonth()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在月的上一个月的开始，即上月一号
	public static DateTime beforeMonth()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 系统当前时间所在年的第一天
	public static DateTime thisYear()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 明年的第一天
	public static DateTime nextYear()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 去年的第一天
	public static DateTime beforeYear()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTime(cal.getTime());
	}

	// 当前日期前30天
	public static DateTime last15Date()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -14);
		return new DateTime(cal.getTime());
	}

	// 当前日期后30天
	public static DateTime next15Date()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 14);
		return new DateTime(cal.getTime());
	}
}
