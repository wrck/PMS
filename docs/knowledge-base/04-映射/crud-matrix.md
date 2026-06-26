# PMS 全模块-表 CRUD 映射矩阵

> 数据库：dppms_d365 (MySQL 8.0.16) / activiti (MySQL，独立库)
> 本文档描述 PMS 系统各功能模块对数据表的 CRUD 操作映射关系，覆盖 core、PMS-struts、PMS-springmvc、PMS-activiti、PMS-ext-d365、pms-rules、pms-ext-fp、pms-security 全部模块。
> C=创建(Create) R=读取(Read) U=更新(Update) D=删除(Delete)
> 频率：高(>100次/天) / 中(10-100次/天) / 低(<10次/天)
> 量级：大(>1万条) / 中(1千-1万条) / 小(<1千条)

---

## 1. 完整模块-表 CRUD 矩阵

### 1.1 core 模块（用户权限与基础平台）

> 模块定位：共享框架模块，提供用户、角色、菜单、权限、部门、基础数据、邮件、系统参数等系统级管理能力。
> 基础包名：`com.dp.plat.core`

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| core | fnd_user_info | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 中 | 用户CRUD、密码更新、角色分配 |
| core | fnd_roles | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 角色CRUD、菜单权限配置 |
| core | fnd_department | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 部门CRUD |
| core | fnd_menus | | ✓ | | | R:高 | 小 | 菜单树形查询（系统预置数据） |
| core | fnd_role_menus | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 角色-菜单关联CRUD |
| core | fnd_user_menus | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:中 | 中 | 用户-菜单关联CRUD |
| core | fnd_user_power | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 中 | 用户-权限关联CRUD |
| core | fnd_basic_data | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 中 | 基础数据项CRUD |
| core | fnd_basic_data_type | | ✓ | | | R:高 | 小 | 基础数据类型查询（系统预置） |
| core | fnd_company | ✓ | ✓ | ✓ | ✓ | R:高 C:低 U:低 D:低 | 小 | 公司CRUD |
| core | fnd_mails | ✓ | ✓ | ✓ | ✓ | C:高 R:高 U:高 D:低 | 大 | 邮件记录CRUD（MailerJob 定时发送） |
| core | fnd_sys_arg | | ✓ | | | R:高 | 小 | 系统参数查询（SystemConfig 启动加载） |
| core | fnd_files | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 中 | 附件文件CRUD |
| core | fnd_sequence | ✓ | ✓ | ✓ | | R:中 C:低 U:中 | 小 | 序列号生成 |
| core | tb_sys_log | ✓ | ✓ | | | C:高 R:中 | 大 | 操作日志（SystemLogAspect 异步记录） |
| core | fnd_data_refresh_log | ✓ | ✓ | | | C:低 R:低 | 中 | 数据刷新日志 |

### 1.2 PMS-struts 模块（售前、回访、项目管理、问题管理、转包、工作流）

> 模块定位：遗留 Struts2 Web 应用，承载售前测试、项目回访闭环、项目管理、技术公告、项目转包、报表统计、数据同步等核心业务。
> 基础包名：`com.dp.plat.*`（35 个子包），源码目录 `src/`

