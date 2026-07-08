# 验证插件清单（PLUGIN_REGISTRY）

> 本文件记录所有已批准的验证插件。新增/修改/删除插件必须更新本清单。
> 插件规范见 [RULES.md §5.2](../RULES.md#52-插件规范)。
>
> **校验和同步规则（RULES.md §5.6）**：任何对已注册插件文件的修改，必须原子性地同步更新本清单中的对应校验和。禁止分步提交。

## 已批准插件

| 文件名 | 版本 | 作者 | 用途 | 批准日期 | timeout_sec | on_timeout | criticality | checksum |
|--------|------|------|------|---------|-------------|------------|-------------|----------|
| `30-flyway-version-check.sh` | 1.0.0 | 主代理 | 检查 Flyway 迁移版本号是否冲突（同模块内 Vxx 重复） | 2026-07-08 | 30 | fail | critical | bc446970bdd55ba073d8bcfc175debf8851854d3fa5ce59b0845cb1bd618b7de |

## 插件配置字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `timeout_sec` | number | 插件执行超时阈值（秒），默认 30，上限 60 |
| `on_timeout` | `fail` \| `warn` | 超时时的处理策略：`fail`=中断验证（exit 1），`warn`=降级警告（继续执行） |
| `criticality` | `critical` \| `optional` | 插件重要性：`critical`=失败/超时必须中断，`optional`=失败/超时仅警告 |
| `checksum` | string | 插件文件 SHA-256 校验和，用于检测插件被篡改 |

**criticality 与 on_timeout 的组合行为**：

| criticality | on_timeout | 插件失败时 | 插件超时时 |
|-------------|------------|-----------|-----------|
| critical | fail | 中断验证（exit 1） | 中断验证（exit 1） |
| critical | warn | 中断验证（exit 1） | 降级警告（继续） |
| optional | fail | 降级警告（继续） | 中断验证（exit 1） |
| optional | warn | 降级警告（继续） | 降级警告（继续） |

> 说明：`criticality` 控制失败时的行为，`on_timeout` 控制超时时的行为。两者独立配置，组合出 4 种策略。

## 插件校验和列表

> 以下校验和由 `sha256sum` 生成。修改插件后必须重新生成并原子性更新此处。

```
30-flyway-version-check.sh: bc446970bdd55ba073d8bcfc175debf8851854d3fa5ce59b0845cb1bd618b7de
```

## 插件命名规范

- 文件名格式：`XX-<name>.sh`，XX 为两位数字（01-99），控制执行顺序。
- 数字越小越先执行（字母序）。
- 同一领域建议使用相近数字段（如 01-09 Java 检查，10-19 lowcode 检查，20-29 前端检查，30-39 数据库检查）。

## 新增插件流程

1. 子代理在工作树创建插件文件 `scripts/verify-plugins/XX-<name>.sh`。
2. 生成校验和：`sha256sum scripts/verify-plugins/XX-<name>.sh`。
3. 在本清单登记插件信息（含 timeout_sec/on_timeout/criticality/checksum）。
4. 通过正常提交流程提交（插件不属于受保护资产，无需用户审批）。
5. 主代理合并后插件即生效，下次 `verify-commit.sh --pre` 自动加载。

## 修改插件流程（校验和同步）

1. 修改插件文件 `scripts/verify-plugins/XX-<name>.sh`。
2. 重新生成校验和：`sha256sum scripts/verify-plugins/XX-<name>.sh`。
3. **原子性更新**本清单中的 checksum 字段 + 校验和列表。
4. **禁止分步提交**：插件文件修改和清单更新必须在同一个 commit 中。
5. 提交时 commit message 包含 `Plugin-Updated: <name> <old-checksum> → <new-checksum>`。
