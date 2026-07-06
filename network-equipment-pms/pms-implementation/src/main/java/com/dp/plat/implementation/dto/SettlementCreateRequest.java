package com.dp.plat.implementation.dto;

import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "结算信息不能为空")
    @Valid
    private Settlement settlement;

    @NotEmpty(message = "结算明细不能为空")
    @Valid
    private List<SettlementDetail> details;
}
