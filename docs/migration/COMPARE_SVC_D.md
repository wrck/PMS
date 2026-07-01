# PMS Service层迁移比对报告（子包模块）

**比对时间**: 2026-07-01  
**比对范围**: 子包模块 ServiceImpl 4个文件  
**源目录**: `PMS/PMS-struts/src/com/dp/plat/`  
**目标目录**: `PMS/PMS-springboot/pms-service/src/main/java/com/dp/plat/service/impl/`

---

## 汇总

| # | 源文件 | 目标文件 | 总方法数 | 已迁移 | 部分迁移 | 未迁移 | 结论 |
|---|--------|----------|----------|--------|----------|--------|------|
| 1 | plus/certificate/service/CertificateServiceImpl.java (116行) | CertificateServiceImpl.java | 2 | 0 | 0 | 2 | ❌ **未迁移** |
| 2 | warrantyCallback/service/impl/WarrantyCallbackServiceImpl.java (517行) | WarrantyCallbackServiceImpl.java | ~25 | 0 | 3 | ~22 | ❌ **未迁移** |
| 3 | prob/service/ProbManageServiceImpl.java (1139行) | ProbServiceImpl.java | ~35 | 8 | 5 | ~22 | ⚠️ **部分迁移** |
| 4 | subcontract/service/impl/SubcontractServiceImpl.java (3500行) | SubcontractServiceImpl.java | ~60 | 8 | 10 | ~42 | ⚠️ **部分迁移** |

---

## 1. CertificateServiceImpl（印章/OQC管理）

**源文件**: `plus/certificate/service/CertificateServiceImpl.java` (116行)  
**目标文件**: `CertificateServiceImpl.java`  
**整体评估**: ❌ **未迁移** — 目标文件为全新CRUD模块，与源文件无对应关系

### 方法比对

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `queryOQCInfo(String barcode)` | `getByBarcode(String barcode)` | ❌ **未迁移** | 源方法调用`certificateDao.queryOQCInfo`查询OQC信息，返回`List<Map<String,String>>`。目标方法使用MyBatis-Plus查询PmsCertificate实体，实体模型完全不同 |
| 2 | `parseExcelFile(File file)` — 逐行解析Excel印章登记表，读取id/name/info/description/user/takeTime/backTime/remark等列，先`deleteSealInfo`清空再逐行`insertSealInfo`入库 | `uploadSealInfo(Long id, String sealInfo)` | ❌ **未迁移** | 源方法是完整Excel解析+批量入库（含字段映射、空行跳过、前值继承逻辑）。目标方法仅为单条记录更新sealInfo字段，业务逻辑完全不同 |

### 关键缺失
- **Excel解析逻辑**: `ExcelParser`、`DateCellValueParser`、`StringCellParser`、`IntegerCellParser`等工具类调用完全缺失
- **批量入库**: 源方法循环解析每行并批量insert，目标无此逻辑
- **字段继承逻辑**: 源方法中name/info/description列有前值继承（blank时使用上一行值），目标无此逻辑
- **异常处理**: 源方法catch `InvalidFormatException | IOException`并包装为RuntimeException，目标无

---

## 2. WarrantyCallbackServiceImpl（质保回访管理）

**源文件**: `warrantyCallback/service/impl/WarrantyCallbackServiceImpl.java` (517行)  
**目标文件**: `WarrantyCallbackServiceImpl.java`  
**整体评估**: ❌ **未迁移** — 目标文件为全新简化CRUD，源文件核心业务逻辑几乎全部缺失

### 方法比对

