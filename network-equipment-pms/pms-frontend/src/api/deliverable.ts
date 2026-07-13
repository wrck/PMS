import { get, post, put } from '@/utils/request'

/** 终验交付物类型 */
export type DeliverableType =
  | 'AS_BUILT'
  | 'TEST_REPORT'
  | 'ACCEPTANCE_CERT'
  | 'TRAINING_RECORD'
  | 'OPERATION_MANUAL'
  | 'ASSET_REGISTER'
  | 'WARRANTY_CERT'
  | 'SPARE_PARTS_LIST'

export interface DeliverableChecklist {
  id?: number
  projectId: number
  deliverableType: DeliverableType
  required: boolean
  uploaded: boolean
  attachmentId?: number
  checkedAt?: string
  remarks?: string
}

export function listDeliverables(projectId: number): Promise<DeliverableChecklist[]> {
  return get<DeliverableChecklist[]>(`/api/project/deliverable-checklist/project/${projectId}`)
}

export function initChecklist(projectId: number): Promise<boolean> {
  return post<boolean>(`/api/project/deliverable-checklist/project/${projectId}/init`)
}

export function markUploaded(
  id: number,
  attachmentId: number
): Promise<DeliverableChecklist> {
  return put<DeliverableChecklist>('/api/project/deliverable-checklist', { id, attachmentId, uploaded: true })
}

/** 取消已上传的附件标记 */
export function cancelUploaded(id: number): Promise<DeliverableChecklist> {
  return put<DeliverableChecklist>('/api/project/deliverable-checklist', { id, attachmentId: null, uploaded: false })
}

/** 附件下载地址 */
export function downloadAttachment(id: number): string {
  return `/api/file/${id}/download`
}
