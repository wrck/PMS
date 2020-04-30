package com.dp.plat.pms.springmvc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

public interface ProjectHeaderMapper extends AbstractBaseMapper<ProjectHeader> {

    int updateByPrimaryKeyWithBLOBs(ProjectHeader record);

	long countUncreateProjectList(PageParam<Object> pageParam);

	List<Object> selectUncreateProjectList(PageParam<Object> pageParam);

	Project queryProjectByContractNoAndType(Map<String, Object> params);

	Map<String, Boolean> checkPermission(@Param("model") ProjectVO project, @Param("user") Principal currentPrincipal);
}
