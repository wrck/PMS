package com.dp.plat.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.dp.plat.data.bean.MailContent;

/**
 * 替换邮件模板中的变量
* @ClassName: MailHandleUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author dp 
* @date 2015年6月6日 上午10:41:32 
*
 */
public class MailHandleUtil {

	private static final String STR_PRE = "[";//前缀字符
	private static final String STR_SUF = "]";//后缀字符
	private static final String STR_GET = "get";//get方法需添加
	/**
	 * 处理邮件变量
	 * @param str 传入字符串
	 * @param mc 需替换的变量名所在的类
	 */
	public static String dealwithMail(String str, MailContent mc) throws Exception{
		//获取传入类的class属性
		Class<?> clazz = mc.getClass();
		//获取所有字段名称
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields){
			String fieldname = f.getName();//字段名称
			//如果字段长度为0，跳过当前循环
			if(fieldname.length() == 0){
				continue;
			}
			String name = STR_GET + fieldname.substring(0, 1).toUpperCase() + fieldname.substring(1, fieldname.length());//拼接名称为get+首字母大写
			Method declaredMethod = clazz.getDeclaredMethod(name, new Class[]{});//获取声明的方法
			String returnVal = (String) declaredMethod.invoke(mc, new Object[]{});//方法调用
			if(returnVal != null){//如果返回值不是空，则替换[字段名]为返回值
				String replacename = STR_PRE + fieldname + STR_SUF;//
				str = str.replace(replacename, returnVal);
			}
		}
		return str;
	}
	
}
