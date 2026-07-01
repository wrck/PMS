package com.dp.plat.model.entity;

import lombok.Data;

/** 工作流流程类型 - 对应老系统 DpActProcType (3字段) */
@Data
public class DpActProcType {
    private String key;
    private String name;
    private String description;
}
