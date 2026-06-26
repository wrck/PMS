package com.dp.plat.data.bean;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.bean.Prob;

public class ProjectSoftVersion extends ShipmentInfo {
    private static final long serialVersionUID = -7597759449171928749L;
    
    // 项目软件版本表
    private Integer id;

    // 软件版本变更记录
    private Integer logId;

    // 数据状态 0 失效 1 有效
    private Integer datastate;
    
    // 辅助字段
    private String officeCode;
    private String marketCode;
    private String systemCode;
    private String expendCode;
    private String industryCode;
    private String officeName;
    private String marketName;
    private String systemName;
    private String expendName;
    private String industryName;
    // 检查发货
    private Boolean checkShipment;
    // 检查无效发货记录
    private Boolean updateInvalidShipment;
    // 检查conp是否有值
    private Boolean checkHasConp;
    // 检查conpMark为空的内容
    private Boolean checkNullConpMark;
    // 查询软件版本受影响技术公告
    private Boolean queryAffectedProbs;
    // 受影响软件版本技术公告技术公告
    private List<Prob> affectedProbs;

    private DisplayParam displayParam;
    
    
    /**
     * 获取项目软件版本表
     *
     * @return id - 项目软件版本表
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置项目软件版本表
     *
     * @param id 项目软件版本表
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取软件版本变更记录
     *
     * @return logId - 软件版本变更记录
     */
    public Integer getLogId() {
        return logId;
    }

    /**
     * 设置软件版本变更记录
     *
     * @param logId 软件版本变更记录
     */
    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    /**
     * 获取数据状态 0 失效 1 有效
     *
     * @return datastate - 数据状态 0 失效 1 有效
     */
    public Integer getDatastate() {
        return datastate;
    }

    /**
     * 设置数据状态 0 失效 1 有效
     *
     * @param datastate 数据状态 0 失效 1 有效
     */
    public void setDatastate(Integer datastate) {
        this.datastate = datastate;
    }
    
    
    /* 辅助字段 */
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getExpendCode() {
        return expendCode;
    }

    public void setExpendCode(String expendCode) {
        this.expendCode = expendCode;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getExpendName() {
        return expendName;
    }

    public void setExpendName(String expendName) {
        this.expendName = expendName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }
    
    public Boolean getCheckShipment() {
        return checkShipment;
    }

    public void setCheckShipment(Boolean checkShipment) {
        this.checkShipment = checkShipment;
    }
    
    public Boolean getUpdateInvalidShipment() {
        return updateInvalidShipment;
    }

    public void setUpdateInvalidShipment(Boolean updateInvalidShipment) {
        this.updateInvalidShipment = updateInvalidShipment;
    }

    public Boolean getCheckHasConp() {
        return checkHasConp;
    }

    public void setCheckHasConp(Boolean checkHasConp) {
        this.checkHasConp = checkHasConp;
    }
    
    public Boolean getQueryAffectedProbs() {
        return queryAffectedProbs;
    }

    public void setQueryAffectedProbs(Boolean queryAffectedProbs) {
        this.queryAffectedProbs = queryAffectedProbs;
    }

    public List<Prob> getAffectedProbs() {
        return affectedProbs;
    }

    public void setAffectedProbs(List<Prob> affectedProbs) {
        this.affectedProbs = affectedProbs;
    }
    
    public void setAffectedProbsJson(String affectedProbsJson) {
        this.affectedProbs = JSON.parseArray(affectedProbsJson, Prob.class);
    }

    public Boolean getCheckNullConpMark() {
        return checkNullConpMark;
    }

    public void setCheckNullConpMark(Boolean checkNullConpMark) {
        this.checkNullConpMark = checkNullConpMark;
    }

    public DisplayParam getDisplayParam() {
        return displayParam;
    }

    public void setDisplayParam(DisplayParam displayParam) {
        this.displayParam = displayParam;
    }

}