package com.dp.plat.system.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SysUser} Jackson 序列化测试。
 *
 * <p>验证密码字段保护：序列化时不输出 password，反序列化时可接收 password
 * （用于创建用户场景）。</p>
 */
class SysUserSerializationTest {

    /** Jackson ObjectMapper，配置为与 Spring Boot 默认一致。 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("序列化 SysUser 时不应输出 password 字段")
    void serializeShouldNotOutputPassword() throws Exception {
        SysUser user = SysUser.builder()
                .username("admin")
                .password("$2a$10$sensitiveBcryptHashShouldNotLeak")
                .realName("管理员")
                .email("admin@example.com")
                .phone("13800138000")
                .status("0")
                .build();

        String json = objectMapper.writeValueAsString(user);
        JsonNode node = objectMapper.readTree(json);

        assertThat(node.has("password"))
                .as("序列化结果不应包含 password 字段")
                .isFalse();
        assertThat(json.contains("sensitiveBcryptHashShouldNotLeak"))
                .as("序列化结果不应泄露密码哈希值")
                .isFalse();

        // 其他非敏感字段应正常输出
        assertThat(node.get("username").asText()).isEqualTo("admin");
        assertThat(node.get("realName").asText()).isEqualTo("管理员");
        assertThat(node.get("email").asText()).isEqualTo("admin@example.com");
        assertThat(node.get("phone").asText()).isEqualTo("13800138000");
    }

    @Test
    @DisplayName("反序列化时应能接收 password 字段（用于创建用户）")
    void deserializeShouldAcceptPassword() throws Exception {
        String json = "{\"username\":\"newuser\",\"password\":\"PlainPass123\",\"realName\":\"新用户\","
                + "\"email\":\"new@example.com\",\"phone\":\"13900139000\",\"status\":\"0\"}";

        SysUser user = objectMapper.readValue(json, SysUser.class);

        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getPassword())
                .as("反序列化应能接收 password 字段")
                .isEqualTo("PlainPass123");
        assertThat(user.getRealName()).isEqualTo("新用户");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPhone()).isEqualTo("13900139000");
    }

    @Test
    @DisplayName("password 为 null 时序列化也不应出现 password 字段")
    void serializeNullPasswordShouldNotOutputField() throws Exception {
        SysUser user = SysUser.builder()
                .username("guest")
                .build();

        String json = objectMapper.writeValueAsString(user);
        JsonNode node = objectMapper.readTree(json);

        assertThat(node.has("password"))
                .as("password 为 null 时序列化结果也不应包含 password 字段")
                .isFalse();
    }

    @Test
    @DisplayName("LoginResponse 不应包含 password 字段")
    void loginResponseShouldNotContainPassword() throws Exception {
        // LoginResponse 仅含 token/userId/username/realName，无敏感字段
        Class<?> loginResponseClass = Class.forName("com.dp.plat.system.dto.LoginResponse");
        boolean hasPasswordField = Arrays.stream(loginResponseClass.getDeclaredFields())
                .anyMatch(f -> "password".equalsIgnoreCase(f.getName()));

        assertThat(hasPasswordField)
                .as("LoginResponse 不应声明 password 字段")
                .isFalse();
    }
}
