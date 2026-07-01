# PMS迁移比对报告 - 第1组：小模块

> 比对时间: 2026-07-01
> 比对范围: 7个Struts Action → 对应SpringBoot Controller/Service

---

## 1. BaseAction.java → (无直接对应)

### 说明
BaseAction是所有Struts Action的基类，提供通用功能。SpringBoot中这些功能由框架自动处理，无需手动迁移。

| 源方法 | 功能 | SpringBoot对应 | 状态 |
|--------|------|----------------|------|
| `start()` | 默认返回INPUT页面 | Spring MVC自动路由 | ✅ 框架内置 |
| `setErrmsg()` | 错误信息设置 | 全局异常处理器 `@ControllerAdvice` | ✅ 框架内置 |
| `setWarnMessage()` | 警告信息设置 | 全局异常处理器 | ✅ 框架内置 |
| `getErrmsg()` | 获取错误信息 | R统一响应封装 | ✅ 框架内置 |
| Servlet Request/Response注入 | Struts Aware接口 | Spring自动注入 `HttpServletRequest` | ✅ 框架内置 |
| `addActionError/Message/FieldError` | Struts验证 | `@Valid` + `BindingResult` | ✅ 框架内置 |

**总结: ✅ 完全迁移** — 基类功能由SpringBoot框架自动承担，无需额外代码。

---

## 2. BasicDataManageAction.java → BasicDataController.java + BasicDataServiceImpl.java

### 方法: `execute()`
- **源逻辑摘要**: 查询所有基础数据类型列表(`queryBasicDataType`)，若传入`basicData`则按类型编码查询该类型下的所有数据(`queryBasicDataBeanAll`)，否则返回空列表
- **目标实现**: Controller提供`list(@RequestParam dataType)`和`all(@RequestParam dataType)`两个接口，Service中`queryByType()`和`queryAllByType()`分别实现
- **状态**: ✅ 完全迁移
- **差异说明**: 源方法一次返回类型+数据两个列表；目标拆分为两个独立接口，前端按需调用，架构更合理

### 方法: `basicdataUpdate()`
- **源逻辑摘要**: 两阶段操作——(1)若`id!=0`且`basicDataId==null`则按ID查询详情并返回编辑页面(INPUT)；(2)否则执行更新操作，刷新`SystemContext`缓存
- **目标实现**: `update(@RequestBody SysBasicData data)`直接执行更新，`queryName()`和`queryByType()`提供查询功能；无缓存刷新调用
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 更新逻辑已迁移
  - ✅ 查询单条数据已迁移（通过`queryName`和`queryByType`）
  - ⚠️ 缺少`SystemContext.getSystemContext().refresh()`缓存刷新调用（目标Service有`refreshCacheData()`方法但Controller未在update后调用）

### 方法: `basicdataInsert()`
- **源逻辑摘要**: 若`basicData==null`则查询数据类型列表并返回新增页面(INPUT)；否则执行插入操作
- **目标实现**: `add(@RequestBody SysBasicData data)`执行插入
- **状态**: ✅ 完全迁移
- **差异说明**: 源方法的"显示新增表单"逻辑在前后端分离架构中由前端处理，后端只需提供插入接口

### 方法: `findBasicDataId()`
- **源逻辑摘要**: AJAX检查指定`dataTypeCode`+`basicDataId`组合是否已存在，返回结果数量
- **目标实现**: **无对应接口**
- **状态**: ❌ 未迁移
- **差异说明**: 缺少基础数据编码唯一性校验接口。建议在Controller中增加 `@GetMapping("/check-code")` 接口

### 方法: `executeSql()`
- **源逻辑摘要**: 执行原始SQL（带安全检查：必须有WHERE条件或为INSERT语句），返回执行结果消息
- **目标实现**: **无对应接口**
- **状态**: ✅ 有意不迁移
- **差异说明**: 此接口存在严重SQL注入风险，SpringBoot架构下不应提供原始SQL执行接口，不迁移是正确的安全决策

---

## 3. ClusterAction.java → (无直接对应Controller)

