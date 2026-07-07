import { test, expect } from '@playwright/test'

/**
 * 登录页 E2E 测试。
 *
 * 覆盖：登录页渲染、登录成功跳转、未登录访问受保护路由的重定向。
 * 所有 /api/** 请求均通过 page.route 拦截 mock，不依赖真实后端。
 */

/** 后端统一响应信封：{ code, message, data }。响应拦截器在 code=200 时返回 data。 */
function ok(data: unknown) {
  return { code: 200, message: 'ok', data }
}

test.describe('登录流程', () => {
  test('登录页渲染系统标题与用户名/密码输入框', async ({ page }) => {
    await page.goto('/login')

    await expect(page.getByText('网络设备工程项目管理系统').first()).toBeVisible()
    await expect(page.getByPlaceholder('请输入用户名')).toBeVisible()
    await expect(page.getByPlaceholder('请输入密码')).toBeVisible()
    await expect(page.getByRole('button', { name: /登.*录/ })).toBeVisible()
  })

  test('输入用户名密码后登录成功并跳转到 /dashboard', async ({ page }) => {
    // mock 登录接口：返回 token 与 userInfo
    await page.route(/\/api\/auth\/login/, async (route) => {
      await route.fulfill({
        json: ok({
          token: 'fake-token',
          userInfo: { id: 1, username: 'admin', nickname: '管理员', permissions: [] }
        })
      })
    })
    // mock 用户信息接口（应用可能在后续访问中拉取）
    await page.route(/\/api\/auth\/info/, async (route) => {
      await route.fulfill({
        json: ok({ id: 1, username: 'admin', nickname: '管理员', permissions: [] })
      })
    })
    // mock 仪表盘报表接口，避免跳转后报错
    await page.route(/\/api\/report\//, async (route) => {
      await route.fulfill({ json: ok({}) })
    })

    await page.goto('/login')
    await page.getByPlaceholder('请输入用户名').fill('admin')
    await page.getByPlaceholder('请输入密码').fill('123456')
    await page.getByRole('button', { name: /登.*录/ }).click()

    await expect(page).toHaveURL(/\/dashboard/)
  })

  test('未登录访问受保护路由时重定向到登录页并携带 redirect 参数', async ({ page }) => {
    // 不设置 token，直接访问受保护路由
    await page.goto('/project/list')

    await expect(page).toHaveURL(/\/login\?redirect=/)
    // redirect 参数应编码原路径
    await expect(page).toHaveURL(/project/)
  })
})
