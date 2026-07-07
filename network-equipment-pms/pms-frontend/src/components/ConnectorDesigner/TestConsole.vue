<!-- src/components/ConnectorDesigner/TestConsole.vue -->
<script setup lang="ts">
/**
 * 连接器测试控制台（Task 10）。
 *
 * <p>借鉴 Power Apps Custom Connectors 的 Test 标签页：
 * <ol>
 *   <li>选择操作（下拉）</li>
 *   <li>根据操作的 params / body 动态生成输入表单</li>
 *   <li>点击「发送」调用 testOperation API 测试单个操作</li>
 *   <li>展示响应：状态码（带颜色）、Headers 表格、Body（JSON 树形）、耗时</li>
 *   <li>请求历史（最近 10 次）：点击可重新加载请求参数和响应</li>
 * </ol></p>
 */
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import JsonTree from './JsonTree.vue'
import {
  testOperation,
  type ConnectorType,
  type RestOperation,
  type SqlTemplate,
  type TestOperationResult
} from '@/api/lowcode-connector'

const props = defineProps<{
  /** 连接器编码（已保存后用于调用测试接口） */
  connectorCode: string
  type: ConnectorType
  operations: RestOperation[]
  sqlTemplates: SqlTemplate[]
}>()

interface HistoryItem {
  id: number
  operationName: string
  params: Record<string, unknown>
  body: unknown
  result: TestOperationResult | null
  timestamp: number
}

const selectedOpName = ref<string>('')
const paramValues = ref<Record<string, string>>({})
const bodyText = ref<string>('')
const sending = ref(false)
const currentResult = ref<TestOperationResult | null>(null)
const history = ref<HistoryItem[]>([])
let historyIdSeq = 0

const availableOps = computed(() => {
  if (props.type === 'REST') {
    return props.operations
  }
  return props.sqlTemplates.map((t) => ({
    name: t.name,
    method: 'GET' as const,
    path: '',
    headers: [],
    params: [],
    body: null
  }))
})

const currentOp = computed<RestOperation | null>(() => {
  if (!selectedOpName.value) return null
  if (props.type === 'REST') {
    return props.operations.find((o) => o.name === selectedOpName.value) || null
  }
  const t = props.sqlTemplates.find((s) => s.name === selectedOpName.value)
  if (!t) return null
  return {
    name: t.name,
    method: 'GET',
    path: '',
    headers: [],
    params: [],
    body: null
  }
})

const showBody = computed(
  () =>
    props.type === 'REST' &&
    currentOp.value !== null &&
    (currentOp.value.method === 'POST' || currentOp.value.method === 'PUT')
)

watch(selectedOpName, () => {
  resetForm()
})

function resetForm() {
  paramValues.value = {}
  if (currentOp.value) {
    currentOp.value.params.forEach((p) => {
      paramValues.value[p.key] = ''
    })
  }
  bodyText.value = ''
  currentResult.value = null
}

function statusClass(code: number | undefined): string {
  if (code === undefined) return 'status-unknown'
  if (code >= 200 && code < 300) return 'status-2xx'
  if (code >= 300 && code < 400) return 'status-3xx'
  if (code >= 400 && code < 500) return 'status-4xx'
  if (code >= 500) return 'status-5xx'
  return 'status-unknown'
}

function statusText(code: number | undefined): string {
  if (code === undefined) return '—'
  return String(code)
}

const headerRows = computed<[string, string][]>(() => {
  if (!currentResult.value?.headers) return []
  return Object.entries(currentResult.value.headers)
})

const hasResult = computed(() => currentResult.value !== null)
const hasHistory = computed(() => history.value.length > 0)