#### 2.1 基础查询（部分保留框架）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `selectProjectWarrantyCallbackById(Integer)` | `getDetail(Long)` | ⚠️ **部分迁移** | 目标增加了null检查+BusinessException，但ID类型从Integer改为Long |
| 2 | `selectProjectWarrantyCallbackVOById(Integer)` | 无 | ❌ **未迁移** | VO查询方法缺失 |
| 3 | `selectProjectWarrantyCallbackList(...)` | 无 | ❌ **未迁移** | 条件列表查询缺失 |
| 4 | `selectProjectWarrantyCallbackVOList(...)` | 无 | ❌ **未迁移** | VO条件列表查询缺失 |
| 5 | `selectProjectWarrantyCallbackVOListPageable(...)` — 含DisplayParam分页、export导出、count统计 | `queryPage(...)` | ⚠️ **部分迁移** | 目标使用MyBatis-Plus分页，但缺失：① export导出模式处理 ② 自定义offset/limit分页逻辑 ③ UnsupportedEncodingException处理 |

#### 2.2 写入操作

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 6 | `insertProjectWarrantyCallback(...)` — 设createBy/createTime | `create(...)` | ⚠️ **部分迁移** | 目标额外设置isDelete=0，但缺失源的字段映射（如projectId等业务字段） |
| 7 | `insertProjectWarrantyCallbackSelective(...)` | 无 | ❌ **未迁移** | 选择性插入缺失 |
| 8 | `updateProjectWarrantyCallbackByIdSelective(...)` | `update(...)` | ⚠️ **部分迁移** | 基本框架迁移，但缺失源方法的Selective更新语义 |
| 9 | `insertOrUpdateProjectWarrantyCallback(...)` — id==null时insert，否则update | 无 | ❌ **未迁移** | 插入或更新逻辑缺失 |

#### 2.3 维保信息管理（全部缺失）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 10 | `fillProjectWarrantyInfo(...)` — 填充项目维保状态、级别、增值服务，含BeanUtils.populate和customInfo合并 | 无 | ❌ **未迁移** | 复杂的维保信息填充逻辑完全缺失 |
| 11 | `selectProjectWarrantyByProjectId(Integer)` | 无 | ❌ **未迁移** | 按项目ID查询维保信息缺失 |
| 12 | `selectProjectWarranty(...)` (2个重载) | 无 | ❌ **未迁移** | 维保查询方法缺失 |
| 13 | `selectCustomerProjectWarrantyCallbackStatistics(...)` | `queryCustomerProject(...)` | ⚠️ **部分迁移** | 目标仅按客户名like查询，缺失DisplayParam分页和统计聚合逻辑 |

#### 2.4 工作流任务管理（全部缺失）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 14 | `queryWarrantyCallbackTaskList(...)` (7个重载) — 基于Activiti TaskService查询任务，含角色判断(emRole/cbRole/wcbRole/zrRole)、区域权限过滤 | 无 | ❌ **未迁移** | 整套工作流任务查询体系缺失，目标无TaskService依赖 |

#### 2.5 邮件/通知辅助方法（全部缺失）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 15 | `getNextAssignPer(String roleStr)` | 无 | ❌ **未迁移** | 按角色查询下级审核人缺失 |
| 16 | `getNextAssignPer(int roleId)` / `getNextAssignPer(int roleId, String dpNo)` — 含部门转换、区域权限回退 | 无 | ❌ **未迁移** | 复杂的审批人查找逻辑缺失 |
| 17 | `initProjectDetailTable(List<Project>)` | 无 | ❌ **未迁移** | 项目明细HTML生成缺失 |

#### 2.6 其他查询

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 18 | `queryProjectList(Project)` / `queryProjectList(ProjectWarrantyCallback)` | 无 | ❌ **未迁移** | 项目列表查询缺失 |
| 19 | `selectProjectWarrantyCallbackMapList(...)` | 无 | ❌ **未迁移** | Map列表查询缺失 |
| 20 | `countProjectWarrantyCallbackVOListPageable(...)` | 无 | ❌ **未迁移** | 计数方法缺失 |

