import request from '@/utils/request'

export function listSupervisions(params) { return request.get('/api/supervision/list', { params }) }
export function getSupervision(id) { return request.get(`/api/supervision/${id}`) }
export function addSupervision(data) { return request.post('/api/supervision', data) }
export function updateSupervision(data) { return request.put('/api/supervision', data) }
export function rectifySupervision(id, data) { return request.post(`/api/supervision/${id}/rectify`, data) }
export function getSupervisionPowerUsers() { return request.get('/api/supervision/power-users') }
