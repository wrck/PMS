# 模块-表CRUD映射矩阵

> 数据库：dppms_d365 (MySQL)
> 本文档描述PMS系统各功能模块对数据表的CRUD操作映射关系，包含操作频率、数据量级、数据转换规则和数据校验机制。
> C=创建(Create) R=读取(Read) U=更新(Update) D=删除(Delete)
> 频率：高(>100次/天) / 中(10-100次/天) / 低(<10次/天)
> 量级：大(>1万条) / 中(1千-1万条) / 小(<1千条)

---

## 1. 完整模块-表CRUD矩阵

### 1.1 系统管理模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| fnd_user_info | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 中 | 用户CRUD、密码更新、角色分配 |
| fnd_roles | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 角色CRUD、菜单权限配置 |
| fnd_department | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 部门CRUD |
| fnd_menus | | ✓ | | | R:高 | 小 | 菜单树形查询（系统预置数据） |
| fnd_role_menus | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 角色-菜单关联CRUD |
| fnd_user_menus | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:中 | 中 | 用户-角色关联CRUD |
| fnd_user_power | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 中 | 用户-权限关联CRUD |
| fnd_basic_data | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 中 | 基础数据项CRUD |
| fnd_basic_data_type | | ✓ | | | R:高 | 小 | 基础数据类型查询（系统预置） |
| fnd_mails | ✓ | ✓ | ✓ | ✓ | C:高 R:高 U:高 D:低 | 大 | 邮件记录CRUD |
| fnd_sys_arg | | ✓ | | | R:高 | 小 | 系统参数查询 |
| tb_sys_log | ✓ | ✓ | | | C:高 R:中 | 大 | 操作日志 |

### 1.2 项目管理模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| pm_project_header | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:高 D:低 | 大 | 项目主表CRUD、状态更新 |
| pm_project_state | ✓ | ✓ | ✓ | | R:高 C:中 U:高 | 大 | 项目状态变更记录 |
| pm_project_member | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 大 | 项目成员CRUD、失效变更 |
| pm_project_contract | ✓ | ✓ | | | R:高 C:中 | 大 | 项目合同关联（创建/查询） |
| pm_project_product_line | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 大 | 项目产品线CRUD |
| pm_project_soft_version | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 项目软件版本CRUD |
| pm_project_weekly | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 大 | 项目周报主表 |
| pm_project_weekly_content | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 大 | 周报内容 |
| pm_project_weekly_feedback | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 中 | 周报反馈 |
| pm_project_log | ✓ | ✓ | | | C:中 R:低 | 大 | 项目日志 |
| pm_project_task | ✓ | ✓ | ✓ | | R:高 C:中 U:中 | 大 | 项目任务 |
| pm_column_of_relationship | | ✓ | | | R:中 | 小 | 泛化字段映射查询 |
| pm_project_group | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:低 D:低 | 大 | 项目分组CRUD |
| pm_project_group_relationship | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:低 D:低 | 大 | 项目分组关联CRUD |
| pm_project_related_party | ✓ | ✓ | ✓ | | R:中 C:中 U:低 | 中 | 项目相关方 |
| pm_project_notification | ✓ | ✓ | | | C:高 R:高 | 大 | 项目通知 |
| pm_project_notification_state | ✓ | ✓ | ✓ | | R:高 C:高 U:高 | 大 | 通知状态 |
| pm_project_deliver | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 中 | 项目交付件 |

### 1.3 售前测试模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| pm_presales_project_header | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 售前项目主表CRUD、状态流转 |
| pm_presales_project_product_line | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 中 | 售前项目产品线CRUD |
| pm_presales_project_callback | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 中 | 售前项目回访 |
| pm_presales_project_duration | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 中 | 售前项目耗时 |
| pm_presales_project_rma_info | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 售前项目RMA信息 |

