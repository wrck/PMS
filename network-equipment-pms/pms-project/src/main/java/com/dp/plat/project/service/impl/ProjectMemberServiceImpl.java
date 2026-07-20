package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.mapper.ProjectMemberMapper;
import com.dp.plat.project.entity.ProjectMember;
import com.dp.plat.project.service.IProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements IProjectMemberService {

    private final ProjectMemberMapper memberMapper;

    @Override
    public List<ProjectMember> listByProjectId(Long projectId) {
        return memberMapper.selectList(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, projectId));
    }

    @Override
    @Transactional
    public ProjectMember create(ProjectMember member) {
        if (member.getRole() == null) {
            member.setRole("PROJECT_MEMBER");
        }
        memberMapper.insert(member);
        return member;
    }

    @Override
    @Transactional
    public ProjectMember update(ProjectMember member) {
        memberMapper.updateById(member);
        return member;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        memberMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByProjectId(Long projectId) {
        memberMapper.delete(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, projectId));
    }

    @Override
    @Transactional
    public List<ProjectMember> batchCreate(List<ProjectMember> members) {
        for (ProjectMember m : members) {
            if (m.getRole() == null) {
                m.setRole("PROJECT_MEMBER");
            }
            memberMapper.insert(m);
        }
        return members;
    }
}
