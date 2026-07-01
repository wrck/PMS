# PMS 迁移比对报告 — Dao 层

> 比对时间：2026-06-30
> 比对范围：24个 DaoImpl + 26个 iBATIS XML → 52个 Mapper + 7个 MyBatis XML

---

## 总体统计

| 指标 | 源(Struts/iBATIS) | 目标(SpringBoot/MyBatis) | 差距 |
|------|-------------------|--------------------------|------|
| Dao/Mapper 文件数 | 24 DaoImpl | 52 Mapper接口 | +28(拆分更细) |
| 数据访问方法数 | ~471 | ~110 | **-361 (-77%)** |
| SQL XML 文件数 | 26 XML | 7 XML | **-19 (-73%)** |
| SQL 语句数 | ~1,560+ | ~28 | **-1,532 (-98%)** |
| SQL 代码行数 | 29,319行 | 836行 | **-28,483 (-97%)** |

### ⚠️ 关键结论
- **SQL层迁移率约 2%**：原系统 29,319 行 iBATIS SQL，新系统仅 836 行 MyBatis XML
- **52个Mapper中约45个是空桩**：仅有基础CRUD（继承BaseMapper），无自定义SQL
- **仅7个Mapper有XML**：WorkSpaceMapper(587行)、PmsPresalesMapper(76行)、SysUserMapper(60行)、PmsProbRestoreMapper(46行)、PmsProbProductMapper(39行)、PmsProbSoftVersionMapper(28行)

---

## 分模块对比

### 1. ProjectDao → PmsProjectMapper (差距最大)

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | ProjectDaoImpl (1939行) | PmsProjectMapper (56行) |
| 方法数 | ~123 | ~8 |
| SQL XML | sql-map-project-config (6413行, ~320 SQL) + sql-map-project-config2 (5241行, ~261 SQL) | **无XML** |
| **SQL语句** | **~581** | **~8(基础CRUD)** |

**缺失SQL类别（全部未迁移）：**
- 项目列表查询（复杂多表JOIN + 动态条件 + 分页 + 权限过滤）~50 SQL
- 项目合同/产品线/发货/软版本管理 ~80 SQL
- 项目任务/计划/周报管理 ~60 SQL
- 项目成员/干系人管理 ~30 SQL
- 项目维护/督导/闭环管理 ~40 SQL
- 项目统计/报表查询 ~30 SQL
- 设备清单/序列号/租赁配置 ~40 SQL
- 项目状态流转/回退 ~20 SQL
- 批量导入/导出 ~15 SQL
- 其他杂项 ~200+ SQL

### 2. PresalesDao → PmsPresalesMapper

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | PresalesDaoImpl (357行) | PmsPresalesMapper (25行) |
| 方法数 | ~20 | ~4 |
| SQL XML | sql-map-presales-config (834行, ~36 SQL) | PmsPresalesMapper.xml (76行, ~4 SQL) |
| **SQL语句** | **~36** | **~4** |

**已迁移**: 基础列表查询、详情查询
**未迁移**: 售前申请/审核/回调、发货信息查询、临时授权查询、OA数据同步、交付件管理、统计导出等 ~32 SQL

### 3. SubcontractDao → PmsSubcontractMapper

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | SubcontractDaoImpl (407行) | PmsSubcontractMapper (1行) + 5个子Mapper |
| 方法数 | ~36 | ~8 |
| SQL XML | sql-map-subcontract-config (2703行, ~128 SQL) | **无XML** |
| **SQL语句** | **~128** | **~8(基础CRUD)** |

**缺失**: 分包创建/申请/审核/关闭、付款管理、服务商管理、交付管理、评论查询等 ~120 SQL

### 4. ProbManageDao → PmsProbMapper + 相关Mapper

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | ProbManageDaoImpl (759行) | PmsProbMapper (1行) + PmsProbRestore + PmsProbProduct + PmsProbSoftVersion |
| 方法数 | ~41 | ~8 |
| SQL XML | sql-map-prob-config (2577行, ~145 SQL) | 3个XML (113行, ~3 SQL) |
| **SQL语句** | **~145** | **~3** |

**缺失**: 公告CRUD、恢复任务管理、产品/组件管理、统计分析、软件版本管理、导入导出等 ~142 SQL

### 5. WorkSpaceDao → WorkSpaceMapper (迁移最好)

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | WorkSpaceDaoImpl (255行) | WorkSpaceMapper (79行) |
| 方法数 | ~5 | ~6 |
| SQL XML | sql-map-work-config (587行, ~28 SQL) | WorkSpaceMapper.xml (587行, ~17 SQL) |
| **SQL语句** | **~28** | **~17** |

**迁移率约 60%**：基础查询已迁移，部分复杂查询仍缺失

### 6. PmClosedLoopDao → PmClosedLoopMapper

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | PmClosedLoopDaoImpl (151行) | PmClosedLoopMapper (1行) |
| 方法数 | ~11 | ~1(空桩) |
| SQL XML | 无独立XML(内嵌在其他文件) | **无XML** |
| **SQL语句** | **~11** | **~1** |

