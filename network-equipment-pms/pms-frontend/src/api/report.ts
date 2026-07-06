import { get } from '@/utils/request'

// ===================== Delivery Stats =====================

export interface MonthlyStat {
  month: string
  initiated: number
  completed: number
}

export interface DeliveryStats {
  monthlyStats: MonthlyStat[]
  totalInitiated: number
  totalInProgress: number
  totalCompleted: number
  avgCycleDays: number
  delayRate: number
}

export interface DeliveryStatsQuery {
  startDate?: string
  endDate?: string
}

/** 项目交付统计 */
export function getDeliveryStats(params?: DeliveryStatsQuery): Promise<DeliveryStats> {
  return get<DeliveryStats>('/api/report/delivery', params)
}

// ===================== Asset Stats =====================

export interface AssetStats {
  byStatus: Record<string, number>
  byCategory: Record<string, number>
  totalValue: number
  total: number
  inStock: number
  allocated: number
  inTransfer: number
  scrapped: number
}

/** 设备资产统计 */
export function getAssetStats(): Promise<AssetStats> {
  return get<AssetStats>('/api/report/asset')
}

// ===================== Implementation Stats =====================

export interface MonthlyEfficiency {
  month: string
  completedCount: number
  avgDurationDays: number
}

export interface TypeStat {
  total: number
  completed: number
  completionRate: number
  avgDurationDays: number
}

export interface AgentRankingRow {
  rank: number
  agentId?: number
  agentName: string
  overallScore: number
  taskCount: number
  responseSpeedScore: number
  constructionQualityScore: number
  documentCompletenessScore: number
}

export interface ImplementationStats {
  efficiency: MonthlyEfficiency[]
  efficiencyByType: {
    OEM: TypeStat
    AGENT: TypeStat
  }
  agentRanking: AgentRankingRow[]
}

/** 实施效能统计 */
export function getImplementationStats(): Promise<ImplementationStats> {
  return get<ImplementationStats>('/api/report/implementation')
}

// ===================== Dashboard Stats (Task 31) =====================

export interface DashboardStats {
  /** 项目总数 */
  projectTotal: number
  /** 在库设备数 */
  assetInStock: number
  /** 待办任务数 */
  todoCount: number
  /** 本月交付项目数 */
  monthDelivery: number
  /** 进行中项目数 */
  projectInProgress: number
  /** 本月新增项目 */
  monthNewProject: number
  /** 本月新增资产 */
  monthNewAsset: number
  /** 告警数（逾期任务 + 30 天内到期质保） */
  alertCount: number
}

export interface ProjectTrendItem {
  /** 月份 yyyy-MM */
  month: string
  /** 项目状态 */
  status: string
  /** 数量 */
  count: number
}

export interface TodoItem {
  id: number
  title: string
  /** 类型 TASK/APPROVAL/PUNCH_LIST/WARRANTY */
  type: string
  /** 优先级 HIGH/NORMAL/LOW */
  priority: string
  assigneeName?: string
  deadline?: string
  projectCode?: string
  projectName?: string
  status: string
}

export interface ActivityItem {
  id: number
  /** 类型 LOGIN/OPER/SCHEDULE/INTEGRATION */
  type: string
  description: string
  operatorName?: string
  /** 创建时间 yyyy-MM-dd HH:mm:ss */
  createdAt: string
  bizType?: string
  bizId?: number
}

/** 仪表盘统计（项目/在库设备/待办/本月交付等） */
export function getDashboardStats(): Promise<DashboardStats> {
  return get<DashboardStats>('/api/report/dashboard/stats')
}

/** 项目趋势（最近 6 月状态分布） */
export function getProjectTrend(): Promise<ProjectTrendItem[]> {
  return get<ProjectTrendItem[]>('/api/report/project/trend')
}

/** 待办列表（Top N，默认 5） */
export function getTodoList(limit = 5): Promise<TodoItem[]> {
  return get<TodoItem[]>('/api/report/todo/list', { limit })
}

/** 近期动态（最近 N 条日志，默认 10） */
export function getRecentActivities(limit = 10): Promise<ActivityItem[]> {
  return get<ActivityItem[]>('/api/report/recent-activities', { limit })
}
