package com.dp.plat.asset.enums;

import lombok.Getter;

/**
 * Equipment asset lifecycle status (9-state machine).
 *
 * <p>States are ordered to reflect the canonical forward lifecycle:
 * ORDERED → IN_TRANSIT → RECEIVED → STAGED → INSTALLED → COMMISSIONED → IN_PRODUCTION,
 * with RMA and DECOMMISSIONED as side / terminal branches.</p>
 */
@Getter
public enum AssetStatus {

    ORDERED("已下单", 1),
    IN_TRANSIT("运输中", 2),
    RECEIVED("已收货", 3),
    STAGED("已暂存", 4),
    INSTALLED("已安装", 5),
    COMMISSIONED("已调试", 6),
    IN_PRODUCTION("已投产", 7),
    RMA("返修中", 8),
    DECOMMISSIONED("已退役", 9);

    private final String description;
    private final int sortOrder;

    AssetStatus(String description, int sortOrder) {
        this.description = description;
        this.sortOrder = sortOrder;
    }
}
