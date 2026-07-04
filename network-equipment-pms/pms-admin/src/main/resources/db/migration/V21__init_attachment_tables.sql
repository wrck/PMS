-- =============================================================
-- V21__init_attachment_tables.sql
-- Initialize the attachment table used by pms-file module.
-- 统一文件附件管理：存储抽象 + 元数据 + GPS EXIF + 缩略图
-- =============================================================

DROP TABLE IF EXISTS `pms_attachment`;
CREATE TABLE `pms_attachment` (
    `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `biz_type`          VARCHAR(50)   NOT NULL COMMENT '业务类型（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE 等）',
    `biz_id`            BIGINT        DEFAULT NULL COMMENT '业务对象 id',
    `file_name`         VARCHAR(255)  NOT NULL COMMENT '文件名',
    `file_size`         BIGINT        DEFAULT NULL COMMENT '文件大小（字节）',
    `mime_type`         VARCHAR(100)  DEFAULT NULL COMMENT 'MIME 类型',
    `upload_user_id`    BIGINT        DEFAULT NULL COMMENT '上传人 id',
    `upload_user_name`  VARCHAR(100)  DEFAULT NULL COMMENT '上传人姓名',
    `upload_time`       DATETIME      DEFAULT NULL COMMENT '上传时间',
    `md5`               VARCHAR(64)   DEFAULT NULL COMMENT '文件 MD5 摘要',
    `storage_path`      VARCHAR(500)  NOT NULL COMMENT '存储路径（本地相对路径或对象存储 key）',
    `storage_type`      VARCHAR(20)   DEFAULT NULL COMMENT '存储类型（LOCAL/OSS/MINIO）',
    `gps_latitude`      DECIMAL(10,7) DEFAULT NULL COMMENT 'GPS 纬度',
    `gps_longitude`     DECIMAL(10,7) DEFAULT NULL COMMENT 'GPS 经度',
    `photo_taken_at`    DATETIME      DEFAULT NULL COMMENT '照片拍摄时间（来自 EXIF）',
    `geo_fence_status`  VARCHAR(20)   DEFAULT NULL COMMENT 'GPS 围栏比对结果（NORMAL/ABNORMAL）',
    `create_by`         VARCHAR(64)   DEFAULT '' COMMENT 'Creator',
    `create_time`       DATETIME      DEFAULT NULL COMMENT 'Create time',
    `update_by`         VARCHAR(64)   DEFAULT '' COMMENT 'Updater',
    `update_time`       DATETIME      DEFAULT NULL COMMENT 'Update time',
    `deleted`           TINYINT       DEFAULT 0 COMMENT 'Logical delete 0=no 1=yes',
    PRIMARY KEY (`id`),
    KEY `idx_biz` (`biz_type`, `biz_id`),
    KEY `idx_md5` (`md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件附件';
