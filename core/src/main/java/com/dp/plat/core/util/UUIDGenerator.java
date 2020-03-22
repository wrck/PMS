package com.dp.plat.core.util;

import java.util.UUID;

public class UUIDGenerator {
	public UUIDGenerator() {
		
	}

	/**
	 * 获得一个UUID
	 * 
	 * @return String UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉"-"符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
	}
	/**
	 * 获取一个UUID
	 * @param boolean isReplace  是否去掉中间的‘-’
	 * @return String UUID
	 */
	public static String getUUID(boolean isReplace){
		if(isReplace){
			return UUID.randomUUID().toString().replace("-", "");
		}else{
			return UUID.randomUUID().toString();
		}
	}
	/**
	 * 获得指定数目的UUID
	 * 
	 * @param number
	 *            int 需要获得的UUID数量
	 * @return String[] UUID数组
	 */
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] s = new String[number];
		for (int i = 0; i < number; i++) {
			s[i] = getUUID();
		}
		return s;
	}
	
	public static void main(String[] args) {
		System.out.println(getUUID());
	}
}
