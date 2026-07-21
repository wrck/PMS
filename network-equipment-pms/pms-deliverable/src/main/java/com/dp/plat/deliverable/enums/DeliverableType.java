package com.dp.plat.deliverable.enums;

/**
 * 交付件类型枚举（统一 8 类标准终验类型 + OTHER 兜底）。
 *
 * <p>与 pms-project 模块的 {@code com.dp.plat.project.deliverable.enums.DeliverableType} 对齐，
 * 新增 {@link #OTHER} 兜底类型用于非标准交付件。</p>
 */
public enum DeliverableType {

    /** 竣工资料。 */
    AS_BUILT("竣工资料"),
    /** 测试报告。 */
    TEST_REPORT("测试报告"),
    /** 验收证书。 */
    ACCEPTANCE_CERT("验收证书"),
    /** 培训记录。 */
    TRAINING_RECORD("培训记录"),
    /** 操作手册。 */
    OPERATION_MANUAL("操作手册"),
    /** 资产清单。 */
    ASSET_REGISTER("资产清单"),
    /** 质保证书。 */
    WARRANTY_CERT("质保证书"),
    /** 备件清单。 */
    SPARE_PARTS_LIST("备件清单"),
    /** 其他类型（兜底）。 */
    OTHER("其他");

    /** 中文显示名称。 */
    private final String displayName;

    DeliverableType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
