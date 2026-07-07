package com.dp.plat.lowcode.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * 低代码配置版本快照。
 *
 * <p>每次发布操作生成不可变的全量 JSON 快照，支持版本历史查看、Diff 对比与回滚。
 * 按 environment（DEV/TEST/PROD）区分环境，支持环境间配置包晋升。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_lowcode_config_version")
public class LowCodeConfigVersion extends BaseEntity {

    /** 配置类型: FORM/LIST/TAB/RELATED_PAGE/ENTITY/MICROFLOW/RULE/CONNECTOR */
    @NotBlank(message = "配置类型不能为空")
    @Size(max = 32, message = "配置类型长度不能超过 32 个字符")
    private String configType;

    @NotNull(message = "配置ID不能为空")
    private Long configId;

    @NotBlank(message = "配置编码不能为空")
    @Size(max = 64, message = "配置编码长度不能超过 64 个字符")
    private String configCode;

    @NotNull(message = "版本号不能为空")
    private Integer version;

    @NotBlank(message = "快照不能为空")
    private String snapshot;

    @Size(max = 512, message = "变更说明长度不能超过 512 个字符")
    private String changeLog;

    /** 状态: ACTIVE/ARCHIVED */
    @NotBlank(message = "状态不能为空")
    @Size(max = 16, message = "状态长度不能超过 16 个字符")
    @Builder.Default
    private String status = "ACTIVE";

    /** 环境: DEV/TEST/PROD */
    @NotBlank(message = "环境不能为空")
    @Size(max = 16, message = "环境长度不能超过 16 个字符")
    @Builder.Default
    private String environment = "DEV";
}
