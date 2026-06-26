package com.dp.plat.common.utils;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordUtil {

    public static String encrypt(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    public static String generateRandomPassword() {
        return cn.hutool.core.util.RandomUtil.randomString(12);
    }
}
