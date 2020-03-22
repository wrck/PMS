package com.dp.plat.util;

import java.util.Random;

public class PasswordUtil {

	public static String generatePass() {
		char[] strs = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
				'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(strs[rand.nextInt(strs.length - 1)]);
		}
		return sb.toString();
	}
	
}
