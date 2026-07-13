import { del, get, post, put } from '@/utils/request'

/** 缺陷严重等级 */
export type PunchListSeverity = 'SAFETY' | 'FUNCTIONAL' | 'COSMETIC'

/** 缺陷状态 */
export type PunchListStatus = 'OPEN' | 'RESOLVED' | 'VERIFIED'

/** 走场阶段 */
export type WalkdownStage = 'PRE_PUNCH' | 'FORMAL'

export interface PunchList {
  id?: number
  projectId: number
  milestoneId?: number
  severity: PunchListSeverity
  title: string
  description?: string
  walkdownStage: WalkdownStage
  assigneeId?: number
  assigneeName?: string
  deadline?: string
  status: PunchListStatus
  resolvedAt?: string
  verifiedAt?: string
  attachmentIds?: number[]
  createdAt?: string
  createdBy?: number
}

export interface PunchListListResult {
  records: PunchList[]
  total: number
  page: number
  size: number
}

export function listPunchLists(params: {
  page: number
  size: number
  projectId?: number
  severity?: string
  status?: string
}): Promise<PunchListListResult> {
  return get<PunchListListResult>('/api/project/punch-list/list', params)
}

export function createPunchList(data: PunchList): Promise<PunchList> {
  return post<PunchList>('/api/project/punch-list', data)
}

export function updatePunchList(data: PunchList): Promise<PunchList> {
  return put<PunchList>('/api/project/punch-list', data)
}

export function resolvePunchList(id: number): Promise<boolean> {
  return post<boolean>(`/api/project/punch-list/${id}/resolve`)
}

export function verifyPunchList(id: number): Promise<boolean> {
  return post<boolean>(`/api/project/punch-list/${id}/verify`)
}

export function deletePunchList(id: number): Promise<boolean> {
  return del<boolean>(`/api/project/punch-list/${id}`)
}
