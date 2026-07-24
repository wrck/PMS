import { del, get, post, put } from '@/utils/request'
import type { YudaoPageParam, YudaoPageResult } from './yudao-system'

/**
 * yudao 底座工作流模块原生 API。
 *
 * <p>直接调用 yudao {@code /admin-api/bpm/*} 接口，
 * 分页使用 yudao 约定的 {@code pageNo/pageSize} + {@code list/total} 结构。</p>
 */

// ===================== 流程分类 =====================

export interface CategoryRespVO {
  id: number
  name: string
  code: string
  status: number
  sort: number
}

export interface CategorySaveReqVO {
  id?: number
  name: string
  code: string
  status: number
  sort: number
}

export interface CategoryPageReqVO extends YudaoPageParam {
  name?: string
  code?: string
  status?: number
}

export interface CategorySimpleRespVO {
  id: number
  name: string
  code: string
}

export function getCategoryPage(
  params: CategoryPageReqVO
): Promise<YudaoPageResult<CategoryRespVO>> {
  return get<YudaoPageResult<CategoryRespVO>>('/admin-api/bpm/category/page', params)
}

export function getCategorySimpleList(): Promise<CategorySimpleRespVO[]> {
  return get<CategorySimpleRespVO[]>('/admin-api/bpm/category/simple-list')
}

export function getCategory(id: number): Promise<CategoryRespVO> {
  return get<CategoryRespVO>('/admin-api/bpm/category/get', { id })
}

export function createCategory(data: CategorySaveReqVO): Promise<number> {
  return post<number>('/admin-api/bpm/category/create', data)
}

export function updateCategory(data: CategorySaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/category/update', data)
}

// 批量修改流程分类的排序
export function updateCategorySortBatch(ids: number[]): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/category/update-sort-batch', undefined, {
    params: { ids: ids.join(',') }
  })
}

export function deleteCategory(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/category/delete', { id })
}

// ===================== 动态表单 =====================

export interface FormRespVO {
  id: number
  name: string
  conf: string
  fields: string[]
  status: number
  remark?: string
  createTime: string
}

export interface FormSaveReqVO {
  id?: number
  name: string
  conf: string
  fields: string[]
  status: number
  remark?: string
}

export interface FormPageReqVO extends YudaoPageParam {
  name?: string
  status?: number
}

export interface FormSimpleRespVO {
  id: number
  name: string
}

export function getFormPage(params: FormPageReqVO): Promise<YudaoPageResult<FormRespVO>> {
  return get<YudaoPageResult<FormRespVO>>('/admin-api/bpm/form/page', params)
}

export function getForm(id: number): Promise<FormRespVO> {
  return get<FormRespVO>('/admin-api/bpm/form/get', { id })
}

export function getFormSimpleList(): Promise<FormSimpleRespVO[]> {
  return get<FormSimpleRespVO[]>('/admin-api/bpm/form/simple-list')
}

export function createForm(data: FormSaveReqVO): Promise<number> {
  return post<number>('/admin-api/bpm/form/create', data)
}

export function updateForm(data: FormSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/form/update', data)
}

export function deleteForm(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/form/delete', { id })
}

// ===================== 流程模型 =====================

export interface ProcessDefinitionVO {
  id: string
  version: number
  deploymentTIme: string
  suspensionState: number
  formType?: number
  formCustomCreatePath?: string
}

export interface ModelRespVO {
  id: number
  key: string
  name: string
  description?: string
  category: string
  formType: number
  formId?: number
  formCustomCreatePath?: string
  formCustomViewPath?: string
  processDefinition?: ProcessDefinitionVO
  status: number
  remark?: string
  createTime: string
  bpmnXml?: string
}

export interface ModelSaveReqVO {
  id?: number
  key: string
  name: string
  description?: string
  category: string
  formType: number
  formId?: number
  formCustomCreatePath?: string
  formCustomViewPath?: string
  status?: number
  remark?: string
}

export function getModelList(name?: string): Promise<ModelRespVO[]> {
  return get<ModelRespVO[]>('/admin-api/bpm/model/list', { name })
}

export function getModel(id: string): Promise<ModelRespVO> {
  return get<ModelRespVO>('/admin-api/bpm/model/get', { id })
}

export function createModel(data: ModelSaveReqVO): Promise<number> {
  return post<number>('/admin-api/bpm/model/create', data)
}

export function updateModel(data: ModelSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/model/update', data)
}

// 更新模型的 BPMN XML
export function updateModelBpmn(data: ModelSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/model/update-bpmn', data)
}

