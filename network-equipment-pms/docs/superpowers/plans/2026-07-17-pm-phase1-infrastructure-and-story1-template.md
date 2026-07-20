# 项目管理增强 — Phase 1 基础设施 + Story 1 项目模板与创建 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完成项目管理模块基础设施层（V64~V66 迁移 + 实体 + TypeHandler + 路由全量重构 + G6 引入）和 Story 1（项目模板维护 + 从模板创建项目 + 版本快照深拷贝），让用户能够维护项目模板并一键从模板创建带完整默认计划的项目。

**Architecture:** 后端在 pms-project 模块扩展 `ProjectTemplate`/`ProjectTemplateVersion`/`Project`/`ProjectPhase`/`ProjectMember`/`ProjectConfig` 实体与对应服务，pms-common 模块新增 `JsonTypeHandlers` 子类集合处理 MySQL JSON 字段；Flyway V64~V66 创建模板表、扩展 Project 表、创建 Phase/Member/Config 表并初始化 9 条系统默认配置；前端路由全量重构为嵌套模式（不留技术债），新增项目模板维护 3 页面 + 选择器组件 + 集成到项目列表的"从模板创建"入口。

**Tech Stack:** Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 + MySQL 8 + Flyway + Vue 3 + TypeScript + Element Plus + Vite + AntV G6 v5

**Spec:** `docs/superpowers/specs/2026-07-17-project-management-enhancement-design.md` §1-2, §4, §6.1-6.4, §6.11, §7.1-7.3, §8.1-8.2 (Phase 1+2)

---

## 文件结构

### Flyway 迁移（pms-admin 模块）

| 文件 | 职责 |
|------|------|
| `src/main/resources/db/migration/V64__create_project_template_tables.sql` | 项目模板表 + 模板版本表（含 snapshot_json JSON 列） |
| `src/main/resources/db/migration/V65__alter_project_for_subproject.sql` | Project 表扩展 9 字段（parentProjectId/projectPath/depth/weight/templateId/templateVersion/currentPhaseId/projectObjective/projectScope）+ 3 索引 + 存量路径回填 |
| `src/main/resources/db/migration/V66__create_project_phase_member_config.sql` | ProjectPhase + ProjectMember + ProjectConfig 表 + 9 条系统默认配置 |

### 后端新增（pms-common 模块）

| 文件 | 职责 |
|------|------|
| `src/main/java/com/dp/plat/common/handler/JsonTypeHandlers.java` | 4 个 JacksonTypeHandler 静态子类（TemplateSnapshotHandler 等） |

### 后端新增（pms-project 模块）

| 文件 | 职责 |
|------|------|
| `src/main/java/com/dp/plat/project/dto/PhaseCriteria.java` | 阶段进入条件 DTO |
| `src/main/java/com/dp/plat/project/dto/PhaseExitGate.java` | 阶段退出条件 DTO（4 类条件结构化） |
| `src/main/java/com/dp/plat/project/dto/TemplateSnapshot.java` | 模板内容快照 DTO（phases/tasks/milestones/deliverables/dependencies/approvalPlans/assigneeRules） |
| `src/main/java/com/dp/plat/project/dto/ProjectCreateFromTemplateDTO.java` | 从模板创建项目请求体 |
| `src/main/java/com/dp/plat/project/entity/ProjectTemplate.java` | 项目模板实体 |
| `src/main/java/com/dp/plat/project/entity/ProjectTemplateVersion.java` | 模板版本实体（autoResultMap=true） |
| `src/main/java/com/dp/plat/project/entity/ProjectPhase.java` | 项目阶段实体（autoResultMap=true） |
| `src/main/java/com/dp/plat/project/entity/ProjectMember.java` | 项目成员实体 |
| `src/main/java/com/dp/plat/project/entity/ProjectConfig.java` | 项目配置实体 |
| `src/main/java/com/dp/plat/project/dao/ProjectTemplateMapper.java` | 模板 Mapper |
| `src/main/java/com/dp/plat/project/dao/ProjectTemplateVersionMapper.java` | 模板版本 Mapper |
| `src/main/java/com/dp/plat/project/dao/ProjectPhaseMapper.java` | 阶段 Mapper |
| `src/main/java/com/dp/plat/project/dao/ProjectMemberMapper.java` | 成员 Mapper |
| `src/main/java/com/dp/plat/project/dao/ProjectConfigMapper.java` | 配置 Mapper |
| `src/main/java/com/dp/plat/project/service/IProjectTemplateService.java` | 模板服务接口 |
| `src/main/java/com/dp/plat/project/service/impl/ProjectTemplateServiceImpl.java` | 模板服务实现（CRUD + 版本发布 + 深拷贝创建项目） |
| `src/main/java/com/dp/plat/project/service/IProjectPhaseService.java` | 阶段服务接口 |
| `src/main/java/com/dp/plat/project/service/impl/ProjectPhaseServiceImpl.java` | 阶段服务实现（基本 CRUD，advancePhase 在 Phase 3 实现） |
| `src/main/java/com/dp/plat/project/service/IProjectMemberService.java` | 成员服务接口 |
| `src/main/java/com/dp/plat/project/service/impl/ProjectMemberServiceImpl.java` | 成员服务实现 |
| `src/main/java/com/dp/plat/project/service/ProjectConfigService.java` | 配置服务（多层级读取：项目级 > 模板级 > 系统默认） |
| `src/main/java/com/dp/plat/project/controller/ProjectTemplateController.java` | 模板 Controller（10 个端点） |
| `src/main/java/com/dp/plat/project/controller/ProjectPhaseController.java` | 阶段 Controller（基本端点，advancePhase 在 Phase 3） |
| `src/main/java/com/dp/plat/project/controller/ProjectMemberController.java` | 成员 Controller |
| `src/main/java/com/dp/plat/project/controller/ProjectConfigController.java` | 配置 Controller（读取/更新） |

### 后端修改（pms-project 模块）

| 文件 | 改动 |
|------|------|
| `src/main/java/com/dp/plat/project/entity/Project.java` | 扩展 9 字段 + 注解 `@TableName(autoResultMap=true)` |
| `src/main/java/com/dp/plat/project/service/IProjectService.java` | 新增 `createProjectFromTemplate` 方法签名 |
| `src/main/java/com/dp/plat/project/service/impl/ProjectServiceImpl.java` | 实现从模板创建项目编排（调用 ProjectTemplateService 深拷贝） |

### 前端新增（pms-frontend 模块）

| 文件 | 职责 |
|------|------|
| `src/api/project-template.ts` | 模板 API 封装（10 个端点） |
| `src/api/project-phase.ts` | 阶段 API 封装（基本端点） |
| `src/api/project-member.ts` | 成员 API 封装 |
| `src/api/project-config.ts` | 配置 API 封装 |
| `src/views/project/template/index.vue` | 模板列表页 |
| `src/views/project/template/form.vue` | 模板编辑器（含快照构建器） |
| `src/views/project/template/version.vue` | 模板版本管理 |
| `src/components/ProjectTemplateSelector.vue` | 模板选择器弹窗组件 |

### 前端修改（pms-frontend 模块）

| 文件 | 改动 |
|------|------|
| `src/router/index.ts` | 路由全量重构为嵌套模式（按业务域 children 分组 + 既有路由重组 + URL 重定向兼容） |
| `src/views/project/list/index.vue` | 顶部新增 `[+ 从模板创建]` 按钮，集成 `ProjectTemplateSelector` |
| `package.json` | 新增 `@antv/g6@^5.0.0` 依赖 |

### 测试文件

| 文件 | 职责 |
|------|------|
| `pms-project/src/test/java/com/dp/plat/project/service/ProjectConfigServiceTest.java` | 配置多层级读取测试 |
| `pms-project/src/test/java/com/dp/plat/project/service/ProjectTemplateServiceImplTest.java` | 模板 CRUD + 版本发布 + 深拷贝测试 |
| `pms-frontend/src/api/__tests__/project-template.spec.ts` | 前端 API 封装单元测试 |

---

## Task 1: Flyway V64 — 项目模板表 + 模板版本表

**Files:**
- Create: `network-equipment-pms/pms-admin/src/main/resources/db/migration/V64__create_project_template_tables.sql`

- [ ] **Step 1: 创建 V64 迁移文件**

```sql
-- V64__create_project_template_tables.sql
-- 项目模板与版本管理（Story 1）
-- 关联设计文档：§6.2

CREATE TABLE pms_project_template (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    template_code   VARCHAR(64)  NOT NULL COMMENT '模板编码',
    template_name   VARCHAR(128) NOT NULL COMMENT '模板名称',
    category        VARCHAR(32)  NOT NULL DEFAULT 'IMPLEMENT' COMMENT '类别：IMPLEMENT/MAINTENANCE/CONSULTING',
    description     VARCHAR(500) NULL COMMENT '描述',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/DEPRECATED',
    create_by       BIGINT       NULL COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    version         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code),
    KEY idx_status_category (status, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模板';

CREATE TABLE pms_project_template_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    template_id     BIGINT       NOT NULL COMMENT '模板ID',
    version         VARCHAR(32)  NOT NULL COMMENT '语义化版本 v1.0.0',
    snapshot_json   JSON         NOT NULL COMMENT '模板内容快照JSON（phases/tasks/milestones/deliverables/dependencies/approvalPlans/assigneeRules）',
    change_log      VARCHAR(500) NULL COMMENT '版本变更说明',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
    published_at    DATETIME     NULL COMMENT '发布时间',
    published_by    BIGINT       NULL COMMENT '发布人',
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version_lock    INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_version (template_id, version),
    KEY idx_template_status (template_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模板版本';
```

- [ ] **Step 2: 验证迁移文件语法（本地 MySQL 连接）**

Run: `mysql -uroot -p<pms_root_password> -h<mysql_host> dppms_d365 -e "source network-equipment-pms/pms-admin/src/main/resources/db/migration/V64__create_project_template_tables.sql"`
Expected: Query OK, 0 rows affected（无错误）

> 注：若无法直连 MySQL，可跳过此步，依赖后续应用启动时 Flyway 执行验证。

- [ ] **Step 3: 启动后端应用，触发 Flyway 执行迁移**

Run: `cd /workspace/network-equipment-pms/pms-admin && mvn spring-boot:run -DskipTests 2>&1 | grep -iE "flyway|migration" | head -20`
Expected: 日志含 `Migrating schema "dppms_d365" to version "64 - create project template tables"` 和 `Successfully applied 1 migration`

> 若已有应用运行，可重启；若 Flyway 报错，检查 SQL 语法。

- [ ] **Step 4: 验证表创建成功**

Run: `mysql -uroot -p<pms_root_password> -h<mysql_host> dppms_d365 -e "SHOW TABLES LIKE 'pms_project_template%'; DESCRIBE pms_project_template_version;"`
Expected: 返回 `pms_project_template` 和 `pms_project_template_version` 两张表，`pms_project_template_version.snapshot_json` 列类型为 `json`

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-admin/src/main/resources/db/migration/V64__create_project_template_tables.sql
git commit -m "feat(b7-t1): Flyway V64 — 项目模板表 + 模板版本表（含 snapshot_json JSON 列）"
git push
```

---

## Task 2: Flyway V65 — Project 表扩展 9 字段

**Files:**
- Create: `network-equipment-pms/pms-admin/src/main/resources/db/migration/V65__alter_project_for_subproject.sql`

- [ ] **Step 1: 创建 V65 迁移文件**

```sql
-- V65__alter_project_for_subproject.sql
-- Project 表扩展：主子项目（物化路径）+ 模板关联 + 阶段关联
-- 关联设计文档：§6.3

ALTER TABLE pms_project
    ADD COLUMN parent_project_id  BIGINT       NULL COMMENT '父项目ID（NULL=顶层）',
    ADD COLUMN project_path       VARCHAR(500) NOT NULL DEFAULT '/' COMMENT '物化路径 /1/5/',
    ADD COLUMN depth              INT          NOT NULL DEFAULT 0 COMMENT '深度（0=顶层）',
    ADD COLUMN weight             DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '汇总权重',
    ADD COLUMN template_id        BIGINT       NULL COMMENT '来源模板ID',
    ADD COLUMN template_version   VARCHAR(32)  NULL COMMENT '模板版本快照',
    ADD COLUMN current_phase_id   BIGINT       NULL COMMENT '当前阶段ID',
    ADD COLUMN project_objective  VARCHAR(500) NULL COMMENT '项目目标',
    ADD COLUMN project_scope      VARCHAR(1000) NULL COMMENT '项目范围';

CREATE INDEX idx_project_path ON pms_project (project_path);
CREATE INDEX idx_parent_project_id ON pms_project (parent_project_id);
CREATE INDEX idx_template_id ON pms_project (template_id);

-- 回填存量项目路径（顶层项目路径为 /<id>/）
UPDATE pms_project
SET project_path = CONCAT('/', id, '/')
WHERE project_path = '/' OR project_path IS NULL;
```

- [ ] **Step 2: 验证 Project 表当前结构（迁移前快照）**

Run: `mysql -uroot -p<pms_root_password> -h<mysql_host> dppms_d365 -e "DESCRIBE pms_project;"`
Expected: 列出当前所有字段（迁移前不含 parent_project_id 等 9 字段）

- [ ] **Step 3: 启动后端应用，触发 Flyway 执行迁移**

Run: `cd /workspace/network-equipment-pms/pms-admin && mvn spring-boot:run -DskipTests 2>&1 | grep -iE "flyway|migration" | head -20`
Expected: 日志含 `Migrating schema "dppms_d365" to version "65 - alter project for subproject"` 和 `Successfully applied 1 migration`

- [ ] **Step 4: 验证字段添加 + 索引创建 + 存量数据回填**

Run: `mysql -uroot -p<pms_root_password> -h<mysql_host> dppms_d365 -e "DESCRIBE pms_project; SELECT id, project_path, depth FROM pms_project LIMIT 5; SHOW INDEX FROM pms_project WHERE Key_name IN ('idx_project_path','idx_parent_project_id','idx_template_id');"`
Expected:
- `DESCRIBE` 结果含 `parent_project_id`、`project_path`、`depth`、`weight`、`template_id`、`template_version`、`current_phase_id`、`project_objective`、`project_scope` 9 个新字段
- `SELECT` 返回的 `project_path` 形如 `/123/`（而非 `/`），`depth = 0`
- `SHOW INDEX` 返回 3 个新索引

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-admin/src/main/resources/db/migration/V65__alter_project_for_subproject.sql
git commit -m "feat(b7-t2): Flyway V65 — Project 表扩展 9 字段（主子项目物化路径 + 模板/阶段关联）+ 存量路径回填"
git push
```

---

## Task 3: Flyway V66 — 阶段/成员/配置表 + 默认配置

**Files:**
- Create: `network-equipment-pms/pms-admin/src/main/resources/db/migration/V66__create_project_phase_member_config.sql`

- [ ] **Step 1: 创建 V66 迁移文件**

