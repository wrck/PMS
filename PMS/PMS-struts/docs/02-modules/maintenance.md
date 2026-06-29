# 维护管理功能说明文档

## 1. 模块概述

维护管理模块负责项目交付后的维护记录管理，包括维护记录创建/编辑、维护问卷填写、交付件上传、服务交付统计等功能。该模块与项目管理模块紧密关联，支持售后项目(projectType=10)、售前项目(projectType=20)、非业务类(projectType=30)、自定义(projectType=40)四种维护记录类型。维护记录不使用独立的工作流审批，而是通过问卷模板实现维护质量评估。

### 涉及的Action类列表

| Action类 | 包路径 | 职责 |
|----------|--------|------|
| `MaintenanceAction` | `com.dp.plat.maintenance.action` | 维护记录管理（列表/创建/编辑/交付件/服务交付统计） |

### 涉及的Service类列表

| Service类 | 依赖DAO | 说明 |
|-----------|---------|------|
| `ProjectService` | `ProjectDao` | 维护模块无独立Service，直接使用ProjectService处理维护记录的增删改查 |
| `PresalesService` | `PresalesDao` | 查询售前项目信息（projectType=20时） |
| `BasicDataService` | `BasicDataDao` | 查询基础数据（维护类型、项目分类等） |
| `DepartmentManageService` | `DepartmentManageDao` | 查询公司和办事处信息 |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `pm_project_maintenance` | 维护记录主表 |
| `pm_project_member` | 项目成员表（关联查询服务经理和项目经理） |
| `pm_basic_deliver_detail` | 交付件明细表（关联查询维护交付件） |
| `pm_project_header` | 售后项目主表（projectType=10时关联） |
| `pm_presales_project_header` | 售前项目主表（projectType=20时关联） |
| `pm_cl_quesnaire_result_header` | 问卷结果头表 |
| `pm_cl_quesnaire_result_line` | 问卷结果行表 |
| `fnd_department` | 部门/办事处信息表 |
| `fnd_basic_data` | 基础数据表 |
| `temp_project_warranty_state` | 项目维保状态临时表（⚠️ 运行时临时表，非持久化） |

### 依赖的其他模块

- 项目管理模块（项目信息、项目成员、项目交付件、项目维保状态）
- 售前管理模块（售前项目信息，projectType=20时）
- 闭环管理模块（问卷模板、问卷评分）
- 系统管理模块（用户信息、部门信息、基础数据）

## 2. 业务流程

### 2.1 维护记录管理流程

<<<<<<< HEAD
```mermaid
graph LR
    A["维护记录列表<br/>MaintenanceAction.execute()"] --> B["创建维护记录"]
    B --> C["填写维护问卷"]
    C --> D["上传交付件"]
    D --> E["保存<br/>MaintenanceAction.createProjectMaintenance()"]
=======
```
[维护记录列表] ──> [创建维护记录] ──> [填写维护问卷] ──> [上传交付件] ──> [保存]
      |                  |                  |               |              |
 MaintenanceAction  MaintenanceAction  MaintenanceAction  MaintenanceAction  MaintenanceAction
 .execute()         .createProjectMaintenance()                .createProjectMaintenance()
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.2 维护记录创建流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["进入创建页面"] --> B["MaintenanceAction.createProjectMaintenance()"]
    B --> C{判断项目类型}
    C -->|"售后 projectType=10"| D["查询项目信息"]
    C -->|"售前 projectType=20"| E["查询售前项目信息"]
    C -->|"非业务类 projectType=30"| F["无关联项目"]
    C -->|"自定义 projectType=40"| G["无关联项目"]
    D --> H["权限校验"]
    E --> H
    F --> H
    G --> H
    H --> I["加载问卷模板<br/>quesType=projectMaintenance"]
    I --> J["填写表单+问卷"]
    J --> K["保存<br/>projectService.insertOrUpdateProjectMaintenance()"]
    K --> L["更新项目实施状态<br/>projectService.updateProjectExecutionState()"]
