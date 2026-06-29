package com.dp.plat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.ProjectDTO;
import com.dp.plat.model.dto.ProjectMemberDTO;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.model.entity.PmsProjectMember;
import com.dp.plat.model.vo.ProjectVO;

import java.util.List;

public interface PmsProjectService extends BaseService<PmsProject> {

    Page<ProjectVO> listProjects(int pageNum, int pageSize, String projectCode, String projectName,
                                  String projectType, String projectState, String pmCode);

    ProjectVO getProjectById(Long id);

    void createProject(ProjectDTO projectDTO);

    void updateProject(ProjectDTO projectDTO);

    void deleteProject(Long id);

    List<PmsProjectMember> getProjectMembers(Long projectId);

    void addProjectMember(ProjectMemberDTO memberDTO);

    void removeProjectMember(Long memberId);
}