// 批量修改流程模型的排序
export function updateModelSortBatch(ids: number[]): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/model/update-sort-batch', undefined, {
    params: { ids: ids.join(',') }
  })
}

// 修改模型状态（任务状态）
export function updateModelState(id: number, state: number): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/model/update-state', { id, state })
}

export function deleteModel(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/model/delete', { id })
}

// 部署模型
export function deployModel(id: number): Promise<boolean> {
  return post<boolean>('/admin-api/bpm/model/deploy', undefined, { params: { id } })
}

// 清空模型部署信息
export function cleanModel(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/model/clean', { id })
}

// ===================== 简单流程模型 =====================

export interface SimpleModelNode {
  id: string
  name?: string
  type?: number
  [key: string]: unknown
}

/** 简单模型整体结构（simtrle = simple + structure） */
export interface SimpleModel {
  simtrleId?: string
  processId?: string
  processName?: string
  node?: SimpleModelNode
  [key: string]: unknown
}

export function getBpmSimpleModel(id: string): Promise<SimpleModel> {
  return get<SimpleModel>('/admin-api/bpm/model/simple/get', { id })
}

export function updateBpmSimpleModel(data: SimpleModel): Promise<boolean> {
  return post<boolean>('/admin-api/bpm/model/simple/update', data)
}

// ===================== 流程定义 =====================

export interface ProcessDefinitionRespVO {
  id: string
  key: string
  name: string
  version: number
  deploymentId: string
  deploymentTime?: string
  suspensionState: number
  resourceName?: string
  diagramResourceName?: string
  formType?: number
  formId?: number
  formConf?: string
  formCustomCreatePath?: string
  formCustomViewPath?: string
  category?: string
}

export interface ProcessDefinitionPageReqVO extends YudaoPageParam {
  key?: string
  name?: string
  suspensionState?: number
}

export interface ProcessDefinitionListReqVO {
  key?: string
  name?: string
  suspensionState?: number
}

export interface ProcessDefinitionSimpleRespVO {
  id: string
  key: string
  name: string
  version: number
  suspensionState: number
}

export function getProcessDefinition(
  id?: string,
  key?: string
): Promise<ProcessDefinitionRespVO> {
  return get<ProcessDefinitionRespVO>('/admin-api/bpm/process-definition/get', { id, key })
}

export function getProcessDefinitionPage(
  params: ProcessDefinitionPageReqVO
): Promise<YudaoPageResult<ProcessDefinitionRespVO>> {
  return get<YudaoPageResult<ProcessDefinitionRespVO>>(
    '/admin-api/bpm/process-definition/page',
    params
  )
}

export function getProcessDefinitionList(
  params?: ProcessDefinitionListReqVO
): Promise<ProcessDefinitionRespVO[]> {
  return get<ProcessDefinitionRespVO[]>('/admin-api/bpm/process-definition/list', params)
}

export function getSimpleProcessDefinitionList(): Promise<ProcessDefinitionSimpleRespVO[]> {
  return get<ProcessDefinitionSimpleRespVO[]>(
    '/admin-api/bpm/process-definition/simple-list'
  )
}

// ===================== 流程实例 =====================

export interface ProcessInstanceTask {
  id: string
  name: string
}

export interface ProcessInstanceRespVO {
  id: string
  name: string
  processDefinitionId: string
  category?: string
  result: number
  tasks: ProcessInstanceTask[]
  fields?: string[]
  status: number
  remark?: string
  businessKey?: string
  createTime: string
  endTime?: string
  processDefinition?: ProcessDefinitionVO
}

export interface ProcessInstancePageReqVO extends YudaoPageParam {
  name?: string
  processDefinitionId?: string
  status?: number
  result?: number
  category?: string
  createTime?: string[]
}

export interface ProcessInstanceCopyPageReqVO extends YudaoPageParam {
  processInstanceId?: string
  createTime?: string[]
}

/** 流程实例用户信息 */
export interface ProcessInstanceUser {
  id: number
  nickname: string
  avatar?: string
}

/** 审批任务信息 */
export interface ApprovalTaskInfo {
  id: number
  ownerUser?: ProcessInstanceUser
  assigneeUser?: ProcessInstanceUser
  status: number
  reason?: string
  attachments?: string[]
  signPicUrl?: string
}

/** 审批节点信息 */
export interface ApprovalNodeInfo {
  id: number
  name: string
  nodeType: number
  candidateStrategy?: number
  status: number
  startTime?: string
  endTime?: string
  processInstanceId?: string
  candidateUsers?: ProcessInstanceUser[]
  tasks: ApprovalTaskInfo[]
}

