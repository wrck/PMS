<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ArrowLeft, View } from '@element-plus/icons-vue'
import {
  listHelpContents,
  getHelpContent,
  type HelpContent,
  type HelpCategory
} from '@/api/help'

/**
 * 帮助中心页面。
 *
 * <p>布局：左侧分类菜单（快速开始 / 常见问题 / 视频教程 / 进阶技巧）
 * + 右侧内容列表 + 详情查看（Markdown 渲染）+ 顶部搜索框。</p>
 *
 * <p>调用 GET /api/system/help-content/list（公开接口）。</p>
 */

interface CategoryMeta {
  label: string
  value: HelpCategory
  icon: string
}

const CATEGORIES: CategoryMeta[] = [
  { label: '快速开始', value: 'QUICK_START', icon: 'Rocket' },
  { label: '常见问题', value: 'FAQ', icon: 'QuestionFilled' },
  { label: '视频教程', value: 'VIDEO', icon: 'VideoCamera' },
  { label: '进阶技巧', value: 'ADVANCED', icon: 'MagicStick' }
]

const loading = ref(false)
const allContents = ref<HelpContent[]>([])
const selectedCategory = ref<HelpCategory>('QUICK_START')
const selectedContent = ref<HelpContent | null>(null)
const keyword = ref('')

/** 按分类 + 关键词过滤后的内容列表 */
const filteredContents = computed<HelpContent[]>(() => {
  let list = allContents.value.filter((c) => c.category === selectedCategory.value)
  if (keyword.value.trim()) {
    const kw = keyword.value.trim().toLowerCase()
    list = list.filter(
      (c) =>
        c.title.toLowerCase().includes(kw) || (c.content || '').toLowerCase().includes(kw)
    )
  }
  return list
})

/** 各分类下的内容数量（用于菜单 badge） */
function countOf(category: HelpCategory): number {
  return allContents.value.filter((c) => c.category === category).length
}

/** 当前选中的分类对象 */
const currentCategoryMeta = computed(
  () => CATEGORIES.find((c) => c.value === selectedCategory.value) ?? CATEGORIES[0]
)

async function loadContents() {
  loading.value = true
  try {
    const list = await listHelpContents()
    allContents.value = list || []
    // 默认选中第一个内容
    if (filteredContents.value.length > 0) {
      await selectContent(filteredContents.value[0])
    } else {
      selectedContent.value = null
    }
  } catch {
    ElMessage.error('加载帮助内容失败')
    allContents.value = []
  } finally {
    loading.value = false
  }
}

async function selectCategory(cat: HelpCategory) {
  selectedCategory.value = cat
  keyword.value = ''
  await nextTickSafe()
  if (filteredContents.value.length > 0) {
    await selectContent(filteredContents.value[0])
  } else {
    selectedContent.value = null
  }
}

async function selectContent(item: HelpContent) {
  if (item.id == null) {
    selectedContent.value = item
    return
  }
  try {
    // 调用详情接口累加浏览次数
    const detail = await getHelpContent(item.id)
    selectedContent.value = detail
  } catch {
    selectedContent.value = item
  }
}

function backToList() {
  selectedContent.value = null
}

/** 安全 nextTick（避免在 setup 顶层使用） */
async function nextTickSafe() {
  await Promise.resolve()
}

// ===================== 极简 Markdown 渲染 =====================

/**
 * 极简 Markdown 渲染器（无外部依赖）。
 *
 * <p>支持：代码块、标题、表格、有序/无序列表、加粗、行内代码、
 * 段落、水平线。输出经过 HTML 转义的安全 HTML 字符串。</p>
 *
 * <p>本渲染器不追求完整 Markdown 规范，仅覆盖帮助文档实际使用的语法。</p>
 */
