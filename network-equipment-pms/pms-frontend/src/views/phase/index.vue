<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  advancePhase,
  listPhasesByProjectId,
  type PhaseExitGateResult,
  type PhaseExitGateViolation,
  type ProjectPhase
} from '@/api/project-phase'
import { getProject, type Project } from '@/api/project'
import type { EpTagType } from '@/types'

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.projectId))

const loading = ref(false)
const advancing = ref(false)
const project = ref<Project | null>(null)
const phases = ref<ProjectPhase[]>([])

// ===== 状态映射 =====
function phaseStatusMeta(status?: string): { label: string; tagType: EpTagType } {
  switch (status) {
    case 'IN_PROGRESS':
      return { label: '进行中', tagType: 'primary' }
    case 'COMPLETED':
      return { label: '已完成', tagType: 'success' }
    case 'SKIPPED':
      return { label: '已跳过', tagType: 'info' }
    default:
      return { label: '未开始', tagType: 'info' }
  }
}

/** 退出条件 4 类标签 */
function gateTypeLabel(gateType?: string): { label: string; tagType: EpTagType } {
  switch (gateType) {
    case 'DELIVERABLE':
      return { label: '交付件', tagType: 'warning' }
    case 'TASK':
      return { label: '任务', tagType: 'primary' }
    case 'MILESTONE':
      return { label: '里程碑', tagType: 'success' }
    case 'APPROVAL':
      return { label: '审批', tagType: 'danger' }
    default:
      return { label: gateType ?? '-', tagType: 'info' }
  }
}

