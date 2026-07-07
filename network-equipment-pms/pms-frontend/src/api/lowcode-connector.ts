import { del, get, post } from '@/utils/request'

export interface LowCodeConnector {
  id?: number
  code: string
  name: string
  description?: string
  type: 'REST' | 'DB'
  config: string
  status?: string
  bizType?: string
}

export function getConnectorList() {
  return get<LowCodeConnector[]>('/api/lowcode/connector')
}

export function getConnector(id: number) {
  return get<LowCodeConnector>(`/api/lowcode/connector/${id}`)
}

export function saveConnector(data: LowCodeConnector) {
  return post<LowCodeConnector>('/api/lowcode/connector', data)
}

export function deleteConnector(id: number) {
  return del(`/api/lowcode/connector/${id}`)
}

export function testConnector(code: string) {
  return post<any>(`/api/lowcode/connector/${code}/test`)
}

export function executeConnector(code: string, params: Record<string, any>) {
  return post<any>(`/api/lowcode/connector/${code}/execute`, params)
}
