package com.dp.plat.governance.risk.dto;

import com.dp.plat.governance.risk.entity.Risk;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * DTO representing a 5x5 risk matrix for a project.
 *
 * <p>The matrix is indexed as {@code matrix[likelihood-1][impact-1]} where each
 * cell holds the count of risks at that likelihood/impact combination.</p>
 */
@Data
@Builder
@Schema(description = "风险矩阵")
public class RiskMatrixDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "5x5风险矩阵，matrix[likelihood-1][impact-1]为该象限风险数量")
    private List<List<Integer>> matrix;

    @Schema(description = "项目下全部风险列表")
    private List<Risk> risks;

    @Schema(description = "风险总数")
    private int totalRisks;

    @Schema(description = "高优先级风险数量")
    private int highPriorityCount;
}
