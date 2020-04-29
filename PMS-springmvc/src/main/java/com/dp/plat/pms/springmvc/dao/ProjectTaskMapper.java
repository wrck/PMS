package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import java.util.List;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;

public interface ProjectTaskMapper extends AbstractBaseMapper<ProjectTask> {

    int updateByPrimaryKeyWithBLOBs(ProjectTask record);

    List<ProjectDeliver> selectProjectDeliverBySelective(ProjectDeliver projectDeliver);
}
