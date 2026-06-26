# core 模块 — 索引分析

> 本文档分析 core 模块管理的 `t_` 前缀表与 `fnd_` 前缀表的索引策略，评估索引覆盖度与优化建议。
> 数据库：core 主数据源由 `jdbc.properties` 配置（dev=`dppms_d365`，release=`dppms_d365`）

---

## 1. 索引总览

### 1.1 t_ 前缀表索引统计

| 表名 | 主键 | 唯一索引 | 普通索引 | 外键索引 | 总索引数 |
|------|------|---------|---------|---------|---------|
| `t_user` | ✓ (user_id) | ✓ (user_name) | - | - | 2 |
| `t_user_info` | ✓ (id) | - | ✓ (workNo, compID, depID, jobID, reportTo, wfreportTo, user_id) | - | 8 |
| `t_user_role` | ✓ (id) | - | ✓ (user_id, role_id) | - | 3 |
| `t_user_login_record` | ✓ (id) | - | - | - | 1 |
| `t_role` | ✓ (role_id) | - | ✓ (role_name) | - | 2 |
| `t_role_menu` | ✓ (id) | - | - | - | 1 |
| `t_role_permission` | ✓ (id) | - | ✓ (role_id, permission_id) | ✓ (role_id, permission_id) UNIQUE | 4 |
| `t_permission` | ✓ (permission_id) | - | ✓ (permission_name) | - | 2 |
| `t_menu` | ✓ (id) | - | - | - | 1 |
| `t_resource` | ✓ (id) | - | - | - | 1 |
| `t_company` | ✓ (id) | - | ✓ (compCode, adminID) | - | 3 |
| `t_department` | ✓ (id) | - | - | - | 1 |
| `t_dictionary` | ✓ (id) | - | - | - | 1 |
| `t_sys_log` | ✓ (id) | - | - | - | 1 |
| `t_file` | ✓ (id) | - | - | - | 1 |
| `t_file_type` | ✓ (id) | - | - | - | 1 |
| `t_down_log` | ✓ (id) | - | - | - | 1 |
| `t_mails` | ✓ (id) | - | - | - | 1 |
| `t_notify_template` | ✓ (id) | ✓ (templateCode) | - | - | 2 |
| `t_sync_log` | ✓ (id) | - | - | - | 1 |
| `t_sync_state` | ✓ (id) | - | - | - | 1 |
| `t_sys_variable` | ✓ (id) | - | - | - | 1 |

### 1.2 fnd_ 前缀表索引（PMS-struts 管理，core 引用）

| 表名 | 主键 | 唯一索引 | 普通索引 | 说明 |
|------|------|---------|---------|------|
| `fnd_basic_data` | ✓ (basicDataId) | - | ✓ (dataTypeId) | 字典项 |
| `fnd_basic_data_type` | ✓ (dataTypeId) | ✓ (dataTypeCode) | - | 字典类型 |

---

## 2. 关键表索引分析

### 2.1 t_user（用户表）

| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| `PRIMARY` | 主键 | `user_id` | 主键索引 |
| `user_name` | 唯一 | `user_name` | 登录用户名唯一 |

**查询场景覆盖**：
- ✓ 登录认证：`SELECT * FROM t_user WHERE user_name = ?`（命中 user_name 索引）
- ✓ 主键查询：`SELECT * FROM t_user WHERE user_id = ?`（命中主键）

**优化建议**：
- `status` 字段无索引，但值域小（0/1/2），全表扫描成本可控，无需加索引；
- `isSysUser` 同上，值域小，无需索引。

### 2.2 t_user_info（用户信息表）

| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| `PRIMARY` | 主键 | `id` | 主键索引 |
| `workNo` | 普通 | `workNo` | 工号查询 |
| `compID` | 普通 | `compID` | 按公司查询 |
| `depID` | 普通 | `depID` | 按部门查询 |
| `jobID` | 普通 | `jobID` | 按岗位查询 |
| `reportTo` | 普通 | `reportTo` | 按上级查询 |
| `wfreportTo` | 普通 | `wfreportTo` | 按职能上级查询 |
| `user_id` | 普通 | `user_id` | 关联 t_user |

**查询场景覆盖**：
- ✓ 按 userId 查询用户信息（命中 user_id 索引）
- ✓ 按公司查询员工列表（命中 compID 索引）
- ✓ 按部门查询员工列表（命中 depID 索引）
- ✓ 按工号查询（命中 workNo 索引）

### 2.3 t_user_role（用户-角色表）

| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| `PRIMARY` | 主键 | `id` | 主键索引 |
| `user_id` | 普通 | `user_id` | 按用户查询角色 |
| `role_id` | 普通 | `role_id` | 按角色查询用户 |

**查询场景覆盖**：
- ✓ 授权时按 userId + compId 查询角色（命中 user_id 索引）
- ✓ 按角色查询用户列表（命中 role_id 索引）

**优化建议**：
- 可考虑添加 `(user_id, comp_id)` 复合索引，优化授权查询。

