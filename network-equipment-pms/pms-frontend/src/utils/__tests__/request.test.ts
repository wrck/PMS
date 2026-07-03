import { beforeEach, describe, expect, it, vi } from 'vitest'
import type { InternalAxiosRequestConfig } from 'axios'

// Mock ElMessage so no DOM message rendering happens in jsdom.
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  }
}))

// Mock the router — request.ts only uses router.push for 401 redirects.
const mocks = vi.hoisted(() => ({ routerPush: vi.fn() }))
vi.mock('@/router', () => ({
  default: { push: mocks.routerPush }
}))

import service, { get, TOKEN_KEY } from '@/utils/request'
import { ElMessage } from 'element-plus'
import router from '@/router'

/** Build a mock axios adapter that resolves with the given envelope payload. */
function makeAdapter(payload: any) {
  return (config: InternalAxiosRequestConfig) =>
    Promise.resolve({
      data: payload,
      status: 200,
      statusText: 'OK',
      headers: config.headers,
      config
    })
}

/** Build a mock axios adapter that rejects with the given error. */
function makeErrorAdapter(err: any) {
  return () => Promise.reject(err)
}

describe('request utility', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  describe('request interceptor', () => {
    it('adds Authorization header when a token exists in localStorage', async () => {
      localStorage.setItem(TOKEN_KEY, 'my-token')
      let captured!: InternalAxiosRequestConfig
      const adapter = (config: InternalAxiosRequestConfig) => {
        captured = config
        return Promise.resolve({
          data: { code: 200, message: 'ok', data: {} },
          status: 200,
          statusText: 'OK',
          headers: config.headers,
          config
        })
      }

      await get('/api/test', undefined, { adapter: adapter as any })

      expect(captured.headers.Authorization).toBe('Bearer my-token')
    })

    it('does not add Authorization header when there is no token', async () => {
      let captured!: InternalAxiosRequestConfig
      const adapter = (config: InternalAxiosRequestConfig) => {
        captured = config
        return Promise.resolve({
          data: { code: 200, message: 'ok', data: {} },
          status: 200,
          statusText: 'OK',
          headers: config.headers,
          config
        })
      }

      await get('/api/test', undefined, { adapter: adapter as any })

      expect(captured.headers.Authorization).toBeUndefined()
    })
  })

  describe('response interceptor', () => {
    it('extracts the data payload when code === 200', async () => {
      const payload = { code: 200, message: 'ok', data: { foo: 'bar' } }

      const result = await get<{ foo: string }>('/api/test', undefined, {
        adapter: makeAdapter(payload) as any
      })

      expect(result).toEqual({ foo: 'bar' })
    })

    it('shows an error and rejects when code !== 200', async () => {
      const payload = { code: 500, message: '操作失败', data: null }

      await expect(
        get('/api/test', undefined, { adapter: makeAdapter(payload) as any })
      ).rejects.toThrow('操作失败')

      expect(ElMessage.error).toHaveBeenCalledWith('操作失败')
    })

    it('falls back to a default message when the envelope message is empty', async () => {
      const payload = { code: 500, message: '', data: null }

      await expect(
        get('/api/test', undefined, { adapter: makeAdapter(payload) as any })
      ).rejects.toThrow('请求失败')

      expect(ElMessage.error).toHaveBeenCalledWith('请求失败')
    })

    it('handles 401 by clearing the token and redirecting to /login', async () => {
      localStorage.setItem(TOKEN_KEY, 'expired-token')
      const payload = { code: 401, message: '未授权', data: null }

      await expect(
        get('/api/test', undefined, { adapter: makeAdapter(payload) as any })
      ).rejects.toThrow('未授权')

      expect(ElMessage.error).toHaveBeenCalledWith('登录状态已过期，请重新登录')
      expect(localStorage.getItem(TOKEN_KEY)).toBeNull()
      expect(router.push).toHaveBeenCalledWith('/login')
    })

    it('handles network/HTTP errors by showing the message and rejecting', async () => {
      await expect(
        get('/api/test', undefined, {
          adapter: makeErrorAdapter(new Error('Network Error')) as any
        })
      ).rejects.toThrow('Network Error')

      expect(ElMessage.error).toHaveBeenCalledWith('Network Error')
    })

    it('uses a default message for network errors without one', async () => {
      await expect(
        get('/api/test', undefined, {
          adapter: makeErrorAdapter({}) as any
        })
      ).rejects.toBeDefined()

      expect(ElMessage.error).toHaveBeenCalledWith('网络异常，请稍后重试')
    })
  })

  describe('default export', () => {
    it('is the configured axios instance', () => {
      expect(service).toBeDefined()
      expect(typeof service.get).toBe('function')
      expect(typeof service.post).toBe('function')
    })
  })
})
