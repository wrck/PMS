import request from '@/utils/request'

export function listProbs(params) { return request.get('/api/prob/list', { params }) }
export function getProb(id) { return request.get(`/api/prob/${id}`) }
export function addProb(data) { return request.post('/api/prob', data) }
export function createProb(data) { return request.post('/api/prob', data) }
export function updateProb(data) { return request.put('/api/prob', data) }
export function deleteProb(id) { return request.delete(`/api/prob/${id}`) }
export function exportProbs(params) { return request.get('/api/prob/export', { params, responseType: 'blob' }) }
export function checkSoftVersion(params) { return request.get('/api/prob/check-soft-version', { params }) }
export function getProbSoftVersions(id) { return request.get(`/api/prob/${id}/soft-versions`) }
export function saveProbSoftVersions(id, versions) { return request.post(`/api/prob/${id}/soft-versions`, versions) }
export function getProbAffectedProjects(id) { return request.get(`/api/prob/affected-project-soft-version`, { params: { probId: id } }) }
export function getProbProducts(id) { return request.get(`/api/prob/${id}/products`) }
export function saveProbProduct(data) { return request.post('/api/prob/product', data) }
export function getProbReadLogs(id) { return request.get(`/api/prob/${id}/read-logs`) }
export function recordProbRead(id) { return request.post(`/api/prob/${id}/read`) }
export function auditProb(id, status) { return request.post(`/api/prob/${id}/audit`, null, { params: { status } }) }
export function releaseTask(data) { return request.post('/api/prob/release-task', data) }
export function createProbTask(data) { return request.post('/api/prob/release-task', data) }
export function getPrivateTasks(params) { return request.get('/api/prob/private-tasks', { params }) }
export function updatePrivateTask(data) { return request.put('/api/prob/private-tasks', data) }
export function updateProbTask(data) { return request.put('/api/prob/private-tasks', data) }
export function getAllTasks(params) { return request.get('/api/prob/all-tasks', { params }) }
export function updateAllTask(data) { return request.put('/api/prob/all-task', data) }
export function getProbTasks(params) { return request.get('/api/prob/private-tasks', { params }) }
export function getProbFiles(id) { return request.get(`/api/prob/${id}/files`) }
export function getProbStatistics(params) { return request.get('/api/prob/statistics', { params }) }
