import { get, post } from '@/utils/request'

export interface LowCodeProcessBinding {
  id?: number
  processDefinitionKey: string
  processDefinitionName?: string
  /** 节点表单绑定 JSON 字符串：[{ nodeId, formCode, microflowCode }] */
  nodeFormBindings: string
  /** BPMN 2.0 XML 内容（流程图本体） */
  bpmnXml?: string
  status?: string
}

export function getProcessBindings() {
  return get<LowCodeProcessBinding[]>('/api/lowcode/process/bindings')
}

export function saveProcessBinding(data: LowCodeProcessBinding) {
  return post<LowCodeProcessBinding>('/api/lowcode/process/bindings', data)
}

export function getProcessDefinitions(page = 1, size = 20) {
  return get<Record<string, unknown>>('/api/lowcode/process/definitions', { page, size })
}

export function getTaskForm(processDefinitionKey: string, nodeId: string) {
  return get<string>('/api/lowcode/process/task-form', { processDefinitionKey, nodeId })
}

/**
 * 查询流程实例当前活动节点 ID 列表（用于流程预览模式高亮）。
 *
 * <p>返回当前未完成的活动节点 taskDefinitionKey 数组。
 * 若后端尚未实现，前端会以空数组降级处理。</p>
 */
export function getProcessInstanceActivityIds(processInstanceId: string) {
  return get<string[]>('/api/lowcode/process/instance/activity-ids', { processInstanceId })
}

/**
 * 部署 BPMN XML 到 Flowable 流程引擎。
 *
 * <p>将 BPMN XML 以 multipart 形式上传到工作流部署接口。</p>
 */
export function deployBpmnXml(bpmnXml: string, processName: string) {
  const formData = new FormData()
  const blob = new Blob([bpmnXml], { type: 'application/xml' })
  formData.append('file', blob, `${processName || 'process'}.bpmn20.xml`)
  return post<Record<string, unknown>>('/api/workflow/deploy', formData)
}
