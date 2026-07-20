-- =====================================================================
-- V67__fix_onboarding_task_emp_id_nullable.sql
-- 修复 demo_onboarding_task 表 emp_id 字段：
--   原始定义 emp_id BIGINT NOT NULL，但入职任务表单未包含 emp_id 字段，
--   导致创建任务时 MySQL 报错 "Field 'emp_id' doesn't have a default value"。
--   改为允许 NULL，使入职任务可以独立创建（不强制关联员工）。
-- =====================================================================

ALTER TABLE `demo_onboarding_task` MODIFY COLUMN `emp_id` BIGINT NULL DEFAULT NULL COMMENT '员工ID（可选）';

SELECT 'emp_id 已改为可空' AS message;
