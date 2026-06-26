# 项目转包表结构（pm_subcontract_*）

> 数据库：dppms_d365 (MySQL)  
> 模块：项目转包管理  
> 命名前缀：pm_subcontract_ / pm_facilitator

---

## 1. pm_subcontract_project_header（转包项目主表）

项目转包核心信息表，记录转包项目的基本属性、服务商信息和流程状态。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 转包项目主键ID |
| subcontractName | VARCHAR(512) | DEFAULT NULL | '' | 转包名称 |
| subcontractNo | VARCHAR(64) | DEFAULT NULL, MUL | '' | 转包合同号 |
| contractNos | VARCHAR(2048) | DEFAULT NULL | '' | 项目合同号（逗号分隔） |
| projectIds | VARCHAR(1024) | DEFAULT NULL | '' | 转包的项目ID（逗号分隔） |
| type | INT(11) | DEFAULT NULL | NULL | 转包类型 |
| state | INT(11) | NOT NULL | 0 | 转包状态 |
| callbackState | INT(11) | DEFAULT NULL | NULL | 回访状态 |
| facilitatorId | INT(11) | DEFAULT NULL, MUL | NULL | 服务商表ID，关联pm_facilitator.id |
| facilitatorName | VARCHAR(64) | DEFAULT NULL | '' | 服务商名 |
| bankInfo | VARCHAR(255) | DEFAULT NULL | '' | 服务商开户地址 |
| bankAccount | VARCHAR(64) | DEFAULT NULL | '' | 服务商收款账户 |
| officeCode | VARCHAR(25) | DEFAULT NULL, MUL | '' | 办事处部门 |
| profitDepCode | VARCHAR(25) | DEFAULT NULL, MUL | '' | 收益部门 |
| isAccrued | BIT(1) | DEFAULT NULL | NULL | 是否计提 |
| isInvoiced | BIT(1) | DEFAULT NULL | NULL | 是否提供发票 |
| subcontractAmount | VARCHAR(25) | DEFAULT NULL | '' | 转包价 |
| reason | VARCHAR(512) | DEFAULT NULL | '' | 转包原因 |
| remark | VARCHAR(512) | DEFAULT NULL | '' | 备注 |
| effectiveFrom | DATETIME | DEFAULT NULL | NULL | 有效开始时间 |
| effectiveTo | DATETIME | DEFAULT NULL | NULL | 有效结束时间 |
| zrApproveTime | DATETIME | DEFAULT NULL | NULL | 最新主任审批通过时间 |
| createBy | VARCHAR(25) | DEFAULT NULL | '' | 创建人 |
| createTime | DATETIME | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | '' | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |
| orgId | INT(2) | DEFAULT NULL | 1 | 所属公司 |
| customInfo | JSON | DEFAULT NULL | NULL | 自定义信息 |

---

## 2. pm_subcontract_project_line（转包项目行项目表）

转包项目设备明细表，记录转包涉及的设备信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| subcontractId | INT(11) | NOT NULL, MUL | - | 转包项目ID，关联pm_subcontract_project_header.id |
| projectId | INT(11) | DEFAULT NULL, MUL | NULL | 原项目ID |
| barcode | VARCHAR(25) | DEFAULT NULL, MUL | NULL | 设备序列号 |
| itemCode | VARCHAR(25) | DEFAULT NULL, MUL | NULL | 设备编码 |
| itemModel | VARCHAR(255) | DEFAULT NULL | NULL | 设备型号 |
| itemName | VARCHAR(255) | DEFAULT NULL | NULL | 设备名称 |
| contractNo | VARCHAR(50) | DEFAULT NULL, MUL | NULL | 合同号 |
| createBy | VARCHAR(25) | DEFAULT NULL | NULL | 创建人 |
| createTime | DATETIME | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |

**索引说明：**
- unique_index：subcontractId + barcode 复合唯一索引
- barcode：barcode 单独非唯一索引

---

## 3. pm_subcontract_project_payment（转包项目付款表）

转包项目付款计划表，记录分期付款信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| subcontractId | INT(11) | NOT NULL, MUL | - | 转包项目ID，关联pm_subcontract_project_header.id |
| ratio | VARCHAR(10) | DEFAULT NULL | NULL | 付款比例（百分比） |
| amount | VARCHAR(25) | DEFAULT NULL | NULL | 付款金额 |
| confirmTime | DATETIME | DEFAULT NULL | NULL | 提交/确认时间 |
| paymentTime | DATETIME | DEFAULT NULL | NULL | 实际付款时间 |
| remark | VARCHAR(512) | DEFAULT NULL | NULL | 备注 |
| sseId | BIGINT(20) | DEFAULT NULL | -1 | SSE报销单审批行ID |
| customInfo | JSON | DEFAULT NULL | NULL | 自定义扩展信息 |
| createBy | VARCHAR(25) | DEFAULT NULL | NULL | 创建人 |
| createTime | DATETIME | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |

