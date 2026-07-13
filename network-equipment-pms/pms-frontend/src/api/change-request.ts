import { get, post } from '@/utils/request'

/** 变更请求状态 */
export type ChangeRequestStatus =
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'CCB_APPROVED'
  | 'CCB_REJECTED'
  | 'IMPLEMENTING'
  | 'CLOSED'

/** 变更请求优先级 */
export type ChangeRequestPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

/** 变更请求实体 */
export interface ChangeRequest {
  id?: number
  crNo?: string
  projectId: number
  title: string
  description?: string
  requesterId?: number
  requesterName?: string
  requestDate?: string
  impactScope?: string
  impactSchedule?: string
  impactCost?: string
  impactQuality?: string
  priority: ChangeRequestPriority
  status: ChangeRequestStatus
  approverId?: number
  approverName?: string
  approveTime?: string
  approveOpinion?: string
  baselineUpdated?: boolean
  createdAt?: string
}

/** 基线变更历史 */
export interface BaselineHistory {
  id: number
  changeRequestId: number
  baselineType: string // SCHEDULE / COST / SCOPE
  oldValue: string
  newValue: string
  changeDescription: string
  changedAt: string
  changedBy: string
}

/** 变更请求列表查询参数 */
export interface ChangeRequestListQuery {
  page: number
  size: number
  projectId?: number
  status?: string
}

/** 分页结果 */
export interface ChangeRequestListResult {
  records: ChangeRequest[]
  total: number
}

export function listChangeRequests(
  params: ChangeRequestListQuery
): Promise<ChangeRequestListResult> {
  return get<ChangeRequestListResult>('/api/governance/change-request', params)
}

export function createChangeRequest(data: ChangeRequest): Promise<ChangeRequest> {
  return post<ChangeRequest>('/api/governance/change-request', data)
}

export function approveChangeRequest(id: number, opinion: string): Promise<ChangeRequest> {
  return post<ChangeRequest>(`/api/governance/change-request/${id}/approve`, { opinion })
}

export function rejectChangeRequest(id: number, opinion: string): Promise<ChangeRequest> {
  return post<ChangeRequest>(`/api/governance/change-request/${id}/reject`, { opinion })
}

// TODO: 后端 ChangeRequestController 暂无 baseline-history 端点，以下调用将返回 404，
// 待后端补充该端点后再启用。函数保留以避免调用方（views/change-request/index.vue）编译失败。
export function listBaselineHistory(changeRequestId: number): Promise<BaselineHistory[]> {
  return get<BaselineHistory[]>(`/api/governance/change-request/${changeRequestId}/baseline-history`)
}
