# 交付件管理模块 测试用例

> **模块**: 交付件管理  |  **用例数**: 6  |  **更新日期**: 2026-07-24

---

### TC-5.1 交付件列表

| 项 | 内容 |
|----|------|
| **路由** | `/deliverable` |
| **操作** | 点击侧边栏"终验交付物" |
| **API** | `GET /api/deliverable/list` |
| **预期** | 交付件列表正确显示 |

### TC-5.2 创建交付件

| 项 | 内容 |
|----|------|
| **操作** | 点击"新建交付件"，填写表单，保存 |
| **API** | `POST /api/deliverable` |
| **预期** | 交付件创建成功，默认状态 `DRAFT` |

### TC-5.3 交付件状态流转（7 状态机）

| 步骤 | 操作 | API | 预期状态 |
|------|------|-----|----------|
| 1 | 创建交付件 | `POST /api/deliverable` | `DRAFT` |
| 2 | 提交审核 | `POST /api/deliverable/{id}/submit` | `SUBMITTED` |
| 3 | 审核通过 | `POST /api/deliverable/{id}/review?passed=true` | `REVIEWED` |
| 4 | 签署 | `POST /api/deliverable/{id}/sign` | `SIGNED` |
| 5 | 发布 | `POST /api/deliverable/{id}/publish` | `PUBLISHED` |
| 6 | 引用 | `POST /api/deliverable/{id}/reference` | `REFERENCED` |
| 7 | 归档 | `POST /api/deliverable/{id}/archive` | `ARCHIVED` |

**关注**: 状态必须按顺序流转，不可跳跃

### TC-5.4 交付件详情页

| 项 | 内容 |
|----|------|
| **路由** | `/deliverable/detail/{id}` |
| **API** | `GET /api/deliverable/{id}` |
| **预期** | 详情页正确显示，含基本信息、版本历史、签署记录 |

### TC-5.5 交付件全生命周期

| 项 | 内容 |
|----|------|
| **路由** | `/deliverable/lifecycle` |
| **预期** | 全生命周期视图正确渲染 |

### TC-5.6 上传交付件版本

| 项 | 内容 |
|----|------|
| **操作** | 在交付件详情页点击"上传新版本"，选择文件 |
| **API** | `POST /api/deliverable/{id}/upload` (multipart/form-data) |
| **预期** | 新版本创建成功 |

---

<!-- 新增用例在此下方追加 -->
