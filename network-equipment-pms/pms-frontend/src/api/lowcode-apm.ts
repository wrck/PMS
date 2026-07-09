import { get } from '@/utils/request'
import type { MicroflowExecutionLog } from '@/api/lowcode-microflow'
import type { LowCodeTriggerExecutionLog } from '@/api/lowcode-trigger'

/**
 * 低代码 APM 可视化看板 API（批次5-T9 前端）。
 *
 * <p>后端 {@code LowCodeApmService} 通过 Micrometer 将微流/规则/连接器/触发器/
 * Flowable 回调指标写入 Prometheus，但未提供独立的 APM 查询 REST API。
 * 本看板采用「真实日志 + 兜底」策略：复用已存在的执行日志接口拼接近实时统计，
 * 接口不可用或无数据时由前端做兜底渲染（显示 0 或「暂无数据」）。</p>
 *
 * <p>借鉴 Joget APM 全链路指标看板。</p>
 */

/**
 * 查询近 N 小时的微流执行日志（用于 APM 统计）。
 *
 * <p>对应后端 {@code /api/lowcode/microflow-execution-log/recent}。后端该端点
 * 历史上要求 microflowId 参数；本看板按 APM 全局视角传 hours，若后端未支持
 * 全局查询则由调用方做兜底处理。</p>
 *
 * @param hours 时间窗口（小时）
 */
export function getMicroflowExecutionStats(
  hours: number
): Promise<MicroflowExecutionLog[]> {
  return get<MicroflowExecutionLog[]>(
    `/api/lowcode/microflow-execution-log/recent?hours=${hours}`
  )
}

/**
 * 查询近 N 小时的触发器执行日志（用于 APM 统计）。
 *
 * @param hours 时间窗口（小时）
 */
export function getTriggerExecutionStats(
  hours: number
): Promise<LowCodeTriggerExecutionLog[]> {
  return get<LowCodeTriggerExecutionLog[]>(
    `/api/lowcode/trigger/execution-logs/recent?hours=${hours}`
  )
}
