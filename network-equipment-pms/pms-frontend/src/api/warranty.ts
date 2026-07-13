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

export function updateWarranty(data: Warranty): Promise<Warranty> {
  return put<Warranty>('/api/asset/warranty', data)
}

/** 续保 — 通过通用更新接口修改截止日期和时长 */
export function renewWarranty(
  id: number,
  data: { durationMonths: number; endDate: string }
): Promise<Warranty> {
  return put<Warranty>('/api/asset/warranty', { id, durationMonths: data.durationMonths, endDate: data.endDate } as Warranty)
}

/** 退网 — 通过通用更新接口将截止日期设为今天 */
export function decommissionAsset(id: number): Promise<boolean> {
  const today = new Date().toISOString().slice(0, 10)
  return put<boolean>('/api/asset/warranty', { id, endDate: today } as unknown as Warranty).then(() => true)
}

export function listExpiringSoon(days: number): Promise<Warranty[]> {
  return get<Warranty[]>('/api/asset/warranty/expiring-soon', { days })
}
