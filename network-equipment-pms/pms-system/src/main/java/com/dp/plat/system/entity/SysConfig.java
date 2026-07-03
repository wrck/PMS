package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * System parameter configuration entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    private String configName;

    private String configKey;

    private String configValue;

    /** 0=system built-in, 1=user-defined. */
    private String configType;

    private String remark;
}
