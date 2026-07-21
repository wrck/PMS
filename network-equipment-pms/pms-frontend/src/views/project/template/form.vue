<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowDown,
  ArrowUp,
  Check,
  CirclePlus,
  Close,
  Delete,
  Plus,
  Promotion,
  RefreshLeft
} from '@element-plus/icons-vue'
import {
  getTemplate,
  getPublishedVersion,
  createTemplate,
  updateTemplate,
  publishVersion,
  type PhaseDef,
  type ProjectTemplate,
  type TemplateSnapshot
} from '@/api/project-template'
import { getAllRoles, type RoleOption } from '@/api/system'
import PageHeader from '@/components/common/PageHeader.vue'

defineOptions({ name: 'ProjectTemplateForm' })

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const publishing = ref(false)

const isEdit = computed(() => !!route.params.id)

// 7 个步骤
const steps = [
  { title: '基本信息', description: '模板名称与分类' },
  { title: '阶段配置', description: '阶段定义与退出条件' },
  { title: '任务配置', description: '任务树与属性' },
  { title: '交付件配置', description: '交付件类型与签核' },
  { title: '依赖配置', description: '任务依赖关系' },
  { title: '审批配置', description: '审批计划列表' },
  { title: '里程碑配置', description: '关键里程碑' }
]

const activeStep = ref(0)

// 模板基本信息
const form = reactive<ProjectTemplate>({
  templateCode: '',
  templateName: '',
  category: 'IMPLEMENT',
  description: '',
  status: 'DRAFT'
})

// 阶段列表
const phases = ref<PhaseDef[]>([])

// 任务树（简化结构：每个任务可有 children）
interface TaskNode {
  id: string
  taskName: string
  taskCode: string
  phaseCode: string
  assigneeRole?: string
  plannedHours?: number
  weight?: number
  description?: string
  children?: TaskNode[]
}
const tasks = ref<TaskNode[]>([])

// 交付件
interface DeliverableNode {
  id: string
  name: string
  type: string
  required: boolean
  signOffRole?: string
  phaseCode?: string
}
const deliverables = ref<DeliverableNode[]>([])

// 依赖关系
interface DependencyNode {
  id: string
  fromTaskCode: string
  toTaskCode: string
  type: string // FINISH_TO_START / START_TO_START / FINISH_TO_FINISH / START_TO_FINISH
}
const dependencies = ref<DependencyNode[]>([])

// 审批计划
interface ApprovalPlanNode {
  id: string
  name: string
  phaseCode: string
  approverRole: string
  trigger: string // PHASE_EXIT / DELIVERABLE_SUBMIT / MILESTONE
}
const approvalPlans = ref<ApprovalPlanNode[]>([])

// 里程碑
interface MilestoneNode {
  id: string
  name: string
  phaseCode: string
  plannedDate?: string
  description?: string
}
const milestones = ref<MilestoneNode[]>([])

// 角色下拉数据
const roles = ref<RoleOption[]>([])

// 发布对话框
const publishDialogVisible = ref(false)
const publishForm = reactive({ version: '', changeLog: '' })

// ============== 工具函数 ==============

function genId(prefix: string) {
  return `${prefix}_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`
}

// ============== 阶段操作 ==============

function addPhase() {
  phases.value.push({
    phaseCode: '',
    phaseName: '',
    sortOrder: phases.value.length + 1,
    entryCriteria: '',
    exitCriteria: ''
  } as PhaseDef)
}

function removePhase(idx: number) {
  phases.value.splice(idx, 1)
}

function movePhase(idx: number, direction: 'up' | 'down') {
  const target = direction === 'up' ? idx - 1 : idx + 1
  if (target < 0 || target >= phases.value.length) return
  const list = phases.value
  ;[list[idx], list[target]] = [list[target], list[idx]]
  // 重新计算 sortOrder
  list.forEach((p, i) => (p.sortOrder = i + 1))
}

// ============== 任务操作 ==============

const taskTreeProps = {
  label: 'taskName',
  children: 'children'
}

function allowDrop(_draggingNode: any, _dropNode: any, type: any) {
  return type !== 'inner' || true
}

function handleTaskDrop() {
  ElMessage.success('任务顺序已更新')
}

function addTask(parent?: TaskNode) {
  const newTask: TaskNode = {
    id: genId('task'),
    taskName: '新任务',
    taskCode: '',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    assigneeRole: '',
    plannedHours: 0,
    weight: 1,
    description: '',
    children: []
  }
  if (parent) {
    parent.children = parent.children ?? []
    parent.children.push(newTask)
  } else {
    tasks.value.push(newTask)
  }
}