### 1.4 回访与闭环模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| pm_cl_callback | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 回访申请CRUD |
| pm_cl_callback_quesnaire | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 回访问卷关联CRUD |
| pm_cl_evaluation_header | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 中 | 评价头表 |
| pm_cl_quesnaire_result_header | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 中 | 问卷结果头 |
| pm_cl_quesnaire_result_line | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 中 | 问卷结果行 |
| pm_cl_quesnaire_template_header | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 问卷模板头 |
| pm_cl_quesnaire_template_line | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 问卷模板题目 |
| pm_cl_quesnaire_template_options | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 问卷模板选项 |

### 1.5 技术公告模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| prob_main | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 技术公告主表CRUD |
| prob_restore | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 修复方案表CRUD |
| prob_restore_process | ✓ | ✓ | ✓ | | R:中 C:低 U:中 | 中 | 修复进度表（通过probId关联prob_main） |
| prob_restore_weekly | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 修复周报表（通过fileId关联附件） |
| prob_soft_version | ✓ | ✓ | | | R:低 C:低 | 小 | 软件版本参考表（INSERT IGNORE去重） |
| prob_softwares | ✓ | ✓ | ✓ | | R:中 C:低 U:中 | 中 | 影响软件版本明细表（含版本号/影响类型/分组） |
| prob_read_log | ✓ | ✓ | | | C:高 R:低 | 大 | 阅读日志（reader+status跟踪阅读确认） |

### 1.6 项目转包模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| pm_subcontract_project_header | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 转包项目主表CRUD |
| pm_subcontract_project_line | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 转包设备清单CRUD |
| pm_subcontract_project_payment | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 转包付款记录CRUD |
| pm_subcontract_project_price | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 转包价格表 |
| pm_subcontract_project_callback | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 转包回访 |
| pm_subcontract_deliver_files | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 转包交付文件 |
| pm_subcontract_project_payment_sse | | ✓ | ✓ | ✓ | R:低 U:低 D:低 | 小 | 转包付款SSE视图表（外部数据同步） |
| pm_subcontract_facilitator | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 服务商表 |

### 1.7 报表统计模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| pm_report_line_data | ✓ | ✓ | | | R:中 C:低 | 中 | 报表行数据 |
| pm_project_maintenance | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 维护记录（报表数据源） |

### 1.8 工作流模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| act_ru_task | ✓ | ✓ | ✓ | | R:高 C:高 U:高 | 大 | Activiti运行时任务 |
| act_hi_comment / fnd_act_hi_comment | ✓ | ✓ | | | C:高 R:中 | 大 | 审批意见 |
| act_hi_procinst | | ✓ | | | R:中 | 大 | Activiti历史流程实例 |
| dp_act_unify_task | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:高 D:低 | 大 | 统一工作流任务CRUD |

### 1.9 数据同步（定时任务）

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| pm_order_data_from_erp_source | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 大 | ERP订单源数据 |
| pm_order_data_from_erp_sap | ✓ | ✓ | ✓ | | C:低 R:中 U:低 | 大 | SAP订单数据 |
| pm_order_data_from_erp_d365 | ✓ | ✓ | ✓ | | C:低 R:中 U:低 | 大 | D365订单数据 |
| pm_person_from_oa | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 中 | OA人员数据 |
| pm_project_property_from_sms | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 大 | SMS项目属性 |

### 1.10 合格证与维保模块

| 数据表 | C | R | U | D | 操作频率 | 数据量级 | 说明 |
|--------|---|---|---|---|----------|----------|------|
| mes_oqc_info | | ✓ | | | R:中 | 大 | OQC检验信息（MES系统只读） |
| mes_seal_info | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 印章信息 |
| pm_project_warranty_callback | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 维保回访记录 |

---

## 2. 数据转换规则表

