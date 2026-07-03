package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agent quality evaluation entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_agent_score")
public class AgentScore extends BaseEntity {

    private Long agentId;

    private Long taskId;

    /** Response speed score 0-10. */
    private Integer responseSpeedScore;

    /** Construction quality score 0-10. */
    private Integer constructionQualityScore;

    /** Document completeness score 0-10. */
    private Integer documentCompletenessScore;

    /** Overall score of this evaluation. */
    private BigDecimal overallScore;

    private String comment;

    private Long evaluatorId;

    private String evaluatorName;

    private LocalDateTime evaluateTime;
}
