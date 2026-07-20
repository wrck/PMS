-- V72__alter_impltask_and_create_task_tables.sql
-- 任务层级字段 + 检查项 / 评论 / 活动表
-- 关联设计文档：§2.2（行 133-158）、§3.3 任务状态机、§6.5（行 1365-1434）

-- ======================================================================
-- 1. 扩展 pms_impl_task：任务层级与汇总字段
-- ======================================================================
ALTER TABLE pms_impl_task
    ADD COLUMN parent_task_id   BIGINT        NULL COMMENT '父任务ID（NULL=顶层）',
    ADD COLUMN task_path        VARCHAR(500)  NOT NULL DEFAULT '/' COMMENT '物化路径 /12/45/78/',
    ADD COLUMN depth            INT           NOT NULL DEFAULT 0 COMMENT '层级深度（0=顶层）',
    ADD COLUMN priority         VARCHAR(16)   NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级 LOW/MEDIUM/HIGH/CRITICAL',
    ADD COLUMN actual_hours     DECIMAL(8,2)  NULL COMMENT '实际工时',
    ADD COLUMN remaining_hours  DECIMAL(8,2)  NULL COMMENT '剩余工时',
    ADD COLUMN phase_id         BIGINT        NULL COMMENT '关联项目阶段ID',
    ADD COLUMN task_weight      DECIMAL(5,2)  NOT NULL DEFAULT 1.00 COMMENT '自定义汇总权重（配置开启时生效）';

CREATE INDEX idx_parent_task_id  ON pms_impl_task (parent_task_id);
CREATE INDEX idx_task_path       ON pms_impl_task (task_path);
CREATE INDEX idx_project_id_phase ON pms_impl_task (project_id, phase_id);

-- 回填存量任务路径（顶层任务路径 = /<id>/）
UPDATE pms_impl_task
SET task_path = CONCAT('/', id, '/'),
    depth = 0,
    priority = 'MEDIUM',
    task_weight = 1.00
WHERE task_path = '/' OR task_path IS NULL;

-- ======================================================================
-- 2. 任务检查项 pms_task_checklist
-- ======================================================================
CREATE TABLE pms_task_checklist (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    task_id         BIGINT       NOT NULL COMMENT '关联任务ID',
    title           VARCHAR(128) NOT NULL COMMENT '检查项标题',
    description     VARCHAR(500) NULL COMMENT '检查项描述',
    mandatory       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否强制检查项（提交评审前必须勾选）',
    checked         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已勾选',
    checked_by      BIGINT       NULL COMMENT '勾选人ID',
    checked_at      DATETIME     NULL COMMENT '勾选时间',
    sort_order      INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_mandatory (task_id, mandatory),
    KEY idx_task_checked (task_id, checked)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务检查项';

-- ======================================================================
-- 3. 任务评论 pms_task_comment
-- ======================================================================
CREATE TABLE pms_task_comment (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    task_id             BIGINT       NOT NULL COMMENT '关联任务ID',
    user_id             BIGINT       NOT NULL COMMENT '评论人ID',
    user_name           VARCHAR(64)  NULL COMMENT '评论人姓名（冗余）',
    content             TEXT         NOT NULL COMMENT '评论内容',
    parent_comment_id   BIGINT       NULL COMMENT '父评论ID（二级回复）',
    create_by           BIGINT       NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT       NULL,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_parent (task_id, parent_comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评论';

-- ======================================================================
-- 4. 任务活动记录 pms_task_activity（追加型，记录任务全生命周期事件）
-- ======================================================================
CREATE TABLE pms_task_activity (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    task_id         BIGINT       NOT NULL COMMENT '关联任务ID',
    user_id         BIGINT       NOT NULL COMMENT '操作人ID',
    user_name       VARCHAR(64)  NULL COMMENT '操作人姓名（冗余）',
    activity_type   VARCHAR(50)  NOT NULL COMMENT '活动类型 CREATE/UPDATE/STATUS_CHANGE/SUBMIT_REVIEW/APPROVE/REJECT/CHECKLIST_CHECK/COMMENT/PROGRESS_CHANGE/ASSIGN/MOVE',
    content         TEXT         NULL COMMENT '活动描述',
    metadata        JSON         NULL COMMENT '附加元数据（如 old_value/new_value 结构化数据）',
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_type_time (task_id, activity_type, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务活动记录';