function formatDate(date?: string) {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

// ===== 数据加载 =====
async function loadProject() {
  try {
    project.value = await getProject(projectId.value)
  } catch {
    /* handled by interceptor */
  }
}

async function loadPhases() {
  loading.value = true
  try {
    phases.value = (await listPhasesByProjectId(projectId.value)) ?? []
  } catch {
    phases.value = []
  } finally {
    loading.value = false
  }
}

async function reload() {
  await Promise.all([loadProject(), loadPhases()])
}

/** 当前可推进的阶段（唯一一个 IN_PROGRESS） */
const currentPhase = computed(() => phases.value.find((p) => p.status === 'IN_PROGRESS') ?? null)

const inProgressCount = computed(() => phases.value.filter((p) => p.status === 'IN_PROGRESS').length)
const completedCount = computed(() => phases.value.filter((p) => p.status === 'COMPLETED').length)

// ===== 推进阶段 =====
async function handleAdvance(phase: ProjectPhase) {
  if (!phase.id) return
  // 二次确认（写操作 + 幂等键已由请求拦截器注入）
  try {
    await ElMessageBox.confirm(
      `确定推进阶段「${phase.phaseName}」吗？将完成当前阶段并激活下一阶段。`,
      '推进确认',
      { type: 'warning', confirmButtonText: '推进', cancelButtonText: '取消' }
    )
  } catch {
    return // 用户取消
  }

  advancing.value = true
  try {
    const result = await advancePhase(phase.id)
    // 响应拦截器在 code===200 时直接解包为 data；
    // 推进失败时后端 ProjectExceptionHandler 返回 code=200 + data.success=false，
    // 故此处以 resolved 形式收到 PhaseExitGateResult，需运行时判定。
    if (result && typeof result === 'object' && 'success' in result && result.success === false) {
      showViolations(result as PhaseExitGateResult)
    } else {
      ElMessage.success('阶段推进成功')
      await loadPhases()
    }
  } catch {
    /* 网络异常由拦截器统一提示 */
  } finally {
    advancing.value = false
  }
}

// ===== 违规弹窗 =====
const violationDialogVisible = ref(false)
const violationResult = ref<PhaseExitGateResult | null>(null)

function showViolations(result: PhaseExitGateResult) {
  violationResult.value = result
  violationDialogVisible.value = true
}

function violationRowKey(v: PhaseExitGateViolation, idx: number): string {
  return `${v.gateType ?? ''}-${v.businessId ?? ''}-${idx}`
}

function goBack() {
  router.back()
}

/** 返回项目详情（阶段 Tab） */
function backToProject() {
  router.push({ name: 'ProjectDetail', params: { id: String(projectId.value) } })
}

onMounted(reload)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header :icon="null" @back="goBack">
      <template #content>
        <div class="header-content">
          <span class="header-title">{{ project?.name ?? '项目阶段管理' }}</span>
          <el-tag v-if="project?.code" type="info" effect="plain" size="small">{{ project.code }}</el-tag>
          <span v-if="phases.length > 0" class="header-stat">
            共 {{ phases.length }} 个阶段 · 已完成 {{ completedCount }} · 进行中 {{ inProgressCount }}
          </span>
        </div>
      </template>
      <template #extra>
        <el-button type="primary" link @click="backToProject">返回项目详情</el-button>
      </template>
    </el-page-header>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>阶段流水线</span>
          <el-button type="primary" :icon="'Refresh'" link @click="reload">刷新</el-button>
        </div>
      </template>

      <el-empty v-if="phases.length === 0" description="暂无阶段数据，可从模板创建项目时生成" />

      <!-- 横向流水线：阶段卡片 + 连接箭头 -->
      <div v-else class="phase-pipeline">
        <template v-for="(phase, idx) in phases" :key="phase.id ?? idx">
          <div
            class="phase-card"
            :class="{
              'phase-completed': phase.status === 'COMPLETED',
              'phase-in-progress': phase.status === 'IN_PROGRESS',
              'phase-skipped': phase.status === 'SKIPPED',
              'phase-not-started': !phase.status || phase.status === 'NOT_STARTED'
            }"
          >
            <div class="phase-card-header">
              <span class="phase-order">#{{ phase.sortOrder ?? idx + 1 }}</span>
              <el-tag :type="phaseStatusMeta(phase.status).tagType" size="small" effect="dark">
                {{ phaseStatusMeta(phase.status).label }}
              </el-tag>
            </div>
            <div class="phase-name" :title="phase.phaseName">{{ phase.phaseName }}</div>
            <div class="phase-code">{{ phase.phaseCode || '-' }}</div>
            <el-divider class="phase-divider" />
            <div class="phase-meta">
              <div class="meta-row">
                <span class="meta-label">计划开始</span>
                <span class="meta-value">{{ formatDate(phase.plannedStartDate) }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">计划结束</span>
                <span class="meta-value">{{ formatDate(phase.plannedEndDate) }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">实际开始</span>
                <span class="meta-value">{{ formatDate(phase.actualStartDate) }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">实际结束</span>
                <span class="meta-value">{{ formatDate(phase.actualEndDate) }}</span>
              </div>
            </div>
            <div class="phase-actions">
              <el-button
                v-if="phase.status === 'IN_PROGRESS'"
                type="primary"
                size="small"
                :loading="advancing"
                @click="handleAdvance(phase)"
              >
                推进阶段
              </el-button>
              <span v-else-if="phase.status === 'COMPLETED'" class="phase-done-tip">已完成</span>
              <span v-else-if="phase.status === 'SKIPPED'" class="phase-done-tip">已跳过</span>
              <span v-else class="phase-done-tip">等待前置阶段</span>
            </div>
          </div>
          <!-- 阶段间连接箭头（最后一个不显示） -->
          <div v-if="idx < phases.length - 1" class="phase-connector">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </template>
      </div>

      <!-- 当前推进阶段提示 -->
      <div v-if="currentPhase" class="current-tip">
        <el-icon><InfoFilled /></el-icon>
        当前推进阶段：<strong>{{ currentPhase.phaseName }}</strong>
        ，点击「推进阶段」按钮将完成该阶段并激活下一阶段。
      </div>
      <div v-else-if="phases.length > 0" class="current-tip current-tip-warning">
        <el-icon><WarningFilled /></el-icon>
        当前没有进行中的阶段。若所有阶段已完成，项目将进入收尾状态（CLOSING）。
      </div>
    </el-card>

    <!-- 违规弹窗：展示 PhaseExitGate 校验失败详情 -->
    <el-dialog
      v-model="violationDialogVisible"
      title="阶段推进被阻止 — 退出条件未满足"
      width="720px"
      destroy-on-close
    >
      <div v-if="violationResult" class="violation-summary">
        <el-alert
          :title="violationResult.errorMessage || '当前阶段退出条件未满足'"
          type="error"
          :closable="false"
          show-icon
        >
          <template v-if="violationResult.errorCode" #default>
            <div class="violation-code">错误码：{{ violationResult.errorCode }}</div>
          </template>
        </el-alert>
        <div class="violation-count">
          共 {{ violationResult.violations?.length ?? 0 }} 项未满足条件：
        </div>
      </div>
      <el-table
        :data="violationResult?.violations ?? []"
        border
        stripe
        :row-key="violationRowKey"
        empty-text="无违规项"
      >
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="gateTypeLabel(row.gateType).tagType" size="small">
              {{ gateTypeLabel(row.gateType).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="业务对象" min-width="160">
          <template #default="{ row }">
            <span v-if="row.businessName">{{ row.businessName }}</span>
            <span v-else-if="row.businessId">#{{ row.businessId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="期望状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.expectedStatus" type="success" size="small" effect="plain">
              {{ row.expectedStatus }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="实际状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.actualStatus" type="danger" size="small" effect="plain">
              {{ row.actualStatus }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="violationDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-stat {
  font-size: 13px;
  color: #909399;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #303133;
}

/* 横向流水线 */
.phase-pipeline {
  display: flex;
  align-items: stretch;
  flex-wrap: wrap;
  gap: 0;
  padding: 8px 0 4px;
  overflow-x: auto;
}
.phase-card {
  flex: 0 0 auto;
  width: 220px;
  padding: 14px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 6px;
  transition: box-shadow 0.2s, border-color 0.2s;
}
.phase-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.phase-completed {
  border-color: #67c23a;
  background: #f0f9eb;
}
.phase-in-progress {
  border-color: #409eff;
  background: #ecf5ff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
.phase-skipped {
  border-color: #c0c4cc;
  background: #f4f4f5;
  opacity: 0.75;
}
.phase-not-started {
  border-color: #ebeef5;
  background: #fafafa;
}
.phase-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.phase-order {
  font-size: 12px;
  color: #909399;
  font-weight: 600;
}
.phase-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.phase-code {
  font-size: 12px;
  color: #909399;
}
.phase-divider {
  margin: 4px 0;
}
.phase-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
}
.meta-row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.meta-label {
  color: #909399;
}
.meta-value {
  color: #303133;
}
.phase-actions {
  margin-top: 8px;
  display: flex;
  justify-content: center;
  min-height: 32px;
  align-items: center;
}
.phase-done-tip {
  font-size: 12px;
  color: #c0c4cc;
}

/* 阶段连接箭头 */
.phase-connector {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  color: #c0c4cc;
  font-size: 20px;
}

.current-tip {
  margin-top: 16px;
  padding: 10px 14px;
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 6px;
  color: #303133;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.current-tip-warning {
  background: #fdf6ec;
  border-color: #faecd8;
}

/* 违规弹窗 */
.violation-summary {
  margin-bottom: 12px;
}
.violation-code {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.violation-count {
  margin-top: 10px;
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}
</style>
