package com.dp.plat.core.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author w02611
 *
 */
public class Result {

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
	}

	public Result(boolean success) {
		this.success = success;
	}

	public Result(boolean success, Object data) {
		this.success = success;
		this.data = data;
	}

	public Result(boolean success, Object data, String message) {

		this.success = success;
		this.data = data;
		this.message = message;
	}

	public Result(boolean success, Object data, String message, String code) {
		this.success = success;
		this.data = data;
		this.message = message;
		this.code = code;
	}

	public Result(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public Result(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public Result(ResultCode rc) {
		this.code = rc.getCode();
		this.message = rc.getMessage();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public Map<String, Object> getMap() {
	    Map<String, Object> map = new HashMap<>();
	    map.put("success", this.success);
	    map.put("data", this.data);
	    map.put("message", this.message);
	    map.put("code", this.code);
	    return map;
	}
}
