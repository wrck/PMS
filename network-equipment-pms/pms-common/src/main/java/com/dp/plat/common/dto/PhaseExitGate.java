package com.dp.plat.common.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 阶段退出条件（结构化 JSON，4 类条件）
 * 关联设计文档：§3.2
 */
@Data
public class PhaseExitGate implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 必需交付件 */
    private List<RequiredDeliverable> requiredDeliverables;

    /** 必需任务 */
    private List<RequiredTask> requiredTasks;

    /** 必需里程碑 */
    private List<RequiredMilestone> requiredMilestones;

    /** 必需审批 */
    private List<RequiredApproval> requiredApprovals;

    @Data
    public static class RequiredDeliverable implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long deliverableId;
        private String deliverableName;
        private String requiredStatus; // PUBLISHED/REFERENCED/ARCHIVED
    }

    @Data
    public static class RequiredTask implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long phaseId;
        private Boolean allCompleted;
    }

    @Data
    public static class RequiredMilestone implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long milestoneId;
        private Boolean mustReached;
    }

    @Data
    public static class RequiredApproval implements Serializable {
        private static final long serialVersionUID = 1L;
        private String approvalType; // PHASE_EXIT 等
        private Boolean mustApproved;
    }
}
