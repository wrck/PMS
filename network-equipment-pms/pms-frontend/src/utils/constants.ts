/**
 * yudao 通用常量适配层。
 *
 * <p>提供 yudao 原生页面所需的 {@code CommonStatusEnum}、
 * {@code BpmModelFormType}、{@code BpmModelType}、{@code BpmAutoApproveType} 等。</p>
 */

/** 通用状态枚举（yudao CommonStatusEnum） */
export const CommonStatusEnum = {
  ENABLE: 0,
  DISABLE: 1
} as const

/** 通用状态选项（用于 el-select） */
export const CommonStatusOptions = [
  { label: '开启', value: CommonStatusEnum.ENABLE },
  { label: '关闭', value: CommonStatusEnum.DISABLE }
]

/** 是否（布尔字符串字典） */
export const BooleanStringEnum = {
  TRUE: 'true',
  FALSE: 'false'
} as const

/** BPM 模型表单类型 */
export const BpmModelFormType = {
  NORMAL: 10,
  CUSTOM: 20
} as const

/** BPM 模型类型 */
export const BpmModelType = {
  BPMN: 10,
  SIMPLE: 20
} as const

/** BPM 自动审批类型 */
export const BpmAutoApproveType = {
  NO_AUTO_APPROVE: 0,
  AUTO_APPROVE_ALL: 1,
  AUTO_APPROVE_NOT_APPROVE: 2
} as const

/** 用户类型 */
export const UserType = {
  ADMIN: 1,
  MEMBER: 2
} as const

/** 用户类型选项 */
export const UserTypeOptions = [
  { label: '管理员', value: UserType.ADMIN },
  { label: '会员', value: UserType.MEMBER }
]
