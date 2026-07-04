import { get, post, put } from '@/utils/request'

/** RMA 工单状态 */
export type RmaTicketStatus =
  | 'REGISTERED'
  | 'WARRANTY_CHECKED'
  | 'RMA_ISSUED'
  | 'RETURNING'
  | 'INSPECTED'
  | 'CLOSED'

/** 保修状态 */
export type WarrantyStatus = 'IN_WARRANTY' | 'OUT_OF_WARRANTY'

export interface Rma {
  id?: number
  rmaNo?: string
  assetId: number
  sn?: string
  faultDescription: string
  ticketStatus: RmaTicketStatus
  warrantyStatus?: WarrantyStatus
  createdAt?: string
  closedAt?: string
  inspectionResult?: string
  remark?: string
}

export interface RmaKpi {
  mttrHours: number
  firstPassRate: number
  total: number
  closed: number
}

export interface RmaListResult {
  records: Rma[]
  total: number
}

export function listRmas(params: {
  page: number
  size: number
  ticketStatus?: string
  assetId?: number
}): Promise<RmaListResult> {
  return get<RmaListResult>('/api/asset/rma/list', params)
}

export function createRma(data: Rma): Promise<Rma> {
  return post<Rma>('/api/asset/rma', data)
}

export function checkWarranty(id: number): Promise<Rma> {
  return put<Rma>(`/api/asset/rma/${id}/warranty-check`)
}

export function issueRma(id: number): Promise<Rma> {
  return put<Rma>(`/api/asset/rma/${id}/issue`)
}

export function markReturning(id: number): Promise<Rma> {
  return put<Rma>(`/api/asset/rma/${id}/returning`)
}

export function inspectRma(
  id: number,
  data: { inspectionResult: string; updateAsset: boolean }
): Promise<Rma> {
  return put<Rma>(`/api/asset/rma/${id}/inspect`, data)
}

export function closeRma(id: number): Promise<Rma> {
  return put<Rma>(`/api/asset/rma/${id}/close`)
}

export function getRmaKpi(): Promise<RmaKpi> {
  return get<RmaKpi>('/api/asset/rma/kpi')
}
