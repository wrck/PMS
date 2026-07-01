package com.dp.plat.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 审批意见 - 对应老系统 DpComment (11字段) */
@Data
public class DpComment {
    private Long id;
    private String instId;
    private String taskId;
    private String assignee;
    private String assigneeName;
    private String message;
    private Integer result;
    private LocalDateTime assigneeTime;
    private String procdefKey;
}