#### 1.2.1 项目管理子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | pm_project_header | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:高 D:低 | 大 | 项目主表CRUD、状态更新 |
| PMS-struts | pm_project_state | ✓ | ✓ | ✓ | | R:高 C:中 U:高 | 大 | 项目状态变更记录 |
| PMS-struts | pm_project_member | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 大 | 项目成员CRUD、失效变更 |
| PMS-struts | pm_project_contract | ✓ | ✓ | | | R:高 C:中 | 大 | 项目合同关联（创建/查询） |
| PMS-struts | pm_project_product_line | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 大 | 项目产品线CRUD |
| PMS-struts | pm_project_soft_version | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 项目软件版本CRUD |
| PMS-struts | pm_project_weekly | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 大 | 项目周报主表 |
| PMS-struts | pm_project_weekly_content | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 大 | 周报内容 |
| PMS-struts | pm_project_weekly_feedback | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 中 | 周报反馈 |
| PMS-struts | pm_project_log | ✓ | ✓ | | | C:中 R:低 | 大 | 项目日志 |
| PMS-struts | pm_project_task | ✓ | ✓ | ✓ | | R:高 C:中 U:中 | 大 | 项目任务 |
| PMS-struts | pm_column_of_relationship | | ✓ | | | R:中 | 小 | 泛化字段映射查询 |
| PMS-struts | pm_project_group | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:低 D:低 | 大 | 项目分组CRUD |
| PMS-struts | pm_project_group_relationship | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:低 D:低 | 大 | 项目分组关联CRUD |
| PMS-struts | pm_project_related_party | ✓ | ✓ | ✓ | | R:中 C:中 U:低 | 中 | 项目相关方 |
| PMS-struts | pm_project_notification | ✓ | ✓ | | | C:高 R:高 | 大 | 项目通知 |
| PMS-struts | pm_project_notification_state | ✓ | ✓ | ✓ | | R:高 C:高 U:高 | 大 | 通知状态 |
| PMS-struts | pm_project_deliver | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 中 | 项目交付件 |
| PMS-struts | pm_project_maintenance | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 维护记录（报表数据源） |
| PMS-struts | pm_project_shipment | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 中 | 项目发货关联 |
| PMS-struts | pm_project_warranty_callback | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 维保回访记录 |
| PMS-struts | pm_project_instruction | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 项目说明 |
| PMS-struts | pm_project_supervision | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 项目督导 |

#### 1.2.2 售前测试子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | pm_presales_project_header | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 售前项目主表CRUD、状态流转 |
| PMS-struts | pm_presales_project_product_line | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:低 D:低 | 中 | 售前项目产品线CRUD |
| PMS-struts | pm_presales_project_callback | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 中 | 售前项目回访 |
| PMS-struts | pm_presales_project_duration | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 中 | 售前项目耗时 |
| PMS-struts | pm_presales_project_rma_info | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 售前项目RMA信息 |

#### 1.2.3 回访与闭环子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | pm_cl_callback | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 回访申请CRUD |
| PMS-struts | pm_cl_callback_quesnaire | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 回访问卷关联CRUD |
| PMS-struts | pm_cl_evaluation_header | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 中 | 评价头表 |
| PMS-struts | pm_cl_quesnaire_result_header | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 中 | 问卷结果头 |
| PMS-struts | pm_cl_quesnaire_result_line | ✓ | ✓ | ✓ | | R:中 C:中 U:中 | 中 | 问卷结果行 |
| PMS-struts | pm_cl_quesnaire_template_header | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 问卷模板头 |
| PMS-struts | pm_cl_quesnaire_template_line | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 问卷模板题目 |
| PMS-struts | pm_cl_quesnaire_template_options | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 问卷模板选项 |

#### 1.2.4 技术公告（问题管理）子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | prob_main | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 技术公告主表CRUD |
| PMS-struts | prob_restore | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 修复方案表CRUD |
| PMS-struts | prob_restore_process | ✓ | ✓ | ✓ | | R:中 C:低 U:中 | 中 | 修复进度表（通过probId关联prob_main） |
| PMS-struts | prob_restore_weekly | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 修复周报表（通过fileId关联附件） |
| PMS-struts | prob_soft_version | ✓ | ✓ | | | R:低 C:低 | 小 | 软件版本参考表（INSERT IGNORE去重） |
| PMS-struts | prob_softwares | ✓ | ✓ | ✓ | | R:中 C:低 U:中 | 中 | 影响软件版本明细表 |
| PMS-struts | prob_read_log | ✓ | ✓ | | | C:高 R:低 | 大 | 阅读日志（reader+status跟踪阅读确认） |

