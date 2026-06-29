package com.dp.plat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

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
        SimpleHash simpleHash;
        try {
            simpleHash = new SimpleHash(hashAlgorithmName, credentials, saltSource, hashIterations);
            return simpleHash.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                '9', '~', '!', '@', '#', '$', '%', '^', '&', '*', '_', '-', '.'
        };
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
    
    public static String generatePass() {
        return createRandomPassword(8);
    }

    public static void main(String[] args) {
        String username = "w02611";
//      System.out.println(encryptSHA1Password("123456", username, 1));
        System.out.println(encryptPassword(username, "123456"));
//      String credentials = "1";
//      String hashAlgorithmName = "MD5";
//      ByteSource salt = ByteSource.Util.bytes(username);
//      int hashIterations = 1024;
//      SimpleHash simpleHash = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
//      System.out.println(simpleHash.toString());
    }

    public static class SimpleHash {

        private static final int DEFAULT_ITERATIONS = 1;

        /**
         * The {@link java.security.MessageDigest MessageDigest} algorithm name to use when performing the hash.
         */
        private final String algorithmName;

        /**
         * The hashed data
         */
        private byte[] bytes;

        /**
         * Supplied salt, if any.
         */
        private byte[] salt;

        /**
         * Number of hash iterations to perform.  Defaults to 1 in the constructor.
         */
        private int iterations;

        /**
         * Cached value of the {@link #toHex() toHex()} call so multiple calls won't incur repeated overhead.
         */
        private transient String hexEncoded = null;

        /**
         * Cached value of the {@link #toBase64() toBase64()} call so multiple calls won't incur repeated overhead.
         */
        private transient String base64Encoded = null;

        public SimpleHash(String algorithmName) {
            this.algorithmName = algorithmName;
            this.iterations = DEFAULT_ITERATIONS;
        }

        public SimpleHash(String algorithmName, String source) throws Exception {
            //noinspection NullableProblems
            this(algorithmName, source, null, DEFAULT_ITERATIONS);
        }

        public SimpleHash(String algorithmName, String source, String salt) throws Exception {
            this(algorithmName, source, salt, DEFAULT_ITERATIONS);
        }

        public SimpleHash(String algorithmName, String source, String salt, int hashIterations)
                throws Exception {
            if (!StringUtils.hasText(algorithmName)) {
                throw new NullPointerException("algorithmName argument cannot be null or empty.");
            }
            this.algorithmName = algorithmName;
            this.iterations = Math.max(DEFAULT_ITERATIONS, hashIterations);
            if (source != null) {
                this.bytes = source.getBytes("UTF-8");
            }
            if (salt != null) {
                this.salt = salt.getBytes("UTF-8");
            }
            byte[] hashedBytes = hash(this.bytes, this.salt, hashIterations);
            setBytes(hashedBytes);
        }

        /**
         * Returns the {@link java.security.MessageDigest MessageDigest} algorithm name to use when performing the hash.
         *
         * @return the {@link java.security.MessageDigest MessageDigest} algorithm name to use when performing the hash.
         */
        public String getAlgorithmName() {
            return this.algorithmName;
        }

        public byte[] getSalt() {
            return this.salt;
        }

        public int getIterations() {
            return this.iterations;
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public void setBytes(byte[] alreadyHashedBytes) {
            this.bytes = alreadyHashedBytes;
            this.hexEncoded = null;
            this.base64Encoded = null;
        }

        public void setIterations(int iterations) {
            this.iterations = Math.max(DEFAULT_ITERATIONS, iterations);
        }

        public void setSalt(byte[] salt) {
            this.salt = salt;
        }

        protected MessageDigest getDigest(String algorithmName) throws Exception {
            try {
                return MessageDigest.getInstance(algorithmName);
            } catch (NoSuchAlgorithmException e) {
                String msg = "No native '" + algorithmName + "' MessageDigest instance available on the current JVM.";
                throw new Exception(msg, e);
            }
        }

        protected byte[] hash(byte[] bytes) throws Exception {
            return hash(bytes, null, DEFAULT_ITERATIONS);
        }

        protected byte[] hash(byte[] bytes, byte[] salt) throws Exception {
            return hash(bytes, salt, DEFAULT_ITERATIONS);
        }

        protected byte[] hash(byte[] bytes, byte[] salt, int hashIterations) throws Exception {
            MessageDigest digest = getDigest(getAlgorithmName());
            if (salt != null) {
                digest.reset();
                digest.update(salt);
            }
            byte[] hashed = digest.digest(bytes);
            int iterations = hashIterations - 1; //already hashed once above
            //iterate remaining number:
            for (int i = 0; i < iterations; i++) {
                digest.reset();
                hashed = digest.digest(hashed);
            }
            return hashed;
        }

        public boolean isEmpty() {
            return this.bytes == null || this.bytes.length == 0;
        }

        public String toHex() {
            if (this.hexEncoded == null) {
                this.hexEncoded = Hex.encodeHexString(getBytes());
            }
            return this.hexEncoded;
        }

        public String toBase64() {
            if (this.base64Encoded == null) {
                //cache result in case this method is called multiple times.
                this.base64Encoded = Base64.encodeBase64String(getBytes());
            }
            return this.base64Encoded;
        }

        public String toString() {
            return toHex();
        }

        public boolean equals(Object o) {
            if (o instanceof SimpleHash) {
                SimpleHash other = (SimpleHash) o;
                return MessageDigest.isEqual(getBytes(), other.getBytes());
            }
            return false;
        }

        public int hashCode() {
            if (this.bytes == null || this.bytes.length == 0) {
                return 0;
            }
            return Arrays.hashCode(this.bytes);
        }
    }


}
