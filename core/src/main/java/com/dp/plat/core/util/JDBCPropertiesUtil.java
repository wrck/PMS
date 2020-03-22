package com.dp.plat.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取JDBC.properties配置
* @ClassName: JDBCPropertiesUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author dp 
* @date 2015年4月28日 下午5:29:43 
*
 */
public class JDBCPropertiesUtil {

	public static Properties prop;
	public static final String FILE_PATH = "/jdbc.properties";
	
	static{
		prop = new Properties();
	}
	
	public static Properties returnProperties(){
		InputStream in = JDBCPropertiesUtil.class.getResourceAsStream(FILE_PATH);
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public static String returnParam(String param){
		Properties p = returnProperties();
		return p.getProperty(param);
	}
	
}
