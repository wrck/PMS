import { del, get } from '@/utils/request'

/**
 * 附件 API。对应后端 `FileController`，挂载在 `/api/file` 下。
 *
 * <p>统一管理各业务模块（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE/TASK 等）
 * 的附件上传/下载/删除/查询。</p>
 *
 * <p>上传由 `FileUploader` 组件直接调用 `POST /api/file/upload`（multipart/form-data），
 * 本模块仅封装查询、删除、下载地址生成等辅助 API。</p>
 */

/** 附件元数据，与后端 `Attachment` 实体对齐 */
export interface Attachment {
  id?: number
  /** 业务类型（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE/TASK 等） */
  bizType?: string
  /** 业务对象 id（新建场景可为空） */
  bizId?: number
  fileName?: string
  fileSize?: number
  mimeType?: string
  uploadUserId?: number
  uploadUserName?: string
  uploadTime?: string
  md5?: string
  storagePath?: string
  storageType?: string
  geoFenceStatus?: string
}

/** 按业务类型 + 业务 id 查询附件列表 */
export function listAttachmentsByBiz(bizType: string, bizId: number): Promise<Attachment[]> {
  return get<Attachment[]>('/api/file/biz', { bizType, bizId })
}

/** 删除附件（先删存储再删记录） */
export function deleteAttachment(id: number): Promise<boolean> {
  return del<boolean>(`/api/file/${id}`)
}

/** 附件下载地址 */
export function attachmentDownloadUrl(id: number): string {
  return `/api/file/${id}/download`
}

/** 附件缩略图地址（仅图片类附件有效） */
export function attachmentThumbnailUrl(id: number, width = 200, height = 200): string {
  return `/api/file/${id}/thumbnail?width=${width}&height=${height}`
}

/** 格式化文件大小 */
export function formatFileSize(bytes?: number): string {
  if (!bytes || bytes <= 0) return '-'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
}

/** 格式化上传时间：去 T 截到秒 */
export function formatUploadTime(s?: string): string {
  return s ? s.replace('T', ' ').slice(0, 19) : '-'
}
