-- =====================================================================
-- 用户反馈模块迁移脚本
-- 表名：t_feedback
-- 说明：用于收集用户在使用系统过程中提交的问题、建议与咨询
-- 状态流转：open(待处理) -> processing(处理中) -> resolved(已解决) -> closed(已关闭)
-- =====================================================================

CREATE TABLE IF NOT EXISTS `t_feedback` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type`        VARCHAR(32)  DEFAULT NULL            COMMENT '反馈类型（bug-缺陷，suggestion-建议，question-咨询）',
  `title`       VARCHAR(255) DEFAULT NULL            COMMENT '标题',
  `content`     TEXT         DEFAULT NULL            COMMENT '内容',
  `contact`     VARCHAR(128) DEFAULT NULL            COMMENT '联系方式',
  `status`      VARCHAR(20)  DEFAULT 'open'          COMMENT '状态（open-待处理，processing-处理中，resolved-已解决，closed-已关闭）',
  `create_user` VARCHAR(64)  DEFAULT NULL            COMMENT '创建人',
  `create_time` DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_user` VARCHAR(64)  DEFAULT NULL            COMMENT '更新人',
  `update_time` DATETIME     DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈';
