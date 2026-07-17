<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import DependencyGraph from '@/components/DependencyGraph.vue'
import {
  deleteDependency,
  listDependencies,
  saveDependency,
  type DependencyCycleResult,
  type DependencyType,
  type TaskDependency
} from '@/api/task-dependency'
import { getProject, type Project } from '@/api/project'
import { getTasksByProject, type ImplTask } from '@/api/implementation'

defineOptions({ name: 'TaskDependencyGraph' })

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.projectId))

const loading = ref(false)
const saving = ref(false)
const project = ref<Project | null>(null)
const tasks = ref<ImplTask[]>([])
const dependencies = ref<TaskDependency[]>([])
const cycleResult = ref<DependencyCycleResult | null>(null)

const graphRef = ref<InstanceType<typeof DependencyGraph> | null>(null)

/** 闭环路径任务ID列表（首尾相同），传给图组件高亮 */
const highlightCycle = computed<number[]>(() =>
  cycleResult.value ? cycleResult.value.cyclePath.map((n) => n.taskId) : []
)

/** 闭环路径文本：任务A → 任务B → ... → 任务A */
const cyclePathText = computed(() =>
  cycleResult.value
    ? cycleResult.value.cyclePath.map((n) => n.taskName ?? `#${n.taskId}`).join(' → ')
    : ''
)

const taskMap = computed(() => {
  const m = new Map<number, string>()
  tasks.value.forEach((t) => m.set(t.id!, t.taskName))
  return m
})

function taskName(id?: number): string {
  return id != null ? (taskMap.value.get(id) ?? `#${id}`) : '-'
}

const dependencyTypeOptions: { label: string; value: DependencyType }[] = [
  { label: 'FS 完成-开始', value: 'FS' },
  { label: 'FF 完成-完成', value: 'FF' },
  { label: 'SS 开始-开始', value: 'SS' },
  { label: 'SF 开始-完成', value: 'SF' }
]

function dependencyTypeLabel(t?: DependencyType): string {
  return dependencyTypeOptions.find((o) => o.value === t)?.label ?? t ?? '-'
}

// ============ 新增依赖弹窗 ============
const dialogVisible = ref(false)
const form = reactive({
  predecessorTaskId: undefined as number | undefined,
  successorTaskId: undefined as number | undefined,
  dependencyType: 'FS' as DependencyType,
  lagDays: 0
})

function openAddDialog() {
  form.predecessorTaskId = undefined
  form.successorTaskId = undefined
  form.dependencyType = 'FS'
  form.lagDays = 0
  cycleResult.value = null
  dialogVisible.value = true
}

async function handleSaveDependency() {
  if (!form.predecessorTaskId || !form.successorTaskId) {
    ElMessage.warning('请选择前置任务与后续任务')
    return
  }
  saving.value = true
  try {
    const result = await saveDependency({
      projectId: projectId.value,
      predecessorTaskId: form.predecessorTaskId,
      successorTaskId: form.successorTaskId,
      dependencyType: form.dependencyType,
      lagDays: form.lagDays
    })
    // 检测到循环依赖：后端返回 code=200 + data.success=false（Promise resolve）
    if ((result as DependencyCycleResult).success === false) {
      cycleResult.value = result as DependencyCycleResult
      ElMessage.warning('检测到循环依赖，已拦截保存')
      return
    }
    ElMessage.success('保存依赖成功')
    cycleResult.value = null
    dialogVisible.value = false
    await loadDependencies()
    graphRef.value?.refresh()
  } catch {
    /* handled by interceptor */
  } finally {
    saving.value = false
  }
}

