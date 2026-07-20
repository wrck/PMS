import { del, get, post, put } from '@/utils/request'

// ===================== Project =====================
//
// 字段名兼容说明（重要）
// ----------------------
// 历史 `Project` interface 使用短名 `code/name/type/managerName`，
// 但后端 `pms-project/.../entity/Project.java` 使用长前缀
// `projectCode/projectName/projectType/projectManagerName`，无 Jackson
// 字段映射兜底，理论上 `POST /api/project` 会因 @NotBlank 校验失败返回 400。
//
// 为消除这一前后端字段名不一致风险，已建立「数据集成校验对象」：
// - `@/validators/project.ts` 定义了 `projectSchema`（与后端实体字段一一对应）
//   和 `projectFieldMapping`（旧短名 → 后端长前缀的映射表）。
// - `@/utils/request.ts` 请求拦截器在发送写操作前，自动调用
//   `projectRequestValidator` 做字段映射 + 必填/类型/范围校验。
// - 因此现有 view 代码可继续使用 `name/type/managerName` 短名提交，
//   拦截器会自动转换为 `projectName/projectType/projectManagerName`。
//
// 新代码推荐使用 `@/validators/project.ts` 中的 `ProjectDTO` interface
// （字段名与后端严格一致），逐步替代本文件的短名 `Project`。

/** 项目状态枚举 */
export type ProjectStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'IN_PROGRESS'
  | 'INITIAL_ACCEPTANCE'
  | 'FINAL_ACCEPTANCE'
  | 'COMPLETED'
  | 'CLOSED'
  | 'REJECTED'

/** 项目类型枚举 */
export type ProjectType = 'NETWORK_DEVICE' | 'SECURITY' | 'DATACENTER'

/**
 * 项目（前端短名版本，向后兼容）。
 *
 * @deprecated 新代码请使用 `@/validators/project.ts` 中的 `ProjectDTO`，
 *             其字段名与后端 `Project` 实体严格一致。
 */
export interface Project {
  id?: number
  code?: string
  name: string
  type?: ProjectType
  customerName?: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  managerName?: string
  priority?: number
  description?: string
  status?: ProjectStatus
  progress?: number
  createTime?: string
}

/**
 * 把前端短名 Project 转换为后端长前缀字段名的 payload。
 *
 * 用于显式转换（推荐新代码使用），避免依赖拦截器隐式映射。
 * 拦截器层也会做同样的映射作为兜底，但显式转换更安全、可追溯。
 *
 * @example
 * ```ts
 * const payload = toProjectPayload(form)  // { projectName, projectType, ... }
 * await createProject(payload)
 * ```
 */
export function toProjectPayload(form: Project): Record<string, unknown> {
  const payload: Record<string, unknown> = {}
  if (form.id != null) payload.id = form.id
  if (form.code != null) payload.projectCode = form.code
  if (form.name != null) payload.projectName = form.name
  if (form.type != null) payload.projectType = form.type
  if (form.customerName != null) payload.customerName = form.customerName
  if (form.customerContact != null) payload.customerContact = form.customerContact
  if (form.customerPhone != null) payload.customerPhone = form.customerPhone
  if (form.contractNo != null) payload.contractNo = form.contractNo
  if (form.contractAmount != null) payload.contractAmount = form.contractAmount
  if (form.planStartDate != null) payload.planStartDate = form.planStartDate
  if (form.planEndDate != null) payload.planEndDate = form.planEndDate
  if (form.managerName != null) payload.projectManagerName = form.managerName
  if (form.priority != null) payload.priority = form.priority
  if (form.description != null) payload.description = form.description
  if (form.status != null) payload.status = form.status
  if (form.progress != null) payload.progress = form.progress
  return payload
}

export interface ProjectListResult {
  records: Project[]
  total: number
  page: number
  size: number
}

/** 看板数据：按状态分组的项目列表 */
export type ProjectDashboard = Partial<Record<ProjectStatus, Project[]>>

export function createProject(data: Project): Promise<Project> {
  return post<Project>('/api/project', data)
}

export function getProject(id: number): Promise<Project> {
  return get<Project>(`/api/project/${id}`)
}

