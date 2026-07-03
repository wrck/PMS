package com.dp.plat.project.deliverable.enums;

/**
 * Standard final acceptance deliverable types.
 *
 * <p>Each project is initialised with one checklist record per deliverable type
 * via {@code initChecklist}. The {@link #displayName} is used in validation
 * messages listing missing deliverables.</p>
 */
public enum DeliverableType {

    /** As-built documentation. */
    AS_BUILT("竣工资料"),
    /** Test report. */
    TEST_REPORT("测试报告"),
    /** Acceptance certificate. */
    ACCEPTANCE_CERT("验收证书"),
    /** Training record. */
    TRAINING_RECORD("培训记录"),
    /** Operation manual. */
    OPERATION_MANUAL("操作手册"),
    /** Asset register. */
    ASSET_REGISTER("资产清单"),
    /** Warranty certificate. */
    WARRANTY_CERT("质保证书"),
    /** Spare parts list. */
    SPARE_PARTS_LIST("备件清单");

    /** Chinese display name used in validation messages. */
    private final String displayName;

    DeliverableType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
