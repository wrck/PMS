package com.dp.plat.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 字段加密 TypeHandler。
 *
 * <p>配合 {@link AesGcmEncryptor} 实现：</p>
 * <ul>
 *     <li>写入数据库（{@code setNonNullParameter}）时自动加密明文；</li>
 *     <li>读取数据库（{@code getNullableResult}）时自动解密密文。</li>
 * </ul>
 *
 * <p>使用方式（任选其一）：</p>
 * <ol>
 *     <li>实体字段标注 {@code @TableField(typeHandler = EncryptTypeHandler.class)}，
 *     MyBatis-Plus 自动应用；</li>
 *     <li>Mapper XML 中在 {@code <result>} 或参数上声明
 *     {@code typeHandler="com.dp.plat.common.crypto.EncryptTypeHandler"}。</li>
 * </ol>
 *
 * <p>注意：当字段值为 null 或空串时不做加解密处理，原样存取。</p>
 *
 * <p><b>重要</b>：此类 intentionally 不使用 {@code @Component} / {@code @MappedTypes} 注解。
 * 否则 MyBatis-Spring 会将其注册为所有 String 字段的默认 TypeHandler，
 * 导致全部字符串字段被错误加密。加密器通过 {@link AesGcmEncryptor#getInstance()}
 * 静态获取，由 Spring 管理的 {@link AesGcmEncryptor} bean 在 {@code @PostConstruct}
 * 阶段完成静态实例注入。</p>
 */
@Slf4j
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    /**
     * 无参构造器：由 MyBatis-Plus 在解析 {@code @TableField(typeHandler = ...)} 时通过反射调用。
     * 加密器在首次使用时通过 {@link AesGcmEncryptor#getInstance()} 懒加载，
     * 避免 MyBatis mapper 解析阶段 AesGcmEncryptor bean 尚未初始化的时序问题。
     */
    public EncryptTypeHandler() {
        // no-op：加密器在首次加/解密时懒加载
    }

    /** 懒加载获取加密器实例。 */
    private AesGcmEncryptor encryptor() {
        return AesGcmEncryptor.getInstance();
    }

    /**
     * 写入参数时加密：将明文加密为 Base64 密文后设置到 PreparedStatement。
     *
     * @param ps        PreparedStatement
     * @param i         参数索引
     * @param parameter 明文参数
     * @param jdbcType  JDBC 类型
     * @throws SQLException SQL 异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        String encrypted = encryptor().encrypt(parameter);
        ps.setString(i, encrypted);
    }

    /**
     * 按列名读取时解密。
     *
     * @param rs         ResultSet
     * @param columnName 列名
     * @return 解密后的明文
     * @throws SQLException SQL 异常
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decryptSafely(value);
    }

    /**
     * 按列索引读取时解密。
     *
     * @param rs          ResultSet
     * @param columnIndex 列索引
     * @return 解密后的明文
     * @throws SQLException SQL 异常
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decryptSafely(value);
    }

    /**
     * 从 CallableStatement 读取时解密。
     *
     * @param cs          CallableStatement
     * @param columnIndex 列索引
     * @return 解密后的明文
     * @throws SQLException SQL 异常
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decryptSafely(value);
    }

    /**
     * 安全解密：对 null/空串原样返回；解密失败时记录日志并返回原值（兼容历史明文数据）。
     *
     * @param value 数据库中的值（密文或历史明文）
     * @return 解密后的明文，或原值
     */
    private String decryptSafely(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            return encryptor().decrypt(value);
        } catch (Exception e) {
            // 解密失败说明该值可能是历史明文数据，原样返回并记录警告
            log.warn("字段解密失败，可能为历史明文数据，原样返回。原因：{}", e.getMessage());
            return value;
        }
    }
}
