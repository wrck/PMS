package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.dao.ProjectPhaseMapper;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.service.IProjectPhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectPhaseServiceImpl implements IProjectPhaseService {

    private final ProjectPhaseMapper phaseMapper;

    @Override
    public List<ProjectPhase> listByProjectId(Long projectId) {
        return phaseMapper.selectList(new LambdaQueryWrapper<ProjectPhase>()
            .eq(ProjectPhase::getProjectId, projectId)
            .orderByAsc(ProjectPhase::getSortOrder));
    }

    @Override
    public ProjectPhase getById(Long id) {
        return phaseMapper.selectById(id);
    }

    @Override
    @Transactional
    public ProjectPhase create(ProjectPhase phase) {
        if (phase.getStatus() == null) {
            phase.setStatus("NOT_STARTED");
        }
        phaseMapper.insert(phase);
        return phase;
    }

    @Override
    @Transactional
    public ProjectPhase update(ProjectPhase phase) {
        phaseMapper.updateById(phase);
        return phase;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        phaseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public List<ProjectPhase> batchCreate(List<ProjectPhase> phases) {
        for (ProjectPhase phase : phases) {
            if (phase.getStatus() == null) {
                phase.setStatus("NOT_STARTED");
            }
            phaseMapper.insert(phase);
        }
        return phases;
    }
}
