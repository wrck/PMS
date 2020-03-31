package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;

public interface DispatchProjectMapper extends AbstractBaseMapper<DispatchProject> {

    void insertOrUpdateSelective(DispatchProject dispatch);
}
