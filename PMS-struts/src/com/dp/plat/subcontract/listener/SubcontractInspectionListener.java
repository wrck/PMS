package com.dp.plat.subcontract.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.User;
import com.dp.plat.exception.CustomRuntimeException;
import com.dp.plat.pms.extend.d365.entity.BaseEntity;
import com.dp.plat.pms.extend.d365.entity.PurchaseReceipt;
import com.dp.plat.pms.extend.d365.model.PurchaseHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseLine;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptLine;
import com.dp.plat.pms.extend.d365.service.IPurchaseLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptService;
import com.dp.plat.pms.extend.d365.service.IPurchaseService;
import com.dp.plat.pms.extend.d365.util.D365Api;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.subcontract.constant.SubcontractConstant;
import com.dp.plat.subcontract.constant.SubcontractConstant.TaskKey;
import com.dp.plat.subcontract.entity.SubcontractFacilitator;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;
import com.dp.plat.util.MailUtil;

import cn.hutool.core.date.DateUtil;

/**
 * 项目转包流程任务监听器
 * 
 * @author w02611
 */
//@Component("subcontractInspectionListener")
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class SubcontractInspectionListener implements TaskListener {
    private static final long serialVersionUID = 1L;
    public final static String OBJ_TYPE_SUBCONTRCT = "subcontract";
    public final static String DATA_TYPE_SUBCONTRACT = "subcontract";
    public final static String DATA_TYPE_PAYMENT = "payment";

    private final static String definedVariablesKey = "pm.workflow.subcontractInspection.defineVariable";
    private final static String defaultDefinedVariables = "{}";
    private final static String defaultTaskMailTemplateKey = "pm.workflow.subcontractInspection.mail";

    private final static String defaultDataType = DATA_TYPE_SUBCONTRACT;

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SubcontractService subcontractService;
    @Autowired
    private UserManageService userManageService;
    @Autowired
    private BasicDataService basicDataService;
    @Autowired
    private DepartmentManageService departmentManageService;
    
    @Autowired
    private IPurchaseService purchaseService;
    @Autowired
    private IPurchaseLineService purchaseLineService;
    @Autowired
    private IPurchaseReceiptService purchaseReceiptService;
    @Autowired
    private IPurchaseReceiptLineService purchaseReceiptLineService;
    
    public void onEvent(ActivitiEvent event) {
        if (event instanceof ActivitiEntityEventImpl) {
            ActivitiEntityEventImpl eventImpl = (ActivitiEntityEventImpl) event;
            Object entity = eventImpl.getEntity();
            ActivitiEventType eventEnum = event.getType();
            if (entity instanceof DelegateTask) {
                notify((DelegateTask) entity);
            }
        }
    }
            
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String processKey = delegateTask.getProcessDefinitionId().split(":")[0];
        String taskKey = delegateTask.getTaskDefinitionKey();
        if (SubcontractConstant.PROCESS_SUBCONTRACT_KEY.equals(processKey)) {
            if (TaskListener.EVENTNAME_COMPLETE.equals(eventName) && TaskKey.GENERATE_CONTRACT.equals(taskKey)) {
                Integer subcontractId = delegateTask.getVariable("subcontractId", Integer.class);
                Integer result = delegateTask.getVariableLocal("result", Integer.class);
                if (result != null) {
                    SubcontractProjectVO subcontract = subcontractService.selectSubcontractProjectVOById(subcontractId);
                    
                    // 多维度信息为空，则查询默认的多维度信息
                    if (subcontract.getCustomInfoByKey("multiDimInfo") == null) {
                        Map<String, String> multiDimInfo = subcontractService.selectDefaultMultiDimByDep(subcontract.getProfitDepCode(), true);
                        subcontract.setCustomInfoByKey("multiDimInfo", multiDimInfo);
                        subcontract.getCustomInfo().putAll(multiDimInfo);
                    }
                    
                    // 推D365采购订单
                    pushPurchaseOrder(subcontract);
                }
            }
        } else if (SubcontractConstant.PROCESS_INSPECTION_KEY.equals(processKey)) {
            System.out.println(eventName);
        }
    }

    public boolean isFailOnException() {
        return false;
    }

    /**
     * 流程开始执行监听器
     */
    public void processStartExecution(DelegateExecution delegateExecution) throws Exception {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        String executionId = delegateExecution.getId();
        String processKey = delegateExecution.getProcessDefinitionId();
//		PmWorkFlow pmWorkFlow = delegateExecution.getVariable("entity", PmWorkFlow.class);
        Map<String, Object> pmWorkFlow = getCurrentEntity(delegateExecution);
        runtimeService.setVariable(executionId, "entity", pmWorkFlow);

        String dataType = String.valueOf(pmWorkFlow.getOrDefault("dataType", defaultDataType));
        Map<String, Object> taskDefinedVariables = getTaskDefinedVariable(dataType);
        ProcessDefinitionImpl processDefinition = ((ExecutionEntity) delegateExecution).getProcessDefinition();
        List<ActivityImpl> activityList = processDefinition.getActivities();
        String nextTaskKey = "";
        for (Iterator<ActivityImpl> iterator = activityList.iterator(); iterator.hasNext();) {
            ActivityImpl activityVo = iterator.next();
            if (delegateExecution.getCurrentActivityId().equals(activityVo.getId())) {
                PvmTransition transition = activityVo.getOutgoingTransitions().get(0);
                PvmActivity destination = transition.getDestination();
                nextTaskKey = destination.getId();
                break;
            }
        }
        // 审批节点的候选角色组
        List<Map<String, Object>> approverList = (List<Map<String, Object>>) taskDefinedVariables
                .getOrDefault(nextTaskKey, Collections.emptyList());
        if (approverList.isEmpty()) {
//			PlanObjectiveAppraiserRelationship noAssigenee = new PlanObjectiveAppraiserRelationship();
//			noAssigenee.setAppraiserName("无");
//			objectiveAppraiserList.add(noAssigenee);
//            throw new RuntimeException("验收审批关系不能为空！");
        }
        // 如果付款信息为空，则跳过流程
        List<SubcontractPayment> payments = (List<SubcontractPayment>) pmWorkFlow.get("entity");
        if (payments.isEmpty()) {
            approverList = Collections.emptyList();
            runtimeService.setVariable(executionId, "isPass", true);
        } else {
            // 增加流程默认值
            runtimeService.setVariable(executionId, "isPass", false);
        }
        runtimeService.setVariable(executionId, "approverList", approverList);

        String processName = (String) taskDefinedVariables.getOrDefault("title", "");
        if (StringUtils.isNotBlank(processName)) {
            processName += " -- ";
        }
        runtimeService.setVariable(executionId, "processName", processName);

//        if (DATA_TYPE_SUBCONTRCT.equals(dataType)) {
//            SubcontractProject subcontract = new SubcontractProject();
//            subcontract.setId(Integer.valueOf(pmWorkFlow.get("subcontractId")));
//            subcontract.setCustomInfoByKey("currentProcInstId", delegateExecution.getProcessInstanceId());
//            subcontractService.updateByPrimaryKeySelective(subcontract);
//        } else if (DATA_TYPE_PAYMENT.equals(dataType)) {
//            SubcontractPayment settlement = new SubcontractPayment();
//            settlement.setId(pmWorkFlow.getDataId());
//            settlement.setCustomInfoByKey("currentProcInstId", delegateExecution.getProcessInstanceId());
//            subcontractSettlementService.updateByPrimaryKeySelective(settlement);
//        }
    }

    /**
     * 绩效计划目标审批流程完成后执行结束监听器
     */
    public void processEndExecution(DelegateExecution delegateExecution) throws Exception {
        String taskId = delegateExecution.getId();
        ExecutionEntity executionEntity = (ExecutionEntity) delegateExecution;
        String processKey = executionEntity.getProcessDefinitionKey();
//			PmWorkFlow pmWorkFlow = delegateExecution.getVariable("entity", PmWorkFlow.class);
        Map<String, Object> pmWorkFlow = getCurrentEntity(delegateExecution);

        Boolean isPass = Boolean.TRUE.equals(delegateExecution.getVariable("isPass"));
        String dataType = String.valueOf(pmWorkFlow.getOrDefault("dataType", defaultDataType));
        String workflowStatus = isPass ? "1" : "-1";
        String dataStatus = isPass ? "1" : "-1";
        if (DATA_TYPE_SUBCONTRACT.equals(dataType)) {
            //
        } else if (DATA_TYPE_PAYMENT.equals(dataType)) {
            List<SubcontractPayment> payments = (List<SubcontractPayment>) pmWorkFlow.get("entity");
            for (SubcontractPayment payment : payments) {
//                SubcontractPayment temp = new SubcontractPayment();
//                temp.setId(payment.getId());
                // 审批通过后，进行结算确认
                if (isPass) {
                    payment.setConfirmTime(new Date());
                    payment.setCustomInfoByKey("approved", true);
                    payment.setCustomInfoByKey("readonly", true);
                    payment.setCustomInfoByKey("status", "审批通过");
                } else {
                    payment.setCustomInfoByKey("approved", false);
                    payment.setCustomInfoByKey("readonly", false);
                    payment.setCustomInfoByKey("status", "审批驳回");
                }
                payment.setCustomInfoByKey("currentTaskId", null);
                payment.setCustomInfoByKey("currentTaskKey", null);
                payment.setCustomInfoByKey("currentProcInstId", null);
                subcontractService.updateSubcontractPaymentByIdSelective(payment);
            }
            // 审批通过后，进行采购收货
            if (isPass) {
                pushPurchaseReceipt(payments);
            }
        }

        pmWorkFlow.put("status", workflowStatus);
        pmWorkFlow.put("taskKey", dataStatus);
        pmWorkFlow.put("endTime", new Date());
        runtimeService.setVariable(delegateExecution.getProcessInstanceId(), "entity", pmWorkFlow);
        return;
    }

    /**
     * 任务创建监听器
     */
    public void createTask(DelegateTask delegateTask) throws Exception {
        String procInstId = delegateTask.getProcessInstanceId();
        String taskId = delegateTask.getId();
        String taskKey = delegateTask.getTaskDefinitionKey();
        String processKey = delegateTask.getProcessDefinitionId();
//		PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
        Map<String, Object> pmWorkFlow = getCurrentEntity(delegateTask);
        String objType = String.valueOf(pmWorkFlow.getOrDefault("objType", DATA_TYPE_SUBCONTRACT));
        String dataType = String.valueOf(pmWorkFlow.getOrDefault("dataType", DATA_TYPE_PAYMENT));
//		List<Map<String, Object>> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
        Map<String, Object> taskDefinedVariables = (Map<String, Object>) delegateTask.getVariable("approver");
        List<Map<String, Object>> approverList = (List<Map<String, Object>>) delegateTask.getVariable("approverList");
        Integer loopCounter = (Integer) delegateTask.getVariable("loopCounter");
        Map<String, Object> nextTaskDefinedVariables = (loopCounter + 1) < approverList.size() ? approverList.get(loopCounter + 1) : Collections.emptyMap();

        // 更新任务状态,补充流程信息
        if (DATA_TYPE_SUBCONTRACT.equals(dataType)) {
//            SubcontractVO subcontract = new SubcontractVO();
//            subcontract.setId(pmWorkFlow.getDataId());
////			subcontract.setStatus(taskKey);
//            subcontract.setCustomInfoByKey("currentTaskId", taskId);
//            subcontract.setCustomInfoByKey("currentTaskKey", taskKey);
//            subcontract.setCustomInfoByKey("currentProcInstId", procInstId);
//            subcontractService.updateByPrimaryKeySelective(subcontract);
        } else if (DATA_TYPE_PAYMENT.equals(dataType)) {
            List<SubcontractPayment> payments = (List<SubcontractPayment>) pmWorkFlow.get("entity");
            for (SubcontractPayment payment : payments) {
//                SubcontractPayment temp = new SubcontractPayment();
//                temp.setId(payment.getId());
                payment.setCustomInfoByKey("readonly", true);
                payment.setCustomInfoByKey("status", taskDefinedVariables.getOrDefault("taskName", "") + "审批中");
                payment.setCustomInfoByKey("currentTaskId", taskId);
                payment.setCustomInfoByKey("currentTaskKey", taskKey);
                payment.setCustomInfoByKey("currentProcInstId", procInstId);
                subcontractService.updateSubcontractPaymentByIdSelective(payment);
            }
        }
        
        // 当前办理人相关参数
        Map<String, Object> currentAssigneeConfig = findAssignee(delegateTask, taskDefinedVariables);
        String assignee = (String) currentAssigneeConfig.get("assignee");
        Set<String> candidates = (Set<String>) currentAssigneeConfig.get("candidates");
        String candidateGroup = (String) currentAssigneeConfig.get("candidateGroup");
        String candidateRole = (String) currentAssigneeConfig.get("candidateRole");
        String permissionProjectTypes = (String) currentAssigneeConfig.get("permissionProjectTypes");
        boolean checkDep = (boolean) currentAssigneeConfig.getOrDefault("checkDep", false);
        String areaPower = (String) currentAssigneeConfig.get("areaPower");
        String assigneeName = (String) currentAssigneeConfig.get("assigneeName");
        
        // 下级办理人相关参数
        Map<String, Object> nextAssigneeConfig = findAssignee(delegateTask, nextTaskDefinedVariables);
        
        if (StringUtils.isNotBlank(assignee)) {
            delegateTask.setAssignee(assignee);
        } else if (candidates != null && !candidates.isEmpty()) {
            delegateTask.addCandidateUsers(candidates);
        } else {
            // 设置assignee
//            delegateTask.addCandidateGroup(candidateGroup);
            delegateTask.setAssignee(candidateRole);
        }
        delegateTask.setVariableLocal("startTime", new Date());
        delegateTask.setVariableLocal("checkDep", checkDep);
        delegateTask.setVariableLocal("areaPower", areaPower);
//        if (!"all".equals(areaPower)) {
            delegateTask.setVariableLocal("dpNo", areaPower);
//        }
        delegateTask.setVariableLocal("projectTypes", permissionProjectTypes);
        if (loopCounter == 0) {
            ExecutionEntity execution = ((TaskEntity) delegateTask).getExecution();
            if (execution == null) {
                execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(procInstId).singleResult();
            }
            if (execution != null) {
                // 查找最顶层的流程
                while (execution.getSuperExecution() != null || execution.getParent() != null) {
                    execution = execution.getSuperExecution() != null ? execution.getSuperExecution() : execution.getParent();
                }
                execution.setVariable("nextAssigneeCode", currentAssigneeConfig.get("assigneeCode"));
                execution.setVariable("nextAssigneeName", currentAssigneeConfig.get("assigneeName"));
            }
        }
        delegateTask.setVariableLocal("nextAssigneeCode", nextAssigneeConfig.get("assigneeCode"));
        delegateTask.setVariableLocal("nextAssigneeName", nextAssigneeConfig.get("assigneeName"));
//		delegateTask.setVariable("assignee", assignee);
//		delegateTask.setVariable("candidates", candidates);
//		delegateTask.setVariable("candidateGroup", candidateGroup);

        pmWorkFlow.put("taskId", delegateTask.getId());
        pmWorkFlow.put("taskKey", taskKey);
        runtimeService.setVariable(delegateTask.getProcessInstanceId(), "entity", pmWorkFlow);

        // 任务创建时的邮件提醒
        String templateCode = (String) taskDefinedVariables.getOrDefault("mailCode", defaultTaskMailTemplateKey);
        if (StringUtils.isNotBlank(templateCode)) {
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("templateCode", templateCode);
            List<String> tos = selectActivitiUserMails(assignee, candidates, candidateGroup, areaPower,
                    permissionProjectTypes);
            context.put("tos", StringUtils.join(tos, ";"));
            context.put("ccs", taskDefinedVariables.get("ccs"));
            context.put("dataSource", new Object[] { pmWorkFlow, delegateTask });
            MailUtil.keepMailWithTemplate(context);
        }
    }
    
    /**
     * 查找任务办理人
     * @param taskDefinedVariables 
     * @return 
     */
    public Map<String, Object> findAssignee(DelegateTask delegateTask, Map<String, Object> taskDefinedVariables) {
        String procInstId = delegateTask.getProcessInstanceId();
        String taskId = delegateTask.getId();
        String taskKey = delegateTask.getTaskDefinitionKey();
        String processKey = delegateTask.getProcessDefinitionId();

//      PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
        Map<String, Object> pmWorkFlow = getCurrentEntity(delegateTask);
        String objType = String.valueOf(pmWorkFlow.getOrDefault("objType", DATA_TYPE_SUBCONTRACT));
        String dataType = String.valueOf(pmWorkFlow.getOrDefault("dataType", DATA_TYPE_PAYMENT));
//      List<Map<String, Object>> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
//        Map<String, Object> taskDefinedVariables = (Map<String, Object>) delegateTask.getVariable("approver");
        
        String assignee = null;
        Set<String> candidates = null;
        String candidateGroup = null;
        String memberRole = null;
        String candidateRole = null;
        String permissionProjectTypes = "all";
        String areaPower = "all";
        String departmentName = "";
        List<String> assigeeCodes = new ArrayList<String>();
        List<String> assigeeNames = new ArrayList<String>();
        boolean checkArea = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("checkArea", false));
        assignee = (String) taskDefinedVariables.getOrDefault("assignee", "");
        candidates = new HashSet(Arrays.asList(StringUtils.split((String) taskDefinedVariables.getOrDefault("candidates", ""), ",")));
        memberRole = (String) taskDefinedVariables.getOrDefault("memberRole", "");
        candidateRole = (String) taskDefinedVariables.getOrDefault("candidateRole", "");
        String candidateRoleName = (String) taskDefinedVariables.getOrDefault("candidateRoleName", taskDefinedVariables.getOrDefault("taskName", ""));
        if (TaskKey.ACCEPTANCE_TASK.equals(taskKey)) {
            if (checkArea) {
                if (DATA_TYPE_SUBCONTRACT.equals(dataType)) {
                    SubcontractProject entity = (SubcontractProject) pmWorkFlow.get("entity");
                    areaPower = entity.getProfitDepCode();
                } else if (DATA_TYPE_PAYMENT.equals(dataType)) {
//                    SubcontractPayment entity = (SubcontractPayment) pmWorkFlow.get("entity");
//                    Integer subcontractId = entity.getSubcontractId();
                    Integer subcontractId = Integer.valueOf(String.valueOf(pmWorkFlow.get("objId")));
                    SubcontractProject subcontract = subcontractService.selectSubcontractProjectById(subcontractId);
                    areaPower = subcontract.getProfitDepCode();
                }
                Department department = departmentManageService.queryDepartmentByDepartmentNum(areaPower);
                if (department != null) {
                    departmentName = department.getDepartmentName();
                }
            }
            List<ProjectMember> members = Collections.emptyList();
            if (StringUtils.isNotBlank(memberRole)) {
                members = projectService.queryProjectMembers(Integer.valueOf(String.valueOf(pmWorkFlow.get("objId"))));
                Date date = new Date();
                for (Iterator iterator = members.iterator(); iterator.hasNext();) {
                    ProjectMember m = (ProjectMember) iterator.next();
                    Date effectiveTo = m.getEffectiveTo();
                    if (!(memberRole.equals(m.getMemberRole()) && (effectiveTo == null || effectiveTo.after(date)))) {
                        members.remove(m);
                    }
                }
            }
            if (StringUtils.isNotBlank(assignee) || (candidates != null && !candidates.isEmpty())) {
                // 指定了具体的办理人，或者候选人
                List<String> users = new ArrayList<String>();
                users.add(assignee);
                if (candidates != null) {
                    users.addAll(candidates);
                }
                for (String user : users) {
                    User temp = userManageService.queryUserByUserName(assignee);
                    if (temp != null) {
                        assigeeCodes.add(temp.getUsername());
                        assigeeNames.add(joinStr(temp.getUsername(), temp.getRealName()));
                    }
                }
            } else if (!members.isEmpty() && members.size() == 1) {
                ProjectMember member = members.get(0);
                assignee = member.getMemberCode();
                assigeeCodes.add(assignee);
                assigeeNames.add(member.getMemberName());
            } else if (!members.isEmpty()) {
                candidates = new HashSet<String>(members.size());
                for (ProjectMember member : members) {
                    candidates.add(member.getMemberCode());
                    assigeeCodes.add(member.getMemberCode());
                    assigeeNames.add(member.getMemberName());
                }
            } else {
                candidateGroup = candidateRole;
                assigeeCodes.add(areaPower+ candidateRole);
                assigeeNames.add(joinStr(departmentName, candidateRoleName));
            }
        } else {
            candidateGroup = candidateRole;
            assigeeCodes.add(areaPower + candidateRole);
            assigeeNames.add(joinStr(departmentName, candidateRoleName));
        }
        
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("assignee", assignee);
        config.put("candidates", candidates);
        config.put("candidateGroup", candidateGroup);
        config.put("candidateRole", candidateRole);
        config.put("checkDep", checkArea);
        config.put("areaPower", areaPower);
        config.put("permissionProjectTypes", permissionProjectTypes);
        config.put("assigneeCode", StringUtils.join(assigeeCodes, ","));
        config.put("assigneeName", StringUtils.join(assigeeNames, ","));
        return config;
    }

    /**
     * 任务完成监听器
     */
    public void completeTask(DelegateTask delegateTask) throws Exception {
        String taskId = delegateTask.getId();
        String taskKey = delegateTask.getTaskDefinitionKey();
        String processKey = delegateTask.getProcessDefinitionId();
//		PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
        Map<String, Object> pmWorkFlow = getCurrentEntity(delegateTask);

        String dataType = String.valueOf(pmWorkFlow.getOrDefault("dataType", DATA_TYPE_PAYMENT));
        List<Map<String, Object>> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
        Boolean isPass = (Boolean) delegateTask.getVariableLocal("isPass");
        String data = StringUtils.defaultIfBlank((String) delegateTask.getVariableLocal("data"), "{}");
        Map<String, Object> customData = JSON.parseObject(data, HashMap.class);
        Integer flowState = Boolean.TRUE.equals(isPass) ? 1 : -1;
        String status = null;
        // 处理数据
        customData = (Map<String, Object>) customData.getOrDefault(dataType, new HashMap());
        Map<String, Object> filtedCustomData = new HashMap<String, Object>(customData.size());
        for (Entry<String, Object> entry : customData.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> subCustomData = (Map<String, Object>) value;
                Map<String, Object> filtedSubCustomData = new HashMap<String, Object>(subCustomData.size());
                for (Entry<String, Object> subEntry : subCustomData.entrySet()) {
                    filtedSubCustomData.put(subEntry.getKey(), Boolean.TRUE.equals(isPass) ? subEntry.getValue() : "");
                }
                filtedCustomData.put(entry.getKey(), filtedSubCustomData);
            } else {
                filtedCustomData.put(entry.getKey(), Boolean.TRUE.equals(isPass) ? entry.getValue() : "");
            }
        }
        if (DATA_TYPE_SUBCONTRACT.equals(dataType)) {
//          SubcontractVO subcontract = new SubcontractVO();
//          subcontract.setId(pmWorkFlow.getDataId());
            SubcontractProject temp = new SubcontractProject();
            temp.setId(Integer.valueOf(String.valueOf(pmWorkFlow.get("objId"))));
            temp.setCustomInfo(filtedCustomData);
            subcontractService.updateSubcontractProjectByIdSelective(temp);
        } else if (DATA_TYPE_PAYMENT.equals(dataType)) {
//          SubcontractPayment payment = new SubcontractPayment();
//          payment.setId((Integer) pmWorkFlow.get("dataId"));
            List<SubcontractPayment> payments = (List<SubcontractPayment>) pmWorkFlow.get("entity");
            for (SubcontractPayment payment : payments) {
//                SubcontractPayment temp = new SubcontractPayment();
//                temp.setId(payment.getId());
                Object customInfo = filtedCustomData.get(String.valueOf(payment.getId()));
                if (customInfo instanceof Map) {
                    payment.setCustomInfo((Map<String, Object>) customInfo);
                } else {
                    payment.setCustomInfo(filtedCustomData);
                }
                subcontractService.updateSubcontractPaymentByIdSelective(payment);
            }
        }
        // 将自定义审批变量保存到当前示例中
        Map customInfo = (Map) pmWorkFlow.getOrDefault("customInfo", new HashMap<>());
        customInfo.putAll(filtedCustomData);
        pmWorkFlow.put("customInfo", customInfo);
        runtimeService.setVariable(delegateTask.getProcessInstanceId(), "entity", pmWorkFlow);
    }

    /**
     * 获取定义的数据类型的流程变量
     * @param dataType
     * @return
     */
    private Map<String, Object> getTaskDefinedVariable(String dataType) {
        String definedVars = getSysArg(definedVariablesKey);
        definedVars = StringUtils.defaultIfBlank(definedVars, defaultDefinedVariables);
        Map<String, Object> definedVariables = JSON.parseObject(definedVars, Map.class);
        Map<String, Object> taskDefinedVariables = (Map<String, Object>) definedVariables.getOrDefault(dataType,
                new HashMap<>());
        return taskDefinedVariables;
    }

    /**
     * 获取定义的数据类型的任务的流程变量
     * @param dataType
     * @param taskType
     * @return
     */
    private List<Map<String, Object>> getTaskDefinedVariable(String dataType, String taskType) {
        Map<String, Object> definedVariable = this.getTaskDefinedVariable(dataType);

        List<Map<String, Object>> taskDefinedVariables = (List<Map<String, Object>>) definedVariable
                .getOrDefault(taskType, new ArrayList<Map<String, Object>>());
        return taskDefinedVariables;
    }
    
    /**
     * 获取定义的数据类型的任务审批状态
     * @param dataType
     * @param taskType
     * @return
     */
    public static Map<String, String> getTaskApprovedStatusList(String dataType, String taskType) {
        SubcontractInspectionListener listener = SpringContext.getBean("subcontractInspectionListener", SubcontractInspectionListener.class);
        List<Map<String, Object>> taskDefinedVariable = listener.getTaskDefinedVariable(dataType, taskType);
        Map<String, String> approvedStatusMap = new LinkedHashMap<String, String>(taskDefinedVariable.size());
        approvedStatusMap.put("待审批", "待审批");
        for (Map<String, Object> var : taskDefinedVariable) {
            String approvedStatus = var.getOrDefault("taskName", "") + "审批中";
            approvedStatusMap.put(approvedStatus, approvedStatus);
        }
        approvedStatusMap.put("审批驳回", "审批驳回");
        approvedStatusMap.put("审批通过", "审批通过");
        return approvedStatusMap;
    }

    private List<String> selectActivitiUserMails(String assignee, Set<String> candidates, String candidateGroup,
            String areaPower, String projectTypes) {
        Set<String> userIds = new HashSet<String>();
        if (assignee != null) {
            userIds.add(assignee);
        } else if (candidates != null) {
            userIds.addAll(candidates);
        }
        if (userIds.isEmpty()) {
            userIds.add(String.valueOf(Integer.MIN_VALUE));
        }
        String[] groupIds = StringUtils.split(candidateGroup, ",");
        if (groupIds == null || groupIds.length == 0) {
            groupIds = new String[] { "empty" };
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userIds", userIds);
        params.put("groupIds", groupIds);
        if (!"all".equals(areaPower)) {
            params.put("areaPower", areaPower);
        }
        if (!"all".equals(projectTypes)) {
            params.put("projectType", projectTypes);
        }
        List<String> mails = Collections.emptyList(); // pmFlowService.selectActivitiUserMails(params);
        return mails;
    }

    /**
     * 目标审批任务创建时出给任务增加办理人，分组或指定人任务监听器
     */
    public void approveAcceptanceCreateTask(DelegateTask delegateTask) throws Exception {
        UserEntity approver = (UserEntity) delegateTask.getVariable("approver");
        String approverId = approver.getId();
        String candidateGroups = getSysArg("perf.activiti.candidate.groups", "");
        // 无审批人标识
        String ignoreAssignee = getSysArg("perf.activiti.ignoreAssignee", "无");
        String approverName = StringUtils.trimToNull(approver.getLastName());
        if ((approverId == null || "0".equals(approverId)) && approverName != null
                && candidateGroups.matches("(.*)\\b" + approverName + "\\b(.*)")) {
            Group group = identityService.createGroupQuery().groupType(approverName).singleResult();
            delegateTask.addCandidateGroup(group.getId());
        } else if (approverName != null && ignoreAssignee.contains(approverName)) {
            delegateTask.setAssignee(approverName);
        } else {
            delegateTask.setAssignee(approverId == null ? "0" : String.valueOf(approverId));
        }
        return;
    }
    
    /**
     * 推D365的采购订单
     * @param subcontract
     */
    public void pushPurchaseOrder(SubcontractProject subcontract) {
        Integer subcontractType = subcontract.getType();
        // 获取项目转包推采购订单的配置项
        String configStr = getSysArg("sys.d365.api.config");
        configStr = StringUtils.defaultIfBlank(configStr, "{}");
        Map<String, Object> config = JSON.parseObject(configStr, new TypeReference<HashMap<String, Object>>() {});
        boolean enablePushPurchaseOrder = Boolean.TRUE.equals(Boolean.parseBoolean(String.valueOf(config.get("enablePushPurchaseOrder"))));
        if (!enablePushPurchaseOrder) {
            return;
        }
        
        // 是否允许已经推过采购订单的项目转包，默认不允许重复推
        boolean repeatablePushPurchaseOrder = Boolean.TRUE.equals(Boolean.parseBoolean(String.valueOf(config.get("repeatablePushPurchaseOrder"))));
        if (!repeatablePushPurchaseOrder && StringUtils.isNotBlank((String) subcontract.getCustomInfoByKey("purchId"))) {
            return;
        }
               
        // 设置账套
        Company company = new Company(subcontract.getOrgId());
        company = departmentManageService.queryCompanyOne(company);
        String dataAreaId = company.getAccount();
        config.put("dataAreaId", dataAreaId);
        
        // 创建采购订单头
        PurchaseHeader purchTable = this.createPurchashHeader(subcontract, config);
        List<PurchaseLine> purchLines = this.createPurchaseLines(subcontract, config);
        
        // 初始化D365接口配置
        SubcontractProject subcontractProject = D365Api.pushPurchaseOrder(subcontract, dataAreaId, purchTable, purchLines, config);
        subcontractProject.setCustomInfoByKey("subcontractTime", new Date());
        subcontractService.updateSubcontractProjectByIdSelective(subcontractProject);
    }
    
    /**
     * 基于项目转包创建采购订单头
     * @param subcontract
     * @param config
     * @return
     */
    public PurchaseHeader createPurchashHeader(SubcontractProject subcontract, Map<String, Object> config) {
        // 检查服务商编码是不是存在
        Integer facilitatorId = subcontract.getFacilitatorId();
        SubcontractFacilitator facilitator = subcontractService.selectSubcontractFacilitatorById(facilitatorId);
        if (facilitator == null) {
            throw new CustomRuntimeException("该服务商不存在或转包类型与合作类型不匹配！");
        }
        
        SubcontractProjectVO subcontractVO = null;
        if (subcontract instanceof SubcontractProjectVO) {
            subcontractVO = (SubcontractProjectVO) subcontract;
        } else {
            subcontractVO = subcontractService.selectSubcontractProjectVOById(subcontract.getId());
        }
        
        // 判断采购订单池是否指定
        String purchPoolId = (String) config.get("purchPoolId");
        if (StringUtils.isBlank(purchPoolId)) {
            throw new CustomRuntimeException("采购订单池未指定！");
        }
        
        // 获取指定的仓库
        String inventLocationId = (String) config.getOrDefault("inventLocationId", "");
        
        // 获取工号
        String workNo = UserContext.getUserContext().getUsername().substring(1);
        // 处理备注信息
//        String remark = SystemLogUtil.format((String) config.getOrDefault("remarkFormat", subcontract.getRemark()), subcontract);
        String remark = subcontract.getReason() + "\r\n" + subcontract.getRemark();
        
        // 获取项目进度
        Project minProgressProject = this.queryMinProgressProject(subcontract);
        
        // 填充采购订单基准单位
        D365Api.fillPurchaseUnitBase(subcontract, config);
        
        // 创建采购订单头
        PurchaseHeader purchTable = new PurchaseHeader();
        purchTable.setSourceType(DATA_TYPE_SUBCONTRACT);
        purchTable.setSourceId(subcontract.getId());
        purchTable.setDataAreaId((String) config.get("dataAreaId")); // 账套
        purchTable.vendAccount(facilitator.getAccount()) // D365供应商编号
//                .purchName(subcontract.getSubcontractName())// 采购事项（供应商名称）
                .purchName(facilitator.getName())// 采购事项（供应商名称）
                .purchPoolId(purchPoolId.toString()) // 采购订单池
                .purContract(subcontract.getSubcontractNo()) // 采购合同号
                .salesContract(subcontract.getContractNos()) // 销售合同号
                .contractAmount(RegExUtils.replaceAll(subcontract.getSubcontractAmount(), ",", "")) // 合同金额
                .inventLocationId(inventLocationId) // 仓库
                .deliveryDate((String) subcontract.getCustomInfoByKey("deliveryDate", DateUtil.formatDateTime(new Date()))) // 交货日期
                .dlvMode((String) subcontract.getCustomInfoByKey("dlvMode")) // 交货模式
                .dlvTerm((String) subcontract.getCustomInfoByKey("dlvTerm")) // 交货条款
                .payment((String) subcontract.getCustomInfoByKey("prepaidRule")) // 付款条款
                .paymMode((String) subcontract.getCustomInfoByKey("paymMode")) // 付款方式
                .remark(remark) // 备注，解析remarkFormat
                .otherSysNum(String.valueOf(config.getOrDefault("sysTag", "PMS#")) + subcontract.getId()) // 外部系统编号
                .projectName((String) subcontract.getSubcontractName()) // 项目名称
                .projectProgress((String) minProgressProject.getCustomInfoByKey("projectProgress", "0")) // 项目进度
                .subcontractType((String) config.getOrDefault("typeTag", "用服") + subcontract.getCustomInfoByKey("typeName", subcontractVO.getTypeName())) // 转包类型
                .subcontStartDate(StringUtils.trimToNull((String) subcontract.getCustomInfoByKey("subcontStartDate"))) // 转包周期开始
                .subcontEndDate(StringUtils.trimToNull((String) subcontract.getCustomInfoByKey("subcontEndDate"))) // 转包周期结束
                .applicant(workNo) // 申请人
                .workerPurchPlacer(workNo) // 订货人
        ;
        return purchTable;
    }
    
    /**
     * 基于项目转包创建采购订单行
     * @param subcontract
     * @param config
     * @return
     */
    public List<PurchaseLine> createPurchaseLines(SubcontractProject subcontract, Map<String, Object> config) {
        // 判断采购订单物料是否指定
        String itemId = (String) config.get("itemId");
        if (StringUtils.isBlank(itemId)) {
            throw new CustomRuntimeException("采购订单物料编码未指定！");
        }
        // 获取指定的仓库
        String inventLocationId = (String) config.getOrDefault("inventLocationId", "");
        
        // 获取采购订单的基准单位
        String purchUnitBase = (String) subcontract.getCustomInfoByKey("purchUnitBase", config.getOrDefault("purchUnitBase", "price"));
        // 获取采购订单的基准单价，默认为1
        BigDecimal purchPriceBase = new BigDecimal(String.valueOf(subcontract.getCustomInfoByKey("purchPriceBase", config.getOrDefault("purchPriceBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);
        // 获取采购订单的基准数量，默认为1,
        BigDecimal purchQtyBase = new BigDecimal(String.valueOf(subcontract.getCustomInfoByKey("purchQtyBase", config.getOrDefault("purchQtyBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal subcontractAmount = BigDecimal.ZERO;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        decimalFormat.setParseBigDecimal(true);
        String amountStr = StringUtils.defaultIfBlank(StringUtils.trimToEmpty(subcontract.getSubcontractAmount()), "0");
        try {
            subcontractAmount = (BigDecimal) decimalFormat.parse(amountStr);
        } catch (ParseException e) {
            subcontractAmount = new BigDecimal(amountStr.replaceAll(",", ""));
        }
        subcontractAmount = subcontractAmount.setScale(2, RoundingMode.HALF_UP);
        // 默认指定基准采购价，数量由转包价和基准采购价确定
        BigDecimal purchPrice = purchPriceBase;
        BigDecimal purchQty = purchQtyBase;
        if ("price".equalsIgnoreCase(purchUnitBase)) {
            // 根据转包价和基准采购价计算采购订单的采购数量
            purchQty = subcontractAmount.divide(purchPriceBase, 2, RoundingMode.HALF_UP);
        } else {
            // 根据转包价和基准数量计算采购订单的采购单价
            purchPrice = subcontractAmount.divide(purchQtyBase, 2, RoundingMode.HALF_UP);
        }

        // 处理备注信息
//        String remark = StringUtils.defaultIfBlank(SystemLogUtil.format((String) config.getOrDefault("lineRemarkFormat", config.getOrDefault("remarkFormat", "")), subcontract), subcontract.getRemark());
        String remark = subcontract.getReason() + "\r\n" + subcontract.getRemark();
        
        // 税组，默认为税组，如果没有则税率加碎组前缀组成税组
        String taxRate = (String) subcontract.getCustomInfoByKey("taxRate");
        String taxItemGroup = (String) subcontract.getCustomInfoByKey("taxItemGroup");
        if (StringUtils.isBlank(taxItemGroup) && StringUtils.isNotBlank(taxRate)) {
            taxItemGroup = config.getOrDefault("taxGroupPrefix", "J") + taxRate;
        }
        
        // 创建采购订单行
        List<PurchaseLine> purchLines = new ArrayList<PurchaseLine>();
        PurchaseLine purchaseLine = new PurchaseLine();
        purchaseLine.setDataAreaId((String) config.get("dataAreaId")); // 账套
        purchaseLine.lineNum(subcontract.getId().toString()) // 行号（用系统ID代替）
                .itemId(itemId) // 物料编码
                .purchQty(purchQty) // 采购数量
                .purchPrice(purchPrice)// 采购价
                .inventLocationId(inventLocationId)// 仓库
                .taxItemGroup(taxItemGroup)// 税收组
                .inventSerialId((String) config.get("inventSerialId"))// 厂商型号
                .officeCode(subcontract.getProfitDepCode())// 办事处
                .deliveryDate((String) subcontract.getCustomInfoByKey("deliveryDate", DateUtil.formatDateTime(new Date())))// 交货日期
                .remark(remark)// 行备注
                .multiDimID((String) subcontract.getCustomInfoByKey("multiDimID"))// 行多维度ID
                .investmentProject((String) subcontract.getCustomInfoByKey("investmentProject"))// 募投项目
                .dimBankAccount((String) subcontract.getCustomInfoByKey("dimBankAccount"))// 维度-银行账户
                .dimCustomer((String) subcontract.getCustomInfoByKey("dimCustomer"))// 维度-客户
                .dimVendor((String) subcontract.getCustomInfoByKey("dimVendor"))// 维度-供应商
                .dimEmployee((String) subcontract.getCustomInfoByKey("dimEmployee"))// 维度-员工
                .dimContract((String) subcontract.getCustomInfoByKey("dimContract"))// 维度-合同号
                .dimDepartment(subcontract.getProfitDepCode())// 维度-部门
                .dimBU((String) subcontract.getCustomInfoByKey("dimBU"))// 维度-BU
                .dimProductLine((String) subcontract.getCustomInfoByKey("dimProductLine"))// 维度-产品线
                .dimTerritory((String) subcontract.getCustomInfoByKey("dimTerritory"))// 维度-区域
                .dimIndustry((String) subcontract.getCustomInfoByKey("dimIndustry"))// 维度-行业
                .dimMultiDimID((String) subcontract.getCustomInfoByKey("dimMultiDimID"))// 维度-多维度ID
        ;
        purchLines.add(purchaseLine);
        return purchLines;
    }
    
    /**
     * 获取项目进度最慢的项目
     * @param subcontract
     * @return
     */
    public Project queryMinProgressProject(SubcontractProject subcontract) {
        // 获取项目进展最慢的项目
        List<String> projectIds = Arrays.asList(StringUtils.split(subcontract.getProjectIds(), ","));
        Project minProgressProject = projectIds.parallelStream().map(projectId -> {
            return projectService.queryProjectById(Integer.valueOf(projectId));
        }).min((p, n) -> {
            Integer progressPrev = Integer.valueOf(p.getProjectStatus());
            Integer progressNext = Integer.valueOf(n.getProjectStatus());
            return progressPrev.compareTo(progressNext);
        })
//        .map(p -> {
//            Project project = new Project();
//            BeanUtils.copyProperties(p, project);
//            return project;
//        })
        .orElse(new Project(-1));
        return minProgressProject;
    }
    
    /**
     * 推D365的采购订单
     * @param payments
     */
    public void pushPurchaseReceipt(List<SubcontractPayment> payments) {
        // 获取项目转包推采购订单的配置项
        String configStr = getSysArg("sys.d365.api.config");
        configStr = StringUtils.defaultIfBlank(configStr, "{}");
        Map<String, Object> config = JSON.parseObject(configStr, new TypeReference<HashMap<String, Object>>() {});
        boolean enablePushPurchaseOrder = Boolean.TRUE.equals(Boolean.parseBoolean(String.valueOf(config.get("enablePushPurchaseOrder"))));
        if (!enablePushPurchaseOrder) {
            return;
        }
        
        // 查询项目转包的采购订单号和批次号
        Map<Integer, SubcontractProject> subcontractMap = new HashMap<Integer, SubcontractProject>();
        Map<Integer, List<Map<String, SubcontractPayment>>> paymentMap = new HashMap<>(payments.size());
        for (SubcontractPayment payment : payments) {
            Integer subcontractId = payment.getSubcontractId();
            SubcontractProject subcontract = subcontractMap.get(subcontractId);
            if (null == subcontract) {
                subcontract = subcontractService.selectSubcontractProjectById(payment.getSubcontractId());
                D365Api.fillPurchaseUnitBase(subcontract, config);
                subcontractMap.put(subcontractId, subcontract);
            }
            String purchId = (String) subcontract.getCustomInfoByKey("purchId");
            String inventTransId = (String) subcontract.getCustomInfoByKey("inventTransId");
            payment.setCustomInfoByKey("purchId", purchId);
            payment.setCustomInfoByKey("inventTransId", inventTransId);
            payment.setCustomInfoByKey("deliveryDate", DateUtil.formatDateTime(new Date()));
            payment.setCustomInfoByKey("documentDate", DateUtil.formatDateTime(new Date()));
            
            // 一次收货中同行保证只出现一次，便于后续数据关联的时候不出出现多对多的情况
            List<Map<String, SubcontractPayment>> subPaymentsMapList = paymentMap.getOrDefault(subcontractId, new ArrayList<>());
            // 查找第一个不存在该行的集合
            Map<String, SubcontractPayment> subPaymentsMap = null;
            for (Map<String, SubcontractPayment> subMap : subPaymentsMapList) {
                if (!subMap.containsKey(inventTransId)) {
                    subPaymentsMap = subMap;
                    break;
                }
            }
            // 如果没有找到，则新增一个集合
            if (subPaymentsMap == null) {
                subPaymentsMap = new HashMap();
                subPaymentsMapList.add(subPaymentsMap);
            }
            // 集合中添加该行
            subPaymentsMap.put(inventTransId, payment);
            paymentMap.put(subcontractId, subPaymentsMapList);
        }
        
        Map<Integer, String> dataAreaIdMap = new HashMap<Integer, String>(subcontractMap.size());
        for (SubcontractProject subcontract : subcontractMap.values()) {
            // 传入项目转包申请
            config.put("subcontract", subcontract);
            // 设置账套
            String dataAreaId = dataAreaIdMap.get(subcontract.getOrgId());
            if (StringUtils.isBlank(dataAreaId)) {
                Company company = new Company(subcontract.getOrgId());
                company = departmentManageService.queryCompanyOne(company);
                dataAreaId = company.getAccount();
                dataAreaIdMap.put(subcontract.getOrgId(), dataAreaId);
            }
            config.put("dataAreaId", dataAreaId);
            
            // 创建采购订单头
            List<Map<String, SubcontractPayment>> subPaymentsMapList = paymentMap.get(subcontract.getId());
            for (Map<String, SubcontractPayment> subPaymentsMap : subPaymentsMapList) {
                List<SubcontractPayment> subPayments = new ArrayList(subPaymentsMap.values());
                SubcontractPayment subPayment = subPayments.get(0);
                PurchaseReceiptHeader receipt = this.createPurchashReceipt(subPayment, config);
                List<PurchaseReceiptLine> receiptLines = this.createPurchaseReceiptLines(subPayments, config);
                
                // 初始化D365接口配置
                BaseEntity entity = new BaseEntity();
                entity = D365Api.pushPurchaseReceipt(entity, dataAreaId, receipt, receiptLines, config);
                for (SubcontractPayment payment : subPayments) {
                    List<Object> purchIds = (List<Object>) payment.getCustomInfoByKey("purchIds", new ArrayList<Object>());
                    List<Object> inventTransIds = (List<Object>) payment.getCustomInfoByKey("inventTransIds", new ArrayList<Object>());
                    purchIds.addAll((Collection<? extends Object>) entity.getCustomInfoByKey("purchIds"));
                    inventTransIds.addAll((Collection<? extends Object>) entity.getCustomInfoByKey("inventTransIds"));
//                    SubcontractPayment temp = new SubcontractPayment();
//                    temp.setId(payment.getId());
                    payment.setCustomInfoByKey("packingSlipId", receipt.getPackingSlipId());
                    payment.setCustomInfoByKey("purchId", entity.getCustomInfoByKey("purchId"));
                    payment.setCustomInfoByKey("purchIds", purchIds);
                    payment.setCustomInfoByKey("inventTransId", entity.getCustomInfoByKey("inventTransId"));
                    payment.setCustomInfoByKey("inventTransIds", inventTransIds);
                    subcontractService.updateSubcontractPaymentByIdSelective(payment);
                }
            }
        }
    }
    
    /**
     * 基于项目转包结算创建采购收货头
     * @param payment
     * @param config
     * @return
     */
    public PurchaseReceiptHeader createPurchashReceipt(SubcontractPayment payment, Map<String, Object> config) {
//        // 获取结算编号
//        String settleSeq = settlement.getSettleSeq();
        // 此次付款说明
        String memo = payment.getRemark();
        // 实施进度
        String progressDesc = "";
//        // 验收进度
//        String acceptanceDesc = settlement.getAcceptanceDesc();
        
        String dataAreaId = (String) config.get("dataAreaId");
        String purchId = (String) payment.getCustomInfoByKey("purchId");
        PurchaseReceipt t = new PurchaseReceipt();
        t.setPurchId(purchId);
        t.setDataAreaId(dataAreaId);
        long count = purchaseReceiptService.countBySelective(t) + 1;
        String packingSlipId = purchId + "_" + String.format("%02d", count);
        
        // 处理备注信息
        // String remark = SystemLogUtil.format((String) config.getOrDefault("remarkFormat", memo), settlement);
        String remark = memo;
        
        // 创建采购收货头
        PurchaseReceiptHeader receipt = new PurchaseReceiptHeader();
        receipt.setSourceOrderType(DATA_TYPE_SUBCONTRACT);
        receipt.setSourceOrderId(payment.getSubcontractId());
        receipt.setSourceReceiptType(DATA_TYPE_PAYMENT);
        receipt.setSourceReceiptId(payment.getId());
        receipt.setPurchId(purchId);
        receipt.packingSlipId(packingSlipId) // 采购收货单号（物料收货）
                .packingSlipRemark(remark)// 采购收货备注（物料收货描述）
                .projectProgress(progressDesc) // 项目进度
                .deliveryDate((String) payment.getCustomInfoByKey("deliveryDate")) // 交货日期
                .documentDate((String) payment.getCustomInfoByKey("documentDate")) // 下单日期
                .dataAreaId(dataAreaId) // 账套
        ;
        return receipt;
    }
    
    /**
     * 基于项目转包结算创建采购收货行
     * @param payment
     * @param config
     * @return
     */
    public List<PurchaseReceiptLine> createPurchaseReceiptLines(List<SubcontractPayment> payments, Map<String, Object> config) {
        // 获取指定的站点
        String inventSiteId = (String) config.getOrDefault("inventSiteId", "");
        // 获取指定的仓库
        String inventLocationId = (String) config.getOrDefault("inventLocationId", "");
        // 获取指定的库位
        String wmsLocationId = (String) config.getOrDefault("wmsLocationId", "");
        
        SubcontractProject subcontract = (SubcontractProject) config.getOrDefault("subcontract", new SubcontractProject());
        
        // 获取采购订单的基准单位
        String purchUnitBase = (String) subcontract.getCustomInfoByKey("purchUnitBase", config.getOrDefault("purchUnitBase", "price"));
        // 获取采购订单的基准单价，默认为1
        BigDecimal purchPriceBase = new BigDecimal(String.valueOf(subcontract.getCustomInfoByKey("purchPriceBase", config.getOrDefault("purchPriceBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);
        // 获取采购订单的基准数量，默认为1,
        BigDecimal purchQtyBase = new BigDecimal(String.valueOf(subcontract.getCustomInfoByKey("purchQtyBase", config.getOrDefault("purchQtyBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);
        
        
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        decimalFormat.setParseBigDecimal(true);
        
        // 创建采购订单行
        List<PurchaseReceiptLine> receiptLines = new ArrayList<PurchaseReceiptLine>(payments.size());
        for (SubcontractPayment payment : payments) {
            BigDecimal amount = BigDecimal.ZERO;
            String amountStr = StringUtils.defaultIfBlank(StringUtils.trimToEmpty(String.valueOf(payment.getCustomInfoByKey("approvedAmount", payment.getAmount()))), "0");
            try {
                amount = (BigDecimal) decimalFormat.parse(amountStr);
            } catch (ParseException e) {
                amount = new BigDecimal(amountStr.replaceAll(",", ""));
            }
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            // 默认指定基准采购价，数量由转包价和基准采购价确定
            BigDecimal qty = purchQtyBase;
            if ("price".equalsIgnoreCase(purchUnitBase)) {
                // 根据转包价和基准采购价计算采购订单的采购数量
                qty = amount.divide(purchPriceBase, 2, RoundingMode.HALF_UP);
            } else {
                // 根据转包价和基准数量计算采购订单的采购单价
                qty = purchQtyBase.multiply(new BigDecimal(payment.getRatio()).setScale(2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100d))).setScale(2, RoundingMode.HALF_UP);
            }
            // 根据转包价和基准数量计算采购订单的采购单价
            BigDecimal price = amount.divide(qty, 2, RoundingMode.HALF_UP);
            PurchaseReceiptLine receiptLine = new PurchaseReceiptLine();
            receiptLine.setDataAreaId((String) config.get("dataAreaId")); // 采购价
            receiptLine.setAmount(amount); // 采购价
            receiptLine.setPrice(price); // 采购单价
            receiptLine.purchId((String) payment.getCustomInfoByKey("purchId")) // 采购单号
    //                .lineNum(settlement.getSubcontractId().toString()) // 行号（用系统ID代替）
                    .inventTransId((String) payment.getCustomInfoByKey("inventTransId")) // 批次号
                    .qty(qty) // 采购数量
                    .inventSiteId(inventSiteId) // 站点
                    .inventLocationId(inventLocationId) // 仓库
                    .wmsLocationId(wmsLocationId) // 库位
            ;
            receiptLines.add(receiptLine);
        }
        return receiptLines;
    }

    public void noAssigneeAssignmentTask(DelegateTask delegateTask) throws Exception {
        String assignee = delegateTask.getAssignee();
        String ignoreAssignee = StringUtils.defaultIfBlank(getSysArg("perf.activiti.ignoreAssignee"), "无");
        if (StringUtils.isBlank(assignee) || ignoreAssignee.equals(assignee)) {
            taskService.addComment(delegateTask.getId(), delegateTask.getProcessInstanceId(), "无任务办理人，自动完成该任务！");
            taskService.complete(delegateTask.getId());
        }
    }

    private Map<String, Object> getCurrentEntity(VariableScope variableScope) {
        Map<String, Object> entity = variableScope.getVariable("entity", HashMap.class);
        if (entity == null || entity.isEmpty()) {
            entity = new HashMap<>();
            Integer subcontractId = variableScope.getVariable("subcontractId", Integer.class);
            SubcontractProject subcontract = subcontractService.selectSubcontractProjectById(subcontractId);
            SubcontractPayment payment = new SubcontractPayment();
            payment.setSubcontractId(subcontractId);
            payment.setOrgId(subcontract.getOrgId());
            List<SubcontractPayment> paymentList = subcontractService.selectSubcontractPaymentList(payment);
//            SubcontractPayment subcontractPayment = paymentList.stream().filter(p -> p.getConfirmTime() == null).findFirst().get();
            paymentList = paymentList.stream().filter(p -> p.getConfirmTime() == null).collect(Collectors.toList());
//            entity.put("dataId", subcontractPayment.getId());
            entity.put("objId", subcontractId);
            entity.put("objType", DATA_TYPE_SUBCONTRACT);
            entity.put("dataType", DATA_TYPE_PAYMENT);
            entity.put("entity", paymentList);
        }
        String procInstId = "";
        if (variableScope instanceof DelegateTask) {
            procInstId = ((DelegateTask) variableScope).getProcessInstanceId();
        } else if (variableScope instanceof DelegateExecution) {
            procInstId = ((DelegateExecution) variableScope).getProcessInstanceId();
        }
        entity.put("procInstId", procInstId);
//        pmWorkFlow = pmFlowService.decoratorEntity(pmWorkFlow);
        return entity;
    }

    public String getSysArg(String code) {
        String args = basicDataService.querySysArg(code);
        return args;
    }

    public String getSysArg(String code, String defaultValue) {
        String args = basicDataService.querySysArg(code);
        return StringUtils.defaultIfBlank(args, defaultValue);
    }
    
    public String joinStr(Object... array) {
        return StringUtils.join(Arrays.asList(array).stream().filter(s -> s != null && s.toString().length() > 0)
                .toArray(String[]::new), "-");
    }
}
