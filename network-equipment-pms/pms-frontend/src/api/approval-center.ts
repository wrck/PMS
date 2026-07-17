import { get, post } from '@/utils/request'

/**
 * 统一审批中心 API。对应后端 {@code ApprovalCenterController}，
 * 挂载在 {@code /api/workflow/approval} 下。
 *
 * <p>关联设计文档 §5.7 统一审批中心 API（Story 6，行 1080-1147）。</p>
 */

/** 审批类型 */
export type ApprovalType =
  | 'PROJECT'
  | 'TASK'
  | 'DELIVERABLE'
  | 'RISK'
  | 'ISSUE'
  | 'CHANGE'
  | 'RESOURCE'
  | 'COST'
  | 'PHASE_EXIT'
  | 'BASELINE_CHANGE'

/** 审批状态：PENDING / APPROVED / REJECTED / WITHDRAWN / TIMEOUT */
export type ApprovalStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'WITHDRAWN'
  | 'TIMEOUT'

/** 统一审批记录 */
export interface ApprovalRecord {
  id?: number
  approvalType: ApprovalType | string
  businessId: number
  businessCode?: string
  projectId?: number
  processInstanceId?: string
  title: string
  submitterId: number
  submitterName?: string
  currentNodeId?: string
  currentNodeName?: string
  status?: ApprovalStatus | string
  round?: number
  submittedAt?: string
  completedAt?: string
  timeoutAt?: string
  escalated?: boolean
  version?: number
  createTime?: string
  updateTime?: string
}

/** 审批历史 */
export interface ApprovalHistory {
  id?: number
  recordId: number
  round: number
  nodeName: string
  operatorId: number
  operatorName?: string
  /** 动作：SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT */
  action: string
  opinion?: string
  operatedAt?: string
}

/** 脱敏字段元数据 */
export interface MaskedField {
  fieldName: string
  /** VISIBLE / MASKED / HIDDEN */
  permission: string
  maskedValue?: string
  /** phone-mask / amount-mask / email-mask / custom */
  maskPattern?: string
}

/** 审批详情（含脱敏业务数据 + 历史） */
export interface ApprovalDetailVO {
  record: ApprovalRecord
  /** 脱敏后的业务数据（HIDDEN 字段不会出现） */
  businessData?: Record<string, unknown>
  maskedFields?: MaskedField[]
  history?: ApprovalHistory[]
}

/** 审批统计 */
export interface ApprovalStatistics {
  pendingCount: number
  approvedCount: number
  rejectedCount: number
  withdrawnCount: number
  timeoutCount: number
  totalCount: number
}

/** 通用列表查询参数 */
export interface ApprovalListQuery {
  status?: string
  approvalType?: string
  projectId?: number
}

/** 我的待办审批 */
export function getPendingApprovals(): Promise<ApprovalRecord[]> {
  return get<ApprovalRecord[]>('/api/workflow/approval/pending')
}

/** 我提交的审批 */
export function getSubmittedApprovals(): Promise<ApprovalRecord[]> {
  return get<ApprovalRecord[]>('/api/workflow/approval/submitted')
}

/** 项目维度审批列表 */
export function getApprovalsByProject(projectId: number): Promise<ApprovalRecord[]> {
  return get<ApprovalRecord[]>(`/api/workflow/approval/project/${projectId}`)
}

/** 通用审批列表（可按状态/类型/项目过滤） */
export function listApprovals(params: ApprovalListQuery): Promise<ApprovalRecord[]> {
  return get<ApprovalRecord[]>('/api/workflow/approval/list', params)
}

/** 审批统计（按状态聚合） */
export function getApprovalStatistics(): Promise<ApprovalStatistics> {
  return get<ApprovalStatistics>('/api/workflow/approval/statistics')
}

/** 审批详情（含字段脱敏） */
export function getApprovalDetail(id: number): Promise<ApprovalDetailVO> {
  return get<ApprovalDetailVO>(`/api/workflow/approval/${id}`)
}

/** 审批历史（含所有轮次） */
export function getApprovalHistory(id: number): Promise<ApprovalHistory[]> {
  return get<ApprovalHistory[]>(`/api/workflow/approval/${id}/history`)
}

/** 通过当前节点 */
export function approveApproval(id: number, comment?: string): Promise<ApprovalRecord> {
  return post<ApprovalRecord>(`/api/workflow/approval/${id}/approve`, undefined, {
    params: { comment }
  })
}

/** 退回当前节点 */
export function rejectApproval(id: number, comment?: string): Promise<ApprovalRecord> {
  return post<ApprovalRecord>(`/api/workflow/approval/${id}/reject`, undefined, {
    params: { comment }
  })
}

/** 撤回审批（仅提交人） */
export function withdrawApproval(id: number): Promise<ApprovalRecord> {
  return post<ApprovalRecord>(`/api/workflow/approval/${id}/withdraw`)
}

/** 重新提交（保留历史，round+1） */
export function resubmitApproval(id: number, comment?: string): Promise<ApprovalRecord> {
  return post<ApprovalRecord>(`/api/workflow/approval/${id}/resubmit`, undefined, {
    params: { comment }
  })
}