=======
```
[进入创建页面] ──> MaintenanceAction.createProjectMaintenance()
      |
[判断项目类型]
 /     |      |      \
售后   售前   非业务类  自定义
(10)   (20)   (30)     (40)
 |      |      |        |
查询项目 查询售前 无关联   无关联
信息    项目信息  项目     项目
 |
[权限校验]
 |
[加载问卷模板] ──> quesType="projectMaintenance"
 |
[填写表单+问卷]
 |
[保存] ──> projectService.insertOrUpdateProjectMaintenance()
 |
[更新项目实施状态] ──> projectService.updateProjectExecutionState()
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.3 项目类型说明

维护记录支持四种项目类型（projectType字段）：

| projectType值 | 含义 | 关联项目 | 问卷要求 |
|---------------|------|----------|----------|
| 10 | 售后项目 | 关联pm_project_header | 需要填写问卷 |
| 20 | 售前项目 | 关联pm_presales_project_header | 需要填写问卷 |
| 30 | 非业务类 | 无关联项目 | 不需要填写问卷 |
| 40 | 自定义 | 无关联项目 | 需要填写问卷 |

### 2.4 服务交付统计流程

<<<<<<< HEAD
```mermaid
graph LR
    A["进入服务交付页面"] --> B["MaintenanceAction.serviceDelivery()"]
    B --> C["按季度/月份筛选"]
    C --> D["查询维护记录统计<br/>projectService.selectProjectMaintenanceServiceDeliveryList()"]