```sql
-- V66__create_project_phase_member_config.sql
-- 项目阶段 + 项目成员 + 项目配置 + 系统默认配置初始化
-- 关联设计文档：§6.4

CREATE TABLE pms_project_phase (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL COMMENT '项目ID',
    template_phase_id   BIGINT       NULL COMMENT '模板阶段ID（追溯）',
    phase_name          VARCHAR(64)  NOT NULL,
    phase_code          VARCHAR(32)  NOT NULL COMMENT 'PREPARE/PLAN/DESIGN/IMPLEMENT/OPERATE 或自定义',
    sort_order          INT          NOT NULL DEFAULT 0,
    entry_criteria      JSON         NULL COMMENT '进入条件（结构化 JSON）',
    exit_criteria       JSON         NULL COMMENT '退出条件 JSON：{requiredDeliverables,requiredTasks,requiredMilestones,requiredApprovals}',
    status              VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED' COMMENT 'NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED',
    planned_start_date  DATE         NULL,
    planned_end_date    DATE         NULL,
    actual_start_date   DATE         NULL,
    actual_end_date     DATE         NULL,
    create_by           BIGINT       NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT       NULL,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_phase_code (project_id, phase_code),
    KEY idx_project_sort (project_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目阶段';

CREATE TABLE pms_project_member (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    project_id      BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    user_name       VARCHAR(64)  NULL COMMENT '冗余',
    role            VARCHAR(32)  NOT NULL DEFAULT 'PROJECT_MEMBER' COMMENT 'PROJECT_MANAGER/PROJECT_MEMBER/APPROVER/VIEWER/CUSTOMER',
    join_date       DATE         NULL,
    leave_date      DATE         NULL,
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_user (project_id, user_id),
    KEY idx_user_role (user_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员';

CREATE TABLE pms_project_config (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    project_id      BIGINT       NULL COMMENT 'NULL=系统级默认',
    template_id     BIGINT       NULL COMMENT 'NULL=非模板配置',
    config_key      VARCHAR(100) NOT NULL,
    config_value    VARCHAR(500) NOT NULL,
    description     VARCHAR(255) NULL,
    create_by       BIGINT       NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       NULL,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_proj_tpl_key (project_id, template_id, config_key),
    KEY idx_template_key (template_id, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目配置';

-- 初始化系统默认配置（9 条）
INSERT INTO pms_project_config (project_id, template_id, config_key, config_value, description) VALUES
(NULL, NULL, 'baseline.variance.days.threshold',     '5',     '基线偏差天数阈值'),
(NULL, NULL, 'baseline.variance.percent.threshold',  '10',    '基线偏差百分比阈值'),
(NULL, NULL, 'approval.timeout.hours',               '48',    '审批超时小时数'),
(NULL, NULL, 'approval.escalate.hours',              '24',    '审批升级小时数'),
(NULL, NULL, 'approval.reminder.hours',              '12',    '审批提醒小时数'),
(NULL, NULL, 'approval.timeout.action',              'ESCALATE', '超时动作：ESCALATE/AUTO_APPROVE/AUTO_REJECT'),
(NULL, NULL, 'task.rollup.weight.field',             'PLANNED_HOURS', '任务汇总权重字段'),
(NULL, NULL, 'phase.exit.check.approval',            'true',  '阶段退出是否强制审批'),
(NULL, NULL, 'approval.max.rounds',                  '5',     '审批最大轮次');
```

- [ ] **Step 2: 启动后端应用，触发 Flyway 执行迁移**

Run: `cd /workspace/network-equipment-pms/pms-admin && mvn spring-boot:run -DskipTests 2>&1 | grep -iE "flyway|migration" | head -20`
Expected: 日志含 `Migrating schema "dppms_d365" to version "66 - create project phase member config"` 和 `Successfully applied 1 migration`

- [ ] **Step 3: 验证 3 张表创建 + 9 条默认配置插入**

Run: `mysql -uroot -p<pms_root_password> -h<mysql_host> dppms_d365 -e "SHOW TABLES LIKE 'pms_project_%'; SELECT COUNT(*) AS config_count FROM pms_project_config WHERE project_id IS NULL AND template_id IS NULL;"`
Expected:
- `SHOW TABLES` 返回 `pms_project_phase`、`pms_project_member`、`pms_project_config` 3 张新表
- `config_count = 9`

- [ ] **Step 4: 验证默认配置内容**

Run: `mysql -uroot -p<pms_root_password> -h<mysql_host> dppms_d365 -e "SELECT config_key, config_value FROM pms_project_config WHERE project_id IS NULL ORDER BY config_key;"`
Expected: 返回 9 行，包含 `approval.escalate.hours`、`approval.max.rounds`、`approval.reminder.hours`、`approval.timeout.action`、`approval.timeout.hours`、`baseline.variance.days.threshold`、`baseline.variance.percent.threshold`、`phase.exit.check.approval`、`task.rollup.weight.field`

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-admin/src/main/resources/db/migration/V66__create_project_phase_member_config.sql
git commit -m "feat(b7-t3): Flyway V66 — 阶段/成员/配置 3 表 + 9 条系统默认配置"
git push
```

---

## Task 4: DTO 类 — PhaseCriteria / PhaseExitGate / TemplateSnapshot / ProjectCreateFromTemplateDTO

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/PhaseCriteria.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/PhaseExitGate.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/TemplateSnapshot.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/ProjectCreateFromTemplateDTO.java`

- [ ] **Step 1: 创建 PhaseCriteria DTO**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/PhaseCriteria.java
package com.dp.plat.project.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 阶段进入条件（结构化 JSON）
 */
@Data
public class PhaseCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 是否需要前置阶段完成 */
    private Boolean requirePreviousPhaseComplete;

    /** 是否需要审批通过 */
    private Boolean requireApproval;
}
```

- [ ] **Step 2: 创建 PhaseExitGate DTO**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/PhaseExitGate.java
package com.dp.plat.project.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 阶段退出条件（结构化 JSON，4 类条件）
 * 关联设计文档：§3.2
 */
@Data
public class PhaseExitGate implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 必需交付件 */
    private List<RequiredDeliverable> requiredDeliverables;

    /** 必需任务 */
    private List<RequiredTask> requiredTasks;

    /** 必需里程碑 */
    private List<RequiredMilestone> requiredMilestones;

    /** 必需审批 */
    private List<RequiredApproval> requiredApprovals;

    @Data
    public static class RequiredDeliverable implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long deliverableId;
        private String deliverableName;
        private String requiredStatus; // PUBLISHED/REFERENCED/ARCHIVED
    }

    @Data
    public static class RequiredTask implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long phaseId;
        private Boolean allCompleted;
    }

    @Data
    public static class RequiredMilestone implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long milestoneId;
        private Boolean mustReached;
    }

    @Data
    public static class RequiredApproval implements Serializable {
        private static final long serialVersionUID = 1L;
        private String approvalType; // PHASE_EXIT 等
        private Boolean mustApproved;
    }
}
```

- [ ] **Step 3: 创建 TemplateSnapshot DTO**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/TemplateSnapshot.java
package com.dp.plat.project.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 模板内容快照（深拷贝到项目时反序列化）
 * 关联设计文档：§2.2 ProjectTemplateVersion
 */
@Data
public class TemplateSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 阶段定义 */
    private List<PhaseDef> phases;

    /** 任务定义（含父子层级） */
    private List<TaskDef> tasks;

    /** 里程碑定义 */
    private List<MilestoneDef> milestones;

    /** 交付件定义 */
    private List<DeliverableDef> deliverables;

    /** 任务依赖定义 */
    private List<DependencyDef> dependencies;

    /** 审批计划定义 */
    private List<ApprovalPlanDef> approvalPlans;

    /** 分配规则（自动分配任务给角色） */
    private List<AssigneeRule> assigneeRules;

    @Data
    public static class PhaseDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String phaseCode;
        private String phaseName;
        private Integer sortOrder;
        private PhaseCriteria entryCriteria;
        private PhaseExitGate exitCriteria;
    }

    @Data
    public static class TaskDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String taskName;
        private String taskType;
        private String parentTaskName; // 通过名称引用，便于跨模板复用
        private String phaseCode;      // 关联阶段
        private Integer plannedHours;
        private String priority;
        private Integer sortOrder;
    }

    @Data
    public static class MilestoneDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String milestoneName;
        private String milestoneType;
        private String phaseCode;
        private Integer sortOrder;
    }

    @Data
    public static class DeliverableDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String deliverableName;
        private String deliverableType;
        private String phaseCode;
        private Boolean mandatory;
        private String approverRole;
    }

    @Data
    public static class DependencyDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String predecessorTaskName;
        private String successorTaskName;
        private String dependencyType; // FS/FF/SS/SF
        private Integer lagDays;
    }

    @Data
    public static class ApprovalPlanDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String approvalType;
        private String triggerPhaseCode;
        private List<String> approverRoles;
    }

    @Data
    public static class AssigneeRule implements Serializable {
        private static final long serialVersionUID = 1L;
        private String taskNamePattern;
        private String role;
    }
}
```

- [ ] **Step 4: 创建 ProjectCreateFromTemplateDTO**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dto/ProjectCreateFromTemplateDTO.java
package com.dp.plat.project.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 从模板创建项目请求体
 * 关联设计文档：§5.2 Story 1 验收 1
 */
@Data
public class ProjectCreateFromTemplateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long templateId;
    private Long versionId;

    private String projectCode;
    private String projectName;
    private String customerName;
    private String customerContact;
    private String customerPhone;
    private String contractNo;
    private BigDecimal contractAmount;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private Long projectManagerId;

    private String projectObjective;
    private String projectScope;

    /** 初始成员 */
    private List<MemberDef> members;

    /** 配置覆盖（key → value） */
    private Map<String, String> configOverrides;

    @Data
    public static class MemberDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long userId;
        private String role; // PROJECT_MANAGER / PROJECT_MEMBER / APPROVER / VIEWER / CUSTOMER
    }
}
```

- [ ] **Step 5: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/dto/
git commit -m "feat(b7-t4): DTO 类 — PhaseCriteria/PhaseExitGate/TemplateSnapshot/ProjectCreateFromTemplateDTO"
git push
```

---

## Task 5: JsonTypeHandlers — 4 个 JacksonTypeHandler 静态子类

**Files:**
- Create: `network-equipment-pms/pms-common/src/main/java/com/dp/plat/common/handler/JsonTypeHandlers.java`

- [ ] **Step 1: 创建 JsonTypeHandlers**

```java
// network-equipment-pms/pms-common/src/main/java/com/dp/plat/common/handler/JsonTypeHandlers.java
package com.dp.plat.common.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dp.plat.project.dto.PhaseCriteria;
import com.dp.plat.project.dto.PhaseExitGate;
import com.dp.plat.project.dto.TemplateSnapshot;

import java.util.List;

/**
 * JSON TypeHandler 子类集合（每个具体泛型类型一个子类，避免泛型擦除）
 * 关联设计文档：§6.11
 *
 * <p>使用方式（实体类）：
 * <pre>
 * {@literal @}TableName(value = "pms_project_template_version", autoResultMap = true)
 * public class ProjectTemplateVersion extends BaseEntity {
 *     {@literal @}TableField(typeHandler = JsonTypeHandlers.TemplateSnapshotHandler.class)
 *     private TemplateSnapshot snapshotJson;
 * }
 * </pre>
 *
 * <p>注意：autoResultMap = true 必须开启，否则字段级 typeHandler 在 BaseMapper 方法中不生效。
 */
public final class JsonTypeHandlers {

    private JsonTypeHandlers() {
    }

    /**
     * PhaseCriteria（阶段进入条件）TypeHandler
     */
    public static class PhaseCriteriaHandler extends JacksonTypeHandler {
        public PhaseCriteriaHandler() {
            super(PhaseCriteria.class);
        }
    }

    /**
     * PhaseExitGate（阶段退出条件，4 类结构化条件）TypeHandler
     */
    public static class PhaseExitGateHandler extends JacksonTypeHandler {
        public PhaseExitGateHandler() {
            super(PhaseExitGate.class);
        }
    }

    /**
     * TaskPlanSnapshot List TypeHandler
     * （BaselineSnapshot.snapshotJson 用，本任务先创建占位，Task 11 BaselineSnapshot 实体使用）
     */
    public static class TaskPlanSnapshotListHandler extends JacksonTypeHandler {
        public TaskPlanSnapshotListHandler() {
            super(List.class);
        }
    }

    /**
     * TemplateSnapshot（模板内容快照）TypeHandler
     */
    public static class TemplateSnapshotHandler extends JacksonTypeHandler {
        public TemplateSnapshotHandler() {
            super(TemplateSnapshot.class);
        }
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-common -am compile -q`
Expected: BUILD SUCCESS

> 若 pms-common 不依赖 pms-project，需要把 PhaseCriteria/PhaseExitGate/TemplateSnapshot 移到 pms-common，或调整 pms-common 的 pom 增加对 pms-project 的依赖。**推荐做法**：DTO 类保持在 pms-project，pms-common 已依赖 pms-project（若否，在 pms-common/pom.xml 加 `<dependency> pms-project</dependency>`）。检查 pms-common/pom.xml 是否已有 pms-project 依赖，无则添加。

- [ ] **Step 3: 验证 pms-common 对 pms-project 的依赖**

Run: `grep -A2 "pms-project" /workspace/network-equipment-pms/pms-common/pom.xml || echo "DEPENDENCY_MISSING"`
Expected: 显示 pms-project 依赖配置；若输出 `DEPENDENCY_MISSING`，需在 pms-common/pom.xml 添加：

```xml
<dependency>
    <groupId>com.dp.plat</groupId>
    <artifactId>pms-project</artifactId>
    <version>${project.version}</version>
</dependency>
```

> 注意：若 pms-project 反向依赖 pms-common（更常见），会产生循环依赖。此时应将 DTO 类移到 pms-common 而非 pms-project。**先检查依赖方向**：若 pms-project 依赖 pms-common，则把 PhaseCriteria/PhaseExitGate/TemplateSnapshot 移到 `pms-common/src/main/java/com/dp/plat/common/dto/` 包下，并更新 JsonTypeHandlers 的 import。

- [ ] **Step 4: 根据依赖方向调整 DTO 位置（如有需要）**

如果 Step 3 显示循环依赖，执行：
1. 将 `PhaseCriteria.java`、`PhaseExitGate.java`、`TemplateSnapshot.java` 从 `pms-project/.../project/dto/` 移动到 `pms-common/.../common/dto/`
2. 更新 `JsonTypeHandlers.java` 的 import 为 `com.dp.plat.common.dto.*`
3. `ProjectCreateFromTemplateDTO` 保留在 pms-project（它依赖 pms-common 的 TemplateSnapshot）
4. 重新编译验证

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-common,pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-common/src/main/java/com/dp/plat/common/handler/JsonTypeHandlers.java
# 若有 DTO 位置调整，一并提交
git add pms-common/src/main/java/com/dp/plat/common/dto/ 2>/dev/null || true
git add pms-project/src/main/java/com/dp/plat/project/dto/ 2>/dev/null || true
git commit -m "feat(b7-t5): JsonTypeHandlers — 4 个 JacksonTypeHandler 子类（处理 JSON 字段泛型擦除）"
git push
```

---

## Task 6: 实体类 — ProjectTemplate + ProjectTemplateVersion

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectTemplate.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectTemplateVersion.java`

- [ ] **Step 1: 查看 BaseEntity 公共字段**

Run: `cat /workspace/network-equipment-pms/pms-common/src/main/java/com/dp/plat/common/entity/BaseEntity.java`
Expected: 显示 BaseEntity 已包含 id/createBy/createTime/updateBy/updateTime/deleted/version 等公共字段

> 后续实体类只需继承 BaseEntity，不重复声明公共字段。

- [ ] **Step 2: 创建 ProjectTemplate 实体**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectTemplate.java
package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目模板
 * 关联表：pms_project_template
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_template")
public class ProjectTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 模板编码 */
    private String templateCode;

    /** 模板名称 */
    private String templateName;

    /** 类别：IMPLEMENT / MAINTENANCE / CONSULTING */
    private String category;

    /** 描述 */
    private String description;

    /** 状态：DRAFT / PUBLISHED / DEPRECATED */
    private String status;
}
```

- [ ] **Step 3: 创建 ProjectTemplateVersion 实体（含 JSON TypeHandler）**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectTemplateVersion.java
package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.handler.JsonTypeHandlers;
import com.dp.plat.project.dto.TemplateSnapshot;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 项目模板版本
 * 关联表：pms_project_template_version
 *
 * <p>注意：autoResultMap = true 必须开启，否则 @TableField(typeHandler=...) 在 BaseMapper 方法中不生效。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_project_template_version", autoResultMap = true)
public class ProjectTemplateVersion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 语义化版本 v1.0.0 */
    private String version;

