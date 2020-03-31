package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.ProjectTask;

public interface ProjectTaskMapper extends AbstractBaseMapper<ProjectTask> {
    int updateByPrimaryKeyWithBLOBs(ProjectTask record);
}