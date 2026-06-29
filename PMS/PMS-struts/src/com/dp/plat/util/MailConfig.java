/**
 * 
 */
package com.dp.plat.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;

import com.alibaba.fastjson.JSON;
import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.BasicDataDao;

/**
 * @author w02611
 *
 */
public class MailConfig {

	public static Map<String, String> mailVariables;

	static {
		mailVariables = getMailVariables();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getMailVariables() {
		try {
			BasicDataDao basicDataDao = (BasicDataDao) SpringContext.getBean("basicDataDao");
			String mailConfig = StringUtils.defaultIfBlank(basicDataDao.querySysArg("sys.mail.config"), "{}");
			mailVariables = JSON.parseObject(mailConfig, HashMap.class);
			if (mailVariables.isEmpty()) {
				mailVariables = new HashMap<>((Map) getProperties());
			}
		} catch (Throwable e) {
			mailVariables = new HashMap<>((Map) getProperties());
		}
		return mailVariables;
	}

	private static Properties getProperties() {
		Properties props = new Properties();
		try {
			InputStream in = MailConfig.class.getClassLoader().getResourceAsStream("mailConfig.properties");
			props.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return props;
	}

}