#### 1.2.5 项目转包子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | pm_subcontract_project_header | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 转包项目主表CRUD |
| PMS-struts | pm_subcontract_project_line | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 转包设备清单CRUD |
| PMS-struts | pm_subcontract_project_payment | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:中 D:低 | 中 | 转包付款记录CRUD |
| PMS-struts | pm_subcontract_project_price | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 转包价格表 |
| PMS-struts | pm_subcontract_project_callback | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 转包回访 |
| PMS-struts | pm_subcontract_deliver_files | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 转包交付文件 |
| PMS-struts | pm_subcontract_project_payment_sse | | ✓ | ✓ | ✓ | R:低 U:低 D:低 | 小 | 转包付款SSE视图表（外部数据同步） |
| PMS-struts | pm_subcontract_facilitator | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 服务商表 |

#### 1.2.6 报表统计子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | pm_report_line_data | ✓ | ✓ | | | R:中 C:低 | 中 | 报表行数据 |

#### 1.2.7 数据同步子模块（定时任务）

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | pm_order_data_from_erp_source | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 大 | ERP订单源数据 |
| PMS-struts | pm_order_data_from_erp_sap | ✓ | ✓ | ✓ | | C:低 R:中 U:低 | 大 | SAP订单数据 |
| PMS-struts | pm_order_data_from_erp_d365 | ✓ | ✓ | ✓ | | C:低 R:中 U:低 | 大 | D365订单数据 |
| PMS-struts | pm_person_from_oa | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 中 | OA人员数据 |
| PMS-struts | pm_project_property_from_sms | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 大 | SMS项目属性 |
| PMS-struts | pm_presales_lend_info_from_sms | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 中 | SMS售前借用信息 |
| PMS-struts | pm_presales_lend_order_from_sms | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 中 | SMS售前借用订单 |

#### 1.2.8 合格证与维保子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-struts | mes_oqc_info | | ✓ | | | R:中 | 大 | OQC检验信息（MES系统只读） |
| PMS-struts | mes_seal_info | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 印章信息 |

### 1.3 PMS-springmvc 模块（项目管理、外派、行业资产、工作流）

> 模块定位：较新的 Spring MVC Web 表现层，承载项目管理、外派管理、行业资产/漏洞管理、工作流审批等业务。
> 基础包名：`com.dp.plat.pms.springmvc`

#### 1.3.1 项目管理子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc | pm_project_header | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:高 D:低 | 大 | 项目主表（复用 PMS-struts 表，CRUD） |
| PMS-springmvc | pm_project_task | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 大 | 项目任务CRUD（ProjectTaskController） |
| PMS-springmvc | pm_project_member | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 大 | 项目成员CRUD（ProjectMemberController） |
| PMS-springmvc | data_field_relation | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 小 | 字段配置关系（DataFieldRelationMapper） |

#### 1.3.2 外派管理子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc | pm_dispatch_project_header | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 外派项目主表CRUD（DispatchProjectController） |
| PMS-springmvc | pm_dispatch_project_settlement | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 外派结算CRUD（DispatchSettlementController） |
| PMS-springmvc | pm_facilitator | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 服务商管理（FacilitatorController） |
| PMS-springmvc | pm_facilitator_form_d365 | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 服务商D365表单 |

#### 1.3.3 日报与通用数据子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc | pm_daily_report | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:低 | 中 | 日报CRUD（DailyReportController） |
| PMS-springmvc | pm_common_related_data | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 通用关联数据CRUD（支持Excel导入） |

#### 1.3.4 行业资产与漏洞子模块（安服 AF）

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc | pm_industry_asset | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 行业资产CRUD（IndustryAssetController） |
| PMS-springmvc | pm_industry_leak | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 行业漏洞CRUD（IndustryLeakController） |
| PMS-springmvc | pm_industry_leak_warning | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 行业漏洞预警CRUD |
| PMS-springmvc | pm_industry_asset_leak_relation | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 资产漏洞关联CRUD |
| PMS-springmvc | pm_industry_asset_project_relation | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 资产项目关联CRUD |

