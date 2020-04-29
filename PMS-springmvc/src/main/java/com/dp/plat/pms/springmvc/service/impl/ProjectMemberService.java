package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.entity.ProjectMember;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.dao.ProjectMemberMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectMemberService")
public class ProjectMemberService extends AbstractBaseService<ProjectMemberMapper, ProjectMember> implements IProjectMemberService {
}
