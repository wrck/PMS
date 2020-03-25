package com.dp.plat.pms.springmvc.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;

public interface ProjectHeaderMapper extends AbstractBaseMapper<ProjectHeader> {

    int updateByPrimaryKeyWithBLOBs(ProjectHeader record);

	long countUncreateProjectList(PageParam<Object> pageParam);

	List<Object> selectUncreateProjectList(PageParam<Object> pageParam);

	Project queryProjectByContractNoAndType(Map<String, Object> params);
}
