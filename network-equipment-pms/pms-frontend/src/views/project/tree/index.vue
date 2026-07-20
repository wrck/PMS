<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Edit, Folder, FolderOpened, Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  getProject,
  getProjectProgress,
  getProjectTree,
  listProjects,
  type Project,
  type ProjectProgress,
  type ProjectTreeNode
} from '@/api/project'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'

defineOptions({ name: 'ProjectTree' })

const router = useRouter()

const loading = ref(false)
const treeLoading = ref(false)

// 项目列表（顶层根）
const rootProjects = ref<Project[]>([])
// 多棵树（每个根项目对应一棵树）
const treeData = ref<ProjectTreeNode[]>([])
const treeProps = { label: 'projectName', children: 'children' }
const treeRef = ref()

const selectedProject = ref<Project | null>(null)
const selectedProgress = ref<ProjectProgress | null>(null)
const selectedTree = ref<ProjectTreeNode | null>(null)

// 搜索关键字
const searchKeyword = ref('')

const selectedChildren = computed<ProjectTreeNode[]>(() => {
  return selectedTree.value?.children ?? []
})

// el-tree 过滤方法
function filterNode(value: string, data: any) {
  if (!value) return true
  return (data.projectName || '').toLowerCase().includes(value)
}

watch(searchKeyword, (val) => {
  treeRef.value?.filter(val)
})

// ============== 加载 ==============

async function loadRootProjects() {
  loading.value = true
  try {
    const res = await listProjects({ page: 1, size: 200 })
    rootProjects.value = res?.records ?? []
    // 默认对每个顶层项目拉取一次树（限制最多 50 个避免性能问题）
    const top = rootProjects.value.slice(0, 50)
    const trees: ProjectTreeNode[] = []
    for (const p of top) {
      if (!p.id) continue
      try {
        const t = await getProjectTree(p.id)
        if (t) trees.push(t)
      } catch {
        /* skip failed */
      }
    }
    treeData.value = trees
    if (trees.length > 0) {
      handleNodeClick(trees[0])
    }
  } catch {
    rootProjects.value = []
    treeData.value = []
  } finally {
    loading.value = false
  }
}

async function loadProjectDetail(id: number) {
  treeLoading.value = true
  try {
    const [proj, prog] = await Promise.all([
      getProject(id),
      getProjectProgress(id).catch(() => null)
    ])
    selectedProject.value = proj
    selectedProgress.value = prog
  } catch {
    selectedProject.value = null
    selectedProgress.value = null
  } finally {
    treeLoading.value = false
  }
}

function handleNodeClick(data: ProjectTreeNode) {
  selectedTree.value = data
  if (data.id) loadProjectDetail(data.id)
}

function refreshTree() {
  loadRootProjects()
}

// ============== 跳转 ==============

function goWorkspace(id?: number) {
  if (!id) return
  router.push(`/project/workspace/${id}`)
}

function goEdit(id?: number) {
  if (!id) return
  router.push(`/project/detail/${id}`)
}

function goCreateSub(parentId?: number) {
  if (!parentId) return
  router.push({ path: '/project/create', query: { parentId: String(parentId) } })
}

onMounted(loadRootProjects)
</script>

