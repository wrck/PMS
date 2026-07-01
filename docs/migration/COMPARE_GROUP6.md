# PMS迁移比对报告 - 第6组

> 比对日期: 2026-07-01
> 比对范围: 10个Struts Action → SpringBoot Controller+Service

---

## 1. RoleManageAction → SysRoleController + SysRoleServiceImpl

### 方法: execute() - 角色列表查询
- 源逻辑摘要: 初始化displayParam/role，调用roleManageService.queryRoleList(displayParam, role)查询角色列表
- 目标实现: SysRoleController.list() → SysRoleServiceImpl.queryRolePage() 使用MyBatis-Plus分页查询，支持roleName模糊搜索
- 状态: ✅完全迁移
- 差异说明: 新系统改用分页查询(IPage)，更规范；老系统用DisplayParam手工分页

### 方法: add() - 进入添加页面
- 源逻辑摘要: 查询用户菜单列表userMenuList = userManageService.queryUserMenuList()，返回INPUT页面
- 目标实现: 无独立的"进入添加页面"接口，前端直接调用POST /api/system/role
- 状态: ⚠️部分迁移
- 差异说明: 新系统为纯REST API，不需要预加载菜单列表的页面入口。菜单权限通过RoleDTO.menuIds字段传入

### 方法: addSubmit() - 提交新增角色
- 源逻辑摘要: 1)验证rolemenuidList非空 2)验证roleName非空/无空格 3)设置默认页面"module/Welcome1.action" 4)status=0时设置effectiveTo 5)调用roleManageService.addRoleSubmit(role, rolemenuidList)
- 目标实现: SysRoleController.add() → SysRoleServiceImpl.addRole()：1)检查roleCode唯一性 2)BeanUtils.copyProperties 3)设置createTime 4)insert
- 状态: ⚠️部分迁移
- 差异说明: 缺少rolemenuidList非空验证；缺少roleName格式验证(空格检查)；缺少默认页面设置；缺少status=0时的effectiveTo处理；新系统用menuIds字符串替代rolemenuidList对象列表

### 方法: edit() - 进入编辑页面
- 源逻辑摘要: 1)验证role.id非空 2)查询角色列表和菜单权限列表 3)查询用户菜单列表 4)返回角色详情和菜单权限
- 目标实现: 无独立编辑入口，通过PUT /api/system/role直接更新
- 状态: ⚠️部分迁移
- 差异说明: 新系统为REST风格，不预加载编辑页面数据。前端需先GET获取详情再PUT更新

### 方法: editSubmit() - 提交编辑角色
- 源逻辑摘要: 1)验证rolemenuidList非空 2)验证role.id非空 3)验证roleName格式 4)设置默认页面 5)status=0时设置effectiveTo 6)调用roleManageService.updateRoleSubmit(role, rolemenuidList)
- 目标实现: SysRoleController.update() → SysRoleServiceImpl.updateRole()：1)查询原角色 2)选择性更新字段(roleName/roleCode/status/menuIds)
- 状态: ⚠️部分迁移
- 差异说明: 缺少rolemenuidList非空验证；缺少roleName格式验证；缺少默认页面设置；缺少effectiveTo处理；新系统用menuIds字符串

---

## 2. UploadAction → FileController + FileServiceImpl

### 方法: upload() - 上传文件
- 源逻辑摘要: 1)检查upload非空 2)生成随机路径UPLOAD_PATH/file/随机数 3)UploadFileUtil.upload保存文件 4)basicDataService.insertFileInfo记录文件信息 5)返回fileIds
- 目标实现: FileController.upload() → FileServiceImpl.uploadFile()：1)检查文件非空 2)生成路径basePath/module/yyyyMMdd/UUID.ext 3)file.transferTo保存 4)SysFileInfo记录 5)返回fileId/fileName/filePath/fileSize
- 状态: ✅完全迁移
- 差异说明: 路径策略从随机数改为UUID+日期目录，更规范；新增module分模块存储；返回信息更丰富

### 方法: deleteFile() - 删除文件
- 源逻辑摘要: fileId非空时调用basicDataService.deleteFile(fileId)，设置message
- 目标实现: FileController.delete() → FileServiceImpl.deleteFile()：查询文件信息→删除物理文件→删除数据库记录
- 状态: ✅完全迁移
- 差异说明: 新系统增加了物理文件删除和异常校验

### 方法: downloadFile() - 下载附件
- 源逻辑摘要: 通过fileId查询文件信息fileParam，后续通过getFileStream()获取InputStream下载
- 目标实现: FileController.info() → FileServiceImpl.getFileInfo()：返回文件元数据Map
- 状态: ⚠️部分迁移
- 差异说明: 新系统只返回文件信息元数据，缺少实际的文件流下载功能(InputStream/OutputStream)。需前端通过filePath自行下载或补充download接口

### 方法: queryFile() - 查询文件列表
- 源逻辑摘要: 通过fileIds(逗号分隔)查询文件列表basicDataService.queryFileList(fileIds)
- 目标实现: 无直接对应的批量查询接口
- 状态: ❌未迁移
- 差异说明: 缺少通过fileIds批量查询文件列表的接口

