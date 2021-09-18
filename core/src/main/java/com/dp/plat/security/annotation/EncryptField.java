package com.dp.plat.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 安全字段注解 加在需要加密/解密的字段上，配合EncryptEntity使用
 * 
 * @author w02611
 * @see EncryptEntity
 */
@Documented
@Target({ ElementType.FIELD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface EncryptField {
}