function removeTask(node: TaskNode, list: TaskNode[]) {
  const idx = list.indexOf(node)
  if (idx >= 0) list.splice(idx, 1)
}

// ============== 交付件操作 ==============

function addDeliverable() {
  deliverables.value.push({
    id: genId('delv'),
    name: '新交付件',
    type: 'DOCUMENT',
    required: true,
    signOffRole: '',
    phaseCode: phases.value[0]?.phaseCode ?? ''
  })
}

function removeDeliverable(idx: number) {
  deliverables.value.splice(idx, 1)
}

// ============== 依赖操作 ==============

function addDependency() {
  dependencies.value.push({
    id: genId('dep'),
    fromTaskCode: '',
    toTaskCode: '',
    type: 'FINISH_TO_START'
  })
}

function removeDependency(idx: number) {
  dependencies.value.splice(idx, 1)
}

// ============== 审批计划操作 ==============

function addApprovalPlan() {
  approvalPlans.value.push({
    id: genId('appr'),
    name: '新审批计划',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    approverRole: '',
    trigger: 'PHASE_EXIT'
  })
}

function removeApprovalPlan(idx: number) {
  approvalPlans.value.splice(idx, 1)
}

// ============== 里程碑操作 ==============

function addMilestone() {
  milestones.value.push({
    id: genId('ms'),
    name: '新里程碑',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    plannedDate: '',
    description: ''
  })
}

function removeMilestone(idx: number) {
  milestones.value.splice(idx, 1)
}

// ============== 数据加载 ==============

/**
 * 将后端 TemplateSnapshot 中的 DTO 字段映射到前端各 ref 节点。
 * 字段差异参考 com.dp.plat.common.dto.TemplateSnapshot。
 */
function applySnapshot(snap?: TemplateSnapshot) {
  if (!snap) return
  // 阶段：字段名一致
  phases.value = (snap.phases ?? []) as PhaseDef[]

  // 任务：字段名一致（id/children 等前端字段在加载时缺失，由 addTask 等生成）
  tasks.value = ((snap.tasks ?? []) as any[]).map((t: any) => ({
    id: genId('task'),
    taskName: t.taskName ?? '',
    taskCode: t.taskCode ?? '',
    phaseCode: t.phaseCode ?? '',
    assigneeRole: t.assigneeRole ?? '',
    plannedHours: t.plannedHours ?? 0,
    weight: t.weight ?? 1,
    description: t.description ?? '',
    children: Array.isArray(t.children) ? t.children.map((c: any) => ({ ...c, id: genId('task') })) : []
  })) as TaskNode[]

  // 交付件：name↔deliverableName, type↔deliverableType, required↔mandatory, signOffRole↔approverRole
  deliverables.value = ((snap.deliverables ?? []) as any[]).map((d: any) => ({
    id: genId('delv'),
    name: d.name ?? d.deliverableName ?? '',
    type: d.type ?? d.deliverableType ?? 'DOCUMENT',
    required: d.required ?? d.mandatory ?? true,
    signOffRole: d.signOffRole ?? d.approverRole ?? '',
    phaseCode: d.phaseCode ?? ''
  })) as DeliverableNode[]

  // 依赖：fromTaskCode↔predecessorTaskName, toTaskCode↔successorTaskName, type↔dependencyType
  dependencies.value = ((snap.dependencies ?? []) as any[]).map((dep: any) => ({
    id: genId('dep'),
    fromTaskCode: dep.fromTaskCode ?? dep.predecessorTaskName ?? '',
    toTaskCode: dep.toTaskCode ?? dep.successorTaskName ?? '',
    type: dep.type ?? dep.dependencyType ?? 'FINISH_TO_START'
  })) as DependencyNode[]

  // 审批计划：name↔approvalType, approverRole↔approverRoles[0], trigger↔triggerPhaseCode/phaseCode
  approvalPlans.value = ((snap.approvalPlans ?? []) as any[]).map((a: any) => ({
    id: genId('appr'),
    name: a.name ?? a.approvalType ?? '',
    phaseCode: a.phaseCode ?? a.triggerPhaseCode ?? '',
    approverRole: a.approverRole ?? (Array.isArray(a.approverRoles) ? (a.approverRoles[0] ?? '') : ''),
    trigger: a.trigger ?? 'PHASE_EXIT'
  })) as ApprovalPlanNode[]

  // 里程碑：name↔milestoneName, plannedDate/description 后端无对应字段
  milestones.value = ((snap.milestones ?? []) as any[]).map((m: any) => ({
    id: genId('ms'),
    name: m.name ?? m.milestoneName ?? '',
    phaseCode: m.phaseCode ?? '',
    plannedDate: m.plannedDate ?? '',
    description: m.description ?? ''
  })) as MilestoneNode[]
}