### 关键缺失
- **Activiti工作流集成**: 源文件深度依赖`TaskService`，包含7个`queryWarrantyCallbackTaskList`重载，目标完全缺失
- **邮件通知**: 源文件通过`SendMailService`发送通知，目标无
- **维保信息管理**: `fillProjectWarrantyInfo`、`selectProjectWarranty`等核心业务方法缺失
- **审批人查找**: 复杂的角色+部门+区域权限审批人查找逻辑缺失
- **依赖服务**: `BasicDataService`、`CallBackService`、`UserManageService`、`PmClosedLoopDao`、`WorkFlowService`、`DepartmentManageService`全部缺失

---

## 3. ProbManageServiceImpl（技术公告/缺陷管理）

**源文件**: `prob/service/ProbManageServiceImpl.java` (1139行)  
**目标文件**: `ProbServiceImpl.java`  
**整体评估**: ⚠️ **部分迁移** — 基础CRUD已迁移，核心业务逻辑（邮件通知、工作流、统计）大量缺失

### 方法比对

#### 3.1 技术公告CRUD

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `saveProb(Prob, List<SoftVersion>, String root)` — 保存公告+软件版本+产品型号，非草稿时发送邮件通知 | `create(PmsProb)` | ⚠️ **部分迁移** | 目标仅做基础insert，缺失：① 产品型号解析(probProductList JSON) ② 软件版本保存 ③ 邮件通知 ④ 草稿/非草稿状态判断 |
| 2 | `queryProbList(Prob, DisplayParam)` | `queryPage(...)` | ⚠️ **部分迁移** | 目标使用MyBatis-Plus分页，缺失DisplayParam自定义分页逻辑 |
| 3 | `queryOneProb(Prob)` | `getDetail(Long)` | ✅ **完全迁移** | 基本查询+null检查已迁移 |
| 4 | `updateProb(Prob, List<SoftVersion>)` — 含角色判断(PROB_ADMIN)、状态设置、产品型号解析、邮件通知 | `update(PmsProb)` | ⚠️ **部分迁移** | 目标仅做基础update，缺失：① 角色判断逻辑 ② 状态自动设置("8"待确认) ③ 软件版本/产品型号更新 ④ 邮件通知(根据角色发送不同群组) |
| 5 | `deleteProbInfo(int)` | `delete(Long)` | ✅ **完全迁移** | 目标额外删除关联数据(softVersion/restore/product)，更完整 |

#### 3.2 软件版本管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 6 | `updateProbSoftVersion(List<SoftVersion>, int)` — 先失效原版本再新增 | `saveSoftVersions(Long, List)` / `updateProbSoftVersion(...)` | ✅ **完全迁移** | 逻辑一致：删除旧版本+插入新版本 |
| 7 | `checkSoftVersionList(SoftVersion)` | `checkSoftVersionList(Map)` | ⚠️ **部分迁移** | 参数类型从实体改为Map，需确认Mapper层兼容 |
| 8 | `querySoftVersionList(int)` / `querySoftVersionList(SoftVersion)` | `querySoftVersionList(Long)` | ✅ **完全迁移** | 按probId查询已迁移 |

#### 3.3 恢复任务管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 9 | `queryProbRestoreList(...)` | `queryRestorePage(...)` | ⚠️ **部分迁移** | 目标使用MyBatis-Plus分页，缺失DisplayParam |
| 10 | `insertBatchProbRestoreTask(...)` — 更新公告状态为"解决中"+插入流程记录+批量插入子任务+邮件通知 | `releaseTask(...)` | ⚠️ **部分迁移** | 目标保留了基本的批量插入逻辑，但缺失：① 公告状态更新("5"解决中) ② 流程过程记录插入 ③ 邮件通知(总部二线/维护经理/产品经理/用服) |
| 11 | `queryProbRestoreTaskList(...)` | 无 | ❌ **未迁移** | 恢复任务列表查询缺失 |
| 12 | `queryProbRestoreTaskProjectList(...)` | 无 | ❌ **未迁移** | 恢复任务项目列表查询缺失 |
| 13 | `updateProbRestoreTask(...)` — 插入流程记录+更新办理人+更新状态+邮件通知(根据isProbAdmin不同发送不同群组) | `updateRestoreTask(...)` | ⚠️ **部分迁移** | 目标仅做状态/assignee更新，缺失：① 流程过程记录 ② 复杂的邮件通知逻辑(3种角色分支) ③ 软件版本变更历史查询 |
| 14 | `bacthDeleteProbRestores(String)` | `batchDeleteRestores(String)` | ✅ **完全迁移** | 逻辑一致 |
| 15 | 无 | `countUnfinishedRestores(Long)` | ✅ 新增 | 目标新增方法 |

