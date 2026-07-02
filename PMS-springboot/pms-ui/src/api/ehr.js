import request from '@/utils/request'

export function listEmployees(params) { return request.get('/api/ehr/employee/list', { params }) }
export function getEmployee(id) { return request.get(`/api/ehr/employee/${id}`) }
export function listJobs(params) { return request.get('/api/ehr/job/list', { params }) }
export function listHolidays(params) { return request.get('/api/ehr/holiday/list', { params }) }
export function syncFromEHR() { return request.post('/api/ehr/sync') }
