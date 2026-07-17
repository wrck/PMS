import { get, post } from '@/utils/request'

/**
 * 计划基线 API。对应后端 {@code BaselineController}，
 * 挂载在 {@code /api/baseline} 下。
 *
 * <p>关联设计文档 §5.5 依赖与基线 API（Story 4）：
 * 列表/偏差分析为只读，保存基线需 {@code project:baseline:save} 权限，
 * 申请变更需 {@code project:baseline:change} 权限。</p>
 */

/** 基线状态：DRAFT / APPROVED / SUPERSEDED */
export type BaselineStatus = 'DRAFT' | 'APPROVED' | 'SUPERSEDED'

/** 任务计划快照（基线快照内的单任务条目，日期为 ISO yyyy-MM-dd 字符串） */
export interface TaskPlanSnapshot {
  taskId: number
  taskName: string
  /** 计划开始日期 ISO yyyy-MM-dd */
  plannedStart?: string
  /** 计划结束日期 ISO yyyy-MM-dd */
  plannedEnd?: string
  /** 工期天数 */
  duration?: number
  /** 计划工时 */
  plannedHours?: number
  /** 任务类型 */
  taskType?: string
}

/** 计划基线快照 */
export interface BaselineSnapshot {
  id?: number
  projectId: number
  baselineName: string
  status?: BaselineStatus
  /** 全部任务计划快照（JSON 列，新建时由后端填充） */
  snapshotJson?: TaskPlanSnapshot[] | null
  /** 变更原因（关联审批） */
  changeReason?: string
  /** 关联审批记录ID */
  approvalRecordId?: number
  /** 审批时间 */
  approvedAt?: string
  /** 审批人ID */
  approvedBy?: number
  /** 乐观锁版本号 */
  version?: number
  createTime?: string
  updateTime?: string
}

/** 基线摘要信息（偏差分析响应中的 baseline 字段） */
export interface BaselineInfo {
  id: number
  baselineName: string
  status: BaselineStatus
  approvedAt?: string
}

/** 单任务偏差对比（current - baseline，单位：天） */
export interface TaskDiff {
  taskId: number
  taskName: string
  baselineStart?: string
  currentStart?: string
  /** 开始偏差天数（current - baseline） */
  startVariance?: number
  baselineEnd?: string
  currentEnd?: string
  /** 结束偏差天数（current - baseline） */
  endVariance?: number
  /** 偏差百分比（|endVariance| / baselineDuration * 100） */
  percentVariance?: number
}

/**
 * 基线偏差分析结果。
 *
 * <p>关联设计文档 §5.5 Story 4 验收 2。{@code needsApproval=true} 表示偏差
 * 超过双阈值（天数 OR 百分比 OR 偏差任务数），需触发 BASELINE_CHANGE 审批。</p>
 */
export interface BaselineDiffResult {
  baseline: BaselineInfo
  diffs: TaskDiff[]
  /** 偏差任务总数（开始或结束偏差非 0 的任务数） */
  totalVarianced: number
  /** 是否需要审批 */
  needsApproval: boolean
  /** 审批原因（偏差超阈值时填充） */
  approvalReason?: string
}

/** 查询项目基线列表（按创建时间倒序） */
export function listBaselines(projectId: number): Promise<BaselineSnapshot[]> {
  return get<BaselineSnapshot[]>('/api/baseline/list', { projectId })
}

/** 保存基线（快照项目全部任务，单一活跃基线规则） */
export function saveBaseline(
  projectId: number,
  baselineName?: string
): Promise<BaselineSnapshot> {
  return post<BaselineSnapshot>('/api/baseline/save', undefined, {
    params: { projectId, baselineName }
  })
}

/**
 * 申请基线变更（双阈值 OR 触发审批）。
 *
 * <p>返回偏差分析结果：{@code needsApproval=true} 表示已触发审批
 * （Phase 7 实现具体审批流程，基线保持 DRAFT）；{@code needsApproval=false}
 * 表示偏差未超阈值，基线已直接转为 APPROVED。</p>
 */
export function requestBaselineChange(
  baselineId: number,
  changeReason?: string
): Promise<BaselineDiffResult> {
  return post<BaselineDiffResult>(
    `/api/baseline/${baselineId}/request-change`,
    undefined,
    { params: { changeReason } }
  )
}

/** 基线偏差分析（逐任务对比当前计划与基线快照） */
export function diffBaseline(baselineId: number): Promise<BaselineDiffResult> {
  return get<BaselineDiffResult>('/api/baseline/diff', { baselineId })
}
