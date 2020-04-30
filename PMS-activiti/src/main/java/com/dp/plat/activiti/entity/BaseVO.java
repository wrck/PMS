package com.dp.plat.activiti.entity;

import java.io.Serializable;

import javax.persistence.Transient;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.fasterxml.jackson.annotation.JsonBackReference;


public class BaseVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6165121688276341503L;

	// 业务类型
	public final static String VACATION = "vacation";	
	public final static String SALARY = "salary";	
	public final static String EXPENSE = "expense";
	
	// 待办任务标识
	public final static String CANDIDATE = "candidate";
	
	// 受理任务标识
	public final static String ASSIGNEE = "assignee";
	
	// 运行中的流程表示
	public final static String RUNNING = "running";
	
	// 已结束任务标识
	public final static String FINISHED = "finished";
	
	//审批中
	public static final String PENDING = "PENDING";
	//待审批
	public static final String WAITING_FOR_APPROVAL = "WAITING_FOR_APPROVAL";
	//审批成功
	public static final String APPROVAL_SUCCESS = "APPROVAL_SUCCESS";
	//审批失败
	public static final String APPROVAL_FAILED = "APPROVAL_FAILED";
	
	
	// 申请人id
	private Integer userId;
	// 申请人名称
	private String userName;
		
	// 申请人id
	private Integer applyUserId;
	
	// 申请人名称
	private String applyUserName;
	
	// 申请的标题
	private String title;
	
	// 业务类型
	private String businessType;
	
	//对应业务的id
	private String businessKey;
	
    // 流程任务
	@JsonBackReference 
    private Task task;

    // 运行中的流程实例
	@JsonBackReference 
    private ProcessInstance processInstance;

    // 历史的流程实例
	@JsonBackReference 
    private HistoricProcessInstance historicProcessInstance;

    // 历史任务
	@JsonBackReference 
    private HistoricTaskInstance historicTaskInstance;
    
    // 流程定义
	@JsonBackReference 
    private ProcessDefinition processDefinition;
	
	// 任务中的流程变量实体
	private Object taskEntity;

    @Transient
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Transient
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Transient
	public Integer getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Transient
	public String getApplyUserName() {
		return applyUserName;
	}

	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	@Transient
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Transient
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Transient
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	@Transient
	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public void setHistoricProcessInstance(
			HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}

	@Transient
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	@Transient
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Transient
	public static String getVacation() {
		return VACATION;
	}

	@Transient
	public static String getSalary() {
		return SALARY;
	}

	@Transient
	public static String getExpense() {
		return EXPENSE;
	}

	@Transient
	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	@Transient
	public HistoricTaskInstance getHistoricTaskInstance() {
		return historicTaskInstance;
	}

	public void setHistoricTaskInstance(HistoricTaskInstance historicTaskInstance) {
		this.historicTaskInstance = historicTaskInstance;
	}

	public Object getTaskEntity() {
		return taskEntity;
	}

	public void setTaskEntity(Object taskEntity) {
		this.taskEntity = taskEntity;
	}
	
}
