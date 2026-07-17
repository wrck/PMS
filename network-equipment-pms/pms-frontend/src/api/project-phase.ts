import request from '@/utils/request'

export interface ProjectPhase {
  id?: number
  projectId: number
  templatePhaseId?: number
  phaseName: string
  phaseCode: string
  sortOrder: number
  entryCriteria?: any
  exitCriteria?: any
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'SKIPPED'
  plannedStartDate?: string
  plannedEndDate?: string
  actualStartDate?: string
  actualEndDate?: string
}

export function listPhasesByProjectId(projectId: number) {
  return request.get<ProjectPhase[]>(`/api/project/phase/project/${projectId}`)
}

export function getPhase(id: number) {
  return request.get<ProjectPhase>(`/api/project/phase/${id}`)
}

export function createPhase(data: ProjectPhase) {
  return request.post<ProjectPhase>('/api/project/phase', data)
}

export function updatePhase(data: ProjectPhase) {
  return request.put<ProjectPhase>('/api/project/phase', data)
}

export function deletePhase(id: number) {
  return request.del<void>(`/api/project/phase/${id}`)
}

// ===================== 阶段推进（Phase 3 Story 2） =====================

/** 阶段退出条件违规项（对齐后端 PhaseExitGateViolation） */
export interface PhaseExitGateViolation {
  gateType: string
  message: string
  businessId?: number
  businessName?: string
  expectedStatus?: string
  actualStatus?: string
}

/**
 * 阶段退出条件校验结果。
 *
 * 推进失败时后端 ProjectExceptionHandler 返回 code=200 + 此结构
 * （success=false + violations），响应拦截器按 code=200 解包为 data，
 * 故前端 advancePhase 会以 resolved（非 rejected）形式收到本对象。
 */
export interface PhaseExitGateResult {
  success: boolean
  errorCode?: string
  errorMessage?: string
  violations?: PhaseExitGateViolation[]
}

/**
 * 推进阶段 — POST /api/project/phase/{phaseId}/advance
 *
 * 成功返回 ProjectPhase（无 success 字段）；失败返回 PhaseExitGateResult
 * （success=false）。调用方通过 `result.success === false` 判定失败。
 */
export function advancePhase(
  phaseId: number
): Promise<ProjectPhase | PhaseExitGateResult> {
  return request.post(`/api/project/phase/${phaseId}/advance`) as unknown as Promise<
    ProjectPhase | PhaseExitGateResult
  >
}
