package com.dp.plat.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class PasswordUtilTest {

    @Test
    public void testEncryptPassword_NotNull() {
        String encrypted = PasswordUtil.encryptPassword("salt123", "password123");
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncryptPassword_Consistent() {
        // 同样的输入应该产生同样的输出
        String encrypted1 = PasswordUtil.encryptPassword("salt123", "password123");
        String encrypted2 = PasswordUtil.encryptPassword("salt123", "password123");
        assertEquals(encrypted1, encrypted2);
    }

    @Test
    public void testEncryptPassword_DifferentSalt() {
        // 不同的盐值应该产生不同的输出
        String encrypted1 = PasswordUtil.encryptPassword("salt1", "password123");
        String encrypted2 = PasswordUtil.encryptPassword("salt2", "password123");
        assertFalse(encrypted1.equals(encrypted2));
    }

    @Test
    public void testEncryptPassword_DifferentPassword() {
        // 不同的密码应该产生不同的输出
        String encrypted1 = PasswordUtil.encryptPassword("salt123", "password1");
        String encrypted2 = PasswordUtil.encryptPassword("salt123", "password2");
        assertFalse(encrypted1.equals(encrypted2));
    }

    @Test
    public void testEncryptMD5Password_NotNull() {
        String encrypted = PasswordUtil.encryptMD5Password("test");
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncryptMD5Password_WithSalt() {
        String encrypted = PasswordUtil.encryptMD5Password("test", "salt");
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncryptMD5Password_WithIterations() {
        String encrypted = PasswordUtil.encryptMD5Password("test", "salt", 10);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncryptSHA1Password_NotNull() {
        String encrypted = PasswordUtil.encryptSHA1Password("test");
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncryptSHA1Password_WithSalt() {
        String encrypted = PasswordUtil.encryptSHA1Password("test", "salt");
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncryptSHA1Password_WithIterations() {
        String encrypted = PasswordUtil.encryptSHA1Password("test", "salt", 10);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void testEncrypt_InvalidAlgorithm() {
        // 使用无效的算法应该返回null
        String encrypted = PasswordUtil.encrypt("INVALID_ALGORITHM", "test");
        assertNull(encrypted);
    }

    @Test
    public void testCreateRandomPassword_DefaultLength() {
        String password = PasswordUtil.createRandomPassword();
        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    public void testCreateRandomPassword_CustomLength() {
        String password = PasswordUtil.createRandomPassword(16);
        assertNotNull(password);
        assertEquals(16, password.length());
    }

    @Test
    public void testCreateRandomPassword_SmallLength() {
        // 小于8的长度应该使用默认长度8
        String password = PasswordUtil.createRandomPassword(4);
        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    public void testGeneratePass() {
        String password = PasswordUtil.generatePass();
        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    public void testSimpleHash_Equals() {
        try {
            PasswordUtil.SimpleHash hash1 = new PasswordUtil.SimpleHash("MD5", "test", "salt", 1);
            PasswordUtil.SimpleHash hash2 = new PasswordUtil.SimpleHash("MD5", "test", "salt", 1);
            assertEquals(hash1, hash2);
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_NotEquals() {
        try {
            PasswordUtil.SimpleHash hash1 = new PasswordUtil.SimpleHash("MD5", "test1", "salt", 1);
            PasswordUtil.SimpleHash hash2 = new PasswordUtil.SimpleHash("MD5", "test2", "salt", 1);
            assertFalse(hash1.equals(hash2));
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_IsEmpty() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5");
            assertTrue(hash.isEmpty());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_IsNotEmpty() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test");
            assertFalse(hash.isEmpty());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_HashCode() {
        try {
            PasswordUtil.SimpleHash hash1 = new PasswordUtil.SimpleHash("MD5", "test", "salt", 1);
            PasswordUtil.SimpleHash hash2 = new PasswordUtil.SimpleHash("MD5", "test", "salt", 1);
            assertEquals(hash1.hashCode(), hash2.hashCode());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_ToHex() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test");
            String hex = hash.toHex();
            assertNotNull(hex);
            assertFalse(hex.isEmpty());
            // Hex编码应该是偶数长度
            assertEquals(0, hex.length() % 2);
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_ToBase64() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test");
            String base64 = hash.toBase64();
            assertNotNull(base64);
            assertFalse(base64.isEmpty());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_GetIterations() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test", "salt", 5);
            assertEquals(5, hash.getIterations());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_GetAlgorithmName() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("SHA1", "test");
            assertEquals("SHA1", hash.getAlgorithmName());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_GetSalt() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test", "salt");
            assertNotNull(hash.getSalt());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_SetIterations() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test");
            hash.setIterations(10);
            assertEquals(10, hash.getIterations());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testSimpleHash_MinIterations() {
        try {
            PasswordUtil.SimpleHash hash = new PasswordUtil.SimpleHash("MD5", "test");
            // 设置小于1的值应该被调整为1
            hash.setIterations(0);
            assertEquals(1, hash.getIterations());
        } catch (Exception e) {
            fail("不应抛出异常: " + e.getMessage());
        }
    }
}