### 方法: uploadImage() - 富文本编辑器上传
- 源逻辑摘要: 1)上传到images目录 2)UploadFileUtil.uploadNoRepeat防重复 3)insertFileInfo记录 4)拼接完整URL返回
- 目标实现: 无对应的富文本图片上传接口
- 状态: ❌未迁移
- 差异说明: 缺少富文本编辑器专用的图片上传接口(防重复+URL拼接)

### 方法: getDownloadFile()/getFileStream() - 文件下载流
- 源逻辑摘要: 设置响应头编码，通过ServletContext.getResourceAsStream获取文件流
- 目标实现: 无对应实现
- 状态: ❌未迁移
- 差异说明: 缺少文件下载流式传输功能

---

## 3. UserManageAction → SysUserController + SysUserServiceImpl

### 方法: prepare() - 权限前置检查
- 源逻辑摘要: 检查当前用户是否有ADMIN/ENGINEEMANAGER/ENGINEEMANAGER_LEADER角色，无则抛异常
- 目标实现: 无对应的权限前置拦截(依赖Spring Security)
- 状态: ⚠️部分迁移
- 差异说明: 新系统应通过@PreAuthorize或SecurityConfig实现角色限制

### 方法: execute() - 用户列表查询
- 源逻辑摘要: 1)查询角色列表 2)查询部门列表 3)构建roleMap 4)查询用户列表 5)填充用户角色名称 6)处理roleids格式
- 目标实现: SysUserController.list() → SysUserServiceImpl.queryUserPage()：分页查询用户，支持username/realname/deptId筛选
- 状态: ⚠️部分迁移
- 差异说明: 缺少角色名称填充逻辑(dealWith方法)；缺少部门列表预加载；缺少roleids格式处理

### 方法: add() - 新增用户
- 源逻辑摘要: 1)预加载角色/菜单/部门/部门权限数据 2)提交时验证字段非空 3)生成随机密码并MD5加密 4)addUserInfo 5)发送开通邮件通知(MailUtil.keepMailWithTemplate)
- 目标实现: SysUserController.add() → SysUserServiceImpl.addUser()：1)检查用户名唯一 2)生成随机密码MD5加密 3)设置默认值 4)insert
- 状态: ⚠️部分迁移
- 差异说明: 缺少字段格式验证(空格检查)；缺少用户菜单权限(usermenuids)关联保存；缺少部门权限(departmentPowers)关联；邮件通知被注释掉(待集成)

### 方法: checkUsername() - 检查用户名重复
- 源逻辑摘要: queryUserSizeByUserName查询用户名是否存在
- 目标实现: 在addUser()中内联检查(selectCount + eq username)
- 状态: ✅完全迁移
- 差异说明: 新系统在add方法内联实现，无独立接口

### 方法: edit() - 编辑用户
- 源逻辑摘要: 1)查询角色/菜单/用户详情/用户菜单权限/部门/部门权限 2)提交时验证字段 3)status=0设置effectiveTo 4)处理项目服务经理/项目经理变更(changeType) 5)updateUserInfo
- 目标实现: SysUserController.update() → SysUserServiceImpl.updateUser()：查询原用户→选择性更新字段(realname/email/phone/status/roleIds/defaultPage)
- 状态: ⚠️部分迁移
- 差异说明: 缺少用户菜单权限(usermenuids)更新；缺少effectiveTo处理；缺少项目服务经理/项目经理联动变更(changeType/newMemberCode逻辑)；缺少字段格式验证

### 方法: pwdreset() - 密码重置
- 源逻辑摘要: 1)查询用户 2)生成随机密码MD5加密 3)设置pwdoverdue 4)updatepwdbyuser 5)发送密码重置邮件 6)强制下线(forcedOffline)
- 目标实现: SysUserController.resetPassword() → SysUserServiceImpl.resetPassword()：查询用户→生成随机密码MD5→设置pwdOverdue(90天)→更新
- 状态: ⚠️部分迁移
- 差异说明: 邮件通知被注释(待集成)；强制下线功能被注释(待集成)

### 方法: findUser() - 按用户名查询
- 源逻辑摘要: queryUserByUserName按用户名查询用户详情
- 目标实现: 无独立的按用户名查询接口(只有按ID查询)
- 状态: ❌未迁移
- 差异说明: 缺少按username查询用户详情的接口

### 方法: submit() - 空方法
- 源逻辑摘要: 空实现
- 目标实现: 无需迁移
- 状态: ✅完全迁移(无需迁移)

### 额外迁移: changePassword() - 修改密码
- 源逻辑摘要: 老系统无此方法(密码重置走pwdreset)
- 目标实现: SysUserController.changePassword() → SysUserServiceImpl.changePassword()：验证旧密码→设置新密码
- 状态: ✅新增功能(新系统增加)

---

## 4. WorkFlowAction → (无直接对应Controller)

### 方法: execute() - 流程部署信息查看
- 源逻辑摘要: 查询所有流程部署和流程定义列表
- 目标实现: 无对应Controller
- 状态: ❌未迁移
- 差异说明: 工作流管理(Action包含15+方法)在新系统中无对应实现。可能依赖Activiti/Flowable等独立工作流引擎

