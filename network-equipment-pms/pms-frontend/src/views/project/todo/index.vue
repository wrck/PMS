<script setup lang="ts">
// =============================================================================
// ProjectTodo - 项目维度待办视图
// -----------------------------------------------------------------------------
// Task 6 Step 2：复用 GlobalTodoCenter 组件，传入 projectId 进行过滤。
// 顶部 PageHeader：标题"项目待办 · {项目名}"
// =============================================================================
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElButton } from 'element-plus'
import { getProject, type Project } from '@/api/project'
import PageHeader from '@/components/common/PageHeader.vue'
import GlobalTodoCenter from '@/components/workflow/GlobalTodoCenter.vue'

defineOptions({ name: 'ProjectTodo' })

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.id))
const project = ref<Project | null>(null)

async function loadProject() {
  if (!projectId.value || Number.isNaN(projectId.value)) return
  try {
    project.value = await getProject(projectId.value)
  } catch {
    project.value = null
  }
}

const pageTitle = computed(() =>
  project.value ? `项目待办 · ${project.value.projectName}` : '项目待办'
)

function goBack() {
  if (history.length > 1) {
    router.back()
  } else {
    router.push(`/project/workspace/${projectId.value}`)
  }
}

onMounted(loadProject)
</script>

<template>
  <div class="project-todo-page">
    <PageHeader :title="pageTitle" description="聚合本项目下的所有待办事项（审批 / 任务 / 阶段推进 / 基线告警）">
      <template #actions>
        <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
      </template>
    </PageHeader>
    <GlobalTodoCenter :project-id="projectId" />
  </div>
</template>

<style scoped>
.project-todo-page {
  padding: 16px 24px;
}
</style>
