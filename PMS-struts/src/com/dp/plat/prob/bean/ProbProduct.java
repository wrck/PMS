package com.dp.plat.prob.bean;

import java.util.Date;

import com.dp.plat.data.bean.CustomInfoEntity;

public class ProbProduct extends CustomInfoEntity {

    private static final long serialVersionUID = 5041982605339160604L;

    private Integer id;

    // ProbId
    private Integer probId;

    // 产品大类
    private String productCode;

    // 产品小类
    private String productSubCode;

    // item编码
    private String itemCode;

    // item类型
    private String itemModel;

    // item描述
    private String itemDesc;

    // 0 失效 1 有效
    private Integer status;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取ProbId
     *
     * @return probId - ProbId
     */
    public Integer getProbId() {
        return probId;
    }

    /**
     * 设置ProbId
     *
     * @param probId ProbId
     */
    public void setProbId(Integer probId) {
        this.probId = probId;
    }

    /**
     * 获取产品大类
     *
     * @return productCode - 产品大类
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * 设置产品大类
     *
     * @param productCode 产品大类
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * 获取产品小类
     *
     * @return productSubCode - 产品小类
     */
    public String getProductSubCode() {
        return productSubCode;
    }

    /**
     * 设置产品小类
     *
     * @param productSubCode 产品小类
     */
    public void setProductSubCode(String productSubCode) {
        this.productSubCode = productSubCode;
    }

    /**
     * 获取item编码
     *
     * @return itemCode - item编码
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * 设置item编码
     *
     * @param itemCode item编码
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 获取item类型
     *
     * @return itemModel - item类型
     */
    public String getItemModel() {
        return itemModel;
    }

    /**
     * 设置item类型
     *
     * @param itemModel item类型
     */
    public void setItemModel(String itemModel) {
        this.itemModel = itemModel;
    }

    /**
     * 获取item描述
     *
     * @return itemDesc - item描述
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     * 设置item描述
     *
     * @param itemDesc item描述
     */
    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    /**
     * 获取0 失效 1 有效
     *
     * @return status - 0 失效 1 有效
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置0 失效 1 有效
     *
     * @param status 0 失效 1 有效
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return createBy
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * @return createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return updateBy
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * @return updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
