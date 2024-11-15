package com.dp.plat.data.bean;

public class Arg extends BaseBean {

    private Integer id;
    private String code;
    private String var;
    private Object cache;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public Object getCache() {
        return cache;
    }

    public void setCache(Object cache) {
        this.cache = cache;
    }

    public Integer getIntegerValue() {
        try {
            return Integer.parseInt(var);
        } catch (Exception e) {
        }
        return null;
    }

    public Long getLongValue() {
        try {
            return Long.parseLong(var);
        } catch (Exception e) {
        }
        return null;
    }

    public Double getDoubleValue() {
        try {
            return Double.parseDouble(var);
        } catch (Exception e) {
        }
        return null;
    }
    
    public Boolean getBooleanValue() {
        try {
            return Boolean.parseBoolean(var) || "1".equals(var);
        } catch (Exception e) {
        }
        return false;
    }

    public String getTextValue() {
        return var;
    }

    public String getBigTextValue() {
        return var;
    }

}
