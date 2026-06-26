# 数据同步中间表结构（pm_*_from_*）

> 数据库：dppms_d365 (MySQL)
> 模块：外部数据同步
> 命名规则：pm_{业务对象}_from_{来源系统}
> 数据来源：[complete-data-dictionary.md](./complete-data-dictionary.md)（实际数据库导出）

> ⚠️ **修订说明**：本文档已基于实际数据库字段定义（complete-data-dictionary.md）和SQL映射文件代码进行全面校对。此前版本存在大量虚构字段（如 createBy/createTime/syncTime 在多数同步表中并不存在）和错误的字段类型/长度。所有修正均以实际数据库结构为准，并添加了代码比对注释。

---

## 1. pm_order_data_from_erp_source（ERP订单数据源表）

ERP订单数据统一源表，存储从各ERP系统同步的订单头信息。由 `pm_order_data_from_erp_d365` 和 `pm_order_data_from_erp_sap` 两表UNION合并而来。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderNumber | varchar(25) | - | NULL | 订单号 | OrderBean.orderNumber；INSERT字段 |
| contractNo | varchar(50) | - | NULL | 合同号 | OrderBean.contractNo；INSERT字段 |
| orderExecNumber | varchar(50) | - | NULL | 订单执行号 | OrderBean.orderExecNumber；INSERT字段 |
| orderExecNumberShort | varchar(50) | - | NULL | 订单执行号简写 | INSERT字段；D365/SAP子表无此字段 |
| orderCreateTime | datetime | - | NULL | 订单创建时间 | OrderBean.orderCreateTime；INSERT字段 |
| customerRequireTime | datetime | - | NULL | 客户要求交期 | OrderBean.customerRequireTime；INSERT字段 |
| customerCode | varchar(55) | - | NULL | 客户编码 | OrderBean.customerCode；INSERT字段 |
| customerName | varchar(255) | - | NULL | 客户名称 | OrderBean.customerName；INSERT字段 |
| projectName | varchar(255) | - | NULL | 项目名称 | OrderBean.projectName；INSERT字段 |
| orderComment | varchar(2048) | - | NULL | 订单备注 | OrderBean.orderComment；INSERT字段 |
| orderType | int(11) | - | 0 | 订单类型（0=正常销售 1=退货） | INSERT字段；SAP查询时由正常/退货UNION决定 |
| compCode | varchar(25) | - | 0 | 公司编码 | OrderBean.compCode；INSERT字段 |
| salesType | varchar(25) | - | 01 | 销售类型（01=正常合同,02=借转销,14=销售类借货） | OrderBean.salesType；INSERT字段 |
| source | varchar(25) | - | SAP | 数据来源（SAP/D365/SMS） | INSERT字段；D365数据设为'D365'，SAP数据设为'SAP' |
| customInfo | json | - | NULL | 自定义扩展信息 | 继承CustomInfoEntity.customInfo；INSERT字段 |
| syncTime | datetime | - | CURRENT_TIMESTAMP | 同步时间 | 数据库自动填充，代码不显式INSERT |

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderNumber | orderNumber | 是 | BTREE | 防止订单重复 |
| contractNo | contractNo | 是 | BTREE | 合同号关联查询 |
| orderExecNumber | orderExecNumber | 是 | BTREE | 执行号关联查询 |
| orderType | orderType, salesType | 是 | BTREE | 按类型+销售类型组合查询 |

**SQL映射**：`sql-map-refresh-data-common-config.xml`
- `selectOrderInfoFromERP`：从d365和sap两表UNION查询
- `insertOrderInfoFromERP`：插入到本表
- `deleteOrderInfoFromERP`：TRUNCATE本表

---

## 2. pm_order_data_from_erp_sap（SAP ERP订单数据表）

从SAP系统同步的订单头数据表。同步方式为TRUNCATE+全量INSERT。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderNumber | varchar(25) | - | NULL | SAP订单号 | OrderBeans resultMap: orderNumber←order_number |
| contractNo | varchar(50) | - | NULL | 合同号 | OrderBeans resultMap: contractNo←contract_number |
| orderExecNumber | varchar(50) | - | NULL | 订单执行号 | OrderBeans resultMap: orderExecNumber←order_exec_number |
| orderCreateTime | datetime | - | NULL | 订单创建时间 | OrderBeans resultMap: orderCreateTime←order_creation_date |
| customerRequireTime | datetime | - | NULL | 客户要求交期 | OrderBeans resultMap: customerRequireTime←customer_require_date |
| customerCode | varchar(55) | - | NULL | 客户编码 | OrderBeans resultMap: customerCode←customer_code |
| customerName | varchar(255) | - | NULL | 客户名称 | OrderBeans resultMap: customerName←customer_name |
| projectName | varchar(255) | - | NULL | 项目名称 | OrderBeans resultMap: projectName←project_name |
| orderComment | varchar(255) | - | NULL | 订单备注 | OrderBeans resultMap: orderComment←comment；【疑问】SAP表为varchar(255)而source/d365表为varchar(2048)，可能存在长文本截断风险 |
| orderType | int(11) | - | 0 | 订单类型（0=正常销售 1=退货） | INSERT字段；正常订单和退货订单分别查询后UNION |
| compCode | varchar(25) | - | 0 | 公司编码 | OrderBeans resultMap: compCode←company_code(nullValue=0) |
| salesType | varchar(25) | - | 01 | 销售类型 | OrderBeans resultMap: salesType←u_sordertype |