#### 1.3.5 工作流业务子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc | pm_workflow | ✓ | ✓ | ✓ | ✓ | R:高 C:中 U:中 D:低 | 中 | 工作流业务表CRUD（PmWorkFlowMapper） |

#### 1.3.6 EHR 人事集成子模块

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc | ehr_company | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 小 | EHR公司数据同步（EhrDataJob） |
| PMS-springmvc | ehr_department | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 中 | EHR部门数据同步 |
| PMS-springmvc | ehr_employee | ✓ | ✓ | ✓ | | R:中 C:低 U:低 | 大 | EHR员工数据同步 |
| PMS-springmvc | ehr_job | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | EHR岗位数据同步 |
| PMS-springmvc | ehr_emp_power | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | EHR员工权限同步 |
| PMS-springmvc | ehr_login | ✓ | ✓ | | | R:低 C:低 | 小 | EHR登录记录 |

### 1.4 PMS-activiti 模块（工作流引擎）

> 模块定位：Activiti 5.23.0 工作流引擎模块，使用独立的 `activiti` 数据库存储流程数据。
> 基础包名：`com.dp.plat.activiti`

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-activiti | act_re_deployment | ✓ | ✓ | | ✓ | R:中 C:低 D:低 | 中 | 流程部署（RepositoryService） |
| PMS-activiti | act_re_procdef | | ✓ | | | R:高 | 中 | 流程定义查询 |
| PMS-activiti | act_re_model | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | 流程模型CRUD（ModelController） |
| PMS-activiti | act_ru_execution | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:高 D:中 | 大 | 运行时执行实例（RuntimeService） |
| PMS-activiti | act_ru_task | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:高 D:中 | 大 | 运行时任务（TaskService） |
| PMS-activiti | act_ru_variable | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:高 D:中 | 大 | 运行时流程变量 |
| PMS-activiti | act_ru_identitylink | ✓ | ✓ | | ✓ | R:高 C:高 D:中 | 大 | 运行时身份链接（候选人/候选组） |
| PMS-activiti | act_ru_job | ✓ | ✓ | ✓ | ✓ | R:中 C:中 U:中 D:中 | 中 | 运行时作业（定时/异步任务） |
| PMS-activiti | act_ru_event_subscr | ✓ | ✓ | | ✓ | R:低 C:低 D:低 | 小 | 运行时事件订阅 |
| PMS-activiti | act_hi_procinst | ✓ | ✓ | ✓ | | R:高 C:高 U:中 | 大 | 历史流程实例（HistoryService） |
| PMS-activiti | act_hi_taskinst | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:中 D:低 | 大 | 历史任务实例 |
| PMS-activiti | act_hi_actinst | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:低 D:低 | 大 | 历史活动实例 |
| PMS-activiti | act_hi_varinst | ✓ | ✓ | | | R:高 C:高 | 大 | 历史变量 |
| PMS-activiti | act_hi_comment | ✓ | ✓ | | | C:高 R:中 | 大 | 审批意见（taskService.addComment） |
| PMS-activiti | act_hi_detail | ✓ | ✓ | | | C:中 R:低 | 大 | 历史明细 |
| PMS-activiti | act_hi_attachment | ✓ | ✓ | | ✓ | C:低 R:低 D:低 | 中 | 历史附件 |
| PMS-activiti | act_hi_identitylink | ✓ | ✓ | | | C:高 R:中 | 大 | 历史身份链接 |
| PMS-activiti | act_ge_bytearray | ✓ | ✓ | | ✓ | C:中 R:中 D:低 | 中 | 通用字节数组（BPMN/流程图） |
| PMS-activiti | act_ge_property | | ✓ | ✓ | | R:中 U:低 | 小 | 引擎属性（版本/next.dbid） |
| PMS-activiti | act_id_user | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | Activiti用户（IdentityService） |
| PMS-activiti | act_id_group | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 小 | Activiti组 |
| PMS-activiti | act_id_membership | ✓ | ✓ | | ✓ | R:低 C:低 D:低 | 小 | 用户-组关联 |
| PMS-activiti | act_id_info | ✓ | ✓ | | ✓ | R:低 C:低 D:低 | 小 | 用户信息 |
| PMS-activiti | act_evt_log | ✓ | ✓ | | | C:低 R:低 | 中 | 事件日志 |
| PMS-activiti | act_procdef_info | ✓ | ✓ | | ✓ | R:低 C:低 D:低 | 小 | 流程定义动态信息 |
| PMS-activiti | dp_act_unify_task | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 小 | 动态任务分配配置（ActUserTaskMapper） |

