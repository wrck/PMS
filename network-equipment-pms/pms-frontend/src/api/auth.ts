import { get, post } from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  email?: string
  phone?: string
  deptId?: number
  deptName?: string
  roles?: string[]
  permissions?: string[]
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

/** Login with username & password */
export function login(data: LoginParams): Promise<LoginResult> {
  return post<LoginResult>('/api/auth/login', data)
}

/** Logout the current user */
export function logout(): Promise<void> {
  return post<void>('/api/auth/logout')
}

/** Get the current user's info */
export function getUserInfo(): Promise<UserInfo> {
  return get<UserInfo>('/api/auth/info')
}