#### 3.4 产品型号管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 16 | `updateProbProduct(Prob, List<? extends ProbProduct>)` — 失效原产品+逐个插入新产品 | `saveProduct(PmsProbProduct)` | ⚠️ **部分迁移** | 目标仅处理单个产品，缺失批量更新逻辑（失效+重新插入） |
| 17 | `selectProbProductById/VOById/List/ListPageable/count/insert/insertSelective/insertOrUpdate/updateById/updateByIdSelective/updateByProbIdSelective/deleteById/deleteByProbId` (14个方法) | `queryProducts(Long)` / `saveProduct(...)` | ❌ **未迁移** | 源文件提供完整CRUD+分页+VO查询，目标仅有2个方法 |
| 18 | `selectProductComponentById/VOById/List/ListPageable/count/insert/insertSelective/insertOrUpdate/updateById/updateByIdSelective/deleteById` (11个方法) | 无 | ❌ **未迁移** | 产品组件管理完全缺失 |

#### 3.5 产品物料查询

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 19 | `selectProductItemListByParams(Map)` — ProductItemExample构建+查询 | `queryProductItemList(Map)` | ⚠️ **部分迁移** | 目标简化为查prob_product表，缺失ProductItemExample复杂查询构建 |
| 20 | `selectProductItemListFilteredByParams(Map)` | 无 | ❌ **未迁移** | 过滤查询缺失 |
| 21 | `selectProductItemListByItemSearch(...)` — 含空格分词+OR模糊+排除条件 | 无 | ❌ **未迁移** | 搜索逻辑缺失 |
| 22 | `selectProductItemListByExample(ProductItemExample)` | 无 | ❌ **未迁移** | Example查询缺失 |

#### 3.6 邮件通知（全部缺失）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 23 | `keepRelaseEmail(Prob, String, String, String, Map)` — 使用模板发送 | 无 | ❌ **未迁移** | 模板邮件发送缺失 |
| 24 | `keepRelaseEmail(Prob, List<SoftVersion>, String, String, String, String)` — 无模板发送 | 无 | ❌ **未迁移** | 无模板邮件发送缺失 |
| 25 | `defaultParaMap(Prob, List<SoftVersion>, int, Map)` — 构建邮件模板参数(含角色判断：isProbAdmin=1/2/3/其他，状态判断，HTML表格生成) | 无 | ❌ **未迁移** | ~200行的邮件模板参数构建逻辑完全缺失 |

#### 3.7 其他功能

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 26 | `queryNextProbNum()` — 生成"SP.yyyyNNNN"格式编号 | 无 | ❌ **未迁移** | 编号生成缺失 |
| 27 | `insertProbTaskWeekly(...)` — 保存周报+邮件通知 | `uploadRestoreWeekly(...)` | ⚠️ **部分迁移** | 目标有基本框架但实现不完整(注释提示需补充) |
| 28 | `queryProbWeekly(int, String)` | 无 | ❌ **未迁移** | 周报查询缺失 |
| 29 | `updateProbStatus(Prob)` — 更新状态+邮件通知 | `audit(Long, String)` | ⚠️ **部分迁移** | 目标仅做状态更新，缺失邮件通知 |
| 30 | `queryExportProbList(Map)` | `exportProbList(Map)` | ⚠️ **部分迁移** | 目标返回byte[]但引用了不存在的ExportUtils类 |
| 31 | `batchAddSoftVersion(List)` | `batchImportSoftVersion(List<Map>)` | ⚠️ **部分迁移** | 参数类型不同 |
| 32 | `queryProbStatisticList(...)` / `queryProbStatisticListWithReport(...)` / `queryProbStatisticProjectList(...)` / `queryContractShipmentSoftList(...)` | `queryStatistics(Map)` | ⚠️ **部分迁移** | 目标用Java Stream做内存聚合，源方法在DB层做统计，性能和功能差异大 |
| 33 | `readLog(int, int)` — 异步线程记录阅读日志 | `recordRead(Long, String)` | ⚠️ **部分迁移** | 目标同步执行，缺失异步线程处理 |
| 34 | `queryProbReadLogList(...)` — 含角色权限过滤 | `queryReadLogs(Long)` | ⚠️ **部分迁移** | 目标仅按probId查询，缺失角色权限过滤 |
| 35 | `queryProbFileMap(int)` | 无 | ❌ **未迁移** | 附件文件查询缺失 |

