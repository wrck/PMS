package com.dp.plat.project.spi;

import com.dp.plat.common.spi.ProjectPhaseLookup;
import com.dp.plat.project.dao.ProjectPhaseMapper;
import com.dp.plat.project.entity.ProjectPhase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectPhaseLookupImpl implements ProjectPhaseLookup {
    private final ProjectPhaseMapper phaseMapper;

    @Override
    public Long findProjectId(Long phaseId) {
        ProjectPhase phase = phaseMapper.selectById(phaseId);
        return phase == null ? null : phase.getProjectId();
    }
}
