package com.dp.plat.implementation.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Excel export DTO for {@link com.dp.plat.implementation.entity.Settlement}.
 *
 * <p>Carries the columns surfaced to finance reviewers when auditing agent
 * settlement records: settlement number, project binding, total amount, push
 * (payment) status, approval status and creation timestamp.</p>
 */
@Data
public class SettlementExportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "结算单号", index = 0)
    private String settlementNo;

    @ExcelProperty(value = "项目ID", index = 1)
    private Long projectId;

    @ExcelProperty(value = "总金额", index = 2)
    private BigDecimal totalAmount;

    @ExcelProperty(value = "付款状态", index = 3)
    private String pushStatus;

    @ExcelProperty(value = "审批状态", index = 4)
    private String status;

    @ExcelProperty(value = "创建时间", index = 5)
    private LocalDateTime createTime;
}