async function handleDelete(dep: TaskDependency) {
  try {
    await ElMessageBox.confirm(
      `确认删除依赖：${taskName(dep.predecessorTaskId)} → ${taskName(dep.successorTaskId)}？`,
      '删除确认',
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await deleteDependency(dep.id!)
    ElMessage.success('删除成功')
    await loadDependencies()
    graphRef.value?.refresh()
  } catch {
    /* handled by interceptor */
  }
}

function clearCycle() {
  cycleResult.value = null
}

function onSelectTask(taskId: number) {
  ElMessage.info(`已选中任务：${taskName(taskId)}`)
}

// ============ 数据加载 ============
async function loadAll() {
  loading.value = true
  try {
    const [proj, t] = await Promise.all([getProject(projectId.value), getTasksByProject(projectId.value)])
    project.value = proj
    tasks.value = t
    await loadDependencies()
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadDependencies() {
  try {
    dependencies.value = await listDependencies(projectId.value)
  } catch {
    /* handled by interceptor */
  }
}

function goBack() {
  router.back()
}

onMounted(loadAll)
</script>

<template>
  <el-page-header :icon="null" @back="goBack">
    <template #content>
      <div class="header-content">
        <span class="header-title">{{ project?.name ?? '任务依赖关系图' }}</span>
        <span v-if="dependencies.length > 0" class="header-stat">
          共 {{ dependencies.length }} 条依赖
        </span>
      </div>
    </template>
    <template #extra>
      <el-button type="primary" @click="openAddDialog">新增依赖</el-button>
      <el-button :icon="'Refresh'" @click="loadAll">刷新</el-button>
    </template>
  </el-page-header>

  <el-alert
    v-if="cycleResult"
    type="error"
    :closable="true"
    show-icon
    :title="`检测到循环依赖：${cycleResult.errorMessage}`"
    @close="clearCycle"
  >
    <div class="cycle-path">闭环路径：{{ cyclePathText }}</div>
  </el-alert>

  <el-card v-loading="loading" shadow="never">
    <template #header>
      <div class="card-header">
        <span>依赖关系图（DAG）</span>
        <span class="card-tip">拖拽平移 · 滚轮缩放 · 拖拽节点调整位置</span>
      </div>
    </template>
    <DependencyGraph
      ref="graphRef"
      :project-id="projectId"
      :highlight-cycle="highlightCycle"
      @select-task="onSelectTask"
    />
  </el-card>

  <el-card shadow="never">
    <template #header>
      <span>依赖列表</span>
    </template>
    <el-table :data="dependencies" stripe>
      <el-table-column label="前置任务" min-width="180">
        <template #default="{ row }">{{ taskName(row.predecessorTaskId) }}</template>
      </el-table-column>
      <el-table-column label="后续任务" min-width="180">
        <template #default="{ row }">{{ taskName(row.successorTaskId) }}</template>
      </el-table-column>
      <el-table-column label="依赖类型" width="160">
        <template #default="{ row }">
          <el-tag size="small" effect="plain">{{ dependencyTypeLabel(row.dependencyType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="滞后天数" width="120" align="center">
        <template #default="{ row }">{{ row.lagDays ?? 0 }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="dependencies.length === 0" description="暂无依赖关系，点击「新增依赖」创建" />
  </el-card>

  <el-dialog v-model="dialogVisible" title="新增任务依赖" width="480px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="前置任务" required>
        <el-select v-model="form.predecessorTaskId" placeholder="选择前置任务" filterable style="width: 100%">
          <el-option
            v-for="t in tasks"
            :key="t.id"
            :label="t.taskName"
            :value="t.id!"
            :disabled="t.id === form.successorTaskId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="后续任务" required>
        <el-select v-model="form.successorTaskId" placeholder="选择后续任务" filterable style="width: 100%">
          <el-option
            v-for="t in tasks"
            :key="t.id"
            :label="t.taskName"
            :value="t.id!"
            :disabled="t.id === form.predecessorTaskId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="依赖类型">
        <el-select v-model="form.dependencyType" style="width: 100%">
          <el-option
            v-for="o in dependencyTypeOptions"
            :key="o.value"
            :label="o.label"
            :value="o.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="滞后天数">
        <el-input-number v-model="form.lagDays" :min="-365" :max="365" />
        <span class="form-tip">可负，表示提前</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSaveDependency">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
.header-stat {
  color: #909399;
  font-size: 13px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-tip {
  color: #909399;
  font-size: 12px;
}
.cycle-path {
  margin-top: 4px;
  font-weight: 600;
}
.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
