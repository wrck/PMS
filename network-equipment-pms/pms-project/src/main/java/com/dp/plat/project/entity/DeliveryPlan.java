package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Delivery plan entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_delivery_plan")
public class DeliveryPlan extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Plan name. */
    private String planName;

    /** Plan content. */
    private String planContent;

    /** Plan date. */
    private LocalDate planDate;
}
