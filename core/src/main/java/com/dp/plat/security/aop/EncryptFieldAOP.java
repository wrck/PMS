package com.dp.plat.security.aop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.dp.plat.security.annotation.EncryptEntity;
import com.dp.plat.security.annotation.EncryptField;
import com.dp.plat.security.util.ASEUtil;

/**
 * 安全字段加密解密切面
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Aspect
@Component
public class EncryptFieldAOP {
	Logger log = LoggerFactory.getLogger(this.getClass());
	@Value("${secretkey}")
	private String secretKey;

	@Pointcut("execution(* com.dp.plat..*.dao..*.*(..)) || within(com.dp.plat..*.dao..*)")
	public void annotationPointCut() {
	}

	@Around("annotationPointCut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Object responseObj = null;
		try {
			Object[] args = joinPoint.getArgs();
			for (int i = 0; i < args.length; i++) {
				Object requestObj = args[i];
				args[i] = handleEncrypt(requestObj);
			}
			responseObj = joinPoint.proceed();
			handleDecrypt(responseObj);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			log.error("SecureFieldAop处理出现异常{}", e);
			throw e;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			log.error("SecureFieldAop处理出现异常{}", throwable);
			throw throwable;
		}
		return responseObj;
	}

	/**
	 * 处理加密
	 *
	 * @param requestObj
	 */
	private Object handleEncrypt(Object requestObj) throws IllegalAccessException {
		if (Objects.isNull(requestObj)) {
			return requestObj;
		}
		if (requestObj instanceof Collection) {
			for (Object obj : (Collection) requestObj) {
				handleEncrypt(obj);
			}
		} else if (requestObj.getClass().isAnnotationPresent(EncryptEntity.class)){
			List<Field> fields = getAllDeclaredFields(requestObj.getClass());
			for (Field field : fields) {
				boolean hasSecureField = field.isAnnotationPresent(EncryptField.class);
				if (hasSecureField) {
					field.setAccessible(true);
					String plaintextValue = (String) field.get(requestObj);
					String encryptValue = ASEUtil.encrypt(plaintextValue, secretKey);
					field.set(requestObj, encryptValue);
				}
			}
		}
		return requestObj;
	}

	/**
	 * 处理解密
	 *
	 * @param responseObj
	 */
	private Object handleDecrypt(Object responseObj) throws IllegalAccessException {
		if (Objects.isNull(responseObj)) {
			return responseObj;
		}

		if (responseObj instanceof Collection) {
			for (Object obj : (Collection) responseObj) {
				handleDecrypt(obj);
			}
		} else if (responseObj.getClass().isAnnotationPresent(EncryptEntity.class)){
			List<Field> fields = getAllDeclaredFields(responseObj.getClass());
			for (Field field : fields) {
				boolean hasSecureField = field.isAnnotationPresent(EncryptField.class);
				if (hasSecureField) {
					field.setAccessible(true);
					String encryptValue = (String) field.get(responseObj);
					String plaintextValue = ASEUtil.decrypt(encryptValue, secretKey);
					field.set(responseObj, plaintextValue);
				}
			}
		}
		return responseObj;
	}
	
	private static List<Field> getAllDeclaredFields(Class<?> cls) {
        final List<Field> fields = new ArrayList<>();
        while (cls != null) {
            for (final Field field : cls.getDeclaredFields()) {
                fields.add(field);
            }
            cls = cls.getSuperclass();
        }
        return fields;
    }
}