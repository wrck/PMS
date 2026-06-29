# P0 迁移进度

> 更新时间：2026-06-29

## 第一批：补全项目管理缺失功能 ✅

| # | 功能 | 端点 | 状态 |
|---|------|------|------|
| 1 | PmsProject 补全字段 | - | ✅ |
| 2 | updateMember | `PUT /member/{id}` | ✅ |
| 3 | saveInstallAddress | `POST /{id}/install-address` | ✅ |
| 4 | updateProjectExecutionState | `PUT /{id}/execution-state` | ✅ |
| 5 | instruction | `POST /{id}/instruction` | ✅ |
| 6 | queryContractList | `GET /contract/list` | ✅ |
| 7 | mergeContract | `POST /{id}/merge-contract` | ✅ |
| 8 | branchContract | `POST /{id}/branch-contract` | ✅ |
| 9 | uploadDeliverableFile | `POST /{id}/deliver` | ✅ |
| 10 | deleteDeliverById | `DELETE /deliver/{deliverId}` | ✅ |

## 第二批：技术公告模块 ✅

| # | 功能 | 端点 | 状态 |
|---|------|------|------|
| 1 | PmsProb 扩展字段 | - | ✅ 13→30+字段 |
| 2 | PmsProbSoftVersion Entity | - | ✅ 新建 |
| 3 | PmsProbRestore Entity | - | ✅ 新建 |
| 4 | PmsProbProduct Entity | - | ✅ 新建 |
| 5 | PmsProbReadLog Entity | - | ✅ 新建 |
| 6 | 列表查询 | `GET /api/prob/list` | ✅ |
| 7 | 详情 | `GET /api/prob/{id}` | ✅ |
| 8 | 创建/更新/删除 | `POST/PUT/DELETE` | ✅ |
| 9 | 软件版本管理 | `GET/POST /{id}/soft-versions` | ✅ |
| 10 | 恢复任务CRUD | `GET/POST/PUT/DELETE /restore` | ✅ |
| 11 | 批量删除恢复任务 | `DELETE /restore/batch` | ✅ |
| 12 | 产品管理 | `GET/POST /{id}/products` | ✅ |
| 13 | 阅读记录 | `POST /{id}/read` | ✅ |
| 14 | 阅读日志 | `GET /{id}/read-logs` | ✅ |
| 15 | 审核 | `POST /{id}/audit` | ✅ |
| 16 | 统计 | `GET /statistics` | ✅ |

## 第三批：售前项目模块 ✅

| # | 功能 | 端点 | 状态 |
|---|------|------|------|
| 1 | PmsPresales 扩展字段 | - | ✅ 新增 lendfiles, hasTransfer, hasRma, closeRemark 等 |
| 2 | PmsPresalesProduct Entity | - | ✅ 新建 |
| 3 | PmsPresalesTask Entity | - | ✅ 新建 |
| 4 | PmsPresalesComment Entity | - | ✅ 新建 |
| 5 | 列表查询 | `GET /api/presales/list` | ✅ |
| 6 | 详情 | `GET /api/presales/{id}` | ✅ |
| 7 | 创建/更新/删除 | `POST/PUT/DELETE` | ✅ |
| 8 | 发起流程 | `POST /{id}/start-flow` | ✅ |
| 9 | 重新申请 | `POST /{id}/re-apply` | ✅ |
| 10 | 服务经理审批 | `POST /{id}/sm-audit` | ✅ |
| 11 | 项目经理审批 | `POST /{id}/pm-audit` | ✅ |
| 12 | 工程管理部审批 | `POST /{id}/em-audit` | ✅ |
| 13 | 终止并关闭 | `POST /{id}/terminate` | ✅ |
| 14 | 产品管理 | `GET/POST /{id}/products` | ✅ |
| 15 | 任务管理 | `GET/PUT /{id}/tasks` | ✅ |
| 16 | 审批意见 | `GET /{id}/comments` | ✅ |
| 17 | 上传交付件 | `POST /{id}/deliver` | ✅ |
| 18 | 更新确认文件 | `PUT /{id}/confirm-files` | ✅ |
| 19 | 导出 | `GET /export` | ✅ |

## 新增文件汇总

### Entity (13个)
- PmsInstruction, PmsProjectContract, PmsProjectGroupRelationship, PmsProjectProductLine
- PmsProbSoftVersion, PmsProbRestore, PmsProbProduct, PmsProbReadLog
- PmsPresalesProduct, PmsPresalesTask, PmsPresalesComment

### Mapper (12个)
- PmsInstructionMapper, PmsProjectContractMapper, PmsProjectGroupRelationshipMapper
- PmsProjectProductLineMapper, PmsProjectTaskExtMapper
- PmsProbSoftVersionMapper, PmsProbRestoreMapper, PmsProbProductMapper, PmsProbReadLogMapper
- PmsPresalesProductMapper, PmsPresalesTaskMapper, PmsPresalesCommentMapper

### Controller 新增端点汇总

**项目管理 (PmsProjectController)** - 9个新端点
```
PUT    /member/{id}                 更新成员
POST   /{id}/install-address       安装地址
PUT    /{id}/execution-state       实施状态
POST   /{id}/instruction           批示
GET    /contract/list              合同列表
POST   /{id}/merge-contract        合并
POST   /{id}/branch-contract       拆分
POST   /{id}/deliver               交付件上传
DELETE /deliver/{deliverId}        交付件删除
```

**技术公告 (ProbController)** - 12个新端点
```
GET    /{id}/soft-versions         软件版本列表
POST   /{id}/soft-versions         保存软件版本
GET    /{id}/restores              恢复任务列表
POST   /restore                    保存恢复任务
PUT    /restore                    更新恢复任务
DELETE /restore/batch              批量删除恢复任务
GET    /{id}/products              产品列表
POST   /product                    保存产品
POST   /{id}/read                  记录阅读
GET    /{id}/read-logs             阅读日志
POST   /{id}/audit                 审核
GET    /statistics                 统计
```

**售前项目 (PmsPresalesController)** - 12个新端点
```
POST   /{id}/re-apply              重新申请
POST   /{id}/sm-audit              SM审批
POST   /{id}/pm-audit              PM审批
POST   /{id}/em-audit              EM审批
POST   /{id}/terminate             终止关闭
GET    /{id}/products              产品列表
POST   /product                    保存产品
GET    /{id}/tasks                 任务列表
PUT    /task                       更新任务
GET    /{id}/comments              审批意见
POST   /{id}/deliver               上传交付件
PUT    /{id}/confirm-files         确认文件
GET    /export                     导出
```

## 下一步：P1 第四批（分包管理）
