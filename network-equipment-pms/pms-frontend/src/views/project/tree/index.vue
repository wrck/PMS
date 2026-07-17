<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getProjectProgress,
  getProjectTree,
  listProjects,
  type Project,
  type ProjectProgress,
  type ProjectTreeNode
} from '@/api/project'
import type { EpTagType } from '@/types'

const router = useRouter()

const treeLoading = ref(false)
const treeData = ref<ProjectTreeNode | null>(null)
const progress = ref<ProjectProgress | null>(null)

// 根项目选择器（按名称远程搜索）
const selectedProjectId = ref<number | undefined>(undefined)
const projectOptions = ref<Project[]>([])
const searching = ref(false)

// 状态标签映射（兼容生命周期新状态与历史状态）
const statusMeta = (status?: string): { label: string; tagType: EpTagType } => {
  switch (status) {
    case 'PLANNING':
      return { label: '规划中', tagType: 'info' }
    case 'EXECUTING':
    case 'IN_PROGRESS':
      return { label: '执行中', tagType: 'primary' }
    case 'CLOSING':
      return { label: '收尾中', tagType: 'warning' }
    case 'CLOSED':
      return { label: '已关闭', tagType: 'info' }
    case 'CANCELLED':
      return { label: '已取消', tagType: 'danger' }
    case 'APPROVED':
      return { label: '已立项', tagType: 'warning' }
    case 'PENDING':
      return { label: '待审批', tagType: 'info' }
    case 'COMPLETED':
      return { label: '已完成', tagType: 'success' }
    case 'REJECTED':
      return { label: '已驳回', tagType: 'danger' }
    default:
      return { label: status ?? '-', tagType: 'info' }
  }
}

async function remoteSearch(query: string) {
  if (!query) {
    projectOptions.value = []
    return
  }
  searching.value = true
  try {
    const res = await listProjects({ page: 1, size: 20, projectName: query })
    projectOptions.value = res?.records ?? []
  } catch {
    /* handled by interceptor */
  } finally {
    searching.value = false
  }
}

async function loadTree(id: number) {
  treeLoading.value = true
  try {
    treeData.value = await getProjectTree(id)
    loadProgress(id)
  } catch {
    treeData.value = null
  } finally {
    treeLoading.value = false
  }
}

async function loadProgress(id: number) {
  try {
    progress.value = await getProjectProgress(id)
  } catch {
    progress.value = null
  }
}

function onSelectChange(id: number) {
  if (id) loadTree(id)
}

/** el-tree props：标签取 projectName，子节点取 children */
const treeProps = {
  label: 'projectName',
  children: 'children'
}

function goToDetail(id: number) {
  router.push({ name: 'ProjectDetail', params: { id: String(id) } })
}

onMounted(async () => {
  // 默认载入一批顶层项目供选择
  try {
    const res = await listProjects({ page: 1, size: 20 })
    projectOptions.value = res?.records ?? []
  } catch {
    /* handled by interceptor */
  }
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="selector-row">
        <span class="selector-label">选择主项目：</span>
        <el-select
          v-model="selectedProjectId"
          filterable
          remote
          reserve-keyword
          clearable
          placeholder="输入项目名称搜索"
          :remote-method="remoteSearch"
          :loading="searching"
          style="width: 360px"
          @change="onSelectChange"
        >
          <el-option
            v-for="p in projectOptions"
            :key="p.id"
            :label="p.name"
            :value="p.id as number"
          />
        </el-select>
        <el-tag v-if="progress" type="info" effect="plain">
          加权平均进度：{{ progress.aggregatedProgress }}%
        </el-tag>
      </div>
    </el-card>

    <el-card shadow="never" v-loading="treeLoading">
      <template v-if="treeData">
        <div class="root-info">
          <span class="root-title">{{ treeData.projectName }}</span>
          <el-tag :type="statusMeta(treeData.status).tagType" size="small">
            {{ statusMeta(treeData.status).label }}
          </el-tag>
          <el-progress
            :percentage="Number(treeData.progress ?? 0)"
            :stroke-width="10"
            style="width: 180px"
          />
          <el-button link type="primary" @click="goToDetail(treeData.id)">查看详情</el-button>
        </div>

        <el-tree
          :data="treeData.children"
          :props="treeProps"
          :default-expand-all="true"
          node-key="id"
          :expand-on-click-node="false"
          class="project-tree"
        >
          <template #default="{ data }">
            <div class="tree-node" @click="goToDetail(data.id)">
              <span class="node-name">{{ data.projectName }}</span>
              <el-tag :type="statusMeta(data.status).tagType" size="small" effect="plain">
                {{ statusMeta(data.status).label }}
              </el-tag>
              <el-progress
                :percentage="Number(data.progress ?? 0)"
                :stroke-width="8"
                :show-text="false"
                class="node-progress"
              />
              <span class="node-progress-text">{{ data.progress ?? 0 }}%</span>
            </div>
          </template>
          <template #empty>
            <el-empty description="该主项目暂无子项目" />
          </template>
        </el-tree>
      </template>
      <el-empty v-else description="请选择一个主项目查看其子项目树" />
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.selector-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.selector-label {
  font-weight: 600;
  color: #303133;
}
.root-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}
.root-title {
  font-size: 15px;
  font-weight: 600;
}
.project-tree {
  margin-top: 8px;
}
.tree-node {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  padding: 2px 0;
  cursor: pointer;
}
.node-name {
  font-size: 14px;
  color: #303133;
}
.node-progress {
  width: 100px;
}
.node-progress-text {
  font-size: 12px;
  color: #909399;
  min-width: 36px;
}
</style>
