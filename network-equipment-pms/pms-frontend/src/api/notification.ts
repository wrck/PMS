import { get, put } from '@/utils/request'

/** 通知分类 */
export type NotificationCategory =
  | 'MILESTONE'
  | 'TASK'
  | 'APPROVAL'
  | 'PUNCH_LIST'
  | 'WARRANTY'
  | 'RMA'
  | 'SETTLEMENT'

export type NotificationReadStatus = 'UNREAD' | 'READ'

export interface Notification {
  id: number
  userId: number
  title: string
  content: string
  category: NotificationCategory
  bizType?: string
  bizId?: number
  readStatus: NotificationReadStatus
  channel: string
  createdAt: string
}

export interface NotificationListResult {
  records: Notification[]
  total: number
}

export function listNotifications(params: {
  page: number
  size: number
  category?: string
  readStatus?: string
}): Promise<NotificationListResult> {
  return get<NotificationListResult>('/api/notification/page', params)
}

export function getUnreadCount(): Promise<number> {
  return get<number>('/api/notification/unread/count')
}

export function markAsRead(id: number): Promise<boolean> {
  return put<boolean>(`/api/notification/${id}/read`)
}

export function markAllAsRead(): Promise<boolean> {
  return put<boolean>('/api/notification/read/all')
}
