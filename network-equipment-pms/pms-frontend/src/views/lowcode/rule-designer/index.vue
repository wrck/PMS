<script setup lang="ts">
/**
 * 低代码规则设计器（可视化）。
 *
 * <p>列表页保留 el-table（code/name/type/status/操作）。新建/编辑打开可视化对话框：
 * 顶部规则元信息（type 选中后不可更改），中部按类型分发可视化编辑器——
 * 决策表编辑器 / 表达式编辑器 / LiteFlow 文本编辑，底部可折叠测试面板。</p>
 */
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteRule,
  getRuleList,
  saveRule,
  type LowCodeRule
} from '@/api/lowcode-rule'
import DecisionTableEditor from '@/components/RuleDesigner/DecisionTableEditor.vue'
import ExpressionRuleEditor from '@/components/RuleDesigner/ExpressionRuleEditor.vue'
import RuleTestPanel from '@/components/RuleDesigner/RuleTestPanel.vue'

defineOptions({ name: 'RuleDesignerView' })

const list = ref<LowCodeRule[]>([])
const current = ref<LowCodeRule | null>(null)
const dialogVisible = ref(false)
/** 列表「执行」入口打开时自动展开测试面板 */
const testExpanded = ref(false)

const typeOptions = [
  { label: '决策表', value: 'DECISION_TABLE' },
  { label: '表达式', value: 'EXPRESSION' },
  { label: 'LiteFlow', value: 'LITEFLOW' }
] as const

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' }
]

async function load() {
  list.value = await getRuleList()
}

function openNew() {
  current.value = {
    code: '',
    name: '',
    description: '',
    type: 'EXPRESSION',
    definition: '',
    ext: '',
    status: 'DRAFT'
  }
  testExpanded.value = false
  dialogVisible.value = true
}

function openEdit(row: LowCodeRule) {
  current.value = { ...row }
  testExpanded.value = false
  dialogVisible.value = true
}

/** 列表「执行」入口：打开编辑对话框并自动展开测试面板 */
function openExec(row: LowCodeRule) {
  current.value = { ...row }
  testExpanded.value = true
  dialogVisible.value = true
}

/**
 * 类型切换（仅新建时可切换）。切换时清空 definition 与 ext，避免不同类型格式串扰。
 * 已有 id 的规则（编辑态）类型锁定，不会触发本方法。
 */
function onTypeChange(t: LowCodeRule['type']) {
  if (!current.value) return
  current.value.type = t
  current.value.definition = ''
  if (t !== 'EXPRESSION') current.value.ext = ''
}

/** 当前规则是否处于编辑态（已有 id，类型锁定） */
const isEdit = computed(() => !!current.value?.id)

/** 表达式规则的 inputsSchema（从 ext 解析，传给测试面板生成模板） */
const testInputsSchema = computed(() => {
  if (!current.value || current.value.type !== 'EXPRESSION' || !current.value.ext) return undefined
  try {
    const obj = JSON.parse(current.value.ext) as { inputsSchema?: { name: string; type?: string }[] }
    return Array.isArray(obj.inputsSchema) ? obj.inputsSchema : undefined
  } catch {
    return undefined
  }
})

async function save() {
  if (!current.value) return
  if (!current.value.code.trim()) {
    ElMessage.warning('请填写规则编码')
    return
  }
  if (!current.value.name.trim()) {
    ElMessage.warning('请填写规则名称')
    return
  }
  await saveRule(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

async function remove(row: LowCodeRule) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认删除规则「${row.name}」？`, '确认', { type: 'warning' })
    await deleteRule(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch {
    /* cancelled or error */
  }
}

onMounted(load)
</script>

<template>
  <div style="padding: 16px">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>规则设计器</span>
          <el-button type="primary" @click="openNew">新建规则</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="编码" prop="code" min-width="140" show-overflow-tooltip />
        <el-table-column label="名称" prop="name" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="140">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="openExec(row)">执行</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="规则设计器"
      width="1100px"
      top="5vh"
      :close-on-click-modal="false"
    >
      <div v-if="current" class="rule-editor">
        <!-- 顶部：规则元信息 -->
        <el-form label-width="80px" class="rule-meta-form">
          <div class="rule-meta-row">
            <el-form-item label="编码" class="rule-meta-item">
              <el-input v-model="current.code" :disabled="isEdit" placeholder="唯一编码" />
            </el-form-item>
            <el-form-item label="名称" class="rule-meta-item">
              <el-input v-model="current.name" placeholder="规则名称" />
            </el-form-item>
            <el-form-item label="类型" class="rule-meta-item rule-meta-type">
              <el-select
                :model-value="current.type"
                :disabled="isEdit"
                placeholder="选择类型"
                @update:model-value="(v) => onTypeChange(v as LowCodeRule['type'])"
              >
                <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" class="rule-meta-item rule-meta-status">
              <el-select v-model="current.status">
                <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
          </div>
          <el-form-item label="描述">
            <el-input v-model="current.description" placeholder="规则描述（可选）" />
          </el-form-item>
        </el-form>

        <!-- 中部：按类型分发可视化编辑器 -->
        <div class="rule-editor-section">
          <!-- 决策表编辑器 -->
          <DecisionTableEditor
            v-if="current.type === 'DECISION_TABLE'"
            v-model="current.definition"
          />
          <!-- 表达式编辑器（包装 ExpressionEditor + inputsSchema 侧栏） -->
          <ExpressionRuleEditor
            v-else-if="current.type === 'EXPRESSION'"
            v-model="current.definition"
            v-model:ext="current.ext"
          />
          <!-- LiteFlow 占位文本编辑 -->
          <div v-else class="rule-liteflow-placeholder">
            <el-alert
              title="LiteFlow 集成暂未启用"
              type="warning"
              :closable="false"
              show-icon
              description="当前后端 LiteFlow 仍为占位实现，此处仅提供 EL 文本编辑。"
            />
            <el-input
              v-model="current.definition"
              type="textarea"
              :rows="10"
              placeholder="THEN(a, b, c)"
              class="rule-liteflow-text"
            />
          </div>
        </div>

        <!-- 底部：测试面板（可折叠） -->
        <RuleTestPanel
          :rule-code="current.code"
          :rule-type="current.type"
          :inputs-schema="testInputsSchema"
          :default-expanded="testExpanded"
        />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.rule-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 78vh;
  overflow-y: auto;
  padding-right: 4px;
}

.rule-meta-form {
  margin-bottom: 0;
}

.rule-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0 16px;
}

.rule-meta-item {
  flex: 1;
  min-width: 200px;
  margin-bottom: 12px;
}

.rule-meta-type {
  max-width: 200px;
}

.rule-meta-status {
  max-width: 160px;
}

.rule-editor-section {
  min-height: 200px;
}

.rule-liteflow-placeholder {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rule-liteflow-text :deep(textarea) {
  font-family: 'SF Mono', Menlo, Consolas, 'Courier New', monospace;
  font-size: 13px;
}
</style>
