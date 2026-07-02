package com.dp.plat.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class ASEUtil {
    private static final String ALGORITHM = "AES";
    public static String encrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), ALGORITHM));
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }
    public static String decrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), ALGORITHM));
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }
}
