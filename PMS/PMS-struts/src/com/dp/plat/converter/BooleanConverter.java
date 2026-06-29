package com.dp.plat.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

/**
 * Boolean类型转换器，用来接收Boolean 的 null
 * 
 * @author user
 *
 */
public class BooleanConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		String value = null;
		if (values != null && values.length > 0) {
			value = values[0];
			if (value != null) {
				if (value.equals("null") || value.equals("")) {
					return null;
				} else if (value.equals("1")) {
					return true;
				} else if (value.equals("0")) {
					return false;
				}
			}
		}
		if (value == null)
            return null;
		return Boolean.valueOf(value);
	}

	@Override
	public String convertToString(Map context, Object o) {
		return stringValue(o);
	}

}