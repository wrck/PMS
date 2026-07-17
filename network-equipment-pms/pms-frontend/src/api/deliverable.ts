import { del, get, post, put } from '@/utils/request'

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

// ====================================================================
// 交付件全生命周期 API（Story 5，7 态状态机）
// 关联设计文档：§5.6 行 1024-1079
// ====================================================================

/** 交付件 7 态状态 */
export type DeliverableStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'REVIEWED'
  | 'SIGNED'
  | 'PUBLISHED'
  | 'REFERENCED'
  | 'ARCHIVED'

/** 7 态中文标签映射（与后端 DeliverableStatus#label 对齐） */
export const DELIVERABLE_STATUS_LABELS: Record<DeliverableStatus, string> = {
  DRAFT: '草稿',
  SUBMITTED: '已提交',
  REVIEWED: '已审核',
  SIGNED: '已签核',
  PUBLISHED: '已发布',
  REFERENCED: '已引用',
  ARCHIVED: '已归档'
}

/** 7 态顺序（用于状态流可视化） */
export const DELIVERABLE_STATUS_ORDER: DeliverableStatus[] = [
  'DRAFT',
  'SUBMITTED',
  'REVIEWED',
  'SIGNED',
  'PUBLISHED',
  'REFERENCED',
  'ARCHIVED'
]

/** 交付件类型（全生命周期） */
export type DeliverableKind = 'DOCUMENT' | 'CONFIG' | 'REPORT' | 'OTHER'

