-- V88__deliverable_structured_content.sql
-- 交付件结构化文档内容块（借鉴问卷功能，支持动态配置内容块）
-- 1. pms_deliverable 新增 content_blocks JSON 列（AFTER file_path），用 PREPARE/EXECUTE 幂等
-- 2. 新增字典 pms_deliverable_block_type（dict id=112，6 项：富文本/内嵌表/选项卡/标题/分隔线/代码块）
-- 3. 新增表 pms_deliverable_type_template（按交付件类型预置默认内容块模板）
-- 4. 预置 7 种交付件类型的默认内容块模板（DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER）
--
-- NOTE: 使用 PREPARE/EXECUTE + INFORMATION_SCHEMA 实现幂等（参考 V86 写法），
--       不使用 DELIMITER/存储过程，确保 Flyway 完全兼容。

-- =============================================================
-- 1. 幂等新增 pms_deliverable.content_blocks JSON 列
-- =============================================================
SET @c = (SELECT COUNT(*) FROM information_schema.COLUMNS
          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_deliverable' AND COLUMN_NAME = 'content_blocks');
SET @sql = IF(@c = 0,
    'ALTER TABLE pms_deliverable ADD COLUMN `content_blocks` JSON DEFAULT NULL COMMENT ''结构化内容块（JSON 数组，元素见 DeliverableContentBlock DTO）'' AFTER `file_path`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =============================================================
-- 2. 新增字典：pms_deliverable_block_type（内容块类型，6 项）
-- =============================================================
INSERT IGNORE INTO `sys_dict` (`id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
(112, '交付件内容块类型', 'pms_deliverable_block_type', '0', 'admin', NOW());

INSERT IGNORE INTO `sys_dict_item` (`dict_id`, `item_text`, `item_value`, `sort_order`, `create_by`, `create_time`) VALUES
(112, '富文本',   'RICH_TEXT',  1, 'admin', NOW()),
(112, '内嵌表',   'TABLE',      2, 'admin', NOW()),
(112, '选项卡',   'TABS',       3, 'admin', NOW()),
(112, '标题',     'HEADING',    4, 'admin', NOW()),
(112, '分隔线',   'DIVIDER',    5, 'admin', NOW()),
(112, '代码块',   'CODE_BLOCK', 6, 'admin', NOW());

-- =============================================================
-- 3. 新增表：pms_deliverable_type_template（按交付件类型预置默认内容块模板）
-- =============================================================
CREATE TABLE IF NOT EXISTS `pms_deliverable_type_template` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `deliverable_type` VARCHAR(32) NOT NULL COMMENT '交付件性质类型（见字典 pms_deliverable_type）：DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER',
    `default_blocks`  JSON         NOT NULL COMMENT '默认内容块（JSON 数组，元素见 DeliverableContentBlock DTO）',
    `description`     VARCHAR(255) DEFAULT NULL COMMENT '模板说明',
    `create_by`       VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    `create_time`     DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`       VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    `update_time`     DATETIME     DEFAULT NULL COMMENT '更新时间',
    `deleted`         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=否 1=是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_deliverable_type` (`deliverable_type`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '交付件类型默认内容块模板';

-- =============================================================
-- 4. 预置 7 种交付件类型的默认内容块模板
--    每个内容块对象结构：
--    JSON_OBJECT('blockType','XXX','blockKey','key','blockTitle','标题',
--                'blockConfig',JSON_OBJECT(...),'blockContent',初始值,'sortOrder',N)
--    blockContent：RICH_TEXT/HEADING/CODE_BLOCK 为空字符串 ''，
--                  TABLE 为 JSON_ARRAY()，TABS 为 JSON_OBJECT()，DIVIDER 为 null
-- =============================================================

-- DOCUMENT(1): HEADING(title,level1) + RICH_TEXT(body)
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(1, 'DOCUMENT',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','RICH_TEXT','blockKey','body','blockTitle','正文','blockConfig',JSON_OBJECT(),'blockContent','','sortOrder',2)
 ),
 '文档类默认模板：标题 + 正文', 'admin', NOW());

-- CODE(2): HEADING(title,level1) + CODE_BLOCK(code,language=text) + RICH_TEXT(desc)
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(2, 'CODE',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','CODE_BLOCK','blockKey','code','blockTitle','代码','blockConfig',JSON_OBJECT('language','text'),'blockContent','','sortOrder',2),
     JSON_OBJECT('blockType','RICH_TEXT','blockKey','desc','blockTitle','说明','blockConfig',JSON_OBJECT(),'blockContent','','sortOrder',3)
 ),
 '代码类默认模板：标题 + 代码块 + 说明', 'admin', NOW());

-- ENTITY_REF(3): HEADING(title,level1) + RICH_TEXT(desc)
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(3, 'ENTITY_REF',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','RICH_TEXT','blockKey','desc','blockTitle','说明','blockConfig',JSON_OBJECT(),'blockContent','','sortOrder',2)
 ),
 '实体引用类默认模板：标题 + 说明', 'admin', NOW());

-- MODEL(4): HEADING(title,level1) + RICH_TEXT(desc) + TABLE(attrs,columns=['属性名','类型','说明','默认值'])
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(4, 'MODEL',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','RICH_TEXT','blockKey','desc','blockTitle','说明','blockConfig',JSON_OBJECT(),'blockContent','','sortOrder',2),
     JSON_OBJECT('blockType','TABLE','blockKey','attrs','blockTitle','属性表','blockConfig',JSON_OBJECT('columns',JSON_ARRAY('属性名','类型','说明','默认值')),'blockContent',JSON_ARRAY(),'sortOrder',3)
 ),
 '模型类默认模板：标题 + 说明 + 属性表', 'admin', NOW());

-- CONFIG(5): HEADING(title,level1) + CODE_BLOCK(config,language=ini) + TABLE(params,columns=['参数名','取值','说明'])
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(5, 'CONFIG',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','CODE_BLOCK','blockKey','config','blockTitle','配置内容','blockConfig',JSON_OBJECT('language','ini'),'blockContent','','sortOrder',2),
     JSON_OBJECT('blockType','TABLE','blockKey','params','blockTitle','参数表','blockConfig',JSON_OBJECT('columns',JSON_ARRAY('参数名','取值','说明')),'blockContent',JSON_ARRAY(),'sortOrder',3)
 ),
 '配置类默认模板：标题 + 配置代码 + 参数表', 'admin', NOW());

-- DATA(6): HEADING(title,level1) + TABLE(dataset,columns=['列1','列2','列3']) + RICH_TEXT(desc)
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(6, 'DATA',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','TABLE','blockKey','dataset','blockTitle','数据集','blockConfig',JSON_OBJECT('columns',JSON_ARRAY('列1','列2','列3')),'blockContent',JSON_ARRAY(),'sortOrder',2),
     JSON_OBJECT('blockType','RICH_TEXT','blockKey','desc','blockTitle','说明','blockConfig',JSON_OBJECT(),'blockContent','','sortOrder',3)
 ),
 '数据类默认模板：标题 + 数据集表 + 说明', 'admin', NOW());

-- OTHER(7): HEADING(title,level1) + RICH_TEXT(body)
INSERT IGNORE INTO `pms_deliverable_type_template`
    (`id`, `deliverable_type`, `default_blocks`, `description`, `create_by`, `create_time`) VALUES
(7, 'OTHER',
 JSON_ARRAY(
     JSON_OBJECT('blockType','HEADING','blockKey','title','blockTitle','标题','blockConfig',JSON_OBJECT('level',1),'blockContent','','sortOrder',1),
     JSON_OBJECT('blockType','RICH_TEXT','blockKey','body','blockTitle','正文','blockConfig',JSON_OBJECT(),'blockContent','','sortOrder',2)
 ),
 '其他类默认模板：标题 + 正文', 'admin', NOW());