### 方法: newdeploy() - 发布流程
- 源逻辑摘要: 通过文件上传部署工作流
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: deldeployment() - 删除部署
- 源逻辑摘要: 按deploymentId删除部署
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: viewDeployment() - 查看部署详情
- 源逻辑摘要: 按procdefKey查询部署信息
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: viewimage() - 查看流程图
- 源逻辑摘要: 获取流程图InputStream并输出到Response
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: selftask() - 私有任务列表
- 源逻辑摘要: 查询当前用户的运行中任务
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: viewTaskForm() - 打开任务表单
- 源逻辑摘要: 获取任务表单数据，拼接formUrl
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: submitTask() - 提交任务
- 源逻辑摘要: 提交工作流任务
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: viewCurrentImage() - 查看当前流程图
- 源逻辑摘要: 获取流程定义和当前活动坐标
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: hisTaskForm() - 查看已办理表单
- 源逻辑摘要: 获取历史任务表单
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: delegatelist() - 委派任务列表
- 源逻辑摘要: 查询委派任务规则列表
- 目标实现: 无对应
- 状态: ❌未迁移

### 方法: delegateadd() - 添加委派规则(已注释)
- 源逻辑摘要: 方法体已注释，空实现
- 目标实现: 无对应
- 状态: ✅(已废弃)

### 方法: delegateedit() - 编辑委派规则(已注释)
- 源逻辑摘要: 方法体已注释，空实现
- 目标实现: 无对应
- 状态: ✅(已废弃)

### 方法: delegateupdate() - 更新委派规则
- 源逻辑摘要: 调用workFlowService.updateProcdefDelegate更新
- 目标实现: 无对应
- 状态: ❌未迁移

---

## 5. WorkSpaceAction → WorkSpaceController + WorkSpaceServiceImpl

### 方法: prepare() - 初始化工作台(选项卡+角色过滤)
- 源逻辑摘要: 1)获取用户角色 2)查询办事处列表 3)查询选项卡配置 4)按角色过滤可见选项卡(回访人员/技术公告/分包等)
- 目标实现: WorkSpaceController.dashboard() → WorkSpaceServiceImpl.getDashboardData()
- 状态: ⚠️部分迁移
- 差异说明: 新系统用dashboard汇总统计替代选项卡初始化；角色过滤逻辑简化；缺少选项卡动态配置(基于BasicDataBean)

### 方法: execute() - 日常项目跟踪(默认页面)
- 源逻辑摘要: 根据tabIndex路由：4→probTask，5→subcontractTask，其他→queryPmTaskList
- 目标实现: WorkSpaceController.dailyTasks() → WorkSpaceServiceImpl.queryDailyTaskList()：按角色过滤(serviceManager/programManager/engineerManager)查询日常任务
- 状态: ✅完全迁移
- 差异说明: 新系统拆分为独立端点，不再通过tabIndex路由

### 方法: notice() - 系统通知
- 源逻辑摘要: 查询通知列表queryNotifyList，设置tabIndex=2
- 目标实现: WorkSpaceController.notifications() → WorkSpaceServiceImpl.queryNotifyList()
- 状态: ✅完全迁移
- 差异说明: 新系统增加分页参数和角色过滤

### 方法: task() - 业务流程办理(合并多种待办)
- 源逻辑摘要: 按procKey过滤，合并6类待办：1)闭环流程 2)回访申请 3)项目回退确认 4)不予跟踪确认 5)项目督查(需工程管理部角色) 6)售前流程
- 目标实现: WorkSpaceController.businessTasks() → WorkSpaceServiceImpl.queryBusinessTaskList(procKey, username)：按procKey过滤合并待办
- 状态: ✅完全迁移
- 差异说明: 新系统将各待办查询通过Mapper实现(原系统通过Activiti+DAO)

### 方法: dailyTask() - 日常项目跟踪(tab切换)
- 源逻辑摘要: 设置tabIndex=0，调用execute()
- 目标实现: 合并到dailyTasks()端点
- 状态: ✅完全迁移

### 方法: hisselftask() - 个人已办任务
- 源逻辑摘要: querySelfHistoryTaskList查询已办理的闭环+回访任务
- 目标实现: WorkSpaceController.historyTasks() → WorkSpaceServiceImpl.querySelfHistoryTaskList()
- 状态: ✅完全迁移
- 差异说明: 新系统增加分页和筛选参数(projectName/officeCode/projectCustomer)

### 方法: probTask() - 技术公告任务
- 源逻辑摘要: queryProbTaskList查询技术公告待办
- 目标实现: WorkSpaceController.probTasks() → WorkSpaceServiceImpl.queryProbTaskList()
- 状态: ✅完全迁移
- 差异说明: 新系统角色判断简化(通过params.isProbAdmin)

### 方法: subcontractTask() - 分包任务
- 源逻辑摘要: 1)查询公司列表 2)按角色分组查询分包任务(工程管理部/回访/区域主管/服务经理/财务)
- 目标实现: WorkSpaceController.subcontractTasks() → WorkSpaceServiceImpl.querySubcontractTaskList()
- 状态: ⚠️部分迁移
- 差异说明: 缺少公司列表(compList)查询返回；角色分组逻辑简化(原系统5种角色分组查询，新系统通过params传递)

