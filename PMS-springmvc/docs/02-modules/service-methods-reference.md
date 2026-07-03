# PMS-springmvc Service 方法级参考文档

> 本文档深度分析 PMS-springmvc 所有 Service 接口和实现类的完整方法签名、核心算法、事务逻辑和异常处理机制。

---

## 目录

1. [IProjectService — 项目服务](#1-iprojectservice--项目服务)
2. [IProjectHeaderService — 项目头信息服务](#2-iprojectheaderservice--项目头信息服务)
3. [IProjectMemberService — 项目成员服务](#3-iprojectmemberservice--项目成员服务)
4. [IProjectTaskService — 项目任务服务](#4-iprojecttaskservice--项目任务服务)
5. [IPmWorkFlowService — 工作流服务](#5-ipmworkflowservice--工作流服务)
6. [IDispatchProjectService — 发运项目服务](#6-idispatchprojectservice--发运项目服务)
7. [IDispatchSettlementService — 发运结算服务](#7-idispatchsettlementservice--发运结算服务)
8. [IIndustryAssetService — 行业资产服务](#8-iindustryassetservice--行业资产服务)
9. [IIndustryLeakService — 行业漏洞服务](#9-iindustryleakservice--行业漏洞服务)
10. [IDailyReportService — 日报服务](#10-idailyreportservice--日报服务)
11. [IExcelAnalysisService — Excel分析服务](#11-iexcelanalysisservice--excel分析服务)

---

## 1. IProjectService — 项目服务

### 类概述
- 接口：`IProjectService extends IAbstractBaseService<Project>`
- 实现类：`ProjectService`
- 依赖 DAO：`ProjectMapper`

### 方法列表

#### `ProjectVO queryProjectByContractNoAndType(String contractNo, String projectType)`
- **功能**：根据合同号和类型查询项目
- **事务类型**：无事务（query* 前缀）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 |
  |--------|------|----------|----------|
  | contractNo | String | 合同号 | 非空 |
  | projectType | String | 项目类型 | 非空 |
- **返回值**：ProjectVO - 项目视图对象
- **核心算法**：
  1. 根据合同号和类型查询项目
  2. 转换为 VO 对象
  3. 返回项目信息
- **调用的DAO方法**：`projectMapper.selectByContractNoAndType()`
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | 查询失败 | 记录日志并返回 null |

---

## 2. IProjectHeaderService — 项目头信息服务

### 类概述
- 接口：`IProjectHeaderService extends ProjectService`
- 实现类：`ProjectHeaderService`
- 依赖 DAO：`ProjectHeaderMapper`

### 方法列表

#### `int deleteByPrimaryKey(Object pk)`
- **功能**：根据主键删除项目头信息
- **事务类型**：事务（delete* 前缀）
- **参数**：`pk` - 主键ID
- **返回值**：int - 删除行数

#### `int insert(ProjectHeader t)`
- **功能**：插入项目头信息
- **事务类型**：事务（insert* 前缀）
- **参数**：`t` - 项目头信息实体
- **返回值**：int - 插入行数

#### `int insertSelective(ProjectHeader t)`
- **功能**：选择性插入项目头信息
- **事务类型**：事务（insert* 前缀）
- **参数**：`t` - 项目头信息实体
- **返回值**：int - 插入行数

#### `ProjectHeader selectByPrimaryKey(Object pk)`
- **功能**：根据主键查询项目头信息
- **事务类型**：无事务（select* 前缀）
- **参数**：`pk` - 主键ID
- **返回值**：ProjectHeader - 项目头信息实体

#### `int updateByPrimaryKeySelective(ProjectHeader t)`
- **功能**：选择性更新项目头信息
- **事务类型**：事务（update* 前缀）
- **参数**：`t` - 项目头信息实体
- **返回值**：int - 更新行数

#### `int updateByPrimaryKey(ProjectHeader t)`
- **功能**：更新项目头信息
- **事务类型**：事务（update* 前缀）
- **参数**：`t` - 项目头信息实体
- **返回值**：int - 更新行数

#### `long countBySelectivePageable(PageParam<?> pageParam)`
- **功能**：分页计数
- **事务类型**：无事务
- **参数**：`pageParam` - 分页参数
- **返回值**：long - 记录数

#### `long countBySelective(ProjectHeader t)`
- **功能**：条件计数
- **事务类型**：无事务
- **参数**：`t` - 查询条件
- **返回值**：long - 记录数

#### `<T> List<T> selectBySelectivePageable(PageParam<?> pageParam)`
- **功能**：分页查询
- **事务类型**：无事务
- **参数**：`pageParam` - 分页参数
- **返回值**：List<T> - 查询结果列表

#### `List<ProjectHeader> selectBySelective(ProjectHeader t)`
- **功能**：条件查询
- **事务类型**：无事务
- **参数**：`t` - 查询条件
- **返回值**：List<ProjectHeader> - 查询结果列表

#### `long countUncreateProjectList(PageParam<Object> tempParam)`
- **功能**：未创建项目数
- **事务类型**：无事务
- **参数**：`tempParam` - 分页参数
- **返回值**：long - 记录数

#### `List<Object> selectUncreateProjectList(PageParam<Object> pageParam)`
- **功能**：未创建项目列表
- **事务类型**：无事务
- **参数**：`pageParam` - 分页参数
- **返回值**：List<Object> - 查询结果列表

#### `Map<String, Object> checkPermission(ProjectVO project)`
- **功能**：检查权限
- **事务类型**：无事务
- **参数**：`project` - 项目视图对象
- **返回值**：Map<String, Object> - 权限信息

#### `PermissionResult checkPermission(ProjectVO project, String... permissions)`
- **功能**：检查权限（返回结果）
- **事务类型**：无事务
- **参数**：`project` - 项目视图对象, `permissions` - 权限编码
- **返回值**：PermissionResult - 权限结果

#### `ProjectVO selectVOByProjectId(Object projectId)`
- **功能**：查询项目VO
- **事务类型**：无事务
- **参数**：`projectId` - 项目ID
- **返回值**：ProjectVO - 项目视图对象

#### `ProjectVO queryProjectStateByProjectId(Object projecrId)`
- **功能**：查询项目状态
- **事务类型**：无事务
- **参数**：`projecrId` - 项目ID
- **返回值**：ProjectVO - 项目视图对象

#### `List<ProjectProduct> queryProductInfoFromSmsByProjectCode(ProjectProduct project)`
- **功能**：查询SMS产品信息
- **事务类型**：无事务
- **参数**：`project` - 项目产品对象
- **返回值**：List<ProjectProduct> - 产品信息列表

#### `Result insertMergeContract(ProjectVO project, Integer projectId)`
- **功能**：插入合并合同
- **事务类型**：事务（insert* 前缀）
- **参数**：`project` - 项目视图对象, `projectId` - 项目ID
- **返回值**：Result - 操作结果

#### `Result transferProject(ProjectVO project, Integer projectId, String projectType)`
- **功能**：转移项目
- **事务类型**：事务（transfer* 前缀）
- **参数**：`project` - 项目视图对象, `projectId` - 项目ID, `projectType` - 项目类型
- **返回值**：Result - 操作结果

---

## 3. IProjectMemberService — 项目成员服务

### 类概述
- 接口：`IProjectMemberService extends IAbstractBaseService<ProjectMember>`
- 实现类：`ProjectMemberService`
- 依赖 DAO：`ProjectMemberMapper`

### 方法列表
继承 `IAbstractBaseService<ProjectMember>` 的基础 CRUD 方法：
- `selectByPrimaryKey(Integer id)` - 根据ID查询
- `insert(ProjectMember record)` - 插入
- `insertSelective(ProjectMember record)` - 选择性插入
- `updateByPrimaryKeySelective(ProjectMember record)` - 选择性更新
- `deleteByPrimaryKey(Integer id)` - 删除

---

## 4. IProjectTaskService — 项目任务服务

### 类概述
- 接口：`IProjectTaskService extends IAbstractBaseService<ProjectTask>`
- 实现类：`ProjectTaskService`
- 依赖 DAO：`ProjectTaskMapper`

### 方法列表
继承 `IAbstractBaseService<ProjectTask>` 的基础 CRUD 方法：
- `selectByPrimaryKey(Integer id)` - 根据ID查询
- `insert(ProjectTask record)` - 插入
- `insertSelective(ProjectTask record)` - 选择性插入
- `updateByPrimaryKeySelective(ProjectTask record)` - 选择性更新
- `deleteByPrimaryKey(Integer id)` - 删除

---

## 5. IPmWorkFlowService — 工作流服务

### 类概述
- 接口：`IPmWorkFlowService extends IAbstractBaseService<PmWorkFlow>`
- 实现类：`PmWorkFlowService`
- 依赖 DAO：`PmWorkFlowMapper`

### 方法列表

#### `List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(...)`
- **功能**：查询待办任务
- **事务类型**：无事务

#### `List<PmWorkFlow> selectFinishedTasksByAssignee(...)`
- **功能**：查询已办任务
- **事务类型**：无事务

#### `PmWorkFlow currentParticipantWorkFlow(PmWorkFlow pmWorkFlow, Object planParticipant)`
- **功能**：查询当前参与人工作流
- **事务类型**：无事务

#### `List<String> selectProcInstIdsBySelective(PmWorkFlowVO workFlow)`
- **功能**：查询流程实例ID
- **事务类型**：无事务

#### `void deleteProcess(Object planParticipant, String participantIds)`
- **功能**：删除流程
- **事务类型**：事务

#### `void deleteProcessThread(List<String> procInstIds)`
- **功能**：线程删除流程
- **事务类型**：事务

#### `void deleteProcess(PmWorkFlowVO pmWorkFlowVO)`
- **功能**：删除流程
- **事务类型**：事务

#### `Integer selectParticipantFinallyTask(String taskKey, Integer participantId)`
- **功能**：查询最终办理人
- **事务类型**：无事务

#### `String startProcess(PmWorkFlow pmWorkFlow, Object entity)`
- **功能**：启动流程
- **事务类型**：事务

#### `void terminateProcess(PmWorkFlowVO workflow, String terminateReason)`
- **功能**：终止流程
- **事务类型**：事务

#### `List<String> selectActivitiUserMails(Map<String, Object> params)`
- **功能**：查询用户邮箱
- **事务类型**：无事务

#### `PmWorkFlow decoratorEntity(PmWorkFlow pmWorkFlow)`
- **功能**：装饰流程变量实体
- **事务类型**：无事务

---

## 6. IDispatchProjectService — 发运项目服务

### 类概述
- 接口：`IDispatchProjectService extends IAbstractBaseService<DispatchProject>`
- 实现类：`DispatchProjectService`
- 依赖 DAO：`DispatchProjectMapper`

### 方法列表

#### `void insertOrUpdateSelective(DispatchProject dispatch)`
- **功能**：插入或更新发运项目
- **事务类型**：事务

#### `List<DispatchVO> selectDispatchProjectVOList(DispatchVO dispatch)`
- **功能**：查询发运项目VO列表
- **事务类型**：无事务

#### `Long countDispatchProjectVOList(DispatchVO dispatch)`
- **功能**：计数发运项目VO列表
- **事务类型**：无事务

#### `DispatchVO selectVOByPrimaryKey(Integer id)`
- **功能**：根据主键查询发运项目VO
- **事务类型**：无事务

#### `void dispatchSubmit(Integer id, DispatchVO dispatch)`
- **功能**：提交发运项目
- **事务类型**：事务

#### `String generateDispatchSeq(String facilitatorCode)`
- **功能**：生成派单编号
- **事务类型**：无事务

#### `String generateDispatchNo(Date dispatchTime, String dispatchSeq)`
- **功能**：生成发运合同号
- **事务类型**：无事务

#### `List<DispatchVO> selectDispatchVOWithAmountBySelective(DispatchVO dispatchProject)`
- **功能**：查询带金额的发运项目VO
- **事务类型**：无事务

#### `DispatchVO selectDispatchVOWithAmount(Integer dispatchId)`
- **功能**：查询单个带金额的发运项目VO
- **事务类型**：无事务

#### `PermissionResult checkPermission(DispatchVO dispatchVO, String... permissions)`
- **功能**：检查权限
- **事务类型**：无事务

---

## 7. IDispatchSettlementService — 发运结算服务

### 类概述
- 接口：`IDispatchSettlementService extends IAbstractBaseService<DispatchSettlement>`（`com.dp.plat.pms.springmvc.service.IDispatchSettlementService`）
- 实现类：`DispatchSettlementService`（`com.dp.plat.pms.springmvc.service.impl.DispatchSettlementService`，`@Service("dispatchSettlementService")`）
- 依赖 DAO：`DispatchSettlementMapper`
- 关键依赖：`IDispatchProjectService`、`INotifyTemplateService`、`ICompanyService`、`IPurchaseReceiptService`（D365 采购收货）、`IPurchaseReceiptLineService`、`IFileInfoService`、`FPApi`（发票平台）、`D365Api`、`DispatchSettlementUpdateAspect`
- 业务定位：转包合同结算全流程，含 D365 采购收货推送、电子发票查验、付款同步、发票号回填等核心集成逻辑

### 方法列表（接口声明 — 共 9 个）

#### `void insertOrUpdateSelective(SettlementVO settlement)`
- **功能**：插入或更新结算记录（带 Selective 语义，仅更新非空字段）
- **事务类型**：事务
- **源码行号**：`IDispatchSettlementService.java:19`

#### `void settlementSubmit(Integer id, SettlementVO settlement)`
- **功能**：结算提交 — 触发流程提交、生成结算编号 `settleSeq`、推送 D365 采购收货单（如配置启用），并自动校验权限
- **事务类型**：事务
- **源码行号**：`IDispatchSettlementService.java:21`

#### `long countSettlementWidthDispatchPageable(PageParam<Object> pageParam)`
- **功能**：分页统计结算记录数（关联 DispatchProject 维度）
- **事务类型**：无事务
- **源码行号**：`IDispatchSettlementService.java:23`

#### `List<Object> selectSettlementWidthDispatchPageable(PageParam<Object> pageParam)`
- **功能**：分页查询结算列表（关联 DispatchProject，返回 Object 列表以适配动态字段）
- **事务类型**：无事务
- **源码行号**：`IDispatchSettlementService.java:25`

#### `List<SettlementVO> querySSEDispatchSettlementPaymentList()`
- **功能**：查询匹配到带 SSE（Seeed Storage Energy / 结算付款调度）的结算信息列表，供定时任务 `DispatchSettlementSEEPaymentJob` 同步付款
- **事务类型**：无事务
- **源码行号**：`IDispatchSettlementService.java:31`

#### `void saveSettlementPayment(List<SettlementVO> settlementPaymentList)`
- **功能**：批量保存结算付款信息
- **事务类型**：事务
- **源码行号**：`IDispatchSettlementService.java:33`

#### `void saveSettlementPayment(List<SettlementVO> settlementPaymentList, Integer[] delIds)`
- **功能**：批量保存结算付款信息，并按 `delIds` 删除被移除的付款记录（重载版本）
- **事务类型**：事务
- **源码行号**：`IDispatchSettlementService.java:35`

#### `Result verifySettlementInvoice(DispatchSettlement settlement)`
- **功能**：发票查验 — 调用 `FPApi` 进行电子发票查验，回填发票号、金额、税率等字段到结算单及子付款记录
- **事务类型**：事务
- **返回**：`Result` 含成功/失败状态与消息
- **源码行号**：`IDispatchSettlementService.java:37`

#### `List<FileInfo> selectDispatchSettlementInvoiceDetails(DispatchSettlement settlement)`
- **功能**：查询去重后的发票附件明细（按发票号去重，过滤已识别发票）
- **事务类型**：无事务
- **源码行号**：`IDispatchSettlementService.java:44`

### 实现类辅助方法（impl-only，非接口契约）

> ⚠️ **归属说明**：以下方法仅存在于 `DispatchSettlementService` 实现类，**不在 `IDispatchSettlementService` 接口中声明**。提供这些方法是为了让 `DispatchSettlementController` 与 AOP 切面 `DispatchSettlementUpdateAspect` 复用核心业务逻辑。

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `String generateSettleSeq(DispatchSettlement settlement)` | `DispatchSettlementService.java:167` | 生成结算编号（格式：`JN+yyyyMMdd+seq`） |
| `void pushPurchaseReceipt(DispatchSettlement settlement)` | `DispatchSettlementService.java:204` | 推送 D365 采购收货单（含 header + lines） |
| `PurchaseReceiptHeader createPurchashReceipt(DispatchProject dispatch, Map<String, Object> params)` | `DispatchSettlementService.java:253` | 构造 D365 采购收货单头 |
| `List<PurchaseReceiptLine> createPurchaseReceiptLines(DispatchProject dispatch, Map<String, Object> params)` | `DispatchSettlementService.java:297` | 构造 D365 采购收货行 |
| `Result verifySettlementInvoice(DispatchSettlement settlement, List<File> invoiceFiles, List<Object> invoiceList)` | `DispatchSettlementService.java:457` | 发票查验重载版本（传入已识别文件列表） |
| `void fillSettlementInvoiceNumber(DispatchSettlement settlement, Collection<FileInfo> invoiceFileInfos, Collection<String> invoiceNumbers)` | `DispatchSettlementService.java:542` | 回填发票号到结算单及子付款记录 |
| `void updateSubcontractPaymentInvoiceNumber(Integer subcontractPaymentId, Integer dispatchSettlementId, List<String> invoiceNumbers, List<String> invoiceFileIds)` | `DispatchSettlementService.java:633` | 更新转包付款记录的发票号 |
| `void updateSubcontractPaymentInvoiceNumber(DispatchSettlement settlement)` | `DispatchSettlementService.java:646` | 按结算单批量更新转包付款发票号（重载版本） |

### 调用关系
- **Controller**：`DispatchSettlementController`（`/pm/settlement/`）
  - `list` → `countSettlementWidthDispatchPageable` + `selectSettlementWidthDispatchPageable`
  - `create` → `insertOrUpdateSelective`
  - `settlementSubmit` → `insertOrUpdateSelective` + `settlementSubmit`
  - `exportProjectInfoDoc` → `selectDispatchSettlementInvoiceDetails`
  - `settlementInvoiceDetails` → `selectDispatchSettlementInvoiceDetails`
  - `verifySettlementInvoice` → `verifySettlementInvoice`
  - `syncSettlementPayment` → `DispatchSettlementSEEPaymentJob.execute()` → `querySSEDispatchSettlementPaymentList` + `saveSettlementPayment`
- **AOP**：`DispatchSettlementUpdateAspect` 在结算单更新时触发发票状态校验
- **Job**：`DispatchSettlementSEEPaymentJob` 定时同步 SSE 付款

> 📝 **历史修订记录**：2026-06-30 修订。原版本列出 5 个虚构方法（`selectSettlementVOList`/`countSettlementVOList`/`selectVOByPrimaryKey`/`confirm`/`payment`），全部不存在于源码。已按 `IDispatchSettlementService.java` 实际 9 个接口方法 + 8 个实现类辅助方法重写。

---

## 8. IIndustryAssetService — 行业资产服务

### 类概述
- 接口：`IIndustryAssetService extends IExcelAnalysisService<IndustryAsset>`（**非** `IAbstractBaseService`，间接通过 `IExcelAnalysisService` 继承）
- 实现类：`IndustryAssetService`（`@Service("industryAssetService")`，重写 `doImportData`/`submitImportData`）
- 依赖 DAO：`IndustryAssetMapper` + `ExcelAnalysisMapper`（通过 `getExcelAnalysisDao()` 暴露）
- 业务定位：行业资产台账管理，含 Excel 导入流程

### 方法列表（接口声明 — 共 2 个，另继承 IExcelAnalysisService 的默认方法 + IAbstractBaseService 的 CRUD）

#### `List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam)`
- **功能**：分页查询项目资产关联视图（含项目名称等关联字段）
- **事务类型**：无事务
- **源码行号**：`IIndustryAssetService.java:14`

#### `long countProjectAssetBySelectivePageable(PageParam<Object> pageParam)`
- **功能**：分页统计项目资产关联数
- **事务类型**：无事务
- **源码行号**：`IIndustryAssetService.java:16`

> 📝 **历史修订记录**：2026-06-30 修订。原版本错误声明继承 `IAbstractBaseService<IndustryAsset>`，实际继承 `IExcelAnalysisService<IndustryAsset>`。补充 2 个遗漏的接口声明方法。

---

## 9. IIndustryLeakService — 行业漏洞服务

### 类概述
- 接口：`IIndustryLeakService extends IAbstractBaseService<IndustryLeak>`（`com.dp.plat.pms.springmvc.service.IIndustryLeakService`）
- 实现类：`IndustryLeakService`（`@Service("industryLeakService")`，**同时 implements `IExcelAnalysisService<IndustryLeak>`**）
- 依赖 DAO：`IndustryLeakMapper` + `ExcelAnalysisMapper`
- 业务定位：行业漏洞台账管理，含 Excel 导入流程

### 方法列表
继承 `IAbstractBaseService<IndustryLeak>` 的基础 CRUD 方法 + `IExcelAnalysisService<IndustryLeak>` 的 Excel 导入默认方法（`createTempTable`/`insertTempData`/`selectTempData`/`countTempData`/`dropTempTable`/`doImportData`/`submitImportData`，见 §11）。

> 📝 **历史修订记录**：2026-06-30 修订。补充说明实现类同时实现 `IExcelAnalysisService` 并重写 Excel 导入相关方法（`IndustryLeakService.java:53-123`）。

---

## 10. IDailyReportService — 日报服务

### 类概述
- 接口：`IDailyReportService extends IAbstractBaseService<DailyReport>`（`com.dp.plat.pms.springmvc.service.IDailyReportService`）
- 实现类：`DailyReportService`
- 依赖 DAO：`DailyReportMapper`
- 业务定位：项目日报管理，含权限校验

### 方法列表（接口声明 — 共 2 个，另继承 IAbstractBaseService 的基础 CRUD）

#### `PermissionResult checkPermission(DailyReportVO v, String... permissions)`
- **功能**：日报权限校验（基于用户角色与项目成员关系判断 list/detail/edit/delete 权限）
- **事务类型**：无事务
- **源码行号**：`IDailyReportService.java:15`

#### `Map<String, Object> checkPermissionMap(DailyReportVO v, String... permissions)`
- **功能**：日报权限校验并返回权限 Map（用于 Controller 注入 Model）
- **事务类型**：无事务
- **源码行号**：`IDailyReportService.java:17`

> 📝 **历史修订记录**：2026-06-30 修订。原版本仅说"继承基础 CRUD 方法"，遗漏 2 个权限校验方法。

---

## 11. IExcelAnalysisService<T> — Excel 导入分析基础接口

### 类概述
- 接口：`IExcelAnalysisService<T>` extends `IAbstractBaseService<T>`（`com.dp.plat.pms.springmvc.service.IExcelAnalysisService`）
- **无独立实现类** — 该接口作为基接口被以下 Service 继承：
  - `FacilitatorService`（服务商管理）
  - `IndustryLeakService`（行业漏洞预警）
  - `CommonRelatedDataService`（关联数据管理）
  - `IndustryAssetService`（行业资产管理）
- 依赖 DAO：`ExcelAnalysisMapper`（通过 `getExcelAnalysisDao()` 暴露）
- 设计模式：接口默认方法（Java 8 default methods）— 提供完整的 Excel 导入流程模板，子类只需实现 `getExcelAnalysisDao()` 和 `getSourceTableName()` 两个抽象方法

### 方法列表

#### 抽象方法（子类必须实现）

##### `ExcelAnalysisMapper getExcelAnalysisDao()`
- **功能**：返回 Excel 分析 Mapper 实例
- **事务类型**：无事务

##### `String getSourceTableName()`
- **功能**：返回导入目标源表名（用于创建临时表、提交数据）
- **事务类型**：无事务

#### 默认方法（接口已实现，子类可直接使用）

##### `Result importPreview(Map<String, Object> params, String execlPath)`
- **功能**：Excel 导入预览（不写库），返回解析后的数据列表、临时表名、列对应关系
- **事务类型**：无事务
- **流程**：
  1. 从 `params.columns` 构建表头映射 `headRelationMapping`
  2. 使用 EasyExcel 读取文件，注册 `ExcelAnalysisEventListener<T>` 监听器
  3. 监听器解析 Excel 数据并填充临时表（如启用）
  4. 返回 `Result(true, {data, tempTableName, columns, columnKeys})`

##### `@Transactional Result importSubmit(Map<String, Object> params, String execlPath)`
- **功能**：Excel 导入提交（写库），用于完整报表行的情况
- **事务类型**：`@Transactional`
- **流程**：与 `importPreview` 类似，但 `listener.setSync(true)` 同步写入

##### `void createTempImportTable(String tempTableName)`
- **功能**：创建导入临时表（默认委托 `createTempTable`）
- **事务类型**：无事务

##### `void createTempImportTable(String tempTableName, String sourceTableName)`
- **功能**：创建导入临时表（指定源表结构）
- **事务类型**：无事务

##### `void createTempTable(String tempTableName)`
- **功能**：创建临时表（结构来自 `getSourceTableName()`）
- **调用 DAO**：`excelAnalysisDao.createTempTable(tempTableName, getSourceTableName())`

##### `void createTempTable(String tempTableName, String sourceTableName)`
- **功能**：创建临时表（显式指定源表）
- **调用 DAO**：`excelAnalysisDao.createTempTable(tempTableName, sourceTableName)`

##### `void insertTempImportData(String tempTableName, Object vo, Collection<String> columns)`
- **功能**：插入单条临时数据（封装为 List 后调用 `insertTempData`）
- **调用 DAO**：`excelAnalysisDao.insertTempData(tempTableName, list, columns)`

##### `void insertTempImportData(String tempTableName, List<Object> list, Collection<String> columns)`
- **功能**：批量插入临时数据
- **调用 DAO**：`excelAnalysisDao.insertTempData(tempTableName, list, columns)`

##### `void insertTempData(String tempTableName, List<Object> list, Collection<String> columns)`
- **功能**：实际执行临时表批量插入（委托 DAO）
- **调用 DAO**：`excelAnalysisDao.insertTempData(tempTableName, list, columns)`

##### `Result submitTempTable(Map<String, Object> params, String tempTableName, Collection<String> columns)`
- **功能**：提交临时表数据到源表，然后删除临时表
- **流程**：`submitImportData` → `dropTempTable`

##### `Result submitImportData(Map<String, Object> params, String tempTableName, Collection<String> columns)`
- **功能**：将临时表数据提交到源表
- **调用 DAO**：`excelAnalysisDao.submitImportData(tempTableName, getSourceTableName(), columns, params)`

##### `List<?> selectTempImportData(String tempTableName, PageParam<?> pageParam)`
- **功能**：分页查询临时表数据（自动设置 total）
- **流程**：`countTempData` → `selectTempData`

##### `List<?> selectTempData(PageParam<?> pageParam)`
- **功能**：查询临时表数据（`pageParam.customField` 携带表名）
- **调用 DAO**：`excelAnalysisDao.selectTempData(pageParam)`

##### `long countTempData(PageParam<?> pageParam)`
- **功能**：统计临时表数据量
- **调用 DAO**：`excelAnalysisDao.countTempData(pageParam)`

##### `void dropTempTable(String tempTableName)`
- **功能**：删除临时表
- **调用 DAO**：`excelAnalysisDao.dropTempTable(tempTableName)`

##### `Result doImportData(List<?> list, Collection<String> columns, Map<String, Object> params)`
- **功能**：直接导入数据到源表（不经过临时表）
- **调用 DAO**：`excelAnalysisDao.doImportData(list, getSourceTableName(), columns, params)`

### 使用模式

子类继承 `IExcelAnalysisService<T>` 后，仅需实现两个抽象方法即可获得完整的 Excel 导入能力：

```java
@Service
public class FacilitatorService extends AbstractBaseService<Facilitator> 
    implements IFacilitatorService {
    
    @Autowired
    private ExcelAnalysisMapper excelAnalysisMapper;
    
    @Override
    public ExcelAnalysisMapper getExcelAnalysisDao() {
        return excelAnalysisMapper;
    }
    
    @Override
    public String getSourceTableName() {
        return "pm_facilitator";
    }
    
    // Controller 调用 importPreview() / importSubmit() 完成导入
}
```

> ⚠️ **虚构内容清理记录**：旧版文档列出的 `void importExcel(MultipartFile, String)`、`List<Map<String,Object>> analyzeData(String, String)`、`void saveAnalysisResult(String, List<Map<String,Object>>)` 三个方法在源码中**均不存在**，已替换为上述实际方法列表。

---

## 12. IEmployeeService — EHR 员工服务

### 类概述
- 接口：`IEmployeeService`（`com.dp.plat.ehr.service.IEmployeeService`）
- 实现类：`EmployeeService`（`com.dp.plat.ehr.service.impl.EmployeeService`）
- 依赖 DAO：`EmployeeMapper`
- 详细方法说明：[ehr-integration.md](ehr-integration.md)
- 业务定位：EHR 员工数据同步、Activiti 用户同步、员工考评人关系

### 方法列表（接口声明 — 共 11 个）

#### `List<EmployeeAppraiserVO> selectEmployeeAppraiserBySelectivePageableVO(PageParam<EmployeeVO> pageParam)`
- **功能**：员工考评人关系分页查询（关联 `perf_appraiser_relationship` 表）
- **返回**：员工列表，每条记录携带考评人关系列表
- **数据源**：默认 `dataSourceLocal`（查询 `view_ehr_employee` 视图）
- **详细 SQL**：见 [ehr-integration.md 第 9.6 节](ehr-integration.md#96-查询-sql)
- ⚠️ **死代码**：此方法在 Service/DAO 层已声明，但全代码库无 Controller 调用
- **源码行号**：`IEmployeeService.java:62`

#### `List<EmployeeVO> selectBySelectivePageableVO(PageParam<EmployeeVO> pageParam)`
- **功能**：员工分页查询
- **源码行号**：`IEmployeeService.java:30`

#### `long countBySelectivePageableVO(PageParam<EmployeeVO> pageParam)`
- **功能**：员工分页查询总数统计（与 `selectBySelectivePageableVO` 配套）
- **源码行号**：`IEmployeeService.java:24`

#### `EmployeeVO selectVOByPrimaryKey(Integer id)`
- **功能**：根据主键查询员工 VO（含关联账号信息）
- **源码行号**：`IEmployeeService.java:36`

#### `List<EmployeeVO> selectEmployeeVOByIds(String ids)`
- **功能**：根据 ID 集合（逗号分隔字符串）批量查询员工 VO
- **源码行号**：`IEmployeeService.java:42`

#### `EmployeeVO selectByWorkNo(String workNo)`
- **功能**：根据工号查询员工 VO
- **源码行号**：`IEmployeeService.java:56`

#### `List<EmployeeVO> selectEmployeeWithAccount(EmployeeVO employee)`
- **功能**：查询员工列表并携带关联的 PMS 用户账号信息
- **源码行号**：`IEmployeeService.java:68`

#### `void initUser(List<EmployeeVO> employeeList)`
- **功能**：初始化用户账号（根据 EHR 员工数据创建 PMS 用户）
- **源码行号**：`IEmployeeService.java:52`

#### `void initActivitiUser()`
- **功能**：初始化 Activiti 工作流用户（同步 EHR 员工到 `act_id_user`/`act_id_info`/`act_id_membership`）
- **源码行号**：`IEmployeeService.java:70`

#### `void insertOrUpdateActivitiUser(UserEntity userEntity)`
- **功能**：插入或更新单个 Activiti 用户（来自 Activiti Engine 的 `UserEntity`）
- **源码行号**：`IEmployeeService.java:72`

#### `List<Select2Data> selectEmployeeSelect2Data(Select2Data select2Data)`
- **功能**：员工 Select2 数据查询（用于下拉选择）
- **源码行号**：`IEmployeeService.java:47`

> 📝 **历史修订记录**：2026-06-30 修订。原版本仅列出 4 个"关键方法"，遗漏 7 个接口声明方法（含 Activiti 用户同步、按工号查询、VO 查询等）。

---

## 13. IPmSynchronizeService — 跨系统数据同步服务

### 类概述
- 接口：`IPmSynchronizeService` extends `ISynchronizeService`（`com.dp.plat.pms.springmvc.service.IPmSynchronizeService`）
- 实现类：`PmSynchronizeService`（`com.dp.plat.pms.springmvc.service.impl.PmSynchronizeService`）
- 依赖 DAO：`PmSynchronizeMapper`
- 详细方法说明：[sap-contract.md](sap-contract.md)
- 业务定位：跨系统数据同步，含 SAP 合同、D365 资产属性、Facilitator、采购收货结算等多源数据同步

### 方法列表（接口声明 — 共 19 个，按业务分组）

#### A. SAP 合同同步（4 个）

##### `int insertOfstContractHeadSAP(List<OfstContractHeadSAP> record)`
- **功能**：批量插入 SAP 合同头数据到本地表 `sms_ofst_contract_head_sap`
- **入参**：`List<OfstContractHeadSAP>`（子类，含主键 id）
- **返回**：受影响行数
- **数据源**：`dataSourceLocal`（PMS MySQL）
- **SQL 特性**：`INSERT INTO ... ON DUPLICATE KEY UPDATE`
- **源码行号**：`IPmSynchronizeService.java:41`

##### `List<OfstContractHead> selectAllOfstContractHeadSAP()`
- **功能**：从 CRM 数据源查询所有 SAP 合同头数据
- **返回**：`List<OfstContractHead>`（父类）
- **数据源**：`dataSourceCRM`（SQL Server，查询视图 `DPtech_v_order_contract_4_pms`）
- **注意**：调用前必须通过 `DataSourceHolder.setDataSourceType("CRM")` 显式切换数据源
- **源码行号**：`IPmSynchronizeService.java:42`

##### `void clearAllOfstContractHeadSAP()`
- **功能**：清空本地 SAP 合同头表
- **数据源**：`dataSourceLocal`（PMS MySQL）
- **SQL**：`TRUNCATE TABLE sms_ofst_contract_head_sap`
- **源码行号**：`IPmSynchronizeService.java:43`

##### `void insertSyncLog(SyncLog syncLog)` / `void insertSyncState(SyncState syncState)`
- **功能**：记录同步日志 / 同步状态
- **继承自**：`ISynchronizeService`

#### B. 安服项目属性（AfPrjProperty）同步（3 个）

##### `List<AfPrjProperty> selectAllAfPrjProperty()`
- **功能**：从 D365 数据源查询所有安服项目属性
- **数据源**：`dataSourceD365`
- **源码行号**：`IPmSynchronizeService.java:16`

##### `void clearAllAfPrjProperty()`
- **功能**：清空本地安服项目属性表
- **源码行号**：`IPmSynchronizeService.java:18`

##### `void insertAfPrjProperty(List<AfPrjProperty> list)`
- **功能**：批量插入安服项目属性到本地表
- **源码行号**：`IPmSynchronizeService.java:20`

#### C. 项目产品（ProjectProduct）同步（3 个）

##### `List<ProjectProduct> selectAllProjectProduct()`
- **功能**：从 D365 查询所有项目产品数据
- **源码行号**：`IPmSynchronizeService.java:22`

##### `void clearAllProjectProduct()`
- **功能**：清空本地项目产品表
- **源码行号**：`IPmSynchronizeService.java:24`

##### `void insertProjectProduct(List<ProjectProduct> list)`
- **功能**：批量插入项目产品到本地表
- **源码行号**：`IPmSynchronizeService.java:26`

#### D. 项目拆分（2 个重载）

##### `void splitAfProjectByProductCode(Map<String, Object> params)`
- **功能**：按产品编码 Map 拆分工程实施项目与安服项目
- **数据源**：`dataSourceLocal`
- **源码行号**：`IPmSynchronizeService.java:39`

##### `void splitAfProjectByProductCode(String productFirstCodes)`
- **功能**：按产品首字母编码字符串拆分项目（重载版本）
- **源码行号**：`IPmSynchronizeService.java:33`

#### E. Facilitator 服务商同步（4 个）

##### `List<Facilitator> selectAllFacilitator()`
- **功能**：从 D365 查询所有服务商数据
- **源码行号**：`IPmSynchronizeService.java:45`

##### `void clearAllFacilitator()`
- **功能**：清空本地服务商表
- **源码行号**：`IPmSynchronizeService.java:46`

##### `void insertFacilitator(List<Facilitator> list)`
- **功能**：批量插入服务商到本地表
- **源码行号**：`IPmSynchronizeService.java:47`

##### `void insertOrUpdateFacilitatorFromD365()`
- **功能**：从 D365 增量同步服务商（insert or update 语义）
- **源码行号**：`IPmSynchronizeService.java:48`

#### F. 采购收货结算（PurchaseReceiptSettlement）同步（3 个）

##### `List<PurchaseReceiptSettlement> selectAllPurchaseReceiptSettlement(Map<String, Object> params)`
- **功能**：从 D365 查询采购收货结算数据（支持参数过滤）
- **源码行号**：`IPmSynchronizeService.java:54`

##### `void clearAllPurchaseReceiptSettlement(Map<String, Object> params)`
- **功能**：按参数清空本地采购收货结算表
- **源码行号**：`IPmSynchronizeService.java:55`

##### `void insertPurchaseReceiptSettlement(List<PurchaseReceiptSettlement> list)`
- **功能**：批量插入采购收货结算到本地表
- **源码行号**：`IPmSynchronizeService.java:56`

#### G. 转包付款同步（1 个）

##### `void updateDispatchAndSubcontractPaymentFromD365(Map<String, Object> params)`
- **功能**：从 D365 同步更新转包结算与转包付款的付款金额、状态字段
- **源码行号**：`IPmSynchronizeService.java:57`

> 📝 **历史修订记录**：2026-06-30 修订。原版本仅列出 5 个 SAP 相关方法，遗漏 15 个方法（含 D365 资产属性、Facilitator、采购收货结算、转包付款同步等关键 D365 集成方法）。补充完整 19 个接口方法，并按业务分组重排。

---

## 14. IFacilitatorService — 服务商管理服务

### 类概述
- 接口：`IFacilitatorService extends IExcelAnalysisService<Facilitator>`（`com.dp.plat.pms.springmvc.service.IFacilitatorService`）
- 实现类：`FacilitatorService`（`@Service("facilitatorService")`，重写 `getExcelAnalysisDao`/`getSourceTableName`/`doImportData`）
- 依赖 DAO：`FacilitatorMapper` + `ExcelAnalysisMapper`
- 数据表：`pm_facilitator`

### 方法列表
继承 `IExcelAnalysisService<Facilitator>` 的 Excel 导入默认方法 + `IAbstractBaseService<Facilitator>` 的基础 CRUD。接口本身**无声明新方法**。

---

## 15. ICommonRelatedDataService — 通用关联数据服务

### 类概述
- 接口：`ICommonRelatedDataService extends IExcelAnalysisService<CommonRelatedData>`（`com.dp.plat.pms.springmvc.service.ICommonRelatedDataService`）
- 实现类：`CommonRelatedDataService`（`@Service("commonRelatedDataService")`，重写 `doImportData` 实现按 `type` 字段路由插入/更新）
- 依赖 DAO：`CommonRelatedDataMapper` + `ExcelAnalysisMapper`
- 数据表：`pm_common_related_data`

### 方法列表
继承 `IExcelAnalysisService<CommonRelatedData>` 的 Excel 导入默认方法 + `IAbstractBaseService<CommonRelatedData>` 的基础 CRUD。接口本身**无声明新方法**。

> 实现类重写 `doImportData(List, Collection, Map)`：当 `params.targetValue.type` 非空时，按 type 字段逐条插入/更新；否则走默认 `excelAnalysisMapper.doImportData`。

---

## 16. IProjectManageUserService — 项目用户管理服务

### 类概述
- 接口：`IProjectManageUserService extends IUserService`（`com.dp.plat.pms.springmvc.service.IProjectManageUserService`，父接口来自 core 模块）
- 实现类：`ProjectManageUserService`
- 依赖 DAO：`ProjectManageUserMapper`
- 业务定位：项目用户管理，含 Activiti 用户同步

### 方法列表（接口声明 — 共 7 个，另继承 `IUserService` 的方法）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `void insertOrUpdateActivitiUser(UserEntity userEntity)` | `IProjectManageUserService.java:14` | 插入或更新 Activiti 用户 |
| `void initActivitiUser()` | `IProjectManageUserService.java:16` | 初始化所有 Activiti 用户（批量同步） |
| `long countBySelectivePageable(PageParam<UserDetail> pageParam)` | `:18` | 分页统计用户数 |
| `long countBySelective(UserDetail t)` | `:20` | 按条件统计用户数 |
| `List<UserDetail> selectBySelectivePageable(PageParam<UserDetail> pageParam)` | `:22` | 分页查询用户 |
| `List<UserDetail> selectBySelective(UserDetail record)` | `:24` | 按条件查询用户 |
| `List<UserInfo> selectBySelective(UserInfo ui)` | `:26` | 按 `UserInfo` 条件查询（重载） |

---

## 17. IIndustryLeakWarningService — 行业漏洞预警服务

### 类概述
- 接口：`IIndustryLeakWarningService extends IAbstractBaseService<IndustryLeakWarning>`（`com.dp.plat.pms.springmvc.service.IIndustryLeakWarningService`）
- 实现类：`IndustryLeakWarningService`
- 依赖 DAO：`IndustryLeakWarningMapper`
- 业务定位：行业漏洞预警与关联资产查询

### 方法列表（接口声明 — 共 2 个，另继承 IAbstractBaseService 的 CRUD）

#### `List<Object> selectWarningAssetBySelectivePageable(PageParam<Object> pageParam)`
- **功能**：分页查询预警关联的资产视图（含资产维度信息）
- **源码行号**：`IIndustryLeakWarningService.java:16`

#### `long countWarningAssetBySelectivePageable(PageParam<Object> pageParam)`
- **功能**：分页统计预警关联资产数
- **源码行号**：`IIndustryLeakWarningService.java:18`

---

## 18. IIndustryAssetProjectRelationService — 行业资产项目关联服务

### 类概述
- 接口：`IIndustryAssetProjectRelationService extends IAbstractBaseService<IndustryAssetProjectRelation>`（`com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService`）
- 实现类：`IndustryAssetProjectRelationService`
- 依赖 DAO：`IndustryAssetProjectRelationMapper`
- 业务定位：行业资产与项目的关联关系管理

### 方法列表（接口声明 — 共 4 个，另继承 IAbstractBaseService 的 CRUD）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `long countProjectAssetBySelectivePageable(PageParam<Object> tempParam)` | `:15` | 分页统计项目资产关联数 |
| `List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam)` | `:17` | 分页查询项目资产关联视图 |
| `void insertProjectAssetSelective(IndustryAssetVO v)` | `:19` | 按 VO 选择性插入项目资产关联 |
| `void invalidAssetProjectRelation(IndustryAssetProjectRelation t)` | `:21` | 失效资产-项目关联（软删除） |

---

## 19. IIndustryAssetLeakRelationService — 资产漏洞关联服务

### 类概述
- 接口：`IIndustryAssetLeakRelationService extends IAbstractBaseService<IndustryAssetLeakRelation>`（`com.dp.plat.pms.springmvc.service.IIndustryAssetLeakRelationService`）
- 实现类：`IndustryAssetLeakRelationService`
- 依赖 DAO：`IndustryAssetLeakRelationMapper`
- 业务定位：资产与漏洞的关联关系管理

### 方法列表（接口声明 — 共 4 个，另继承 IAbstractBaseService 的 CRUD）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `long countProjectAssetLeakBySelectivePageable(PageParam<Object> tempParam)` | `:16` | 分页统计资产漏洞关联数 |
| `List<Object> selectProjectAssetLeakBySelectivePageable(PageParam<Object> pageParam)` | `:18` | 分页查询资产漏洞关联视图 |
| `void insertProjectAssetLeakSelective(IndustryLeakVO v)` | `:20` | 按 VO 选择性插入资产漏洞关联 |
| `void invalidProjectAssetLeakRelation(IndustryAssetLeakRelation t)` | `:22` | 失效资产漏洞关联（软删除） |

---

## 20. IPmWorkBenchService — 绩效工作台服务

### 类概述
- 接口：`IPmWorkBenchService`（`com.dp.plat.pms.springmvc.service.IPmWorkBenchService`）
- ⚠️ 该接口**不继承 `IAbstractBaseService`**，无 CRUD 方法
- 实现类：`PmWorkBenchService`
- 依赖 DAO：`PmWorkFlowMapper`
- 业务定位：绩效工作台任务查询（待办/已办）

### 方法列表（接口声明 — 共 4 个）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, Integer assignee, List<String> processKeys, List<String> taskKeys)` | `:20` | 根据受理人 ID 和流程/任务 key 查询代办绩效任务 |
| `List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, Integer assignee)` | `:30` | 根据受理人 ID 查询已办绩效任务 |
| `List<PmWorkFlow> selectFinishedTasksByAssignee(PageParam<PmWorkFlow> pageParam, PmWorkFlow workFlow)` | `:32` | 按工作流条件查询已办任务（重载版本） |
| `List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(PageParam<PmWorkFlow> pageParam, PmWorkFlow workFlow)` | `:34` | 按工作流条件查询代办任务（重载版本） |

---

## 21. IDataFieldRelationService — 数据字段关联服务

### 类概述
- 接口：`IDataFieldRelationService extends IAbstractBaseService<DataFieldRelation>`（`com.dp.plat.pms.springmvc.service.IDataFieldRelationService`）
- 实现类：`DataFieldRelationService`
- 依赖 DAO：`DataFieldRelationMapper`
- 业务定位：数据字段关联配置

### 方法列表
继承 `IAbstractBaseService<DataFieldRelation>` 的基础 CRUD。接口本身**无声明新方法**。

---

## 22. IEhrSynchronizeService — EHR 数据同步服务

### 类概述
- 接口：`IEhrSynchronizeService extends ISynchronizeService`（`com.dp.plat.ehr.service.IEhrSynchronizeService`，父接口来自 core 模块）
- 实现类：`EhrSynchronizeService`
- 依赖 DAO：`EhrSynchronizeMapper`
- 业务定位：EHR 全量数据同步（公司/部门/岗位/员工/账号/假期）

### 方法列表（接口声明 — 共 13 个，另继承 `ISynchronizeService` 的 `insertSyncLog`/`insertSyncState`）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `List<Company> selectAllCompany()` | `:19` | 查询所有公司数据（来自 EHR 源） |
| `List<Department> selectAllDepartment()` | `:24` | 查询所有部门数据 |
| `List<Job> selectAllJob()` | `:29` | 查询所有岗位数据 |
| `List<Employee> selectAllEmployee()` | `:34` | 查询所有员工数据 |
| `List<EHRLoginAccount> selectAllEHRLoginAccount()` | `:39` | 查询所有 EHR 登录账号 |
| `List<Holiday> selectAllHoliday()` | `:44` | 查询所有假期数据 |
| `void clearAllHoliday()` | `:46` | 清空本地假期表 |
| `void insertCompany(List<Company> companyList)` | `:51` | 批量插入公司数据 |
| `void insertDepartment(List<Department> list)` | `:56` | 批量插入部门数据 |
| `void insertJob(List<Job> list)` | `:61` | 批量插入岗位数据 |
| `void insertEmployee(List<Employee> list)` | `:66` | 批量插入员工数据 |
| `void insertEHRLoginAccount(List<EHRLoginAccount> list)` | `:71` | 批量插入 EHR 登录账号 |
| `void insertHoliday(List<Holiday> list)` | `:76` | 批量插入假期数据 |

---

## 23. IJobService — 岗位服务

### 类概述
- 接口：`IJobService extends IAbstractBaseService<Job>`（`com.dp.plat.ehr.service.IJobService`）
- 实现类：`JobService`
- 依赖 DAO：`JobMapper`
- 数据表：`ehr_job`

### 方法列表
继承 `IAbstractBaseService<Job>` 的基础 CRUD。接口本身**无声明新方法**。

---

## 24. IEhrDepartmentService — EHR 部门服务

### 类概述
- 接口：`IEhrDepartmentService extends IAbstractBaseService<Department>`（`com.dp.plat.ehr.service.IEhrDepartmentService`）
- 实现类：`EhrDepartmentService`
- 依赖 DAO：`EhrDepartmentMapper`
- 业务定位：EHR 部门树形数据查询

### 方法列表（接口声明 — 共 4 个，另继承 IAbstractBaseService 的 CRUD）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `List<TreeNode> getTreeData(Department department) throws Exception` | `:22` | 按部门条件查询树形数据（含异常声明） |
| `List<TreeNode> getTreeData(DepartmentVO departmentVO) throws Exception` | `:24` | 按 VO 查询树形数据（重载） |
| `long countVOBySelective(DepartmentVO departmentVO)` | `:26` | 按 VO 统计部门数 |
| `List<DepartmentVO> selectVOBySelective(DepartmentVO departmentVO)` | `:28` | 按 VO 查询部门列表 |

---

## 25. IEhrCompanyService — EHR 公司服务

### 类概述
- 接口：`IEhrCompanyService extends IAbstractBaseService<Company>`（`com.dp.plat.ehr.service.IEhrCompanyService`）
- 实现类：`EhrCompanyService`
- 依赖 DAO：`EhrCompanyMapper`

### 方法列表（接口声明 — 共 1 个，另继承 IAbstractBaseService 的 CRUD）

#### `List<TreeNode> getTreeData(Company company) throws Exception`
- **功能**：按公司条件查询树形数据
- **源码行号**：`IEhrCompanyService.java:20`

---

## 26. IEhrEmpPowerService — EHR 员工权限服务

### 类概述
- 接口：`IEhrEmpPowerService extends IAbstractBaseService<EhrEmpPower>`（`com.dp.plat.ehr.service.IEhrEmpPowerService`）
- 实现类：`EhrEmpPowerService`
- 依赖 DAO：`EhrEmpPowerMapper`
- 业务定位：员工 EHR 部门/员工权限初始化

### 方法列表（接口声明 — 共 3 个，另继承 IAbstractBaseService 的 CRUD）

| 方法签名 | 源码行号 | 功能 |
|---------|---------|------|
| `EhrEmpPower selectByEmpID(Integer empID)` | `:17` | 根据 empID 查询绩效计划权限 |
| `void insertEhrDepPower()` | `:22` | 初始化员工 EHR 部门权限 |
| `void insertEhrEmpPower()` | `:27` | 初始化员工 EHR 员工权限 |

---

## 27. IHolidayService — 假期服务

### 类概述
- 接口：`IHolidayService extends IAbstractBaseService<Holiday>`（`com.dp.plat.ehr.service.IHolidayService`）
- 实现类：`HolidayService`
- 依赖 DAO：`HolidayMapper`
- 数据表：`ehr_holiday`

### 方法列表
继承 `IAbstractBaseService<Holiday>` 的基础 CRUD。接口本身**无声明新方法**。

---

## 28. IEHRLoginAccountService — EHR 登录账号服务

### 类概述
- 接口：`IEHRLoginAccountService extends IAbstractBaseService<EHRLoginAccount>`（`com.dp.plat.ehr.service.IEHRLoginAccountService`）
- 实现类：`EHRLoginAccountService`
- 依赖 DAO：`EHRLoginAccountMapper`
- 数据表：`ehr_login_account`

### 方法列表
继承 `IAbstractBaseService<EHRLoginAccount>` 的基础 CRUD。接口本身**无声明新方法**。

> 📝 **历史修订记录**：2026-06-30 修订。新增 15 个 Service 章节（§14-§28），填补原版本完全未文档化的 Service 类缺口。文档化后 PMS-springmvc Service 类级覆盖率从 46.4% 提升至 **100%**（28/28）。

---

## 事务规则总结

### 事务方法前缀（PROPAGATION_REQUIRED）
`insert*` | `update*` | `delete*` | `add*` | `save*` | `do*` | `start*` | `submit*` | `transfer*`

### 非事务方法
`query*` | `find*` | `get*` | `check*` | `select*` | `count*` | `list*`