async function send() {
  if (!selectedOpName.value) {
    ElMessage.warning('请先选择操作')
    return
  }
  if (!props.connectorCode) {
    ElMessage.warning('请先保存连接器后再测试')
    return
  }

  // 解析 body
  let body: unknown = undefined
  if (showBody.value && bodyText.value.trim()) {
    try {
      body = JSON.parse(bodyText.value)
    } catch {
      ElMessage.error('Body 不是合法 JSON')
      return
    }
  }

  // 组装 params（过滤空值）
  const params: Record<string, unknown> = {}
  Object.entries(paramValues.value).forEach(([k, v]) => {
    if (v !== '' && v !== null && v !== undefined) {
      params[k] = v
    }
  })

  sending.value = true
  const timestamp = Date.now()
  const historyItem: HistoryItem = {
    id: ++historyIdSeq,
    operationName: selectedOpName.value,
    params: { ...params },
    body,
    result: null,
    timestamp
  }

  try {
    const result = await testOperation(props.connectorCode, {
      operationName: selectedOpName.value,
      params,
      body
    })
    currentResult.value = result
    historyItem.result = result
    if (result.error) {
      ElMessage.warning('测试返回错误：' + result.error)
    } else if (result.statusCode && result.statusCode >= 400) {
      ElMessage.warning(`响应状态码 ${result.statusCode}`)
    } else {
      ElMessage.success('测试完成')
    }
  } catch (e: any) {
    const errorResult: TestOperationResult = {
      error: e?.message || String(e)
    }
    currentResult.value = errorResult
    historyItem.result = errorResult
    ElMessage.error('测试请求失败：' + (e?.message || String(e)))
  } finally {
    sending.value = false
    // 入历史，最多保留 10 条
    history.value.unshift(historyItem)
    if (history.value.length > 10) {
      history.value = history.value.slice(0, 10)
    }
  }
}

function loadHistory(item: HistoryItem) {
  selectedOpName.value = item.operationName
  // 等待 currentOp 计算完成后再回填
  setTimeout(() => {
    paramValues.value = {}
    Object.entries(item.params).forEach(([k, v]) => {
      paramValues.value[k] = String(v ?? '')
    })
    // 补齐操作定义中存在但历史中缺失的参数
    if (currentOp.value) {
      currentOp.value.params.forEach((p) => {
        if (!(p.key in paramValues.value)) {
          paramValues.value[p.key] = ''
        }
      })
    }
    bodyText.value = item.body !== undefined ? JSON.stringify(item.body, null, 2) : ''
    currentResult.value = item.result
  }, 0)
}

function clearHistory() {
  history.value = []
  currentResult.value = null
}

