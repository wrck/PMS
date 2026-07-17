package com.dp.plat.project.service;

import com.dp.plat.project.entity.ProjectMember;

import java.util.List;

public interface IProjectMemberService {

    List<ProjectMember> listByProjectId(Long projectId);

    ProjectMember create(ProjectMember member);

    ProjectMember update(ProjectMember member);

    void delete(Long id);

    void deleteByProjectId(Long projectId);

    List<ProjectMember> batchCreate(List<ProjectMember> members);
}
