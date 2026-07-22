package com.dp.plat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 交付件结构化文档内容块（单个元素）。
 *
 * <p>借鉴问卷功能的设计：交付件文档内容由若干内容块有序组成，每个内容块支持
 * 富文本/内嵌表/选项卡/标题/分隔线/代码块 6 种类型。blockType 取值由数据字典
 * {@code pms_deliverable_block_type} 维护，不硬编码枚举。</p>
 *
 * <p>序列化策略：作为 {@code List<DeliverableContentBlock>} 存入
 * {@code pms_deliverable.content_blocks} JSON 列，由
 * {@link com.dp.plat.common.handler.JsonTypeHandlers.DeliverableContentBlockListHandler}
 * 负责反序列化（解决泛型擦除）。</p>
 *
 * <p>各 blockType 的 blockConfig / blockContent 结构约定：</p>
 * <ul>
 *   <li><b>RICH_TEXT</b>（富文本）：
 *     <ul>
 *       <li>blockConfig：{@code {}} （预留，目前无配置项）</li>
 *       <li>blockContent：{@code String}（HTML 字符串）</li>
 *     </ul>
 *   </li>
 *   <li><b>TABLE</b>（内嵌表）：
 *     <ul>
 *       <li>blockConfig：{@code { "columns": ["列名1","列名2",...] } }（列名数组）</li>
 *       <li>blockContent：{@code List<List<String>>}（二维行数据，外层为行，内层为单元格值）</li>
 *     </ul>
 *   </li>
 *   <li><b>TABS</b>（选项卡）：
 *     <ul>
 *       <li>blockConfig：{@code {}}（预留，标签名从 blockContent 键读取）</li>
 *       <li>blockContent：{@code Map<String,String>}（key 为标签名，value 为富文本内容）</li>
 *     </ul>
 *   </li>
 *   <li><b>HEADING</b>（标题）：
 *     <ul>
 *       <li>blockConfig：{@code { "level": 1~4 } }（标题级别，1=H1，4=H4）</li>
 *       <li>blockContent：{@code String}（标题文本）</li>
 *     </ul>
 *   </li>
 *   <li><b>DIVIDER</b>（分隔线）：
 *     <ul>
 *       <li>blockConfig：{@code {}} （无配置）</li>
 *       <li>blockContent：{@code null}（始终为空）</li>
 *     </ul>
 *   </li>
 *   <li><b>CODE_BLOCK</b>（代码块）：
 *     <ul>
 *       <li>blockConfig：{@code { "language": "text|javascript|sql|java|ini|..." } }（代码语言）</li>
 *       <li>blockContent：{@code String}（代码文本）</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>关联设计文档：交付件结构化内容块（借鉴问卷功能动态配置）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverableContentBlock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 内容块类型（见字典 {@code pms_deliverable_block_type}）：
     * RICH_TEXT/TABLE/TABS/HEADING/DIVIDER/CODE_BLOCK。
     */
    private String blockType;

    /** 内容块唯一标识（在同一交付件内唯一，用于前端引用，如 title/body/code/attrs）。 */
    private String blockKey;

    /** 内容块标题（前端展示用，用户可编辑）。 */
    private String blockTitle;

    /**
     * 内容块配置（结构随 blockType 不同而不同，详见类级 javadoc）。
     * <p>预留扩展：未来可加 readOnly / required / placeholder 等元数据。</p>
     */
    private Map<String, Object> blockConfig;

    /**
     * 内容块内容（类型随 blockType 不同而不同，详见类级 javadoc）。
     * <p>反序列化为 Object 是为了兼容 String / List / Map / null 多种结构。</p>
     */
    private Object blockContent;

    /** 排序号（从 1 开始，升序排列，前端编辑器会重算）。 */
    private Integer sortOrder;
}