### 2.4 t_role_permission（角色-权限表）

| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| `PRIMARY` | 主键 | `id` | 主键索引 |
| `role_id` | 普通 | `role_id` | 按角色查权限 |
| `permission_id` | 普通 | `permission_id` | 按权限查角色 |
| `(role_id, permission_id)` | 唯一 | `role_id, permission_id` | 防重复分配 |

**查询场景覆盖**：
- ✓ 授权时按 roleId 查询权限（命中 role_id 索引）
- ✓ 防重复分配（唯一索引保障）

---

## 3. 索引缺失分析

### 3.1 缺失索引的表

| 表名 | 缺失索引 | 影响查询 | 优化建议 |
|------|---------|---------|---------|
| `t_user_login_record` | `userId` | 按用户查登录记录 | 添加 `userId` 索引 |
| `t_user_login_record` | `loginTime` | 按时间范围查 | 添加 `loginTime` 索引 |
| `t_sys_log` | `userId` | 按用户查操作日志 | 添加 `userId` 索引 |
| `t_sys_log` | `operationTime` | 按时间范围查 | 添加 `operationTime` 索引 |
| `t_menu` | `pid` | 按父菜单查子菜单 | 添加 `pid` 索引 |
| `t_sync_log` | `syncType` | 按类型查同步日志 | 添加 `syncType` 索引 |
| `t_sync_log` | `startTime` | 按时间范围查 | 添加 `startTime` 索引 |
| `t_mails` | `status` | 查待发邮件 | 添加 `status` 索引 |
| `t_resource` | `url` | 按 URL 查权限 | 添加 `url` 索引 |

### 3.2 优先级排序

| 优先级 | 表名 | 索引 | 原因 |
|--------|------|------|------|
| P1 | `t_sys_log` | `operationTime` | 日志查询频繁，全表扫描慢 |
| P1 | `t_user_login_record` | `loginTime` | 安全审计按时间查询 |
| P2 | `t_menu` | `pid` | 菜单树构建频繁 |
| P2 | `t_mails` | `status` | MailerJob 扫描待发邮件 |
| P2 | `t_resource` | `url` | 过滤器链构建按 URL 查 |
| P3 | `t_sync_log` | `syncType, startTime` | 同步日志查询 |

---

## 4. 索引优化建议

### 4.1 日志表分区

`t_sys_log` 和 `t_user_login_record` 数据量大，建议按时间分区：

```sql
-- t_sys_log 按月分区
ALTER TABLE t_sys_log PARTITION BY RANGE (TO_DAYS(operationTime)) (
    PARTITION p202601 VALUES LESS THAN (TO_DAYS('2026-02-01')),
    PARTITION p202602 VALUES LESS THAN (TO_DAYS('2026-03-01')),
    PARTITION p202603 VALUES LESS THAN (TO_DAYS('2026-04-01')),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
```

### 4.2 复合索引优化

```sql
-- t_user_role: 优化授权查询
CREATE INDEX idx_user_comp ON t_user_role(user_id, comp_id);

-- t_sys_log: 优化按用户+时间查询
CREATE INDEX idx_user_time ON t_sys_log(userId, operationTime);

-- t_user_login_record: 优化按用户+时间查询
CREATE INDEX idx_user_time ON t_user_login_record(userId, loginTime);
```

### 4.3 索引维护

| 维护项 | 频率 | 说明 |
|--------|------|------|
| 索引统计信息更新 | 每周 | `ANALYZE TABLE` |
| 碎片整理 | 每月 | `OPTIMIZE TABLE` |
| 慢查询分析 | 持续 | 开启慢查询日志 |
| 索引使用率分析 | 每月 | 检查未使用索引 |

---

## 5. 索引与查询性能

### 5.1 查询性能评估

| 查询场景 | 表 | 索引覆盖 | 性能评估 |
|---------|-----|---------|---------|
| 登录认证 | t_user | ✓ user_name | 优 |
| 授权查询角色 | t_user_role | ✓ user_id | 优 |
| 授权查询权限 | t_role_permission | ✓ role_id | 优 |
| 菜单树构建 | t_menu | ✗ pid | 差（全表扫描） |
| 操作日志查询 | t_sys_log | ✗ operationTime | 差（全表扫描） |
| 待发邮件扫描 | t_mails | ✗ status | 差（全表扫描） |

### 5.2 全表扫描风险

| 表 | 数据量级 | 全表扫描风险 |
|----|---------|------------|
| `t_sys_log` | 大（>1万/月） | 高 |
| `t_user_login_record` | 大（>1万/月） | 高 |
| `t_menu` | 小（<千） | 低 |
| `t_mails` | 中（千/月） | 中 |
| `t_resource` | 小（<千） | 低 |

---

## 6. 相关文档

- [03-database 数据字典](complete-data-dictionary.md) — 表字段详情
- [er-diagram ER 图](er-diagram.md) — 表关系
- [05-standards 性能优化](../05-standards/performance-optimization.md) — 索引优化策略