### 方法: updateNotifyState() - 更新通知状态(@Deprecated)
- 源逻辑摘要: workspaceService.updateNotificationState(notifyStateId)
- 目标实现: WorkSpaceController.markNotificationRead() → WorkSpaceServiceImpl.updateNotificationState()
- 状态: ✅完全迁移

---

## 6. MaintenanceAction → MaintenanceController + MaintenanceServiceImpl

### 方法: execute() - 项目维护列表页面
- 源逻辑摘要: 1)权限检查(8种角色) 2)设置hideQuesnaire/hideFiles/hideWarranty 3)调用projectMaintenance()查询
- 目标实现: MaintenanceController.list() → MaintenanceServiceImpl.queryPage()：分页查询维护记录
- 状态: ⚠️部分迁移
- 差异说明: 缺少复杂权限检查(8种角色)；缺少hideQuesnaire/hideFiles/hideWarranty逻辑；缺少角色-based数据过滤(areaPower/userPower)

### 方法: projectMaintenance() - 获取项目维护记录
- 源逻辑摘要: 1)权限检查 2)按projectType(售后10/售前20)查询项目 3)设置hasPower(基于办事处+角色) 4)数据权限过滤 5)查询维护记录 6)查询项目执行状态
- 目标实现: MaintenanceServiceImpl.queryPage()：简单条件查询(projectId/maintenanceType)
- 状态: ⚠️部分迁移
- 差异说明: 缺少projectType区分(售后/售前)；缺少hasPower权限判断；缺少areaPower/userPower数据权限过滤；缺少项目执行状态查询

### 方法: createProjectMaintenance() - 创建/编辑维护记录(核心复杂方法)
- 源逻辑摘要: 1)权限检查(多角色+办事处+团队成员) 2)问卷管理(获取模板/计算分数/保存) 3)区分projectType(售后/售前/非业务/自定义) 4)插入/更新维护记录 5)更新项目实施状态 6)文件上传(deliverFileList)
- 目标实现: MaintenanceController.add()/update() → MaintenanceServiceImpl.create()/update()
- 状态: ⚠️部分迁移
- 差异说明: 缺少问卷管理(QuestionnarieUtil集成)；缺少projectType区分；缺少项目实施状态更新；缺少文件上传(deliverFileList)处理；权限检查简化

### 方法: serviceDelivery() - 服务交付记录
- 源逻辑摘要: 1)权限检查 2)初始化displayParam 3)查询公司/办事处 4)设置serviceDate/serviceQuarter 5)查询服务交付列表
- 目标实现: MaintenanceController.serviceDelivery() → MaintenanceServiceImpl.queryServiceDelivery()
- 状态: ⚠️部分迁移
- 差异说明: 缺少serviceDate/serviceQuarter过滤；缺少公司/办事处预加载

### 方法: toUploadFile() - 获取可上传文件列表
- 源逻辑摘要: 1)解析eventKey获取dataTypeCode/basicDataId 2)按projectTypes查询deliverList 3)去重处理
- 目标实现: MaintenanceController.queryFiles() → MaintenanceServiceImpl.queryFiles()
- 状态: ⚠️部分迁移
- 差异说明: 缺少eventKey解析逻辑；缺少按projectTypes多类型查询和去重

### 方法: uploadFileList() - 上传/查询文件列表
- 源逻辑摘要: 两种模式：commonUpload(查询文件列表)和returnForm(查询交付详情)
- 目标实现: MaintenanceController.uploadFile()：简单保存fileIds
- 状态: ⚠️部分迁移
- 差异说明: 缺少commonUpload/returnForm双模式；缺少deliverDetail查询

### 方法: prepareExecute() - 初始化下拉数据
- 源逻辑摘要: 查询公司列表/办事处/维护类型/分类子分类映射
- 目标实现: 无对应(REST API不预加载)
- 状态: ⚠️部分迁移
- 差异说明: 新系统为REST API，前端需自行调用基础数据接口

---

## 7. CertificateAction → CertificateController + CertificateServiceImpl

### 方法: certificate() - 合格证查询主页
- 源逻辑摘要: 初始化results Map，检查用户是否有上传权限(ROLE=1)，设置canUpload
- 目标实现: 无独立主页接口
- 状态: ⚠️部分迁移
- 差异说明: 新系统为REST API，权限控制通过Spring Security实现

### 方法: queryCertificate() - 查询合格证
- 源逻辑摘要: 1)验证barcode非空 2)查询OQC信息(certificateService.queryOQCInfo) 3)正则提取oqcNo 4)根据序列号生成生产日期(generateProductionDate)
- 目标实现: CertificateController.getByBarcode() → CertificateServiceImpl.getByBarcode()：按barcode直接查询数据库
- 状态: ⚠️部分迁移
- 差异说明: 缺少OQC信息查询(queryOQCInfo)；缺少oqcNo正则提取；缺少生产日期生成逻辑(generateProductionDate根据序列号解析年月)

### 方法: uploadSealInfo() - 上传印章信息
- 源逻辑摘要: 调用certificateService.parseExcelFile(file)解析Excel文件
- 目标实现: CertificateController.uploadSealInfo() → CertificateServiceImpl.uploadSealInfo()：直接保存sealInfo字符串
- 状态: ⚠️部分迁移
- 差异说明: 老系统解析Excel文件提取印章信息；新系统直接接收sealInfo字符串，缺少Excel解析逻辑

