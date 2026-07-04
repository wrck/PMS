import { get } from '@/utils/request'

/**
 * 定时任务监控 API。
 * 对应后端 ScheduleMonitorController（/api/system/schedule）。
 */

/** 定时任务执行状态 */
export const SCHEDULE_STATUS = {
  SUCCESS: 'SUCCESS',
  FAIL: 'FAIL'
} as const

/** 触发类型 */
export const TRIGGER_TYPE = {
  AUTO: 'AUTO',
  MANUAL: 'MANUAL'
} as const

/** 定时任务日志实体（对齐后端 ScheduleLog） */
export interface ScheduleLog {
  id?: number
  /** 任务名称 */
  taskName?: string
  /** 任务分组 */
  taskGroup?: string
  /** Cron 表达式 */
  cronExpression?: string
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
  /** 耗时（毫秒） */
  costMs?: number
  /** 执行状态：SUCCESS / FAIL */
  status?: string
  /** 错误信息 */
  errorMessage?: string
  /** 触发类型：AUTO / MANUAL */
  triggerType?: string
}

/** MyBatis Plus IPage 序列化结构 */
export interface IPage<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages?: number
}

/** 顶部统计卡片数据 */
export interface ScheduleStatistic {
  /** 总任务数 */
  totalTasks?: number
  /** 最近 24h 成功数 */
  success24h?: number
  /** 最近 24h 失败数 */
  failed24h?: number
}

/** 最近日志列表查询参数 */
export interface ScheduleLogQuery {
  page?: number
  size?: number
  /** SUCCESS / FAIL */
  status?: string
  /** 触发类型过滤：MANUAL_TRIGGER 选项映射为 triggerType=MANUAL */
  triggerType?: string
  /** 起始日期（YYYY-MM-DD） */
  startDate?: string
  /** 结束日期（YYYY-MM-DD） */
  endDate?: string
}

/** 获取顶部统计卡片数据 */
export function getScheduleStatistic(): Promise<ScheduleStatistic> {
  return get<ScheduleStatistic>('/api/system/schedule/statistic')
}

/** 分页查询最近调度日志 */
export function listScheduleLogs(params: ScheduleLogQuery): Promise<IPage<ScheduleLog>> {
  return get<IPage<ScheduleLog>>('/api/system/schedule/page', params)
}

/** 查询失败任务列表 */
export function listFailedScheduleLogs(
  params: { page?: number; size?: number } = {}
): Promise<IPage<ScheduleLog>> {
  return get<IPage<ScheduleLog>>('/api/system/schedule/failed', params)
}
