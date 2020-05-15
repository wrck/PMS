package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import java.util.List;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.realms.Principal;

public interface ProjectHeaderMapper extends AbstractBaseMapper<ProjectHeader> {

    long countUncreateProjectList(PageParam<Object> pageParam);

    List<Object> selectUncreateProjectList(PageParam<Object> pageParam);

    Project queryProjectByContractNoAndType(Map<String, Object> params);

    Map<String, Object> checkPermission(@Param("model") ProjectVO project, @Param("user") Principal currentPrincipal);

    Map<String, Object> checkPermission(@Param("model") ProjectVO project, @Param("permissionTypes") String permissionTypes, @Param("user") Principal currentPrincipal);

	ProjectVO selectVOByProjectId(Integer projectId);

	ProjectVO queryProjectStateByProjectId(Integer projectId);
}