function escapeHtml(s: string): string {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function renderInline(text: string): string {
  let s = escapeHtml(text)
  // 行内代码 `code`
  s = s.replace(/`([^`]+)`/g, '<code class="md-inline-code">$1</code>')
  // 加粗 **text**
  s = s.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
  // 链接 [text](url)
  s = s.replace(
    /\[([^\]]+)\]\(([^)]+)\)/g,
    '<a href="$2" target="_blank" rel="noopener">$1</a>'
  )
  return s
}

function renderMarkdown(md: string): string {
  if (!md) return ''
  const lines = md.replace(/\r\n/g, '\n').split('\n')
  const html: string[] = []
  let i = 0
  let inUl = false
  let inOl = false
  let inTable = false
  let tableHeader: string[] = []

  function closeLists() {
    if (inUl) {
      html.push('</ul>')
      inUl = false
    }
    if (inOl) {
      html.push('</ol>')
      inOl = false
    }
  }

  function closeTable() {
    if (inTable) {
      html.push('</tbody></table>')
      inTable = false
      tableHeader = []
    }
  }

  while (i < lines.length) {
    const line = lines[i]

    // 代码块
    if (line.trim().startsWith('```')) {
      closeLists()
      closeTable()
      const lang = line.trim().slice(3)
      const buf: string[] = []
      i++
      while (i < lines.length && !lines[i].trim().startsWith('```')) {
        buf.push(lines[i])
        i++
      }
      i++ // skip closing ```
      html.push(
        `<pre class="md-code-block${lang ? ` md-code-block--${lang}` : ''}"><code>${escapeHtml(
          buf.join('\n')
        )}</code></pre>`
      )
      continue
    }

    // 水平线
    if (/^---+$/.test(line.trim())) {
      closeLists()
      closeTable()
      html.push('<hr class="md-hr" />')
      i++
      continue
    }

    // 标题
    const headerMatch = line.match(/^(#{1,6})\s+(.*)$/)
    if (headerMatch) {
      closeLists()
      closeTable()
      const level = headerMatch[1].length
      html.push(`<h${level} class="md-h md-h-${level}">${renderInline(headerMatch[2])}</h${level}>`)
      i++
      continue
    }

    // 表格行（包含 |）
    if (line.includes('|') && line.trim().startsWith('|')) {
      // 检查下一行是否为分隔符 | --- | --- |
      const next = lines[i + 1] || ''
      if (/^\s*\|[\s:|-]+\|\s*$/.test(next) && next.includes('-')) {
        closeLists()
        // 表头
        const headers = line
          .trim()
          .slice(1, -1)
          .split('|')
          .map((c) => c.trim())
        tableHeader = headers
        if (!inTable) {
          html.push('<table class="md-table"><thead><tr>')
          headers.forEach((h) => html.push(`<th>${renderInline(h)}</th>`))
          html.push('</tr></thead><tbody>')
          inTable = true
        }
        i += 2 // skip header + separator
        continue
      }
      if (inTable) {
        const cells = line
          .trim()
          .slice(1, -1)
          .split('|')
          .map((c) => c.trim())
        html.push('<tr>')
        cells.forEach((c, idx) => {
          html.push(`<td>${renderInline(c)}</td>`)
          void idx
        })
        html.push('</tr>')
        i++
        continue
      }
    }

    if (inTable) {
      closeTable()
    }

    // 无序列表
    if (/^\s*[-*+]\s+/.test(line)) {
      if (inOl) {
        html.push('</ol>')
        inOl = false
      }
      if (!inUl) {
        html.push('<ul class="md-ul">')
        inUl = true
      }
      const item = line.replace(/^\s*[-*+]\s+/, '')
      html.push(`<li>${renderInline(item)}</li>`)
      i++
      continue
    }

    // 有序列表
    if (/^\s*\d+\.\s+/.test(line)) {
      if (inUl) {
        html.push('</ul>')
        inUl = false
      }
      if (!inOl) {
        html.push('<ol class="md-ol">')
        inOl = true
      }
      const item = line.replace(/^\s*\d+\.\s+/, '')
      html.push(`<li>${renderInline(item)}</li>`)
      i++
      continue
    }

    // 空行
    if (line.trim() === '') {
      closeLists()
      closeTable()
      i++
      continue
    }

    // 段落（连续非空行合并）
    closeLists()
    closeTable()
    const para: string[] = [line]
    i++
    while (
      i < lines.length &&
      lines[i].trim() !== '' &&
      !/^(#{1,6})\s+/.test(lines[i]) &&
      !/^\s*[-*+]\s+/.test(lines[i]) &&
      !/^\s*\d+\.\s+/.test(lines[i]) &&
      !lines[i].trim().startsWith('```') &&
      !/^---+$/.test(lines[i].trim()) &&
      !(lines[i].includes('|') && lines[i].trim().startsWith('|'))
    ) {
      para.push(lines[i])
      i++
    }
    html.push(`<p class="md-p">${renderInline(para.join('<br>'))}</p>`)
  }

  closeLists()
  closeTable()
  return html.join('\n')
}

const renderedContent = computed(() =>
  selectedContent.value ? renderMarkdown(selectedContent.value.content || '') : ''
)

function formatTime(time?: string): string {
  if (!time) return ''
  return time.length >= 10 ? time.slice(0, 10) : time
}

onMounted(() => {
  loadContents()
})
</script>

<template>
  <div class="help-page">
    <!-- 左侧分类菜单 -->
    <aside class="help-sidebar">
      <h3 class="help-sidebar__title">帮助中心</h3>
      <ul class="help-sidebar__menu">
        <li
          v-for="cat in CATEGORIES"
          :key="cat.value"
          class="help-sidebar__item"
          :class="{ 'is-active': selectedCategory === cat.value }"
          @click="selectCategory(cat.value)"
        >
          <span class="help-sidebar__item-label">{{ cat.label }}</span>
          <span class="help-sidebar__item-count">{{ countOf(cat.value) }}</span>
        </li>
      </ul>
    </aside>

    <!-- 右侧主区域 -->
    <section class="help-main">
      <!-- 顶部搜索栏 -->
      <header class="help-header">
        <el-input
          v-model="keyword"
          placeholder="在当前分类下搜索标题或内容"
          :prefix-icon="Search"
          clearable
          class="help-header__search"
        />
        <el-button
          v-if="selectedContent"
          :icon="ArrowLeft"
          @click="backToList"
        >
          返回列表
        </el-button>
      </header>

      <!-- 内容列表视图 -->
      <div v-if="!selectedContent" v-loading="loading" class="help-list">
        <h2 class="help-list__title">{{ currentCategoryMeta.label }}</h2>
        <el-empty
          v-if="filteredContents.length === 0 && !loading"
          description="暂无帮助内容"
        />
        <ul v-else class="help-list__items">
          <li
            v-for="item in filteredContents"
            :key="item.id"
            class="help-list__item"
            @click="selectContent(item)"
          >
            <div class="help-list__item-title">{{ item.title }}</div>
            <div class="help-list__item-meta">
              <el-icon><View /></el-icon>
              <span>{{ item.viewCount ?? 0 }} 次浏览</span>
              <span v-if="item.updateTime" class="help-list__item-time">
                · 更新于 {{ formatTime(item.updateTime) }}
              </span>
            </div>
          </li>
        </ul>
      </div>

      <!-- 内容详情视图 -->
      <article v-else class="help-detail">
        <h1 class="help-detail__title">{{ selectedContent.title }}</h1>
        <div class="help-detail__meta">
          <span>分类：{{ currentCategoryMeta.label }}</span>
          <span>· {{ selectedContent.viewCount ?? 0 }} 次浏览</span>
          <span v-if="selectedContent.updateTime">
            · 更新于 {{ formatTime(selectedContent.updateTime) }}
          </span>
        </div>
        <!-- eslint-disable-next-line vue/no-v-html -- 内容来自后端管理员配置，已通过 escapeHtml 转义 -->
        <div class="md-body" v-html="renderedContent" />
      </article>
    </section>
  </div>
</template>

<style scoped>
.help-page {
  display: flex;
  height: 100%;
  min-height: calc(100vh - 100px);
  background-color: #f5f7fa;
}

.help-sidebar {
  width: 220px;
  flex-shrink: 0;
  background-color: #fff;
  border-right: 1px solid #e6e6eb;
  padding: 16px 0;
}

.help-sidebar__title {
  margin: 0 16px 12px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}

.help-sidebar__menu {
  list-style: none;
  margin: 0;
  padding: 0;
}

.help-sidebar__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  cursor: pointer;
  color: #606266;
  transition: all 0.2s ease;
}

