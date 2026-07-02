import request from '@/utils/request'

export function listPresales(params) { return request.get('/api/presales/list', { params }) }
export function getPresales(id) { return request.get(`/api/presales/${id}`) }
export function applyPresales(data) { return request.post('/api/presales/apply', data) }
export function auditPresales(data) { return request.post('/api/presales/audit', data) }
export function terminatePresales(id) { return request.post(`/api/presales/${id}/terminate`) }
export function exportPresales(params) { return request.get('/api/presales/export', { params, responseType: 'blob' }) }
export function getPresalesShipment(id) { return request.get(`/api/presales/${id}/shipment`) }
export function getLend2Sale(id) { return request.get(`/api/presales/${id}/lend2sale`) }
export function getLend2Rma(id) { return request.get(`/api/presales/${id}/lend2rma`) }
export function getTempAuth(id) { return request.get(`/api/presales/${id}/temp-auth`) }
export function syncOaData() { return request.post('/api/presales/sync-oa') }
