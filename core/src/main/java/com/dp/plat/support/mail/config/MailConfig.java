/**
 * 
 */
package com.dp.plat.support.mail.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.support.PropertiesUtil;

/**
 * @author w02611
 *
 */
public class MailConfig {

	public static HashMap<String, String> mailVariables;

	static {
		mailVariables = getMailVariables();
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getMailVariables() {
		try {
			Class<?> systemConfigClass = Class.forName("com.dp.plat.core.config.SystemConfig");
			SystemConfig systemConfig = SpringContext.getApplicationContext().getBean(SystemConfig.class);
			mailVariables = systemConfig.systemVariables;
//			Method method = systemConfigClass.getMethod("getSystemVariables");
//			mailVariables = (HashMap<String, String>) method.invoke(null);
		} catch (ClassNotFoundException | IllegalArgumentException | SecurityException e) {
			try {
				Properties props = new Properties();
				InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("mailConfig.properties");
				props.load(in);
				mailVariables = new HashMap<>((Map) props);
				in.close();
			} catch (Exception ex) {
				e.printStackTrace();
			}
		}
		return mailVariables;
	}

}
