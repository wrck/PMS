import { test, expect, type Page } from '@playwright/test'

/**
 * 仪表盘 E2E 测试。
 *
 * 前置条件：通过 addInitScript 向 localStorage 注入 token，绕过登录守卫。
 * 所有 /api/report/** 请求均通过 page.route 拦截 mock。
 */

function ok(data: unknown) {
  return { code: 200, message: 'ok', data }
}

/** mock 仪表盘所需的全部报表接口 */
async function mockDashboardApis(page: Page) {
  await page.route(/\/api\/report\/dashboard\/stats/, async (route) => {
    await route.fulfill({
      json: ok({
        projectTotal: 12,
        assetInStock: 5,
        todoCount: 3,
        monthDelivery: 1,
        projectInProgress: 4,
        monthNewProject: 2,
        monthNewAsset: 1,
        alertCount: 0
      })
    })
  })
  await page.route(/\/api\/report\/project\/trend/, async (route) => {
    await route.fulfill({ json: ok([]) })
  })
  await page.route(/\/api\/report\/todo\/list/, async (route) => {
    await route.fulfill({ json: ok([]) })
  })
  await page.route(/\/api\/report\/recent-activities/, async (route) => {
    await route.fulfill({ json: ok([]) })
  })
  await page.route(/\/api\/report\/asset/, async (route) => {
    await route.fulfill({
      json: ok({
        byStatus: {},
        byCategory: {},
        totalValue: 0,
        total: 0,
        inStock: 0,
        allocated: 0,
        inTransfer: 0,
        scrapped: 0
      })
    })
  })
}

test.describe('仪表盘', () => {
  test.beforeEach(async ({ page }) => {
    // 注入 token 绕过路由守卫（应用未自动拉取 userInfo，welcomeName 降级为「管理员」）
    await page.addInitScript(() => {
      localStorage.setItem('pms_token', 'fake-token')
      // 跳过 UserGuide 首次登录自动显示，避免 guide-mask 拦截点击事件
      localStorage.setItem('pms_first_login_done_anonymous', String(Date.now()))
    })
  })

  test('已登录用户访问仪表盘显示欢迎信息', async ({ page }) => {
    await mockDashboardApis(page)
    await page.goto('/dashboard')

    // welcomeName 在无 userInfo 时降级为「管理员」
    await expect(page.locator('.welcome-title')).toContainText('管理员')
  })

  test('仪表盘渲染 4 个统计卡片及对应标题', async ({ page }) => {
    await mockDashboardApis(page)
    await page.goto('/dashboard')

    await expect(page.locator('.stat-card')).toHaveCount(4)
    const titles = await page.locator('.stat-title').allTextContents()
    expect(titles).toEqual(['项目总数', '在库设备', '待办任务', '本月交付'])
  })

  test('统计卡片数值随报表接口数据更新', async ({ page }) => {
    await mockDashboardApis(page)
    await page.goto('/dashboard')

    // 第一个卡片（项目总数）应反映 mock 的 projectTotal=12
    await expect(page.locator('.stat-value').first()).toHaveText('12')
  })
})
