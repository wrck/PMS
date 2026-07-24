/**
 * yudao 兼容层：合并工具集
 *
 * <p>合并自 yudao-ui-reference 的以下文件：
 * - utils/dict.ts（字典工具）
 * - utils/formatTime.ts（时间格式化）
 * - utils/tree.ts（树工具）
 * - utils/constants.ts（常量枚举）
 * - utils/download.ts（下载工具）
 *
 * <p>适配要点：
 * - 去掉 useI18n 依赖，用中文常量替代
 * - DICT_TYPE 由 enum 改为 const 对象（受 erasableSyntaxOnly 约束）
 * - 字典数据暂时硬编码常用枚举，后续对接后端 dict API 时通过 setDictData 注入
 * - dayjs 已安装可直接使用</p>
 */
import dayjs from 'dayjs'

// ====================================================================
// 一、字典工具（dict.ts）
// ====================================================================

/** Element Plus Tag 的颜色类型 */
export type DictColorType = 'primary' | 'success' | 'info' | 'warning' | 'danger' | ''

/** 字典数据类型 */
export interface DictDataType {
  dictType: string
  label: string
  value: string | number | boolean
  colorType: DictColorType
  cssClass: string
}

export interface NumberDictDataType extends DictDataType {
  value: number
}

export interface StringDictDataType extends DictDataType {
  value: string
}

export interface BooleanDictDataType extends DictDataType {
  value: boolean
}

/**
 * 字典数据缓存。
 *
 * <p>暂时硬编码常用枚举；后续对接后端 dict API 后，
 * 可通过 {@link setDictData} 动态注入覆盖。</p>
 */
const dictDataMap: Record<string, DictDataType[]> = {
  // 通用状态
  common_status: [
    { dictType: 'common_status', label: '开启', value: 0, colorType: 'success', cssClass: '' },
    { dictType: 'common_status', label: '禁用', value: 1, colorType: 'danger', cssClass: '' }
  ],
  // 用户类型
  user_type: [
    { dictType: 'user_type', label: '会员', value: 1, colorType: 'primary', cssClass: '' },
    { dictType: 'user_type', label: '管理员', value: 2, colorType: 'success', cssClass: '' }
  ],
  // 终端
  terminal: [
    { dictType: 'terminal', label: 'PC', value: 0, colorType: 'info', cssClass: '' },
    { dictType: 'terminal', label: 'App', value: 1, colorType: 'info', cssClass: '' }
  ],
  // ========== SYSTEM 模块 ==========
  system_user_sex: [
    { dictType: 'system_user_sex', label: '未知', value: 0, colorType: 'info', cssClass: '' },
    { dictType: 'system_user_sex', label: '男', value: 1, colorType: 'primary', cssClass: '' },
    { dictType: 'system_user_sex', label: '女', value: 2, colorType: 'danger', cssClass: '' }
  ],
  system_menu_type: [
    { dictType: 'system_menu_type', label: '目录', value: 1, colorType: 'info', cssClass: '' },
    { dictType: 'system_menu_type', label: '菜单', value: 2, colorType: 'success', cssClass: '' },
    { dictType: 'system_menu_type', label: '按钮', value: 3, colorType: 'warning', cssClass: '' }
  ],
  system_role_type: [
    { dictType: 'system_role_type', label: '内置角色', value: 1, colorType: 'danger', cssClass: '' },
    { dictType: 'system_role_type', label: '自定义角色', value: 2, colorType: 'primary', cssClass: '' }
  ],
  system_data_scope: [
    { dictType: 'system_data_scope', label: '全部数据权限', value: 1, colorType: 'danger', cssClass: '' },
    { dictType: 'system_data_scope', label: '指定部门数据权限', value: 2, colorType: 'primary', cssClass: '' },
    { dictType: 'system_data_scope', label: '本部门数据权限', value: 3, colorType: 'info', cssClass: '' },
    { dictType: 'system_data_scope', label: '本部门及以下数据权限', value: 4, colorType: 'success', cssClass: '' },
    { dictType: 'system_data_scope', label: '仅本人数据权限', value: 5, colorType: 'warning', cssClass: '' }
  ],
  system_notice_type: [
    { dictType: 'system_notice_type', label: '通知', value: 1, colorType: 'primary', cssClass: '' },
    { dictType: 'system_notice_type', label: '公告', value: 2, colorType: 'success', cssClass: '' }
  ],
  system_login_type: [
    { dictType: 'system_login_type', label: '账号登录', value: 100, colorType: 'primary', cssClass: '' },
    { dictType: 'system_login_type', label: '社交登录', value: 101, colorType: 'info', cssClass: '' }
  ],
  system_login_result: [
    { dictType: 'system_login_result', label: '成功', value: 0, colorType: 'success', cssClass: '' },
    { dictType: 'system_login_result', label: '账号或密码不正确', value: 10, colorType: 'danger', cssClass: '' },
    { dictType: 'system_login_result', label: '账号被停用', value: 20, colorType: 'warning', cssClass: '' }
  ],
  system_sms_send_status: [
    { dictType: 'system_sms_send_status', label: '初始化', value: 0, colorType: 'info', cssClass: '' },
    { dictType: 'system_sms_send_status', label: '发送成功', value: 10, colorType: 'success', cssClass: '' },
    { dictType: 'system_sms_send_status', label: '发送失败', value: 20, colorType: 'danger', cssClass: '' },
    { dictType: 'system_sms_send_status', label: '不发送', value: 30, colorType: 'warning', cssClass: '' }
  ],
  system_mail_send_status: [
    { dictType: 'system_mail_send_status', label: '初始化', value: 0, colorType: 'info', cssClass: '' },
    { dictType: 'system_mail_send_status', label: '发送成功', value: 10, colorType: 'success', cssClass: '' },
    { dictType: 'system_mail_send_status', label: '发送失败', value: 20, colorType: 'danger', cssClass: '' },
    { dictType: 'system_mail_send_status', label: '不发送', value: 30, colorType: 'warning', cssClass: '' }
  ],
  // ========== INFRA 模块 ==========
  infra_boolean_string: [
    { dictType: 'infra_boolean_string', label: '是', value: 'true', colorType: 'success', cssClass: '' },
    { dictType: 'infra_boolean_string', label: '否', value: 'false', colorType: 'danger', cssClass: '' }
  ],
  infra_job_status: [
    { dictType: 'infra_job_status', label: '初始化', value: 0, colorType: 'info', cssClass: '' },
    { dictType: 'infra_job_status', label: '开启', value: 1, colorType: 'success', cssClass: '' },
    { dictType: 'infra_job_status', label: '暂停', value: 2, colorType: 'danger', cssClass: '' }
  ],
  infra_job_log_status: [
    { dictType: 'infra_job_log_status', label: '成功', value: 0, colorType: 'success', cssClass: '' },
    { dictType: 'infra_job_log_status', label: '失败', value: 1, colorType: 'danger', cssClass: '' }
  ],
  infra_api_error_log_process_status: [
    { dictType: 'infra_api_error_log_process_status', label: '未处理', value: 0, colorType: 'info', cssClass: '' },
    { dictType: 'infra_api_error_log_process_status', label: '已处理', value: 1, colorType: 'success', cssClass: '' },
    { dictType: 'infra_api_error_log_process_status', label: '已忽略', value: 2, colorType: 'warning', cssClass: '' }
  ],
  infra_config_type: [
    { dictType: 'infra_config_type', label: '参数', value: 0, colorType: 'primary', cssClass: '' },
    { dictType: 'infra_config_type', label: '内置', value: 1, colorType: 'info', cssClass: '' }
  ]
}

