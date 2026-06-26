import request from '@/utils/request'

export function login(data) {
  return request({ url: '/api/auth/login', method: 'post', data })
}

export function logout() {
  return request({ url: '/api/auth/logout', method: 'post' })
}

export function getUserInfo() {
  return request({ url: '/api/auth/info', method: 'get' })
}

export function getUserList(params) {
  return request({ url: '/api/system/user/list', method: 'get', params })
}

export function addUser(data) {
  return request({ url: '/api/system/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/api/system/user', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/api/system/user/${id}`, method: 'delete' })
}

export function resetPassword(userId) {
  return request({ url: '/api/system/user/reset-password', method: 'post', params: { userId } })
}

export function getRoleList(params) {
  return request({ url: '/api/system/role/list', method: 'get', params })
}

export function addRole(data) {
  return request({ url: '/api/system/role', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/api/system/role', method: 'put', data })
}

export function deleteRole(id) {
  return request({ url: `/api/system/role/${id}`, method: 'delete' })
}

export function getDeptList() {
  return request({ url: '/api/system/dept/list', method: 'get' })
}

export function addDept(data) {
  return request({ url: '/api/system/dept', method: 'post', data })
}

export function updateDept(data) {
  return request({ url: '/api/system/dept', method: 'put', data })
}

export function deleteDept(id) {
  return request({ url: `/api/system/dept/${id}`, method: 'delete' })
}

export function getProjectList(params) {
  return request({ url: '/api/project/list', method: 'get', params })
}

export function addProject(data) {
  return request({ url: '/api/project', method: 'post', data })
}

export function updateProject(data) {
  return request({ url: '/api/project', method: 'put', data })
}

export function getProjectDetail(id) {
  return request({ url: `/api/project/${id}`, method: 'get' })
}

export function getProjectMembers(id) {
  return request({ url: `/api/project/${id}/members`, method: 'get' })
}

export function addProjectMember(data) {
  return request({ url: '/api/project/member', method: 'post', data })
}

// ===== 售前管理 =====
export function getPresalesList(params) {
  return request({ url: '/api/presales/list', method: 'get', params })
}
export function getPresalesDetail(id) {
  return request({ url: `/api/presales/${id}`, method: 'get' })
}
export function addPresales(data) {
  return request({ url: '/api/presales', method: 'post', data })
}
export function updatePresales(data) {
  return request({ url: '/api/presales', method: 'put', data })
}
export function deletePresales(id) {
  return request({ url: `/api/presales/${id}`, method: 'delete' })
}
export function startPresalesFlow(id) {
  return request({ url: `/api/presales/${id}/start-flow`, method: 'post' })
}
export function approvePresales(id, comment, approved) {
  return request({ url: `/api/presales/${id}/approve`, method: 'post', params: { comment, approved } })
}

// ===== 基础数据 =====
export function getBasicDataList(dataType) {
  return request({ url: '/api/system/basic-data/list', method: 'get', params: { dataType } })
}
export function addBasicData(data) {
  return request({ url: '/api/system/basic-data', method: 'post', data })
}
export function updateBasicData(data) {
  return request({ url: '/api/system/basic-data', method: 'put', data })
}
export function deleteBasicData(id) {
  return request({ url: `/api/system/basic-data/${id}`, method: 'delete' })
}

// ===== 操作日志 =====
export function getOperateLogList(params) {
  return request({ url: '/api/system/operate-log/list', method: 'get', params })
}

// ===== 文件上传 =====
export function uploadFile(file, module) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('module', module || 'common')
  return request({ url: '/api/file/upload', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
export function deleteFile(id) {
  return request({ url: `/api/file/${id}`, method: 'delete' })
}

// ===== 通知 =====
export function getNotifications(params) {
  return request({ url: '/api/notification/list', method: 'get', params })
}
export function getUnreadCount() {
  return request({ url: '/api/notification/unread-count', method: 'get' })
}
export function markNotificationRead(id) {
  return request({ url: `/api/notification/${id}/read`, method: 'post' })
}