### 1.5 PMS-ext-d365 模块（D365 集成）

> 模块定位：D365 ERP 集成扩展层，封装采购订单、采购收货、合同验收交付等业务数据向 D365 的推送与本地持久化。
> 基础包名：`com.dp.plat.pms.extend.d365`

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-ext-d365 | dp_erp_purchase_order_header | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 采购订单头（PurchaseMapper） |
| PMS-ext-d365 | dp_erp_purchase_order_line | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 采购订单行（PurchaseLineMapper） |
| PMS-ext-d365 | dp_erp_purchase_receipt_header | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 中 | 采购收货头（PurchaseReceiptMapper） |
| PMS-ext-d365 | dp_erp_purchase_receipt_line | ✓ | ✓ | ✓ | ✓ | R:低 C:低 U:低 D:低 | 中 | 采购收货行（PurchaseReceiptLineMapper） |

### 1.6 pms-rules 模块（规则引擎）

> 模块定位：规则引擎公共模块，提供基于 Aviator 的表达式/脚本求值能力。
> 基础包名：`com.dp.plat.rules`

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| pms-rules | （无独立数据表） | - | - | - | - | - | - | 规则脚本以字符串形式存储于业务模块的系统参数表（fnd_sys_arg）或业务配置 JSON 中，由 AviatorUtils 在内存中 LRU 缓存（默认 100），不持久化到独立表 |

> **说明**：pms-rules 模块本身不持久化规则，规则脚本来源包括：
> - 系统参数表 `fnd_sys_arg`（如 `subcontract.areaLeader.auditEngineeFee.offices`）
> - 业务配置 JSON（如 `config.scripts`，由 `ProjectStateUpdateAspect.execScripts` 读取）
> - 工作流节点配置（如 `SubcontractInspectionListener.checkAssignee`）

### 1.7 pms-ext-fp 模块（FP 财务平台集成）

> 模块定位：FP 财务平台集成扩展，封装电子发票识别、验真、归档等能力。
> 基础包名：`com.dp.plat.pms.extend.fp`

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| pms-ext-fp | tb_invoice | ✓ | ✓ | ✓ | ✓ | R:中 C:低 U:低 D:低 | 中 | 发票主表（InvoiceProviderInfo 关联） |
| pms-ext-fp | tb_invoice_provider_info | ✓ | ✓ | ✓ | | R:低 C:低 U:低 | 小 | 发票来源信息（电子签名/文件元数据） |

> **说明**：pms-ext-fp 模块主要通过 `FPApi` 调用 FP 平台远程接口（Token 获取、发票识别验真、档案归档），本地仅持久化发票来源信息。发票类型/状态判定通过 pms-rules 的 Aviator 表达式实现（`invoiceTypeCondition` / `invoiceStatusCondition`）。

### 1.8 pms-security 模块（安全组件）

> 模块定位：安全组件基础库，提供 CSRF 防护、XSS 防护、SQL 解析、AES 加密、验证码等能力。
> 基础包名：`com.dp.plat.security`

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| pms-security | （无独立数据表） | - | - | - | - | - | - | 纯工具库，CSRF Token 存储于 Session，XSS 过滤在请求包装层处理，不涉及数据库表 |

### 1.9 统一任务推送（跨模块）

> 由外部 jar `activiti-api-unifytask-mybatis` 提供，PMS-springmvc 与 PMS-struts 共同使用。

