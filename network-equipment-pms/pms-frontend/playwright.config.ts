import { defineConfig, devices } from '@playwright/test'

/**
 * Playwright E2E 测试配置。
 *
 * 说明：
 *   - baseURL 与 vite.config.ts 中的 dev server 端口保持一致（3000），
 *     而非任务模板默认的 5173。
 *   - webServer 自动启动 `npm run dev`；若已有 dev server 在运行则复用。
 *   - E2E 用例通过 page.route 拦截 /api/** 请求并返回 mock 数据，
 *     不依赖真实后端。
 *   - 运行前需安装浏览器二进制：`npx playwright install chromium`。
 */
export default defineConfig({
  testDir: './e2e',
  // 仅收集 *.spec.ts（Playwright 用例）；e2e 目录下的 *.test.ts 为 vitest 用例，
  // 不应被 Playwright 加载（两者运行时互不兼容）。
  testMatch: '**/*.spec.ts',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    actionTimeout: 10_000
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
    timeout: 120_000
  }
})
