# 回访管理表结构（pm_cl_*）

> 数据库：dppms_d365 (MySQL)  
> 模块：项目闭环回访管理  
> 命名前缀：pm_cl_ (Closed Loop)

---

## 1. pm_cl_callback（回访申请表）

项目回访申请主表，记录回访流程的发起信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT(11) | DEFAULT NULL, MUL | NULL | 项目ID，关联pm_project_header.projectId |
| instId | VARCHAR(25) | DEFAULT NULL, MUL | NULL | 流程实例ID（Activiti工作流） |
| remark | TEXT | DEFAULT NULL | NULL | 回访备注 |
| applyState | INT(11) | DEFAULT NULL | NULL | 申请状态（-1:草稿 1:审批中 2:审批通过） |
| applyBy | VARCHAR(25) | DEFAULT NULL | NULL | 申请人编码 |
| applyTime | DATETIME | DEFAULT NULL | NULL | 申请时间 |
| createBy | VARCHAR(25) | DEFAULT NULL | NULL | 创建人 |
| createTime | TIMESTAMP | DEFAULT NULL | NULL | 创建时间 |
| updateBy | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updateTime | DATETIME | DEFAULT NULL | NULL | 更新时间 |
| effectiveFrom | DATETIME | DEFAULT NULL | NULL | 生效开始时间 |
| effectiveTo | DATETIME | DEFAULT NULL | NULL | 生效结束时间 |

---

## 2. pm_cl_callback_quesnaire（回访问卷关联表）

回访与问卷模板的关联表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| callbackId | INT | NOT NULL, FK | - | 回访ID，关联pm_cl_callback.id |
| quesnaireId | INT | NOT NULL | - | 问卷模板ID |
| version | INT | - | 1 | 问卷版本号 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 3. pm_cl_evaluation_header（评价表头）

项目/转包闭环评价主表，记录评价流程信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) UNSIGNED | PK, AUTO_INCREMENT | - | 主键ID |
| projectCode | VARCHAR(45) | NOT NULL, MUL | - | 评测项目编码 |
| projectId | INT(11) | NOT NULL, MUL | 0 | 项目ID |
| projectName | VARCHAR(120) | DEFAULT NULL | NULL | 项目名称 |
| applyHeaderId | INT(11) | NOT NULL | 0 | 申请表ID |
| evaluationTime | DATETIME | DEFAULT NULL | 0000-00-00 00:00:00 | 审核时间 |
| evaluationPeopleId | VARCHAR(25) | DEFAULT NULL | NULL | 审核人员用户名 |
| evaluationPeopleName | VARCHAR(45) | DEFAULT NULL | NULL | 审核人员姓名 |
| evaluationScore | DOUBLE | NOT NULL | 0 | 评测总分数 |
| evaluationComment | TEXT | DEFAULT NULL | NULL | 项目评价（驳回时为驳回原因） |
| evaluationResult | INT(11) | NOT NULL | 0 | 评测结果（通过/未通过） |
| evaluationType | INT(11) | NOT NULL | 0 | 400回访/项目组总分评定 |
| status | INT(11) | NOT NULL | 0 | 状态 |
| nextAcceptPerson | VARCHAR(25) | DEFAULT NULL | NULL | 下一个接收申请的人员 |
| nextAcceptPersonName | VARCHAR(25) | DEFAULT NULL | NULL | 下一审批人姓名 |
| createdPerson | VARCHAR(25) | DEFAULT NULL | NULL | 创建人 |
| createdTime | DATETIME | DEFAULT NULL | 0000-00-00 00:00:00 | 创建时间 |
| updatedPerson | VARCHAR(25) | DEFAULT NULL | NULL | 更新人 |
| updatedTime | DATETIME | DEFAULT NULL | 0000-00-00 00:00:00 | 更新时间 |

---

## 4. pm_cl_quesnaire_result_header（问卷结果表头）

回访问卷填写结果主表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| evaluationHeaderId | INT | NOT NULL, FK | - | 评价ID，关联pm_cl_evaluation_header.id |
| quesnaireId | INT | NOT NULL | - | 问卷模板ID |
| status | INT | - | 0 | 状态（0:未填写 1:已填写） |
| totalScore | DECIMAL(5,2) | - | 0 | 总得分 |
| passScore | DECIMAL(5,2) | - | 0 | 及格分数 |
| isPass | INT | - | 0 | 是否及格（0:不及格 1:及格） |
| fillPerson | VARCHAR(100) | - | NULL | 填写人 |
| fillTime | DATETIME | - | NULL | 填写时间 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 5. pm_cl_quesnaire_result_line（问卷结果明细表）

问卷各题目的填写结果明细。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| resultHeaderId | INT | NOT NULL, FK | - | 结果表头ID，关联pm_cl_quesnaire_result_header.id |
| templateLineId | INT | NOT NULL | - | 问卷模板行ID，关联pm_cl_quesnaire_template_line.id |
| questionContent | TEXT | - | NULL | 题目内容 |
| questionScore | DECIMAL(5,2) | - | 0 | 题目分值 |
| actualScore | DECIMAL(5,2) | - | 0 | 实际得分 |
| selectedOption | VARCHAR(500) | - | NULL | 选中选项 |
| remark | VARCHAR(500) | - | NULL | 备注 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 6. pm_cl_quesnaire_template_header（问卷模板表头）