/**
 * 动态注入字典数据（后续对接后端 dict API 后使用）。
 *
 * @param type 字典类型
 * @param data 字典数据数组
 */
export function setDictData(type: string, data: DictDataType[]): void {
  dictDataMap[type] = data
}

/** 获取 dictType 对应的数据字典数组 */
export const getDictOptions = (dictType: string): DictDataType[] => {
  return dictDataMap[dictType] || []
}

/** 获取 dictType 对应的 number 类型字典数组 */
export const getIntDictOptions = (dictType: string): NumberDictDataType[] => {
  const dictOptions = getDictOptions(dictType)
  return dictOptions.map((dict) => ({
    ...dict,
    value: parseInt(dict.value + '')
  }))
}

/** 获取 dictType 对应的 string 类型字典数组 */
export const getStrDictOptions = (dictType: string): StringDictDataType[] => {
  const dictOptions = getDictOptions(dictType)
  return dictOptions.map((dict) => ({
    ...dict,
    value: dict.value + ''
  }))
}

/** 获取 dictType 对应的 boolean 类型字典数组 */
export const getBoolDictOptions = (dictType: string): BooleanDictDataType[] => {
  const dictOptions = getDictOptions(dictType)
  return dictOptions.map((dict) => ({
    ...dict,
    value: dict.value + '' === 'true'
  }))
}

/** 获取指定字典类型的指定值对应的字典对象 */
export const getDictObj = (dictType: string, value: unknown): DictDataType | undefined => {
  const dictOptions = getDictOptions(dictType)
  for (const dict of dictOptions) {
    if (dict.value === value + '') {
      return dict
    }
  }
  return undefined
}

/** 获得字典数据的文本展示 */
export const getDictLabel = (dictType: string, value: unknown): string => {
  const dictOptions = getDictOptions(dictType)
  for (const dict of dictOptions) {
    if (dict.value === value + '') {
      return dict.label
    }
  }
  return ''
}

