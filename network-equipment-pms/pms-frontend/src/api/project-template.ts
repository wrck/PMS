import request from '@/utils/request'

export interface ProjectTemplate {
  id?: number
  templateCode: string
  templateName: string
  category: 'IMPLEMENT' | 'MAINTENANCE' | 'CONSULTING'
  description?: string
  status: 'DRAFT' | 'PUBLISHED' | 'DEPRECATED'
  createTime?: string
  updateTime?: string
}

export interface PhaseDef {
  phaseCode: string
  phaseName: string
  sortOrder: number
  entryCriteria?: any
  exitCriteria?: any
}

export interface TemplateSnapshot {
  phases?: PhaseDef[]
  tasks?: any[]
  milestones?: any[]
  deliverables?: any[]
  dependencies?: any[]
  approvalPlans?: any[]
  assigneeRules?: any[]
}

export interface ProjectTemplateVersion {
  id: number
  templateId: number
  version: string
  snapshotJson: TemplateSnapshot
  changeLog?: string
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  publishedAt?: string
  publishedBy?: number
}

export interface PublishVersionRequest {
  version: string
  snapshot: TemplateSnapshot
  changeLog?: string
}

export interface ProjectCreateFromTemplateDTO {
  templateId: number
  versionId: number
  projectCode: string
  projectName: string
  customerName?: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  projectManagerId?: number
  projectObjective?: string
  projectScope?: string
  members?: { userId: number; role: string }[]
  configOverrides?: Record<string, string>
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export function listTemplates(params: {
  page?: number
  size?: number
  templateName?: string
  category?: string
  status?: string
}) {
  return request.get<PageResult<ProjectTemplate>>('/api/project/template/list', { params })
}

export function getTemplate(id: number) {
  return request.get<ProjectTemplate>(`/api/project/template/${id}`)
}

export function createTemplate(data: ProjectTemplate) {
  return request.post<ProjectTemplate>('/api/project/template', data)
}

export function updateTemplate(data: ProjectTemplate) {
  return request.put<ProjectTemplate>('/api/project/template', data)
}

export function deleteTemplate(id: number) {
  return request.del<void>(`/api/project/template/${id}`)
}

export function listTemplateVersions(id: number, page = 1, size = 10) {
  return request.get<PageResult<ProjectTemplateVersion>>(`/api/project/template/${id}/versions`, {
    params: { page, size }
  })
}

export function publishVersion(id: number, data: PublishVersionRequest) {
  return request.post<ProjectTemplateVersion>(`/api/project/template/${id}/publish`, data)
}

export function getPublishedVersion(id: number) {
  return request.get<ProjectTemplateVersion>(`/api/project/template/${id}/published-version`)
}

export function createProjectFromTemplate(data: ProjectCreateFromTemplateDTO) {
  return request.post('/api/project/template/create-project', data)
}
