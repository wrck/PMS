import { get } from '@/utils/request'

/**
 * 任务活动记录 API（只读）。对应后端 {@code TaskActivityController}，
 * 挂载在 {@code /api/implementation/task/activity} 下。
 *
 * <p>活动由各业务操作（创建/更新/状态变更/提交评审/审批/驳回/勾选/评论/
 * 进度变更/分配/移动）内部追加记录，前端只负责展示。</p>
 */

/** 任务活动记录 */
export interface TaskActivityItem {
  id?: number
  taskId: number
  userId?: number
  userName?: string
  /** 活动类型：CREATE/UPDATE/STATUS_CHANGE/SUBMIT_REVIEW/APPROVE/REJECT/... */
  activityType: string
  content?: string
  /** 附加元数据（JSON 字符串，如 old_value/new_value） */
  metadata?: string | null
  version?: number
  createTime?: string
  updateTime?: string
}

/** 查询任务活动记录（按时间倒序） */
export function listActivities(taskId: number): Promise<TaskActivityItem[]> {
  return get<TaskActivityItem[]>(`/api/implementation/task/activity/${taskId}`)
}
