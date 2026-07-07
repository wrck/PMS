import { del, get, post } from '@/utils/request'

export interface LowCodeComment {
  id?: number
  configType: string
  configId: number
  userId: number
  userName?: string
  content: string
  mentions?: string
  parentId?: number
  createTime?: string
}

export function getComments(configType: string, configId: number) {
  return get<LowCodeComment[]>('/api/lowcode/comment', { configType, configId })
}

export function addComment(data: LowCodeComment) {
  return post<LowCodeComment>('/api/lowcode/comment', data)
}

export function deleteComment(id: number) {
  return del(`/api/lowcode/comment/${id}`)
}
