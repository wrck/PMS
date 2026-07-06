package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.annotation.FieldEncrypt;
import com.dp.plat.common.crypto.EncryptTypeHandler;
import com.dp.plat.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * System user entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user", autoResultMap = true)
public class SysUser extends BaseEntity {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 登录密码（BCrypt 加密存储）。
     *
     * <p>{@code @JsonProperty(access = WRITE_ONLY)} 确保序列化（响应）时不输出密码，
     * 仅在反序列化（创建/更新用户请求）时接收密码字段。</p>
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Size(max = 50, message = "真实姓名长度不能超过 50 个字符")
    private String realName;

    /**
     * 邮箱（AES-256-GCM 字段级加密存储）。
     *
     * <p>通过 {@link EncryptTypeHandler} 在数据库读写时自动加解密，
     * {@code autoResultMap = true} 使 MyBatis-Plus 自动应用 typeHandler。</p>
     */
    @FieldEncrypt
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String email;

    /**
     * 手机号（AES-256-GCM 字段级加密存储）。
     *
     * <p>通过 {@link EncryptTypeHandler} 在数据库读写时自动加解密。</p>
     */
    @FieldEncrypt
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String phone;

    /** 0=normal, 1=disabled. */
    @Pattern(regexp = "^[01]$", message = "状态只能为 0(正常) 或 1(禁用)")
    private String status;

    private Long deptId;

    private Long companyId;
}
