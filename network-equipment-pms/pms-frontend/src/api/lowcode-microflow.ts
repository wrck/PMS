import { del, get, post } from '@/utils/request'

export interface LowCodeMicroflow {
  id?: number
  code: string
  name: string
  description?: string
  definition?: string
  status?: string
  version?: number
  bizType?: string
}

/** 微流节点类型（与后端 MicroflowNodeType 枚举对齐） */
export type MicroflowNodeType =
  | 'START'
  | 'END'
  | 'ASSIGN'
  | 'CONDITION'
  | 'LOOP'
  | 'CALL_SERVICE'
  | 'CALL_MICROFLOW'
  | 'CALL_RULE'
  | 'CALL_CONNECTOR'
  | 'THROW_EXCEPTION'
  | 'RETURN'

/** 微流节点定义（DAG 画布节点数据模型） */
export interface MicroflowNode {
  id: string
  type: MicroflowNodeType
  label: string
  x: number
  y: number
  config: Record<string, unknown>
}

/** 微流连线定义（DAG 画布边数据模型） */
export interface MicroflowEdge {
  id: string
  source: string
  target: string
  sourcePort: string
  targetPort: string
}

/** 微流变量定义 */
export interface MicroflowVariable {
  name: string
  type: string
}

/** 微流变量集合 */
export interface MicroflowVariables {
  inputs: MicroflowVariable[]
  locals: MicroflowVariable[]
  returnType: string
}

/** 微流 definition JSON 顶层结构 */
export interface MicroflowDefinition {
  nodes: MicroflowNode[]
  edges: MicroflowEdge[]
  variables: MicroflowVariables
}

/** 微流执行日志单条记录（节点级轨迹） */
export interface MicroflowExecutionLog {
  id?: number
  microflowId?: number
  microflowCode?: string
  executionId: string
  nodeId: string
  nodeType: string
  startTime: string
  endTime?: string
  durationMs?: number
  inputs?: string
  outputs?: string
  variablesSnapshot?: string
  status: 'RUNNING' | 'SUCCESS' | 'FAILED'
  errorMessage?: string
}

export function getMicroflowList() {
  return get<LowCodeMicroflow[]>('/api/lowcode/microflow')
}

export function getMicroflow(id: number) {
  return get<LowCodeMicroflow>(`/api/lowcode/microflow/${id}`)
}

export function saveMicroflow(data: LowCodeMicroflow) {
  return post<LowCodeMicroflow>('/api/lowcode/microflow', data)
}

export function deleteMicroflow(id: number) {
  return del(`/api/lowcode/microflow/${id}`)
}

export function executeMicroflow(code: string, inputs: Record<string, unknown>) {
  return post(`/api/lowcode/microflow/${code}/execute`, inputs)
}

/** 查询某次执行的节点级轨迹日志 */
export function getExecutionLogs(executionId: string) {
  return get<MicroflowExecutionLog[]>(`/api/lowcode/microflow-execution-log/${executionId}`)
}

/** 查询某微流最近若干次执行日志（按时间倒序） */
export function getRecentExecutionLogs(microflowId: number, limit = 10) {
  return get<MicroflowExecutionLog[]>(
    `/api/lowcode/microflow-execution-log/recent?microflowId=${microflowId}&limit=${limit}`
  )
}
