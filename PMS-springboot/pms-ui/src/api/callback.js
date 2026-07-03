import request from '@/utils/request'

export function listCallbacks(params) { return request.get('/api/callback/list', { params }) }
export function getCallback(id) { return request.get(`/api/callback/${id}`) }
export function applyCallback(data) { return request.post('/api/callback/apply', data) }
export function resubmitCallback(id, data) { return request.post(`/api/callback/${id}/resubmit`, data) }
export function auditCallback(id, comment, approved) { return request.post(`/api/callback/${id}/approve`, null, { params: { comment, approved } }) }
export function saveQuestionnaire(id, data) { return request.post(`/api/callback/${id}/questionnaire/save`, data) }
export function getQuesnaire(id) { return request.get(`/api/callback/${id}/questionnaire`) }
export function getQuestionnaire(id) { return request.get(`/api/callback/${id}/questionnaire`) }
export function saveQuestionnaire(id, data) { return request.post(`/api/callback/${id}/questionnaire/save`, data) }
export function getCallbackHistory(id) { return request.get(`/api/callback/${id}/history`) }
export function resubmitCallbackFlow(id, data) { return request.post(`/api/callback/${id}/resubmit/flow`, data) }
export function getResubmitForm(id) { return request.get(`/api/callback/${id}/resubmit/form`) }
export function getAuditForm(id) { return request.get(`/api/callback/${id}/audit/form`) }
export function getQuesnaireDetail(quesnaireId) { return request.get(`/api/callback/questionnaire/${quesnaireId}`) }
export function getQuesnaireTemplates() { return request.get('/api/callback/questionnaire/templates') }
