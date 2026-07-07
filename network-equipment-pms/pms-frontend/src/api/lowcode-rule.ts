import { del, get, post } from '@/utils/request'

export interface LowCodeRule {
  id?: number
  code: string
  name: string
  description?: string
  type: 'DECISION_TABLE' | 'EXPRESSION' | 'LITEFLOW'
  definition: string
  status?: string
  version?: number
  bizType?: string
}

export function getRuleList() {
  return get<LowCodeRule[]>('/api/lowcode/rule')
}

export function getRule(id: number) {
  return get<LowCodeRule>(`/api/lowcode/rule/${id}`)
}

export function saveRule(data: LowCodeRule) {
  return post<LowCodeRule>('/api/lowcode/rule', data)
}

export function deleteRule(id: number) {
  return del(`/api/lowcode/rule/${id}`)
}

export function executeRule(code: string, facts: Record<string, unknown>) {
  return post(`/api/lowcode/rule/${code}/execute`, facts)
}
