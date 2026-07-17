import { get, put } from '@/utils/request'

/**
 * 项目配置 API（Story 3 / 4 阈值与策略配置）。
 *
 * <p>响应拦截器已剥离外层 Result 信封，故此处直接返回 {@code Promise<T>}。</p>
 */

/** 查询项目级配置（覆盖系统默认） */
export function getProjectConfigs(projectId: number): Promise<Record<string, string>> {
  return get<Record<string, string>>(`/api/project/config/${projectId}`)
}

/** 更新项目级配置（全量覆盖） */
export function updateProjectConfigs(
  projectId: number,
  configs: Record<string, string>
): Promise<void> {
  return put<void>(`/api/project/config/${projectId}`, configs)
}