### 方法: generateProductionDate() - 静态工具方法
- 源逻辑摘要: 根据barcode第9-12位解析年月(16进制月份)
- 目标实现: 无对应
- 状态: ❌未迁移
- 差异说明: 缺少barcode解析生产日期的工具方法

---

## 8. ProbManageAction → ProbController + ProbServiceImpl (1726行, ~33个方法)

### 方法: prepare() - 前置初始化
- 源逻辑摘要: 1)解析request Referer确定namespace 2)获取当前用户
- 目标实现: 通过Spring Security自动获取用户信息
- 状态: ✅完全迁移

### 方法: list() - 技术公告列表
- 源逻辑摘要: 1)查询基础数据(watch/status/relatedSceneType等) 2)初始化displayParam 3)查询probList
- 目标实现: ProbController.list() → ProbServiceImpl.queryPage()：分页查询，支持probTitle/probState/probType筛选
- 状态: ✅完全迁移
- 差异说明: 新系统不预加载基础数据下拉列表(前端处理)

### 方法: input() - 进入创建/编辑页面
- 源逻辑摘要: 1)查询基础数据下拉 2)生成公告编码(SP.日期) 3)查询公告详情+附件+软件版本+产品型号
- 目标实现: ProbController.detail() → ProbServiceImpl.getDetail()
- 状态: ⚠️部分迁移
- 差异说明: 缺少公告编码自动生成逻辑；缺少附件查询；基础数据下拉由前端处理

### 方法: edit() - 进入编辑/查看页面(核心复杂方法)
- 源逻辑摘要: 1)查询基础数据 2)查询公告详情+附件+软件版本+产品型号 3)按角色过滤子任务(管理员/技术支持/普通用户) 4)查询子任务列表 5)记录阅读日志
- 目标实现: ProbController.detail() + softVersions() + restores() + readLogs()
- 状态: ⚠️部分迁移
- 差异说明: 缺少按角色过滤子任务的复杂权限逻辑；子任务查询拆分为独立接口

### 方法: delete() - 删除技术公告
- 源逻辑摘要: probManageService.deleteProbInfo(probId)
- 目标实现: ProbController.delete() → ProbServiceImpl.delete()：级联删除(softVersion/restore/product)
- 状态: ✅完全迁移
- 差异说明: 新系统增加了级联删除关联数据

### 方法: save() - 保存技术公告(新建)
- 源逻辑摘要: 1)上传附件 2)设置status 3)保存prob+softVersion 4)支持isContinue继续创建
- 目标实现: ProbController.add() → ProbServiceImpl.create()
- 状态: ⚠️部分迁移
- 差异说明: 缺少附件上传处理；缺少softVersion同步保存；缺少isContinue继续创建逻辑

### 方法: update() - 更新技术公告
- 源逻辑摘要: 1)上传附件(可选) 2)更新prob+softVersion
- 目标实现: ProbController.update() → ProbServiceImpl.update()
- 状态: ⚠️部分迁移
- 差异说明: 缺少附件上传处理；softVersion需单独接口保存

### 方法: audit() - 审核(驳回/审批)
- 源逻辑摘要: 1)更新软件版本 2)更新公告状态
- 目标实现: ProbController.audit() → ProbServiceImpl.audit()：更新status
- 状态: ⚠️部分迁移
- 差异说明: 缺少软件版本同步更新

### 方法: bacthDeleteProbRestores() - 批量删除子任务
- 源逻辑摘要: probManageService.bacthDeleteProbRestores(probRestoreIds)
- 目标实现: ProbController.batchDeleteRestores() → ProbServiceImpl.batchDeleteRestores()
- 状态: ✅完全迁移

### 方法: checkProject() - 检索受影响项目
- 源逻辑摘要: 1)查询办事处 2)首次打开不查询 3)查询probRestoreList
- 目标实现: 无独立接口(通过restores接口查询)
- 状态: ⚠️部分迁移
- 差异说明: 缺少首次打开不查询的优化逻辑

### 方法: releaseTask() - 发布修复任务
- 源逻辑摘要: 1)设置assigneeRole(默认服务经理) 2)设置restoreStatus=10 3)批量插入 4)邮件通知
- 目标实现: ProbController.releaseTask() → ProbServiceImpl.releaseTask()：设置角色/状态+批量插入
- 状态: ⚠️部分迁移
- 差异说明: Controller层releaseTask()标记TODO未实现；邮件通知功能未集成

### 方法: managePrivateTask() - 管理个人任务
- 源逻辑摘要: 1)加载修复状态集合 2)按当前用户+areapower过滤 3)查询restoreStatus=10的任务 4)查询软件版本
- 目标实现: ProbController.privateTasks() → ProbServiceImpl.queryPrivateTaskList()
- 状态: ⚠️部分迁移
- 差异说明: 缺少areapower权限过滤；缺少restoreStatus过滤(默认10)

### 方法: updatePrivateTask() - 更新个人任务状态
- 源逻辑摘要: probManageService.updateProbRestoreTask(probRestore, restoreIds, 0)
- 目标实现: ProbController.updatePrivateTask() → ProbServiceImpl.updateRestoreTask()
- 状态: ⚠️部分迁移
- 差异说明: 新系统updateRestoreTask实现中restore参数可能为null