### 7. PmClosedLoopQuesnaireDao → 6个Quesnaire Mapper

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | PmClosedLoopQuesnaireDaoImpl (167行) | 6个Mapper (各1行) |
| 方法数 | ~14 | ~6(空桩) |
| **SQL语句** | **~14** | **~6(空桩)** |

### 8. UserManageDao → SysUserMapper

| 项目 | 源 | 目标 |
|------|-----|------|
| 文件 | UserManageDaoImpl (369行) | SysUserMapper (38行) |
| 方法数 | ~20 | ~7 |
| SQL XML | sql-map-admin-config (1059行, ~123 SQL) | SysUserMapper.xml (60行, ~4 SQL) |
| **SQL语句** | **~123(含角色/部门)** | **~4** |

### 9. 其他Dao

| 源Dao | 方法数 | 目标Mapper | 目标方法 | 迁移状态 |
|-------|--------|-----------|----------|----------|
| BasicDataDaoImpl | ~12 | SysBasicDataMapper | ~3 | ⚠️ 基础CRUD |
| CallBackDaoImpl | ~11 | PmsCallBackMapper | ~1 | ❌ 空桩 |
| DepartmentManageDaoImpl | ~5 | SysDepartmentMapper | ~3 | ⚠️ 基础CRUD |
| LoginDaoImpl | ~3 | (在SysUserMapper中) | - | ⚠️ 部分 |
| OpLogDaoImpl | ~5 | SysOperateLogMapper | ~1 | ❌ 空桩 |
| ReportDaoImpl | ~11 | (无对应) | 0 | ❌ 未迁移 |
| RoleManageDaoImpl | ~2 | SysRoleMapper | ~1 | ⚠️ 基础CRUD |
| WorkflowDaoImpl | ~8 | (无对应) | 0 | ❌ 未迁移 |
| CertificateDaoImpl | ~3 | PmsCertificateMapper | ~1 | ❌ 空桩 |
| WarrantyCallbackDaoImpl | ~11 | PmsWarrantyCallbackMapper | ~1 | ❌ 空桩 |
| ProjectWarrantyCallbackDaoImpl | ~6 | (合并到上者) | - | ❌ 空桩 |
| SendMailDaoImpl | ~2 | (无对应) | 0 | ❌ 未迁移 |

---

## 🔴 高风险SQL缺失清单

### 1. 复杂动态查询（项目/售前/分包列表）
源系统使用iBATIS的`<dynamic>`、`<isNotEmpty>`、`<iterate>`等标签构建复杂动态SQL，包含：
- 多表JOIN（10+表）
- 动态WHERE条件（20+可选条件）
- 权限过滤子查询
- 分页排序
**新系统仅实现基础CRUD，无复杂动态查询**

### 2. 存储过程/函数调用
源系统可能包含存储过程调用（需进一步确认），新系统未见对应实现

### 3. 批量操作SQL
- 批量导入项目（Excel解析后批量INSERT）
- 批量删除/无效化项目
- 批量变更项目成员
**新系统无对应批量SQL**

### 4. 统计报表SQL
- 项目汇总状态统计（多维度GROUP BY）
- 指派率/跟踪率/闭环率/实施率统计
- 质量统计（复杂聚合查询）
**新系统ReportMapper完全缺失**

### 5. 外部数据同步SQL
- CRM数据刷新（44 SQL）
- D365数据刷新（6 SQL）
- OA数据刷新（13 SQL）
- SAP数据刷新（20 SQL）
- ITR/License/SMS/SSE数据刷新
**新系统全部缺失**

---

## 详细数据映射对比

### iBATIS → MyBatis 语法迁移注意事项

| iBATIS特性 | MyBatis对应 | 迁移状态 |
|------------|------------|----------|
| `<dynamic>` | `<where>` + `<if>` | 需逐个转换 |
| `<isNotEmpty>` | `<if test="x != null and x != ''">` | 需逐个转换 |
| `<iterate>` | `<foreach>` | 需逐个转换 |
| `#value#` | `#{value}` | 语法替换 |
| `$value$` | `${value}` | 语法替换 |
| `resultClass` | `resultType` | 属性名变更 |
| `parameterClass` | `parameterType` | 属性名变更 |

---

## 总结

| 层级 | 迁移率 | 说明 |
|------|--------|------|
| Dao接口 | ~23% | 471方法→110方法，多数为空桩 |
| SQL XML | ~3% | 29,319行→836行，仅WorkSpace完整迁移 |
| 复杂查询 | ~2% | 动态SQL/多表JOIN/统计查询几乎全部缺失 |
| 基础CRUD | ~80% | 通过BaseMapper泛型继承实现 |

**Dao层是迁移最薄弱的环节**，需要大量补充MyBatis XML中的自定义SQL。
