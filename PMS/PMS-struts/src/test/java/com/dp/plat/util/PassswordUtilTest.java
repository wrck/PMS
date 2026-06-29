package com.dp.plat.util;

import org.junit.Test;

public class PassswordUtilTest {

    @Test
    public void generatePwd() {
        String username = "w02611";
        String password = "!q2w3e4r";
        String encryptMD5Password = PasswordUtil.encryptMD5Password(password, username);
        System.out.println(encryptMD5Password);
    }
}
