<script setup lang="ts">
/**
 * 低代码触发器列表 + 可视化触发器构建器。
 *
 * <p>将原先的「表格 + JSON 文本框」升级为分步表单构建器（借鉴 ServiceNow Flow
 * Designer / Budibase Automation）：</p>
 * <ol>
 *   <li>Step 1 触发器元信息：code / name / type（编辑时不可更改）/ targetType /
 *       targetCode（下拉从微流/流程列表加载）/ status</li>
 *   <li>Step 2 触发配置：按 type 分发 — CRUD / QUARTZ（Cron 可视化编辑器）/ EVENT</li>
 *   <li>Step 3 手动测试：输入数据 JSON → 调用 executeTrigger → 展示结果</li>
 * </ol>
 *
 * <p>config 在编辑器内部以结构化对象操作，保存时序列化为 JSON 字符串存入
 * LowCodeTrigger.config；加载时通过 parseTriggerConfig 反向解析并兼容历史写法。</p>
 */
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createDefaultTriggerConfig,
  deleteTrigger,
  executeTrigger,
  getTriggerList,
  parseTriggerConfig,
  saveTrigger,
  serializeTriggerConfig,
  type CrudTriggerConfig,
  type EventTriggerConfig,
  type LowCodeTrigger,
  type LowCodeTriggerConfig,
  type QuartzTriggerConfig,
  type TriggerTargetType,
  type TriggerType
} from '@/api/lowcode-trigger'
import { getMicroflowList, type LowCodeMicroflow } from '@/api/lowcode-microflow'
import { getProcessBindings, type LowCodeProcessBinding } from '@/api/lowcode-process'
import CrudTriggerConfigView from '@/components/TriggerDesigner/CrudTriggerConfig.vue'
import QuartzTriggerConfigView from '@/components/TriggerDesigner/QuartzTriggerConfig.vue'
import EventTriggerConfigView from '@/components/TriggerDesigner/EventTriggerConfig.vue'

defineOptions({ name: 'TriggerListView' })

const list = ref<LowCodeTrigger[]>([])
const dialogVisible = ref(false)
const activeStep = ref(0)
const saving = ref(false)

const typeOptions: Array<{ label: string; value: TriggerType }> = [
  { label: 'CRUD（数据增删改）', value: 'CRUD' },
  { label: 'Quartz（定时）', value: 'QUARTZ' },
  { label: 'Event（事件）', value: 'EVENT' }
]
const targetTypeOptions: Array<{ label: string; value: TriggerTargetType }> = [
  { label: '微流', value: 'MICROFLOW' },
  { label: '流程', value: 'PROCESS' }
]
const statusOptions = [
  { label: '启用', value: 'ACTIVE' },
  { label: '停用', value: 'INACTIVE' }
]

const steps = [
  { title: '元信息', description: '编码 / 名称 / 类型 / 目标' },
  { title: '触发配置', description: '按类型分发的可视化配置' },
  { title: '手动测试', description: '输入数据 → 执行 → 查看结果' }
]
const isLastStep = computed(() => activeStep.value === steps.length - 1)

// ---------------- 编辑状态 ----------------
/** 当前编辑的触发器顶层字段 */
const current = ref<LowCodeTrigger | null>(null)
/** 结构化 config（按 current.type 解析） */
const configObj = ref<LowCodeTriggerConfig>(createDefaultTriggerConfig('CRUD'))

/** 微流 / 流程列表，用于目标编码下拉 */
const microflows = ref<LowCodeMicroflow[]>([])
const processes = ref<LowCodeProcessBinding[]>([])

/** 是否为编辑已有触发器（决定 type 是否可更改） */
const isEdit = computed(() => !!current.value?.id)

/** 目标编码下拉选项（按 targetType 切换数据源） */
const targetOptions = computed(() => {
  if (current.value?.targetType === 'PROCESS') {
    return processes.value.map((p) => ({
      label: `${p.processDefinitionName || p.processDefinitionKey} (${p.processDefinitionKey})`,
      value: p.processDefinitionKey
    }))
  }
  return microflows.value.map((m) => ({ label: `${m.name} (${m.code})`, value: m.code }))
})

// ---------------- 各类型 config 的 v-model 代理 ----------------
const crudConfig = computed<CrudTriggerConfig>({
  get: () => configObj.value as CrudTriggerConfig,
  set: (v) => {
    configObj.value = v
  }
})
const quartzConfig = computed<QuartzTriggerConfig>({
  get: () => configObj.value as QuartzTriggerConfig,
  set: (v) => {
    configObj.value = v
  }
})
const eventConfig = computed<EventTriggerConfig>({
  get: () => configObj.value as EventTriggerConfig,
  set: (v) => {
    configObj.value = v
  }
})

// ---------------- 列表加载 ----------------
async function load() {
  list.value = await getTriggerList()
}

