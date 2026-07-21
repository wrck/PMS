import { del, get, post, put } from '@/utils/request'
import request from '@/utils/request'
import { getDictItems, type SysDictItem } from '@/api/system'

/** 交付件性质类型字典编码 */
export const DELIVERABLE_TYPE_DICT = 'pms_deliverable_type'
/** 交付件引用实体类型字典编码 */
export const DELIVERABLE_REF_ENTITY_TYPE_DICT = 'pms_deliverable_ref_entity_type'

/**
 * @deprecated 交付件类型已改为数据字典驱动（字典 pms_deliverable_type），
 * 请使用 {@link loadDeliverableTypes} 动态加载。本类型保留用于历史兼容。
 */
export type DeliverableType = string

/**
 * @deprecated 已改为数据字典驱动，请使用 {@link loadDeliverableTypes}。
 */
export const DELIVERABLE_TYPE_LABELS: Record<string, string> = {
  DOCUMENT: '文档',
  CODE: '代码',
  ENTITY_REF: '实体引用',
  MODEL: '模型',
  CONFIG: '配置',
  DATA: '数据',
  OTHER: '其他'
}

/** 交付件类型字典项缓存（首次调用后填充） */
let deliverableTypeItems: SysDictItem[] | null = null

/** 加载交付件性质类型字典（带缓存） */
export async function loadDeliverableTypes(): Promise<SysDictItem[]> {
  if (deliverableTypeItems) return deliverableTypeItems
  try {
    deliverableTypeItems = await getDictItems(DELIVERABLE_TYPE_DICT)
  } catch {
    deliverableTypeItems = []
  }
  return deliverableTypeItems
}

/** 交付件引用实体类型字典项缓存 */
let deliverableRefEntityTypeItems: SysDictItem[] | null = null

/** 加载交付件引用实体类型字典（带缓存） */
export async function loadDeliverableRefEntityTypes(): Promise<SysDictItem[]> {
  if (deliverableRefEntityTypeItems) return deliverableRefEntityTypeItems
  try {
    deliverableRefEntityTypeItems = await getDictItems(DELIVERABLE_REF_ENTITY_TYPE_DICT)
  } catch {
    deliverableRefEntityTypeItems = []
  }
  return deliverableRefEntityTypeItems
}

/** 翻译交付件类型值为中文标签（优先用字典缓存，兜底 DELIVERABLE_TYPE_LABELS） */
export function translateDeliverableType(value?: string): string {
  if (!value) return '-'
  if (deliverableTypeItems) {
    const item = deliverableTypeItems.find((it) => it.itemValue === value)
    if (item) return item.itemText
  }
  return DELIVERABLE_TYPE_LABELS[value] ?? value
}

/**
 * @deprecated 终验校验已改为直接查 pms_deliverable 表，本接口保留用于历史兼容，将在下版本删除。
 * 新代码请使用 {@link listFullDeliverables} 查询项目交付件。
 */
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

/**
 * @deprecated 使用 {@link listFullDeliverables} 替代。
 */
export function listDeliverables(projectId: number): Promise<DeliverableChecklist[]> {
  return get<DeliverableChecklist[]>(`/api/project/deliverable-checklist/project/${projectId}`)
}

/**
 * @deprecated 终验时后端会自动初始化标准交付件，无需前端手动调用。
 */
export function initChecklist(projectId: number): Promise<boolean> {
  return post<boolean>(`/api/project/deliverable-checklist/project/${projectId}/init`)
}

/**
 * @deprecated 使用 {@link uploadDeliverableInitialVersion} 替代。
 */
export function markUploaded(
  id: number,
  attachmentId: number
): Promise<DeliverableChecklist> {
  // 专用端点：仅更新 attachmentId / uploaded / checkedAt 字段，
  // 绕开 PUT /api/project/deliverable-checklist 的 @Valid 全字段校验
  return put<DeliverableChecklist>(
    `/api/project/deliverable-checklist/${id}/mark-uploaded`,
    { attachmentId }
  )
}

/**
 * @deprecated 使用 Deliverable 状态机流转（submit/review/sign/publish）替代。
 */
/** 取消已上传的附件标记 */
export function cancelUploaded(id: number): Promise<DeliverableChecklist> {
  return put<DeliverableChecklist>(
    `/api/project/deliverable-checklist/${id}/cancel-uploaded`
  )
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

/**
 * @deprecated 已统一到 {@link DeliverableType}，新代码请直接使用 DeliverableType。
 */
export type DeliverableKind = DeliverableType

/** 交付件实体（7 态状态机） */
export interface Deliverable {
  id?: number
  projectId: number
  projectName?: string
  deliverableName: string
  deliverableType?: DeliverableType | string
  filePath?: string
  status?: DeliverableStatus
  phaseId?: number
  currentVersion?: number
  mandatory?: boolean
  templateInherited?: boolean
  approverRole?: string
  refEntityType?: string
  refEntityId?: number
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

/** 按项目/阶段/状态/来源过滤查询交付件列表 */
export function listFullDeliverables(params: {
  projectId?: number
  phaseId?: number
  status?: DeliverableStatus | string
  templateInherited?: boolean
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

/** 上传交付件初始文件并创建 v1 版本。 */
export async function uploadDeliverableInitialVersion(
  id: number,
  file: File,
  changeLog?: string
): Promise<DeliverableVersion> {
  const form = new FormData()
  form.append('file', file)
  if (changeLog) form.append('changeLog', changeLog)
  return request.post(`/api/deliverable/${id}/upload`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
    skipValidate: true
  }) as Promise<DeliverableVersion>
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
