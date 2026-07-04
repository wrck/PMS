-- =============================================================
-- V23__add_core_indexes.sql
-- 为核心业务表补充复合索引以加速高频查询场景：
-- 项目维度过滤、状态过滤、时间范围扫描、外键关联查询等。
--
-- 注意 1（列名修正）：列名以 V1~V22 已落地的 schema 为准。任务描述中的部分
-- 列名与实际 schema 不一致，已修正为真实列名：
--   pms_milestone.planned_date    -> plan_date
--   pms_impl_task.assigned_agent_id -> agent_id
--   pms_rma.status                -> ticket_status，pms_rma.created_at -> create_time
--   pms_warranty.expiry_date      -> end_date（pms_warranty 无 status 列，已跳过）
--   pms_change_request.created_at -> create_time
--   pms_risk.probability          -> likelihood
--   pms_attachment.created_by     -> create_by
--   sys_exception_log.created_at  -> occur_time，exception_class -> exception_type
--   sys_schedule_log.started_at   -> start_time
--   pms_asset.asset_no 列不存在，已跳过该索引
--
-- 注意 2（语法）：MySQL 8.x 不支持 `CREATE INDEX IF NOT EXISTS`
-- （该语法为 MariaDB 专有扩展，在 MySQL 8 上会报 Error 1064 语法错误）。
-- 由于 Flyway 每个版本迁移仅执行一次，且本脚本使用的索引名
-- （idx_<table>_<cols>）与 V1~V22 中已有索引名无冲突，因此采用
-- 标准 `CREATE INDEX` 语法即可保证幂等与可重放。
-- =============================================================

-- ----------------------------
-- pms_asset
-- ----------------------------
CREATE INDEX `idx_pms_asset_project_status` ON `pms_asset` (`project_id`, `status`);
CREATE INDEX `idx_pms_asset_serial_no` ON `pms_asset` (`serial_no`);
CREATE INDEX `idx_pms_asset_warranty_id` ON `pms_asset` (`warranty_id`);

-- ----------------------------
-- pms_milestone
-- ----------------------------
CREATE INDEX `idx_pms_milestone_project_type` ON `pms_milestone` (`project_id`, `milestone_type`);
CREATE INDEX `idx_pms_milestone_plan_date` ON `pms_milestone` (`plan_date`);
CREATE INDEX `idx_pms_milestone_actual_date` ON `pms_milestone` (`actual_date`);

-- ----------------------------
-- pms_impl_task
-- ----------------------------
CREATE INDEX `idx_pms_impl_task_project_status` ON `pms_impl_task` (`project_id`, `status`);
CREATE INDEX `idx_pms_impl_task_agent_status` ON `pms_impl_task` (`agent_id`, `status`);
CREATE INDEX `idx_pms_impl_task_plan_dates` ON `pms_impl_task` (`plan_start_date`, `plan_end_date`);

-- ----------------------------
-- pms_punch_list
-- ----------------------------
CREATE INDEX `idx_pms_punch_list_project_status` ON `pms_punch_list` (`project_id`, `status`);
CREATE INDEX `idx_pms_punch_list_deadline` ON `pms_punch_list` (`deadline`);
CREATE INDEX `idx_pms_punch_list_severity_status` ON `pms_punch_list` (`severity`, `status`);

-- ----------------------------
-- pms_rma
-- ----------------------------
CREATE INDEX `idx_pms_rma_asset_ticket_status` ON `pms_rma` (`asset_id`, `ticket_status`);
CREATE INDEX `idx_pms_rma_ticket_status_create_time` ON `pms_rma` (`ticket_status`, `create_time`);

-- ----------------------------
-- pms_warranty
-- ----------------------------
CREATE INDEX `idx_pms_warranty_asset_id` ON `pms_warranty` (`asset_id`);
CREATE INDEX `idx_pms_warranty_end_date` ON `pms_warranty` (`end_date`);

-- ----------------------------
-- pms_change_request
-- ----------------------------
CREATE INDEX `idx_pms_change_request_project_status` ON `pms_change_request` (`project_id`, `status`);
CREATE INDEX `idx_pms_change_request_create_time` ON `pms_change_request` (`create_time`);

-- ----------------------------
-- pms_risk
-- ----------------------------
CREATE INDEX `idx_pms_risk_project_status` ON `pms_risk` (`project_id`, `status`);
CREATE INDEX `idx_pms_risk_likelihood_impact` ON `pms_risk` (`likelihood`, `impact`);

-- ----------------------------
-- pms_issue
-- ----------------------------
CREATE INDEX `idx_pms_issue_project_status` ON `pms_issue` (`project_id`, `status`);
CREATE INDEX `idx_pms_issue_source_risk_id` ON `pms_issue` (`source_risk_id`);

-- ----------------------------
-- pms_notification
-- ----------------------------
CREATE INDEX `idx_pms_notification_user_read_created` ON `pms_notification` (`user_id`, `read_status`, `created_at`);
CREATE INDEX `idx_pms_notification_biz` ON `pms_notification` (`biz_type`, `biz_id`);

-- ----------------------------
-- pms_attachment
-- ----------------------------
CREATE INDEX `idx_pms_attachment_biz` ON `pms_attachment` (`biz_type`, `biz_id`);
CREATE INDEX `idx_pms_attachment_create_by_time` ON `pms_attachment` (`create_by`, `create_time`);

-- ----------------------------
-- sys_login_log
-- ----------------------------
CREATE INDEX `idx_sys_login_log_user_time` ON `sys_login_log` (`user_id`, `login_time`);
CREATE INDEX `idx_sys_login_log_login_time` ON `sys_login_log` (`login_time`);

-- ----------------------------
-- sys_exception_log
-- ----------------------------
CREATE INDEX `idx_sys_exception_log_occur_time` ON `sys_exception_log` (`occur_time`);
CREATE INDEX `idx_sys_exception_log_exception_type` ON `sys_exception_log` (`exception_type`);

-- ----------------------------
-- sys_schedule_log
-- ----------------------------
CREATE INDEX `idx_sys_schedule_log_task_status_start` ON `sys_schedule_log` (`task_name`, `status`, `start_time`);
