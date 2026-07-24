# 项目管理模块 测试用例

> **模块**: 项目管理  |  **用例数**: 30  |  **更新日期**: 2026-07-24

---

## 4.1 项目列表

### TC-4.1.1 列表页加载

| 项 | 内容 |
|----|------|
| **路由** | `/project/list` |
| **操作** | 点击侧边栏"项目管理" → "项目列表" |
| **API** | `GET /api/project/list?page=1&size=12` |
| **预期** | 项目列表正确渲染，显示项目编号、名称、状态、类型等字段 |

### TC-4.1.2 列表视图与卡片视图切换

| 项 | 内容 |
|----|------|
| **操作** | 点击视图切换按钮在列表/卡片视图间切换 |
| **预期** | 两种视图均正确渲染项目信息 |

### TC-4.1.3 搜索查询

| 项 | 内容 |
|----|------|
| **操作** | 在搜索框输入项目名称关键词，点击查询 |
| **API** | `GET /api/project/list?page=1&size=12&projectName={keyword}` |
| **预期** | 仅显示匹配的项目 |

### TC-4.1.4 重置查询

| 项 | 内容 |
|----|------|
| **操作** | 点击重置按钮 |
| **预期** | 清空搜索条件，显示全部项目 |

### TC-4.1.5 分页导航

| 项 | 内容 |
|----|------|
| **操作** | 切换到第 2 页，修改每页条数 |
| **API** | `GET /api/project/list?page=2&size=20` |
| **预期** | 页码指示器更新，列表内容正确 |

### TC-4.1.6 新建项目

| 项 | 内容 |
|----|------|
| **操作** | 点击"新建项目"按钮，填写表单，提交 |
| **API** | `POST /api/project` |
| **请求体** | `{"projectCode":"TC-001","projectName":"测试项目","projectType":"IMPLEMENT",...}` |
| **预期** | 创建成功，列表中显示新项目 |
| **必填字段** | projectCode, projectName, projectType |

### TC-4.1.7 编辑项目

| 项 | 内容 |
|----|------|
| **操作** | 点击项目行"编辑"按钮，修改字段，保存 |
| **API** | `PUT /api/project` |
| **预期** | 更新成功，列表显示新值 |

### TC-4.1.8 项目审批

| 项 | 内容 |
|----|------|
| **前置** | 项目状态为 `PENDING_APPROVAL` |
| **操作** | 点击"审批"按钮，选择通过/拒绝 |
| **API** | `POST /api/project/{id}/approve` |
| **预期** | 项目状态变更为 `APPROVED` 或 `REJECTED` |
| **关注** | 验证乐观锁 `@Version` 字段是否正常工作 |

### TC-4.1.9 删除项目

| 项 | 内容 |
|----|------|
| **操作** | 点击"删除"按钮，确认删除 |
| **API** | `DELETE /api/project/{id}` |
| **预期** | 项目从列表中移除 |

---

## 4.2 项目工作区

### TC-4.2.1 工作区加载

| 项 | 内容 |
|----|------|
| **路由** | `/project/workspace/{id}` |
| **操作** | 在项目列表中点击项目名称 |
| **预期** | 工作区页面加载，8 个 Tab 正确渲染 |

### TC-4.2.2 项目概览 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"项目概览"Tab |
| **API** | `GET /api/project/{id}` |
| **预期** | 显示项目基本信息、进度、关键里程碑 |

### TC-4.2.3 阶段管理 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"阶段管理"Tab |
| **API** | `GET /api/project/phase/project/{projectId}` |
| **预期** | 阶段列表正确显示，含阶段编码、名称、排序、状态 |

### TC-4.2.4 任务管理 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"任务管理"Tab |
| **API** | `GET /api/implementation/task/project/{projectId}` |
| **预期** | 任务列表正确显示，含任务名称、状态、负责人、计划日期 |

### TC-4.2.5 交付件 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"交付件"Tab |
| **API** | `GET /api/deliverable?projectId={projectId}` |
| **预期** | 交付件列表正确显示 |

### TC-4.2.6 基线 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"基线"Tab |
| **API** | `GET /api/baseline?projectId={projectId}` |
| **预期** | 基线列表正确显示 |

### TC-4.2.7 成员 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"成员"Tab |
| **API** | `GET /api/project/member/project/{projectId}` |
| **预期** | 成员列表正确显示，含用户名、角色 |

### TC-4.2.8 配置 Tab