### 关键缺失
- **邮件通知体系**: `keepRelaseEmail`(2个版本) + `defaultParaMap`(~200行模板参数构建) 完全缺失
- **产品组件管理**: 11个ProductComponent方法完全缺失
- **产品型号管理**: 14个ProbProduct方法仅迁移2个
- **产品物料搜索**: `selectProductItemListByItemSearch`含复杂分词+模糊+排除逻辑缺失
- **统计分析**: 源方法在DB层聚合，目标改为Java内存聚合，大数量场景性能堪忧
- **Activiti工作流**: 源文件无直接工作流调用(通过邮件通知间接触发)，但依赖`ServletActionContext`

---

## 4. SubcontractServiceImpl（项目转包管理）

**源文件**: `subcontract/service/impl/SubcontractServiceImpl.java` (3500行)  
**目标文件**: `SubcontractServiceImpl.java`  
**整体评估**: ⚠️ **部分迁移** — 基础CRUD已迁移，工作流审批体系(核心业务)全部缺失

### 方法比对

#### 4.1 基础CRUD

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `selectSubcontractProjectById(Integer)` | `getDetail(Long)` | ✅ **完全迁移** | 基本查询已迁移 |
| 2 | `selectSubcontractProjectVOById(Integer)` | 无 | ❌ **未迁移** | VO查询缺失 |
| 3 | `selectSubcontractProjectList/VOList/VOListPageable` | `queryPage(...)` | ⚠️ **部分迁移** | 目标使用MyBatis-Plus分页，缺失DisplayParam、export模式、VO查询 |
| 4 | `insertSubcontractProject(...)` / `insertSubcontractProjectSelective(...)` | `create(...)` | ⚠️ **部分迁移** | 目标设默认状态0(草稿)，缺失effectiveFrom字段设置 |
| 5 | `updateSubcontractProjectByIdSelective(...)` | `update(...)` | ✅ **完全迁移** | 基本更新已迁移 |
| 6 | `deleteProbInfo` → 无 | `delete(Long)` | ✅ 新增 | 目标新增删除方法(含级联删除关联数据) |

#### 4.2 设备行管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 7 | `selectSubcontractLineList(...)` | `queryLines(Long)` | ✅ **完全迁移** | |
| 8 | `batchInsertSubcontractLine(...)` | `saveLine(...)` / `deleteLine(...)` | ⚠️ **部分迁移** | 源方法含批量删除+批量插入(barCodes数组)，目标改为单条CRUD |

#### 4.3 交付件管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 9 | `selectSubcontractDeliverById/List/VOList` | `queryDelivers(Long)` | ⚠️ **部分迁移** | 仅按subcontractId查询，缺失按条件查询和VO查询 |
| 10 | `insertSubcontractDeliver(...)` | `saveDeliver(...)` | ⚠️ **部分迁移** | 目标含insert/update逻辑，但缺失源方法的文件上传处理 |
| 11 | `updateSubcontractDeliverByIdSelective(...)` | 无 | ❌ **未迁移** | |
| 12 | `deleteSubcontractDeliver(...)` — 含发票编号更新 | `deleteDeliver(Long)` | ⚠️ **部分迁移** | 目标仅删除记录，缺失关联的发票编号更新逻辑 |
| 13 | `saveDeliverFiles(...)` — 文件上传+类型检查+发票识别+付款更新 | 无 | ❌ **未迁移** | ~100行的文件上传+发票识别逻辑完全缺失 |

