import { del, get, post } from '@/utils/request'

export interface LowCodeTrigger {
  id?: number
  code: string
  name: string
  type: 'CRUD' | 'QUARTZ' | 'EVENT'
  config: string
  targetType: 'MICROFLOW' | 'PROCESS'
  targetCode: string
  status?: string
}

export function getTriggerList() {
  return get<LowCodeTrigger[]>('/api/lowcode/trigger')
}

export function getTrigger(id: number) {
  return get<LowCodeTrigger>(`/api/lowcode/trigger/${id}`)
}

export function saveTrigger(data: LowCodeTrigger) {
  return post<LowCodeTrigger>('/api/lowcode/trigger', data)
}

export function deleteTrigger(id: number) {
  return del(`/api/lowcode/trigger/${id}`)
}

export function executeTrigger(code: string, data: Record<string, unknown>) {
  return post(`/api/lowcode/trigger/${code}/execute`, data)
}
