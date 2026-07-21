<script setup lang="ts">
/**
 * 阶段退出条件编辑器（PhaseExitGate Editor）。
 *
 * 关联设计文档：§3.2 阶段状态机 + PhaseExitGate 4 类退出条件。
 *
 * 通过 v-model 双向绑定一个 PhaseExitGate 对象，支持编辑 4 类退出条件：
 *  - requiredDeliverables：必需交付件（deliverableId + 名称 + requiredStatus）
 *  - requiredTasks：必需任务（phaseId + allCompleted）
 *  - requiredMilestones：必需里程碑（milestoneId + mustReached）
 *  - requiredApprovals：必需审批（approvalType + mustApproved）
 *
 * 每类条件以折叠面板呈现，面板内为可编辑表格，支持添加/删除条目。
 * 字段类型对齐后端 {@code com.dp.plat.common.dto.PhaseExitGate}。
 */
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'

// ===================== 类型定义（对齐后端 PhaseExitGate DTO） =====================

export interface RequiredDeliverable {
  deliverableId?: number
  deliverableName?: string
  requiredStatus?: string
}

export interface RequiredTask {
  phaseId?: number
  allCompleted?: boolean
}

export interface RequiredMilestone {
  milestoneId?: number
  mustReached?: boolean
}

export interface RequiredApproval {
  approvalType?: string
  mustApproved?: boolean
}

export interface PhaseExitGate {
  requiredDeliverables?: RequiredDeliverable[]
  requiredTasks?: RequiredTask[]
  requiredMilestones?: RequiredMilestone[]
  requiredApprovals?: RequiredApproval[]
}

export interface PhaseResourceOption {
  id: number
  label: string
}

// ===================== Props / Emits =====================

defineOptions({ name: 'PhaseExitGateEditor' })

const props = defineProps<{
  /** v-model 绑定的退出条件对象 */
  modelValue?: PhaseExitGate | null
  /** 是否禁用（查看态） */
  disabled?: boolean
  /** 当前项目交付件选项 */
  deliverableOptions?: PhaseResourceOption[]
  /** 当前项目阶段选项（必需任务按阶段校验全部完成） */
  phaseOptions?: PhaseResourceOption[]
  /** 当前项目里程碑选项 */
  milestoneOptions?: PhaseResourceOption[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: PhaseExitGate): void
}>()

/** 折叠面板展开项（默认全部展开） */
const activeNames = ref<string[]>(['deliverable', 'task', 'milestone', 'approval'])

/** 当前 gate（保证非空，简化模板访问） */
const gate = computed<PhaseExitGate>(() => props.modelValue ?? {
  requiredDeliverables: [],
  requiredTasks: [],
  requiredMilestones: [],
  requiredApprovals: []
})

/** 通用：触发 update:modelValue，深拷贝避免父组件直接引用被就地修改 */
function emitChange(next: PhaseExitGate) {
  emit('update:modelValue', next)
}

// ===================== 选项常量 =====================

/** 交付件要求状态（关联 PhaseExitGate.RequiredDeliverable.requiredStatus 注释） */
const deliverableStatusOptions: { value: string; label: string }[] = [
  { value: 'PUBLISHED', label: '已发布（PUBLISHED）' },
  { value: 'REFERENCED', label: '已引用（REFERENCED）' },
  { value: 'ARCHIVED', label: '已归档（ARCHIVED）' }
]

/** 审批类型（关联 PhaseExitGate.RequiredApproval.approvalType 注释） */
const approvalTypeOptions: { value: string; label: string }[] = [
  { value: 'PHASE_EXIT', label: '阶段退出审批（PHASE_EXIT）' },
  { value: 'DELIVERABLE_APPROVAL', label: '交付件审批（DELIVERABLE_APPROVAL）' },
  { value: 'FINAL_ACCEPTANCE', label: '终验审批（FINAL_ACCEPTANCE）' },
  { value: 'CUSTOM', label: '自定义' }
]

// ===================== 必需交付件 =====================

const deliverables = computed(() => gate.value.requiredDeliverables ?? [])

