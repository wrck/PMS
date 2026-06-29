package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;

public interface PmWorkBenchMapper {

	long countRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam);

	List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam);

	long countFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam);

	List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam);

}
