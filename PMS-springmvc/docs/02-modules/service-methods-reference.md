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
- 接口：`IDispatchSettlementService extends IAbstractBaseService<DispatchSettlement>`
- 实现类：`DispatchSettlementService`
- 依赖 DAO：`DispatchSettlementMapper`

### 方法列表

#### `List<SettlementVO> selectSettlementVOList(SettlementVO settlement)`
- **功能**：查询结算VO列表
- **事务类型**：无事务

#### `Long countSettlementVOList(SettlementVO settlement)`
- **功能**：计数结算VO列表
- **事务类型**：无事务

#### `SettlementVO selectVOByPrimaryKey(Integer id)`
- **功能**：根据主键查询结算VO
- **事务类型**：无事务

#### `void confirm(Integer id)`
- **功能**：确认结算
- **事务类型**：事务

#### `void payment(Integer id)`
- **功能**：付款确认
- **事务类型**：事务

---

## 8. IIndustryAssetService — 行业资产服务

### 类概述
- 接口：`IIndustryAssetService extends IAbstractBaseService<IndustryAsset>`
- 实现类：`IndustryAssetService`
- 依赖 DAO：`IndustryAssetMapper`

### 方法列表
继承 `IAbstractBaseService<IndustryAsset>` 的基础 CRUD 方法。

---

## 9. IIndustryLeakService — 行业漏洞服务

### 类概述
- 接口：`IIndustryLeakService extends IAbstractBaseService<IndustryLeak>`
- 实现类：`IndustryLeakService`
- 依赖 DAO：`IndustryLeakMapper`

### 方法列表
继承 `IAbstractBaseService<IndustryLeak>` 的基础 CRUD 方法。

---

## 10. IDailyReportService — 日报服务

### 类概述
- 接口：`IDailyReportService extends IAbstractBaseService<DailyReport>`
- 实现类：`DailyReportService`
- 依赖 DAO：`DailyReportMapper`

### 方法列表
继承 `IAbstractBaseService<DailyReport>` 的基础 CRUD 方法。

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

### 方法列表（仅列出关键方法）

#### `List<EmployeeAppraiserVO> selectEmployeeAppraiserBySelectivePageableVO(PageParam<EmployeeVO> pageParam)`
- **功能**：员工考评人关系分页查询（关联 `perf_appraiser_relationship` 表）
- **返回**：员工列表，每条记录携带考评人关系列表
- **数据源**：默认 `dataSourceLocal`（查询 `view_ehr_employee` 视图）
- **详细 SQL**：见 [ehr-integration.md 第 9.6 节](ehr-integration.md#96-查询-sql)
- ⚠️ **死代码**：此方法在 Service/DAO 层已声明，但全代码库无 Controller 调用

#### `List<EmployeeVO> selectBySelectivePageableVO(PageParam<EmployeeVO> pageParam)`
- **功能**：员工分页查询

#### `Integer initUser(List<EmployeeVO> employeeList)`
- **功能**：初始化用户账号（根据 EHR 员工数据创建 PMS 用户）

#### `List<Select2Data> selectEmployeeSelect2Data(Select2Data select2Data)`
- **功能**：员工 Select2 数据查询（用于下拉选择）

---

## 13. IPmSynchronizeService — 跨系统数据同步服务

### 类概述
- 接口：`IPmSynchronizeService` extends `ISynchronizeService`（`com.dp.plat.pms.springmvc.service.IPmSynchronizeService`）
- 实现类：`PmSynchronizeService`（`com.dp.plat.pms.springmvc.service.impl.PmSynchronizeService`）
- 依赖 DAO：`PmSynchronizeMapper`
- 详细方法说明：[sap-contract.md](sap-contract.md)

### 方法列表（仅列出 SAP 合同相关方法）

#### `int insertOfstContractHeadSAP(List<OfstContractHeadSAP> record)`
- **功能**：批量插入 SAP 合同头数据到本地表 `sms_ofst_contract_head_sap`
- **入参**：`List<OfstContractHeadSAP>`（子类，含主键 id）
- **返回**：受影响行数
- **数据源**：`dataSourceLocal`（PMS MySQL）
- **SQL 特性**：`INSERT INTO ... ON DUPLICATE KEY UPDATE`

#### `List<OfstContractHead> selectAllOfstContractHeadSAP()`
- **功能**：从 CRM 数据源查询所有 SAP 合同头数据
- **返回**：`List<OfstContractHead>`（父类）
- **数据源**：`dataSourceCRM`（SQL Server，查询视图 `DPtech_v_order_contract_4_pms`）
- **注意**：调用前必须通过 `DataSourceHolder.setDataSourceType("CRM")` 显式切换数据源

#### `void clearAllOfstContractHeadSAP()`
- **功能**：清空本地 SAP 合同头表
- **数据源**：`dataSourceLocal`（PMS MySQL）
- **SQL**：`TRUNCATE TABLE sms_ofst_contract_head_sap`

#### `void splitAfProjectByProductCode(Map<String, Object> params)`
- **功能**：按产品编码拆分工程实施项目与安服项目
- **数据源**：`dataSourceLocal`

#### `void insertSyncLog(SyncLog syncLog)` / `void insertSyncState(SyncState syncState)`
- **功能**：记录同步日志 / 同步状态
- **继承自**：`ISynchronizeService`

---

## 事务规则总结

### 事务方法前缀（PROPAGATION_REQUIRED）
`insert*` | `update*` | `delete*` | `add*` | `save*` | `do*` | `start*` | `submit*` | `transfer*`

### 非事务方法
`query*` | `find*` | `get*` | `check*` | `select*` | `count*` | `list*`