async function loadTargets() {
  const [mfs, procs] = await Promise.all([
    getMicroflowList().catch(() => [] as LowCodeMicroflow[]),
    getProcessBindings().catch(() => [] as LowCodeProcessBinding[])
  ])
  microflows.value = mfs
  processes.value = procs
}

// ---------------- 新建 / 编辑 ----------------
function openNew() {
  current.value = {
    code: '',
    name: '',
    type: 'CRUD',
    config: '{}',
    targetType: 'MICROFLOW',
    targetCode: '',
    status: 'ACTIVE'
  }
  configObj.value = createDefaultTriggerConfig('CRUD')
  activeStep.value = 0
  testData.value = '{\n  \n}'
  testResult.value = ''
  dialogVisible.value = true
  loadTargets()
}

function openEdit(row: LowCodeTrigger, startStep = 0) {
  current.value = { ...row }
  configObj.value = parseTriggerConfig(row.config, row.type)
  activeStep.value = startStep
  testData.value = '{\n  \n}'
  testResult.value = ''
  dialogVisible.value = true
  loadTargets()
}

/** 新建模式下切换类型：重置触发配置为该类型默认值 */
function onTypeChange(val: TriggerType) {
  if (!current.value) return
  if (isEdit.value) return // 编辑模式 type 不可更改
  current.value.type = val
  configObj.value = createDefaultTriggerConfig(val)
  activeStep.value = 0
}

// ---------------- 步骤导航 ----------------
function next() {
  if (activeStep.value === 0 && !validateBasic()) return
  if (activeStep.value < steps.length - 1) activeStep.value++
}

function prev() {
  if (activeStep.value > 0) activeStep.value--
}

// ---------------- 校验 ----------------
function validateBasic(): boolean {
  const c = current.value
  if (!c) return false
  if (!c.code.trim() || !c.name.trim()) {
    ElMessage.warning('请填写编码和名称')
    return false
  }
  if (!c.targetCode.trim()) {
    ElMessage.warning('请选择目标编码')
    return false
  }
  return true
}

function validateConfig(): boolean {
  const c = current.value
  if (!c) return false
  if (c.type === 'CRUD') {
    const cfg = configObj.value as CrudTriggerConfig
    if (!cfg.entityCode) {
      ElMessage.warning('请选择触发实体')
      return false
    }
    if (!cfg.operations.length) {
      ElMessage.warning('请至少选择一个触发操作')
      return false
    }
    if (!cfg.timing.length) {
      ElMessage.warning('请至少选择一个触发时机')
      return false
    }
  } else if (c.type === 'EVENT') {
    const cfg = configObj.value as EventTriggerConfig
    if (!cfg.eventName.trim()) {
      ElMessage.warning('请填写事件名')
      return false
    }
  }
  return true
}

// ---------------- 保存 ----------------
function buildTrigger(): LowCodeTrigger | null {
  const c = current.value
  if (!c) return null
  return {
    id: c.id,
    code: c.code,
    name: c.name,
    type: c.type,
    config: serializeTriggerConfig(configObj.value),
    targetType: c.targetType,
    targetCode: c.targetCode,
    status: c.status
  }
}

async function save(closeAfter = false) {
  if (!validateBasic()) {
    activeStep.value = 0
    return
  }
  if (!validateConfig()) {
    activeStep.value = 1
    return
  }
  const payload = buildTrigger()
  if (!payload) return
  saving.value = true
  try {
    const saved = await saveTrigger(payload)
    if (current.value) current.value.id = saved.id
    ElMessage.success('保存成功')
    await load()
    if (closeAfter) dialogVisible.value = false
  } catch (e: unknown) {
    ElMessage.error('保存失败：' + (e instanceof Error ? e.message : String(e)))
  } finally {
    saving.value = false
  }
}

