import request from '@/utils/request'

export function listCertificates(params) { return request.get('/api/certificate/list', { params }) }
export function getCertificate(id) { return request.get(`/api/certificate/${id}`) }
export function addCertificate(data) { return request.post('/api/certificate', data) }
export function updateCertificate(data) { return request.put('/api/certificate', data) }
export function getOQCInfo(params) { return request.get('/api/certificate/oqc', { params }) }
export function getSealInfo(params) { return request.get('/api/certificate/seal', { params }) }
