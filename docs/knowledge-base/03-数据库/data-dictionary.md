# dppms_d365 数据库完整数据字典

> 基于生产环境 MySQL 数据库 dppms_d365 实际结构生成
> 引擎: MySQL 8.0.16 | 核心业务表: 151 张 | 字段总数: 2136 | 索引总数: 639

---

## addressee_info

**索引列表:**
- PRIMARY: 主键 (addre_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| addre_id | int(11) | 否 |  | PRI | auto_increment | ID |
| username | varchar(25) | 否 |  |  |  | 关联用户账号 |
| addre_name | varchar(64) | 是 |  |  |  | 收件人姓名 |
| addre_tel | varchar(64) | 是 |  |  |  | 收件人电话 |
| addre_mail | varchar(64) | 是 |  |  |  | 收件人邮箱 |
| addr | varchar(1024) | 是 |  |  |  | 地址/where |
| zip_code | varchar(10) | 是 |  |  |  | 邮编 |
| company | varchar(64) | 是 |  |  |  | 公司 |
| depName | varchar(25) | 是 |  |  |  | 部门 |
| remark | text | 是 |  |  |  | 备注 |
| state | int(11) | 是 | 1 |  |  | 状态（生效或失效） |

---

## agent_info

**索引列表:**
- index_id: 普通 (id)
- PRIMARY: 主键 (agent_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| agent_id | int(10) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| id | varchar(16) | 否 |  | MUL |  | 业务含义待确认 |
| name | varchar(64) | 否 |  |  |  | 业务含义待确认 |
| type | int(8) | 否 |  |  |  | 业务含义待确认 |
| level | varchar(64) | 是 |  |  |  | 业务含义待确认 |
| enable | int(8) | 否 | 1 |  |  | 业务含义待确认 |
| agent_version | int(8) | 否 | 0 |  |  | 业务含义待确认 |

---

## app_accessory_info

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(25) | 是 |  |  |  | 流水号 |
| accessoryName | varchar(255) | 是 |  |  |  | 附件名称 |
| uploader | varchar(10) | 是 |  |  |  | 上传者 |
| uploadTime | datetime | 是 |  |  |  | 上传时间 |
| accessoryType | int(11) | 是 |  |  |  | 附件类型  1 发货信息  -1 坏件返回信息 |
| accessoryPath | varchar(100) | 是 |  |  |  | 上传路径 |
| data_creater | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| data_creatime | datetime | 是 |  |  |  | 业务含义待确认 |
| data_updater | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| data_updatime | datetime | 是 |  |  |  | 业务含义待确认 |
| data_from | datetime | 是 |  |  |  | 业务含义待确认 |
| data_to | datetime | 是 |  |  |  | 业务含义待确认 |

---

## app_comment

**索引列表:**
- PRIMARY: 主键 (id)
- sheetID: 普通 (sheetID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(10) | 是 |  | MUL |  | 单据代码 |
| is_pass | varchar(2) | 是 |  |  |  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  |
| opinion | text | 是 |  |  |  | 意见 |
| approve_time | datetime | 是 |  |  |  | 审批时间 |
| approver | varchar(10) | 是 |  |  |  | 审批人 |
| state | char(1) | 是 |  |  |  | (1:为最新审批结果；0：为旧审批结果) |
| take_place | varchar(15) | 是 | 0 |  |  | 0:未选择 1:供应链 2：库存 |
| isUnion | int(11) | 是 |  |  |  | 是否联合供应链发货 |

---

## app_spare_part

**索引列表:**
- PRIMARY: 主键 (id)
- tx_id: 唯一 (tx_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| tx_id | int(11) | 是 |  | UNI |  | 业务含义待确认 |
| action_time | datetime | 是 |  |  |  | 操作时间 |
| isOK | char(1) | 是 |  |  |  | 是否核销(是否核销，0为未核销，1为核销) |
| hexiao_time | datetime | 是 |  |  |  | 核销时间 |
| hexiao_remark | text | 是 |  |  |  | 核销说明 |
| isNew | char(1) | 是 |  |  |  | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) |
| contract_sub_type | char(1) | 是 |  |  |  | 发货类型（0为RMA ,1为项目保障,2为库存,null:转移申请 3:借用申请) |
| item_code | varchar(25) | 是 |  |  |  | 物料号 |
| item_name | varchar(255) | 是 |  |  |  | 物料名称 |
| tain_process | varchar(255) | 是 |  |  |  | 检测报告 |
| isReceive | char(1) | 是 | 0 |  |  | 0：已发货待确认接收 1：已确认接货 2：待发货确认 |

---

## back_type

**索引列表:**
- back_where_index: 普通 (back)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 主键 |
| back | varchar(10) | 否 |  | MUL |  | 业务含义待确认 |
| back_type | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| back_state | varchar(200) | 是 |  |  |  | 业务含义待确认 |
| remark | text | 是 |  |  |  | 备注 |
| status | int(11) | 是 | 1 |  |  | 有效状态0失效 1有效 |
| updateTime | datetime | 是 |  |  |  | 更新时间 |

---

## bar

**索引列表:**
- PRIMARY: 主键 (id)
- spare_id: 普通 (spare_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(10) | 否 |  | PRI | auto_increment | 主键 |
| spare_id | int(10) | 否 |  | MUL |  | pps的主键 |
| bar_code | varchar(50) | 是 |  |  |  | 设备编码 |
| bar_model | varchar(1000) | 是 |  |  |  | 设备型号 |
| bar_num | varchar(50) | 是 |  |  |  | 数量 |
| remark | text | 是 |  |  |  | 备注 |
| serial_number | varchar(50) | 是 |  |  |  | 序列号 |
| spare_code | varchar(15) | 是 |  |  |  | 业务含义待确认 |

---

## brw_app_info

**索引列表:**
- PRIMARY: 主键 (id)
- sheetID: 唯一 (sheetID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(15) | 是 |  | UNI |  | 单据代码 |
| applicant | varchar(25) | 是 |  |  |  | 申请人 |
| app_time | datetime | 是 |  |  |  | 申请时间 |
| app_dptNo | varchar(10) | 是 |  |  |  | 申请办事处名称 |
| contractNo | varchar(25) | 是 |  |  |  | 合同号 |
| prt_name | varchar(255) | 是 |  |  |  | 项目名称 |
| app_reason | text | 是 |  |  |  | 申请原因 |
| duty_person | varchar(10) | 是 |  |  |  | 负责人 |
| start_use_time | datetime | 是 |  |  |  | 开始使用时间 |
| kept_place | varchar(10) | 是 |  |  |  | 备件存放地 |
| promise_returntime | datetime | 是 |  |  |  | 承诺备件归还时间 |
| extend_returntime | datetime | 是 |  |  |  | 延长归还时间 |
| demand_type | varchar(25) | 是 |  |  |  | 需求类型（维护在sys_state_or_type） |
| trade_classify | varchar(100) | 是 |  |  |  | 行业分类（手动填写） |
| signing_state | char(1) | 是 |  |  |  | 签单状态（0：已签单；1：未签单） 废弃字段 |
| app_type | char(1) | 是 |  |  |  | 申请类型（0：借用申请；1：转移申请;2:历史数据） |
| addre_id | int(11) | 是 |  |  |  | 关联收件人表ID |
| his_addre | varchar(64) | 是 |  |  |  | 收件人 |
| his_addre_tel | varchar(64) | 是 |  |  |  | 联系电话 |
| his_addr | varchar(1024) | 是 |  |  |  | 地址/where |
| his_zipCode | varchar(25) | 是 |  |  |  | 邮编 |
| ischange_duty | char(1) | 是 |  |  |  | 是否转移责任人（0:转移；1：不转移） |
| isQuit | char(1) | 是 |  |  |  | 是否为离职原因导致责任人变更，0：否，1：是 |
| change_type | char(1) | 是 |  |  |  | 转移类型 |
| remark | text | 是 |  |  |  | 备注 |
| data_state | char(1) | 是 |  |  |  | 数据状态（0：历史；1：最新） |
| isSend | char(1) | 是 |  |  |  | 是否发货(0:待发货确认 1：待收货确认) |
| isReceive | char(1) | 是 | 0 |  |  | 是否收货(1:已接受 0：未接受) |
| beforeChange_sheetID | varchar(15) | 是 |  |  |  | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 |
| change_time | datetime | 是 |  |  |  | 备件转移时间 |
| version_no | int(11) | 是 | 0 |  |  | 库存发货配置的版本号 |

---

## brw_spare_info

**索引列表:**
- PRIMARY: 主键 (id)
- sheetID: 普通 (sheetID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(10) | 是 |  | MUL |  | 单据代码 |
| item_code | varchar(10) | 是 |  |  |  | 物料编码 |
| item_name | varchar(255) | 是 |  |  |  | 物料名称 |
| quantity | int(11) | 是 |  |  |  | 数量 |
| remark | text | 是 |  |  |  | 备注 |
| state | char(1) | 是 | 1 |  |  | 状态（0：历史数据；1：有效数据） |

---

## data_field_relation

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| dataName | varchar(255) | 否 |  |  |  | 数据名 |
| dataType | varchar(255) | 否 |  |  |  | 数据类型 |
| dataId | int(11) | 是 | 0 |  |  | 数据实例ID |
| field | varchar(128) | 否 |  |  |  | 字段 |
| alias | varchar(128) | 是 |  |  |  | 字段别名 |
| name | varchar(128) | 否 |  |  |  | 字段名 |
| title | varchar(255) | 是 |  |  |  | 字段标题 |
| titleKey | varchar(255) | 是 |  |  |  | 字段标题Key |
| cssId | varchar(255) | 是 |  |  |  | 字段CSS id |
| cssClass | varchar(255) | 是 |  |  |  | 字段CSS class |
| cssStyle | varchar(255) | 是 |  |  |  | 字段CSS style |
| type | varchar(255) | 是 |  |  |  | 字段类型 |
| render | varchar(4096) | 是 |  |  |  | 字段处理 |
| sort | int(11) | 是 | 0 |  |  | 排序 |
| orderable | bit(1) | 是 | b'1' |  |  | 允许排序 |
| searchable | bit(1) | 是 | b'0' |  |  | 允许搜索 |
| visible | bit(1) | 是 | b'1' |  |  | 允许可见 |
| required | bit(1) | 是 | b'0' |  |  | 必填 |
| readonly | bit(1) | 是 | b'0' |  |  | 只读 |
| disabled | bit(1) | 是 | b'0' |  |  | 组件失效 |
| extData | varchar(8192) | 是 |  |  |  | 外部数据 |
| extKey | varchar(255) | 是 |  |  |  | 外部数据key |
| extValue | varchar(255) | 是 |  |  |  | 外部数据value |
| media | varchar(255) | 是 |  |  |  | 传播媒介 |
| clazzName | varchar(255) | 是 |  |  |  | 类名 |
| superData | varchar(255) | 是 |  |  |  | 父类dataName |
| status | int(1) | 是 | 1 |  |  | 状态 |
| compId | int(11) | 是 |  |  |  | 公司ID |
| isSystemField | bit(1) | 是 | b'1' |  |  | 是否为系统字段 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## department

**索引列表:**
- ocrCode: 普通 (ocrCode)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| ocrCode | varchar(25) | 否 |  | MUL |  | 业务含义待确认 |
| ocrName | varchar(25) | 否 |  |  |  | 业务含义待确认 |
| isparam | int(11) | 是 |  |  |  | 业务含义待确认 |

---

## dp_act_unify_task

**索引列表:**
- originTaskId: 普通 (originTaskId)
- PRIMARY: 主键 (id)
- procInstId: 普通 (procInstId)
- taskId: 普通 (taskId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| taskId | varchar(64) | 否 |  | MUL |  | 统一待办任务Id |
| originTaskId | varchar(32) | 否 |  | MUL |  | Activiti源TaskId |
| procInstId | varchar(64) | 否 |  | MUL |  | 流程实例ID |
| processKey | varchar(255) | 否 |  |  |  | 流程定义key |
| taskKey | varchar(255) | 否 |  |  |  | 任务Key |
| taskName | varchar(255) | 是 |  |  |  | 任务名 |
| eventType | varchar(255) | 否 |  |  |  | 事件类型 |
| title | varchar(255) | 是 |  |  |  | 任务标题 |
| assignee | varchar(255) | 否 |  |  |  | 办理人 |
| formUrl | varchar(255) | 是 |  |  |  | 待办链接地址 |
| beginTime | datetime | 是 |  |  |  | 开始时间 |
| endTime | datetime | 是 |  |  |  | 结束时间 |
| dueTime | datetime | 是 |  |  |  | 过期时间 |
| state | varchar(25) | 是 |  |  |  | 办理状态 |
| subState | varchar(25) | 是 |  |  |  | 办理子状态 |
| success | bit(1) | 否 | b'0' |  |  | 推送结果 |
| message | varchar(255) | 是 |  |  |  | 推送消息 |
| latest | bit(1) | 否 | b'1' |  |  | 是否最新 |
| pushSender | varchar(255) | 是 |  |  |  | 推送发送实体类 |
| pushData | varchar(4096) | 是 |  |  |  | 推送JSON内容 |
| createBy | varchar(45) | 否 |  |  |  | 业务含义待确认 |
| createTime | timestamp | 否 | CURRENT_TIMESTAMP |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |
| updateBy | varchar(45) | 否 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## dp_erp_purchase_order_header

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sourceType | varchar(25) | 是 |  |  |  | 源数据类型 |
| sourceId | int(11) | 是 |  |  |  | 源数据ID |
| purchPoolId | varchar(25) | 是 |  |  |  | 采购订单池 |
| purchId | varchar(25) | 是 |  |  |  | 采购订单号 |
| vendAccount | varchar(25) | 是 |  |  |  | 供应商账号 |
| purchName | varchar(255) | 是 |  |  |  | 采购事项 |
| purContract | varchar(25) | 是 |  |  |  | 采购合同号 |
| salesContract | varchar(2048) | 是 |  |  |  | 销售合同号 |
| contractAmount | varchar(25) | 是 |  |  |  | 总金额 |
| workerPurchPlacer | varchar(25) | 是 |  |  |  | 订货人 |
| applicant | varchar(25) | 是 |  |  |  | 申请人 |
| inventLocationId | varchar(25) | 是 |  |  |  | 仓库 |
| deliveryDate | date | 是 |  |  |  | 交货日期 |
| dlvMode | varchar(25) | 是 |  |  |  | 交货模式 |
| dlvTerm | varchar(25) | 是 |  |  |  | 交货条款 |
| payment | varchar(255) | 是 |  |  |  | 付款条款 |
| paymMode | varchar(25) | 是 |  |  |  | 付款方式 |
| remark | varchar(4096) | 是 |  |  |  | 整单备注 |
| otherSysNum | varchar(25) | 是 |  |  |  | 外部系统编号 |
| projectName | varchar(255) | 是 |  |  |  | 项目名称 |
| projectProgress | varchar(25) | 是 |  |  |  | 项目进度 |
| subcontractType | varchar(25) | 是 |  |  |  | 转包类型 |
| subcontStartDate | varchar(25) | 是 |  |  |  | 转包开始日期 |
| subcontEndDate | varchar(25) | 是 |  |  |  | 转包结束日期 |
| dataAreaId | varchar(25) | 是 |  |  |  | 账套 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## dp_erp_purchase_order_line

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| headerId | int(11) | 是 |  |  |  | 采购订单HeaderId |
| purchId | varchar(25) | 是 |  |  |  | 采购订单号 |
| lineNum | varchar(25) | 是 |  |  |  | 采购订单行号（可指定） |
| itemId | varchar(25) | 是 |  |  |  | 物料编码 |
| purchQty | decimal(25,2) | 是 |  |  |  | 采购数量 |
| purchPrice | decimal(25,2) | 是 |  |  |  | 采购价 |
| taxItemGroup | varchar(25) | 是 |  |  |  | 税收组 |
| inventSerialId | varchar(25) | 是 |  |  |  | 厂商型号（复用D365序列号字段） |
| inventSiteId | varchar(25) | 是 |  |  |  | 站点 |
| inventLocationId | varchar(25) | 是 |  |  |  | 仓库 |
| wmsLocationId | varchar(25) | 是 |  |  |  | 库位 |
| inventTransId | varchar(25) | 是 |  |  |  | 批次号 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处 |
| deliveryDate | date | 是 |  |  |  | 交货日期 |
| remark | varchar(4096) | 是 |  |  |  | 行备注 |
| multiDimID | varchar(25) | 是 |  |  |  | 行多维度ID |
| investmentProject | varchar(255) | 是 |  |  |  | 募投项目 |
| dimBankAccount | varchar(25) | 是 |  |  |  | 维度-银行账户 |
| dimCustomer | varchar(25) | 是 |  |  |  | 维度-客户 |
| dimVendor | varchar(25) | 是 |  |  |  | 维度-供应商 |
| dimEmployee | varchar(25) | 是 |  |  |  | 维度-员工 |
| dimContract | varchar(25) | 是 |  |  |  | 维度-合同号 |
| dimDepartment | varchar(25) | 是 |  |  |  | 维度-部门 |
| dimBU | varchar(25) | 是 |  |  |  | 维度-BU |
| dimProductLine | varchar(25) | 是 |  |  |  | 维度-产品线 |
| dimTerritory | varchar(25) | 是 |  |  |  | 维度-区域 |
| dimIndustry | varchar(25) | 是 |  |  |  | 维度-行业 |
| dimMultiDimID | varchar(25) | 是 |  |  |  | 维度-多维度ID |
| dataAreaId | varchar(25) | 是 |  |  |  | 账套 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## dp_erp_purchase_receipt_header

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sourceOrderType | varchar(25) | 是 |  |  |  | 订单源数据类型（Subcontract,Dispatch） |
| sourceOrderId | int(11) | 是 |  |  |  | 订单源数据ID |
| sourceReceiptType | varchar(25) | 是 |  |  |  | 订单源收货类型（SubcontractPayment, DispatchSettlement） |
| sourceReceiptId | int(11) | 是 |  |  |  | 订单源收货ID |
| purchId | varchar(25) | 是 |  |  |  | 采购订单号 |
| deliveryDate | date | 是 |  |  |  | 交货日期 |
| documentDate | date | 是 |  |  |  | 业务含义待确认 |
| packingSlipId | varchar(512) | 是 |  |  |  | 采购收货单号 |
| packingSlipRemark | varchar(1024) | 是 |  |  |  | 采购收货备注 |
| projectProgress | varchar(1024) | 是 |  |  |  | 项目进度 |
| dataAreaId | varchar(1024) | 是 |  |  |  | 账套 |
| customInfo | json | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## dp_erp_purchase_receipt_line

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| receiptId | int(11) | 是 |  |  |  | 采购订单收货ID |
| purchId | varchar(25) | 是 |  |  |  | 采购订单号 |
| inventSiteId | varchar(25) | 是 |  |  |  | 站点 |
| inventLocationId | varchar(25) | 是 |  |  |  | 仓库 |
| wmsLocationId | varchar(25) | 是 |  |  |  | 库位 |
| inventTransId | varchar(25) | 是 |  |  |  | 批次号 |
| lineNum | varchar(25) | 是 |  |  |  | 采购订单行号（与批次号二选一，有批次号按批次号收货） |
| qty | decimal(25,2) | 是 |  |  |  | 收货数量 |
| price | decimal(25,2) | 是 |  |  |  | 收货单价 |
| amount | decimal(25,2) | 是 |  |  |  | 收货金额 |
| dataAreaId | varchar(25) | 是 |  |  |  | 账套 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## ehr_company

**索引列表:**
- adminID: 普通 (adminID)
- PRIMARY: 主键 (compID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| compID | int(11) | 否 |  | PRI |  | 公司ID，关联表外键 |
| compCode | varchar(10) | 是 |  |  |  | 公司编号 |
| compName | varchar(100) | 是 |  |  |  | 公司名称 |
| compAbbr | varchar(100) | 是 |  |  |  | 公司简称 |
| adminID | int(11) | 是 |  | MUL |  | 上级ID |
| compGrade | int(11) | 是 |  |  |  | 公司级别 |
| compType | int(11) | 是 |  |  |  | 公司类别 |
| compArea | int(11) | 是 |  |  |  | 业务含义待确认 |
| effectDate | datetime | 是 |  |  |  | 成立时间 |
| lawyer | varchar(50) | 是 |  |  |  | 法人 |
| address | varchar(200) | 是 |  |  |  | 地址 |
| regAddress | varchar(200) | 是 |  |  |  | 注册地址 |
| tel | varchar(50) | 是 |  |  |  | 电话 |
| fax | varchar(50) | 是 |  |  |  | 传真 |
| postCode | varchar(50) | 是 |  |  |  | 邮编 |
| webSite | varchar(100) | 是 |  |  |  | 网站 |
| isDisabled | bit(1) | 是 |  |  |  | 失效状态 |
| disabledDate | datetime | 是 |  |  |  | 失效时间 |
| remark | varchar(500) | 是 |  |  |  | 备注 |

---

## ehr_department

**索引列表:**
- adminID: 普通 (adminID)
- compID: 普通 (compID)
- director: 普通 (director)
- director2: 普通 (director2)
- PRIMARY: 主键 (depID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| depID | int(11) | 否 |  | PRI |  | 部门ID，关联外键 |
| depCode | varchar(20) | 是 |  |  |  | 部门编码 |
| depName | varchar(100) | 是 |  |  |  | 部门名称 |
| depAbbr | varchar(100) | 是 |  |  |  | 部门简称 |
| compID | int(11) | 是 |  | MUL |  | 公司ID，外键 |
| adminID | int(11) | 是 |  | MUL |  | 上级ID |
| depGrade | int(11) | 是 |  |  |  | 部门级别 |
| depType | int(11) | 是 |  |  |  | 部门类型 |
| depProperty | int(11) | 是 |  |  |  | 部门属性 |
| depCost | int(11) | 是 |  |  |  | 存在部门内分级计数用 |
| director | int(11) | 是 |  | MUL |  | 主管 |
| director2 | int(11) | 是 |  | MUL |  | 分管领导 |
| depEmp | int(11) | 是 |  |  |  | 业务含义待确认 |
| depNum | int(11) | 是 |  |  |  | 业务含义待确认 |
| effectDate | datetime | 是 |  |  |  | 生效时间 |
| xOrder | varchar(20) | 是 |  |  |  | 排序 |
| isDisabled | bit(1) | 是 |  |  |  | 失效状态 |
| disabledDate | datetime | 是 |  |  |  | 失效时间 |
| remark | varchar(500) | 是 |  |  |  | 备注 |
| depCustom1 | int(11) | 是 |  |  |  | 保留字段1 |
| depCustom2 | int(11) | 是 |  |  |  | 保留字段2、部门秘书 |
| depCustom3 | int(11) | 是 |  |  |  | 保留字段3 |
| depCustom4 | int(11) | 是 |  |  |  | 保留字段4 |
| depCustom5 | int(11) | 是 |  |  |  | 保留字段5 |

---

## ehr_emp_power

**索引列表:**
- empID: 唯一 (empID)
- PRIMARY: 主键 (id)
- workNo: 普通 (workNo)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| empID | int(11) | 否 |  | UNI |  | empID |
| workNo | varchar(25) | 否 |  | MUL |  | 工号 |
| compID | int(11) | 否 |  |  |  | 公司id |
| depIDs | varchar(4096) | 否 |  |  |  | 从ehr同步数据生成的部门权限，固定的 |
| extraDepIDs | varchar(4096) | 否 |  |  |  | 绩效管理附加的部门权限 |
| adminDepIDs | varchar(4096) | 否 |  |  |  | 绩效考核管理的部门 |
| empIDs | varchar(4096) | 否 |  |  |  | 从ehr同步数据生成的下属权限，固定的 |
| extraEmpIDs | varchar(4096) | 否 |  |  |  | 绩效管理附加的下属权限 |
| state | bit(1) | 否 | b'1' |  |  | 是否生效状态 |
| createBy | varchar(25) | 否 |  |  |  | 业务含义待确认 |
| createTime | datetime | 否 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## ehr_employee

**索引列表:**
- compID: 普通 (compID)
- depID: 普通 (depID)
- jobID: 普通 (jobID)
- PRIMARY: 主键 (empID)
- reportTo: 普通 (reportTo)
- wfreportTo: 普通 (wfreportTo)
- workNo: 普通 (workNo)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| empID | int(11) | 否 |  | PRI |  | 员工ID，外键 |
| workNo | varchar(100) | 否 |  | MUL |  | 工号 |
| name | varchar(200) | 是 |  |  |  | 姓名 |
| eName | varchar(200) | 是 |  |  |  | 英文名 |
| compID | int(11) | 否 |  | MUL |  | 公司ID |
| depID | int(11) | 否 |  | MUL |  | 部门ID |
| jobID | int(11) | 否 |  | MUL |  | 岗位ID |
| reportTo | int(11) | 是 |  | MUL |  | 直接上级 |
| wfreportTo | int(11) | 是 |  | MUL |  | 职能上级 |
| empStatus | int(11) | 否 |  |  |  | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | 是 |  |  |  | 岗位状态 |
| empType | int(11) | 是 |  |  |  | 聘用类型：1：正式，3：实习生 |
| joinDate | datetime | 是 |  |  |  | 加入公司日期 |
| workBeginDate | datetime | 是 |  |  |  | 工作开始日期 |
| jobBeginDate | datetime | 是 |  |  |  | 加入公司日期（未知） |
| pracBeginDate | datetime | 是 |  |  |  | 实习开始时间 |
| pracEndDate | datetime | 是 |  |  |  | 实习结束时间 |
| probBeginDate | datetime | 是 |  |  |  | 业务含义待确认 |
| probEndDate | datetime | 是 |  |  |  | 业务含义待确认 |
| leaveDate | datetime | 是 |  |  |  | 离职时间 |
| gender | int(11) | 是 |  |  |  | 性别：1：男，2：女 |
| email | varchar(500) | 是 |  |  |  | 邮箱 |
| mobile | varchar(50) | 是 |  |  |  | 手机 |
| officePhone | varchar(50) | 是 |  |  |  | 座机 |
| remark | varchar(100) | 是 |  |  |  | 备注 |
| disabled | int(11) | 是 | 0 |  |  | 失效 |
| empCustom1 | int(11) | 是 |  |  |  | 预留字段1 |
| empCustom2 | int(11) | 是 |  |  |  | 预留字段2 |
| empCustom3 | int(11) | 是 |  |  |  | 预留字段3 |
| empCustom4 | varchar(50) | 是 |  |  |  | 预留字段4 |
| empCustom5 | int(11) | 是 |  |  |  | 预留字段5 |

---

## ehr_job

**索引列表:**
- adminID: 普通 (adminID)
- depID: 普通 (depID)
- PRIMARY: 主键 (jobID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| jobID | int(11) | 否 |  | PRI |  | 岗位ID，关联表外键 |
| jobCode | varchar(10) | 是 |  |  |  | 岗位编码 |
| jobName | varchar(100) | 是 |  |  |  | 岗位名称 |
| jobAbbr | varchar(100) | 是 |  |  |  | 岗位简称 |
| depID | int(11) | 是 |  | MUL |  | 部门ID |
| adminID | int(11) | 是 |  | MUL |  | 上级ID |
| jobGrage | int(11) | 是 |  |  |  | 岗位级别 |
| jobType | int(11) | 是 |  |  |  | 岗位类型 |
| jobProperty | int(11) | 是 |  |  |  | 岗位属性 |
| jobNum | int(11) | 是 |  |  |  | 业务含义待确认 |
| isCore | bit(1) | 是 | b'0' |  |  | 业务含义待确认 |
| effectDate | datetime | 否 |  |  |  | 生效时间 |
| xorder | varchar(20) | 是 |  |  |  | 排序 |
| isDisabled | bit(1) | 是 | b'0' |  |  | 失效状态 |
| disabledDate | datetime | 是 |  |  |  | 失效时间 |
| remark | varchar(500) | 是 |  |  |  | 备注 |
| xType | int(11) | 是 |  |  |  | 业务含义待确认 |
| jobCustom1 | int(11) | 是 |  |  |  | 保留字段1 |
| jobCustom2 | int(11) | 是 |  |  |  | 保留字段2 |
| jobCustom3 | int(11) | 是 |  |  |  | 保留字段3 |
| jobCustom4 | int(11) | 是 |  |  |  | 保留字段4 |
| jobCustom5 | int(11) | 是 |  |  |  | 保留字段5 |

---

## ehr_login

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI |  | 业务含义待确认 |
| title | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| account | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| empID | int(11) | 是 |  |  |  | 业务含义待确认 |
| workNo | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| name | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| isDisabled | int(11) | 是 |  |  |  | 业务含义待确认 |

---

## fb_contract

**索引列表:**
- contract_code: 普通 (contract_code)
- contract_type: 普通 (contract_type)
- fb_contract_contract_id_IDX: 普通 (contract_id, office_code)
- office_code_IDX: 普通 (office_code, contract_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| contract_id | varchar(64) | 是 |  | MUL |  | 业务含义待确认 |
| contract_code | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| office_code | varchar(15) | 是 |  | MUL |  | 业务含义待确认 |
| contract_type | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| customer_name | varchar(512) | 是 |  |  |  | 业务含义待确认 |
| project_name | varchar(512) | 是 |  |  |  | 业务含义待确认 |
| warranty | varchar(2) | 是 |  |  |  | 业务含义待确认 |
| marketCode | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| marketName | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| systemId | int(11) | 是 |  |  |  | 业务含义待确认 |
| systemName | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| remark | varchar(4096) | 是 |  |  |  | 业务含义待确认 |

---

## fb_ft_result1

**索引列表:**
- item_id: 普通 (item_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| item_id | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| serial_number | varchar(100) | 是 |  |  |  | 业务含义待确认 |

---

## fb_ft_result2

**索引列表:**
- result1_id: 普通 (result1_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| result1_id | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| result_desc | text | 是 |  |  |  | 业务含义待确认 |

---

## fb_items

**索引列表:**
- cover_index: 普通 (item, itemname, describe_)
- itemname: 普通 (itemname)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 是 |  |  |  | 业务含义待确认 |
| item | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| describe_ | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| itemname | varchar(255) | 是 |  | MUL |  | 业务含义待确认 |

---

## fb_items2

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 是 |  |  |  | 业务含义待确认 |
| item | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| describe_ | varchar(150) | 是 |  |  |  | 业务含义待确认 |
| itemname | varchar(255) | 是 |  |  |  | 业务含义待确认 |

---

## fb_market_system

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| marketCode | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| marketName | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| systemId | int(11) | 是 |  |  |  | 业务含义待确认 |
| systemName | varchar(15) | 是 |  |  |  | 业务含义待确认 |

---

## fb_office_relationship

**索引列表:**
- contractNo: 普通 (contractNo)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 表记录历史数据中合同号与办事处的关系 |
| contractNo | varchar(100) | 是 |  | MUL |  | 合同号 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处编码 |

---

## fb_service

**索引列表:**
- barcode_: 普通 (barcode)
- con_xb: 普通 (con_xb)
- id: 普通 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | varchar(64) | 是 |  | MUL |  | 业务含义待确认 |
| con_xb | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| barcode | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| grade | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| begin_date | datetime | 是 |  |  |  | 业务含义待确认 |
| end_date | datetime | 是 |  |  |  | 业务含义待确认 |
| warranty | char(1) | 是 |  |  |  | 业务含义待确认 |
| remark | text | 是 |  |  |  | 业务含义待确认 |
| isyb | int(11) | 是 | 1 |  |  | 1 延保 0 其他数据 |
| state | int(11) | 是 |  |  |  | 针对多条续保，保留最新数据 |

---

## fb_shipment

**索引列表:**
- fb_shipment_con_id_IDX: 普通 (con_id, packlist_id)
- fb_shipment_packdate_IDX: 普通 (packdate, con_id, packlist_id)
- fb_shipment_packlist_id_IDX: 普通 (packlist_id, con_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| packlist_id | varchar(64) | 是 |  | MUL |  | 业务含义待确认 |
| con_id | varchar(64) | 是 |  | MUL |  | 业务含义待确认 |
| packdate | datetime | 是 |  | MUL |  | 业务含义待确认 |
| warrantyStartTime | datetime | 是 |  |  |  | 业务含义待确认 |
| warrantyEndTime | datetime | 是 |  |  |  | 业务含义待确认 |
| receiveName | text | 是 |  |  |  | 收件人 |
| emsNum | text | 是 |  |  |  | 快递单号 |
| emsCompany | text | 是 |  |  |  | 快递公司 |

---

## fb_shipment_barcode

**索引列表:**
- barcode2: 普通 (barcode2)
- barcode_pack_rma_IDX: 普通 (barcode, pack_id, rma_no)
- barcode_rma_pack_IDX: 普通 (barcode, rma_no, pack_id)
- item: 普通 (item)
- item2: 普通 (item2)
- orderNumber_barcode_IDX: 普通 (barcode, orderNumber)
- pack_barcode_IDX: 普通 (pack_id, barcode, rma_no)
- pack_item_IDX: 普通 (pack_id, item, rma_no)
- PRIMARY: 主键 (id)
- uuid: 唯一 (uuid)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | bigint(20) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| pack_id | varchar(64) | 是 |  | MUL |  | 业务含义待确认 |
| item | varchar(16) | 是 |  | MUL |  | 业务含义待确认 |
| barcode | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| com_barcode | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| rma_no | varchar(64) | 是 |  |  |  | 业务含义待确认 |
| isRMA | int(11) | 是 |  |  |  | 标注是RMA替换添加的记录 |
| item2 | varchar(16) | 是 |  | MUL |  | 母子公司发货物料编码对应关系 |
| barcode2 | varchar(50) | 是 |  | MUL |  | 母子公司发货序列号对应关系 |
| orderNumber | varchar(32) | 是 |  |  |  | 订单号 |
| lineNum | int(11) | 是 |  |  |  | 订单行号 |
| profitCenter | varchar(32) | 是 |  |  |  | 利润中心 |
| soleAgentSuffix | varchar(32) | 是 |  |  |  | 总代orderNumber后缀 |
| warrantyStartDate | date | 是 |  |  |  | 维保开始时间，为空默认装箱单发货日期+90天 |
| warrantyMonth | int(11) | 是 |  |  |  | 维保月数 |
| rmaBarcode | varchar(50) | 是 |  |  |  | RMA逆向序列号（维保替换的序列号） |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| syncTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| uuid | varchar(64) | 是 |  | UNI |  | 业务含义待确认 |

---

## fb_shipment_barcode_change_log

**索引列表:**
- idx_dataid_lasted_logid: 普通 (dataId, lasted, logID)
- idx_lasted_dataid_logid: 普通 (lasted, dataId, logID)
- idx_syncFlag_lasted: 普通 (syncFlag, lasted)
- PRIMARY: 主键 (logID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| logID | bigint(20) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| tableName | varchar(128) | 是 |  |  |  | 业务含义待确认 |
| operation | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| changedBy | varchar(128) | 是 |  |  |  | 业务含义待确认 |
| changeTime | datetime | 是 |  |  |  | 业务含义待确认 |
| dataId | varchar(128) | 是 |  | MUL |  | 业务含义待确认 |
| barCode | varchar(128) | 是 |  |  |  | 业务含义待确认 |
| lasted | smallint(6) | 是 |  | MUL |  | 业务含义待确认 |
| oldValues | longtext | 是 |  |  |  | 业务含义待确认 |
| newValues | longtext | 是 |  |  |  | 业务含义待确认 |
| syncTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| syncFlag | smallint(6) | 是 | 0 | MUL |  | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 |

---

## fb_shipment_barcode_order_line

**索引列表:**
- barcode: 普通 (barcode)
- orderNumber: 普通 (orderNumber, lineNum)
- pack_id: 普通 (pack_id, barcode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| pack_id | varchar(64) | 是 |  | MUL |  | 业务含义待确认 |
| packlist_no | varchar(64) | 是 |  |  |  | 业务含义待确认 |
| barcode | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| contractNo | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| orderNumber | varchar(32) | 是 |  | MUL |  | 业务含义待确认 |
| lineNum | int(11) | 是 |  |  |  | 业务含义待确认 |
| orderQty | int(11) | 是 |  |  |  | 业务含义待确认 |
| deliveredQty | int(11) | 是 |  |  |  | 业务含义待确认 |
| profitCenter | varchar(32) | 是 |  |  |  | 利润中心 |
| orderExecNumber | varchar(50) | 是 |  |  |  | 执行单号 |
| soleAgentSuffix | varchar(32) | 是 |  |  |  | 总代orderNumber后缀 |
| warrantyMonth | int(11) | 是 | 0 |  |  | 维保月限 |

---

## fb_shipment_barcode_relation

**索引列表:**
- contract_sn1_IDX: 普通 (contract, sn1)
- item1: 普通 (item1)
- item2: 普通 (item2)
- PRIMARY: 主键 (id)
- sn1: 普通 (sn1)
- sn2: 普通 (sn2)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI |  | 业务含义待确认 |
| sn1 | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| item1 | varchar(15) | 是 |  | MUL |  | 业务含义待确认 |
| sn2 | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| item2 | varchar(15) | 是 |  | MUL |  | 业务含义待确认 |
| contract | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| createtime | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| updatetime | varchar(50) | 是 |  |  |  | 业务含义待确认 |

---

## fb_soft_version

**索引列表:**
- boot: 普通 (boot)
- conp: 普通 (conp)
- cpld: 普通 (cpld)
- pcb: 普通 (pcb)
- serial_number: 普通 (serial_number)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| serial_number | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| conp | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| cpld | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| boot | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| pcb | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |

---

## fb_warranty_grade

**索引列表:**
- gradecode: 普通 (gradecode)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| gradecode | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| gradename | varchar(125) | 是 |  |  |  | 业务含义待确认 |
| gradestatus | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| sort | int(3) | 是 | 0 |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## find_in_set_help

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI |  | where中FIND_IN_SET函数替代方法 |

---

## firebird_operation_log

**索引列表:**
- barCode: 普通 (barCode)
- contractNo: 普通 (contractNo)
- insteadOfNum: 普通 (insteadOfNum)
- PRIMARY: 主键 (id)
- sheetId: 普通 (sheetId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(10) unsigned | 否 |  | PRI | auto_increment | ID |
| sheetId | varchar(25) | 是 |  | MUL |  | 流水号SheetId |
| txId | int(11) | 是 |  |  |  | tx_info的tx_id |
| contractNo | varchar(45) | 是 |  | MUL |  | 合同号 |
| barCode | varchar(25) | 是 |  | MUL |  | 设备序列号 |
| insteadOfNum | varchar(25) | 是 |  | MUL |  | RMA申请被替代的设备序列号 |
| changeTable | varchar(45) | 是 |  |  |  | 操作的表 |
| operatTime | timestamp | 否 | CURRENT_TIMESTAMP |  |  | 操作时间 |
| sqlText | text | 是 |  |  |  | 操作表的sql语句 |
| remark | varchar(45) | 是 |  |  |  | 备注 |

---

## fnd_basic_data

**索引列表:**
- basicDataId: 普通 (basicDataId)
- basicDataId_dataTypeCode: 普通 (dataTypeCode, basicDataId)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| dataTypeCode | varchar(45) | 是 |  | MUL |  | 业务含义待确认 |
| basicDataId | varchar(255) | 是 |  | MUL |  | 业务含义待确认 |
| basicDataName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| basicDataAttri1 | varchar(255) | 是 |  |  |  | 字段属性1 |
| sortId | int(11) | 是 |  |  |  | 查询排序字段数值越大越在前 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_basic_data_type

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| dataTypeCode | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| dataTypeName | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| status | int(11) | 是 |  |  |  | 是否需要放在前台管理 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_basic_prjstate

**索引列表:**
- dataTypeCode: 普通 (dataTypeCode, basicDataId)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| dataTypeCode | varchar(45) | 是 |  | MUL |  | 数据类型编码，对应fnd_basic_data |
| basicDataId | varchar(11) | 是 |  |  |  | 基础数据ID，对应fnd_basic_data |
| column010 | varchar(10) | 是 |  |  |  | 项目类型，对应pm_project_header |
| column011 | varchar(10) | 是 |  |  |  | 项目类别，对应pm_project_header |
| createTime | datetime | 是 |  |  |  | 记录数据创建时间 |
| createBy | varchar(45) | 是 |  |  |  | 记录数据创建用户 |
| updateTime | datetime | 是 |  |  |  | 记录数据最新更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 记录数据最新更新用户 |
| effectiveFrom | datetime | 是 |  |  |  | 数据有效性开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 数据有效性结束时间 |

---

## fnd_company

**索引列表:**
- code: 普通 (code)
- pid: 普通 (pid)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| pid | int(11) | 否 |  | MUL |  | 父组织机构ID |
| name | varchar(128) | 否 |  |  |  | 组织机构全名 |
| abbr | varchar(64) | 否 |  |  |  | 组织机构简写 |
| website | varchar(128) | 是 |  |  |  | 组织机构网址 |
| code | varchar(25) | 是 | 0 | MUL |  | 组织机构代码 |
| account | varchar(25) | 是 |  |  |  | 组织机构账套 |
| status | smallint(1) | 否 | 1 |  |  | 有效性（1-有效，0-失效），默认有效 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(32) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(32) | 是 |  |  |  | 业务含义待确认 |

---

## fnd_department

**索引列表:**
- deparmentNum: 唯一 (departmentNum)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| departmentNum | varchar(20) | 否 |  | UNI |  | 业务含义待确认 |
| departmentName | varchar(20) | 否 |  |  |  | 业务含义待确认 |
| isparam | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| status | int(11) | 否 | 1 |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_files

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 系统上传文件信息 |
| fileName | varchar(255) | 是 |  |  |  | 文件名称 |
| filePath | varchar(255) | 是 |  |  |  | 文件路径 |
| fileType | varchar(255) | 是 |  |  |  | 文件分类 |
| uploadBy | varchar(25) | 是 |  |  |  | 上传用户 |
| uploadTime | datetime | 是 |  |  |  | 上传时间 |

---

## fnd_mails

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| mailSubject | varchar(255) | 否 |  |  |  | 邮件主题 |
| mailContent | longtext | 否 |  |  |  | 邮件正文 |
| mailTos | text | 是 |  |  |  | 邮件主送 |
| mailCcs | text | 是 |  |  |  | 邮件抄送 |
| mailBcc | text | 是 |  |  |  | 邮件密送 |
| mailAttachFiles | text | 是 |  |  |  | 邮件附件 以特殊符号间隔多个文件 |
| mailSendTime | datetime | 是 |  |  |  | 邮件实际发送时间 |
| mailExpectSendTime | datetime | 是 |  |  |  | 邮件期望发送时间 |
| mailServerPort | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| mailServerHost | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| mailUsername | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| mailPassword | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| mailFromaddress | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| sendFlag | int(11) | 是 | 0 |  |  | 邮件是否发送 1 为已发送 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatteTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_menus

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| menuCode | varchar(50) | 是 |  |  |  | 菜单编码 |
| menuName | varchar(25) | 是 |  |  |  | 菜单名称 |
| menuLevel | int(1) | 是 |  |  |  | 菜单级别 |
| superId | int(11) | 是 |  |  |  | 父菜单ID |
| path | varchar(200) | 是 |  |  |  | 访问路径 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_role_menus

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| roleId | int(11) | 否 |  |  |  | 业务含义待确认 |
| menuId | int(11) | 否 |  |  |  | 业务含义待确认 |
| menuPower | varchar(20) | 否 |  |  |  | 各菜单增删改权限 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_roles

**索引列表:**
- PRIMARY: 主键 (id)
- roleName: 唯一 (roleName)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(6) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| roleName | varchar(64) | 否 |  | UNI |  | 业务含义待确认 |
| defaultPage | varchar(255) | 是 |  |  |  | 该角色登录的默认首页 |
| status | int(1) | 否 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| roleRemark | varchar(200) | 是 |  |  |  | 业务含义待确认 |

---

## fnd_spms_arg

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 备件系统一些特殊参数控制 如邮件等 |
| code | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| var | text | 是 |  |  |  | 业务含义待确认 |
| mark | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_sys_arg

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 系统变量 |
| code | varchar(64) | 是 |  |  |  | 业务含义待确认 |
| var | text | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_user_info

**索引列表:**
- PRIMARY: 主键 (id)
- username: 普通 (username)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(8) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| username | varchar(128) | 否 |  | MUL |  | 业务含义待确认 |
| password | varchar(32) | 否 | 5416d7cd6ef195a0f7622a9c56b55e84 |  |  | 业务含义待确认 |
| email | varchar(128) | 否 |  |  |  | 业务含义待确认 |
| dpNo | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| realName | varchar(128) | 否 |  |  |  | 业务含义待确认 |
| roleIds | varchar(64) | 是 |  |  |  | 用户角色，支持多角色 |
| isemail | int(11) | 是 |  |  |  | 业务含义待确认 |
| status | int(1) | 是 |  |  |  | 业务含义待确认 |
| defaultPage | varchar(255) | 是 |  |  |  | 该用户登录首页 |
| pwdoverdue | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## fnd_user_menus

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| fnd_user_id | int(11) | 是 |  |  |  | 业务含义待确认 |
| username | varchar(128) | 是 |  |  |  | 业务含义待确认 |
| menuCode | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| menuValue | int(1) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| createdBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## fnd_user_power

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| fndUserId | int(11) | 是 |  |  |  | 业务含义待确认 |
| username | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| areapower | varchar(4096) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## hexiao

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| 单据编号 | double | 是 |  |  |  | 业务含义待确认 |
| 过帐日期 | timestamp | 是 |  |  |  | 业务含义待确认 |
| 物料代码 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| 物料/服务描述 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| 未核销数量 | double | 是 |  |  |  | 业务含义待确认 |
| 设备序列号 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| 注释 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| 合同号 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| 责任部门 | varchar(255) | 是 |  |  |  | 业务含义待确认 |

---

## mes_oqc_info

**索引列表:**
- barcode: 普通 (barcode)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| packNo | varchar(64) | 是 |  |  |  | 装箱单号 |
| contractNo | varchar(64) | 是 |  |  |  | 合同号 |
| itemCode | varchar(25) | 是 |  |  |  | 物料号 |
| barcode | varchar(64) | 是 |  | MUL |  | 设备序列号 |
| itemNo | varchar(25) | 是 |  |  |  | 装箱销售明细行号 |
| workNo | varchar(25) | 是 |  |  |  | 工号 |
| inspectUser | varchar(25) | 是 |  |  |  | 检验人 |
| inspectTime | datetime | 是 |  |  |  | 检验时间 |

---

## mes_seal_info

**索引列表:**
- user: 普通 (user)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| name | varchar(255) | 是 |  |  |  | 印章名称 |
| info | varchar(255) | 是 |  |  |  | 印记 |
| description | varchar(255) | 是 |  |  |  | 用途 |
| user | varchar(255) | 是 |  | MUL |  | 领用人 |
| takeTime | datetime | 是 |  |  |  | 领用时间 |
| backTime | datetime | 是 |  |  |  | 归还时间 |
| remark | varchar(255) | 是 |  |  |  | 备注 |
| uploadBy | varchar(255) | 是 |  |  |  | 上传人 |
| uploadTime | datetime | 是 |  |  |  | 上传时间 |

---

## pm_basic_deliver_detail

**索引列表:**
- deliverId: 普通 (deliverId)
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)
- projectType_projectId_deliverType: 普通 (projectType, projectId, deliverableType)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 项目ID |
| projectType | varchar(25) | 是 | 10 | MUL |  | 项目类型 |
| contractNo | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| taskId | int(11) | 是 |  |  |  | TaskId |
| deliverId | int(11) | 是 |  | MUL |  | 对应pm_basic_prj_deliver主键 |
| deliverableName | varchar(255) | 是 |  |  |  | 交付件名称 |
| deliverablePath | varchar(255) | 是 |  |  |  | 交付件路径 |
| deliverableType | varchar(45) | 是 |  |  |  | 交付件类型 |
| uploadUser | varchar(45) | 是 |  |  |  | 上传者 |
| uploadTime | datetime | 是 |  |  |  | 上传时间 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_basic_prj_deliver

**索引列表:**
- dataTypeCode: 普通 (dataTypeCode, basicDataId)
- dataTypeCodeSon: 普通 (dataTypeCodeSon, basicDataIdSon)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| column010 | varchar(25) | 是 |  |  |  | 对应pm_project_header |
| column011 | varchar(25) | 是 |  |  |  | 对应pm_project_header |
| dataTypeCode | varchar(45) | 是 |  | MUL |  | 活动节点，对应fnd_basic_data表 |
| basicDataId | varchar(45) | 是 |  |  |  | 活动节点，对应fnd_basic_data表 |
| dataTypeCodeSon | varchar(45) | 是 |  | MUL |  | 交付件节点，对应fnd_basic_data表 |
| basicDataIdSon | varchar(45) | 是 |  |  |  | 交付件节点，对应fnd_basic_data表 |
| isNeed | int(11) | 是 | 0 |  |  | 是否必须，1表示必须，2表示分情况确定 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_cl_callback

**索引列表:**
- instId: 普通 (instId)
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 运营商直签项目回访申请主表 |
| projectId | int(11) | 是 |  | MUL |  | 项目ID |
| instId | varchar(25) | 是 |  | MUL |  | 流程ID |
| remark | text | 是 |  |  |  | 回访申请备注 |
| applyState | int(11) | 是 |  |  |  | -1草稿 1 审批中 2审批通过 |
| applyBy | varchar(25) | 是 |  |  |  | 申请人 |
| applyTime | datetime | 是 |  |  |  | 申请时间 |
| createTime | timestamp | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_cl_callback_quesnaire

**索引列表:**
- callBackId: 普通 (callBackId)
- PRIMARY: 主键 (id)
- quesnaireId: 普通 (quesnaireId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| callBackId | int(11) | 是 |  | MUL |  | 回访主键主表 |
| taskId | varchar(25) | 是 |  |  |  | 对应任务ID |
| quesnaireId | int(11) | 是 |  | MUL |  | 对应pm_cl_quesnaire_result_header主键 |
| quesnaireVersion | int(11) | 是 |  |  |  | 版本号 |
| quesnaireState | int(11) | 是 |  |  |  | -1 草稿 1已提交 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_cl_evaluation_header

**索引列表:**
- PRIMARY: 主键 (id)
- projectCode_index: 普通 (projectCode)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) unsigned | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectCode | varchar(45) | 否 |  | MUL |  | 评测项目编码 |
| projectId | int(11) | 否 | 0 | MUL |  | 项目ID |
| projectName | varchar(120) | 是 |  |  |  | 项目名称 |
| evaluationTime | datetime | 是 | 0000-00-00 00:00:00 |  |  | 审核时间 |
| evaluationPeopleName | varchar(45) | 是 |  |  |  | 审核人员姓名 |
| evaluationScore | double | 否 | 0 |  |  | 评测总分数 |
| evaluationResult | int(11) | 否 | 0 |  |  | 评测结果（通过/未通过） |
| evaluationComment | text | 是 |  |  |  | 项目评价（驳回时为驳回原因） |
| evaluationType | int(11) | 否 | 0 |  |  | 400回访/项目组总分评定 |
| status | int(11) | 否 | 0 |  |  | 业务含义待确认 |
| createdTime | datetime | 是 | 0000-00-00 00:00:00 |  |  | 业务含义待确认 |
| createdPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatedTime | datetime | 是 | 0000-00-00 00:00:00 |  |  | 业务含义待确认 |
| updatedPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| nextAcceptPerson | varchar(25) | 是 |  |  |  | 下一个接收申请的人员 |
| evaluationPeopleId | varchar(25) | 是 |  |  |  | 审核人员用户名 |
| nextAcceptPersonName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| applyHeaderId | int(11) | 否 | 0 |  |  | 申请表Id |

---

## pm_cl_quesnaire_result_header

**索引列表:**
- index_evaluationHeaderId: 普通 (evaluationHeaderId)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) unsigned | 否 |  | PRI | auto_increment | 业务含义待确认 |
| evaluationHeaderId | int(11) | 否 |  | MUL |  | 测评记录Id |
| quesnaireTemplateHeaderId | int(11) | 是 |  |  |  | 问卷模板Id |
| quesMarkScore | double | 是 | 0 |  |  | 问卷得分 |
| createdTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatedTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updatedPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| quesMarkResult | int(11) | 是 |  |  |  | 评分结果 |
| status | int(11) | 否 | 0 |  |  | 业务含义待确认 |

---

## pm_cl_quesnaire_result_line

**索引列表:**
- PRIMARY: 主键 (id)
- quesnaireResultHeaderId: 普通 (quesnaireResultHeaderId, quesTypeForCB)
- quesnaireTemplateHeaderId: 普通 (quesnaireTemplateHeaderId, quesnaireTemplateLineId)
- quesnaireTemplateLineId: 普通 (quesnaireTemplateLineId, questionTemplateOptId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) unsigned | 否 |  | PRI | auto_increment | 业务含义待确认 |
| quesnaireTemplateHeaderId | int(11) | 否 |  | MUL |  | 回访问卷Id |
| quesnaireTemplateLineId | int(11) | 否 |  | MUL |  | 问卷中问题的Id |
| questionTemplateOptId | int(11) | 是 |  |  |  | 选中的选项id |
| questionAnswer | text | 是 |  |  |  | 业务含义待确认 |
| questionScore | double | 否 | 0 |  |  | 问题得分 |
| quesnaireResultHeaderId | int(11) | 否 |  | MUL |  | 回访结果头信息Id |
| createdTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatedTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updatedPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| quesTypeForCB | varchar(10) | 是 |  |  |  | 问题回访类型 |
| quesEvaResult | int(11) | 是 |  |  |  | 选项是否为不同选项 |

---

## pm_cl_quesnaire_template_header

**索引列表:**
- PRIMARY: 主键 (id)
- quesType: 普通 (quesType)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(10) unsigned | 否 |  | PRI | auto_increment | 业务含义待确认 |
| questionnaireTemplateNum | varchar(45) | 否 |  |  |  | 问卷模板编号 |
| questionnaireTemplateName | varchar(200) | 否 |  |  |  | 问卷模板名称 |
| questionnaireScore | double | 否 | 0 |  |  | 问卷总分数 |
| questionnairePassScore | double | 否 | 0 |  |  | 问卷达标分数 |
| questionnaireStatus | int(11) | 否 | 0 |  |  | 问卷状态 |
| effectiveStartTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveEndTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updatedTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatedPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| quesType | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| markIndexs | varchar(45) | 是 |  |  |  | 问卷计分规则的index |

---

## pm_cl_quesnaire_template_line

**索引列表:**
- id_UNIQUE: 唯一 (id)
- PRIMARY: 主键 (id)
- quesnaireTemplateHeaderId: 普通 (quesnaireTemplateHeaderId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) unsigned | 否 |  | PRI | auto_increment | 业务含义待确认 |
| questionContent | varchar(200) | 否 |  |  |  | 题目内容 |
| questionType | int(11) | 否 |  |  |  | 题目类型,如:多选\单选 |
| questionScore | double | 否 | 0 |  |  | 题目分数 |
| questionRemark | varchar(200) | 是 |  |  |  | 题目备注 |
| questionNum | int(11) | 否 | 0 |  |  | 问题编号,表示了问卷中问题的顺序 |
| quesnaireTemplateHeaderId | int(11) | 否 | 0 | MUL |  | 问卷模板Id |
| questionStatus | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| effectiveStartTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveEndTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updatedTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatedPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| questionTypeForCB | varchar(10) | 是 |  |  |  | 回访问题类型 |

---

## pm_cl_quesnaire_template_options

**索引列表:**
- id_UNIQUE: 唯一 (id)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) unsigned | 否 |  | PRI | auto_increment | 业务含义待确认 |
| questionId | int(11) | 否 | 0 |  |  | 题目Id |
| questionOptionNum | int(11) | 否 | 0 |  |  | 选项编号 |
| questionOptionsContent | varchar(200) | 否 |  |  |  | 选项内容 |
| questionOptionScore | double | 是 | 0 |  |  | 选项分数 |
| quesnaireTemplateHeaderId | int(11) | 否 | 0 |  |  | 问卷模板Id |
| effectiveStartTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveEndTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updatedTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createdPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updatedPerson | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| quesLineType | varchar(10) | 是 |  |  |  | 问题类型 |

---

## pm_column_of_relationship

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectType | int(11) | 是 |  |  |  | 业务含义待确认 |
| columnCode | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| colemnName | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| columnDesc | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_common_related_data

**索引列表:**
- PRIMARY: 主键 (id)
- type: 普通 (type, objType, objId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| type | varchar(64) | 否 |  | MUL |  | 数据类型 |
| objType | varchar(64) | 否 |  |  |  | 主数据类型 |
| objId | int(11) | 否 | 0 |  |  | 主数据Id |
| field1 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field2 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field3 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field4 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field5 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field6 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field7 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field8 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field9 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| field10 | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| disabled | bit(1) | 否 | b'0' |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 业务含义待确认 |

---

## pm_daily_report

**索引列表:**
- category: 普通 (category, subCategory)
- createBy: 普通 (createBy)
- createTime: 普通 (createTime)
- officeCode: 普通 (officeCode)
- PRIMARY: 主键 (id)
- projectCode: 普通 (projectCode)
- projectId: 普通 (projectId)
- projectType: 普通 (projectType)
- subCategory: 普通 (subCategory)
- type: 普通 (type)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 否 | -1 | MUL |  | 项目头信息主键 |
| projectType | varchar(45) | 否 |  | MUL |  | 项目类型，售前:20/售后:10 |
| projectCode | varchar(45) | 否 |  | MUL |  | 项目名称 |
| projectName | varchar(200) | 是 |  |  |  | 项目名称 |
| contractNo | varchar(255) | 是 |  |  |  | 合同号 |
| officeCode | varchar(25) | 是 |  | MUL |  | 办事处编码 |
| type | varchar(45) | 是 |  | MUL |  | 任务性质 |
| category | varchar(45) | 是 |  | MUL |  | 任务分类 |
| subCategory | varchar(45) | 是 |  | MUL |  | 任务小类 |
| processTime | datetime | 是 |  |  |  | 处理时间 |
| processDesc | varchar(1024) | 是 |  |  |  | 事项描述 |
| processStep | varchar(1024) | 是 |  |  |  | 解决进展 |
| remainProblem | varchar(1024) | 是 |  |  |  | 遗留问题 |
| customerInteraction | varchar(1024) | 是 |  |  |  | 客户互动情况 |
| transitHour | float | 是 | 0 |  |  | 在途耗时(h) |
| processHour | float | 是 | 0 |  |  | 处理耗时(h) |
| itemModel | varchar(255) | 是 |  |  |  | 产品型号 |
| softVersion | varchar(255) | 是 |  |  |  | 在网版本 |
| enabledFeatures | varchar(255) | 是 |  |  |  | 启用功能 |
| customTos | varchar(255) | 是 |  |  |  | 自定义主送 |
| customCcs | varchar(255) | 是 |  |  |  | 自定义抄送 |
| projectExecutionState | varchar(45) | 是 |  |  |  | 项目实施状态 |
| hasReport | bit(1) | 否 | b'0' |  |  | 是否有巡检报告 |
| quesnaireId | int(11) | 是 |  |  |  | 问卷ID |
| deliverFileIds | varchar(255) | 是 |  |  |  | 交付件，fnd_files id |
| remark | varchar(1024) | 是 |  |  |  | 备注 |
| isReported | bit(1) | 是 | b'0' |  |  | 已上报 |
| qualityFactor | float | 是 | 0 |  |  | 质量系数 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| status | varchar(25) | 是 |  |  |  | 状态 |
| disabled | bit(1) | 是 | b'0' |  |  | 失效标记 |
| createTime | datetime | 是 |  | MUL |  | 创建时间 |
| createBy | varchar(45) | 是 |  | MUL |  | 创建用户 |
| updateTime | datetime | 是 |  |  |  | 最新更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 最新更新用户 |

---

## pm_dispatch_project_header

**索引列表:**
- facilitatorId: 普通 (facilitatorCode)
- officeCode: 普通 (officeCode)
- PRIMARY: 主键 (id)
- profitDepCode: 普通 (profitDepCode)
- smsProjectCode: 普通 (smsProjectCode)
- subcontractNo: 普通 (dispatchNo)
- UNIQUE_dispatchSeq: 唯一 (dispatchSeq)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| dispatchName | varchar(512) | 是 |  |  |  | 外派名称 |
| dispatchNo | varchar(64) | 是 |  | MUL |  | 外派合同号 |
| dispatchSeq | varchar(64) | 是 |  | UNI |  | 外派编号 |
| contractNos | varchar(2048) | 是 |  |  |  | 项目合同号 |
| projectIds | varchar(1024) | 是 |  |  |  | 外派的项目ID |
| type | varchar(25) | 是 |  |  |  | 外派类型 |
| state | int(11) | 否 | 0 |  |  | 外派状态 |
| peopleNum | int(11) | 是 | 0 |  |  | 外派人数 |
| callbackState | int(11) | 是 |  |  |  | 回访状态 |
| facilitatorId | int(11) | 是 |  |  |  | 服务商ID |
| facilitatorCode | varchar(25) | 是 |  | MUL |  | 服务商编码 |
| facilitatorName | varchar(64) | 是 |  |  |  | 服务商名 |
| bankInfo | varchar(255) | 是 |  |  |  | 服务商开户地址 |
| bankAccount | varchar(64) | 是 |  |  |  | 服务商收款账户 |
| officeCode | varchar(25) | 是 |  | MUL |  | 办事处部门 |
| profitDepCode | varchar(25) | 是 |  | MUL |  | 收益部门 |
| dutyPerson | varchar(25) | 是 |  |  |  | 项目总接口人 |
| officeDutyPerson | varchar(25) | 是 |  |  |  | 办事处接口人 |
| isAccrued | bit(1) | 是 |  |  |  | 是否计提 |
| isInvoiced | bit(1) | 是 |  |  |  | 是否提供发票 |
| dispatchAmount | varchar(25) | 是 |  |  |  | 外派价 |
| prepaidInfo | varchar(255) | 是 |  |  |  | 预付信息（比例、金额） |
| prepaidRule | varchar(255) | 是 |  |  |  | 预付遵循原则 |
| acceptanceInfo | varchar(255) | 是 |  |  |  | 验收要求 |
| reason | varchar(512) | 是 |  |  |  | 外派原因 |
| remark | varchar(512) | 是 |  |  |  | 备注 |
| dispatchTime | datetime | 是 |  |  |  | 派单时间 |
| smsProjectCode | varchar(255) | 是 |  | MUL |  | SMS项目编码 |
| smsSubmitTime | datetime | 是 |  |  |  | SMS项目提交时间 |
| smsProjectAmount | varchar(25) | 是 |  |  |  | SMS项目金额 |
| smsAfProjectAmount | varchar(25) | 是 |  |  |  | 安服项目金额 |
| effectiveFrom | datetime | 是 |  |  |  | 有效开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 有效结束时间 |
| disabled | bit(1) | 是 | b'0' |  |  | 删除状态 |
| dispatched | bit(1) | 是 | b'0' |  |  | 派单状态 |
| settled | bit(1) | 是 | b'0' |  |  | 结算状态 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_dispatch_project_settlement

**索引列表:**
- dispatchId: 普通 (dispatchId)
- dispatchSeq: 普通 (dispatchSeq)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| settleSeq | varchar(512) | 是 |  |  |  | 结算编号 |
| dispatchId | int(11) | 否 |  | MUL |  | 派单Id |
| dispatchSeq | varchar(25) | 否 |  | MUL |  | 派单编号 |
| progressDesc | varchar(1024) | 是 |  |  |  | 实施进展 |
| progressRatio | float(3,2) | 是 |  |  |  | 实施比例 |
| acceptanceDesc | varchar(1024) | 是 |  |  |  | 验收进度 |
| acceptanceRatio | varchar(10) | 是 |  |  |  | 验收比例 |
| ratio | varchar(10) | 是 |  |  |  | 此次付款比例 |
| amount | varchar(25) | 是 |  |  |  | 此次付款金额 |
| memo | varchar(512) | 是 |  |  |  | 此次付款说明 |
| confirmTime | datetime | 是 |  |  |  | 提交时间 |
| paymentTime | datetime | 是 |  |  |  | 付款时间 |
| remark | varchar(512) | 是 |  |  |  | 备注 |
| state | int(1) | 是 | 0 |  |  | 状态 |
| disabled | bit(1) | 是 | b'0' |  |  | 删除标记 |
| quarter | int(4) | 是 |  |  |  | 结算季度 |
| month | int(2) | 是 |  |  |  | 结算月份 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| sseId | bigint(20) | 是 | -1 |  |  | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| settled | bit(1) | 是 | b'0' |  |  | 结算状态 |
| year | int(4) | 是 |  |  |  | 结算年份 |

---

## pm_facilitator

**索引列表:**
- account: 唯一 (account, state)
- code: 唯一 (code)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| code | varchar(25) | 是 |  | UNI |  | 服务商编号 |
| account | varchar(25) | 是 |  | MUL |  | 服务商账号 |
| name | varchar(64) | 是 |  |  |  | 服务商名 |
| type | varchar(64) | 是 |  |  |  | 合作类型 |
| bankInfo | varchar(255) | 是 |  |  |  | 开户行信息 |
| bankAccount | varchar(64) | 是 |  |  |  | 收款账户 |
| cnapsCode | varchar(25) | 是 |  |  |  | 联行号 |
| contacts | varchar(64) | 是 |  |  |  | 联系人 |
| tel | varchar(64) | 是 |  |  |  | 联系电话 |
| email | varchar(64) | 是 |  |  |  | 联系邮箱 |
| state | bit(1) | 是 | b'1' |  |  | 状态 |
| needApprove | bit(1) | 是 | b'0' |  |  | 是否评审 |
| approveStatus | int(1) | 是 | 0 |  |  | 审批结果 |
| deliveryIds | varchar(25) | 是 |  |  |  | 附件材料 |
| relateType | varchar(25) | 是 |  |  |  | 关联类型 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_facilitator_form_d365

**索引列表:**
- account: 唯一 (account, code)
- code: 唯一 (code)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| code | varchar(25) | 是 |  | UNI |  | 服务商编号 |
| account | varchar(25) | 是 |  | MUL |  | 服务商账号 |
| name | varchar(64) | 是 |  |  |  | 服务商名 |
| type | varchar(64) | 是 |  |  |  | 合作类型 |
| bankInfo | varchar(255) | 是 |  |  |  | 开户行信息 |
| bankAccount | varchar(64) | 是 |  |  |  | 收款账户 |
| cnapsCode | varchar(25) | 是 |  |  |  | 联行号 |
| contacts | varchar(64) | 是 |  |  |  | 联系人 |
| tel | varchar(64) | 是 |  |  |  | 联系电话 |
| email | varchar(64) | 是 |  |  |  | 联系邮箱 |
| state | bit(1) | 是 | b'1' |  |  | 状态 |
| needApprove | bit(1) | 是 | b'0' |  |  | 是否评审 |
| approveStatus | int(1) | 是 | 0 |  |  | 审批结果 |
| deliveryIds | varchar(25) | 是 |  |  |  | 附件材料 |
| relateType | varchar(25) | 是 |  |  |  | 关联类型 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_notification_template

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| templateCode | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| notificationObject | varchar(45) | 是 |  |  |  | 主题 |
| notificationContent | text | 是 |  |  |  | 内容 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_order_data_from_erp

**索引列表:**
- contractNo: 普通 (contractNo)
- orderExecNumber: 普通 (orderExecNumber)
- orderNumber: 普通 (orderNumber)
- orderType: 普通 (orderType, salesType)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| orderNumber | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| contractNo | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| orderExecNumber | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| orderCreateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customerRequireTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customerCode | varchar(55) | 是 |  |  |  | 业务含义待确认 |
| customerName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| projectName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderComment | varchar(2048) | 是 |  |  |  | 业务含义待确认 |
| orderType | int(11) | 是 | 0 | MUL |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | 是 | 0 |  |  | 公司编码 |
| salesType | varchar(25) | 是 | 01 |  |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | 是 | SAP |  |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | 是 |  |  |  | 自定义字段 |
| syncTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |

---

## pm_order_data_from_erp_d365

**索引列表:**
- contractNo: 普通 (contractNo)
- orderExecNumber: 普通 (orderExecNumber)
- orderNumber: 普通 (orderNumber)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| orderNumber | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| contractNo | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| orderExecNumber | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| orderCreateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customerRequireTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customerCode | varchar(55) | 是 |  |  |  | 业务含义待确认 |
| customerName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| projectName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderComment | varchar(2048) | 是 |  |  |  | 业务含义待确认 |
| orderType | int(11) | 是 | 0 |  |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | 是 | 0 |  |  | 公司编码 |
| salesType | varchar(25) | 是 | 01 |  |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| customInfo | json | 是 |  |  |  | 自定义字段 |
| syncTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |

---

## pm_order_data_from_erp_sap

**索引列表:**
- contractNo: 普通 (contractNo)
- orderExecNumber: 普通 (orderExecNumber)
- orderNumber: 普通 (orderNumber)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| orderNumber | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| contractNo | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| orderExecNumber | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| orderCreateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customerRequireTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customerCode | varchar(55) | 是 |  |  |  | 业务含义待确认 |
| customerName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| projectName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderComment | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderType | int(11) | 是 | 0 |  |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | 是 | 0 |  |  | 公司编码 |
| salesType | varchar(25) | 是 | 01 |  |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |

---

## pm_order_line_from_erp

**索引列表:**
- itemCode: 普通 (itemCode)
- orderNumber: 普通 (orderNumber)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| orderNumber | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| lineNum | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| itemCode | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| itemDesc | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderQuantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| openQuantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| bundleCode | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| warrantyMonth | int(11) | 是 |  |  |  | 业务含义待确认 |
| lineType | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| compCode | varchar(25) | 是 | 0 |  |  | 公司编码 |
| profitCenter | varchar(25) | 是 |  |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | 是 |  |  |  | 真实执行单号 |
| source | varchar(25) | 是 | SAP |  |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | 是 |  |  |  | 自定义字段 |
| syncTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |

---

## pm_order_line_from_erp_d365

**索引列表:**
- itemCode: 普通 (itemCode)
- orderNumber: 普通 (orderNumber, lineNum)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| orderNumber | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| lineNum | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| itemCode | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| itemDesc | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderQuantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| openQuantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| bundleCode | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| warrantyMonth | int(11) | 是 |  |  |  | 业务含义待确认 |
| lineType | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| compCode | varchar(25) | 是 | 0 |  |  | 公司编码 |
| profitCenter | varchar(25) | 是 |  |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | 是 |  |  |  | 真实执行单号 |
| customInfo | json | 是 |  |  |  | 自定义字段 |
| syncTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |

---

## pm_order_line_from_erp_sap

**索引列表:**
- itemCode: 普通 (itemCode)
- orderNumber: 普通 (orderNumber, lineNum)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| orderNumber | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| lineNum | int(11) | 是 |  |  |  | 业务含义待确认 |
| itemCode | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| itemDesc | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderQuantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| openQuantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| bundleCode | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| warrantyMonth | int(11) | 是 |  |  |  | 业务含义待确认 |
| lineType | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| compCode | varchar(25) | 是 | 0 |  |  | 公司编码 |
| profitCenter | varchar(25) | 是 |  |  |  | 利润中心 |

---

## pm_person_from_oa

**索引列表:**
- PRIMARY: 主键 (id)
- salesmanCode1: 普通 (salesmanCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| salesmanCode | varchar(45) | 是 |  | MUL |  | 业务含义待确认 |
| salesmanTel | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| salesmanName | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| salesmanMail | varchar(100) | 是 |  |  |  | 业务含义待确认 |

---

## pm_presales_project_callback

**索引列表:**
- presalesId: 普通 (presalesId)
- PRIMARY: 主键 (id)
- quesnaireId: 普通 (quesnaireId)
- taskId: 普通 (taskId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 售前回访问卷表 |
| presalesId | int(11) | 是 |  | MUL |  | 售前项目ID |
| taskId | varchar(25) | 是 |  | MUL |  | 任务ID |
| quesnaireId | int(11) | 是 |  | MUL |  | 问卷ID |
| quesnaireVersion | int(11) | 是 |  |  |  | 问卷版本 |
| quesnaireState | int(11) | 是 |  |  |  | 状态 -1 草稿 1已提交 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_presales_project_duration

**索引列表:**
- PRIMARY: 主键 (presalesId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| presalesId | int(11) | 否 |  | PRI |  | 业务含义待确认 |
| instId | int(11) | 是 |  |  |  | 流程实例ID |
| totalDuration | varchar(20) | 是 |  |  |  | 开始时间 |
| serviceDuration | varchar(20) | 是 |  |  |  | 指派服务经理时间 |
| programDuration | varchar(20) | 是 |  |  |  | 指派项目经理时间 |
| testDuration | varchar(20) | 是 |  |  |  | 测试开始时间 |
| callbackDuration | varchar(20) | 是 |  |  |  | 回访开始时间 |
| serviceApproveDuration | varchar(100) | 是 |  |  |  | 服务经理审批时间 |

---

## pm_presales_project_header

**索引列表:**
- instId: 普通 (instId)
- lendInfoId: 普通 (lendInfoId)
- PRIMARY: 主键 (presalesId)
- projectCode: 普通 (projectCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| presalesId | int(11) | 否 |  | PRI | auto_increment | 售前项目主表 |
| instId | varchar(64) | 是 |  | MUL |  | activity工作流流程ID |
| applyState | int(11) | 是 |  |  |  | -1草稿 1 审批中 2结束 |
| applyBy | varchar(25) | 是 |  |  |  | 申请人 |
| applyTime | datetime | 是 |  |  |  | 申请时间 |
| endTime | datetime | 是 |  |  |  | 申请结束时间 |
| projectState | varchar(25) | 是 | 10 |  |  | 项目状态 ，同售后项目状态  10 未创建 20 直接闭环 30 已创建 31待指派项目经理 32 项目经理跟踪 33工程管理部回访 100闭环 |
| presalesCode | varchar(64) | 是 |  |  |  | 售前项目编码 |
| projectCode | varchar(64) | 是 |  | MUL |  | 项目编码 |
| projectName | varchar(255) | 是 |  |  |  | 项目名称 |
| projectType | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| marketName | varchar(25) | 是 |  |  |  | 市场部名称 |
| systemName | varchar(25) | 是 |  |  |  | 系统部名称 |
| expendName | varchar(25) | 是 |  |  |  | 拓展部名称 |
| industryName | varchar(25) | 是 |  |  |  | 子行业名称 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处编码 |
| salesman | varchar(25) | 是 |  |  |  | 销售人员 |
| productManager | varchar(25) | 是 |  |  |  | 产品经理 |
| salesmanLink | varchar(125) | 是 |  |  |  | 销售人员联系方式 |
| lendInfoId | varchar(64) | 是 |  | MUL |  | SMS系统测试类借货申请主键，标识存在则不再刷新过来 |
| lendfiles | varchar(2048) | 是 |  |  |  | 借货交付件 从SMS中同步过来 |
| confirmFileIds | varchar(2048) | 是 |  |  |  | 现场测试服务确认单 |
| hasRma | int(1) | 是 | 0 |  |  | 是否有未核销数据 |
| hasTransfer | int(1) | 是 | 0 |  |  | 是否发生借转销 |
| closeRemark | varchar(512) | 是 |  |  |  | 闭环备注 |
| createBy | varchar(25) | 是 |  |  |  | 数据创建人 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 数据创建时间 |
| updateBy | varchar(25) | 是 |  |  |  | 数据更新人 |
| updateTime | datetime | 是 |  |  |  | 数据更新时间 |
| effectiveFrom | datetime | 是 |  |  |  | 数据有效开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 数据有效结束时间 |
| source | varchar(25) | 否 | SMS |  |  | 数据来源 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_presales_project_product_line

**索引列表:**
- lendInfoId: 普通 (lendInfoId)
- presalesId: 普通 (presalesId)
- PRIMARY: 主键 (productLineId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| productLineId | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| presalesId | int(11) | 是 |  | MUL |  | 售前项目ID |
| lendInfoId | varchar(64) | 是 |  | MUL |  | 借货主表主键 |
| productFirstName | varchar(255) | 是 |  |  |  | 产品一级 |
| productTypeName | varchar(255) | 是 |  |  |  | 产品类别 |
| itemCode | varchar(255) | 是 |  |  |  | item编码 |
| itemModel | varchar(255) | 是 |  |  |  | item型号 |
| itemDesc | text | 是 |  |  |  | item描述 |
| price | double | 是 |  |  |  | 目录价 |
| productNum | int(11) | 否 | 0 |  |  | 产品数量 |
| orderNum | int(11) | 否 | 0 |  |  | 下单数量 |
| deliverNum | int(11) | 否 | 0 |  |  | 发货数量 |
| hexiaoNum | int(11) | 否 | 0 |  |  | 核销数量 |
| transferNum | int(11) | 否 | 0 |  |  | 转销数量 |
| remark | text | 是 |  |  |  | 备注 |
| effectiveFrom | datetime | 是 |  |  |  | 数据有效开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 数据有效结束时间 |
| source | varchar(25) | 是 | SMS |  |  | 业务含义待确认 |

---

## pm_presales_project_rma_info

**索引列表:**
- contract: 普通 (contract)
- itemcode: 普通 (itemcode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| orderNumber | varchar(11) | 是 |  |  |  | 业务含义待确认 |
| ppliCode | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| orderType | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| contract | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| itemcode | varchar(10) | 是 |  | MUL |  | 业务含义待确认 |
| itemModel | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| description | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| productfirstName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| productName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| orderQty | decimal(32,0) | 是 |  |  |  | 业务含义待确认 |
| dlvQty | decimal(32,0) | 是 |  |  |  | 业务含义待确认 |
| rmaQty | decimal(32,0) | 是 |  |  |  | 业务含义待确认 |
| createDate | date | 是 |  |  |  | 业务含义待确认 |
| canceled | char(1) | 是 |  |  |  | 业务含义待确认 |
| deliveryDate | date | 是 |  |  |  | 业务含义待确认 |
| rmaDate | date | 是 |  |  |  | 业务含义待确认 |

---

## pm_project

**索引列表:**
- department: 普通 (column001)
- PRIMARY: 主键 (projectId)
- projectCode_index: 普通 (projectCode, projectType)
- projectType_projectId_IDX: 普通 (projectType, projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| projectId | int(11) | 否 |  | PRI | auto_increment | 项目头信息主键,跟项目其他具体信息关联 |
| projectType | varchar(45) | 否 | 10 | MUL |  | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx |
| projectCode | varchar(45) | 否 |  | MUL |  | 项目名称 |
| projectName | varchar(200) | 是 |  |  |  | 项目名称 |
| projectState | varchar(11) | 是 |  |  |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| isback | varchar(11) | 是 | 30 |  |  | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 |
| column001 | varchar(255) | 是 |  | MUL |  | 办事处编码 |
| column002 | varchar(255) | 是 |  |  |  | 客户编码--ERP |
| column003 | varchar(255) | 是 |  |  |  | 客户名称--ERP |
| column004 | varchar(255) | 是 |  |  |  | 市场部编码 |
| column005 | varchar(255) | 是 |  |  |  | 系统部ID |
| column006 | varchar(255) | 是 |  |  |  | 拓展部ID |
| column007 | varchar(255) | 是 |  |  |  | 子行业ID |
| column008 | varchar(255) | 是 |  |  |  | 不予跟踪原因 notGrantTailCause |
| column009 | datetime | 是 |  |  |  | 订单创建时间 |
| column010 | varchar(10) | 是 |  |  |  | 项目类型 |
| column011 | varchar(10) | 是 |  |  |  | 项目分类 |
| column012 | varchar(2) | 是 |  |  |  | 项目实施方式 |
| columno12_readonly | int(2) | 是 | -1 |  |  | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly |
| column013 | varchar(255) | 是 |  |  |  | 最终客户名称 |
| column014 | text | 是 |  |  |  | 回退说明 |
| customerProjectName | varchar(255) | 是 |  |  |  | 客户项目名称 |
| salesType | varchar(25) | 是 | 01 |  |  | 销售类型 |
| majorProjectLevel | varchar(255) | 是 |  |  |  | 重大项目级别 |
| compId | int(2) | 是 | 0 |  |  | 公司ID |
| createTime | datetime | 是 |  |  |  | 记录数据创建时间 |
| createBy | varchar(45) | 是 |  |  |  | 记录数据创建用户 |
| updateTime | datetime | 是 |  |  |  | 记录数据最新更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 记录数据最新更新用户 |
| effectiveFrom | datetime | 是 |  |  |  | 数据有效性开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 数据有效性结束时间 |
| disabled | bit(1) | 是 | b'0' |  |  | 数据是否失效 |
| projectStartTime | datetime | 是 |  |  |  | 项目开始实施时间 |
| projectRefreshTime | datetime | 是 |  |  |  | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | 是 |  |  |  | 项目闭环时间点 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| customConfig | json | 是 |  |  |  | 自定义配置 |

---

## pm_project_contract

**索引列表:**
- contract_projectGroupCode_IDX: 普通 (contractNo, projectGroupCode)
- PRIMARY: 主键 (id)
- projectGroupCode_contract_IDX: 普通 (projectGroupCode, contractNo)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| contractNo | varchar(45) | 否 |  | MUL |  | 合同号 |
| projectGroupCode | varchar(45) | 否 |  | MUL |  | 项目组编码 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_group

**索引列表:**
- PRIMARY: 主键 (id)
- projectGroupCode_UNIQUE: 唯一 (projectGroupCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectGroupCode | varchar(45) | 否 |  | UNI |  | 项目组组编码 |
| projectGroupName | varchar(45) | 是 |  |  |  | 项目组名称 |
| projectType | varchar(25) | 是 | 10 |  |  | 项目类型  默认10 为工程管理售后项目 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(15) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_group_relationship

**索引列表:**
- PRIMARY: 主键 (id)
- projectCode: 普通 (projectCode)
- projectGroupCode: 普通 (projectGroupCode)
- smsProjectCode: 普通 (smsProjectCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectGroupCode | varchar(45) | 否 |  | MUL |  | 项目组编码 |
| projectCode | varchar(45) | 是 |  | MUL |  | 项目编码 |
| mergeBranchMark | varchar(45) | 是 |  |  |  | 项目拆分合并 |
| smsProjectCode | varchar(45) | 是 |  | MUL |  | 原SMS项目编码 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_instruction

**索引列表:**
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 项目头关联主键 |
| instructionsInfo | text | 是 |  |  |  | 批示内容或反馈内容 |
| instructionsTime | datetime | 是 |  |  |  | 批示时间或反馈时间 |
| instructionsUser | varchar(45) | 是 |  |  |  | 批示用户或反馈用户 |
| dataType | int(11) | 是 | 0 |  |  | 数据类型  0 批示信息 1 批示反馈 |
| instructionsId | int(11) | 是 |  |  |  | 批示ID 针对批示反馈的信息 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_log

**索引列表:**
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| handleName | varchar(255) | 是 |  |  |  | 操作名称 |
| handleDesc | varchar(255) | 是 |  |  |  | 操作描述或原因 |
| handleUser | varchar(45) | 是 |  |  |  | 操作用户 |
| taskStartTime | datetime | 是 |  |  |  | 操作开始时间 |
| handleEndTime | datetime | 是 |  |  |  | 操作结束时间 |
| handleState | int(11) | 是 |  |  |  | 有无通知用户 0 无 1 有 |

---

## pm_project_maintenance

**索引列表:**
- category: 普通 (category, subCategory)
- createBy: 普通 (createBy)
- createTime: 普通 (createTime)
- officeCode: 普通 (officeCode)
- PRIMARY: 主键 (id)
- processTime_IDX: 普通 (processTime)
- projectCode: 普通 (projectCode)
- projectId: 普通 (projectId)
- projectType: 普通 (projectType)
- subCategory: 普通 (subCategory)
- type: 普通 (type)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 否 |  | MUL |  | 项目头信息主键 |
| projectCode | varchar(45) | 否 |  | MUL |  | 项目名称 |
| projectName | varchar(200) | 是 |  |  |  | 项目名称 |
| projectType | int(11) | 否 | 10 | MUL |  | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | 是 |  |  |  | 项目实施状态 |
| contractNo | varchar(255) | 是 |  |  |  | 合同号 |
| officeCode | varchar(25) | 是 |  | MUL |  | 办事处编码 |
| compId | int(2) | 是 | 1 |  |  | 所属公司 |
| type | varchar(45) | 是 |  | MUL |  | 任务性质 |
| category | varchar(45) | 是 |  | MUL |  | 任务分类 |
| subCategory | varchar(45) | 是 |  | MUL |  | 任务小类 |
| processTime | datetime | 是 |  | MUL |  | 处理时间 |
| processDesc | varchar(1024) | 是 |  |  |  | 事项描述 |
| processStep | varchar(1024) | 是 |  |  |  | 解决进展 |
| remainProblem | varchar(1024) | 是 |  |  |  | 遗留问题 |
| transitHour | float | 是 | 0 |  |  | 在途耗时(h) |
| processHour | float | 是 | 0 |  |  | 处理耗时(h) |
| itemModel | varchar(255) | 是 |  |  |  | 产品型号 |
| softVersion | varchar(255) | 是 |  |  |  | 在网版本 |
| enabledFeatures | varchar(255) | 是 |  |  |  | 启用功能 |
| customTos | varchar(512) | 是 |  |  |  | 自定义主送 |
| customCcs | varchar(512) | 是 |  |  |  | 自定义抄送 |
| hasReport | bit(1) | 否 | b'0' |  |  | 是否有巡检报告 |
| quesnaireId | int(11) | 是 |  |  |  | 问卷ID |
| deliverFileIds | varchar(255) | 是 |  |  |  | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | 是 |  |  |  | 维保状态 |
| industryName | varchar(25) | 是 |  |  |  | 行业 |
| userOffice | varchar(25) | 是 |  |  |  | 用户办事处 |
| year | int(4) | 是 |  |  |  | 所属年度 |
| quarter | int(1) | 是 |  |  |  | 所属季度 |
| month | int(2) | 是 |  |  |  | 所属月份 |
| wsCount | int(2) | 是 |  |  |  | 当前维保服务次数 |
| wafCount | int(2) | 是 |  |  |  | 当前其他服务次数 |
| wsYearCount | int(2) | 是 |  |  |  | 维保服务年次数 |
| wafYearCount | int(2) | 是 |  |  |  | 其他服务年次数 |
| warrantyInfo | varchar(4096) | 是 |  |  |  | 维保信息 |
| serviceInfo | varchar(2048) | 是 |  |  |  | 其他服务信息 |
| remark | varchar(2048) | 是 |  |  |  | 备注 |
| createTime | datetime | 是 |  | MUL |  | 创建时间 |
| createBy | varchar(45) | 是 |  | MUL |  | 创建用户 |
| updateTime | datetime | 是 |  |  |  | 最新更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 最新更新用户 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_project_member

**索引列表:**
- memberCode_IDX: 普通 (memberCode, projectId, projectType)
- PRIMARY: 主键 (id)
- projectId_role: 普通 (projectId, memberRole)
- projectId_type: 普通 (projectId, projectType)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| projectType | varchar(25) | 是 | 10 |  |  | 项目类型 售后10 或售前 20 详见fnd_basic_data |
| memberRole | varchar(45) | 是 |  |  |  | 人员在项目中所处的角色 |
| memberCode | varchar(45) | 是 |  | MUL |  | 人员编码,外部人员为空 |
| memberName | varchar(45) | 是 |  |  |  | 人员名称 |
| phoneNum | varchar(20) | 是 |  |  |  | 电话 |
| email | varchar(45) | 是 |  |  |  | 邮箱 |
| fromFlag | varchar(2) | 是 | 0 |  |  | 信息来源，1表示来源于项目信息，2表示来源于成员信息 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 有效结束时间 |
| effectiveFrom | datetime | 是 |  |  |  | 有效开始时间 |

---

## pm_project_notification

**索引列表:**
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| notifySubject | varchar(255) | 是 |  |  |  | 通知标题 |
| notifyContent | text | 是 |  |  |  | 通知内容 |
| projectId | int(11) | 是 |  | MUL |  | 相关项目ID |
| createTime | datetime | 是 |  |  |  | 创建时间 |
| createBy | varchar(25) | 是 |  |  |  | 创建用户 |

---

## pm_project_notification_state

**索引列表:**
- notifyId: 普通 (notifyId)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| notifyId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| notifyObject | varchar(25) | 是 |  |  |  | 通知主题，系统用户 |
| notifyState | int(11) | 是 |  |  |  | 通知状态，有无通知 0 无 1 有 |
| checkTime | datetime | 是 |  |  |  | 用户查看通知时间 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_product_line

**索引列表:**
- contractNo: 普通 (contractNo)
- id: 普通 (id)
- itemCode: 普通 (itemCode)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | MUL | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 关联主表 |
| contractNo | varchar(45) | 是 |  | MUL |  | 合同号 |
| itemCode | varchar(15) | 是 |  | MUL |  | 产品编码 |
| itemName | varchar(255) | 是 |  |  |  | 产品名称 |
| projectQuantity | int(11) | 是 |  |  |  | 项目产品数量 |
| orderQuantity | int(11) | 是 |  |  |  | 产品订单数量 |
| deliverQuantity | int(11) | 是 |  |  |  | 已发货数量 |
| openQuantity | int(11) | 是 |  |  |  | 未发货数量 |
| orderNumber | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| lineNum | varchar(25) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_related_party

**索引列表:**
- partyRole_parojectId: 普通 (partyRole, projectId)
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| partyRole | varchar(45) | 是 |  | MUL |  | 业务含义待确认 |
| partyCode | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| partyName | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(45) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 更新人 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_shipment

**索引列表:**
- barcode: 普通 (barcode)
- contractNo: 普通 (contractNo, barcode)
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| barcode | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| itemCode | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| itemModel | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| itemName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| receiveName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| emsNum | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| emsCompany | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| packdate | datetime | 是 |  |  |  | 业务含义待确认 |
| contractNo | varchar(50) | 是 |  | MUL |  | 业务含义待确认 |
| installAddress | text | 是 |  |  |  | 业务含义待确认 |
| chProjectId | int(11) | 是 |  |  |  | 串货转移之前的projectId |
| chContractNo | varchar(50) | 是 |  |  |  | 串货转移之前的contractNo |
| transferProjectId | int(11) | 是 |  |  |  | 串货转移之后的projectId |
| transferContractNo | varchar(50) | 是 |  |  |  | 串货转移之后的projectId |
| transferFlag | varchar(2) | 是 | -1 |  |  | 转移标识，默认:-1,转出:1，转入:0 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |

---

## pm_project_state

**索引列表:**
- index_projectId: 唯一 (projectId)
- projectPlanState: 普通 (projectPlanState)
- shipmentState: 普通 (shipmentState)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| projectId | int(11) | 否 |  | PRI |  | 业务含义待确认 |
| projectPlanState | varchar(10) | 是 |  | MUL |  | 工程计划状态 |
| projectplanTime | datetime | 是 |  |  |  | 工程计划状态更新时间 |
| shipmentState | varchar(11) | 是 |  | MUL |  | 项目发货状态 -1 已发货 1 未发货 2部分发货 |
| shipmentTime | datetime | 是 |  |  |  | 发货状态更新时间戳 |
| executionState | varchar(45) | 是 | 5 |  |  | 实施状态 |
| executionStateTime | datetime | 是 |  |  |  | 实施状态更新时间 |
| closeProcessState | varchar(45) | 是 | 10 |  |  | 闭环流程状态 |
| closeProcessStateTime | datetime | 是 |  |  |  | 闭环流程状态更新时间 |

---

## pm_project_supervision

**索引列表:**
- department: 普通 (officeCode)
- PRIMARY: 主键 (id)
- projectCode_index: 普通 (projectCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 否 |  |  |  | 项目头信息主键 |
| projectCode | varchar(45) | 否 |  | MUL |  | 项目名称 |
| projectName | varchar(200) | 是 |  |  |  | 项目名称 |
| channel | varchar(64) | 是 |  |  |  | 代理商/服务商 |
| officeCode | varchar(25) | 是 |  | MUL |  | 办事处编码 |
| type | varchar(25) | 是 |  |  |  | 任务性质 |
| processTime | datetime | 是 |  |  |  | 处理时间 |
| state | bit(1) | 否 | b'0' |  |  | 是否完成 |
| isDelete | bit(1) | 否 | b'0' |  |  | 是否删除 |
| quesnaireId | int(11) | 是 |  |  |  | 问卷ID |
| deliverFileIds | varchar(255) | 是 |  |  |  | 交付件，fnd_files id |
| remark | text | 是 |  |  |  | 备注 |
| createTime | datetime | 是 |  |  |  | 创建时间 |
| createBy | varchar(45) | 是 |  |  |  | 创建用户 |
| updateTime | datetime | 是 |  |  |  | 最新更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 最新更新用户 |

---

## pm_project_task

**索引列表:**
- PRIMARY: 主键 (taskId)
- projectId: 普通 (projectId, projectType)
- projectType: 普通 (projectType, projectId)
- taskTypeCode_Id: 普通 (taskTypeCode, taskTypeId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| taskId | int(11) | 否 |  | PRI | auto_increment | 任务ID |
| projectId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| projectType | varchar(25) | 是 | 10 | MUL |  | 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data |
| contractNo | varchar(45) | 是 |  |  |  | 合同号 |
| taskTypeCode | varchar(45) | 是 |  | MUL |  | 任务类型code，关联基础数据表 |
| taskTypeId | varchar(25) | 是 |  |  |  | 任务类型id，关联基础数据表 |
| taskName | varchar(255) | 是 |  |  |  | 任务名 |
| eventPlanHappenDate | datetime | 是 |  |  |  | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | 是 |  |  |  | 工程计划发生日期 |
| planStartTime | datetime | 是 |  |  |  | 计划开始日期 |
| planEndTime | datetime | 是 |  |  |  | 计划结束日期 |
| actualStartTime | datetime | 是 |  |  |  | 实际开始日期 |
| eventActualFinishDate | datetime | 是 |  |  |  | 实际完成日期 |
| priority | varchar(25) | 是 |  |  |  | 优先级 |
| progress | int(3) | 是 | 0 |  |  | 进度百分比 |
| progressDesc | varchar(255) | 是 |  |  |  | 进度描述 |
| status | varchar(25) | 是 | 0 |  |  | 状态 |
| parentId | int(11) | 是 |  |  |  | 父级任务 |
| remark | text | 是 |  |  |  | 备注 |
| createTime | datetime | 是 |  |  |  | 记录数据创建时间 |
| createBy | varchar(45) | 是 |  |  |  | 记录数据创建用户 |
| updateTime | datetime | 是 |  |  |  | 记录数据最新更新时间 |
| updateBy | varchar(45) | 是 |  |  |  | 记录数据最新更新用户 |
| effectiveFrom | datetime | 是 |  |  |  | 数据有效性开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 数据有效性结束时间 |
| visibleFlag | varchar(2) | 是 | 1 |  |  | 是否可见，1表示可见，2表示不可见 |
| deliverFileIds | varchar(255) | 是 |  |  |  | 上传的交付件 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_project_weekly

**索引列表:**
- PRIMARY: 主键 (weeklyId)
- projectId: 普通 (projectId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| weeklyId | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| projectId | int(11) | 是 |  | MUL |  | 项目信息头ID |
| currentTask | varchar(100) | 是 |  |  |  | 当前工程阶段 |
| taskStartTime | datetime | 是 |  |  |  | 阶段开始时间 |
| taskEndTime | datetime | 是 |  |  |  | 阶段结束时间 |
| taskDeviation | text | 是 |  |  |  | 偏差 |
| remark | text | 是 |  |  |  | 备注 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| weeklyStartTime | datetime | 是 |  |  |  | 报告开始时间 |
| weeklyEndTime | datetime | 是 |  |  |  | 报告结束时间 |
| weeklyState | int(11) | 是 | 0 |  |  | 周报状态 0 草稿 1提交 |

---

## pm_project_weekly_content

**索引列表:**
- PRIMARY: 主键 (id)
- weeklyId: 普通 (weeklyId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| weeklyId | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| optionDesc001 | text | 是 |  |  |  | 业务含义待确认 |
| optionDesc002 | text | 是 |  |  |  | 业务含义待确认 |
| optionType | int(11) | 是 |  |  |  | option对应周报的部分 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_report_line_data

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 报表趋势图数据集合 |
| dataTypeCode | varchar(15) | 是 |  |  |  | 区分统计的哪种数据 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处 |
| conditionValue | varchar(25) | 是 |  |  |  | 条件值 |
| totalValue | varchar(25) | 是 |  |  |  | 总值 |
| specificValue | varchar(25) | 是 |  |  |  | 比值 |
| settingTime | datetime | 是 |  |  |  | 数据固化时间 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |

---

## pm_subcontract_project_callback

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 项目转包回访问卷表 |
| subcontractId | int(11) | 是 |  |  |  | 项目转包ID |
| taskKey | varchar(25) | 是 |  |  |  | 任务类型 |
| taskId | varchar(25) | 是 |  |  |  | 任务ID |
| quesnaireId | int(11) | 是 |  |  |  | 问卷ID |
| quesnaireVersion | int(11) | 是 |  |  |  | 问卷版本 |
| quesnaireState | int(11) | 是 |  |  |  | 状态 -1 草稿 1已提交 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_subcontract_project_header

**索引列表:**
- facilitatorId: 普通 (facilitatorId)
- officeCode: 普通 (officeCode)
- PRIMARY: 主键 (id)
- profitDepCode: 普通 (profitDepCode)
- subcontractNo: 普通 (subcontractNo)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| subcontractName | varchar(512) | 是 |  |  |  | 转包名称 |
| subcontractNo | varchar(64) | 是 |  | MUL |  | 转包合同号 |
| contractNos | varchar(2048) | 是 |  |  |  | 项目合同号 |
| projectIds | varchar(1024) | 是 |  |  |  | 转包的项目ID |
| type | int(11) | 是 |  |  |  | 转包类型 |
| state | int(11) | 否 | 0 |  |  | 转包状态 |
| callbackState | int(11) | 是 |  |  |  | 回访状态 |
| facilitatorId | int(11) | 是 |  | MUL |  | 服务商表ID |
| facilitatorName | varchar(64) | 是 |  |  |  | 服务商名 |
| bankInfo | varchar(255) | 是 |  |  |  | 服务商开户地址 |
| bankAccount | varchar(64) | 是 |  |  |  | 服务商收款账户 |
| officeCode | varchar(25) | 是 |  | MUL |  | 办事处部门 |
| profitDepCode | varchar(25) | 是 |  | MUL |  | 收益部门 |
| isAccrued | bit(1) | 是 |  |  |  | 是否计提 |
| isInvoiced | bit(1) | 是 |  |  |  | 是否提供发票 |
| subcontractAmount | varchar(25) | 是 |  |  |  | 转包价 |
| reason | varchar(512) | 是 |  |  |  | 转包原因 |
| remark | varchar(512) | 是 |  |  |  | 备注 |
| effectiveFrom | datetime | 是 |  |  |  | 有效开始时间 |
| effectiveTo | datetime | 是 |  |  |  | 有效结束时间 |
| zrApproveTime | datetime | 是 |  |  |  | 最新主任审批通过时间 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| orgId | int(2) | 是 | 1 |  |  | 所属公司 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_subcontract_project_line

**索引列表:**
- barcode: 普通 (barcode)
- contractNo: 普通 (contractNo)
- itemCode: 普通 (itemCode)
- PRIMARY: 主键 (id)
- projectId: 普通 (projectId)
- unique_index: 唯一 (subcontractId, barcode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| subcontractId | int(11) | 否 |  | MUL |  | 转包项目Id |
| projectId | int(11) | 是 |  | MUL |  | 原项目Id |
| barcode | varchar(25) | 是 |  | MUL |  | 设备序列号 |
| itemCode | varchar(25) | 是 |  | MUL |  | 设备编码 |
| itemModel | varchar(255) | 是 |  |  |  | 设备型号 |
| itemName | varchar(255) | 是 |  |  |  | 设备名称 |
| contractNo | varchar(50) | 是 |  | MUL |  | 合同号 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |

---

## pm_subcontract_project_payment

**索引列表:**
- PRIMARY: 主键 (id)
- subcontractId: 普通 (subcontractId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| subcontractId | int(11) | 否 |  | MUL |  | 转包项目Id |
| ratio | varchar(10) | 是 |  |  |  | 比例 |
| amount | varchar(25) | 是 |  |  |  | 付款金额 |
| confirmTime | datetime | 是 |  |  |  | 提交时间 |
| paymentTime | datetime | 是 |  |  |  | 付款时间 |
| remark | varchar(512) | 是 |  |  |  | 备注 |
| sseId | bigint(20) | 是 | -1 |  |  | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## pm_subcontract_project_price

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| subcontractId | int(11) | 否 |  |  |  | 转包项目Id |
| contractNo | varchar(50) | 是 |  |  |  | 合同号 |
| orderExecNumber | varchar(25) | 是 |  |  |  | 执行单号 |
| projectCode | varchar(25) | 是 |  |  |  | 项目编码 |
| engineeFee | varchar(25) | 是 |  |  |  | 工程服务价 |
| objId | varchar(64) | 是 |  |  |  | SMS链接参数1 |
| procType | varchar(25) | 是 |  |  |  | SMS链接参数2 |
| price | varchar(25) | 是 |  |  |  | 合同转包价 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |

---

## pm_workflow

**索引列表:**
- objDataKey: 普通 (objType, objId, dataType, dataId)
- PRIMARY: 主键 (id)
- procInstId: 普通 (procInstId)
- worfFlow_objId: 普通 (objId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| processKey | varchar(25) | 否 |  |  |  | 流程定义key |
| taskKey | varchar(25) | 是 |  |  |  | 任务Key |
| applyTime | datetime | 是 |  |  |  | 申请时间 |
| beginTime | datetime | 是 |  |  |  | 开始时间 |
| endTime | datetime | 是 |  |  |  | 结束时间 |
| dueTime | datetime | 是 |  |  |  | 过期时间 |
| procInstId | varchar(64) | 是 |  | MUL |  | 流程实例ID |
| message | varchar(255) | 是 |  |  |  | 处理消息 |
| status | varchar(255) | 否 | PENDING |  |  | 状态 |
| userId | int(11) | 否 | 0 |  |  | userinfo表ID |
| objType | varchar(25) | 否 |  | MUL |  | 对象类型 |
| objId | int(11) | 否 | 0 | MUL |  | 对象Id |
| dataType | varchar(25) | 否 |  |  |  | 数据类型 |
| dataId | int(11) | 否 | 0 |  |  | 数据Id |
| createBy | varchar(45) | 否 |  |  |  | 业务含义待确认 |
| createTime | datetime | 否 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(45) | 否 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |
| orgId | int(2) | 是 | 0 |  |  | 组织ID |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## prob_main

**索引列表:**
- PRIMARY: 主键 (id)
- probNum_IDX: 普通 (probNum, id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| probNum | varchar(25) | 是 |  | MUL |  | 编码 |
| watch | varchar(10) | 是 |  |  |  | 跟踪 |
| theme | varchar(255) | 是 |  |  |  | 主题 |
| desc | text | 是 |  |  |  | 问题描述 |
| solution | text | 是 |  |  |  | 解决方案 |
| status | varchar(10) | 是 |  |  |  | 状态 |
| startdate | date | 是 |  |  |  | 开始日期 |
| duedate | date | 是 |  |  |  | 计划完成日期 |
| attachments | varchar(255) | 是 |  |  |  | 文件 |
| priority | varchar(10) | 是 |  |  |  | 严重级别 |
| productType | text | 是 |  |  |  | 产品类型 |
| trackingUser | varchar(10) | 是 |  |  |  | 跟踪用户 |
| visibleRange | int(1) | 否 | 0 |  |  | 可见范围，0:All, 1:对内 |
| createBy | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveFrom | datetime | 是 |  |  |  | 业务含义待确认 |
| effectiveTo | datetime | 是 |  |  |  | 业务含义待确认 |
| remark | text | 是 |  |  |  | 审批意见 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| probTicketNo | varchar(255) | 是 |  |  |  | 网上问题单号 |
| relatedSceneTypes | varchar(255) | 是 |  |  |  | relatedSceneTypes |
| relatedSceneTypesMark | bigint(20) | 是 |  |  |  | relatedSceneTypes的bitmark |
| mitigationActionTypes | varchar(255) | 是 |  |  |  | mitigationActionTypes |
| mitigationActionTypesMark | bigint(20) | 是 |  |  |  | mitigationActionTypes的bitmark |
| solutionActionTypes | varchar(255) | 是 |  |  |  | solutionActionTypes |
| solutionActionTypesMark | bigint(20) | 是 |  |  |  | solutionActionTypes的bitmark |

---

## prob_product

**索引列表:**
- PRIMARY: 主键 (id)
- probId_Status_IDX: 普通 (probId, status)
- probId_status_item_IDX: 普通 (probId, status, itemCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| probId | int(11) | 是 | 0 | MUL |  | ProbId |
| productCode | varchar(255) | 是 |  |  |  | 产品大类 |
| productSubCode | varchar(255) | 是 |  |  |  | 产品小类 |
| itemCode | varchar(255) | 否 |  |  |  | item编码 |
| itemModel | varchar(255) | 是 |  |  |  | item类型 |
| itemDesc | varchar(255) | 是 |  |  |  | item描述 |
| status | int(11) | 是 | 1 |  |  | 0 失效 1 有效 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## prob_product_component

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| type | varchar(100) | 是 |  |  |  | 分组 |
| name | varchar(100) | 是 |  |  |  | 名称 |
| version | varchar(100) | 是 |  |  |  | 版本 |
| parentId | int(11) | 是 |  |  |  | 父节点 |
| state | bit(1) | 是 | b'1' |  |  | 状态 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |

---

## prob_read_log

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| probId | int(11) | 否 |  |  |  | 业务含义待确认 |
| reader | varchar(25) | 否 |  |  |  | 查阅人 |
| readTime | datetime | 否 |  |  |  | 查阅时间 |
| status | int(1) | 否 | 0 |  |  | 是否已经确认查阅 |
| firstTime | datetime | 是 |  |  |  | 第一次查阅时间 |
| commitTime | datetime | 是 |  |  |  | 确认时间 |

---

## prob_restore

**索引列表:**
- itemModel: 普通 (itemModel)
- PRIMARY: 主键 (id)
- probId_serialNum_IDX: 普通 (probId, serialNum)
- processId: 普通 (processId)
- projectId: 普通 (projectId)
- serialNum: 普通 (serialNum)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 问题修复数据对象 |
| probId | int(11) | 是 | 0 | MUL |  | 涉及到的问题ID |
| serialNum | varchar(50) | 是 |  | MUL |  | 序列号 |
| itemModel | varchar(50) | 是 |  | MUL |  | 设备类型 |
| processId | int(11) | 是 | 0 | MUL |  | 记录任务流程过程中的相关信息 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处编码 |
| conp | varchar(255) | 是 |  |  |  | 任务发布时的软件版本 |
| boot | varchar(100) | 是 |  |  |  | 业务含义待确认 |
| cpld | varchar(100) | 是 |  |  |  | 业务含义待确认 |
| pcb | varchar(100) | 是 |  |  |  | 业务含义待确认 |
| projectId | int(11) | 是 | 0 | MUL |  | 涉及到的项目ID |
| projectName | varchar(255) | 是 |  |  |  | 项目名称 |
| contractNo | varchar(255) | 是 |  |  |  | 合同号 |
| assignee | varchar(25) | 是 |  |  |  | 办理用户 |
| assigneeRole | int(11) | 是 | 0 |  |  | 办理角色 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## prob_restore_process

**索引列表:**
- PRIMARY: 主键 (id)
- probId: 普通 (probId, restoreStatus)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 记录问题修复的流程流转过程 |
| probId | int(11) | 是 |  | MUL |  | 问题ID |
| restoreStatus | int(11) | 是 |  |  |  | 修复任务流转状态 |
| restoreRemark | text | 是 |  |  |  | 流转备注说明 |
| createBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## prob_softwares

**索引列表:**
- affectedType: 普通 (affectedType)
- boot: 普通 (boot)
- conp: 普通 (conp)
- cpld: 普通 (cpld)
- datastate_entry_probId_IDX: 普通 (datastate, entryType, entrySeries, probId)
- pcb: 普通 (pcb)
- PRIMARY: 主键 (id)
- probId_datastate_IDX: 普通 (probId, datastate)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 已知问题影响的软件版本表 |
| probId | int(11) | 是 | 0 | MUL |  | 问题ID |
| conp | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| cpld | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| boot | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| pcb | varchar(100) | 是 |  | MUL |  | 业务含义待确认 |
| manualEntry | varchar(2048) | 是 |  |  |  | 手工录入 |
| manualEntrySub | varchar(2048) | 是 |  |  |  | 手工录入拆解 |
| entryType | varchar(100) | 是 |  |  |  | 版本类型 |
| entrySeries | varchar(100) | 是 |  |  |  | 版本系列 |
| entryStart | varchar(255) | 是 |  |  |  | 版本范围开始 |
| entryEnd | varchar(255) | 是 |  |  |  | 版本范围结束 |
| markStart | varchar(255) | 是 |  |  |  | 缺省补充版本范围开始 |
| markEnd | varchar(255) | 是 |  |  |  | 缺省补充版本范围结束 |
| affectedType | int(11) | 是 | 0 | MUL |  | 影响类型，0：所有系列，1：盒式系列，2：框式系列 |
| groupId | bigint(11) | 是 | 0 |  |  | 分组ID |
| splited | int(11) | 是 | 0 |  |  | 是否拆解 |
| datastate | int(11) | 是 | 1 | MUL |  | 0 失效 1 有效 |
| createBy | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| createTime | datetime | 是 |  |  |  | 业务含义待确认 |
| updateBy | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## project_info_from_sms

**索引列表:**
- orderCode: 普通 (orderCode, org_id)
- org_id: 普通 (org_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| smsId | bigint(11) | 否 | 0 |  |  | 业务含义待确认 |
| orderCode | varchar(25) | 否 |  | MUL |  | 业务含义待确认 |
| predBidDate | datetime | 是 |  |  |  | 业务含义待确认 |
| projectName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| firstChannelCode | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| firstChannelName | varchar(100) | 是 |  |  |  | 业务含义待确认 |
| channelCode | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| channelName | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| contractNo | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| marketName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| systemName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| expendName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| industryName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| industryNewName | varchar(25) | 是 |  |  |  | 对应的子行业 |
| totaljine | decimal(12,2) | 是 |  |  |  | 业务含义待确认 |
| salesName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| officeName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| solutionname | varchar(1000) | 是 |  |  |  | 业务含义待确认 |
| projectpropertyName | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| customerProjectCode | varchar(255) | 是 |  |  |  | 客户项目编码 |
| customerProjectName | varchar(255) | 是 |  |  |  | 客户项目名称 |
| username | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| realname | varchar(128) | 是 |  |  |  | 业务含义待确认 |
| officeCode | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| org_id | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| customInfo | json | 是 |  |  |  | 自定义信息 |
| source | varchar(25) | 是 | SMS |  |  | 数据来源 |

---

## rma_app_info

**索引列表:**
- PRIMARY: 主键 (id)
- sheetID: 唯一 (sheetID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(25) | 否 |  | UNI |  | RMA申请单据代码 |
| applicant | varchar(10) | 是 |  |  |  | 申请发起人 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处或部门编码 |
| customer_name | varchar(255) | 是 |  |  |  | 客户名称 |
| project_name | varchar(255) | 是 |  |  |  | 项目名称 |
| addreID | int(11) | 是 |  |  |  | 收件人ID，关联addressee_info表 |
| application_time | datetime | 是 |  |  |  | 申请发起时间 |
| back | varchar(10) | 是 |  |  |  | 返回类型 |
| tain | varchar(10) | 是 |  |  |  | 维保类型 |
| serve | varchar(10) | 是 |  |  |  | 服务类型 |
| duty_person | varchar(10) | 是 |  |  |  | 负责人 |
| isSend | char(1) | 是 | 0 |  |  | 申请备件状态（0：未发货；1：已发货 2：已接货） |
| isReceive | char(1) | 是 | 0 |  |  | 是否接收(0:未接受 1：已接收) |
| take_place | char(1) | 是 | 0 |  |  | 备件出处(0:未选择 1:供应链；2：库存) 此时的备件出处只是记录临时的状态，但也可算真实的状态，根据系统设定已不可更改，只需等审批确定后 |
| isUnion | int(11) | 是 |  |  |  | 是否联合供应链发货 |
| remark | text | 是 |  |  |  | 备注 |
| data_state | char(1) | 是 | 0 |  |  | 数据状态（0：最新；1：历史数据） |
| his_addre | varchar(64) | 是 |  |  |  | 处理历史数据 |
| his_zipCode | varchar(10) | 是 |  |  |  | 处理历史数据 |
| his_addr | varchar(1024) | 是 |  |  |  | 处理历史数据 |
| his_addre_tel | varchar(25) | 是 |  |  |  | 处理历史数据 |
| version_no | int(11) | 是 | 0 |  |  | 发货配置版本号 |
| insteadState | int(11) | 是 | 0 |  |  | 业务含义待确认 |
| rma_back_time | datetime | 是 |  |  |  | 技服执行坏件返回时间 |
| rmaRoleIsPass | int(11) | 是 |  |  |  | 故障审核是否通过 0否1是 |
| rmaRoleOpinion | varchar(255) | 是 |  |  |  | 故障审核审批意见 |
| rmaRoleAuditTime | datetime | 是 |  |  |  | 故障审核时间 |
| rmaRoleAuditUser | varchar(25) | 是 |  |  |  | 故障审核用户 |
| qaRoleIsPass | int(11) | 是 |  |  |  | 质量审核是否通过 |
| qaRoleOpinion | varchar(255) | 是 |  |  |  | 质量审核审批意见 |
| qaRoleAuditTime | datetime | 是 |  |  |  | 质量审核时间 |
| qaRoleAuditUser | varchar(25) | 是 |  |  |  | 质量审核用户 |
| insteadLicense | int(11) | 是 | 0 |  |  | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | 是 |  |  |  | 授权License接收邮箱 |
| licenseMailTime | datetime | 是 |  |  |  | 授权License邮件发送时间 |
| customInfo | json | 是 |  |  |  | 自定义信息 |

---

## rma_applicant

**索引列表:**
- id: 唯一 (id)
- PRIMARY: 主键 (id)
- spare_code: 唯一 (spare_code)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| spare_code | varchar(8) | 是 |  | UNI |  | 流水号 |
| product_code | varchar(50) | 是 |  |  |  | 产品号 |
| product_name | varchar(200) | 是 |  |  |  | 产品名称 |
| username | varchar(50) | 是 |  |  |  | 用户名称 |
| project_name | varchar(200) | 是 |  |  |  | 项目名称/申请原因 |
| old_bar_code | varchar(20) | 是 |  |  |  | 旧设备序列号 |
| user_linkman | varchar(50) | 是 |  |  |  | 用户联系人 |
| back_type | varchar(1) | 是 |  |  |  | 返回类型('1'为"开坏箱",'2'为"开局坏",'3'为“网上运行坏”,'4'为"备件发货坏",'5'为"其他") |
| back_state | varchar(200) | 是 |  |  |  | 返回类型说明 |
| back_num | varchar(11) | 是 |  |  |  | 返回数量 |
| user_linkman_telephone | varchar(50) | 是 |  |  |  | 用户联系人电话 |
| applicant_time | datetime | 是 |  |  |  | 申请时间 |
| problem_description | text | 是 |  |  |  | 问题描述 |
| analysis_process | varchar(1000) | 是 |  |  |  | 现场分析过程(上传) |
| duty_person | varchar(50) | 是 |  |  |  | 代理公司和负责人 |
| start_first_time | varchar(50) | 是 |  |  |  | 初次运行时间 |
| problem_first_time | varchar(50) | 是 |  |  |  | 故障发生时间 |
| applicant_person | varchar(50) | 是 |  |  |  | 申请人 |
| take_place | varchar(1) | 是 | 1 |  |  | 取处(1为供应链部门，2为库存) |
| os_id | varchar(1000) | 是 |  |  |  | 库存id |
| address | text | 是 |  |  |  | 地址 |
| zip_code | varchar(50) | 是 |  |  |  | 邮政编码 |
| tain_type | varchar(1) | 是 |  |  |  | 维保类型(‘1’为“服务合同”,'2'为"项目订单") |
| project_code | varchar(50) | 是 |  |  |  | 项目订单号(针对维保类型为项目订单) |
| serve_type | varchar(1) | 是 |  |  |  | 服务类型('1'为“坏件先退”,'2'为“好件先行”) |
| remark | text | 是 |  |  |  | 备注 |
| isPass | varchar(1) | 是 | 0 |  |  | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | 是 | 0 |  |  | 是否发货(0为未发货，1为已发货) |
| rma_type | varchar(1) | 是 | 0 |  |  | 备件类型(0为显示，1为不显示) |
| isNew | varchar(1) | 是 | y |  |  | 业务含义待确认 |
| isChange_duty | varchar(1) | 是 | n |  |  | 业务含义待确认 |
| opinion | text | 是 |  |  |  | 业务含义待确认 |

---

## rma_bar

**索引列表:**
- PRIMARY: 主键 (id)
- rma_id: 普通 (rma_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(10) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| rma_id | int(10) | 是 |  | MUL |  | 业务含义待确认 |
| old_bar_code | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| item_code | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| item_name | varchar(1000) | 是 |  |  |  | 业务含义待确认 |
| project_code | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| project_name | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| problem_description | text | 是 |  |  |  | 业务含义待确认 |
| back_state | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| start_first_time | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| problem_first_time | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| analysis_process | varchar(200) | 是 |  |  |  | 业务含义待确认 |
| tain_process | varchar(200) | 是 |  |  |  | 业务含义待确认 |
| isOK | varchar(1) | 是 | 0 |  |  | 是否核销(0为未核销 1为已核销) |
| hexiao_time | datetime | 是 |  |  |  | 核销时间 |
| isBack | varchar(1) | 是 | 0 |  |  | 是否返回(0为未返回1为已返回) |
| back_time | datetime | 是 |  |  |  | 返回时间 |
| EMS | varchar(20) | 是 |  |  |  | 快递单号 |
| EMS_company | varchar(20) | 是 |  |  |  | 快递公司 |
| receive_person | varchar(10) | 是 |  |  |  | 收件人 |
| back_type | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| tain_type | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| serve_type | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| spare_code | varchar(15) | 是 |  |  |  | 业务含义待确认 |

---

## rma_info2mes_result

**索引列表:**
- PRIMARY: 主键 (id)
- sheetID: 普通 (sheetID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(15) | 否 |  | MUL |  | RMA申请流水号，多个合同号_n |
| type | varchar(1) | 否 |  |  |  | 接口上传结果，S、E |
| message | varchar(255) | 否 |  |  |  | 上传结果信息 |
| xmlStr | text | 是 |  |  |  | 上传的xml：rmaInfoHeader |
| xmlStr1 | text | 是 |  |  |  | 上传的xml：rmaInfoDeatil |
| createTime | datetime | 是 |  |  |  | 上传时间 |

---

## rma_repair_report_from_mes

**索引列表:**
- PRIMARY: 主键 (id)
- sheetId_where_index: 普通 (sheetId)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetId | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| barCode | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| contractNo | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| result | tinytext | 是 |  |  |  | 业务含义待确认 |
| path | varchar(255) | 是 |  |  |  | 业务含义待确认 |

---

## rma_spare_info

**索引列表:**
- PRIMARY: 主键 (id)
- tx_id: 普通 (tx_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| tx_id | int(11) | 是 |  | MUL |  | 交易号(关联application_transInfo表） |
| item_code | varchar(15) | 是 |  |  |  | 物料号 |
| item_name | varchar(255) | 是 |  |  |  | 物料名称 |
| contractNo | varchar(25) | 是 |  |  |  | 合同号 |
| contractRemark | varchar(4096) | 是 |  |  |  | 合同备注 |
| project_name | varchar(255) | 是 |  |  |  | 项目名称 |
| problem_desc | text | 是 |  |  |  | 问题描述 |
| first_working_time | varchar(25) | 是 |  |  |  | 第一次运行时间 |
| conk_out_time | varchar(25) | 是 |  |  |  | 故障发生时间 |
| doa_path | varchar(100) | 是 |  |  |  | doa故障分析单（下载路径） |
| check_path | varchar(100) | 是 |  |  |  | 检测报告(下载路径) |
| repair_state | char(1) | 是 |  |  |  | 维修状态（保留字段） |
| isBack | char(1) | 是 | 0 |  |  | 坏件是否返回（0：未返回;1:已返回） |
| back_time | datetime | 是 |  |  |  | 返回时间 |
| isOK | char(1) | 是 | 0 |  |  | 核销状态(0:未核销；1:已核销) |
| hexiao_time | datetime | 是 |  |  |  | 核销时间 |
| analysis_state | int(11) | 是 |  |  |  | 坏件故障分析状态  -1 未分析  1 已分析 |
| insteadLicense | int(11) | 是 | 0 |  |  | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | 是 |  |  |  | 授权License接收邮箱 |
| licenseMailTime | datetime | 是 |  |  |  | 授权License邮件发送时间 |

---

## role

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| roleId | int(11) | 否 |  |  |  | 业务含义待确认 |
| roleName | varchar(10) | 否 |  |  |  | 业务含义待确认 |
| status | int(11) | 是 |  |  |  | 业务含义待确认 |
| mark | text | 是 |  |  |  | 业务含义待确认 |

---

## serve_type

**索引列表:**
- PRIMARY: 主键 (id)
- serve_where_index: 普通 (serve)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| serve | varchar(10) | 否 |  | MUL |  | 业务含义待确认 |
| serve_type | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| remark | text | 是 |  |  |  | 备注 |
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |

---

## shipment_barcode_from_spms_unique

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| contract_code | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| barcode | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| itemCode | varchar(16) | 是 |  |  |  | 业务含义待确认 |
| barcode2 | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| itemCode2 | varchar(16) | 是 |  |  |  | 业务含义待确认 |
| rmaState | int(1) | 否 | 0 |  |  | 业务含义待确认 |

---

## sms_ofst_contract_head_sap

**索引列表:**
- index_contract_num: 普通 (contract_num)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | bigint(20) | 否 |  | PRI | auto_increment | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 |
| contract_num | varchar(45) | 是 |  | MUL |  | 业务含义待确认 |
| batch_code | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| project_name | varchar(200) | 是 |  |  |  | 业务含义待确认 |
| order_num | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| client_supplier_code | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| client_supplier_name | varchar(200) | 是 |  |  |  | 业务含义待确认 |
| contract_money_amount | decimal(20,2) | 否 |  |  |  | 业务含义待确认 |
| delivered_money_amount | decimal(20,2) | 否 |  |  |  | 业务含义待确认 |
| collected_money_amount | decimal(20,2) | 否 |  |  |  | 业务含义待确认 |
| collected_money_ratio | double | 是 | 0 |  |  | 业务含义待确认 |
| receivables_money_amount | decimal(20,2) | 是 |  |  |  | 业务含义待确认 |
| over_due_money_amount | decimal(20,2) | 是 |  |  |  | 业务含义待确认 |
| maketing_department_name | varchar(40) | 是 |  |  |  | 业务含义待确认 |
| office_name | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| industry_name | varchar(40) | 是 |  |  |  | 业务含义待确认 |
| marketing_representative_name | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| currency_name | varchar(25) | 是 |  |  |  | 币种 |
| create_by | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| create_time | datetime | 是 |  |  |  | 业务含义待确认 |
| update_by | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| update_time | datetime | 是 |  |  |  | 业务含义待确认 |
| effective_from | datetime | 是 |  |  |  | 业务含义待确认 |
| effective_to | datetime | 是 |  |  |  | 业务含义待确认 |
| import_batch_num | varchar(12) | 是 |  |  |  | 业务含义待确认 |
| contract_create_date | datetime | 是 |  |  |  | SAP合同创建日期 |
| projectCode | varchar(80) | 是 |  |  |  | 业务含义待确认 |
| marketCode | varchar(80) | 是 |  |  |  | 业务含义待确认 |
| systemId | int(11) | 是 |  |  |  | 业务含义待确认 |
| industryId | int(11) | 是 |  |  |  | 业务含义待确认 |
| officeCode | varchar(80) | 是 |  |  |  | 业务含义待确认 |
| expendId | int(11) | 是 |  |  |  | 业务含义待确认 |
| usernamec | varchar(10) | 是 |  |  |  | 销售用户账号 |
| latest_ship_date | datetime | 是 |  |  |  | 交货日期 |
| usernamec2 | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| systemid_o | int(11) | 是 |  |  |  | 业务含义待确认 |
| expendid_o | int(11) | 是 |  |  |  | 业务含义待确认 |
| industry_name_o | varchar(40) | 是 |  |  |  | 业务含义待确认 |
| dataSource | varchar(25) | 是 | CRM |  |  | 业务含义待确认 |

---

## spare_parts

**索引列表:**
- PRIMARY: 主键 (id)
- spare_code: 普通 (spare_code)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(10) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| bar_code | varchar(25) | 是 |  |  |  | 备件序列号 |
| spare_code | varchar(50) | 是 |  | MUL |  | 流水号 |
| action_time | varchar(50) | 否 |  |  |  | 操作时间 |
| isOK | char(1) | 是 | 0 |  |  | 设备状态(是否核销，0为未核销，1为核销) |
| isNew | char(1) | 是 | y |  |  | 数据状态(是否是最新的数据) |
| in_time | varchar(50) | 是 |  |  |  | 收货时间 |
| out_time | varchar(50) | 是 |  |  |  | 发货时间 |
| contract_sub_type | varchar(5) | 是 |  |  |  | 类型(0为RMA ,1为项目保障,2为库存) |
| EMS | varchar(50) | 是 |  |  |  | 快递单号 |
| EMS_company | varchar(50) | 是 |  |  |  | 快递公司 |
| item_code | varchar(50) | 是 |  |  |  | 物料号 |
| item_name | varchar(200) | 是 |  |  |  | 物料名称 |
| tain_process | varchar(200) | 是 |  |  |  | 检测报告 |
| isSure | varchar(1) | 是 | 0 |  |  | 确认(1待确认,2以确认) |

---

## spare_parts_applicant

**索引列表:**
- PRIMARY: 主键 (id)
- spare_code: 唯一 (spare_code)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(10) | 否 |  | PRI | auto_increment | 主键 |
| applicant_person | varchar(50) | 是 |  |  |  | 申请人 |
| applicant_time | datetime | 是 |  |  |  | 申请时间 |
| applicant_department | varchar(50) | 是 |  |  |  | 申请部门 |
| spare_code | varchar(50) | 是 |  | UNI |  | 流水号 |
| applicant_reason | varchar(500) | 是 |  |  |  | 申请原因 |
| remark | text | 是 |  |  |  | 备注 |
| zip_code | varchar(50) | 是 |  |  |  | 邮政编码 |
| isPass | varchar(1) | 是 | 0 |  |  | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | 否 | 0 |  |  | 是否通过(0为未发货，1为已发货) |
| address | varchar(200) | 是 |  |  |  | 地址 |
| receive_person | varchar(200) | 是 |  |  |  | 收件人 |
| receive_person_tel | varchar(200) | 是 |  |  |  | 收件人电话 |
| spare_parts_type | varchar(1) | 是 |  |  |  | 备件类型(0为项目保障，1为库存) |
| duty_person | varchar(10) | 是 |  |  |  | 责任人 |
| applicant_type | varchar(1) | 是 | 0 |  |  | 申请类型(0为普通申请，1为转移申请) |
| isChange_duty | varchar(1) | 是 | 1 |  |  | 转移类型(0为转移责任人，1为不转移责任人) |
| isQuit | char(1) | 是 |  |  |  | 是否为离职原因导致责任人变更，0：否，1：是 |
| isReceive | varchar(1) | 是 | 0 |  |  | 是否收到(0为未收到，1为收到) |
| transfer_time | datetime | 是 |  |  |  | 转移时间 |
| applicant_project | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| start_time | date | 是 |  |  |  | 业务含义待确认 |
| promise_returntime | date | 是 |  |  |  | 业务含义待确认 |
| kept_place | varchar(255) | 是 |  |  |  | 业务含义待确认 |
| beforeChange_spareCode | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| change_type | char(1) | 是 |  |  |  | 业务含义待确认 |

---

## sys_state_or_type

**索引列表:**
- PRIMARY: 主键 (id)
- st: 普通 (resolveCode)
- stCode: 普通 (stCode)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| stCode | varchar(25) | 是 |  | MUL |  | 业务含义待确认 |
| stName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| resolveCode | varchar(10) | 是 |  | MUL |  | 业务含义待确认 |
| resolveName | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| validity | int(11) | 是 | 1 |  |  | 1有效 0 无效 |
| remark | varchar(100) | 是 |  |  |  | 说明 |

---

## t_menu

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 用户菜单定义 |
| pid | int(11) | 否 | 0 |  |  | 父菜单ID |
| name | varchar(100) | 是 |  |  |  | 菜单名称 |
| url | varchar(100) | 是 |  |  |  | 超链接 |
| icon | varchar(64) | 是 |  |  |  | 菜单对应的class样式，会影响菜单的显示效果 |
| sort | int(11) | 是 | 0 |  |  | 子菜单排序 |
| status | bit(1) | 是 | b'1' |  |  | 是否有效，1：有效，0：失效 |
| target | varchar(15) | 是 |  |  |  | 业务含义待确认 |
| remark | varchar(255) | 是 |  |  |  | 备注说明 |
| create_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| crate_time | datetime | 是 |  |  |  | 业务含义待确认 |
| update_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| update_time | datetime | 是 |  |  |  | 业务含义待确认 |

---

## t_permission

**索引列表:**
- permission_name: 普通 (permission_name)
- PRIMARY: 主键 (permission_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| permission_id | int(11) | 否 |  | PRI | auto_increment | 权限ID |
| permission_name | varchar(100) | 是 |  | MUL |  | 权限字符串 |
| create_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| create_time | datetime | 是 |  |  |  | 业务含义待确认 |
| update_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| update_time | datetime | 是 |  |  |  | 业务含义待确认 |

---

## t_resource

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 系统资源需要的权限定义 |
| url | varchar(100) | 是 |  |  |  | 资源请求地址 |
| authc | varchar(255) | 是 |  |  |  | 需要的权限控制 ， 类似于 authc,roles[admin],perms[admin:create] |
| priority | int(11) | 是 | 0 |  |  | 访问资源权限排序，越低越往后排 |
| remark | varchar(255) | 是 |  |  |  | 关于资源定义的备注说明 |
| status | int(11) | 是 | 1 |  |  | 数据有效性0 失效 1 有效 |
| create_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| create_time | datetime | 是 |  |  |  | 业务含义待确认 |
| update_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| update_time | datetime | 是 |  |  |  | 业务含义待确认 |

---

## t_role

**索引列表:**
- PRIMARY: 主键 (role_id)
- role_name: 普通 (role_name)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| role_id | int(11) | 否 |  | PRI | auto_increment | 角色ID |
| role_name | varchar(100) | 是 |  | MUL |  | 角色名称 |
| role_name_zn | varchar(100) | 是 |  |  |  | 中文别名 |
| home_page | varchar(100) | 是 |  |  |  | 角色默认主页 |
| priority | int(11) | 是 | 100 |  |  | 角色优先级，默认100，优先级最高 |
| status | smallint(1) | 是 | 1 |  |  | 角色有效性，1有效，0无效 |
| create_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| create_time | datetime | 是 |  |  |  | 业务含义待确认 |
| update_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| update_time | datetime | 是 |  |  |  | 业务含义待确认 |
| remark | varchar(255) | 是 |  |  |  | 备注说明 |

---

## t_role_menu

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 角色菜单权限 |
| role_id | int(11) | 是 |  |  |  | 角色ID |
| menu_id | int(11) | 是 |  |  |  | 菜单ID |
| create_time | datetime | 是 |  |  |  | 创建时间 |
| create_by | varchar(25) | 是 |  |  |  | 创建用户 |

---

## t_role_permission

**索引列表:**
- permission_id: 普通 (permission_id)
- PRIMARY: 主键 (id)
- role_id: 唯一 (role_id, permission_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 角色-权限 一对多 |
| role_id | int(11) | 是 |  | MUL |  | 角色ID |
| permission_id | int(11) | 是 |  | MUL |  | 权限ID |

---

## t_user

**索引列表:**
- PRIMARY: 主键 (user_id)
- unique_username: 唯一 (user_name)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| user_id | int(11) | 否 |  | PRI | auto_increment | 用户ID |
| user_name | varchar(25) | 是 |  | UNI |  | 用户名称 |
| password | varchar(100) | 是 |  |  |  | 密码 |
| create_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| create_time | datetime | 否 | CURRENT_TIMESTAMP |  |  | 业务含义待确认 |
| update_by | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| update_time | datetime | 是 |  |  | on update CURRENT_TIMESTAMP | 业务含义待确认 |
| status | smallint(1) | 否 | 1 |  |  | 用户状态，0：失效，1有效，2：锁定 |
| needChangePwd | bit(1) | 否 | b'1' |  |  | 用户创建后需要修改密码判断 |
| loginErrorCount | int(1) | 否 | 0 |  |  | 用户密码输入错误次数 |
| isSysUser | smallint(1) | 否 | 0 |  |  | 是否为系统用户,0为普通用户 |
| userCustom1 | varchar(50) | 是 |  |  |  | 用户自定义字段1 |
| userCustom2 | varchar(50) | 是 |  |  |  | 用户自定义字段2 |
| userCustom3 | varchar(50) | 是 |  |  |  | 用户自定义字段3 |
| userCustom4 | int(11) | 是 | 0 |  |  | 用户自定义字段4 |
| userCustom5 | int(11) | 是 | 0 |  |  | 用户自定义字段5 |

---

## t_user_info

**索引列表:**
- compID: 普通 (compID)
- depID: 普通 (depID)
- fk_userInfo_userId: 唯一 (user_id, compID)
- jobID: 普通 (jobID)
- PRIMARY: 主键 (id)
- reportTo: 普通 (reportTo)
- wfreportTo: 普通 (wfreportTo)
- workNo: 普通 (workNo)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 员工ID，外键 |
| workNo | varchar(25) | 否 |  | MUL |  | 工号 |
| realName | varchar(50) | 否 |  |  |  | 姓名 |
| eName | varchar(50) | 是 |  |  |  | 英文名 |
| compID | int(11) | 否 | 0 | MUL |  | 公司ID |
| depID | int(11) | 否 | 0 | MUL |  | 部门ID |
| jobID | int(11) | 否 | 0 | MUL |  | 岗位ID |
| reportTo | int(11) | 是 |  | MUL |  | 直接上级 |
| wfreportTo | int(11) | 是 |  | MUL |  | 职能上级 |
| empStatus | int(11) | 否 | 1 |  |  | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | 是 |  |  |  | 岗位状态 |
| empType | int(11) | 是 |  |  |  | 聘用类型：1：正式，3：实习生 |
| sex | smallint(1) | 是 |  |  |  | 性别：1：男，0：女 |
| birthday | date | 是 |  |  |  | 生日 |
| email | varchar(50) | 是 |  |  |  | 邮箱 |
| mobile | varchar(50) | 是 |  |  |  | 手机 |
| telphone | varchar(50) | 是 |  |  |  | 座机 |
| avatar | varchar(500) | 是 |  |  |  | 头像 |
| remark | varchar(100) | 是 |  |  |  | 备注 |
| state | int(11) | 是 | 1 |  |  | 状态 |
| user_id | int(11) | 是 |  | MUL |  | userId |
| custom1 | int(11) | 是 |  |  |  | 预留字段1 |
| custom2 | int(11) | 是 |  |  |  | 预留字段2 |
| custom3 | varchar(50) | 是 |  |  |  | 预留字段3 officeCode |
| custom4 | varchar(50) | 是 |  |  |  | 预留字段4 projectTypes |
| custom5 | varchar(4096) | 是 |  |  |  | 预留字段5 areaPower |

---

## t_user_role

**索引列表:**
- PRIMARY: 主键 (id)
- t_user_role_ibfk_2: 普通 (role_id)
- unique_userId_roleId: 唯一 (user_id, role_id, comp_id)
- user_id: 普通 (user_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 用户-角色  一对多 |
| user_id | int(11) | 否 |  | MUL |  | 用户ID |
| role_id | int(11) | 否 |  | MUL |  | 角色ID |
| comp_id | int(11) | 是 |  |  |  | 公司ID |

---

## tain_type

**索引列表:**
- PRIMARY: 主键 (id)
- tain_where_index: 普通 (tain)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| tain | varchar(10) | 否 |  | MUL |  | 业务含义待确认 |
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| tain_type | varchar(50) | 是 |  |  |  | 业务含义待确认 |
| remark | text | 是 |  |  |  | 备注 |

---

## transnum

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| transNum | int(50) | 是 |  |  |  | 业务含义待确认 |

---

## tx_info

**索引列表:**
- PRIMARY: 主键 (tx_id)
- sheetID: 普通 (sheetID)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| tx_id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(10) | 是 |  | MUL |  | 单据代码 |
| tx_type | int(1) | 是 |  |  |  | 单据类型(0:RMA单据;1:借用申请 2：转移) |
| spare_serialNum | varchar(50) | 是 |  |  |  | 备件序列号 |
| sendout_place | char(1) | 是 |  |  |  | 历史记录（1：供应链；2：库存） |
| sendout_whsCode | varchar(10) | 是 |  |  |  | 备件发出库房 |
| send_time | datetime | 是 |  |  |  | 出库时间 |
| receving_place | varchar(50) | 是 |  |  |  | 备件接受地 |
| receving_whsCode | varchar(10) | 是 |  |  |  | 备件接收库房 |
| receive_time | datetime | 是 |  |  |  | 收货时间 |
| quantity | int(11) | 是 | 1 |  |  | 数量 |
| EMS_num | varchar(255) | 是 |  |  |  | 快递单号 |
| EMS_company | varchar(255) | 是 |  |  |  | 快递公司 |
| addressee | varchar(25) | 是 |  |  |  | 收件人 |
| isRMA | char(1) | 是 | 0 |  |  | 是否是RMA的坏件返回（1：是;0:好件） |
| version_no | int(11) | 是 | 0 |  |  | 版本号  -1时为历史选择的数据 |
| detail_id | int(11) | 是 |  |  |  | 库存表中的id |
| instead_of_num | varchar(25) | 是 |  |  |  | 好件替换坏件关系 |
| shiftimes | int(11) | 是 |  |  |  | 备件经过转移次数 |
| turnovertimes | int(11) | 是 |  |  |  | 业务含义待确认 |
| allottimes | int(11) | 是 |  |  |  | 业务含义待确认 |
| instead_time | datetime | 是 |  |  |  | 业务含义待确认 |
| datastate | int(1) | 是 | 1 |  |  | 保持历史数据有效性 0 失效 1 有效 |

---

## warehouse

**索引列表:**
- PRIMARY: 主键 (whs_id)
- whs_code: 普通 (whs_code)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| whs_id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| whs_code | varchar(10) | 是 |  | MUL |  | 库房编码 |
| whs_name | varchar(25) | 是 |  |  |  | 库房名称 |
| whs_addr | varchar(255) | 是 |  |  |  | 库房地址 |
| username | varchar(10) | 是 |  |  |  | 负责人工号 |
| department | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| contact_tel | varchar(15) | 是 |  |  |  | 联系电话 |
| contact_mail | varchar(50) | 是 |  |  |  | 联系邮箱 |
| remark | text | 是 |  |  |  | 备注 |
| whs_state | char(1) | 是 | 1 |  |  | 1:有效 |

---

## warehouse_info

**索引列表:**
- PRIMARY: 主键 (info_id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| info_id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| item_code | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| item_name | varchar(100) | 是 |  |  |  | 业务含义待确认 |
| whs_code | varchar(10) | 是 |  |  |  | 业务含义待确认 |
| quantity | int(11) | 是 |  |  |  | 业务含义待确认 |
| item_state | char(1) | 是 |  |  |  | 0:坏件 1：好件 |

---

## warehouse_info_detail

**索引列表:**
- info_id: 普通 (info_id)
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| info_id | int(11) | 是 |  | MUL |  | 业务含义待确认 |
| spare_serialNum | varchar(25) | 是 |  |  |  | 业务含义待确认 |
| demand_type | varchar(25) | 是 |  |  |  | 状态维护在sys_state_or_type |
| tx_id | int(11) | 是 |  |  |  | 业务含义待确认 |
| state | varchar(2) | 是 |  |  |  | 1：在库 2：客户 3：被申请 |
| data_state | char(1) | 是 | 1 |  |  | 0:历史 1：最新 |
| in_time | datetime | 是 |  |  |  | 入库时间 |
| finance_in_time | datetime | 是 |  |  |  | 财务入库时间 |
| analyse_in_time | datetime | 是 |  |  |  | 业务含义待确认 |
| analyse_out_time | datetime | 是 |  |  |  | 业务含义待确认 |
| gaizhi_in_time | datetime | 是 |  |  |  | 业务含义待确认 |
| gaizhi_out_time | datetime | 是 |  |  |  | 业务含义待确认 |
| remark | text | 是 |  |  |  | 业务含义待确认 |

---

## warranty_change_logs

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| username | varchar(10) | 否 |  |  |  | 业务含义待确认 |
| updateType | int(11) | 是 |  |  |  | 业务含义待确认 |
| barcode | varchar(20) | 是 |  |  |  | 业务含义待确认 |
| warrantyStartTime | datetime | 是 |  |  |  | 业务含义待确认 |
| warrantyEndTime | datetime | 是 |  |  |  | 业务含义待确认 |
| warrantyTimes | int(11) | 是 |  |  |  | 业务含义待确认 |
| updateTime | datetime | 是 |  |  |  | 业务含义待确认 |

---

## warranty_info

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| packlistId | int(11) | 是 |  |  |  | 标识已经同步 |
| contractId | int(11) | 是 |  |  |  | 标识已经同步 |
| barCode | varchar(25) | 是 |  |  |  | 序列号 |
| officeCode | varchar(25) | 是 |  |  |  | 办事处 |
| projectName | varchar(255) | 是 |  |  |  | 项目名称 |
| contractNo | varchar(25) | 是 |  |  |  | 合同号 |
| contractType | int(11) | 是 |  |  |  | 合同类型 |
| customerName | varchar(255) | 是 |  |  |  | 客户名称 |
| itemCode | varchar(8) | 是 |  |  |  | 物料编码 |
| itemName | varchar(255) | 是 |  |  |  | 物料描述 |
| warrantyLevel | varchar(8) | 是 |  |  |  | 维保级别 |
| warrantyStartTime | datetime | 是 |  |  |  | 维保开始时间 |
| warrantyEndTime | datetime | 是 |  |  |  | 维保结束时间 |
| warrantyLimit | int(11) | 是 |  |  |  | 维保年限 |

---

## workflow_info

**索引列表:**
- PRIMARY: 主键 (id)

| 字段名 | 数据类型 | 可空 | 默认值 | 键 | 额外 | 字段描述 |
|--------|----------|------|--------|----|----|----------|
| id | int(11) | 否 |  | PRI | auto_increment | 业务含义待确认 |
| sheetID | varchar(10) | 否 |  |  |  | 业务含义待确认 |
| sheet_type | char(1) | 是 |  |  |  | 单据类型（0：RMA 1：借用 2：转移） |
| workflow_action | char(1) | 否 |  |  |  | 所需做的操作(1:审批选择备件 ；2：发货确认 ；3：接货确认 5：重新审批 6：RMA申请坏件替换关系确认 4：坏件返回确认 ，7.坏件核销 8 技服执行坏件返回确认) |
| action_people | varchar(10) | 否 |  |  |  | 操作的用户 |
| action_state | char(1) | 否 |  |  |  | 1:待完成 2：已完成 |
| node | int(11) | 是 |  |  |  | 节点 |

---