| 项 | 内容 |
|----|------|
| **操作** | 点击"配置"Tab |
| **路由** | `/project/config/{id}` |
| **预期** | 项目配置页面正确加载 |

---

## 4.3 阶段管理

### TC-4.3.1 创建阶段

| 项 | 内容 |
|----|------|
| **路由** | `/project/phase/{projectId}` |
| **操作** | 点击"新建阶段"按钮，填写表单，保存 |
| **API** | `POST /api/project/phase` |
| **请求体** | `{"projectId":1,"phaseCode":"P1","phaseName":"需求分析","sortOrder":1}` |
| **预期** | 阶段创建成功，列表刷新 |

### TC-4.3.2 编辑阶段

| 项 | 内容 |
|----|------|
| **操作** | 点击阶段行"编辑"按钮，修改字段，保存 |
| **API** | `PUT /api/project/phase` |
| **预期** | 阶段更新成功 |

### TC-4.3.3 删除阶段

| 项 | 内容 |
|----|------|
| **操作** | 点击阶段行"删除"按钮，确认 |
| **API** | `DELETE /api/project/phase/{id}` |
| **预期** | 阶段从列表移除 |

---

## 4.4 任务管理

### TC-4.4.1 创建任务

| 项 | 内容 |
|----|------|
| **操作** | 点击"新建任务"按钮，填写表单，保存 |
| **API** | `POST /api/implementation/task` |
| **请求体** | `{"taskName":"任务1","projectId":1,"taskType":"DESIGN",...}` |
| **预期** | 任务创建成功 |

### TC-4.4.2 开始任务

| 项 | 内容 |
|----|------|
| **前置** | 任务状态为 `PENDING` |
| **操作** | 点击任务行"开始"按钮 |
| **API** | `POST /api/implementation/task/{id}/start` |
| **预期** | 任务状态变为 `IN_PROGRESS`，前端列表状态刷新 |
| **关注** | 验证前端是否自动刷新列表（已知问题 ISS-002） |

### TC-4.4.3 完成任务

| 项 | 内容 |
|----|------|
| **前置** | 任务状态为 `IN_PROGRESS` |
| **操作** | 点击任务行"完成"按钮 |
| **API** | `POST /api/implementation/task/{id}/complete` |
| **预期** | 任务状态变为 `COMPLETED` |

### TC-4.4.4 创建子任务

| 项 | 内容 |
|----|------|
| **操作** | 在任务树中点击"添加子任务" |
| **API** | `POST /api/implementation/task` (parentTaskId 指定父任务) |
| **预期** | 子任务创建成功，父子关系正确建立 |

### TC-4.4.5 任务检查项

| 项 | 内容 |
|----|------|
| **操作** | 在任务详情页添加检查项，勾选/取消勾选 |
| **API** | `POST /api/implementation/task/checklist` |
| **API** | `POST /api/implementation/task/checklist/{id}/check?checked=true` |
| **预期** | 检查项创建成功，勾选状态正确切换 |

### TC-4.4.6 任务依赖

| 项 | 内容 |
|----|------|
| **操作** | 在任务详情页点击"添加依赖"，选择关联任务和依赖类型 |
| **API** | `POST /api/task-dependency` |
| **请求体** | `{"projectId":1,"predecessorTaskId":2,"successorTaskId":3,"dependencyType":"FS"}` |
| **预期** | 依赖关系创建成功 |
| **关注** | 单任务项目无可选依赖项时应有提示（ISS-003） |

### TC-4.4.7 任务详情页

| 项 | 内容 |
|----|------|
| **路由** | `/implementation/task/detail/{id}` |
| **API** | `GET /api/implementation/task/{id}` |
| **预期** | 任务详情正确显示，包含基本信息、检查项、评论、活动记录 |

### TC-4.4.8 任务树列表

| 项 | 内容 |
|----|------|
| **路由** | `/implementation/task/list` |
| **API** | `GET /api/implementation/task/list` |
| **预期** | 树形列表正确渲染，支持展开/折叠 |

### TC-4.4.9 任务依赖关系图

| 项 | 内容 |
|----|------|
| **路由** | `/implementation/task/dependency/{projectId}` |
| **预期** | 依赖关系图正确渲染 |

---

## 4.5 项目模板

### TC-4.5.1 模板列表

| 项 | 内容 |
|----|------|
| **路由** | `/project/template` |
| **API** | `GET /api/project/template/list` |
| **预期** | 模板列表正确显示 |