function formatTime(ts: number): string {
  const d = new Date(ts)
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${hh}:${mm}:${ss}`
}
</script>

<template>
  <div class="test-console">
    <el-alert
      v-if="!props.connectorCode"
      title="请先保存连接器后再进行测试"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 12px"
    />

    <el-row :gutter="16">
      <!-- 左侧：请求构建 -->
      <el-col :span="12">
        <el-card shadow="never" class="panel">
          <template #header>
            <span class="panel-title">请求构建</span>
          </template>
          <el-form label-width="100px" size="small">
            <el-form-item label="选择操作">
              <el-select
                v-model="selectedOpName"
                placeholder="请选择操作"
                style="width: 100%"
                filterable
              >
                <el-option
                  v-for="op in availableOps"
                  :key="op.name"
                  :label="op.name"
                  :value="op.name"
                />
              </el-select>
            </el-form-item>

            <template v-if="currentOp">
              <el-form-item v-if="props.type === 'REST'" label="HTTP">
                <el-tag size="small">{{ currentOp.method }}</el-tag>
                <span class="op-path">{{ currentOp.path || '—' }}</span>
              </el-form-item>

              <el-divider content-position="left">参数</el-divider>
              <div v-if="currentOp.params.length === 0" class="muted-tip">该操作无参数</div>
              <el-form-item
                v-for="p in currentOp.params"
                :key="p.key"
                :label="p.key"
              >
                <el-input v-model="paramValues[p.key]" :placeholder="p.value || '请输入值'" />
              </el-form-item>

              <template v-if="showBody">
                <el-divider content-position="left">请求 Body</el-divider>
                <el-form-item label="Body">
                  <el-input
                    v-model="bodyText"
                    type="textarea"
                    :rows="6"
                    placeholder='{"key":"value"}'
                  />
                </el-form-item>
              </template>
            </template>

            <el-form-item>
              <el-button
                type="primary"
                :loading="sending"
                :disabled="!selectedOpName || !props.connectorCode"
                @click="send"
              >
                发送请求
              </el-button>
              <el-button :disabled="!selectedOpName" @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：响应展示 -->
      <el-col :span="12">
        <el-card shadow="never" class="panel">
          <template #header>
            <span class="panel-title">响应结果</span>
          </template>
          <div v-if="!hasResult" class="muted-tip">点击「发送请求」后在此查看响应</div>
          <div v-else class="response-area">
            <!-- 状态码 + 耗时 -->
            <div class="response-meta">
              <div class="meta-item">
                <span class="meta-label">状态码</span>
                <span class="status-badge" :class="statusClass(currentResult?.statusCode)">
                  {{ statusText(currentResult?.statusCode) }}
                </span>
              </div>
              <div class="meta-item">
                <span class="meta-label">耗时</span>
                <span class="meta-value">
                  {{ currentResult?.durationMillis != null ? currentResult.durationMillis + ' ms' : '—' }}
                </span>
              </div>
              <div v-if="currentResult?.error" class="meta-item">
                <span class="meta-label">错误</span>
                <span class="meta-value error-text">{{ currentResult.error }}</span>
              </div>
            </div>

            <!-- 响应 Headers -->
            <div v-if="headerRows.length > 0" class="response-section">
              <div class="section-title">响应 Headers</div>
              <el-table :data="headerRows" size="small" border max-height="160">
                <el-table-column label="名称" width="180" show-overflow-tooltip>
                  <template #default="{ row }">
                    <span class="header-name">{{ row[0] }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="值" show-overflow-tooltip>
                  <template #default="{ row }">{{ row[1] }}</template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 响应 Body -->
            <div class="response-section">
              <div class="section-title">响应 Body</div>
              <div v-if="currentResult?.body === undefined || currentResult?.body === null" class="muted-tip">
                无响应体
              </div>
              <div v-else class="body-tree">
                <JsonTree :data="currentResult.body" :default-expanded="true" />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 请求历史 -->
    <el-card v-if="hasHistory" shadow="never" class="history-panel">
      <template #header>
        <div class="history-header">
          <span class="panel-title">请求历史（最近 10 次）</span>
          <el-button size="small" link type="danger" @click="clearHistory">清空</el-button>
        </div>
      </template>
      <el-table :data="history" size="small" border>
        <el-table-column label="时间" width="100">
          <template #default="{ row }">{{ formatTime(row.timestamp) }}</template>
        </el-table-column>
        <el-table-column label="操作" prop="operationName" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态码" width="90" align="center">
          <template #default="{ row }">
            <span
              v-if="row.result?.statusCode"
              class="status-badge"
              :class="statusClass(row.result.statusCode)"
            >
              {{ row.result.statusCode }}
            </span>
            <span v-else-if="row.result?.error" class="status-badge status-5xx">ERR</span>
            <span v-else class="muted-tip">—</span>
          </template>
        </el-table-column>
        <el-table-column label="耗时" width="90" align="center">
          <template #default="{ row }">
            {{ row.result?.durationMillis != null ? row.result.durationMillis + ' ms' : '—' }}
          </template>
        </el-table-column>
        <el-table-column label="参数" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ JSON.stringify(row.params) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center">
          <template #default="{ row }">
            <el-button size="small" link @click="loadHistory(row)">重载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script lang="ts">
export default { name: 'TestConsole' }
</script>

<style scoped lang="scss">
.test-console {
  .panel {
    margin-bottom: 12px;
  }
  .panel-title {
    font-weight: 600;
  }
  .op-path {
    margin-left: 8px;
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
  .muted-tip {
    color: var(--el-text-color-placeholder);
    font-size: 13px;
    padding: 8px 0;
  }
  .response-meta {
    display: flex;
    gap: 24px;
    align-items: center;
    padding: 10px 12px;
    background: var(--el-fill-color-light);
    border-radius: 4px;
    margin-bottom: 12px;
    flex-wrap: wrap;
  }
  .meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  .meta-label {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
  .meta-value {
    font-size: 13px;
    font-weight: 600;
    &.error-text {
      color: var(--el-color-danger);
    }
  }
  .status-badge {
    display: inline-block;
    padding: 2px 10px;
    border-radius: 10px;
    font-size: 12px;
    font-weight: 600;
    color: #fff;
    &.status-2xx {
      background: #67c23a;
    }
    &.status-3xx {
      background: #909399;
    }
    &.status-4xx {
      background: #e6a23c;
    }
    &.status-5xx {
      background: #f56c6c;
    }
    &.status-unknown {
      background: #909399;
    }
  }
  .response-section {
    margin-top: 12px;
  }
  .section-title {
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 6px;
    color: var(--el-text-color-primary);
  }
  .body-tree {
    padding: 10px;
    background: var(--el-fill-color-light);
    border-radius: 4px;
    max-height: 360px;
    overflow: auto;
    border: 1px solid var(--el-border-color-lighter);
  }
  .header-name {
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 12px;
    color: #871094;
  }
  .history-panel {
    margin-top: 12px;
  }
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
