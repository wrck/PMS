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
 * <p>将 bpmn-js 导出的 BPMN 2.0 XML 以 JSON 形式提交到低代码部署接口，
 * 后端转换为 MultipartFile 调用 WorkflowService.deployProcess。</p>
 */
export function deployBpmnXml(bpmnXml: string, processName: string) {
  return post<Record<string, unknown>>('/api/lowcode/process/deploy', {
    xml: bpmnXml,
    name: processName
  })
}

/**
 * 获取已部署流程定义的最新版本 BPMN 2.0 XML 原文。
 *
 * <p>用于流程预览（bpmn-js Viewer）与节点表单绑定时解析 UserTask 节点列表。</p>
 */
export function getProcessDefinitionBpmnXml(processDefinitionKey: string) {
  return get<string>('/api/lowcode/process/bpmn-xml', { processDefinitionKey })
}

/**
 * 获取流程实例的流程图（PNG 图片二进制）。
 *
 * <p>复用 pms-workflow 的 DefaultProcessDiagramGenerator，返回 image/png 字节流。
 * 调用方可将其转为 blob URL 在 <img> 中展示。</p>
 */
export function getProcessDiagram(processInstanceId: string) {
  return get<Blob>(`/api/workflow/diagram/${processInstanceId}`, undefined, {
    responseType: 'blob'
  })
}