| 源表 | 目标表 | 转换逻辑 | 触发条件 | 校验机制 |
|------|--------|----------|----------|----------|
| pm_project_property_from_sms | pm_project_header | projectCode→projectCode, officeCode→column001, marketName→column002, systemName→column003, industryName→column005, serviceType→column012, majorLevel→column010, corpCode→compId | Quartz定时同步任务 | projectCode非空校验；officeCode在fnd_department中存在 |
| pm_order_data_from_erp_sap + pm_order_data_from_erp_d365 | pm_order_data_from_erp_source | 合并SAP和D365订单数据，按contractNo去重 | Quartz定时同步任务 | contractNo非空；profitCenter非空 |
| pm_order_data_from_erp_source | pm_project_contract | contractNo→contractNo, projectGroupCode由projectCode关联获取 | 项目创建时(projectService.insertProject) | contractNo唯一性校验；projectGroupCode存在性校验 |
| pm_order_data_from_erp_source | pm_project_product_line | itemCode/itemName/orderQuantity/deliverQuantity/openQuantity直接映射 | 项目创建时 | projectId非空；itemCode非空 |
| pm_presales_lend_info_from_sms | pm_presales_project_header | projectCode→projectCode, projectName→projectName, lendInfoId→lendInfoId, officeCode→officeCode, source='SMS' | Quartz定时同步任务 | projectCode非空；officeCode存在性校验 |
| pm_presales_lend_order_from_sms | pm_presales_project_product_line | itemCode/itemModel/productNum直接映射 | 售前项目创建时 | presalesId非空；itemCode非空 |
| pm_person_from_oa | pm_project_member | salesmanCode→memberCode, salesmanTel→phoneNum, salesmanMail→email | 项目成员创建时查询补充 | memberCode在fnd_user_info中存在 |
| fnd_user_info.roleIds | pm_project_member.memberRole | 角色ID解析：`;1;`→角色1, `;2;`→角色2，逗号分隔转分号包裹格式 | 用户信息更新时(userManageService.updateUserInfo) | roleIds格式校验（`;id;`格式） |
| fnd_user_power.areapower | 区域权限过滤 | 逗号分隔的officeCode列表，FIND_IN_SET匹配 | 项目列表查询时 | officeCode在fnd_department中存在 |
| pm_project_member | pm_project_state | 成员变更触发状态机：无SM/PM→STATE_30, 有SM无PM→STATE_31, 均有→STATE_32 | 成员变更时(projectService.updateProjectByProjectId) | 状态转换合法性校验 |
| pm_cl_evaluation_header | pm_project_state.closeProcessState | 闭环流程状态映射：processStatus×10=closeProcessState值 | 闭环审批完成时 | closeProcessState值范围校验(10/15/30/50) |
| pm_subcontract_project_payment | D365 PurchTable | 生成采购订单：amount→PurchAmount, facilitatorId→VendAccount | 转包流程GENERATE_CON节点 | amount>0；facilitatorId非空 |
| D365 VendPackingSlipTrans | pm_subcontract_project_payment.customInfo | 保存收货信息：packingSlipId/purchIds写入customInfo JSON | D365回调时 | packingSlipId非空 |
| 序列号条码 | 生产日期 | 序列号第10-12位解析年份和月份（16进制），格式化为yyyy-MM | 合格证查询时(certificateAction.generateProductionDate) | 序列号长度≥12位；16进制解析有效 |

---

## 3. 数据校验机制汇总

### 3.1 唯一性校验

| 校验对象 | 校验字段 | 校验方式 | 校验位置 | 失败处理 |
|----------|----------|----------|----------|----------|
| 用户名 | fnd_user_info.username | `userManageService.queryUserSizeByUserName()` | UserManageAction.add() / checkUsername() | Ajax返回提示，阻止提交 |
| 项目编码 | pm_project_header.projectCode | `projectService.queryProjectCode()` 自动生成 | ProjectServiceImpl.insertProject() | 自动生成唯一编码，无需手动校验 |
| 合同号-项目 | pm_project_contract.contractNo | 查询合同号是否已创建项目 | ProjectAction.insertProject() | 提示"合同号已创建项目" |
| 售前项目编码 | pm_presales_project_header.presalesCode | 自动生成，格式与projectCode一致 | PresalesServiceImpl.startPresalesFlow() | 自动生成唯一编码 |
| 转包项目名称 | pm_subcontract_project_header.subcontractName | `subcontractService.checkSubcontractName()` | SubcontractAction | Ajax返回提示 |
| 基础数据编码 | fnd_basic_data.basicDataId | `basicDataService.findBasicDataId()` | BasicDataManageAction.findBasicDataId() | Ajax返回提示 |
| 问卷模板编号 | pm_cl_quesnaire_template_header.questionnaireTemplateNum | `pmClosedLoopQuesnaireService` 查询最大编号+1 | PmClosedLoopQuesnaireServiceImpl.insertQuesnaireHeader() | 自动递增生成 |
| 项目组编码 | pm_project_group.projectGroupCode | `projectDao.queryMaxProjectGroupCode()` + 1 | ProjectServiceImpl.insertProject() | ⚠️ 并发安全隐患，FIXME已标注 |

