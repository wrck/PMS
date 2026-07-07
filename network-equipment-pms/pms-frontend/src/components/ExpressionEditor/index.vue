<script setup lang="ts">
/**
 * 表达式编辑器组件（借鉴 Budibase Bindings Drawer）。
 *
 * <p>基于 @guolao/vue-monaco-editor 封装，提供：</p>
 * <ul>
 *   <li>语法高亮（JavaScript/Groovy）</li>
 *   <li>字段/变量自动补全：输入 fields. / vars. 触发对应列表</li>
 *   <li>常用函数库提示（math/string/date）</li>
 *   <li>左侧绑定树侧栏（可折叠 + 可搜索），点击叶子插入到光标位置</li>
 *   <li>v-model 双向绑定 modelValue</li>
 * </ul>
 *
 * <p>使用 loader.config 指向本地安装的 monaco-editor，避免 CDN 依赖；
 * 通过 self.MonacoEnvironment 注册 worker（Vite ?worker 语法）。</p>
 */
import { computed, onBeforeUnmount, ref, shallowRef, watch } from 'vue'
import { VueMonacoEditor, loader } from '@guolao/vue-monaco-editor'
import * as monaco from 'monaco-editor'
import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'
import {
  buildBindingTree,
  flattenBindings,
  type BindingItem,
  type BindingNode
} from './bindings'

defineOptions({ name: 'ExpressionEditor' })

const props = withDefaults(
  defineProps<{
    /** 表达式字符串（v-model） */
    modelValue: string
    /** 语言：javascript / groovy，默认 javascript */
    language?: string
    /** 可用字段列表 */
    fields?: BindingItem[]
    /** 可用变量列表 */
    variables?: BindingItem[]
    /** 编辑器高度（px），默认 200 */
    height?: number
  }>(),
  {
    language: 'javascript',
    fields: () => [],
    variables: () => [],
    height: 200
  }
)

const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

// ===================== Monaco loader / worker 配置 =====================

// 注册 worker（仅注册一次；多次赋值以最后一次为准，安全）
;(self as unknown as { MonacoEnvironment: monaco.Environment }).MonacoEnvironment = {
  getWorker() {
    return new editorWorker()
  }
}
// 使用本地安装的 monaco-editor，避免从 CDN 加载
loader.config({ monaco })

// ===================== v-model 双向绑定 =====================

const code = ref(props.modelValue || '')
watch(
  () => props.modelValue,
  (v) => {
    if (v !== code.value) code.value = v ?? ''
  }
)
function onCodeChange(v: string | undefined) {
  code.value = v ?? ''
  emit('update:modelValue', v ?? '')
}

// ===================== 编辑器实例 + completion provider =====================

const editorRef = shallowRef<monaco.editor.IStandaloneCodeEditor | null>(null)
let completionDisposer: monaco.IDisposable | null = null

/** 当前语言对应的 monaco language id */
const monacoLanguage = computed(() => (props.language === 'groovy' ? 'java' : 'javascript'))

function handleMount(editor: monaco.editor.IStandaloneCodeEditor) {
  editorRef.value = editor
  registerCompletion()
}

/** 注册补全：输入 fields. / vars. 时弹出对应列表，并提供函数库提示 */
function registerCompletion() {
  if (completionDisposer) {
    completionDisposer.dispose()
    completionDisposer = null
  }
  const flat = flattenBindings(buildBindingTree(props.fields, props.variables))
  const fieldsItems = flat.filter((b) => b.prefix === 'fields')
  const varsItems = flat.filter((b) => b.prefix === 'vars')
  const funcItems = flat.filter((b) => b.prefix === '')

  completionDisposer = monaco.languages.registerCompletionItemProvider(monacoLanguage.value, {
    triggerCharacters: ['.'],
    provideCompletionItems(model, position) {
      const word = model.getWordUntilPosition(position)
      const lineUntil = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column
      })
      const range = {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: word.startColumn,
        endColumn: word.endColumn
      }

      // 输入 fields. 时补全字段
      if (/fields\.\w*$/.test(lineUntil)) {
        return {
          suggestions: fieldsItems.map((b) => ({
            label: b.name,
            kind: monaco.languages.CompletionItemKind.Field,
            insertText: b.insertText,
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            detail: b.detail || b.type,
            range
          }))
        }
      }
      // 输入 vars. 时补全变量
      if (/vars\.\w*$/.test(lineUntil)) {
        return {
          suggestions: varsItems.map((b) => ({
            label: b.name,
            kind: monaco.languages.CompletionItemKind.Variable,
            insertText: b.insertText,
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            detail: b.detail || b.type,
            range
          }))
        }
      }
      // 函数库（输入字母时按需提示，前缀过滤）
      return {
        suggestions: funcItems.map((b) => ({
          label: b.name,
          kind: monaco.languages.CompletionItemKind.Function,
          insertText: b.insertText,
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: b.detail,
          range
        }))
      }
    }
  })
}

