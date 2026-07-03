import { get, post } from '@/utils/request'
import type { PageQuery, PageResult } from './system'

// ===================== Process Definition =====================

export interface ProcessDefinition {
  id: string
  key: string
  name: string
  version: number
  deploymentId?: string
  category?: string
  suspended?: boolean
  deployTime?: string
}

export function getProcessDefinitions(params: PageQuery): Promise<PageResult<ProcessDefinition>> {
  return get<PageResult<ProcessDefinition>>('/api/workflow/definitions', params)
}

export function deployProcess(data: { name: string; file: string }): Promise<ProcessDefinition> {
  return post<ProcessDefinition>('/api/workflow/deploy', data)
}

// ===================== Process Instance & Task =====================

export interface ProcessInstance {
  id: string
  processDefinitionKey: string
  processDefinitionName?: string
  startUserId?: string
  startTime?: string
  businessKey?: string
  status?: string
}

export interface WorkflowTask {
  id: string
  name: string
  taskDefinitionKey?: string
  assignee?: string
  processInstanceId?: string
  processDefinitionName?: string
  businessKey?: string
  createTime?: string
  variables?: Record<string, unknown>
}

export function startProcess(data: { processDefinitionKey: string; businessKey?: string; variables?: Record<string, unknown> }): Promise<ProcessInstance> {
  return post<ProcessInstance>('/api/workflow/process/start', data)
}

export function completeTask(id: string, data: { approved?: boolean; comment?: string; variables?: Record<string, unknown> }): Promise<void> {
  return post<void>(`/api/workflow/tasks/${id}/complete`, data)
}

export function getTodoTasks(params: PageQuery): Promise<PageResult<WorkflowTask>> {
  return get<PageResult<WorkflowTask>>('/api/workflow/tasks/todo', params)
}

export function getDoneTasks(params: PageQuery): Promise<PageResult<WorkflowTask>> {
  return get<PageResult<WorkflowTask>>('/api/workflow/tasks/done', params)
}

export interface ProcessHistory {
  id: string
  activityName: string
  assignee?: string
  startTime?: string
  endTime?: string
  duration?: string
  comment?: string
}

export function getProcessHistory(processInstanceId: string): Promise<ProcessHistory[]> {
  return get<ProcessHistory[]>(`/api/workflow/process/${processInstanceId}/history`)
}

export function getProcessDiagram(processInstanceId: string): Promise<string> {
  return get<string>(`/api/workflow/process/${processInstanceId}/diagram`)
}
