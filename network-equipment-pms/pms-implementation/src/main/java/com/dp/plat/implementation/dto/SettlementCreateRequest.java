package com.dp.plat.implementation.dto;

import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Request payload for creating a settlement with its line items.
 */
@Data
public class SettlementCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Settlement settlement;

    private List<SettlementDetail> details;
}