### 方法: `refreshCacheData()`
- **源逻辑摘要**: 验证签名或管理员权限后，调用`basicDataService.refreshCacheData()`刷新集群缓存数据
- **目标实现**: `BasicDataServiceImpl.refreshCacheData()`方法存在但实现为空（仅`return true`）
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ Service层方法已定义
  - ⚠️ 实现体为空，未实际实现缓存刷新逻辑
  - ⚠️ 无Controller端点暴露此功能
  - ⚠️ 缺少集群签名验证逻辑（SpringBoot可用Spring Security替代）

### 方法: `notifyCluster()`（静态方法）
- **源逻辑摘要**: 生成集群通知签名，用于多节点间通信
- **目标实现**: **无对应实现**
- **状态**: ❌ 未迁移
- **差异说明**: 集群通知机制未迁移。如仍需多节点同步，需要实现（可通过Redis Pub/Sub或消息队列替代）

---

## 4. DataAnalysisAction.java → DataAnalysisController.java

### 方法: `execute()`
- **源逻辑摘要**: 查询多个维度的基础数据（公司列表、办事处列表、项目阶段、施工类型、项目类型、导航选项卡），调用`getcbdata()`获取回访统计数据
- **目标实现**: Controller提供5个接口(`overview`, `project-status`, `by-office`, `by-time`, `custom-query`)，但**全部返回空集合`Collections.emptyList()`/`emptyMap()`**
- **状态**: ❌ 未迁移
- **差异说明**: 
  - Controller仅为占位骨架（stub），无任何实际业务逻辑
  - **无对应的Service实现文件**（`DataAnalysisServiceImpl.java`不存在）
  - 源方法中6个维度数据查询 + 回访统计查询均未实现
  - 需要创建`DataAnalysisServiceImpl.java`并实现所有查询逻辑

### 方法: `getcbdata()`（私有方法）
- **源逻辑摘要**: 查询回访数据列表(`dataAnalysisService.quesyCbDataList`)，设置`returnType`标记
- **目标实现**: **无对应实现**
- **状态**: ❌ 未迁移
- **差异说明**: 回访数据统计功能完全缺失，需要完整实现

---

## 5. DepartmentManageAction.java → SysDeptController.java + SysDeptServiceImpl.java

### 方法: `execute()`
- **源逻辑摘要**: 初始化`DisplayParam`分页参数，调用`queryDepartmentList(displayParam, department)`查询部门列表（支持分页+条件筛选）
- **目标实现**: `list()`调用`sysDeptService.queryDeptTree()`返回树形结构部门列表，无分页参数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 部门列表查询已迁移
  - ⚠️ 源方法支持`DisplayParam`分页，目标无分页（改为树形全量返回）
  - ⚠️ 源方法支持`Department`对象作为查询条件筛选，目标无筛选参数
  - 架构变更：从平铺分页改为树形结构，属于合理重构

### 方法: `refresh()`
- **源逻辑摘要**: 调用`departmentManageService.refreshDepartment()`从外部系统刷新部门数据
- **目标实现**: `refresh()`调用`sysDeptService.refreshDept()`
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 接口已暴露
  - ⚠️ Service实现体为空（仅有注释说明需要调用外部系统），实际刷新逻辑未实现

### 方法: `add()`
- **源逻辑摘要**: 返回INPUT视图（显示新增部门表单）
- **目标实现**: `add(@RequestBody DeptDTO dto)`直接创建部门
- **状态**: ✅ 完全迁移
- **差异说明**: 前后端分离架构下，前端自行渲染表单，后端只需提供创建接口

### 方法: `addSubmit()`
- **源逻辑摘要**: 调用`addDepartmentSubmit(department)`提交新部门，若返回ID<=0则返回ERROR
- **目标实现**: `add()` + `sysDeptService.createDept(dto)`实现，包含编码唯一性校验
- **状态**: ✅ 完全迁移
- **差异说明**: 目标增加了编码唯一性校验和`BusinessException`异常处理，优于源码

