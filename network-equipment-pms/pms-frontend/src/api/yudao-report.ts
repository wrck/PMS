import { del, get, post, put } from '@/utils/request'
import type { YudaoPageParam, YudaoPageResult } from './yudao-system'

/**
 * yudao 报表模块 API。
 *
 * <p>包含 GoView 数据大屏和 JimuReport 积木报表的 API。
 * JimuReport 主要通过 iframe 嵌入，无需前端 API 封装。</p>
 */

// ===================== GoView 数据大屏 =====================

/** GoView 项目状态：-1 未发布，1 已发布 */
export const GOVIEW_STATUS_UNPUBLISHED = -1
export const GOVIEW_STATUS_PUBLISHED = 1

/** GoView 项目响应 */
export interface GoViewProjectRespVO {
  id: number
  name: string
  status: number // -1 未发布 1 发布
  remark?: string
  creator: string
  createTime: string
  // 项目内容（JSON 字符串，包含画布、组件等配置）
  content?: string
  // 缩略图
  thumbnail?: string
}

export interface GoViewProjectSaveReqVO {
  id?: number
  name: string
  remark?: string
  content?: string
  thumbnail?: string
}

export interface GoViewProjectPageReqVO extends YudaoPageParam {
  name?: string
  status?: number
}

/** 分页查询 GoView 项目 */
export function getGoViewProjectPage(
  params: GoViewProjectPageReqVO
): Promise<YudaoPageResult<GoViewProjectRespVO>> {
  return get<YudaoPageResult<GoViewProjectRespVO>>('/admin-api/data-view/page', params)
}

/** 获取 GoView 项目详情 */
export function getGoViewProject(id: number): Promise<GoViewProjectRespVO> {
  return get<GoViewProjectRespVO>('/admin-api/data-view/get', { id })
}

/** 获取 GoView 项目内容数据（供大屏渲染） */
export function getGoViewProjectDataById(id: number): Promise<GoViewProjectRespVO> {
  return get<GoViewProjectRespVO>('/admin-api/data-view/get-data-by-id', { id })
}

/** 创建 GoView 项目 */
export function createGoViewProject(data: GoViewProjectSaveReqVO): Promise<number> {
  return post<number>('/admin-api/data-view/create', data)
}

/** 更新 GoView 项目 */
export function updateGoViewProject(data: GoViewProjectSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/data-view/update', data)
}

/** 删除 GoView 项目 */
export function deleteGoViewProject(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/data-view/delete', { id })
}

/** 发布 GoView 项目 */
export function publishGoViewProject(id: number): Promise<boolean> {
  return put<boolean>('/admin-api/data-view/publish', { id })
}

// ===================== JimuReport 积木报表 =====================
// JimuReport 通过 iframe 嵌入，定义入口 URL 常量

/** 积木报表 - 报表列表入口 */
export const JMREPORT_LIST_URL = '/jmreport/list'

/** 积木报表 - 报表查看入口（拼接报表 id：`${JMREPORT_VIEW_URL}${id}`） */
export const JMREPORT_VIEW_URL = '/jmreport/view/'
