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

    /** Deliverable type (DOCUMENT, CONFIG, REPORT, OTHER). */
    private String deliverableType;

    /** File path. */
    private String filePath;

    /** Status (PENDING, SUBMITTED, CONFIRMED). */
    private String status;
}
