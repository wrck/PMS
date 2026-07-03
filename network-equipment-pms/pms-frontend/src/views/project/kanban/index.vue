<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboard, type Project, type ProjectDashboard, type ProjectStatus } from '@/api/project'

const router = useRouter()

const loading = ref(false)
const dashboard = ref<ProjectDashboard>({})

// 看板列定义（按交付流程顺序排列）
const columns: { status: ProjectStatus; title: string; color: string }[] = [
  { status: 'PENDING', title: '待审批', color: '#909399' },
  { status: 'APPROVED', title: '已立项', color: '#e6a23c' },
  { status: 'IN_PROGRESS', title: '执行中', color: '#409eff' },
  { status: 'INITIAL_ACCEPTANCE', title: '初验', color: '#e6a23c' },
  { status: 'FINAL_ACCEPTANCE', title: '终验中', color: '#f56c6c' },
  { status: 'COMPLETED', title: '已完成', color: '#67c23a' }
]

function getColumnProjects(status: ProjectStatus): Project[] {
  return dashboard.value[status] ?? []
}

function getColumnCount(status: ProjectStatus): number {
  return getColumnProjects(status).length
}

function formatDate(date?: string) {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

// 判断项目是否逾期（计划结束日期早于今天且未完成）
function isOverdue(project: Project): boolean {
  if (!project.planEndDate) return false
  if (project.status === 'COMPLETED' || project.status === 'CLOSED') return false
  const end = project.planEndDate.length > 10 ? project.planEndDate.substring(0, 10) : project.planEndDate
  const today = new Date()
  const todayStr =
    today.getFullYear() +
    '-' +
    String(today.getMonth() + 1).padStart(2, '0') +
    '-' +
    String(today.getDate()).padStart(2, '0')
  return end < todayStr
}

function viewDetail(project: Project) {
  if (!project.id) return
  router.push(`/project/detail/${project.id}`)
}

async function loadDashboard() {
  loading.value = true
  try {
    dashboard.value = (await getDashboard()) ?? {}
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

const totalCount = computed(() =>
  columns.reduce((sum, col) => sum + getColumnCount(col.status), 0)
)

onMounted(loadDashboard)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">交付看板</span>
          <span class="page-summary">项目总数：{{ totalCount }}</span>
          <el-button class="refresh-btn" :icon="'Refresh'" link @click="loadDashboard">刷新</el-button>
        </div>
      </template>

      <div v-loading="loading" class="kanban-board">
        <div v-for="col in columns" :key="col.status" class="kanban-col">
          <div class="kanban-col-header" :style="{ borderTopColor: col.color }">
            <span class="col-title">
              <span class="col-dot" :style="{ backgroundColor: col.color }"></span>
              {{ col.title }}
            </span>
            <el-badge :value="getColumnCount(col.status)" :max="999" class="col-badge" />
          </div>
          <div class="kanban-col-body">
            <div
              v-for="project in getColumnProjects(col.status)"
              :key="project.id"
              class="kanban-card"
              :class="{ overdue: isOverdue(project) }"
              @click="viewDetail(project)"
            >
              <div class="card-name" :title="project.name">{{ project.name }}</div>
              <div class="card-code">{{ project.code || '-' }}</div>
              <div class="card-progress">
                <el-progress
                  :percentage="Number(project.progress ?? 0)"
                  :stroke-width="8"
                  :status="(Number(project.progress ?? 0) >= 100 ? 'success' : '') as any"
                />
              </div>
              <div class="card-row">
                <el-icon><User /></el-icon>
                <span>{{ project.customerName || '-' }}</span>
              </div>
              <div class="card-row">
                <el-icon><Avatar /></el-icon>
                <span>{{ project.managerName || '-' }}</span>
              </div>
              <div class="card-row">
                <el-icon><Calendar /></el-icon>
                <span class="card-date" :class="{ 'date-overdue': isOverdue(project) }">
                  {{ formatDate(project.planEndDate) }}
                </span>
                <el-tag v-if="isOverdue(project)" size="small" type="danger" effect="plain" class="overdue-tag">
                  逾期
                </el-tag>
              </div>
            </div>
            <el-empty
              v-if="getColumnCount(col.status) === 0"
              :image-size="50"
              description="暂无项目"
              class="col-empty"
            />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.page-summary {
  color: #909399;
  font-size: 13px;
}
.refresh-btn {
  margin-left: auto;
}

.kanban-board {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.kanban-col {
  flex: 0 0 300px;
  background-color: #f5f7fa;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  min-height: 520px;
  max-height: calc(100vh - 220px);
}

.kanban-col-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-top: 3px solid #909399;
  border-bottom: 1px solid #e6e6eb;
  background-color: #fff;
  border-radius: 6px 6px 0 0;
}

.col-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #303133;
}

.col-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.col-badge :deep(.el-badge__content) {
  background-color: #c0c4cc;
}

.kanban-col-body {
  padding: 10px;
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.kanban-card {
  background-color: #fff;
  border-radius: 6px;
  padding: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.kanban-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.kanban-card.overdue {
  border-left-color: #f56c6c;
}

.card-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.card-code {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.card-progress {
  margin-bottom: 8px;
}

.card-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
}

.card-row .el-icon {
  color: #909399;
  flex-shrink: 0;
}

.card-date {
  flex: 1;
}

.card-date.date-overdue {
  color: #f56c6c;
  font-weight: 600;
}

.overdue-tag {
  margin-left: auto;
}

.col-empty {
  margin-top: 20px;
}
</style>
