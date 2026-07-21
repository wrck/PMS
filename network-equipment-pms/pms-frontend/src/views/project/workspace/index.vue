<script setup lang="ts">
// =============================================================================
// ProjectWorkspace - 项目工作区（新枢纽页）
// -----------------------------------------------------------------------------
// 8 Tab 整合：概览 / 阶段 / 任务 / 交付件 / 基线 / 审批 / 成员 / 配置
// 顶部 PageHeader：项目名 + 状态标签 + 关键操作（关闭 / 取消 / 从模板创建子项目）
// 进入时调用 setProject 同步上下文，离开时调用 clearProject 清理。
// Tab 子组件路径约定见实施计划 Task 3；本 Task 仅做引用占位，子组件将在
// 后续 Task（4+）中按工作区上下文重做。
// =============================================================================
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelProject,
  closeProject,
  getProject,
  getProjectProgress,
  type Project
} from '@/api/project'
import { useProjectContext } from '@/composables/useProjectContext'
import PageHeader from '@/components/common/PageHeader.vue'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'
import ProjectTemplateSelector from '@/components/ProjectTemplateSelector.vue'
import ProjectOverview from '@/views/project/overview/index.vue'
import PhaseManage from '@/views/phase/index.vue'
import TaskListView from '@/views/task/list/index.vue'
import DeliverableList from '@/views/deliverable/index.vue'
import BaselineList from '@/views/baseline/index.vue'
import ApprovalCenter from '@/views/workflow/approval-center/index.vue'
import ProjectMemberList from '@/views/project-member/index.vue'
import ProjectConfig from '@/views/project-config/index.vue'

defineOptions({ name: 'ProjectWorkspace' })

const route = useRoute()
const router = useRouter()
const { setProject, clearProject } = useProjectContext()

const projectId = computed(() => Number(route.params.id))
const project = ref<Project | null>(null)
const progress = ref(0)
const activeTab = ref('overview')
const loading = ref(false)
const acting = ref(false)

const templateSelectorRef = ref<InstanceType<typeof ProjectTemplateSelector> | null>(null)

async function loadProject() {
  if (!projectId.value || Number.isNaN(projectId.value)) return
  loading.value = true
  try {
    project.value = await getProject(projectId.value)
    setProject({
      id: project.value.id!,
      projectCode: project.value.projectCode ?? '',
      projectName: project.value.projectName,
      status: project.value.status,
      currentPhaseId: null
    })
    // 进度优先取聚合值（含子项目加权），失败回退到项目自身 progress
    try {
      const prog = await getProjectProgress(projectId.value)
      progress.value = prog?.aggregatedProgress ?? project.value.progress ?? 0
    } catch {
      progress.value = project.value.progress ?? 0
    }
  } catch {
    project.value = null
  } finally {
    loading.value = false
  }
}

function goGantt() {
  router.push(`/project/${projectId.value}/gantt`)
}

function goTodo() {
  router.push(`/project/${projectId.value}/todo`)
}

function handleOpenCreateDialog() {
  templateSelectorRef.value?.open()
}

function handleSubprojectCreated(newProjectId: number) {
  ElMessage.success(`子项目已创建（ID: ${newProjectId}）`)
  loadProject()
}

