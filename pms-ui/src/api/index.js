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
