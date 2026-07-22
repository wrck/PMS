<script setup lang="ts">
// =============================================================================
// DeliverableContentBlockEditor - 交付件结构化文档内容块编辑器
// -----------------------------------------------------------------------------
// 借鉴问卷功能的设计：交付件文档内容由若干内容块有序组成，支持动态配置：
//   - 富文本 RICH_TEXT    ：contenteditable 编辑器 + 工具栏
//   - 内嵌表 TABLE        ：可编辑单元格，列头可增删，行可增删
//   - 选项卡 TABS         ：动态标签，每标签为富文本/文本
//   - 标题   HEADING      ：1-4 级标题
//   - 分隔线 DIVIDER      ：无内容
//   - 代码块 CODE_BLOCK   ：language 选择 + textarea
// 所有 blockType 取值由字典 pms_deliverable_block_type 维护，不硬编码枚举。
// 支持 disabled 只读模式（详情页渲染）。
// =============================================================================
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  loadDeliverableBlockTypes,
  translateBlockType,
  type DeliverableBlockType,
  type DeliverableContentBlock,
  type SysDictItem
} from '@/api/deliverable'

defineOptions({ name: 'DeliverableContentBlockEditor' })

interface Props {
  /** 内容块数组（可为 null） */
  modelValue?: DeliverableContentBlock[] | null
  /** 是否只读（详情页渲染用） */
  disabled?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  modelValue: null,
  disabled: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', val: DeliverableContentBlock[] | null): void
}>()

// ============ 字典加载 ============
const blockTypeOptions = ref<SysDictItem[]>([])

onMounted(async () => {
  try {
    blockTypeOptions.value = await loadDeliverableBlockTypes()
  } catch {
    blockTypeOptions.value = []
  }
})

// ============ 内容块列表 ============
const blocks = computed<DeliverableContentBlock[]>(() => props.modelValue ?? [])

// ============ 类型 → el-tag 颜色映射 ============
const BLOCK_TYPE_TAG_COLOR: Record<string, string> = {
  RICH_TEXT: 'success',
  TABLE: 'primary',
  TABS: 'warning',
  HEADING: 'info',
  DIVIDER: 'info',
  CODE_BLOCK: 'danger'
}

function tagTypeOf(blockType?: string): string {
  return BLOCK_TYPE_TAG_COLOR[blockType ?? ''] ?? 'info'
}

// ============ 代码语言常量 ============
const codeLanguages = [
  { label: '纯文本', value: 'text' },
  { label: 'Java', value: 'java' },
  { label: 'JavaScript', value: 'javascript' },
  { label: 'TypeScript', value: 'typescript' },
  { label: 'Python', value: 'python' },
  { label: 'SQL', value: 'sql' },
  { label: 'Shell', value: 'bash' },
  { label: 'JSON', value: 'json' },
  { label: 'XML/HTML', value: 'html' },
  { label: 'CSS', value: 'css' },
  { label: 'INI/Properties', value: 'ini' },
  { label: 'YAML', value: 'yaml' },
  { label: 'Go', value: 'go' },
  { label: 'Rust', value: 'rust' }
]

// ============ 添加内容块弹窗 ============
const addDialogVisible = ref(false)
const addForm = ref<{ blockType: DeliverableBlockType; blockKey: string; blockTitle: string }>({
  blockType: 'RICH_TEXT',
  blockKey: '',
  blockTitle: ''
})

function openAddBlock() {
  if (props.disabled) return
  addForm.value = { blockType: 'RICH_TEXT', blockKey: '', blockTitle: '' }
  addDialogVisible.value = true
}

/** 按类型创建默认结构的内容块 */
function createEmptyBlock(blockType: DeliverableBlockType, blockKey: string, blockTitle: string): DeliverableContentBlock {
  const key = blockKey || `${String(blockType).toLowerCase()}_${Date.now()}`
  const title = blockTitle || translateBlockType(String(blockType))
  const sortOrder = blocks.value.length + 1
  switch (blockType) {
    case 'RICH_TEXT':
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: {},
        blockContent: '',
        sortOrder
      }
    case 'TABLE':
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: { columns: ['列1', '列2', '列3'] },
        blockContent: [] as string[][],
        sortOrder
      }
    case 'TABS':
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: {},
        blockContent: {} as Record<string, string>,
        sortOrder
      }
    case 'HEADING':
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: { level: 1 },
        blockContent: '',
        sortOrder
      }
    case 'DIVIDER':
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: {},
        blockContent: null,
        sortOrder
      }
    case 'CODE_BLOCK':
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: { language: 'text' },
        blockContent: '',
        sortOrder
      }
    default:
      return {
        blockType,
        blockKey: key,
        blockTitle: title,
        blockConfig: {},
        blockContent: '',
        sortOrder
      }
  }
}

