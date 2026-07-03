import { del, get, post, put } from '@/utils/request'
import type { PageQuery, PageResult } from './system'

// ===================== Implementation Task =====================

/** Implementation task type */
export type TaskType = 'OEM' | 'AGENT'

/** Implementation task status */
export type TaskStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CONFIRMED'
  | 'REJECTED'

export interface ImplTask {
  id?: number
  taskName: string
  taskType?: TaskType
  projectId: number
  projectName?: string
  milestoneId?: number
  engineerId?: number
  engineerName?: string
  agentId?: number
  agentName?: string
  status?: TaskStatus
  progress?: number
  planStartDate?: string
  planEndDate?: string
  actualStart?: string
  actualEnd?: string
  description?: string
  remark?: string
  createTime?: string
}

export interface TaskPageQuery extends PageQuery {
  projectId?: number
  taskType?: TaskType
  status?: TaskStatus
}

/** OEM task assignment payload */
export interface OemAssignPayload {
  taskName: string
  projectId: number
  milestoneId?: number
  engineerId: number
  engineerName?: string
  planStartDate?: string
  planEndDate?: string
}

/** Agent task assignment payload */
export interface AgentAssignPayload {
  taskName: string
  projectId: number
  milestoneId?: number
  agentId: number
  planStartDate?: string
  planEndDate?: string
}

export function assignOemTask(data: OemAssignPayload): Promise<ImplTask> {
  return post<ImplTask>('/api/impl/task/oem/assign', data)
}

export function assignAgentTask(data: AgentAssignPayload): Promise<ImplTask> {
  return post<ImplTask>('/api/impl/task/agent/assign', data)
}

export function acceptTask(id: number): Promise<void> {
  return post<void>(`/api/impl/task/${id}/accept`)
}

export function startTask(id: number): Promise<void> {
  return post<void>(`/api/impl/task/${id}/start`)
}

export interface ProgressReportPayload {
  progressPercent: number
  workLog: string
  photoUrls?: string[]
}

export function reportTaskProgress(id: number, data: ProgressReportPayload): Promise<void> {
  return post<void>(`/api/impl/task/${id}/progress`, data)
}

export function completeTask(id: number, description?: string): Promise<void> {
  return post<void>(`/api/impl/task/${id}/complete`, { description })
}

export function confirmTask(id: number, opinion?: string): Promise<void> {
  return post<void>(`/api/impl/task/${id}/confirm`, { opinion })
}

export function rejectTask(id: number, opinion?: string): Promise<void> {
  return post<void>(`/api/impl/task/${id}/reject`, { opinion })
}

export function getTaskDetail(id: number): Promise<ImplTask> {
  return get<ImplTask>(`/api/impl/task/${id}`)
}

export function getTaskPage(params: TaskPageQuery): Promise<PageResult<ImplTask>> {
  return get<PageResult<ImplTask>>('/api/impl/task/list', params)
}

export function getTasksByProject(projectId: number): Promise<ImplTask[]> {
  return get<ImplTask[]>(`/api/impl/task/project/${projectId}`)
}

// ===================== Task Progress =====================

export interface TaskProgress {
  id?: number
  taskId: number
  progressPercent: number
  workLog?: string
  photoUrls?: string[]
  reporter?: string
  reportTime?: string
}

export function getProgressByTask(taskId: number): Promise<TaskProgress[]> {
  return get<TaskProgress[]>(`/api/impl/progress/task/${taskId}`)
}

// ===================== Agent =====================

export interface Agent {
  id?: number
  agentName: string
  agentCode?: string
  contactPerson?: string
  contactPhone?: string
  contactEmail?: string
  address?: string
  qualification?: string
  overallScore?: number
  status?: number
  remark?: string
  createTime?: string
}

export function getAgentPage(params: PageQuery): Promise<PageResult<Agent>> {
  return get<PageResult<Agent>>('/api/impl/agent/list', params)
}

export function getAgentDetail(id: number): Promise<Agent> {
  return get<Agent>(`/api/impl/agent/${id}`)
}

export function createAgent(data: Agent): Promise<Agent> {
  return post<Agent>('/api/impl/agent', data)
}

export function updateAgent(data: Agent): Promise<Agent> {
  return put<Agent>('/api/impl/agent', data)
}

export function deleteAgent(id: number): Promise<void> {
  return del<void>(`/api/impl/agent/${id}`)
}

// ===================== Agent Score / Evaluation =====================

export interface AgentScore {
  id?: number
  agentId: number
  agentName?: string
  taskId?: number
  responseSpeedScore?: number
  constructionQualityScore?: number
  documentCompletenessScore?: number
  overallScore?: number
  comment?: string
  scorer?: string
  scoreTime?: string
}

export interface EvaluatePayload {
  agentId: number
  taskId?: number
  responseSpeedScore: number
  constructionQualityScore: number
  documentCompletenessScore: number
  comment?: string
}

export function getAgentScores(agentId: number): Promise<AgentScore[]> {
  return get<AgentScore[]>(`/api/impl/agent/${agentId}/scores`)
}

export function evaluateAgent(data: EvaluatePayload): Promise<AgentScore> {
  return post<AgentScore>('/api/impl/agent/score/evaluate', data)
}

export function getScoresByAgent(agentId: number): Promise<AgentScore[]> {
  return get<AgentScore[]>(`/api/impl/agent/score/agent/${agentId}`)
}

// ===================== Settlement =====================

export type SettlementStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'PUSHED'

export interface SettlementDetail {
  id?: number
  settlementId?: number
  itemName: string
  workQuantity: number
  unit?: string
  unitPrice: number
  amount?: number
}

export interface Settlement {
  id?: number
  settlementNo?: string
  agentId?: number
  agentName?: string
  projectId?: number
  projectName?: string
  taskId?: number
  taxRate?: number
  totalAmount?: number
  taxAmount?: number
  totalWithTax?: number
  status?: SettlementStatus
  applyTime?: string
  remark?: string
  details?: SettlementDetail[]
}

export interface SettlementPageQuery extends PageQuery {
  agentId?: number
  status?: SettlementStatus
}

export interface CreateSettlementPayload {
  settlement: Settlement
  details: SettlementDetail[]
}

export function getSettlementPage(params: SettlementPageQuery): Promise<PageResult<Settlement>> {
  return get<PageResult<Settlement>>('/api/impl/settlement/list', params)
}

export function createSettlement(data: CreateSettlementPayload): Promise<Settlement> {
  return post<Settlement>('/api/impl/settlement', data)
}

export function approveSettlement(id: number, opinion?: string): Promise<void> {
  return post<void>(`/api/impl/settlement/${id}/approve`, { opinion })
}

export function rejectSettlement(id: number, opinion?: string): Promise<void> {
  return post<void>(`/api/impl/settlement/${id}/reject`, { opinion })
}

export function getSettlementDetail(id: number): Promise<Settlement> {
  return get<Settlement>(`/api/impl/settlement/${id}`)
}