> **字段差异说明**：
> - 相比统一源表（表1），SAP表**缺少** `orderExecNumberShort`、`source`、`customInfo`、`syncTime` 字段
> - SAP表的 `orderComment` 为 varchar(255)，而source/d365表为 varchar(2048)
> - SAP表**没有** `profitCenter`（利润中心）字段——此前文档错误地添加了此虚构字段

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderNumber | orderNumber | 是 | BTREE | 防止SAP订单重复 |
| contractNo | contractNo | 是 | BTREE | 合同号关联查询 |
| orderExecNumber | orderExecNumber | 是 | BTREE | 执行号关联查询 |

**SQL映射**：`sql-map-refresh-data-sap-config.xml`
- `selectOrderInfoFromSAP`：从SAP视图查询（含正常+退货UNION）
- `insertOrderInfoFromSAP`：插入到本表
- `deleteOrderInfoFromSAP`：TRUNCATE本表
- 旧版SQL ID（@Deprecated）：`query_DP_V_SO_ORDER_4_PMS`、`query_DP_V_RMA_ORDER_4_PMS`、`insert_pm_order_data`、`delete_pm_order_data`

---

## 3. pm_order_data_from_erp_d365（D365 ERP订单数据表）

从D365系统同步的订单头数据表。同步方式为TRUNCATE+全量INSERT。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderNumber | varchar(25) | - | NULL | D365订单号 | INSERT字段 |
| contractNo | varchar(50) | - | NULL | 合同号 | INSERT字段 |
| orderExecNumber | varchar(50) | - | NULL | 订单执行号 | INSERT字段 |
| orderCreateTime | datetime | - | NULL | 订单创建时间 | INSERT字段 |
| customerRequireTime | datetime | - | NULL | 客户要求交期 | INSERT字段 |
| customerCode | varchar(55) | - | NULL | 客户编码 | INSERT字段 |
| customerName | varchar(255) | - | NULL | 客户名称 | INSERT字段 |
| projectName | varchar(255) | - | NULL | 项目名称 | INSERT字段 |
| orderComment | varchar(2048) | - | NULL | 订单备注 | INSERT字段 |
| orderType | int(11) | - | 0 | 订单类型（0=正常销售 1=退货） | INSERT字段 |
| compCode | varchar(25) | - | 0 | 公司编码 | INSERT字段 |
| salesType | varchar(25) | - | 01 | 销售类型 | INSERT字段 |
| customInfo | json | - | NULL | 自定义扩展信息 | 继承CustomInfoEntity.customInfo；INSERT字段 |
| syncTime | datetime | - | CURRENT_TIMESTAMP | 同步时间 | 数据库自动填充 |

> **字段差异说明**：
> - 相比统一源表（表1），D365表**缺少** `orderExecNumberShort`、`source` 字段
> - D365表**没有** `profitCenter`（利润中心）字段

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderNumber | orderNumber | 是 | BTREE | 防止D365订单重复 |
| contractNo | contractNo | 是 | BTREE | 合同号关联查询 |
| orderExecNumber | orderExecNumber | 是 | BTREE | 执行号关联查询 |

**SQL映射**：`sql-map-refresh-data-d365-config.xml`
- `selectOrderInfoFromD365`：从D365查询订单
- `insertOrderInfoFromD365`：插入到本表
- `deleteOrderInfoFromD365`：TRUNCATE本表

---

## 4. pm_order_line_from_erp_source（ERP订单行数据源表）

ERP订单行明细统一源表。由 `pm_order_line_from_erp_d365` 和 `pm_order_line_from_erp_sap` 两表UNION合并而来。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderNumber | varchar(25) | - | NULL | 订单号 | OrderLineBean.orderNumber；INSERT字段 |
| lineNum | varchar(25) | - | NULL | 行号 | OrderLineBean.lineNum；INSERT字段 |
| itemCode | varchar(25) | - | NULL | 产品编码 | OrderLineBean.itemCode；INSERT字段 |
| itemDesc | varchar(255) | - | NULL | 产品描述 | OrderLineBean.itemDesc；INSERT字段 |
| orderQuantity | int(11) | - | NULL | 订单数量 | OrderLineBean.orderQuantity(int类型)；INSERT字段 |
| openQuantity | int(11) | - | NULL | 未清数量 | OrderLineBean.openQuantity(int类型)；INSERT字段 |
| bundleCode | varchar(25) | - | NULL | 捆绑父物料编码 | OrderLineBean.bundleCode；INSERT字段 |
| warrantyMonth | int(11) | - | NULL | 保修月数 | OrderLineBean.warrantyMonth；INSERT字段 |
| lineType | int(11) | - | 0 | 行类型（0=正常 1=退货） | INSERT字段 |
| compCode | varchar(25) | - | 0 | 公司编码 | OrderLineBean.compCode；INSERT字段 |
| profitCenter | varchar(25) | - | NULL | 利润中心 | OrderLineBean.profitCenter；INSERT字段 |
| realOrderExecNumber | varchar(25) | - | NULL | 真实执行单号 | OrderLineBean.realOrderExecNumber；INSERT字段 |
| source | varchar(25) | - | SAP | 数据来源 | INSERT字段 |
| customInfo | json | - | NULL | 自定义扩展信息 | 继承CustomInfoEntity.customInfo；INSERT字段 |
| syncTime | datetime | - | CURRENT_TIMESTAMP | 同步时间 | 数据库自动填充 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderNumber | orderNumber, lineNum | 是 | BTREE | 订单+行号唯一约束 |
| itemCode | itemCode | 是 | BTREE | 产品编码查询 |

**SQL映射**：`sql-map-refresh-data-common-config.xml`
- `selectOrderLineFromERP`：从d365和sap两表UNION查询
- `insertOrderLineFromERP`：插入到本表
- `deleteOrderLineFromERP`：TRUNCATE本表