=======
```
[进入服务交付页面] ──> MaintenanceAction.serviceDelivery()
      |
[按季度/月份筛选]
 |
[查询维护记录统计] ──> projectService.selectProjectMaintenanceServiceDeliveryList()
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

## 3. 接口文档

### 3.1 维护记录列表

| 项目 | 说明 |
|------|------|
| URL | /module/maintenance_execute.action |
| HTTP方法 | GET |
| 功能描述 | 分页查询维护记录列表，支持多条件筛选 |
| 权限要求 | 项目经理/服务经理/管理员/工程管理部/回访员/区域负责人/项目管理员/项目查看者 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| projectMaintenance.projectName | String | 否 | - | 无 | 项目名称模糊搜索 |
| projectMaintenance.projectCode | String | 否 | - | 无 | 项目编码模糊搜索 |
| projectMaintenance.projectType | Integer | 否 | - | 无 | 项目类型过滤(10/20/30/40) |
| projectMaintenance.officeCode | String | 否 | - | 无 | 办事处过滤 |
| projectMaintenance.category | String | 否 | - | 无 | 任务分类过滤 |
| projectMaintenance.subCategory | String | 否 | - | 无 | 任务小类过滤 |
| projectMaintenance.hasReport | Boolean | 否 | - | 无 | 是否有巡检报告 |
| projectMaintenance.serviceManager | String | 否 | - | 无 | 服务经理过滤 |
| projectMaintenance.programManager | String | 否 | - | 无 | 项目经理过滤 |
| projectMaintenance.warrantyStatus | String | 否 | - | 无 | 维保状态过滤 |
| projectMaintenance.processStartTime | Date | 否 | - | 无 | 处理时间起 |
| projectMaintenance.processEndTime | Date | 否 | - | 无 | 处理时间止 |

**返回结果**：

| result名 | 类型 | 跳转页面 | 说明 |
|----------|------|----------|------|
| SUCCESS | String | /sys/maintenance/maintenance_execute.jsp | 查询成功 |
| ERROR | String | /sys/error.jsp | 权限不足 |

**处理逻辑**：
1. prepareExecute()预加载公司列表、办事处列表、维护类型列表、项目分类列表
2. 根据用户角色设置权限过滤（非管理员/工程管理部/回访员/项目管理员设置areaPower和userPower）
3. 服务经理角色额外设置checkServicePower
4. 查询维护记录列表 → `projectService.selectProjectMaintenanceMapList()`
5. 设置最新维护记录ID（用于编辑时更新项目实施状态）

### 3.2 创建/编辑维护记录

| 项目 | 说明 |
|------|------|
| URL | /module/sub/maintenance_createProjectMaintenance.action |
| HTTP方法 | GET(进入创建页) / POST(提交) |
| 功能描述 | 创建或编辑维护记录，支持问卷填写和交付件上传 |
| 权限要求 | 项目经理/服务经理/管理员/工程管理部/售前专员/项目查看者（按项目类型和区域权限校验） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| projectMaintenance.id | Integer | 否 | - | 无 | 维护记录ID（编辑时传入） |
| projectMaintenance.projectId | Integer | 否 | - | 无 | 关联项目ID |
| projectMaintenance.projectType | Integer | 否 | - | 无 | 项目类型(10/20/30/40) |
| projectMaintenance.category | String | 否 | - | 无 | 任务分类 |
| projectMaintenance.subCategory | String | 否 | - | 无 | 任务小类 |
| projectMaintenance.processTime | Date | 否 | - | 无 | 处理时间 |
| projectMaintenance.processDesc | String | 否 | - | 无 | 事项描述 |
| projectMaintenance.processStep | String | 否 | - | 无 | 解决进展 |
| projectMaintenance.remainProblem | String | 否 | - | 无 | 遗留问题 |
| projectMaintenance.transitHour | Float | 否 | - | 无 | 在途耗时(h) |
| projectMaintenance.processHour | Float | 否 | - | 无 | 处理耗时(h) |
| projectMaintenance.itemModel | String | 否 | - | 无 | 产品型号 |
| projectMaintenance.softVersion | String | 否 | - | 无 | 在网版本 |
| projectMaintenance.enabledFeatures | String | 否 | - | 无 | 启用功能 |
| projectMaintenance.hasReport | Boolean | 否 | - | 无 | 是否有巡检报告 |
| projectMaintenance.remark | String | 否 | - | 无 | 备注 |
| projectMaintenance.customTos | String | 否 | - | 无 | 自定义主送 |
| projectMaintenance.customCcs | String | 否 | - | 无 | 自定义抄送 |
| projectMaintenance.projectExecutionState | String | 否 | - | 无 | 项目实施状态 |
| pmClosedLoopQuesnaire | PmClosedLoopQuesnaire | 否 | - | 无 | 问卷模板 |
| pmClQuesnaireResultHeader | PmClQuesnaireResultHeader | 否 | - | 无 | 问卷结果头 |
| pmClQuesnaireResultLineList | List | 否 | - | 无 | 问卷结果行列表 |
| projectDeliver | ProjectDeliver | 否 | - | 无 | 交付件信息 |
| projectDeliverList | List | 否 | - | 无 | 交付件文件列表 |
| message | String | 否 | - | 无 | "isCopy"表示复制记录 |

**返回结果**：

| result名 | 类型 | 跳转页面 | 说明 |
|----------|------|----------|------|
| SUCCESS | String | /sys/maintenance/sub/createProjectMaintenance.jsp | 进入创建/编辑页面 |
| redirect | String | /sys/sub/redirect.jsp | 保存成功，重定向 |
| ERROR | String | /sys/sub/error.jsp | 权限不足或非法操作 |

**处理逻辑**：
1. 若projectMaintenance.projectId为空（进入创建页面）：
   - 权限校验（根据项目类型校验用户是否有权操作）
   - 加载问卷模板（quesType="projectMaintenance"，非业务类除外）
   - 加载公司列表、办事处列表、维护类型列表、项目实施状态列表
   - 查询项目维保状态和维保级别
2. 若projectMaintenance.projectId不为空（提交表单）：
   - 判断是否本人操作（编辑时校验createBy）
   - 处理问卷提交（status=1时计算分数，保存问卷结果）
   - 设置项目关联信息（项目编码、名称、办事处等）
   - 保存维护记录 → `projectService.insertOrUpdateProjectMaintenance()`
   - 更新项目实施状态（售后项目且为最新记录时）
   - 处理交付件上传

### 3.3 服务交付统计

| 项目 | 说明 |
|------|------|
| URL | /module/maintenance_serviceDelivery.action |
| HTTP方法 | GET |
| 功能描述 | 按季度/月份统计维护服务交付情况 |
| 权限要求 | 项目经理/服务经理/管理员/工程管理部/回访员/区域负责人/项目管理员/项目查看者 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| projectMaintenance.serviceDate | Date | 否 | - | 当前日期 | 服务日期 |
| projectMaintenance.serviceQuarter | Boolean | 否 | - | true | 是否按当前季度查询 |
| projectMaintenance.officeCode | String | 否 | - | 无 | 办事处过滤 |

**返回结果**：

| result名 | 类型 | 跳转页面 | 说明 |
|----------|------|----------|------|
| SUCCESS | String | /sys/maintenance/maintenance_serviceDelivery.jsp | 查询成功 |
| ERROR | String | /sys/error.jsp | 权限不足 |

### 3.4 查看交付件列表

| 项目 | 说明 |
|------|------|
| URL | /module/sub/maintenance_uploadFileList.action |
| HTTP方法 | GET/POST |
| 功能描述 | 查询维护记录关联的交付件列表 |
| 返回格式 | JSON |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| projectMaintenance.id | Integer | 是 | 非空 | 无 | 维护记录ID |
| projectMaintenance.projectType | Integer | 是 | 非空 | 无 | 项目类型 |
| projectMaintenance.deliverFileIds | String | 否 | - | 无 | 交付件文件ID列表 |
| message | String | 否 | - | 无 | "commonUpload"查通用文件, "returnForm"查交付件明细 |

**返回结果**：JSON `{ id, type, deliverFileIds, projectType, fileList/projectDeliverList }`

### 3.5 上传交付件页面

| 项目 | 说明 |
|------|------|
| URL | /module/sub/maintenance_toUploadFile.action |
| HTTP方法 | GET |
| 功能描述 | 进入交付件上传页面，查询已有交付件 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| projectDeliver.eventKey | String | 是 | 非空 | 无 | 事件节点key（格式：dataTypeCode-basicDataId） |
| projectDeliver.column011 | String | 否 | - | 无 | 项目类型列表(逗号分隔) |

**返回结果**：

| result名 | 类型 | 跳转页面 | 说明 |
|----------|------|----------|------|
| SUCCESS | String | /sys/maintenance/sub/toUploadFile.jsp | 查询成功 |
| ERROR | String | /sys/sub/error.jsp | 查询失败 |

## 4. Service层详解

维护模块无独立Service类，所有业务逻辑通过Action直接调用ProjectService实现。

### 4.1 ProjectService.insertOrUpdateProjectMaintenance(ProjectMaintenanceVO)

- **功能描述**：新增或更新维护记录
- **核心逻辑**：
  1. 若id为空则新增，否则更新
  2. 新增时设置createTime和createBy
  3. 更新时设置updateTime和updateBy
- **调用的DAO方法**：`projectDao.insertProjectMaintenance()` / `projectDao.updateProjectMaintenance()`

### 4.2 ProjectService.selectProjectMaintenanceMapList(ProjectMaintenanceVO, DisplayParam)

- **功能描述**：分页查询维护记录列表（Map形式返回）
- **核心逻辑**：
  1. 构建查询条件（支持项目名称/编码/类型/办事处/分类/维保状态等多条件筛选）
  2. 按用户权限过滤（areaPower区域权限，userPower人员权限，checkServicePower服务经理人员权限）
  3. 关联查询项目成员（服务经理memberRole=20，项目经理memberRole=30）
  4. 可选关联查询交付件和维保状态
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceMapList()`, `projectDao.countProjectMaintenanceList()`

