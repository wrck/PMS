package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Response
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Integer SUCCESS_CODE = 200;

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "data")
    private List<Map<String, Object>> data = new ArrayList<>();

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

    public Response data(java.util.List<Map<String, Object>> data) {
        this.data = data;
        return this;
    }

    public Response addDataItem(Map<String, Object> dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     * 
     * @return data
     */
    public java.util.List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(java.util.List<Map<String, Object>> data) {
        this.data = data;
    }

    /**
     * 是否成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.code);
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
                && Objects.equals(this.data, response.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Response {\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
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
