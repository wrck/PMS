package com.dp.plat.baseline.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 任务依赖实体。
 *
 * <p>dependency_type：FS（完成-开始）/ FF（完成-完成）/ SS（开始-开始）/ SF（开始-完成）。
 * lag_days 滞后天数（可负，表示提前）。保存时执行 DFS 循环依赖检测。</p>
 *
 * <p>关联设计文档：§2.2 TaskDependency（行 161-168）、§3.6 依赖与基线规则。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_task_dependency")
public class TaskDependency extends BaseEntity {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "前置任务ID不能为空")
    private Long predecessorTaskId;

    @NotNull(message = "后续任务ID不能为空")
    private Long successorTaskId;

    /** FS / FF / SS / SF。 */
    @NotBlank(message = "依赖类型不能为空")
    @Size(max = 4, message = "依赖类型长度不能超过 4 个字符")
    @Builder.Default
    private String dependencyType = "FS";

    /** 滞后天数（可负）。 */
    @Builder.Default
    private Integer lagDays = 0;

    /** 乐观锁版本号（MyBatis-Plus @Version）。 */
    @Version
    private Integer version;
}