#### 4.4 付款管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 14 | `selectSubcontractPaymentList(...)` / `selectSubcontractPaymentById(...)` | `queryPayments(Long)` | ⚠️ **部分迁移** | 目标仅按subcontractId查询 |
| 15 | `insertSubcontractPayment(...)` | `savePayment(...)` | ✅ **完全迁移** | |
| 16 | `updateSubcontractPaymentByIdSelective(...)` | 含在`savePayment`中 | ✅ **完全迁移** | |
| 17 | `saveSubcontractPayment(List, Integer[])` — 含批量删除+批量更新 | 无 | ❌ **未迁移** | 批量保存(含删除)逻辑缺失 |
| 18 | `deleteSubcontractPaymentById(Integer)` | `deletePayment(Long)` | ✅ **完全迁移** | |
| 19 | `querySubcontractPaiedAmount(Integer)` | 无 | ❌ **未迁移** | 已付金额查询缺失 |
| 20 | `verifySubcontractPaymentDeliver(...)` (3个重载) — 发票识别+验真 | 无 | ❌ **未迁移** | ~120行的发票识别验真逻辑完全缺失 |
| 21 | `updateSubcontractPaymentInvoiceNumber(Set<Integer>)` | 无 | ❌ **未迁移** | 发票编号汇总更新缺失 |

#### 4.5 服务商管理

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 22 | `selectSubcontractFacilitatorList(...)` | `queryFacilitators()` | ⚠️ **部分迁移** | 目标查全部活跃，缺失条件查询 |
| 23 | `selectSubcontractFacilitatorById(Integer)` | `getFacilitator(Long)` | ✅ **完全迁移** | |
| 24 | `insertSubcontractFacilitator(...)` | `saveFacilitator(...)` | ⚠️ **部分迁移** | 目标合并insert/update，缺失源方法的state=true设置和effectiveFrom处理 |
| 25 | `updateSubcontractFacilitatorByIdSelective(...)` | 含在`saveFacilitator`中 | ✅ **完全迁移** | |

#### 4.6 工作流审批（核心业务 — 全部缺失）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 26 | `startSubcontractFlow(WorkflowCommonParam, ...)` (2个重载) — 启动转包申请流程，含重复流程判断、状态更新、Activiti启动、任务办理、邮件通知、自动审批(受益部门相同时) | `startFlow(Long)` | ⚠️ **部分迁移** | 目标仅更新状态为1(审批中)，缺失：① Activiti流程启动 ② 任务办理 ③ 角色判断 ④ 邮件通知 ⑤ 自动审批逻辑 |
| 27 | `profitSerivceManagerFlow(...)` — 受益部门服务经理审批，含从流程获取下级审批人、特殊部门办事处主任审批、角色候选 | `approve(...)` | ⚠️ **部分迁移** | 目标仅更新状态(2通过/3驳回)，缺失全部工作流逻辑 |
| 28 | `normalApproveSubcontractFlow(...)` — 通用审批节点，含循环流程判断、nextState动态状态 | 无 | ❌ **未迁移** | |
| 29 | `auditSubcontractFlow(...)` (3个重载) — 工程管理部主管审批，含价格保存、办事处主任查找、角色候选 | 无 | ❌ **未迁移** | |
| 30 | `auditNormalApproveSubcontractFlow(...)` — 通用审批+价格保存 | 无 | ❌ **未迁移** | |
| 31 | `approveSubcontractFlow(...)` — 办事处主任审批，含zrApproveTime更新、服务商通知 | 无 | ❌ **未迁移** | |
| 32 | `closeSubcontractFlow(...)` — 闭环流程 | `close(...)` | ⚠️ **部分迁移** | 目标仅更新状态为4，缺失工作流闭环逻辑 |
| 33 | `generateContractFlow(...)` — 生成合同号，含合同号校验、服务经理通知 | 无 | ❌ **未迁移** | |
| 34 | `applyPaymentFlow(...)` (2个重载) — 付款申请，含回访判断、付款人通知 | 无 | ❌ **未迁移** | |
| 35 | `submitCallBackFlow(...)` / `submitCallBackFlow2(...)` — 回访审批，含回访通过/不通过/无法回访三种状态处理 | 无 | ❌ **未迁移** | |
| 36 | `approvePaymentFlow(...)` — 付款审批，含闭环/驳回/付款三种状态 | 无 | ❌ **未迁移** | |
| 37 | `submitAcceptanceFlow(...)` — 验收审批 | 无 | ❌ **未迁移** | |
| 38 | `startCallBackFlow(Integer)` / `startCallBackFlow(Integer, ActComment)` — 启动回访流程 | `startCallBackFlow(Long)` | ⚠️ **部分迁移** | 目标仅更新callbackState=1，缺失Activiti流程启动+问卷版本+任务关联 |
| 39 | `terminateWorkFlow(Integer)` / `terminateWorkFlow(Integer, String)` / `terminateWorkFlow(Integer, WorkflowCommonParam)` — 终止流程，含循环终止所有任务 | 无 | ❌ **未迁移** | |