---

## 5. pm_order_line_from_erp_sap（SAP订单行数据表）

从SAP系统同步的订单行明细数据。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderNumber | varchar(25) | - | NULL | SAP订单号 | orderLineBeans resultMap: orderNumber←order_number |
| lineNum | int(11) | - | NULL | 行号 | orderLineBeans resultMap: lineNum←LineNum；【疑问】SAP表lineNum为int(11)而source/d365表为varchar(25)，类型不一致 |
| itemCode | varchar(25) | - | NULL | 产品编码 | orderLineBeans resultMap: itemCode←item_code |
| itemDesc | varchar(255) | - | NULL | 产品描述 | orderLineBeans resultMap: itemDesc←item_description |
| orderQuantity | int(11) | - | NULL | 订单数量 | orderLineBeans resultMap: orderQuantity←order_quantity(nullValue=0) |
| openQuantity | int(11) | - | NULL | 未清数量 | orderLineBeans resultMap: openQuantity←open_quantity(nullValue=0) |
| bundleCode | varchar(25) | - | NULL | 捆绑父物料编码 | orderLineBeans resultMap: bundleCode←bundle_parent_item_code |
| warrantyMonth | int(11) | - | NULL | 保修月数 | orderLineBeans resultMap: warrantyMonth←warranty_by_month(nullValue=0) |
| lineType | int(11) | - | 0 | 行类型（0=正常 1=退货） | INSERT字段 |
| compCode | varchar(25) | - | 0 | 公司编码 | orderLineBeans resultMap: compCode←company_code(nullValue=0) |
| profitCenter | varchar(25) | - | NULL | 利润中心 | orderLineBeans resultMap: profitCenter←profitcenter |

> **字段差异说明**：
> - 相比统一源表（表4），SAP表**缺少** `realOrderExecNumber`、`source`、`customInfo`、`syncTime` 字段
> - SAP表的 `lineNum` 为 int(11)，而source/d365表为 varchar(25)
> - SAP表**有** `profitCenter` 字段（通过SAP视图映射），D365表同样有此字段

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderNumber | orderNumber, lineNum | 是 | BTREE | 订单+行号唯一约束 |
| itemCode | itemCode | 是 | BTREE | 产品编码查询 |

**SQL映射**：`sql-map-refresh-data-sap-config.xml`
- `selectOrderLineFromSAP`：从SAP视图查询（含正常+退货UNION）
- `insertOrderLineFromSAP`：插入到本表
- `deleteOrderLineFromSAP`：TRUNCATE本表

---

## 6. pm_order_line_from_erp_d365（D365订单行数据表）

从D365系统同步的订单行明细数据。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderNumber | varchar(25) | - | NULL | D365订单号 | INSERT字段 |
| lineNum | varchar(25) | - | NULL | 行号 | INSERT字段 |
| itemCode | varchar(25) | - | NULL | 产品编码 | INSERT字段 |
| itemDesc | varchar(255) | - | NULL | 产品描述 | INSERT字段 |
| orderQuantity | int(11) | - | NULL | 订单数量 | INSERT字段 |
| openQuantity | int(11) | - | NULL | 未清数量 | INSERT字段 |
| bundleCode | varchar(25) | - | NULL | 捆绑父物料编码 | INSERT字段 |
| warrantyMonth | int(11) | - | NULL | 保修月数 | INSERT字段 |
| lineType | int(11) | - | 0 | 行类型（0=正常 1=退货） | INSERT字段 |
| compCode | varchar(25) | - | 0 | 公司编码 | INSERT字段 |
| profitCenter | varchar(25) | - | NULL | 利润中心 | INSERT字段 |
| realOrderExecNumber | varchar(25) | - | NULL | 真实执行单号 | INSERT字段 |
| customInfo | json | - | NULL | 自定义扩展信息 | 继承CustomInfoEntity.customInfo；INSERT字段 |
| syncTime | datetime | - | CURRENT_TIMESTAMP | 同步时间 | 数据库自动填充 |

> **字段差异说明**：
> - 相比统一源表（表4），D365表**缺少** `source` 字段
> - D365表**有** `profitCenter` 和 `realOrderExecNumber` 字段

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderNumber | orderNumber, lineNum | 是 | BTREE | 订单+行号唯一约束 |
| itemCode | itemCode | 是 | BTREE | 产品编码查询 |

**SQL映射**：`sql-map-refresh-data-d365-config.xml`
- `selectOrderLineFromD365`：从D365查询订单行
- `insertOrderLineFromD365`：插入到本表
- `deleteOrderLineFromD365`：TRUNCATE本表

---

## 7. pm_person_from_oa（OA/EHR人员数据表）

从OA/EHR系统同步的人员信息表。当前由 `GainPersonByEHR` Job从EHR系统同步（原`GainPersonByOA`已废弃）。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| salesmanCode | varchar(45) | - | NULL | 人员编码 | Person.salesmanCode；INSERT字段 |
| salesmanTel | varchar(45) | - | NULL | 联系电话 | Person.salesmanTel；INSERT字段 |
| salesmanName | varchar(45) | - | NULL | 人员姓名 | Person.salesmanName；INSERT字段 |
| salesmanMail | varchar(100) | - | NULL | 电子邮箱 | Person.salesmanMail；INSERT字段 |

> **此前文档错误**：此前文档列出了 `departmentCode`、`departmentName`、`syncTime`、`createBy`、`createTime` 等字段，实际数据库中均不存在。

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| salesmanCode1 | salesmanCode | 是 | BTREE | 人员编码唯一约束 |

