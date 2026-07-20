/**
 * Project 实体 Schema 与 Validator
 * ===========================================================================
 *
 * 与后端 `pms-project/.../entity/Project.java` 严格对齐，作为后端 Jakarta
 * Validation 注解（@NotBlank/@Size/@Min/@Max 等）的前端镜像。
 *
 * 字段命名以后端为准：projectCode / projectName / projectType 等。
 *
 * 历史问题：前端 `api/project.ts` 的 `Project` interface 曾使用短名
 * `code/name/type`，与后端不一致，导致 `POST /api/project` 触发 400 校验失败。
 * 本 validator 通过 `fieldMapping` 提供旧字段名→新字段名的自动映射，
 * 在不破坏现有 view 代码的前提下保证出站请求体字段名正确。
 * ===========================================================================
 */
import {
  createValidatorWithMapping,
  defineSchema,
  max,
  maxLen,
  min,
  number,
  optional,
  required,
  string,
  type Schema,
  type ValidationResult
} from './index'
import { registerValidator } from './registry'

// ============ Project Schema ============
//
// 与后端 Project.java 字段一一对应：
// - projectCode:   @NotBlank @Size(max=50)
// - projectName:   @NotBlank @Size(max=200)
// - projectType:   @NotBlank @Size(max=50)
// - status:        @Size(max=50)
// - customerName:  @NotBlank @Size(max=200)
// - customerContact: @Size(max=100)
// - customerPhone:   @Size(max=50)
// - contractNo:      @Size(max=100)
// - contractAmount:  @DecimalMin(0)
// - planStartDate / planEndDate: LocalDate
// - projectManagerName: @Size(max=50)
// - description: @Size(max=2000)
// - progress: @Min(0) @Max(100)
// - priority: @Size(max=20)
//
// 注：BaseEntity 字段（id/createTime/updateTime/createBy/updateBy/version）
//     在前端 schema 中以可选形式声明，便于响应数据校验。
const PROJECT_STATUS_ENUM = [
  'PENDING',
  'APPROVED',
  'IN_PROGRESS',
  'INITIAL_ACCEPTANCE',
  'FINAL_ACCEPTANCE',
  'COMPLETED',
  'CLOSED',
  'REJECTED'
] as const

const PROJECT_TYPE_ENUM = ['NETWORK_DEVICE', 'SECURITY', 'DATACENTER'] as const

export const projectSchema: Schema = defineSchema({
  // ============ BaseEntity 字段 ============
  id: [optional(), number()],
  createTime: [optional(), string()],
  updateTime: [optional(), string()],
  createBy: [optional(), string()],
  updateBy: [optional(), string()],
  version: [optional(), number()],

  // ============ Project 自身字段 ============
  projectCode: [optional(), string(), maxLen(50)],
  projectName: [required(), string(), maxLen(200)],
  projectType: [required(), string(), maxLen(50)],
  status: [optional(), string(), maxLen(50)],
  customerName: [required(), string(), maxLen(200)],
  customerContact: [optional(), string(), maxLen(100)],
  customerPhone: [optional(), string(), maxLen(50)],
  contractNo: [optional(), string(), maxLen(100)],
  contractAmount: [optional(), number(), min(0)],
  planStartDate: [optional(), string()],
  planEndDate: [optional(), string()],
  actualStartDate: [optional(), string()],
  actualEndDate: [optional(), string()],
  projectManagerId: [optional(), number()],
  projectManagerName: [optional(), string(), maxLen(50)],
  description: [optional(), string(), maxLen(2000)],
  progress: [optional(), number(), min(0), max(100)],
  priority: [optional(), string(), maxLen(20)],
  processInstanceId: [optional(), string()],
  parentProjectId: [optional(), number()],
  projectPath: [optional(), string()],
  depth: [optional(), number()],
  weight: [optional(), number()],
  templateId: [optional(), number()],
  templateVersion: [optional(), string()],
  currentPhaseId: [optional(), number()],
  projectObjective: [optional(), string()],
  projectScope: [optional(), string()]
})

/**
 * 旧字段名 → 后端字段名映射。
 *
 * 用于兼容历史代码：当前端代码仍使用 `name/type/managerName` 时，
 * validator 会自动映射到 `projectName/projectType/projectManagerName`。
 */
export const projectFieldMapping = {
  code: 'projectCode',
  name: 'projectName',
  type: 'projectType',
  managerName: 'projectManagerName'
} as const

/** Project 请求体 validator（带旧字段名兼容映射） */
export const projectRequestValidator = createValidatorWithMapping(
  projectSchema,
  projectFieldMapping
)

/** Project 响应数据 validator（无映射，直接按后端字段名校验） */
export function validateProjectResponse(input: unknown): ValidationResult {
  // 响应数据已经是后端字段名，无需映射；用同 schema 做白名单过滤
  return createValidatorWithMapping(projectSchema, {})(input)
}

/**
 * Project 类型定义——与后端 Project 实体字段名严格一致。
 *
 * 替代 `api/project.ts` 中的旧 `Project` interface（短名版）。
 * 现有 view 代码迁移到新 interface 后，可删除旧定义。
 */
export interface ProjectDTO {
  // BaseEntity
  id?: number
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
  version?: number

  // Project 自身字段
  projectCode?: string
  projectName: string
  projectType: string
  status?: typeof PROJECT_STATUS_ENUM[number]
  customerName: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  actualStartDate?: string
  actualEndDate?: string
  projectManagerId?: number
  projectManagerName?: string
  description?: string
  progress?: number
  priority?: string
  processInstanceId?: string
  parentProjectId?: number
  projectPath?: string
  depth?: number
  weight?: number
  templateId?: number
  templateVersion?: string
  currentPhaseId?: number
  projectObjective?: string
  projectScope?: string
}

// ============ 注册到全局 registry ============
//
// 注册后，request.ts 拦截器会自动调用 projectRequestValidator 校验以下 URL：
// - POST   /api/project              （创建项目）
// - PUT    /api/project              （更新项目）
// - POST   /api/project/{id}/subproject （创建子项目）
//
// 校验失败 → ElMessage.error + 阻止请求发送（避免触发后端 400）
registerValidator({
  method: 'POST',
  urlPattern: '/api/project',
  requestValidator: projectRequestValidator,
  description: '创建项目 - 校验请求体字段名与必填'
})

registerValidator({
  method: 'PUT',
  urlPattern: '/api/project',
  requestValidator: projectRequestValidator,
  description: '更新项目 - 校验请求体字段名'
})

registerValidator({
  method: 'POST',
  urlPattern: /^\/api\/project\/\d+\/subproject$/,
  requestValidator: projectRequestValidator,
  description: '创建子项目 - 校验请求体字段名'
})