async function loadTemplate(id: number) {
  loading.value = true
  try {
    const res = await getTemplate(id)
    Object.assign(form, res)
    // 加载最新已发布版本的快照数据，回显到 6 个 ref
    try {
      const published = await getPublishedVersion(id)
      applySnapshot(published?.snapshotJson)
    } catch {
      /* 已发布版本不存在（如纯草稿模板），保持 ref 为空即可 */
    }
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadRoles() {
  try {
    roles.value = await getAllRoles()
  } catch {
    roles.value = []
  }
}

// ============== 提交 ==============

function validateBasic(): string | null {
  if (!form.templateCode?.trim()) return '请填写模板编码'
  if (!form.templateName?.trim()) return '请填写模板名称'
  return null
}

async function handleSaveDraft() {
  const err = validateBasic()
  if (err) {
    ElMessage.warning(err)
    activeStep.value = 0
    return
  }
  submitting.value = true
  try {
    form.status = 'DRAFT'
    if (form.id) {
      await updateTemplate(form)
      ElMessage.success('草稿已保存')
    } else {
      const created = await createTemplate(form)
      form.id = created.id
      ElMessage.success('草稿已创建')
    }
  } finally {
    submitting.value = false
  }
}

function buildSnapshot(): TemplateSnapshot {
  return {
    phases: phases.value,
    tasks: tasks.value,
    deliverables: deliverables.value,
    dependencies: dependencies.value,
    approvalPlans: approvalPlans.value,
    milestones: milestones.value
  }
}

async function handleSave() {
  const err = validateBasic()
  if (err) {
    ElMessage.warning(err)
    activeStep.value = 0
    return
  }
  submitting.value = true
  try {
    form.status = 'DRAFT'
    if (form.id) {
      await updateTemplate(form)
      ElMessage.success('保存成功')
    } else {
      const created = await createTemplate(form)
      form.id = created.id
      ElMessage.success('创建成功')
    }
    router.back()
  } finally {
    submitting.value = false
  }
}

async function handlePublish() {
  if (!form.id) {
    ElMessage.warning('请先保存模板')
    return
  }
  if (phases.value.length === 0) {
    ElMessage.warning('请至少配置一个阶段')
    activeStep.value = 1
    return
  }
  if (!publishForm.version.trim()) {
    ElMessage.warning('请填写版本号')
    return
  }
  publishing.value = true
  try {
    const snapshot = buildSnapshot()
    await publishVersion(form.id, {
      version: publishForm.version,
      snapshot,
      changeLog: publishForm.changeLog
    })
    ElMessage.success('版本已发布')
    publishDialogVisible.value = false
    publishForm.version = ''
    publishForm.changeLog = ''
    // 重新加载模板，让 form.status 同步为 'PUBLISHED'
    await loadTemplate(form.id)
  } finally {
    publishing.value = false
  }
}

function handleNext() {
  if (activeStep.value === 0) {
    const err = validateBasic()
    if (err) {
      ElMessage.warning(err)
      return
    }
  }
  if (activeStep.value < steps.length - 1) activeStep.value++
}

function handlePrev() {
  if (activeStep.value > 0) activeStep.value--
}

onMounted(() => {
  loadRoles()
  const id = route.params.id as string | undefined
  if (id) loadTemplate(Number(id))
})
</script>

<template>
  <div class="template-form-page" v-loading="loading">
    <PageHeader :title="isEdit ? '编辑模板' : '新建模板'" description="按 7 步分步配置模板内容">
      <template #actions>
        <el-button :icon="RefreshLeft" @click="router.back()">取消</el-button>
        <el-button v-permission="'project:template:add'" :icon="Check" :loading="submitting" @click="handleSaveDraft">保存草稿</el-button>
        <el-button v-if="form.id" v-permission="'project:template:publish'" type="success" :icon="Promotion" @click="publishDialogVisible = true">
          发布版本
        </el-button>
        <el-button v-permission="'project:template:add'" type="primary" :icon="Check" :loading="submitting" @click="handleSave">保存</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never" class="steps-card">
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step v-for="(s, idx) in steps" :key="idx" :title="s.title" :description="s.description" />
      </el-steps>
    </el-card>

    <el-card shadow="never" class="step-content-card">
      <!-- Step 1: 基本信息 -->
      <div v-if="activeStep === 0" class="step-panel">
        <el-form :model="form" label-width="100px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="模板编码" required>
                <el-input
                  v-model="form.templateCode"
                  :disabled="!!form.id"
                  placeholder="如 TPL-IMPL-STD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="模板名称" required>
                <el-input v-model="form.templateName" placeholder="请输入模板名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目类型">
                <el-select v-model="form.category" style="width: 100%">
                  <el-option label="实施" value="IMPLEMENT" />
                  <el-option label="维护" value="MAINTENANCE" />
                  <el-option label="咨询" value="CONSULTING" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-tag :type="form.status === 'PUBLISHED' ? 'success' : 'info'" effect="light">
                  {{ form.status }}
                </el-tag>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="描述">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="3"
                  placeholder="说明模板用途、适用项目类型等"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- Step 2: 阶段配置 -->
      <div v-else-if="activeStep === 1" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ phases.length }} 个阶段 · 使用上下箭头调整顺序</span>
          <el-button type="primary" :icon="Plus" @click="addPhase">添加阶段</el-button>
        </div>
        <el-empty v-if="phases.length === 0" description="暂无阶段，点击「添加阶段」开始配置" />
        <div v-for="(phase, idx) in phases" :key="idx" class="phase-row">
          <div class="phase-order">{{ idx + 1 }}</div>
          <div class="phase-fields">
            <el-input v-model="phase.phaseCode" placeholder="阶段编码 PREPARE" style="width: 180px" />
            <el-input v-model="phase.phaseName" placeholder="阶段名称" style="width: 200px" />
            <el-input v-model="phase.entryCriteria" placeholder="进入条件" style="width: 240px" />
            <el-input v-model="phase.exitCriteria" placeholder="退出条件" style="width: 240px" />
          </div>
          <div class="phase-actions">
            <el-button
              link
              :icon="ArrowUp"
              :disabled="idx === 0"
              @click="movePhase(idx, 'up')"
            />
            <el-button
              link
              :icon="ArrowDown"
              :disabled="idx === phases.length - 1"
              @click="movePhase(idx, 'down')"
            />
            <el-button link type="danger" :icon="Delete" @click="removePhase(idx)" />
          </div>
        </div>
      </div>

      <!-- Step 3: 任务配置 -->
      <div v-else-if="activeStep === 2" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">支持拖拽调整层级与顺序</span>
          <el-button type="primary" :icon="Plus" @click="addTask()">添加任务</el-button>
        </div>
        <el-empty v-if="tasks.length === 0" description="暂无任务，点击「添加任务」开始配置" />
        <el-tree
          v-else
          :data="tasks"
          :props="taskTreeProps"
          node-key="id"
          draggable
          default-expand-all
          :expand-on-click-node="false"
          :allow-drop="allowDrop"
          @node-drop="handleTaskDrop"
        >
          <template #default="{ data }">
            <div class="task-node">
              <el-input v-model="data.taskName" size="small" style="width: 200px" />
              <el-input v-model="data.taskCode" size="small" placeholder="编码" style="width: 140px" />
              <el-select
                v-model="data.phaseCode"
                size="small"
                placeholder="所属阶段"
                style="width: 160px"
              >
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
              <el-select
                v-model="data.assigneeRole"
                size="small"
                filterable
                clearable
                placeholder="请选择负责角色"
                style="width: 160px"
              >
                <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
              </el-select>
              <el-input-number
                v-model="data.plannedHours"
                :min="0"
                size="small"
                placeholder="计划工时"
                style="width: 120px"
              />
              <el-button
                link
                type="primary"
                size="small"
                :icon="CirclePlus"
                @click.stop="addTask(data)"
              >子任务</el-button>
              <el-button link type="danger" size="small" :icon="Close" @click.stop="removeTask(data, tasks)" />
            </div>
          </template>
        </el-tree>
      </div>

      <!-- Step 4: 交付件配置 -->
      <div v-else-if="activeStep === 3" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ deliverables.length }} 个交付件</span>
          <el-button type="primary" :icon="Plus" @click="addDeliverable">添加交付件</el-button>
        </div>
        <el-empty v-if="deliverables.length === 0" description="暂无交付件" />
        <el-table v-else :data="deliverables" border stripe>
          <el-table-column label="名称" min-width="160">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="类型" width="140">
            <template #default="{ row }">
              <el-select v-model="row.type" size="small">
                <el-option label="文档" value="DOCUMENT" />
                <el-option label="代码" value="CODE" />
                <el-option label="模型" value="MODEL" />
                <el-option label="测试报告" value="TEST_REPORT" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="所属阶段" width="160">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" clearable>
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="必需" width="80" align="center">
            <template #default="{ row }">
              <el-switch v-model="row.required" />
            </template>
          </el-table-column>
          <el-table-column label="签核角色" width="160">
            <template #default="{ row }">
              <el-select v-model="row.signOffRole" size="small" filterable clearable placeholder="请选择签核角色">
                <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeDeliverable($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 5: 依赖配置 -->
      <div v-else-if="activeStep === 4" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ dependencies.length }} 条依赖关系</span>
          <el-button type="primary" :icon="Plus" @click="addDependency">添加依赖</el-button>
        </div>
        <el-empty v-if="dependencies.length === 0" description="暂无依赖关系" />
        <el-table v-else :data="dependencies" border stripe>
          <el-table-column label="前置任务" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.fromTaskCode" size="small" placeholder="任务编码" />
            </template>
          </el-table-column>
          <el-table-column label="后置任务" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.toTaskCode" size="small" placeholder="任务编码" />
            </template>
          </el-table-column>
          <el-table-column label="依赖类型" width="200">
            <template #default="{ row }">
              <el-select v-model="row.type" size="small">
                <el-option label="完成-开始 (FS)" value="FINISH_TO_START" />
                <el-option label="开始-开始 (SS)" value="START_TO_START" />
                <el-option label="完成-完成 (FF)" value="FINISH_TO_FINISH" />
                <el-option label="开始-完成 (SF)" value="START_TO_FINISH" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeDependency($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 6: 审批配置 -->
      <div v-else-if="activeStep === 5" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ approvalPlans.length }} 个审批计划</span>
          <el-button type="primary" :icon="Plus" @click="addApprovalPlan">添加审批计划</el-button>
        </div>
        <el-empty v-if="approvalPlans.length === 0" description="暂无审批计划" />
        <el-table v-else :data="approvalPlans" border stripe>
          <el-table-column label="名称" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="触发阶段" width="180">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" clearable>
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="触发类型" width="200">
            <template #default="{ row }">
              <el-select v-model="row.trigger" size="small">
                <el-option label="阶段退出" value="PHASE_EXIT" />
                <el-option label="交付件提交" value="DELIVERABLE_SUBMIT" />
                <el-option label="里程碑达成" value="MILESTONE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="审批角色" width="180">
            <template #default="{ row }">
              <el-select v-model="row.approverRole" size="small" filterable clearable placeholder="请选择审批角色">
                <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeApprovalPlan($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 7: 里程碑配置 -->
      <div v-else-if="activeStep === 6" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ milestones.length }} 个里程碑</span>
          <el-button type="primary" :icon="Plus" @click="addMilestone">添加里程碑</el-button>
        </div>
        <el-empty v-if="milestones.length === 0" description="暂无里程碑" />
        <el-table v-else :data="milestones" border stripe>
          <el-table-column label="名称" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="关联阶段" width="180">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" clearable>
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="计划日期" width="180">
            <template #default="{ row }">
              <el-date-picker
                v-model="row.plannedDate"
                type="date"
                size="small"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="描述" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.description" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeMilestone($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 步骤导航 -->
      <div class="step-actions">
        <el-button v-if="activeStep > 0" @click="handlePrev">上一步</el-button>
        <el-button v-if="activeStep < steps.length - 1" type="primary" @click="handleNext">
          下一步
        </el-button>
        <el-button v-if="activeStep === steps.length - 1" type="primary" @click="handleSave">
          完成保存
        </el-button>
      </div>
    </el-card>

    <!-- 发布版本对话框 -->
    <el-dialog v-model="publishDialogVisible" title="发布新版本" width="500px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="版本号" required>
          <el-input v-model="publishForm.version" placeholder="如 v1.0.0" />
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input v-model="publishForm.changeLog" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.template-form-page {
  padding: 16px 24px;
}

.steps-card {
  margin-bottom: 16px;
  border-radius: var(--pms-radius-lg);
}

.step-content-card {
  border-radius: var(--pms-radius-lg);
}

.step-panel {
  min-height: 360px;
  padding: 8px 4px;
}

.panel-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.panel-tip {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}

.phase-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-md);
  margin-bottom: 8px;
}
.phase-order {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--pms-color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}
.phase-fields {
  display: flex;
  gap: 8px;
  flex: 1;
  flex-wrap: wrap;
}
.phase-actions {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-shrink: 0;
}

.task-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding-right: 8px;
  flex-wrap: wrap;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 24px 0 8px;
  border-top: 1px solid var(--pms-color-border-light);
  margin-top: 16px;
}
</style>
