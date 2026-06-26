# 技术公告表结构（prob_*）

> 数据库：dppms_d365 (MySQL)  
> 模块：技术公告管理  
> 命名前缀：prob_

---

## 1. prob_main（技术公告主表）

技术公告核心信息表，记录技术公告的主题、描述、解决方案及各类标记信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 技术公告主键ID |
| probNum | VARCHAR(25) | DEFAULT NULL, MUL | NULL | 技术公告编号（格式：SP.yyyyMMddHHmm） |
| probTicketNo | VARCHAR(255) | DEFAULT NULL | NULL | 网上问题单号 |
| watch | VARCHAR(10) | DEFAULT NULL | NULL | 跟踪类型编码，关联fnd_basic_data(dataTypeCode=30) |
| theme | VARCHAR(255) | DEFAULT NULL | NULL | 主题 |
| desc | TEXT | DEFAULT NULL | NULL | 技术公告描述 |
| solution | TEXT | DEFAULT NULL | NULL | 解决方案 |
| status | VARCHAR(10) | DEFAULT NULL | NULL | 状态编码，关联fnd_basic_data(dataTypeCode=31) |
| startdate | DATE | DEFAULT NULL | NULL | 开始日期 |
| duedate | DATE | DEFAULT NULL | NULL | 计划完成日期 |
| attachments | VARCHAR(255) | DEFAULT NULL | NULL | 附件ID列表（逗号分隔） |
| priority | VARCHAR(10) | DEFAULT NULL | NULL | 严重级别编码，关联fnd_basic_data(dataTypeCode=32) |
| productType | TEXT | DEFAULT NULL | NULL | 产品类型（多个型号以"、"分隔） |
| trackingUser | VARCHAR(10) | DEFAULT NULL | NULL | 跟踪用户编码 |
| visibleRange | INT(1) | NOT NULL | 0 | 可见范围（0:全部 1:对内） |
| customInfo | JSON | DEFAULT NULL | NULL | 自定义扩展信息（JSON格式） |
| relatedSceneTypes | VARCHAR(255) | DEFAULT NULL | NULL | 关联场景类型（逗号分隔） |
| relatedSceneTypesMark | BIGINT(20) | DEFAULT NULL | NULL | 关联场景类型的bitMark值 |
| mitigationActionTypes | VARCHAR(255) | DEFAULT NULL | NULL | 规避方案操作类型（逗号分隔） |
| mitigationActionTypesMark | BIGINT(20) | DEFAULT NULL | NULL | 规避方案操作类型的bitMark值 |
| solutionActionTypes | VARCHAR(255) | DEFAULT NULL | NULL | 解决方案操作类型（逗号分隔） |
| solutionActionTypesMark | BIGINT(20) | DEFAULT NULL | NULL | 解决方案操作类型的bitMark值 |
| remark | TEXT | DEFAULT NULL | NULL | 审批意见/备注 |
| createBy | VARCHAR(15) | DEFAULT NULL | NULL | 创建人 |
| createTime | DATETIME | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |
| effectiveFrom | DATETIME | DEFAULT NULL | NULL | 生效开始时间 |
| effectiveTo | DATETIME | DEFAULT NULL | NULL | 生效结束时间 |

**状态流转：**

| 状态码 | 含义 | 说明 |
|--------|------|------|
| 0 | 草稿 | 保存草稿 |
| 1 | 新建/发布 | 新创建的技术公告 |
| 4 | 审批通过 | 管理员审批通过 |
| 5 | 处理中 | 跟踪任务处理中 |
| 6 | 驳回 | 管理员驳回 |
| 8 | 待确认 | 非管理员更新后状态变为待确认 |
| 10 | 闭环 | 技术公告闭环 |

---

## 2. prob_restore（技术公告跟踪恢复表）

技术公告跟踪任务表，记录各跟踪任务的处理信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| probId | INT(11) | DEFAULT NULL, MUL | 0 | 技术公告ID，关联prob_main.id |
| serialNum | VARCHAR(50) | DEFAULT NULL, MUL | NULL | 设备序列号 |
| itemModel | VARCHAR(50) | DEFAULT NULL, MUL | NULL | 设备类型 |
| processId | INT(11) | DEFAULT NULL, MUL | 0 | 任务流程过程相关信息 |
| officeCode | VARCHAR(25) | DEFAULT NULL | NULL | 办事处编码 |
| conp | VARCHAR(255) | DEFAULT NULL | NULL | 任务发布时的软件版本 |
| boot | VARCHAR(100) | DEFAULT NULL | NULL | Boot版本号 |
| cpld | VARCHAR(100) | DEFAULT NULL | NULL | CPLD/驱动版本号 |
| pcb | VARCHAR(100) | DEFAULT NULL | NULL | PCB/硬件版本号 |
| projectId | INT(11) | DEFAULT NULL, MUL | 0 | 涉及到的项目ID |
| projectName | VARCHAR(255) | DEFAULT NULL | NULL | 项目名称 |
| contractNo | VARCHAR(255) | DEFAULT NULL | NULL | 合同号 |
| assignee | VARCHAR(25) | DEFAULT NULL | NULL | 办理用户 |
| assigneeRole | INT(11) | DEFAULT NULL | 0 | 办理角色 |
| createBy | VARCHAR(25) | DEFAULT NULL | NULL | 创建人 |
| createTime | DATETIME | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |

---

## 3. prob_restore_process（跟踪恢复过程表）

