// src/api/lowcode-collaboration.ts
import { get, post } from '@/utils/request'

export interface OnlineUser {
  userId: number
  userName: string
  avatar?: string
  joinedAt?: string
  lastHeartbeat?: string
}

export interface CollaborationChange {
  seq?: number
  operation: 'UPDATE' | 'INSERT' | 'DELETE' | 'CURSOR'
  path: string
  oldValue?: string
  newValue?: string
  userId?: number
  userName?: string
  timestamp?: string
}

export function joinCollaboration(configType: string, configId: number, user: OnlineUser) {
  return post<void>('/api/lowcode/collaboration/join', {
    configType,
    configId,
    user
  })
}

export function leaveCollaboration(configType: string, configId: number, userId: number) {
  return post<void>('/api/lowcode/collaboration/leave', {
    configType,
    configId,
    userId
  })
}

export function heartbeatCollaboration(configType: string, configId: number, userId: number) {
  return post<void>('/api/lowcode/collaboration/heartbeat', {
    configType,
    configId,
    userId
  })
}

export function getOnlineUsers(configType: string, configId: number) {
  return get<OnlineUser[]>('/api/lowcode/collaboration/online', { configType, configId })
}

export function broadcastChange(
  configType: string,
  configId: number,
  change: CollaborationChange
) {
  return post<void>('/api/lowcode/collaboration/change', {
    configType,
    configId,
    change
  })
}

export function getChanges(configType: string, configId: number, sinceSeq = 0) {
  return get<CollaborationChange[]>('/api/lowcode/collaboration/changes', {
    configType,
    configId,
    sinceSeq
  })
}
