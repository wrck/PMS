import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/** localStorage key for the JWT token */
export const TOKEN_KEY = 'pms_token'

/** 幂等性请求头名称：与后端 IdempotentAspect 约定一致 */
export const IDEMPOTENT_KEY_HEADER = 'X-Idempotent-Key'

/** 需要注入幂等键的 HTTP 方法（写操作） */
const IDEMPOTENT_METHODS = ['post', 'put', 'delete', 'patch']

/**
 * Backend unified response envelope: { code, message, data }.
 *
 * <p>与 {@link '@/types'.Result} 的差异：后端历史接口未稳定输出 `success`
 * 与 `timestamp` 字段，因此内部拦截器仅依赖 `code / message / data`。
 * `success` 与 `timestamp` 设为可选以便与公共 Result 类型兼容。</p>
 */
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
  success?: boolean
  timestamp?: number
}

/**
 * 生成 UUID v4 字符串，用于幂等键。
 *
 * 优先使用浏览器原生 {@link crypto.randomUUID}（HTTPS/localhost 安全上下文），
 * 降级到手动 RFC 4122 v4 实现（适用于 jsdom 等不支持 randomUUID 的环境）。
 */
function generateIdempotentKey(): string {
  // 浏览器原生 API（需安全上下文：HTTPS 或 localhost）
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  // 降级：手动生成 RFC 4122 v4 UUID
  if (typeof crypto !== 'undefined' && typeof crypto.getRandomValues === 'function') {
    const bytes = new Uint8Array(16)
    crypto.getRandomValues(bytes)
    // 设置 version (4) 和 variant (10xx) 位
    bytes[6] = (bytes[6] & 0x0f) | 0x40
    bytes[8] = (bytes[8] & 0x3f) | 0x80
    const hex = Array.from(bytes, (b) => b.toString(16).padStart(2, '0'))
    return `${hex.slice(0, 4).join('')}-${hex.slice(4, 6).join('')}-${hex.slice(6, 8).join('')}-${hex.slice(8, 10).join('')}-${hex.slice(10, 16).join('')}`
  }
  // 最终降级：基于 Date + Math.random（仅用于极端环境，碰撞概率极低）
  return `id-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`
}

const service: AxiosInstance = axios.create({
  // Use the Vite dev-server proxy (/api -> http://localhost:8080)
  baseURL: '',
  timeout: 30000
})

// Request interceptor: inject the JWT token + idempotent key for write operations
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 写操作（POST/PUT/DELETE/PATCH）注入 X-Idempotent-Key 头
    // 配合后端 @Idempotent 注解实现接口幂等性，防止用户重复点击导致重复提交
    const method = (config.method || '').toLowerCase()
    if (IDEMPOTENT_METHODS.includes(method) && !config.headers[IDEMPOTENT_KEY_HEADER]) {
      config.headers[IDEMPOTENT_KEY_HEADER] = generateIdempotentKey()
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor: unwrap the unified envelope and handle business errors
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data as ApiResult
    // Success: return the data payload directly (cast because axios types
    // expect an AxiosResponse, but the helpers below unwrap it to <T>)
    if (res.code === 200) {
      return res.data as unknown as AxiosResponse
    }
    // Unauthorized: clear token and redirect to login
    if (res.code === 401) {
      ElMessage.error('登录状态已过期，请重新登录')
      localStorage.removeItem(TOKEN_KEY)
      router.push('/login')
      return Promise.reject(new Error(res.message || '未授权'))
    }
    // Other business errors
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // Network / HTTP errors
    const msg = error?.response?.data?.message || error.message || '网络异常，请稍后重试'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

/**
 * Typed GET helper that resolves to the unwrapped data payload.
 *
 * <p>响应拦截器已剥离外层 {@link ApiResult} 信封，故此处的 {@code Promise<T>}
 * 即对应后端 {@code Result.data}，而非完整 envelope。若需 envelope，
 * 请直接调用 {@link service}（默认 axios 实例）。</p>
 *
 * <p>params 使用 {@code object} 而非 {@code Record<string, unknown>}：
 * TypeScript 接口（如 AssetListQuery）没有隐式索引签名，无法赋值给
 * {@code Record<string, unknown>}，故放宽为 {@code object} 以兼容所有查询 DTO。</p>
 */
export function get<T = unknown>(
  url: string,
  params?: object,
  config?: AxiosRequestConfig
): Promise<T> {
  return service.get(url, { params, ...config }) as unknown as Promise<T>
}

/** Typed POST helper that resolves to the unwrapped data payload */
export function post<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig
): Promise<T> {
  return service.post(url, data, config) as unknown as Promise<T>
}

/** Typed PUT helper that resolves to the unwrapped data payload */
export function put<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig
): Promise<T> {
  return service.put(url, data, config) as unknown as Promise<T>
}

/** Typed DELETE helper that resolves to the unwrapped data payload */
export function del<T = unknown>(
  url: string,
  params?: object,
  config?: AxiosRequestConfig
): Promise<T> {
  return service.delete(url, { params, ...config }) as unknown as Promise<T>
}

export default service
