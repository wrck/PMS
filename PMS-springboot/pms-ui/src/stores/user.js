import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('pms_token') || '')
  const userInfo = ref(null)

  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.token
    localStorage.setItem('pms_token', res.data.token)
    return res
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    return res.data
  }

  async function logout() {
    try { await logoutApi() } catch (e) { /* ignore */ }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('pms_token')
  }

  return { token, userInfo, login, fetchUserInfo, logout }
})
