package com.dp.plat.pms.extend.fp.model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Response
 */
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Integer[] SUCCESS_CODE = new Integer[] {0, 200};

    /**
     * 原始请求
     */
    @JSONField(serialize = false, deserialize = false)
    private Request<T> request;

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "msg", alternateNames = "message")
    private String message;

    @JSONField(name = "data")
    private List<T> data = new ArrayList<T>();
    
    @JSONField(name = "extend")
    private Map<String, Object> extend = new HashMap<String, Object>();

    @JSONField(name = "status")
    private Boolean isSuccess;
    
    /**
     * 原始响应
     */
    @JSONField(serialize = false, deserialize = false)
    private Map<String, List<String>> headers;

    public Response code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Response message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get message
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Response data(List<T> data) {
        this.data = data;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Response addData(T dataItem) {
        if (this.getData() == null) {
            this.setData(new ArrayList<>());
        }
        this.data.add(dataItem);
        return this;
    }

/*    public Response data(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public Response dataList(List<Map<String, Object>> data) {
        this.data = data;
        return this;
    }

    public Response addListData(Map<String, Object> dataItem) {
        if (this.getListData() == null) {
            this.setListData(new ArrayList<>());
        }
        ((List<Map<String, Object>>) this.data).add(dataItem);
        return this;
    }

    *//**
     * Get data
     *
     * @return data
     *//*
    public List<Map<String, Object>> getListData() {
        if (data instanceof List) {
            return (List<Map<String, Object>>) data;
        } else {
            return null;
        }
    }

    public void setListData(List<Map<String, Object>> data) {
        this.data = data;
    }*/

    /**
     * Get data
     *
     * @return data
     */
    public List<T> getList() {
        if (data instanceof List) {
            return (List<T>) data;
        } else {
            return null;
        }
    }

    public void setList(List<T> data) {
        this.data = data;
    }

    public Request getRequest() {
        return request;
    }

    /**
     * 原始请求
     * @param request
     */
    public void setRequest(Request request) {
        this.request = request;
    }
    
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }
    
    public Response extend(Map<String, Object> extend) {
        this.extend = extend;
        return this;
    }
    
    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(getIsSuccess()) || this.code != null && Arrays.asList(SUCCESS_CODE).contains(this.code);
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 获取数据量
     * @return
     */
    public long getDataSize() {
        if (data instanceof Collection) {
            return data != null ? ((Collection) data).size() : 0;
        } else {
            return data != null ? 1 : 0;
        }
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Response response = (Response) o;
        return Objects.equals(this.code, response.code) && Objects.equals(this.message, response.message)
                && Objects.equals(this.data, response.data) && Objects.equals(this.isSuccess, response.isSuccess);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ").append(this.getClass().getName()).append(" {\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    isSuccess: ").append(toIndentedString(isSuccess)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public static <T> Response<T> failure(String message) {
        return new Response<T>().message(message);
    }
    
    public static <T, R extends Response<T>> R failure(String message, Type responseType) {
        Response<T> response = null;
        if (responseType != null && responseType instanceof Class) {
            return failure(message, (Class<R>) responseType);
        }
        return (R) response.message(message);
    }
    
    public static <T, R extends Response<T>> R failure(String message, Class<R> responseClass) {
        Response<T> response = null;
        if (responseClass != null && Response.class.isAssignableFrom(responseClass)) {
            try {
                response = responseClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                response = JSON.parseObject("{}", responseClass);
            }
        } else {
            response = new Response<T>();
        }
        return (R) response.message(message);
    }
}