**业务说明：**
- 项目成员创建时，通过 `memberCode` 查询此表获取电话和邮箱信息
- 查询时使用 `salesmanCode` 去掉首字母后匹配（见SQL `query-person-fromoa-bycode`）
- 同步流程使用临时表 `pm_person_from_oa_temp` 进行增量比对，处理离职人员失效

**SQL映射**：`sql-map-refresh-data-common-config.xml`
- `insert_pm_person_from_oa`：批量插入
- `delete_pm_person_from_oa`：TRUNCATE
- `update_keshanhui_from_oa`：修正特定人员编码
- `createInvalidPersonsTempTable`/`insert_pm_person_from_oa_temp`/`dropInvalidPersonsTempTable`：临时表增量比对
- `invalidQuitProjectQuitSalesMan`：失效离职销售关联项目
- `update_pm_salesmember_info`：更新项目销售成员

`sql-map-project-config.xml`：
- `query_person_list`：查询所有人员列表
- `query-person-fromoa-bycode`：按编码查询人员

---

## 8. pm_project_property_from_sms（SMS项目属性表）

从SMS/CRM系统同步的项目属性信息表。支持多数据源（SMS/CRM），通过 `dataSource` 字段区分。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| orderExecNumber | varchar(25) | - | NULL | 订单执行号 | PrjProperty.orderExecNumber；prjPropertys resultMap←orderCodeReal |
| projectCode | varchar(25) | - | NULL | 项目编码 | PrjProperty.projectCode；INSERT字段 |
| projectName | varchar(255) | - | NULL | 项目名称 | PrjProperty.projectName；INSERT字段 |
| salesManCode | varchar(45) | - | NULL | 销售人员编码 | PrjProperty.salesManCode；resultMap←usernamec |
| salesManName | varchar(45) | - | NULL | 销售人员姓名 | PrjProperty.salesManName；resultMap←realName |
| marketCode | varchar(64) | - | NULL | 市场部编码 | PrjProperty.marketCode；INSERT字段 |
| marketName | varchar(255) | - | NULL | 市场部名称 | PrjProperty.marketName；INSERT字段 |
| systemId | varchar(64) | - | NULL | 系统部ID | PrjProperty.systemId；resultMap←systemid(nullValue=-1) |
| systemName | varchar(255) | - | NULL | 系统部名称 | PrjProperty.systemName；INSERT字段 |
| expendId | varchar(64) | - | NULL | 拓展部ID | PrjProperty.expendId；resultMap←expendId(nullValue=-1) |
| expendName | varchar(255) | - | NULL | 拓展部名称 | PrjProperty.expendName；INSERT字段 |
| industryId | varchar(64) | - | NULL | 行业ID | PrjProperty.industryId；resultMap←industryid(nullValue=-1) |
| industryName | varchar(255) | - | NULL | 行业名称 | PrjProperty.industryName；INSERT字段 |
| officeCode | varchar(15) | - | NULL | 办事处编码 | PrjProperty.officeCode；INSERT字段 |
| officeName | varchar(15) | - | NULL | 办事处名称 | PrjProperty.officeName；INSERT字段 |
| serviceTypeName | varchar(10) | - | NULL | 服务类型名称 | PrjProperty.serviceTypeName；INSERT字段 |
| channelName | varchar(255) | - | NULL | 渠道名称/出货代理商名称 | PrjProperty.channelName；INSERT字段 |
| engineeFee | varchar(25) | - | NULL | 工程服务费 | PrjProperty.engineeFee；INSERT字段 |
| objId | varchar(64) | - | NULL | 对象ID | PrjProperty.objId；INSERT字段 |
| applyType | varchar(25) | - | NULL | 申请类型 | PrjProperty.applyType；INSERT字段 |
| corporationCode | varchar(25) | - | 01 | 法人编码 | PrjProperty.corporationCode；resultMap←corporationCode(nullValue=01) |
| customerProjectName | varchar(255) | - | NULL | 客户项目名称 | PrjProperty.customerProjectName；INSERT字段 |
| finalCustomerName | varchar(255) | - | NULL | 最终客户名称 | PrjProperty.finalCustomerName；INSERT字段 |
| agentName | varchar(500) | - | NULL | 代理商名称 | PrjProperty.agentName；INSERT字段 |
| projectMoney | decimal(16,2) | - | 0.00 | 出货价 | PrjProperty.projectMoney(BigDecimal)；INSERT字段 |
| submitTime | datetime | - | NULL | 项目创建时间 | PrjProperty.submitTime；INSERT字段 |
| majorProjectLevel | varchar(255) | - | NULL | 重大项目级别 | PrjProperty.majorProjectLevel；INSERT字段 |
| predBidDate | datetime | - | NULL | 项目投标时间 | PrjProperty.predBidDate；INSERT字段 |
| linkmanName | varchar(255) | - | NULL | 客户联系人 | PrjProperty.linkmanName；INSERT字段 |
| linkmanTel | varchar(64) | - | NULL | 联系人电话 | PrjProperty.linkmanTel；INSERT字段 |
| dataSource | varchar(25) | - | SMS | 数据来源（SMS/CRM） | INSERT字段；DELETE时支持按dataSource过滤 |

> **此前文档错误**：此前文档列出了 `syncTime`、`createBy`、`createTime` 等字段，实际数据库中均不存在。此前文档缺少 `dataSource` 字段。

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| orderExecNum | orderExecNumber | 是 | BTREE | 执行号唯一约束 |
| projectCode | projectCode | 是 | BTREE | 项目编码唯一约束 |