技术公告跟踪任务的处理过程记录表，记录每次恢复状态变更的操作信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| probId | INT(11) | MUL | NULL | 技术公告ID，关联prob_main.probId |
| restoreStatus | INT(11) | - | NULL | 恢复状态编码（10:已指派 20:未接受 30:已处理 40:闭环申请 50:已闭环） |
| restoreRemark | TEXT | - | NULL | 恢复备注/处理说明 |
| createBy | VARCHAR(25) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(25) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |

**样例数据：**
- probId=14, restoreStatus=10, createBy=l00673 → 技术公告14的恢复过程记录，状态为"已指派"

---

## 4. prob_restore_weekly（跟踪恢复周报表）

技术公告跟踪任务的周报记录表，通过fileId关联上传的周报文件。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| probId | INT(11) | MUL | NULL | 技术公告ID，关联prob_main.probId |
| fileId | INT(11) | - | NULL | 周报文件ID，关联fnd_files.id |
| createBy | VARCHAR(25) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(25) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |

**说明：** 周报内容以附件形式上传，通过fileId关联文件表，不直接存储周报文本内容。

---

## 5. prob_soft_version（技术公告软件版本参考表）

技术公告关联的软件版本参考信息表，存储版本组合模板供prob_softwares引用。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| conp | VARCHAR(100) | MUL | NULL | App版本号 |
| cpld | VARCHAR(100) | - | NULL | CPLD/驱动版本号 |
| boot | VARCHAR(100) | - | NULL | Boot版本号 |
| pcb | VARCHAR(100) | - | NULL | PCB/硬件版本号 |
| createdBy | VARCHAR(25) | - | NULL | 创建人 |
| createdTime | DATETIME | - | NULL | 创建时间 |

**说明：** 此表为版本组合参考表，通过INSERT IGNORE去重插入，供prob_softwares表按版本号匹配关联。不直接关联probId。

---

## 6. prob_softwares（技术公告软件版本明细表）

技术公告关联的软件版本明细表，记录受影响的具体版本信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| probId | INT(11) | MUL | 0 | 技术公告ID，关联prob_main.probId |
| conp | VARCHAR(100) | MUL | NULL | App版本号 |
| cpld | VARCHAR(100) | MUL | NULL | CPLD/驱动版本号 |
| boot | VARCHAR(100) | MUL | NULL | Boot版本号 |
| pcb | VARCHAR(100) | MUL | NULL | PCB/硬件版本号 |
| manualEntry | VARCHAR(2048) | - | NULL | 手动输入版本信息 |
| manualEntrySub | VARCHAR(2048) | - | NULL | 手动输入版本子信息 |
| entryType | VARCHAR(100) | - | NULL | 版本类型（如产品大类） |
| entrySeries | VARCHAR(100) | - | NULL | 版本系列 |
| entryStart | VARCHAR(255) | - | NULL | 版本影响起始范围 |
| entryEnd | VARCHAR(255) | - | NULL | 版本影响结束范围 |
| markStart | VARCHAR(255) | - | NULL | 标记起始 |
| markEnd | VARCHAR(255) | - | NULL | 标记结束 |
| affectedType | INT(11) | MUL | 0 | 影响类型（0:受影响 1:已修复） |
| groupId | BIGINT(11) | - | 0 | 版本分组ID，同一组版本可合并显示 |
| splited | INT(11) | - | 0 | 是否已拆分（0:否 1:是） |
| datastate | INT(11) | MUL | 1 | 数据状态（0:失效 1:有效） |
| createBy | VARCHAR(10) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(10) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |
| customInfo | JSON | - | NULL | 自定义扩展信息 |

**样例数据：**
- probId=2, conp=L211C008D011P02P09, datastate=0 → 技术公告2的App版本记录，已失效

---

## 7. prob_read_log（技术公告阅读日志表）

技术公告阅读/查看记录表，记录用户对技术公告的阅读状态。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| probId | INT(11) | NOT NULL | - | 技术公告ID，关联prob_main.probId |
| reader | VARCHAR(25) | NOT NULL | - | 阅读者用户名 |
| readTime | DATETIME | NOT NULL | - | 阅读时间 |
| status | INT(1) | NOT NULL | 0 | 阅读状态（0:未读/已浏览 1:已读/已确认） |
| firstTime | DATETIME | - | NULL | 首次阅读时间 |
| commitTime | DATETIME | - | NULL | 确认阅读时间（status变为1的时间） |

**样例数据：**
- probId=75, reader=w02611, status=0, firstTime=2018-08-29 18:02:30 → 用户w02611浏览了公告75但未确认
- probId=75, reader=w02611, status=1, firstTime=2018-08-29 18:02:30, commitTime=2018-08-29 18:02:37 → 用户w02611确认阅读了公告75

---

## 表间关系概览

```
prob_main (1) ──→ (N) prob_restore           通过 probId
prob_main (1) ──→ (N) prob_restore_process   通过 probId
prob_main (1) ──→ (N) prob_restore_weekly    通过 probId
prob_main (1) ──→ (N) prob_softwares         通过 probId
prob_main (1) ──→ (N) prob_read_log          通过 probId

prob_softwares ──→ prob_soft_version  通过 conp/cpld/boot/pcb版本号匹配（非外键，SQL JOIN关联）

prob_restore_weekly ──→ fnd_files  通过 fileId（文件附件关联）
prob_main ──→ fnd_files  通过 attachments（文件ID列表）
```
