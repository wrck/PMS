import request from '@/utils/request'

export function getProjectConfigs(projectId: number) {
  return request.get<Record<string, string>>(`/api/project/config/${projectId}`)
}

export function updateProjectConfigs(projectId: number, configs: Record<string, string>) {
  return request.put<void>(`/api/project/config/${projectId}`, configs)
}
