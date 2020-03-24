package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;

public interface ProjectHeaderMapper extends AbstractBaseMapper<ProjectHeader> {

    int updateByPrimaryKeyWithBLOBs(ProjectHeader record);
}
