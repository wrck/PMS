import { del, get, post, put } from '@/utils/request'
import type { PageQuery, PageResult } from './system'

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
  return get<AssetCategory[]>('/api/asset/categories/tree')
}

export function createCategory(data: AssetCategory): Promise<AssetCategory> {
  return post<AssetCategory>('/api/asset/categories', data)
}

export function updateCategory(data: AssetCategory): Promise<AssetCategory> {
  return put<AssetCategory>(`/api/asset/categories/${data.id}`, data)
}

export function deleteCategory(id: number): Promise<void> {
  return del<void>(`/api/asset/categories/${id}`)
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
  unit?: string
  remark?: string
}

export function getModelPage(params: PageQuery & { categoryId?: number }): Promise<PageResult<AssetModel>> {
  return get<PageResult<AssetModel>>('/api/asset/models', params)
}

export function createModel(data: AssetModel): Promise<AssetModel> {
  return post<AssetModel>('/api/asset/models', data)
}

export function updateModel(data: AssetModel): Promise<AssetModel> {
  return put<AssetModel>(`/api/asset/models/${data.id}`, data)
}

export function deleteModel(id: number): Promise<void> {
  return del<void>(`/api/asset/models/${id}`)
}

// ===================== Asset =====================

export interface Asset {
  id?: number
  sn: string
  modelId: number
  modelName?: string
  categoryId?: number
  projectName?: string
  location?: string
  status?: string
  inboundDate?: string
  ownerId?: number
  ownerName?: string
  remark?: string
}

export interface AssetLifecycleRecord {
  id?: number
  assetId: number
  action: string
  operator?: string
  operateTime?: string
  remark?: string
}

export function getAssetPage(params: PageQuery & { status?: string; categoryId?: number }): Promise<PageResult<Asset>> {
  return get<PageResult<Asset>>('/api/assets', params)
}

export function inboundAsset(data: Asset): Promise<Asset> {
  return post<Asset>('/api/assets/inbound', data)
}

export function allocateAsset(id: number, data: { ownerId: number; projectId?: number; remark?: string }): Promise<void> {
  return post<void>(`/api/assets/${id}/allocate`, data)
}

export function returnAsset(id: number, remark?: string): Promise<void> {
  return post<void>(`/api/assets/${id}/return`, { remark })
}

export function getAssetLifecycle(id: number): Promise<AssetLifecycleRecord[]> {
  return get<AssetLifecycleRecord[]>(`/api/assets/${id}/lifecycle`)
}

// ===================== Transfer =====================

export interface AssetTransfer {
  id?: number
  assetId: number
  assetSn?: string
  fromUserId?: number
  fromUserName?: string
  toUserId: number
  toUserName?: string
  reason?: string
  status?: string
  applyDate?: string
}

export function getTransferPage(params: PageQuery): Promise<PageResult<AssetTransfer>> {
  return get<PageResult<AssetTransfer>>('/api/asset/transfers', params)
}

export function applyTransfer(data: AssetTransfer): Promise<AssetTransfer> {
  return post<AssetTransfer>('/api/asset/transfers/apply', data)
}

export function approveTransfer(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/asset/transfers/${id}/approve`, { comment })
}

export function rejectTransfer(id: number, comment?: string): Promise<void> {
  return post<void>(`/api/asset/transfers/${id}/reject`, { comment })
}
