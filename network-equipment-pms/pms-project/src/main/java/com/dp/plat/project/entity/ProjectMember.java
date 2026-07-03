package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Project member entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_member")
public class ProjectMember extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** User id. */
    private Long userId;

    /** Role type (PM, ENGINEER, QA, OBSERVER). */
    private String roleType;
}
