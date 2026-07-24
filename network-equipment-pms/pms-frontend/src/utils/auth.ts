/**
 * yudao 认证工具适配层。
 *
 * <p>提供 yudao 原生页面所需的 {@code getAccessToken / getRefreshToken /
 * setAccessToken / setRefreshToken / removeToken} 等工具。</p>
 *
 * <p>与 pms-frontend 的 {@code TOKEN_KEY='pms_token'} 对齐：
 * accessToken 复用 {@code pms_token}；refreshToken 单独存储。</p>
 */
import { TOKEN_KEY } from '@/utils/request'

const REFRESH_TOKEN_KEY = 'pms_refresh_token'
const TENANT_ID_KEY = 'pms_tenant_id'

/** 获取 accessToken（即 pms_token） */
export function getAccessToken(): string {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** 设置 accessToken */
export function setAccessToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 获取 refreshToken */
export function getRefreshToken(): string {
  return localStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

/** 设置 refreshToken */
export function setRefreshToken(token: string): void {
  localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

/** 移除所有 token */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

/** 获取租户 id（多租户场景） */
export function getTenantId(): string {
  return localStorage.getItem(TENANT_ID_KEY) || '1'
}

/** 设置租户 id */
export function setTenantId(id: string): void {
  localStorage.setItem(TENANT_ID_KEY, id)
}
