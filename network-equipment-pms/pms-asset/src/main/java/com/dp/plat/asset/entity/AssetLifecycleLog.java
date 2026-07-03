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
 * Equipment lifecycle log entity.
 * actionType: INBOUND, ALLOCATE, TRANSFER, RETURN, SCRAP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_asset_lifecycle_log")
public class AssetLifecycleLog extends BaseEntity {

    private Long assetId;

    /** INBOUND, ALLOCATE, TRANSFER, RETURN, SCRAP. */
    private String actionType;

    private Long fromProjectId;

    private Long toProjectId;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime actionTime;

    private String remarks;
}
