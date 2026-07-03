package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Final acceptance record entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_final_acceptance")
public class FinalAcceptance extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Apply time. */
    private LocalDateTime applyTime;

    /** Apply user id. */
    private Long applyUserId;

    /** Apply user name. */
    private String applyUserName;

    /** Status (PENDING, APPROVED, REJECTED). */
    private String status;

    /** Acceptance report. */
    private String acceptanceReport;

    /** Acceptance opinion. */
    private String acceptanceOpinion;

    /** Accept user id. */
    private Long acceptUserId;

    /** Accept user name. */
    private String acceptUserName;

    /** Accept time. */
    private LocalDateTime acceptTime;
}