### 3.2 引用完整性校验

| 校验对象 | 外键字段 | 被引用表 | 校验方式 | 校验位置 | 失败处理 |
|----------|----------|----------|----------|----------|----------|
| 项目成员-用户 | pm_project_member.memberCode | fnd_user_info.username | 查询用户是否存在 | ProjectAction.createMember() | 提示用户不存在 |
| 项目成员-角色 | pm_project_member.memberRole | fnd_basic_data(dataTypeCode=03) | 角色编码合法性 | ProjectServiceImpl.insertProjectMember() | 角色编码必须为10(销售)/20(服务经理)/30(项目经理)/40(团队成员)/50(服务渠道)/60(最终客户)/70(技术经理)之一 |
| 项目-办事处 | pm_project_header.column001 | fnd_department.departmentNum | 办事处编码存在性 | 项目创建/更新时 | 下拉选择，不存在则无法选择 |
| 角色-菜单 | fnd_role_menus.menuId | fnd_menus.id | 菜单ID存在性 | RoleManageAction.addSubmit() | 前端选择，不存在则无法选择 |
| 用户-默认页面 | fnd_user_info.defaultPage | fnd_menus.path | 页面路径有效性 | UserManageServiceImpl.updateUserInfo() | 抛出RuntimeException |
| 闭环-服务经理 | pm_cl_evaluation_header.nextAcceptPerson | fnd_user_info.username | 服务经理有效性 | PmClosedLoopAction.addPmCLApply() | 抛出异常提示"服务经理无效" |
| 转包-服务商 | pm_subcontract_project_header.facilitatorId | pm_subcontract_facilitator.id | 服务商存在性 | 转包项目创建时 | 下拉选择 |
| 问卷-模板 | pm_cl_quesnaire_result_header.quesnaireTemplateHeaderId | pm_cl_quesnaire_template_header.id | 模板存在性 | 问卷填写时 | 模板必须为生效状态 |
| 审批意见-流程 | fnd_act_hi_comment.instId | act_ru_task / act_hi_procinst | 流程实例存在性 | WorkFlowServiceImpl.addSelfActComment() | Activiti框架校验 |

### 3.3 业务规则校验

| 校验规则 | 适用场景 | 校验逻辑 | 校验位置 | 失败处理 |
|----------|----------|----------|----------|----------|
| 项目状态转换合法性 | 项目状态流转 | STATE_30→STATE_31(指派SM)→STATE_32(指派PM)→STATE_100(闭环)；任意状态→STATE_20(不予跟踪) | ProjectServiceImpl.insertProject() / updateProjectByProjectId() | 状态不合法时操作失败 |
| 闭环流程状态机 | 闭环审批 | PM申请→SM审核→CB回访→CL闭环；每步可驳回回到上一步 | PmClosedLoopServiceImpl.addPmCLApply()等 | 驳回时流程回退 |
| 项目回退合法性 | 项目回退操作 | 根据当前状态和回退类型判断合法性：PROJECT_CREATE_STATE42/40/30/50各有不同规则 | ProjectServiceImpl.backToLastStep() | 不合法时发送对应邮件通知 |
| 问卷评分不超过总分 | 问卷题目提交 | 各题目分数之和≤问卷总分 | PmClosedLoopQuesnaireAction.submitLine() | 超出时返回ERROR |
| 闭环建议问卷唯一生效 | 问卷生效操作 | 闭环建议类型(quesType=30)同时只能有一个生效问卷 | PmClosedLoopQuesnaireServiceImpl.updateEffecticeStart() | 先失效其他同类问卷 |
| 密码过期检查 | 用户登录后每次请求 | 当前日期与pwdoverdue比较 | UserCheckFilter | 过期则重定向到密码修改页 |
| 验证码校验 | 生产环境登录 | Session中的rand属性与用户输入比对 | LoginServiceImpl.login() | 不匹配则登录失败 |
| 文件扩展名白名单 | 文件上传 | 检查文件扩展名是否在白名单内 | ProjectAction.UploadFile() / PresalesServiceImpl.uploadFile() | 不在白名单则拒绝上传 |
| SQL执行安全检查 | 基础数据SQL执行 | SQL必须包含WHERE或INSERT，无WHERE的SQL不允许执行 | BasicDataManageAction.executeSql() | 无WHERE条件则拒绝执行 |
| 管理员权限检查 | 用户管理/批量操作 | 检查当前用户是否为管理员/工程管理部/工程管理部领导 | UserManageAction.prepare() / ProjectAction.clearProject() | 抛出CustomRuntimeException |
| 项目修改权限 | 项目信息更新 | 检查当前用户是否为项目相关人员(SM/PM/管理员) | ProjectAction.updateProject() | 返回ERROR |

