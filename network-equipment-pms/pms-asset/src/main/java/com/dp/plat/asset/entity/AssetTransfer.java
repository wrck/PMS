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
 * Equipment transfer between projects.
 * status: PENDING, APPROVED, REJECTED.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_asset_transfer")
public class AssetTransfer extends BaseEntity {

    private Long assetId;

    private Long fromProjectId;

    private Long toProjectId;

    private String transferReason;

    /** PENDING, APPROVED, REJECTED. */
    private String status;

    private Long applyUserId;

    private String applyUserName;

    private LocalDateTime applyTime;

    private Long approveUserId;

    private String approveUserName;

    private LocalDateTime approveTime;

    private String approveOpinion;
}
