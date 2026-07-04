package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务日志实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_schedule_log")
public class ScheduleLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务名称 */
    private String taskName;

    /** 任务分组 */
    private String taskGroup;

    /** Cron 表达式 */
    private String cronExpression;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 耗时（毫秒） */
    private Long costMs;

    /** 执行状态：SUCCESS/FAIL */
    private String status;

    /** 错误信息（TEXT） */
    private String errorMessage;

    /** 触发类型：AUTO/MANUAL */
    private String triggerType;
}
