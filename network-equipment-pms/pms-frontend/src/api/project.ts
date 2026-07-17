import { del, get, post, put } from '@/utils/request'

// ===================== Project =====================

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
