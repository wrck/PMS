package com.dp.plat.deliverable.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 交付件状态（7 态状态机）。
 *
 * <p>关联设计文档：§3.4（行 393-428）。状态流转规则：</p>
 * <pre>
 *   DRAFT ──提交──► SUBMITTED ──审核──► REVIEWED ──签核──► SIGNED ──发布──► PUBLISHED
 *     ▲                  │                     │                   │            │
 *     │ 退回              │ 退回                │                   │            │ 引用
 *     │                  └─────────────────────┘                   │            ▼
 *     │                                                            │     REFERENCED
 *     │ 修订（新建版本 v(n+1)，旧版本保留）                         │            │
 *     └────────────────────────────────────────────────────────────┘            │ 归档
 *                                                                               ▼
 *                                                                          ARCHIVED（终态）
 * </pre>
 *
 * <p>{@link #allowedNextStates()} 定义每个状态允许流转的下一状态集合，
 * 由 {@code DeliverableServiceImpl#transition} 校验合法性。</p>
 */
public enum DeliverableStatus {

    /** 草稿（初始态）。 */
    DRAFT,
    /** 已提交（待审核）。 */
    SUBMITTED,
    /** 已审核（待签核）。 */
    REVIEWED,
    /** 已签核（待发布）。 */
    SIGNED,
    /** 已发布（版本固化，可被引用）。 */
    PUBLISHED,
    /** 已被引用。 */
    REFERENCED,
    /** 已归档（终态，只读）。 */
    ARCHIVED;

    /**
     * 该状态允许流转的下一状态集合（参考 §3.4 状态流转规则表）。
     *
     * <ul>
     *   <li>DRAFT → SUBMITTED（提交）</li>
     *   <li>SUBMITTED → REVIEWED（审核通过）/ DRAFT（退回）</li>
     *   <li>REVIEWED → SIGNED（签核）/ DRAFT（退回）</li>
     *   <li>SIGNED → PUBLISHED（发布）</li>
     *   <li>PUBLISHED → REFERENCED（被引用）/ DRAFT（修订新建版本）</li>
     *   <li>REFERENCED → ARCHIVED（归档）</li>
     *   <li>ARCHIVED → （终态，无后续）</li>
     * </ul>
     */
    public Set<DeliverableStatus> allowedNextStates() {
        return switch (this) {
            case DRAFT -> EnumSet.of(SUBMITTED);
            case SUBMITTED -> EnumSet.of(REVIEWED, DRAFT);
            case REVIEWED -> EnumSet.of(SIGNED, DRAFT);
            case SIGNED -> EnumSet.of(PUBLISHED);
            case PUBLISHED -> EnumSet.of(REFERENCED, DRAFT);
            case REFERENCED -> EnumSet.of(ARCHIVED);
            case ARCHIVED -> EnumSet.noneOf(DeliverableStatus.class);
        };
    }

    /** 是否允许流转到目标状态。 */
    public boolean canTransitionTo(DeliverableStatus target) {
        return target != null && allowedNextStates().contains(target);
    }

    /** 状态码（与数据库列值一致）。 */
    public String code() {
        return name();
    }

    /** 由字符串解析状态（忽略大小写，无法识别返回 null）。 */
    public static DeliverableStatus of(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElse(null);
    }

    /** 全部状态码集合（供前端枚举展示）。 */
    public static Map<String, String> codeLabelMap() {
        return Collections.unmodifiableMap(
                Arrays.stream(values()).collect(Collectors.toMap(
                        Enum::name,
                        DeliverableStatus::label,
                        (a, b) -> a)));
    }

    /** 中文标签。 */
    public String label() {
        return switch (this) {
            case DRAFT -> "草稿";
            case SUBMITTED -> "已提交";
            case REVIEWED -> "已审核";
            case SIGNED -> "已签核";
            case PUBLISHED -> "已发布";
            case REFERENCED -> "已引用";
            case ARCHIVED -> "已归档";
        };
    }

    /**
     * 是否已批准（达到 PUBLISHED 及之后的状态）。
     *
     * <p>用于阶段退出校验：必需交付件必须达到「已批准」状态
     * （PUBLISHED/REFERENCED/ARCHIVED）才允许阶段推进。</p>
     */
    public boolean isApproved() {
        return this == PUBLISHED || this == REFERENCED || this == ARCHIVED;
    }
}
