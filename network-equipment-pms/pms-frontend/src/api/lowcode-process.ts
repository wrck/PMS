import { get, post } from '@/utils/request'

export interface LowCodeProcessBinding {
  id?: number
  processDefinitionKey: string
  processDefinitionName?: string
  nodeFormBindings: string
  status?: string
}

export function getProcessBindings() {
  return get<LowCodeProcessBinding[]>('/api/lowcode/process/bindings')
}

export function saveProcessBinding(data: LowCodeProcessBinding) {
  return post<LowCodeProcessBinding>('/api/lowcode/process/bindings', data)
}

export function getProcessDefinitions(page = 1, size = 20) {
  return get<Record<string, unknown>>('/api/lowcode/process/definitions', { page, size })
}

export function getTaskForm(processDefinitionKey: string, nodeId: string) {
  return get<string>('/api/lowcode/process/task-form', { processDefinitionKey, nodeId })
}