// ---------------- 删除 ----------------
async function remove(row: LowCodeTrigger) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认删除触发器「${row.name}」？`, '确认', { type: 'warning' })
    await deleteTrigger(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch {
    /* cancelled or error */
  }
}

// ---------------- 手动测试 ----------------
const testData = ref('{\n  \n}')
const testResult = ref('')
const testing = ref(false)

async function runTest() {
  const c = current.value
  if (!c) return
  if (!c.id) {
    ElMessage.warning('请先保存触发器再测试')
    return
  }
  let data: Record<string, unknown> = {}
  try {
    data = testData.value.trim() ? JSON.parse(testData.value) : {}
  } catch {
    ElMessage.error('输入数据 JSON 解析失败')
    return
  }
  testing.value = true
  testResult.value = ''
  try {
    const result = await executeTrigger(c.code, data)
    testResult.value = JSON.stringify(result, null, 2)
    ElMessage.success('执行完成')
  } catch (e: unknown) {
    testResult.value = '执行失败：' + (e instanceof Error ? e.message : String(e))
  } finally {
    testing.value = false
  }
}

// ---------------- 配置预览 JSON（步骤 2 底部展示序列化结果） ----------------
const configPreview = computed(() => serializeTriggerConfig(configObj.value))

onMounted(load)
</script>

<template>
  <div style="padding: 16px">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>触发器</span>
          <el-button type="primary" @click="openNew">新建触发器</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="编码" prop="code" min-width="140" show-overflow-tooltip />
        <el-table-column label="名称" prop="name" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="100" />
        <el-table-column label="目标" min-width="160">
          <template #default="{ row }">
            <el-tag size="small">{{ row.targetType }}</el-tag>
            <span style="margin-left: 6px">{{ row.targetCode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="openEdit(row, 2)">执行</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分步表单构建器对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑触发器' : '新建触发器'"
      width="920px"
      :close-on-click-modal="false"
      destroy-on-close
      class="trigger-designer-dialog"
    >
      <el-steps :active="activeStep" finish-status="success" align-center class="step-header">
        <el-step
          v-for="(s, idx) in steps"
          :key="s.title"
          :title="s.title"
          :description="s.description"
          @click="activeStep = idx"
        />
      </el-steps>

      <div class="step-body" v-if="current">
        <!-- Step 1 元信息 -->
        <div v-show="activeStep === 0">
          <el-form :model="current" label-width="100px" class="meta-form">
            <el-form-item label="编码" required>
              <el-input v-model="current.code" placeholder="如 orderCreateTrigger" :disabled="isEdit" />
            </el-form-item>
            <el-form-item label="名称" required>
              <el-input v-model="current.name" placeholder="如 订单创建触发器" />
            </el-form-item>
            <el-form-item label="类型" required>
              <el-select
                :model-value="current.type"
                :disabled="isEdit"
                style="width: 100%"
                @update:model-value="onTypeChange($event as TriggerType)"
              >
                <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
              <div class="field-hint">
                {{ isEdit ? '编辑模式下类型不可更改' : '选择后将进入对应类型的可视化配置；切换类型会重置触发配置' }}
              </div>
            </el-form-item>
            <el-form-item label="目标类型" required>
              <el-radio-group v-model="current.targetType">
                <el-radio-button v-for="o in targetTypeOptions" :key="o.value" :value="o.value">
                  {{ o.label }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="目标编码" required>
              <el-select
                v-model="current.targetCode"
                filterable
                allow-create
                default-first-option
                placeholder="选择目标微流/流程，或手动输入编码"
                style="width: 100%"
              >
                <el-option v-for="o in targetOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="current.status" style="width: 200px">
                <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>

        <!-- Step 2 触发配置（按类型分发） -->
        <div v-show="activeStep === 1">
          <CrudTriggerConfigView
            v-if="current.type === 'CRUD'"
            v-model="crudConfig"
          />
          <QuartzTriggerConfigView
            v-else-if="current.type === 'QUARTZ'"
            v-model="quartzConfig"
          />
          <EventTriggerConfigView
            v-else-if="current.type === 'EVENT'"
            v-model="eventConfig"
          />

          <el-divider content-position="left">配置预览（序列化 JSON）</el-divider>
          <pre class="config-preview">{{ configPreview }}</pre>
        </div>

        <!-- Step 3 手动测试 -->
        <div v-show="activeStep === 2">
          <el-form label-width="100px">
            <el-form-item label="输入数据">
              <el-input
                v-model="testData"
                type="textarea"
                :rows="8"
                placeholder='{"key":"value"}'
                style="font-family: monospace"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="testing" @click="runTest">执行</el-button>
              <span v-if="!current.id" class="field-hint" style="margin-left: 12px">
                触发器尚未保存，请先保存再测试
              </span>
            </el-form-item>
            <el-form-item v-if="testResult" label="执行结果">
              <pre class="config-preview">{{ testResult }}</pre>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button :disabled="activeStep === 0" @click="prev">上一步</el-button>
          <el-button v-if="!isLastStep" type="primary" @click="next">下一步</el-button>
          <el-button type="success" :loading="saving" @click="save(false)">保存</el-button>
          <el-button type="primary" :loading="saving" @click="save(true)">保存并关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.step-header {
  margin-bottom: 20px;
  cursor: pointer;
}

.step-body {
  min-height: 360px;
  padding: 8px 4px;
}

.meta-form {
  max-width: 640px;
}

.field-hint {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}

.config-preview {
  margin: 0;
  padding: 10px 12px;
  max-height: 220px;
  overflow: auto;
  font-family: 'SF Mono', Menlo, Consolas, 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
  color: var(--el-text-color-primary);
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

:deep(.trigger-designer-dialog) {
  .el-dialog__body {
    max-height: 72vh;
    overflow-y: auto;
  }
}
</style>
