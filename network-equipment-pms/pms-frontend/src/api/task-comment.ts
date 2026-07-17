import { del, get, post } from '@/utils/request'

/**
 * 任务评论 API。对应后端 {@code TaskCommentController}，
 * 挂载在 {@code /api/impl/task/comment} 下，支持二级回复。
 */

/** 任务评论（支持 parentCommentId 二级回复） */
export interface TaskCommentItem {
  id?: number
  taskId: number
  userId?: number
  userName?: string
  content: string
  /** 父评论ID（NULL=顶级评论，非 NULL=二级回复） */
  parentCommentId?: number | null
  version?: number
  createTime?: string
  updateTime?: string
}

/** 查询任务评论列表 */
export function listComments(taskId: number): Promise<TaskCommentItem[]> {
  return get<TaskCommentItem[]>(`/api/impl/task/comment/${taskId}`)
}

/** 新增评论 */
export function createComment(data: TaskCommentItem): Promise<TaskCommentItem> {
  return post<TaskCommentItem>('/api/impl/task/comment', data)
}

/** 删除评论 */
export function deleteComment(id: number): Promise<void> {
  return del<void>(`/api/impl/task/comment/${id}`)
}
