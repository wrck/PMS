package com.dp.plat.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Equipment allocation to a project.
 * status: ACTIVE, RETURNED.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_asset_allocation")
public class AssetAllocation extends BaseEntity {

    private Long assetId;

    private Long projectId;

    private Long modelId;

    private Integer quantity;

    private LocalDateTime allocateTime;

    private Long allocateUserId;

    private String allocateUserName;

    /** ACTIVE, RETURNED. */
    private String status;

    private LocalDateTime returnTime;
}
