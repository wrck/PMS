import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/** localStorage key for the JWT token */
export const TOKEN_KEY = 'pms_token'

/** Backend unified response envelope: { code, message, data } */
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

const service: AxiosInstance = axios.create({
  // Use the Vite dev-server proxy (/api -> http://localhost:8080)
  baseURL: '',
  timeout: 30000
})

// Request interceptor: inject the JWT token into the Authorization header
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
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

/** Typed GET helper that resolves to the unwrapped data payload */
export function get<T = unknown>(url: string, params?: object, config?: AxiosRequestConfig): Promise<T> {
  return service.get(url, { params, ...config }) as unknown as Promise<T>
}

/** Typed POST helper that resolves to the unwrapped data payload */
export function post<T = unknown>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
  return service.post(url, data, config) as unknown as Promise<T>
}

/** Typed PUT helper that resolves to the unwrapped data payload */
export function put<T = unknown>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
  return service.put(url, data, config) as unknown as Promise<T>
}

/** Typed DELETE helper that resolves to the unwrapped data payload */
export function del<T = unknown>(url: string, params?: object, config?: AxiosRequestConfig): Promise<T> {
  return service.delete(url, { params, ...config }) as unknown as Promise<T>
}

export default service
