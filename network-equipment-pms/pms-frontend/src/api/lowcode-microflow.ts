import { del, get, post } from '@/utils/request'

export interface LowCodeMicroflow {
  id?: number
  code: string
  name: string
  description?: string
  definition?: string
  status?: string
  version?: number
  bizType?: string
}

export function getMicroflowList() {
  return get<LowCodeMicroflow[]>('/api/lowcode/microflow')
}

export function getMicroflow(id: number) {
  return get<LowCodeMicroflow>(`/api/lowcode/microflow/${id}`)
}

export function saveMicroflow(data: LowCodeMicroflow) {
  return post<LowCodeMicroflow>('/api/lowcode/microflow', data)
}

export function deleteMicroflow(id: number) {
  return del(`/api/lowcode/microflow/${id}`)
}

export function executeMicroflow(code: string, inputs: Record<string, unknown>) {
  return post(`/api/lowcode/microflow/${code}/execute`, inputs)
}