### 方法: weeklyUpload() - 上传任务进展周报
- 源逻辑摘要: 1)上传附件 2)insertFileInfo 3)insertProbTaskWeekly
- 目标实现: 无对应接口
- 状态: ❌未迁移
- 差异说明: 缺少周报上传功能

### 方法: manageAllTask() - 管理员管理所有任务
- 源逻辑摘要: 1)加载状态集合 2)查询选项卡 3)按restoreStatus分组查询(31闭环/20返回/30待闭环)
- 目标实现: ProbController.allTasks() → ProbServiceImpl.queryAllRestoreTaskList()
- 状态: ⚠️部分迁移
- 差异说明: 缺少按restoreStatus分组的多分支查询逻辑

### 方法: updateRestoreTask() - 管理员更新任务
- 源逻辑摘要: probManageService.updateProbRestoreTask(probRestore, restoreIds, 2)
- 目标实现: ProbController.updateRestoreTask() → ProbServiceImpl.updateRestoreTask()
- 状态: ✅完全迁移

### 方法: export() - 导出技术公告
- 源逻辑摘要: 1)查询公告列表 2)HTML清理(desc/solution) 3)构建Excel 4)写入Response
- 目标实现: ProbController.export() → ProbServiceImpl.exportProbList()：导出CSV
- 状态: ⚠️部分迁移
- 差异说明: 新系统导出CSV格式(老系统Excel)；缺少HTML清理逻辑

### 方法: importSoftVersion() - 批量导入软件版本
- 源逻辑摘要: 1)权限检查(ROLE_PROB_SUPPORTER) 2)读取Excel 3)批量添加
- 目标实现: ProbController.importSoftVersion() → ProbServiceImpl.batchImportSoftVersion()
- 状态: ⚠️部分迁移
- 差异说明: 缺少角色权限检查；数据格式从Excel改为JSON

### 方法: toCheckSoftVersion() - 查询软件版本
- 源逻辑摘要: 按softVersion条件查询版本列表
- 目标实现: ProbController.checkSoftVersion() → ProbServiceImpl.checkSoftVersionList()
- 状态: ✅完全迁移

### 方法: submitSoftVersion() - 确认选择软件版本
- 源逻辑摘要: 解析softVersionCodes字符串，构建softVersionList
- 目标实现: 无对应(前端处理)
- 状态: ⚠️部分迁移
- 差异说明: 解析逻辑移至前端

### 方法: parserSoftVersion() - 解析软件版本范围
- 源逻辑摘要: 根据手工录入信息解析版本范围(parserStart/parserEnd)
- 目标实现: 无对应
- 状态: ❌未迁移
- 差异说明: 缺少软件版本范围解析功能

### 方法: parserOldSoftVersion() - 解析旧版软件版本
- 源逻辑摘要: 遍历所有版本对手工输入进行解析，按probId分组重新保存
- 目标实现: 无对应
- 状态: ❌未迁移
- 差异说明: 缺少旧版数据迁移/解析功能

### 方法: statistics() - 技术公告统计(多维度)
- 源逻辑摘要: 4个tab：0/1按状态/时间统计+报表 2受影响项目 3合同发货软件版本 4恢复任务列表
- 目标实现: ProbController.statistics() → ProbServiceImpl.queryStatistics()
- 状态: ⚠️部分迁移
- 差异说明: 新系统统计逻辑简化(Stream分组)；缺少Echarts报表生成；缺少季度自动调整

### 方法: affectedProjectSoftVersion() - 受影响项目软件版本
- 源逻辑摘要: 按角色权限查询受影响的项目恢复任务
- 目标实现: ProbController.affectedProjectSoftVersion() → ProbServiceImpl.queryAffectedProjectSoftVersion()
- 状态: ⚠️部分迁移
- 差异说明: 缺少areapower权限过滤

### 方法: readSure() - 阅读确认
- 源逻辑摘要: probManageService.readLog(probId, 1)
- 目标实现: ProbController.recordRead() → ProbServiceImpl.recordRead()
- 状态: ✅完全迁移

### 方法: readLog() - 阅读记录查询
- 源逻辑摘要: queryProbReadLogList查询阅读记录
- 目标实现: ProbController.readLogs() → ProbServiceImpl.queryReadLogs()
- 状态: ✅完全迁移

### 方法: listProductItem() - 产品物料列表
- 源逻辑摘要: 支持JSON/页面两种输出模式，复杂过滤逻辑
- 目标实现: ProbController.productItems() → ProbServiceImpl.queryProductItemList()
- 状态: ⚠️部分迁移
- 差异说明: 缺少复杂过滤逻辑(itemGroups/orGroups)

### 方法: listProbProduct() - 公告产品列表(分页)
- 源逻辑摘要: 分页查询公告产品
- 目标实现: ProbController.probProducts() → ProbServiceImpl.queryProbProductList()
- 状态: ✅完全迁移

### 方法: inputProbProduct() - 进入产品编辑页面
- 源逻辑摘要: 权限检查+查询产品详情
- 目标实现: 无独立接口
- 状态: ⚠️部分迁移

