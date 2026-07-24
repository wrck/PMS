-- =============================================================
-- V93__fix_infra_api_access_log_result_code_column.sql
-- 修复 infra_api_access_log 表的 resultCode 列名为 result_code
--
-- 背景：
--   V91 创建 infra_api_access_log 时误将 result_code 写成 resultCode（驼峰），
--   yudao ApiAccessLogDO.resultCode 字段经 MyBatis-Plus 驼峰转下划线后
--   映射到 result_code，导致 INSERT 时报 "Unknown column 'result_code'"。
--
-- 幂等性：通过 information_schema 检测列名，仅在 resultCode 存在时执行 RENAME。
-- =============================================================

DROP PROCEDURE IF EXISTS pms_v93_fix_result_code;
DELIMITER $$
CREATE PROCEDURE pms_v93_fix_result_code()
BEGIN
    DECLARE col_exists INT DEFAULT 0;

    SELECT COUNT(*) INTO col_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'infra_api_access_log'
      AND COLUMN_NAME = 'resultCode';

    IF col_exists > 0 THEN
        ALTER TABLE `infra_api_access_log`
            CHANGE COLUMN `resultCode` `result_code` INT NOT NULL DEFAULT 0 COMMENT '结果码';
    END IF;
END$$
DELIMITER ;

CALL pms_v93_fix_result_code();
DROP PROCEDURE IF EXISTS pms_v93_fix_result_code;
