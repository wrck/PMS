package com.dp.plat.core.exception;

import org.apache.shiro.authc.AuthenticationException;
/**
 * 自定义验证码异常类
 * @author j01441
 *
 */
public class CaptchaException extends AuthenticationException implements CustomExceptionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CaptchaException() {

		super();

	}

	public CaptchaException(String message, Throwable cause) {

		super(message, cause);

	}

	public CaptchaException(String message) {

		super(message);

	}

	public CaptchaException(Throwable cause) {

		super(cause);

	}

	
}
