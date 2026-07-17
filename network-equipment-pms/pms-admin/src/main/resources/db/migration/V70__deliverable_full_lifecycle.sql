-- V70__deliverable_full_lifecycle.sql
-- 交付件全生命周期（Story 5）：Deliverable 扩展为 7 态状态机 + 版本/签名/引用表
-- 关联设计文档：§2.2（行 185-211）、§3.4 交付件状态机 7 态（行 393-428）、§6.8（行 1494-1564）

-- ======================================================================
-- 1. 扩展 pms_deliverable 表（V2 已建表）
--    新增字段：phase_id / current_version / mandatory / approver_role /
--             published_at / archived_at
--    status 字段升级为 7 态：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED
--    （status 列已存在，仅做数据迁移 + 修改注释）
-- ======================================================================

-- 1.1 数据迁移：旧状态值映射到 7 态
UPDATE pms_deliverable SET status = 'DRAFT'     WHERE status = 'PENDING';
UPDATE pms_deliverable SET status = 'PUBLISHED' WHERE status = 'CONFIRMED';

-- 1.2 幂等新增列（参考 V69 的 PROCEDURE 写法，防止环境中已部分存在）
DROP PROCEDURE IF EXISTS pms_v70_alter_deliverable;
DELIMITER $$
CREATE PROCEDURE pms_v70_alter_deliverable()
BEGIN
    -- phase_id
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND COLUMN_NAME = 'phase_id') THEN
        ALTER TABLE pms_deliverable
            ADD COLUMN phase_id BIGINT NULL COMMENT '所属阶段ID' AFTER project_id;
    END IF;

    -- current_version
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND COLUMN_NAME = 'current_version') THEN
        ALTER TABLE pms_deliverable
            ADD COLUMN current_version INT NOT NULL DEFAULT 1 COMMENT '当前版本号，从 1 开始' AFTER status;
    END IF;

    -- mandatory
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND COLUMN_NAME = 'mandatory') THEN
        ALTER TABLE pms_deliverable
            ADD COLUMN mandatory TINYINT(1) NOT NULL DEFAULT 0 COMMENT '必需交付件（影响阶段退出）' AFTER current_version;
    END IF;

    -- approver_role
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND COLUMN_NAME = 'approver_role') THEN
        ALTER TABLE pms_deliverable
            ADD COLUMN approver_role VARCHAR(32) NULL COMMENT '签核角色' AFTER mandatory;
    END IF;

    -- published_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND COLUMN_NAME = 'published_at') THEN
        ALTER TABLE pms_deliverable
            ADD COLUMN published_at DATETIME NULL COMMENT '发布时间' AFTER approver_role;
    END IF;

    -- archived_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND COLUMN_NAME = 'archived_at') THEN
        ALTER TABLE pms_deliverable
            ADD COLUMN archived_at DATETIME NULL COMMENT '归档时间' AFTER published_at;
    END IF;

    -- 1.3 幂等新增索引
    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND INDEX_NAME = 'idx_phase_mandatory') THEN
        ALTER TABLE pms_deliverable ADD INDEX idx_phase_mandatory (phase_id, mandatory);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'pms_deliverable'
                     AND INDEX_NAME = 'idx_project_status') THEN
        ALTER TABLE pms_deliverable ADD INDEX idx_project_status (project_id, status);
    END IF;
END$$
DELIMITER ;

CALL pms_v70_alter_deliverable();
DROP PROCEDURE IF EXISTS pms_v70_alter_deliverable;

-- 1.4 修改 status 列注释为 7 态说明（不影响数据）
ALTER TABLE pms_deliverable
    MODIFY COLUMN status VARCHAR(32) DEFAULT 'DRAFT'
    COMMENT '状态：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED';

-- ======================================================================
-- 2. 交付件版本表 pms_deliverable_version
--    版本不可变：已发布版本的 file_path 不允许覆盖；修订时新建 version_no+1 的记录。
-- ======================================================================
CREATE TABLE pms_deliverable_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    deliverable_id  BIGINT       NOT NULL                          COMMENT '交付件ID',
    version_no      INT          NOT NULL                          COMMENT '版本号 1,2,3...',
    file_path       VARCHAR(500) NOT NULL                          COMMENT '文件路径',
    file_checksum   VARCHAR(128) NULL                              COMMENT 'SHA256 校验和',
    uploaded_by     BIGINT       NOT NULL                          COMMENT '上传人ID',
    uploaded_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    change_log      VARCHAR(500) NULL                              COMMENT '版本变更说明',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'           COMMENT '该版本流转状态',
    create_by       VARCHAR(64)  NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64)  NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version_lock    INT          NOT NULL DEFAULT 0                COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_deliverable_version (deliverable_id, version_no)  COMMENT '同一交付件版本号唯一',
    KEY idx_deliverable_id (deliverable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件版本';

-- ======================================================================
-- 3. 交付件签名表 pms_deliverable_signature
--    记录 SIGNED 阶段的签核动作（电子/印章/数字签名）。
-- ======================================================================
CREATE TABLE pms_deliverable_signature (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    deliverable_id  BIGINT       NOT NULL                          COMMENT '交付件ID',
    version_no      INT          NOT NULL                          COMMENT '签名对应的版本',
    signer_id       BIGINT       NOT NULL                          COMMENT '签核人ID',
    signer_name     VARCHAR(64)  NULL                              COMMENT '签核人姓名（冗余）',
    signer_role     VARCHAR(32)  NULL                              COMMENT '签核角色',
    signature_type  VARCHAR(20)  NOT NULL DEFAULT 'ELECTRONIC'     COMMENT 'ELECTRONIC/STAMP/DIGITAL',
    signature_data  VARCHAR(500) NULL                              COMMENT '签名数据（证书指纹/印章图URL）',
    signed_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签核时间',
    create_by       VARCHAR(64)  NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_deliverable_version (deliverable_id, version_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件签名';

-- ======================================================================
-- 4. 交付件引用关系表 pms_deliverable_reference
--    记录 PUBLISHED 状态交付件被其他业务对象（TASK/PHASE/PROJECT/DELIVERABLE/REPORT）引用的关系。
-- ======================================================================
CREATE TABLE pms_deliverable_reference (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    source_deliverable_id BIGINT     NOT NULL                      COMMENT '被引用的交付件ID',
    target_deliverable_id BIGINT     NULL                           COMMENT '引用方为交付件时填，否则 NULL',
    reference_type      VARCHAR(32)  NOT NULL                       COMMENT '引用方业务类型：TASK/PHASE/PROJECT/DELIVERABLE/REPORT',
    referenced_by_id    BIGINT       NOT NULL                       COMMENT '引用方业务ID',
    referenced_by_name  VARCHAR(128) NULL                           COMMENT '引用方名称（冗余）',
    created_by          VARCHAR(64)  NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_source_deliverable (source_deliverable_id),
    KEY idx_ref_by (reference_type, referenced_by_id),
    KEY idx_target_deliverable (target_deliverable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件引用关系';
