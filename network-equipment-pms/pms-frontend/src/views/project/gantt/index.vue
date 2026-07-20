<script setup lang="ts">
// =============================================================================
// ProjectGanttPage - 项目甘特图页面
// -----------------------------------------------------------------------------
// 顶部 PageHeader（项目名 + 返回工作区）；主体 ProjectGantt；右侧侧栏（图例 +
// 基线对比选择 + 关键路径开关）。通过 route.params.id 获取 projectId。
// =============================================================================
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProject, type Project } from '@/api/project'
import { listBaselines, type BaselineSnapshot } from '@/api/baseline'
import PageHeader from '@/components/common/PageHeader.vue'
import ProjectGantt from '@/components/project/ProjectGantt.vue'
import EmptyState from '@/components/common/EmptyState.vue'

defineOptions({ name: 'ProjectGanttPage' })

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.id))

const loading = ref(false)
const project = ref<Project | null>(null)
const baselines = ref<BaselineSnapshot[]>([])

/** 选中的基线ID（null 表示不对比） */
const selectedBaselineId = ref<number | null>(null)
/** 关键路径开关（与 ProjectGantt 双向绑定） */
const showCriticalPath = ref(true)

const ganttRef = ref<InstanceType<typeof ProjectGantt> | null>(null)

const statusLegend = [
  { color: '#10b981', label: '已完成' },
  { color: '#3b82f6', label: '进行中' },
  { color: '#f59e0b', label: '评审中' },
  { color: '#9ca3af', label: '未开始' },
  { color: '#ef4444', label: '已延期/阻塞' }
]

const edgeLegend = [
  { dash: 'solid', label: 'FS 完成-开始' },
  { dash: '5,5', label: 'SS 开始-开始' },
  { dash: '2,4', label: 'FF 完成-完成' },
  { dash: '10,3,2,3', label: 'SF 开始-完成' }
]

async function loadAll() {
  if (!projectId.value || Number.isNaN(projectId.value)) return
  loading.value = true
  try {
    const [proj, baseList] = await Promise.all([
      getProject(projectId.value),
      listBaselines(projectId.value)
    ])
    project.value = proj
    baselines.value = baseList ?? []
  } catch {
    project.value = null
  } finally {
    loading.value = false
  }
}

function goWorkspace() {
  router.push(`/project/workspace/${projectId.value}`)
}

function goBack() {
  router.back()
}

function onTaskClick(task: { id?: number }) {
  if (task.id != null) {
    router.push(`/implementation/task/detail/${task.id}`)
  }
}

onMounted(loadAll)
</script>

<template>
  <div class="gantt-page">
    <PageHeader
      :title="`项目甘特图${project?.projectName ? ' · ' + project.projectName : ''}`"
      description="Dagre 从左到右布局 · 节点宽度按工期 · 关键路径红色加粗"
    >
      <template #actions>
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" @click="goWorkspace">返回工作区</el-button>
      </template>
    </PageHeader>

    <div class="gantt-body">
      <div class="gantt-main">
        <div v-if="loading" class="gantt-loading">
          <el-skeleton :rows="10" animated />
        </div>
        <EmptyState
          v-else-if="!project"
          icon="WarningFilled"
          title="项目不存在"
          description="无法加载项目信息，请检查项目ID"
        />
        <ProjectGantt
          v-else
          ref="ganttRef"
          :project-id="projectId"
          :baseline-id="selectedBaselineId ?? undefined"
          v-model:show-critical-path="showCriticalPath"
          @task-click="onTaskClick"
        />
      </div>

      <aside class="gantt-sidebar">
        <el-card shadow="never" class="side-card">
          <template #header>
            <span class="card-title">控制面板</span>
          </template>
          <div class="ctrl-row">
            <label class="ctrl-label">关键路径</label>
            <el-switch v-model="showCriticalPath" />
          </div>
          <div class="ctrl-row">
            <label class="ctrl-label">基线对比</label>
            <el-select
              v-model="selectedBaselineId"
              placeholder="不对比"
              clearable
              size="small"
              style="width: 100%; margin-top: 4px"
            >
              <el-option
                v-for="b in baselines"
                :key="b.id"
                :label="b.baselineName"
                :value="b.id!"
              />
            </el-select>
          </div>
        </el-card>

        <el-card shadow="never" class="side-card">
          <template #header>
            <span class="card-title">节点图例</span>
          </template>
          <ul class="legend-list">
            <li v-for="item in statusLegend" :key="item.label" class="legend-item">
              <span
                class="legend-swatch"
                :style="{ background: item.color }"
              />
              <span>{{ item.label }}</span>
            </li>
          </ul>
        </el-card>

        <el-card shadow="never" class="side-card">
          <template #header>
            <span class="card-title">依赖边图例</span>
          </template>
          <ul class="legend-list">
            <li
              v-for="item in edgeLegend"
              :key="item.label"
              class="legend-item edge-legend"
            >
              <svg width="48" height="10" class="edge-swatch">
                <line
                  x1="0"
                  y1="5"
                  x2="44"
                  y2="5"
                  stroke="#94a3b8"
                  stroke-width="2"
                  :stroke-dasharray="item.dash === 'solid' ? '' : item.dash"
                />
                <polygon points="44,1 48,5 44,9" fill="#94a3b8" />
              </svg>
              <span>{{ item.label }}</span>
            </li>
          </ul>
        </el-card>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.gantt-page {
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.gantt-body {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}
.gantt-main {
  flex: 1;
  min-width: 0;
}
.gantt-loading {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}
.gantt-sidebar {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: auto;
}
.side-card :deep(.el-card__header) {
  padding: 10px 14px;
  background: var(--pms-color-bg-page, #fafafa);
}
.side-card :deep(.el-card__body) {
  padding: 12px 14px;
}
.card-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #1f2937);
}
.ctrl-row {
  margin-bottom: 12px;
}
.ctrl-row:last-child {
  margin-bottom: 0;
}
.ctrl-label {
  display: block;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  margin-bottom: 4px;
}
.legend-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--pms-color-text-regular, #374151);
  margin-bottom: 8px;
}
.legend-item:last-child {
  margin-bottom: 0;
}
.legend-swatch {
  display: inline-block;
  width: 18px;
  height: 12px;
  border-radius: 2px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}
.edge-legend {
  gap: 8px;
}
.edge-swatch {
  flex-shrink: 0;
}
</style>