/** DICT_TYPE 枚举（由 enum 改为 const 对象，受 erasableSyntaxOnly 约束） */
export const DICT_TYPE = {
  USER_TYPE: 'user_type',
  COMMON_STATUS: 'common_status',
  TERMINAL: 'terminal',
  DATE_INTERVAL: 'date_interval',

  // SYSTEM 模块
  SYSTEM_USER_SEX: 'system_user_sex',
  SYSTEM_MENU_TYPE: 'system_menu_type',
  SYSTEM_ROLE_TYPE: 'system_role_type',
  SYSTEM_DATA_SCOPE: 'system_data_scope',
  SYSTEM_NOTICE_TYPE: 'system_notice_type',
  SYSTEM_LOGIN_TYPE: 'system_login_type',
  SYSTEM_LOGIN_RESULT: 'system_login_result',
  SYSTEM_SMS_CHANNEL_CODE: 'system_sms_channel_code',
  SYSTEM_SMS_TEMPLATE_TYPE: 'system_sms_template_type',
  SYSTEM_SMS_SEND_STATUS: 'system_sms_send_status',
  SYSTEM_SMS_RECEIVE_STATUS: 'system_sms_receive_status',
  SYSTEM_OAUTH2_GRANT_TYPE: 'system_oauth2_grant_type',
  SYSTEM_MAIL_SEND_STATUS: 'system_mail_send_status',
  SYSTEM_NOTIFY_TEMPLATE_TYPE: 'system_notify_template_type',
  SYSTEM_SOCIAL_TYPE: 'system_social_type',

  // INFRA 模块
  INFRA_BOOLEAN_STRING: 'infra_boolean_string',
  INFRA_JOB_STATUS: 'infra_job_status',
  INFRA_JOB_LOG_STATUS: 'infra_job_log_status',
  INFRA_API_ERROR_LOG_PROCESS_STATUS: 'infra_api_error_log_process_status',
  INFRA_CONFIG_TYPE: 'infra_config_type',
  INFRA_CODEGEN_TEMPLATE_TYPE: 'infra_codegen_template_type',
  INFRA_CODEGEN_FRONT_TYPE: 'infra_codegen_front_type',
  INFRA_CODEGEN_SCENE: 'infra_codegen_scene',
  INFRA_FILE_STORAGE: 'infra_file_storage',
  INFRA_OPERATE_TYPE: 'infra_operate_type',

  // BPM 模块
  BPM_MODEL_TYPE: 'bpm_model_type',
  BPM_MODEL_FORM_TYPE: 'bpm_model_form_type',
  BPM_TASK_CANDIDATE_STRATEGY: 'bpm_task_candidate_strategy',
  BPM_PROCESS_INSTANCE_STATUS: 'bpm_process_instance_status',
  BPM_TASK_STATUS: 'bpm_task_status',
  BPM_OA_LEAVE_TYPE: 'bpm_oa_leave_type',
  BPM_PROCESS_LISTENER_TYPE: 'bpm_process_listener_type',
  BPM_PROCESS_LISTENER_VALUE_TYPE: 'bpm_process_listener_value_type'
} as const

// ====================================================================
// 二、时间格式化（formatTime.ts）
// ====================================================================

/** 日期快捷选项适用于 el-date-picker */
export const defaultShortcuts = [
  { text: '今天', value: () => new Date() },
  {
    text: '昨天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      return [date, date]
    }
  },
  {
    text: '最近七天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
      return [date, new Date()]
    }
  },
  {
    text: '最近 30 天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 30)
      return [date, new Date()]
    }
  },
  {
    text: '本月',
    value: () => {
      const date = new Date()
      date.setDate(1)
      return [date, new Date()]
    }
  },
  {
    text: '今年',
    value: () => {
      const date = new Date()
      return [new Date(`${date.getFullYear()}-01-01`), date]
    }
  }
]

/**
 * 时间日期转换
 * @param date 当前时间，支持 new Date()、字符串、时间戳、dayjs 等格式
 * @param format 需要转换的时间格式字符串
 */