export interface ApprovalDetailRespVO {
  processInstance?: ProcessInstanceRespVO
  processDefinition?: ProcessDefinitionRespVO
  formId?: number
  formName?: string
  formConf?: string
  formFields?: string[]
  approvalNodes: ApprovalNodeInfo[]
  status?: number
}

export interface NextApprovalNodeReqVO {
  processInstanceId: string
  taskId?: string
}

export interface FormFieldsPermissionReqVO {
  processInstanceId?: string
  processDefinitionId?: string
  formId?: number
  activityId?: string
}

export function getProcessInstanceMyPage(
  params: ProcessInstancePageReqVO
): Promise<YudaoPageResult<ProcessInstanceRespVO>> {
  return get<YudaoPageResult<ProcessInstanceRespVO>>(
    '/admin-api/bpm/process-instance/my-page',
    params
  )
}

export function getProcessInstanceManagerPage(
  params: ProcessInstancePageReqVO
): Promise<YudaoPageResult<ProcessInstanceRespVO>> {
  return get<YudaoPageResult<ProcessInstanceRespVO>>(
    '/admin-api/bpm/process-instance/manager-page',
    params
  )
}

export function createProcessInstance(data: {
  processDefinitionId: string
  variables?: Record<string, unknown>
  businessKey?: string
}): Promise<string> {
  return post<string>('/admin-api/bpm/process-instance/create', data)
}

// 发起人取消流程实例
export function cancelProcessInstanceByStartUser(id: string, reason: string): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/process-instance/cancel-by-start-user', { id, reason })
}

// 管理员取消流程实例
export function cancelProcessInstanceByAdmin(id: string, reason: string): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/process-instance/cancel-by-admin', { id, reason })
}

export function getProcessInstance(id: string): Promise<ProcessInstanceRespVO> {
  return get<ProcessInstanceRespVO>('/admin-api/bpm/process-instance/get', { id })
}

// 获取抄送流程实例分页
export function getProcessInstanceCopyPage(
  params: ProcessInstanceCopyPageReqVO
): Promise<YudaoPageResult<ProcessInstanceRespVO>> {
  return get<YudaoPageResult<ProcessInstanceRespVO>>(
    '/admin-api/bpm/process-instance/copy/page',
    params
  )
}

// 获取审批详情
export function getApprovalDetail(params: {
  processInstanceId: string
  taskId?: string
  activityId?: string
}): Promise<ApprovalDetailRespVO> {
  return get<ApprovalDetailRespVO>(
    '/admin-api/bpm/process-instance/get-approval-detail',
    params
  )
}

// 获取下一个执行的流程节点
export function getNextApprovalNodes(
  params: NextApprovalNodeReqVO
): Promise<ApprovalNodeInfo[]> {
  return get<ApprovalNodeInfo[]>(
    '/admin-api/bpm/process-instance/get-next-approval-nodes',
    params
  )
}

// 获取表单字段权限
export function getFormFieldsPermission(
  params: FormFieldsPermissionReqVO
): Promise<Record<string, string>> {
  return get<Record<string, string>>(
    '/admin-api/bpm/process-instance/get-form-fields-permission',
    params
  )
}

// 获取流程实例的 BPMN 模型视图
export function getProcessInstanceBpmnModelView(id: string): Promise<unknown> {
  return get<unknown>('/admin-api/bpm/process-instance/get-bpmn-model-view', { id })
}

// 获取流程实例打印数据
export function getProcessInstancePrintData(id: string): Promise<unknown> {
  return get<unknown>('/admin-api/bpm/process-instance/get-print-data', {
    processInstanceId: id
  })
}

// ===================== 任务管理 =====================

/**
 * 任务状态枚举（由 enum 改为 const 对象，受 erasableSyntaxOnly 约束）
 */
export const TaskStatusEnum = {
  /** 跳过 */
  SKIP: -2,
  /** 未开始 */
  NOT_START: -1,
  /** 待审批 */
  WAIT: 0,
  /** 审批中 */
  RUNNING: 1,
  /** 审批通过 */
  APPROVE: 2,
  /** 审批不通过 */
  REJECT: 3,
  /** 已取消 */
  CANCEL: 4,
  /** 已退回 */
  RETURN: 5,
  /** 审批通过中 */
  APPROVING: 7
} as const

/** 任务状态值类型 */
export type TaskStatus = (typeof TaskStatusEnum)[keyof typeof TaskStatusEnum]

