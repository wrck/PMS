-- V83__align_createby_updateby_to_varchar.sql
-- 修复 Phase 1-7 新表 create_by / update_by 类型与 BaseEntity 不一致问题
--
-- 背景：
--   - pms-common/.../BaseEntity.java:33  定义 createBy / updateBy 为 String
--   - pms-common/.../MyBatisPlusConfig.java:52  MetaObjectHandler 总是以
--     SecurityUtils.getCurrentUsername()（String）填充 createBy / updateBy
--   - V2 表（pms_project / pms_impl_task / pms_deliverable / pms_project_member）的
--     create_by / update_by 已是 VARCHAR(64)，与新表逻辑一致
--   - 但 V69/V71/V72/V73/V74/V76 新建的 13 张表误用 BIGINT，导致 MyBatis-Plus
--     自动填充 'admin' 字符串时抛出 java.sql.SQLException:
--       Incorrect integer value: 'admin' for column 'create_by' at row 1
--
-- 修复方案：将 13 张表的 create_by / update_by 列改为 VARCHAR(64) NULL，
--          并把历史 BIGINT 值（如 1）回填为 'admin'，与 BaseEntity 语义对齐。
--
-- 影响表清单（13 张）：
--   V69: pms_project_template, pms_project_template_version
--   V71: pms_project_phase, pms_project_config
--   V72: pms_task_checklist, pms_task_comment, pms_task_activity
--   V73: pms_task_dependency
--   V74: pms_baseline_snapshot
--   V76: pms_approval_record, pms_approval_node, pms_approval_history,
--        pms_approval_field_permission
-- =============================================================

-- -------------------------------------------------------------
-- 1. 使用存储过程幂等修改列类型 BIGINT -> VARCHAR(64)
--    仅在目标列为 BIGINT 时执行 ALTER MODIFY，避免重复执行时报错
-- -------------------------------------------------------------
DROP PROCEDURE IF EXISTS pms_v83_align_audit_columns;
DELIMITER $$
CREATE PROCEDURE pms_v83_align_audit_columns()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE tbl_name VARCHAR(64);
    DECLARE col_name VARCHAR(64);
    DECLARE cur CURSOR FOR
        SELECT TABLE_NAME, COLUMN_NAME
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND DATA_TYPE = 'bigint'
          AND COLUMN_NAME IN ('create_by', 'update_by')
          AND TABLE_NAME IN (
              'pms_project_template',
              'pms_project_template_version',
              'pms_project_phase',
              'pms_project_config',
              'pms_task_checklist',
              'pms_task_comment',
              'pms_task_activity',
              'pms_task_dependency',
              'pms_baseline_snapshot',
              'pms_approval_record',
              'pms_approval_node',
              'pms_approval_history',
              'pms_approval_field_permission'
          );
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO tbl_name, col_name;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 动态执行 ALTER TABLE ... MODIFY COLUMN
        SET @ddl = CONCAT(
            'ALTER TABLE `', tbl_name, '` ',
            'MODIFY COLUMN `', col_name, '` VARCHAR(64) NULL',
            CASE col_name
                WHEN 'create_by' THEN ' COMMENT ''创建人（用户名）'''
                WHEN 'update_by' THEN ' COMMENT ''更新人（用户名）'''
                ELSE ''
            END
        );
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE cur;
END$$
DELIMITER ;

CALL pms_v83_align_audit_columns();
DROP PROCEDURE IF EXISTS pms_v83_align_audit_columns;

-- -------------------------------------------------------------
-- 2. 数据回填：将历史 BIGINT 值（如 1、'1'）统一改为 'admin'
--    ALTER 后 BIGINT 1 会被 MySQL 转换为 VARCHAR '1'，
--    这里把 '1' 等纯数字 ID 值替换为统一用户名 'admin'
--    （V82 演示数据使用 1 作为 create_by/update_by，对应 admin 用户）
-- -------------------------------------------------------------
UPDATE `pms_project_template`              SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_project_template`              SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_project_template_version`      SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_project_template_version`      SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_project_phase`                 SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_project_phase`                 SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_project_config`                SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_project_config`                SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_task_checklist`                SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_task_checklist`                SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_task_comment`                  SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_task_comment`                  SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_task_activity`                 SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_task_activity`                 SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_task_dependency`               SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_task_dependency`               SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_baseline_snapshot`             SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_baseline_snapshot`             SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_record`               SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_record`               SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_node`                 SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_node`                 SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_history`              SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_history`              SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_field_permission`     SET create_by = 'admin' WHERE create_by IS NOT NULL AND create_by REGEXP '^[0-9]+$';
UPDATE `pms_approval_field_permission`     SET update_by = 'admin' WHERE update_by IS NOT NULL AND update_by REGEXP '^[0-9]+$';

-- -------------------------------------------------------------
-- 3. V82 演示数据修正：V82 INSERT 使用了 create_by = 1（BIGINT 字面量），
--    上述 ALTER 后这些值会变为字符串 '1'，已被 §2 回填为 'admin'。
--    本节再补一次针对 V82 新增数据（如 §23 全量补全的 13 条新 task、
--    22 条新 phase、4 条新 member 等）的统一回填，确保 V82 新增的
--    create_by / update_by 全部为 'admin'。
--    （§2 的 UPDATE 已覆盖此场景，这里仅为防御性二次回填）
-- -------------------------------------------------------------
-- （无需额外 SQL，§2 的 REGEXP '^[0-9]+$' 已覆盖所有数字字符串值）

-- =============================================================
-- 4. 自检注释
-- =============================================================
-- 预期变更：
--   - 13 张表 × 2 列 = 26 个 ALTER MODIFY COLUMN BIGINT -> VARCHAR(64) NULL
--   - 历史值 '1' / 1 统一回填为 'admin'（与 SecurityUtils.getCurrentUsername() 语义一致）
--   - 修复后 ProjectPhase.insert 等 MyBatis-Plus 自动填充不再抛
--     'Incorrect integer value: admin for column create_by' 错误
--
-- 兼容性说明：
--   - 已存在 VARCHAR(64) 的表（V2 表 + V75 deliverable 三张子表）不受影响
--   - V82 演示数据中的 create_by = 1 / update_by = 1 字面量
--     经 ALTER 后变为 '1'，再经 §2 UPDATE 回填为 'admin'
--   - V83 可重复执行：第二次执行时 information_schema 检测到列已为 VARCHAR，
--     不会再次 ALTER；UPDATE WHERE REGEXP 也匹配不到任何行
-- =============================================================
-- 文件结束