<template>
  <div class="project-tree-page">
    <PageHeader title="主子项目树" description="以左树右详情的方式浏览项目层级">
      <template #actions>
        <el-button :icon="Refresh" @click="refreshTree">刷新</el-button>
      </template>
    </PageHeader>

    <el-row :gutter="16" class="tree-row">
      <el-col :span="10" class="tree-col">
        <el-card shadow="never" class="tree-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><FolderOpened /></el-icon>
                项目树
              </span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索项目名"
                clearable
                size="small"
                :prefix-icon="Search"
                style="width: 180px"
              />
            </div>
          </template>

          <SkeletonCard v-if="loading" :loading="true" :rows="6" />

          <EmptyState
            v-else-if="treeData.length === 0"
            title="暂无项目"
            description="未获取到项目数据"
          />

          <el-tree
            v-else
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            :filter-node-method="filterNode"
            default-expand-all
            :expand-on-click-node="false"
            highlight-current
            class="project-tree"
            @node-click="handleNodeClick"
          >
            <template #default="{ data }">
              <div class="tree-node">
                <el-icon class="node-icon"><Folder /></el-icon>
                <span class="node-name" :title="data.projectName">{{ data.projectName }}</span>
                <ProjectStatusTag
                  v-if="data.status"
                  :status="data.status"
                  size="small"
                />
                <span v-if="data.children?.length" class="node-count">
                  {{ data.children.length }}
                </span>
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <el-col :span="14" class="tree-col">
        <el-card shadow="never" class="detail-card" v-loading="treeLoading">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Folder /></el-icon>
                {{ selectedTree?.projectName ?? '项目详情' }}
              </span>
              <div v-if="selectedProject" class="header-actions">
                <el-button size="small" :icon="Edit" @click="goEdit(selectedProject.id)">
                  编辑
                </el-button>
                <el-button
                  v-if="selectedTree"
                  size="small"
                  type="primary"
                  :icon="Plus"
                  @click="goCreateSub(selectedTree.id)"
                >
                  新增子项目
                </el-button>
                <el-button
                  size="small"
                  type="success"
                  @click="goWorkspace(selectedProject.id)"
                >
                  进入工作区
                </el-button>
              </div>
            </div>
          </template>

          <EmptyState
            v-if="!selectedProject"
            title="未选择项目"
            description="请在左侧树中选择一个项目节点"
          />

          <div v-else class="detail-body">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="项目名称">
                {{ selectedProject.name }}
              </el-descriptions-item>
              <el-descriptions-item label="项目编码">
                {{ selectedProject.code || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <ProjectStatusTag
                  v-if="selectedProject.status"
                  :status="selectedProject.status"
                  size="small"
                />
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="客户">
                {{ selectedProject.customerName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="项目经理">
                {{ selectedProject.managerName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="计划起止">
                {{ selectedProject.planStartDate?.substring(0, 10) ?? '-' }} ~
                {{ selectedProject.planEndDate?.substring(0, 10) ?? '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="自身进度">
                <el-progress
                  :percentage="Number(selectedProgress?.ownProgress ?? 0)"
                  :stroke-width="10"
                />
              </el-descriptions-item>
              <el-descriptions-item label="加权进度">
                <el-progress
                  :percentage="Number(selectedProgress?.aggregatedProgress ?? 0)"
                  :stroke-width="10"
                  status="success"
                />
              </el-descriptions-item>
              <el-descriptions-item label="项目描述" :span="2">
                {{ selectedProject.description || '-' }}
              </el-descriptions-item>
            </el-descriptions>

            <div class="sub-section">
              <div class="sub-header">
                <span class="sub-title">子项目列表 ({{ selectedChildren.length }})</span>
              </div>
              <el-empty
                v-if="selectedChildren.length === 0"
                :image-size="60"
                description="暂无子项目"
              />
              <el-table v-else :data="selectedChildren" border stripe size="small">
                <el-table-column prop="projectName" label="子项目名称" min-width="200" />
                <el-table-column prop="projectCode" label="编码" width="140" />
                <el-table-column label="状态" width="120">
                  <template #default="{ row }">
                    <ProjectStatusTag
                      v-if="row.status"
                      :status="row.status"
                      size="small"
                    />
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="进度" width="160">
                  <template #default="{ row }">
                    <el-progress
                      :percentage="Number(row.progress ?? 0)"
                      :stroke-width="6"
                      :show-text="false"
                    />
                    <span class="progress-text">{{ row.progress ?? 0 }}%</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                  <template #default="{ row }">
                    <el-button
                      link
                      type="primary"
                      size="small"
                      @click="goWorkspace(row.id)"
                    >
                      工作区
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.project-tree-page {
  padding: 16px 24px;
}

.tree-row {
  margin: 0 !important;
}
.tree-col {
  margin-bottom: 16px;
}

.tree-card,
.detail-card {
  border-radius: var(--pms-radius-lg);
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
}
.header-actions {
  display: flex;
  gap: 8px;
}

.project-tree {
  flex: 1;
  overflow-y: auto;
  background: transparent;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding: 2px 4px;
}
.node-icon {
  color: var(--pms-color-primary);
}
.node-name {
  font-size: 13px;
  color: var(--pms-color-text-primary);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.node-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  border-radius: var(--pms-radius-full);
  background: var(--pms-color-bg-page);
  color: var(--pms-color-text-secondary);
  font-size: 11px;
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  max-height: calc(100vh - 280px);
}

.sub-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sub-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.sub-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
}

.progress-text {
  font-size: 11px;
  color: var(--pms-color-text-secondary);
  margin-left: 4px;
}
</style>
