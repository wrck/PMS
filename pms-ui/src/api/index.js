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

// ===== 回访管理 =====
export function getCallBackList(params) {
  return request({ url: '/api/callback/list', method: 'get', params })
}
export function getCallBackDetail(id) {
  return request({ url: `/api/callback/${id}`, method: 'get' })
}
export function addCallBack(data) {
  return request({ url: '/api/callback', method: 'post', data })
}
export function startCallBackFlow(id) {
  return request({ url: `/api/callback/${id}/start-flow`, method: 'post' })
}
export function approveCallBack(id, comment, approved) {
  return request({ url: `/api/callback/${id}/approve`, method: 'post', params: { comment, approved } })
}

// ===== 项目闭环 =====
export function getClosedLoopList(params) {
  return request({ url: '/api/closed-loop/list', method: 'get', params })
}
export function getClosedLoopDetail(id) {
  return request({ url: `/api/closed-loop/${id}`, method: 'get' })
}
export function applyClosedLoop(data) {
  return request({ url: '/api/closed-loop', method: 'post', data })
}
export function approveClosedLoop(id, comment, approved, role) {
  return request({ url: `/api/closed-loop/${id}/approve`, method: 'post', params: { comment, approved, role } })
}

// ===== 转包管理 =====
export function getSubcontractList(params) {
  return request({ url: '/api/subcontract/list', method: 'get', params })
}
export function getSubcontractDetail(id) {
  return request({ url: `/api/subcontract/${id}`, method: 'get' })
}
export function addSubcontract(data) {
  return request({ url: '/api/subcontract', method: 'post', data })
}
export function updateSubcontract(data) {
  return request({ url: '/api/subcontract', method: 'put', data })
}
export function deleteSubcontract(id) {
  return request({ url: `/api/subcontract/${id}`, method: 'delete' })
}

// ===== 技术公告 =====
export function getProbList(params) {
  return request({ url: '/api/prob/list', method: 'get', params })
}
export function getProbDetail(id) {
  return request({ url: `/api/prob/${id}`, method: 'get' })
}
export function addProb(data) {
  return request({ url: '/api/prob', method: 'post', data })
}
export function updateProb(data) {
  return request({ url: '/api/prob', method: 'put', data })
}
export function deleteProb(id) {
  return request({ url: `/api/prob/${id}`, method: 'delete' })
}

// ===== 维保管理 =====
export function getMaintenanceList(params) {
  return request({ url: '/api/maintenance/list', method: 'get', params })
}
export function getMaintenanceDetail(id) {
  return request({ url: `/api/maintenance/${id}`, method: 'get' })
}
export function addMaintenance(data) {
  return request({ url: '/api/maintenance', method: 'post', data })
}
export function updateMaintenance(data) {
  return request({ url: '/api/maintenance', method: 'put', data })
}
export function deleteMaintenance(id) {
  return request({ url: `/api/maintenance/${id}`, method: 'delete' })
}

// ===== 督查管理 =====
export function getSupervisionList(params) {
  return request({ url: '/api/supervision/list', method: 'get', params })
}
export function getSupervisionDetail(id) {
  return request({ url: `/api/supervision/${id}`, method: 'get' })
}
export function addSupervision(data) {
  return request({ url: '/api/supervision', method: 'post', data })
}
export function updateSupervision(data) {
  return request({ url: '/api/supervision', method: 'put', data })
}
export function deleteSupervision(id) {
  return request({ url: `/api/supervision/${id}`, method: 'delete' })
}

// ===== 合格证 =====
export function getCertificateList(params) {
  return request({ url: '/api/certificate/list', method: 'get', params })
}
export function getCertificateByBarcode(barcode) {
  return request({ url: `/api/certificate/barcode/${barcode}`, method: 'get' })
}
export function addCertificate(data) {
  return request({ url: '/api/certificate', method: 'post', data })
}
export function deleteCertificate(id) {
  return request({ url: `/api/certificate/${id}`, method: 'delete' })
}

// ===== 工作台 =====
export function getDashboardData() {
  return request({ url: '/api/workspace/dashboard', method: 'get' })
}
export function getPendingTasks() {
  return request({ url: '/api/workspace/pending-tasks', method: 'get' })
}
export function getRecentNotifications() {
  return request({ url: '/api/workspace/notifications', method: 'get' })
}