function confirmAddBlock() {
  if (!addForm.value.blockType) {
    ElMessage.warning('请选择内容块类型')
    return
  }
  const newBlock = createEmptyBlock(addForm.value.blockType, addForm.value.blockKey, addForm.value.blockTitle)
  const next = [...blocks.value, newBlock]
  emitChange(next)
  addDialogVisible.value = false
}

// ============ 块级操作：移除/上移/下移/更新 ============
function removeBlock(index: number) {
  if (props.disabled) return
  const next = blocks.value.filter((_, i) => i !== index)
  emitChange(next)
}

function moveBlock(index: number, direction: 'up' | 'down') {
  if (props.disabled) return
  const target = direction === 'up' ? index - 1 : index + 1
  if (target < 0 || target >= blocks.value.length) return
  const next = [...blocks.value]
  const tmp = next[index]
  next[index] = next[target]
  next[target] = tmp
  emitChange(next)
}

/** 更新块的任意字段 */
function updateBlock(index: number, patch: Partial<DeliverableContentBlock>) {
  if (props.disabled) return
  const next = blocks.value.map((b, i) => (i === index ? { ...b, ...patch } : b))
  emitChange(next)
}

/** 更新块的 blockConfig 局部字段 */
function updateBlockConfig(index: number, configPatch: Record<string, unknown>) {
  if (props.disabled) return
  const next = blocks.value.map((b, i) => {
    if (i !== index) return b
    const oldConfig = (b.blockConfig ?? {}) as Record<string, unknown>
    return { ...b, blockConfig: { ...oldConfig, ...configPatch } }
  })
  emitChange(next)
}

/** 深拷贝并重算 sortOrder 后 emit */
function emitChange(next: DeliverableContentBlock[]) {
  const reordered = next.map((b, i) => ({ ...b, sortOrder: i + 1 }))
  emit('update:modelValue', reordered)
}

// ============ 表格块操作 ============
function tableColumns(block: DeliverableContentBlock): string[] {
  const cfg = block.blockConfig as { columns?: string[] } | undefined
  return cfg?.columns ?? []
}

function tableRows(block: DeliverableContentBlock): string[][] {
  const content = block.blockContent
  if (!Array.isArray(content)) return []
  return content as string[][]
}

function addTableRow(blockIndex: number) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const cols = tableColumns(block)
  const newRow = Array.from({ length: cols.length }, () => '')
  const oldRows = tableRows(block)
  const newContent = [...oldRows, newRow]
  updateBlock(blockIndex, { blockContent: newContent })
}

function removeTableRow(blockIndex: number, rowIndex: number) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const oldRows = tableRows(block)
  const newRows = oldRows.filter((_, i) => i !== rowIndex)
  updateBlock(blockIndex, { blockContent: newRows })
}

function addTableColumn(blockIndex: number) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const oldCols = tableColumns(block)
  const newCols = [...oldCols, `列${oldCols.length + 1}`]
  // 同步给已有行补空单元格
  const oldRows = tableRows(block)
  const newRows = oldRows.map((row) => {
    const padded = [...row]
    while (padded.length < newCols.length) padded.push('')
    return padded
  })
  const newConfig = { ...((block.blockConfig ?? {}) as Record<string, unknown>), columns: newCols }
  const next = blocks.value.map((b, i) =>
    i === blockIndex ? { ...b, blockConfig: newConfig, blockContent: newRows } : b
  )
  emitChange(next)
}

function removeTableColumn(blockIndex: number, colIndex: number) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const oldCols = tableColumns(block)
  if (oldCols.length <= 1) {
    ElMessage.warning('至少保留一列')
    return
  }
  const newCols = oldCols.filter((_, i) => i !== colIndex)
  const oldRows = tableRows(block)
  const newRows = oldRows.map((row) => row.filter((_, i) => i !== colIndex))
  const newConfig = { ...((block.blockConfig ?? {}) as Record<string, unknown>), columns: newCols }
  const next = blocks.value.map((b, i) =>
    i === blockIndex ? { ...b, blockConfig: newConfig, blockContent: newRows } : b
  )
  emitChange(next)
}

