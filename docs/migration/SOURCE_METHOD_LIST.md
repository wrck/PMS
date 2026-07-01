# PMS-STRUTS 原系统完整业务方法清单

> 扫描时间：2026-06-30
> 扫描范围：PMS-struts/src 下所有 *Action.java
> 过滤规则：排除 getter/setter/prepare 方法，仅保留业务方法

---

## Action层 (24个类, ~250个业务方法)

### BaseAction (126行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L43 | start() | 首页跳转 |

### BasicDataManageAction (158行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L29 | execute() | 基础数据列表查询 |
| L39 | basicdataUpdate() | 更新基础数据 |
| L51 | basicdataInsert() | 新增基础数据 |
| L64 | findBasicDataId() | 查找基础数据ID |
| L72 | executeSql() | 执行SQL(管理员) |

### CallBackAction (581行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L84 | input() | 回访列表 |
| L97 | apply() | 发起回访申请 |
| L111 | read() | 查看回访详情 |
| L130 | seeQuesnaire() | 查看回访问卷 |
| L191 | resubmit() | 重新提交回访 |
| L216 | aduit() | 审核回访 |

### ClusterAction (43行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L23 | refreshCacheData() | 刷新缓存 |

### DataAnalysisAction (170行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L37 | execute() | 数据分析查询 |

### DepartmentManageAction (93行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L18 | execute() | 部门列表 |
| L38 | add() | 新增部门页面 |
| L43 | addSubmit() | 提交新增部门 |
| L51 | edit() | 编辑部门 |

### LoginAction (168行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L27 | start() | 登录页 |
| L37 | execute() | 执行登录 |
| L119 | logout() | 登出 |

### OperateLogAction (271行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L48 | execute() | 操作日志列表 |
| L63 | exportlog() | 导出操作日志 |

### PmClosedLoopAction (1170行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L68 | execute() | 闭环列表 |
| L439 | addPmCLApply() | PM发起闭环申请 |
| L488 | addSmCLApply() | SM发起闭环申请 |
| L533 | addCbCLApply() | CB发起闭环申请 |
| L645 | cantCB() | 无法闭环 |
| L686 | addClCLApply() | CL发起闭环申请 |
| L785 | pmSeeCbCl() | PM查看闭环 |

### PmClosedLoopQuesnaireAction (491行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L37 | execute() | 问卷列表 |
| L46 | addPCLQuesnaire() | 新建问卷 |
| L53 | pmCLQuesEdit() | 编辑问卷 |
| L81 | submitQues() | 提交问卷 |
| L100 | addLine() | 添加问卷行 |
| L138 | submitLine() | 提交问卷行 |
| L185 | updateQues() | 更新问卷 |
| L205 | deleteHeader() | 删除问卷头 |
| L212 | startEffective() | 生效问卷 |
| L227 | pmCLQuesSee() | 查看问卷 |
| L256 | deleteLine() | 删除问卷行 |
| L278 | editLine() | 编辑问卷行 |
| L307 | endEffective() | 失效问卷 |

### PresalesAction (1195行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L160 | list() | 售前项目列表 |
| L209 | input() | 新建/编辑售前 |
| L240 | apply() | 发起售前申请 |
| L262 | read() | 查看售前详情 |
| L281 | aduit() | 审核售前 |
| L302 | smaduit() | 服务经理审核 |
| L330 | pmaduit() | 项目经理审核 |
| L388 | updateTask() | 更新售前任务 |
| L406 | emaduit() | 工程管理部审核 |
| L444 | callback() | 售前回调/问卷 |
| L470 | shipmentInfo() | 查询发货信息 |
| L500 | tempAuthInfo() | 查询临时授权信息 |
| L523 | syncOaData() | 同步OA数据 |
| L577 | upload() | 上传交付件 |
| L612 | deleteDeliverById() | 删除交付件 |
| L621 | updateDeliverById() | 更新交付件 |
| L832 | updateConfirmFiles() | 更新确认文件 |
| L845 | exportPresales() | 导出售前项目 |