#### 4.7 工作流辅助方法（全部缺失）

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 40 | `querySubcontractTaskList(...)` (8个重载) — 含角色组判断(emRole/cbRole/zrRole/smRole/profitSmRole等)、区域权限过滤 | 无 | ❌ **未迁移** | 整套工作流任务查询体系缺失 |
| 41 | `submitSelfTask(WorkflowCommonParam, Map)` / `submitSelfTask(SubcontractComment, Map)` | 无 | ❌ **未迁移** | 任务提交方法缺失 |
| 42 | `queryCurrentTask(Integer)` | 无 | ❌ **未迁移** | |
| 43 | `queryCurrentSubcontractCommon(Integer)` | 无 | ❌ **未迁移** | |
| 44 | `queryCurrentWorkFlowCommonParam(...)` (4个重载) | 无 | ❌ **未迁移** | |

#### 4.8 回访问卷

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 45 | `insertSubcontractQuesnaire(...)` — 插入问卷头+行+关联回访 | 无 | ❌ **未迁移** | 问卷管理完全缺失 |
| 46 | `insertSubcontractCallback(...)` / `insertSubcontractCallbackSelective(...)` / `updateSubcontractCallbackByIdSelective(...)` | 无 | ❌ **未迁移** | 回访记录CRUD缺失 |
| 47 | `selectSubcontractCallbackList(...)` / `selectMaxSubcontractCallback(...)` | 无 | ❌ **未迁移** | |

#### 4.9 其他查询

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 48 | `queryProjectList(Project)` / `queryProjectList(SubcontractProject)` | `queryProjectList(String)` | ⚠️ **部分迁移** | 目标改为按contractNos查PmsProject，逻辑不同 |
| 49 | `queryShipmentinfoByContractNosAndProjectIds(...)` (3个重载) | `queryShipmentInfo(String, String)` | ⚠️ **部分迁移** | 目标简化为按contractNos查，缺失projectIds过滤和excludeTransferOut参数 |
| 50 | `checkSubcontractName(...)` (2个重载) | 无 | ❌ **未迁移** | 名称校验缺失 |
| 51 | `queryContractNoEngineeFee(...)` / `queryContractNoEngineeFeeWithSubPrice(...)` | 无 | ❌ **未迁移** | 工程费查询缺失 |
| 52 | `querySubcontractCommentList(Integer)` | 无 | ❌ **未迁移** | 审批意见查询缺失 |
| 53 | `insertSubcontractPrice(...)` / `updateSubcontractPriceByIdSelective(...)` | 无 | ❌ **未迁移** | 转包价格管理缺失 |
| 54 | `selectRejectedSubcontractProjectList(...)` | 无 | ❌ **未迁移** | 驳回列表查询缺失 |
| 55 | `querySubcontractExportData(...)` | 无 | ❌ **未迁移** | 导出数据查询缺失 |
| 56 | `queryNextPaymentTask()` / `querySSESubcontractPaymentList()` | 无 | ❌ **未迁移** | SSE推送和付款任务查询缺失 |
| 57 | `querySubcontractInfoForProject(String)` | 无 | ❌ **未迁移** | |
| 58 | `selectDefaultMultiDimByDep(...)` (2个重载) | 无 | ❌ **未迁移** | 多维度信息查询缺失 |

