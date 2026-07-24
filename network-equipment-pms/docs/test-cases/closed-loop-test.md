# 闭环测试流程 测试用例

> **模块**: 闭环测试  |  **用例数**: 4  |  **更新日期**: 2026-07-24

> **说明**: 闭环测试用于验证核心业务流程的端到端完整性，每个闭环覆盖一个完整业务链路。  
> **前置条件**: 已登录（admin / admin123），后端服务正常运行

---

## 闭环一：项目创建闭环

> 流程：从模板创建 → 验证数据 → 编辑 → 配置里程碑

### TC-CL.1 项目创建闭环

| 阶段 | 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|------|-----|--------|------|------|
| 1.从模板创建 | /project/list | 选择项目模板，创建新项目 | POST /api/project/create-from-template | `{"templateId":1,"name":"闭环测试项目"}` | 项目创建成功，跳转项目详情页 | 模板数据复制完整性、乐观锁 version 字段 |
| 2.验证数据 | /project/list/{id} | 进入项目详情，核对模板带入数据 | GET /api/project/{id} | - | 项目基本信息、阶段、任务等模板数据完整无误 | 数据一致性、关联数据完整性 |
| 3.编辑项目 | /project/list/{id} | 修改项目名称、描述等字段并保存 | PUT /api/project | `{"id":1,"name":"更新后项目名","version":0}` | 项目信息更新成功，version 递增 | 乐观锁校验（@Version）、并发更新冲突 |
| 4.配置里程碑 | /project/list/{id} | 新增/编辑项目里程碑及进度 | POST /api/project/milestone | `{"projectId":1,"name":"里程碑1","planDate":"2026-08-01"}` | 里程碑创建成功，进度可更新展示 | 里程碑进度 payload 格式正确（参考 FIX-003） |

---

## 闭环二：实施执行闭环

> 流程：阶段管理 → 任务执行(创建/开始/检查项/完成) → 交付件流转(DRAFT→SUBMITTED→REVIEWED→SIGNED→PUBLISHED) → 基线管理

### TC-CL.2 实施执行闭环

| 阶段 | 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|------|-----|--------|------|------|
| 1.阶段管理 | /phase | 进入阶段管理，新增/调整项目阶段 | POST /api/project/phase | `{"projectId":1,"name":"实施阶段"}` | 阶段创建成功，阶段顺序与状态正确 | 阶段排序、状态流转 |
| 2.任务创建 | /implementation/task | 新建实施任务 | POST /api/implementation/task | `{"projectId":1,"phaseId":1,"name":"任务A"}` | 任务创建成功，出现在任务树列表 | 任务树层级、依赖关系配置 |
| 3.任务开始 | /implementation/task | 点击"开始"按钮启动任务 | PUT /api/implementation/task/start | `{"id":1}` | 任务状态变更为"进行中" | 状态机校验、状态变更后列表刷新（参考 ISS-002） |
| 4.检查项完成 | /implementation/task | 勾选任务检查项 | PUT /api/implementation/task/checklist | `{"taskId":1,"checklistId":1,"checked":true}` | 检查项状态更新，任务进度同步推进 | 检查项联动进度计算 |
| 5.任务完成 | /implementation/task | 点击"完成"按钮 | PUT /api/implementation/task/complete | `{"id":1}` | 任务状态变更为"已完成" | 前置检查项是否全部完成校验 |
| 6.交付件流转 | /deliverable | 提交交付件并依次流转状态 | PUT /api/deliverable/status | `{"id":1,"status":"SUBMITTED"}` | 交付件状态按 DRAFT→SUBMITTED→REVIEWED→SIGNED→PUBLISHED 顺序流转 | **状态机不可跳跃**（7 状态机顺序）、每步流转权限校验 |
| 7.基线管理 | /baseline/list | 创建基线并分析偏差 | POST /api/baseline | `{"projectId":1,"name":"实施基线"}` | 基线创建成功，偏差分析数据正确 | 基线快照、偏差计算准确性 |

---

## 闭环三：审批闭环

> 流程：提交审批 → 验证结果

### TC-CL.3 审批闭环

| 阶段 | 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|------|-----|--------|------|------|
| 1.提交审批 | /workflow/approval-center | 选择业务单据提交审批 | POST /api/workflow/approval/submit | `{"businessType":"DELIVERABLE","businessId":1}` | 审批流程发起成功，生成审批记录 | 流程定义匹配、审批人路由（参考 FIX-001） |
| 2.验证结果 | /workflow/approval-detail/{id} | 进入审批详情，办理审批（同意/驳回） | PUT /api/workflow/approval/handle | `{"id":1,"action":"APPROVE","comment":"同意"}` | 审批结果回写业务单据，状态正确流转 | 业务异常捕获（参考 FIX-002）、审批结果同步 |

---

## 闭环四：团队协作闭环

> 流程：成员管理 → 任务分配

### TC-CL.4 团队协作闭环

| 阶段 | 路由 | 操作 | API | 请求体 | 预期 | 关注 |
|------|------|------|-----|--------|------|------|
| 1.成员管理 | /project/list/{id} | 进入项目团队，新增/移除项目成员 | POST /api/project/member | `{"projectId":1,"userId":2,"role":"MEMBER"}` | 成员添加成功，成员列表刷新 | 成员角色权限、重复添加校验 |
| 2.任务分配 | /implementation/task | 将任务分配给项目成员 | PUT /api/implementation/task/assign | `{"id":1,"assigneeId":2}` | 任务分配成功，被分配人收到任务 | 任务通知、工作量统计、单任务依赖可选性（参考 ISS-003） |

---
<!-- 后续用例在此追加 -->
