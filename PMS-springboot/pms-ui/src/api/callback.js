import request from '@/utils/request'

export function listCallbacks(params) { return request.get('/api/callback/list', { params }) }
export function getCallback(id) { return request.get(`/api/callback/${id}`) }
export function applyCallback(data) { return request.post('/api/callback/apply', data) }
export function resubmitCallback(data) { return request.post('/api/callback/resubmit', data) }
export function auditCallback(data) { return request.post('/api/callback/audit', data) }
export function getQuesnaire(id) { return request.get(`/api/callback/${id}/quesnaire`) }
