# PMS-struts 数据库文档目录

> 本目录维护 PMS-struts 模块管辖数据库 `dppms_d365` 的数据字典、ER 图、索引分析等文档。
> 最近更新：2026-06-29

---

## 一、权威数据字典（唯一推荐使用）

| 文件 | 行数 | 大小 | 数据基准 | 状态 |
|------|------|------|----------|------|
| **[database_dict final.md](database_dict%20final.md)** | 13337 | 723 KB | 2026-06-13 | ✅ **权威版本**，字段描述最详尽 |

> ⚠️ **概览数据已过时**：`database_dict final.md` 第 14-15 行标注"基表 273 张 + 视图 39 个"为 2026-06-13 时点数据。截至 2026-06-29 实测，`dppms_d365` 实际拥有 **286 张基表 + 43 个视图**。差 13 表 + 4 视图为后续新增。使用前请用 MCP MySQL 工具或 `SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='dppms_d365'` 复核最新数量。

---

## 二、历史归档文件（已过时，仅供追溯）

> ⚠️ 以下文件**不应作为数据参考依据**，仅保留用于追溯字典演进历史。如需查阅，请优先使用权威版本。

| 文件 | 行数 | 大小 | 最后修改 | 与权威版本差异 |
|------|------|------|----------|----------------|
| `database_dict final copy.md` | 13337 | 651 KB | 2026-06-29 | 与 `final.md` 行数相同但内容不同（MD5 不同）。字段描述更简洁（如"创建时间" vs `final.md` 的"记录数据创建时间"），为 `final.md` 的早期版本 |
| `database_dict.md` | 10861 | 552 KB | 2026-06-29 | 与 `final.md` 同基准日期（2026-06-13），但缺少后期补充的章节（如"补录：缺失表定义"等），为 `final.md` 之前版本 |
| `database_dict_full.md` | 12821 | 540 KB | 2026-06-13 | 独立格式（标题"完整数据字典"），按 4 章组织（历史迁移/系统支撑/视图/项目管理），与 `final.md` 内容重叠但结构不同 |
| `database_dict_part1.md` | 3063 | 151 KB | 2026-06-12 | 旧版分片（1/3） |
| `database_dict_part2.md` | 2661 | 151 KB | 2026-06-13 | 旧版分片（2/3） |
| `database_dict_part3.md` | 5133 | 288 KB | 2026-06-29 | 旧版分片（3/3） |
| `_generated_dict.md` | 1509 | 49 KB | 2026-05-21 | 最早自动生成版本，纯字段表格无业务含义注释 |
| `complete-data-dictionary.md` | 10524 | 420 KB | 2026-05-26 | 早期独立完整版，排除 `temp_*/tmp_*` 表，6 章结构（概览/表结构/枚举/字段映射/数据类型/外键/索引） |

**冗余文件统计**：8 个历史归档 .md 文件 + 多个 .xlsx Excel 导出版本 + 多个 .py 生成/验证脚本，建议后续归档到 `archive/` 子目录。

---

## 三、辅助分析文档（有效）

| 文件 | 大小 | 内容 |
|------|------|------|
| [er-diagram.md](er-diagram.md) | 14 KB | 实体关系图 |
| [index-analysis.md](index-analysis.md) | 33 KB | 索引有效性分析 |
| [dao-sql-reference.md](dao-sql-reference.md) | 76 KB | DAO 层 SQL 参考 |
| [database-governance-report.md](database-governance-report.md) | 113 KB | 数据库治理报告（2026-06-12） |

---

## 四、按业务域分片文档（早期手工维护，已被全量字典覆盖）

