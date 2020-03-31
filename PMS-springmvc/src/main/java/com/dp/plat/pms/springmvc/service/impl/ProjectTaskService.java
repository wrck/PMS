package com.dp.plat.pms.springmvc.service.impl;

import org.springframework.stereotype.Service;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;

import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.dao.ProjectTaskMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectTaskService")
public class ProjectTaskService extends AbstractBaseService<ProjectTaskMapper, ProjectTask> implements IProjectTaskService {
}