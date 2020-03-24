package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.dao.ProjectHeaderMapper;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import org.springframework.stereotype.Service;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectHeaderService")
public class ProjectHeaderService extends AbstractBaseService<ProjectHeaderMapper, ProjectHeader> implements IProjectHeaderService {
}