function updateTableCell(blockIndex: number, rowIndex: number, colIndex: number, value: string) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const oldRows = tableRows(block).map((r) => [...r])
  if (!oldRows[rowIndex]) {
    while (oldRows.length <= rowIndex) {
      const cols = tableColumns(block)
      oldRows.push(Array.from({ length: cols.length }, () => ''))
    }
  }
  const padded = oldRows[rowIndex]
  while (padded.length <= colIndex) padded.push('')
  padded[colIndex] = value
  oldRows[rowIndex] = padded
  updateBlock(blockIndex, { blockContent: oldRows })
}

// ============ 选项卡块操作 ============
function tabContentMap(block: DeliverableContentBlock): Record<string, string> {
  const content = block.blockContent
  if (content && typeof content === 'object' && !Array.isArray(content)) {
    return content as Record<string, string>
  }
  return {}
}

function tabNames(block: DeliverableContentBlock): string[] {
  return Object.keys(tabContentMap(block))
}

function addTab(blockIndex: number) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const map = { ...tabContentMap(block) }
  const baseName = '标签'
  let idx = 1
  while (map[`${baseName}${idx}`]) idx++
  const newName = `${baseName}${idx}`
  map[newName] = ''
  updateBlock(blockIndex, { blockContent: map })
}

function removeTab(blockIndex: number, tabName: string) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const map = { ...tabContentMap(block) }
  delete map[tabName]
  updateBlock(blockIndex, { blockContent: map })
}

function updateTabContent(blockIndex: number, tabName: string, value: string) {
  if (props.disabled) return
  const block = blocks.value[blockIndex]
  if (!block) return
  const map = { ...tabContentMap(block) }
  map[tabName] = value
  updateBlock(blockIndex, { blockContent: map })
}

// ============ 富文本块编辑（document.execCommand） ============
/**
 * 富文本编辑器 DOM 元素缓存（key 为 blockIndex）。
 *
 * <p>不使用 v-html 绑定 blockContent，因为 v-html 在每次输入后会重置 innerHTML，
 * 导致光标跳回开头。改为：
 * <ol>
 *   <li>挂载时通过 setRichEditorEl 设置初始 innerHTML</li>
 *   <li>用户输入时通过 @input 同步到 blockContent（不触发 DOM 重渲染）</li>
 *   <li>watch blocks 时仅在外部内容变化时（如加载模板）才更新 innerHTML</li>
 * </ol>
 */
const richEditorEls = ref<Map<number, HTMLElement>>(new Map())

/** v-for 中收集富文本编辑器 DOM 元素并设置初始内容 */
function setRichEditorEl(el: unknown, blockIndex: number) {
  if (el instanceof HTMLElement) {
    richEditorEls.value.set(blockIndex, el)
    const block = blocks.value[blockIndex]
    const expected = String(block?.blockContent ?? '')
    if (el.innerHTML !== expected) {
      el.innerHTML = expected
    }
  } else {
    richEditorEls.value.delete(blockIndex)
  }
}

/** 外部内容变化时同步 DOM（仅当 DOM 内容与模型不一致时才更新，避免光标跳跃） */
watch(
  () => blocks.value,
  (newBlocks) => {
    richEditorEls.value.forEach((el, idx) => {
      const block = newBlocks[idx]
      if (!block || block.blockType !== 'RICH_TEXT') return
      const expected = String(block.blockContent ?? '')
      if (el.innerHTML !== expected) {
        el.innerHTML = expected
      }
    })
  },
  { deep: true }
)

const activeRichBlockIndex = ref<number>(-1)

function focusRichEditor(blockIndex: number) {
  activeRichBlockIndex.value = blockIndex
}

function execRichText(command: string, value?: string) {
  if (props.disabled) return
  if (activeRichBlockIndex.value < 0) {
    ElMessage.warning('请先点击富文本编辑区')
    return
  }
  const el = richEditorEls.value.get(activeRichBlockIndex.value)
  if (!el) return
  el.focus()
  try {
    // eslint-disable-next-line deprecation/deprecation
    document.execCommand(command, false, value)
  } catch {
    /* ignore unsupported command */
  }
  syncRichContent(activeRichBlockIndex.value)
}

function onRichTextInput(blockIndex: number) {
  syncRichContent(blockIndex)
}

