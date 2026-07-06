-- =============================================================
-- V28__create_sys_feedback_and_help_content.sql
-- Task 33 + Task 34：用户引导 / 帮助中心 + 技术支持反馈机制
--
-- 创建两张表：
--   sys_help_content  帮助中心内容（QUICK_START/FAQ/VIDEO/ADVANCED）
--   sys_feedback      技术支持反馈工单（BUG/SUGGESTION/QUESTION/OTHER）
--
-- <p>两张表均与 BaseEntity 对齐，包含审计字段 create_by/create_time/
-- update_by/update_time/deleted（create_by/update_by 使用 VARCHAR(64)
-- 与 BaseEntity 中 String 类型保持一致）。</p>
--
-- <p>版本号说明：tasks.md 原计划 SubTask 34.3 使用 V28，V27 已被
-- Task 27（lowcode 配置表）占用，故本迁移为 V28。</p>
-- =============================================================

-- -------------------------------------------------------------
-- 1. 帮助中心内容表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `sys_help_content`;
CREATE TABLE `sys_help_content` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category`    VARCHAR(32)   NOT NULL COMMENT '分类: QUICK_START/FAQ/VIDEO/ADVANCED',
    `title`       VARCHAR(200)  NOT NULL COMMENT '标题',
    `content`     MEDIUMTEXT    NOT NULL COMMENT '富文本内容（Markdown）',
    `sort_order`  INT           NOT NULL DEFAULT 0 COMMENT '排序值，升序展示',
    `status`      CHAR(1)       NOT NULL DEFAULT '0' COMMENT '状态: 0=启用 1=禁用',
    `view_count`  INT           NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `create_by`   VARCHAR(64)   DEFAULT '' COMMENT '创建人',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)   DEFAULT '' COMMENT '更新人',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0=否 1=是',
    PRIMARY KEY (`id`),
    KEY `idx_help_category` (`category`),
    KEY `idx_help_status` (`status`),
    KEY `idx_help_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帮助中心内容';

-- -------------------------------------------------------------
-- 2. 技术支持反馈表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `sys_feedback`;
CREATE TABLE `sys_feedback` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT        DEFAULT NULL COMMENT '提交人用户 ID（冗余字段）',
    `username`    VARCHAR(50)   DEFAULT NULL COMMENT '提交人用户名（冗余字段）',
    `category`    VARCHAR(32)   NOT NULL COMMENT '分类: BUG/SUGGESTION/QUESTION/OTHER',
    `title`       VARCHAR(200)  NOT NULL COMMENT '标题',
    `content`     VARCHAR(4000) NOT NULL COMMENT '内容',
    `contact`     VARCHAR(100)  DEFAULT NULL COMMENT '联系方式（电话/邮箱，选填）',
    `status`      VARCHAR(16)   NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/PROCESSING/RESOLVED/CLOSED',
    `reply`       VARCHAR(4000) DEFAULT NULL COMMENT '管理员回复内容',
    `reply_by`    VARCHAR(50)   DEFAULT NULL COMMENT '回复人用户名',
    `reply_at`    DATETIME      DEFAULT NULL COMMENT '回复时间',
    `create_by`   VARCHAR(64)   DEFAULT '' COMMENT '创建人',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)   DEFAULT '' COMMENT '更新人',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0=否 1=是',
    PRIMARY KEY (`id`),
    KEY `idx_feedback_user_id` (`user_id`),
    KEY `idx_feedback_username` (`username`),
    KEY `idx_feedback_status` (`status`),
    KEY `idx_feedback_category` (`category`),
    KEY `idx_feedback_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术支持反馈工单';

-- -------------------------------------------------------------
-- 3. 帮助内容初始化数据（5 条：快速开始 2 条、FAQ 2 条、进阶 1 条）
-- -------------------------------------------------------------
INSERT INTO `sys_help_content` (`category`, `title`, `content`, `sort_order`, `status`, `view_count`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
('QUICK_START', '5 分钟上手指南',
'## 5 分钟上手指南\n\n欢迎来到网络设备工程项目管理系统（PMS）！本指南将带您在 5 分钟内熟悉核心操作。\n\n### 1. 登录系统\n\n使用管理员分配的用户名 / 密码登录。首次登录建议立即修改密码。\n\n### 2. 创建项目\n\n进入「项目管理 → 项目列表」，点击「新建项目」，填写项目编码、名称、客户、起止日期后保存。\n\n### 3. 录入资产\n\n进入「资产管理 → 资产清单」，导入或手工录入设备资产，绑定到对应项目。\n\n### 4. 派发实施任务\n\n进入「实施管理 → 实施任务」，将施工任务派发给 OEM 或服务商。\n\n### 5. 查看仪表盘\n\n回到首页仪表盘，实时查看项目交付、资产状态、待办任务等关键指标。\n\n---\n\n如需更详细的功能说明，请继续阅读其他帮助文档。',
1, '0', 0, 'system', NOW(), 'system', NOW(), 0),

('QUICK_START', '常用快捷键与操作技巧',
'## 常用快捷键与操作技巧\n\n### 全局快捷键\n\n| 快捷键 | 功能 |\n| --- | --- |\n| `Alt + N` | 新建当前模块记录 |\n| `Alt + S` | 保存当前表单 |\n| `Esc` | 关闭对话框 |\n| `/` | 聚焦搜索框 |\n\n### 操作技巧\n\n- **批量操作**：在列表页勾选多行后可批量删除 / 导出。\n- **筛选持久化**：列表筛选条件会自动保存 7 天，下次进入自动恢复。\n- **拖拽排序**：看板视图支持拖拽卡片切换状态。\n- **快捷搜索**：在顶部搜索框输入 `#项目编码` 可快速跳转项目详情。\n\n### 移动端适配\n\n系统在 768px 以下自动切换为移动端布局，左侧菜单收起为抽屉，点击汉堡按钮展开。',
2, '0', 0, 'system', NOW(), 'system', NOW(), 0),

