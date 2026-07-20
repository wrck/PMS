-- V76__create_approval_center.sql
-- 统一审批中心（Story 6）：审批记录/节点/历史/字段权限 4 表
-- 关联设计文档：§2.2 ApprovalRecord/ApprovalFieldPermission（行 214-243）、
--              §3.5 审批中心统一规则（行 429-500）、§5.7 统一审批中心 API（行 1080-1147）、
--              §6.9（行 1565-1648）
--
-- 状态机：[DRAFT] ──提交──► [PENDING] ──通过──► [APPROVED]
--                          │
--             ┌────────────┼────────────┐
--             ▼            ▼            ▼
--        [REJECTED]   [WITHDRAWN]   [TIMEOUT]
--             │ 重新提交（round+1，复用原记录）
--             ▼
--        [PENDING]

-- ======================================================================
-- 1. 统一审批记录表 pms_approval_record
--    approval_type：PROJECT/TASK/DELIVERABLE/RISK/ISSUE/CHANGE/RESOURCE/COST/PHASE_EXIT/BASELINE_CHANGE
--    status：PENDING/APPROVED/REJECTED/WITHDRAWN/TIMEOUT
--    round：审批轮次（退回后重新提交 +1，复用原记录）
-- ======================================================================
CREATE TABLE pms_approval_record (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    approval_type       VARCHAR(32)  NOT NULL COMMENT 'PROJECT/TASK/DELIVERABLE/RISK/ISSUE/CHANGE/RESOURCE/COST/PHASE_EXIT/BASELINE_CHANGE',
    business_id         BIGINT       NOT NULL COMMENT '业务对象ID',
    business_code       VARCHAR(64)  NULL COMMENT '业务编码冗余',
    project_id          BIGINT       NULL COMMENT '项目维度',
    process_instance_id VARCHAR(64)  NULL COMMENT 'Flowable流程实例ID',
    title               VARCHAR(200) NOT NULL,
    submitter_id        BIGINT       NOT NULL,
    submitter_name      VARCHAR(64)  NULL,
    current_node_id     VARCHAR(64)  NULL COMMENT '当前节点ID（Flowable）',
    current_node_name   VARCHAR(64)  NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/WITHDRAWN/TIMEOUT',
    round               INT          NOT NULL DEFAULT 1 COMMENT '审批轮次',
    submitted_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at        DATETIME     NULL,
    timeout_at          DATETIME     NULL COMMENT '超时时间点',
    escalated           TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已升级',
    create_by           BIGINT       NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT       NULL,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_business_type_id (approval_type, business_id),
    KEY idx_project_status (project_id, status),
    KEY idx_submitter_status (submitter_id, status),
    KEY idx_status_timeout (status, timeout_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一审批记录';

-- ======================================================================
-- 2. 审批节点表 pms_approval_node
--    节点顺序流转：当前节点通过后激活 node_order+1 的节点。
--    approver_id 与 approver_role 二选一（多选一时存实际处理人）。
-- ======================================================================
CREATE TABLE pms_approval_node (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    record_id           BIGINT       NOT NULL,
    node_name           VARCHAR(64)  NOT NULL,
    node_order          INT          NOT NULL COMMENT '节点顺序',
    approver_id         BIGINT       NULL COMMENT '指定审批人',
    approver_role       VARCHAR(32)  NULL COMMENT '审批角色（多选一）',
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    approver_actual_id  BIGINT       NULL COMMENT '实际处理人',
    opinion             VARCHAR(500) NULL,
    operated_at         DATETIME     NULL,
    timeout_at          DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_record_order (record_id, node_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点';

-- ======================================================================
-- 3. 审批历史表 pms_approval_history
--    记录每轮每次操作（节点、操作人、动作、意见、时间戳），支持多轮次追溯。
--    action：SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT
-- ======================================================================
CREATE TABLE pms_approval_history (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    record_id       BIGINT       NOT NULL,
    round           INT          NOT NULL,
    node_name       VARCHAR(64)  NOT NULL,
    operator_id     BIGINT       NOT NULL,
    operator_name   VARCHAR(64)  NULL,
    action          VARCHAR(20)  NOT NULL COMMENT 'SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT',
    opinion         VARCHAR(500) NULL,
    operated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_record_round_time (record_id, round, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批历史';

-- ======================================================================
-- 4. 审批敏感字段权限表 pms_approval_field_permission
--    permission：VISIBLE/MASKED/HIDDEN
--    mask_pattern：phone-mask/amount-mask/email-mask/custom
-- ======================================================================
CREATE TABLE pms_approval_field_permission (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    approval_node_id BIGINT      NOT NULL COMMENT '关联审批节点（或节点模板）',
    entity_type     VARCHAR(128) NOT NULL COMMENT '业务实体类名',
    field_name      VARCHAR(64)  NOT NULL,
    permission      VARCHAR(20)  NOT NULL DEFAULT 'VISIBLE' COMMENT 'VISIBLE/MASKED/HIDDEN',
    mask_pattern    VARCHAR(64)  NULL COMMENT '脱敏规则：phone-mask/amount-mask/email-mask/custom',
    custom_pattern  VARCHAR(128) NULL COMMENT '自定义正则（当 mask_pattern=custom）',
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_node_entity (approval_node_id, entity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批敏感字段权限';
