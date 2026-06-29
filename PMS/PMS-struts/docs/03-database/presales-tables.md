# 售前测试表结构（pm_presales_*）

> 数据库：dppms_d365 (MySQL)  
> 模块：售前项目管理  
> 命名前缀：pm_presales_project_

---

## 1. pm_presales_project_header（售前项目主表）

售前测试项目核心信息表，记录售前测试项目的基本属性和流程状态。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| presalesId | INT(11) | PK, AUTO_INCREMENT | - | 售前项目主键ID |
| instId | VARCHAR(64) | DEFAULT NULL, MUL | NULL | 流程实例ID（Activiti工作流） |
| applyState | INT(11) | DEFAULT NULL | NULL | 申请状态（-1:草稿 1:审批中 2:结束） |
| applyBy | VARCHAR(25) | DEFAULT NULL | NULL | 申请人编码 |
| applyTime | DATETIME | DEFAULT NULL | NULL | 申请时间 |
| endTime | DATETIME | DEFAULT NULL | NULL | 申请结束时间 |
| projectState | VARCHAR(25) | DEFAULT NULL | 10 | 项目状态（10:未创建 20:直接闭环 30:已创建 31:待指派项目经理 32:项目经理跟踪 33:工程管理部回访 100:闭环） |
| presalesCode | VARCHAR(64) | DEFAULT NULL | NULL | 售前项目编码（自动生成） |
| projectCode | VARCHAR(64) | DEFAULT NULL, MUL | NULL | 原项目编码（关联售后项目） |
| projectName | VARCHAR(255) | DEFAULT NULL | NULL | 项目名称 |
| projectType | VARCHAR(25) | DEFAULT NULL | NULL | 项目类型（20:售前） |
| marketName | VARCHAR(25) | DEFAULT NULL | NULL | 市场部名称 |
| systemName | VARCHAR(25) | DEFAULT NULL | NULL | 系统部名称 |
| expendName | VARCHAR(25) | DEFAULT NULL | NULL | 拓展部名称 |
| industryName | VARCHAR(25) | DEFAULT NULL | NULL | 子行业名称 |
| officeCode | VARCHAR(25) | DEFAULT NULL | NULL | 办事处编码 |
| salesman | VARCHAR(25) | DEFAULT NULL | NULL | 销售人员 |
| salesmanLink | VARCHAR(125) | DEFAULT NULL | NULL | 销售联系方式 |
| productManager | VARCHAR(25) | DEFAULT NULL | NULL | 产品经理 |
| lendInfoId | VARCHAR(64) | DEFAULT NULL, MUL | NULL | SMS借货主键ID |
| lendfiles | VARCHAR(2048) | DEFAULT NULL | NULL | 借货交付件 |
| confirmFileIds | VARCHAR(2048) | DEFAULT NULL | NULL | 现场测试确认单文件ID列表 |
| hasRma | INT(1) | DEFAULT NULL | 0 | 是否有未核销数据 |
| hasTransfer | INT(1) | DEFAULT NULL | 0 | 是否发生借转销 |
| closeRemark | VARCHAR(512) | DEFAULT NULL | NULL | 闭环备注 |
| source | VARCHAR(25) | NOT NULL | SMS | 数据来源 |
| customInfo | JSON | DEFAULT NULL | NULL | 自定义扩展信息（JSON格式） |
| createBy | VARCHAR(25) | DEFAULT NULL | NULL | 创建人 |
| createTime | DATETIME | DEFAULT NULL | CURRENT_TIMESTAMP | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |
| effectiveFrom | DATETIME | DEFAULT NULL | NULL | 生效开始时间 |
| effectiveTo | DATETIME | DEFAULT NULL | NULL | 生效结束时间 |

**项目状态流转：**
- 30: 创建中 → 31: 审批中 → 32: 测试中 → 33: 已完成
- 特殊状态：闭环(闭环完成)、拒绝(审批拒绝)

---

## 2. pm_presales_project_product_line（售前项目产品线表）

