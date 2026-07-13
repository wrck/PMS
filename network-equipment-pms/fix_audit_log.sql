ALTER TABLE `pms_lowcode_config_audit_log`
    ADD COLUMN `operate_time` DATETIME DEFAULT NULL COMMENT 'operate time' AFTER `diff_summary`,
    ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time' AFTER `create_time`,
    ADD COLUMN `create_by` VARCHAR(64) DEFAULT NULL COMMENT 'create by' AFTER `update_time`,
    ADD COLUMN `update_by` VARCHAR(64) DEFAULT NULL COMMENT 'update by' AFTER `create_by`,
    ADD COLUMN `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'deleted' AFTER `update_by`;
