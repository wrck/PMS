# 数据库说明

## 1. 概述

PMS-security 模块是**纯工具库**，不直接管理任何数据库表，不包含 MyBatis Mapper、iBATIS SqlMap 或任何 DAO/Service 层代码。

---

## 2. 模块性质

| 项 | 值 |
|----|----|
| 模块类型 | 纯工具库（jar） |
| 数据库表 | **无** |
| Mapper/DAO | **无** |
| ORM 依赖 | 无（pom.xml 未引入 mybatis/ibatis） |
| 数据源依赖 | 无（SQLParser 接受外部传入 DataSource，自身不持有） |

---

## 3. 与数据库的间接关系

虽然本模块无数据库表，但部分组件在调用方环境中会间接接触数据库：

| 组件 | 间接关系 |
|------|---------|
| `SQLParser.getCurrentDbType(DataSource)` | 接受外部 DataSource，查询 `DatabaseProductName` |
| `SQLParser.parseTables(sql, DbType)` | 解析 SQL 语法，不执行查询 |
| `PasswordInterceptor`（子类） | core 子类可能查询用户密码过期信息 |
| `CSRFTokenManager` | Token 存储在 HttpSession，不涉及数据库 |

---

## 4. 旧文档纠正

> 旧版 `database-overview.md` 中描述的 `user_info`、`tb_sys_log` 关联表、BCrypt 密码存储、审计日志表结构等内容均为虚构，本模块源码中不存在任何数据库相关代码。详见 [../audit/audit-modules.md](../audit/audit-modules.md)。

---

## 5. 相关文档

| 文档 | 说明 |
|------|------|
| [database-overview.md](database-overview.md) | 数据库概览（已修正） |
| [../04-mapping/filter-interceptor-matrix.md](../04-mapping/filter-interceptor-matrix.md) | 过滤器/拦截器矩阵 |