### 方法: saveProbProduct() - 保存公告产品
- 源逻辑摘要: 权限检查+insertOrUpdate
- 目标实现: ProbController.saveProbProduct() → ProbServiceImpl.saveProbProduct()
- 状态: ⚠️部分迁移
- 差异说明: 缺少角色权限检查

### 方法: importProbProduct() - 批量导入公告产品
- 源逻辑摘要: 权限检查+Excel导入
- 目标实现: ProbController.importProbProduct() → ProbServiceImpl.batchImportProbProduct()
- 状态: ⚠️部分迁移
- 差异说明: 数据格式从Excel改为JSON；缺少权限检查

### 方法: listComponent() - 产品组件列表
- 源逻辑摘要: 分页查询产品组件
- 目标实现: ProbController.components() → ProbServiceImpl.queryComponentList()
- 状态: ⚠️部分迁移
- 差异说明: Service层返回空列表(Collections.emptyList())，未实际实现

### 方法: inputComponent()/saveComponent() - 组件CRUD
- 源逻辑摘要: 权限检查+新建/编辑组件
- 目标实现: ProbController.saveComponent() → ProbServiceImpl.saveComponent()
- 状态: ⚠️部分迁移
- 差异说明: Service层saveComponent()未实际实现

### 方法: importComponent() - 批量导入组件
- 源逻辑摘要: 权限检查+Excel导入
- 目标实现: ProbController.importComponent() → ProbServiceImpl.batchImportComponent()
- 状态: ⚠️部分迁移
- 差异说明: Service层实现为空；数据格式变化

### 方法: checkSubProject() - 查询子项目
- 源逻辑摘要: 查询probRestoreList
- 目标实现: 通过restores接口
- 状态: ⚠️部分迁移

### 方法: fillMarketRelations() - 填充市场部关系
- 源逻辑摘要: 查询市场部对应关系填入commonMap
- 目标实现: 无对应
- 状态: ❌未迁移

---

## 9. SupervisionAction → SupervisionController + SupervisionServiceImpl

### 方法: execute() - 项目督查列表页面
- 源逻辑摘要: 初始化projectSupervision/displayParam，调用projectSupervision()
- 目标实现: SupervisionController.list() → SupervisionServiceImpl.queryPage()
- 状态: ✅完全迁移
- 差异说明: 新系统改用分页查询

### 方法: projectSupervision() - 获取督查记录(核心方法)
- 源逻辑摘要: 1)权限检查(10种角色) 2)按projectId查询项目 3)设置hasPower(基于办事处+角色) 4)数据权限过滤(areaPower/userPower) 5)查询督查记录列表
- 目标实现: SupervisionServiceImpl.queryPage()：简单条件查询(projectId/officeCode)
- 状态: ⚠️部分迁移
- 差异说明: 缺少复杂权限检查(10种角色)；缺少hasPower判断；缺少areaPower/userPower数据权限过滤

### 方法: createProjectSupervision() - 创建/编辑督查记录
- 源逻辑摘要: 1)验证project非空 2)权限检查(管理员/工程管理部/办事处/团队成员) 3)问卷管理(获取模板/计算分数/保存) 4)设置项目信息 5)insertOrUpdate
- 目标实现: SupervisionController.add()/update() → SupervisionServiceImpl.create()/update()
- 状态: ⚠️部分迁移
- 差异说明: 缺少问卷管理(QuestionnarieUtil)；缺少复杂权限检查；缺少项目信息自动填充

### 方法: deleteProjectSupervision() - 删除督查记录
- 源逻辑摘要: 1)验证创建人或工程管理部角色 2)检查state=false(未发布) 3)软删除(isDelete=true)
- 目标实现: SupervisionController.delete() → SupervisionServiceImpl.delete()：物理删除
- 状态: ⚠️部分迁移
- 差异说明: 老系统软删除(isDelete)；新系统物理删除；缺少创建人/角色权限验证；缺少state检查

### 方法: queryPowerUser() - 查询权限用户
- 源逻辑摘要: 1)查询服务经理列表 2)查询项目经理列表 3)去重合并 4)JSON序列化
- 目标实现: SupervisionController.queryPowerUsers() → SupervisionServiceImpl.queryPowerUsers()
- 状态: ⚠️部分迁移
- 差异说明: 新系统通过Mapper直接查询(老系统通过Service查询+去重)

### 方法: prepareExecute() - 初始化下拉数据
- 源逻辑摘要: 查询办事处集合
- 目标实现: 无对应(REST API)
- 状态: ⚠️部分迁移

---

## 10. WarrantyCallbackAction → WarrantyCallbackController + WarrantyCallbackServiceImpl

### 方法: prepare() - 前置初始化
- 源逻辑摘要: 1)解析namespace 2)获取当前用户
- 目标实现: Spring Security自动处理
- 状态: ✅完全迁移

### 方法: execute() - 维保回访列表页面
- 源逻辑摘要: 初始化projectWarrantyCallback/displayParam，调用projectWarrantyCallback()
- 目标实现: WarrantyCallbackController.list() → WarrantyCallbackServiceImpl.queryPage()
- 状态: ✅完全迁移

