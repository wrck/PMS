import { get, post } from '@/utils/request'

/** 集成健康单项 */
export interface IntegrationHealthItem {
  system: string // D365 / FP / OA
  status: string // UP / DOWN / DEGRADED
  tokenValid: boolean
  lastPushTime?: string
  lastPushStatus?: string // SUCCESS / FAIL
  totalPushes: number
  failedPushes: number
  successRate: number
  message?: string
}

/** 集成健康总览响应 */
export interface IntegrationHealthResponse {
  items: IntegrationHealthItem[]
  overallStatus: string // HEALTHY / DEGRADED / DOWN
  timestamp: string
}

/** 集成推送日志 */
export interface IntegrationLog {
  id: number
  logType: string // D365 / FP / OA
  businessType: string
  businessId?: number
  requestUrl: string
  responseStatus: string
  errorMessage?: string
  retryCount: number
  createTime: string
}

/** 集成日志列表查询参数 */
export interface IntegrationLogQuery {
  page: number
  size: number
  logType?: string
  responseStatus?: string
}

/** 分页结果 */
export interface IntegrationLogResult {
  records: IntegrationLog[]
  total: number
}

export function getIntegrationHealth(): Promise<IntegrationHealthResponse> {
  return get<IntegrationHealthResponse>('/api/integration/health')
}

export function listIntegrationLogs(
  params: IntegrationLogQuery
): Promise<IntegrationLogResult> {
  return get<IntegrationLogResult>('/api/integration/log/list', params)
}

export function retryPush(logId: number): Promise<boolean> {
  return post<boolean>(`/api/integration/log/${logId}/retry`)
}