async function handleClose() {
  try {
    await ElMessageBox.confirm(
      `确定关闭项目「${project.value?.projectName ?? ''}」吗？关闭后项目将不可继续执行。`,
      '关闭项目',
      { type: 'warning', confirmButtonText: '确定关闭', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  acting.value = true
  try {
    await closeProject(projectId.value)
    ElMessage.success('项目已关闭')
    loadProject()
  } catch {
    /* handled by interceptor */
  } finally {
    acting.value = false
  }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm(
      `确定取消项目「${project.value?.projectName ?? ''}」吗？此操作不可逆。`,
      '取消项目',
      { type: 'warning', confirmButtonText: '确定取消', cancelButtonText: '返回' }
    )
  } catch {
    return
  }
  acting.value = true
  try {
    await cancelProject(projectId.value)
    ElMessage.success('项目已取消')
    loadProject()
  } catch {
    /* handled by interceptor */
  } finally {
    acting.value = false
  }
}

// 路由参数变化时重新加载（侧栏切换项目场景）
watch(() => route.params.id, loadProject)

onMounted(() => {
  const tab = route.query.tab
  if (typeof tab === 'string' && tab) activeTab.value = tab
  loadProject()
})

onBeforeUnmount(() => {
  clearProject()
})
</script>

<template>
  <div class="project-workspace" v-loading="loading">
    <PageHeader :title="project?.projectName ?? '加载中...'">
      <template #actions>
        <el-button-group>
          <el-button @click="goGantt">甘特图</el-button>
          <el-button @click="goTodo">待办</el-button>
        </el-button-group>
        <el-button :loading="acting" @click="handleClose">关闭项目</el-button>
        <el-button :loading="acting" type="danger" plain @click="handleCancel">取消项目</el-button>
        <el-button type="primary" @click="handleOpenCreateDialog">从模板创建子项目</el-button>
      </template>
    </PageHeader>

    <div class="workspace-meta">
      <span class="meta-item">
        <span class="meta-label">项目编码：</span>
        <span class="meta-value">{{ project?.projectCode || '-' }}</span>
      </span>
      <span class="meta-item">
        <span class="meta-label">项目经理：</span>
        <span class="meta-value">{{ project?.projectManagerName || '-' }}</span>
      </span>
      <span class="meta-item">
        <span class="meta-label">状态：</span>
        <ProjectStatusTag v-if="project?.status" :status="project.status" size="small" />
        <span v-else class="meta-value">-</span>
      </span>
      <span class="meta-item">
        <span class="meta-label">进度：</span>
        <span class="meta-value">{{ progress }}%</span>
      </span>
    </div>

    <el-tabs v-model="activeTab" class="workspace-tabs">
      <el-tab-pane label="概览" name="overview">
        <ProjectOverview v-if="project" :project="project" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <el-tab-pane label="阶段" name="phase">
        <PhaseManage v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <el-tab-pane label="任务" name="task">
        <TaskListView v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <el-tab-pane label="交付件" name="deliverable">
        <DeliverableList v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <el-tab-pane label="基线" name="baseline">
        <BaselineList v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <!-- 注：BaselineList 嵌入工作区时仅显示当前项目 + 子项目基线，禁止跨项目 -->
      <el-tab-pane label="审批" name="approval">
        <ApprovalCenter v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <el-tab-pane label="成员" name="member">
        <ProjectMemberList v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
      <el-tab-pane label="配置" name="config">
        <ProjectConfig v-if="project" :project-id="projectId" />
        <el-empty v-else description="暂无项目数据" />
      </el-tab-pane>
    </el-tabs>

    <!-- 从模板创建子项目（复用全局模板选择器；后续 Task 可替换为子项目专用对话框） -->
    <ProjectTemplateSelector ref="templateSelectorRef" @success="handleSubprojectCreated" />
  </div>
</template>

<style scoped>
/* 工作区采用自然文档流：让外层 el-main 负责纵向滚动，
   不要在内部用 height:100% + overflow:auto 产生嵌套滚动条
   导致页面内容超出窗口时无法滚动浏览。 */
.project-workspace {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}
.workspace-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 0 0 12px;
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  border-bottom: 1px solid var(--pms-color-border-light);
  margin-bottom: 12px;
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.meta-label {
  color: var(--pms-color-text-secondary);
}
.meta-value {
  color: var(--pms-color-text-primary);
  font-weight: 500;
}
.workspace-tabs {
  /* tabs 内容跟随自然流，超出部分由外层 el-main 滚动 */
  flex: 1 1 auto;
}
.workspace-tabs :deep(.el-tabs__content) {
  padding-top: 16px;
  /* 内容超出时由外层 main 滚动，此处不再 overflow:auto */
  overflow: visible;
}
</style>