/** 交付件实体（7 态状态机） */
export interface Deliverable {
  id?: number
  projectId: number
  deliverableName: string
  deliverableType?: DeliverableKind | string
  filePath?: string
  status?: DeliverableStatus
  phaseId?: number
  currentVersion?: number
  mandatory?: boolean
  approverRole?: string
  publishedAt?: string
  archivedAt?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

/** 交付件版本记录（不可变历史） */
export interface DeliverableVersion {
  id?: number
  deliverableId: number
  versionNo: number
  filePath: string
  fileChecksum?: string
  uploadedBy?: number
  uploadedAt?: string
  changeLog?: string
  status?: DeliverableStatus
}

/** 交付件签名记录 */
export interface DeliverableSignature {
  id?: number
  deliverableId: number
  versionNo?: number
  signerId: number
  signerName?: string
  signerRole?: string
  signatureType?: 'ELECTRONIC' | 'STAMP' | 'DIGITAL'
  signatureData?: string
  signedAt?: string
}

/** 引用方业务类型 */
export type DeliverableReferenceType =
  | 'TASK'
  | 'PHASE'
  | 'PROJECT'
  | 'DELIVERABLE'
  | 'REPORT'

/** 交付件引用关系 */
export interface DeliverableReference {
  id?: number
  sourceDeliverableId: number
  targetDeliverableId?: number
  referenceType: DeliverableReferenceType | string
  referencedById: number
  referencedByName?: string
  createBy?: string
  createTime?: string
}

/** 阶段必需交付件校验结果（Story 5 验收 2） */
export interface MandatoryDeliverableValidationResult {
  allApproved: boolean
  items: MandatoryDeliverableItem[]
}

/** 单个未满足的必需交付件 */
export interface MandatoryDeliverableItem {
  deliverableId: number
  deliverableName: string
  mandatory: boolean
  expectedStatus: DeliverableStatus | string
  actualStatus: DeliverableStatus | string
  approved: boolean
}

/** 修订请求体 */
export interface ReviseRequest {
  filePath: string
  changeLog?: string
  uploadedBy?: number
}

// -------------------- CRUD --------------------

/** 按项目/阶段/状态过滤查询交付件列表 */
export function listFullDeliverables(params: {
  projectId?: number
  phaseId?: number
  status?: DeliverableStatus | string
}): Promise<Deliverable[]> {
  return get<Deliverable[]>('/api/deliverable/list', params)
}

/** 查询交付件详情 */
export function getDeliverable(id: number): Promise<Deliverable> {
  return get<Deliverable>(`/api/deliverable/${id}`)
}

/** 新建交付件（默认 DRAFT，若提供 filePath 则同步创建 v1 版本） */
export function createDeliverable(data: Deliverable): Promise<Deliverable> {
  return post<Deliverable>('/api/deliverable', data)
}

/** 更新交付件基础信息 */
export function updateDeliverable(id: number, data: Deliverable): Promise<Deliverable> {
  return put<Deliverable>(`/api/deliverable/${id}`, data)
}

/** 删除交付件 */
export function deleteDeliverable(id: number): Promise<void> {
  return del<void>(`/api/deliverable/${id}`)
}

// -------------------- 7 态状态机 --------------------

/** 提交：DRAFT → SUBMITTED */
export function submitDeliverable(id: number): Promise<Deliverable> {
  return post<Deliverable>(`/api/deliverable/${id}/submit`)
}

/** 审核：SUBMITTED → REVIEWED（通过）/ DRAFT（退回） */
export function reviewDeliverable(id: number, passed: boolean): Promise<Deliverable> {
  return post<Deliverable>(`/api/deliverable/${id}/review?passed=${passed}`)
}

/** 签核：REVIEWED → SIGNED */
export function signDeliverable(id: number): Promise<Deliverable> {
  return post<Deliverable>(`/api/deliverable/${id}/sign`)
}

/** 发布：SIGNED → PUBLISHED */
export function publishDeliverable(id: number): Promise<Deliverable> {
  return post<Deliverable>(`/api/deliverable/${id}/publish`)
}

/** 归档：REFERENCED → ARCHIVED */
export function archiveDeliverable(id: number): Promise<Deliverable> {
  return post<Deliverable>(`/api/deliverable/${id}/archive`)
}

// -------------------- 版本管理 --------------------

/** 查询版本历史（按版本号倒序） */
export function listDeliverableVersions(id: number): Promise<DeliverableVersion[]> {
  return get<DeliverableVersion[]>(`/api/deliverable/${id}/versions`)
}

/** 修订：新建版本不覆盖旧版本（Story 5 验收 1） */
export function reviseDeliverable(id: number, body: ReviseRequest): Promise<DeliverableVersion> {
  const qs = new URLSearchParams({ filePath: body.filePath })
  if (body.changeLog) qs.set('changeLog', body.changeLog)
  if (body.uploadedBy != null) qs.set('uploadedBy', String(body.uploadedBy))
  return post<DeliverableVersion>(`/api/deliverable/${id}/revise?${qs.toString()}`)
}

/** 查询指定版本记录 */
export function getDeliverableVersion(id: number, versionNo: number): Promise<DeliverableVersion> {
  return get<DeliverableVersion>(`/api/deliverable/${id}/versions/${versionNo}`)
}

// -------------------- 签名管理 --------------------

/** 查询交付件签名记录（按签核时间倒序） */
export function listDeliverableSignatures(id: number): Promise<DeliverableSignature[]> {
  return get<DeliverableSignature[]>(`/api/deliverable/${id}/signatures`)
}

/** 新增签名记录（REVIEWED → SIGNED 阶段的签核动作） */
export function addDeliverableSignature(id: number, body: Partial<DeliverableSignature>): Promise<DeliverableSignature> {
  return post<DeliverableSignature>(`/api/deliverable/${id}/signatures`, body)
}

// -------------------- 引用管理 --------------------

/** 查询交付件被引用记录（按创建时间倒序） */
export function listDeliverableReferences(id: number): Promise<DeliverableReference[]> {
  return get<DeliverableReference[]>(`/api/deliverable/${id}/references`)
}

/** 新增引用关系（PUBLISHED → REFERENCED 流转） */
export function addDeliverableReference(id: number, body: Partial<DeliverableReference>): Promise<DeliverableReference> {
  return post<DeliverableReference>(`/api/deliverable/${id}/references`, body)
}

// -------------------- 阶段退出校验 --------------------

/** 阶段必需交付件校验（Story 5 验收 2） */
export function validateMandatoryDeliverables(phaseId: number): Promise<MandatoryDeliverableValidationResult> {
  return get<MandatoryDeliverableValidationResult>(`/api/deliverable/phase/${phaseId}/validate`)
}
