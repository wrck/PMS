import request from '@/utils/request'

export function listMaintenance(params) { return request.get('/api/maintenance/list', { params }) }
export function getMaintenance(id) { return request.get(`/api/maintenance/${id}`) }
export function addMaintenance(data) { return request.post('/api/maintenance', data) }
export function updateMaintenance(data) { return request.put('/api/maintenance', data) }
export function getDailyReport(params) { return request.get('/api/maintenance/daily-report', { params }) }
export function getQuarterReport(params) { return request.get('/api/maintenance/quarter-report', { params }) }
