package com.dp.plat.common.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link AesGcmEncryptor} 单元测试。
 *
 * <p>验证 AES-256-GCM 加解密核心能力：往返正确性、IV 随机性、密钥校验与错误密钥异常。</p>
 */
class AesGcmEncryptorTest {

    /** 32 字节密钥对应的 Base64 字符串。 */
    private static final String KEY_BASE64_1 =
            Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes());

    /** 另一把 32 字节密钥（用于错误密钥场景）。 */
    private static final String KEY_BASE64_2 =
            Base64.getEncoder().encodeToString("fedcba9876543210fedcba9876543210".getBytes());

    /** 被测加密器实例。 */
    private AesGcmEncryptor encryptor;

    @BeforeEach
    void setUp() {
        encryptor = new AesGcmEncryptor();
        ReflectionTestUtils.setField(encryptor, "encryptKeyBase64", KEY_BASE64_1);
        encryptor.init();
    }

    @Test
    @DisplayName("加密后再解密应还原明文")
    void encryptThenDecryptShouldReturnOriginal() {
        String plain = "13800138000";
        String cipher = encryptor.encrypt(plain);

        assertThat(cipher)
                .as("密文不应与明文相同")
                .isNotEqualTo(plain);
        assertThat(encryptor.decrypt(cipher))
                .as("解密后应还原明文")
                .isEqualTo(plain);
    }

    @Test
    @DisplayName("加密中文与特殊字符应正确往返")
    void encryptThenDecryptShouldHandleUnicodeAndSpecialChars() {
        String plain = "用户邮箱:test+tag@example.com#端到端";
        String cipher = encryptor.encrypt(plain);

        assertThat(encryptor.decrypt(cipher)).isEqualTo(plain);
    }

    @Test
    @DisplayName("相同明文每次密文应不同（IV 随机）")
    void samePlainShouldProduceDifferentCipherEachTime() {
        String plain = "confidential-data";

        String cipher1 = encryptor.encrypt(plain);
        String cipher2 = encryptor.encrypt(plain);
        String cipher3 = encryptor.encrypt(plain);

        assertThat(cipher1)
                .as("每次加密的密文应不同（IV 随机）")
                .isNotEqualTo(cipher2)
                .isNotEqualTo(cipher3);

        // 不同密文都应能解密为同一明文
        assertThat(encryptor.decrypt(cipher1)).isEqualTo(plain);
        assertThat(encryptor.decrypt(cipher2)).isEqualTo(plain);
        assertThat(encryptor.decrypt(cipher3)).isEqualTo(plain);
    }

    @Test
    @DisplayName("密文与明文长度均应经过 Base64 编码且包含 IV")
    void cipherShouldBeBase64AndContainIv() {
        String plain = "abcd";
        String cipher = encryptor.encrypt(plain);

        byte[] decoded = Base64.getDecoder().decode(cipher);
        // 密文结构：IV(12) + ciphertext + tag(16)，至少应大于 12 字节
        assertThat(decoded.length)
                .as("密文解码后长度应大于 IV 长度（12 字节）")
                .isGreaterThan(12);
    }

    @Test
    @DisplayName("错误密钥解密应抛出异常（GCM 认证失败）")
    void decryptWithWrongKeyShouldThrow() {
        String plain = "secret-value";
        String cipher = encryptor.encrypt(plain);

        AesGcmEncryptor wrongKeyEncryptor = new AesGcmEncryptor();
        ReflectionTestUtils.setField(wrongKeyEncryptor, "encryptKeyBase64", KEY_BASE64_2);
        wrongKeyEncryptor.init();

        assertThatThrownBy(() -> wrongKeyEncryptor.decrypt(cipher))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("AES-256-GCM 解密失败");
    }

    @Test
    @DisplayName("密文被篡改后解密应抛出异常（认证标签校验失败）")
    void decryptTamperedCipherShouldThrow() {
        String plain = "integrity-check";
        String cipher = encryptor.encrypt(plain);

        // 篡改密文最后一个字符
        char lastChar = cipher.charAt(cipher.length() - 1);
        char tampered = (lastChar == 'A') ? 'B' : 'A';
        String tamperedCipher = cipher.substring(0, cipher.length() - 1) + tampered;

        assertThatThrownBy(() -> encryptor.decrypt(tamperedCipher))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("null 与空串输入应原样返回")
    void nullAndEmptyInputShouldReturnAsIs() {
        assertThat(encryptor.encrypt(null)).isNull();
        assertThat(encryptor.encrypt("")).isEmpty();
        assertThat(encryptor.decrypt(null)).isNull();
        assertThat(encryptor.decrypt("")).isEmpty();
    }

    @Test
    @DisplayName("未配置密钥时初始化应抛出异常")
    void initWithoutKeyShouldThrow() {
        AesGcmEncryptor noKeyEncryptor = new AesGcmEncryptor();
        ReflectionTestUtils.setField(noKeyEncryptor, "encryptKeyBase64", "");

        assertThatThrownBy(noKeyEncryptor::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("字段加密密钥未配置");
    }

    @Test
    @DisplayName("密钥长度不为 32 字节时初始化应抛出异常")
    void initWithInvalidKeyLengthShouldThrow() {
        AesGcmEncryptor shortKeyEncryptor = new AesGcmEncryptor();
        // 16 字节密钥（AES-128），不符合 AES-256 要求
        String shortKey = Base64.getEncoder().encodeToString(new byte[16]);
        ReflectionTestUtils.setField(shortKeyEncryptor, "encryptKeyBase64", shortKey);

        assertThatThrownBy(shortKeyEncryptor::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 字节");
    }

    @Test
    @DisplayName("密钥非合法 Base64 时初始化应抛出异常")
    void initWithInvalidBase64ShouldThrow() {
        AesGcmEncryptor invalidEncryptor = new AesGcmEncryptor();
        ReflectionTestUtils.setField(invalidEncryptor, "encryptKeyBase64", "不是合法的base64!!!");

        assertThatThrownBy(invalidEncryptor::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Base64");
    }
}
