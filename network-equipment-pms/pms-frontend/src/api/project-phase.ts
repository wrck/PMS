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
