# PMS 数据字典与枚举值文档

本文档从 PMS 源码（`MessageUtil`、Bean 类、SQL 映射等）中提取所有枚举值及含义，按业务模块分类整理。

---

## 1. projectState（项目状态）

项目主状态，存储在 `pm_project_header.projectState` 字段中。

| 枚举值 | 常量名 | 含义 | 说明 |
|---|---|---|---|
| `10` | `PROJECT_STATE_CREATING` | 待创建 | 工程管理部待创建项目 |
| `20` | `PROJECT_STATE_DENY` | 不予跟踪 | 工程管理部标记为不予跟踪 |
| `30` | `PROJECT_STATE_30` | 已创建/待指定服务经理 | 项目已创建，等待指定服务经理 |
| `31` | `PROJECT_STATE_31` | 待指派项目经理 | 服务经理已指定，等待指派项目经理 |
| `32` | `PROJECT_STATE_32` | 待制定工程计划 | 项目经理已指派，等待制定工程计划 |
| `33` | `PROJECT_STATE_33` | 回访阶段 | 售前项目进入回访阶段 |
| `34` | `PROJECT_CREATE_STATE34` | 项目经理填写项目信息 | 注意：此值实际存储在isback字段而非projectState字段 |
| `100` | `PROJECT_STATE_CLOSEDLOOP` | 项目闭环 | 项目已完成闭环 |

**状态流转**：`10(待创建)` → `30(已创建)` → `31(待指派项目经理)` → `32(待制定工程计划)` → ... → `100(项目闭环)`

---

## 2. isback（项目回退状态）

项目回退/流转标记，存储在 `pm_project_header.isback` 字段中，标识项目当前所处的审批/回退环节。

| 枚举值 | 常量名 | 含义 | 说明 |
|---|---|---|---|
| `30` | `PROJECT_CREATE_STATE30` | 工程管理部创建项目 | 待指定服务经理状态 |
| `32` | `PROJECT_CREATE_STATE32` | 服务经理指定项目经理 | 项目经理跟踪状态 |
| `34` | `PROJECT_CREATE_STATE34` | 项目经理填写项目信息 | 项目信息维护状态 |
| `36` | `PROJECT_CREATE_STATE36` | 需工程管理部同意回退 | 服务经理/项目经理申请回退至工程管理部 |
| `38` | `PROJECT_CREATE_STATE38` | 需服务经理同意回退 | 项目经理申请回退至服务经理 |
| `40` | `PROJECT_CREATE_STATE40` | 工程管理部不予跟踪 | 工程管理部确认不予跟踪 |
| `42` | `PROJECT_CREATE_STATE42` | 项目经理选择不予跟踪 | 项目经理标记不予跟踪 |
| `50` | `PROJECT_CREATE_STATE50` | 服务经理确认跟踪 | 服务经理将不予跟踪项目返回工程管理部，说明需要跟踪 |

---

## 3. memberRole（项目成员角色）

项目成员角色，存储在 `pm_project_member.memberRole` 字段中，基础数据类型编码 `03`。

| 枚举值 | 常量名 | 含义 | 说明 |
|---|---|---|---|
| `10` | `MEMBER_SALESMAN` | 销售人员 | 项目销售代表 |
| `20` | `MEMBER_SM` | 服务经理 | 项目服务经理，在项目信息中指定 |
| `30` | `MEMBER_PM` | 项目经理 | 项目项目经理，在项目信息中指定 |
| `40` | `MEMBER_PARTY` | 团队成员 | 项目团队成员 |
| `50` | `MEMBER_SERVICE_CHANNEL` | 出货代理商/服务渠道工程师 | 渠道方人员 |
| `60` | `MEMBER_CUSTOMER` | 最终客户 | 客户方联系人 |
| `70` | `MEMBER_TECH_MANMER` | 技术经理 | 项目技术负责人 |

> **注意**：服务经理（20）和项目经理（30）在项目成员下拉列表中被过滤掉，需从项目信息中直接指定。

---

## 4. orderType（订单类型）

订单数据类型，存储在订单同步相关表中。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `0` | 正常销售订单 | 标准销售订单数据 |
| `1` | 退货订单（RMA） | 退货授权订单数据 |

---

