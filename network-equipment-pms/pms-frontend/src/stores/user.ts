import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getUserInfo as getUserInfoApi,
  login as loginApi,
  logout as logoutApi,
  type LoginParams,
  type UserInfo
} from '@/api/auth'
import { TOKEN_KEY } from '@/utils/request'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // Token is hydrated from localStorage so a refresh keeps the session
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])

  async function login(params: LoginParams) {
    const res = await loginApi(params)
    token.value = res.token
    localStorage.setItem(TOKEN_KEY, res.token)
    userInfo.value = res.userInfo
    permissions.value = res.userInfo.permissions ?? []
    return res
  }

  async function fetchUserInfo() {
    const info = await getUserInfoApi()
    userInfo.value = info
    permissions.value = info.permissions ?? []
    return info
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      // Ignore logout request errors; always clear local state
    } finally {
      reset()
      router.push('/login')
    }
  }

  function reset() {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    localStorage.removeItem(TOKEN_KEY)
  }

  return { token, userInfo, permissions, login, fetchUserInfo, logout, reset }
})
