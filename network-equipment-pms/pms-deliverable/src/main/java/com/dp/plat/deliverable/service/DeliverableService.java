package com.dp.plat.deliverable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.entity.DeliverableVersion;

import java.util.List;

/**
 * 交付件全生命周期服务（7 态状态机）。
 *
 * <p>关联设计文档：§3.4 交付件状态机 7 态（行 393-428）、§4.3（行 683-691）。
 *
 * <p>状态流转：DRAFT → SUBMITTED → REVIEWED → SIGNED → PUBLISHED → REFERENCED → ARCHIVED
 * （SUBMITTED/REVIEWED 可退回 DRAFT；PUBLISHED 经修订新建版本回到 DRAFT —— 由 {@code revise} 处理）。</p>
 */
public interface DeliverableService extends IService<Deliverable> {

    // ==================== CRUD ====================

    /**
     * 按项目/阶段/状态过滤查询交付件列表（参数均可空）。
     *
     * @param projectId 项目ID（可空）
     * @param phaseId   阶段ID（可空）
     * @param status    状态码（可空，参考 {@link com.dp.plat.deliverable.enums.DeliverableStatus}）
     * @return 交付件列表（按 id 倒序）
     */
    List<Deliverable> list(Long projectId, Long phaseId, String status);

    /**
     * 新建交付件 — 默认 status=DRAFT、currentVersion=1；若提供 filePath 则同步创建 v1 版本记录。
     *
     * @param deliverable 交付件（projectId/deliverableName 必填）
     * @return 已创建的交付件（含生成的 id）
     */
    Deliverable create(Deliverable deliverable);

    // ==================== 7 态状态机 ====================

    /**
     * 通用状态流转 — 校验状态机合法性并更新状态。
     *
     * <p>关联设计文档：§3.4 状态流转规则表。流程：</p>
     * <ol>
     *   <li>加载交付件与当前状态。</li>
     *   <li>校验转换合法（参考 {@link com.dp.plat.deliverable.enums.DeliverableStatus#canTransitionTo}）。</li>
     *   <li>非法转换 → 抛 {@link com.dp.plat.deliverable.exception.IllegalStateTransitionException}。</li>
     *   <li>合法 → 更新状态，并记录相关时间戳：
     *     <ul>
     *       <li>SIGNED → PUBLISHED：写入 {@code publishedAt}。</li>
     *       <li>REFERENCED → ARCHIVED：写入 {@code archivedAt}。</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * <p>注：PUBLISHED → DRAFT（修订新建版本）不由此方法处理，请使用 {@code revise}
     * （需提供新文件路径与变更说明）。</p>
     *
     * @param id        交付件ID
     * @param toStatus  目标状态码
     * @return 更新后的交付件
     */
    Deliverable transition(Long id, String toStatus);

    /** 提交：DRAFT → SUBMITTED。 */
    Deliverable submit(Long id);

    /**
     * 审核：SUBMITTED → REVIEWED（通过）或 → DRAFT（退回）。
     *
     * @param id      交付件ID
     * @param passed  true=审核通过进入 REVIEWED；false=退回 DRAFT
     */
    Deliverable review(Long id, boolean passed);

    /** 签核：REVIEWED → SIGNED。 */
    Deliverable sign(Long id);

    /** 发布：SIGNED → PUBLISHED（写入 publishedAt，版本固化）。 */
    Deliverable publish(Long id);

    /** 归档：REFERENCED → ARCHIVED（写入 archivedAt）。 */
    Deliverable archive(Long id);

    // ==================== 版本管理 ====================

    /**
     * 修订 — 新建版本不覆盖旧版本（Story 5 验收 1）。
     *
     * <p>关联设计文档：§3.4（行 421-425）、§4.5 事务边界（单事务：新建版本 + 更新交付件）。
     * 流程：</p>
     * <ol>
     *   <li>加载交付件，校验当前状态为 PUBLISHED 或 REFERENCED（其他状态禁止修订）。</li>
     *   <li>currentVersion + 1。</li>
     *   <li>创建新 {@link DeliverableVersion} 记录（versionNo = 新 currentVersion，status = DRAFT）。</li>
     *   <li>更新 {@code Deliverable.currentVersion}、{@code status = DRAFT}、{@code filePath = 新文件}。</li>
     *   <li><b>旧版本记录保留不变</b>（versionNo < 新版本的历史记录不受影响）。</li>
     * </ol>
     *
     * @param deliverableId 交付件ID
     * @param filePath      新版本文件路径
     * @param changeLog     版本变更说明
     * @param uploadedBy    上传人ID（可空，由审计字段兜底）
     * @return 新建的版本记录
     */
    DeliverableVersion revise(Long deliverableId, String filePath, String changeLog, Long uploadedBy);

    /**
     * 查询交付件的版本历史（按版本号倒序，最新在前）。
     *
     * @param deliverableId 交付件ID
     * @return 版本列表
     */
    List<DeliverableVersion> listVersions(Long deliverableId);

    /**
     * 查询指定版本的版本记录。
     *
     * @param deliverableId 交付件ID
     * @param versionNo    版本号
     * @return 版本记录（不存在返回 null）
     */
    DeliverableVersion getVersion(Long deliverableId, Integer versionNo);
}