## 5. salesType（销售类型/项目订单类型）

项目订单类型，存储在 `pm_project_header.salesType` 字段中。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `01` | 正常销售 | 直销正常订单 |
| `02` | 借转销 | 借货转销售 |
| `14` | 销售类借货（总代借货） | 总代理借货项目，需按利润中心拆分订单 |

> **特殊处理**：`salesType=14` 的项目在查询订单、发货信息时需额外传入 `column001`（利润中心/办事处编码）进行数据过滤。

---

## 6. projectType（项目类型）

项目分类，存储在 `pm_project_header.projectType` 字段中，基础数据类型编码 `02`。

| 枚举值 | 常量名 | 含义 | 说明 |
|---|---|---|---|
| `10` | `PROJECT_TYPE_AFTERSALES` | 售后项目 | 标准售后服务项目 |
| `20` | `PROJECT_TYPE_PRESALES` | 售前测试项目 | 售前测试/评估项目 |

---

## 7. column010（项目类别）

项目类别划分，存储在 `pm_project_header.column010` 字段中。

| 枚举值 | 常量名 | 含义 | 说明 |
|---|---|---|---|
| `10` | `PROJECT_TYPE_NORMAL` | 普通类 | 普通服务项目 |
| `20` | `PROJECT_TYPE_ENGINEE` | 工程类 | 工程实施项目 |

> 项目类别影响立项通知邮件模板的选择：普通类使用模板 `09`，工程类使用模板 `10`。

---

## 8. projectPlanState（工程计划状态）

工程计划阶段状态，存储在 `pm_project_state.projectPlanState` 字段中，基础数据类型编码 `22`。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `40` | `PROJECT_PLAN_STATE_40` | 尚未制定计划 |
| `41` | `PROJECT_PLAN_STATE_41` | 工程启动会 |
| `42` | `PROJECT_PLAN_STATE_42` | 工程准备 |
| `43` | `PROJECT_PLAN_STATE_43` | 到货验收 |
| `44` | `PROJECT_PLAN_STATE_44` | 安装调试 |
| `45` | `PROJECT_PLAN_STATE_45` | 初验 |
| `46` | `PROJECT_PLAN_STATE_46` | 终验 |
| `47` | `PROJECT_PLAN_STATE_47` | 闭环申请 |
| `48` | `PROJECT_PLAN_STATE_48` | 项目闭环 |

---

## 9. closeProcessState（闭环流程状态）

项目闭环流程状态，存储在 `pm_project_state.closeProcessState` 字段中。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `10` | `PROJECT_CLOSE_PROCESS_STATE_10` | 项目跟踪 |
| `15` | `PROJECT_CLOSE_PROCESS_STATE_15` | 闭环申请 |
| `20` | `PROJECT_CLOSE_PROCESS_STATE_20` | 服务经理审批 |
| `30` | `PROJECT_CLOSE_PROCESS_STATE_30` | 回访 |
| `40` | `PROJECT_CLOSE_PROCESS_STATE_40` | 工程人员审核 |
| `50` | `PROJECT_CLOSE_PROCESS_STATE_50` | 项目闭环 |

**闭环流程**：`10(项目跟踪)` → `15(闭环申请)` → `20(服务经理审批)` → `30(回访)` → `40(工程人员审核)` → `50(项目闭环)`

---

## 10. executionState（项目实施状态）

项目实施状态，存储在 `pm_project_state.executionState` 字段中，基础数据类型编码 `projectExecutionState`。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `5`/`10` | 未实施 | 项目尚未开始实施 |
| `7` | 挂起 | 项目实施暂停 |
| `80` | 项目闭环 | 项目实施完成并闭环 |

> 实施状态的具体枚举值由基础数据表 `pm_basic_data` 动态配置，`80` 为代码中硬编码的闭环状态。

---

## 11. shipmentState（发货状态）

订单发货状态，存储在 `pm_project_state.shipmentState` 字段中，基础数据类型编码 `20`。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| 由基础数据表动态配置 | 发货状态 | 包含未发货、部分发货、已发货等状态 |

---

## 12. column012（实施方式）

