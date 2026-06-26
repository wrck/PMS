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
9. [IIndustryLeakService — 行业泄露服务](#9-iindustryleakservice--行业泄露服务)
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

## 9. IIndustryLeakService — 行业泄露服务

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

## 11. IExcelAnalysisService — Excel分析服务

### 类概述
- 接口：`IExcelAnalysisService`
- 实现类：`ExcelAnalysisService`
- 依赖 DAO：`ExcelAnalysisMapper`

### 方法列表

#### `void importExcel(MultipartFile file, String dataType)`
- **功能**：导入Excel文件
- **事务类型**：事务

#### `List<Map<String, Object>> analyzeData(String dataType, String fileId)`
- **功能**：分析数据
- **事务类型**：无事务

#### `void saveAnalysisResult(String fileId, List<Map<String, Object>> results)`
- **功能**：保存分析结果
- **事务类型**：事务

---

## 事务规则总结

### 事务方法前缀（PROPAGATION_REQUIRED）
`insert*` | `update*` | `delete*` | `add*` | `save*` | `do*` | `start*` | `submit*` | `transfer*`

### 非事务方法
`query*` | `find*` | `get*` | `check*` | `select*` | `count*` | `list*`
