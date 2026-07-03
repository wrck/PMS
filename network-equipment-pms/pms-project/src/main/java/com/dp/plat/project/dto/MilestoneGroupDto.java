package com.dp.plat.project.dto;

import com.dp.plat.project.entity.Milestone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * DTO grouping milestones under a single PPDIOO lifecycle phase,
 * used by the milestone dashboard endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneGroupDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** PPDIOO phase name (PREPARE, PLAN, DESIGN, IMPLEMENT, OPERATE). */
    private String ppdiooPhase;

    /** Display name of the PPDIOO phase. */
    private String ppdiooPhaseName;

    /** Milestones belonging to this phase, ordered by sort order. */
    private List<Milestone> milestones;
}