### 方法: `edit()`
- **源逻辑摘要**: 调用`super.execute()`（即BaseAction的execute，返回SUCCESS）
- **目标实现**: `update(@RequestBody DeptDTO dto)`实现编辑功能
- **状态**: ✅ 完全迁移
- **差异说明**: 目标实现了完整的字段更新逻辑，比源码更完善

### 目标新增方法（源码无对应）
| 方法 | 说明 |
|------|------|
| `delete(@PathVariable id)` | 删除部门（含子部门检查） |
| `update(@RequestBody dto)` | 完整的字段更新 |

---

## 6. LoginAction.java → AuthController.java + AuthServiceImpl.java

### 方法: `start()`
- **源逻辑摘要**: 检查用户是否已登录，若已登录且有默认页面则重定向
- **目标实现**: **无对应接口**
- **状态**: ✅ 有意不迁移
- **差异说明**: 前后端分离架构下，登录状态由前端JWT管理，无需后端重定向

### 方法: `execute()`
- **源逻辑摘要**: 判断是否CAS认证模式——是则走`casLogin()`，否则走`noCasLogin()`
- **目标实现**: `login(@RequestBody LoginDTO dto)`统一登录入口
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 普通登录（`noCasLogin`）已迁移
  - ❌ **CAS单点登录完全未迁移**——源码中CAS认证流程（`AssertionHolder.getAssertion()`、CAS用户名获取、`loginCas()`）全部丢失
  - 目标仅支持用户名+密码的JWT登录

### 方法: `casLogin()`
- **源逻辑摘要**: 通过CAS Filter获取`Assertion`，提取用户名，调用`loginCas()`完成CAS登录，保存登录前页面URL用于登录后跳转
- **目标实现**: **无对应实现**
- **状态**: ❌ 未迁移
- **差异说明**: CAS单点登录功能完全缺失。如生产环境使用CAS，需要集成`spring-security-cas`或类似方案

### 方法: `noCasLogin()`
- **源逻辑摘要**: 获取客户端IP，保存登录前默认页面，调用`loginService.login(user, ip)`验证用户名密码，登录成功后设置跳转URL
- **目标实现**: `login()` → `authService.login(dto)`实现
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 用户名密码验证已迁移（MD5兼容）
  - ✅ 用户状态检查已迁移
  - ✅ 密码过期检查已迁移
  - ⚠️ 缺少客户端IP记录（源码通过`HttpContext.getRemoteAddr()`获取IP，目标未传递request获取IP）
  - ⚠️ 缺少登录前默认页面保存/跳转逻辑

### 方法: `logout()`
- **源逻辑摘要**: 判断CAS模式——CAS用户重定向到CAS登出页面(`cas.dptech.com:8443/logout`)，普通用户重定向到`index.jsp`，最后调用`loginService.logout()`
- **目标实现**: `logout()` → `authService.logout(username)`
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 基本登出逻辑已迁移
  - ⚠️ JWT无状态，logout实现为空（仅客户端清除Token）
  - ❌ 缺少CAS登出重定向逻辑
  - ⚠️ 缺少Token黑名单机制（目标注释提到Redis但未实现）

### 方法: `error404()`
- **源逻辑摘要**: 返回404错误页面
- **目标实现**: **无对应接口**
- **状态**: ✅ 有意不迁移
- **差异说明**: SpringBoot全局异常处理器自动处理404

---

## 7. OperateLogAction.java → OperateLogController.java + OperateLogServiceImpl.java

### 方法: `execute()`
- **源逻辑摘要**: 调用`displayParam.getParam()`获取分页参数，查询日志列表`queryLogList(displayParam)`
- **目标实现**: `list()`支持`pageNum`、`pageSize`、`username`、`module`参数，返回分页结果
- **状态**: ✅ 完全迁移
- **差异说明**: 目标使用MyBatis-Plus分页，参数更清晰，支持更多筛选条件

