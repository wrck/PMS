import { del, get, post, put } from '@/utils/request'

export interface ProjectTemplate {
  id?: number
  templateCode: string
  templateName: string
  category: 'IMPLEMENT' | 'MAINTENANCE' | 'CONSULTING'
  description?: string
  status: 'DRAFT' | 'PUBLISHED' | 'DEPRECATED'
  createTime?: string
  updateTime?: string
}

/** 阶段进入条件（对齐后端 com.dp.plat.common.dto.PhaseCriteria） */
export interface PhaseCriteria {
  requirePreviousPhaseComplete?: boolean
  requireApproval?: boolean
}

/** 阶段退出条件（对齐后端 com.dp.plat.common.dto.PhaseExitGate） */
export interface PhaseExitGate {
  requiredDeliverables?: Array<{
    /** 项目态为数据库 Long；模板态为前端生成的字符串 ID */
    deliverableId?: number | string
    deliverableName?: string
    requiredStatus?: string
  }>
  requiredTasks?: Array<{
    phaseId?: number | string
    allCompleted?: boolean
  }>
  requiredMilestones?: Array<{
    milestoneId?: number | string
    mustReached?: boolean
  }>
  requiredApprovals?: Array<{
    approvalType?: string
    mustApproved?: boolean
  }>
}

export interface PhaseDef {
  phaseCode: string
  phaseName: string
  sortOrder: number
  entryCriteria?: PhaseCriteria
  exitCriteria?: PhaseExitGate
}

/** 任务定义（对齐后端 TemplateSnapshot.TaskDef） */
export interface TaskDef {
  taskName: string
  taskType?: string
  parentTaskName?: string
  phaseCode?: string
  plannedHours?: number
  /** 指派角色（对应 TaskNode.assigneeRole） */
  assigneeRole?: string
  /** 任务权重（对应 TaskNode.weight） */
  weight?: number
  priority?: string
  sortOrder?: number
  /** 任务描述 */
  description?: string
}

/** 交付件定义（对齐后端 TemplateSnapshot.DeliverableDef） */
export interface DeliverableDef {
  deliverableName: string
  deliverableType?: string
  phaseCode?: string
  mandatory?: boolean
  approverRole?: string
}

/** 依赖定义（对齐后端 TemplateSnapshot.DependencyDef） */
export interface DependencyDef {
  predecessorTaskName: string
  successorTaskName: string
  dependencyType?: string
  lagDays?: number
}

/** 审批计划定义（对齐后端 TemplateSnapshot.ApprovalPlanDef） */
export interface ApprovalPlanDef {
  approvalType: string
  triggerPhaseCode?: string
  approverRoles?: string[]
}

/** 里程碑定义（对齐后端 TemplateSnapshot.MilestoneDef） */
export interface MilestoneDef {
  milestoneName: string
  milestoneType?: string
  phaseCode?: string
  sortOrder?: number
}

export interface TemplateSnapshot {
  phases?: PhaseDef[]
  tasks?: TaskDef[]
  milestones?: MilestoneDef[]
  deliverables?: DeliverableDef[]
  dependencies?: DependencyDef[]
  approvalPlans?: ApprovalPlanDef[]
  assigneeRules?: Array<{ taskNamePattern?: string; role?: string }>
}

export interface ProjectTemplateVersion {
  id: number
  templateId: number
  version: string
  snapshotJson: TemplateSnapshot
  changeLog?: string
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  publishedAt?: string
  publishedBy?: number
  /** 创建时间（后端返回） */
  createTime?: string
}

export interface PublishVersionRequest {
  version: string
  snapshot: TemplateSnapshot
  changeLog?: string
}

export interface ProjectCreateFromTemplateDTO {
  templateId: number
  versionId: number
  projectCode: string
  projectName: string
  /** 项目类型（对齐后端 Project.projectType） */
  projectType?: string
  customerName?: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  projectManagerId?: number
  /** 项目经理名称冗余字段（对齐 Project.projectManagerName） */
  projectManagerName?: string
  /** 优先级 HIGH/NORMAL/LOW（对齐 Project.priority） */
  priority?: string
  description?: string
  projectObjective?: string
  projectScope?: string
  members?: { userId: number; role: string }[]
  configOverrides?: Record<string, string>
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export function listTemplates(params: {
  page?: number
  size?: number
  templateName?: string
  category?: string
  status?: string
}) {
  return get<PageResult<ProjectTemplate>>('/api/project/template/list', params)
}

export function getTemplate(id: number) {
  return get<ProjectTemplate>(`/api/project/template/${id}`)
}

export function createTemplate(data: ProjectTemplate) {
  return post<ProjectTemplate>('/api/project/template', data)
}

export function updateTemplate(data: ProjectTemplate) {
  return put<ProjectTemplate>('/api/project/template', data)
}

export function deleteTemplate(id: number) {
  return del<void>(`/api/project/template/${id}`)
}

export function listTemplateVersions(id: number, page = 1, size = 10) {
  return get<PageResult<ProjectTemplateVersion>>(`/api/project/template/${id}/versions`, { page, size })
}

export function publishVersion(id: number, data: PublishVersionRequest) {
  return post<ProjectTemplateVersion>(`/api/project/template/${id}/publish`, data)
}

export function getPublishedVersion(id: number) {
  return get<ProjectTemplateVersion>(`/api/project/template/${id}/published-version`)
}

/**
 * 保存草稿快照（创建或更新 DRAFT 状态版本记录，不影响模板发布状态）。
 * 用于新建/编辑模板时持久化阶段/任务/交付件等详细配置。
 */
export function saveDraftSnapshot(id: number, snapshot: TemplateSnapshot) {
  return put<ProjectTemplateVersion>(`/api/project/template/${id}/draft-snapshot`, snapshot)
}

/**
 * 获取模板草稿版本（最新 DRAFT 状态版本，无则返回 null）。
 */
export function getDraftVersion(id: number) {
  return get<ProjectTemplateVersion | null>(`/api/project/template/${id}/draft-version`)
}

export function createProjectFromTemplate(data: ProjectCreateFromTemplateDTO): Promise<{ id: number }> {
  return post<{ id: number }>('/api/project/template/create-project', data)
}

/**
 * 废弃模板（PUBLISHED → DEPRECATED）。
 */
export function deprecateTemplate(id: number) {
  return put<ProjectTemplate>(`/api/project/template/${id}/deprecate`)
}

/**
 * 重新启用模板（DEPRECATED → PUBLISHED）。
 */
export function enableTemplate(id: number) {
  return put<ProjectTemplate>(`/api/project/template/${id}/enable`)
}

/**
 * 复制模板（深拷贝源模板快照到新模板，新模板状态为 DRAFT）。
 */
export function copyTemplate(id: number, data: { templateCode: string; templateName: string }) {
  return post<ProjectTemplate>(`/api/project/template/${id}/copy`, data)
}
