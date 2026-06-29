/**
 * 
 */
package com.dp.plat.prob.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author w02611
 *
 */
public class HTMLRegexUtil {

	/**
	 * 去除html中的img 、br、p、table、p、table、tr、th、td标签
	 * @param htmlStr
	 * @return
	 */
	public static String simplifyHTML(String htmlStr) {
		if (StringUtils.isNotBlank(htmlStr)) {
			htmlStr = htmlStr.replaceAll("\r\n", "");
			htmlStr = htmlStr.replaceAll("<(?!img|br|/p|/table|/tr|/th|/td).*?>", "");
			htmlStr = htmlStr.replaceAll("<(?!img|br|/p|/table|/tr).*?>", "    ");
			htmlStr = htmlStr.replaceAll("<(?!img).*?>", "\r\n");
			Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");// <img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
			Matcher m = p.matcher(htmlStr);
			while (m.find()) {
				htmlStr = htmlStr.replace(m.group(), "\r\n" + m.group(1) + "\r\n");
			}
		}
		return StringUtils.trimToEmpty(htmlStr);
	}
	
	public static String simplifyHTML(String htmlStr, String regex) {
		return simplifyHTML(htmlStr, regex, "");
	}
	
	public static String simplifyHTML(String htmlStr, String regex, String replacement) {
		if (StringUtils.isNotBlank(htmlStr)) {
			if (StringUtils.isEmpty(replacement)) {
				replacement = "";
			}
			htmlStr = htmlStr.replaceAll(regex, replacement);
		}
		return StringUtils.trimToEmpty(htmlStr);
	}
}
