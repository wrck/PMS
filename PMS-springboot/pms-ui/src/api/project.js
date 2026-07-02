import request from '@/utils/request'

export function listProjects(params) { return request.get('/api/project/list', { params }) }
export function getProject(id) { return request.get(`/api/project/${id}`) }
export function addProject(data) { return request.post('/api/project', data) }
export function updateProject(data) { return request.put('/api/project', data) }
export function deleteProject(id) { return request.delete(`/api/project/${id}`) }
export function getProjectMembers(id) { return request.get(`/api/project/${id}/members`) }
export function addProjectMember(data) { return request.post('/api/project/member', data) }
export function deleteProjectMember(id) { return request.delete(`/api/project/member/${id}`) }
export function getProjectWeekly(id) { return request.get(`/api/project/${id}/weekly`) }
export function createWeekly(data) { return request.post('/api/project/weekly', data) }
export function submitWeekly(id) { return request.post(`/api/project/weekly/${id}/submit`) }
export function getProjectFiles(id) { return request.get(`/api/project/${id}/files`) }
export function uploadFile(data) { return request.post('/api/file/upload', data) }
export function deleteFile(id) { return request.delete(`/api/file/${id}`) }
export function downloadFile(id) { return request.get(`/api/file/${id}/download`, { responseType: 'blob' }) }
export function getProjectPlan(id) { return request.get(`/api/project/${id}/plan`) }
export function editPlan(data) { return request.put('/api/project/plan', data) }
export function getOrderData(id) { return request.get(`/api/project/${id}/order`) }
export function getShipmentInfo(id) { return request.get(`/api/project/${id}/shipment`) }
export function getSoftVersion(id) { return request.get(`/api/project/${id}/soft-version`) }
export function updateSoftVersion(data) { return request.put('/api/project/soft-version', data) }
export function backToLastStep(data) { return request.post('/api/project/back-to-last-step', data) }
export function batchImport(formData) { return request.post('/api/project/batch-import', formData) }
export function transferShipment(data) { return request.post('/api/project/transfer-shipment', data) }