| 模块 | 表名 | C | R | U | D | 操作频率 | 数据量 | 说明 |
|------|------|---|---|---|---|---------|--------|------|
| PMS-springmvc/struts | dp_act_unify_task | ✓ | ✓ | ✓ | ✓ | R:高 C:高 U:高 D:低 | 大 | 统一工作流任务CRUD（推送致远 OA） |
| PMS-springmvc/struts | fnd_act_hi_comment | ✓ | ✓ | | | C:高 R:中 | 大 | 审批意见（PMS 业务侧副本） |

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
| EHR 数据库 | ehr_company/ehr_department/ehr_employee | EHR 全量/增量同步至本地表 | EhrDataJob 定时任务（0 0 5 * * ?） | 员工工号唯一性校验 |
| D365 SQL Server | dp_erp_purchase_order_header/line | D365 采购订单回填：purchId/inventTransId 持久化 | pushPurchaseOrder 调用后 | response.code==200；lineNum 匹配 |
| D365 SQL Server | dp_erp_purchase_receipt_header/line | D365 采购收货回填：packingSlipId 持久化 | pushPurchaseReceipt 调用后 | response.code==200；inventTransId 匹配 |

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
| 外派单号 | pm_dispatch_project_header.dispatchSeq | `generateDispatchSeq()` 自动生成 | DispatchProjectController | 自动生成唯一编码 |

### 3.2 引用完整性校验

| 校验对象 | 外键字段 | 被引用表 | 校验方式 | 校验位置 | 失败处理 |
|----------|----------|----------|----------|----------|----------|
| 项目成员-用户 | pm_project_member.memberCode | fnd_user_info.username | 查询用户是否存在 | ProjectAction.createMember() | 提示用户不存在 |
| 项目成员-角色 | pm_project_member.memberRole | fnd_basic_data(dataTypeCode=03) | 角色编码合法性 | ProjectServiceImpl.insertProjectMember() | 角色编码必须为10/20/30/40/50/60/70之一 |
| 项目-办事处 | pm_project_header.column001 | fnd_department.departmentNum | 办事处编码存在性 | 项目创建/更新时 | 下拉选择，不存在则无法选择 |
| 角色-菜单 | fnd_role_menus.menuId | fnd_menus.id | 菜单ID存在性 | RoleManageAction.addSubmit() | 前端选择，不存在则无法选择 |
| 用户-默认页面 | fnd_user_info.defaultPage | fnd_menus.path | 页面路径有效性 | UserManageServiceImpl.updateUserInfo() | 抛出RuntimeException |
| 闭环-服务经理 | pm_cl_evaluation_header.nextAcceptPerson | fnd_user_info.username | 服务经理有效性 | PmClosedLoopAction.addPmCLApply() | 抛出异常提示"服务经理无效" |
| 转包-服务商 | pm_subcontract_project_header.facilitatorId | pm_subcontract_facilitator.id | 服务商存在性 | 转包项目创建时 | 下拉选择 |
| 问卷-模板 | pm_cl_quesnaire_result_header.quesnaireTemplateHeaderId | pm_cl_quesnaire_template_header.id | 模板存在性 | 问卷填写时 | 模板必须为生效状态 |
| 审批意见-流程 | fnd_act_hi_comment.instId | act_ru_task / act_hi_procinst | 流程实例存在性 | WorkFlowServiceImpl.addSelfActComment() | Activiti框架校验 |
| 采购订单行-头 | dp_erp_purchase_order_line.headerId | dp_erp_purchase_order_header.id | 头表ID存在性 | PurchaseLineService.insertSelective() | 框架级外键约束 |
| 外派结算-外派项目 | pm_dispatch_project_settlement.dispatchProjectId | pm_dispatch_project_header.id | 外派项目存在性 | DispatchSettlementController.create() | 抛出异常 |

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
| D365 推送开关 | 采购订单/收货推送 | 检查 enablePushPurchaseOrder / enablePushContractAcceptanceDelivery | D365Api.pushPurchaseOrder | 开关关闭时跳过推送 |
| Token 过期检查 | D365/FP API 调用 | 通过 expiresOn/expiresIn/expireTime 判断 | D365Api.getToken / FPApi.getToken | 过期则重新获取 |

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
| pm_project_header | PMS-struts、PMS-springmvc、回访管理、售前测试、报表统计、数据同步 | struts(CRUD)、springmvc(CRUD)、回访(U-状态)、售前(R)、报表(R)、同步(U-属性) | **高** - 多模块写入 |
| pm_project_member | PMS-struts、PMS-springmvc、售前测试、数据同步 | struts(CRUD)、springmvc(CRUD)、售前(CR-售前成员)、同步(R-查询联系方式) | **高** - 多模块写入 |
| pm_project_state | PMS-struts、回访管理 | struts(CRU)、回访(U-闭环状态) | **中** - 状态字段分区更新 |
| pm_project_task | PMS-struts、PMS-springmvc | struts(CRU)、springmvc(CRUD) | **中** - 多模块写入 |
| fnd_user_info | core、所有模块 | core(CRUD)、所有模块(R-用户上下文) | **低** - 单模块写入 |
| fnd_basic_data | core、所有模块 | core(CRUD)、所有模块(R-数据字典) | **低** - 单模块写入 |
| fnd_mails | core、项目管理、售前测试、转包、维护 | core(CRUD)、各业务模块(C-发送邮件) | **中** - 多模块写入但仅新增 |
| dp_act_unify_task | PMS-activiti、PMS-springmvc、PMS-struts | activiti(CRUD)、各业务流程模块(CR-任务记录) | **中** - 多模块写入 |
| fnd_act_hi_comment | PMS-activiti、售前测试、回访管理、转包 | activiti(CR)、各业务流程模块(R) | **低** - 单模块写入 |
| dp_erp_purchase_order_header | PMS-ext-d365、PMS-struts、PMS-springmvc | d365(CRUD)、struts(R-转包查询)、springmvc(R-外派查询) | **低** - 单模块写入 |

