<script setup lang="ts">
/**
 * 低代码页面通用渲染入口。
 *
 * <p>路由 /lowcode/:pageType/:pageCode 进入此组件。组件职责：</p>
 * <ul>
 *   <li>根据 pageType 调用对应 API 拉取已发布配置（getFormByCode 等）</li>
 *   <li>调用权限校验接口 checkLowCodePermission，无权限时显示 403 提示</li>
 *   <li>解析配置 JSON 字符串后，动态加载对应渲染器渲染</li>
 *   <li>加载中显示骨架屏；配置不存在显示 404；无权限显示 403</li>
 *   <li>将路由 query 参数透传给渲染器作为上下文数据</li>
 * </ul>
 *
 * <p>4 种渲染器对应关系：</p>
 * <ul>
 *   <li>form → LowCodeFormRenderer（props: config: FormConfig）</li>
 *   <li>list → LowCodeListRenderer（props: config: ListConfig）</li>
 *   <li>tab → LowCodeTabRenderer（props: config: TabConfig, contextData）</li>
 *   <li>related-page → LowCodeRelatedPageRenderer（props: config: RelatedPageConfig, contextData）</li>
 * </ul>
 */
import { computed, defineAsyncComponent, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  checkLowCodePermission,
  getFormByCode,
  getListByCode,
  getRelatedPageByCode,
  getTabByCode,
  type LowCodeFormConfig,
  type LowCodeListConfig,
  type LowCodePageType,
  type LowCodeRelatedPageConfig,
  type LowCodeTabConfig
} from '@/api/lowcode'

const route = useRoute()
const router = useRouter()

/** 加载状态：loading / done / not-found / forbidden / error */
const state = ref<'loading' | 'done' | 'not-found' | 'forbidden' | 'error'>('loading')

/** 解析后的配置对象（FormConfig / ListConfig / TabConfig / RelatedPageConfig 之一） */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const config = ref<any>(null)

const pageType = computed(() => route.params.pageType as LowCodePageType)
const pageCode = computed(() => route.params.pageCode as string)

/** 合法的页面类型集合 */
const VALID_PAGE_TYPES: ReadonlySet<string> = new Set(['form', 'list', 'tab', 'related-page'])

/** 根据 pageType 解析渲染器组件（异步加载，避免首屏加载全部渲染器） */
const renderer = computed(() => {
  switch (pageType.value) {
    case 'form':
      return defineAsyncComponent(() => import('@/components/LowCodeFormRenderer/index.vue'))
    case 'list':
      return defineAsyncComponent(() => import('@/components/LowCodeListRenderer/index.vue'))
    case 'tab':
      return defineAsyncComponent(() => import('@/components/LowCodeTabRenderer/index.vue'))
    case 'related-page':
      return defineAsyncComponent(() => import('@/components/LowCodeRelatedPageRenderer/index.vue'))
    default:
      return null
  }
})

/** 透传给渲染器的上下文数据（route / params / query） */
const contextData = computed(() => ({
  route,
  params: route.params,
  query: route.query
}))

/**
 * 拉取已发布配置并解析 JSON。
 * 返回 { name, config } 或 null（配置不存在 / 状态非 PUBLISHED）。
 */
async function fetchConfig(
  type: LowCodePageType,
  code: string
): Promise<{ name: string; config: unknown } | null> {
  let meta:
    | (LowCodeFormConfig & { formConfig?: string })
    | (LowCodeListConfig & { listConfig?: string })
    | (LowCodeTabConfig & { tabConfig?: string })
    | (LowCodeRelatedPageConfig & { relatedConfig?: string })
    | null = null
  let configStr: string | undefined
  switch (type) {
    case 'form':
      meta = await getFormByCode(code)
      configStr = (meta as LowCodeFormConfig | null)?.formConfig
      break
    case 'list':
      meta = await getListByCode(code)
      configStr = (meta as LowCodeListConfig | null)?.listConfig
      break
    case 'tab':
      meta = await getTabByCode(code)
      configStr = (meta as LowCodeTabConfig | null)?.tabConfig
      break
    case 'related-page':
      meta = await getRelatedPageByCode(code)
      configStr = (meta as LowCodeRelatedPageConfig | null)?.relatedConfig
      break
    default:
      return null
  }
  if (!meta) {
    return null
  }
  if (!configStr) {
    return null
  }
  try {
    const parsed = typeof configStr === 'string' ? JSON.parse(configStr) : configStr
    return { name: meta.name || '', config: parsed }
  } catch (e) {
    console.error('低代码配置 JSON 解析失败', e)
    return null
  }
}

/** 加载并校验低代码页面 */
async function load() {
  state.value = 'loading'
  config.value = null

  // 1. 校验 pageType 合法性
  if (!VALID_PAGE_TYPES.has(pageType.value)) {
    state.value = 'not-found'
    return
  }
  if (!pageCode.value) {
    state.value = 'not-found'
    return
  }

  try {
    // 2. 权限校验（调用后端 /api/lowcode/permission/check）
    //    后端返回 false 时显示 403；接口异常时降级为放行（由配置接口本身的鉴权兜底）
    let allowed = true
    try {
      allowed = await checkLowCodePermission(pageType.value, pageCode.value)
    } catch (e) {
      // 权限校验接口不可用时降级放行，避免阻断已发布的低代码页面访问
      console.warn('低代码权限校验接口不可用，降级放行', e)
      allowed = true
    }
    if (!allowed) {
      state.value = 'forbidden'
      ElMessage.error('您没有访问该低代码页面的权限')
      return
    }

    // 3. 拉取已发布配置
    const result = await fetchConfig(pageType.value, pageCode.value)
    if (!result) {
      state.value = 'not-found'
      return
    }
    config.value = result.config
    // 更新浏览器标题
    if (result.name) {
      document.title = `${result.name} - 网络设备工程项目管理系统`
    }
    state.value = 'done'
  } catch (e) {
    console.error('加载低代码页面失败', e)
    state.value = 'error'
  }
}

onMounted(load)

// 路由参数变化时重新加载（同一组件实例复用场景）
watch([pageType, pageCode], () => {
  load()
})

function goBack() {
  router.back()
}
</script>

<template>
  <div class="lowcode-render-page">
    <!-- 加载中：骨架屏 -->
    <el-skeleton v-if="state === 'loading'" :rows="10" animated />

    <!-- 404：配置不存在 -->
    <el-result
      v-else-if="state === 'not-found'"
      icon="warning"
      title="页面不存在"
      sub-title="请检查页面编码或联系管理员"
    >
      <template #extra>
        <el-button type="primary" @click="goBack">返回</el-button>
      </template>
    </el-result>

    <!-- 403：无权限 -->
    <el-result
      v-else-if="state === 'forbidden'"
      icon="error"
      title="无访问权限"
      sub-title="您没有访问该低代码页面的权限，请联系管理员授权"
    >
      <template #extra>
        <el-button type="primary" @click="goBack">返回</el-button>
      </template>
    </el-result>

    <!-- 加载异常 -->
    <el-result
      v-else-if="state === 'error'"
      icon="error"
      title="加载失败"
      sub-title="低代码页面加载异常，请稍后重试"
    >
      <template #extra>
        <el-button type="primary" @click="load">重试</el-button>
        <el-button @click="goBack">返回</el-button>
      </template>
    </el-result>

    <!-- 正常渲染 -->
    <component
      v-else-if="state === 'done' && renderer && config"
      :is="renderer"
      :config="config"
      :context-data="contextData"
    />
  </div>
</template>

<style scoped>
.lowcode-render-page {
  padding: 12px;
  min-height: calc(100vh - 110px);
}
</style>