### ProjectAction (3371行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L347 | execute() | 项目列表(权限过滤) |
| L373 | insertProject() | 创建项目 |
| L434 | createCHProject() | 创建串货项目 |
| L470 | transferShipment() | 转移设备 |
| L519 | transferProject() | 查询可转移项目 |
| L532 | exportSpotCheck() | 现场验货单下载 |
| L549 | exportOverWarrantyRemind() | 超期保修提醒导出 |
| L567 | importSpotCheckIgnoreItem() | 导入现场验货单 |
| L639 | updateProject() | 更新项目(含状态流转) |
| L861 | checkOrderData() | 查询设备清单 |
| L885 | checkRealOrderData() | 查询实施发货设备 |
| L899 | projectLeaseLine() | 查询租赁配置清单 |
| L914 | projectProductConfigLevelInfo() | 查询配置关系清单 |
| L928 | checkShipmentInfo() | 查询发货序列号 |
| L951 | deleteShipmentInfo() | 删除发货安装信息 |
| L974 | checkSoftVersion() | 查询设备软件版本 |
| L1011 | updateSoftVersion() | 更新设备软件版本 |
| L1035 | checkhistsoftversion() | 获取软件版本历史 |
| L1051 | queryProjectNotification() | 获取项目通知 |
| L1066 | problemTicket() | 问题工单 |
| L1106 | licenseInfo() | License信息 |
| L1136 | projectMaintenance() | 获取项目维护记录 |
| L1158 | createProjectMaintenance() | 创建维护记录 |
| L1376 | editProjectPlan() | 制定/修改工程计划 |
| L1421 | uploadDeliverableFile() | 上传工程交付件 |
| L1449 | deleteDeliverById() | 删除工程交付件 |
| L1463 | backToLastStep() | 项目回退上一步 |
| L1529 | createWeekly() | 创建周报 |
| L1579 | saveWeekly() | 保存周报草稿 |
| L1602 | submitWeekly() | 提交周报 |
| L1667 | updateWeekly() | 更新周报 |
| L1684 | toUploadFile() | 进入文件上传页面 |
| L1688 | toUploadDeliverableFile() | 进入交付件上传页面 |
| L1760 | downloadFile() | 下载文件 |
| L1825 | deleteFile() | 删除文件 |
| L1840 | feedback() | 周报回复 |
| L1854 | instruction() | 项目批示 |
| L1871 | queryalluser() | 根据角色查询用户 |
| L1883 | queryperson() | 查询项目干系人 |
| L1918 | updateprojectisback() | 项目回退流程 |
| L2042 | createMember() | 创建项目成员 |
| L2072 | updateMember() | 更新项目成员 |
| L2093 | saveInstallAdress() | 保存安装地址 |
| L2115 | updateProjectExecutionState() | 更新项目实施状态 |
| L2137 | toMergeOrBranch() | 合同拆分合并页面 |
| L2148 | checkMergeContract() | 查询要合并的合同 |
| L2163 | mergeContract() | 合并操作 |
| L2177 | branchContract() | 项目拆分 |
| L2185 | queryDpNoRoleUser() | 查询部门无角色用户 |
| L2195 | batchChangeMember() | 批量变更项目成员 |
| L2967 | importProject() | 批量创建项目(Excel) |
| L2995 | clearProject() | 批量删除/无效化项目 |

### ReportAction (1073行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L113 | show() | 报表首页 |
| L160 | loadLineData() | 加载折线数据 |
| L291 | assignedRate() | 指派率统计 |
| L322 | traceRate() | 跟踪率统计 |
| L349 | closeRate() | 闭环率统计 |
| L392 | implRate() | 实施率统计 |
| L464 | quality() | 质量统计 |
| L537 | projectSummaryStatus() | 项目汇总状态 |
| L1064 | input() | 报表输入 |

### RoleManageAction (191行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L30 | execute() | 角色列表 |
| L45 | add() | 新增角色页面 |
| L50 | addSubmit() | 提交新增角色 |
| L74 | edit() | 编辑角色 |
| L99 | editSubmit() | 提交编辑角色 |

### UploadAction (225行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L49 | upload() | 上传文件 |
| L69 | deleteFile() | 删除文件 |
| L86 | downloadFile() | 下载文件 |
| L97 | queryFile() | 查询文件 |
| L141 | uploadImage() | 上传图片 |

### UserManageAction (357行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L58 | execute() | 用户列表 |
| L108 | add() | 新增用户 |
| L149 | checkUsername() | 检查用户名 |
| L154 | edit() | 编辑用户 |
| L192 | submit() | 提交用户 |
| L238 | findUser() | 查找用户 |
| L243 | checkSubmitData() | 检查提交数据 |

### WorkFlowAction (464行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L64 | execute() | 流程列表 |
| L77 | newdeploy() | 新部署 |
| L91 | deldeployment() | 删除部署 |
| L101 | viewDeployment() | 查看部署 |
| L116 | viewimage() | 查看流程图 |
| L137 | selftask() | 我的任务 |
| L152 | viewTaskForm() | 查看任务表单 |
| L166 | submitTask() | 提交任务 |
| L176 | viewCurrentImage() | 查看当前流程图 |
| L189 | taskmanager() | 任务管理 |
| L197 | hisTaskForm() | 历史任务表单 |
| L214 | delegatelist() | 委托列表 |
| L227 | delegateadd() | 添加委托 |
| L243 | delegateedit() | 编辑委托 |
| L273 | delegateupdate() | 更新委托 |

### WorkSpaceAction (540行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L167 | execute() | 日常项目跟踪 |
| L194 | notice() | 通知列表 |
| L216 | task() | 业务流程办理 |
| L278 | dailyTask() | 日常项目跟踪 |
| L290 | hisselftask() | 历史任务 |
| L306 | probTask() | 技术公告任务 |
| L319 | subcontractTask() | 分包任务 |
| L339 | updateNotifyState() | 更新通知状态 |

### MaintenanceAction (765行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L104 | execute() | 运维列表 |
| L142 | projectMaintenance() | 项目维护记录 |
| L218 | createProjectMaintenance() | 创建维护记录 |
| L432 | serviceDelivery() | 服务交付 |
| L479 | toUploadFile() | 上传文件页面 |
| L529 | uploadFileList() | 上传文件列表 |