**SQL映射**：
- SMS: `sql-map-refresh-data-sms-config.xml` — `insert_pm_project_property_from_sms`、`delete_pm_project_property_from_sms`、`query_v_prj_property_4_pm`
- CRM: `sql-map-refresh-data-crm-config.xml` — `insertProjectPropertyFormCRM`、`deleteProjectPropertyFormCRM`、`selectProjectPropertyFormCRM`
- Common: `sql-map-refresh-data-common-config.xml` — 临时表操作（`create_temp_max_ppfsId`等），用于增量同步到业务表

---

## 9. pm_presales_lend_info_from_sms（SMS借货主信息表）

从SMS/CRM系统同步的售前借货主信息表。此前文档错误命名为 `pm_presales_lend_header_from_sms`。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(1) | PK, AUTO_INCREMENT | - | 主键ID | 【疑问】类型为int(1)而非int(11)，可能是建表时笔误 |
| lendInfoId | varchar(64) | NOT NULL | 0 | SMS借货主键ID | LendInfoParam.lendInfoId；INSERT字段 |
| projectCode | varchar(64) | - | NULL | 项目编码 | LendInfoParam.projectCode；INSERT字段 |
| projectName | varchar(765) | - | NULL | 项目名称 | LendInfoParam.projectName；INSERT字段 |
| dutyName | varchar(189) | - | NULL | 责任人姓名 | LendInfoParam.dutyName；INSERT字段 |
| dutyContactWay | varchar(300) | - | NULL | 责任人联系方式 | LendInfoParam.dutyContactWay；INSERT字段 |
| decPath | varchar(765) | - | NULL | DEC路径 | LendInfoParam.decPath；INSERT字段 |
| officeCode | varchar(765) | - | NULL | 办事处编码 | LendInfoParam.officeCode；INSERT字段 |
| marketName | varchar(255) | - | NULL | 市场部名称 | LendInfoParam.marketName；INSERT字段 |
| systemName | varchar(128) | - | NULL | 系统部名称 | LendInfoParam.systemName；INSERT字段 |
| expendName | varchar(255) | - | NULL | 拓展部名称 | LendInfoParam.expendName；INSERT字段 |
| industryName | varchar(128) | - | NULL | 行业名称 | LendInfoParam.industryName；INSERT字段 |
| pspm | varchar(257) | - | NULL | 产品经理 | LendInfoParam.pspm；INSERT字段 |
| dataSource | varchar(25) | - | SMS | 数据来源（SMS/CRM） | INSERT字段；DELETE时支持按dataSource过滤 |

> **此前文档错误**：
> - 表名错误：`pm_presales_lend_header_from_sms` → 实际为 `pm_presales_lend_info_from_sms`
> - 虚构字段：`lendType`、`lendState`、`syncTime`、`createBy`、`createTime` 在实际数据库中均不存在
> - 缺失字段：`dutyName`、`dutyContactWay`、`decPath`、`marketName`、`systemName`、`expendName`、`industryName`、`pspm`、`dataSource`

**SQL映射**：
- SMS: `sql-map-refresh-data-sms-config.xml` — `LendInfoMap`(resultMap)、`query_lend_info_list`、`insert_pm_presales_lend_info_from_sms`、`delete_pm_presales_lend_info_from_sms`
- CRM: `sql-map-refresh-data-crm-config.xml` — `LendInfoMap`(resultMap)、`query_pm_presales_lend_info_from_crm`、`insert_pm_presales_lend_info_from_crm`、`delete_pm_presales_lend_info_from_crm`

---

## 10. pm_presales_lend_product_from_sms（SMS借货产品明细表）

从SMS/CRM系统同步的售前借货产品明细表。此前文档错误命名为 `pm_presales_lend_line_from_sms`。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| lendInfoId | varchar(64) | NOT NULL | - | 借货主键ID | LendProductParam.lendInfoId；INSERT字段 |
| productfirstName | varchar(255) | - | NULL | 一级产品名称 | LendProductParam.productFirstName；INSERT字段 |
| productName | varchar(128) | - | NULL | 产品名称 | LendProductParam.productName；INSERT字段 |
| productsubCode | varchar(765) | - | NULL | 子产品编码 | LendProductParam.productSubCode；INSERT字段 |
| productSubModel | varchar(765) | - | NULL | 子产品型号 | LendProductParam.productSubModel；INSERT字段 |
| productSubName | varchar(765) | - | NULL | 子产品名称 | LendProductParam.productSubName；INSERT字段 |
| lendNum | int(11) | - | NULL | 借货数量 | LendProductParam.lendNum；INSERT字段 |
| memo | text | - | NULL | 备注 | LendProductParam.memo；INSERT字段 |
| dataSource | varchar(25) | - | SMS | 数据来源 | INSERT字段 |
| productfirstCode | varchar(64) | - | NULL | 一级产品编码 | CRM INSERT时包含此字段；SMS INSERT不含此字段 |
| productCode | varchar(64) | - | NULL | 产品编码 | CRM INSERT时包含此字段；SMS INSERT不含此字段 |

> **此前文档错误**：
> - 表名错误：`pm_presales_lend_line_from_sms` → 实际为 `pm_presales_lend_product_from_sms`
> - 虚构字段：`itemCode`、`itemModel`、`itemName`、`barCode`、`syncTime`、`createBy`、`createTime` 在实际数据库中均不存在
> - Java实体类 LendProductParam 中有 `orderNum`/`deliverNum`/`hexiaoNum`/`transferNum` 等计算属性，但这些字段不在本表中，而是在 `pm_presales_lend_order_from_sms` 表中