项目实施方式，存储在 `pm_project_header.column012` 字段中，基础数据类型编码 `15`。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `0` | `IMPL_WAY_0` | 原厂直服 |
| `1` | `IMPL_WAY_1` | 原厂督导 |
| `3` | `IMPL_WAY_3` | 代理商自服 |
| `4` | — | 原厂集成 | 无对应MessageUtil常量，来自基础数据表动态配置 |
| `-1` | `IMPL_WAY_ALL` | 所有实施方式 |

> **特殊逻辑**：`column012Readonly` 字段控制实施方式是否可修改，值为 `-1` 表示可以修改，其他值（来自 SMS 系统同步）不可修改。

---

## 13. fromFlag（成员来源标记）

项目成员来源标记，存储在 `pm_project_member.fromFlag` 字段中。

| 枚举值 | 常量名 | 含义 | 说明 |
|---|---|---|---|
| `0` | — | 手动添加 | 用户在页面上手动添加的成员 |
| `1` | `FLAG_FROM_PROJECT` | 来源于项目信息 | 从项目信息中的服务经理/项目经理自动同步 |
| `2` | `FLAG_FROM_MEMBER` | 来源于成员信息 | 从成员信息模块同步 |

---

## 14. weeklyState（周报状态）

项目周报状态，存储在 `pm_project_weekly.weeklyState` 字段中。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `0` | `WEEKLY_STATE_RAFT` | 草稿 | ⚠️ RAFT为源码中DRAFT的拼写错误，全项目均使用RAFT |
| `1` | `WEEKLY_STATE_SUBMIT` | 已提交 |
| `-1` | `WEEKLY_STATE_ALL` | 全部（查询条件） |

---

## 15. weeklyContentType（周报内容类型）

周报内容分类，存储在 `pm_weekly_content.optionType` 字段中。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `1` | `OPTION_TYPE_WORK` | 本周工作内容 |
| `2` | `OPTION_TYPE_RISK` | 风险与问题 |
| `3` | `OPTION_TYPE_HELP` | 需要协助 |
| `4` | `OPTION_TYPE_PROPGRESS` | 进度说明 |
| `5` | `OPTION_TYPE_PLAN` | 下周计划 |
| `6` | `OPTION_TYPE_FILE` | 附件 |
| `7` | `OPTION_TYPE_MAIL` | 邮件抄送 |

---

## 16. 角色定义（roleId）

系统角色 ID，存储在用户-角色关联表中。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `1` | `ROLE_ADMIN` | 系统管理员 |
| `3` | `ROLE_COMMON` | 普通用户 |
| `5` | `ROLE_PROJECT_ADMIN` | 项目管理员 |
| `6` | `ROLE_PROJECT_VIEWER` | 项目查阅人员 |
| `9` | `ROLE_AREA_LEADER` | 办事处主任 |
| `10` | `ROLE_ENGINEEMANAGER_LEADER` | 工程管理部主管 |
| `11` | `ROLE_SERVICEMANAGER` | 服务经理 |
| `12` | `ROLE_PROGRAMMANAGER` | 项目经理 |
| `13` | `ROLE_ENGINEEMANAGER` | 工程管理部 |
| `14` | `ROLE_CALLBACKPER` | 回访人员 |
| `15` | `ROLE_SALESPEOPLE` | 销售代表 |
| `16` | `ROLE_FINANCIAL_STAFF` | 财务人员 |
| `17` | `ROLE_PRESALES_STAFF` | 售前专员 |
| `18` | `ROLE_PROB_ADMIN` | 技术公告管理员 |
| `19` | `ROLE_PROB_SUPPORTER` | 技术支持人员 |
| `20` | `ROLE_PROB_RD` | 研发人员 |
| `21` | `ROLE_WARRANTY_CALLBACKER` | 维保回访人员 |
| `22` | `ROLE_COMPONENT_ADMIN` | 产品组件管理人员 |

---

## 17. 基础数据类型编码

基础数据通过 `pm_basic_data` 表统一管理，按 `dataTypeCode` 分类。

