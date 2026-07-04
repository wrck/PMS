import { get, post, put } from '@/utils/request'

/** SLA 等级 */
export type SlaLevel = 'BASIC' | 'PREMIUM' | 'PLATINUM'

export interface Warranty {
  id?: number
  assetId: number
  startDate: string
  endDate: string
  durationMonths: number
  slaLevel: SlaLevel
  contractNo?: string
  remark?: string
}

export interface WarrantyListResult {
  records: Warranty[]
  total: number
}

export function listWarranties(params: {
  page: number
  size: number
  assetId?: number
  expiringDays?: number
}): Promise<WarrantyListResult> {
  return get<WarrantyListResult>('/api/asset/warranty/list', params)
}

export function createWarranty(data: Warranty): Promise<Warranty> {
  return post<Warranty>('/api/asset/warranty', data)
}

export function updateWarranty(id: number, data: Warranty): Promise<Warranty> {
  return put<Warranty>(`/api/asset/warranty/${id}`, data)
}

export function renewWarranty(
  id: number,
  data: { durationMonths: number; endDate: string }
): Promise<Warranty> {
  return put<Warranty>(`/api/asset/warranty/${id}/renew`, data)
}

export function decommissionAsset(id: number): Promise<boolean> {
  return put<boolean>(`/api/asset/warranty/${id}/decommission`)
}

export function listExpiringSoon(days: number): Promise<Warranty[]> {
  return get<Warranty[]>('/api/asset/warranty/expiring', { days })
}
