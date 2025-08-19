package com.dp.plat.data.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author w02611
 *
 */
public class Result {

	/**
	 * 执行状态
	 */
	private Object status;
	/**
	 * 执行结果
	 */
	private boolean success;

	/**
	 * 结果集
	 */
	private Object data;

	/**
	 * 返回信息
	 */
	private String message;

	/**
	 * 返回状态码
	 */
	private String code;

	public Result() {
		this.success = true;
		this.status = true;
	}

	public Result(boolean success) {
		this.success = success;
		this.status = success;
	}
	
	public Result(boolean success, Object data) {
		this(success);
		this.success = success;
		this.data = data;
	}

	public Result(boolean success, Object data, String message) {
		this(success);
		this.success = success;
		this.data = data;
		this.message = message;
	}

	public Result(boolean success, Object data, String message, String code) {
		this(success);
		this.success = success;
		this.data = data;
		this.message = message;
		this.code = code;
	}

	public Result(boolean success, String message) {
		this(success);
		this.success = success;
		this.message = message;
	}
	
	public Result(Object status) {
		if (status instanceof Boolean) {
			this.success = (boolean) status;
		}
		this.status = status;
	}
	
	public Result(Object status, Object data) {
		this(status);
		this.status = status;
		this.data = data;
	}

	public Result(Object status, Object data, String message) {
		this(status);
		this.status = status;
		this.data = data;
		this.message = message;
	}

	public Result(Object status, Object data, String message, String code) {
		this(status);
		this.status = status;
		this.data = data;
		this.message = message;
		this.code = code;
	}

	public Result(Object status, String message) {
		this(status);
		this.status = status;
		this.message = message;
	}

	public Result(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getStatus() {
		return status;
	}

	public void setStatus(Object status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Result success(boolean success) {
		this.success = success;
		return this;
	}

	public Result status(Object status) {
		this.status = status;
		return this;
	}

	public Result data(Object data) {
		this.data = data;
		return this;
	}

	public Result message(String message) {
		this.message = message;
		return this;
	}

	public Result code(String code) {
		this.code = code;
		return this;
	}

	public Map<String, Object> getMap() {
	    Map<String, Object> map = new HashMap<>();
	    map.put("success", this.success);
	    if (this.status != null) {
	    	map.put("status", this.status);
	    }
    	if (this.data != null) {
    		map.put("data", this.data);
    	}
    	if (this.message != null) {
    		map.put("message", this.message);
    	}
	    if (this.code != null) {
    		map.put("code", this.code);
    	}
	    return map;
	}


	public static Result success() {
		return new Result(true);
	}

	public static Result success(Object data) {
		return new Result(true, data);
	}

	public static Result fail(String message) {
		return new Result(false, message);
	}


}
