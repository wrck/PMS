package com.dp.plat.core.util;

import java.util.Random;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author w02611
 *
 */
public class PasswordUtil {

	/**
	 * 对密码进行1次SHA1有盐加密，再对加密后的密码进行1024次MD5有盐加密
	 * 
	 * @param saltSource
	 *            盐值
	 * @param credentials
	 *            加密前密码
	 * @return 加密后密码
	 */
	public static String encryptPassword(String saltSource, String credentials) {
		return encryptMD5Password(encryptSHA1Password(credentials, saltSource, 1), saltSource, 1024);
	}

	/**
	 * MD5 对密码进行加密
	 * 
	 * @param credentials
	 *            加密前密码
	 * @default hashAlgorithmName = MD5, hashIterations = 1
	 * @return 加密后密码
	 */
	public static String encryptMD5Password(String credentials) {
		return encrypt("MD5", credentials, null, 1);
	}

	/**
	 * MD5 对密码进行加密,加盐值
	 * 
	 * @param credentials
	 *            加密前密码
	 * @param saltSource
	 *            bytes作为 盐值
	 * @default hashAlgorithmName = MD5, hashIterations = 1
	 * @return 加密后密码
	 */
	public static String encryptMD5Password(String credentials, String saltSource) {
		return encrypt("MD5", credentials, saltSource, 1);
	}

	/**
	 * MD5 对密码进行加密，指定盐值和迭代次数
	 * 
	 * @param credentials
	 *            加密前密码
	 * @param saltSource
	 *            bytes作为 盐值
	 * @param hashIterations
	 *            迭代次数
	 * @return 加密后密码
	 */
	public static String encryptMD5Password(String credentials, String saltSource, int hashIterations) {
		return encrypt("MD5", credentials, saltSource, hashIterations);
	}

	/**
	 * SHA1 对密码进行加密
	 * 
	 * @param credentials
	 *            加密前密码
	 * @default hashAlgorithmName = SHA1, hashIterations = 1
	 * @return 加密后密码
	 */
	public static String encryptSHA1Password(String credentials) {
		return encrypt("SHA1", credentials, null, 1);
	}

	/**
	 * SHA1 对密码进行加密，加盐值
	 * 
	 * @param credentials
	 *            加密前密码
	 * @param saltSource
	 *            bytes作为 盐值
	 * @default hashAlgorithmName = SHA1, hashIterations = 1
	 * @return 加密后密码
	 */
	public static String encryptSHA1Password(String credentials, String saltSource) {
		return encrypt("SHA1", credentials, saltSource, 1);
	}

	/**
	 * SHA1 对密码进行加密
	 * 
	 * @param credentials
	 *            加密前密码
	 * @param saltSource
	 *            bytes作为 盐值
	 * @param hashIterations
	 *            迭代次数
	 * @default hashAlgorithmName = SHA1, hashIterations = 1
	 * @return 加密后密码
	 */
	public static String encryptSHA1Password(String credentials, String saltSource, int hashIterations) {
		return encrypt("SHA1", credentials, saltSource, hashIterations);
	}

	/**
	 * 根据加密方式，以及密码进行1次hash加密
	 * 
	 * @param hashAlgorithmName
	 *            加密方式：MD5、SHA1
	 * @param credentials
	 *            credentials
	 * @default 无saltSource,hash次数1
	 * @return 加密后密码
	 */
	public static String encrypt(String hashAlgorithmName, String credentials) {
		return encrypt(hashAlgorithmName, credentials, null, 1);
	}

	/**
	 * 根据加密方式，盐值，以及密码进行1次hash加密
	 * 
	 * @param hashAlgorithmName
	 *            加密方式：MD5、SHA1
	 * @param credentials
	 *            credentials
	 * @param saltSource
	 *            bytes作为 盐值
	 * @default hash次数1
	 * @return 加密后密码
	 */
	public static String encrypt(String hashAlgorithmName, String credentials, String saltSource) {
		return encrypt(hashAlgorithmName, credentials, saltSource, 1);
	}

	/**
	 * 根据加密方式，盐值以及hash次数对密码进行加密
	 * 
	 * @param hashAlgorithmName
	 *            加密方式：MD5、SHA1
	 * 
	 * @param credentials
	 *            加密前密码
	 * @param saltSource
	 *            bytes作为 盐值
	 * @param credentials
	 *            加密前密码
	 * @param hashIterations
	 *            hash次数
	 * @return 加密后密码
	 * 
	 */
	public static String encrypt(String hashAlgorithmName, String credentials, String saltSource, int hashIterations) {
		ByteSource salt = saltSource != null ? ByteSource.Util.bytes(saltSource) : null;
		SimpleHash simpleHash = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
		return simpleHash.toString();
	}

	/**
	 * 获取随机密码
	 * 
	 * @param pwdLength
	 *            密码长度 ，默认8位
	 * 
	 * @return
	 */
	public static String createRandomPassword(int... pwdLength) {
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', '~', '!', '@', '#', '$', '%', '^', '&', '*', '_', '-', '.' };
		int length = 8;
		if (pwdLength.length > 1 && pwdLength[0] > 8) {
			length = pwdLength[0];
		}
		StringBuilder randPassword = new StringBuilder();
		Random random = new Random();
		for (int j = 0; j < length; j++) {
			int i = random.nextInt(str.length - 1);
			randPassword.append(str[i]);
		}
		return randPassword.toString();
	}

	public static void main(String[] args) {
		String username = "w02611";
//		System.out.println(encryptSHA1Password("123456", username, 1));
		System.out.println(encryptPassword(username, "123456"));
//		String credentials = "1";
//		String hashAlgorithmName = "MD5";
//		ByteSource salt = ByteSource.Util.bytes(username);
//		int hashIterations = 1024;
//		SimpleHash simpleHash = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
//		System.out.println(simpleHash.toString());
	}

}
