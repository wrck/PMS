package com.dp.plat.pms.springmvc.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.dao.PmWorkBenchMapper;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.service.IPmWorkBenchService;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

@Service("perfWorkBenchService")
public class PmWorkBenchService implements IPmWorkBenchService {

	@Autowired
	private PmWorkBenchMapper dao;
	
	@Autowired
	private IProcessService processService;
	
	@Override
	public List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, PmWorkFlow workFlow) {
		pageParam.setModel(workFlow);
		
		boolean fuzzySearch = pageParam.isFuzzySearch();
		pageParam.setFuzzySearch(false);
		
		pageParam.setTotal(dao.countRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam));
		
		pageParam.setFuzzySearch(fuzzySearch);
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		} else {
			//pageParam.setFiltered(dao.countRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam));
		}
		
		List<PmWorkFlow> pmWorkFlows = dao.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam);
		return pmWorkFlows;
	}
	
	@Override
	public List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam,
			Integer assignee, List<String> processKeys, List<String> taskKeys) {
		PmWorkFlowVO workFlow = new PmWorkFlowVO();
		workFlow.setTaskKey(StringUtils.join(taskKeys, ","));
		workFlow.setProcessKey(StringUtils.join(processKeys, ","));
		workFlow.setAssignee(String.valueOf(assignee));
		workFlow.setBeginTime(new Date());
		return selectRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam, workFlow);
//		pageParam.setModel(workFlow);
//		
//		boolean fuzzySearch = pageParam.isFuzzySearch();
//		pageParam.setFuzzySearch(false);
//		
//		pageParam.setTotal(dao.countRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam));
//		
//		pageParam.setFuzzySearch(fuzzySearch);
//		if (pageParam.getPageSize() == -1L) {
//			pageParam.setPageSize(pageParam.getTotal());
//		} else {
//			//pageParam.setFiltered(dao.countRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam));
//		}
//		
//		List<PmWorkFlow> pmWorkFlows = dao.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam);
//		return pmWorkFlows;
	}
	
	@Override
	public List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, PmWorkFlowVO workFlow) {
		pageParam.setModel(workFlow);
		
		boolean fuzzySearch = pageParam.isFuzzySearch();
		pageParam.setFuzzySearch(false);
		
		pageParam.setTotal(dao.countFinishedTasksByAssignee(pageParam));
		
		pageParam.setFuzzySearch(fuzzySearch);
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		} else {
			//pageParam.setFiltered(dao.countFinishedTasksByAssignee(pageParam));
		}
		
		List<PmWorkFlow> pmWorkFlows = dao.selectFinishedTasksByAssignee(pageParam);
		for (PmWorkFlow pmWorkFlow : pmWorkFlows) {
			Result canWithdraw = processService.canWithdraw(pmWorkFlow.getProcInstId(), pmWorkFlow.getAssignee());
			pmWorkFlow.setCanWithdraw(canWithdraw.isSuccess());
		}
		return pmWorkFlows;
	}
	
	@Override
	public List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, Integer assignee) {
		PmWorkFlowVO workFlow = new PmWorkFlowVO();
		workFlow.setAssignee(String.valueOf(assignee));
		return selectFinishedTasksByAssignee(pageParam, workFlow);
//		pageParam.setModel(workFlow);
//		
//		boolean fuzzySearch = pageParam.isFuzzySearch();
//		pageParam.setFuzzySearch(false);
//		
//		pageParam.setTotal(dao.countFinishedTasksByAssignee(pageParam));
//		
//		pageParam.setFuzzySearch(fuzzySearch);
//		if (pageParam.getPageSize() == -1L) {
//			pageParam.setPageSize(pageParam.getTotal());
//		} else {
//			//pageParam.setFiltered(dao.countFinishedTasksByAssignee(pageParam));
//		}
//		
//		List<PmWorkFlow> pmWorkFlows = dao.selectFinishedTasksByAssignee(pageParam);
//		for (PmWorkFlow pmWorkFlow : pmWorkFlows) {
//			Result canWithdraw = processService.canWithdraw(pmWorkFlow.getProcInstId(), pmWorkFlow.getAssignee());
//			pmWorkFlow.setCanWithdraw(canWithdraw.isSuccess());
//		}
//		return pmWorkFlows;
	}
}