| 编码 | 常量名 | 含义 |
|---|---|---|
| `02` | — | 项目分类 |
| `03` | `BASIC_DATA_MEMBER_ROLE` | 项目成员角色 |
| `05` | `BASIC_DATA_PRORANK` | 项目类型划分 |
| `06` | `BASIC_DATA_PROTYPE` | 项目类别 |
| `09` | `BASIC_DATA_PRJ_PHASE` | 项目阶段划分 |
| `10` | `BASIC_DATA_NAV_TAB` | 项目维护界面选项卡 |
| `12` | `BASIC_DATA_NAV_WORK_TAB` | 工作台页面选项卡 |
| `15` | `BASIC_DATA_SERVICE_TYPE` | 项目实施方式划分 |
| `16` | `BASIC_DATA_NAV_MERGE_TAB` | 拆分页面选项卡 |
| `18` | `BASIC_DATA_NAV_DATA_TAB` | 数据统计界面选项卡 |
| `20` | `BASIC_DATA_DELIVERSTATE` | 订单发货状态 |
| `22` | `BASIC_DATA_ENGINEERSTATE` | 项目工程状态 |
| `24` | `BASIC_DATA_PORJECT_TIME` | 项目查询条件-时间点集合 |
| `29` | `BASIC_DATA_PROJECT_TYPE` | 系统项目分类 |
| `subcontractType` | `BASIC_DATA_SUBCONTRACT_TYPE` | 项目转包分类 |
| `subcontractState` | `BASIC_DATA_SUBCONTRACT_STATE` | 项目转包状态 |
| `subcontractDeliverState` | `BASIC_DATA_SUBCONTRACT_DELIVER_STATE` | 项目转包交付件类型 |
| `subcontractWorkFlowState` | `BASIC_DATA_SUBCONTRACT_WORKFLOW_STATE` | 项目转包审批状态 |
| `maintenanceType` | — | 项目维护类型 |
| `projectExecutionState` | — | 项目实施状态 |
| `projectCloseProcessState` | — | 项目闭环流程状态 |
| `majorProjectLevel` | — | 重大项目级别 |

---

## 18. 通知模板编码

邮件/系统通知模板编码，存储在通知模板表中。

| 编码 | 常量名 | 含义 |
|---|---|---|
| `01` | `NOTIFICATION_CODE_WEEKLY_SUBMIT` | 周报提交邮件 |
| `02` | `NOTIFICATION_CODE_WEEKLY_PISHI` | 周报批复邮件 |
| `03` | `NOTIFICATION_CODE_INSTRUCTION` | 项目留言邮件 |
| `04` | `NOTIFICATION_CODE_DENY_PRJ` | 项目不予跟踪邮件 |
| `05` | `NOTIFICATION_CODE_CONTINUE_PRJ` | 项目继续跟踪邮件 |
| `06` | `NOTIFICATION_CODE_SURE_PRJ` | 项目确认继续跟踪 |
| `07` | `NOTIFICATION_CODE_DENY_PRJ_42` | 项目经理选择不予跟踪邮件 |
| `08` | `NOTIFICATION_CODE_DENY_PRJ_SURE` | 工程管理确认不予跟踪邮件 |
| `09` | `NOTIFICATION_CODE_CREATEPRJ_NORMAL` | 项目立项通知-普通类 |
| `10` | `NOTIFICATION_CODE_CREATEPRJ_ENGINEE` | 项目立项通知-工程类 |
| `11` | `NOTIFICATION_CODE_PMNOMINATE_NORMAL` | 项目经理任命通知-普通类 |
| `12` | `NOTIFICATION_CODE_PMNOMINATE_ENGINEE` | 项目经理任命通知-工程类 |
| `13` | `NOTIFICATION_CODE_PROJECT_VALIDATE` | 项目组成立 |
| `14` | `NOTIFICATION_CODE_PROJECT_BACK` | 回退邮件 |
| `29` | `NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP` | 项目计划到期提醒 |
| `30` | `NOTIFICATION_CODE_PROJECT_UPLOAD_DELIVER` | 项目计划上传交付件提醒 |
| `50` | `NOTIFICATION_CODE_PROB` | 技术公告邮件 |
| `101` | `NOTIFICATION_CODE_101` | 项目创建 |
| `102` | `NOTIFICATION_CODE_102` | 不予跟踪 |
| `103` | `NOTIFICATION_CODE_103` | 指定服务经理 |
| `104` | `NOTIFICATION_CODE_104` | 指定项目经理 |
| `105` | `NOTIFICATION_CODE_105` | 需要跟踪 |
| `106` | `NOTIFICATION_CODE_106` | 确认跟踪 |
| `107` | `NOTIFICATION_CODE_107` | 项目回退 |
| `108` | `NOTIFICATION_CODE_108` | 同意回退 |
| `109` | `NOTIFICATION_CODE_109` | 项目经理选择不予跟踪 |
| `110` | `NOTIFICATION_CODE_110` | 工程管理部确认不予跟踪 |
| `111` | `NOTIFICATION_CODE_111` | 上传交付件 |
| `112` | `NOTIFICATION_CODE_112` | 制定工程计划 |
| `113` | `NOTIFICATION_CODE_113` | 增加项目干系人 |
| `114` | `NOTIFICATION_CODE_114` | 增加设备安装地址 |
| `115` | `NOTIFICATION_CODE_115` | 修改工程计划 |
| `116` | `NOTIFICATION_CODE_116` | 修改项目干系人 |
| `117` | `NOTIFICATION_CODE_117` | 删除工程交付件 |
| `118` | `NOTIFICATION_CODE_118` | 提交工程周报 |
| `119` | `NOTIFICATION_CODE_119` | 项目闭环 |
| `120` | `NOTIFICATION_CODE_120` | 项目设备转移 |

