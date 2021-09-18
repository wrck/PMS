package com.dp.plat.core.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * 全局日期处理类 Converter<T,S> 泛型T:代表客户端提交的参数 String 泛型S:通过converter转换的类型
 * 
 */
public class DateConverter implements Converter<String, Date> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static String[] dateFormatPattern = { "yy-MM-dd HH:mm:ss", "yy/MM/dd HH:mm:ss", "yy-MM-dd HH:mm",
			"yy/MM/dd HH:mm", "yy-MM-dd HH", "yy/MM/dd HH", "yy-MM-dd", "yy/MM/dd", "yy-MM", "yy/MM", "yy-M", "yy/M" };

	@Override
	public Date convert(String stringDate) {
		if(StringUtils.isEmpty(stringDate))
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		ParseException parseException = null;
		for (String pattern : dateFormatPattern) {
			try {
				simpleDateFormat.applyPattern(pattern);
				return simpleDateFormat.parse(stringDate);
			} catch (ParseException e) {
				parseException = e;
			}
		}
		if (parseException != null) {
			logger.error("表单日期格式转换发生异常", parseException);
		}
		return null;
	}

	public static Date covert(String stringDate) {
		if(StringUtils.isEmpty(stringDate))
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		ParseException parseException = null;
		for (String pattern : dateFormatPattern) {
			try {
				simpleDateFormat.applyPattern(pattern);
				return simpleDateFormat.parse(stringDate);
			} catch (ParseException e) {
				parseException = e;
			}
		}
		if (parseException != null) {
		}
		return null;
	}
	
	public static String covert(Date date) {
		if(date == null)
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		for (String pattern : dateFormatPattern) {
			simpleDateFormat.applyPattern(pattern);
			return simpleDateFormat.format(date);
		}
		return null;
	}
}