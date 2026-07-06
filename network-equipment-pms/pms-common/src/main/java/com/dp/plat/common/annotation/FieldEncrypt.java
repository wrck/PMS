package com.dp.plat.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段级加密注解。
 *
 * <p>标注在实体字段上，表示该字段需要使用 AES-256-GCM 进行加解密。
 * 加解密由两层机制保障：</p>
 * <ol>
 *     <li>MyBatis {@code TypeHandler}（{@link com.dp.plat.common.crypto.EncryptTypeHandler}）
 *     在数据库读写时自动加解密；</li>
 *     <li>{@link com.dp.plat.common.aspect.FieldEncryptAspect} 作为兜底，对未经过
 *     TypeHandler 处理的返回对象（如跨服务调用、手写 SQL）执行解密。</li>
 * </ol>
 *
 * <p>密文格式：{@code Base64(IV(12B) || ciphertext+tag)}，每次加密生成随机 IV，
 * 相同明文每次密文不同。</p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldEncrypt {

    /**
     * 加密算法，默认 AES/GCM/NoPadding（即 AES-256-GCM）。
     *
     * @return 算法 transformation
     */
    String algorithm() default "AES/GCM/NoPadding";

    /**
     * 密钥配置项名，对应 application.yml 中的属性 key。
     *
     * @return 配置项名
     */
    String key() default "app.security.encrypt-key";
}
