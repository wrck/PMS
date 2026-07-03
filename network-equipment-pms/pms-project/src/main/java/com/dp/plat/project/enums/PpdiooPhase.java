package com.dp.plat.project.enums;

/**
 * PPDIOO lifecycle phases used to group project milestones.
 *
 * <p>The PPDIOO (Prepare, Plan, Design, Implement, Operate, Optimize) methodology
 * is Cisco's network lifecycle framework. This enum captures the five phases that
 * are relevant for milestone grouping in the project delivery domain.</p>
 */
public enum PpdiooPhase {

    /** Prepare phase. */
    PREPARE("准备"),
    /** Plan phase. */
    PLAN("规划"),
    /** Design phase. */
    DESIGN("设计"),
    /** Implement phase. */
    IMPLEMENT("实施"),
    /** Operate phase. */
    OPERATE("运维");

    /** Chinese display name for the phase. */
    private final String displayName;

    PpdiooPhase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
