import request from '@/utils/request'

export function listWeeklys(params) { return request.get('/api/weekly/list', { params }) }
export function getWeekly(id) { return request.get(`/api/weekly/${id}`) }
export function createWeekly(data) { return request.post('/api/weekly', data) }
export function saveWeekly(data) { return request.put('/api/weekly', data) }
export function submitWeekly(id) { return request.post(`/api/weekly/${id}/submit`) }
export function feedbackWeekly(data) { return request.post('/api/weekly/feedback', data) }
