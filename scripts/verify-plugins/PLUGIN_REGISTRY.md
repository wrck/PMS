# 验证插件清单（PLUGIN_REGISTRY）

> 本文件记录所有已批准的验证插件。新增/修改/删除插件必须更新本清单。
> 插件规范见 [RULES.md §5.2](../RULES.md#52-插件规范)。
>
> **校验和同步规则（RULES.md §5.6）**：任何对已注册插件文件的修改，必须原子性地同步更新本清单中的对应校验和。禁止分步提交。

## 已批准插件

| 文件名 | 版本 | 作者 | 用途 | 批准日期 | timeout_sec | on_timeout | criticality | depends_on | checksum |
|--------|------|------|------|---------|-------------|------------|-------------|------------|----------|
| `30-flyway-version-check.sh` | 1.0.0 | 主代理 | 检查 Flyway 迁移版本号是否冲突（同模块内 Vxx 重复） | 2026-07-08 | 30 | fail | critical | - | bc446970bdd55ba073d8bcfc175debf8851854d3fa5ce59b0845cb1bd618b7de |

## 插件配置字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `timeout_sec` | number | 插件执行超时阈值（秒），默认 30，上限 60 |
| `on_timeout` | `fail` \| `warn` | 超时时的处理策略：`fail`=中断验证（exit 1），`warn`=降级警告（继续执行） |
| `criticality` | `critical` \| `optional` | 插件重要性：`critical`=失败/超时必须中断，`optional`=失败/超时仅警告 |
| `depends_on` | `[plugin-name]` \| `-` | 依赖的插件列表（可选），verify-commit.sh 据此做拓扑排序；`-` 表示无依赖 |
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

## 插件依赖关系（RULES.md §5.9）

### depends_on 字段格式

- **无依赖**：填 `-`（短横线）。
- **单依赖**：填依赖插件文件名，如 `01-java-import-check.sh`。
- **多依赖**：用逗号分隔，如 `01-java-import-check.sh,02-sql-injection-check.sh`。

### 拓扑排序规则

verify-commit.sh 在执行插件前，根据 `depends_on` 字段做拓扑排序：

1. **构建依赖图**：解析每个插件的 depends_on，构建有向图。
2. **Kahn 算法**：从入度为 0 的节点开始，逐步移除并加入执行序列。
3. **循环依赖检测**：如果拓扑排序后仍有节点未处理，说明存在循环依赖，立即报错（exit 1）。
4. **依赖失败传播**：如果被依赖的插件失败（critical），依赖它的插件跳过执行并标记为 SKIPPED。

### 依赖声明示例

```
| `01-java-import-check.sh`       | ... | -                                   | ... |
| `02-sql-injection-check.sh`     | ... | 01-java-import-check.sh             | ... |
| `10-lowcode-schema-validator.sh`| ... | -                                   | ... |
| `20-i18n-key-check.sh`          | ... | 10-lowcode-schema-validator.sh      | ... |
```

上例执行顺序：01 → 02 → 10 → 20（拓扑排序后）。

### 依赖失败传播

| 被依赖插件状态 | 依赖插件行为 |
|---------------|-------------|
| PASS | 正常执行 |
| FAIL (critical) | 跳过执行，标记 SKIPPED (dependency failed) |
| FAIL (optional) | 正常执行（optional 失败不阻断依赖） |
| TIMEOUT (critical) | 跳过执行，标记 SKIPPED (dependency timeout) |
| TIMEOUT (optional, warn) | 正常执行（optional 超时不阻断依赖） |
