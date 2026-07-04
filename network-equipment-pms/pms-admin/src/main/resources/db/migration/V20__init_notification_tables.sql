-- =============================================================
-- V20__init_notification_tables.sql
-- Initialize the notification center tables: in-app notifications
-- and notification templates (multi-channel delivery + Freemarker
-- template engine).
-- =============================================================

DROP TABLE IF EXISTS `pms_notification`;
CREATE TABLE `pms_notification` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `user_id`      BIGINT       NOT NULL COMMENT 'Recipient user id',
    `title`        VARCHAR(200) DEFAULT NULL COMMENT 'Notification title',
    `content`      TEXT         DEFAULT NULL COMMENT 'Notification body',
    `category`     VARCHAR(50)  DEFAULT NULL COMMENT 'Business category (MILESTONE/TASK/APPROVAL/PUNCH_LIST/WARRANTY/RMA/SETTLEMENT)',
    `biz_type`     VARCHAR(50)  DEFAULT NULL COMMENT 'Business type (maps to template code)',
    `biz_id`       BIGINT       DEFAULT NULL COMMENT 'Related business record id',
    `read_status`  VARCHAR(20)  DEFAULT 'UNREAD' COMMENT 'Read status (UNREAD/READ)',
    `channel`      VARCHAR(20)  DEFAULT 'IN_APP' COMMENT 'Delivery channel (IN_APP/WS/EMAIL/OA)',
    `created_at`   DATETIME     DEFAULT NULL COMMENT 'Created time',
    `created_by`   BIGINT       DEFAULT NULL COMMENT 'Creator user id',
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `read_status`),
    KEY `idx_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='In-app notification';

DROP TABLE IF EXISTS `pms_notification_template`;
CREATE TABLE `pms_notification_template` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `template_code` VARCHAR(100) NOT NULL COMMENT 'Unique template code',
    `subject`       VARCHAR(500) DEFAULT NULL COMMENT 'Subject template with ${var} placeholders',
    `body`          TEXT         DEFAULT NULL COMMENT 'Body template with ${var} placeholders',
    `variables`     TEXT         DEFAULT NULL COMMENT 'Variable definitions in JSON',
    `description`   VARCHAR(500) DEFAULT NULL COMMENT 'Template description',
    `created_at`    DATETIME     DEFAULT NULL COMMENT 'Created time',
    `updated_at`    DATETIME     DEFAULT NULL COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Notification template';

-- =============================================================
-- Preset standard notification templates.
-- subject/body contain Freemarker ${var} placeholders.
-- =============================================================

INSERT INTO `pms_notification_template` (`template_code`, `subject`, `body`, `variables`, `description`, `created_at`, `updated_at`) VALUES
('MILESTONE_OVERDUE',
 '里程碑逾期提醒：${projectName} - ${milestoneName}',
 '项目 ${projectName} 的里程碑「${milestoneName}」计划完成日期为 ${planDate}，现已逾期，请尽快推进并更新进度。',
 '[{"name":"projectName","desc":"项目名称"},{"name":"milestoneName","desc":"里程碑名称"},{"name":"planDate","desc":"计划完成日期"}]',
 '里程碑逾期提醒',
 NOW(), NOW()),

('TASK_ASSIGNED',
 '新任务分派：${taskName}',
 '您已被分派任务「${taskName}」（项目：${projectName}），计划完成日期 ${planEndDate}，请及时跟进。',
 '[{"name":"taskName","desc":"任务名称"},{"name":"projectName","desc":"项目名称"},{"name":"planEndDate","desc":"计划完成日期"}]',
 '任务分派通知',
 NOW(), NOW()),

('TASK_DELEGATED',
 '任务转派给您：${taskName}',
 '${fromUser} 将任务「${taskName}」转派给您（项目：${projectName}），请于 ${planEndDate} 前完成。',
 '[{"name":"fromUser","desc":"转派人"},{"name":"taskName","desc":"任务名称"},{"name":"projectName","desc":"项目名称"},{"name":"planEndDate","desc":"计划完成日期"}]',
 '任务转派通知',
 NOW(), NOW()),

('APPROVAL_TODO',
 '待办审批：${approvalTitle}',
 '您有新的审批待办「${approvalTitle}」，提交人 ${submitter}，请尽快处理。',
 '[{"name":"approvalTitle","desc":"审批标题"},{"name":"submitter","desc":"提交人"}]',
 '审批待办通知',
 NOW(), NOW()),

('PUNCH_LIST_DEADLINE',
 '尾项清单到期提醒：${punchItemName}',
 '尾项「${punchItemName}」将于 ${deadline} 到期，当前状态 ${status}，请及时闭环。',
 '[{"name":"punchItemName","desc":"尾项名称"},{"name":"deadline","desc":"到期日期"},{"name":"status","desc":"当前状态"}]',
 '尾项清单到期提醒',
 NOW(), NOW()),

('WARRANTY_EXPIRE_90',
 '质保即将到期（剩余 90 天）：${assetName}',
 '设备 ${assetName}（编号 ${assetCode}）的质保将于 ${warrantyEndDate} 到期，剩余 90 天，请关注续保事宜。',
 '[{"name":"assetName","desc":"设备名称"},{"name":"assetCode","desc":"设备编号"},{"name":"warrantyEndDate","desc":"质保到期日期"}]',
 '质保到期 90 天预警',
 NOW(), NOW()),

('WARRANTY_EXPIRE_60',
 '质保即将到期（剩余 60 天）：${assetName}',
 '设备 ${assetName}（编号 ${assetCode}）的质保将于 ${warrantyEndDate} 到期，剩余 60 天，请关注续保事宜。',
 '[{"name":"assetName","desc":"设备名称"},{"name":"assetCode","desc":"设备编号"},{"name":"warrantyEndDate","desc":"质保到期日期"}]',
 '质保到期 60 天预警',
 NOW(), NOW()),

('WARRANTY_EXPIRE_30',
 '质保即将到期（剩余 30 天）：${assetName}',
 '设备 ${assetName}（编号 ${assetCode}）的质保将于 ${warrantyEndDate} 到期，剩余 30 天，请尽快联系续保或安排下线。',
 '[{"name":"assetName","desc":"设备名称"},{"name":"assetCode","desc":"设备编号"},{"name":"warrantyEndDate","desc":"质保到期日期"}]',
 '质保到期 30 天预警',
 NOW(), NOW()),

('RMA_STATUS_CHANGE',
 'RMA 状态变更：${rmaNo}',
 'RMA 申请 ${rmaNo} 的状态已变更为 ${status}，请留意后续处理。',
 '[{"name":"rmaNo","desc":"RMA 单号"},{"name":"status","desc":"最新状态"}]',
 'RMA 状态变更通知',
 NOW(), NOW()),

('SETTLEMENT_APPROVED',
 '结算已审批通过：${settlementNo}',
 '结算单 ${settlementNo} 已审批通过，结算金额 ${amount}，请查看详情。',
 '[{"name":"settlementNo","desc":"结算单号"},{"name":"amount","desc":"结算金额"}]',
 '结算审批通过通知',
 NOW(), NOW()),

('CHANGE_REQUEST_CCB',
 '变更请求待 CCB 评审：${crNo}',
 '变更请求 ${crNo}「${title}」待 CCB 评审，请及时处理。',
 '[{"name":"crNo","desc":"变更请求编号"},{"name":"title","desc":"变更标题"}]',
 '变更请求 CCB 评审通知',
 NOW(), NOW()),

('RISK_ESCALATED',
 '风险升级提醒：${riskName}',
 '风险「${riskName}」已升级为 ${level} 等级，请关注并制定应对措施。',
 '[{"name":"riskName","desc":"风险名称"},{"name":"level","desc":"风险等级"}]',
 '风险升级提醒',
 NOW(), NOW());
