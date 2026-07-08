-- 批次4-T8: 组件市场 — 为组件元数据增加市场字段
-- 支持版本管理、作者、状态（PUBLISHED/DRAFT/ARCHIVED）、标签、下载量

ALTER TABLE pms_lowcode_component_meta
    ADD COLUMN version VARCHAR(32) DEFAULT '1.0.0' COMMENT '组件版本' AFTER description,
    ADD COLUMN author VARCHAR(64) DEFAULT NULL COMMENT '作者' AFTER version,
    ADD COLUMN status VARCHAR(16) DEFAULT 'PUBLISHED' COMMENT '状态: PUBLISHED/DRAFT/ARCHIVED' AFTER author,
    ADD COLUMN tags VARCHAR(256) DEFAULT NULL COMMENT '标签（逗号分隔）' AFTER status,
    ADD COLUMN download_count INT DEFAULT 0 COMMENT '下载量' AFTER tags,
    ADD COLUMN source_type VARCHAR(16) DEFAULT 'BUILTIN' COMMENT '来源: BUILTIN/CUSTOM/MARKETPLACE' AFTER download_count,
    ADD COLUMN entry_url VARCHAR(512) DEFAULT NULL COMMENT '远程组件入口 URL（MARKETPLACE 类型）' AFTER source_type;

-- 为已有内置组件设置来源
UPDATE pms_lowcode_component_meta SET source_type = 'BUILTIN' WHERE builtin = 1;

-- 组件市场权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('组件市场', 0, 99, 'component-market', NULL, 'lowcode:component:market', 'C', '0', '0', NOW());

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('组件市场查看', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms = 'lowcode:component:market' LIMIT 1) t), 1, '', NULL, 'lowcode:component:market:list', 'F', '0', '0', NOW());

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('组件市场编辑', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms = 'lowcode:component:market' LIMIT 1) t), 2, '', NULL, 'lowcode:component:market:edit', 'F', '0', '0', NOW());

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('组件市场发布', (SELECT id FROM (SELECT id FROM sys_menu WHERE perms = 'lowcode:component:market' LIMIT 1) t), 3, '', NULL, 'lowcode:component:market:publish', 'F', '0', '0', NOW());