/** 把 contenteditable 元素的 innerHTML 同步到 blockContent */
function syncRichContent(blockIndex: number) {
  const el = richEditorEls.value.get(blockIndex)
  if (!el) return
  updateBlock(blockIndex, { blockContent: el.innerHTML })
}

// 链接插入
const linkDialogVisible = ref(false)
const linkUrl = ref('')

function openLinkDialog() {
  if (activeRichBlockIndex.value < 0) {
    ElMessage.warning('请先点击富文本编辑区')
    return
  }
  linkUrl.value = ''
  linkDialogVisible.value = true
  // 保留选区：失焦后浏览器仍可记住 selection（execCommand 需在选区内执行）
  nextTick(() => {
    /* no-op，等待用户输入 */
  })
}

function confirmInsertLink() {
  if (!linkUrl.value) {
    ElMessage.warning('请填写链接地址')
    return
  }
  execRichText('createLink', linkUrl.value)
  linkDialogVisible.value = false
}

// ============ 标题级别选项 ============
const headingLevels = [
  { label: 'H1', value: 1 },
  { label: 'H2', value: 2 },
  { label: 'H3', value: 3 },
  { label: 'H4', value: 4 }
]
</script>

<template>
  <div class="content-block-editor">
    <!-- 工具栏 -->
    <div class="editor-toolbar">
      <span class="toolbar-title">内容块（{{ blocks.length }}）</span>
      <el-button v-if="!disabled" type="primary" size="small" @click="openAddBlock">+ 添加内容块</el-button>
    </div>

    <!-- 空提示 -->
    <el-empty
      v-if="blocks.length === 0"
      description="暂无内容块，点击「添加内容块」开始配置"
      :image-size="60"
    />

    <!-- 内容块卡片列表 -->
    <div
      v-for="(block, index) in blocks"
      :key="`${block.blockKey}-${index}`"
      class="block-card"
    >
      <!-- 卡片头 -->
      <div class="block-header">
        <el-tag :type="tagTypeOf(block.blockType)" size="small" effect="light">
          {{ translateBlockType(block.blockType) }}
        </el-tag>
        <el-input
          :model-value="block.blockTitle"
          :disabled="disabled"
          size="small"
          placeholder="块标题"
          class="block-title-input"
          @update:model-value="(v: string) => updateBlock(index, { blockTitle: v })"
        />
        <div v-if="!disabled" class="block-actions">
          <el-button link size="small" :disabled="index === 0" @click="moveBlock(index, 'up')">上移</el-button>
          <el-button
            link
            size="small"
            :disabled="index === blocks.length - 1"
            @click="moveBlock(index, 'down')"
          >下移</el-button>
          <el-button link type="danger" size="small" @click="removeBlock(index)">删除</el-button>
        </div>
      </div>

      <!-- 卡片体：按 blockType 渲染编辑器 -->
      <div class="block-body">
        <!-- HEADING -->
        <template v-if="block.blockType === 'HEADING'">
          <div class="heading-editor">
            <el-select
              v-if="!disabled"
              :model-value="(block.blockConfig as { level?: number })?.level ?? 1"
              size="small"
              style="width: 100px"
              @update:model-value="(v: number) => updateBlockConfig(index, { level: v })"
            >
              <el-option v-for="lvl in headingLevels" :key="lvl.value" :label="lvl.label" :value="lvl.value" />
            </el-select>
            <input
              :value="String(block.blockContent ?? '')"
              :disabled="disabled"
              class="heading-input"
              :class="`heading-level-${(block.blockConfig as { level?: number })?.level ?? 1}`"
              placeholder="标题文本"
              @input="updateBlock(index, { blockContent: ($event.target as HTMLInputElement).value })"
            >
          </div>
        </template>

        <!-- DIVIDER -->
        <template v-else-if="block.blockType === 'DIVIDER'">
          <el-divider />
        </template>

        <!-- RICH_TEXT -->
        <template v-else-if="block.blockType === 'RICH_TEXT'">
          <div v-if="!disabled" class="rich-toolbar">
            <el-button-group size="small">
              <el-button @click="execRichText('bold')">B</el-button>
              <el-button @click="execRichText('italic')"><i>I</i></el-button>
              <el-button @click="execRichText('underline')">U</el-button>
              <el-button @click="execRichText('strikeThrough')">S</el-button>
            </el-button-group>
            <el-button-group size="small">
              <el-button @click="execRichText('insertUnorderedList')">•列表</el-button>
              <el-button @click="execRichText('insertOrderedList')">1.列表</el-button>
              <el-button @click="execRichText('formatBlock', '<h3>')">标题</el-button>
              <el-button @click="execRichText('formatBlock', '<blockquote>')">引用</el-button>
            </el-button-group>
            <el-button size="small" @click="openLinkDialog">链接</el-button>
            <el-button size="small" @click="execRichText('removeFormat')">清除格式</el-button>
          </div>
          <div
            :ref="(el) => setRichEditorEl(el, index)"
            class="rich-editor"
            :class="{ 'is-disabled': disabled }"
            :contenteditable="!disabled"
            @focus="focusRichEditor(index)"
            @input="onRichTextInput(index)"
          />
        </template>

        <!-- CODE_BLOCK -->
        <template v-else-if="block.blockType === 'CODE_BLOCK'">
          <div class="code-editor">
            <el-select
              v-if="!disabled"
              :model-value="(block.blockConfig as { language?: string })?.language ?? 'text'"
              size="small"
              filterable
              style="width: 160px; margin-bottom: 8px"
              @update:model-value="(v: string) => updateBlockConfig(index, { language: v })"
            >
              <el-option v-for="lang in codeLanguages" :key="lang.value" :label="lang.label" :value="lang.value" />
            </el-select>
            <pre v-if="disabled" class="code-preview">{{ String(block.blockContent ?? '') }}</pre>
            <el-input
              v-else
              :model-value="String(block.blockContent ?? '')"
              type="textarea"
              :rows="8"
              autosize
              placeholder="在此输入代码..."
              @update:model-value="(v: string) => updateBlock(index, { blockContent: v })"
            />
          </div>
        </template>

        <!-- TABLE -->
        <template v-else-if="block.blockType === 'TABLE'">
          <div class="table-editor">
            <div v-if="!disabled" class="table-toolbar">
              <el-button size="small" @click="addTableRow(index)">+ 行</el-button>
              <el-button size="small" @click="addTableColumn(index)">+ 列</el-button>
            </div>
            <el-table :data="tableRows(block)" border size="small" style="width: 100%">
              <el-table-column
                v-for="(col, colIdx) in tableColumns(block)"
                :key="colIdx"
                :label="col"
                min-width="120"
              >
                <template #header>
                  <span>{{ col }}</span>
                  <el-button
                    v-if="!disabled"
                    link
                    type="danger"
                    size="small"
                    style="margin-left: 4px"
                    @click="removeTableColumn(index, colIdx)"
                  >×</el-button>
                </template>
                <template #default="{ row, $index }">
                  <el-input
                    v-if="!disabled"
                    :model-value="String(row[colIdx] ?? '')"
                    size="small"
                    @update:model-value="(v: string) => updateTableCell(index, $index, colIdx, v)"
                  />
                  <span v-else>{{ row[colIdx] ?? '' }}</span>
                </template>
              </el-table-column>
              <el-table-column v-if="!disabled" label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <el-button link type="danger" size="small" @click="removeTableRow(index, $index)">删行</el-button>
                </template>
              </el-table-column>
              <template #empty>
                <span style="color: #909399; font-size: 12px">暂无数据，点击「+ 行」添加</span>
              </template>
            </el-table>
          </div>
        </template>

        <!-- TABS -->
        <template v-else-if="block.blockType === 'TABS'">
          <div class="tabs-editor">
            <div v-if="!disabled" class="table-toolbar">
              <el-button size="small" @click="addTab(index)">+ 标签</el-button>
            </div>
            <el-tabs v-if="tabNames(block).length > 0" type="border-card">
              <el-tab-pane
                v-for="tabName in tabNames(block)"
                :key="tabName"
                :label="tabName"
              >
                <el-input
                  v-if="!disabled"
                  :model-value="tabContentMap(block)[tabName] ?? ''"
                  type="textarea"
                  :rows="6"
                  :placeholder="`标签「${tabName}」的内容`"
                  @update:model-value="(v: string) => updateTabContent(index, tabName, v)"
                />
                <pre v-else class="tab-content-preview">{{ tabContentMap(block)[tabName] ?? '' }}</pre>
                <el-button
                  v-if="!disabled"
                  link
                  type="danger"
                  size="small"
                  style="margin-top: 8px"
                  @click="removeTab(index, tabName)"
                >移除标签</el-button>
              </el-tab-pane>
            </el-tabs>
            <el-empty v-else description="暂无标签，点击「+ 标签」添加" :image-size="40" />
          </div>
        </template>

        <!-- 兜底：未知类型 -->
        <template v-else>
          <div class="unknown-block">未知内容块类型：{{ block.blockType }}</div>
        </template>
      </div>
    </div>

    <!-- 添加内容块弹窗 -->
    <el-dialog v-model="addDialogVisible" title="添加内容块" width="480px" destroy-on-close>
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="块类型" required>
          <el-select v-model="addForm.blockType" placeholder="选择内容块类型" style="width: 100%">
            <el-option
              v-for="opt in blockTypeOptions"
              :key="opt.itemValue"
              :label="opt.itemText"
              :value="opt.itemValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="块标识">
          <el-input
            v-model="addForm.blockKey"
            placeholder="同一交付件内唯一，如 title/body；留空自动生成"
          />
        </el-form-item>
        <el-form-item label="块标题">
          <el-input v-model="addForm.blockTitle" placeholder="展示用，留空使用类型名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddBlock">确定</el-button>
      </template>
    </el-dialog>

    <!-- 插入链接弹窗 -->
    <el-dialog v-model="linkDialogVisible" title="插入链接" width="420px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="链接地址" required>
          <el-input v-model="linkUrl" placeholder="https://..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="linkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmInsertLink">插入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.content-block-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
}

