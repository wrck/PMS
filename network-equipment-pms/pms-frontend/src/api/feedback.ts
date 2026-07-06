import { get, post, put } from '@/utils/request'

/** 反馈分类 */
export type FeedbackCategory = 'BUG' | 'SUGGESTION' | 'QUESTION' | 'OTHER'

/** 反馈状态 */
export type FeedbackStatus = 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'CLOSED'

/** 反馈实体（与后端 com.dp.plat.system.entity.Feedback 对应） */
export interface Feedback {
  id?: number
  userId?: number
  username?: string
  category: FeedbackCategory
  title: string
  content: string
  /** 联系方式（电话/邮箱，选填） */
  contact?: string
  status?: FeedbackStatus
  reply?: string
  replyBy?: string
  replyAt?: string
  createTime?: string
  updateTime?: string
}

/** 反馈回复请求体 */
export interface FeedbackReplyRequest {
  reply: string
}

/** 按状态统计的反馈数量 */
export type FeedbackStatusStats = Record<FeedbackStatus, number>

/** 提交新反馈（任意已登录用户） */
export function createFeedback(data: Feedback): Promise<boolean> {
  return post<boolean>('/api/system/feedback', data)
}

/** 获取反馈详情（创建者本人或管理员） */
export function getFeedback(id: number): Promise<Feedback> {
  return get<Feedback>(`/api/system/feedback/${id}`)
}

/** 列出当前用户提交的反馈 */
export function listMyFeedbacks(): Promise<Feedback[]> {
  return get<Feedback[]>('/api/system/feedback/my')
}

/** 列出所有反馈（管理员） */
export function listAllFeedbacks(
  status?: FeedbackStatus,
  category?: FeedbackCategory
): Promise<Feedback[]> {
  return get<Feedback[]>('/api/system/feedback/list', {
    ...(status ? { status } : {}),
    ...(category ? { category } : {})
  })
}

/** 回复反馈（管理员） */
export function replyFeedback(id: number, data: FeedbackReplyRequest): Promise<boolean> {
  return put<boolean>(`/api/system/feedback/${id}/reply`, data)
}

/** 关闭反馈（管理员） */
export function closeFeedback(id: number): Promise<boolean> {
  return put<boolean>(`/api/system/feedback/${id}/close`)
}

/** 按状态统计当前用户的反馈数量 */
export function getFeedbackStatusStats(): Promise<FeedbackStatusStats> {
  return get<FeedbackStatusStats>('/api/system/feedback/stats')
}
