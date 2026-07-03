import { del, get, post, put } from '@/utils/request'

// ===================== Category =====================

export interface AssetCategory {
  id?: number
  parentId: number
  code: string
  name: string
  sort?: number
  status?: number
  children?: AssetCategory[]
}

export function getCategoryTree(): Promise<AssetCategory[]> {
  return get<AssetCategory[]>('/api/asset/category/tree')
}

export function createCategory(data: AssetCategory): Promise<AssetCategory> {
  return post<AssetCategory>('/api/asset/category', data)
}

export function updateCategory(data: AssetCategory): Promise<AssetCategory> {
  return put<AssetCategory>('/api/asset/category', data)
}

export function deleteCategory(id: number): Promise<void> {
  return del<void>(`/api/asset/category/${id}`)
}

// ===================== Model =====================

export interface AssetModel {
  id?: number
  categoryId: number
  categoryName?: string
  code: string
  name: string
  brand?: string
  spec?: string
  standardPrice?: number
  unit?: string
  status?: number
  remark?: string
}

export interface ModelPageQuery {
  categoryId?: number
  name?: string
  page?: number
  size?: number
}

export interface ModelPageResult {
  records: AssetModel[]
  total: number
  page: number
  size: number
}

export function listModels(params: ModelPageQuery): Promise<ModelPageResult> {
  return get<ModelPageResult>('/api/asset/model/list', params)
}

export function createModel(data: AssetModel): Promise<AssetModel> {
  return post<AssetModel>('/api/asset/model', data)
}

export function updateModel(data: AssetModel): Promise<AssetModel> {
  return put<AssetModel>('/api/asset/model', data)
}

export function deleteModel(id: number): Promise<void> {
  return del<void>(`/api/asset/model/${id}`)
}

// ===================== Asset =====================

/** Asset status enumeration values */
export const ASSET_STATUS = {
  IN_STOCK: 'IN_STOCK',
  ALLOCATED: 'ALLOCATED',
  IN_TRANSIT: 'IN_TRANSIT',
  SCRAPPED: 'SCRAPPED'
} as const

export interface Asset {
  id?: number
  serialNo: string
  name?: string
  modelId?: number
  modelName?: string
  categoryId?: number
  categoryName?: string
  status?: string
  warehouse?: string
  location?: string
  projectId?: number
  projectName?: string
  inboundDate?: string
  ownerId?: number
  ownerName?: string
  remark?: string
}

export interface AssetListQuery {
  page: number
  size: number
  serialNo?: string
  status?: string
  projectId?: number
}

export interface AssetPageResult {
  records: Asset[]
  total: number
  page: number
  size: number
}

export function inboundAsset(data: Asset): Promise<Asset> {
  return post<Asset>('/api/asset/inbound', data)
}

export function allocateAsset(id: number, data: { projectId: number }): Promise<void> {
  return post<void>(`/api/asset/${id}/allocate`, data)
}

export function returnAsset(id: number): Promise<void> {
  return post<void>(`/api/asset/${id}/return`)
}

export function getAsset(id: number): Promise<Asset> {
  return get<Asset>(`/api/asset/${id}`)
}

export function listAssets(params: AssetListQuery): Promise<AssetPageResult> {
  return get<AssetPageResult>('/api/asset/list', params)
}

export function updateAsset(data: Asset): Promise<Asset> {
  return put<Asset>('/api/asset', data)
}

export function deleteAsset(id: number): Promise<void> {
  return del<void>(`/api/asset/${id}`)
}

export interface AssetLifecycleRecord {
  id?: number
  assetId: number
  action: string // INBOUND | ALLOCATE | TRANSFER | RETURN | SCRAP
  operator?: string
  operateTime?: string
  fromProjectId?: number
  fromProjectName?: string
  toProjectId?: number
  toProjectName?: string
  remark?: string
}

export function getAssetLifecycle(id: number): Promise<AssetLifecycleRecord[]> {
  return get<AssetLifecycleRecord[]>(`/api/asset/${id}/lifecycle`)
}

export function returnAssetsByProject(projectId: number): Promise<void> {
  return post<void>(`/api/asset/return-by-project/${projectId}`)
}

// ===================== Transfer =====================

export const TRANSFER_STATUS = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED'
} as const

export interface AssetTransfer {
  id?: number
  assetId: number
  assetSerialNo?: string
  fromProjectId?: number
  fromProjectName?: string
  toProjectId?: number
  toProjectName?: string
  transferReason?: string
  status?: string
  applicantId?: number
  applicantName?: string
  applyTime?: string
  opinion?: string
}

export interface TransferApplyPayload {
  assetId: number
  fromProjectId: number
  toProjectId: number
  transferReason: string
}

export interface TransferListQuery {
  page: number
  size: number
  status?: string
}

export interface TransferPageResult {
  records: AssetTransfer[]
  total: number
  page: number
  size: number
}

export function applyTransfer(data: TransferApplyPayload): Promise<AssetTransfer> {
  return post<AssetTransfer>('/api/asset/transfer/apply', data)
}

export function approveTransfer(id: number, data: { opinion: string }): Promise<void> {
  return post<void>(`/api/asset/transfer/${id}/approve`, data)
}

export function rejectTransfer(id: number, data: { opinion: string }): Promise<void> {
  return post<void>(`/api/asset/transfer/${id}/reject`, data)
}

export function listTransfers(params: TransferListQuery): Promise<TransferPageResult> {
  return get<TransferPageResult>('/api/asset/transfer/list', params)
}