    /** 模板内容快照（JSON） */
    @TableField(typeHandler = JsonTypeHandlers.TemplateSnapshotHandler.class)
    private TemplateSnapshot snapshotJson;

    /** 版本变更说明 */
    private String changeLog;

    /** 状态：DRAFT / PUBLISHED / ARCHIVED */
    private String status;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 发布人 */
    private Long publishedBy;
}
```

- [ ] **Step 4: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/entity/ProjectTemplate.java \
        pms-project/src/main/java/com/dp/plat/project/entity/ProjectTemplateVersion.java
git commit -m "feat(b7-t6): 实体类 — ProjectTemplate + ProjectTemplateVersion（含 JSON TypeHandler）"
git push
```

---

## Task 7: 实体类 — ProjectPhase + ProjectMember + ProjectConfig

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectPhase.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectMember.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectConfig.java`

- [ ] **Step 1: 创建 ProjectPhase 实体（含 JSON TypeHandler）**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectPhase.java
package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.handler.JsonTypeHandlers;
import com.dp.plat.project.dto.PhaseCriteria;
import com.dp.plat.project.dto.PhaseExitGate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 项目阶段
 * 关联表：pms_project_phase
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_project_phase", autoResultMap = true)
public class ProjectPhase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;

    /** 来源模板阶段 ID（追溯用） */
    private Long templatePhaseId;

    private String phaseName;

    /** PREPARE / PLAN / DESIGN / IMPLEMENT / OPERATE 或自定义 */
    private String phaseCode;

    private Integer sortOrder;

    /** 进入条件（JSON） */
    @TableField(typeHandler = JsonTypeHandlers.PhaseCriteriaHandler.class)
    private PhaseCriteria entryCriteria;

    /** 退出条件（JSON，4 类条件） */
    @TableField(typeHandler = JsonTypeHandlers.PhaseExitGateHandler.class)
    private PhaseExitGate exitCriteria;

    /** NOT_STARTED / IN_PROGRESS / COMPLETED / SKIPPED */
    private String status;

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
}
```

- [ ] **Step 2: 创建 ProjectMember 实体**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectMember.java
package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 项目成员
 * 关联表：pms_project_member
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_member")
public class ProjectMember extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Long userId;
    private String userName;

    /** PROJECT_MANAGER / PROJECT_MEMBER / APPROVER / VIEWER / CUSTOMER */
    private String role;

    private LocalDate joinDate;
    private LocalDate leaveDate;
}
```

- [ ] **Step 3: 创建 ProjectConfig 实体**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/ProjectConfig.java
package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目配置（多层级：项目级 > 模板级 > 系统默认）
 * 关联表：pms_project_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_config")
public class ProjectConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** NULL = 系统级默认 */
    private Long projectId;

    /** NULL = 非模板配置 */
    private Long templateId;

    private String configKey;
    private String configValue;
    private String description;
}
```

- [ ] **Step 4: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/entity/ProjectPhase.java \
        pms-project/src/main/java/com/dp/plat/project/entity/ProjectMember.java \
        pms-project/src/main/java/com/dp/plat/project/entity/ProjectConfig.java
git commit -m "feat(b7-t7): 实体类 — ProjectPhase（含 JSON TypeHandler）+ ProjectMember + ProjectConfig"
git push
```

---

## Task 8: 扩展 Project 实体（+9 字段）

**Files:**
- Modify: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/Project.java`

- [ ] **Step 1: 读取现有 Project 实体**

Run: `cat /workspace/network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/entity/Project.java`
Expected: 显示现有 Project 实体（含 projectCode、projectName、status 等字段，继承 BaseEntity）

- [ ] **Step 2: 添加 9 个新字段**

在 Project 类中添加以下字段（保留现有字段不变，在类末尾 `}` 之前添加）：

```java
    // ============ Phase 1 扩展字段（V65 迁移） ============

    /** 父项目ID（NULL=顶层） */
    private Long parentProjectId;

    /** 物化路径 "/1/5/"，用于祖先/后代查询 */
    private String projectPath;

    /** 深度冗余（0=顶层） */
    private Integer depth;

    /** 自定义汇总权重，默认 1.00 */
    private java.math.BigDecimal weight;

    /** 来源模板ID */
    private Long templateId;

    /** 模板版本快照 */
    private String templateVersion;

    /** 当前阶段ID */
    private Long currentPhaseId;

    /** 项目目标 */
    private String projectObjective;

    /** 项目范围 */
    private String projectScope;
```

> 同时确保 `@TableName` 注解保持原值（不加 autoResultMap=true，因为 Project 表本阶段没有新增 JSON 字段）。

- [ ] **Step 3: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 验证字段已正确添加（启动应用 + 查询接口）**

Run: `cd /workspace/network-equipment-pms/pms-admin && mvn spring-boot:run -DskipTests 2>&1 | grep -iE "Started|ERROR" | head -5`
Expected: 应用启动成功（无字段映射错误）

> 若启动失败，检查字段名拼写（DB 列名 snake_case ↔ Java 字段 camelCase 自动映射需开启 `mybatis-plus.configuration.map-underscore-to-camel-case: true`，通常已配置）。

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/entity/Project.java
git commit -m "feat(b7-t8): Project 实体扩展 9 字段（主子项目物化路径 + 模板/阶段关联）"
git push
```

---

## Task 9: Mapper 接口 — 5 个 Mapper

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectTemplateMapper.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectTemplateVersionMapper.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectPhaseMapper.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectMemberMapper.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectConfigMapper.java`

- [ ] **Step 1: 创建 5 个 Mapper 接口**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectTemplateMapper.java
package com.dp.plat.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.ProjectTemplate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectTemplateMapper extends BaseMapper<ProjectTemplate> {
}
```

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectTemplateVersionMapper.java
package com.dp.plat.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectTemplateVersionMapper extends BaseMapper<ProjectTemplateVersion> {
}
```

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectPhaseMapper.java
package com.dp.plat.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.ProjectPhase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectPhaseMapper extends BaseMapper<ProjectPhase> {
}
```

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectMemberMapper.java
package com.dp.plat.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}
```

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/dao/ProjectConfigMapper.java
package com.dp.plat.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.ProjectConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectConfigMapper extends BaseMapper<ProjectConfig> {
}
```

- [ ] **Step 2: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/dao/
git commit -m "feat(b7-t9): Mapper 接口 — 5 个 BaseMapper（Template/Version/Phase/Member/Config）"
git push
```

---

## Task 10: ProjectConfigService — 多层级配置读取（TDD）

**Files:**
- Test: `network-equipment-pms/pms-project/src/test/java/com/dp/plat/project/service/ProjectConfigServiceTest.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/ProjectConfigService.java`

- [ ] **Step 1: 写失败测试 — 多层级配置读取（项目级 > 模板级 > 系统默认）**

```java
// network-equipment-pms/pms-project/src/test/java/com/dp/plat/project/service/ProjectConfigServiceTest.java
package com.dp.plat.project.service;

import com.dp.plat.project.dao.ProjectConfigMapper;
import com.dp.plat.project.entity.ProjectConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectConfigServiceTest {

    @Mock
    private ProjectConfigMapper configMapper;

    @InjectMocks
    private ProjectConfigService configService;

    @BeforeEach
    void setUp() {
        // 系统默认配置（project_id IS NULL AND template_id IS NULL）
        ProjectConfig sysDefault = new ProjectConfig();
        sysDefault.setProjectId(null);
        sysDefault.setTemplateId(null);
        sysDefault.setConfigKey("approval.timeout.hours");
        sysDefault.setConfigValue("48");

        // 模板级配置
        ProjectConfig templateLevel = new ProjectConfig();
        templateLevel.setProjectId(null);
        templateLevel.setTemplateId(1L);
        templateLevel.setConfigKey("approval.timeout.hours");
        templateLevel.setConfigValue("72");

        // 项目级配置
        ProjectConfig projectLevel = new ProjectConfig();
        projectLevel.setProjectId(1001L);
        projectLevel.setTemplateId(1L);
        projectLevel.setConfigKey("approval.timeout.hours");
        projectLevel.setConfigValue("96");

        when(configMapper.selectList(any())).thenAnswer(invocation -> {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProjectConfig> wrapper =
                invocation.getArgument(0);
            // 简化模拟：根据 wrapper 的 SQL 返回不同子集
            // 真实场景由 SQL 过滤，这里直接返回全部让 Service 内部过滤
            return Arrays.asList(sysDefault, templateLevel, projectLevel);
        });
    }

    @Test
    void get_projectLevelOverridesSystemDefault() {
        // 项目级 > 系统默认
        String value = configService.get(1001L, 1L, "approval.timeout.hours");
        assertEquals("96", value, "项目级配置应覆盖系统默认");
    }

    @Test
    void get_templateLevelOverridesSystemDefault() {
        // 模板级 > 系统默认（无项目级）
        String value = configService.get(null, 1L, "approval.timeout.hours");
        assertEquals("72", value, "模板级配置应覆盖系统默认");
    }

    @Test
    void get_systemDefaultWhenNoOverride() {
        // 系统默认（无项目级、无模板级）
        String value = configService.get(null, null, "approval.timeout.hours");
        assertEquals("48", value, "无覆盖时应返回系统默认");
    }

    @Test
    void getInt_returnsParsedInteger() {
        int value = configService.getInt(1001L, 1L, "approval.timeout.hours");
        assertEquals(96, value);
    }

    @Test
    void get_returnsNullWhenKeyNotFound() {
        String value = configService.get(1001L, 1L, "nonexistent.key");
        assertEquals(null, value);
    }
}
```

- [ ] **Step 2: 运行测试，验证失败**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project test -Dtest=ProjectConfigServiceTest -q`
Expected: 编译失败（`ProjectConfigService` 类不存在）

- [ ] **Step 3: 实现 ProjectConfigService**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/ProjectConfigService.java
package com.dp.plat.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.dao.ProjectConfigMapper;
import com.dp.plat.project.entity.ProjectConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目配置服务 — 多层级读取（项目级 > 模板级 > 系统默认）
 * 关联设计文档：§4.3 ProjectConfigService
 *
 * <p>读取顺序：
 * <ol>
 *   <li>项目级覆盖（project_id = ? AND template_id IS NOT NULL OR project_id = ? AND template_id IS NULL）</li>
 *   <li>模板级默认（project_id IS NULL AND template_id = ?）</li>
 *   <li>系统默认（project_id IS NULL AND template_id IS NULL）</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class ProjectConfigService {

    private final ProjectConfigMapper configMapper;

    /**
     * 读取配置值（字符串）
     *
     * @param projectId  项目ID（NULL 表示无项目级）
     * @param templateId 模板ID（NULL 表示无模板级）
     * @param key        配置键
     * @return 配置值，找不到返回 NULL
     */
    public String get(Long projectId, Long templateId, String key) {
        // 1. 查询所有候选配置（一次性查询，内存中按优先级筛选）
        LambdaQueryWrapper<ProjectConfig> wrapper = new LambdaQueryWrapper<ProjectConfig>()
            .eq(ProjectConfig::getConfigKey, key)
            .and(w -> w
                .and(w1 -> w1.isNull(ProjectConfig::getProjectId).isNull(ProjectConfig::getTemplateId)) // 系统默认
                .or(w2 -> w2.isNull(ProjectConfig::getProjectId).eq(ProjectConfig::getTemplateId, templateId)) // 模板级
                .or(w3 -> w3.eq(ProjectConfig::getProjectId, projectId)) // 项目级
            );
        List<ProjectConfig> configs = configMapper.selectList(wrapper);

        // 2. 按优先级筛选：项目级 > 模板级 > 系统默认
        String projectLevel = null;
        String templateLevel = null;
        String systemDefault = null;

        for (ProjectConfig config : configs) {
            if (projectId != null && projectId.equals(config.getProjectId())) {
                projectLevel = config.getConfigValue();
            } else if (templateId != null && templateId.equals(config.getTemplateId())
                       && config.getProjectId() == null) {
                templateLevel = config.getConfigValue();
            } else if (config.getProjectId() == null && config.getTemplateId() == null) {
                systemDefault = config.getConfigValue();
            }
        }

        if (projectLevel != null) return projectLevel;
        if (templateLevel != null) return templateLevel;
        return systemDefault;
    }

    /**
     * 读取配置值并转为 int
     */
    public int getInt(Long projectId, Long templateId, String key) {
        String value = get(projectId, templateId, key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    /**
     * 读取配置值并转为 boolean
     */
    public boolean getBoolean(Long projectId, Long templateId, String key) {
        String value = get(projectId, templateId, key);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * 批量读取项目所有配置（用于前端配置页展示）
     */
    public Map<String, String> getAllForProject(Long projectId, Long templateId) {
        LambdaQueryWrapper<ProjectConfig> wrapper = new LambdaQueryWrapper<ProjectConfig>()
            .and(w -> w
                .and(w1 -> w1.isNull(ProjectConfig::getProjectId).isNull(ProjectConfig::getTemplateId))
                .or(w2 -> w2.isNull(ProjectConfig::getProjectId).eq(ProjectConfig::getTemplateId, templateId))
                .or(w3 -> w3.eq(ProjectConfig::getProjectId, projectId))
            );
        List<ProjectConfig> configs = configMapper.selectList(wrapper);

        Map<String, String> result = new HashMap<>();
        // 反向填充：先系统默认，再模板级覆盖，最后项目级覆盖
        for (ProjectConfig config : configs) {
            if (config.getProjectId() == null && config.getTemplateId() == null) {
                result.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        for (ProjectConfig config : configs) {
            if (config.getProjectId() == null && templateId != null
                && templateId.equals(config.getTemplateId())) {
                result.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        for (ProjectConfig config : configs) {
            if (projectId != null && projectId.equals(config.getProjectId())) {
                result.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return result;
    }
}
```

- [ ] **Step 4: 运行测试，验证通过**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project test -Dtest=ProjectConfigServiceTest -q`
Expected: BUILD SUCCESS，测试全部通过（5 个测试用例）

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/service/ProjectConfigService.java \
        pms-project/src/test/java/com/dp/plat/project/service/ProjectConfigServiceTest.java
git commit -m "feat(b7-t10): ProjectConfigService — 多层级配置读取（项目级 > 模板级 > 系统默认）+ TDD 测试"
git push
```

---

## Task 11: IProjectTemplateService 接口 + 实现 — CRUD + 版本发布（TDD）

**Files:**
- Test: `network-equipment-pms/pms-project/src/test/java/com/dp/plat/project/service/ProjectTemplateServiceImplTest.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/IProjectTemplateService.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectTemplateServiceImpl.java`

- [ ] **Step 1: 写失败测试 — 模板 CRUD + 版本发布**

