package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Project deliverable entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_deliverable")
public class Deliverable extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Deliverable name. */
    private String deliverableName;

    /** 交付件类型（AS_BUILT/TEST_REPORT/ACCEPTANCE_CERT/TRAINING_RECORD/OPERATION_MANUAL/ASSET_REGISTER/WARRANTY_CERT/SPARE_PARTS_LIST/OTHER）。 */
    private String deliverableType;

    /** File path. */
    private String filePath;

    /** 状态（7 态）：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED。 */
    private String status;

    /** 所属阶段ID（终验交付件可为 null）。 */
    private Long phaseId;

    /** 当前版本号，从 1 开始。 */
    private Integer currentVersion;

    /** 是否必需交付件（影响阶段退出校验和终验校验）。 */
    private Boolean mandatory;

    /** 签核角色。 */
    private String approverRole;
}
