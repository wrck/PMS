/**
 * yudao 字典工具适配层。
 *
 * <p>对接 yudao 后端 {@code /admin-api/system/dict-data/simple-list} 接口，
 * 提供 yudao 原生页面所需的 {@code getDictOptions / getIntDictOptions /
 * getStrDictOptions / getBoolDictOptions / getDictLabel / getDictObj} 等工具，
 * 以及集中管理字典 type 字符串的 {@code DICT_TYPE} 枚举。</p>
 *
 * <p>字典数据在首次调用时从后端拉取并缓存到模块级 Map，避免重复请求。</p>
 */
import { get } from '@/utils/request'

/** yudao 字典数据项 */
export interface DictDataVO {
  dictType: string
  value: number | string | boolean
  label: string
  colorType: string
  cssClass: string
}

/** yudao 简单字典响应项 */
interface SimpleDictDataVO {
  dictType: string
  value: number | string | boolean
  label: string
  colorType: string
  cssClass: string
}

/** 字典缓存：dictType → DictDataVO[] */
const dictCache = new Map<string, DictDataVO[]>()

/** 加载中的 Promise：防止并发重复请求 */
const loadingPromises = new Map<string, Promise<DictDataVO[]>>()

/**
 * 拉取指定字典类型的字典数据（带缓存）。
 *
 * @param dictType 字典类型
 * @returns 字典数据数组
 */
export async function loadDict(dictType: string): Promise<DictDataVO[]> {
  if (dictCache.has(dictType)) {
    return dictCache.get(dictType)!
  }
  if (loadingPromises.has(dictType)) {
    return loadingPromises.get(dictType)!
  }
  const promise = get<SimpleDictDataVO[]>('/admin-api/system/dict-data/simple-list', {
    dictType
  })
    .then((list) => {
      const data: DictDataVO[] = (list || []).map((item) => ({
        dictType: item.dictType,
        value: item.value,
        label: item.label,
        colorType: item.colorType,
        cssClass: item.cssClass
      }))
      dictCache.set(dictType, data)
      loadingPromises.delete(dictType)
      return data
    })
    .catch((err) => {
      loadingPromises.delete(dictType)
      console.warn(`[dict] 加载字典 ${dictType} 失败:`, err)
      return []
    })
  loadingPromises.set(dictType, promise)
  return promise
}

/**
 * 预加载多个字典类型（在应用启动或页面 mount 时调用）。
 *
 * @param dictTypes 字典类型数组
 */
export async function preloadDicts(dictTypes: string[]): Promise<void> {
  await Promise.all(dictTypes.map(loadDict))
}

/** 字典选项（通用） */
export interface DictOption {
  label: string
  value: number | string | boolean
  raw: DictDataVO
}

/**
 * 获取字典选项列表（通用，value 保持原始类型）。
 *
 * <p>同步读取缓存，若未加载则返回空数组（需先调 {@link loadDict} 预加载）。</p>
 */
export function getDictOptions(dictType: string): DictOption[] {
  const list = dictCache.get(dictType) || []
  return list.map((item) => ({
    label: item.label,
    value: item.value,
    raw: item
  }))
}

/** 获取 int 类型字典选项（value 强制为 number） */
export function getIntDictOptions(dictType: string): DictOption[] {
  const list = dictCache.get(dictType) || []
  return list.map((item) => ({
    label: item.label,
    value: Number(item.value),
    raw: item
  }))
}

/** 获取 string 类型字典选项（value 强制为 string） */
export function getStrDictOptions(dictType: string): DictOption[] {
  const list = dictCache.get(dictType) || []
  return list.map((item) => ({
    label: item.label,
    value: String(item.value),
    raw: item
  }))
}

/** 获取 boolean 类型字典选项（value 强制为 boolean） */
export function getBoolDictOptions(dictType: string): DictOption[] {
  const list = dictCache.get(dictType) || []
  return list.map((item) => ({
    label: item.label,
    value: Boolean(item.value),
    raw: item
  }))
}

/**
 * 根据 type + value 获取字典对象（含 colorType / cssClass，用于 DictTag 渲染）。
 */
export function getDictObj(
  dictType: string,
  value: number | string | boolean
): DictDataVO | undefined {
  const list = dictCache.get(dictType) || []
  return list.find((item) => String(item.value) === String(value))
}

/**
 * 根据 type + value 获取字典标签文本。
 */
export function getDictLabel(
  dictType: string,
  value: number | string | boolean
): string {
  const obj = getDictObj(dictType, value)
  return obj?.label || ''
}

/**
 * yudao 字典类型枚举（仅包含本次集成用到的）。
 *
 * <p>完整列表参考 yudao {@code utils/dict.ts} 的 {@code DICT_TYPE}。
 * 本项目按需补充，避免冗余。</p>
 */
export const DICT_TYPE = {
  // 通用
  COMMON_STATUS: 'common_status',
  USER_TYPE: 'user_type',
  INFRA_BOOLEAN_STRING: 'infra_boolean_string',

  // 系统管理
  SYSTEM_MAIL_SEND_STATUS: 'system_mail_send_status',
  SYSTEM_SMS_CHANNEL_CODE: 'system_sms_channel_code',
  SYSTEM_SMS_TEMPLATE_TYPE: 'system_sms_template_type',
  SYSTEM_SMS_SEND_STATUS: 'system_sms_send_status',
  SYSTEM_SMS_RECEIVE_STATUS: 'system_sms_receive_status',
  SYSTEM_OAUTH2_GRANT_TYPE: 'system_oauth2_grant_type',
  SYSTEM_NOTIFY_TEMPLATE_TYPE: 'system_notify_template_type',
  SYSTEM_SOCIAL_TYPE: 'system_social_type',

  // 基础设施
  INFRA_FILE_STORAGE: 'infra_file_storage',
  INFRA_CODEGEN_TEMPLATE_TYPE: 'infra_codegen_template_type',
  INFRA_CODEGEN_FRONT_TYPE: 'infra_codegen_front_type',
  INFRA_CODEGEN_SCENE: 'infra_codegen_scene',
  INFRA_API_ERROR_LOG_PROCESS_STATUS: 'infra_api_error_log_process_status',

  // 工作流
  BPM_MODEL_TYPE: 'bpm_model_type',
  BPM_MODEL_FORM_TYPE: 'bpm_model_form_type',
  BPM_PROCESS_LISTENER_TYPE: 'bpm_process_listener_type',
  BPM_PROCESS_LISTENER_VALUE_TYPE: 'bpm_process_listener_value_type',
  BPM_TASK_STATUS: 'bpm_task_status',
  BPM_PROCESS_INSTANCE_STATUS: 'bpm_process_instance_status',
  BPM_OA_LEAVE_TYPE: 'bpm_oa_leave_type'
} as const

/** 列出当前已缓存的字典类型（调试用） */
export function listCachedDictTypes(): string[] {
  return Array.from(dictCache.keys())
}
