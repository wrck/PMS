<script setup lang="ts">
// =============================================================================
// ProjectOverview - 项目概览 Tab
// -----------------------------------------------------------------------------
// 在 ProjectWorkspace 的「概览」Tab 内渲染，三块内容：
//   1. 基础信息卡片：项目名/状态/类型/起止日期/项目经理/客户/描述
//   2. 关键指标卡片：进度 / 任务统计 / 交付件统计 / 基线条数
//      （任务/交付件/基线计数将在后续 Task 接入对应 list API，此处先占位）
//   3. 主子项目关系：若有父项目则显示父项目；若有子项目则列出子项目列表
//      （通过 getProjectTree 获取子树，根节点 parentProjectId 判断父项目）
// =============================================================================
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  getProject,
  getProjectTree,
  type Project,
  type ProjectTreeNode
} from '@/api/project'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'

interface Props {
  project: Project
}
const props = defineProps<Props>()

const router = useRouter()

const tree = ref<ProjectTreeNode | null>(null)
const parentProject = ref<Project | null>(null)
const treeLoading = ref(false)

const TYPE_LABELS: Record<string, string> = {
  NETWORK_DEVICE: '网络设备',
  SECURITY: '安全设备',
  DATACENTER: '数据中心'
}

function typeLabel(type?: string): string {
  return TYPE_LABELS[type ?? ''] ?? type ?? '-'
}

function formatDate(date?: string): string {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

async function loadTree() {
  if (!props.project?.id) return
  treeLoading.value = true
  try {
    tree.value = await getProjectTree(props.project.id)
    // 若存在父项目 ID，加载父项目基础信息
    const parentId = tree.value?.parentProjectId
    if (parentId && parentId > 0) {
      try {
        parentProject.value = await getProject(parentId)
      } catch {
        parentProject.value = null
      }
    } else {
      parentProject.value = null
    }
  } catch {
    tree.value = null
    parentProject.value = null
  } finally {
    treeLoading.value = false
  }
}

function goProject(id: number) {
  router.push(`/project/workspace/${id}`)
}

const childProjects = ref<ProjectTreeNode[]>([])
watch(tree, (t) => {
  childProjects.value = t?.children ?? []
})

onMounted(loadTree)

// 切换项目时重新加载（workspace 复用同一组件实例的场景）
watch(
  () => props.project?.id,
  (newId) => {
    if (newId) loadTree()
  }
)
</script>

<template>
  <div class="project-overview">
    <!-- 关键指标卡片 -->
    <el-row :gutter="16" class="metric-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <template #header>项目进度</template>
          <div class="metric-center">
            <el-progress type="circle" :percentage="Number(project.progress ?? 0)" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <template #header>任务统计</template>
          <div class="metric-block">
            <div class="metric-line">
              <span class="metric-key">任务总数</span>
              <span class="metric-val">—</span>
            </div>
            <div class="metric-line">
              <span class="metric-key">已完成</span>
              <span class="metric-val">—</span>
            </div>
            <div class="metric-line">
              <span class="metric-key">进行中</span>
              <span class="metric-val">—</span>
            </div>
            <div class="metric-hint">数据将在后续 Task 接入任务 API</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <template #header>交付件统计</template>
          <div class="metric-block">
            <div class="metric-line">
              <span class="metric-key">交付件总数</span>
              <span class="metric-val">—</span>
            </div>
            <div class="metric-line">
              <span class="metric-key">已完成</span>
              <span class="metric-val">—</span>
            </div>
            <div class="metric-hint">数据将在后续 Task 接入交付件 API</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <template #header>基线</template>
          <div class="metric-block">
            <div class="metric-line">
              <span class="metric-key">基线条数</span>
              <span class="metric-val">—</span>
            </div>
            <div class="metric-hint">数据将在后续 Task 接入基线 API</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 基础信息 + 主子关系 -->
    <el-row :gutter="16" class="info-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover">
          <template #header>基础信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="项目名称">{{ project.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="项目编码">{{ project.code || '-' }}</el-descriptions-item>
            <el-descriptions-item label="项目状态">
              <ProjectStatusTag v-if="project.status" :status="project.status" size="small" />
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="项目类型">{{ typeLabel(project.type) }}</el-descriptions-item>
            <el-descriptions-item label="计划开始">{{ formatDate(project.planStartDate) }}</el-descriptions-item>
            <el-descriptions-item label="计划结束">{{ formatDate(project.planEndDate) }}</el-descriptions-item>
            <el-descriptions-item label="项目经理">{{ project.managerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ project.customerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="项目描述" :span="2">
              {{ project.description || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" v-loading="treeLoading">
          <template #header>主子项目关系</template>
          <!-- 父项目 -->
          <div v-if="parentProject" class="relation-block">
            <div class="relation-title">父项目</div>
            <el-link type="primary" @click="goProject(parentProject.id!)">
              {{ parentProject.name }}
            </el-link>
            <span class="relation-meta">（{{ parentProject.code || '-' }}）</span>
          </div>
          <div v-else class="relation-block">
            <div class="relation-title">父项目</div>
            <span class="relation-empty">无（当前为主项目）</span>
          </div>
          <!-- 子项目列表 -->
          <div class="relation-block">
            <div class="relation-title">
              子项目（{{ childProjects.length }}）
            </div>
            <el-empty
              v-if="childProjects.length === 0"
              description="暂无子项目"
              :image-size="60"
            />
            <ul v-else class="child-list">
              <li v-for="child in childProjects" :key="child.id" class="child-item">
                <el-link type="primary" @click="goProject(child.id)">
                  {{ child.projectName }}
                </el-link>
                <span class="relation-meta">（{{ child.projectCode || '-' }}）</span>
                <ProjectStatusTag
                  v-if="child.status"
                  :status="child.status"
                  size="small"
                />
                <span v-if="typeof child.progress === 'number'" class="relation-progress">
                  {{ child.progress }}%
                </span>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.project-overview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.metric-row,
.info-row {
  margin-bottom: 0;
}
.metric-center {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px 0;
}
.metric-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.metric-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}
.metric-key {
  color: var(--pms-color-text-secondary);
}
.metric-val {
  color: var(--pms-color-text-primary);
  font-weight: 600;
  font-size: 16px;
}
.metric-hint {
  font-size: 12px;
  color: var(--pms-color-text-placeholder);
  margin-top: 4px;
}
.relation-block {
  margin-bottom: 16px;
}
.relation-block:last-child {
  margin-bottom: 0;
}
.relation-title {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 8px;
}
.relation-empty {
  color: var(--pms-color-text-placeholder);
  font-size: 13px;
}
.relation-meta {
  color: var(--pms-color-text-secondary);
  font-size: 13px;
  margin-left: 4px;
}
.child-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.child-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  border-bottom: 1px dashed var(--pms-color-border-light);
}
.child-item:last-child {
  border-bottom: none;
}
.relation-progress {
  margin-left: auto;
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}
</style>