export interface TaskRespVO {
  id: string
  name: string
  processInstanceId: string
  processInstanceName?: string
  assigneeUser?: ProcessInstanceUser
  ownerUser?: ProcessInstanceUser
  status: number
  reason?: string
  formId?: number
  formName?: string
  formConf?: string
  createTime: string
  endTime?: string
  [key: string]: unknown
}

export interface TaskPageReqVO extends YudaoPageParam {
  name?: string
  processInstanceId?: string
  createTime?: string[]
}

export interface TaskApproveReqVO {
  id: string
  reason: string
  variables?: Record<string, unknown>
}

export interface TaskRejectReqVO {
  id: string
  reason: string
  variables?: Record<string, unknown>
}

export interface TaskReturnReqVO {
  id: string
  targetTaskDefinitionKey: string
  reason: string
}

export interface TaskDelegateReqVO {
  id: string
  delegateUserId: number
  reason: string
}

export interface TaskTransferReqVO {
  id: string
  transferUserId: number
  reason: string
}

export interface TaskSignReqVO {
  id: string
  userIds: number[]
  reason: string
  type?: number
}

export interface TaskCopyReqVO {
  id: string
  userIds: number[]
  reason: string
}

export interface TaskSignDeleteReqVO {
  id: string
  reason: string
}

// 获取我的待办任务分页
export function getTaskTodoPage(
  params: TaskPageReqVO
): Promise<YudaoPageResult<TaskRespVO>> {
  return get<YudaoPageResult<TaskRespVO>>('/admin-api/bpm/task/todo-page', params)
}

// 获取我的已办任务分页
export function getTaskDonePage(
  params: TaskPageReqVO
): Promise<YudaoPageResult<TaskRespVO>> {
  return get<YudaoPageResult<TaskRespVO>>('/admin-api/bpm/task/done-page', params)
}

// 获取全部任务分页（管理端）
export function getTaskManagerPage(
  params: TaskPageReqVO
): Promise<YudaoPageResult<TaskRespVO>> {
  return get<YudaoPageResult<TaskRespVO>>('/admin-api/bpm/task/manager-page', params)
}

// 通过任务
export function approveTask(data: TaskApproveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/approve', data)
}

// 拒绝任务
export function rejectTask(data: TaskRejectReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/reject', data)
}

// 根据流程实例 ID 获取任务列表
export function getTaskListByProcessInstanceId(
  processInstanceId: string
): Promise<TaskRespVO[]> {
  return get<TaskRespVO[]>('/admin-api/bpm/task/list-by-process-instance-id', {
    processInstanceId
  })
}

// 获取所有可退回的节点
export function getTaskListByReturn(id: string): Promise<unknown[]> {
  return get<unknown[]>('/admin-api/bpm/task/list-by-return', { id })
}

// 退回任务
export function returnTask(data: TaskReturnReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/return', data)
}

// 委派任务
export function delegateTask(data: TaskDelegateReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/delegate', data)
}

// 转办任务
export function transferTask(data: TaskTransferReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/transfer', data)
}

// 加签
export function signCreateTask(data: TaskSignReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/create-sign', data)
}

// 减签
export function signDeleteTask(data: TaskSignDeleteReqVO): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/task/delete-sign', data)
}

// 抄送
export function copyTask(data: TaskCopyReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/copy', data)
}

// 撤回任务
export function withdrawTask(taskId: string): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/task/withdraw', undefined, { params: { taskId } })
}

// 获取流程实例中我的待办任务
export function myTodoTask(processInstanceId: string): Promise<TaskRespVO[]> {
  return get<TaskRespVO[]>('/admin-api/bpm/task/my-todo', { processInstanceId })
}

// 获取减签任务列表（根据父任务 ID）
export function getChildrenTaskList(parentTaskId: string): Promise<TaskRespVO[]> {
  return get<TaskRespVO[]>('/admin-api/bpm/task/list-by-parent-task-id', {
    parentTaskId
  })
}

// ===================== 用户组 =====================

export interface UserGroupRespVO {
  id: number
  name: string
  description?: string
  userIds: number[]
  status: number
  remark?: string
  createTime: string
}

export interface UserGroupSaveReqVO {
  id?: number
  name: string
  description?: string
  userIds: number[]
  status: number
  remark?: string
}

export interface UserGroupPageReqVO extends YudaoPageParam {
  name?: string
  status?: number
}

export interface UserGroupSimpleRespVO {
  id: number
  name: string
}

export function getUserGroupPage(
  params: UserGroupPageReqVO
): Promise<YudaoPageResult<UserGroupRespVO>> {
  return get<YudaoPageResult<UserGroupRespVO>>('/admin-api/bpm/user-group/page', params)
}

