package com.dp.plat.core.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * 全局日期处理类 Converter<T,S> 泛型T:代表客户端提交的参数 String 泛型S:通过converter转换的类型
 * 
 */
public class DecimalConverter implements Converter<String, BigDecimal> {
	private final static Logger logger = LoggerFactory.getLogger(DecimalConverter.class);

	private final static String[] formatPattern = { "##,##0.00", "####0.00" };

	/**
	 * 线程安全化DecimalFormat，用于格式化金额
	 */
	private static ThreadLocal<DecimalFormat> decimalFormat = new ThreadLocal<DecimalFormat>() {
		public DecimalFormat initialValue() {
			DecimalFormat format = new DecimalFormat("##,##0.00");
			format.setParseBigDecimal(true);
			return format;
		}
	};

	@Override
	public BigDecimal convert(String amount) {
		return DecimalConverter.parse(amount);
	}

	public static BigDecimal parse(String amount) {
		if (StringUtils.isEmpty(amount))
			return null;
		DecimalFormat format = decimalFormat.get();
		ParseException parseException = null;
		for (String pattern : formatPattern) {
			try {
				format.applyPattern(pattern);
				return (BigDecimal) format.parse(amount);
			} catch (ParseException e) {
				parseException = e;
			}
		}
		if (parseException != null) {
			logger.error("表单货币格式转换发生异常", parseException);
		}
		return null;
	}
}