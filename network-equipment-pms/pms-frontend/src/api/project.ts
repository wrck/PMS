import { del, get, post, put } from '@/utils/request'
import type { PageQuery, PageResult } from './system'

// ===================== Project =====================

export interface Project {
  id?: number
  code: string
  name: string
  customerId?: number
  customerName?: string
  managerId?: number
  managerName?: string
  status?: string
  startDate?: string
  endDate?: string
  amount?: number
  description?: string
  createTime?: string
}

export interface ProjectDashboard {
  total: number
  inProgress: number
  completed: number
  delayed: number
}

export function getProjectPage(params: PageQuery): Promise<PageResult<Project>> {
  return get<PageResult<Project>>('/api/projects', params)
}

export function getProjectById(id: number): Promise<Project> {
  return get<Project>(`/api/projects/${id}`)
}

export function createProject(data: Project): Promise<Project> {
  return post<Project>('/api/projects', data)
}

export function updateProject(data: Project): Promise<Project> {
  return put<Project>(`/api/projects/${data.id}`, data)
}

export function deleteProject(id: number): Promise<void> {
  return del<void>(`/api/projects/${id}`)
}

export function approveProject(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/projects/${id}/approve`, { comment })
}

export function getProjectDashboard(): Promise<ProjectDashboard> {
  return get<ProjectDashboard>('/api/projects/dashboard')
}

// ===================== Milestone =====================

export interface Milestone {
  id?: number
  projectId: number
  name: string
  plannedDate?: string
  actualDate?: string
  progress?: number
  status?: string
  remark?: string
}

export function listMilestones(projectId: number): Promise<Milestone[]> {
  return get<Milestone[]>(`/api/projects/${projectId}/milestones`)
}

export function createMilestone(data: Milestone): Promise<Milestone> {
  return post<Milestone>('/api/projects/milestones', data)
}

export function updateMilestone(data: Milestone): Promise<Milestone> {
  return put<Milestone>(`/api/projects/milestones/${data.id}`, data)
}

export function deleteMilestone(id: number): Promise<void> {
  return del<void>(`/api/projects/milestones/${id}`)
}

export function updateMilestoneProgress(id: number, progress: number): Promise<void> {
  return put<void>(`/api/projects/milestones/${id}/progress`, { progress })
}

// ===================== Final Acceptance =====================

export interface FinalAcceptance {
  id?: number
  projectId: number
  applicantId?: number
  applicantName?: string
  status?: string
  applyDate?: string
  acceptDate?: string
  comment?: string
}

export function applyFinalAcceptance(projectId: number, data?: Partial<FinalAcceptance>): Promise<FinalAcceptance> {
  return post<FinalAcceptance>(`/api/projects/${projectId}/final-acceptance/apply`, data)
}

export function approveFinalAcceptance(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/projects/final-acceptance/${id}/approve`, { comment })
}

export function rejectFinalAcceptance(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/projects/final-acceptance/${id}/reject`, { comment })
}
