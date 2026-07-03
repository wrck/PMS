import request from '@/utils/request'

export function listMaintenance(params) { return request.get('/api/maintenance/list', { params }) }
export function getMaintenance(id) { return request.get(`/api/maintenance/${id}`) }
export function addMaintenance(data) { return request.post('/api/maintenance', data) }
export function updateMaintenance(data) { return request.put('/api/maintenance', data) }
export function deleteMaintenance(id) { return request.delete(`/api/maintenance/${id}`) }
export function getMaintenanceDelivery(id) { return request.get(`/api/maintenance/${id}/delivery`) }
export function getMaintenanceDelivers(id) { return request.get(`/api/maintenance/${id}/delivery`) }
export function uploadMaintenanceFile(id, data) { return request.post(`/api/maintenance/${id}/upload`, data) }
export function getMaintenanceFiles(id) { return request.get(`/api/maintenance/${id}/files`) }
export function getDailyReport(params) { return request.get('/api/maintenance/daily-report', { params }) }
export function getQuarterReport(params) { return request.get('/api/maintenance/quarter-report', { params }) }
export function getMaintenanceDailyReport(params) { return request.get('/api/maintenance/daily-report', { params }) }
export function getMaintenanceQuestionnaire(id) { return request.get(`/api/maintenance/${id}/questionnaire`) }