### 4.3 ProjectService.selectProjectMaintenanceById(int)

- **功能描述**：根据ID查询维护记录详情
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceById()`

### 4.4 ProjectService.updateProjectExecutionState(Project, String)

- **功能描述**：更新项目实施状态
- **核心逻辑**：仅当维护记录为该项目的最新记录时才更新项目实施状态
- **调用的DAO方法**：`projectDao.updateProjectExecutionState()`

### 4.5 ProjectService.uploadMaintenanceFile(ProjectMaintenanceVO, ProjectDeliver, String, ProjectDeliver)

- **功能描述**：上传维护交付件
- **核心逻辑**：保存上传文件并插入交付件记录
- **调用的DAO方法**：`projectDao.uploadMaintenanceFile()`

### 4.6 ProjectService.selectProjectMaintenanceServiceDeliveryList(ProjectMaintenanceVO, DisplayParam)

- **功能描述**：查询服务交付统计列表
- **核心逻辑**：按季度/月份统计维护服务交付情况
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceServiceDeliveryList()`

### 4.7 ProjectService.queryProjectWarrantyState(int)

- **功能描述**：查询项目维保状态
- **核心逻辑**：查询项目的维保状态、维保级别、增值服务等信息

## 5. 数据操作

### 5.1 本模块涉及的数据库表及CRUD操作

| 表名 | CREATE | READ | UPDATE | DELETE |
|------|--------|------|--------|--------|
| pm_project_maintenance | insertProjectMaintenance / insertProjectMaintenanceSelective | selectProjectMaintenanceById / selectProjectMaintenanceList / selectProjectMaintenanceMapList / countProjectMaintenanceList | updateProjectMaintenance | - |
| pm_basic_deliver_detail | batchInsertDeliverFiles | queryDeliverDetailByProjectIdAndProjectType | updateProjectDeliverById | deleteDeliverById(置为失效) |
| pm_cl_quesnaire_result_header | addPmClQuesResultHeader | - | - | - |
| pm_cl_quesnaire_result_line | addPmClQuesResultLineList | - | - | - |

### 5.2 数据校验规则

| 数据对象 | 校验字段 | 校验规则 | 说明 |
|----------|----------|----------|------|
| MaintenanceAction | 权限校验 | 角色+区域权限+项目相关人员 | 创建维护记录时校验用户是否有权操作该项目 |
| MaintenanceAction | createBy | 编辑时必须为本人 | 非本人创建的记录不允许编辑 |
| MaintenanceAction | 问卷提交 | pmClQuesnaireResultHeader.status != 0 | status=0为草稿不提交，status=1为已提交需计算分数 |
| MaintenanceAction | 项目关联 | 非业务类/自定义外必须关联项目 | 售后/售前类型必须有对应项目 |

