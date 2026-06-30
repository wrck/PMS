# 归档说明

> 本目录存放 `03-database/` 下的历史冗余文件，已于 2026-06-30 归档。

## 归档原因

1. **旧版数据字典**（`.md` 文件）：`database_dict final.md` 已被项目记忆确认为唯一权威版本，其他 `database_dict*.md` 均为历史中间产物或副本，存在版本冲突风险，故归档。
2. **Excel 数据字典**（`.xlsx` 文件）：均为生成 `database_dict final.md` 过程中的中间产物，权威版本已固化在 Markdown 文件中，Excel 仅保留作为源数据备份。
3. **Python 脚本**（`.py` 文件）：用于从数据库提取、校验、修复数据字典的脚本，一次性使用工具，已无运行必要。
4. **JavaScript 脚本**（`.js` 文件）：与 Python 脚本同性质，旧版字典生成工具。

## 归档文件清单（共 90 个）

| 类别 | 数量 | 示例文件 |
|------|------|----------|
| 旧版 .md 字典 | 8 | `database_dict.md`、`database_dict final copy.md`、`database_dict_full.md`、`database_dict_part1/2/3.md`、`_generated_dict.md` |
| Excel 字典 | 13 | `database_dict.xlsx`、`database_dict_final.xlsx`、`database_dict_flat*.xlsx` 系列 |
| Python 脚本 | 67 | `verify_*.py`、`check_*.py`、`fill_*.py`、`fix_*.py`、`inspect_*.py`、`compare_*.py` 等 |
| JavaScript 脚本 | 2 | `generate_dict.js`、`fix_empty_meanings.js` |

## 权威版本声明

- **唯一权威数据字典**：`../database_dict final.md`（位于上级目录 `03-database/`）
- **DAO-SQL 映射参考**：`../dao-sql-reference.md`（位于上级目录，已通过第六阶段方法级审查）
- **数据库治理报告**：`../database-governance-report.md`（位于上级目录）

## 注意事项

- 本目录文件**不应被引用**于任何新文档，所有引用应指向上级目录的权威版本
- 如需查阅历史版本演变过程，可参考本目录文件的时间戳
- 如需恢复某个文件，请将其从 `archive/` 移回上级目录并更新本说明
