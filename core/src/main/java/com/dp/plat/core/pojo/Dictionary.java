package com.dp.plat.core.pojo;

public class Dictionary {
    private Integer id;

    private Integer dicTypeId;

    private String dicTypeName;

    private String dicKey;

    private String dicValue;

    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDicTypeId() {
        return dicTypeId;
    }

    public void setDicTypeId(Integer dicTypeId) {
        this.dicTypeId = dicTypeId;
    }

    public String getDicTypeName() {
        return dicTypeName;
    }

    public void setDicTypeName(String dicTypeName) {
        this.dicTypeName = dicTypeName;
    }

    public String getDicKey() {
        return dicKey;
    }

    public void setDicKey(String dicKey) {
        this.dicKey = dicKey;
    }

    public String getDicValue() {
        return dicValue;
    }

    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}