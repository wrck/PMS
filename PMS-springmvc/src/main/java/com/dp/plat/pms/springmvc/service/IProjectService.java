package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.Project;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.core.service.IAbstractBaseService;

/**
 *
 * Created by CodeGenerator
 */
public interface IProjectService extends IAbstractBaseService<Project> {

	ProjectVO queryProjectByContractNoAndType(String contractNo, String projectType);

}
