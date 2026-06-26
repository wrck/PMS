# 数据库概览

## 1. 模块定位

PMS-security 是**纯工具库**，不直接管理任何数据库表。

> ⚠️ **重要纠正**：旧版本文档描述的 `user_info`、`tb_sys_log` 关联表、BCrypt 密码存储、审计日志表结构等内容均为虚构。本模块源码中不存在任何数据库表定义、Mapper、DAO 或 Service 代码。

详见 [no-database.md](no-database.md)。

---

## 2. 无数据库表

| 项 | 值 |
|----|----|
| 数据库表数量 | 0 |
| Mapper 文件数量 | 0 |
| DAO 接口数量 | 0 |
| Service 数量 | 0 |

---

## 3. 间接数据库访问

本模块的 `SQLParser.getCurrentDbType(DataSource)` 方法接受外部传入的 `DataSource`，用于查询数据库类型（`DatabaseProductName`），但：

- 本模块**不持有**任何 DataSource
- 本模块**不执行**任何 SQL 查询
- DataSource 由调用方（如 PMS-struts/PMS-springmvc）传入

---

## 4. 密码存储说明

> 旧文档虚构了 BCrypt 密码存储相关内容。实际密码存储与校验逻辑在 **core 模块**（Shiro 集成）中，不在本模块。

本模块的 `PasswordInterceptor` 仅定义抽象的 `isNeedRedirect()` 方法，具体密码过期检查逻辑由 core 模块子类实现，可能涉及数据库查询，但那是 core 模块的职责。

---

## 5. 相关文档

| 文档 | 说明 |
|------|------|
| [no-database.md](no-database.md) | 无数据库说明 |
| [../audit/audit-modules.md](../audit/audit-modules.md) | 文档审计报告 |
