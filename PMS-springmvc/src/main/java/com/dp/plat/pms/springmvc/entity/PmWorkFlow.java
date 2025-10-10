package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PmWorkFlow extends BaseVO {

	private static final long serialVersionUID = -4242852386637363245L;
	   
    private Integer id;

    // 流程定义key
    private String processKey;

    // 任务Key
    private String taskKey;

    // 申请时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date applyTime;

    // 开始时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date beginTime;

    // 结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date endTime;

    // 过期时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date dueTime;

    // 流程实例ID
    private String procInstId;

    // 处理消息
    private String message;

    // 状态
    private String status;

    // userinfo表ID
    private Integer userId;

    // 对象类型
    private String objType;

    // 对象Id
    private Integer objId;

    // 数据类型
    private String dataType;

    // 数据Id
    private Integer dataId;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    // 组织ID
    private Integer orgId;

    // 自定义信息
    private Map customInfo;

    // 任务id
    private String taskId;

    private Object entity;

    // 判断是否有任务，或者有过任务
    private boolean hasTask;

    // 当前办理人
    private String assignee;

    // 当前任务办理人姓名
    private String assigneeName;

    // 办理任务的表单url
    private String formUrl;

    // 评估级别
    private String currentPriority;

    private boolean canWithdraw;

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
     * 获取流程定义key
     *
     * @return processKey - 流程定义key
     */
    public String getProcessKey() {
        return processKey;
    }

    /**
     * 设置流程定义key
     *
     * @param processKey 流程定义key
     */
    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    /**
     * 获取任务Key
     *
     * @return taskKey - 任务Key
     */
    public String getTaskKey() {
        return taskKey;
    }

    /**
     * 设置任务Key
     *
     * @param taskKey 任务Key
     */
    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    /**
     * 获取申请时间
     *
     * @return applyTime - 申请时间
     */
    public Date getApplyTime() {
        return applyTime;
    }

    /**
     * 设置申请时间
     *
     * @param applyTime 申请时间
     */
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    /**
     * 获取开始时间
     *
     * @return beginTime - 开始时间
     */
    public Date getBeginTime() {
        return beginTime;
    }

    /**
     * 设置开始时间
     *
     * @param beginTime 开始时间
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * 获取结束时间
     *
     * @return endTime - 结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取过期时间
     *
     * @return dueTime - 过期时间
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * 设置过期时间
     *
     * @param dueTime 过期时间
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * 获取流程实例ID
     *
     * @return procInstId - 流程实例ID
     */
    public String getProcInstId() {
        return procInstId;
    }

    /**
     * 设置流程实例ID
     *
     * @param procInstId 流程实例ID
     */
    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    /**
     * 获取处理消息
     *
     * @return message - 处理消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置处理消息
     *
     * @param message 处理消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取状态
     *
     * @return status - 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取userinfo表ID
     *
     * @return userId - userinfo表ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置userinfo表ID
     *
     * @param userId userinfo表ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取对象类型
     *
     * @return objType - 对象类型
     */
    public String getObjType() {
        return objType;
    }

    /**
     * 设置对象类型
     *
     * @param objType 对象类型
     */
    public void setObjType(String objType) {
        this.objType = objType;
    }

    /**
     * 获取对象Id
     *
     * @return objId - 对象Id
     */
    public Integer getObjId() {
        return objId;
    }

    /**
     * 设置对象Id
     *
     * @param objId 对象Id
     */
    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    /**
     * 获取数据类型
     *
     * @return dataType - 数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型
     *
     * @param dataType 数据类型
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取数据Id
     *
     * @return dataId - 数据Id
     */
    public Integer getDataId() {
        return dataId;
    }

    /**
     * 设置数据Id
     *
     * @param dataId 数据Id
     */
    public void setDataId(Integer dataId) {
        this.dataId = dataId;
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

    /**
     * 获取组织ID
     *
     * @return orgId - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取自定义信息
     *
     * @return customInfo - 自定义信息
     */
    public Map getCustomInfo() {
        return customInfo;
    }

    /**
     * 设置自定义信息
     *
     * @param customInfo 自定义信息
     */
    public void setCustomInfo(Map customInfo) {
        this.customInfo = customInfo;
    }

    public Object getEntity() {
    	if (entity == null) {
    		return this.getCustomInfoByKey("entity");
    	}
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
        this.setCustomInfoByKey("entity", entity);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public boolean isHasTask() {
        return hasTask;
    }

    public void setHasTask(boolean hasTask) {
        this.hasTask = hasTask;
    }

    public String getCurrentPriority() {
        return currentPriority;
    }

    public void setCurrentPriority(String currentPriority) {
        this.currentPriority = currentPriority;
    }

    public boolean isCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }
    
    public Object getCustomInfoByKey(String key) {
		Map<?, ?> customInfo = getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.get(key);
		}
		return null;
	}

	public void setCustomInfoByKey(String key, Object value) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo == null) {
			customInfo = new HashMap<>();
			this.setCustomInfo(customInfo);
			customInfo =  (Map<String, Object>) this.getCustomInfo();
		}
		customInfo.put(key, value);
	}
}
