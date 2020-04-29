/**
 * 
 */
package com.dp.plat.core.serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author w02611
 *
 */
public class JsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Object> {
	private final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
	private final FastDateFormat dateTimeFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	// private final DecimalFormat decimalFormat = new
	// DecimalFormat("##,##0.00");
	// private final NumberFormat percentFormat =
	// NumberFormat.getPercentInstance();

	/**
	 * 线程安全化DecimalFormat，用于格式化金额
	 */
	private static ThreadLocal<DecimalFormat> decimalFormat = new ThreadLocal<DecimalFormat>() {
        public DecimalFormat initialValue() {
            return new DecimalFormat("##,##0.00");
        }
    };
    
    /**
	 * 线程安全化NumberFormat，用于格式化百分比
	 */
    private static ThreadLocal<NumberFormat> percentFormat = new ThreadLocal<NumberFormat>() {
        public NumberFormat initialValue() {
            return NumberFormat.getPercentInstance();
        }
    };
    
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers)
			throws IOException {
		if (value == null) {
			return;
		}
		if (value instanceof Date) {
			try {
				String tempValue = dateFormat.format(value);
				if(StringUtils.isNotBlank(tempValue) && value.equals(DateTime.parse(tempValue).toDate())){
					jsonGenerator.writeString(tempValue);
					return;
				} else if (StringUtils.isBlank(tempValue)){
					return;
				}
			} catch (Exception e) {
			}
			jsonGenerator.writeString(dateTimeFormat.format(value));
		} else if (value instanceof BigDecimal) {
			jsonGenerator.writeString(decimalFormat.get().format(value));
		} else if (value instanceof Double) {
			percentFormat.get().setMinimumFractionDigits(2);
			percentFormat.get().setMaximumFractionDigits(2);
			jsonGenerator.writeString(percentFormat.get().format(value));
		} else {
			jsonGenerator.writeString(String.valueOf(value));
		}
	}

}