.toolbar-title {
  font-size: 13px;
  color: var(--pms-color-text-secondary, #909399);
  font-weight: 500;
}

.block-card {
  border: 1px solid var(--pms-color-border-light, #ebeef5);
  border-radius: 6px;
  background: var(--pms-color-bg-card, #fff);
  overflow: hidden;
}

.block-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: var(--pms-color-bg-page, #f5f7fa);
  border-bottom: 1px solid var(--pms-color-border-light, #ebeef5);
}

.block-title-input {
  flex: 1;
}

.block-actions {
  display: flex;
  gap: 4px;
}

.block-body {
  padding: 12px;
}

/* HEADING */
.heading-editor {
  display: flex;
  align-items: center;
  gap: 8px;
}

.heading-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-weight: 600;
  padding: 4px 0;
  color: var(--pms-color-text-primary, #303133);
}

.heading-input.heading-level-1 {
  font-size: 22px;
}

.heading-input.heading-level-2 {
  font-size: 19px;
}

.heading-input.heading-level-3 {
  font-size: 16px;
}

.heading-input.heading-level-4 {
  font-size: 14px;
}

/* RICH_TEXT */
.rich-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  padding: 6px 0;
  margin-bottom: 6px;
  border-bottom: 1px dashed var(--pms-color-border-light, #ebeef5);
}

.rich-editor {
  min-height: 120px;
  padding: 8px 12px;
  border: 1px solid var(--pms-color-border-light, #ebeef5);
  border-radius: 4px;
  outline: none;
  font-size: 14px;
  line-height: 1.6;
  color: var(--pms-color-text-regular, #606266);
  overflow-y: auto;
}

.rich-editor:focus {
  border-color: var(--el-color-primary, #409eff);
}

.rich-editor.is-disabled {
  cursor: default;
  background: var(--pms-color-bg-page, #f5f7fa);
}

.rich-editor :deep(ul),
.rich-editor :deep(ol) {
  padding-left: 24px;
  margin: 4px 0;
}

.rich-editor :deep(blockquote) {
  margin: 4px 0;
  padding-left: 12px;
  border-left: 3px solid var(--pms-color-border-light, #ebeef5);
  color: var(--pms-color-text-secondary, #909399);
}

/* CODE_BLOCK */
.code-editor pre.code-preview {
  margin: 0;
  padding: 12px;
  background: #f6f8fa;
  border-radius: 4px;
  font-family: 'Courier New', Consolas, monospace;
  font-size: 13px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
  color: #24292e;
}

/* TABLE */
.table-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.table-toolbar {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* TABS */
.tabs-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tab-content-preview {
  margin: 0;
  padding: 8px;
  background: var(--pms-color-bg-page, #f5f7fa);
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  color: var(--pms-color-text-regular, #606266);
}

/* 兜底 */
.unknown-block {
  padding: 8px 12px;
  color: var(--el-color-danger, #f56c6c);
  font-size: 13px;
}
</style>
