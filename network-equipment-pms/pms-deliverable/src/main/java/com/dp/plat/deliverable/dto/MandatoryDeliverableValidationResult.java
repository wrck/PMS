package com.dp.plat.deliverable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 阶段必需交付件校验结果（Story 5 验收 2）。
 *
 * <p>关联设计文档：§3.4（行 427）、§5.6（行 1059-1078）。
 * 供 {@code advancePhase} 在阶段退出时调用：必需交付件必须达到「已批准」状态
 * （PUBLISHED/REFERENCED/ARCHIVED），否则 {@code allApproved=false} 且 {@code items}
 * 列出未满足的交付件。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MandatoryDeliverableValidationResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 全部必需交付件是否均已批准（items 为空时为 true）。 */
    private Boolean allApproved;

    /** 未满足的必需交付件列表（已批准的不在此列表中）。 */
    private List<Item> items;

    /**
     * 单个未满足的必需交付件。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** 交付件ID。 */
        private Long deliverableId;

        /** 交付件名称。 */
        private String deliverableName;

        /** 是否必需（恒为 true，因本列表仅含必需项）。 */
        private Boolean mandatory;

        /** 期望状态（必需交付件阶段退出要求达到 PUBLISHED）。 */
        private String expectedStatus;

        /** 实际状态。 */
        private String actualStatus;

        /** 是否已批准（PUBLISHED/REFERENCED/ARCHIVED 之一）。 */
        private Boolean approved;
    }
}
