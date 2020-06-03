package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import java.util.List;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;
import com.dp.plat.core.service.IAbstractBaseService;

/**
 *
 * Created by CodeGenerator
 */
public interface IPmWorkFlowService extends IAbstractBaseService<PmWorkFlow> {

    /**
	 * 根据受理人id和任务key查询代办的绩效任务
	 * 
	 * @param pageParam
	 * @param assignee
	 * @param processKey
	 * @param taskKey
	 * @return
	 */
    List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, Integer assignee, String processKey, String taskKey);

    /**
	 * 根据受理人id和任务keys查询代办的绩效任务
	 * 
	 * @param pageParam
	 * @param assignee
	 * @param processKeys
	 * @param taskKeys
	 * @return
	 */
    List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, Integer assignee, List<String> processKeys, List<String> taskKeys);

    /**
	 * 根据受理人id,查询所有已办任务
	 * 
	 * @param pageParam
	 * @param assignee
	 * @return
	 */
    List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, Integer assignee);

    /**
	 * 根据被考核人，以及流程查询参数，获取当前用户正在对该被考核人进行考核的任务，以及历史任务
	 * 
	 * @param pmWorkFlow
	 * @param planParticipant
	 * @return currentParticipantWorkFlow
	 */
    PmWorkFlow currentParticipantWorkFlow(PmWorkFlow pmWorkFlow, Object planParticipant);

    /**
	 * 查询流程实例IDs
	 * 
	 * @param workFlow
	 * @return
	 */
    List<String> selectProcInstIdsBySelective(PmWorkFlowVO workFlow);

    /**
	 * 删除流程
	 * 
	 * @param planParticipant
	 * @param participantIds
	 */
    void deleteProcess(Object planParticipant, String participantIds);

    /**
	 * 删除流程,线程处理
	 * 
	 * @param procInstIds
	 */
    void deleteProcessThread(List<String> procInstIds);

    /**
	 * 删除流程
	 * 
	 * @param pmWorkFlowVO
	 */
    void deleteProcess(PmWorkFlowVO pmWorkFlowVO);

    /**
	 * 查找某个考核人的某个任务的最终办理人
	 * @param taskKey
	 * @param participantId
	 * @return
	 */
    Integer selectParticipantFinallyTask(String taskKey, Integer participantId);

    void deleteProcess(List<String> procInstIds);

    String startProcess(PmWorkFlow pmWorkFlow, Object entity);

    /**
	 * 终止流程
	 * @param workflow
	 * @param terminateReason
	 */
    void terminateProcess(PmWorkFlowVO workflow, String terminateReason);
}