回访问卷模板主表，定义问卷的基本信息和评分规则。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(10) UNSIGNED | PK, AUTO_INCREMENT | - | 主键ID |
| questionnaireTemplateNum | VARCHAR(45) | NOT NULL | - | 问卷模板编号（格式：CH+yyyyMMdd+序号） |
| questionnaireTemplateName | VARCHAR(200) | NOT NULL | - | 问卷模板名称 |
| questionnaireScore | DOUBLE | NOT NULL | 0 | 问卷总分 |
| questionnairePassScore | DOUBLE | NOT NULL | 0 | 及格分数 |
| questionnaireStatus | INT(11) | NOT NULL | 0 | 模板状态（0:草稿 1:启用 -1:停用） |
| effectiveStartTime | DATETIME | - | NULL | 生效开始时间 |
| effectiveEndTime | DATETIME | - | NULL | 生效结束时间 |
| createdTime | DATETIME | - | NULL | 创建时间 |
| updatedTime | DATETIME | - | NULL | 更新时间 |
| createdPerson | VARCHAR(25) | - | NULL | 创建人 |
| updatedPerson | VARCHAR(25) | - | NULL | 更新人 |
| quesType | VARCHAR(25) | MUL | NULL | 问卷类型（10:项目回访 30:闭环建议 presales:售前回访） |
| markIndexs | VARCHAR(45) | - | NULL | 评分指标列表（逗号分隔索引号） |

**样例数据：**
- id=1, questionnaireTemplateNum=CH20150703001, questionnaireTemplateName=400工程回访问卷, questionnaireScore=90, questionnairePassScore=55, questionnaireStatus=1, quesType=10
- id=2, questionnaireTemplateNum=CH20180621007, questionnaireTemplateName=闭环建议第二版, questionnaireScore=30, questionnairePassScore=19, questionnaireStatus=1, quesType=30

---

## 7. pm_cl_quesnaire_template_line（问卷模板明细表）

问卷模板题目明细表，定义问卷中的各题目。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) UNSIGNED | PK, AUTO_INCREMENT | - | 主键ID |
| questionContent | VARCHAR(200) | NOT NULL | - | 题目内容 |
| questionType | INT(11) | NOT NULL | - | 题目类型（1:单选 2:多选 3:填空） |
| questionScore | DOUBLE | NOT NULL | 0 | 题目分值 |
| questionRemark | VARCHAR(200) | - | NULL | 题目备注/说明 |
| questionNum | INT(11) | NOT NULL | 0 | 题目序号（排序用） |
| quesnaireTemplateHeaderId | INT(11) | NOT NULL, MUL | 0 | 模板表头ID，关联pm_cl_quesnaire_template_header.id |
| questionStatus | INT(11) | - | 0 | 题目状态（0:禁用 1:启用） |
| effectiveStartTime | DATETIME | - | NULL | 生效开始时间 |
| effectiveEndTime | DATETIME | - | NULL | 生效结束时间 |
| createdTime | DATETIME | - | NULL | 创建时间 |
| updatedTime | DATETIME | - | NULL | 更新时间 |
| createdPerson | VARCHAR(25) | - | NULL | 创建人 |
| updatedPerson | VARCHAR(25) | - | NULL | 更新人 |
| questionTypeForCB | VARCHAR(10) | - | NULL | CB回访专用题目类型（10:通用题目） |

**样例数据：**
- id=1, questionContent=对本工程施工整体满意度如何, questionType=1, questionScore=20, questionNum=1, quesnaireTemplateHeaderId=1, questionTypeForCB=10

---

## 8. pm_cl_quesnaire_template_options（问卷模板选项表）

问卷题目选项表，定义选择题的各选项。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT(11) UNSIGNED | PK, AUTO_INCREMENT | - | 主键ID |
| questionId | INT(11) | NOT NULL | 0 | 题目ID，关联pm_cl_quesnaire_template_line.id |
| questionOptionNum | INT(11) | NOT NULL | 0 | 选项序号 |
| questionOptionsContent | VARCHAR(200) | NOT NULL | - | 选项内容 |
| questionOptionScore | DOUBLE | - | 0 | 选项对应分值 |
| quesnaireTemplateHeaderId | INT(11) | NOT NULL | 0 | 模板表头ID，关联pm_cl_quesnaire_template_header.id |
| effectiveStartTime | DATETIME | - | NULL | 生效开始时间 |
| effectiveEndTime | DATETIME | - | NULL | 生效结束时间 |
| createdTime | DATETIME | - | NULL | 创建时间 |
| updatedTime | DATETIME | - | NULL | 更新时间 |
| createdPerson | VARCHAR(25) | - | NULL | 创建人 |
| updatedPerson | VARCHAR(25) | - | NULL | 更新人 |
| quesLineType | VARCHAR(10) | - | NULL | 选项行类型 |

**样例数据：**
- id=1, questionId=1, questionOptionNum=1, questionOptionsContent=满意, questionOptionScore=20, quesnaireTemplateHeaderId=1
- id=2, questionId=1, questionOptionNum=2, questionOptionsContent=一般, questionOptionScore=15, quesnaireTemplateHeaderId=1
- id=3, questionId=1, questionOptionNum=3, questionOptionsContent=不满意, questionOptionScore=0, quesnaireTemplateHeaderId=1

---

## 表间关系概览

```
pm_cl_callback (1) ──→ (N) pm_cl_callback_quesnaire           通过 callbackId
pm_cl_evaluation_header (1) ──→ (N) pm_cl_quesnaire_result_header  通过 evaluationHeaderId
pm_cl_quesnaire_result_header (1) ──→ (N) pm_cl_quesnaire_result_line  通过 resultHeaderId

pm_cl_quesnaire_template_header (1) ──→ (N) pm_cl_quesnaire_template_line     通过 quesnaireTemplateHeaderId
pm_cl_quesnaire_template_line (1) ──→ (N) pm_cl_quesnaire_template_options    通过 id(questionId)

pm_cl_quesnaire_result_header ──→ pm_cl_quesnaire_template_header  通过 quesnaireId
pm_cl_quesnaire_result_line ──→ pm_cl_quesnaire_template_line      通过 templateLineId
```
