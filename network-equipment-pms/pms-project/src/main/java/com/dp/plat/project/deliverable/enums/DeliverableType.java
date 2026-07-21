package com.dp.plat.project.deliverable.enums;

/**
 * Standard final acceptance deliverable types (legacy).
 *
 * @deprecated 交付件类型已改为数据字典驱动（字典 pms_deliverable_type），
 * 终验校验改为只看 mandatory 标记。本枚举保留用于历史代码兼容，将在下版本删除。
 */
@Deprecated
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