**SQL映射**：
- SMS: `sql-map-refresh-data-sms-config.xml` — `LendProductMap`(resultMap)、`query_lend_product_list`、`insert_pm_presales_lend_product_from_sms`、`delete_pm_presales_lend_product_from_sms`
- CRM: `sql-map-refresh-data-crm-config.xml` — `LendProductMap`(resultMap)、`query_pm_presales_lend_product_from_crm`、`insert_pm_presales_lend_product_from_crm`、`delete_pm_presales_lend_product_from_crm`

---

## 11. pm_project_soleagent_lend_from_sms（SMS总代借货表）

从SMS/CRM系统同步的总代（独家代理）借货信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| soleAgentLendId | int(11) | NOT NULL | 0 | 总代借货跟踪ID | INSERT字段；无专用Java实体类，使用Map传递 |
| orderExecNumber | varchar(255) | - | NULL | 执行单号 | INSERT字段 |
| orderExecNumberShort | varchar(255) | - | NULL | 忽略版本执行单号 | INSERT字段 |
| orderCodes | varchar(255) | - | NULL | 合并的执行单号 | INSERT字段 |
| contract | varchar(25) | - | NULL | 合同号 | INSERT字段 |
| projectName | varchar(255) | - | NULL | 项目名称（由商务输入） | INSERT字段 |
| soleAgent | varchar(25) | - | NULL | 总代名称 | INSERT字段 |
| profitCenter | varchar(6) | - | NULL | 利润中心 | INSERT字段 |
| dataSource | varchar(25) | - | SMS | 数据来源 | INSERT字段 |

> **此前文档错误**：
> - 虚构字段：`projectCode`、`lendInfoId`、`itemCode`、`itemModel`、`barCode`、`syncTime`、`createBy`、`createTime` 在实际数据库中均不存在
> - 实际字段 `soleAgentLendId`、`orderExecNumber`、`orderExecNumberShort`、`orderCodes`、`contract`、`soleAgent`、`profitCenter` 此前均未记录

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| contract | contract, profitCenter | 是 | BTREE | 合同+利润中心唯一约束 |

**SQL映射**：
- SMS: `sql-map-refresh-data-sms-config.xml` — `query_v_soleagent_lend_4_pms`、`insert_pm_project_soleagent_lend_from_sms`、`delete_pm_project_soleagent_lend_from_sms`
- CRM: `sql-map-refresh-data-crm-config.xml` — `selectProjectSoleagentLendFormCRM`、`insertProjectSoleagentLendFormCRM`、`deleteProjectSoleagentLendFormCRM`

---

## 12. pm_project_real_product_line_from_sms（SMS实际产品线表）

从SMS/CRM系统同步的项目实际产品线/设备清单信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| projectCode | varchar(255) | NOT NULL | - | 项目编码 | RealProductLineBean.projectCode；INSERT字段；【疑问】无自增主键id，projectCode+productSubCode组成业务主键 |
| orderExecNumber | varchar(255) | - | NULL | 执行单号 | RealProductLineBean.orderExecNumber；INSERT字段 |
| productFirstName | varchar(255) | NOT NULL | - | 产品类型（一级产品名称） | RealProductLineBean.productFirstName；INSERT字段 |
| productName | varchar(128) | NOT NULL | - | 产品名称 | RealProductLineBean.productName；INSERT字段 |
| productSubCode | varchar(255) | NOT NULL | - | 子产品编码（item编码） | RealProductLineBean.productSubCode；INSERT字段 |
| productSubModel | varchar(255) | - | NULL | 子产品型号（item型号） | RealProductLineBean.productSubModel；INSERT字段 |
| productSubName | varchar(255) | - | NULL | 子产品名称（item描述） | RealProductLineBean.productSubName；INSERT字段 |
| num | int(11) | NOT NULL | 0 | 订单数量 | RealProductLineBean.num；INSERT字段 |
| memo | mediumtext | - | NULL | 备注 | RealProductLineBean.memo；INSERT字段 |
| dataSource | varchar(25) | - | SMS | 数据来源 | INSERT字段 |

> **此前文档错误**：
> - 虚构字段：`id`（本表无自增主键）、`contractNo`、`itemCode`、`itemName`、`itemModel`、`orderQuantity`、`deliverQuantity`、`openQuantity`、`syncTime`、`createBy`、`createTime` 在实际数据库中均不存在
> - 实际字段命名体系完全不同：使用 `product*` 前缀而非 `item*` 前缀

**SQL映射**：
- SMS: `sql-map-refresh-data-sms-config.xml` — `query_view_refer_product`、`insert_pm_project_real_product_line_from_sms`、`delete_pm_project_real_product_line_from_sms`
- CRM: `sql-map-refresh-data-crm-config.xml` — `query_view_refer_product_from_crm`、`insert_pm_project_real_product_line_from_crm`、`delete_pm_project_real_product_line_from_crm`
- Common: `sql-map-refresh-data-common-config.xml` — `insert_pm_project_product_line_real`（从中间表更新到正式产品清单表pm_project_product_line）

---

## 补充表：其他同步中间表

以下表存在于数据库中，但当前文档不作为主要描述对象（历史备份表或辅助表）。

### pm_order_data_from_erp（ERP订单数据旧版表）

与 `pm_order_data_from_erp_source` 结构相同，约52,940行数据。可能是旧版同步目标表，当前同步Job已改用 `_source` 后缀表。

### pm_order_line_from_erp（ERP订单行旧版表）

约219,652行数据。字段与 `_source` 表类似但包含 `profitCenter` 和 `realOrderExecNumber`，不含 `source` 字段。

### pm_presales_lend_info_from_oa（OA借货主信息表）

