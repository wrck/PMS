import request from '@/utils/request'

export function listProbs(params) { return request.get('/api/prob/list', { params }) }
export function getProb(id) { return request.get(`/api/prob/${id}`) }
export function addProb(data) { return request.post('/api/prob', data) }
export function updateProb(data) { return request.put('/api/prob', data) }
export function exportProbs(params) { return request.get('/api/prob/export', { params, responseType: 'blob' }) }
export function checkSoftVersion(params) { return request.get('/api/prob/check-soft-version', { params }) }
export function releaseTask(data) { return request.post('/api/prob/release-task', data) }
export function managePrivateTask(data) { return request.put('/api/prob/private-task', data) }
export function manageAllTask(data) { return request.put('/api/prob/all-task', data) }
export function weeklyUpload(data) { return request.post('/api/prob/weekly-upload', data) }
