package com.dp.plat.lowcode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 低代码组件元数据实体。
 *
 * <p>记录预置/自定义组件的注册信息与属性 JSON Schema。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_lowcode_component_meta")
public class LowCodeComponentMeta {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 组件名（注册 key） */
    private String name;

    /** 显示名 */
    private String displayName;

    /** 分类: SELECTOR / INPUT / DISPLAY / ... */
    private String category;

    /** 图标 */
    private String icon;

    /** 属性 JSON Schema */
    private String propsSchema;

    /** 描述 */
    private String description;

    /** 是否内置组件 */
    private Integer builtin;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