约1,963行数据。从OA系统同步的借货审批信息，字段结构与SMS表完全不同，包含OA审批流程相关字段（applyUserCode、applyUserName、applyDeptCode、applyDeptName、applyDate、applyType、salesUserCode、salesUserName、testStartTime、testEndTime、authPlanDate、authDate等）。

### pm_presales_lend_detail_from_oa（OA借货明细表）

约3,024行数据。从OA同步的借货设备明细，字段：id、infoId、contractNum、deviceSerialnum、modelNum、applyCount、isSoftware、customInfo。

### pm_presales_lend_info_from_crm（CRM借货主信息表）

约0行数据。结构与 `pm_presales_lend_info_from_sms` 相同，数据来源为CRM系统。

### pm_presales_lend_order_from_sms（SMS借货订单表）

约0行数据（历史表约20,196行）。从SMS同步的借货订单行信息，字段：orderNumber、ppliCode、orderType、contract、customer、projectName、businessunit、office、dutyperson、itemcode、description、orderQty、dlvQty、rmaQty、lineStatus、createDate、lineId、systemId、canceled、discountVersion、borrowNum、dataSource。

### pm_presales_lend_2_delivery_off_from_sap（SAP借货发货核销表）

约44,530行数据。从SAP同步的借货发货/退货核销信息，字段：orderNumber、lineId、itemCode、ppliCode、contract、deliveryDate、rmaDate。

### pm_presales_lend_2_rma_from_sms（SMS借货退货表）

约0行数据（历史表有数据）。从SMS同步的借货退货信息，字段较多包含orderNumber、ppliCode、orderType、contract、customer、projectName、itemcode、orderQty、dlvQty、rmaQty等。

### pm_presales_lend_2_sale_from_sms（SMS借转销表）

约0行数据（历史表约13,535行）。从SMS同步的借转销信息，字段：productfirstName、productName、projectCode、productSubCode、productSubModel、productSubName、num、borrowNum、contract、memo、dataSource。

### 历史备份表

以下 `_history` 后缀表结构与对应的正式表相同，用于存储历史同步数据快照：

| 历史表 | 对应正式表 | 数据量 |
|--------|-----------|--------|
| pm_project_property_from_sms_history | pm_project_property_from_sms | ~47,550行 |
| pm_project_property_from_sms_history_bak | pm_project_property_from_sms | 备份表 |
| pm_presales_lend_info_from_sms_history | pm_presales_lend_info_from_sms | ~0行 |
| pm_presales_lend_product_from_sms_history | pm_presales_lend_product_from_sms | ~4行 |
| pm_presales_lend_order_from_sms_history | pm_presales_lend_order_from_sms | ~20,196行 |
| pm_presales_lend_2_rma_from_sms_history | pm_presales_lend_2_rma_from_sms | 有数据 |
| pm_presales_lend_2_sale_from_sms_history | pm_presales_lend_2_sale_from_sms | ~13,535行 |
| pm_project_real_product_line_from_sms_history | pm_project_real_product_line_from_sms | ~5,563行 |
| pm_project_soleagent_lend_from_sms_history | pm_project_soleagent_lend_from_sms | ~1,136行 |

---

## 数据同步流向概览

```
┌──────────┐     ┌────────────────────────────────────────┐     ┌──────────────────────┐
│  SAP ERP │────→│ pm_order_data_from_erp_sap             │     │                      │
│  系统    │────→│ pm_order_line_from_erp_sap             │────→│  pm_project_header   │
└──────────┘     └────────────────────────────────────────┘     │  pm_project_contract │
                                                               │  pm_project_product  │
┌──────────┐     ┌────────────────────────────────────────┐     │  _line               │
│  D365    │────→│ pm_order_data_from_erp_d365            │────→│                      │
│  ERP系统 │────→│ pm_order_line_from_erp_d365            │     │  pm_project_member   │
└──────────┘     └────────────────────────────────────────┘     │                      │
                                                               │  pm_project_state    │
┌──────────┐     ┌────────────────────────────────────────┐     │                      │
│  统一源  │←────│ pm_order_data_from_erp_source          │     └──────────────────────┘
│          │←────│ pm_order_line_from_erp_source          │
└──────────┘     └────────────────────────────────────────┘

┌──────────┐     ┌────────────────────────────────────────┐     ┌──────────────────────┐
│  OA/EHR  │────→│ pm_person_from_oa                      │────→│  pm_project_member   │
│  系统    │     │ (人员信息：姓名、电话、邮箱)             │     │  (补充联系方式)       │
└──────────┘     └────────────────────────────────────────┘     └──────────────────────┘

┌──────────┐     ┌────────────────────────────────────────┐     ┌──────────────────────┐
│  SMS/CRM │────→│ pm_project_property_from_sms           │────→│  pm_project_header   │
│  系统    │────→│ pm_presales_lend_info_from_sms         │     │  (项目属性更新)       │
│          │────→│ pm_presales_lend_product_from_sms      │────→│  pm_presales_project │
│          │────→│ pm_project_soleagent_lend_from_sms     │     │  _header             │
│          │────→│ pm_project_real_product_line_from_sms  │     │                      │
└──────────┘     └────────────────────────────────────────┘     └──────────────────────┘

┌──────────┐     ┌────────────────────────────────────────┐
│  OA系统  │────→│ pm_presales_lend_info_from_oa          │
│          │────→│ pm_presales_lend_detail_from_oa        │
└──────────┘     └────────────────────────────────────────┘

┌──────────┐     ┌────────────────────────────────────────┐
│  SAP系统 │────→│ pm_presales_lend_2_delivery_off_from_sap│
└──────────┘     └────────────────────────────────────────┘
```

