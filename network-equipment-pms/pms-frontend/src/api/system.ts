import { del, get, post, put } from '@/utils/request'

/** Common pagination query parameters */
export interface PageQuery {
  page?: number
  size?: number
  keyword?: string
}

/** Common paginated result envelope */
export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

// ===================== User =====================

export interface SysUser {
  id?: number
  username: string
  nickname: string
  email?: string
  phone?: string
  deptId?: number
  deptName?: string
  status?: number
  roleIds?: number[]
  remark?: string
  createTime?: string
}

export interface UserPageQuery extends PageQuery {
  deptId?: number
  status?: number
}

export function getUserPage(params: UserPageQuery): Promise<PageResult<SysUser>> {
  return get<PageResult<SysUser>>('/api/system/users', params)
}

export function getUserById(id: number): Promise<SysUser> {
  return get<SysUser>(`/api/system/users/${id}`)
}

export function createUser(data: SysUser): Promise<SysUser> {
  return post<SysUser>('/api/system/users', data)
}

export function updateUser(data: SysUser): Promise<SysUser> {
  return put<SysUser>(`/api/system/users/${data.id}`, data)
}

export function deleteUser(id: number): Promise<void> {
  return del<void>(`/api/system/users/${id}`)
}

export function resetUserPassword(id: number, password: string): Promise<void> {
  return put<void>(`/api/system/users/${id}/password`, { password })
}

// ===================== Role =====================

export interface SysRole {
  id?: number
  name: string
  code: string
  sort?: number
  status?: number
  menuIds?: number[]
  dataScope?: number
  remark?: string
  createTime?: string
}

export function getRolePage(params: PageQuery): Promise<PageResult<SysRole>> {
  return get<PageResult<SysRole>>('/api/system/roles', params)
}

export function getRoleAll(): Promise<SysRole[]> {
  return get<SysRole[]>('/api/system/roles/all')
}

export function createRole(data: SysRole): Promise<SysRole> {
  return post<SysRole>('/api/system/roles', data)
}

export function updateRole(data: SysRole): Promise<SysRole> {
  return put<SysRole>(`/api/system/roles/${data.id}`, data)
}

export function deleteRole(id: number): Promise<void> {
  return del<void>(`/api/system/roles/${id}`)
}

// ===================== Menu =====================

export interface SysMenu {
  id?: number
  parentId: number
  name: string
  path?: string
  component?: string
  icon?: string
  type: number // 0=directory 1=menu 2=button 3=lowcode page
  permission?: string
  sort?: number
  visible?: number
  status?: number
  /** 低代码页面类型（type=3 时使用）：form / list / tab / related-page */
  pageType?: string
  /** 低代码配置编码（type=3 时使用） */
  pageCode?: string
  children?: SysMenu[]
  createTime?: string
}

export function getMenuTree(): Promise<SysMenu[]> {
  return get<SysMenu[]>('/api/system/menus/tree')
}

export function createMenu(data: SysMenu): Promise<SysMenu> {
  return post<SysMenu>('/api/system/menus', data)
}

export function updateMenu(data: SysMenu): Promise<SysMenu> {
  return put<SysMenu>(`/api/system/menus/${data.id}`, data)
}

export function deleteMenu(id: number): Promise<void> {
  return del<void>(`/api/system/menus/${id}`)
}

// ===================== Dict =====================

export interface SysDict {
  id?: number
  code: string
  name: string
  status?: number
  remark?: string
  createTime?: string
}

export interface SysDictItem {
  id?: number
  dictId: number
  label: string
  value: string
  sort?: number
  status?: number
  remark?: string
}

export function getDictPage(params: PageQuery): Promise<PageResult<SysDict>> {
  return get<PageResult<SysDict>>('/api/system/dicts', params)
}

export function createDict(data: SysDict): Promise<SysDict> {
  return post<SysDict>('/api/system/dicts', data)
}

export function updateDict(data: SysDict): Promise<SysDict> {
  return put<SysDict>(`/api/system/dicts/${data.id}`, data)
}

export function deleteDict(id: number): Promise<void> {
  return del<void>(`/api/system/dicts/${id}`)
}

export function getDictItems(dictId: number): Promise<SysDictItem[]> {
  return get<SysDictItem[]>(`/api/system/dicts/${dictId}/items`)
}

export function createDictItem(data: SysDictItem): Promise<SysDictItem> {
  return post<SysDictItem>('/api/system/dicts/items', data)
}

export function updateDictItem(data: SysDictItem): Promise<SysDictItem> {
  return put<SysDictItem>(`/api/system/dicts/items/${data.id}`, data)
}

export function deleteDictItem(id: number): Promise<void> {
  return del<void>(`/api/system/dicts/items/${id}`)
}
