# core 模块知识库审核报告

> 审核时间：2026-06-24 | 审核范围：core/docs/ 全部新建文档 | 数据基准：core 源码 + core 主数据源 information_schema（dev=`dppms_d365`/release=`dppms_d365`） + cols_user_perm.csv
>
> **2026-06-25 更正**：`dppms_d365` 是所有模块共享的统一数据库（329张表），经数据库实际验证确认。`jdbc.properties` 中的配置名（dpredis/dposs）为过时配置，不代表当前实际数据库。

---

## 总体评估

core 知识库为本次**从零新建**（core 原无 docs/）。文档基于源码实测编写，整体达到 B+ 级（良好，可交付，待持续优化）。

| 维度 | 评级 | 说明 |
|------|------|------|
| 准确性 | A | 字段/类/方法均源自源码与 CSV 实测，未臆测；ER 逻辑外键约定已说明 |
| 完整性 | B | 6 大章节齐全；t_user_login_record/t_company/t_department 字段细节可再补 |
| 可读性/可视化 | A | 含 4 张 Mermaid 图（分层/认证时序/数据流/ER） |
| 关联性 | A | CRUD 矩阵+数据流+跨库链接齐备 |
| 实战价值 | B+ | 含避坑要点与复用套路；可补更多故障案例 |

---

## 问题清单

| 编号 | 级别 | 维度 | 问题描述 | 位置 | 状态 |
|------|------|------|----------|------|------|
| CORE-01 | P1 | 完整性 | `t_user_login_record` 仅概述未列全字段 | 03-database §1.4 | ✅已补全(DB实测) |
| CORE-02 | P2 | 准确性 | `t_menu` 列 `crate_time` 是否为真实列名 | 03-database §3.1 | ✅已核实(真实列名,历史拼写已固化) |
| CORE-03 | P2 | 完整性 | `t_company`/`t_department`/`t_dictionary` 仅概述未列全字段 | 03-database §四 | 待补全 |
| CORE-04 | P2 | 完整性 | `t_sys_variable` 参数 Key 清单未穷举 | 02-modules §1 | 待补 |
| CORE-05 | P3 | 实战 | 缺少 core 常见故障案例（如 ThreadLocal 串数据源事故复现） | 05-standards | 建议补充 |

---

## 重大发现（P0 级，跨模块）

### 发现：`docs/knowledge-base/05-数据库/01-数据字典/README.md` 含虚构内容

**问题**：工作区根 `docs/knowledge-base/`（早期统一库尝试）中的数据字典 README 出现 **`user_info` 表、BCrypt 密码加密、`role` 默认 'user'** 等内容。经核对真实数据库：
- 真实系统用户表为 **`t_user`**（非 `user_info`），用户信息表为 **`t_user_info`**；
- 密码加密为 **MD5 + 用户名盐 + 1024 次迭代**（`PasswordUtil`），**非 BCrypt**；
- 角色体系为 `t_role` + `t_user_role`，无默认 'user'/'admin' 字符串角色。

**结论**：该文件为**模板化/臆测内容**，与真实 core 主数据源架构（dev=`dppms_d365`/release=`dppms_d365`，PMS-struts 使用 `dppms_d365`）不符，属 P0 准确性错误。

**整改建议**：因用户已确认"按项目就地增强、各系统独立维护"，根目录 `docs/knowledge-base/` 整套统一库方案已废弃。建议：
- 将 `docs/knowledge-base/` 整体归档为 `docs/_archive/knowledge-base-deprecated/`，避免其虚构内容误导；
- 权威数据字典统一指向各模块 `docs/03-database/` 与 `PMS-struts/docs/03-database/database_dict final.md`。

**状态**：待用户确认后归档。

---

## 交叉验证记录（抽样核对源码）

| 文档描述 | 源码验证 | 结果 |
|----------|----------|------|
| t_user 列 user_name UNIQUE | `UserMapper.xml` resultMap + CSV `UNI` | ✅ 一致 |
| 密码 MD5+盐+1024迭代 | `ShiroRealm` L101 `encryptMD5Password(pwd,user,1024)` | ✅ 一致 |
| 数据源 AOP @Before/@After 清理 | `DataSourceAspect` L23-49 | ✅ 一致 |
| AbstractBaseService 10 方法 | `IAbstractBaseService.java` | ✅ 一致 |
| isSysUser→compId=-1 全权限 | `ShiroRealm` L127 | ✅ 一致 |
| t_user_info.custom5=areaPower | CSV 注释 `预留字段5 areaPower` | ✅ 一致 |

---

## 改进建议

1. 补全 t_user_login_record / t_company / t_department 全字段表（从 CSV 提取）。
2. 增加 core 故障案例：ThreadLocal 未清理导致读错库的事复现与排查步骤。
3. 穷举 t_sys_variable 参数 Key 清单。
4. 待 DB 直连确认 `crate_time` 是否为真实列名（疑似 t_menu 源码注释笔误）。

---

## 相关
- [审核标准](../../../docs/知识库质量审核标准.md)
- [core 知识库首页](../README.md)
