package com.dp.plat.deliverable.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.dto.DeliverableContentBlock;
import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.handler.JsonTypeHandlers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 交付件类型默认内容块模板。
 *
 * <p>每种交付件性质类型（见字典 {@code pms_deliverable_type}：DOCUMENT/CODE/ENTITY_REF/
 * MODEL/CONFIG/DATA/OTHER）预置一份默认内容块模板。前端新建交付件切换类型时，
 * 调用 {@code GET /api/deliverable/type-templates/{deliverableType}} 加载模板填充
 * {@code Deliverable.contentBlocks}，用户可在此基础上二次编辑。</p>
 *
 * <p>TypeHandler：{@link JsonTypeHandlers.DeliverableContentBlockListHandler}
 * （与 {@link Deliverable#getContentBlocks()} 共用，确保序列化/反序列化结构一致）。
 * {@code autoResultMap = true} 已开启，确保字段级 typeHandler 生效。</p>
 *
 * <p>V88 迁移预置 7 种类型的默认模板，{@code deliverable_type} 与 {@code deleted}
 * 联合唯一（保证每种类型仅一份未删除模板）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_deliverable_type_template", autoResultMap = true)
public class DeliverableTypeTemplate extends BaseEntity {

    /**
     * 交付件性质类型（见字典 {@code pms_deliverable_type}）：
     * DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER。
     */
    private String deliverableType;

    /**
     * 默认内容块（JSON 数组，元素见 {@link DeliverableContentBlock}）。
     *
     * <p>TypeHandler 指定 {@link JsonTypeHandlers.DeliverableContentBlockListHandler}
     * 以解决泛型擦除。</p>
     */
    @TableField(typeHandler = JsonTypeHandlers.DeliverableContentBlockListHandler.class)
    private List<DeliverableContentBlock> defaultBlocks;

    /** 模板说明。 */
    private String description;
}
