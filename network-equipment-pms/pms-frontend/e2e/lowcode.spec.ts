import { test, expect, type Page } from '@playwright/test'

/**
 * 低代码表单配置 E2E 测试。
 *
 * 覆盖：表单列表加载渲染、新建表单跳转设计器、状态标签与操作按钮渲染。
 * 所有 /api/lowcode/** 请求均通过 page.route 拦截 mock。
 */

function ok(data: unknown) {
  return { code: 200, message: 'ok', data }
}

/** mock 低代码表单列表接口 */
async function mockFormList(page: Page, records: unknown[]) {
  await page.route(/\/api\/lowcode\/form(\?|$)/, async (route) => {
    await route.fulfill({
      json: ok({ records, total: records.length, current: 1, size: 10 })
    })
  })
}

test.describe('低代码表单配置', () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      localStorage.setItem('pms_token', 'fake-token')
      // 跳过 UserGuide 首次登录自动显示，避免 guide-mask 拦截点击事件
      localStorage.setItem('pms_first_login_done_anonymous', String(Date.now()))
    })
  })

  test('加载并渲染表单配置列表', async ({ page }) => {
    await mockFormList(page, [
      {
        id: 1,
        code: 'ASSET_FORM',
        name: '资产登记表单',
        bizType: 'ASSET',
        status: 'PUBLISHED',
        version: 3,
        updateTime: '2026-07-01 10:00:00'
      },
      {
        id: 2,
        code: 'RMA_FORM',
        name: 'RMA 返修表单',
        bizType: 'RMA',
        status: 'DRAFT',
        version: 1,
        updateTime: '2026-07-02 12:00:00'
      }
    ])
    await page.goto('/lowcode/form-list')

    // 列表标题
    await expect(page.getByText('表单配置列表')).toBeVisible()
    // 表单名称可见
    await expect(page.getByText('资产登记表单').first()).toBeVisible()
    await expect(page.getByText('RMA 返修表单').first()).toBeVisible()
    // 「新建表单」按钮可见
    await expect(page.getByRole('button', { name: '新建表单' })).toBeVisible()
  })

  test('点击「新建表单」跳转到表单设计器', async ({ page }) => {
    await mockFormList(page, [])
    await page.goto('/lowcode/form-list')

    await page.getByRole('button', { name: '新建表单' }).click()

    await expect(page).toHaveURL(/\/lowcode\/form-designer/)
  })

  test('已发布表单显示「归档」操作，草稿表单显示「发布」操作', async ({ page }) => {
    await mockFormList(page, [
      {
        id: 1,
        code: 'PUB',
        name: '已发布表单',
        bizType: 'ASSET',
        status: 'PUBLISHED',
        version: 1,
        updateTime: '2026-07-01 10:00:00'
      },
      {
        id: 2,
        code: 'DRAFT',
        name: '草稿表单',
        bizType: 'ASSET',
        status: 'DRAFT',
        version: 1,
        updateTime: '2026-07-01 10:00:00'
      }
    ])
    await page.goto('/lowcode/form-list')

    // PUBLISHED 行显示「归档」按钮
    await expect(page.getByRole('button', { name: '归档' })).toBeVisible()
    // DRAFT 行显示「发布」按钮
    await expect(page.getByRole('button', { name: '发布' })).toBeVisible()
  })
})