function addDeliverable() {
  const list = [...deliverables.value, { deliverableId: undefined, deliverableName: '', requiredStatus: 'PUBLISHED' }]
  emitChange({ ...gate.value, requiredDeliverables: list })
}

function removeDeliverable(idx: number) {
  const list = deliverables.value.filter((_, i) => i !== idx)
  emitChange({ ...gate.value, requiredDeliverables: list })
}

function onDeliverableChange(idx: number, patch: Partial<RequiredDeliverable>) {
  const list = deliverables.value.map((d, i) => (i === idx ? { ...d, ...patch } : d))
  emitChange({ ...gate.value, requiredDeliverables: list })
}

function selectDeliverable(idx: number, deliverableId?: number) {
  const option = props.deliverableOptions?.find((item) => item.id === deliverableId)
  onDeliverableChange(idx, {
    deliverableId,
    deliverableName: option?.label ?? ''
  })
}

// ===================== 必需任务 =====================

const tasks = computed(() => gate.value.requiredTasks ?? [])

function addTask() {
  const list = [...tasks.value, { phaseId: undefined, allCompleted: true }]
  emitChange({ ...gate.value, requiredTasks: list })
}

function removeTask(idx: number) {
  const list = tasks.value.filter((_, i) => i !== idx)
  emitChange({ ...gate.value, requiredTasks: list })
}

function onTaskChange(idx: number, patch: Partial<RequiredTask>) {
  const list = tasks.value.map((t, i) => (i === idx ? { ...t, ...patch } : t))
  emitChange({ ...gate.value, requiredTasks: list })
}

// ===================== 必需里程碑 =====================

const milestones = computed(() => gate.value.requiredMilestones ?? [])

function addMilestone() {
  const list = [...milestones.value, { milestoneId: undefined, mustReached: true }]
  emitChange({ ...gate.value, requiredMilestones: list })
}

function removeMilestone(idx: number) {
  const list = milestones.value.filter((_, i) => i !== idx)
  emitChange({ ...gate.value, requiredMilestones: list })
}

function onMilestoneChange(idx: number, patch: Partial<RequiredMilestone>) {
  const list = milestones.value.map((m, i) => (i === idx ? { ...m, ...patch } : m))
  emitChange({ ...gate.value, requiredMilestones: list })
}

// ===================== 必需审批 =====================

const approvals = computed(() => gate.value.requiredApprovals ?? [])

function addApproval() {
  const list = [...approvals.value, { approvalType: 'PHASE_EXIT', mustApproved: true }]
  emitChange({ ...gate.value, requiredApprovals: list })
}

function removeApproval(idx: number) {
  const list = approvals.value.filter((_, i) => i !== idx)
  emitChange({ ...gate.value, requiredApprovals: list })
}

function onApprovalChange(idx: number, patch: Partial<RequiredApproval>) {
  const list = approvals.value.map((a, i) => (i === idx ? { ...a, ...patch } : a))
  emitChange({ ...gate.value, requiredApprovals: list })
}

// ===================== 计数与校验提示 =====================

const totalCount = computed(
  () => deliverables.value.length + tasks.value.length + milestones.value.length + approvals.value.length
)

/** 校验必填字段，返回是否合法；不合法时弹出提示 */
function validate(): boolean {
  for (const d of deliverables.value) {
    if (!d.deliverableId) {
      ElMessage.warning('存在未选择交付件的必需交付件条目')
      return false
    }
    if (!d.requiredStatus) {
      ElMessage.warning('必需交付件未选择要求状态')
      return false
    }
  }
  for (const t of tasks.value) {
    if (!t.phaseId) {
      ElMessage.warning('存在未选择任务所属阶段的必需任务条目')
      return false
    }
  }
  for (const m of milestones.value) {
    if (!m.milestoneId) {
      ElMessage.warning('存在未选择里程碑的必需里程碑条目')
      return false
    }
  }
  for (const a of approvals.value) {
    if (!a.approvalType) {
      ElMessage.warning('必需审批未选择审批类型')
      return false
    }
  }
  return true
}

defineExpose({ validate })
</script>