### 3.4 数据格式校验

| 校验对象 | 校验字段 | 格式规则 | 校验方式 | 校验位置 | 失败处理 |
|----------|----------|----------|----------|----------|----------|
| 用户邮箱 | fnd_user_info.email | 合法邮箱格式 | 前端正则+后端非空 | UserManageAction.add()/edit() | 返回ERROR |
| 日期格式 | 各表日期字段 | yyyy-MM-dd / yyyy-MM-dd HH:mm:ss | iBatis DateTimeTypeHandler自动转换 | DAO层 | TypeHandler异常时返回null |
| 金额格式 | pm_subcontract_project_payment.amount | 数字格式，支持千分位逗号 | SQL中REPLACE去掉逗号后CONVERT为DECIMAL | SubcontractDaoImpl | 解析失败返回0 |
| 角色ID格式 | fnd_user_info.roleIds | `;id;` 分号包裹格式，多个角色：`;1;,;2;` | UserManageServiceImpl.dealWith()转换 | UserManageServiceImpl.addUserInfo() | 格式错误导致权限映射失败 |
| JSON扩展字段 | 各表customInfo | 合法JSON格式 | FastjsonTypeHandler序列化/反序列化 | DAO层 | 解析失败使用空JSON对象"{}" |
| 序列号格式 | 合格证查询 | 长度≥12位，第10-12位为16进制 | CertificateAction.generateProductionDate() | CertificateAction | 解析失败返回null |
| Base64编码 | ProjectAction.paramId | Base64编码的项目ID | Base64解码 | ProjectAction.updateProject() | 解码失败抛出异常 |
| 密码格式 | fnd_user_info.password | MD5 32位十六进制字符串 | Md5Util.getMD5() | UserManageAction.add() / pwdreset() | 自动加密，无需手动校验 |

---

## 4. 共享表分析

### 4.1 跨模块共享表

| 数据表 | 操作模块 | 各模块操作类型 | 共享风险 |
|--------|----------|----------------|----------|
| pm_project_header | 项目管理、回访管理、售前测试、项目维护、报表统计、数据同步 | 项目管理(CRUD)、回访管理(U-状态)、售前测试(R)、项目维护(R)、报表统计(R)、数据同步(U-属性) | **高** - 多模块写入 |
| pm_project_member | 项目管理、售前测试、数据同步 | 项目管理(CRUD)、售前测试(CR-售前成员)、数据同步(R-查询联系方式) | **高** - 多模块写入 |
| pm_project_state | 项目管理、回访管理 | 项目管理(CRU)、回访管理(U-闭环状态) | **中** - 状态字段分区更新 |
| fnd_user_info | 系统管理、所有模块 | 系统管理(CRUD)、所有模块(R-用户上下文) | **低** - 单模块写入 |
| fnd_basic_data | 系统管理、所有模块 | 系统管理(CRUD)、所有模块(R-数据字典) | **低** - 单模块写入 |
| fnd_mails | 系统管理、项目管理、售前测试、转包、维护 | 系统管理(CRUD)、各业务模块(C-发送邮件) | **中** - 多模块写入但仅新增 |
| dp_act_unify_task | 工作流、售前测试、回访管理、转包 | 工作流(CRUD)、各业务流程模块(CR-任务记录) | **中** - 多模块写入 |
| fnd_act_hi_comment | 工作流、售前测试、回访管理、转包 | 工作流(CR)、各业务流程模块(R) | **低** - 单模块写入 |

