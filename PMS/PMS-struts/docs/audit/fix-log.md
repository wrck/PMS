# PMS-struts 文档修正日志

> 修正日期：2026-06-24
> 依据文档：`docs/audit/deep-verification-report.md`
> 修正范围：依据报告中识别的问题清单进行针对性修正

---

## 修正项汇总

| 序号 | 修正状态 | 文件 | 修正内容 | 验证来源 |
|------|---------|------|---------|---------|
| 1 | ✅ 已修正 | subcontract.md | 表列表中 `pm_facilitator` → `pm_subcontract_facilitator` | sql-map-subcontract-config.xml |
| 2 | ⏭️ 已跳过（已修正） | project-management.md | projectState 表中 34/36/38/40/42/50 已在之前轮次移至 isback 表 | MessageUtil.java |
| 3 | ✅ 已修正 | project-management.md | 补充 `transferProject()` 方法接口文档 | ProjectAction.java 第519行 |
| 4 | ⏭️ 已跳过（已修正） | project-management.md | 虚构表名（tb_project_info 等）已在之前轮次修正 | - |
| 5 | ⏭️ 已跳过（已修正） | project-management.md | 遗漏表名（pm_project_related_party 等）已在之前轮次补充 | - |
| 6 | ⏭️ 已跳过（已修正） | project-management.md | URL 命名空间说明已在之前轮次补充 | struts-sys.xml |
| 7 | ⏭️ 已跳过（已修正） | prob.md | 类名（ProbManageAction/ProbManageServiceImpl）和表名已在之前轮次修正 | - |
| 8 | ⏭️ 已跳过（已修正） | workflow.md | URL 命名空间 `/module/` → `/work/` 已在之前轮次修正 | struts-sys.xml |
| 9 | ⏭️ 已跳过（已修正） | project-tables.md | 泛化字段映射表已在之前轮次补充 | sql-map-project-config.xml |
| 10 | ⏭️ 已跳过（已修正） | crud-matrix.md | pm_subcontract_facilitator 和 pm_subcontract_project_payment_sse 已在之前轮次修正 | - |
| 11 | ⏭️ 已跳过（已修正） | data-dictionary.md | projectState 表中状态值 34 已在之前轮次补充 | MessageUtil.java |
| 12 | ⏭️ 已跳过（无需修正） | data-dictionary.md | isback=40 含义保持"工程管理部不予跟踪"（见下方说明） | MessageUtil.java 第130行 |

---

## 详细修正说明

### 1. subcontract.md — 服务商表名修正 ✅

**文件**：`docs/02-modules/subcontract.md`

**修正内容**：第1节"涉及的数据库表列表"中，服务商表名从 `pm_facilitator` 修正为 `pm_subcontract_facilitator`。

**修正原因**：SQL映射文件 `sql-map-subcontract-config.xml` 中实际使用 `pm_subcontract_facilitator`，CRUD矩阵（5.1节）中已使用正确表名，但表列表遗漏修正。

**验证来源**：sql-map-subcontract-config.xml、crud-matrix.md

---

### 2. project-management.md — 状态编码分离 ⏭️ 已跳过（已修正）

**文件**：`docs/02-modules/project-management.md`

**预期修正**：将 34/36/38/40/42/50 从 projectState 表移到 isback 表。

**实际状态**：经检查，projectState 表（第827-835行）仅包含 10/20/30/31/32/33/100，isback 表（第851-860行）已包含 30/32/34/36/38/40/42/50。此修正已在之前轮次完成，无需重复修正。

**验证来源**：MessageUtil.java

---

### 3. project-management.md — 补充 transferProject() 方法 ✅

**文件**：`docs/02-modules/project-management.md`

**修正内容**：在 3.13 节"设备转移"下补充 `transferProject()` 方法的接口文档。

**新增内容**：
- URL：`/module/sub/projectSub_transferProject.action`
- 功能：查询可转移到的目标项目列表（设备转移时选择目标项目）
- 输入参数：`project.contractNo`（合同号）
- 返回结果：INPUT → `/sys/module/sub/transferProject.jsp`
- 处理逻辑：根据合同号查询可转移的项目列表

**修正原因**：ProjectAction.java 第519行存在 `transferProject()` 方法，但接口文档中未记录。该方法通过 struts-sys.xml 中 `projectSub_*` 通配符映射调用（第676行，namespace="/module/sub"）。

**验证来源**：ProjectAction.java 第515-526行、struts-sys.xml 第676行

---

