import { del, get, post, put } from '@/utils/request'

/** 帮助内容分类：QUICK_START / FAQ / VIDEO / ADVANCED */
export type HelpCategory = 'QUICK_START' | 'FAQ' | 'VIDEO' | 'ADVANCED'

/** 帮助内容实体（与后端 com.dp.plat.system.entity.HelpContent 对应） */
export interface HelpContent {
  id?: number
  category: HelpCategory
  title: string
  /** 富文本内容（Markdown） */
  content: string
  sortOrder?: number
  /** 状态：0=启用，1=禁用 */
  status?: string
  viewCount?: number
  createTime?: string
  updateTime?: string
}

/** 公开获取启用的帮助内容列表（可按分类过滤） */
export function listHelpContents(category?: HelpCategory): Promise<HelpContent[]> {
  return get<HelpContent[]>('/api/system/help-content/list', category ? { category } : undefined)
}

/** 公开获取帮助内容详情 */
export function getHelpContent(id: number): Promise<HelpContent> {
  return get<HelpContent>(`/api/system/help-content/${id}`)
}

/** 获取所有帮助内容分类 */
export function listHelpCategories(): Promise<HelpCategory[]> {
  return get<HelpCategory[]>('/api/system/help-content/categories')
}

/** 新建帮助内容（管理员） */
export function createHelpContent(data: HelpContent): Promise<boolean> {
  return post<boolean>('/api/system/help-content', data)
}

/** 更新帮助内容（管理员） */
export function updateHelpContent(data: HelpContent): Promise<boolean> {
  return put<boolean>('/api/system/help-content', data)
}

/** 删除帮助内容（管理员） */
export function deleteHelpContent(id: number): Promise<boolean> {
  return del<boolean>(`/api/system/help-content/${id}`)
}