### TC-4.5.2 创建模板

| 项 | 内容 |
|----|------|
| **路由** | `/project/template/form` |
| **操作** | 填写模板基本信息，配置阶段/任务/交付件，保存 |
| **API** | `POST /api/project/template` |
| **API** | `POST /api/project/template/{id}/draft-snapshot` (保存草稿快照) |
| **预期** | 模板创建成功，草稿快照保存成功 |

### TC-4.5.3 编辑模板

| 项 | 内容 |
|----|------|
| **路由** | `/project/template/form/{id}` |
| **API** | `GET /api/project/template/{id}` |
| **API** | `PUT /api/project/template` |
| **预期** | 模板数据正确回填，更新成功 |

### TC-4.5.4 发布模板

| 项 | 内容 |
|----|------|
| **操作** | 点击"发布"按钮，填写版本号和变更说明 |
| **API** | `POST /api/project/template/{id}/publish` |
| **请求体** | `{"version":"1.0.0","snapshot":{...},"changeLog":"初始发布"}` |
| **预期** | 模板状态变为 `PUBLISHED` |

### TC-4.5.5 版本管理

| 项 | 内容 |
|----|------|
| **路由** | `/project/template/version/{id}` |
| **API** | `GET /api/project/template/{id}/versions` |
| **预期** | 版本时间线正确显示，含版本号、状态、发布时间 |

### TC-4.5.6 从模板创建项目

| 项 | 内容 |
|----|------|
| **操作** | 在模板列表或项目列表点击"从模板创建" |
| **API** | `POST /api/project/template/create-project` |
| **请求体** | `{"templateId":1,"versionId":1,"projectCode":"TC-TPL-001","projectName":"模板项目",...}` |
| **预期** | 项目创建成功，自动继承模板的阶段/任务/交付件配置 |

---

## 4.6 项目配置

### TC-4.6.1 里程碑配置

| 项 | 内容 |
|----|------|
| **路由** | `/project/config/{id}` |
| **操作** | 在里程碑 Tab 添加里程碑，填写名称/类型/计划日期，保存 |
| **API** | `POST /api/project/milestone` |
| **API** | `PUT /api/project/milestone` |
| **预期** | 里程碑创建/更新成功 |

### TC-4.6.2 里程碑进度更新

| 项 | 内容 |
|----|------|
| **前置** | 里程碑已保存（有 id） |
| **操作** | 点击里程碑行"更新进度"按钮，选择实际完成日期，填写说明，提交 |
| **API** | `POST /api/project/milestone/{id}/progress` |
| **请求体** | `{"actualDate":"2026-07-24","description":"已完成"}` |
| **预期** | 进度更新成功，里程碑显示实际完成日期 |
| **关注** | payload 必须使用 `actualDate` 和 `description` 字段 |

### TC-4.6.3 删除里程碑

| 项 | 内容 |
|----|------|
| **操作** | 点击里程碑行删除按钮 |
| **API** | `DELETE /api/project/milestone/{id}` |
| **预期** | 里程碑从列表移除 |

---

## 4.7 项目成员管理

### TC-4.7.1 添加成员

| 项 | 内容 |
|----|------|
| **操作** | 在成员 Tab 点击"添加成员"，选择用户和角色 |
| **API** | `POST /api/project/member` |
| **请求体** | `{"projectId":1,"userId":1,"role":"PM"}` |
| **预期** | 成员添加成功 |

### TC-4.7.2 移除成员

| 项 | 内容 |
|----|------|
| **操作** | 点击成员行"移除"按钮 |
| **API** | `DELETE /api/project/member/{id}` |
| **预期** | 成员从列表移除 |

---

## 4.8 其他项目页面

### TC-4.8.1 项目待办

| 项 | 内容 |
|----|------|
| **路由** | `/project/{id}/todo` |
| **预期** | 待办列表正确显示 |

### TC-4.8.2 项目甘特图

| 项 | 内容 |
|----|------|
| **路由** | `/project/{id}/gantt` |
| **预期** | 甘特图正确渲染任务时间线 |

### TC-4.8.3 主子项目树

| 项 | 内容 |
|----|------|
| **路由** | `/project/tree` |
| **预期** | 项目树形结构正确渲染 |

### TC-4.8.4 交付看板

| 项 | 内容 |
|----|------|
| **路由** | `/project/kanban` |
| **预期** | 看板视图正确渲染，按状态列展示项目 |

---

<!-- 新增用例在此下方追加 -->