('FAQ', '如何重置忘记的密码？',
'## 如何重置忘记的密码？\n\n### 自助重置\n\n1. 在登录页点击「忘记密码？」链接。\n2. 输入注册邮箱 / 手机号，获取验证码。\n3. 验证通过后设置新密码（需包含大小写字母 + 数字 + 特殊字符，8-20 位）。\n\n### 联系管理员重置\n\n如无法自助重置，请联系系统管理员：\n\n- 管理员可在「系统管理 → 用户管理」中点击「重置密码」按钮。\n- 重置后的临时密码将通过邮件 / 短信发送给您，首次登录后请立即修改。\n\n### 安全提示\n\n- 系统使用 BCrypt 不可逆加密存储密码，任何人无法查看您的明文密码。\n- 建议每 90 天更换一次密码，不要在多个系统复用相同密码。',
1, '0', 0, 'system', NOW(), 'system', NOW(), 0),

('FAQ', '为什么我的数据看不到？数据权限说明',
'## 为什么我的数据看不到？\n\n### 数据权限模型\n\n系统采用基于角色的数据权限控制（RBAC + Data Scope）：\n\n| 数据范围 | 说明 |\n| --- | --- |\n| **全部** | 可见所有数据（仅管理员） |\n| **本公司** | 仅可见所属公司的数据 |\n| **本部门** | 仅可见所属部门的数据 |\n| **本人** | 仅可见自己创建的数据 |\n\n### 常见场景\n\n- **看不到项目**：检查当前角色是否配置了「项目管理」菜单权限，以及数据范围是否包含该项目所属公司 / 部门。\n- **看不到下属的工单**：联系管理员将您的角色数据范围调整为「本部门」或更高。\n- **跨部门协作**：需对方部门负责人提交变更申请，将您加入协作成员。\n\n### 排查步骤\n\n1. 确认登录账号已正确分配角色。\n2. 在「系统管理 → 角色管理」查看角色的数据范围配置。\n3. 联系管理员调整权限后，刷新页面或重新登录生效。',
2, '0', 0, 'system', NOW(), 'system', NOW(), 0),

('ADVANCED', '低代码配置：自定义业务页面',
'## 低代码配置：自定义业务页面\n\n### 概述\n\nPMS 内置低代码引擎，支持通过 JSON 配置快速生成业务页面，无需编写前端代码即可上线新功能。\n\n### 支持的页面类型\n\n| 类型 | 用途 |\n| --- | --- |\n| **Form** | 表单页（增改查表单） |\n| **List** | 列表页（分页查询 / 批量操作） |\n| **Tab** | 标签页（多 tab 切换） |\n| **Related Page** | 关联页（主从表关联展示） |\n\n### 配置流程\n\n1. 进入「低代码 → 表单配置」，点击「新建」打开可视化设计器。\n2. 拖拽字段组件到画布，配置字段属性（名称、类型、校验规则）。\n3. 在「列表配置」中绑定表单，配置列、筛选条件、批量操作按钮。\n4. 发布配置后，在「菜单管理」中新增菜单项，类型选择「低代码页面」。\n5. 用户刷新后即可在左侧菜单看到新页面。\n\n### 进阶技巧\n\n- **API 绑定**：列表配置的 `searchApi` 字段可绑定自定义后端接口。\n- **联动规则**：表单字段支持配置 Aviator 表达式实现联动显隐。\n- **版本管理**：每次发布生成新版本，可一键回滚到历史版本。\n\n### 示例：自定义「巡检记录」页面\n\n```json\n{\n  \"fields\": [\n    {\"name\": \"inspectDate\", \"label\": \"巡检日期\", \"type\": \"date\", \"required\": true},\n    {\"name\": \"result\", \"label\": \"巡检结果\", \"type\": \"select\", \"options\": [\"PASS\", \"FAIL\"]}\n  ]\n}\n```\n\n更多低代码能力请查阅官方文档或联系实施顾问。',
1, '0', 0, 'system', NOW(), 'system', NOW(), 0);
