import request from '@/utils/request'

export function listSubcontracts(params) { return request.get('/api/subcontract/list', { params }) }
export function getSubcontract(id) { return request.get(`/api/subcontract/${id}`) }
export function createSubcontract(data) { return request.post('/api/subcontract', data) }
export function auditSubcontract(data) { return request.post('/api/subcontract/audit', data) }
export function chooseShipment(projectId) { return request.get(`/api/subcontract/choose-shipment/${projectId}`) }
export function getSubcontractPayment(id) { return request.get(`/api/subcontract/${id}/payment`) }
export function getSubcontractDeliver(id) { return request.get(`/api/subcontract/${id}/deliver`) }
export function getSubcontractCallback(id) { return request.get(`/api/subcontract/${id}/callback`) }
export function getSubcontractComment(id) { return request.get(`/api/subcontract/${id}/comment`) }
export function downloadSubcontractFile(id) { return request.get(`/api/subcontract/file/${id}`, { responseType: 'blob' }) }
