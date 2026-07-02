import request from '@/utils/request'

export function listUsers(params) { return request.get('/api/system/user/list', { params }) }
export function getUser(id) { return request.get(`/api/system/user/${id}`) }
export function addUser(data) { return request.post('/api/system/user', data) }
export function updateUser(data) { return request.put('/api/system/user', data) }
export function deleteUser(id) { return request.delete(`/api/system/user/${id}`) }
export function resetPassword(userId) { return request.post('/api/system/user/reset-password', { userId }) }
export function listRoles(params) { return request.get('/api/system/role/list', { params }) }
export function addRole(data) { return request.post('/api/system/role', data) }
export function updateRole(data) { return request.put('/api/system/role', data) }
export function listDepts(params) { return request.get('/api/system/dept/list', { params }) }
export function addDept(data) { return request.post('/api/system/dept', data) }
export function updateDept(data) { return request.put('/api/system/dept', data) }
export function listBasicData(params) { return request.get('/api/system/basic-data/list', { params }) }
export function addBasicData(data) { return request.post('/api/system/basic-data', data) }
export function updateBasicData(data) { return request.put('/api/system/basic-data', data) }
export function listOperateLogs(params) { return request.get('/api/system/operate-log/list', { params }) }
export function exportOperateLogs(params) { return request.get('/api/system/operate-log/export', { params, responseType: 'blob' }) }
export function listMenus(params) { return request.get('/api/system/menu/list', { params }) }
export function addMenu(data) { return request.post('/api/system/menu', data) }
export function updateMenu(data) { return request.put('/api/system/menu', data) }
export function listNotifications(params) { return request.get('/api/notification/list', { params }) }
export function updateNotificationState(id) { return request.put(`/api/notification/${id}/read`) }
export function refreshCache() { return request.post('/api/system/refresh-cache') }