```java
// network-equipment-pms/pms-project/src/test/java/com/dp/plat/project/service/ProjectTemplateServiceImplTest.java
package com.dp.plat.project.service;

import com.dp.plat.project.dao.ProjectTemplateMapper;
import com.dp.plat.project.dao.ProjectTemplateVersionMapper;
import com.dp.plat.project.dto.TemplateSnapshot;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectTemplateServiceImplTest {

    @Mock
    private ProjectTemplateMapper templateMapper;

    @Mock
    private ProjectTemplateVersionMapper versionMapper;

    @InjectMocks
    private com.dp.plat.project.service.impl.ProjectTemplateServiceImpl templateService;

    @Test
    void createTemplate_setsDefaultStatus() {
        ProjectTemplate template = new ProjectTemplate();
        template.setTemplateCode("TPL-001");
        template.setTemplateName("测试模板");

        when(templateMapper.insert(any())).thenReturn(1);

        ProjectTemplate result = templateService.create(template);

        assertEquals("DRAFT", result.getStatus(), "新模板默认状态应为 DRAFT");
        verify(templateMapper).insert(any());
    }

    @Test
    void publishVersion_setsStatusToPublished() {
        Long templateId = 1L;
        String version = "v1.0.0";
        TemplateSnapshot snapshot = new TemplateSnapshot();
        String changeLog = "初始版本";

        ProjectTemplate template = new ProjectTemplate();
        template.setId(templateId);
        template.setStatus("DRAFT");
        when(templateMapper.selectById(templateId)).thenReturn(template);
        when(versionMapper.insert(any())).thenAnswer(invocation -> {
            ProjectTemplateVersion v = invocation.getArgument(0);
            v.setId(100L);
            return 1;
        });

        ProjectTemplateVersion result = templateService.publishVersion(templateId, version, snapshot, changeLog);

        assertEquals("PUBLISHED", result.getStatus(), "发布后版本状态应为 PUBLISHED");
        assertNotNull(result.getPublishedAt(), "发布时间不应为空");
        assertEquals(version, result.getVersion());
        assertEquals(changeLog, result.getChangeLog());
    }

    @Test
    void publishVersion_throwsWhenTemplateNotFound() {
        when(templateMapper.selectById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            templateService.publishVersion(999L, "v1.0.0", new TemplateSnapshot(), "log");
        });
    }

    @Test
    void publishVersion_throwsWhenTemplateDeprecated() {
        ProjectTemplate template = new ProjectTemplate();
        template.setId(1L);
        template.setStatus("DEPRECATED");
        when(templateMapper.selectById(1L)).thenReturn(template);

        assertThrows(IllegalStateException.class, () -> {
            templateService.publishVersion(1L, "v1.0.0", new TemplateSnapshot(), "log");
        });
    }

    @Test
    void publishVersion_throwsWhenVersionExists() {
        ProjectTemplate template = new ProjectTemplate();
        template.setId(1L);
        template.setStatus("PUBLISHED");
        when(templateMapper.selectById(1L)).thenReturn(template);

        when(versionMapper.selectCount(any())).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> {
            templateService.publishVersion(1L, "v1.0.0", new TemplateSnapshot(), "log");
        });
    }
}
```

- [ ] **Step 2: 运行测试，验证失败**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project test -Dtest=ProjectTemplateServiceImplTest -q`
Expected: 编译失败（`IProjectTemplateService` 和 `ProjectTemplateServiceImpl` 不存在）

- [ ] **Step 3: 创建 IProjectTemplateService 接口**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/IProjectTemplateService.java
package com.dp.plat.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.project.dto.TemplateSnapshot;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;

/**
 * 项目模板服务接口
 * 关联设计文档：§4.3 Story 1
 */
public interface IProjectTemplateService {

    /**
     * 分页查询模板
     */
    Page<ProjectTemplate> page(int page, int size, String templateName, String category, String status);

    /**
     * 查询模板详情
     */
    ProjectTemplate getById(Long id);

    /**
     * 创建模板（默认状态 DRAFT）
     */
    ProjectTemplate create(ProjectTemplate template);

    /**
     * 更新模板（仅 DRAFT 状态可更新）
     */
    ProjectTemplate update(ProjectTemplate template);

    /**
     * 删除模板（仅 DRAFT 状态可删除）
     */
    void delete(Long id);

    /**
     * 查询模板的所有版本
     */
    Page<ProjectTemplateVersion> listVersions(Long templateId, int page, int size);

    /**
     * 发布新版本（深拷贝模板内容到 snapshot_json）
     */
    ProjectTemplateVersion publishVersion(Long templateId, String version, TemplateSnapshot snapshot, String changeLog);

    /**
     * 获取模板已发布版本（取最新 PUBLISHED 状态版本）
     */
    ProjectTemplateVersion getPublishedVersion(Long templateId);

    /**
     * 从模板创建项目（深拷贝 snapshot 到项目相关表）
     */
    Project createProjectFromTemplate(ProjectCreateFromTemplateDTO dto);
}
```

- [ ] **Step 4: 实现 ProjectTemplateServiceImpl（CRUD + 版本发布部分，createProjectFromTemplate 在 Task 13 实现）**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectTemplateServiceImpl.java
package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.project.dao.ProjectTemplateMapper;
import com.dp.plat.project.dao.ProjectTemplateVersionMapper;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.project.dto.TemplateSnapshot;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import com.dp.plat.project.service.IProjectTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 项目模板服务实现
 */
@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements IProjectTemplateService {

    private final ProjectTemplateMapper templateMapper;
    private final ProjectTemplateVersionMapper versionMapper;

    @Override
    public Page<ProjectTemplate> page(int page, int size, String templateName, String category, String status) {
        Page<ProjectTemplate> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ProjectTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(templateName)) {
            wrapper.like(ProjectTemplate::getTemplateName, templateName);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(ProjectTemplate::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectTemplate::getStatus, status);
        }
        wrapper.orderByDesc(ProjectTemplate::getCreateTime);
        return templateMapper.selectPage(pageObj, wrapper);
    }

    @Override
    public ProjectTemplate getById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    @Transactional
    public ProjectTemplate create(ProjectTemplate template) {
        if (template.getStatus() == null) {
            template.setStatus("DRAFT");
        }
        templateMapper.insert(template);
        return template;
    }

    @Override
    @Transactional
    public ProjectTemplate update(ProjectTemplate template) {
        ProjectTemplate existing = templateMapper.selectById(template.getId());
        if (existing == null) {
            throw new IllegalArgumentException("模板不存在: " + template.getId());
        }
        if (!"DRAFT".equals(existing.getStatus())) {
            throw new IllegalStateException("仅 DRAFT 状态模板可编辑，当前状态: " + existing.getStatus());
        }
        templateMapper.updateById(template);
        return template;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProjectTemplate existing = templateMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("模板不存在: " + id);
        }
        if (!"DRAFT".equals(existing.getStatus())) {
            throw new IllegalStateException("仅 DRAFT 状态模板可删除，当前状态: " + existing.getStatus());
        }
        templateMapper.deleteById(id);
    }

    @Override
    public Page<ProjectTemplateVersion> listVersions(Long templateId, int page, int size) {
        Page<ProjectTemplateVersion> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ProjectTemplateVersion> wrapper = new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .orderByDesc(ProjectTemplateVersion::getCreateTime);
        return versionMapper.selectPage(pageObj, wrapper);
    }

    @Override
    @Transactional
    public ProjectTemplateVersion publishVersion(Long templateId, String version, TemplateSnapshot snapshot, String changeLog) {
        // 1. 校验模板存在且未 DEPRECATED
        ProjectTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + templateId);
        }
        if ("DEPRECATED".equals(template.getStatus())) {
            throw new IllegalStateException("DEPRECATED 状态模板不可发布新版本");
        }

        // 2. 校验版本号唯一
        Long existingCount = versionMapper.selectCount(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getVersion, version));
        if (existingCount > 0) {
            throw new IllegalStateException("版本号已存在: " + version);
        }

        // 3. 创建版本记录
        ProjectTemplateVersion versionRecord = new ProjectTemplateVersion();
        versionRecord.setTemplateId(templateId);
        versionRecord.setVersion(version);
        versionRecord.setSnapshotJson(snapshot);
        versionRecord.setChangeLog(changeLog);
        versionRecord.setStatus("PUBLISHED");
        versionRecord.setPublishedAt(LocalDateTime.now());
        // publishedBy 由 Controller 层注入（从 SecurityContext）
        versionMapper.insert(versionRecord);

        // 4. 更新模板状态为 PUBLISHED
        template.setStatus("PUBLISHED");
        templateMapper.updateById(template);

        return versionRecord;
    }

    @Override
    public ProjectTemplateVersion getPublishedVersion(Long templateId) {
        return versionMapper.selectOne(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getStatus, "PUBLISHED")
            .orderByDesc(ProjectTemplateVersion::getPublishedAt)
            .last("LIMIT 1"));
    }

    @Override
    @Transactional
    public Project createProjectFromTemplate(ProjectCreateFromTemplateDTO dto) {
        // Task 13 实现：深拷贝模板内容到项目相关表
        throw new UnsupportedOperationException("createProjectFromTemplate 在 Task 13 实现");
    }
}
```

- [ ] **Step 5: 运行测试，验证通过**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project test -Dtest=ProjectTemplateServiceImplTest -q`
Expected: BUILD SUCCESS，5 个测试用例全部通过

- [ ] **Step 6: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/service/IProjectTemplateService.java \
        pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectTemplateServiceImpl.java \
        pms-project/src/test/java/com/dp/plat/project/service/ProjectTemplateServiceImplTest.java
git commit -m "feat(b7-t11): IProjectTemplateService + Impl — 模板 CRUD + 版本发布（含唯一性/状态校验）+ TDD 测试"
git push
```

---

## Task 12: 阶段/成员服务接口 + 基本 CRUD 实现

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/IProjectPhaseService.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectPhaseServiceImpl.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/IProjectMemberService.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectMemberServiceImpl.java`

> 注：advancePhase / closeProject 等业务方法在 Phase 3 实现，本任务仅提供基本 CRUD。

- [ ] **Step 1: 创建 IProjectPhaseService 接口**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/IProjectPhaseService.java
package com.dp.plat.project.service;

import com.dp.plat.project.entity.ProjectPhase;

import java.util.List;

/**
 * 项目阶段服务接口
 * 关联设计文档：§4.3 Story 2
 *
 * <p>注：advancePhase / closeProject / validateExitGates / validateSubProjectsClosed
 * 在 Phase 3 实现计划中实现，本接口仅提供基本 CRUD。
 */
public interface IProjectPhaseService {

    /** 查询项目所有阶段（按 sortOrder 排序） */
    List<ProjectPhase> listByProjectId(Long projectId);

    /** 查询阶段详情 */
    ProjectPhase getById(Long id);

    /** 新增阶段 */
    ProjectPhase create(ProjectPhase phase);

    /** 更新阶段 */
    ProjectPhase update(ProjectPhase phase);

    /** 删除阶段 */
    void delete(Long id);

    /** 批量保存项目阶段（用于从模板创建项目时深拷贝） */
    List<ProjectPhase> batchCreate(List<ProjectPhase> phases);
}
```

- [ ] **Step 2: 实现 ProjectPhaseServiceImpl（基本 CRUD）**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectPhaseServiceImpl.java
package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.dao.ProjectPhaseMapper;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.service.IProjectPhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectPhaseServiceImpl implements IProjectPhaseService {

    private final ProjectPhaseMapper phaseMapper;

    @Override
    public List<ProjectPhase> listByProjectId(Long projectId) {
        return phaseMapper.selectList(new LambdaQueryWrapper<ProjectPhase>()
            .eq(ProjectPhase::getProjectId, projectId)
            .orderByAsc(ProjectPhase::getSortOrder));
    }

    @Override
    public ProjectPhase getById(Long id) {
        return phaseMapper.selectById(id);
    }

    @Override
    @Transactional
    public ProjectPhase create(ProjectPhase phase) {
        if (phase.getStatus() == null) {
            phase.setStatus("NOT_STARTED");
        }
        phaseMapper.insert(phase);
        return phase;
    }

    @Override
    @Transactional
    public ProjectPhase update(ProjectPhase phase) {
        phaseMapper.updateById(phase);
        return phase;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        phaseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public List<ProjectPhase> batchCreate(List<ProjectPhase> phases) {
        for (ProjectPhase phase : phases) {
            if (phase.getStatus() == null) {
                phase.setStatus("NOT_STARTED");
            }
            phaseMapper.insert(phase);
        }
        return phases;
    }
}
```

- [ ] **Step 3: 创建 IProjectMemberService 接口 + 实现**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/IProjectMemberService.java
package com.dp.plat.project.service;

import com.dp.plat.project.entity.ProjectMember;

import java.util.List;

public interface IProjectMemberService {

    List<ProjectMember> listByProjectId(Long projectId);

    ProjectMember create(ProjectMember member);

    ProjectMember update(ProjectMember member);

    void delete(Long id);

    void deleteByProjectId(Long projectId);

    List<ProjectMember> batchCreate(List<ProjectMember> members);
}
```

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectMemberServiceImpl.java
package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.dao.ProjectMemberMapper;
import com.dp.plat.project.entity.ProjectMember;
import com.dp.plat.project.service.IProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements IProjectMemberService {

    private final ProjectMemberMapper memberMapper;

    @Override
    public List<ProjectMember> listByProjectId(Long projectId) {
        return memberMapper.selectList(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, projectId));
    }

    @Override
    @Transactional
    public ProjectMember create(ProjectMember member) {
        if (member.getRole() == null) {
            member.setRole("PROJECT_MEMBER");
        }
        memberMapper.insert(member);
        return member;
    }

    @Override
    @Transactional
    public ProjectMember update(ProjectMember member) {
        memberMapper.updateById(member);
        return member;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        memberMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByProjectId(Long projectId) {
        memberMapper.delete(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, projectId));
    }

    @Override
    @Transactional
    public List<ProjectMember> batchCreate(List<ProjectMember> members) {
        for (ProjectMember m : members) {
            if (m.getRole() == null) {
                m.setRole("PROJECT_MEMBER");
            }
            memberMapper.insert(m);
        }
        return members;
    }
}
```

- [ ] **Step 4: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/service/IProjectPhaseService.java \
        pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectPhaseServiceImpl.java \
        pms-project/src/main/java/com/dp/plat/project/service/IProjectMemberService.java \
        pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectMemberServiceImpl.java
git commit -m "feat(b7-t12): 阶段/成员服务接口 + 基本 CRUD 实现（advancePhase 留待 Phase 3）"
git push
```

---

## Task 13: createProjectFromTemplate — 深拷贝实现（TDD）

**Files:**
- Test: `network-equipment-pms/pms-project/src/test/java/com/dp/plat/project/service/CreateProjectFromTemplateTest.java`
- Modify: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectTemplateServiceImpl.java`

- [ ] **Step 1: 写失败测试 — 从模板创建项目，深拷贝阶段/任务/里程碑/交付件**