.help-sidebar__item:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.help-sidebar__item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
  border-right: 3px solid #409eff;
}

.help-sidebar__item-label {
  font-size: 14px;
}

.help-sidebar__item-count {
  display: inline-block;
  min-width: 20px;
  padding: 0 6px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  background-color: #f0f2f5;
  color: #909399;
  border-radius: 9px;
  font-size: 12px;
}

.help-sidebar__item.is-active .help-sidebar__item-count {
  background-color: #d9ecff;
  color: #409eff;
}

.help-main {
  flex: 1;
  padding: 16px 24px;
  overflow-y: auto;
}

.help-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.help-header__search {
  width: 320px;
  max-width: 60%;
}

.help-list__title {
  margin: 0 0 16px;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.help-list__items {
  list-style: none;
  margin: 0;
  padding: 0;
  background-color: #fff;
  border-radius: 4px;
  overflow: hidden;
}

.help-list__item {
  padding: 14px 16px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.help-list__item:last-child {
  border-bottom: none;
}

.help-list__item:hover {
  background-color: #f5f7fa;
}

.help-list__item-title {
  font-size: 15px;
  font-weight: 500;
  color: #1f2d3d;
  margin-bottom: 6px;
}

.help-list__item-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.help-list__item-time {
  margin-left: 4px;
}

.help-detail {
  background-color: #fff;
  padding: 24px 32px;
  border-radius: 4px;
  min-height: 400px;
}

.help-detail__title {
  margin: 0 0 12px;
  font-size: 24px;
  font-weight: 600;
  color: #1f2d3d;
}

.help-detail__meta {
  font-size: 12px;
  color: #909399;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  gap: 8px;
}

/* Markdown 渲染样式 */
.md-body {
  line-height: 1.8;
  color: #303133;
  font-size: 14px;
}

.md-h {
  margin: 24px 0 12px;
  font-weight: 600;
  color: #1f2d3d;
}

.md-h-1 {
  font-size: 24px;
}

.md-h-2 {
  font-size: 20px;
  border-bottom: 1px solid #e6e6eb;
  padding-bottom: 8px;
}

.md-h-3 {
  font-size: 16px;
}

.md-p {
  margin: 12px 0;
}

.md-ul,
.md-ol {
  margin: 12px 0;
  padding-left: 24px;
}

.md-ul li,
.md-ol li {
  margin: 6px 0;
}

.md-inline-code {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'JetBrains Mono', 'Fira Code', Menlo, monospace;
  font-size: 13px;
  color: #d63384;
}

.md-code-block {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 12px 16px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 12px 0;
  font-family: 'JetBrains Mono', 'Fira Code', Menlo, monospace;
  font-size: 13px;
  line-height: 1.6;
}

.md-code-block code {
  background: transparent;
  color: inherit;
  padding: 0;
}

.md-table {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
  font-size: 13px;
}

.md-table th,
.md-table td {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
  text-align: left;
}

.md-table th {
  background-color: #f5f7fa;
  font-weight: 600;
}

.md-table tr:nth-child(even) td {
  background-color: #fafbfc;
}

.md-hr {
  border: none;
  border-top: 1px solid #e6e6eb;
  margin: 20px 0;
}

.md-body :deep(strong) {
  font-weight: 600;
  color: #1f2d3d;
}

.md-body :deep(a) {
  color: #409eff;
  text-decoration: none;
}

.md-body :deep(a:hover) {
  text-decoration: underline;
}
</style>
