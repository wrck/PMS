import { get, post } from '@/utils/request'
import type { ChangeRequestPriority } from './change-request'

/** 问题状态 */
export type IssueStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'

/** 问题实体 */
export interface Issue {
  id?: number
  issueNo?: string
  projectId: number
  description: string
  raisedBy?: number
  raisedByName?: string
  assigneeId?: number
  assigneeName?: string
  priority?: ChangeRequestPriority // LOW/MEDIUM/HIGH/CRITICAL
  targetResolveDate?: string
  status: IssueStatus
  sourceRiskId?: number
  sourceRiskNo?: string
  sourceChangeId?: number
  sourceChangeNo?: string
  createdAt?: string
  resolvedAt?: string
}

/** 问题列表查询参数 */
export interface IssueListQuery {
  page: number
  size: number
  projectId?: number
  status?: string
  assigneeId?: number
}

/** 分页结果 */
export interface IssueListResult {
  records: Issue[]
  total: number
}

export function listIssues(params: IssueListQuery): Promise<IssueListResult> {
  return get<IssueListResult>('/api/governance/issue', params)
}

export function createIssue(data: Issue): Promise<Issue> {
  return post<Issue>('/api/governance/issue', data)
}

export function assignIssue(id: number, assigneeId: number): Promise<Issue> {
  return post<Issue>(`/api/governance/issue/${id}/assign`, { assigneeId })
}

export function resolveIssue(id: number): Promise<Issue> {
  return post<Issue>(`/api/governance/issue/${id}/resolve`)
}

export function closeIssue(id: number): Promise<Issue> {
  return post<Issue>(`/api/governance/issue/${id}/close`)
}

export function escalateIssue(id: number, reason: string): Promise<boolean> {
  return post<boolean>(`/api/governance/issue/${id}/escalate`, { reason })
}
