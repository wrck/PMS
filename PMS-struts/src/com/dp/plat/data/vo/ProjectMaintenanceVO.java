/**
 * 
 */
package com.dp.plat.data.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.ProjectMaintenance;
import com.dp.plat.param.FileParam;

/**
 * @author w02611
 */
public class ProjectMaintenanceVO extends ProjectMaintenance {

    private boolean hasPower;
    private String typeName;
    private String officeName;
    private String createUser;
    private String areaPower;
    private String userPower;
    
    private String serviceManager;
    private String programManager;
    private String programManagerA;
    private String programManagerB;

    private Date processStartTime;
    private Date processEndTime;
    private Date createStartTime;
    private Date createEndTime;
    
    private List<FileParam> deliverFileList;
    private List<Map<String, String>> quesnaireResultList;
    private Map<String, Object> questionColumns;
    
    private Boolean hideQuesnaire;
    
    private Integer maxId;

    public boolean isHasPower() {
        return hasPower;
    }

    public void setHasPower(boolean hasPower) {
        this.hasPower = hasPower;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getAreaPower() {
        return areaPower;
    }

    public void setAreaPower(String areaPower) {
        this.areaPower = areaPower;
    }

    public String getUserPower() {
        return userPower;
    }

    public void setUserPower(String userPower) {
        this.userPower = userPower;
    }

    public String getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(String serviceManager) {
        this.serviceManager = serviceManager;
    }

    public String getProgramManager() {
        return programManager;
    }

    public void setProgramManager(String programManager) {
        this.programManager = programManager;
    }

    public String getProgramManagerA() {
        return programManagerA;
    }

    public void setProgramManagerA(String programManagerA) {
        this.programManagerA = programManagerA;
    }

    public String getProgramManagerB() {
        return programManagerB;
    }

    public void setProgramManagerB(String programManagerB) {
        this.programManagerB = programManagerB;
    }

    public Date getProcessStartTime() {
        return processStartTime;
    }

    public void setProcessStartTime(Date processStartTime) {
        this.processStartTime = processStartTime;
    }

    public Date getProcessEndTime() {
        return processEndTime;
    }

    public void setProcessEndTime(Date processEndTime) {
        this.processEndTime = processEndTime;
    }

    public Date getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(Date createStartTime) {
        this.createStartTime = createStartTime;
    }

    public Date getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(Date createEndTime) {
        this.createEndTime = createEndTime;
    }

    public List<FileParam> getDeliverFileList() {
        return deliverFileList;
    }

    public void setDeliverFileList(List<FileParam> deliverFileList) {
        this.deliverFileList = deliverFileList;
    }

    public List<Map<String, String>> getQuesnaireResultList() {
        return quesnaireResultList;
    }

    public void setQuesnaireResultList(List<Map<String, String>> quesnaireResultList) {
        this.quesnaireResultList = quesnaireResultList;
    }

    public Map<String, Object> getQuestionColumns() {
        return questionColumns;
    }

    public void setQuestionColumns(Map<String, Object> questionColumns) {
        this.questionColumns = questionColumns;
    }

    public Boolean getHideQuesnaire() {
        return hideQuesnaire;
    }

    public void setHideQuesnaire(Boolean hideQuesnaire) {
        this.hideQuesnaire = hideQuesnaire;
    }

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }
    
}
