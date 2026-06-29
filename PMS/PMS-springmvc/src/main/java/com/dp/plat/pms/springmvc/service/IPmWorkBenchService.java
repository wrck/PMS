package com.dp.plat.pms.springmvc.service;

import java.util.List;

import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

public interface IPmWorkBenchService {

	/**
	 * 根据受理人id和任务key查询代办的绩效任务
	 * 
	 * @param pageParam
	 * @param assignee
	 * @param processKeys
	 * @param taskKeys
	 * @return
	 */
	List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam,
			Integer assignee, List<String> processKeys, List<String> taskKeys);

	/**
	 * 根据受理人id查询已办的绩效任务
	 * 
	 * @param pageParam
	 * @param assignee
	 * @return
	 */
	List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, Integer assignee);

	List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, PmWorkFlowVO workFlow);

	List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam,
			PmWorkFlow workFlow);


}
