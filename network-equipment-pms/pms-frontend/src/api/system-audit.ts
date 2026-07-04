import { get } from '@/utils/request'

/**
 * 审计日志 API。
 * 对应后端 AuditLogController（/api/system/audit）。
 * 端点已通过阅读 AuditLogController.java 确认。
 */

/** MyBatis Plus IPage 序列化结构 */
export interface IPage<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages?: number
}

/** 登录日志（对齐后端 LoginLog 实体） */
export interface LoginLog {
  id?: number
  username?: string
  /** 登录时间 */
  loginTime?: string
  /** 登录 IP */
  loginIp?: string
  /** 登录地点 */
  loginLocation?: string
  /** 浏览器类型 */
  browser?: string
  /** 操作系统 */
  os?: string
  /** 登录状态：SUCCESS / FAIL */
  status?: string
  /** 提示消息 */
  message?: string
  userId?: number
}

/** 异常日志（对齐后端 ExceptionLog 实体） */
export interface ExceptionLog {
  id?: number
  userId?: number
  username?: string
  /** 请求 URI */
  requestUri?: string
  /** 请求方法 */
  requestMethod?: string
  /** 请求参数 */
  requestParams?: string
  /** 异常类型 */
  exceptionType?: string
  /** 异常消息 */
  exceptionMessage?: string
  /** 完整堆栈信息 */
  stackTrace?: string
  /** 请求 IP */
  requestIp?: string
  /** 发生时间 */
  occurTime?: string
}

/** 定时任务日志（对齐后端 ScheduleLog 实体） */
export interface AuditScheduleLog {
  id?: number
  taskName?: string
  taskGroup?: string
  cronExpression?: string
  startTime?: string
  endTime?: string
  costMs?: number
  status?: string
  errorMessage?: string
  triggerType?: string
}

/** 登录日志分页查询参数 */
export interface LoginLogQuery {
  page?: number
  size?: number
  username?: string
  status?: string
}

/** 异常日志分页查询参数 */
export interface ExceptionLogQuery {
  page?: number
  size?: number
  username?: string
  requestUri?: string
}

/** 调度日志分页查询参数 */
export interface AuditScheduleLogQuery {
  page?: number
  size?: number
  taskName?: string
  status?: string
}

/** 分页查询登录日志 */
export function getLoginLogPage(params: LoginLogQuery): Promise<IPage<LoginLog>> {
  return get<IPage<LoginLog>>('/api/system/audit/login/page', params)
}

/** 分页查询异常日志 */
export function getExceptionLogPage(params: ExceptionLogQuery): Promise<IPage<ExceptionLog>> {
  return get<IPage<ExceptionLog>>('/api/system/audit/exception/page', params)
}

/** 分页查询调度日志 */
export function getAuditScheduleLogPage(
  params: AuditScheduleLogQuery
): Promise<IPage<AuditScheduleLog>> {
  return get<IPage<AuditScheduleLog>>('/api/system/audit/schedule/page', params)
}

/** 查询失败的调度日志列表 */
export function getAuditScheduleFailed(
  params: { page?: number; size?: number } = {}
): Promise<IPage<AuditScheduleLog>> {
  return get<IPage<AuditScheduleLog>>('/api/system/audit/schedule/failed', params)
}
