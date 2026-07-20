/**
 * SysDict 实体 Schema 与 Validator
 * ===========================================================================
 *
 * 与后端 `pms-system/.../entity/SysDict.java` 严格对齐，作为后端 Jakarta
 * Validation 注解（@NotBlank/@Size/@Pattern 等）的前端镜像。
 *
 * 字段命名以后端为准：dictName / dictType / status 等。
 *
 * 历史问题：前端 `api/system.ts` 的 `SysDict` interface 曾使用短名
 * `code/name`，与后端 `dictType/dictName` 不一致，导致 `POST /api/system/dict`
 * 触发 400 校验失败。本 validator 通过 `fieldMapping` 提供旧字段名→新字段名
 * 的自动映射，在不破坏现有 view 代码的前提下保证出站请求体字段名正确。
 * ===========================================================================
 */
import {
  createValidatorWithMapping,
  defineSchema,
  number,
  optional,
  string,
  type Schema,
  type ValidationResult
} from './index'
import { registerValidator } from './registry'

// ============ SysDict Schema ============
//
// 与后端 SysDict.java 字段一一对应：
// - dictName:   String
// - dictType:   String
// - status:     String（0=normal, 1=disabled）
//
// 注：BaseEntity 字段（id/createTime/updateTime/createBy/updateBy/deleted）
//     在前端 schema 中以可选形式声明，便于响应数据校验。
export const sysDictSchema: Schema = defineSchema({
  // ============ BaseEntity 字段 ============
  id: [optional(), number()],
  createTime: [optional(), string()],
  updateTime: [optional(), string()],
  createBy: [optional(), string()],
  updateBy: [optional(), string()],
  deleted: [optional(), number()],

  // ============ SysDict 自身字段 ============
  dictName: [optional(), string()],
  dictType: [optional(), string()],
  status: [optional(), string()]
})

/**
 * 旧字段名 → 后端字段名映射。
 *
 * 用于兼容历史代码：当前端代码仍使用 `code/name` 时，
 * validator 会自动映射到 `dictType/dictName`。
 *
 * 注：前端 `remark` 为前端独有字段，后端 SysDict 未定义，
 *     会被 schema 白名单过滤剥离，不参与映射。
 */
export const sysDictFieldMapping = {
  code: 'dictType',
  name: 'dictName'
} as const

/** SysDict 请求体 validator（带旧字段名兼容映射） */
export const sysDictRequestValidator = createValidatorWithMapping(
  sysDictSchema,
  sysDictFieldMapping
)

/** SysDict 响应数据 validator（无映射，直接按后端字段名校验） */
export function validateSysDictResponse(input: unknown): ValidationResult {
  return createValidatorWithMapping(sysDictSchema, {})(input)
}

/**
 * SysDict 类型定义——与后端 SysDict 实体字段名严格一致。
 *
 * 替代 `api/system.ts` 中的旧 `SysDict` interface（含 code/name 等短名）。
 * 现有 view 代码迁移到新 interface 后，可删除旧定义。
 */
export interface SysDictDTO {
  // BaseEntity
  id?: number
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
  deleted?: number

  // SysDict 自身字段
  dictName?: string
  dictType?: string
  status?: string
}

// ============ 注册到全局 registry ============
//
// 注册后，request.ts 拦截器会自动调用 sysDictRequestValidator 校验以下 URL：
// - POST   /api/system/dict   （创建字典）
// - PUT    /api/system/dict    （更新字典）
//
// 校验失败 → ElMessage.error + 阻止请求发送（避免触发后端 400）
registerValidator({
  method: 'POST',
  urlPattern: '/api/system/dict',
  requestValidator: sysDictRequestValidator,
  description: '创建字典 - 校验请求体字段名'
})

registerValidator({
  method: 'PUT',
  urlPattern: '/api/system/dict',
  requestValidator: sysDictRequestValidator,
  description: '更新字典 - 校验请求体字段名'
})