// 字段/变量变化时重新注册补全
watch(
  () => [props.fields, props.variables],
  () => {
    if (editorRef.value) registerCompletion()
  },
  { deep: true }
)

// ===================== 绑定树侧栏 =====================

const sidebarCollapsed = ref(false)
const searchKeyword = ref('')
/** 默认展开全部类别 */
const defaultExpandedKeys = ref<string[]>(['fields', 'vars', 'functions'])

const bindingTree = computed(() => buildBindingTree(props.fields, props.variables))

/** 树的 ref（Element Plus el-tree） */
const treeRef = ref<any>(null)

/** 按关键字过滤绑定树（保留命中叶子及其父节点） */
const filteredTree = computed<BindingNode[]>(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return bindingTree.value
  return bindingTree.value
    .map((group) => {
      const children = (group.children || []).filter((leaf) =>
        leaf.label.toLowerCase().includes(kw)
      )
      return children.length ? { ...group, children } : null
    })
    .filter((g): g is BindingNode => g !== null)
})

/** 点击绑定叶子：插入到编辑器光标位置 */
function onNodeClick(data: BindingNode) {
  if (!data.insertText) return
  const editor = editorRef.value
  if (!editor) return
  const position = editor.getPosition()
  if (!position) return
  // 把 snippet 占位符 ${1:xxx} 转为普通文本插入（简单实现：取占位符默认文本）
  const text = data.insertText.replace(/\$\{[^}]*\}/g, '')
  editor.executeEdits('expression-editor-insert', [
    {
      range: new monaco.Range(
        position.lineNumber,
        position.column,
        position.lineNumber,
        position.column
      ),
      text
    }
  ])
  editor.focus()
}

// ===================== 卸载清理 =====================

onBeforeUnmount(() => {
  if (completionDisposer) {
    completionDisposer.dispose()
    completionDisposer = null
  }
  editorRef.value?.dispose()
})
</script>

<template>
  <div class="expression-editor" :style="{ height: `${height}px` }">
    <!-- 左侧：绑定树侧栏 -->
    <div v-show="!sidebarCollapsed" class="binding-sidebar">
      <div class="sidebar-header">
        <span class="sidebar-title">绑定</span>
        <el-button
          link
          size="small"
          :icon="'Fold'"
          title="折叠侧栏"
          @click="sidebarCollapsed = true"
        />
      </div>
      <el-input
        v-model="searchKeyword"
        size="small"
        placeholder="搜索字段/变量/函数"
        clearable
        class="sidebar-search"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <div class="sidebar-tree">
        <el-tree
          ref="treeRef"
          :data="filteredTree"
          :props="{ label: 'label', children: 'children' }"
          :default-expanded-keys="defaultExpandedKeys"
          node-key="key"
          @node-click="onNodeClick"
        >
          <template #default="{ data: node }">
            <span class="tree-node" :title="node.description || node.label">
              <el-icon v-if="node.children" class="tree-node-icon"><Folder /></el-icon>
              <el-icon v-else class="tree-node-icon"><Document /></el-icon>
              <span class="tree-node-label">{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
    </div>

    <!-- 折叠态：竖条展开按钮 -->
    <div v-if="sidebarCollapsed" class="sidebar-collapsed" @click="sidebarCollapsed = false">
      <el-icon><Expand /></el-icon>
      <span>绑定</span>
    </div>

    <!-- 右侧：Monaco 编辑器 -->
    <div class="editor-wrapper">
      <VueMonacoEditor
        :value="code"
        :language="monacoLanguage"
        theme="vs"
        :options="{
          minimap: { enabled: false },
          fontSize: 13,
          lineNumbers: 'on',
          automaticLayout: true,
          scrollBeyondLastLine: false,
          wordWrap: 'on',
          tabSize: 2
        }"
        :height="height"
        @mount="handleMount"
        @update:value="(v: string | undefined) => onCodeChange(v)"
      />
    </div>
  </div>
</template>

<style scoped>
.expression-editor {
  display: flex;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
  background: var(--el-bg-color);
}

.binding-sidebar {
  width: 200px;
  border-right: 1px solid var(--el-border-color-lighter);
  display: flex;
  flex-direction: column;
  background: var(--el-fill-color-blank);
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.sidebar-title {
  font-size: 13px;
  font-weight: 600;
}

.sidebar-search {
  margin: 6px 8px;
}

.sidebar-tree {
  flex: 1;
  overflow: auto;
  padding: 0 4px 6px;
}

.sidebar-tree :deep(.el-tree-node__content) {
  height: 26px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  width: 100%;
  overflow: hidden;
}

.tree-node-icon {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.tree-node-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar-collapsed {
  width: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-blank);
  cursor: pointer;
  writing-mode: vertical-rl;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.sidebar-collapsed:hover {
  color: var(--el-color-primary);
}

.sidebar-collapsed :deep(.el-icon) {
  writing-mode: horizontal-tb;
}

.editor-wrapper {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}
</style>