### 5.3 数据生命周期

| 数据对象 | 创建 | 修改 | 归档 | 删除 |
|----------|------|------|------|------|
| ProjectMaintenance | 手动创建维护记录 | 编辑时更新 | 不物理删除 | 不物理删除 |
| 问卷结果 | 保存问卷时创建 | 每次保存重新创建 | 随维护记录保留 | 不删除 |
| 交付件 | 上传时创建 | 更新时修改 | effectiveTo置为失效 | 不物理删除 |

### 5.4 数据转换规则

| 转换场景 | 源格式 | 目标格式 | 说明 |
|----------|--------|----------|------|
| 项目类型 | 数字编码 | 10=售后, 20=售前, 30=非业务类, 40=自定义 | projectType字段 |
| 交付件projectType | projectType+1 | 11=售后维护交付件, 21=售前维护交付件 | pm_basic_deliver_detail.projectType |
| 问卷类型 | 字符串 | "projectMaintenance" | 问卷模板的quesType |
| 任务分类 | 基础数据编码 | 中文显示 | 通过fnd_basic_data(dataTypeCode=maintenanceCategory)关联查询 |
| 任务小类 | 基础数据编码 | 中文显示 | 通过fnd_basic_data(dataTypeCode=maintenanceSubCategory)关联查询 |
| 维护类型 | 基础数据编码 | 中文显示 | 通过fnd_basic_data(dataTypeCode=maintenanceType)关联查询 |
| 项目实施状态 | 基础数据编码 | 中文显示 | 通过fnd_basic_data(dataTypeCode=projectExecutionState)关联查询 |

## 6. 业务规则

| 规则编号 | 规则描述 | 触发条件 | 执行逻辑 |
|----------|----------|----------|----------|
| MT-001 | 维护记录权限校验 | 创建维护记录时 | 根据项目类型校验用户角色和区域权限；售后项目校验SM/PM/团队成员；售前项目校验SM/PM/售前专员 |
| MT-002 | 维护记录编辑权限 | 编辑维护记录时 | 仅创建人(createBy)本人可编辑 |
| MT-003 | 项目实施状态更新 | 保存售后项目维护记录时 | 仅当该记录为项目的最新维护记录时才更新项目实施状态 |
| MT-004 | 问卷模板加载 | 进入创建页面时 | 非业务类(category=nonBusiness)不加载问卷模板，其他类型加载quesType="projectMaintenance"的问卷 |
| MT-005 | 问卷提交评分 | 提交问卷时(status=1) | 通过QuestionnarieUtil.queryQuesnaireScore()计算问卷分数 |
| MT-006 | 问卷版本管理 | 保存问卷时 | 每次保存问卷重新生成一份数据(status统一设为1) |
| MT-007 | 维护记录复制 | message="isCopy"时 | 复制已有记录但清空ID、交付件和自定义信息 |
| MT-008 | 数据权限过滤 | 查询维护列表时 | 非管理员/工程管理部/回访员/项目管理员设置areaPower和userPower；服务经理额外设置checkServicePower |
| MT-009 | 服务经理人员权限 | 服务经理查询列表时 | 通过serviceManagerPower_表查询服务经理管辖的项目经理创建的记录 |
| MT-010 | 交付件关联 | 查询交付件时 | pm_basic_deliver_detail.projectType = maintenance.projectType + 1 |

## 7. 配置项

| 配置项 | 配置Key | 默认值 | 说明 |
|--------|---------|--------|------|
| 问卷类型 | 硬编码 | projectMaintenance | 维护问卷模板的quesType |
| 项目类型-售后 | 硬编码 | 10 | projectType=10 |
| 项目类型-售前 | 硬编码 | 20 | projectType=20 |
| 项目类型-非业务类 | 硬编码 | 30 | projectType=30，category=nonBusiness |
| 项目类型-自定义 | 硬编码 | 40 | projectType=40 |
| 维护类型基础数据 | dataTypeCode=maintenanceType | - | 维护任务性质的基础数据类型编码 |
| 任务分类基础数据 | dataTypeCode=maintenanceCategory | - | 维护任务分类的基础数据类型编码 |
| 任务小类基础数据 | dataTypeCode=maintenanceSubCategory | - | 维护任务小类的基础数据类型编码 |
| 项目实施状态基础数据 | dataTypeCode=projectExecutionState | - | 项目实施状态的基础数据类型编码 |
| 成员角色-服务经理 | 硬编码 | 20 | pm_project_member.memberRole=20 |
| 成员角色-项目经理 | 硬编码 | 30 | pm_project_member.memberRole=30 |
