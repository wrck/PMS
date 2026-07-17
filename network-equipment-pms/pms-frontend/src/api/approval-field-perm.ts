import { del, get, post, put } from '@/utils/request'

/**
 * 审批字段权限配置 API。对应后端 {@code ApprovalFieldPermissionController}，
 * 挂载在 {@code /api/workflow/field-perm} 下。
 *
 * <p>关联设计文档 §2.2 ApprovalFieldPermission（行 233-243）、§5.7、§6.9。</p>
 */

/** 权限：VISIBLE / MASKED / HIDDEN */
export type FieldPermission = 'VISIBLE' | 'MASKED' | 'HIDDEN'

/** 脱敏规则：phone-mask / amount-mask / email-mask / custom */
export type MaskPattern = 'phone-mask' | 'amount-mask' | 'email-mask' | 'custom' | string

/** 审批敏感字段权限 */
export interface ApprovalFieldPermission {
  id?: number
  approvalNodeId: number
  /** 业务实体类名 */
  entityType: string
  fieldName: string
  permission?: FieldPermission | string
  maskPattern?: MaskPattern
  /** 自定义正则（当 maskPattern=custom） */
  customPattern?: string
  version?: number
  createTime?: string
  updateTime?: string
}

/** 查询字段权限列表（按节点 + 实体类型过滤） */
export function listFieldPermissions(
  approvalNodeId: number,
  entityType?: string
): Promise<ApprovalFieldPermission[]> {
  return get<ApprovalFieldPermission[]>('/api/workflow/field-perm/list', {
    approvalNodeId,
    entityType
  })
}

/** 新增字段权限 */
export function saveFieldPermission(
  data: ApprovalFieldPermission
): Promise<ApprovalFieldPermission> {
  return post<ApprovalFieldPermission>('/api/workflow/field-perm', data)
}

/** 更新字段权限 */
export function updateFieldPermission(
  data: ApprovalFieldPermission
): Promise<ApprovalFieldPermission> {
  return put<ApprovalFieldPermission>('/api/workflow/field-perm', data)
}

/** 删除字段权限 */
export function deleteFieldPermission(id: number): Promise<void> {
  return del<void>(`/api/workflow/field-perm/${id}`)
}
