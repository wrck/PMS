import request from '@/utils/request'

export function getTodoTasks(params) { return request.get('/api/workspace/daily-tasks', { params }) }
export function getDailyTasks(params) { return request.get('/api/workspace/daily-tasks', { params }) }
export function getBusinessTasks(params) { return request.get('/api/workspace/business-tasks', { params }) }
export function getHistoryTasks(params) { return request.get('/api/workspace/history-tasks', { params }) }
export function getProbTasks(params) { return request.get('/api/workspace/prob-tasks', { params }) }
export function getSubcontractTasks(params) { return request.get('/api/workspace/subcontract-tasks', { params }) }
export function getNotifications(params) { return request.get('/api/workspace/notifications', { params }) }
export function getSystemNotifications(params) { return request.get('/api/workspace/system-notifications', { params }) }
export function getDashboardData() { return request.get('/api/workspace/dashboard') }
export function markWorkspaceNotificationRead(id) { return request.post(`/api/workspace/notification/${id}/read`) }
