package com.dp.plat.activiti.process.exception;

import org.activiti.engine.ActivitiException;

import com.dp.plat.core.exception.CustomExceptionInterface;
/**
 * 自定义Activiti异常，用于捕获主动抛出的异常
 * @author w02611
 *
 */
public class CustomActivitiException extends ActivitiException implements CustomExceptionInterface {

	private static final long serialVersionUID = 1L;

	public CustomActivitiException(String message) {
		super(message);
	}

	public CustomActivitiException(String message, Throwable cause) {
		super(message, cause);
	}

}
