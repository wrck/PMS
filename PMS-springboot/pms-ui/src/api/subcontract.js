import request from '@/utils/request'

export function listSubcontracts(params) { return request.get('/api/subcontract/list', { params }) }
export function getSubcontract(id) { return request.get(`/api/subcontract/${id}`) }
export function createSubcontract(data) { return request.post('/api/subcontract', data) }
export function startSubcontractFlow(id) { return request.post(`/api/subcontract/${id}/start-flow`) }
export function auditSubcontract(id, comment, approved) { return request.post(`/api/subcontract/${id}/approve`, null, { params: { comment, approved } }) }
export function closeSubcontract(id) { return request.post(`/api/subcontract/${id}/close`) }
export function addSubcontractCallback(id, data) { return request.post(`/api/subcontract/${id}/callback`, data) }
export function chooseShipment(projectId) { return request.get(`/api/subcontract/choose-shipment/${projectId}`) }
export function getSubcontractPayment(id) { return request.get(`/api/subcontract/${id}/payment`) }
export function getSubcontractDeliver(id) { return request.get(`/api/subcontract/${id}/deliver`) }
export function getSubcontractCallback(id) { return request.get(`/api/subcontract/${id}/callback`) }
export function getSubcontractComment(id) { return request.get(`/api/subcontract/${id}/comment`) }
export function downloadSubcontractFile(id) { return request.get(`/api/subcontract/file/${id}`, { responseType: 'blob' }) }
export function exportSubcontracts(params) { return request.get('/api/subcontract/export', { params, responseType: 'blob' }) }
export function getFacilitators(params) { return request.get('/api/subcontract/facilitators', { params }) }
export function getSubcontractLines(id) { return request.get(`/api/subcontract/${id}/lines`) }
export function getSubcontractPayments(id) { return request.get(`/api/subcontract/${id}/payments`) }
export function getSubcontractDelivers(id) { return request.get(`/api/subcontract/${id}/delivers`) }
export function getSubcontractCallbacks(id) { return request.get(`/api/subcontract/${id}/callbacks`) }
export function getSubcontractComments(id) { return request.get(`/api/subcontract/${id}/comments`) }
export function addSubcontractLine(data) { return request.post('/api/subcontract/line', data) }
export function deleteSubcontractLine(id) { return request.delete(`/api/subcontract/line/${id}`) }
export function addSubcontractDeliver(data) { return request.post('/api/subcontract/deliver', data) }
export function deleteSubcontractDeliver(id) { return request.delete(`/api/subcontract/deliver/${id}`) }
export function addSubcontractPayment(data) { return request.post('/api/subcontract/payment', data) }
export function deleteSubcontractPayment(id) { return request.delete(`/api/subcontract/payment/${id}`) }
export function getSubcontractProjects(params) { return request.get('/api/subcontract/projects', { params }) }
export function getSubcontractShipmentInfo(params) { return request.get('/api/subcontract/shipment-info', { params }) }