### 4.2 共享表操作冲突分析

| 冲突场景 | 涉及模块 | 冲突类型 | 处理策略 |
|----------|----------|----------|----------|
| 项目状态更新 | 项目管理 vs 回访管理 | 并发更新pm_project_state | 回访模块仅更新closeProcessState字段，项目管理更新其他状态字段，字段级隔离 |
| 项目成员变更 | 项目管理 vs 售前测试 | 并发写入pm_project_member | 售前项目通过fromFlag区分来源，查询时按projectType过滤 |
| 闭环流程状态 | 回访管理 vs 项目管理 | 并发更新closeProcessState | 回访模块检查是否有活跃闭环任务，无活跃任务才更新 |
| 邮件发送 | 多模块并发 | 并发插入fnd_mails | 邮件表仅插入操作，无冲突风险 |
| 统一任务 | 多流程并发 | 并发写入dp_act_unify_task | 通过latest字段标记最新记录，历史记录保留 |
| 项目组编码生成 | 并发创建项目 | 并发读取pm_project_group最大编码 | ⚠️ FIXME：synchronized仅限单JVM，集群环境存在隐患 |

---

## 5. CRUD操作统计

### 5.1 各模块操作权限分布

| 模块 | 表数量 | C | R | U | D | 高频操作 | 大数据量表 |
|------|--------|---|---|---|---|----------|-----------|
| 系统管理 | 12 | 9 | 12 | 10 | 7 | R:用户/菜单/基础数据/邮件 | fnd_mails, tb_sys_log |
| 项目管理 | 18 | 15 | 18 | 13 | 7 | R:项目列表/成员/任务 C:通知 | pm_project_header, pm_project_member, pm_project_weekly_content |
| 售前测试 | 5 | 5 | 5 | 5 | 1 | R:售前项目列表 | pm_presales_project_header |
| 回访与闭环 | 8 | 8 | 8 | 8 | 4 | R:评价/问卷 C:问卷结果 | pm_cl_evaluation_header |
| 技术公告 | 6 | 6 | 6 | 5 | 2 | C:阅读日志 R:公告列表 | prob_read_log |
| 项目转包 | 7 | 7 | 7 | 7 | 3 | R:转包项目列表 | pm_subcontract_project_line |
| 报表统计 | 2 | 1 | 2 | 1 | 1 | R:报表数据 | pm_report_line_data |
| 工作流 | 4 | 3 | 4 | 3 | 1 | R:任务 C:审批意见 | act_ru_task, dp_act_unify_task |
| 数据同步 | 5 | 5 | 5 | 5 | 0 | R:订单数据 C:同步数据 | pm_order_data_from_erp_source |
| 合格证与维保 | 3 | 2 | 3 | 2 | 2 | R:OQC信息 | mes_oqc_info |

### 5.2 操作类型分布

```
创建(C) ████████████████████████████  61个表-模块组合
读取(R) █████████████████████████████ 70个表-模块组合
更新(U) ██████████████████████████    57个表-模块组合
删除(D) ██████████████                24个表-模块组合
```

**分析结论：**
- 读取操作覆盖所有表，是最普遍的操作类型
- 删除操作最为谨慎，仅24个表-模块组合支持删除
- 报表统计模块以只读查询为主
- 数据同步模块不执行删除操作，采用清空后重写策略
- 项目管理模块涉及表最多(18张)，操作最复杂
- 高频操作集中在项目列表查询、用户/菜单/基础数据读取、邮件发送和通知创建
- 大数据量表集中在日志类(pm_project_weekly_content, tb_sys_log, prob_read_log)和主业务表(pm_project_header, pm_project_member)