<template>
  <div class="phase-exit-gate-editor">
    <div class="editor-summary">
      <span class="summary-label">已配置 {{ totalCount }} 项退出条件</span>
      <span class="summary-breakdown">
        交付件 {{ deliverables.length }} · 任务 {{ tasks.length }} · 里程碑
        {{ milestones.length }} · 审批 {{ approvals.length }}
      </span>
    </div>

    <el-collapse v-model="activeNames" class="gate-collapse">
      <!-- 1. 必需交付件 -->
      <el-collapse-item name="deliverable">
        <template #title>
          <span class="panel-title">必需交付件</span>
          <el-tag size="small" type="warning" effect="plain" class="panel-count">
            {{ deliverables.length }}
          </el-tag>
        </template>
        <el-table :data="deliverables" border stripe size="small" empty-text="暂无必需交付件">
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column label="交付件" min-width="220">
            <template #default="{ row, $index }">
              <el-select
                :model-value="row.deliverableId"
                :disabled="disabled"
                filterable
                clearable
                style="width: 100%"
                placeholder="请选择当前项目交付件"
                @update:model-value="(v: number | undefined) => selectDeliverable($index, v ?? undefined)"
              >
                <el-option
                  v-for="option in deliverableOptions ?? []"
                  :key="option.id"
                  :label="option.label"
                  :value="option.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="要求状态" width="200">
            <template #default="{ row, $index }">
              <el-select
                :model-value="row.requiredStatus"
                :disabled="disabled"
                placeholder="选择要求状态"
                style="width: 100%"
                @update:model-value="(v: string) => onDeliverableChange($index, { requiredStatus: v })"
              >
                <el-option
                  v-for="opt in deliverableStatusOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="center" fixed="right">
            <template #default="{ $index }">
              <el-button
                link
                type="danger"
                :disabled="disabled"
                @click="removeDeliverable($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button
          type="primary"
          link
          :icon="'Plus'"
          :disabled="disabled"
          class="add-btn"
          @click="addDeliverable"
        >
          添加必需交付件
        </el-button>
      </el-collapse-item>

      <!-- 2. 必需任务 -->
      <el-collapse-item name="task">
        <template #title>
          <span class="panel-title">必需任务</span>
          <el-tag size="small" type="primary" effect="plain" class="panel-count">
            {{ tasks.length }}
          </el-tag>
        </template>
        <el-table :data="tasks" border stripe size="small" empty-text="暂无必需任务">
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column label="任务所属阶段" min-width="220">
            <template #default="{ row, $index }">
              <el-select
                :model-value="row.phaseId"
                :disabled="disabled"
                filterable
                clearable
                style="width: 100%"
                placeholder="请选择当前项目阶段"
                @update:model-value="(v: number | undefined) => onTaskChange($index, { phaseId: v ?? undefined })"
              >
                <el-option
                  v-for="option in phaseOptions ?? []"
                  :key="option.id"
                  :label="option.label"
                  :value="option.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="校验规则" min-width="200">
            <template #default="{ row, $index }">
              <span class="rule-text">该阶段所有任务须完成</span>
              <el-switch
                :model-value="row.allCompleted ?? false"
                :disabled="disabled"
                active-text="启用"
                inactive-text="关闭"
                inline-prompt
                @update:model-value="(v: boolean | string | number | undefined) => onTaskChange($index, { allCompleted: Boolean(v) })"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="center" fixed="right">
            <template #default="{ $index }">
              <el-button
                link
                type="danger"
                :disabled="disabled"
                @click="removeTask($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button
          type="primary"
          link
          :icon="'Plus'"
          :disabled="disabled"
          class="add-btn"
          @click="addTask"
        >
          添加必需任务
        </el-button>
      </el-collapse-item>

      <!-- 3. 必需里程碑 -->
      <el-collapse-item name="milestone">
        <template #title>
          <span class="panel-title">必需里程碑</span>
          <el-tag size="small" type="success" effect="plain" class="panel-count">
            {{ milestones.length }}
          </el-tag>
        </template>
        <el-table :data="milestones" border stripe size="small" empty-text="暂无必需里程碑">
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column label="里程碑" min-width="220">
            <template #default="{ row, $index }">
              <el-select
                :model-value="row.milestoneId"
                :disabled="disabled"
                filterable
                clearable
                style="width: 100%"
                placeholder="请选择当前项目里程碑"
                @update:model-value="(v: number | undefined) => onMilestoneChange($index, { milestoneId: v ?? undefined })"
              >
                <el-option
                  v-for="option in milestoneOptions ?? []"
                  :key="option.id"
                  :label="option.label"
                  :value="option.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="校验规则" min-width="200">
            <template #default="{ row, $index }">
              <span class="rule-text">里程碑须达成（COMPLETED）</span>
              <el-switch
                :model-value="row.mustReached ?? false"
                :disabled="disabled"
                active-text="启用"
                inactive-text="关闭"
                inline-prompt
                @update:model-value="(v: boolean | string | number | undefined) => onMilestoneChange($index, { mustReached: Boolean(v) })"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="center" fixed="right">
            <template #default="{ $index }">
              <el-button
                link
                type="danger"
                :disabled="disabled"
                @click="removeMilestone($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button
          type="primary"
          link
          :icon="'Plus'"
          :disabled="disabled"
          class="add-btn"
          @click="addMilestone"
        >
          添加必需里程碑
        </el-button>
      </el-collapse-item>

      <!-- 4. 必需审批 -->
      <el-collapse-item name="approval">
        <template #title>
          <span class="panel-title">必需审批</span>
          <el-tag size="small" type="danger" effect="plain" class="panel-count">
            {{ approvals.length }}
          </el-tag>
        </template>
        <el-table :data="approvals" border stripe size="small" empty-text="暂无必需审批">
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column label="审批类型" min-width="240">
            <template #default="{ row, $index }">
              <el-select
                :model-value="approvalTypeOptions.some((o) => o.value === row.approvalType) ? row.approvalType : 'CUSTOM'"
                :disabled="disabled"
                placeholder="选择审批类型"
                style="width: 100%"
                @update:model-value="(v: string) => onApprovalChange($index, { approvalType: v === 'CUSTOM' ? (row.approvalType && !approvalTypeOptions.some((o) => o.value === row.approvalType) ? row.approvalType : '') : v })"
              >
                <el-option
                  v-for="opt in approvalTypeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
              <el-input
                v-if="!approvalTypeOptions.some((o) => o.value === row.approvalType) || row.approvalType === 'CUSTOM'"
                :model-value="row.approvalType === 'CUSTOM' ? '' : row.approvalType"
                :disabled="disabled"
                placeholder="自定义审批类型标识"
                class="custom-approval-input"
                @update:model-value="(v: string) => onApprovalChange($index, { approvalType: v })"
              />
            </template>
          </el-table-column>
          <el-table-column label="校验规则" min-width="200">
            <template #default="{ row, $index }">
              <span class="rule-text">审批须通过（APPROVED）</span>
              <el-switch
                :model-value="row.mustApproved ?? false"
                :disabled="disabled"
                active-text="启用"
                inactive-text="关闭"
                inline-prompt
                @update:model-value="(v: boolean | string | number | undefined) => onApprovalChange($index, { mustApproved: Boolean(v) })"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="center" fixed="right">
            <template #default="{ $index }">
              <el-button
                link
                type="danger"
                :disabled="disabled"
                @click="removeApproval($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button
          type="primary"
          link
          :icon="'Plus'"
          :disabled="disabled"
          class="add-btn"
          @click="addApproval"
        >
          添加必需审批
        </el-button>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<style scoped>
.phase-exit-gate-editor {
  width: 100%;
}
.editor-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
}
.summary-label {
  font-weight: 600;
  color: #303133;
}
.summary-breakdown {
  color: #909399;
}
.gate-collapse {
  border-top: 1px solid #ebeef5;
}
.panel-title {
  font-weight: 600;
  color: #303133;
  margin-right: 8px;
}
.panel-count {
  margin-left: 4px;
}
.add-btn {
  margin-top: 8px;
}
.rule-text {
  margin-right: 12px;
  font-size: 13px;
  color: #606266;
}
.custom-approval-input {
  margin-top: 6px;
}
</style>
