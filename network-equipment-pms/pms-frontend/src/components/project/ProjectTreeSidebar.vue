<script setup lang="ts">
// =============================================================================
// ProjectTreeSidebar - 常驻左侧主子项目树导航
// -----------------------------------------------------------------------------
// 在项目管理相关路由（route.meta.showProjectSidebar === true 或 path 以 /project 开头）
// 时，由 DefaultLayout 渲染在主内容区左侧。
// - 顶部：搜索框 + "新建项目" 按钮
// - 中部：el-tree 递归展示主子项目树（含 ProjectStatusTag 状态标签）
// - 底部：全部折叠 / 全部展开 控制
// 点击项目节点：调用 useProjectContext().setProject 并跳转到 /project/workspace/:id
// =============================================================================
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElTree, ElMessage } from 'element-plus'
import { Search, Plus, Expand, Fold, Refresh } from '@element-plus/icons-vue'
import { getProjectTree, type ProjectTreeNode } from '@/api/project'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'
import { useProjectContext } from '@/composables/useProjectContext'

defineOptions({ name: 'ProjectTreeSidebar' })

const router = useRouter()
const { setProject, projectList } = useProjectContext()

const searchKeyword = ref('')
const treeRef = ref<InstanceType<typeof ElTree> | null>(null)
const treeData = ref<ProjectTreeNode[]>([])
const loading = ref(false)
const loadError = ref(false)

const treeProps = { label: 'projectName', children: 'children' }

/** 搜索关键字变化时触发 el-tree 过滤 */
watch(searchKeyword, (val) => {
  treeRef.value?.filter(val)
})

/** el-tree filter-node-method：匹配项目名 / 项目编码 */
const filterNode = (value: string, data: ProjectTreeNode) => {
  if (!value) return true
  return (
    data.projectName?.toLowerCase().includes(value.toLowerCase()) ||
    data.projectCode?.toLowerCase().includes(value.toLowerCase())
  )
}

/** 加载主子项目树根节点（id = 0 表示根） */
const loadTree = async () => {
  loading.value = true
  loadError.value = false
  try {
    const data = await getProjectTree(0)
    // API 返回单个 ProjectTreeNode；包成数组喂给 el-tree
    treeData.value = data ? [data] : []
    projectList.value = treeData.value
  } catch (e) {
    treeData.value = []
    projectList.value = []
    loadError.value = true
    // 不弹 ElMessage 避免与其他页面错误重复；侧栏内显示重试按钮
  } finally {
    loading.value = false
  }
}

/** 点击项目节点：setProject + 路由跳转工作区 */
const handleNodeClick = (data: ProjectTreeNode) => {
  setProject({
    id: data.id,
    projectCode: data.projectCode ?? '',
    projectName: data.projectName,
    status: data.status,
    currentPhaseId: data.currentPhaseId ?? null
  })
  router.push(`/project/workspace/${data.id}`)
}

/** "新建项目" 按钮：跳转项目列表（列表页内含新建入口） */
const handleCreate = () => {
  router.push('/project/list')
}

/** 全部展开 */
const expandAll = () => {
  const tree = treeRef.value as any
  if (!tree) return
  Object.values(tree.store?.nodesMap ?? {}).forEach((node: any) => {
    node.expanded = true
  })
}

/** 全部折叠 */
const collapseAll = () => {
  const tree = treeRef.value as any
  if (!tree) return
  Object.values(tree.store?.nodesMap ?? {}).forEach((node: any) => {
    node.expanded = false
  })
}

onMounted(loadTree)
</script>

<template>
  <aside class="project-tree-sidebar">
    <!-- 顶部：标题 + 搜索 + 新建 -->
    <div class="sidebar-header">
      <span class="sidebar-title">项目导航</span>
      <el-button
        type="primary"
        size="small"
        :icon="Plus"
        @click="handleCreate"
      >
        新建项目
      </el-button>
    </div>
    <div class="sidebar-search">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索项目..."
        :prefix-icon="Search"
        clearable
        size="small"
      />
    </div>

    <!-- 中部：项目树 -->
    <div v-loading="loading" class="sidebar-tree-wrap">
      <!-- 加载失败状态：显示错误提示 + 重试按钮 -->
      <div v-if="loadError && !loading" class="sidebar-error">
        <el-icon :size="32" color="#909399"><Refresh /></el-icon>
        <p class="error-text">加载项目树失败</p>
        <el-button
          type="primary"
          size="small"
          :icon="Refresh"
          @click="loadTree"
        >
          重试
        </el-button>
      </div>
      <!-- 空数据状态（加载成功但无项目） -->
      <div v-else-if="!loading && treeData.length === 0" class="sidebar-empty">
        <p class="empty-text">暂无项目</p>
        <el-button
          type="primary"
          size="small"
          :icon="Plus"
          @click="handleCreate"
        >
          新建项目
        </el-button>
      </div>
      <!-- 项目树 -->
      <el-tree
        v-else
        ref="treeRef"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        class="project-tree"
        @node-click="handleNodeClick"
      >
        <template #default="{ data }">
          <div class="tree-node">
            <span class="node-label" :title="data.projectName">
              {{ data.projectName }}
            </span>
            <ProjectStatusTag
              v-if="data.status"
              :status="data.status"
              size="small"
            />
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 底部：全部折叠 / 全部展开 -->
    <div class="sidebar-footer">
      <el-button text size="small" :icon="Expand" @click="expandAll">
        全部展开
      </el-button>
      <el-button text size="small" :icon="Fold" @click="collapseAll">
        全部折叠
      </el-button>
    </div>
  </aside>
</template>

<style lang="scss" scoped>
@use '@/styles/design-tokens.scss' as *;

.project-tree-sidebar {
  width: 260px;
  background: $color-bg-card;
  border-right: 1px solid $color-border-light;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  height: 100%;
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-3 $spacing-3 $spacing-2;
  border-bottom: 1px solid $color-border-light;
}

.sidebar-title {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: $color-text-regular;
}

.sidebar-search {
  padding: $spacing-2 $spacing-3;
  border-bottom: 1px solid $color-border-light;
}

.sidebar-tree-wrap {
  flex: 1;
  overflow: auto;
  padding: $spacing-2 $spacing-1;
}

.sidebar-error,
.sidebar-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-2;
  padding: $spacing-6 $spacing-3;
  text-align: center;
}

.sidebar-error .error-text,
.sidebar-empty .empty-text {
  font-size: $font-size-sm;
  color: $color-text-secondary;
  margin: 0;
}

.project-tree {
  background: transparent;

  :deep(.el-tree-node__content) {
    height: 32px;
    border-radius: $radius-base;
    padding-right: $spacing-2;

    &:hover {
      background-color: $color-primary-light-9;
    }
  }

  :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background-color: $color-primary-light-9;
  }
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  gap: $spacing-2;
  padding-right: $spacing-2;
  min-width: 0;
}

.node-label {
  font-size: $font-size-sm;
  color: $color-text-regular;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.sidebar-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-2 $spacing-3;
  border-top: 1px solid $color-border-light;
  background: $color-bg-page;
}
</style>