### 4.2 共享表操作冲突分析

| 冲突场景 | 涉及模块 | 冲突类型 | 处理策略 |
|----------|----------|----------|----------|
| 项目状态更新 | PMS-struts vs 回访管理 | 并发更新pm_project_state | 回访模块仅更新closeProcessState字段，项目管理更新其他状态字段，字段级隔离 |
| 项目成员变更 | PMS-struts vs 售前测试 | 并发写入pm_project_member | 售前项目通过fromFlag区分来源，查询时按projectType过滤 |
| 闭环流程状态 | 回访管理 vs 项目管理 | 并发更新closeProcessState | 回访模块检查是否有活跃闭环任务，无活跃任务才更新 |
| 项目任务操作 | PMS-struts vs PMS-springmvc | 并发写入pm_project_task | struts 通过 *.action 访问，springmvc 通过 *.html 访问，URL 分流隔离 |
| 邮件发送 | 多模块并发 | 并发插入fnd_mails | 邮件表仅插入操作，无冲突风险 |
| 统一任务 | 多流程并发 | 并发写入dp_act_unify_task | 通过latest字段标记最新记录，历史记录保留 |
| 项目组编码生成 | 并发创建项目 | 并发读取pm_project_group最大编码 | ⚠️ FIXME：synchronized仅限单JVM，集群环境存在隐患 |
| D365 采购订单持久化 | 转包流程 vs 外派流程 | 并发写入dp_erp_purchase_order_header | 通过sourceType/sourceId区分来源，无唯一约束冲突 |

---

## 5. CRUD 操作统计

### 5.1 各模块操作权限分布

