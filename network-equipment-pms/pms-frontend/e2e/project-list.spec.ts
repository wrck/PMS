import { test, expect, type Page } from '@playwright/test'

/**
 * 项目列表页 E2E 测试。
 *
 * 覆盖：列表加载渲染、按名称搜索、新建项目对话框。
 * 所有 /api/project/** 请求均通过 page.route 拦截 mock。
 */

function ok(data: unknown) {
  return { code: 200, message: 'ok', data }
}

/** mock 项目列表接口，返回给定项目数组 */
async function mockProjectList(page: Page, records: unknown[]) {
  await page.route(/\/api\/project\/list/, async (route) => {
    await route.fulfill({
      json: ok({ records, total: records.length, page: 1, size: 10 })
    })
  })
}

test.describe('项目列表', () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      localStorage.setItem('pms_token', 'fake-token')
      // 跳过 UserGuide 首次登录自动显示，避免 guide-mask 拦截点击事件
      localStorage.setItem('pms_first_login_done_anonymous', String(Date.now()))
    })
  })

  test('加载并渲染项目数据', async ({ page }) => {
    await mockProjectList(page, [
      {
        id: 1,
        code: 'P-2026-001',
        name: '北京核心交换机部署',
        type: 'NETWORK_DEVICE',
        customerName: '北京联通',
        status: 'IN_PROGRESS',
        managerName: '张三'
      }
    ])
    await page.goto('/project/list')

    // 项目名称应出现在表格中
    await expect(page.getByText('北京核心交换机部署').first()).toBeVisible()
    // 项目编号也应可见
    await expect(page.getByText('P-2026-001').first()).toBeVisible()
    // 存在「新建项目」按钮
    await expect(page.getByRole('button', { name: '新建项目' })).toBeVisible()
  })

  test('按项目名称搜索触发带 projectName 参数的请求', async ({ page }) => {
    const requests: string[] = []
    await page.route(/\/api\/project\/list/, async (route) => {
      requests.push(route.request().url())
      await route.fulfill({
        json: ok({ records: [], total: 0, page: 1, size: 10 })
      })
    })

    await page.goto('/project/list')
    // 初始加载请求
    await expect.poll(() => requests.length).toBeGreaterThan(0)
    const initialCount = requests.length

    await page.getByPlaceholder('请输入项目名称').fill('交换机')
    await page.getByRole('button', { name: '查询' }).click()

    // 新请求应携带 projectName 参数（URL 参数会被百分号编码，需先 decodeURIComponent）
    await expect.poll(() => requests.length).toBeGreaterThan(initialCount)
    expect(decodeURIComponent(requests[requests.length - 1])).toMatch(/projectName=交换机/)
  })

  test('点击「新建项目」打开新建对话框', async ({ page }) => {
    await mockProjectList(page, [])
    await page.goto('/project/list')

    await page.getByRole('button', { name: '新建项目' }).click()

    // 对话框标题为「新建项目」
    await expect(page.getByText('新建项目').first()).toBeVisible()
    // 对话框内含「项目名称」表单项与「确定」按钮
    await expect(page.getByRole('button', { name: '确定' })).toBeVisible()
  })
})
