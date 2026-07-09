/**
 * PMS PWA Service Worker（批次4-T10）。
 *
 * <p>借鉴 Joget PWA 与 Zoho 离线模式，提供以下离线能力：</p>
 *
 * <h3>缓存策略</h3>
 * <ol>
 *   <li><b>App Shell（Stale-While-Revalidate）</b>：HTML/JS/CSS 等应用骨架资源，
 *       优先返回缓存同时后台更新，保证离线可用 + 在线时自动升级</li>
 *   <li><b>静态资源（Cache-First）</b>：图片/字体/图标等不变资源，优先缓存</li>
 *   <li><b>API 请求（Network-First）</b>：动态数据优先网络，失败回退缓存，
 *       保证数据新鲜度 + 离线兜底</li>
 *   <li><b>非 GET 请求（Network-Only）</b>：写操作不缓存，直接走网络</li>
 * </ol>
 *
 * <h3>版本管理</h3>
 * <p>CACHE_VERSION 变更时会触发 activate 阶段清理旧缓存，保证用户拿到最新版本。
 * 建议每次发版时递增此版本号。</p>
 *
 * <h3>离线兜底页</h3>
 * <p>当 HTML 请求网络失败且缓存无命中时，返回 /offline.html 兜底页（需提供）。</p>
 */

const CACHE_VERSION = 'v1.0.0-b4t10'
const APP_SHELL_CACHE = `pms-app-shell-${CACHE_VERSION}`
const STATIC_CACHE = `pms-static-${CACHE_VERSION}`
const API_CACHE = `pms-api-${CACHE_VERSION}`

/** App Shell 资源（HTML/JS/CSS/Workder） */
const APP_SHELL_PATTERN = /\.(?:html|js|css|worker)(?:\?.*)?$|\/$/
/** 静态资源（图片/字体/图标） */
const STATIC_PATTERN = /\.(?:png|jpg|jpeg|gif|svg|webp|ico|woff|woff2|ttf|eot)(?:\?.*)?$/
/** API 路径前缀 */
const API_PATTERN = /^\/api\//

/** 安装阶段：预缓存关键 App Shell 资源 */
self.addEventListener('install', (event) => {
  console.log('[PMS SW] install', CACHE_VERSION)
  event.waitUntil(
    (async () => {
      const cache = await caches.open(APP_SHELL_CACHE)
      // 预缓存首页与离线兜底页（失败不阻断安装）
      const precacheUrls = ['/', '/offline.html', '/manifest.webmanifest']
      await Promise.allSettled(precacheUrls.map((url) => cache.add(url)))
      // 立即激活（跳过 waiting）
      await self.skipWaiting()
    })()
  )
})

/** 激活阶段：清理旧版本缓存 */
self.addEventListener('activate', (event) => {
  console.log('[PMS SW] activate', CACHE_VERSION)
  event.waitUntil(
    (async () => {
      const cacheKeys = await caches.keys()
      // 删除非当前版本的缓存
      await Promise.all(
        cacheKeys
          .filter((key) => !key.endsWith(CACHE_VERSION))
          .map((key) => caches.delete(key))
      )
      // 立即接管所有客户端
      await self.clients.claim()
    })()
  )
})

/** fetch 事件：按资源类型路由到不同缓存策略 */
self.addEventListener('fetch', (event) => {
  const { request } = event
  // 仅处理同源 GET 请求（跨域与非 GET 不缓存）
  const url = new URL(request.url)
  if (request.method !== 'GET') return
  if (url.origin !== self.location.origin) return

  // 路由分发
  if (API_PATTERN.test(url.pathname)) {
    event.respondWith(networkFirstStrategy(request, API_CACHE))
  } else if (STATIC_PATTERN.test(url.pathname)) {
    event.respondWith(cacheFirstStrategy(request, STATIC_CACHE))
  } else if (APP_SHELL_PATTERN.test(url.pathname)) {
    event.respondWith(staleWhileRevalidateStrategy(request, APP_SHELL_CACHE))
  }
  // 其他请求不拦截，走默认网络
})

/**
 * Cache-First 策略：静态资源优先缓存，缓存无命中才走网络。
 */
async function cacheFirstStrategy(request, cacheName) {
  const cache = await caches.open(cacheName)
  const cached = await cache.match(request)
  if (cached) return cached
  try {
    const response = await fetch(request)
    if (response.ok) cache.put(request, response.clone())
    return response
  } catch (e) {
    // 网络失败且无缓存，返回空响应
    return new Response('', { status: 504, statusText: 'Offline' })
  }
}

/**
 * Stale-While-Revalidate 策略：App Shell 优先返回缓存，同时后台更新。
 */
async function staleWhileRevalidateStrategy(request, cacheName) {
  const cache = await caches.open(cacheName)
  const cached = await cache.match(request)
  const fetchPromise = fetch(request)
    .then((response) => {
      if (response.ok) cache.put(request, response.clone())
      return response
    })
    .catch(() => null)
  // 有缓存立即返回，无缓存等待网络
  return cached || fetchPromise || (await caches.match('/offline.html'))
}

/**
 * Network-First 策略：API 优先网络，失败回退缓存。
 */
async function networkFirstStrategy(request, cacheName) {
  const cache = await caches.open(cacheName)
  try {
    const response = await fetch(request)
    // 仅缓存成功响应（不缓存错误响应）
    if (response.ok) cache.put(request, response.clone())
    return response
  } catch (e) {
    // 网络失败，回退缓存
    const cached = await cache.match(request)
    if (cached) return cached
    // 缓存也无，返回离线 JSON
    return new Response(
      JSON.stringify({ code: 503, message: '离线模式，数据不可用', data: null }),
      {
        status: 503,
        headers: { 'Content-Type': 'application/json' }
      }
    )
  }
}

/** 消息通信：允许页面主动触发更新 */
self.addEventListener('message', (event) => {
  if (event.data === 'SKIP_WAITING') {
    self.skipWaiting()
  }
})