### 方法: `exportlog()`
- **源逻辑摘要**: 从Excel模板生成日志导出文件——加载`template/日志.xlsx`模板，查询所有日志(`queryLogAllList`)，逐行写入Excel，保存到服务器`upload/payment/日志.xlsx`，支持下载
- **目标实现**: `export()`接口返回`List<SysOperateLog>` JSON数据
- **状态**: ⚠️ 部分迁移
- **差异说明**: 
  - ✅ 日志数据查询已迁移（`queryAllLogs()`）
  - ⚠️ **Excel文件生成逻辑未迁移**——目标返回JSON而非Excel文件
  - ⚠️ 缺少Excel模板加载、POI写入、文件保存、流式下载等逻辑
  - 如需Excel导出，建议集成`EasyExcel`或`Apache POI`并在Controller中添加文件下载端点

### 方法: `syncTask()`
- **源逻辑摘要**: 通过Spring ApplicationContext动态获取Quartz Job（支持CronTrigger、JobDetailFactoryBean、反射加载三种方式），同步执行指定的定时任务
- **目标实现**: **无对应接口**
- **状态**: ❌ 未迁移
- **差异说明**: 
  - 手动触发定时任务的功能完全缺失
  - 如仍需此功能，可通过Spring `TaskScheduler`或集成`Quartz`实现
  - 建议添加 `@PostMapping("/sync-task")` 接口

### 方法: `getDownloadLogName()` / `getInputLogStream()`
- **源逻辑摘要**: 提供日志文件下载路径和输入流
- **目标实现**: 无对应（export返回JSON）
- **状态**: ⚠️ 部分迁移
- **差异说明**: 被`export()`的JSON返回方式替代，如需文件下载需额外实现

---

## 📊 汇总表

| # | Action → Controller/Service | 方法数 | ✅完全 | ⚠️部分 | ❌未迁移 | 整体评估 |
|---|----------------------------|--------|--------|--------|---------|---------|
| 1 | BaseAction → (框架内置) | 8 | 8 | 0 | 0 | ✅ 完全迁移 |
| 2 | BasicDataManageAction → BasicDataController + Service | 5 | 3 | 1 | 1 | ⚠️ 部分迁移 |
| 3 | ClusterAction → (无直接对应) | 2 | 0 | 1 | 1 | ❌ 大部分未迁移 |
| 4 | DataAnalysisAction → DataAnalysisController | 2 | 0 | 0 | 2 | ❌ 完全未迁移 |
| 5 | DepartmentManageAction → SysDeptController + Service | 5 | 3 | 2 | 0 | ⚠️ 部分迁移 |
| 6 | LoginAction → AuthController + Service | 5 | 1 | 3 | 1 | ⚠️ 部分迁移 |
| 7 | OperateLogAction → OperateLogController + Service | 4 | 1 | 2 | 1 | ⚠️ 部分迁移 |
| **合计** | | **31** | **16** | **9** | **6** | |

### 关键缺失项（按优先级排序）

| 优先级 | 缺失项 | 影响 | 建议 |
|--------|--------|------|------|
| 🔴 P0 | CAS单点登录未迁移 | 若生产环境使用CAS则无法登录 | 集成`spring-security-cas`或确认是否仍需CAS |
| 🔴 P0 | DataAnalysis完全为空壳 | 数据分析功能不可用 | 创建`DataAnalysisServiceImpl`实现所有查询 |
| 🟡 P1 | Cluster缓存刷新未实现 | 集群环境下缓存不一致 | 实现`refreshCacheData()`+暴露Controller端点 |
| 🟡 P1 | Cluster通知机制未迁移 | 多节点间无法同步 | 用Redis Pub/Sub替代 |
| 🟡 P1 | OperateLog Excel导出未迁移 | 无法导出日志为Excel | 集成EasyExcel实现文件导出 |
| 🟡 P1 | OperateLog手动触发任务未迁移 | 无法手动执行定时任务 | 添加sync-task端点+Quartz集成 |
| 🟢 P2 | BasicData编码唯一性校验缺失 | 新增时可能编码重复 | 添加check-code接口 |
| 🟢 P2 | Login IP记录缺失 | 审计日志缺少登录IP | 从HttpServletRequest获取IP |
| 🟢 P2 | Department筛选/分页缺失 | 大量部门时查询性能问题 | 视数据量决定是否需要分页 |