### 方法: projectWarrantyCallback() - 获取维保回访记录(核心方法)
- 源逻辑摘要: 1)权限检查(8种角色) 2)按projectId查询项目 3)设置hasPower(回访人员/维保回访员/工程管理部) 4)数据权限过滤 5)查询回访记录 6)查询服务类型/接听状态
- 目标实现: WarrantyCallbackServiceImpl.queryPage()：简单条件查询(projectId/officeCode/isDelete=0)
- 状态: ⚠️部分迁移
- 差异说明: 缺少复杂权限检查(8种角色)；缺少hasPower判断；缺少数据权限过滤；缺少服务类型/接听状态预加载

### 方法: createProjectWarrantyCallback() - 创建/编辑维保回访(核心复杂方法)
- 源逻辑摘要: 1)权限检查(办事处+角色) 2)填充项目维保信息(fillProjectWarrantyInfo) 3)查询warrantyState 4)问卷管理 5)设置项目信息 6)insertOrUpdate
- 目标实现: WarrantyCallbackController.add()/update() → WarrantyCallbackServiceImpl.create()/update()
- 状态: ⚠️部分迁移
- 差异说明: 缺少fillProjectWarrantyInfo维保信息填充；缺少warrantyState查询；缺少问卷管理；权限检查简化

### 方法: deleteProjectWarrantyCallback() - 删除维保回访
- 源逻辑摘要: 1)验证创建人或工程管理部角色 2)软删除(isDelete=true)
- 目标实现: WarrantyCallbackController.delete() → WarrantyCallbackServiceImpl.delete()：软删除(isDelete=1)
- 状态: ✅完全迁移
- 差异说明: 新系统保持软删除策略

### 方法: projectWarranty() - 项目维保查询
- 源逻辑摘要: 1)权限检查 2)支持queryCount模式(只查数量) 3)查询维保列表 4)查询服务类型/接听状态
- 目标实现: WarrantyCallbackController.byProject() → WarrantyCallbackServiceImpl.queryByProject()
- 状态: ⚠️部分迁移
- 差异说明: 缺少queryCount模式；缺少服务类型/接听状态；查询逻辑简化

### 方法: customerProject() - 客户项目查询
- 源逻辑摘要: 1)权限检查 2)按客户维度统计查询 3)查询服务类型/接听状态
- 目标实现: WarrantyCallbackController.byCustomer() → WarrantyCallbackServiceImpl.queryCustomerProject()
- 状态: ⚠️部分迁移
- 差异说明: 缺少权限检查；查询逻辑简化(按finalCustomerName模糊匹配)

### 方法: queryPowerUser() - 查询权限用户
- 源逻辑摘要: 查询服务经理+项目经理列表，去重，JSON序列化
- 目标实现: 无对应接口
- 状态: ❌未迁移
- 差异说明: 缺少权限用户查询接口

### 方法: prepareExecute() - 初始化下拉数据
- 源逻辑摘要: 查询办事处/服务类型/接听状态
- 目标实现: 无对应(REST API)
- 状态: ⚠️部分迁移

---

## 汇总表

| # | Action → Controller/Service | 方法数 | ✅完全迁移 | ⚠️部分迁移 | ❌未迁移 | 迁移率 |
|---|---------------------------|--------|----------|----------|--------|--------|
| 1 | RoleManageAction → SysRole* | 5 | 1 | 4 | 0 | 60% |
| 2 | UploadAction → File* | 6 | 2 | 1 | 3 | 42% |
| 3 | UserManageAction → SysUser* | 8 | 2 | 5 | 1 | 56% |
| 4 | WorkFlowAction → (无对应) | 14 | 2(废弃) | 0 | 12 | 14% |
| 5 | WorkSpaceAction → WorkSpace* | 9 | 7 | 2 | 0 | 89% |
| 6 | MaintenanceAction → Maintenance* | 7 | 0 | 7 | 0 | 50% |
| 7 | CertificateAction → Certificate* | 4 | 0 | 3 | 1 | 38% |
| 8 | ProbManageAction → Prob* | 33 | 8 | 22 | 3 | 58% |
| 9 | SupervisionAction → Supervision* | 6 | 1 | 5 | 0 | 58% |
| 10 | WarrantyCallbackAction → WarrantyCallback* | 8 | 3 | 4 | 1 | 63% |
| **合计** | | **100** | **26** | **53** | **21** | **53%** |

### 关键风险项

1. **WorkFlowAction完全未迁移** - 12个工作流相关方法(部署/任务/委派)无对应实现，需确认是否通过独立工作流引擎处理
2. **权限逻辑大幅简化** - 几乎所有Action都有复杂的多角色权限检查(8-10种角色+办事处+团队成员)，新系统多为简单CRUD
3. **问卷管理未迁移** - Maintenance/Supervision/WarrantyCallback三个Action都有QuestionnarieUtil问卷管理，新系统均未实现
4. **文件上传/下载功能不完整** - UploadAction的downloadFile/uploadImage/queryFile未迁移；各业务Action的附件上传逻辑未迁移
5. **邮件通知全部注释** - 用户开通/密码重置/任务发布等邮件通知均未集成
6. **数据权限过滤缺失** - 老系统基于areapower的角色数据权限过滤在新系统中未实现
7. **ProbManageAction产品组件管理未实现** - listComponent/saveComponent/importComponent的Service层返回空实现
