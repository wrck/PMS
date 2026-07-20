import { del, get, post, put } from '@/utils/request'

export interface ProjectMember {
  id?: number
  projectId: number
  userId: number
  userName?: string
  role: 'PROJECT_MANAGER' | 'PROJECT_MEMBER' | 'APPROVER' | 'VIEWER' | 'CUSTOMER'
  joinDate?: string
  leaveDate?: string
}

export function listMembersByProjectId(projectId: number) {
  return get<ProjectMember[]>(`/api/project/member/project/${projectId}`)
}

export function createMember(data: ProjectMember) {
  return post<ProjectMember>('/api/project/member', data)
}

export function updateMember(data: ProjectMember) {
  return put<ProjectMember>('/api/project/member', data)
}

export function deleteMember(id: number) {
  return del<void>(`/api/project/member/${id}`)
}
