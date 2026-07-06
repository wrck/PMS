package com.dp.plat.common.aspect;

import com.dp.plat.common.annotation.FieldEncrypt;
import com.dp.plat.common.crypto.AesGcmEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * 字段级加密兜底切面。
 *
 * <p>MyBatis {@link com.dp.plat.common.crypto.EncryptTypeHandler} 已经在数据库读写层
 * 完成加解密，本切面作为兜底，处理以下场景：</p>
 * <ul>
 *     <li>跨服务调用返回的实体（未经 TypeHandler 处理，字段值仍为密文）；</li>
 *     <li>手写 SQL 或未声明 TypeHandler 的查询，返回对象字段值为密文。</li>
 * </ul>
 *
 * <p>通过 {@code @AfterReturning} 拦截 Service/Controller 层方法返回值，反射查找
 * 标注 {@link FieldEncrypt} 的字段并尝试解密。解密采用「容错」策略：若解密失败
 * （说明值已经是明文或为历史数据），则原样保留不抛异常。</p>
 *
 * <p>注意：本切面仅做「解密」兜底，不做加密加密由 TypeHandler 在写入数据库时完成，
 * 避免双重加密。</p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FieldEncryptAspect {

    /** AES-GCM 加密器。 */
    private final AesGcmEncryptor encryptor;

    /**
     * 拦截 Service 层方法返回值，对带 {@link FieldEncrypt} 注解的字段执行兜底解密。
     *
     * @param result 方法返回值
     */
    @AfterReturning(pointcut = "execution(* com.dp.plat..service..*.*(..))",
            returning = "result")
    public void decryptServiceResult(Object result) {
        handleResult(result);
    }

    /**
     * 拦截 Controller 层方法返回值，对带 {@link FieldEncrypt} 注解的字段执行兜底解密。
     *
     * @param result 方法返回值
     */
    @AfterReturning(pointcut = "execution(* com.dp.plat..controller..*.*(..))",
            returning = "result")
    public void decryptControllerResult(Object result) {
        handleResult(result);
    }

    /**
     * 处理返回值：支持单个实体、集合、嵌套 {@code Result} 包装等情况。
     *
     * @param result 方法返回值
     */
    private void handleResult(Object result) {
        if (result == null) {
            return;
        }
        try {
            // 尝试从 com.dp.plat.common.result.Result 中提取 data 字段
            Object target = unwrapResult(result);
            if (target == null) {
                return;
            }
            if (target instanceof Collection<?> collection) {
                for (Object item : collection) {
                    decryptFields(item);
                }
            } else {
                decryptFields(target);
            }
        } catch (Throwable e) {
            // 切面兜底逻辑不应影响主流程，任何异常仅记录日志
            log.warn("字段加密兜底解密失败，跳过本次处理。原因：{}", e.getMessage());
        }
    }

    /**
     * 若返回值为 {@code com.dp.plat.common.result.Result} 包装类型，反射提取其 data 字段。
     *
     * @param result 原始返回值
     * @return 解包后的数据对象
     */
    private Object unwrapResult(Object result) {
        Class<?> clazz = result.getClass();
        // 通过类名匹配 Result 包装类，避免硬依赖具体包路径
        if ("com.dp.plat.common.result.Result".equals(clazz.getName())) {
            try {
                Field dataField = clazz.getDeclaredField("data");
                dataField.setAccessible(true);
                return dataField.get(result);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return result;
            }
        }
        return result;
    }

    /**
     * 反射解密对象中所有标注 {@link FieldEncrypt} 的字段。
     *
     * <p>解密采用容错策略：单个字段解密失败时跳过该字段，不影响其他字段处理。</p>
     *
     * @param target 目标对象
     */
    private void decryptFields(Object target) {
        if (target == null) {
            return;
        }
        Class<?> clazz = target.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(FieldEncrypt.class)) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object value = field.get(target);
                    if (value instanceof String strValue && !strValue.isEmpty()) {
                        String decrypted = tryDecrypt(strValue);
                        if (decrypted != null) {
                            field.set(target, decrypted);
                        }
                    }
                } catch (Exception e) {
                    log.debug("字段 [{}] 兜底解密跳过：{}", field.getName(), e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 尝试解密：成功返回明文，失败（说明已是明文或非密文）返回 null。
     *
     * @param value 字段当前值
     * @return 解密后的明文，或 null 表示无需解密
     */
    private String tryDecrypt(String value) {
        try {
            return encryptor.decrypt(value);
        } catch (Exception e) {
            // 解密失败说明该值可能是明文，无需处理
            return null;
        }
    }
}
