package com.dp.plat.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {
	// 加密
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = Base64.getEncoder().encodeToString(b);
		}
		return s;
	}
	public static String EncodeBase64(Object obj){
		
		return getBase64(obj.toString());
	}
	
	
	// 解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			try {
				b = Base64.getDecoder().decode(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public static Object decodeBase64(String str){
		
		return getFromBase64(str);
	}
}