---

## 4. pm_subcontract_project_payment_sse（转包付款SSE同步表）

转包付款与SSE报销系统的数据同步表，存储从SSE系统同步的报销单信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) UNSIGNED | MUL | 0 | SSE报销单行ID（来自SSE系统） |
| workNo | VARCHAR(10) | MUL | NULL | 工号 |
| name | VARCHAR(10) | - | NULL | 申请人姓名 |
| offerNum | VARCHAR(20) | - | NULL | 报销单号 |
| applyAmount | DECIMAL(16,2) | - | NULL | 申请金额 |
| receiver | VARCHAR(255) | - | NULL | 收款人/服务商名称 |
| bank | VARCHAR(80) | - | NULL | 开户行 |
| bankAccount | VARCHAR(255) | - | NULL | 银行账号 |
| useage | VARCHAR(512) | - | NULL | 用途/项目名称 |
| paystate | VARCHAR(25) | - | NULL | 付款状态（如"已付款"） |
| confirmTime | DATETIME | - | NULL | 提交/确认时间 |
| paymentTime | DATETIME | - | NULL | 实际付款时间 |
| approveState | VARCHAR(25) | NOT NULL | - | 审批状态（如"会计审核通过"） |
| type | VARCHAR(255) | - | NULL | 费用类型（如"技术服务费"） |
| approveAmount | DECIMAL(16,2) | - | NULL | 审批金额 |
| remark | TEXT | - | NULL | 备注（如"100%"、"70%尾款"） |
| subcontractNo | VARCHAR(255) | MUL | NULL | 转包合同号，关联pm_subcontract_project_header.subcontractNo |

**样例数据：**
- id=278456, workNo=00074, name=张淋, offerNum=400074181214001, applyAmount=29800.00, receiver=上海安洵信息技术有限公司, paystate=已付款, approveState=会计审核通过, subcontractNo=SS2018031220180103

**说明：** 此表由SSE系统定时同步（Quartz任务），通过subcontractNo与转包项目关联。数据采用清空后重写策略（TRUNCATE + INSERT）。

---

## 5. pm_subcontract_project_price（转包项目价格表）

转包项目价格/费用明细表，记录每个合同行项的工程费和转包价。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| subcontractId | INT(11) | NOT NULL | - | 转包项目ID，关联pm_subcontract_project_header.id |
| contractNo | VARCHAR(50) | - | NULL | 合同号 |
| orderExecNumber | VARCHAR(25) | - | NULL | 订单执行编号 |
| projectCode | VARCHAR(25) | - | NULL | 项目编码 |
| engineeFee | VARCHAR(25) | - | NULL | 工程费（含千分位逗号） |
| objId | VARCHAR(64) | - | NULL | 行项目ID（ERP系统标识） |
| procType | VARCHAR(25) | - | NULL | 采购类型编码 |
| price | VARCHAR(25) | - | NULL | 转包价格（含千分位逗号） |
| createTime | DATETIME | - | NULL | 创建时间 |
| createBy | VARCHAR(25) | - | NULL | 创建人 |
| updateTime | DATETIME | - | NULL | 更新时间 |
| updateBy | VARCHAR(25) | - | NULL | 更新人 |

**样例数据：**
- id=1, subcontractId=608, contractNo=31020180123Q, orderExecNumber=1620111801193X301, projectCode=16201117022502N, engineeFee=45,645.32, procType=2, price=13,000

---

## 6. pm_subcontract_project_callback（转包项目回访表）

转包项目回访记录表，记录回访问卷的填写和关联信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| subcontractId | INT(11) | - | NULL | 转包项目ID，关联pm_subcontract_project_header.id |
| taskKey | VARCHAR(25) | - | NULL | 工作流任务键（如callbackTask） |
| taskId | VARCHAR(25) | - | NULL | 工作流任务ID（Activiti） |
| quesnaireId | INT(11) | - | NULL | 问卷模板ID，关联pm_cl_quesnaire_template_header.id |
| quesnaireVersion | INT(11) | - | NULL | 问卷版本号 |
| quesnaireState | INT(11) | - | NULL | 问卷填写状态（0:未填写 1:已填写） |
| createBy | VARCHAR(25) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(25) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |
| effectiveFrom | DATETIME | - | NULL | 有效开始时间 |
| effectiveTo | DATETIME | - | NULL | 有效结束时间 |
| customInfo | JSON | - | NULL | 自定义扩展信息 |