| 文件 | 业务域 | 状态 |
|------|--------|------|
| `project-tables.md` | 项目管理（pm_project*） | 已被 `database_dict final.md` 第一章覆盖 |
| `callback-tables.md` | 回访管理（pm_cl*） | 已被 `database_dict final.md` 第二章覆盖 |
| `presales-tables.md` | 售前管理（pm_presales*） | 已被 `database_dict final.md` 第三章覆盖 |
| `subcontract-tables.md` | 转包管理（pm_subcontract*） | 已被 `database_dict final.md` 第四章覆盖 |
| `prob-tables.md` | 问题管理（prob*） | 已被 `database_dict final.md` 第五章覆盖 |
| `fnd-tables.md` | 基础平台（fnd*） | 已被 `database_dict final.md` 第六章覆盖 |
| `sync-tables.md` | 数据同步中间表 | 已被 `database_dict final.md` 第三章（系统支撑域）覆盖 |
| `other-tables.md` | 其他辅助表 | 已被 `database_dict final.md` 第四章（其他辅助表）覆盖 |

---

## 五、Excel 导出文件

> 数据字典的 Excel 导出多版本，最新为 `database_dict_flat_final_merged_v2.xlsx`（410 KB，2026-06-16）。建议统一使用此版本作为 Excel 参考。

| 文件 | 大小 | 最后修改 | 说明 |
|------|------|----------|------|
| `database_dict.xlsx` | 246 KB | 2026-06-13 | 初版 Excel 导出 |
| `database_dict_final.xlsx` | 264 KB | 2026-06-13 | 第一轮修订版 |
| `database_dict_final_v2.xlsx` | 367 KB | 2026-06-13 | 第二轮修订版 |
| `database_dict_v2.xlsx` | 239 KB | 2026-06-13 | 旧版 v2 |
| `database_dict_full.xlsx` | 356 KB | 2026-06-13 | full 版本配套 Excel |
| `database_dict_flat.xlsx` | 203 KB | 2026-06-15 | 扁平化格式 v1 |
| `database_dict_flat_v2.xlsx` | 198 KB | 2026-06-16 | 扁平化格式 v2 |
| `database_dict_flat_v3.xlsx` | 226 KB | 2026-06-16 | 扁平化格式 v3 |
| `database_dict_flat_final.xlsx` | 232 KB | 2026-06-16 | 扁平化最终版 |
| `database_dict_flat_final_match.xlsx` | 232 KB | 2026-06-16 | 字段匹配版 |
| `database_dict_flat_final_matched.xlsx` | 378 KB | 2026-06-16 | 字段匹配完成版 |
| `database_dict_flat_final_merged.xlsx` | 355 KB | 2026-06-16 | 字段合并版 |
| **`database_dict_flat_final_merged_v2.xlsx`** | **410 KB** | **2026-06-16** | ✅ **最新最终版** |
| `项目管理表数据元.xlsx` | 247 KB | 2026-06-16 | 中文数据元表 |

---

## 六、字典生成/验证脚本（开发工具）

> 本目录下 50+ 个 `.py` 脚本和 1 个 `.js` 脚本为字典生成、字段补全、格式校验、Excel 导出等工具。详见各脚本文件头部注释。

主要工具：
- `generate_dict.js` / `generate_full_dict.py` — 字典生成主脚本
- `export_dict_to_excel.py` / `export_flat.py` — Excel 导出
- `fill_*.py` 系列 — 字段含义补全工具
- `check_*.py` 系列 — 字段/格式/覆盖率校验工具
- `compare_*.py` 系列 — 字典对比工具
- `verify_*.py` 系列 — 验证工具

---

## 七、维护建议

1. **字典更新流程**：
   - 通过 MCP MySQL 工具查询 `information_schema` 获取最新表/字段信息
   - 修改 `database_dict final.md`，并同步更新本 README 的"数据基准"日期
   - 不要新建 `database_dict_*` 副本文件

2. **历史文件归档**：
   - 8 个冗余 .md 文件 + 12 个中间 .xlsx 文件可考虑移动到 `archive/` 子目录
   - 保留 `database_dict final.md` 为唯一权威 .md 字典

3. **数量校验**：
   - 使用前用 SQL 校验最新表数：`SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='dppms_d365' AND TABLE_TYPE='BASE TABLE'`
