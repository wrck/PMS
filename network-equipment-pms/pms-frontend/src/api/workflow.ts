import { del, get, post } from '@/utils/request'
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
  return get<PageResult<ProcessDefinition>>('/api/workflow/definition/list', params)
}

/** Deploy a BPMN process file (multipart/form-data) */
export function deployProcess(file: File): Promise<ProcessDefinition> {
  const formData = new FormData()
  formData.append('file', file)
  return post<ProcessDefinition>('/api/workflow/deploy', formData)
}

export function deleteDeployment(deploymentId: string): Promise<void> {
  return del<void>(`/api/workflow/deployment/${deploymentId}`)
}

// ===================== Process Instance =====================

export interface ProcessInstance {
  id: string
  processDefinitionKey: string
  processDefinitionName?: string
  startUserId?: string
  startUserName?: string
  startTime?: string
  endTime?: string
  duration?: string
  businessKey?: string
  status?: string
}

export interface StartProcessPayload {
  processDefinitionKey: string
  businessKey?: string
  variables?: Record<string, unknown>
}

export function startProcess(data: StartProcessPayload): Promise<ProcessInstance> {
  return post<ProcessInstance>('/api/workflow/start', data)
}

export function getProcessInstance(processInstanceId: string): Promise<ProcessInstance> {
  return get<ProcessInstance>(`/api/workflow/instance/${processInstanceId}`)
}

/** Build the direct URL for a process diagram image (used in <img :src>) */
export function getProcessDiagramUrl(processInstanceId: string): string {
  return `/api/workflow/diagram/${processInstanceId}`
}

// ===================== Workflow Task =====================

export interface WorkflowTask {
  id: string
  name: string
  taskDefinitionKey?: string
  assignee?: string
  assigneeName?: string
  processInstanceId?: string
  processDefinitionId?: string
  processDefinitionName?: string
  businessKey?: string
  startUserId?: string
  startUserName?: string
  createTime?: string
  endTime?: string
  duration?: string
  variables?: Record<string, unknown>
}

export function getTodoTasks(params: PageQuery): Promise<PageResult<WorkflowTask>> {
  return get<PageResult<WorkflowTask>>('/api/workflow/task/todo', params)
}

export function getDoneTasks(params: PageQuery): Promise<PageResult<WorkflowTask>> {
  return get<PageResult<WorkflowTask>>('/api/workflow/task/done', params)
}

export interface CompleteTaskPayload {
  taskId: string
  variables?: Record<string, unknown>
  comment?: string
}

export function completeTask(data: CompleteTaskPayload): Promise<void> {
  return post<void>('/api/workflow/task/complete', data)
}

export function withdrawTask(data: { processInstanceId: string; currentTaskId: string }): Promise<void> {
  return post<void>('/api/workflow/task/withdraw', data)
}

export function transferTask(data: { taskId: string; targetUserId: number | string }): Promise<void> {
  return post<void>('/api/workflow/task/transfer', data)
}

// ===================== Process History =====================

export interface ProcessHistory {
  id: string
  activityName: string
  activityType?: string
  assignee?: string
  assigneeName?: string
  startTime?: string
  endTime?: string
  duration?: string
  comment?: string
}

export function getProcessHistory(processInstanceId: string): Promise<ProcessHistory[]> {
  return get<ProcessHistory[]>(`/api/workflow/history/${processInstanceId}`)
}
