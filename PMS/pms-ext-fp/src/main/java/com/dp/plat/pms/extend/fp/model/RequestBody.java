package com.dp.plat.pms.extend.fp.model;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RequestBody
 */
public class RequestBody {
    @JsonProperty("func")
    @JSONField(name = "func")
    private String func;

    @JsonProperty("mainData")
    @JSONField(name = "mainData")
    @Valid
    private List<?> data = null;

    public RequestBody func(String func) {
        this.func = func;
        return this;
    }

    /**
     * 程序名，程序名，即方法名
     * 
     * @return func
     */
    @NotNull
    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }
    
    /**
     * 推送数据
     * 
     * @return data
     */
    public RequestBody data(List<?> data) {
        this.data = data;
        return this;
    }

    /**
     * 推送数据
     * 
     * @return data
     */
    public List<?> getData() {
        return data;
    }

    /**
     * 设置推送数据
     * @param data
     */
    public void setData(List<?> data) {
        this.data = data;
    }
    
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestBody requestBody = (RequestBody) o;
        return Objects.equals(this.func, requestBody.func);
    }

    @Override
    public int hashCode() {
        return Objects.hash(func);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ").append(this.getClass().getName()).append(" {\n");

        sb.append("    func: ").append(toIndentedString(func)).append("\n");
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
