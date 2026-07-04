import { del, get, post, put } from '@/utils/request'

/** 风险优先级 */
export type RiskPriority = 'LOW' | 'MEDIUM' | 'HIGH'

/** 风险状态 */
export type RiskStatus = 'OPEN' | 'IN_PROGRESS' | 'CLOSED' | 'ESCALATED'

/** 风险缓解策略 */
export type RiskMitigation = 'AVOID' | 'MITIGATE' | 'TRANSFER' | 'ACCEPT'

/** 风险分类 */
export type RiskCategory = 'TECHNICAL' | 'EXTERNAL' | 'ORGANIZATIONAL' | 'PM'

/** 风险实体 */
export interface Risk {
  id?: number
  riskNo?: string
  projectId: number
  description: string
  category: RiskCategory
  likelihood: number // 1-5
  impact: number // 1-5
  score?: number // likelihood * impact
  priority?: RiskPriority
  mitigation: RiskMitigation
  contingencyPlan?: string
  ownerId?: number
  ownerName?: string
  status: RiskStatus
  reviewDate?: string
  createdAt?: string
}

/** 5x5 矩阵单元格 */
export interface RiskMatrixCell {
  likelihood: number
  impact: number
  count: number
  risks: Risk[]
}

/** 风险列表查询参数 */
export interface RiskListQuery {
  page: number
  size: number
  projectId?: number
  status?: string
  priority?: string
}

/** 分页结果 */
export interface RiskListResult {
  records: Risk[]
  total: number
}

export function listRisks(params: RiskListQuery): Promise<RiskListResult> {
  return get<RiskListResult>('/api/governance/risk/list', params)
}

export function createRisk(data: Risk): Promise<Risk> {
  return post<Risk>('/api/governance/risk', data)
}

export function updateRisk(id: number, data: Risk): Promise<Risk> {
  return put<Risk>(`/api/governance/risk/${id}`, data)
}

export function markOccurred(id: number): Promise<boolean> {
  return put<boolean>(`/api/governance/risk/${id}/mark-occurred`)
}

export function getRiskMatrix(projectId?: number): Promise<RiskMatrixCell[][]> {
  return get<RiskMatrixCell[][]>('/api/governance/risk/matrix', { projectId })
}

export function deleteRisk(id: number): Promise<boolean> {
  return del<boolean>(`/api/governance/risk/${id}`)
}