export function getUserGroup(id: number): Promise<UserGroupRespVO> {
  return get<UserGroupRespVO>('/admin-api/bpm/user-group/get', { id })
}

export function getUserGroupSimpleList(): Promise<UserGroupSimpleRespVO[]> {
  return get<UserGroupSimpleRespVO[]>('/admin-api/bpm/user-group/simple-list')
}

export function createUserGroup(data: UserGroupSaveReqVO): Promise<number> {
  return post<number>('/admin-api/bpm/user-group/create', data)
}

export function updateUserGroup(data: UserGroupSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/user-group/update', data)
}

export function deleteUserGroup(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/user-group/delete', { id })
}

// ===================== 流程表达式 =====================

export interface ProcessExpressionRespVO {
  id: number
  name: string
  status: number
  expression: string
  createTime?: string
}

export interface ProcessExpressionSaveReqVO {
  id?: number
  name: string
  status: number
  expression: string
}

export interface ProcessExpressionPageReqVO extends YudaoPageParam {
  name?: string
  status?: number
}

export function getProcessExpressionPage(
  params: ProcessExpressionPageReqVO
): Promise<YudaoPageResult<ProcessExpressionRespVO>> {
  return get<YudaoPageResult<ProcessExpressionRespVO>>(
    '/admin-api/bpm/process-expression/page',
    params
  )
}

export function getProcessExpression(id: number): Promise<ProcessExpressionRespVO> {
  return get<ProcessExpressionRespVO>('/admin-api/bpm/process-expression/get', { id })
}

export function createProcessExpression(
  data: ProcessExpressionSaveReqVO
): Promise<number> {
  return post<number>('/admin-api/bpm/process-expression/create', data)
}

export function updateProcessExpression(
  data: ProcessExpressionSaveReqVO
): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/process-expression/update', data)
}

export function deleteProcessExpression(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/process-expression/delete', { id })
}

// 导出流程表达式 Excel
export function exportProcessExpression(
  params: ProcessExpressionPageReqVO
): Promise<Blob> {
  return get<Blob>('/admin-api/bpm/process-expression/export-excel', params, {
    responseType: 'blob'
  })
}

// ===================== 流程监听器 =====================

export interface ProcessListenerRespVO {
  id: number
  name: string
  type: string
  status: number
  event: string
  valueType: string
  value: string
  createTime?: string
}

export interface ProcessListenerSaveReqVO {
  id?: number
  name: string
  type: string
  status: number
  event: string
  valueType: string
  value: string
}

export interface ProcessListenerPageReqVO extends YudaoPageParam {
  name?: string
  type?: string
  status?: number
}

export function getProcessListenerPage(
  params: ProcessListenerPageReqVO
): Promise<YudaoPageResult<ProcessListenerRespVO>> {
  return get<YudaoPageResult<ProcessListenerRespVO>>(
    '/admin-api/bpm/process-listener/page',
    params
  )
}

export function getProcessListener(id: number): Promise<ProcessListenerRespVO> {
  return get<ProcessListenerRespVO>('/admin-api/bpm/process-listener/get', { id })
}

export function createProcessListener(
  data: ProcessListenerSaveReqVO
): Promise<number> {
  return post<number>('/admin-api/bpm/process-listener/create', data)
}

export function updateProcessListener(
  data: ProcessListenerSaveReqVO
): Promise<boolean> {
  return put<boolean>('/admin-api/bpm/process-listener/update', data)
}

export function deleteProcessListener(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/bpm/process-listener/delete', { id })
}

// ===================== OA 请假（示例） =====================

export interface LeaveRespVO {
  id: number
  status: number
  type: number
  reason: string
  processInstanceId: string
  startTime: string
  endTime: string
  createTime?: string
}

export interface LeaveSaveReqVO {
  type: number
  reason: string
  startTime: string
  endTime: string
}

export interface LeavePageReqVO extends YudaoPageParam {
  status?: number
  type?: number
  createTime?: string[]
}

export function createLeave(data: LeaveSaveReqVO): Promise<number> {
  return post<number>('/admin-api/bpm/oa/leave/create', data)
}

export function getLeave(id: number): Promise<LeaveRespVO> {
  return get<LeaveRespVO>('/admin-api/bpm/oa/leave/get', { id })
}

export function getLeavePage(
  params: LeavePageReqVO
): Promise<YudaoPageResult<LeaveRespVO>> {
  return get<YudaoPageResult<LeaveRespVO>>('/admin-api/bpm/oa/leave/page', params)
}