### CertificateAction (143行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L37 | certificate() | 证书列表 |
| L48 | queryCertificate() | 查询证书 |
| L101 | uploadSealInfo() | 上传印章信息 |

### ProbManageAction (1726行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L205 | list() | 技术公告列表 |
| L237 | input() | 新建/编辑页面 |
| L286 | delete() | 删除技术公告 |
| L296 | bacthDeleteProbRestores() | 批量删除恢复任务 |
| L310 | edit() | 编辑技术公告 |
| L408 | checkProject() | 检查关联项目 |
| L449 | checkSubProject() | 检查子项目 |
| L478 | releaseTask() | 发布恢复任务 |
| L502 | managePrivateTask() | 管理个人任务 |
| L541 | updatePrivateTask() | 更新个人任务状态 |
| L559 | weeklyUpload() | 上传任务周报 |
| L584 | manageAllTask() | 管理全部任务 |
| L620 | updateRestoreTask() | 更新恢复任务 |
| L638 | save() | 保存技术公告 |
| L676 | update() | 更新技术公告 |
| L708 | audit() | 审核技术公告 |
| L725 | export() | 导出技术公告 |
| L787 | importSoftVersion() | 导入软件版本 |
| L813 | toCheckSoftVersion() | 检查软件版本 |
| L849 | submitSoftVersion() | 提交软件版本 |
| L874 | parserSoftVersion() | 解析软件版本 |
| L900 | parserOldSoftVersion() | 解析旧软件版本 |
| L978 | statistics() | 统计分析 |
| L1036 | affectedProjectSoftVersion() | 受影响项目软件版本 |
| L1078 | readSure() | 确认阅读 |
| L1096 | readLog() | 阅读日志 |
| L1119 | listProductItem() | 产品物料列表 |
| L1182 | listProbProduct() | 公告产品列表 |
| L1214 | inputProbProduct() | 编辑公告产品 |
| L1228 | saveProbProduct() | 保存公告产品 |
| L1243 | importProbProduct() | 导入公告产品 |
| L1268 | listComponent() | 组件列表 |
| L1300 | inputComponent() | 编辑组件 |
| L1314 | saveComponent() | 保存组件 |
| L1333 | importComponent() | 导入组件 |

### SubcontractAction (1994行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L209 | view() | 分包详情页 |
| L218 | list() | 分包列表 |
| L295 | input() | 新建/编辑页面 |
| L379 | create() | 创建分包 |
| L407 | apply() | 发起申请 |
| L431 | audit() | 审核 |
| L464 | close() | 关闭 |
| L492 | startCallBackFlow() | 发起回调流程 |
| L512 | querySubcontractCallback() | 查询分包回调 |
| L565 | chooseSubcontractProject() | 选择分包项目 |
| L586 | refreshSubcontractProject() | 刷新分包项目 |
| L632 | chooseShipmentInfo() | 选择发货信息 |
| L667 | querySubcontractLine() | 查询分包行 |
| L690 | querySubcontractDeliver() | 查询分包交付 |
| L710 | deleteSubcontractDeliver() | 删除分包交付 |
| L730 | checkSubcontractName() | 检查分包名称 |
| L752 | queryContractNoEngineeFee() | 查询工程费 |
| L784 | querySubcontractPayment() | 查询分包付款 |
| L937 | savePayment() | 保存付款 |
| L1009 | querySubcontractPaymentPrint() | 付款打印 |
| L1069 | verifyPaymentDeliver() | 验证付款交付 |
| L1114 | terminateWorkFlow() | 终止工作流 |
| L1134 | querySubcontractComment() | 查询分包评论 |
| L1188 | queryFacilitator() | 查询服务商 |
| L1210 | querySubcontractInfoForProject() | 查询项目分包信息 |
| L1235 | facilitatorList() | 服务商列表 |
| L1245 | facilitatorEdit() | 编辑服务商 |
| L1264 | downloadFile() | 下载文件 |

### SupervisionAction (468行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L91 | execute() | 监理列表 |
| L108 | projectSupervision() | 项目监理记录 |
| L157 | createProjectSupervision() | 创建监理记录 |
| L236 | deleteProjectSupervision() | 删除监理记录 |
| L253 | queryPowerUser() | 查询权限用户 |

### WarrantyCallbackAction (639行)
| 行号 | 方法名 | 功能 |
|------|--------|------|
| L126 | execute() | 质保回调列表 |
| L143 | projectWarrantyCallback() | 项目质保回调 |
| L220 | createProjectWarrantyCallback() | 创建质保回调 |
| L305 | deleteProjectWarrantyCallback() | 删除质保回调 |
| L322 | projectWarranty() | 项目质保 |
| L374 | customerProject() | 客户项目 |
| L411 | queryPowerUser() | 查询权限用户 |

---

## 统计摘要

| 类别 | 数量 |
|------|------|
| Action类 | 24个 |
| 总代码行数 | 18,391行 |
| 业务方法数 | ~250个 |
| 老系统方法总数(含getter/setter) | 1,203个 |
