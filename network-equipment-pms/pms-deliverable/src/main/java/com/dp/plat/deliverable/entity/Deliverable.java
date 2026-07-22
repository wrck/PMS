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

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交付件实体（全生命周期 7 态状态机）。
 *
 * <p>关联设计文档：§2.2（行 185-196）、§3.4 交付件状态机 7 态（行 393-428）。
 *
 * <p>状态流转：DRAFT → SUBMITTED → REVIEWED → SIGNED → PUBLISHED → REFERENCED → ARCHIVED
 * （其中 SUBMITTED/REVIEWED 可退回 DRAFT；PUBLISHED 可经修订新建版本回到 DRAFT）。
 *
 * <p>注：本实体映射 {@code pms_deliverable} 表（V2 建表、V70 扩展）。
 * pms-project 模块下的旧 {@code com.dp.plat.project.entity.Deliverable} 保留用于历史代码兼容，
 * 本模块为全生命周期的单一权威实体（含 7 态字段 + 版本/签核时间戳）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_deliverable", autoResultMap = true)
public class Deliverable extends BaseEntity {

    /** 项目ID。 */
    private Long projectId;

    /** 交付件名称。 */
    private String deliverableName;

    /** 交付件性质类型（见字典 pms_deliverable_type）：DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER。 */
    private String deliverableType;

    /** 文件路径（最新版本）。 */
    private String filePath;

    /**
     * 结构化内容块（JSON 数组，元素见 {@link DeliverableContentBlock}）。
     *
     * <p>借鉴问卷功能：交付件文档内容由若干内容块有序组成，支持富文本/内嵌表/选项卡/
     * 标题/分隔线/代码块 6 种类型（blockType 取值由字典 {@code pms_deliverable_block_type} 维护）。
     * 新建交付件时由前端按 deliverableType 加载对应默认模板
     * （{@link DeliverableTypeTemplate}）填充，用户可二次编辑。</p>
     *
     * <p>TypeHandler：{@link JsonTypeHandlers.DeliverableContentBlockListHandler}
     * （重写 parse 以 {@code TypeReference<List<DeliverableContentBlock>>} 反序列化，
     * 解决泛型擦除）。{@code autoResultMap = true} 已在类级 {@code @TableName} 开启，
     * 确保 BaseMapper 查询时字段级 typeHandler 生效。</p>
     */
    @TableField(typeHandler = JsonTypeHandlers.DeliverableContentBlockListHandler.class)
    private List<DeliverableContentBlock> contentBlocks;

    /**
     * 状态（7 态）：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED。
     *
     * <p>对应 {@link com.dp.plat.deliverable.enums.DeliverableStatus}。</p>
     */
    private String status;

    /** 所属阶段ID。 */
    private Long phaseId;

    /** 当前版本号，从 1 开始。修订时 +1 并新建 {@link DeliverableVersion} 记录。 */
    private Integer currentVersion;

    /** 是否必需交付件（影响阶段退出校验）。 */
    private Boolean mandatory;

    /** 是否模板预设（模板实例化创建 = true，过程新增 = false）。 */
    private Boolean templateInherited;

    /** 签核角色。 */
    private String approverRole;

    /** 引用实体类型（见字典 pms_deliverable_ref_entity_type：TASK/ASSET/PHASE/PROJECT/DELIVERABLE/REPORT）。 */
    private String refEntityType;

    /** 引用实体ID（当 deliverableType=ENTITY_REF 时指向具体业务对象）。 */
    private Long refEntityId;

    /** 发布时间（SIGNED → PUBLISHED 时写入）。 */
    private LocalDateTime publishedAt;

    /** 归档时间（REFERENCED → ARCHIVED 时写入）。 */
    private LocalDateTime archivedAt;
}