| 模块 | 表数量 | C | R | U | D | 高频操作 | 大数据量表 |
|------|--------|---|---|---|---|----------|-----------|
| core | 15 | 11 | 15 | 12 | 10 | R:用户/菜单/基础数据/邮件 | fnd_mails, tb_sys_log |
| PMS-struts（项目管理） | 23 | 20 | 23 | 18 | 11 | R:项目列表/成员/任务 C:通知 | pm_project_header, pm_project_member, pm_project_weekly_content |
| PMS-struts（售前测试） | 5 | 5 | 5 | 5 | 2 | R:售前项目列表 | pm_presales_project_header |
| PMS-struts（回访闭环） | 8 | 8 | 8 | 8 | 5 | R:评价/问卷 C:问卷结果 | pm_cl_evaluation_header |
| PMS-struts（技术公告） | 7 | 7 | 7 | 6 | 3 | C:阅读日志 R:公告列表 | prob_read_log |
| PMS-struts（项目转包） | 8 | 8 | 8 | 8 | 5 | R:转包项目列表 | pm_subcontract_project_line |
| PMS-struts（报表统计） | 1 | 1 | 1 | 0 | 0 | R:报表数据 | pm_report_line_data |
| PMS-struts（数据同步） | 7 | 7 | 7 | 7 | 0 | R:订单数据 C:同步数据 | pm_order_data_from_erp_source |
| PMS-struts（合格证维保） | 2 | 1 | 2 | 1 | 1 | R:OQC信息 | mes_oqc_info |
| PMS-springmvc（项目管理） | 4 | 4 | 4 | 4 | 4 | R:项目列表 | pm_project_header |
| PMS-springmvc（外派管理） | 4 | 4 | 4 | 4 | 4 | R:外派列表 | pm_dispatch_project_header |
| PMS-springmvc（日报通用） | 2 | 2 | 2 | 2 | 2 | R:日报列表 | pm_daily_report |
| PMS-springmvc（行业资产） | 5 | 5 | 5 | 5 | 5 | R:资产列表 | pm_industry_asset |
| PMS-springmvc（工作流业务） | 1 | 1 | 1 | 1 | 1 | R:工作流列表 | pm_workflow |
| PMS-springmvc（EHR集成） | 6 | 6 | 6 | 5 | 0 | R:员工数据 | ehr_employee |
| PMS-activiti | 26 | 20 | 26 | 14 | 14 | R:任务 C:审批意见 | act_ru_task, act_hi_taskinst, dp_act_unify_task |
| PMS-ext-d365 | 4 | 4 | 4 | 4 | 4 | R:采购订单 | dp_erp_purchase_order_header |
| pms-rules | 0 | - | - | - | - | （内存缓存） | - |
| pms-ext-fp | 2 | 2 | 2 | 2 | 1 | R:发票信息 | tb_invoice |
| pms-security | 0 | - | - | - | - | （无数据表） | - |
| 统一任务推送 | 2 | 2 | 2 | 1 | 1 | R:统一任务 C:任务记录 | dp_act_unify_task |

### 5.2 操作类型分布

```
创建(C) ████████████████████████████████████████  121个表-模块组合
读取(R) ████████████████████████████████████████████  132个表-模块组合
更新(U) ████████████████████████████████████  101个表-模块组合
删除(D) ██████████████████████████  70个表-模块组合
```

**分析结论：**
- 读取操作覆盖几乎所有表，是最普遍的操作类型
- 删除操作最为谨慎，仅 70 个表-模块组合支持删除
- PMS-activiti 模块涉及表最多（26 张），含完整的 Activiti 引擎表
- PMS-struts 项目管理子模块涉及 23 张表，业务最复杂
- pms-rules 与 pms-security 为纯工具/内存模块，不涉及数据表
- 高频操作集中在项目列表查询、用户/菜单/基础数据读取、邮件发送、通知创建、工作流任务查询
- 大数据量表集中在日志类（tb_sys_log, prob_read_log, act_hi_*）、主业务表（pm_project_header, pm_project_member）和工作流运行时表（act_ru_task）

---

## 6. 变更记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-06-24 | - | 初始版本，基于各模块文档与数据字典梳理生成，覆盖 PMS 全部 8 个模块 |
