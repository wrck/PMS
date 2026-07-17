import { del, get, post, put } from '@/utils/request'

/**
 * 任务检查项 API。对应后端 {@code TaskChecklistController}，
 * 挂载在 {@code /api/impl/task/checklist} 下。
 *
 * <p>强制检查项（{@code mandatory=true}）在提交评审前必须勾选，
 * 否则后端会抛出 {@code TaskChecklistRequiredException}，
 * 由前端依据响应中的 {@code uncheckedMandatoryItems} 列表提示用户。</p>
 */

/** 任务检查项 */
export interface TaskChecklistItem {
  id?: number
  taskId: number
  /** 检查项标题（最长 128 字符） */
  title: string
  /** 检查项描述（最长 500 字符，可选） */
  description?: string
  /** 是否强制检查项（提交评审前必须勾选） */
  mandatory?: boolean
  /** 是否已勾选 */
  checked?: boolean
  /** 勾选人ID */
  checkedBy?: number
  /** 勾选时间 */
  checkedAt?: string
  /** 排序序号 */
  sortOrder?: number
  /** 乐观锁版本号 */
  version?: number
  createTime?: string
  updateTime?: string
}

/** 查询任务检查项列表 */
export function listChecklist(taskId: number): Promise<TaskChecklistItem[]> {
  return get<TaskChecklistItem[]>(`/api/impl/task/checklist/${taskId}`)
}

/** 新增检查项 */
export function createChecklist(data: TaskChecklistItem): Promise<TaskChecklistItem> {
  return post<TaskChecklistItem>('/api/impl/task/checklist', data)
}

/** 更新检查项 */
export function updateChecklist(data: TaskChecklistItem): Promise<TaskChecklistItem> {
  return put<TaskChecklistItem>('/api/impl/task/checklist', data)
}

/** 勾选/取消勾选检查项 */
export function toggleCheck(id: number, checked: boolean): Promise<TaskChecklistItem> {
  return post<TaskChecklistItem>(`/api/impl/task/checklist/${id}/check`, undefined, {
    params: { checked }
  })
}

/** 删除检查项 */
export function deleteChecklist(id: number): Promise<void> {
  return del<void>(`/api/impl/task/checklist/${id}`)
}