**同步策略说明：**
1. ERP数据（SAP/D365）→ 子表 → 统一源表(source) → 业务表：定时同步，通过合同号关联
2. OA/EHR人员数据 → 中间表：用于补充项目成员联系方式
3. SMS/CRM项目属性 → 中间表 → 业务表：项目属性增量更新，支持按dataSource区分来源
4. 各中间表**均不包含** `createBy`/`createTime` 审计字段（此前文档错误添加了这些虚构字段）
5. 含 `syncTime` 字段的表（source/d365系列）由数据库自动填充 CURRENT_TIMESTAMP
6. 同步Job类：`GainOrderByERP`（ERP订单）、`GainPersonByEHR`（人员）、`GainPrjPropertyBySMS`（项目属性+总代借货）、`GainPresalesInfoBySMS`（借货信息）、`GainPrjRealProjectLineBySMS`（设备清单）

---

## 索引现状与建议

### 现有索引汇总

| 表名 | 索引 | 类型 | 说明 |
|------|------|------|------|
| pm_order_data_from_erp_source | orderNumber, contractNo, orderExecNumber, orderType(orderType+salesType) | UNIQUE | 已有业务唯一索引 |
| pm_order_data_from_erp_sap | orderNumber, contractNo, orderExecNumber | UNIQUE | 已有业务唯一索引 |
| pm_order_data_from_erp_d365 | orderNumber, contractNo, orderExecNumber | UNIQUE | 已有业务唯一索引 |
| pm_order_line_from_erp_source | orderNumber(orderNumber+lineNum), itemCode | UNIQUE | 已有业务唯一索引 |
| pm_order_line_from_erp_sap | orderNumber(orderNumber+lineNum), itemCode | UNIQUE | 已有业务唯一索引 |
| pm_order_line_from_erp_d365 | orderNumber(orderNumber+lineNum), itemCode | UNIQUE | 已有业务唯一索引 |
| pm_person_from_oa | salesmanCode1 | UNIQUE | 已有业务唯一索引 |
| pm_project_property_from_sms | orderExecNum, projectCode | UNIQUE | 已有业务唯一索引 |
| pm_presales_lend_info_from_sms | PRIMARY(id) | - | 仅有主键索引 |
| pm_presales_lend_product_from_sms | PRIMARY(id) | - | 仅有主键索引 |
| pm_project_soleagent_lend_from_sms | contract(contract+profitCenter) | UNIQUE | 已有业务唯一索引 |
| pm_project_real_product_line_from_sms | 无 | - | 无任何索引 |

### 索引建议

| 表名 | 建议索引字段 | 索引类型 | 理由 |
|------|------------|---------|------|
| pm_presales_lend_info_from_sms | `lendInfoId` | UNIQUE | SMS借货主键去重 |
| pm_presales_lend_info_from_sms | `projectCode` | NORMAL | 按项目编码查询 |
| pm_presales_lend_product_from_sms | `lendInfoId` | NORMAL | 按借货ID查明细 |
| pm_project_real_product_line_from_sms | `projectCode` | NORMAL | 按项目编码查询（高频查询） |
| pm_project_real_product_line_from_sms | `projectCode, productSubCode` | UNIQUE | 项目+子产品编码唯一约束 |

---

## customInfo JSON 字段说明

部分表包含 `customInfo`（JSON类型）字段，用于存储来自源系统的自定义扩展信息。该字段的内部结构取决于源系统的数据格式，当前未定义固定的 JSON Schema。使用时需注意：
- JSON 字段内容可能因源系统版本升级而变化
- 查询 JSON 内部属性需使用 MySQL 的 JSON 函数（如 `JSON_EXTRACT`）
- 不建议在 JSON 字段上建立索引，如需频繁查询应提取为独立列
- 含 customInfo 字段的表：`pm_order_data_from_erp_source`、`pm_order_data_from_erp_d365`、`pm_order_line_from_erp_source`、`pm_order_line_from_erp_d365`

---

## 同步策略细节

| 策略项 | 说明 |
|--------|------|
| 同步方式 | Quartz 定时任务触发，非实时同步 |
| 数据处理 | TRUNCATE+全量INSERT（非增量更新） |
| 删除策略 | 每次同步先TRUNCATE清空，再全量插入 |
| 数据过期 | 历史数据保存在 `_history` 后缀表中 |
| 错误处理 | 同步失败时记录日志到 `fnd_data_refresh_log` 表 |
| 唯一性保证 | 多数表已建立业务字段唯一索引 |
| 多数据源 | SMS/CRM同步表通过 `dataSource` 字段区分来源，DELETE时支持按dataSource过滤 |
| 审计字段 | 同步中间表**不包含** createBy/createTime/updateBy/updateTime 审计字段 |

---

## 修订记录

| 日期 | 修订内容 | 修正问题数 |
|------|---------|-----------|
| 2026-05-20 | 基于complete-data-dictionary.md全面重写字段定义 | 50+ |
| - | 修正所有字段类型/长度错误（如 orderNumber VARCHAR(100)→varchar(25)） | 20+ |
| - | 删除所有虚构字段（createBy/createTime/syncTime等在多数表中不存在） | 15+ |
| - | 修正错误表名（pm_presales_lend_header→pm_presales_lend_info, pm_presales_lend_line→pm_presales_lend_product） | 2 |
| - | 补充缺失字段（dataSource、lineNum、bundleCode、warrantyMonth等） | 10+ |
| - | 添加代码比对注释（SQL映射、Java实体类字段对应关系） | 全部字段 |
| - | 添加实际索引信息（替换此前虚构的索引建议） | 全部表 |
| - | 补充其他同步中间表（OA/CRM/SAP借货相关表、历史表） | 10+ |