### 4-6. project-management.md — 其他修正项 ⏭️ 已跳过（已修正）

**预期修正**：
- 虚构表名（tb_project_info → pm_project_header 等）
- 遗漏表名补充（pm_project_related_party、pm_project_notification 等）
- URL 命名空间说明

**实际状态**：经检查，文档中不存在 tb_* 前缀的虚构表名，所有表名均已正确；遗漏表名均已补充；URL 映射规则说明已存在（第593-599行）。这些修正已在之前轮次完成。

---

### 7. prob.md — 类名和表名修正 ⏭️ 已跳过（已修正）

**预期修正**：ProbAction → ProbManageAction、ProbServiceImpl → ProbManageServiceImpl、虚构表名修正。

**实际状态**：经检查，prob.md 中类名已为 `ProbManageAction` 和 `ProbManageServiceImpl`，表名已为 `prob_main`、`prob_restore`、`prob_restore_process`、`prob_soft_version`、`prob_softwares`、`prob_read_log`。这些修正已在之前轮次完成。

---

### 8. workflow.md — URL 命名空间修正 ⏭️ 已跳过（已修正）

**预期修正**：`/module/` 前缀 → `/work/` 前缀。

**实际状态**：经检查，workflow.md 中所有 URL 均已使用 `/work/` 前缀（如 `/work/WorkFlowAction.action`、`/work/WorkFlowViewTaskForm.action` 等）。此修正已在之前轮次完成。

---

### 9. project-tables.md — 泛化字段映射表 ⏭️ 已跳过（已修正）

**预期修正**：补充 column001~column014 与语义化字段的映射表。

**实际状态**：经检查，project-tables.md 第67-88行已包含完整的泛化字段映射表，涵盖 column001~column014 的语义化名称、实际类型、业务含义和代码比对注释。此修正已在之前轮次完成。

---

### 10. crud-matrix.md — CRUD 矩阵修正 ⏭️ 已跳过（已修正）

**预期修正**：pm_facilitator → pm_subcontract_facilitator、补充 pm_subcontract_project_payment_sse。

**实际状态**：经检查，crud-matrix.md 中转包模块已使用 `pm_subcontract_facilitator`（第99行），且已包含 `pm_subcontract_project_payment_sse`（第98行）。这些修正已在之前轮次完成。

---

### 11. data-dictionary.md — projectState 补充 34 ⏭️ 已跳过（已修正）

**预期修正**：在 projectState 表中补充状态值 `34`（PROJECT_CREATE_STATE34）。

**实际状态**：经检查，data-dictionary.md 第19行已包含 `34` 枚举值，常量名 `PROJECT_CREATE_STATE34`，含义"项目经理填写项目信息"，并标注"注意：此值实际存储在isback字段而非projectState字段"。此修正已在之前轮次完成。

---

### 12. data-dictionary.md — isback=40 含义修正 ⏭️ 已跳过（无需修正）

**预期修正**：将 isback 表中 `40` 的含义修正为"实施中（回退标记）"。

**实际状态**：经源码验证，当前值"工程管理部不予跟踪"是正确的，不应修改。

**详细说明**：
- 任务要求将 isback=40 的含义从"工程管理部不予跟踪"改为"实施中（回退标记）"
- 但 MessageUtil.java 第130行注释明确写道：`* 40表示工程管理部不予跟踪处理`
- deep-verification-report.md 的"交叉验证新发现"第1项也确认："源码注释明确为'工程管理部不予跟踪处理'（MessageUtil.java 第129行），data-dictionary.md 原文正确，project-management.md 原文错误"
- 报告的"修正记录"第8项显示，实际执行的是将 project-management.md 中 isback=40 的含义从"实施中"修正为"工程管理部不予跟踪"（与源码一致）
- 因此 data-dictionary.md 当前的"工程管理部不予跟踪"是正确的，修改为"实施中（回退标记）"将引入事实性错误

**结论**：跳过此修正项，保持 data-dictionary.md 中 isback=40 的现有含义"工程管理部不予跟踪"。

**验证来源**：MessageUtil.java 第130-132行

---

## 修正统计

| 类别 | 数量 |
|------|------|
| 本次实际修正 | 2 |
| 已在之前轮次修正（跳过） | 9 |
| 无需修正（源码验证确认正确） | 1 |
| **合计** | **12** |

---

## 修正涉及的文件

| 文件 | 修正类型 |
|------|---------|
| `docs/02-modules/subcontract.md` | 表名修正 |
| `docs/02-modules/project-management.md` | 补充方法接口文档 |
