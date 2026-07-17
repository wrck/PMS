import request from '@/utils/request'

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
  return request.get<ProjectMember[]>(`/api/project/member/project/${projectId}`)
}

export function createMember(data: ProjectMember) {
  return request.post<ProjectMember>('/api/project/member', data)
}

export function updateMember(data: ProjectMember) {
  return request.put<ProjectMember>('/api/project/member', data)
}

export function deleteMember(id: number) {
  return request.del<void>(`/api/project/member/${id}`)
}