export function formatDate(date: dayjs.ConfigType, format?: string): string {
  if (!date) {
    return ''
  }
  return dayjs(date).format(format ?? 'YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化可为空的时间日期
 */
export function formatNullableDate(
  date?: Date | string | null,
  format = 'YYYY-MM-DD HH:mm:ss',
  emptyText = '-'
): string {
  if (!date) {
    return emptyText
  }
  return formatDate(date, format) || emptyText
}

/** 获取当前的日期+时间 */
export function getNowDateTime() {
  return dayjs()
}

/** 获取当前日期是第几周 */
export function getWeek(dateTime: Date): number {
  const temptTime = new Date(dateTime.getTime())
  const weekday = temptTime.getDay() || 7
  temptTime.setDate(temptTime.getDate() - weekday + 1 + 5)
  let firstDay = new Date(temptTime.getFullYear(), 0, 1)
  const dayOfWeek = firstDay.getDay()
  let spendDay = 1
  if (dayOfWeek != 0) spendDay = 7 - dayOfWeek + 1
  firstDay = new Date(temptTime.getFullYear(), 0, 1 + spendDay)
  const d = Math.ceil((temptTime.valueOf() - firstDay.valueOf()) / 86400000)
  return Math.ceil(d / 7)
}

/**
 * 将时间转换为 `几秒前`、`几分钟前`、`几小时前`、`几天前`
 */
export function formatPast(param: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
  let t: number
  let s: number
  let time: number = new Date().getTime()
  if (typeof param === 'string' || param instanceof Date) {
    t = new Date(param).getTime()
  } else {
    t = param as number
  }
  time = Number.parseInt(`${time - t}`)
  if (time < 10000) {
    return '刚刚'
  } else if (time < 60000 && time >= 10000) {
    s = Math.floor(time / 1000)
    return `${s}秒前`
  } else if (time < 3600000 && time >= 60000) {
    s = Math.floor(time / 60000)
    return `${s}分钟前`
  } else if (time < 86400000 && time >= 3600000) {
    s = Math.floor(time / 3600000)
    return `${s}小时前`
  } else if (time < 259200000 && time >= 86400000) {
    s = Math.floor(time / 86400000)
    return `${s}天前`
  } else {
    const date = typeof param === 'string' || 'object' ? new Date(param) : param
    return formatDate(date, format)
  }
}

/** 时间问候语 */
export function formatAxis(param: Date): string {
  const hour: number = new Date(param).getHours()
  if (hour < 6) return '凌晨好'
  else if (hour < 9) return '早上好'
  else if (hour < 12) return '上午好'
  else if (hour < 14) return '中午好'
  else if (hour < 17) return '下午好'
  else if (hour < 19) return '傍晚好'
  else if (hour < 22) return '晚上好'
  else return '夜里好'
}

/** 将毫秒，转换成时间字符串。例如说，xx 分钟 */
export function formatPast2(ms: number): string {
  const day = Math.floor(ms / (24 * 60 * 60 * 1000))
  const hour = Math.floor(ms / (60 * 60 * 1000) - day * 24)
  const minute = Math.floor(ms / (60 * 1000) - day * 24 * 60 - hour * 60)
  const second = Math.floor(ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60)
  if (day > 0) {
    return day + ' 天' + hour + ' 小时 ' + minute + ' 分钟'
  }
  if (hour > 0) {
    return hour + ' 小时 ' + minute + ' 分钟'
  }
  if (minute > 0) {
    return minute + ' 分钟'
  }
  if (second > 0) {
    return second + ' 秒'
  } else {
    return 0 + ' 秒'
  }
}

/** 将秒数格式化为 mm:ss */
export function formatSeconds(seconds: number): string {
  const s = Math.max(0, Math.floor(seconds || 0))
  const mm = Math.floor(s / 60)
    .toString()
    .padStart(2, '0')
  const ss = (s % 60).toString().padStart(2, '0')
  return `${mm}:${ss}`
}

/** element plus 的时间 Formatter 实现，使用 YYYY-MM-DD HH:mm:ss 格式 */
export function dateFormatter(_row: unknown, _column: unknown, cellValue: dayjs.ConfigType): string {
  return cellValue ? formatDate(cellValue) : ''
}

/** element plus 的时间 Formatter 实现，使用 YYYY-MM-DD 格式 */
export function dateFormatter2(_row: unknown, _column: unknown, cellValue: dayjs.ConfigType): string {
  return cellValue ? formatDate(cellValue, 'YYYY-MM-DD') : ''
}

/** 设置起始日期，时间为00:00:00 */
export function beginOfDay(param: Date): Date {
  return new Date(param.getFullYear(), param.getMonth(), param.getDate(), 0, 0, 0)
}

/** 设置结束日期，时间为23:59:59 */
export function endOfDay(param: Date): Date {
  return new Date(param.getFullYear(), param.getMonth(), param.getDate(), 23, 59, 59)
}

/** 日期转换：将 Date | string 转为 Date */
export function convertDate(param: Date | string): Date {
  if (typeof param === 'string') {
    return new Date(param)
  }
  return param
}

/** 计算两个日期间隔天数 */
export function betweenDay(param1: Date | string, param2: Date | string): number {
  const d1 = convertDate(param1)
  const d2 = convertDate(param2)
  // 计算差值
  return Math.floor((d2.getTime() - d1.getTime()) / (24 * 3600 * 1000))
}