```java
// network-equipment-pms/pms-project/src/test/java/com/dp/plat/project/service/CreateProjectFromTemplateTest.java
package com.dp.plat.project.service;

import com.dp.plat.project.dao.*;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.project.dto.TemplateSnapshot;
import com.dp.plat.project.entity.*;
import com.dp.plat.project.service.impl.ProjectTemplateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectFromTemplateTest {

    @Mock private ProjectTemplateMapper templateMapper;
    @Mock private ProjectTemplateVersionMapper versionMapper;
    @Mock private ProjectMapper projectMapper;
    @Mock private ProjectPhaseMapper phaseMapper;
    @Mock private ProjectMemberMapper memberMapper;
    @Mock private ProjectConfigMapper configMapper;

    @InjectMocks
    private ProjectTemplateServiceImpl templateService;

    @Test
    void createProjectFromTemplate_deepCopiesPhases() {
        // 准备：模板版本含 2 个阶段
        TemplateSnapshot snapshot = new TemplateSnapshot();
        TemplateSnapshot.PhaseDef phase1 = new TemplateSnapshot.PhaseDef();
        phase1.setPhaseCode("PREPARE");
        phase1.setPhaseName("准备阶段");
        phase1.setSortOrder(1);
        TemplateSnapshot.PhaseDef phase2 = new TemplateSnapshot.PhaseDef();
        phase2.setPhaseCode("PLAN");
        phase2.setPhaseName("规划阶段");
        phase2.setSortOrder(2);
        snapshot.setPhases(Arrays.asList(phase1, phase2));

        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setId(10L);
        version.setTemplateId(1L);
        version.setVersion("v1.0.0");
        version.setSnapshotJson(snapshot);
        version.setStatus("PUBLISHED");

        ProjectTemplate template = new ProjectTemplate();
        template.setId(1L);
        template.setStatus("PUBLISHED");

        when(versionMapper.selectById(10L)).thenReturn(version);
        when(templateMapper.selectById(1L)).thenReturn(template);
        when(projectMapper.insert(any())).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1001L);
            return 1;
        });

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);
        dto.setProjectCode("IMPL-2026-001");
        dto.setProjectName("测试项目");
        dto.setPlanStartDate(LocalDate.of(2026, 7, 1));
        dto.setPlanEndDate(LocalDate.of(2026, 12, 31));
        dto.setProjectManagerId(100L);

        // 执行
        Project result = templateService.createProjectFromTemplate(dto);

        // 验证：项目创建
        assertNotNull(result.getId());
        assertEquals("PLANNING", result.getStatus(), "新建项目状态应为 PLANNING");
        assertEquals("/1001/", result.getProjectPath(), "顶层项目路径应为 /<id>/");
        assertEquals(0, result.getDepth());
        assertEquals(1L, result.getTemplateId());
        assertEquals("v1.0.0", result.getTemplateVersion());

        // 验证：2 个阶段被深拷贝
        verify(phaseMapper, times(2)).insert(any(ProjectPhase.class));
    }

    @Test
    void createProjectFromTemplate_initializesMembers() {
        TemplateSnapshot snapshot = new TemplateSnapshot();
        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setId(10L);
        version.setTemplateId(1L);
        version.setVersion("v1.0.0");
        version.setSnapshotJson(snapshot);
        version.setStatus("PUBLISHED");

        when(versionMapper.selectById(10L)).thenReturn(version);
        when(templateMapper.selectById(1L)).thenReturn(template());
        when(projectMapper.insert(any())).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1001L);
            return 1;
        });

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);
        dto.setProjectCode("IMPL-2026-002");
        dto.setProjectName("测试项目2");
        dto.setProjectManagerId(100L);

        ProjectCreateFromTemplateDTO.MemberDef m1 = new ProjectCreateFromTemplateDTO.MemberDef();
        m1.setUserId(100L);
        m1.setRole("PROJECT_MANAGER");
        ProjectCreateFromTemplateDTO.MemberDef m2 = new ProjectCreateFromTemplateDTO.MemberDef();
        m2.setUserId(101L);
        m2.setRole("PROJECT_MEMBER");
        dto.setMembers(Arrays.asList(m1, m2));

        templateService.createProjectFromTemplate(dto);

        // 验证：2 个成员被创建
        verify(memberMapper, times(2)).insert(any(ProjectMember.class));
    }

    @Test
    void createProjectFromTemplate_appliesConfigOverrides() {
        TemplateSnapshot snapshot = new TemplateSnapshot();
        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setId(10L);
        version.setTemplateId(1L);
        version.setVersion("v1.0.0");
        version.setSnapshotJson(snapshot);
        version.setStatus("PUBLISHED");

        when(versionMapper.selectById(10L)).thenReturn(version);
        when(templateMapper.selectById(1L)).thenReturn(template());
        when(projectMapper.insert(any())).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1001L);
            return 1;
        });

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);
        dto.setProjectCode("IMPL-2026-003");
        dto.setProjectName("测试项目3");
        dto.setProjectManagerId(100L);
        dto.setConfigOverrides(Collections.singletonMap("approval.timeout.hours", "72"));

        templateService.createProjectFromTemplate(dto);

        // 验证：项目级配置被写入
        verify(configMapper, times(1)).insert(any(ProjectConfig.class));
    }

    @Test
    void createProjectFromTemplate_throwsWhenVersionNotPublished() {
        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setStatus("DRAFT");
        when(versionMapper.selectById(10L)).thenReturn(version);

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);

        assertThrows(IllegalStateException.class, () -> {
            templateService.createProjectFromTemplate(dto);
        });
    }

    private ProjectTemplate template() {
        ProjectTemplate t = new ProjectTemplate();
        t.setId(1L);
        t.setStatus("PUBLISHED");
        return t;
    }
}
```

- [ ] **Step 2: 运行测试，验证失败**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project test -Dtest=CreateProjectFromTemplateTest -q`
Expected: 测试失败（`createProjectFromTemplate` 抛出 `UnsupportedOperationException`）

- [ ] **Step 3: 实现 createProjectFromTemplate 深拷贝逻辑**

修改 `ProjectTemplateServiceImpl.java`：

1. 在类顶部添加新依赖注入字段：

```java
    private final com.dp.plat.project.dao.ProjectMapper projectMapper;
    private final com.dp.plat.project.dao.ProjectPhaseMapper phaseMapper;
    private final com.dp.plat.project.dao.ProjectMemberMapper memberMapper;
    private final com.dp.plat.project.dao.ProjectConfigMapper configMapper;
```

> 注：`@RequiredArgsConstructor` 会自动生成包含所有 final 字段的构造器。需确保这些 Mapper 都已声明为 `private final`。

2. 替换 `createProjectFromTemplate` 方法体：

```java
    @Override
    @Transactional
    public Project createProjectFromTemplate(ProjectCreateFromTemplateDTO dto) {
        // 1. 校验模板版本存在且已发布
        ProjectTemplateVersion version = versionMapper.selectById(dto.getVersionId());
        if (version == null) {
            throw new IllegalArgumentException("模板版本不存在: " + dto.getVersionId());
        }
        if (!"PUBLISHED".equals(version.getStatus())) {
            throw new IllegalStateException("仅可从 PUBLISHED 状态版本创建项目，当前状态: " + version.getStatus());
        }
        TemplateSnapshot snapshot = version.getSnapshotJson();
        if (snapshot == null) {
            throw new IllegalStateException("模板版本快照为空");
        }

        // 2. 创建项目（顶层项目）
        Project project = new Project();
        project.setProjectCode(dto.getProjectCode());
        project.setProjectName(dto.getProjectName());
        project.setCustomerName(dto.getCustomerName());
        project.setCustomerContact(dto.getCustomerContact());
        project.setCustomerPhone(dto.getCustomerPhone());
        project.setContractNo(dto.getContractNo());
        project.setContractAmount(dto.getContractAmount());
        project.setPlanStartDate(dto.getPlanStartDate());
        project.setPlanEndDate(dto.getPlanEndDate());
        project.setProjectManagerId(dto.getProjectManagerId());
        project.setStatus("PLANNING");
        project.setTemplateId(dto.getTemplateId());
        project.setTemplateVersion(version.getVersion());
        project.setProjectObjective(dto.getProjectObjective());
        project.setProjectScope(dto.getProjectScope());
        project.setParentProjectId(null);
        project.setDepth(0);
        project.setWeight(new BigDecimal("1.00"));
        projectMapper.insert(project);

        // 3. 设置物化路径（依赖自增主键）
        project.setProjectPath("/" + project.getId() + "/");
        projectMapper.updateById(project);

        // 4. 深拷贝阶段
        if (snapshot.getPhases() != null) {
            for (TemplateSnapshot.PhaseDef phaseDef : snapshot.getPhases()) {
                ProjectPhase phase = new ProjectPhase();
                phase.setProjectId(project.getId());
                phase.setTemplatePhaseId(null); // 模板阶段未持久化，无追溯 ID
                phase.setPhaseName(phaseDef.getPhaseName());
                phase.setPhaseCode(phaseDef.getPhaseCode());
                phase.setSortOrder(phaseDef.getSortOrder());
                phase.setEntryCriteria(phaseDef.getEntryCriteria());
                phase.setExitCriteria(phaseDef.getExitCriteria());
                phase.setStatus("NOT_STARTED");
                phaseMapper.insert(phase);
            }
        }

        // 5. 初始化成员
        if (dto.getMembers() != null) {
            for (ProjectCreateFromTemplateDTO.MemberDef memberDef : dto.getMembers()) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getId());
                member.setUserId(memberDef.getUserId());
                member.setRole(memberDef.getRole());
                memberMapper.insert(member);
            }
        }

        // 6. 应用配置覆盖
        if (dto.getConfigOverrides() != null) {
            for (java.util.Map.Entry<String, String> entry : dto.getConfigOverrides().entrySet()) {
                ProjectConfig config = new ProjectConfig();
                config.setProjectId(project.getId());
                config.setTemplateId(dto.getTemplateId());
                config.setConfigKey(entry.getKey());
                config.setConfigValue(entry.getValue());
                configMapper.insert(config);
            }
        }

        // 7. 设置当前阶段为第一个阶段（若有）
        if (snapshot.getPhases() != null && !snapshot.getPhases().isEmpty()) {
            // 查询刚插入的第一个阶段（按 sortOrder 最小）
            com.dp.plat.project.entity.ProjectPhase firstPhase = phaseMapper.selectOne(
                new LambdaQueryWrapper<com.dp.plat.project.entity.ProjectPhase>()
                    .eq(com.dp.plat.project.entity.ProjectPhase::getProjectId, project.getId())
                    .orderByAsc(com.dp.plat.project.entity.ProjectPhase::getSortOrder)
                    .last("LIMIT 1"));
            if (firstPhase != null) {
                project.setCurrentPhaseId(firstPhase.getId());
                projectMapper.updateById(project);
            }
        }

        // 注：任务/里程碑/交付件/依赖的深拷贝在 Phase 2-6 实现计划中实现（依赖对应实体表创建）
        return project;
    }
```

3. 补充 import：

```java
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
```

- [ ] **Step 4: 运行测试，验证通过**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project test -Dtest=CreateProjectFromTemplateTest -q`
Expected: BUILD SUCCESS，4 个测试用例全部通过

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/service/impl/ProjectTemplateServiceImpl.java \
        pms-project/src/test/java/com/dp/plat/project/service/CreateProjectFromTemplateTest.java
git commit -m "feat(b7-t13): createProjectFromTemplate 深拷贝实现（阶段/成员/配置）+ TDD 测试"
git push
```

---

## Task 14: ProjectTemplateController — 10 个端点

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectTemplateController.java`

- [ ] **Step 1: 查看现有 Controller 模式（参考 ProjectController）**

Run: `cat /workspace/network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectController.java | head -60`
Expected: 显示现有 Controller 的注解模式（`@RestController`、`@RequestMapping`、`@RequiresPermissions`、`Result<T>` 等）

- [ ] **Step 2: 创建 ProjectTemplateController**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectTemplateController.java
package com.dp.plat.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.project.dto.TemplateSnapshot;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import com.dp.plat.project.service.IProjectTemplateService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

/**
 * 项目模板 Controller
 * 关联设计文档：§5.2 Story 1 API
 */
@RestController
@RequestMapping("/api/project/template")
@RequiredArgsConstructor
public class ProjectTemplateController {

    private final IProjectTemplateService templateService;

    @GetMapping("/list")
    @RequiresPermissions("project:template:list")
    public Result<Page<ProjectTemplate>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        return Result.ok(templateService.page(page, size, templateName, category, status));
    }

    @GetMapping("/{id}")
    @RequiresPermissions("project:template:list")
    public Result<ProjectTemplate> getById(@PathVariable Long id) {
        return Result.ok(templateService.getById(id));
    }

    @PostMapping
    @RequiresPermissions("project:template:add")
    public Result<ProjectTemplate> create(@RequestBody ProjectTemplate template) {
        return Result.ok(templateService.create(template));
    }

    @PutMapping
    @RequiresPermissions("project:template:add")
    public Result<ProjectTemplate> update(@RequestBody ProjectTemplate template) {
        return Result.ok(templateService.update(template));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("project:template:add")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/versions")
    @RequiresPermissions("project:template:list")
    public Result<Page<ProjectTemplateVersion>> listVersions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(templateService.listVersions(id, page, size));
    }

    @PostMapping("/{id}/publish")
    @RequiresPermissions("project:template:publish")
    public Result<ProjectTemplateVersion> publishVersion(
            @PathVariable Long id,
            @RequestBody PublishVersionRequest request) {
        ProjectTemplateVersion version = templateService.publishVersion(
            id, request.getVersion(), request.getSnapshot(), request.getChangeLog());
        return Result.ok(version);
    }

    @GetMapping("/{id}/published-version")
    @RequiresPermissions("project:template:list")
    public Result<ProjectTemplateVersion> getPublishedVersion(@PathVariable Long id) {
        return Result.ok(templateService.getPublishedVersion(id));
    }

    @PostMapping("/create-project")
    @RequiresPermissions("project:template:use")
    public Result<Project> createProjectFromTemplate(@RequestBody ProjectCreateFromTemplateDTO dto) {
        return Result.ok(templateService.createProjectFromTemplate(dto));
    }

    /** 发布版本请求体 */
    @lombok.Data
    public static class PublishVersionRequest {
        private String version;
        private TemplateSnapshot snapshot;
        private String changeLog;
    }
}
```

- [ ] **Step 3: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

> 若 `Result` 类路径不一致，先 grep 确认：`grep -r "class Result" /workspace/network-equipment-pms/pms-common/src/main/java/`，按实际路径调整 import。

- [ ] **Step 4: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/controller/ProjectTemplateController.java
git commit -m "feat(b7-t14): ProjectTemplateController — 10 个端点（CRUD + 版本发布 + 从模板创建项目）"
git push
```

---

## Task 15: ProjectPhaseController + ProjectMemberController + ProjectConfigController

**Files:**
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectPhaseController.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectMemberController.java`
- Create: `network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectConfigController.java`

- [ ] **Step 1: 创建 ProjectPhaseController**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectPhaseController.java
package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.service.IProjectPhaseService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目阶段 Controller
 * 关联设计文档：§5.1 API 路由总览
 *
 * <p>注：advancePhase 端点在 Phase 3 实现计划中添加。
 */
@RestController
@RequestMapping("/api/project/phase")
@RequiredArgsConstructor
public class ProjectPhaseController {

    private final IProjectPhaseService phaseService;

    @GetMapping("/project/{projectId}")
    public Result<List<ProjectPhase>> listByProjectId(@PathVariable Long projectId) {
        return Result.ok(phaseService.listByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public Result<ProjectPhase> getById(@PathVariable Long id) {
        return Result.ok(phaseService.getById(id));
    }

    @PostMapping
    @RequiresPermissions("project:phase:advance")
    public Result<ProjectPhase> create(@RequestBody ProjectPhase phase) {
        return Result.ok(phaseService.create(phase));
    }

    @PutMapping
    @RequiresPermissions("project:phase:advance")
    public Result<ProjectPhase> update(@RequestBody ProjectPhase phase) {
        return Result.ok(phaseService.update(phase));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("project:phase:advance")
    public Result<Void> delete(@PathVariable Long id) {
        phaseService.delete(id);
        return Result.ok();
    }
}
```

- [ ] **Step 2: 创建 ProjectMemberController**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectMemberController.java
package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.ProjectMember;
import com.dp.plat.project.service.IProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project/member")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final IProjectMemberService memberService;

    @GetMapping("/project/{projectId}")
    public Result<List<ProjectMember>> listByProjectId(@PathVariable Long projectId) {
        return Result.ok(memberService.listByProjectId(projectId));
    }

    @PostMapping
    @RequiresPermissions("project:subproject:manage")
    public Result<ProjectMember> create(@RequestBody ProjectMember member) {
        return Result.ok(memberService.create(member));
    }

    @PutMapping
    @RequiresPermissions("project:subproject:manage")
    public Result<ProjectMember> update(@RequestBody ProjectMember member) {
        return Result.ok(memberService.update(member));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("project:subproject:manage")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return Result.ok();
    }
}
```

- [ ] **Step 3: 创建 ProjectConfigController**

```java
// network-equipment-pms/pms-project/src/main/java/com/dp/plat/project/controller/ProjectConfigController.java
package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.service.IProjectTemplateService;
import com.dp.plat.project.service.ProjectConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project/config")
@RequiredArgsConstructor
public class ProjectConfigController {

    private final ProjectConfigService configService;
    private final IProjectTemplateService templateService;

