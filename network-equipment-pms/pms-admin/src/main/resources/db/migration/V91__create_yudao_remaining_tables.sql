-- =============================================================
-- V91__create_yudao_remaining_tables.sql
-- 补齐 yudao 框架运行所需的其余数据库表（OAuth2 / 日志 / 通知 / 短信 / 邮件 / 社交 / infra）
--
-- 背景：
--   V89 已创建 12 张核心 RBAC 表（system_users/role/menu/user_role/role_menu/
--   dept/post/user_post/dict_type/dict_data/tenant/tenant_package）。
--   yudao 框架运行时（spring-boot:run）还会注入大量 Mapper，对应表若缺失会导致
--   启动报错（如 OAuth2TokenApiImpl 注入 OAuth2AccessTokenMapper 失败）。
--   本脚本根据 yudao-module-system 与 yudao-module-infra 的 Entity 类（@TableName）
--   反向生成剩余表的 DDL，确保 yudao 框架可正常启动。
--
-- 字段来源说明（同 V89）：
--   - BaseDO：create_time, update_time, creator(varchar64), updater(varchar64), deleted(bit1 @TableLogic)
--   - TenantBaseDO：继承 BaseDO + tenant_id(BIGINT)
--   - 多租户已禁用（yudao.tenant.enable: false），但 tenant_id 字段仍保留建表
--     （TenantBaseDO 子类需要），只是不自动注入。
--   - @TenantIgnore 标注的 Entity 即使父类是 BaseDO 也无 tenant_id（与 TenantBaseDO 区分）。
--
-- 本脚本补齐表清单（29 张）：
--   OAuth2（5）：system_oauth2_client / code / access_token / refresh_token / approve
--   日志（2）：system_operate_log / system_login_log
--   通知（3）：system_notice / system_notify_message / system_notify_template
--   短信（4）：system_sms_template / system_sms_log / system_sms_channel / system_sms_code
--   邮件（3）：system_mail_account / system_mail_template / system_mail_log
--   社交（3）：system_social_user / system_social_user_bind / system_social_client
--   infra（9）：infra_config / infra_data_source_config / infra_job / infra_job_log /
--             infra_file / infra_file_content / infra_file_config /
--             infra_api_access_log / infra_api_error_log
--
-- 跳过说明：
--   - system_sensitive_word：yudao-module-system 仓库中未找到 SensitiveWordDO，跳过
--   - infra_codegen_table / infra_codegen_column：代码生成器专用表，开发工具，非运行时必需，跳过
--   - yudao_demo01_* / yudao_demo02_* / yudao_demo03_*：示例表，跳过
--
-- 幂等性：全部使用 CREATE TABLE IF NOT EXISTS / INSERT IGNORE，重复执行不会报错
-- =============================================================

-- =============================================================
-- 一、OAuth2 相关
-- =============================================================

-- -------------------------------------------------------------
-- 1. system_oauth2_client  OAuth2 客户端表
--    OAuth2ClientDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_oauth2_client` (
    `id`                              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `client_id`                       VARCHAR(64)  NOT NULL COMMENT '客户端编号',
    `secret`                          VARCHAR(128) NOT NULL DEFAULT '' COMMENT '客户端密钥',
    `name`                            VARCHAR(64)  NOT NULL COMMENT '应用名',
    `logo`                            VARCHAR(255) DEFAULT NULL COMMENT '应用图标',
    `description`                     VARCHAR(512) DEFAULT NULL COMMENT '应用描述',
    `status`                          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0=开启 1=禁用）',
    `access_token_validity_seconds`   INT          NOT NULL DEFAULT 0 COMMENT '访问令牌有效期（秒）',
    `refresh_token_validity_seconds`  INT          NOT NULL DEFAULT 0 COMMENT '刷新令牌有效期（秒）',
    `redirect_uris`                   VARCHAR(1024) DEFAULT NULL COMMENT '可重定向 URI（JSON 数组）',
    `authorized_grant_types`          VARCHAR(1024) DEFAULT NULL COMMENT '授权类型（JSON 数组）',
    `scopes`                          VARCHAR(1024) DEFAULT NULL COMMENT '授权范围（JSON 数组）',
    `auto_approve_scopes`             VARCHAR(1024) DEFAULT NULL COMMENT '自动授权 Scope（JSON 数组）',
    `authorities`                     VARCHAR(1024) DEFAULT NULL COMMENT '权限（JSON 数组）',
    `resource_ids`                    VARCHAR(1024) DEFAULT NULL COMMENT '资源（JSON 数组）',
    `additional_information`          VARCHAR(1024) DEFAULT NULL COMMENT '附加信息（JSON）',
    `creator`                         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`                     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`                         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`                     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2 客户端表';

