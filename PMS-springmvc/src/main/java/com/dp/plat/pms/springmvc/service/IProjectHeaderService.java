package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.ProjectHeader;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.Project;

/**
 *
 * Created by CodeGenerator
 */
public interface IProjectHeaderService extends IAbstractBaseService<ProjectHeader> {

	long countUncreateProjectList(PageParam<Object> tempParam);

	List<Object> selectUncreateProjectList(PageParam<Object> pageParam);

	Project queryProjectByContractNoAndType(String contractNo, String projectType);
}
