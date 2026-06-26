# PMS-struts 知识库审核报告

> 审核时间：2026-06-24 | 审核范围：PMS-struts/docs/ 全部 | 数据基准：源码 + dppms_d365 + database_dict final.md

---

## 总体评估

PMS-struts 是**最成熟的知识库**（61 文件，含 11k 行功能文档 + 13k 行权威数据字典 + 7 份审核报告）。整体 A- 级。

| 维度 | 评级 | 说明 |
|------|------|------|
| 准确性 | A | 类名/表名/状态码均与源码一致；状态机 30/31/32/36/38/40/42/100 已核 |
| 完整性 | A | 10 个功能模块文档齐全 + 全量字典 + CRUD矩阵 + 错误码/术语/接口模板 |
| 可读性/可视化 | **C+→B(已改进)** | **原 0 张 Mermaid 图**（全 ASCII）；已为 project-management 关键流程补 Mermaid，其余模块待补 |
| 关联性 | A | 类↔表↔接口映射齐；CRUD矩阵完备 |
| 实战价值 | A | 含避坑要点、性能优化、故障排查、功能点清单 |

---

## 问题清单

| 编号 | 级别 | 维度 | 问题描述 | 位置 | 状态 |
|------|------|------|----------|------|------|
| STRUTS-01 | P1 | 可视化 | 9 个功能模块文档**0 张 Mermaid 图**，复杂状态机/流程用 ASCII，渲染差 | 02-modules/* | ⏳project-management已补,其余8个待补 |
| STRUTS-02 | P2 | 完整性 | 原无 README 索引入口 | docs/根 | ✅已新建 README.md |
| STRUTS-03 | P2 | 关联性 | 功能模块文档未显式标注"对表的 CRUD 操作频率/数据量"（虽有 crud-matrix 总表） | 02-modules/* | 建议每模块补小结 |
| STRUTS-04 | P3 | 实战 | project-management 状态机已补 Mermaid stateDiagram，可作其余模块范例 | 02-modules/project-management.md | ✅已完成范例 |

---

## 已完成增强

1. ✅ 新建 `README.md` 知识库索引（模块定位/陷阱/依赖图/功能模块表/导航/跨库共享）
2. ✅ project-management.md §2 业务流程：3 张 ASCII 图 → **Mermaid**（生命周期 graph LR、状态机 stateDiagram-v2、回退流程 flowchart）
3. ✅ 状态码补充速查说明

---

## 交叉验证记录

| 文档描述 | 源码/DB 验证 | 结果 |
|----------|--------------|------|
| 项目状态码 30/31/32/40/100 | project-management.md + MessageUtil 约定 | ✅ |
| ProjectAction.insertProject 创建 | Action 方法参考 | ✅ |
| iBATIS + MyBatis 双栈 | AGENTS.md + config-ibaits/ + extend/mybatis/ | ✅ |
| 源码目录 src/ 非标准 | 实际目录结构 | ✅ |
| 273 表全量字典 | database_dict final.md + DB count | ✅ |

---

## 改进建议（后续迭代）

1. **补 Mermaid 图**（最高优先）：为 presales/subcontract/callback/maintenance/prob/system-management/report/workflow 8 个文档的关键流程补 Mermaid 时序图/流程图/状态图。project-management 已作范例。
2. 每个功能模块文档末尾补"CRUD 操作频率/数据量"小结，与 crud-matrix.md 呼应。
3. database_dict final.md 的第二章（系统支撑域）、第三章（历史迁移/引擎域）目录已有但部分表缺详字段——可按需补全（核心 t_* 已在 core 库覆盖）。

---

## 相关
- [审核标准](../../../docs/知识库质量审核标准.md)
- [PMS-struts 知识库首页](../README.md)
- 历史审核：[final-verification-report.md](final-verification-report.md)