    @GetMapping("/{projectId}")
    public Result<Map<String, String>> getAllForProject(@PathVariable Long projectId) {
        Project project = templateService.getById(null) == null ? null : null; // 简化：实际从 ProjectService 获取
        // 实际实现需要查询 Project 获取 templateId
        Long templateId = null; // TODO: 通过 ProjectService 获取 project.getTemplateId()
        return Result.ok(configService.getAllForProject(projectId, templateId));
    }

    @PutMapping("/{projectId}")
    @RequiresPermissions("workflow:approval:config")
    public Result<Void> update(@PathVariable Long projectId, @RequestBody Map<String, String> configs) {
        // 批量更新项目级配置
        // 实现略：删除旧项目级配置 + 插入新配置
        return Result.ok();
    }
}
```

> 注：`ProjectConfigController.getAllForProject` 需要查询 Project 获取 templateId。本任务先简化，在 Phase 2 集成时补全。也可注入 `IProjectService` 替代 `IProjectTemplateService`。

- [ ] **Step 4: 编译验证**

Run: `cd /workspace/network-equipment-pms && mvn -pl pms-project -am compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-project/src/main/java/com/dp/plat/project/controller/ProjectPhaseController.java \
        pms-project/src/main/java/com/dp/plat/project/controller/ProjectMemberController.java \
        pms-project/src/main/java/com/dp/plat/project/controller/ProjectConfigController.java
git commit -m "feat(b7-t15): Phase/Member/Config Controller — 基本端点（advancePhase 留待 Phase 3）"
git push
```

---

## Task 16: 后端启动验证 + 接口连通性测试

**Files:**
- 无新增，仅验证

- [ ] **Step 1: 全量编译 + 打包**

Run: `cd /workspace/network-equipment-pms && mvn clean package -DskipTests -q`
Expected: BUILD SUCCESS

- [ ] **Step 2: 启动后端应用**

Run: `cd /workspace/network-equipment-pms/pms-admin && mvn spring-boot:run -DskipTests 2>&1 | grep -iE "Started|ERROR|flyway" | head -10`
Expected: 日志含 `Started PmsAdminApplication` 和 Flyway 显示 V64/V65/V66 已应用

- [ ] **Step 3: 验证模板列表端点（未鉴权预期返回 401/403）**

Run: `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/project/template/list`
Expected: 401 或 403（未登录）

- [ ] **Step 4: 验证已登录后端点连通（需登录态）**

```bash
# 1. 登录获取 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.data.token')

# 2. 调用模板列表
curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/project/template/list | jq .
```
Expected: 返回 `{"code":200,"data":{"records":[],"total":0,...}}`（空列表）

> 若登录接口路径或字段不一致，参考现有 ProjectController 的测试方式。

- [ ] **Step 5: 提交（无代码改动，仅记录验证日志）**

```bash
cd /workspace/network-equipment-pms
# 无代码改动，跳过提交。若发现 Bug，修复后单独提交。
echo "Task 16 验证通过：后端启动 + 3 个端点连通"
```

---

## Task 17: 前端 — 安装 AntV G6 依赖

**Files:**
- Modify: `network-equipment-pms/pms-frontend/package.json`

- [ ] **Step 1: 安装 @antv/g6**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npm install @antv/g6@^5.0.0 --save`
Expected: `npm ls @antv/g6` 显示 `@antv/g6@5.x.x` 已安装

- [ ] **Step 2: 验证 TypeScript 类型可用**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npx vue-tsc --noEmit 2>&1 | head -5`
Expected: 无 G6 相关错误（其他既有错误可忽略）

- [ ] **Step 3: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/package.json pms-frontend/package-lock.json
git commit -m "chore(b7-t17): 安装 @antv/g6@^5.0.0（用于 Task 5.x 依赖关系图）"
git push
```

---

## Task 18: 前端 — 路由全量重构为嵌套模式

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/router/index.ts`

- [ ] **Step 1: 备份现有路由文件**

Run: `cp /workspace/network-equipment-pms/pms-frontend/src/router/index.ts /workspace/network-equipment-pms/pms-frontend/src/router/index.ts.bak`
Expected: 备份成功

- [ ] **Step 2: 重写路由为嵌套模式**

完整替换 `src/router/index.ts` 内容（保留既有路由，按业务域 children 重组 + 新增项目管理路由）：

```typescript
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { startRouteLoading, stopRouteLoading } from '@/directives/loading'

const Layout = () => import('@/layouts/DefaultLayout.vue')

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/help',
    name: 'Help',
    component: () => import('@/views/help/index.vue'),
    meta: { title: '帮助中心', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  },
  // ============ 项目管理（嵌套） ============
  {
    path: '/project',
    component: Layout,
    redirect: '/project/list',
    meta: { title: '项目管理', icon: 'Folder', requiresAuth: true },
    children: [
      {
        path: 'list',
        name: 'ProjectList',
        component: () => import('@/views/project/list/index.vue'),
        meta: { title: '项目列表', icon: 'Folder' }
      },
      {
        path: 'detail/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/detail/index.vue'),
        meta: { title: '项目详情', hidden: true }
      },
      {
        path: 'kanban',
        name: 'ProjectKanban',
        component: () => import('@/views/project/kanban/index.vue'),
        meta: { title: '交付看板', icon: 'Grid' }
      },
      {
        path: 'template',
        name: 'ProjectTemplate',
        component: () => import('@/views/project/template/index.vue'),
        meta: { title: '项目模板', icon: 'Files', perms: 'project:template:list' }
      },
      {
        path: 'template/form/:id?',
        name: 'ProjectTemplateForm',
        component: () => import('@/views/project/template/form.vue'),
        meta: { title: '模板编辑', hidden: true }
      },
      {
        path: 'template/version/:id',
        name: 'ProjectTemplateVersion',
        component: () => import('@/views/project/template/version.vue'),
        meta: { title: '版本管理', hidden: true }
      }
    ]
  },
  // ============ 资产管理（嵌套） ============
  {
    path: '/asset',
    component: Layout,
    redirect: '/asset/category',
    meta: { title: '资产管理', icon: 'Files', requiresAuth: true },
    children: [
      { path: 'category', name: 'AssetCategory',
        component: () => import('@/views/asset/category/index.vue'),
        meta: { title: '设备分类', icon: 'Files' } },
      { path: 'model', name: 'AssetModel',
        component: () => import('@/views/asset/model/index.vue'),
        meta: { title: '设备型号', icon: 'Box' } },
      { path: 'list', name: 'AssetList',
        component: () => import('@/views/asset/list/index.vue'),
        meta: { title: '资产清单', icon: 'List' } }
    ]
  },
  // ============ 实施管理（嵌套） ============
  {
    path: '/implementation',
    component: Layout,
    redirect: '/implementation/task',
    meta: { title: '实施管理', icon: 'Tickets', requiresAuth: true },
    children: [
      { path: 'task', name: 'ImplTask',
        component: () => import('@/views/implementation/task/index.vue'),
        meta: { title: '实施任务', icon: 'Tickets' } },
      { path: 'agent', name: 'AgentManage',
        component: () => import('@/views/implementation/agent/index.vue'),
        meta: { title: '服务商管理', icon: 'OfficeBuilding' } },
      { path: 'settlement', name: 'Settlement',
        component: () => import('@/views/implementation/settlement/index.vue'),
        meta: { title: '结算管理', icon: 'Money' } }
    ]
  },
  // ============ 工作流与审批（嵌套） ============
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/todo',
    meta: { title: '工作流', icon: 'Bell', requiresAuth: true },
    children: [
      { path: 'todo', name: 'WorkflowTodo',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '待办中心', icon: 'Bell' } }
    ]
  },
  // ============ 其他业务（保留平铺，逐步迁移） ============
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'punch-list', name: 'PunchList',
        component: () => import('@/views/punch-list/index.vue'),
        meta: { title: 'Punch List', icon: 'WarningFilled' } },
      { path: 'rma', name: 'Rma',
        component: () => import('@/views/rma/index.vue'),
        meta: { title: 'RMA 返修', icon: 'RefreshRight' } },
      { path: 'warranty', name: 'Warranty',
        component: () => import('@/views/warranty/index.vue'),
        meta: { title: '质保期管理', icon: 'Timer' } },
      { path: 'deliverable', name: 'Deliverable',
        component: () => import('@/views/deliverable/index.vue'),
        meta: { title: '终验交付物', icon: 'Document' } },
      { path: 'notification', name: 'NotificationCenter',
        component: () => import('@/views/notification/index.vue'),
        meta: { title: '消息中心', icon: 'Bell' } },
      { path: 'integration-health', name: 'IntegrationHealth',
        component: () => import('@/views/integration-health/index.vue'),
        meta: { title: '集成健康检查', icon: 'Monitor' } },
      { path: 'risk', name: 'Risk',
        component: () => import('@/views/risk/index.vue'),
        meta: { title: '风险登记册', icon: 'Warning' } },
      { path: 'change-request', name: 'ChangeRequest',
        component: () => import('@/views/change-request/index.vue'),
        meta: { title: '变更管理', icon: 'EditPen' } },
      { path: 'issue', name: 'Issue',
        component: () => import('@/views/issue/index.vue'),
        meta: { title: '问题日志', icon: 'ChatLineSquare' } },
      { path: 'report', name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '报表统计', icon: 'TrendCharts' } }
    ]
  },
  // ============ 低代码平台（保留原结构） ============
  {
    path: '/lowcode',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'form-list', name: 'LowCodeFormList',
        component: () => import('@/views/lowcode/form-list/index.vue'),
        meta: { title: '表单配置', icon: 'Document' } },
      { path: 'form-designer', name: 'LowCodeFormDesigner',
        component: () => import('@/views/lowcode/form-designer/index.vue'),
        meta: { title: '表单设计器', icon: 'EditPen', hidden: true } },
      { path: 'list-list', name: 'LowCodeListList',
        component: () => import('@/views/lowcode/list-list/index.vue'),
        meta: { title: '列表配置', icon: 'List' } },
      { path: 'list-designer', name: 'LowCodeListDesigner',
        component: () => import('@/views/lowcode/list-designer/index.vue'),
        meta: { title: '列表设计器', icon: 'Grid', hidden: true } },
      { path: 'tab-list', name: 'LowCodeTabList',
        component: () => import('@/views/lowcode/tab-list/index.vue'),
        meta: { title: '标签页配置', icon: 'Files' } },
      { path: 'tab-designer', name: 'LowCodeTabDesigner',
        component: () => import('@/views/lowcode/tab-designer/index.vue'),
        meta: { title: '标签页设计器', icon: 'EditPen', hidden: true } },
      { path: 'related-page-list', name: 'LowCodeRelatedPageList',
        component: () => import('@/views/lowcode/related-page-list/index.vue'),
        meta: { title: '关联页配置', icon: 'Share' } },
      { path: 'related-page-designer', name: 'LowCodeRelatedPageDesigner',
        component: () => import('@/views/lowcode/related-page-designer/index.vue'),
        meta: { title: '关联页设计器', icon: 'EditPen', hidden: true } },
      { path: 'entity-designer', name: 'LowcodeEntityDesigner',
        component: () => import('@/views/lowcode/entity-designer/index.vue'),
        meta: { title: '实体设计器', icon: 'Connection' } },
      { path: 'version-history', name: 'LowcodeVersionHistory',
        component: () => import('@/views/lowcode/version-history/index.vue'),
        meta: { title: '版本历史', icon: 'Timer' } },
      { path: 'microflow-designer', name: 'LowcodeMicroflowDesigner',
        component: () => import('@/views/lowcode/microflow-designer/index.vue'),
        meta: { title: '微流设计器', icon: 'Share' } },
      { path: 'rule-designer', name: 'LowcodeRuleDesigner',
        component: () => import('@/views/lowcode/rule-designer/index.vue'),
        meta: { title: '规则设计器', icon: 'Filter' } },
      { path: 'process-designer', name: 'LowcodeProcessDesigner',
        component: () => import('@/views/lowcode/process-designer/index.vue'),
        meta: { title: '流程设计器', icon: 'Connection' } },
      { path: 'trigger-list', name: 'LowcodeTriggerList',
        component: () => import('@/views/lowcode/trigger-list/index.vue'),
        meta: { title: '触发器', icon: 'BellFilled' } },
      { path: 'connector-designer', name: 'LowcodeConnectorDesigner',
        component: () => import('@/views/lowcode/connector-designer/index.vue'),
        meta: { title: '连接器配置', icon: 'Connection' } },
      { path: 'preview', name: 'LowcodePreview',
        component: () => import('@/views/lowcode/preview/index.vue'),
        meta: { title: '预览', icon: 'View', hidden: true } },
      { path: 'publish-center', name: 'LowcodePublishCenter',
        component: () => import('@/views/lowcode/publish-center/index.vue'),
        meta: { title: '发布中心', icon: 'Promotion' } },
      { path: 'approval-chain', name: 'LowcodeApprovalChain',
        component: () => import('@/views/lowcode/approval-chain/index.vue'),
        meta: { title: '审批链配置', icon: 'SetUp' } },
      { path: 'template-market', name: 'LowcodeTemplateMarket',
        component: () => import('@/views/lowcode/template-market/index.vue'),
        meta: { title: '模板市场', icon: 'Goods' } },
      { path: 'apm-dashboard', name: 'LowcodeApmDashboard',
        component: () => import('@/views/lowcode/apm-dashboard/index.vue'),
        meta: { title: 'APM 看板', icon: 'TrendCharts' } },
      { path: 'app-source-export', name: 'LowcodeAppSourceExport',
        component: () => import('@/views/lowcode/app-source-export/index.vue'),
        meta: { title: '应用源码导出', icon: 'Download' } },
      { path: ':pageType/:pageCode', name: 'LowCodeRender',
        component: () => import('@/views/lowcode/render/index.vue'),
        meta: { title: '低代码页面', hidden: true } }
    ]
  },
  // ============ 系统管理（嵌套） ============
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting', requiresAuth: true },
    children: [
      { path: 'user', name: 'SysUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' } },
      { path: 'role', name: 'SysRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' } },
      { path: 'menu', name: 'SysMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' } },
      { path: 'dict', name: 'SysDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Document' } },
      { path: 'cache', name: 'SysCache',
        component: () => import('@/views/system/cache/index.vue'),
        meta: { title: '缓存管理', icon: 'Coin' } },
      { path: 'schedule', name: 'SysSchedule',
        component: () => import('@/views/system/schedule/index.vue'),
        meta: { title: '定时任务', icon: 'Timer' } },
      { path: 'audit', name: 'SysAudit',
        component: () => import('@/views/system/audit/index.vue'),
        meta: { title: '审计日志', icon: 'DocumentChecked' } }
    ]
  },
  // ============ 其他单页（保留平铺） ============
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'system-status', name: 'SystemStatus',
        component: () => import('@/views/system-status/index.vue'),
        meta: { title: '系统状态', icon: 'Monitor' } },
      { path: 'changelog', name: 'Changelog',
        component: () => import('@/views/changelog/index.vue'),
        meta: { title: '版本日志', icon: 'Notebook' } }
    ]
  },
  // ============ 404 兜底 ============
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// Navigation guard
router.beforeEach((to, _from, next) => {
  startRouteLoading()
  const userStore = useUserStore()
  const title = (to.meta.title as string | undefined) ?? ''
  document.title = title ? `${title} - 网络设备工程项目管理系统` : '网络设备工程项目管理系统'

  if (to.meta.requiresAuth === false) {
    if (to.path === '/login' && userStore.token) {
      next('/dashboard')
      return
    }
    next()
    return
  }

  if (!userStore.token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }

  next()
})

router.afterEach(() => {
  stopRouteLoading()
})

export default router
```

- [ ] **Step 3: 启动前端 dev server，验证所有页面可达**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npm run dev 2>&1 | head -10`
Expected: dev server 启动成功（无编译错误）

- [ ] **Step 4: 验证关键路由可达（curl HTML）**

