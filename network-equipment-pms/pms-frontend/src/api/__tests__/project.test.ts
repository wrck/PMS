import { beforeEach, describe, expect, it, vi } from 'vitest'

// Mock the request module — the API layer should only translate calls into
// the correct HTTP method + URL + payload, so we assert on the mock calls.
const mocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn()
}))

vi.mock('@/utils/request', () => ({
  get: mocks.get,
  post: mocks.post,
  put: mocks.put,
  del: mocks.del
}))

import {
  createProject,
  getProject,
  listProjects,
  updateProject,
  deleteProject,
  approveProject,
  getDashboard,
  createMilestone,
  updateMilestone,
  deleteMilestone,
  listMilestones,
  updateMilestoneProgress,
  applyAcceptance,
  approveAcceptance,
  rejectAcceptance,
  getAcceptanceByProject,
  type Project,
  type Milestone
} from '@/api/project'

beforeEach(() => {
  vi.clearAllMocks()
})

describe('project API', () => {
  describe('project CRUD', () => {
    it('createProject calls POST /api/project with the given body', () => {
      mocks.post.mockResolvedValue({})
      const data: Project = { name: 'Project A', type: 'NETWORK_DEVICE' }
      createProject(data)
      expect(mocks.post).toHaveBeenCalledWith('/api/project', data)
    })

    it('getProject calls GET /api/project/{id}', () => {
      mocks.get.mockResolvedValue({})
      getProject(7)
      expect(mocks.get).toHaveBeenCalledWith('/api/project/7')
    })

    it('listProjects calls GET /api/project/list with the query params', () => {
      mocks.get.mockResolvedValue({ records: [], total: 0, page: 1, size: 10 })
      const params = { page: 1, size: 10, projectName: 'net', status: 'APPROVED' }
      listProjects(params)
      expect(mocks.get).toHaveBeenCalledWith('/api/project/list', params)
    })

    it('updateProject calls PUT /api/project with the given body', () => {
      mocks.put.mockResolvedValue({})
      const data: Project = { id: 5, name: 'Updated' }
      updateProject(data)
      expect(mocks.put).toHaveBeenCalledWith('/api/project', data)
    })

    it('deleteProject calls DELETE /api/project/{id}', () => {
      mocks.del.mockResolvedValue(undefined)
      deleteProject(9)
      expect(mocks.del).toHaveBeenCalledWith('/api/project/9')
    })

    it('approveProject calls POST /api/project/{id}/approve', () => {
      mocks.post.mockResolvedValue(undefined)
      approveProject(42)
      expect(mocks.post).toHaveBeenCalledWith('/api/project/42/approve')
    })

    it('getDashboard calls GET /api/project/dashboard', () => {
      mocks.get.mockResolvedValue({})
      getDashboard()
      expect(mocks.get).toHaveBeenCalledWith('/api/project/dashboard')
    })
  })

  describe('milestone', () => {
    it('createMilestone calls POST /api/project/milestone', () => {
      mocks.post.mockResolvedValue({})
      const data: Milestone = { projectId: 1, name: 'M1', plannedDate: '2026-01-01' }
      createMilestone(data)
      expect(mocks.post).toHaveBeenCalledWith('/api/project/milestone', data)
    })

    it('updateMilestone calls PUT /api/project/milestone', () => {
      mocks.put.mockResolvedValue({})
      const data: Milestone = { id: 3, name: 'M1-updated' }
      updateMilestone(data)
      expect(mocks.put).toHaveBeenCalledWith('/api/project/milestone', data)
    })

    it('deleteMilestone calls DELETE /api/project/milestone/{id}', () => {
      mocks.del.mockResolvedValue(undefined)
      deleteMilestone(3)
      expect(mocks.del).toHaveBeenCalledWith('/api/project/milestone/3')
    })

    it('listMilestones calls GET /api/project/milestone/project/{projectId}', () => {
      mocks.get.mockResolvedValue([])
      listMilestones(11)
      expect(mocks.get).toHaveBeenCalledWith('/api/project/milestone/project/11')
    })

    it('updateMilestoneProgress calls POST /api/project/milestone/{id}/progress', () => {
      mocks.post.mockResolvedValue(undefined)
      const data = { actualDate: '2026-02-01', description: 'done' }
      updateMilestoneProgress(5, data)
      expect(mocks.post).toHaveBeenCalledWith(
        '/api/project/milestone/5/progress',
        data
      )
    })
  })

  describe('final acceptance', () => {
    it('applyAcceptance calls POST /api/project/acceptance/apply', () => {
      mocks.post.mockResolvedValue({})
      const data = { projectId: 8, report: 'final report' }
      applyAcceptance(data)
      expect(mocks.post).toHaveBeenCalledWith(
        '/api/project/acceptance/apply',
        data
      )
    })

    it('approveAcceptance calls POST /api/project/acceptance/{id}/approve', () => {
      mocks.post.mockResolvedValue(undefined)
      const data = { opinion: 'approved' }
      approveAcceptance(2, data)
      expect(mocks.post).toHaveBeenCalledWith(
        '/api/project/acceptance/2/approve',
        data
      )
    })

    it('rejectAcceptance calls POST /api/project/acceptance/{id}/reject', () => {
      mocks.post.mockResolvedValue(undefined)
      const data = { opinion: 'rejected' }
      rejectAcceptance(2, data)
      expect(mocks.post).toHaveBeenCalledWith(
        '/api/project/acceptance/2/reject',
        data
      )
    })

    it('getAcceptanceByProject calls GET /api/project/acceptance/{projectId}', () => {
      mocks.get.mockResolvedValue(null)
      getAcceptanceByProject(8)
      expect(mocks.get).toHaveBeenCalledWith('/api/project/acceptance/8')
    })
  })
})
