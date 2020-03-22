/**
 * 
 */
package com.dp.plat.core.util;

import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.springframework.util.StringUtils;

/**
 * @author w02611
 *
 */
public class PropertyUtil {
	private static Hashtable<String, ResourceBundle> res = new Hashtable<String, ResourceBundle>();
	private static String[] baseNames;
	static {
		ResourceBundle rb = PropertyResourceBundle.getBundle("config", Locale.CHINA);
		String baseNameStr = rb.getString("sys.resources");
		if (!StringUtils.isEmpty(baseNameStr)) {
			baseNames = baseNameStr.split(";");
		}
	}

	public static String getProperty(String key) {
		String mod = key.substring(0, key.indexOf("."));
		try {
			ResourceBundle rb = res.get(mod);
			if (null == rb) {
				for (String baseName : baseNames) {
					rb = PropertyResourceBundle.getBundle(baseName, Locale.CHINA);
					if (rb.containsKey(key)) {
						res.put(mod, rb);
						break;
					}
				}
			}
			if (!rb.containsKey(key)) {
				return null;
			}
			return MessageFormat.format(rb.getString(key), new Object());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
