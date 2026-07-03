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