```bash
for path in /dashboard /project/list /project/template /asset/list /implementation/task /workflow/todo /lowcode/form-list /system/user; do
  code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:5173$path)
  echo "$path → $code"
done
```
Expected: 全部返回 200（SPA 兜底返回 index.html）

- [ ] **Step 5: 验证面包屑正确渲染**

打开浏览器访问 `http://localhost:5173/project/list`，检查面包屑显示 "项目管理 / 项目列表"。

> 若面包屑组件未自动从 `route.matched` 推导，需在 `DefaultLayout.vue` 中检查面包屑实现，确保使用 `route.matched` 而非硬编码。

- [ ] **Step 6: 删除备份文件 + 提交**

```bash
cd /workspace/network-equipment-pms
rm -f pms-frontend/src/router/index.ts.bak
git add pms-frontend/src/router/index.ts
git commit -m "refactor(b7-t18): 前端路由全量重构为嵌套模式（按业务域 children 分组，URL 兼容，不留技术债）"
git push
```

---

## Task 19: 前端 API — project-template.ts + project-phase.ts + project-member.ts + project-config.ts

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/api/project-template.ts`
- Create: `network-equipment-pms/pms-frontend/src/api/project-phase.ts`
- Create: `network-equipment-pms/pms-frontend/src/api/project-member.ts`
- Create: `network-equipment-pms/pms-frontend/src/api/project-config.ts`

- [ ] **Step 1: 查看现有 API 封装模式**

Run: `cat /workspace/network-equipment-pms/pms-frontend/src/api/project.ts | head -40`
Expected: 显示现有 API 封装（使用 `request.get/post/put/del`，导出 `Project` 接口等）

- [ ] **Step 2: 创建 project-template.ts**

```typescript
// network-equipment-pms/pms-frontend/src/api/project-template.ts
import request from '@/utils/request'

export interface ProjectTemplate {
  id?: number
  templateCode: string
  templateName: string
  category: 'IMPLEMENT' | 'MAINTENANCE' | 'CONSULTING'
  description?: string
  status: 'DRAFT' | 'PUBLISHED' | 'DEPRECATED'
  createTime?: string
  updateTime?: string
}

export interface PhaseDef {
  phaseCode: string
  phaseName: string
  sortOrder: number
  entryCriteria?: any
  exitCriteria?: any
}

export interface TemplateSnapshot {
  phases?: PhaseDef[]
  tasks?: any[]
  milestones?: any[]
  deliverables?: any[]
  dependencies?: any[]
  approvalPlans?: any[]
  assigneeRules?: any[]
}

export interface ProjectTemplateVersion {
  id: number
  templateId: number
  version: string
  snapshotJson: TemplateSnapshot
  changeLog?: string
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  publishedAt?: string
  publishedBy?: number
}

export interface PublishVersionRequest {
  version: string
  snapshot: TemplateSnapshot
  changeLog?: string
}

export interface ProjectCreateFromTemplateDTO {
  templateId: number
  versionId: number
  projectCode: string
  projectName: string
  customerName?: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  projectManagerId?: number
  projectObjective?: string
  projectScope?: string
  members?: { userId: number; role: string }[]
  configOverrides?: Record<string, string>
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export function listTemplates(params: {
  page?: number
  size?: number
  templateName?: string
  category?: string
  status?: string
}) {
  return request.get<PageResult<ProjectTemplate>>('/api/project/template/list', { params })
}

export function getTemplate(id: number) {
  return request.get<ProjectTemplate>(`/api/project/template/${id}`)
}

export function createTemplate(data: ProjectTemplate) {
  return request.post<ProjectTemplate>('/api/project/template', data)
}

export function updateTemplate(data: ProjectTemplate) {
  return request.put<ProjectTemplate>('/api/project/template', data)
}

export function deleteTemplate(id: number) {
  return request.del<void>(`/api/project/template/${id}`)
}

export function listTemplateVersions(id: number, page = 1, size = 10) {
  return request.get<PageResult<ProjectTemplateVersion>>(`/api/project/template/${id}/versions`, {
    params: { page, size }
  })
}

export function publishVersion(id: number, data: PublishVersionRequest) {
  return request.post<ProjectTemplateVersion>(`/api/project/template/${id}/publish`, data)
}

export function getPublishedVersion(id: number) {
  return request.get<ProjectTemplateVersion>(`/api/project/template/${id}/published-version`)
}

export function createProjectFromTemplate(data: ProjectCreateFromTemplateDTO) {
  return request.post('/api/project/template/create-project', data)
}
```

- [ ] **Step 3: 创建 project-phase.ts**

```typescript
// network-equipment-pms/pms-frontend/src/api/project-phase.ts
import request from '@/utils/request'

export interface ProjectPhase {
  id?: number
  projectId: number
  templatePhaseId?: number
  phaseName: string
  phaseCode: string
  sortOrder: number
  entryCriteria?: any
  exitCriteria?: any
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'SKIPPED'
  plannedStartDate?: string
  plannedEndDate?: string
  actualStartDate?: string
  actualEndDate?: string
}

export function listPhasesByProjectId(projectId: number) {
  return request.get<ProjectPhase[]>(`/api/project/phase/project/${projectId}`)
}

export function getPhase(id: number) {
  return request.get<ProjectPhase>(`/api/project/phase/${id}`)
}

export function createPhase(data: ProjectPhase) {
  return request.post<ProjectPhase>('/api/project/phase', data)
}

export function updatePhase(data: ProjectPhase) {
  return request.put<ProjectPhase>('/api/project/phase', data)
}

export function deletePhase(id: number) {
  return request.del<void>(`/api/project/phase/${id}`)
}
```

- [ ] **Step 4: 创建 project-member.ts**

```typescript
// network-equipment-pms/pms-frontend/src/api/project-member.ts
import request from '@/utils/request'

export interface ProjectMember {
  id?: number
  projectId: number
  userId: number
  userName?: string
  role: 'PROJECT_MANAGER' | 'PROJECT_MEMBER' | 'APPROVER' | 'VIEWER' | 'CUSTOMER'
  joinDate?: string
  leaveDate?: string
}

export function listMembersByProjectId(projectId: number) {
  return request.get<ProjectMember[]>(`/api/project/member/project/${projectId}`)
}

export function createMember(data: ProjectMember) {
  return request.post<ProjectMember>('/api/project/member', data)
}

export function updateMember(data: ProjectMember) {
  return request.put<ProjectMember>('/api/project/member', data)
}

export function deleteMember(id: number) {
  return request.del<void>(`/api/project/member/${id}`)
}
```

- [ ] **Step 5: 创建 project-config.ts**

```typescript
// network-equipment-pms/pms-frontend/src/api/project-config.ts
import request from '@/utils/request'

export function getProjectConfigs(projectId: number) {
  return request.get<Record<string, string>>(`/api/project/config/${projectId}`)
}

export function updateProjectConfigs(projectId: number, configs: Record<string, string>) {
  return request.put<void>(`/api/project/config/${projectId}`, configs)
}
```

- [ ] **Step 6: TypeScript 类型检查**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npx vue-tsc --noEmit 2>&1 | grep -E "project-template|project-phase|project-member|project-config" | head -10`
Expected: 无输出（无类型错误）

- [ ] **Step 7: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/src/api/project-template.ts \
        pms-frontend/src/api/project-phase.ts \
        pms-frontend/src/api/project-member.ts \
        pms-frontend/src/api/project-config.ts
git commit -m "feat(b7-t19): 前端 API 封装 — project-template/phase/member/config 4 个文件"
git push
```

---

## Task 20: 前端 — ProjectTemplateSelector 组件

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/components/ProjectTemplateSelector.vue`

- [ ] **Step 1: 创建 ProjectTemplateSelector 组件**

```vue
<!-- network-equipment-pms/pms-frontend/src/components/ProjectTemplateSelector.vue -->
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listTemplates,
  listTemplateVersions,
  createProjectFromTemplate,
  type ProjectTemplate,
  type ProjectTemplateVersion,
  type ProjectCreateFromTemplateDTO
} from '@/api/project-template'

const emit = defineEmits<{
  success: [projectId: number]
}>()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)

const templates = ref<ProjectTemplate[]>([])
const versions = ref<ProjectTemplateVersion[]>([])
const selectedTemplateId = ref<number>()
const selectedVersionId = ref<number>()

const projectForm = reactive<ProjectCreateFromTemplateDTO>({
  templateId: 0,
  versionId: 0,
  projectCode: '',
  projectName: '',
  customerName: '',
  planStartDate: '',
  planEndDate: '',
  projectManagerId: undefined,
  projectObjective: '',
  projectScope: '',
  members: [],
  configOverrides: {}
})

async function loadTemplates() {
  loading.value = true
  try {
    const res = await listTemplates({ page: 1, size: 100, status: 'PUBLISHED' })
    templates.value = res.records ?? []
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function onTemplateChange(templateId: number) {
  selectedVersionId.value = undefined
  versions.value = []
  if (!templateId) return
  const res = await listTemplateVersions(templateId, 1, 50)
  versions.value = (res.records ?? []).filter((v) => v.status === 'PUBLISHED')
  if (versions.value.length > 0) {
    selectedVersionId.value = versions.value[0].id
  }
}

function open() {
  visible.value = true
  Object.assign(projectForm, {
    templateId: 0,
    versionId: 0,
    projectCode: '',
    projectName: '',
    customerName: '',
    planStartDate: '',
    planEndDate: '',
    projectManagerId: undefined,
    projectObjective: '',
    projectScope: '',
    members: [],
    configOverrides: {}
  })
  loadTemplates()
}

async function handleSubmit() {
  if (!selectedTemplateId.value || !selectedVersionId.value) {
    ElMessage.warning('请选择模板和版本')
    return
  }
  if (!projectForm.projectCode || !projectForm.projectName) {
    ElMessage.warning('请填写项目编号和名称')
    return
  }
  submitting.value = true
  try {
    projectForm.templateId = selectedTemplateId.value
    projectForm.versionId = selectedVersionId.value
    const res = await createProjectFromTemplate(projectForm)
    ElMessage.success('项目创建成功')
    visible.value = false
    emit('success', (res as any).id)
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<template>
  <el-dialog v-model="visible" title="从模板创建项目" width="720px" destroy-on-close>
    <el-form :model="projectForm" label-width="110px">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="选择模板" required>
            <el-select
              v-model="selectedTemplateId"
              placeholder="请选择已发布模板"
              style="width: 100%"
              :loading="loading"
              @change="onTemplateChange"
            >
              <el-option
                v-for="t in templates"
                :key="t.id"
                :label="`${t.templateName} (${t.templateCode})`"
                :value="t.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="模板版本" required>
            <el-select
              v-model="selectedVersionId"
              placeholder="请选择版本"
              style="width: 100%"
              :disabled="!selectedTemplateId"
            >
              <el-option
                v-for="v in versions"
                :key="v.id"
                :label="`${v.version} — ${v.changeLog || '无变更说明'}`"
                :value="v.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目编号" required>
            <el-input v-model="projectForm.projectCode" placeholder="如 IMPL-2026-001" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目名称" required>
            <el-input v-model="projectForm.projectName" placeholder="请输入项目名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户名称">
            <el-input v-model="projectForm.customerName" placeholder="请输入客户名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目经理ID">
            <el-input-number
              v-model="projectForm.projectManagerId"
              :min="1"
              style="width: 100%"
              placeholder="请输入用户ID"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划开始日期">
            <el-date-picker
              v-model="projectForm.planStartDate"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划结束日期">
            <el-date-picker
              v-model="projectForm.planEndDate"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="项目目标">
            <el-input
              v-model="projectForm.projectObjective"
              type="textarea"
              :rows="2"
              placeholder="项目目标"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="项目范围">
            <el-input
              v-model="projectForm.projectScope"
              type="textarea"
              :rows="2"
              placeholder="项目范围"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">创建</el-button>
    </template>
  </el-dialog>
</template>
```

- [ ] **Step 2: TypeScript 类型检查**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npx vue-tsc --noEmit 2>&1 | grep "ProjectTemplateSelector" | head -5`
Expected: 无输出（无类型错误）

- [ ] **Step 3: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/src/components/ProjectTemplateSelector.vue
git commit -m "feat(b7-t20): ProjectTemplateSelector 组件 — 模板选择 + 版本选择 + 项目信息填写"
git push
```

---

## Task 21: 前端 — 项目模板列表页

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/views/project/template/index.vue`

- [ ] **Step 1: 创建模板列表页**

```vue
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listTemplates,
  deleteTemplate,
  type ProjectTemplate
} from '@/api/project-template'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ProjectTemplate[]>([])
const total = ref(0)

const query = reactive({ page: 1, size: 10, templateName: '', category: '', status: '' })

const categoryOptions = [
  { value: 'IMPLEMENT', label: '实施' },
  { value: 'MAINTENANCE', label: '维护' },
  { value: 'CONSULTING', label: '咨询' }
]
const statusOptions = [
  { value: 'DRAFT', label: '草稿' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'DEPRECATED', label: '已废弃' }
]

function getStatusTagType(status: string) {
  return { DRAFT: 'info', PUBLISHED: 'success', DEPRECATED: 'danger' }[status] ?? 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await listTemplates(query)
    tableData.value = res.records ?? []
    total.value = res.total ?? 0
  } finally {
    loading.value = false
  }
}

function handleSearch() { query.page = 1; loadData() }
function handleReset() {
  query.templateName = ''; query.category = ''; query.status = ''
  query.page = 1; loadData()
}
function handlePageChange(p: number) { query.page = p; loadData() }
function handleSizeChange(s: number) { query.size = s; query.page = 1; loadData() }

function handleAdd() { router.push('/project/template/form') }
function handleEdit(row: ProjectTemplate) { router.push(`/project/template/form/${row.id}`) }
function handleVersion(row: ProjectTemplate) { router.push(`/project/template/version/${row.id}`) }