**样例数据：**
- id=1, subcontractId=602, taskKey=callbackTask, taskId=87715, quesnaireId=3233, quesnaireVersion=1, quesnaireState=1, createBy=z01300

---

## 7. pm_subcontract_deliver_files（转包项目交付文件表）

转包项目交付物/文件管理表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| subcontractId | INT | NOT NULL, FK | - | 转包项目ID |
| paymentId | INT | - | NULL | 关联付款ID |
| fileName | VARCHAR(500) | - | NULL | 交付件名称 |
| filePath | VARCHAR(1000) | - | NULL | 交付件路径 |
| type | VARCHAR(10) | - | NULL | 交付件类型（0:用服交付合同 1:用服服务单 2:工程合同） |
| uploadBy | VARCHAR(100) | - | NULL | 上传者 |
| uploadTime | DATETIME | - | NULL | 上传时间 |
| customInfo | JSON | - | NULL | 自定义扩展信息 |
| effectiveFrom | DATETIME | - | NULL | 有效开始时间 |
| effectiveTo | DATETIME | - | NULL | 有效结束时间 |

---

## 8. pm_facilitator（服务商表）

转包服务商信息表，管理外部服务商的基本信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| code | VARCHAR(25) | DEFAULT NULL, UNI | NULL | 服务商编号 |
| account | VARCHAR(25) | DEFAULT NULL, MUL | NULL | 服务商账号 |
| type | VARCHAR(64) | DEFAULT NULL | NULL | 合作类型 |
| bankInfo | VARCHAR(255) | DEFAULT NULL | NULL | 开户行信息 |
| bankAccount | VARCHAR(64) | DEFAULT NULL | NULL | 收款账户 |
| cnapsCode | VARCHAR(25) | DEFAULT NULL | NULL | 联行号 |
| contacts | VARCHAR(64) | DEFAULT NULL | NULL | 联系人 |
| tel | VARCHAR(64) | DEFAULT NULL | NULL | 联系电话 |
| email | VARCHAR(64) | DEFAULT NULL | NULL | 联系邮箱 |
| state | BIT(1) | DEFAULT NULL | b'1' | 状态（1:启用 0:停用） |
| needApprove | BIT(1) | DEFAULT NULL | b'0' | 是否评审 |
| approveStatus | INT(1) | DEFAULT NULL | 0 | 审批结果 |
| deliveryIds | VARCHAR(25) | DEFAULT NULL | NULL | 附件材料 |
| relateType | VARCHAR(25) | DEFAULT NULL | NULL | 关联类型 |
| effectiveFrom | DATETIME | DEFAULT NULL | NULL | 有效开始时间 |
| effectiveTo | DATETIME | DEFAULT NULL | NULL | 有效结束时间 |
| createBy | VARCHAR(45) | DEFAULT NULL | NULL | 创建人 |
| createTime | DATETIME | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(45) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |
| customInfo | JSON | DEFAULT NULL | NULL | 自定义信息 |

**索引说明：**
- code：唯一索引
- account + state：复合唯一索引

---

## 表间关系概览

```
pm_subcontract_project_header (1) ──→ (N) pm_subcontract_project_line       通过 subcontractId
pm_subcontract_project_header (1) ──→ (N) pm_subcontract_project_payment   通过 subcontractId
pm_subcontract_project_header (1) ──→ (N) pm_subcontract_project_price     通过 subcontractId
pm_subcontract_project_header (1) ──→ (N) pm_subcontract_project_callback  通过 subcontractId
pm_subcontract_project_header (1) ──→ (N) pm_subcontract_deliver_files     通过 subcontractId

pm_subcontract_project_header ──→ pm_facilitator  通过 facilitatorId

pm_subcontract_project_payment_sse ──→ pm_subcontract_project_header  通过 subcontractNo（非外键，业务关联）

pm_subcontract_project_callback ──→ pm_cl_quesnaire_template_header  通过 quesnaireId（问卷模板关联）

pm_subcontract_project_header ──→ pm_project_header  通过 projectIds（多对多）
pm_subcontract_project_line ──→ pm_project_header    通过 projectId
```
