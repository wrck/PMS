import { get, post } from '@/utils/request'

/**
 * 缓存管理 API。
 * 对应后端 CacheManagementController（/api/system/cache）。
 */

/** 获取全部缓存名称列表 */
export function getCacheNames(): Promise<string[]> {
  return get<string[]>('/api/system/cache/names')
}

/** 清除指定名称的缓存 */
export function clearCache(name: string): Promise<void> {
  return post<void>(`/api/system/cache/clear/${encodeURIComponent(name)}`)
}

/** 清除全部缓存 */
export function clearAllCache(): Promise<void> {
  return post<void>('/api/system/cache/clearAll')
}
