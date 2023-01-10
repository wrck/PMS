/**
 * 
 */
package com.dp.plat.core.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

/**
 * @author w02611
 *
 */
public class PropertyUtil {
	private static ConcurrentHashMap<String, Set<ResourceBundle>> res = new ConcurrentHashMap<String, Set<ResourceBundle>>();
	private static String[] baseNames;
	static {
        try {
            ResourceBundle rb = PropertyResourceBundle.getBundle("config", Locale.CHINA);
            String baseNameStr = rb.containsKey("sys.resources") ? rb.getString("sys.resources") : "config";
            if (!StringUtils.isEmpty(baseNameStr)) {
                baseNames = baseNameStr.split(";");
            } else {
                baseNames = new String[0];
            }
        } catch (Exception e) {
            baseNames = new String[0];
        }
    }
	
	private static Set<ResourceBundle> getBundles(String key, boolean findNew) {
		String mod = key.substring(0, key.indexOf("."));
		Set<ResourceBundle> rbSet = res.get(mod);
		if (null == rbSet) {
			rbSet = ConcurrentHashMap.newKeySet();
		}
		if (findNew || rbSet.isEmpty()) {
			for (String baseName : baseNames) {
				ResourceBundle rb = PropertyResourceBundle.getBundle(baseName, Locale.CHINA);
				if (rb.containsKey(key)) {
					rbSet.add(rb);
					break;
				}
			}
			res.put(mod, rbSet);
		}
		return rbSet;
	}

	public static String getProperty(String key) {
		return getProperty(key, false);
	}
	
	private static String getProperty(String key, boolean isSecond) {
		try {
			Set<ResourceBundle> rbSet = getBundles(key, isSecond);
			ResourceBundle validRb = null; 
			for (ResourceBundle rb : rbSet) {
				if (rb.containsKey(key)) {
					validRb = rb;
					break;
				}
			}
			if (validRb == null && !isSecond) {
				return getProperty(key, true);
			}
			if (validRb == null) {
				return null;
			}
			return MessageFormat.format(validRb.getString(key), new Object());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
