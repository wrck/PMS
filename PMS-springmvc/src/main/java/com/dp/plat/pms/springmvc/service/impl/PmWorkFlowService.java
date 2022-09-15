package com.dp.plat.pms.springmvc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.NativeHistoricTaskInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.dao.PmWorkFlowMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IPmWorkFlowService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("pmWorkFlowService")
public class PmWorkFlowService extends AbstractBaseService<PmWorkFlowMapper, PmWorkFlow> implements IPmWorkFlowService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IProcessService processService;
    
    @Override
    public List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, Integer assignee, String processKey, String taskKey) {
        // TaskQuery taskQuery =
        // taskService.createTaskQuery().processDefinitionKey(processKey).taskAssignee(assignee.toString()).taskDefinitionKey(taskKey);
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(processKey).taskCandidateOrAssigned(assignee.toString()).taskDefinitionKey(taskKey).taskVariableValueLessThanOrEqual("startTime", new Date()).active();
        Integer totalSum = taskQuery.list().size();
        // pageParam.setTotal(totalSum);
        if (pageParam.getPageSize() == -1) {
            pageParam.setPageSize(totalSum);
        }
        List<Task> taskList = taskQuery.orderByTaskCreateTime().asc().listPage(pageParam.getStart(), (int) pageParam.getPageSize());
        List<PmWorkFlow> pmWorkFlowList = new ArrayList<PmWorkFlow>();
        for (Task task : taskList) {
            PmWorkFlow pmWorkFlow = (PmWorkFlow) runtimeService.getVariable(task.getExecutionId(), "entity");
            if (pmWorkFlow == null) {
                pmWorkFlow = new PmWorkFlow();
            }
            //			PlanVO planVO = new PlanVO();
            //			planVO.setId(pmWorkFlow.getPlanId());
            //			planVO.setParticipantId(pmWorkFlow.getParticipantId());
            //			List<PlanVO> planVOs = planService.selectVOBySelective(planVO);
            //			if (planVOs.isEmpty()) {
            //				totalSum--;
            //				continue;
            //			}
            //			planVO = planVOs.get(0);
            //			pmWorkFlow.setPlanVO(planVO);
            //			pmWorkFlow.setTaskId(task.getId());
            //			pmWorkFlow.setAssignee(task.getAssignee());
            //			if (StringUtils.isNotBlank(task.getAssignee())) {
            //				Employee employee = employeeDao.selectByPrimaryKey(Integer.valueOf(task.getAssignee()));
            //				pmWorkFlow.setAssigneeName(employee != null ? (employee.getWorkNo() + "-"  + employee.getName()) : "");
            //			} else {
            //				List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
            //				if (!identityLinks.isEmpty()) {
            //					IdentityLink link = identityLinks.get(0);
            //					if (link.getGroupId() != null) {
            //						Group group = identityService.createGroupQuery().groupId(link.getGroupId()).singleResult();
            //						pmWorkFlow.setAssigneeName(group != null ? group.getName() : "");
            //					} else {
            //						pmWorkFlow.setAssignee(link.getUserId());
            //						Employee employee = employeeDao.selectByPrimaryKey(Integer.valueOf(link.getUserId()));
            //						pmWorkFlow.setAssigneeName(employee != null ? (employee.getWorkNo() + "-"  + employee.getName()) : "");
            //					}
            //				}
            //			}
            pmWorkFlow.setFormUrl(task.getFormKey());
            pmWorkFlow.setTaskKey(taskKey);
            pmWorkFlow.setProcInstId(task.getProcessInstanceId());
            pmWorkFlow.setDueTime(task.getDueDate());
            pmWorkFlowList.add(pmWorkFlow);
        }
        pageParam.setTotal(totalSum + pageParam.getTotal());
        return pmWorkFlowList;
    }

    @Override
    public List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, Integer assignee, List<String> processKeys, List<String> taskKeys) {
        //		TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKeyIn(processKeys)
        //				.taskCandidateOrAssigned(assignee.toString());
        //		taskQuery = taskQuery.or();
        //		for (String taskKey : taskKeys) {
        //			taskQuery = taskQuery.taskDefinitionKey(taskKey);
        //		}
        //		taskQuery.endOr().taskVariableValueLessThanOrEqual("startTime", new Date()).active();// startTime在planStepListener中赋值，取考核步骤的开始时间，默认值当前系统时间
        NativeTaskQuery nativeTaskQuery = taskService.createNativeTaskQuery().sql("");
        //		NativeTaskQuery nativeTaskQuery = taskService.createNativeTaskQuery().sql(PerfSqlConstant.WORKBENCH_TASK_SQL);
        nativeTaskQuery.parameter("processKeys", StringUtils.join(processKeys, ","));
        nativeTaskQuery.parameter("taskKeys", StringUtils.join(taskKeys, ","));
        nativeTaskQuery.parameter("assignee", assignee);
        nativeTaskQuery.parameter("candidateGroups", StringUtils.join(getCandidateGroups(assignee), ","));
        nativeTaskQuery.parameter("startTime", new Date().getTime());
        Integer totalSum = nativeTaskQuery.list().size();
        // pageParam.setTotal(totalSum);
        if (pageParam.getPageSize() == -1) {
            pageParam.setPageSize(totalSum);
        }
        List<Task> taskList = nativeTaskQuery.listPage(pageParam.getStart(), (int) pageParam.getPageSize());
        List<PmWorkFlow> pmWorkFlowList = new ArrayList<PmWorkFlow>();
        for (Task task : taskList) {
            PmWorkFlow pmWorkFlow = (PmWorkFlow) runtimeService.getVariable(task.getExecutionId(), "entity");
            if (pmWorkFlow == null) {
                pmWorkFlow = new PmWorkFlow();
            }
            //			PlanVO planVO = new PlanVO();
            //			planVO.setId(pmWorkFlow.getPlanId());
            //			planVO.setParticipantId(pmWorkFlow.getParticipantId());
            //			List<PlanVO> planVOs = planService.selectVOBySelective(planVO);
            //			if (planVOs.isEmpty()) {
            //				totalSum--;
            //				continue;
            //			}
            //			planVO = planVOs.get(0);
            //			pmWorkFlow.setPlanVO(planVO);
            //			pmWorkFlow.setTaskId(task.getId());
            //			pmWorkFlow.setAssignee(task.getAssignee());
            //			if (StringUtils.isNotBlank(task.getAssignee())) {
            //				Employee employee = employeeDao.selectByPrimaryKey(Integer.valueOf(task.getAssignee()));
            //				pmWorkFlow.setAssigneeName(employee != null ? (employee.getWorkNo() + "-"  + employee.getName()) : "");
            //			} else {
            //				List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
            //				if (!identityLinks.isEmpty()) {
            //					IdentityLink link = identityLinks.get(0);
            //					if (link.getGroupId() != null) {
            //						Group group = identityService.createGroupQuery().groupId(link.getGroupId()).singleResult();
            //						pmWorkFlow.setAssigneeName(group != null ? group.getName() : "");
            //					} else {
            //						pmWorkFlow.setAssignee(link.getUserId());
            //						Employee employee = employeeDao.selectByPrimaryKey(Integer.valueOf(link.getUserId()));
            //						pmWorkFlow.setAssigneeName(employee != null ? (employee.getWorkNo() + "-"  + employee.getName()) : "");
            //					}
            //				}
            //			}
            pmWorkFlow.setFormUrl(task.getFormKey());
            pmWorkFlow.setTaskKey(task.getTaskDefinitionKey());
            pmWorkFlow.setProcInstId(task.getProcessInstanceId());
            pmWorkFlow.setDueTime(task.getDueDate());
            pmWorkFlowList.add(pmWorkFlow);
        }
        pageParam.setTotal(totalSum);
        return pmWorkFlowList;
    }

    @Override
    public List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, Integer assignee) {
        //		HistoricTaskInstanceQuery taskQuery =
        //		 historyService.createHistoricTaskInstanceQuery().or();
        //		 List<String> candidateGroups = getCandidateGroups(assignee);
        //		if (!candidateGroups.isEmpty()) {
        //		 taskQuery = taskQuery.taskCandidateGroupIn(candidateGroups);
        //		 }
        //		 taskQuery = taskQuery.taskAssignee(assignee.toString()).endOr().finished();
        String fuzzy = pageParam.getFuzzy();
        Boolean fuzzySearch = pageParam.isFuzzySearch();
        if (!(fuzzySearch && StringUtils.isNotBlank(fuzzy))) {
            fuzzy = null;
        }
        NativeHistoricTaskInstanceQuery taskQuery = this.candidateOrAssignedHistoricTaskQuery(assignee, fuzzy);
        long totalSum = taskQuery.list().size();
        pageParam.setTotal(totalSum);
        if (pageParam.getPageSize() == -1) {
            pageParam.setPageSize(totalSum);
        }
        // List<HistoricTaskInstance> taskList =
        // taskQuery.orderByHistoricTaskInstanceEndTime().desc().listPage(pageParam.getStart(),
        // (int) pageParam.getPageSize());
        List<HistoricTaskInstance> taskList = taskQuery.listPage(pageParam.getStart(), (int) pageParam.getPageSize());
        List<PmWorkFlow> pmWorkFlowList = new ArrayList<PmWorkFlow>();
        for (HistoricTaskInstance task : taskList) {
            HistoricVariableInstance entity = historyService.createHistoricVariableInstanceQuery().executionId(task.getExecutionId()).variableName("entity").singleResult();
            if (entity == null) {
                entity = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).variableName("entity").singleResult();
            }
            if (entity == null) {
                continue;
            }
            PmWorkFlow pmWorkFlow = (PmWorkFlow) entity.getValue();
            if (pmWorkFlow == null) {
                pmWorkFlow = new PmWorkFlow();
            }
            //			PlanVO planVO = new PlanVO();
            //			planVO.setId(pmWorkFlow.getPlanId());
            //			planVO.setParticipantId(pmWorkFlow.getParticipantId());
            //			List<PlanVO> planVOs = planService.selectVOBySelective(planVO);
            //			if (planVOs.isEmpty()) {
            //				continue;
            //			}
            //			planVO = planVOs.get(0);
            //			pmWorkFlow.setPlanVO(planVO);
            //			pmWorkFlow.setTaskId(task.getId());
            //			pmWorkFlow.setAssignee(task.getAssignee());
            //			if (StringUtils.isNotBlank(task.getAssignee())) {
            //				Employee employee = employeeDao.selectByPrimaryKey(Integer.valueOf(task.getAssignee()));
            //				pmWorkFlow.setAssigneeName(employee != null ? (employee.getWorkNo() + "-" + employee.getName()) : "");
            //			} else {
            //				List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(task.getId());
            //				if (!identityLinks.isEmpty()) {
            //					HistoricIdentityLink link = identityLinks.get(0);
            //					if (link.getGroupId() != null) {
            //						Group group = identityService.createGroupQuery().groupId(link.getGroupId()).singleResult();
            //						pmWorkFlow.setAssigneeName(group != null ? group.getName() : "");
            //					} else {
            //						pmWorkFlow.setAssignee(link.getUserId());
            //						Employee employee = employeeDao.selectByPrimaryKey(Integer.valueOf(link.getUserId()));
            //						pmWorkFlow.setAssigneeName(employee != null ? (employee.getWorkNo() + "-" + employee.getName()) : "");
            //					}
            //				}
            //			}
            pmWorkFlow.setFormUrl(task.getFormKey());
            pmWorkFlow.setTaskKey(task.getTaskDefinitionKey());
            pmWorkFlow.setProcInstId(task.getProcessInstanceId());
            pmWorkFlow.setDueTime(task.getDueDate());
            pmWorkFlow.setEndTime(task.getEndTime());
            Result canWithdraw = processService.canWithdraw(task.getProcessInstanceId(), task.getAssignee());
            pmWorkFlow.setCanWithdraw(canWithdraw.isSuccess());
            pmWorkFlowList.add(pmWorkFlow);
        }
        // pageParam.setTotal(pmWorkFlowList.size());
        return pmWorkFlowList;
    }

    @Override
    public PmWorkFlow currentParticipantWorkFlow(PmWorkFlow pmWorkFlow, Object planParticipant) {
        // User user = UserContext.getCurrentUser();
        Principal user = UserContext.getCurrentPrincipal();
        PmWorkFlow workFlow = new PmWorkFlow();
        Integer empID = user.getUserCustom4();
        //		Integer empID = user.getUserInfoId();
        String assignee = String.valueOf(empID);
        String taskId = pmWorkFlow.getTaskId();
        if (!StringUtils.isNotEmpty(taskId)) {
        //			workFlow.setPlanId(planParticipant.getPlanId());
        //			workFlow.setParticipantId(planParticipant.getId());
        //			workFlow.setStatus(BaseVO.PENDING);
        //			List<PmWorkFlow> pmWorkFlowList = this.selectBySelective(workFlow);
        //			if (!pmWorkFlowList.isEmpty()) {
        //				List<String> processInstanceIds = new ArrayList<>(pmWorkFlowList.size());
        //				for (PmWorkFlow temp : pmWorkFlowList) {
        //					processInstanceIds.add(temp.getProcInstId());
        //				}
        //				List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(assignee)
        //						.processInstanceIdIn(processInstanceIds).active()
        //						.taskVariableValueLessThanOrEqual("startTime", new Date()).list();
        //				// 存在当前任务时赋任务相关的信息
        //				List<String> taskIds = new ArrayList<>(taskList.size());
        //				List<String> taskKeys = new ArrayList<>(taskList.size());
        //				List<String> processKeys = new ArrayList<>(taskList.size());
        //				List<String> bussnessKeys = new ArrayList<>(taskList.size());
        //				List<String> prioritys = new ArrayList<>(taskList.size());
        //				processInstanceIds.clear();
        //				for (Task task : taskList) {
        //					ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionId(task.getProcessDefinitionId()).processInstanceId(task.getProcessInstanceId()).active().singleResult();
        //					
        //					taskIds.add(task.getId());
        //					taskKeys.add(task.getTaskDefinitionKey());
        //					processKeys.add(processInstance.getProcessDefinitionKey());
        //					bussnessKeys.add(String.valueOf(workFlow.getId()));
        //					processInstanceIds.add(task.getProcessInstanceId());
        //					// 获取主管任务等级
        //					if (PlanTaskKey.DIRECT_SUMMARY_KEY.equals(task.getTaskDefinitionKey())) {
        //						// 如果是主管审批任务时获取审批等级（一考二考还是三考）
        //						PlanObjectiveAppraiserRelationshipVO appraiser = (PlanObjectiveAppraiserRelationshipVO) taskService
        //								.getVariable(task.getId(), "appraiser");
        //						prioritys.add(String.valueOf(appraiser.getPriority()));
        //					} else if (PlanTaskKey.SELF_SUMMARY_KEY.equals(task.getTaskDefinitionKey())) {
        //						prioritys.add("0");
        //					}
        //				}
        //				workFlow.setTaskId(StringUtils.join(taskIds, ","));
        //				workFlow.setTaskKey(StringUtils.join(taskKeys, ","));
        //				workFlow.setProcessKey(StringUtils.join(processKeys, ","));
        //				workFlow.setBusinessKey(StringUtils.join(bussnessKeys, ","));
        //				workFlow.setProcInstId(StringUtils.join(processInstanceIds, ","));
        //				workFlow.setCurrentPriority(StringUtils.join(prioritys, ","));
        //				pmWorkFlowList.clear();
        //			}
        //			// 判断当前用户是否有该考核人的任务，如果没有查询
        //			if (StringUtils.isBlank(workFlow.getTaskId())) {
        //				workFlow.setStatus(null);
        //				pmWorkFlowList = this.selectBySelective(workFlow);
        //				if (!pmWorkFlowList.isEmpty()) {
        //					List<String> processInstanceIds = new ArrayList<>(pmWorkFlowList.size());
        //					for (PmWorkFlow temp : pmWorkFlowList) {
        //						processInstanceIds.add(temp.getProcInstId());
        //					}
        //					List<String> candidateGroups = this.getCandidateGroups(empID);
        //					HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
        //							.processInstanceIdIn(processInstanceIds).or().taskAssignee(assignee);
        //					if (!candidateGroups.isEmpty()) {
        //						taskQuery = taskQuery.taskCandidateGroupIn(candidateGroups);
        //					}
        //					List<HistoricTaskInstance> historyTaskList = taskQuery.endOr().list();
        //					if (!historyTaskList.isEmpty()) {
        //						workFlow.setHasTask(true);
        //					}
        //				}
        //			} else {
        //				workFlow.setHasTask(true);
        //			}
        } else {
            // 当前任务
            TaskInfo task = taskService.createTaskQuery().taskId(taskId).taskCandidateOrAssigned(assignee).active().singleResult();
            if (task == null) {
                // 历史任务
                List<String> candidateGroups = this.getCandidateGroups(empID);
                HistoricTaskInstanceQuery historicTaskQuery = historyService.createHistoricTaskInstanceQuery().taskId(taskId).or().taskAssignee(assignee);
                if (!candidateGroups.isEmpty()) {
                    historicTaskQuery = historicTaskQuery.taskCandidateGroupIn(candidateGroups);
                }
                task = historicTaskQuery.endOr().singleResult();
            } else {
                // 存在当前任务时赋任务相关的信息
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionId(task.getProcessDefinitionId()).processInstanceId(task.getProcessInstanceId()).active().singleResult();
                workFlow.setTaskId(task.getId());
                workFlow.setTaskKey(task.getTaskDefinitionKey());
                workFlow.setProcessKey(processInstance.getProcessDefinitionKey());
                workFlow.setProcInstId(task.getProcessInstanceId());
            //				// 获取主管任务等级
            //				if (PlanTaskKey.DIRECT_SUMMARY_KEY.equals(task.getTaskDefinitionKey())) {
            //					// 如果是主管审批任务时获取审批等级（一考二考还是三考）
            //					PlanObjectiveAppraiserRelationshipVO appraiser = (PlanObjectiveAppraiserRelationshipVO) taskService
            //							.getVariable(task.getId(), "appraiser");
            //					workFlow.setCurrentPriority(String.valueOf(appraiser.getPriority()));
            //				} else if (PlanTaskKey.SELF_SUMMARY_KEY.equals(task.getTaskDefinitionKey())) {
            //					workFlow.setCurrentPriority("0");
            //				}
            }
            if (task != null) {
                workFlow.setHasTask(true);
            }
        }
        return workFlow;
    }

    /**
	 * 获取用户的已办任务，关联perf_workflow，保留最新的流程实例
	 * 
	 * @param assignee
	 * @param fuzzy
	 * @return NativeHistoricTaskInstanceQuery
	 */
    private NativeHistoricTaskInstanceQuery candidateOrAssignedHistoricTaskQuery(Integer assignee, String fuzzy) {
        List<String> candidateGroups = this.getCandidateGroups(assignee);
        String fuzzySearchSql = "";
        if (StringUtils.isNotBlank(fuzzy)) {
            fuzzySearchSql = "AND ( pw.`empName` LIKE CONCAT('%', #{fuzzy} , '%') OR pw.`workNo` LIKE CONCAT('%', #{fuzzy} , '%') OR p.`name` LIKE CONCAT('%', #{fuzzy}, '%') OR lk.`codeName` LIKE CONCAT('%', #{fuzzy}, '%'))";
        }
        NativeHistoricTaskInstanceQuery taskQuery = //						+ "GROUP BY RES.`PROC_INST_ID_` ORDER BY RES.END_TIME_ DESC");
        historyService.createNativeHistoricTaskInstanceQuery().sql("SELECT * FROM ACT_HI_TASKINST RES INNER JOIN (" + "SELECT DISTINCT MAX(CAST(RES.ID_ AS UNSIGNED)) AS maxID FROM ACT_HI_TASKINST RES " + "	LEFT JOIN ACT_HI_IDENTITYLINK HI_OR0 ON HI_OR0.TASK_ID_ = RES.ID_ " + " INNER JOIN perf_workflow pw ON pw.`procInstId` = RES.`PROC_INST_ID_` " + "	LEFT JOIN `perf_plan` p ON p.`id` = pw.`planId` " + " LEFT JOIN `perf_lookup` lk ON lk.codeType = 'planParticipantStatus' AND lk.state = TRUE " + "		AND lk.code = IF(LOCATE('directSummary', RES.`TASK_DEF_KEY_`) > 0, CONCAT(RES.TASK_DEF_KEY_, '_', RES.`PRIORITY_`), RES.TASK_DEF_KEY_)" + "WHERE RES.END_TIME_ IS NOT NULL AND ( RES.ASSIGNEE_ = #{assignee} OR ( HI_OR0.`TYPE_` = 'candidate' AND ( HI_OR0.USER_ID_ = #{assignee} OR FIND_IN_SET(HI_OR0.`GROUP_ID_`, #{groupIds}) ) ) ) " + fuzzySearchSql + "GROUP BY pw.`planId`, pw.`participantId`) t ON RES.`ID_` = t.maxID ORDER BY RES.END_TIME_ DESC");
        if (!candidateGroups.isEmpty()) {
            taskQuery.parameter("groupIds", StringUtils.join(candidateGroups, ","));
        }
        if (StringUtils.isNotBlank(fuzzy)) {
            taskQuery.parameter("fuzzy", fuzzy);
        }
        taskQuery.parameter("assignee", String.valueOf(assignee));
        return taskQuery;
    }

    /**
	 * 获取用户的候选组
	 * 
	 * @param assignee
	 * @return candidateGroups
	 */
    private List<String> getCandidateGroups(Integer assignee) {
        List<Group> groups = identityService.createGroupQuery().groupMember(String.valueOf(assignee)).list();
        List<String> candidateGroups = new ArrayList<>(groups.size());
        for (Group group : groups) {
            candidateGroups.add(group.getId());
        }
        return candidateGroups;
    }

    @Override
    public List<String> selectProcInstIdsBySelective(PmWorkFlowVO workFlow) {
        return dao.selectProcInstIdsBySelective(workFlow);
    }

    @Override
    public void deleteProcess(final Object planParticipant, final String participantIds) {
        PmWorkFlowVO pmWorkFlowVO = new PmWorkFlowVO();
        List<String> procInstIds = this.selectProcInstIdsBySelective(pmWorkFlowVO);
        deleteProcessThread(procInstIds);
    }

    @Override
    public void deleteProcess(final PmWorkFlowVO pmWorkFlowVO) {
        List<String> procInstIds = this.selectProcInstIdsBySelective(pmWorkFlowVO);
        deleteProcessThread(procInstIds);
    }

    @Override
    public void deleteProcessThread(final List<String> procInstIds) {
        if (procInstIds.isEmpty()) {
            return;
        }
        Thread generateThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (String processInstanceId : procInstIds) {
                        dao.deleteByProcInstId(processInstanceId);
                        runtimeService.deleteProcessInstance(processInstanceId, "被考核人已被删除，结束流程");
                    }
                } catch (Exception e) {
                    ExceptionHandler.insertException(e);
                }
            }
        });
        generateThread.start();
    }

    @Override
    public void deleteProcess(final List<String> procInstIds) {
        if (procInstIds.isEmpty()) {
            return;
        }
        try {
            dao.deleteByProcInstIds(StringUtils.join(procInstIds, ","));
            for (String processInstanceId : procInstIds) {
                try {
                    runtimeService.deleteProcessInstance(processInstanceId, "被考核人已被删除，结束流程");
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            ExceptionHandler.insertException(e);
        }
    }

    @Override
    public Integer selectParticipantFinallyTask(String taskKey, Integer participantId) {
        try {
            PmWorkFlow tempPwf = new PmWorkFlow();
            tempPwf.setTaskKey(taskKey);
            //			tempPwf.setParticipantId(participantId);
            tempPwf.setStatus(BaseVO.PENDING);
            long count = this.countBySelective(tempPwf);
            if (count == 0) {
                NativeHistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createNativeHistoricTaskInstanceQuery().sql("SELECT * FROM `act_hi_taskinst` WHERE TASK_DEF_KEY_ = #{taskKey} AND (DELETE_REASON_ IS NULL OR DELETE_REASON_ = 'completed') AND FORM_KEY_ = CONCAT('/perf/planParticipant/planList/', #{participantId}, '.html') ORDER BY START_TIME_ DESC LIMIT 1");
                historicTaskInstanceQuery.parameter("taskKey", taskKey).parameter("participantId", participantId);
                HistoricTaskInstance historicTask = historicTaskInstanceQuery.singleResult();
                if (historicTask != null) {
                    String assignee = StringUtils.trimToNull(historicTask.getAssignee());
                    return StringUtils.trimToNull(assignee) != null ? Integer.valueOf(assignee) : null;
                }
            }
        } catch (Exception e) {
            ExceptionHandler.insertException(e);
        }
        return null;
    }

    @Override
    @Transactional
    public String startProcess(PmWorkFlow pmWorkFlow, Object entity) {
        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(String.valueOf(UserContext.getCurrentPrincipal().getUserInfoId()));
        pmWorkFlow.setApplyTime(new Date());
        pmWorkFlow.setUserId(UserContext.getCurrentPrincipal().getUserId());
        pmWorkFlow.setApplyUserId(UserContext.getCurrentPrincipal().getUserInfoId());
        this.insertSelective(pmWorkFlow);
        String businessKey = pmWorkFlow.getId().toString();
        pmWorkFlow.setBusinessKey(businessKey);
        pmWorkFlow.setEntity(entity);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("entity", pmWorkFlow);
        ExecutionEntity processInstance = (ExecutionEntity) runtimeService.startProcessInstanceByKey(pmWorkFlow.getProcessKey(), businessKey, variables);
        pmWorkFlow = (PmWorkFlow) processInstance.getVariable("entity");
        pmWorkFlow.setBeginTime(new Date());
        String processInstanceId = processInstance.getId();
        //		StringBuilder stringBuilder = new StringBuilder(pmWorkFlow.getTitle());
        //		stringBuilder.append(" -- ");
        //		stringBuilder.append(processInstance.getCurrentActivityName());
        //		runtimeService.setProcessInstanceName(processInstanceId, stringBuilder.toString());
        pmWorkFlow.setProcInstId(processInstanceId);
        dao.updateByPrimaryKeySelective(pmWorkFlow);
        runtimeService.setVariable(processInstanceId, "entity", pmWorkFlow);
        //		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
        //		if (null != task) {
        //			pmWorkFlow.setTaskId(task.getId());
        //		}
        boolean isNeedSuspend = false;
        // 如果存在正在进行中的流程，则挂起新发起的流程
        if (isNeedSuspend) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
        }
        // 最后要设置null,清空上下文
        this.identityService.setAuthenticatedUserId(null);
        return processInstanceId;
    }

    @Override
    public void terminateProcess(PmWorkFlowVO workflow, String terminateReason) {
        identityService.setAuthenticatedUserId(String.valueOf(UserContext.getCurrentPrincipal().getUserInfoId()));
        List<String> procInstIds = dao.selectProcInstIdsBySelective(workflow);
        for (String processInstanceId : procInstIds) {
            processService.terminateProcess(processInstanceId, terminateReason);
        }
        //		List<Task> taskList = taskService.createTaskQuery().processInstanceIdIn(procInstIds).list();
        //		List<String> taskIds = new ArrayList<>(taskList.size());
        //		for (Task task : taskList) {
        //			taskIds.add(task.getId());
        //		}
        //		ProjectUtils.terminateActivities(taskIds);
        // 最后要设置null,清空上下文
        identityService.setAuthenticatedUserId(null);
    }
    
    @Override
    public List<String> selectActivitiUserMails(Map<String, Object> params) {
		return dao.selectActivitiUserMails(params);
    }
    
    /**
	 * 装饰流程变量实体
	 * @param pmWorkFlow
	 * @return
	 */
    @Override
	public PmWorkFlow decoratorEntity(PmWorkFlow pmWorkFlow) {
		if (pmWorkFlow == null || StringUtils.isBlank(pmWorkFlow.getProcInstId())) {
			return pmWorkFlow;
		}
		Object entity = pmWorkFlow.getEntity();
		// 查询数据库的最新流程记录
		PageParam<PmWorkFlow> pageParam = new PageParam<PmWorkFlow>();
		pageParam.setOrderBy("id desc");
		pageParam.setPageSize(1);
		PmWorkFlow temp = new PmWorkFlow();
		temp.setProcessKey(pmWorkFlow.getProcessKey());
		temp.setProcInstId(pmWorkFlow.getProcInstId());
		pageParam.setModel(temp);
		List<Object> list = this.selectBySelectivePageable(pageParam);
		if (list == null || list.isEmpty()) {
			return pmWorkFlow;
		}
		BaseVO old = pmWorkFlow;
		
		pmWorkFlow = (PmWorkFlow) list.get(0);
		pmWorkFlow.setApplyUserId(old.getApplyUserId());
		pmWorkFlow.setBusinessKey(pmWorkFlow.getId().toString());
//		String objType = pmWorkFlow.getObjType();
		Integer objId = pmWorkFlow.getObjId();
		String dataType = pmWorkFlow.getDataType();
		Integer dataId = pmWorkFlow.getDataId();
		if (!pmWorkFlow.getBusinessKey().equalsIgnoreCase(old.getBusinessKey())) {
			String serviceBeanName = dataType + "Service";
			IAbstractBaseService<?> service = null;
			try { 
				service = SpringContext.getBean(serviceBeanName, IAbstractBaseService.class);
			} catch (Exception e) {
			}
			if (service != null) {
			    entity = service.selectByPrimaryKey(dataId);
			}
			if (DataType.PROJECT_TASK.equals(dataType)) {
				ProjectTask projectTask = (ProjectTask) entity;
				projectTask.setProjectId(objId);
				projectTask.setTaskId(dataId);
			}  else if (DataType.INDUSTRY_ASSET.equals(dataType)) {
				// 项目资产，更新入库状态和入库时间
				IndustryAsset industryAsset = (IndustryAsset) entity;
				industryAsset.setId(dataId);
			} else if (DataType.INDUSTRY_LEAK.equals(dataType)) {
				// 行业漏洞，更新入库状态和入库时间
				IndustryLeak industryLeak = (IndustryLeak) entity;
				industryLeak.setId(dataId);
			} else if (DataType.PROJECT_DISPATCH.equals(dataType)) {
			    DispatchProject dispatch = (DispatchProject) entity;
			    dispatch.setId(dataId);
            }
		}
		if (DataType.DISPATCH_SETTLEMENT.equals(dataType)) {
            DispatchSettlement settlement = (DispatchSettlement) entity;
            SettlementVO settlementVO = new SettlementVO();
            BeanUtils.copyProperties(settlement, settlementVO);
            IDispatchProjectService dispatchProjectService = SpringContext.getBean("dispatchProjectService", IDispatchProjectService.class);
            DispatchVO dispatch = dispatchProjectService.selectDispatchVOWithAmount(settlementVO.getDispatchId());
            settlementVO.setDispatch(dispatch);
            entity = settlementVO;
        }
		pmWorkFlow.setEntity(entity);
		return pmWorkFlow;
	}
}