-- -------------------------------------------------------------
-- 2. system_oauth2_code  OAuth2 授权码表
--    OAuth2CodeDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_oauth2_code` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `code`          VARCHAR(64)  NOT NULL COMMENT '授权码',
    `user_id`       BIGINT       NOT NULL COMMENT '用户编号',
    `user_type`     TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `client_id`     VARCHAR(64)  NOT NULL COMMENT '客户端编号',
    `scopes`        VARCHAR(1024) DEFAULT NULL COMMENT '授权范围（JSON 数组）',
    `redirect_uri`  VARCHAR(512) DEFAULT NULL COMMENT '重定向地址',
    `state`         VARCHAR(64)  DEFAULT NULL COMMENT '状态',
    `expires_time`  DATETIME     DEFAULT NULL COMMENT '过期时间',
    `creator`       VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2 授权码表';

-- -------------------------------------------------------------
-- 3. system_oauth2_access_token  OAuth2 访问令牌表
--    OAuth2AccessTokenDO extends TenantBaseDO（有 tenant_id）
--    ※ 本表是修复启动报错（OAuth2TokenApiImpl 注入失败）的关键表之一
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_oauth2_access_token` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `access_token`   VARCHAR(128) NOT NULL COMMENT '访问令牌',
    `refresh_token`  VARCHAR(128) DEFAULT NULL COMMENT '刷新令牌',
    `user_id`        BIGINT       NOT NULL COMMENT '用户编号',
    `user_type`      TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `user_info`      VARCHAR(1024) DEFAULT NULL COMMENT '用户信息（JSON Map）',
    `client_id`      VARCHAR(64)  NOT NULL COMMENT '客户端编号',
    `scopes`         VARCHAR(1024) DEFAULT NULL COMMENT '授权范围（JSON 数组）',
    `expires_time`   DATETIME     DEFAULT NULL COMMENT '过期时间',
    `tenant_id`      BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_access_token` (`access_token`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2 访问令牌表';

-- -------------------------------------------------------------
-- 4. system_oauth2_refresh_token  OAuth2 刷新令牌表
--    OAuth2RefreshTokenDO extends TenantBaseDO（有 tenant_id）
--    ※ 本表是修复启动报错（OAuth2TokenApiImpl 注入失败）的关键表之一
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_oauth2_refresh_token` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `refresh_token`   VARCHAR(128) NOT NULL COMMENT '刷新令牌',
    `user_id`         BIGINT       NOT NULL COMMENT '用户编号',
    `user_type`       TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `client_id`       VARCHAR(64)  NOT NULL COMMENT '客户端编号',
    `scopes`          VARCHAR(1024) DEFAULT NULL COMMENT '授权范围（JSON 数组）',
    `expires_time`    DATETIME     DEFAULT NULL COMMENT '过期时间',
    `tenant_id`       BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refresh_token` (`refresh_token`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2 刷新令牌表';

-- -------------------------------------------------------------
-- 5. system_oauth2_approve  OAuth2 批准表
--    OAuth2ApproveDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_oauth2_approve` (
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`        BIGINT      NOT NULL COMMENT '用户编号',
    `user_type`      TINYINT     NOT NULL DEFAULT 0 COMMENT '用户类型',
    `client_id`      VARCHAR(64) NOT NULL COMMENT '客户端编号',
    `scope`          VARCHAR(64) NOT NULL DEFAULT '' COMMENT '授权范围',
    `approved`       BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否接受',
    `expires_time`   DATETIME    DEFAULT NULL COMMENT '过期时间',
    `creator`        VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_client` (`user_id`, `client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2 批准表';

-- =============================================================
-- 二、日志相关
-- =============================================================

-- -------------------------------------------------------------
-- 6. system_operate_log  操作日志表
--    OperateLogDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_operate_log` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `trace_id`        VARCHAR(64)  DEFAULT NULL COMMENT '链路追踪编号',
    `user_id`         BIGINT       NOT NULL DEFAULT 0 COMMENT '用户编号',
    `user_type`       TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `type`            VARCHAR(64)  DEFAULT NULL COMMENT '操作模块类型',
    `sub_type`        VARCHAR(64)  DEFAULT NULL COMMENT '操作名',
    `biz_id`          BIGINT       DEFAULT NULL COMMENT '操作模块业务编号',
    `action`          VARCHAR(1024) DEFAULT NULL COMMENT '日志内容',
    `extra`           VARCHAR(1024) DEFAULT NULL COMMENT '拓展字段（JSON）',
    `request_method`  VARCHAR(32)  DEFAULT NULL COMMENT '请求方法名',
    `request_url`     VARCHAR(512) DEFAULT NULL COMMENT '请求地址',
    `user_ip`         VARCHAR(50)  DEFAULT NULL COMMENT '用户 IP',
    `user_agent`      VARCHAR(512) DEFAULT NULL COMMENT '浏览器 UA',
    `creator`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_biz_id` (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- -------------------------------------------------------------
-- 7. system_login_log  登录日志表
--    LoginLogDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_login_log` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `log_type`     TINYINT      NOT NULL DEFAULT 0 COMMENT '日志类型',
    `trace_id`     VARCHAR(64)  DEFAULT NULL COMMENT '链路追踪编号',
    `user_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '用户编号',
    `user_type`    TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `username`     VARCHAR(64)  DEFAULT NULL COMMENT '用户账号',
    `result`       TINYINT      NOT NULL DEFAULT 0 COMMENT '登录结果',
    `user_ip`      VARCHAR(50)  DEFAULT NULL COMMENT '用户 IP',
    `user_agent`   VARCHAR(512) DEFAULT NULL COMMENT '浏览器 UA',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- =============================================================
-- 三、通知相关
-- =============================================================

-- -------------------------------------------------------------
-- 8. system_notice  通知公告表
--    NoticeDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_notice` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title`        VARCHAR(128) NOT NULL COMMENT '公告标题',
    `type`         TINYINT      NOT NULL DEFAULT 0 COMMENT '公告类型',
    `content`      TEXT         COMMENT '公告内容',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '公告状态（0=开启 1=禁用）',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- -------------------------------------------------------------
-- 9. system_notify_message  站内信消息表
--    NotifyMessageDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_notify_message` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '站内信编号',
    `user_id`            BIGINT       NOT NULL COMMENT '用户编号',
    `user_type`          TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `template_id`        BIGINT       NOT NULL COMMENT '模版编号',
    `template_code`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '模版编码',
    `template_type`      TINYINT      NOT NULL DEFAULT 0 COMMENT '模版类型',
    `template_nickname`  VARCHAR(64)  DEFAULT NULL COMMENT '模版发送人名称',
    `template_content`   TEXT         COMMENT '模版内容',
    `template_params`    VARCHAR(1024) DEFAULT NULL COMMENT '模版参数（JSON Map）',
    `read_status`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否已读',
    `read_time`          DATETIME     DEFAULT NULL COMMENT '阅读时间',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内信消息表';

-- -------------------------------------------------------------
-- 10. system_notify_template  站内信模版表
--     NotifyTemplateDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_notify_template` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`         VARCHAR(64)  NOT NULL COMMENT '模版名称',
    `code`         VARCHAR(64)  NOT NULL COMMENT '模版编码',
    `type`         TINYINT      NOT NULL DEFAULT 0 COMMENT '模版类型',
    `nickname`     VARCHAR(64)  DEFAULT NULL COMMENT '发送人名称',
    `content`      TEXT         COMMENT '模版内容',
    `params`       VARCHAR(1024) DEFAULT NULL COMMENT '参数数组（JSON 数组）',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0=开启 1=禁用）',
    `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内信模版表';

-- =============================================================
-- 四、短信相关
-- =============================================================

-- -------------------------------------------------------------
-- 11. system_sms_template  短信模版表
--     SmsTemplateDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_sms_template` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    `type`             TINYINT      NOT NULL DEFAULT 0 COMMENT '短信类型',
    `status`           TINYINT      NOT NULL DEFAULT 0 COMMENT '启用状态（0=开启 1=禁用）',
    `code`             VARCHAR(64)  NOT NULL COMMENT '模板编码',
    `name`             VARCHAR(64)  NOT NULL COMMENT '模板名称',
    `content`          VARCHAR(1024) DEFAULT NULL COMMENT '模板内容',
    `params`           VARCHAR(1024) DEFAULT NULL COMMENT '参数数组（JSON 数组）',
    `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `api_template_id`  VARCHAR(64)  DEFAULT NULL COMMENT '短信 API 的模板编号',
    `channel_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '短信渠道编号',
    `channel_code`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '短信渠道编码',
    `creator`          VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sms_template_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信模版表';

-- -------------------------------------------------------------
-- 12. system_sms_log  短信日志表
--     SmsLogDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_sms_log` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    `channel_id`        BIGINT       NOT NULL DEFAULT 0 COMMENT '短信渠道编号',
    `channel_code`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '短信渠道编码',
    `template_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '模板编号',
    `template_code`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '模板编码',
    `template_type`     TINYINT      NOT NULL DEFAULT 0 COMMENT '短信类型',
    `template_content`  VARCHAR(1024) DEFAULT NULL COMMENT '模板内容',
    `template_params`   VARCHAR(1024) DEFAULT NULL COMMENT '模板参数（JSON Map）',
    `api_template_id`   VARCHAR(64)  DEFAULT NULL COMMENT '短信 API 的模板编号',
    `mobile`            VARCHAR(32)  NOT NULL COMMENT '手机号',
    `user_id`           BIGINT       DEFAULT NULL COMMENT '用户编号',
    `user_type`         TINYINT      DEFAULT NULL COMMENT '用户类型',
    `send_status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '发送状态',
    `send_time`         DATETIME     DEFAULT NULL COMMENT '发送时间',
    `api_send_code`     VARCHAR(64)  DEFAULT NULL COMMENT '短信 API 发送结果编码',
    `api_send_msg`      VARCHAR(255) DEFAULT NULL COMMENT '短信 API 发送失败提示',
    `api_request_id`    VARCHAR(64)  DEFAULT NULL COMMENT '短信 API 发送返回请求 ID',
    `api_serial_no`     VARCHAR(64)  DEFAULT NULL COMMENT '短信 API 发送返回序号',
    `receive_status`    TINYINT      NOT NULL DEFAULT 0 COMMENT '接收状态',
    `receive_time`      DATETIME     DEFAULT NULL COMMENT '接收时间',
    `api_receive_code`  VARCHAR(64)  DEFAULT NULL COMMENT '短信 API 接收结果编码',
    `api_receive_msg`   VARCHAR(255) DEFAULT NULL COMMENT '短信 API 接收结果提示',
    `creator`           VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`           VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_channel_id` (`channel_id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信日志表';

-- -------------------------------------------------------------
-- 13. system_sms_channel  短信渠道表
--     SmsChannelDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_sms_channel` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '渠道编号',
    `signature`     VARCHAR(64)  NOT NULL COMMENT '短信签名',
    `code`          VARCHAR(64)  NOT NULL COMMENT '渠道编码',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '启用状态（0=开启 1=禁用）',
    `remark`        VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `api_key`       VARCHAR(128) NOT NULL DEFAULT '' COMMENT '短信 API 账号',
    `api_secret`    VARCHAR(128) DEFAULT NULL COMMENT '短信 API 密钥',
    `callback_url`  VARCHAR(255) DEFAULT NULL COMMENT '短信发送回调 URL',
    `creator`       VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sms_channel_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信渠道表';

-- -------------------------------------------------------------
-- 14. system_sms_code  手机验证码表
--     SmsCodeDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_sms_code` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '编号',
    `mobile`        VARCHAR(32) NOT NULL COMMENT '手机号',
    `code`          VARCHAR(16) NOT NULL COMMENT '验证码',
    `scene`         TINYINT     NOT NULL DEFAULT 0 COMMENT '发送场景',
    `create_ip`     VARCHAR(50) DEFAULT NULL COMMENT '创建 IP',
    `today_index`   INT         NOT NULL DEFAULT 0 COMMENT '今日发送第几条',
    `used`          BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否使用',
    `used_time`     DATETIME    DEFAULT NULL COMMENT '使用时间',
    `used_ip`       VARCHAR(50) DEFAULT NULL COMMENT '使用 IP',
    `creator`       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手机验证码表';

-- =============================================================
-- 五、邮件相关
-- =============================================================

-- -------------------------------------------------------------
-- 15. system_mail_account  邮箱账号表
--     MailAccountDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_mail_account` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `mail`               VARCHAR(128) NOT NULL COMMENT '邮箱',
    `username`           VARCHAR(128) NOT NULL DEFAULT '' COMMENT '用户名',
    `password`           VARCHAR(128) NOT NULL DEFAULT '' COMMENT '密码',
    `host`               VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'SMTP 服务器域名',
    `port`               INT          NOT NULL DEFAULT 0 COMMENT 'SMTP 服务器端口',
    `ssl_enable`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否开启 SSL',
    `starttls_enable`    BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否开启 STARTTLS',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱账号表';

-- -------------------------------------------------------------
-- 16. system_mail_template  邮件模版表
--     MailTemplateDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_mail_template` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         VARCHAR(64)  NOT NULL COMMENT '模版名称',
    `code`         VARCHAR(64)  NOT NULL COMMENT '模版编号',
    `account_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '发送的邮箱账号编号',
    `nickname`     VARCHAR(64)  DEFAULT NULL COMMENT '发送人名称',
    `title`        VARCHAR(256) NOT NULL DEFAULT '' COMMENT '标题',
    `content`      TEXT         COMMENT '内容',
    `params`       VARCHAR(1024) DEFAULT NULL COMMENT '参数数组（JSON 数组）',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0=开启 1=禁用）',
    `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mail_template_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件模版表';

-- -------------------------------------------------------------
-- 17. system_mail_log  邮箱日志表
--     MailLogDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_mail_log` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志编号',
    `user_id`              BIGINT       DEFAULT NULL COMMENT '用户编号',
    `user_type`            TINYINT      DEFAULT NULL COMMENT '用户类型',
    `to_mails`             VARCHAR(1024) DEFAULT NULL COMMENT '接收邮箱地址（JSON 数组）',
    `cc_mails`             VARCHAR(1024) DEFAULT NULL COMMENT '抄送邮箱地址（JSON 数组）',
    `bcc_mails`            VARCHAR(1024) DEFAULT NULL COMMENT '密送邮箱地址（JSON 数组）',
    `account_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '邮箱账号编号',
    `from_mail`            VARCHAR(128) DEFAULT NULL COMMENT '发送邮箱地址',
    `template_id`          BIGINT       NOT NULL DEFAULT 0 COMMENT '模版编号',
    `template_code`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '模版编码',
    `template_nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '模版发送人名称',
    `template_title`       VARCHAR(256) DEFAULT NULL COMMENT '模版标题',
    `template_content`     TEXT         COMMENT '模版内容',
    `template_params`      VARCHAR(1024) DEFAULT NULL COMMENT '模版参数（JSON Map）',
    `send_status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '发送状态',
    `send_time`            DATETIME     DEFAULT NULL COMMENT '发送时间',
    `send_message_id`      VARCHAR(128) DEFAULT NULL COMMENT '发送返回的消息 ID',
    `send_exception`       TEXT         COMMENT '发送异常',
    `creator`              VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`              VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`              BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱日志表';

-- =============================================================
-- 六、社交相关
-- =============================================================

-- -------------------------------------------------------------
-- 18. system_social_user  社交用户表
--     SocialUserDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_social_user` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `type`            TINYINT      NOT NULL DEFAULT 0 COMMENT '社交平台类型',
    `openid`          VARCHAR(64)  NOT NULL COMMENT '社交 openid',
    `token`           VARCHAR(256) DEFAULT NULL COMMENT '社交 token',
    `raw_token_info`  TEXT         COMMENT '原始 Token 数据（JSON）',
    `nickname`        VARCHAR(64)  DEFAULT NULL COMMENT '用户昵称',
    `avatar`          VARCHAR(512) DEFAULT NULL COMMENT '用户头像',
    `raw_user_info`   TEXT         COMMENT '原始用户数据（JSON）',
    `code`            VARCHAR(64)  DEFAULT NULL COMMENT '最后一次认证 code',
    `state`           VARCHAR(64)  DEFAULT NULL COMMENT '最后一次认证 state',
    `creator`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_openid` (`type`, `openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交用户表';

-- -------------------------------------------------------------
-- 19. system_social_user_bind  社交用户绑定表
--     SocialUserBindDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_social_user_bind` (
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`         BIGINT      NOT NULL COMMENT '关联的用户编号',
    `user_type`       TINYINT     NOT NULL DEFAULT 0 COMMENT '用户类型',
    `social_user_id`  BIGINT      NOT NULL COMMENT '社交平台的用户编号',
    `social_type`     TINYINT     NOT NULL DEFAULT 0 COMMENT '社交平台的类型',
    `creator`         VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_social` (`user_id`, `user_type`, `social_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交用户绑定表';

-- -------------------------------------------------------------
-- 20. system_social_client  社交客户端表
--     SocialClientDO extends TenantBaseDO（有 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_social_client` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`           VARCHAR(64)  NOT NULL COMMENT '应用名',
    `social_type`    TINYINT      NOT NULL DEFAULT 0 COMMENT '社交类型',
    `user_type`      TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0=开启 1=禁用）',
    `client_id`      VARCHAR(128) NOT NULL DEFAULT '' COMMENT '客户端 id',
    `client_secret`  VARCHAR(128) NOT NULL DEFAULT '' COMMENT '客户端 Secret',
    `agent_id`       VARCHAR(64)  DEFAULT NULL COMMENT '代理编号',
    `public_key`     TEXT         COMMENT '公钥',
    `tenant_id`      BIGINT       NOT NULL DEFAULT 1 COMMENT '多租户编号',
    `creator`        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交客户端表';

-- =============================================================
-- 七、infra 模块表
-- =============================================================

-- -------------------------------------------------------------
-- 21. infra_config  参数配置表
--     ConfigDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `category`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '参数分类',
    `name`         VARCHAR(128) NOT NULL DEFAULT '' COMMENT '参数名称',
    `config_key`   VARCHAR(128) NOT NULL DEFAULT '' COMMENT '参数键名',
    `value`        VARCHAR(1024) DEFAULT NULL COMMENT '参数键值',
    `type`         TINYINT      NOT NULL DEFAULT 0 COMMENT '参数类型',
    `visible`      BIT(1)       NOT NULL DEFAULT b'1' COMMENT '是否可见',
    `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- -------------------------------------------------------------
-- 22. infra_data_source_config  数据源配置表
--     DataSourceConfigDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_data_source_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键编号',
    `name`         VARCHAR(128) NOT NULL COMMENT '连接名',
    `url`          VARCHAR(1024) NOT NULL COMMENT '数据源连接',
    `username`     VARCHAR(128) NOT NULL DEFAULT '' COMMENT '用户名',
    `password`     VARCHAR(512) NOT NULL DEFAULT '' COMMENT '密码（加密）',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源配置表';

-- -------------------------------------------------------------
-- 23. infra_job  定时任务表
--     JobDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_job` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务编号',
    `name`             VARCHAR(64)  NOT NULL COMMENT '任务名称',
    `status`           TINYINT      NOT NULL DEFAULT 0 COMMENT '任务状态',
    `handler_name`     VARCHAR(128) NOT NULL COMMENT '处理器的名字',
    `handler_param`    VARCHAR(1024) DEFAULT NULL COMMENT '处理器的参数',
    `cron_expression`  VARCHAR(64)  NOT NULL COMMENT 'CRON 表达式',
    `retry_count`      INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    `retry_interval`   INT          NOT NULL DEFAULT 0 COMMENT '重试间隔（毫秒）',
    `monitor_timeout`  INT          DEFAULT NULL COMMENT '监控超时时间（毫秒）',
    `creator`          VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_handler_name` (`handler_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务表';

-- -------------------------------------------------------------
-- 24. infra_job_log  定时任务日志表
--     JobLogDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_job_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志编号',
    `job_id`         BIGINT       NOT NULL COMMENT '任务编号',
    `handler_name`   VARCHAR(128) NOT NULL COMMENT '处理器的名字',
    `handler_param`  VARCHAR(1024) DEFAULT NULL COMMENT '处理器的参数',
    `execute_index`  INT          NOT NULL DEFAULT 1 COMMENT '第几次执行',
    `begin_time`     DATETIME     NOT NULL COMMENT '开始执行时间',
    `end_time`       DATETIME     DEFAULT NULL COMMENT '结束执行时间',
    `duration`       INT          DEFAULT NULL COMMENT '执行时长（毫秒）',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态',
    `result`         VARCHAR(1024) DEFAULT NULL COMMENT '结果数据',
    `creator`        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务日志表';

-- -------------------------------------------------------------
-- 25. infra_file  文件表
--     FileDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_file` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `config_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '配置编号',
    `name`         VARCHAR(255) DEFAULT NULL COMMENT '原文件名',
    `path`         VARCHAR(512) NOT NULL DEFAULT '' COMMENT '路径，即文件名',
    `url`          VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '访问地址',
    `type`         VARCHAR(128) DEFAULT NULL COMMENT '文件 MIME 类型',
    `size`         BIGINT       NOT NULL DEFAULT 0 COMMENT '文件大小',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_path` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- -------------------------------------------------------------
-- 26. infra_file_content  文件内容表
--     FileContentDO extends BaseDO @TenantIgnore（无 tenant_id）
--     content 字段对应 byte[]，使用 LONGBLOB
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_file_content` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `config_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '配置编号',
    `path`         VARCHAR(512) NOT NULL DEFAULT '' COMMENT '路径，即文件名',
    `content`      LONGBLOB     COMMENT '文件内容',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_config_path` (`config_id`, `path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件内容表';

-- -------------------------------------------------------------
-- 27. infra_file_config  文件配置表
--     FileConfigDO extends BaseDO @TenantIgnore（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_file_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置编号',
    `name`         VARCHAR(64)  NOT NULL COMMENT '配置名',
    `storage`      TINYINT      NOT NULL DEFAULT 0 COMMENT '存储器',
    `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `master`       BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否为主配置',
    `config`       TEXT         COMMENT '配置（JSON）',
    `creator`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件配置表';

-- -------------------------------------------------------------
-- 28. infra_api_access_log  API 访问日志表
--     ApiAccessLogDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_api_access_log` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `trace_id`           VARCHAR(64)  DEFAULT NULL COMMENT '链路追踪编号',
    `user_id`            BIGINT       NOT NULL DEFAULT 0 COMMENT '用户编号',
    `user_type`          TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `application_name`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '应用名',
    `request_method`     VARCHAR(16)  NOT NULL DEFAULT '' COMMENT '请求方法名',
    `request_url`        VARCHAR(512) NOT NULL DEFAULT '' COMMENT '访问地址',
    `request_params`     TEXT         COMMENT '请求参数',
    `response_body`      TEXT         COMMENT '响应结果',
    `user_ip`            VARCHAR(50)  DEFAULT NULL COMMENT '用户 IP',
    `user_agent`         VARCHAR(512) DEFAULT NULL COMMENT '浏览器 UA',
    `operate_module`     VARCHAR(64)  DEFAULT NULL COMMENT '操作模块',
    `operate_name`       VARCHAR(64)  DEFAULT NULL COMMENT '操作名',
    `operate_type`       TINYINT      DEFAULT NULL COMMENT '操作分类',
    `begin_time`         DATETIME     NOT NULL COMMENT '开始请求时间',
    `end_time`           DATETIME     DEFAULT NULL COMMENT '结束请求时间',
    `duration`           INT          DEFAULT NULL COMMENT '执行时长（毫秒）',
    `resultCode`         INT          NOT NULL DEFAULT 0 COMMENT '结果码',
    `result_msg`         VARCHAR(512) DEFAULT NULL COMMENT '结果提示',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_begin_time` (`begin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 访问日志表';

-- -------------------------------------------------------------
-- 29. infra_api_error_log  API 异常日志表
--     ApiErrorLogDO extends BaseDO（无 tenant_id）
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `infra_api_error_log` (
    `id`                          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`                     BIGINT       NOT NULL DEFAULT 0 COMMENT '用户编号',
    `trace_id`                    VARCHAR(64)  DEFAULT NULL COMMENT '链路追踪编号',
    `user_type`                   TINYINT      NOT NULL DEFAULT 0 COMMENT '用户类型',
    `application_name`            VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '应用名',
    `request_method`              VARCHAR(16)  NOT NULL DEFAULT '' COMMENT '请求方法名',
    `request_url`                 VARCHAR(512) NOT NULL DEFAULT '' COMMENT '访问地址',
    `request_params`              TEXT         COMMENT '请求参数',
    `user_ip`                     VARCHAR(50)  DEFAULT NULL COMMENT '用户 IP',
    `user_agent`                  VARCHAR(512) DEFAULT NULL COMMENT '浏览器 UA',
    `exception_time`              DATETIME     NOT NULL COMMENT '异常发生时间',
    `exception_name`              VARCHAR(128) DEFAULT NULL COMMENT '异常名',
    `exception_message`           TEXT         COMMENT '异常导致的消息',
    `exception_root_cause_message` TEXT        COMMENT '异常导致的根消息',
    `exception_stack_trace`       LONGTEXT     COMMENT '异常的栈轨迹',
    `exception_class_name`        VARCHAR(128) DEFAULT NULL COMMENT '异常发生的类全名',
    `exception_file_name`         VARCHAR(64)  DEFAULT NULL COMMENT '异常发生的类文件',
    `exception_method_name`       VARCHAR(64)  DEFAULT NULL COMMENT '异常发生的方法名',
    `exception_line_number`       INT          DEFAULT NULL COMMENT '异常发生的方法所在行',
    `process_status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '处理状态',
    `process_time`                DATETIME     DEFAULT NULL COMMENT '处理时间',
    `process_user_id`             BIGINT       DEFAULT NULL COMMENT '处理用户编号',
    `creator`                     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`                 DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`                     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`                 DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exception_time` (`exception_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 异常日志表';

-- =============================================================
-- 初始数据
-- =============================================================

-- 默认 OAuth2 客户端（修复启动后无可用 client 的问题）
-- client_id='default'，secret='admin123'，授予所有授权类型
INSERT IGNORE INTO `system_oauth2_client` (
    `id`, `client_id`, `secret`, `name`, `logo`, `description`, `status`,
    `access_token_validity_seconds`, `refresh_token_validity_seconds`,
    `redirect_uris`, `authorized_grant_types`, `scopes`, `auto_approve_scopes`,
    `authorities`, `resource_ids`, `additional_information`,
    `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
    1, 'default', 'admin123', '芋道源码', NULL, '默认 OAuth2 客户端', 0,
    1800, 2592000,
    '["https://www.yudao.com"]',
    '["password","authorization_code","implicit","refresh_token"]',
    '["user.read","user.write"]',
    '["user.read"]',
    '["ROLE_USER"]',
    '[]',
    NULL,
    '1', NOW(), '1', NOW(), b'0'
);

-- =============================================================
-- 文件结束
-- =============================================================