export function listProjects(params: {
  page: number
  size: number
  projectName?: string
  status?: string
}): Promise<ProjectListResult> {
  return get<ProjectListResult>('/api/project/list', params)
}

export function updateProject(data: Project): Promise<Project> {
  return put<Project>('/api/project', data)
}

export function deleteProject(id: number): Promise<void> {
  return del<void>(`/api/project/${id}`)
}

export function approveProject(id: number): Promise<void> {
  return post<void>(`/api/project/${id}/approve`)
}

export function getDashboard(): Promise<ProjectDashboard> {
  return get<ProjectDashboard>('/api/project/dashboard')
}

// ===================== 主子项目树与进度（Phase 3 Story 2） =====================

/** 主子项目树节点（对齐后端 ProjectTreeNode DTO） */
export interface ProjectTreeNode {
  id: number
  projectCode?: string
  projectName: string
  status?: string
  parentProjectId?: number
  projectPath?: string
  depth?: number
  progress?: number
  currentPhaseId?: number
  children: ProjectTreeNode[]
}

/** 项目进度汇总 */
export interface ProjectProgress {
  projectId: number
  projectName: string
  ownProgress: number
  aggregatedProgress: number
}

/** 主子项目树（递归）— GET /api/project/{id}/tree */
export function getProjectTree(id: number): Promise<ProjectTreeNode> {
  return get<ProjectTreeNode>(`/api/project/${id}/tree`)
}

/** 项目进度汇总（含子项目加权平均）— GET /api/project/{id}/progress */
export function getProjectProgress(id: number): Promise<ProjectProgress> {
  return get<ProjectProgress>(`/api/project/${id}/progress`)
}

/** 创建子项目 — POST /api/project/{id}/subproject（TD-P8-006） */
export function createSubproject(id: number, data: Project): Promise<Project> {
  return post<Project>(`/api/project/${id}/subproject`, data)
}

/** 关闭主项目（含子项目校验）— POST /api/project/{id}/close（TD-P8-006） */
export function closeProject(id: number): Promise<Project> {
  return post<Project>(`/api/project/${id}/close`)
}

/** 取消项目 — POST /api/project/{id}/cancel（TD-P8-006） */
export function cancelProject(id: number): Promise<Project> {
  return post<Project>(`/api/project/${id}/cancel`)
}

// ===================== Milestone =====================

export interface Milestone {
  id?: number
  projectId?: number
  name: string
  type?: string
  plannedDate?: string
  actualDate?: string
  description?: string
  status?: string
  progress?: number
}

export function createMilestone(data: Milestone): Promise<Milestone> {
  return post<Milestone>('/api/project/milestone', data)
}

export function updateMilestone(data: Milestone): Promise<Milestone> {
  return put<Milestone>('/api/project/milestone', data)
}

export function deleteMilestone(id: number): Promise<void> {
  return del<void>(`/api/project/milestone/${id}`)
}

export function listMilestones(projectId: number): Promise<Milestone[]> {
  return get<Milestone[]>(`/api/project/milestone/project/${projectId}`)
}

export function updateMilestoneProgress(
  id: number,
  data: { actualDate: string; description: string }
): Promise<void> {
  return post<void>(`/api/project/milestone/${id}/progress`, data)
}

// ===================== Final Acceptance =====================

export interface FinalAcceptance {
  id?: number
  projectId?: number
  report?: string
  status?: string
  applicantName?: string
  applyDate?: string
  opinion?: string
  acceptDate?: string
}

export function applyAcceptance(data: { projectId: number; report: string }): Promise<FinalAcceptance> {
  return post<FinalAcceptance>('/api/project/acceptance/apply', data)
}

export function approveAcceptance(id: number, data: { opinion: string }): Promise<void> {
  return post<void>(`/api/project/acceptance/${id}/approve`, data)
}

export function rejectAcceptance(id: number, data: { opinion: string }): Promise<void> {
  return post<void>(`/api/project/acceptance/${id}/reject`, data)
}

export function getAcceptanceByProject(projectId: number): Promise<FinalAcceptance | null> {
  return get<FinalAcceptance | null>(`/api/project/acceptance/${projectId}`)
}