---

## 19. dataState（数据状态）

项目成员等记录的数据状态标记，用于软删除。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `0` | 正常 | 记录处于有效状态 |
| `1` | 已删除 | 记录已被逻辑删除 |

---

## 20. source（数据来源）

数据来源标识，用于数据同步日志和刷新记录。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `SAP` | SAP 系统 | 旧版 ERP 数据源（已废弃） |
| `ERP` | ERP 系统 | 新版 ERP/D365 数据源 |
| `D365` | Dynamics 365 | 微软 Dynamics 365 |
| `SMS` | 销售管理系统 | 销售管理平台 |
| `CRM` | 客户关系管理 | CRM 系统 |
| `OA` | 办公自动化 | OA 系统 |
| `EHR` | 电子人力资源 | EHR 人员信息系统 |
| `SSE` | 服务支撑系统 | SSE 外部系统 |
| `ITR` | IT 服务请求 | ITR 工单系统 |

---

## 21. warrantyStatus（维保状态）

项目维保状态，存储在项目维保关联表中。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| 由基础数据表动态配置 | 维保状态 | 包含在保、过保等状态 |

> 维保相关字段还包括 `warrantyGrade`（维保级别）和 `wafService`（WAF 服务），均由基础数据表动态配置。

---

## 22. 批示类型

项目批示/回复类型。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `0` | `INSTRUSTION` | 批示 |
| `1` | `FEEDBACK` | 回复 |

---

## 23. 计划可见标识

工程计划节点的可见性控制。

| 枚举值 | 常量名 | 含义 |
|---|---|---|
| `1` | `TASK_SHOW` | 可见 |
| `2` | `TASK_HIDE` | 隐藏 |

---

## 24. column011（项目签约类型）

项目签约类型，存储在 `pm_project_header.column011` 字段中。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `10` | 直签类 | 运营商直签项目 |
| `20` | 非直签类 | 非直签项目 |

> **特殊逻辑**：当 `column004=运营商市场部` 且 `column011=10` 时，项目为"运营商直签项目"，显示回访流程选项卡。

---

## 25. dataRefreshState（数据刷新状态）

数据同步日志中的刷新状态。

| 枚举值 | 含义 | 说明 |
|---|---|---|
| `1` | 同步成功 | 数据刷新完成 |
| 非 1 | 同步失败 | 数据刷新异常，记录异常堆栈 |

---

## 26. softVersion datastate（软件版本数据状态）

软件版本变更标记，用于技术公告中的版本影响范围判定。

| 标记 | 含义 | 说明 |
|---|---|---|
| 版本号匹配 | 受影响 | 设备软件版本在公告影响范围内 |
| 版本号不匹配 | 不受影响 | 设备软件版本不在影响范围内 |

> 软件版本解析由 `SoftVersionStrategy` 和 `NewSoftVersionStrategy` 两种策略实现，通过 `SoftVersionParserFactory` 工厂类自动选择匹配的解析器。
