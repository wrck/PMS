-- ============================================================
-- PMS 工作流相关表初始化脚本
-- 迁移自老系统 Activiti 表结构，适配 Flowable 7.x
-- ============================================================

-- 审批意见表（自定义，对应老系统 dp_act_comment / dp_comment）
CREATE TABLE IF NOT EXISTS `wf_approval_comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `obj_id` BIGINT COMMENT '业务对象ID',
    `procdef_key` VARCHAR(100) COMMENT '流程定义Key',
    `inst_id` VARCHAR(100) COMMENT '流程实例ID',
    `task_id` VARCHAR(100) COMMENT '任务ID',
    `task_key` VARCHAR(100) COMMENT '任务节点Key',
    `assignee` VARCHAR(100) COMMENT '审批人',
    `assignee_name` VARCHAR(100) COMMENT '审批人姓名',
    `message` TEXT COMMENT '审批意见',
    `result` INT COMMENT '审批结果: 1=通过, -1=驳回, 0=待审批',
    `assignee_time` DATETIME COMMENT '审批时间',
    `next_assignee` VARCHAR(100) COMMENT '下一环节审批人',
    `next_assignee_name` VARCHAR(100) COMMENT '下一环节审批人姓名',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_obj_procdef` (`obj_id`, `procdef_key`),
    INDEX `idx_inst_id` (`inst_id`),
    INDEX `idx_assignee` (`assignee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批意见表';

-- Flowable引擎表由框架自动创建，无需手动创建
-- 以下为Flowable自动创建的表（仅列出说明）：
-- ACT_RE_* : 流程定义表（Repository）
-- ACT_RU_* : 运行时表（Runtime）
-- ACT_HI_* : 历史表（History）
-- ACT_GE_* : 通用数据表（General）

-- 插入默认的流程定义说明
-- 流程定义文件位于: pms-service/src/main/resources/processes/
-- - callback.bpmn20.xml : 回访审批流程
-- - closedloop.bpmn20.xml : 闭环审批流程（PM→SM→CB→CL）

-- 回访表添加流程实例ID字段（如不存在）
ALTER TABLE `pm_callback_header` ADD COLUMN IF NOT EXISTS `inst_id` VARCHAR(100) COMMENT '流程实例ID';
ALTER TABLE `pm_callback_header` ADD COLUMN IF NOT EXISTS `task_def_key` VARCHAR(100) COMMENT '当前任务节点Key';

-- 闭环表添加流程实例ID字段（如不存在）
ALTER TABLE `pm_closed_loop` ADD COLUMN IF NOT EXISTS `inst_id` VARCHAR(100) COMMENT '流程实例ID';
