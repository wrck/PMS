package com.dp.plat.deliverable.enums;

/**
 * 交付件类型枚举（旧版终验用途分类）。
 *
 * @deprecated 交付件类型已改为数据字典驱动（字典 pms_deliverable_type），
 * 性质分类为 DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER。
 * 终验校验改为只看 mandatory 标记，不再依赖具体类型。
 * 本枚举保留用于历史代码兼容，将在下版本删除。
 */
@Deprecated
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
