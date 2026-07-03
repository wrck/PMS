/**
 * End-to-end test for the full project lifecycle.
 *
 * This test exercises the real backend HTTP API (login → create project →
 * approve → create milestone → update milestone progress → apply final
 * acceptance → approve final acceptance → assert project COMPLETED).
 *
 * It is gated by the E2E_ENABLED environment variable because the sandbox / CI
 * environment does not always have a running backend. To run it locally:
 *
 *   E2E_ENABLED=true npx vitest run e2e/
 *
 * Optionally override the base URL and credentials:
 *
 *   E2E_ENABLED=true E2E_BASE_URL=http://localhost:8080 \
 *     E2E_USERNAME=admin E2E_PASSWORD=admin123 npx vitest run e2e/
 */
import axios, { type AxiosInstance } from 'axios'
import { beforeAll, describe, expect, it } from 'vitest'

const E2E_ENABLED = process.env.E2E_ENABLED === 'true'
const BASE_URL = process.env.E2E_BASE_URL || 'http://localhost:8080'
const USERNAME = process.env.E2E_USERNAME || 'admin'
const PASSWORD = process.env.E2E_PASSWORD || 'admin123'

const describeE2E = E2E_ENABLED ? describe : describe.skip

interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

function createClient(token?: string): AxiosInstance {
  const client = axios.create({
    baseURL: BASE_URL,
    timeout: 15000,
    validateStatus: () => true
  })
  client.interceptors.response.use((response) => {
    const body = response.data as ApiResult
    if (body && typeof body.code === 'number' && body.code !== 200) {
      const err = new Error(body.message || `API error ${body.code}`) as Error & {
        code?: number
      }
      err.code = body.code
      return Promise.reject(err)
    }
    return response
  })
  if (token) {
    client.defaults.headers.common.Authorization = `Bearer ${token}`
  }
  return client
}

describeE2e('Project Lifecycle E2E', () => {
  let token: string
  let authClient: AxiosInstance
  let projectId: number
  let milestoneId: number
  let acceptanceId: number

  beforeAll(async () => {
    // 1. Login and obtain a JWT token (POST /api/auth/login)
    const unauthClient = createClient()
    const loginResp = await unauthClient.post('/api/auth/login', {
      username: USERNAME,
      password: PASSWORD
    })
    const loginBody = loginResp.data as ApiResult<{
      token: string
      userId?: number
      username?: string
    }>
    expect(loginBody.code).toBe(200)
    expect(loginBody.data?.token).toBeTruthy()
    token = loginBody.data!.token
    authClient = createClient(token)
  })

  it('should complete the full project lifecycle', async () => {
    // 2. Create a project (POST /api/project) -> expect status === 'PENDING'
    const unique = `E2E-${Date.now()}`
    const createResp = await authClient.post('/api/project', {
      projectName: unique,
      projectType: 'NETWORK_DEVICE',
      customerName: 'E2E Customer',
      priority: 'NORMAL',
      planStartDate: '2026-01-01',
      planEndDate: '2026-12-31'
    })
    const createBody = createResp.data as ApiResult<{
      id?: number
      status?: string
    }>
    expect(createBody.code).toBe(200)
    expect(createBody.data).toBeTruthy()
    projectId = createBody.data!.id!
    expect(createBody.data!.status).toBe('PENDING')

    // 3. Approve the project (POST /api/project/{id}/approve) -> status === 'APPROVED'
    const approveResp = await authClient.post(`/api/project/${projectId}/approve`)
    const approveBody = approveResp.data as ApiResult<{ status?: string }>
    expect(approveBody.code).toBe(200)
    expect(approveBody.data?.status).toBe('APPROVED')

    // 4. Create a milestone for the project (POST /api/project/milestone)
    const milestoneResp = await authClient.post('/api/project/milestone', {
      projectId,
      name: `E2E Milestone-${unique}`,
      type: 'PHASE',
      plannedDate: '2026-06-30',
      description: 'E2E milestone'
    })
    const milestoneBody = milestoneResp.data as ApiResult<{ id?: number }>
    expect(milestoneBody.code).toBe(200)
    expect(milestoneBody.data).toBeTruthy()
    milestoneId = milestoneBody.data!.id!

    // 5. Update milestone progress (POST /api/project/milestone/{id}/progress)
    const progressResp = await authClient.post(
      `/api/project/milestone/${milestoneId}/progress`,
      {
        actualDate: '2026-06-15',
        description: 'E2E milestone completed'
      }
    )
    const progressBody = progressResp.data as ApiResult<unknown>
    expect(progressBody.code).toBe(200)

    // 6. Apply for final acceptance (POST /api/project/acceptance/apply)
    const applyResp = await authClient.post('/api/project/acceptance/apply', {
      projectId,
      report: 'E2E final acceptance report'
    })
    const applyBody = applyResp.data as ApiResult<{ id?: number; status?: string }>
    expect(applyBody.code).toBe(200)
    expect(applyBody.data).toBeTruthy()
    acceptanceId = applyBody.data!.id!

    // 7. Approve final acceptance (POST /api/project/acceptance/{id}/approve)
    const acceptApproveResp = await authClient.post(
      `/api/project/acceptance/${acceptanceId}/approve`,
      { opinion: 'E2E approved' }
    )
    const acceptApproveBody = acceptApproveResp.data as ApiResult<unknown>
    expect(acceptApproveBody.code).toBe(200)

    // 8. Verify the project status is 'COMPLETED'
    const getResp = await authClient.get(`/api/project/${projectId}`)
    const getBody = getResp.data as ApiResult<{ status?: string }>
    expect(getBody.code).toBe(200)
    expect(getBody.data?.status).toBe('COMPLETED')
  })
})
