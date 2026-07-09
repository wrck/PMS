// src/api/lowcode-version.ts
import { get, post } from '@/utils/request'

export interface LowCodeConfigVersion {
  id: number
  configType: string
  configId: number
  configCode: string
  version: number
  snapshot: string
  changeLog?: string
  status: string
  environment: string
  createTime: string
  createBy: string
  parentVersionId?: number | null
  branch?: string
  tags?: string
}

export interface DiffEntry {
  changeType: 'ADDED' | 'REMOVED' | 'MODIFIED'
  fieldPath: string
  oldValue?: string
  newValue?: string
}

export interface VersionDiffDTO {
  fromVersion: number
  toVersion: number
  entries: DiffEntry[]
}

/** 版本树节点（按 parentVersionId 构建分支树，支持多分支） */
export interface VersionTreeNode {
  version: number
  configCode: string
  changeLog?: string
  status: string
  environment: string
  createBy: string
  createTime: string
  children: VersionTreeNode[]
  versionId?: number
  parentVersionId?: number | null
  branch?: string
  tags?: string
}

export function getVersionHistory(configType: string, configId: number) {
  return get<LowCodeConfigVersion[]>('/api/lowcode/version/history', {
    configType,
    configId
  })
}

/** 查询版本树（按 parentVersionId 构建分支树，支持多分支） */
export function getVersionTree(configType: string, configId: number) {
  return get<VersionTreeNode[]>('/api/lowcode/version/tree', {
    configType,
    configId
  })
}

export function diffVersions(
  configType: string,
  configId: number,
  fromVersion: number,
  toVersion: number
) {
  return get<VersionDiffDTO>('/api/lowcode/version/diff', {
    configType,
    configId,
    fromVersion,
    toVersion
  })
}

export function rollbackVersion(
  configType: string,
  configId: number,
  targetVersion: number,
  changeLog?: string
) {
  return post<LowCodeConfigVersion>('/api/lowcode/version/rollback', null, {
    params: { configType, configId, targetVersion, changeLog }
  })
}

export function promoteConfig(targetEnvironment: string, configCodes: string[]) {
  return post<void>('/api/lowcode/version/promote', configCodes, {
    params: { targetEnvironment }
  })
}

export function exportPackage(configCodes: string[]) {
  return get<string>('/api/lowcode/version/export-package', {
    configCodes: configCodes.join(',')
  })
}

/** 导出配置包（zip 二进制） */
export function exportPackageZip(configCodes: string[], targetEnvironment: string) {
  return post<Blob>('/api/lowcode/version/export-package', { configCodes, targetEnvironment }, { responseType: 'blob' })
}

/** 导入配置包 */
export function importPackage(file: File, overwrite: boolean) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('overwrite', String(overwrite))
  return post('/api/lowcode/version/import-package', formData)
}

/** 创建分支（基于指定版本创建新分支） */
export function createBranch(params: {
  configType: string
  configId: number
  baseVersionId: number
  branchName: string
  changeLog?: string
}) {
  return post<LowCodeConfigVersion>('/api/lowcode/version/branch', params)
}

/** 为版本添加标签 */
export function addTag(params: {
  configType: string
  configId: number
  versionId: number
  tag: string
}) {
  return post<LowCodeConfigVersion>('/api/lowcode/version/tag', params)
}
