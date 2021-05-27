package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.UserInfoVO;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.entity.ProjectMember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.dao.ProjectMemberMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectMemberService")
public class ProjectMemberService extends AbstractBaseService<ProjectMemberMapper, ProjectMember> implements IProjectMemberService {

	@Autowired
	private ProjectDao projectDao;
	
	@Override
	public int insert(ProjectMember record) {
		Project project = new Project();
		project.setProjectId(record.getProjectId());
		project.setMemberRole(record.getMemberRole());
		project.setMemberCode(record.getMemberCode());
		project.setMemberName(record.getMemberName());
		Integer count = projectDao.queryProjectMemberCountByProject(project);
		// 如果能查到，说明未更改人员，不做操作，否则插入member表
		if (count == 0) {
			return super.insert(record);
		} else {
			return 0;
		}
	}

	@Override
	public int insertSelective(ProjectMember record) {
		Project project = new Project();
		project.setProjectId(record.getProjectId());
		project.setMemberRole(record.getMemberRole());
		project.setMemberCode(record.getMemberCode());
		project.setMemberName(record.getMemberName());
		Integer count = projectDao.queryProjectMemberCountByProject(project);
		// 如果能查到，说明未更改人员，不做操作，否则插入member表
		if (count == 0) {
			return super.insertSelective(record);
		} else {
			return 0;
		}
	}
}
