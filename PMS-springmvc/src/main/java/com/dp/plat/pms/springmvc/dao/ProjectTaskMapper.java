package com.dp.plat.pms.springmvc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;
import com.dp.plat.pms.springmvc.vo.TaskVO;

public interface ProjectTaskMapper extends AbstractBaseMapper<ProjectTask> {

    int updateByPrimaryKeyWithBLOBs(ProjectTask record);

    List<ProjectDeliver> selectProjectDeliverBySelective(ProjectDeliver projectDeliver);
    
    Map<String, Object> checkPermission(@Param("model") TaskVO task, @Param("user") Principal currentPrincipal);

	Map<String, Object> checkPermission(@Param("model") TaskVO task, @Param("permissionTypes") String permissionTypes, @Param("user") Principal currentPrincipal);

	void updateEventActualFinishDateByTask(ProjectTask pt);
}
