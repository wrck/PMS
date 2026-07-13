package com.dp.plat.common.crypto;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 字段级加密器。
 *
 * <p>特性：</p>
 * <ul>
 *     <li>密钥从配置 {@code app.security.encrypt-key} 读取，Base64 解码后必须为 32 字节（256 位）；</li>
 *     <li>每次加密生成随机 12 字节 IV（GCM 推荐长度）；</li>
 *     <li>输出格式：{@code Base64(IV(12B) || ciphertext+tag)}，其中 GCM tag 长度为 128 位（16 字节）；</li>
 *     <li>相同明文每次密文不同（IV 随机），保证语义安全。</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 *   String cipher = encryptor.encrypt("13800138000");
 *   String plain  = encryptor.decrypt(cipher);
 * }</pre>
 */
@Slf4j
@Component
public class AesGcmEncryptor {

    /** GCM 推荐 IV 长度：12 字节。 */
    private static final int IV_LENGTH_BYTES = 12;

    /** GCM 认证标签长度：128 位（16 字节）。 */
    private static final int GCM_TAG_LENGTH_BITS = 128;

    /** AES-256 密钥长度：32 字节（256 位）。 */
    private static final int KEY_LENGTH_BYTES = 32;

    /** 加密算法 transformation。 */
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /** 密钥算法。 */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 静态实例引用：在 {@link #init()} 完成后赋值，
     * 供非 Spring 管理的对象（如 {@link EncryptTypeHandler}）通过 {@link #getInstance()} 获取。
     */
    private static volatile AesGcmEncryptor instance;

    /** 用于生成随机 IV 的安全随机数生成器。 */
    private final SecureRandom secureRandom = new SecureRandom();

    /** 从配置读取的 Base64 编码密钥。 */
    @Value("${app.security.encrypt-key:}")
    private String encryptKeyBase64;

    /** 解码后的 AES 密钥规格。 */
    private SecretKeySpec secretKeySpec;

    /**
     * 获取已初始化的静态实例（供非 Spring 管理的对象使用）。
     *
     * @return 已初始化的 {@link AesGcmEncryptor} 实例
     * @throws IllegalStateException 如果实例尚未初始化
     */
    public static AesGcmEncryptor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AesGcmEncryptor 尚未初始化");
        }
        return instance;
    }

    /**
     * 初始化校验：解码并校验密钥长度必须为 32 字节。
     *
     * @throws IllegalStateException 当密钥未配置或长度不合法时抛出
     */
    @PostConstruct
    public void init() {
        if (encryptKeyBase64 == null || encryptKeyBase64.isBlank()) {
            throw new IllegalStateException(
                    "字段加密密钥未配置，请在 application.yml 中设置 app.security.encrypt-key（Base64 编码的 32 字节密钥）");
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(encryptKeyBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("app.security.encrypt-key 不是合法的 Base64 字符串", e);
        }
        if (keyBytes.length != KEY_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "字段加密密钥长度必须为 " + KEY_LENGTH_BYTES + " 字节（AES-256），当前为 " + keyBytes.length + " 字节");
        }
        this.secretKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        instance = this;
        log.info("AesGcmEncryptor 初始化成功，密钥长度 {} 字节", keyBytes.length);
    }

    /**
     * 加密明文字符串。
     *
     * <p>生成随机 12 字节 IV，使用 AES-256-GCM 加密，输出
     * {@code Base64(IV || ciphertext+tag)}。</p>
     *
     * @param plain 明文，允许为 null 或空串（原样返回不加密）
     * @return Base64 编码的密文，输入为 null 时返回 null
     */
    public String encrypt(String plain) {
        if (plain == null) {
            return null;
        }
        if (plain.isEmpty()) {
            return plain;
        }
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

            byte[] cipherText = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            // 拼接 IV || ciphertext+tag 后整体 Base64 编码
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("AES-256-GCM 加密失败", e);
        }
    }

    /**
     * 解密密文字符串。
     *
     * <p>解析 {@code Base64(IV || ciphertext+tag)}，提取前 12 字节 IV，
     * 使用 AES-256-GCM 解密并校验认证标签。</p>
     *
     * @param cipherText Base64 编码的密文，允许为 null 或空串（原样返回）
     * @return 明文字符串，输入为 null 时返回 null
     * @throws IllegalStateException 当密钥不匹配、密文被篡改或格式错误时抛出
     */
    public String decrypt(String cipherText) {
        if (cipherText == null) {
            return null;
        }
        if (cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);
            if (combined.length <= IV_LENGTH_BYTES) {
                // 长度不足，非合法密文，原样返回（兼容历史明文数据）
                return cipherText;
            }
            byte[] iv = new byte[IV_LENGTH_BYTES];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH_BYTES);
            byte[] cipherBytes = new byte[combined.length - IV_LENGTH_BYTES];
            System.arraycopy(combined, IV_LENGTH_BYTES, cipherBytes, 0, cipherBytes.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES-256-GCM 解密失败，可能密钥不匹配或密文被篡改", e);
        }
    }
}
