package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Implementation progress log entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_impl_progress")
public class ImplProgress extends BaseEntity {

    private Long taskId;

    /** Progress percent 0-100. */
    private Integer progressPercent;

    private String workLog;

    /** Photo urls (comma-separated). */
    private String photoUrls;

    private Long reportUserId;

    private String reportUserName;

    private LocalDateTime reportTime;
}
