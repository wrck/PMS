# 前端功能验证测试用例

> **版本**: v1.0  
> **更新日期**: 2026-07-24  
> **维护说明**: 本目录按模块组织测试用例，新增功能时在对应模块文件追加用例

## 测试环境

| 服务 | 端口 | 启动方式 |
|------|------|----------|
| MySQL | 3307 | `Set-PmsEnvironment` (env.ps1) |
| Redis | 6379 | `redis-server` |
| 后端 | 9080 | `java -jar pms-admin/target/pms-admin-2.0.0-SNAPSHOT.jar` |
| 前端 | 5000 | `npm run dev` (pms-frontend 目录) |

**测试账号**: admin / admin123  
**后端环境变量**: 参见 [环境配置](./00-environment.md)

## 模块索引

| 编号 | 模块 | 文件 | 用例数 |
|------|------|------|--------|
| 01 | 登录与认证 | [01-auth.md](./01-auth.md) | 6 |
| 02 | 首页仪表盘 | [02-dashboard.md](./02-dashboard.md) | 2 |
| 03 | 项目管理 | [03-project.md](./03-project.md) | 30 |
| 04 | 实施管理 | [04-implementation.md](./04-implementation.md) | 4 |
| 05 | 交付件管理 | [05-deliverable.md](./05-deliverable.md) | 6 |
| 06 | 计划基线 | [06-baseline.md](./06-baseline.md) | 3 |
| 07 | 工作流与审批 | [07-workflow.md](./07-workflow.md) | 5 |
| 08 | 资产管理 | [08-asset.md](./08-asset.md) | 3 |
| 09 | 系统管理 | [09-system.md](./09-system.md) | 11 |
| 10 | 基础设施 | [10-infra.md](./10-infra.md) | 5 |
| 11 | 其他业务 | [11-others.md](./11-others.md) | 9 |

## 闭环测试流程

参见 [closed-loop-test.md](./closed-loop-test.md)

## 已知问题

参见 [known-issues.md](./known-issues.md)

## 更新日志

| 日期 | 更新内容 | 更新人 |
|------|----------|--------|
| 2026-07-24 | 初始版本，按模块拆分测试用例 | AI Assistant |