售前项目产品明细表，记录售前项目关联的产品信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| productLineId | INT | PK, AUTO_INCREMENT | - | 明细表主键 |
| presalesId | INT | NOT NULL, FK | - | 售前项目ID，关联pm_presales_project_header.presalesId |
| productFirstName | VARCHAR(200) | - | NULL | 产品一级分类 |
| productTypeName | VARCHAR(200) | - | NULL | 产品类型 |
| itemCode | VARCHAR(100) | - | NULL | 产品编码 |
| itemModel | VARCHAR(200) | - | NULL | 产品型号 |
| itemDesc | VARCHAR(500) | - | NULL | 产品描述 |
| price | DECIMAL(18,2) | - | 0 | 目录价 |
| productNum | INT | - | 0 | 数量 |
| transferNum | INT | - | 0 | 借转销数量 |
| hexiaoNum | INT | - | 0 | 核销数量 |
| remark | VARCHAR(500) | - | NULL | 备注 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 3. pm_presales_project_callback（售前项目回访表）

售前项目回访记录表，记录回访问卷信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| presalesId | INT | NOT NULL, FK | - | 售前项目ID |
| quesnaireId | INT | - | NULL | 问卷模板ID |
| callbackState | INT | - | 0 | 回访状态（0:未回访 1:已回访） |
| callbackBy | VARCHAR(100) | - | NULL | 回访人 |
| callbackTime | DATETIME | - | NULL | 回访时间 |
| callbackRemark | TEXT | - | NULL | 回访备注 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 4. pm_presales_project_duration（售前项目耗时表）

售前项目各阶段耗时统计表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| presalesId | INT | NOT NULL, FK | - | 售前项目ID |
| stageCode | VARCHAR(50) | NOT NULL | - | 阶段编码 |
| stageName | VARCHAR(200) | - | NULL | 阶段名称 |
| startTime | DATETIME | - | NULL | 阶段开始时间 |
| endTime | DATETIME | - | NULL | 阶段结束时间 |
| duration | VARCHAR(100) | - | NULL | 耗时（格式化字符串） |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

**阶段编码说明：**

| 阶段 | 含义 |
|------|------|
| applyDuration | 项目同步到项目开始的时间间隔 |
| totalDuration | 项目开始到结束的时间间隔 |
| serviceDuration | 指派服务经理的时间耗时 |
| programDuration | 指派项目经理的时间耗时 |
| testDuration | 跟踪测试的时间耗时 |
| callbackDuration | 回访的时间耗时 |
| serviceApproveDuration | 服务经理审批的时间耗时 |

---

## 5. pm_presales_project_rma_info（售前项目RMA信息表）

售前项目RMA（退货授权）信息表，记录借转销和RMA核销数据。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| presalesId | INT | NOT NULL, FK | - | 售前项目ID |
| rmaNo | VARCHAR(100) | - | NULL | RMA单号 |
| barCode | VARCHAR(100) | - | NULL | 设备序列号 |
| itemCode | VARCHAR(100) | - | NULL | 产品编码 |
| itemModel | VARCHAR(200) | - | NULL | 产品型号 |
| itemName | VARCHAR(500) | - | NULL | 产品名称 |
| rmaType | VARCHAR(50) | - | NULL | RMA类型 |
| rmaState | VARCHAR(50) | - | NULL | RMA状态 |
| contractNo | VARCHAR(200) | - | NULL | 合同号 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 表间关系概览

```
pm_presales_project_header (1) ──→ (N) pm_presales_project_product_line  通过 presalesId
pm_presales_project_header (1) ──→ (N) pm_presales_project_callback     通过 presalesId
pm_presales_project_header (1) ──→ (N) pm_presales_project_duration     通过 presalesId
pm_presales_project_header (1) ──→ (N) pm_presales_project_rma_info     通过 presalesId

pm_presales_project_header ──→ pm_project_member  通过 presalesId(projectId)
                            （售前项目也使用pm_project_member表管理成员）

pm_presales_project_header ──→ pm_project_header  通过 projectCode
                            （售前项目可关联到售后项目）
```
