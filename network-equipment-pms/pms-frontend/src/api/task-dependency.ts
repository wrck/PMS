import { del, get, post } from '@/utils/request'

/**
 * 任务依赖 API。对应后端 {@code TaskDependencyController}，
 * 挂载在 {@code /api/implementation/task/dependency} 下。
 *
 * <p>保存依赖时后端执行 DFS 循环检测：检测到闭环时返回
 * HTTP 200 + {@code data.success=false}（{@link DependencyCycleResult}），
 * 响应拦截器视为成功并 resolve 该对象，调用方需判断 {@code success} 字段
 * 以区分「保存成功」与「检测到循环依赖」。</p>
 */

/** 任务依赖类型：FS（完成-开始）/ FF（完成-完成）/ SS（开始-开始）/ SF（开始-完成） */
export type DependencyType = 'FS' | 'FF' | 'SS' | 'SF'

/** 任务依赖 */
export interface TaskDependency {
  id?: number
  projectId: number
  predecessorTaskId: number
  successorTaskId: number
  /** FS / FF / SS / SF，默认 FS */
  dependencyType?: DependencyType
  /** 滞后天数（可负，表示提前），默认 0 */
  lagDays?: number
  /** 乐观锁版本号 */
  version?: number
  createTime?: string
  updateTime?: string
}

/** 循环依赖检测结果的闭环路径节点 */
export interface CycleNode {
  taskId: number
  taskName: string | null
}

/**
 * 循环依赖检测结果（HTTP 200 + success=false 的结构化数据载荷）。
 *
 * <p>关联设计文档 §5.5 Story 4 验收 1。{@code cyclePath} 首尾为同一任务
 * （闭合路径），如 [A, B, C, A]。</p>
 */
export interface DependencyCycleResult {
  success: false
  errorCode: 'CYCLE_DETECTED'
  errorMessage: string
  cyclePath: CycleNode[]
}

/** 查询项目下全部任务依赖 */
export function listDependencies(projectId: number): Promise<TaskDependency[]> {
  return get<TaskDependency[]>('/api/implementation/task/dependency', { projectId })
}

/**
 * 保存任务依赖（含 DFS 循环检测）。
 *
 * <p>检测到循环依赖时，Promise 会 <b>resolve</b>（非 reject）一个
 * {@link DependencyCycleResult}（{@code success=false}），调用方需判断
 * 返回值的 {@code success} 字段以区分两种业务结果。</p>
 */
export function saveDependency(
  data: TaskDependency
): Promise<TaskDependency | DependencyCycleResult> {
  return post<TaskDependency | DependencyCycleResult>(
    '/api/implementation/task/dependency',
    data
  )
}

/** 删除任务依赖 */
export function deleteDependency(id: number): Promise<void> {
  return del<void>(`/api/implementation/task/dependency/${id}`)
}
