import { del, get, post, put } from '@/utils/request'
import type { PageQuery, PageResult } from './system'

// ===================== Implementation Task =====================

export interface ImplTask {
  id?: number
  projectId: number
  projectName?: string
  name: string
  agentId?: number
  agentName?: string
  assigneeId?: number
  assigneeName?: string
  status?: string
  plannedStart?: string
  plannedEnd?: string
  actualStart?: string
  actualEnd?: string
  progress?: number
  remark?: string
}

export function getTaskPage(params: PageQuery & { projectId?: number; status?: string }): Promise<PageResult<ImplTask>> {
  return get<PageResult<ImplTask>>('/api/implementation/tasks', params)
}

export function assignTask(id: number, data: { agentId: number; assigneeId?: number }): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/assign`, data)
}

export function acceptTask(id: number): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/accept`)
}

export function startTask(id: number): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/start`)
}

export function reportTaskProgress(id: number, progress: number, remark?: string): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/progress`, { progress, remark })
}

export function completeTask(id: number, remark?: string): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/complete`, { remark })
}

export function confirmTask(id: number, remark?: string): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/confirm`, { remark })
}

export function rejectTask(id: number, remark?: string): Promise<void> {
  return post<void>(`/api/implementation/tasks/${id}/reject`, { remark })
}

// ===================== Agent =====================

export interface Agent {
  id?: number
  name: string
  company?: string
  contact?: string
  phone?: string
  status?: number
  score?: number
  remark?: string
}

export interface AgentScore {
  id?: number
  agentId: number
  agentName?: string
  taskId?: number
  score: number
  comment?: string
  scorer?: string
  scoreTime?: string
}

export function getAgentPage(params: PageQuery): Promise<PageResult<Agent>> {
  return get<PageResult<Agent>>('/api/implementation/agents', params)
}

export function createAgent(data: Agent): Promise<Agent> {
  return post<Agent>('/api/implementation/agents', data)
}

export function updateAgent(data: Agent): Promise<Agent> {
  return put<Agent>(`/api/implementation/agents/${data.id}`, data)
}

export function deleteAgent(id: number): Promise<void> {
  return del<void>(`/api/implementation/agents/${id}`)
}

export function getAgentScores(agentId: number): Promise<AgentScore[]> {
  return get<AgentScore[]>(`/api/implementation/agents/${agentId}/scores`)
}

// ===================== Settlement =====================

export interface Settlement {
  id?: number
  projectId: number
  projectName?: string
  agentId?: number
  agentName?: string
  amount?: number
  status?: string
  period?: string
  remark?: string
  createTime?: string
}

export function getSettlementPage(params: PageQuery & { projectId?: number; status?: string }): Promise<PageResult<Settlement>> {
  return get<PageResult<Settlement>>('/api/implementation/settlements', params)
}

export function createSettlement(data: Settlement): Promise<Settlement> {
  return post<Settlement>('/api/implementation/settlements', data)
}

export function approveSettlement(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/implementation/settlements/${id}/approve`, { comment })
}

export function rejectSettlement(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/implementation/settlements/${id}/reject`, { comment })
}
