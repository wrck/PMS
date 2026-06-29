package com.dp.plat.ehr.entity;

import com.dp.plat.core.entity.BaseEntity;

public class AppraiserRelationship extends BaseEntity {
    //被评估人userId
    private Integer appraiseeId;

    //被评估人username
    private String appraiseeName;

    //评估人userId
    private Integer appraiserId;

    //评估人username
    private String appraiserName;

    //是否为直接主管
    private Boolean isDirectSupervisor;

    //评估人类型,（上级/同事/下属/自评）
    private String type;

    //评估人类型权重,0~100
    private Byte typeWeight;

    //评估人类型权重占比,0~100；weight=typeWeight*personalWeight/100
    private Byte personalWeight;

    //评估人的优先级别,越大优先级越高,越后评估
    private Byte priority;

    private Boolean state;

    /**
     * 获取被评估人userId
     *
     * @return appraiseeId - 被评估人userId
     */
    public Integer getAppraiseeId() {
        return appraiseeId;
    }

    /**
     * 设置被评估人userId
     *
     * @param appraiseeId 被评估人userId
     */
    public void setAppraiseeId(Integer appraiseeId) {
        this.appraiseeId = appraiseeId;
    }

    /**
     * 获取被评估人username
     *
     * @return appraiseeName - 被评估人username
     */
    public String getAppraiseeName() {
        return appraiseeName;
    }

    /**
     * 设置被评估人username
     *
     * @param appraiseeName 被评估人username
     */
    public void setAppraiseeName(String appraiseeName) {
        this.appraiseeName = appraiseeName;
    }

    /**
     * 获取评估人userId
     *
     * @return appraiserId - 评估人userId
     */
    public Integer getAppraiserId() {
        return appraiserId;
    }

    /**
     * 设置评估人userId
     *
     * @param appraiserId 评估人userId
     */
    public void setAppraiserId(Integer appraiserId) {
        this.appraiserId = appraiserId;
    }

    /**
     * 获取评估人username
     *
     * @return appraiserName - 评估人username
     */
    public String getAppraiserName() {
        return appraiserName;
    }

    /**
     * 设置评估人username
     *
     * @param appraiserName 评估人username
     */
    public void setAppraiserName(String appraiserName) {
        this.appraiserName = appraiserName;
    }

    /**
     * 获取是否为直接主管
     *
     * @return isDirectSupervisor - 是否为直接主管
     */
    public Boolean getIsDirectSupervisor() {
        return isDirectSupervisor;
    }

    /**
     * 设置是否为直接主管
     *
     * @param isDirectSupervisor 是否为直接主管
     */
    public void setIsDirectSupervisor(Boolean isDirectSupervisor) {
        this.isDirectSupervisor = isDirectSupervisor;
    }

    /**
     * 获取评估人类型,（上级/同事/下属/自评）
     *
     * @return type - 评估人类型,（上级/同事/下属/自评）
     */
    public String getType() {
        return type;
    }

    /**
     * 设置评估人类型,（上级/同事/下属/自评）
     *
     * @param type 评估人类型,（上级/同事/下属/自评）
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取评估人类型权重,0~100
     *
     * @return typeWeight - 评估人类型权重,0~100
     */
    public Byte getTypeWeight() {
        return typeWeight;
    }

    /**
     * 设置评估人类型权重,0~100
     *
     * @param typeWeight 评估人类型权重,0~100
     */
    public void setTypeWeight(Byte typeWeight) {
        this.typeWeight = typeWeight;
    }

    /**
     * 获取评估人类型权重占比,0~100；weight=typeWeight*personalWeight/100
     *
     * @return personalWeight - 评估人类型权重占比,0~100；weight=typeWeight*personalWeight/100
     */
    public Byte getPersonalWeight() {
        return personalWeight;
    }

    /**
     * 设置评估人类型权重占比,0~100；weight=typeWeight*personalWeight/100
     *
     * @param personalWeight 评估人类型权重占比,0~100；weight=typeWeight*personalWeight/100
     */
    public void setPersonalWeight(Byte personalWeight) {
        this.personalWeight = personalWeight;
    }

    /**
     * 获取评估人的优先级别,越大优先级越高,越后评估
     *
     * @return priority - 评估人的优先级别,越大优先级越高,越后评估
     */
    public Byte getPriority() {
        return priority;
    }

    /**
     * 设置评估人的优先级别,越大优先级越高,越后评估
     *
     * @param priority 评估人的优先级别,越大优先级越高,越后评估
     */
    public void setPriority(Byte priority) {
        this.priority = priority;
    }

    /**
     * @return state
     */
    public Boolean getState() {
        return state;
    }

    /**
     * @param state
     */
    public void setState(Boolean state) {
        this.state = state;
    }
}