#### 4.10 辅助方法

| # | 源方法 | 目标对应 | 状态 | 说明 |
|---|--------|----------|------|------|
| 59 | `getNextAssignPer(String)` / `getNextAssignPer(int)` / `getNextAssignPer(int, String)` — 含部门转换+区域权限回退 | 无 | ❌ **未迁移** | 审批人查找逻辑缺失 |
| 60 | `notifyFacilitator(SubcontractProject)` — 服务商下单通知 | 无 | ❌ **未迁移** | |
| 61 | `initProjectDetailTable(List<Project>)` | 无 | ❌ **未迁移** | |

### 关键缺失
- **Activiti工作流集成**: 源文件深度依赖`RuntimeService`、`TaskService`，包含~20个工作流方法，目标全部缺失或简化为状态更新
- **邮件通知体系**: 每个审批节点都有`NotificationTemplateUtil.keepMail`通知，目标完全缺失
- **发票识别验真**: `verifySubcontractPaymentDeliver`含FPApi调用、电子发票识别，~120行逻辑缺失
- **回访问卷管理**: `insertSubcontractQuesnaire`含问卷头/行插入，完全缺失
- **服务商通知**: `notifyFacilitator`含模板邮件发送，缺失
- **任务查询体系**: 8个`querySubcontractTaskList`重载含复杂角色组判断，完全缺失
- **依赖服务**: `BasicDataService`、`CallBackService`、`UserManageService`、`PmClosedLoopDao`、`WorkFlowService`、`DepartmentManageService`全部缺失

---

## 总体结论

### 迁移完成度评估

| 模块 | 完成度 | 风险等级 |
|------|--------|----------|
| CertificateServiceImpl | **~5%** | 🔴 高 — 目标文件为全新模块，与源文件几乎无对应关系 |
| WarrantyCallbackServiceImpl | **~10%** | 🔴 高 — 仅基础CRUD框架，核心业务全部缺失 |
| ProbManageServiceImpl | **~30%** | 🟡 中 — 基础CRUD+软件版本已迁移，邮件通知/工作流/统计缺失 |
| SubcontractServiceImpl | **~20%** | 🔴 高 — 基础CRUD已迁移，但~20个工作流方法+发票识别+问卷管理全部缺失 |

### 共性缺失项

1. **Activiti工作流集成**: 4个文件中有3个深度依赖Activiti（WarrantyCallback、Prob、Subcontract），目标全部缺失
2. **邮件通知体系**: `NotificationTemplateUtil.keepMail` / `keepMailNoTemplate` 调用全部缺失
3. **复杂审批人查找**: `getNextAssignPer`方法（含角色+部门+区域权限）全部缺失
4. **依赖服务**: `BasicDataService`、`CallBackService`、`UserManageService`、`PmClosedLoopDao`、`WorkFlowService`、`DepartmentManageService`在目标中均未注入
5. **VO/Map查询**: 源文件大量使用VO和Map返回类型，目标统一使用实体类
6. **DisplayParam分页**: 源文件自定义分页参数，目标使用MyBatis-Plus IPage
7. **BaseServiceImpl**: 源文件继承BaseServiceImpl（提供getLoginName/getRealname等），目标无基类
