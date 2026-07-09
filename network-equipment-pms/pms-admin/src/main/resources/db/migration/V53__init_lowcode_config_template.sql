-- 批次5-T8: 模板市场 — 配置模板表 + 权限
-- 支持上架/下架/搜索/下载/评分 + 模板版本 + 参数化
-- 借鉴 Zoho 模板市场 / Appsmith 模板 / Mendix App Store

CREATE TABLE IF NOT EXISTS pms_lowcode_config_template (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(128) NOT NULL COMMENT '模板编码',
  name VARCHAR(255) NOT NULL COMMENT '模板名称',
  config_type VARCHAR(32) NOT NULL COMMENT '配置类型',
  category VARCHAR(64) DEFAULT NULL COMMENT '分类',
  config_json LONGTEXT COMMENT '完整配置 JSON',
  thumbnail VARCHAR(512) DEFAULT NULL COMMENT '缩略图 URL',
  description TEXT DEFAULT NULL COMMENT '描述',
  author VARCHAR(64) DEFAULT NULL COMMENT '作者',
  tags VARCHAR(255) DEFAULT NULL COMMENT '标签（逗号分隔）',
  status VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT 'PUBLISHED/DRAFT/ARCHIVED',
  download_count INT NOT NULL DEFAULT 0,
  rating DECIMAL(3,1) NOT NULL DEFAULT 0.0,
  rating_count INT NOT NULL DEFAULT 0,
  version VARCHAR(32) NOT NULL DEFAULT '1.0.0',
  parameters LONGTEXT DEFAULT NULL COMMENT '参数化定义 JSON',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_code (code),
  KEY idx_config_type (config_type),
  KEY idx_status (status),
  KEY idx_download_count (download_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码配置模板市场';

-- 权限：模板市场父菜单（M 目录）+ 子权限（C 菜单 / F 按钮）
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time)
VALUES ('模板市场', 0, 50, 'template-market', NULL, NULL, 'M', '0', '0', NOW());
-- 通过子查询解析父菜单 id，避免 INSERT IGNORE 命中已存在记录时 LAST_INSERT_ID() 返回 0
SET @parentId = (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '模板市场' AND parent_id = 0 ORDER BY id DESC LIMIT 1) t);
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, perms, menu_type, visible, status, create_time) VALUES
('模板查询',     @parentId, 1, 'list',    'lowcode/template/list',    'lowcode:template:list',     'C', '0', '0', NOW()),
('模板创建',     @parentId, 2, 'create',  'lowcode/template/create',  'lowcode:template:create',   'C', '0', '0', NOW()),
('模板上架/下架', @parentId, 3, 'publish', NULL,                       'lowcode:template:publish',  'F', '0', '0', NOW()),
('模板下载',     @parentId, 4, 'download', NULL,                       'lowcode:template:download', 'F', '0', '0', NOW()),
('模板评分',     @parentId, 5, 'rate',    NULL,                        'lowcode:template:rate',     'F', '0', '0', NOW()),
('模板归档',     @parentId, 6, 'archive', NULL,                        'lowcode:template:delete',   'F', '0', '0', NOW());
