package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Request
 */
public class Request<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@JSONField(serialize = false, deserialize = false)
	private static ConcurrentMap<Type, Type> classTypeCache = new ConcurrentHashMap<Type, Type>(16, 0.75f, 1);

	@JSONField(serialize = false, deserialize = false)
	private Type responseType;
	
	@JSONField(serialize = false, deserialize = false)
	protected Map<String, String> headers;
	
	@JSONField(name = "request")
	protected Object request;

	public Request() {
		ParameterizedType parameterizedType = null;
		Type clazz = getClass();
		Type superClass = getClass().getGenericSuperclass();

		if (superClass instanceof ParameterizedType) {
			parameterizedType = ((ParameterizedType) superClass);
		} else if (clazz instanceof ParameterizedType) {
			parameterizedType = ((ParameterizedType) clazz);
		}
		Type type = null;
		if (parameterizedType != null) {
			type = parameterizedType.getActualTypeArguments()[0];
			Type cachedType = classTypeCache.get(type);
			if (cachedType == null) {
				classTypeCache.putIfAbsent(type, type);
				cachedType = classTypeCache.get(type);
			}
			this.responseType = cachedType;
		} else {
			this.responseType = Response.class;
		}
		
	}

	public Request<T> request(RequestBody request) {
		this.request = request;
		return this;
	}

	/**
	 * Get request
	 * 
	 * @return request
	 */

	public Object getRequest() {
		return request;
	}

	public void setRequest(Object request) {
		this.request = request;
	}

	public Type getResponseType() {
		return responseType;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Request<?> purchaseRequest = (Request<?>) o;
		return Objects.equals(this.request, purchaseRequest.request);
	}

	@Override
	public int hashCode() {
		return Objects.hash(request);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Request {\n");
		sb.append("    request: ").append(toIndentedString(request)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