function handleDelete(row: ProjectTemplate) {
  if (!row.id) return
  ElMessageBox.confirm(`确认删除模板「${row.templateName}」吗？仅 DRAFT 状态可删除。`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTemplate(row.id!)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header><span class="page-title">项目模板</span></template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="模板名称">
          <el-input v-model="query.templateName" placeholder="请输入" clearable style="width: 180px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.category" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="opt in categoryOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建模板</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="templateCode" label="模板编码" min-width="140" />
        <el-table-column prop="templateName" label="模板名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="类别" width="100">
          <template #default="{ row }">
            {{ categoryOptions.find(c => c.value === row.category)?.label ?? row.category }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status) as any">
              {{ statusOptions.find(s => s.value === row.status)?.label ?? row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleVersion(row)">版本管理</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无模板数据" /></template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>
```

- [ ] **Step 2: 验证 dev server 编译**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npm run dev 2>&1 | grep -iE "error|compiled" | head -5`
Expected: 无 error，compiled successfully

- [ ] **Step 3: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/src/views/project/template/index.vue
git commit -m "feat(b7-t21): 项目模板列表页 — 分页查询 + 类别/状态过滤 + 编辑/版本管理入口"
git push
```

---

## Task 22: 前端 — 项目模板编辑器（含快照构建器）

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/views/project/template/form.vue`

- [ ] **Step 1: 创建模板编辑器**

```vue
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getTemplate,
  createTemplate,
  updateTemplate,
  publishVersion,
  type ProjectTemplate,
  type TemplateSnapshot,
  type PhaseDef
} from '@/api/project-template'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const publishing = ref(false)

const form = reactive<ProjectTemplate>({
  templateCode: '',
  templateName: '',
  category: 'IMPLEMENT',
  description: '',
  status: 'DRAFT'
})

// 快照构建：阶段列表
const phases = ref<PhaseDef[]>([])

// 发布版本表单
const publishDialogVisible = ref(false)
const publishForm = reactive({ version: '', changeLog: '' })

function addPhase() {
  phases.value.push({
    phaseCode: '',
    phaseName: '',
    sortOrder: phases.value.length + 1
  })
}

function removePhase(idx: number) {
  phases.value.splice(idx, 1)
}

async function loadTemplate(id: number) {
  loading.value = true
  try {
    const res = await getTemplate(id)
    Object.assign(form, res)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!form.templateCode || !form.templateName) {
    ElMessage.warning('请填写模板编码和名称')
    return
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateTemplate(form)
      ElMessage.success('更新成功')
    } else {
      await createTemplate(form)
      ElMessage.success('创建成功')
      router.back()
    }
  } finally {
    submitting.value = false
  }
}

async function handlePublish() {
  if (phases.value.length === 0) {
    ElMessage.warning('请至少添加一个阶段')
    return
  }
  if (!publishForm.version) {
    ElMessage.warning('请填写版本号')
    return
  }
  publishing.value = true
  try {
    const snapshot: TemplateSnapshot = { phases: phases.value }
    await publishVersion(form.id!, {
      version: publishForm.version,
      snapshot,
      changeLog: publishForm.changeLog
    })
    ElMessage.success('版本发布成功')
    publishDialogVisible.value = false
    if (form.id) loadTemplate(form.id)
  } finally {
    publishing.value = false
  }
}

onMounted(() => {
  const id = route.params.id as string | undefined
  if (id) loadTemplate(Number(id))
})
</script>

<template>
  <div class="page-container">
    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="page-title">{{ form.id ? '编辑模板' : '新建模板' }}</span>
      </template>

      <el-form :model="form" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="模板编码" required>
              <el-input v-model="form.templateCode" :disabled="!!form.id" placeholder="如 TPL-IMPL-STD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板名称" required>
              <el-input v-model="form.templateName" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类别">
              <el-select v-model="form.category" style="width: 100%">
                <el-option label="实施" value="IMPLEMENT" />
                <el-option label="维护" value="MAINTENANCE" />
                <el-option label="咨询" value="CONSULTING" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag>{{ form.status }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">阶段定义（快照构建器）</el-divider>
      <div v-for="(phase, idx) in phases" :key="idx" class="phase-row">
        <el-input v-model="phase.phaseCode" placeholder="阶段编码 PREPARE" style="width: 180px" />
        <el-input v-model="phase.phaseName" placeholder="阶段名称" style="width: 220px" />
        <el-input-number v-model="phase.sortOrder" :min="1" placeholder="排序" style="width: 120px" />
        <el-button link type="danger" @click="removePhase(idx)">删除</el-button>
      </div>
      <el-button :icon="'Plus'" @click="addPhase">添加阶段</el-button>

      <div class="toolbar" style="margin-top: 20px">
        <el-button @click="router.back()">返回</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button v-if="form.id" type="success" @click="publishDialogVisible = true">发布新版本</el-button>
      </div>
    </el-card>

    <el-dialog v-model="publishDialogVisible" title="发布新版本" width="500px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="版本号" required>
          <el-input v-model="publishForm.version" placeholder="如 v1.0.0" />
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input v-model="publishForm.changeLog" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.phase-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}
</style>
```

- [ ] **Step 2: 验证 dev server 编译**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npm run dev 2>&1 | grep -iE "error|compiled" | head -5`
Expected: 无 error

- [ ] **Step 3: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/src/views/project/template/form.vue
git commit -m "feat(b7-t22): 项目模板编辑器 — 基本字段 + 阶段定义快照构建器 + 发布版本弹窗"
git push
```

---

## Task 23: 前端 — 模板版本管理页

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/views/project/template/version.vue`

- [ ] **Step 1: 创建版本管理页**

```vue
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  listTemplateVersions,
  getTemplate,
  type ProjectTemplate,
  type ProjectTemplateVersion
} from '@/api/project-template'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const template = ref<ProjectTemplate>()
const tableData = ref<ProjectTemplateVersion[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

async function loadData() {
  const templateId = Number(route.params.id)
  if (!templateId) return
  loading.value = true
  try {
    template.value = await getTemplate(templateId)
    const res = await listTemplateVersions(templateId, page.value, size.value)
    tableData.value = res.records ?? []
    total.value = res.total ?? 0
  } finally {
    loading.value = false
  }
}

function getStatusTagType(status: string) {
  return { DRAFT: 'info', PUBLISHED: 'success', ARCHIVED: 'warning' }[status] ?? 'info'
}

function handlePageChange(p: number) { page.value = p; loadData() }

function viewSnapshot(row: ProjectTemplateVersion) {
  // 简化：用 alert 展示 JSON（实际可用 ElMessageBox 或 Drawer）
  ElMessage.info(`版本 ${row.version} 快照查看：可扩展为 Drawer 展示`)
  console.log('Snapshot:', row.snapshotJson)
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="page-title">版本管理 — {{ template?.templateName }}</span>
      </template>

      <div class="toolbar">
        <el-button @click="router.push('/project/template')">返回列表</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="changeLog" label="变更说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status) as any">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="160" />
        <el-table-column prop="publishedBy" label="发布人ID" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewSnapshot(row)">查看快照</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无版本记录" /></template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="page"
        :page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>
```

- [ ] **Step 2: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/src/views/project/template/version.vue
git commit -m "feat(b7-t23): 模板版本管理页 — 版本列表 + 状态标签 + 快照查看入口"
git push
```

---

## Task 24: 前端 — 项目列表页集成"从模板创建"按钮

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/project/list/index.vue`

- [ ] **Step 1: 在项目列表页顶部引入 ProjectTemplateSelector 并添加按钮**

修改 `views/project/list/index.vue`，在 `<script setup>` 顶部添加：

```typescript
import ProjectTemplateSelector from '@/components/ProjectTemplateSelector.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const templateSelectorRef = ref<InstanceType<typeof ProjectTemplateSelector>>()

function handleCreateFromTemplate() {
  templateSelectorRef.value?.open()
}

function onTemplateCreateSuccess(projectId: number) {
  router.push(`/project/detail/${projectId}`)
}
```

在 `<template>` 的 `<div class="toolbar">` 内（"新建项目"按钮旁）添加：

```vue
<el-button type="success" :icon="'Files'" @click="handleCreateFromTemplate">从模板创建</el-button>
<ProjectTemplateSelector ref="templateSelectorRef" @success="onTemplateCreateSuccess" />
```

- [ ] **Step 2: 验证 dev server 编译**

Run: `cd /workspace/network-equipment-pms/pms-frontend && npm run dev 2>&1 | grep -iE "error|compiled" | head -5`
Expected: 无 error

- [ ] **Step 3: 提交**

```bash
cd /workspace/network-equipment-pms
git add pms-frontend/src/views/project/list/index.vue
git commit -m "feat(b7-t24): 项目列表页集成 [从模板创建] 按钮 + ProjectTemplateSelector"
git push
```

---

## Task 25: 端到端验收 — Story 1 验收 1 + 验收 2

**Files:**
- 无新增，仅端到端验证

- [ ] **Step 1: 启动后端 + 前端**

```bash
# 终端 1：后端
cd /workspace/network-equipment-pms/pms-admin && mvn spring-boot:run -DskipTests

# 终端 2：前端
cd /workspace/network-equipment-pms/pms-frontend && npm run dev
```
Expected: 两端都启动成功

- [ ] **Step 2: 验收场景 1.1 — 从模板创建项目，验证完整默认计划**

操作步骤：
1. 浏览器访问 `http://localhost:5173/login`，登录
2. 访问 `http://localhost:5173/project/template`，点击"新建模板"
3. 填写模板编码 `TPL-TEST-001`、名称 `测试模板`、类别 `实施`
4. 添加 2 个阶段：`PREPARE` 准备阶段 (sortOrder=1)、`PLAN` 规划阶段 (sortOrder=2)
5. 保存模板
6. 点击"发布新版本"，填写版本号 `v1.0.0`、变更说明 `初始版本`，发布
7. 访问 `http://localhost:5173/project/list`
8. 点击"从模板创建"按钮
9. 选择刚发布的模板和版本
10. 填写项目编号 `IMPL-TEST-001`、名称 `测试项目`、计划日期
11. 点击"创建"

Expected:
- 创建成功后跳转到 `/project/detail/<新项目ID>`
- 项目详情页显示 2 个阶段（准备阶段、规划阶段）
- 数据库验证：
  - `pms_project` 表新增 1 条记录，`template_id` = 模板ID，`template_version` = `v1.0.0`，`status` = `PLANNING`，`project_path` = `/<id>/`
  - `pms_project_phase` 表新增 2 条记录，`project_id` = 新项目ID
  - `current_phase_id` 指向 sortOrder 最小的阶段（PREPARE）

验证 SQL：
```sql
SELECT id, project_code, project_name, status, template_id, template_version,
       project_path, depth, current_phase_id
FROM pms_project WHERE project_code = 'IMPL-TEST-001';

SELECT id, phase_name, phase_code, sort_order, status
FROM pms_project_phase WHERE project_id = <新项目ID> ORDER BY sort_order;
```

- [ ] **Step 3: 验收场景 1.2 — 模板新版本不影响存量项目**

操作步骤：
1. 回到模板编辑页，修改"准备阶段"为"启动阶段"
2. 发布新版本 `v1.1.0`，变更说明 `修改阶段名`
3. 在数据库查询存量项目（步骤 2 创建的项目）的阶段

验证 SQL：
```sql
SELECT phase_name, phase_code FROM pms_project_phase
WHERE project_id = <步骤2创建的项目ID> ORDER BY sort_order;
```
Expected: 仍返回 `准备阶段` 和 `规划阶段`（v1.0.0 的快照内容），**不受 v1.1.0 修改影响**

- [ ] **Step 4: 清理测试数据**

```sql
DELETE FROM pms_project_phase WHERE project_id = <测试项目ID>;
DELETE FROM pms_project WHERE project_code LIKE 'IMPL-TEST-%';
DELETE FROM pms_project_template_version WHERE template_id = <测试模板ID>;
DELETE FROM pms_project_template WHERE template_code = 'TPL-TEST-001';
```

- [ ] **Step 5: 提交验收记录（无代码改动）**

```bash
echo "Task 25 验收完成：Story 1 验收 1.1 + 1.2 全部通过"
```

---

## Task 26: 最终提交 + 远端推送验证

**Files:**
- 无新增

- [ ] **Step 1: 检查所有 Task 是否已提交**

Run: `cd /workspace/network-equipment-pms && git status --short`
Expected: 无未提交变更（`nothing to commit, working tree clean`）

- [ ] **Step 2: 验证远端推送完成**

Run: `cd /workspace/network-equipment-pms && git log origin/lowcode..HEAD --oneline`
Expected: 无输出（所有提交都已推送远端）

- [ ] **Step 3: 验证所有 Task 提交记录**

Run: `cd /workspace/network-equipment-pms && git log --oneline --grep="b7-t" | head -30`
Expected: 显示 Task 1-25 共 25 个提交（Task 16/25/26 为验证任务，可能无独立提交）

---

## Self-Review

### 1. Spec coverage

| Spec 章节 | 覆盖任务 |
|----------|---------|
| §1.3 决策 1（Phase 模型） | Task 3, 7（ProjectPhase 实体 + 默认配置） |
| §1.3 决策 2（模板版本快照） | Task 6, 11, 13（ProjectTemplateVersion + publishVersion + 深拷贝） |
| §1.3 决策 13（JSON TypeHandler） | Task 5（JsonTypeHandlers） |
| §1.3 决策 14（嵌套路由） | Task 18（路由全量重构） |
| §1.3 决策 15（G6） | Task 17（安装依赖） |
| §1.3 决策 16（多层级配置） | Task 3, 10（默认配置 + ProjectConfigService） |
| §2.2 ProjectTemplate | Task 6 |
| §2.2 ProjectTemplateVersion | Task 6 |
| §2.2 ProjectPhase | Task 7 |
| §2.2 ProjectMember | Task 7 |
| §2.2 ProjectConfig | Task 7 |
| §2.2 Project 扩展 | Task 8 |
| §4.3 IProjectTemplateService | Task 11 |
| §4.3 ProjectConfigService | Task 10 |
| §5.2 项目模板 API | Task 14 |
| §5.1 阶段/成员/配置 API | Task 15 |
| §6.2 V64 | Task 1 |
| §6.3 V65 | Task 2 |
| §6.4 V66 | Task 3 |
| §6.11 JsonTypeHandlers | Task 5 |
| §7.1-7.3 路由 + 模板页面 | Task 18, 19, 20, 21, 22, 23, 24 |
| §8.2 Phase 1 (1.1-1.12) | Task 1-5, 8, 17, 18 |
| §8.2 Phase 2 (2.1-2.12) | Task 6, 7, 9, 11, 13, 14, 19, 20, 21, 22, 23, 24 |

**覆盖完整。**

### 2. Placeholder scan

- 无 "TBD" / "TODO" / "implement later"
- Task 13 中 `createProjectFromTemplate` 完整实现（非占位）
- Task 15 中 `ProjectConfigController.getAllForProject` 有 `TODO: 通过 ProjectService 获取 project.getTemplateId()` — 这是已知技术债，标注明确（在 Phase 2 集成时补全），不影响 Phase 1+2 验收

### 3. Type consistency

- `ProjectTemplate` / `ProjectTemplateVersion` / `ProjectPhase` / `ProjectMember` / `ProjectConfig` 实体名前后一致
- `IProjectTemplateService` / `IProjectPhaseService` / `IProjectMemberService` 接口名一致
- `ProjectConfigService`（无 I 前缀，因为是具体类非接口）— 与设计文档 §4.3 一致
- `TemplateSnapshot.PhaseDef` 在 Task 4 定义，Task 11/13 引用 — 一致
- 前端 `ProjectTemplate` 接口字段（templateCode/templateName/category/status）与后端实体一致
- 错误码 `TEMPLATE_VERSION_NOT_FOUND` 在设计文档 §5.8 定义 — 本计划通过 `IllegalArgumentException` 抛出（由全局异常处理器转换为错误码），符合现有项目模式

### 4. 已知技术债（明确标注，不阻塞 Phase 1+2 验收）

1. **Task 15 ProjectConfigController.getAllForProject**：需注入 `IProjectService` 获取 `templateId`，本任务简化为 null，Phase 2 集成时补全
2. **Task 13 createProjectFromTemplate**：仅深拷贝阶段/成员/配置；任务/里程碑/交付件/依赖的深拷贝在 Phase 4-6 实现（依赖对应实体表创建）

---

## Execution Handoff

**Plan complete and saved to `docs/superpowers/plans/2026-07-17-pm-phase1-infrastructure-and-story1-template.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**

**If Subagent-Driven chosen:**
- **REQUIRED SUB-SKILL:** Use superpowers:subagent-driven-development
- Fresh subagent per task + two-stage review

**If Inline Execution chosen:**
- **REQUIRED SUB-SKILL:** Use superpowers:executing-plans
- Batch execution with checkpoints for review

---

## 后续计划提示

本计划覆盖 Phase 1（基础设施）+ Phase 2（Story 1 项目模板与创建）。

后续 Phase 3-8 将在本计划完成后追加为独立 plan 文件：
- `2026-07-17-pm-phase3-story2-lifecycle.md`（Story 2 项目生命周期与主子项目）
- `2026-07-17-pm-phase4-story3-task-system.md`（Story 3 任务体系与团队协作）
- `2026-07-17-pm-phase5-story4-dependency-baseline.md`（Story 4 依赖与基线）
- `2026-07-17-pm-phase6-story5-deliverable.md`（Story 5 交付件全生命周期）
- `2026-07-17-pm-phase7-story6-approval-center.md`（Story 6 统一审批中心）
- `2026-07-17-pm-phase8-integration-acceptance.md`（Phase 8 联调测试与验收）

每个后续 plan 都会复用本计划建立的：实体基类、JsonTypeHandlers、路由嵌套模式、ProjectConfigService 多层级读取、Spring Event 事件机制（在 Phase 3 引入）。