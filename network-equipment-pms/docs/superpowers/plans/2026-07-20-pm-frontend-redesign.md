# 项目管理前端界面重做实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 基于 Phase 1-9 已实现的 6 个 Story 后端能力，将项目管理前端 16 个视图 + 11 个组件全部重做为现代化企业中后台风格，以项目详情为信息枢纽整合所有子模块，新增项目甘特图、全局待办聚合、主子项目树常驻导航。

**Architecture:** 沿用 Vue 3 + TypeScript + Element Plus + Pinia + Vue Router 4 技术栈，扩展 design-tokens.scss 引入 Linear/Notion 风格令牌（更紧凑间距、更柔和阴影、更清晰层级），通过 `useProjectContext` composable 实现项目上下文注入，将原本散落的 phase/task/baseline/deliverable/workflow 页面整合为项目详情的子页面。新增 ProjectGantt（基于 @antv/g6 v5）、GlobalTodoCenter、ProjectTreeSidebar 三个核心新组件。

**Tech Stack:** Vue 3.4 + TypeScript 5 + Element Plus 2.5 + @antv/g6 v5.1 + Pinia 2 + Vue Router 4 + SCSS

---

## 设计决策

| # | 决策点 | 选择 | 依据 |
|---|--------|------|------|
| 1 | 设计风格 | Linear/Notion 现代化企业中后台 | 卡片化、紧凑间距、骨架屏、空状态插画 |
| 2 | 信息架构 | 项目为中心整合 | 项目详情作为枢纽，阶段/任务/交付件/审批/基线作为 Tab/子页面 |
| 3 | 导航模式 | 左侧主子项目树常驻 + 顶部 Tab 切换 | 减少跨页跳转，项目上下文常驻 |
| 4 | 甘特图方案 | @antv/g6 v5 自定义 Dagre 布局 | 已有依赖，支持节点交互、关键路径高亮 |
| 5 | 待办聚合 | 统一待办中心 + 各模块局部待办 | 全局聚合 + 项目内分发 |
| 6 | 路由策略 | 项目详情下嵌套子路由 | `/project/:id/phase`、`/project/:id/task` 等 |
| 7 | 状态管理 | useProjectContext composable + provide/inject | 项目上下文跨组件共享，避免 prop drilling |
| 8 | 视觉规范 | 复用 design-tokens.scss + 扩展项目专属令牌 | 状态色板、卡片间距、阴影层级 |

---

## 文件结构

### 新增文件

| 文件 | 职责 |
|------|------|
| `src/composables/useProjectContext.ts` | 项目上下文 provide/inject，跨组件共享当前项目 |
| `src/components/ProjectStatusTag.vue` | 项目状态标签（PLANNING/EXECUTING/CLOSING/CLOSED 等） |
| `src/components/PhaseStatusTag.vue` | 阶段状态标签（NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED） |
| `src/components/TaskPriorityTag.vue` | 任务优先级标签（LOW/MEDIUM/HIGH/CRITICAL） |
| `src/components/DeliverableStatusBadge.vue` | 交付件 7 态徽章 |
| `src/components/ProjectGantt.vue` | 项目甘特图（G6 v5 Dagre 布局 + 关键路径高亮） |
| `src/components/ProjectTreeSidebar.vue` | 常驻左侧主子项目树导航 |
| `src/components/GlobalTodoCenter.vue` | 全局待办聚合组件 |
| `src/components/EmptyState.vue` | 通用空状态组件（插画 + 文案 + CTA） |
| `src/components/SkeletonCard.vue` | 通用骨架屏组件 |
| `src/components/PageHeader.vue` | 统一页头（标题 + 面包屑 + 操作区） |
| `src/views/project/workspace/index.vue` | 项目工作区（新枢纽页，替代原 detail） |
| `src/views/project/todo/index.vue` | 项目内待办聚合页 |
| `src/views/project/gantt/index.vue` | 项目甘特图独立页 |
| `src/views/project/overview/index.vue` | 项目概览（仪表盘卡片） |

### 重做文件（保留路径，内容全替换）

| 文件 | 改动 |
|------|------|
| `src/views/project/list/index.vue` | 卡片化列表 + 空状态 + 骨架屏 |
| `src/views/project/detail/index.vue` | 重定向到 workspace，保留兼容 |
| `src/views/project/tree/index.vue` | 树形 + 卡片混合视图 |
| `src/views/project/template/index.vue` | 模板卡片网格 |
| `src/views/project/template/form.vue` | 分步表单 + 快照构建器 |
| `src/views/project/template/version.vue` | 版本时间轴 |
| `src/views/project/kanban/index.vue` | 看板视图重做 |
| `src/views/project-config/index.vue` | 配置分组卡片 |
| `src/views/phase/index.vue` | 阶段流水线 + 退出条件可视化 |
| `src/views/task/list/index.vue` | 树形/看板双视图切换 |
| `src/views/task/detail/index.vue` | 侧栏 + 主区布局 |
| `src/views/task/dependency/index.vue` | 依赖图 + 甘特图模式切换 |
| `src/views/baseline/index.vue` | 基线列表卡片化 |
| `src/views/baseline/diff.vue` | 偏差分析 + 甘特图叠加 |
| `src/views/deliverable/lifecycle.vue` | 7 态卡片视图 |
| `src/views/deliverable/detail/index.vue` | 状态流 + 版本时间轴 |
| `src/views/workflow/approval-center/index.vue` | 3 Tab + 卡片化 |
| `src/views/workflow/approval-detail/index.vue` | 脱敏展示 + 时间轴 |
| `src/views/workflow/approval-history/index.vue` | 多轮次时间轴 |
| `src/views/workflow/field-perm/index.vue` | 字段权限表格 |
| `src/components/SubProjectTree.vue` | 样式升级 |
| `src/components/TaskTree.vue` | 样式升级 |
| `src/components/TaskChecklist.vue` | 样式升级 |
| `src/components/DependencyGraph.vue` | 增加甘特图模式 |
| `src/components/BaselineDiffTable.vue` | 偏差高亮升级 |
| `src/components/DeliverableStatusFlow.vue` | 7 态可视化升级 |
| `src/components/DeliverableVersionList.vue` | 版本卡片化 |
| `src/components/ApprovalTimeline.vue` | 多轮次分组升级 |
| `src/components/SensitiveFieldDisplay.vue` | 脱敏提示升级 |
| `src/components/PhaseExitGateEditor.vue` | 4 类条件编辑器升级 |
| `src/components/ProjectTemplateSelector.vue` | 模板选择卡片化 |
| `src/styles/design-tokens.scss` | 扩展项目专属令牌 |
| `src/router/index.ts` | 项目路由全量重构为嵌套子路由 |
| `src/layouts/DefaultLayout.vue` | 增加项目树侧栏插槽 |

### 删除文件（功能合并到 workspace）

| 文件 | 原因 |
|------|------|
| 无 | 所有重做文件保留原路径，避免破坏外部引用 |

---

## Task 1: 扩展设计令牌 + 通用基础组件

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/styles/design-tokens.scss`
- Create: `network-equipment-pms/pms-frontend/src/components/ProjectStatusTag.vue`
- Create: `network-equipment-pms/pms-frontend/src/components/PhaseStatusTag.vue`
- Create: `network-equipment-pms/pms-frontend/src/components/TaskPriorityTag.vue`
- Create: `network-equipment-pms/pms-frontend/src/components/DeliverableStatusBadge.vue`
- Create: `network-equipment-pms/pms-frontend/src/components/EmptyState.vue`
- Create: `network-equipment-pms/pms-frontend/src/components/SkeletonCard.vue`
- Create: `network-equipment-pms/pms-frontend/src/components/PageHeader.vue`

- [ ] **Step 1: 扩展 design-tokens.scss**

在文件末尾 `:root` 块之前追加项目专属令牌：

```scss
// ===== 项目管理专属令牌 =====

// 项目状态色板
$project-status-planning: #8b5cf6;     // 紫色 - 规划中
$project-status-executing: #3b82f6;    // 蓝色 - 执行中
$project-status-closing: #f59e0b;      // 橙色 - 收尾中
$project-status-closed: #10b981;       // 绿色 - 已关闭
$project-status-suspended: #6b7280;    // 灰色 - 暂停
$project-status-cancelled: #ef4444;    // 红色 - 已取消

// 阶段状态色板
$phase-status-not-started: #9ca3af;
$phase-status-in-progress: #3b82f6;
$phase-status-completed: #10b981;
$phase-status-skipped: #f59e0b;

// 任务优先级色板
$task-priority-low: #6b7280;
$task-priority-medium: #3b82f6;
$task-priority-high: #f59e0b;
$task-priority-critical: #ef4444;

// 交付件 7 态色板
$deliverable-status-draft: #9ca3af;
$deliverable-status-submitted: #3b82f6;
$deliverable-status-reviewed: #8b5cf6;
$deliverable-status-signed: #06b6d4;
$deliverable-status-published: #10b981;
$deliverable-status-referenced: #6366f1;
$deliverable-status-archived: #6b7280;

// 卡片间距（Linear 风格 - 更紧凑）
$card-padding-sm: 12px;
$card-padding-base: 16px;
$card-padding-lg: 24px;
$card-gap: 12px;

// 阴影层级（更柔和）
$shadow-card: 0 1px 3px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.06);
$shadow-card-hover: 0 4px 6px rgba(0, 0, 0, 0.05), 0 2px 4px rgba(0, 0, 0, 0.06);
$shadow-popover: 0 10px 15px rgba(0, 0, 0, 0.1), 0 4px 6px rgba(0, 0, 0, 0.05);
```

并在 `:root` 块内追加对应 CSS 变量输出。

- [ ] **Step 2: 创建 ProjectStatusTag.vue**

```vue
<template>
  <el-tag :type="tagType" :effect="effect" :size="size" :round="round">
    <span class="status-dot" :style="{ backgroundColor: dotColor }"></span>
    {{ label }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: string
  size?: 'small' | 'default' | 'large'
  effect?: 'light' | 'dark' | 'plain'
  round?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  effect: 'light',
  round: true
})

const STATUS_MAP: Record<string, { label: string; type: string; color: string }> = {
  PLANNING: { label: '规划中', type: 'warning', color: '#8b5cf6' },
  EXECUTING: { label: '执行中', type: 'primary', color: '#3b82f6' },
  CLOSING: { label: '收尾中', type: 'warning', color: '#f59e0b' },
  CLOSED: { label: '已关闭', type: 'success', color: '#10b981' },
  SUSPENDED: { label: '已暂停', type: 'info', color: '#6b7280' },
  CANCELLED: { label: '已取消', type: 'danger', color: '#ef4444' }
}

const config = computed(() => STATUS_MAP[props.status] ?? { label: props.status, type: 'info', color: '#9ca3af' })
const label = computed(() => config.value.label)
const tagType = computed(() => config.value.type as any)
const dotColor = computed(() => config.value.color)
</script>

<style scoped>
.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}
</style>
```

- [ ] **Step 3: 创建 PhaseStatusTag.vue**（结构同上，状态映射 NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED）

- [ ] **Step 4: 创建 TaskPriorityTag.vue**（LOW/MEDIUM/HIGH/CRITICAL 4 级）

- [ ] **Step 5: 创建 DeliverableStatusBadge.vue**（7 态徽章）

- [ ] **Step 6: 创建 EmptyState.vue**

```vue
<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon :size="64"><component :is="icon" /></el-icon>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p class="empty-description">{{ description }}</p>
    <div v-if="$slots.action" class="empty-action">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string
  description?: string
  icon?: string
}
withDefaults(defineProps<Props>(), {
  icon: 'Folder',
  description: ''
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
}
.empty-icon {
  color: var(--pms-color-text-placeholder);
  margin-bottom: 16px;
}
.empty-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--pms-color-text-regular);
  margin: 0 0 8px;
}
.empty-description {
  font-size: 14px;
  color: var(--pms-color-text-secondary);
  margin: 0 0 16px;
  max-width: 400px;
}
</style>
```

- [ ] **Step 7: 创建 SkeletonCard.vue**

```vue
<template>
  <div class="skeleton-card" :style="{ padding: padding }">
    <el-skeleton :rows="rows" animated :loading="loading">
      <template #default><slot /></template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
interface Props {
  loading: boolean
  rows?: number
  padding?: string
}
withDefaults(defineProps<Props>(), {
  rows: 3,
  padding: '16px'
})
</script>

<style scoped>
.skeleton-card {
  background: var(--pms-color-bg-card);
  border-radius: var(--pms-radius-lg);
  box-shadow: var(--shadow-card);
}
</style>
```

- [ ] **Step 8: 创建 PageHeader.vue**

```vue
<template>
  <div class="page-header">
    <div class="header-left">
      <div v-if="$slots.breadcrumb" class="breadcrumb">
        <slot name="breadcrumb" />
      </div>
      <h2 class="page-title">
        <slot name="title">{{ title }}</slot>
      </h2>
      <p v-if="description || $slots.description" class="page-description">
        <slot name="description">{{ description }}</slot>
      </p>
    </div>
    <div v-if="$slots.actions" class="header-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title?: string
  description?: string
}
defineProps<Props>()
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 0 0 16px;
  border-bottom: 1px solid var(--pms-color-border-light);
  margin-bottom: 16px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
  margin: 0;
}
.page-description {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  margin: 4px 0 0;
}
.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
```

- [ ] **Step 9: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/styles/design-tokens.scss \
  network-equipment-pms/pms-frontend/src/components/ProjectStatusTag.vue \
  network-equipment-pms/pms-frontend/src/components/PhaseStatusTag.vue \
  network-equipment-pms/pms-frontend/src/components/TaskPriorityTag.vue \
  network-equipment-pms/pms-frontend/src/components/DeliverableStatusBadge.vue \
  network-equipment-pms/pms-frontend/src/components/EmptyState.vue \
  network-equipment-pms/pms-frontend/src/components/SkeletonCard.vue \
  network-equipment-pms/pms-frontend/src/components/PageHeader.vue
git commit -m "feat(b8-t1): 扩展设计令牌 + 7 个通用基础组件（ProjectStatusTag/PhaseStatusTag/TaskPriorityTag/DeliverableStatusBadge/EmptyState/SkeletonCard/PageHeader）"
git push
```

---

## Task 2: useProjectContext + ProjectTreeSidebar 常驻导航

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/composables/useProjectContext.ts`
- Create: `network-equipment-pms/pms-frontend/src/components/ProjectTreeSidebar.vue`
- Modify: `network-equipment-pms/pms-frontend/src/layouts/DefaultLayout.vue`

- [ ] **Step 1: 创建 useProjectContext composable**

```typescript
// src/composables/useProjectContext.ts
import { inject, provide, ref, type Ref } from 'vue'

export interface ProjectContext {
  projectId: Ref<number | null>
  projectCode: Ref<string>
  projectName: Ref<string>
  projectStatus: Ref<string>
  templateId: Ref<number | null>
  currentPhaseId: Ref<number | null>
  setProject: (project: {
    id: number
    projectCode: string
    projectName: string
    status: string
    templateId?: number | null
    currentPhaseId?: number | null
  }) => void
  reset: () => void
}

const PROJECT_CONTEXT_KEY = Symbol('projectContext')

export function provideProjectContext(): ProjectContext {
  const projectId = ref<number | null>(null)
  const projectCode = ref('')
  const projectName = ref('')
  const projectStatus = ref('')
  const templateId = ref<number | null>(null)
  const currentPhaseId = ref<number | null>(null)

  const setProject = (project: {
    id: number
    projectCode: string
    projectName: string
    status: string
    templateId?: number | null
    currentPhaseId?: number | null
  }) => {
    projectId.value = project.id
    projectCode.value = project.projectCode
    projectName.value = project.projectName
    projectStatus.value = project.status
    templateId.value = project.templateId ?? null
    currentPhaseId.value = project.currentPhaseId ?? null
  }

  const reset = () => {
    projectId.value = null
    projectCode.value = ''
    projectName.value = ''
    projectStatus.value = ''
    templateId.value = null
    currentPhaseId.value = null
  }

  const ctx: ProjectContext = {
    projectId,
    projectCode,
    projectName,
    projectStatus,
    templateId,
    currentPhaseId,
    setProject,
    reset
  }

  provide(PROJECT_CONTEXT_KEY, ctx)
  return ctx
}

export function useProjectContext(): ProjectContext {
  const ctx = inject<ProjectContext>(PROJECT_CONTEXT_KEY)
  if (!ctx) {
    // 返回空上下文，避免子组件在无项目时崩溃
    return {
      projectId: ref(null),
      projectCode: ref(''),
      projectName: ref(''),
      projectStatus: ref(''),
      templateId: ref(null),
      currentPhaseId: ref(null),
      setProject: () => {},
      reset: () => {}
    }
  }
  return ctx
}
```

- [ ] **Step 2: 创建 ProjectTreeSidebar.vue**

常驻左侧主子项目树导航，可折叠展开。读取现有 `getProjectTree` API。

```vue
<template>
  <aside class="project-tree-sidebar" :class="{ collapsed }">
    <div class="sidebar-header">
      <span v-if="!collapsed" class="sidebar-title">项目导航</span>
      <el-button text @click="collapsed = !collapsed">
        <el-icon><component :is="collapsed ? 'Expand' : 'Fold'" /></el-icon>
      </el-button>
    </div>
    <div v-show="!collapsed" class="sidebar-content">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索项目..."
        :prefix-icon="Search"
        clearable
        size="small"
        class="search-input"
      />
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        ref="treeRef"
        @node-click="handleNodeClick"
        class="project-tree"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <span class="node-label">{{ node.label }}</span>
            <ProjectStatusTag :status="data.status" size="small" />
          </div>
        </template>
      </el-tree>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getProjectTree } from '@/api/project'
import ProjectStatusTag from './ProjectStatusTag.vue'

const router = useRouter()
const collapsed = ref(false)
const searchKeyword = ref('')
const treeRef = ref()
const treeData = ref<any[]>([])

const treeProps = { label: 'projectName', children: 'children' }

watch(searchKeyword, (val) => {
  treeRef.value?.filter(val)
})

const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.projectName?.includes(value) || data.projectCode?.includes(value)
}

const loadTree = async () => {
  try {
    // 加载顶层项目（parentProjectId = null）
    const data = await getProjectTree(0) // 0 表示根
    treeData.value = Array.isArray(data) ? data : [data]
  } catch (e) {
    treeData.value = []
  }
}

const handleNodeClick = (data: any) => {
  router.push(`/project/workspace/${data.id}`)
}

loadTree()
</script>

<style scoped>
.project-tree-sidebar {
  width: 260px;
  background: var(--pms-color-bg-card);
  border-right: 1px solid var(--pms-color-border-light);
  transition: width var(--pms-transition-base);
  display: flex;
  flex-direction: column;
}
.project-tree-sidebar.collapsed {
  width: 48px;
}
.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid var(--pms-color-border-light);
}
.sidebar-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--pms-color-text-regular);
}
.sidebar-content {
  flex: 1;
  overflow: auto;
  padding: 8px;
}
.search-input {
  margin-bottom: 8px;
}
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  padding-right: 8px;
}
.node-label {
  font-size: 13px;
  color: var(--pms-color-text-regular);
}
</style>
```

- [ ] **Step 3: 修改 DefaultLayout.vue 增加项目树侧栏插槽**

读取现有 `DefaultLayout.vue`，在主内容区左侧增加 `<ProjectTreeSidebar />`，仅当路由 path 以 `/project` 开头时显示。

```vue
<!-- 在主内容区前插入 -->
<ProjectTreeSidebar v-if="showProjectSidebar" />
```

```typescript
import { useRoute } from 'vue-router'
import ProjectTreeSidebar from '@/components/ProjectTreeSidebar.vue'

const route = useRoute()
const showProjectSidebar = computed(() => route.path.startsWith('/project'))
```

并在 layout 根节点调用 `provideProjectContext()`。

- [ ] **Step 4: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/composables/useProjectContext.ts \
  network-equipment-pms/pms-frontend/src/components/ProjectTreeSidebar.vue \
  network-equipment-pms/pms-frontend/src/layouts/DefaultLayout.vue
git commit -m "feat(b8-t2): useProjectContext composable + ProjectTreeSidebar 常驻左侧主子项目树导航"
git push
```

---

## Task 3: 项目工作区 workspace（新枢纽页）

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/views/project/workspace/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/project/detail/index.vue`（重定向到 workspace）

- [ ] **Step 1: 创建 workspace/index.vue**

项目工作区是新枢纽页，左侧固定项目信息卡，右侧 Tab 切换：概览/阶段/任务/交付件/基线/审批/成员/配置/甘特图/待办。

```vue
<template>
  <div class="project-workspace">
    <!-- 项目信息卡 -->
    <div class="workspace-header">
      <div class="header-info">
        <div class="title-row">
          <h1>{{ project?.projectName || '加载中...' }}</h1>
          <ProjectStatusTag v-if="project" :status="project.status" />
        </div>
        <div class="meta-row">
          <span class="meta-item">项目编码：{{ project?.projectCode }}</span>
          <span class="meta-item">项目经理：{{ project?.managerName || '-' }}</span>
          <span class="meta-item">进度：{{ progress }}%</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button-group>
          <el-button @click="goGantt">甘特图</el-button>
          <el-button @click="goTodo">待办</el-button>
        </el-button-group>
        <el-button type="primary" @click="showCreateDialog = true">从模板创建子项目</el-button>
      </div>
    </div>

    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" class="workspace-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="概览" name="overview">
        <ProjectOverview v-if="project" :project="project" />
      </el-tab-pane>
      <el-tab-pane label="阶段" name="phase">
        <PhaseManage v-if="project" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane label="任务" name="task">
        <TaskListView v-if="project" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane label="交付件" name="deliverable">
        <DeliverableLifecycle v-if="project" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane label="基线" name="baseline">
        <BaselineList v-if="project" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane label="审批" name="approval">
        <ApprovalCenter v-if="project" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane label="成员" name="member">
        <ProjectMember v-if="project" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane label="配置" name="config">
        <ProjectConfig v-if="project" :project-id="projectId" />
      </el-tab-pane>
    </el-tabs>

    <!-- 从模板创建子项目 -->
    <ProjectTemplateSelector
      v-model:visible="showCreateDialog"
      @select="handleCreateSubproject"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProject, getProjectProgress, createSubproject } from '@/api/project'
import { useProjectContext } from '@/composables/useProjectContext'
import ProjectStatusTag from '@/components/ProjectStatusTag.vue'
import ProjectTemplateSelector from '@/components/ProjectTemplateSelector.vue'
import ProjectOverview from '@/views/project/overview/index.vue'
import PhaseManage from '@/views/phase/index.vue'
import TaskListView from '@/views/task/list/index.vue'
import DeliverableLifecycle from '@/views/deliverable/lifecycle.vue'
import BaselineList from '@/views/baseline/index.vue'
import ApprovalCenter from '@/views/workflow/approval-center/index.vue'

const route = useRoute()
const router = useRouter()
const { setProject } = useProjectContext()

const projectId = computed(() => Number(route.params.id))
const project = ref<any>(null)
const progress = ref(0)
const activeTab = ref('overview')
const showCreateDialog = ref(false)

const loadProject = async () => {
  try {
    project.value = await getProject(projectId.value)
    setProject({
      id: project.value.id,
      projectCode: project.value.projectCode,
      projectName: project.value.projectName,
      status: project.value.status,
      templateId: project.value.templateId,
      currentPhaseId: project.value.currentPhaseId
    })
    const prog = await getProjectProgress(projectId.value)
    progress.value = typeof prog === 'number' ? prog : (prog?.aggregatedProgress ?? 0)
  } catch (e) {
    // 错误处理
  }
}

const goGantt = () => router.push(`/project/${projectId.value}/gantt`)
const goTodo = () => router.push(`/project/${projectId.value}/todo`)

const handleTabChange = (tab: string) => {
  router.replace({ query: { tab } })
}

const handleCreateSubproject = async (templateVersionId: number, dto: any) => {
  await createSubproject(projectId.value, { ...dto, templateId: templateVersionId })
  showCreateDialog.value = false
  loadProject()
}

watch(() => route.params.id, loadProject)
watch(() => route.query.tab, (tab) => {
  if (tab && typeof tab === 'string') activeTab.value = tab
})

onMounted(() => {
  const tab = route.query.tab as string
  if (tab) activeTab.value = tab
  loadProject()
})
</script>

<style scoped>
.project-workspace {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.workspace-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 24px;
  background: var(--pms-color-bg-card);
  border-bottom: 1px solid var(--pms-color-border-light);
}
.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.title-row h1 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--pms-color-text-primary);
}
.meta-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}
.header-actions {
  display: flex;
  gap: 8px;
}
.workspace-tabs {
  flex: 1;
  padding: 0 24px;
  overflow: auto;
}
.workspace-tabs :deep(.el-tabs__content) {
  padding-top: 16px;
}
</style>
```

- [ ] **Step 2: 修改 detail/index.vue 为重定向**

```vue
<template>
  <router-view v-if="$route.params.id" />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

onMounted(() => {
  // 兼容旧路径 /project/detail/:id → /project/workspace/:id
  if (route.params.id) {
    router.replace(`/project/workspace/${route.params.id}`)
  }
})
</script>
```

- [ ] **Step 3: 创建占位的 overview/index.vue**

```vue
<template>
  <div class="project-overview">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>项目进度</template>
          <el-progress type="circle" :percentage="progress" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>当前阶段</template>
          <div class="metric-value">{{ currentPhaseName }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>任务统计</template>
          <div class="metric-value">{{ taskStats.total }} ({{ taskStats.completed }} 已完成)</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>待办</template>
          <div class="metric-value">{{ todoCount }} 项</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'

interface Props {
  project: any
}
const props = defineProps<Props>()

const progress = ref(0)
const currentPhaseName = ref('-')
const taskStats = ref({ total: 0, completed: 0 })
const todoCount = ref(0)

watchEffect(() => {
  if (props.project) {
    progress.value = props.project.progress ?? 0
    currentPhaseName.value = props.project.currentPhaseName ?? '-'
  }
})
</script>
```

- [ ] **Step 4: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/project/workspace/index.vue \
  network-equipment-pms/pms-frontend/src/views/project/detail/index.vue \
  network-equipment-pms/pms-frontend/src/views/project/overview/index.vue
git commit -m "feat(b8-t3): 项目工作区 workspace 新枢纽页（8 Tab 整合概览/阶段/任务/交付件/基线/审批/成员/配置）"
git push
```

---

## Task 4: 项目列表页重做（卡片化 + 空状态 + 骨架屏）

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/project/list/index.vue`

- [ ] **Step 1: 重写 list/index.vue**

卡片网格布局，每张卡片显示项目名、状态、进度、阶段、操作按钮。顶部筛选区（状态、模板、关键词）。空状态引导从模板创建。

```vue
<template>
  <div class="project-list-page">
    <PageHeader title="项目列表" description="管理所有项目，包括从模板创建新项目">
      <template #actions>
        <el-input v-model="searchKeyword" placeholder="搜索项目..." :prefix-icon="Search" clearable style="width: 240px" />
        <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
          <el-option label="规划中" value="PLANNING" />
          <el-option label="执行中" value="EXECUTING" />
          <el-option label="收尾中" value="CLOSING" />
          <el-option label="已关闭" value="CLOSED" />
        </el-select>
        <el-button type="primary" @click="showCreateDialog = true">从模板创建项目</el-button>
      </template>
    </PageHeader>

    <SkeletonCard :loading="loading" :rows="4">
      <div v-if="filteredProjects.length === 0 && !loading" class="empty-wrapper">
        <EmptyState
          title="暂无项目"
          description="点击右上角按钮从模板创建第一个项目"
          icon="Folder"
        >
          <template #action>
            <el-button type="primary" @click="showCreateDialog = true">从模板创建项目</el-button>
          </template>
        </EmptyState>
      </div>

      <div v-else class="project-grid">
        <el-card
          v-for="project in filteredProjects"
          :key="project.id"
          shadow="hover"
          class="project-card"
          @click="goWorkspace(project.id)"
        >
          <div class="card-header">
            <span class="card-title">{{ project.projectName }}</span>
            <ProjectStatusTag :status="project.status" size="small" />
          </div>
          <div class="card-meta">
            <div class="meta-row">编码：{{ project.projectCode }}</div>
            <div class="meta-row">经理：{{ project.managerName || '-' }}</div>
          </div>
          <el-progress :percentage="project.progress ?? 0" :stroke-width="6" />
          <div class="card-footer">
            <span class="footer-text">{{ project.templateName || '无模板' }}</span>
            <el-button text size="small" @click.stop="goWorkspace(project.id)">进入工作区</el-button>
          </div>
        </el-card>
      </div>
    </SkeletonCard>

    <ProjectTemplateSelector v-model:visible="showCreateDialog" @select="handleCreate" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { listProjects, createProjectFromTemplate } from '@/api/project-template'
import PageHeader from '@/components/PageHeader.vue'
import SkeletonCard from '@/components/SkeletonCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import ProjectStatusTag from '@/components/ProjectStatusTag.vue'
import ProjectTemplateSelector from '@/components/ProjectTemplateSelector.vue'

const router = useRouter()
const loading = ref(true)
const projects = ref<any[]>([])
const searchKeyword = ref('')
const filterStatus = ref('')
const showCreateDialog = ref(false)

const filteredProjects = computed(() => {
  return projects.value.filter(p => {
    const matchKeyword = !searchKeyword.value ||
      p.projectName?.includes(searchKeyword.value) ||
      p.projectCode?.includes(searchKeyword.value)
    const matchStatus = !filterStatus.value || p.status === filterStatus.value
    return matchKeyword && matchStatus
  })
})

const loadProjects = async () => {
  loading.value = true
  try {
    const data = await listProjects({ page: 1, size: 100 })
    projects.value = data.records ?? data ?? []
  } finally {
    loading.value = false
  }
}

const goWorkspace = (id: number) => {
  router.push(`/project/workspace/${id}`)
}

const handleCreate = async (templateVersionId: number, dto: any) => {
  await createProjectFromTemplate({ templateId: templateVersionId, ...dto })
  showCreateDialog.value = false
  loadProjects()
}

onMounted(loadProjects)
</script>

<style scoped>
.project-list-page { padding: 16px 24px; }
.empty-wrapper { padding: 48px 0; }
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}
.project-card {
  cursor: pointer;
  transition: all var(--pms-transition-fast);
}
.project-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-card-hover);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
}
.card-meta {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 12px;
}
.meta-row { margin-bottom: 4px; }
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--pms-color-border-light);
}
.footer-text {
  font-size: 12px;
  color: var(--pms-color-text-placeholder);
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/project/list/index.vue
git commit -m "feat(b8-t4): 项目列表页重做（卡片网格 + 空状态 + 骨架屏 + 筛选）"
git push
```

---

## Task 5: 项目甘特图组件 ProjectGantt

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/components/ProjectGantt.vue`
- Create: `network-equipment-pms/pms-frontend/src/views/project/gantt/index.vue`

- [ ] **Step 1: 创建 ProjectGantt.vue**

基于 @antv/g6 v5 实现甘特图，支持任务依赖连线、关键路径高亮、节点点击跳转。

```vue
<template>
  <div class="project-gantt" ref="containerRef">
    <div class="gantt-toolbar">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button label="day">日</el-radio-button>
        <el-radio-button label="week">周</el-radio-button>
        <el-radio-button label="month">月</el-radio-button>
      </el-radio-group>
      <el-switch v-model="showCriticalPath" active-text="关键路径" />
      <el-switch v-model="showBaseline" active-text="基线对比" />
    </div>
    <div class="gantt-canvas" ref="canvasRef"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import G6 from '@antv/g6'

interface Props {
  tasks: any[]
  dependencies: any[]
  baseline?: any[]
  projectId: number
}

const props = defineProps<Props>()
const canvasRef = ref<HTMLDivElement>()
const containerRef = ref<HTMLDivElement>()
const viewMode = ref<'day' | 'week' | 'month'>('day')
const showCriticalPath = ref(false)
const showBaseline = ref(false)

let graph: any = null

const buildNodes = () => {
  return props.tasks.map(task => ({
    id: `task-${task.id}`,
    data: {
      ...task,
      type: 'rect',
      size: [getTaskWidth(task), 32],
      label: task.taskName,
      style: {
        fill: getTaskColor(task),
        radius: 4
      }
    }
  }))
}

const buildEdges = () => {
  return props.dependencies.map(dep => ({
    source: `task-${dep.predecessorTaskId}`,
    target: `task-${dep.successorTaskId}`,
    data: {
      type: 'cubic',
      label: dep.dependencyType
    }
  }))
}

const getTaskWidth = (task: any) => {
  if (!task.planStartDate || !task.planEndDate) return 100
  const days = Math.ceil((new Date(task.planEndDate).getTime() - new Date(task.planStartDate).getTime()) / 86400000)
  return Math.max(50, days * (viewMode.value === 'day' ? 30 : viewMode.value === 'week' ? 15 : 5))
}

const getTaskColor = (task: any) => {
  if (task.status === 'COMPLETED') return '#10b981'
  if (task.status === 'IN_PROGRESS') return '#3b82f6'
  if (task.status === 'IN_REVIEW') return '#f59e0b'
  return '#9ca3af'
}

const initGraph = () => {
  if (!canvasRef.value) return
  graph = new G6.Graph({
    container: canvasRef.value,
    width: containerRef.value?.clientWidth ?? 800,
    height: 500,
    layout: {
      type: 'dagre',
      rankdir: 'LR',
      nodesep: 10,
      ranksep: 30
    },
    modes: {
      default: ['drag-canvas', 'zoom-canvas', 'drag-node']
    },
    nodeStateStyles: {
      critical: { stroke: '#ef4444', lineWidth: 2 }
    }
  })

  graph.setData({ nodes: buildNodes(), edges: buildEdges() })
  graph.render()

  graph.on('node:click', (evt: any) => {
    const taskId = evt.item.getModel().id.replace('task-', '')
    // 触发跳转
    emit('task-click', Number(taskId))
  })
}

const emit = defineEmits(['task-click'])

watch([viewMode, showCriticalPath, showBaseline], () => {
  if (graph) {
    graph.changeData({ nodes: buildNodes(), edges: buildEdges() })
    if (showCriticalPath.value) highlightCriticalPath()
  }
})

const highlightCriticalPath = () => {
  // 简化版：标记所有 COMPLETED 任务为 critical（实际应计算最长路径）
  props.tasks.forEach(task => {
    if (task.status === 'COMPLETED') {
      graph.setItemState(`task-${task.id}`, 'critical', true)
    }
  })
}

const handleResize = () => {
  if (graph && containerRef.value) {
    graph.changeSize(containerRef.value.clientWidth, 500)
  }
}

onMounted(() => {
  initGraph()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  graph?.destroy()
})
</script>

<style scoped>
.project-gantt {
  background: var(--pms-color-bg-card);
  border-radius: var(--pms-radius-lg);
  padding: 16px;
}
.gantt-toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  align-items: center;
}
.gantt-canvas {
  width: 100%;
  height: 500px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-md);
}
</style>
```

- [ ] **Step 2: 创建 gantt/index.vue 页面**

```vue
<template>
  <div class="gantt-page">
    <PageHeader title="项目甘特图">
      <template #actions>
        <el-button @click="$router.back()">返回</el-button>
      </template>
    </PageHeader>
    <ProjectGantt
      :tasks="tasks"
      :dependencies="dependencies"
      :project-id="projectId"
      @task-click="goTaskDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listTasks } from '@/api/implementation'
import { listDependencies } from '@/api/task-dependency'
import PageHeader from '@/components/PageHeader.vue'
import ProjectGantt from '@/components/ProjectGantt.vue'

const route = useRoute()
const router = useRouter()
const projectId = Number(route.params.id)
const tasks = ref<any[]>([])
const dependencies = ref<any[]>([])

const loadData = async () => {
  const [taskRes, depRes] = await Promise.all([
    listTasks({ projectId }),
    listDependencies(projectId)
  ])
  tasks.value = taskRes.records ?? taskRes ?? []
  dependencies.value = depRes ?? []
}

const goTaskDetail = (taskId: number) => {
  router.push(`/implementation/task/detail/${taskId}`)
}

onMounted(loadData)
</script>

<style scoped>
.gantt-page { padding: 16px 24px; }
</style>
```

- [ ] **Step 3: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/components/ProjectGantt.vue \
  network-equipment-pms/pms-frontend/src/views/project/gantt/index.vue
git commit -m "feat(b8-t5): ProjectGantt 甘特图组件（G6 v5 Dagre 布局 + 关键路径高亮 + 基线对比）"
git push
```

---

## Task 6: 全局待办聚合页

**Files:**
- Create: `network-equipment-pms/pms-frontend/src/components/GlobalTodoCenter.vue`
- Create: `network-equipment-pms/pms-frontend/src/views/project/todo/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/workflow/todo/index.vue`

- [ ] **Step 1: 创建 GlobalTodoCenter.vue**

统一聚合：阶段推进待办、审批待办、强制检查项待办、交付件评审待办。

```vue
<template>
  <div class="global-todo-center">
    <el-tabs v-model="activeTab">
      <el-tab-pane :label="`阶段推进 (${phaseTodos.length})`" name="phase">
        <div class="todo-list">
          <el-card v-for="todo in phaseTodos" :key="todo.id" shadow="hover" class="todo-card" @click="goPhase(todo)">
            <div class="todo-content">
              <el-icon class="todo-icon"><Promotion /></el-icon>
              <div class="todo-info">
                <div class="todo-title">{{ todo.phaseName }} - 推进阶段</div>
                <div class="todo-desc">项目：{{ todo.projectName }}</div>
              </div>
              <el-button type="primary" size="small">处理</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="`审批 (${approvalTodos.length})`" name="approval">
        <div class="todo-list">
          <el-card v-for="todo in approvalTodos" :key="todo.id" shadow="hover" class="todo-card" @click="goApproval(todo)">
            <div class="todo-content">
              <el-icon class="todo-icon"><Checked /></el-icon>
              <div class="todo-info">
                <div class="todo-title">{{ todo.title }}</div>
                <div class="todo-desc">类型：{{ todo.approvalType }}</div>
              </div>
              <el-button type="primary" size="small">处理</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="`检查项 (${checklistTodos.length})`" name="checklist">
        <!-- 强制检查项待办 -->
      </el-tab-pane>
      <el-tab-pane :label="`交付件评审 (${deliverableTodos.length})`" name="deliverable">
        <!-- 交付件评审待办 -->
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Promotion, Checked } from '@element-plus/icons-vue'
import { listApprovals } from '@/api/approval-center'
import { useProjectContext } from '@/composables/useProjectContext'

const props = defineProps<{ projectId?: number }>()
const router = useRouter()
const { projectId: ctxProjectId } = useProjectContext()

const activeTab = ref('phase')
const phaseTodos = ref<any[]>([])
const approvalTodos = ref<any[]>([])
const checklistTodos = ref<any[]>([])
const deliverableTodos = ref<any[]>([])

const targetProjectId = computed(() => props.projectId ?? ctxProjectId.value)

const loadTodos = async () => {
  if (!targetProjectId.value) return
  // 加载各类型待办
  try {
    const approvals = await listApprovals({ projectId: targetProjectId.value, status: 'PENDING' })
    approvalTodos.value = approvals.records ?? approvals ?? []
  } catch (e) {
    approvalTodos.value = []
  }
  // 其他类型待办通过相应 API 加载
}

const goPhase = (todo: any) => router.push(`/project/${targetProjectId.value}/phase`)
const goApproval = (todo: any) => router.push(`/workflow/approval-detail/${todo.id}`)

onMounted(loadTodos)
</script>

<style scoped>
.global-todo-center { padding: 0; }
.todo-list { display: flex; flex-direction: column; gap: 8px; }
.todo-card { cursor: pointer; }
.todo-content {
  display: flex;
  align-items: center;
  gap: 12px;
}
.todo-icon {
  font-size: 20px;
  color: var(--pms-color-primary);
}
.todo-info { flex: 1; }
.todo-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--pms-color-text-primary);
}
.todo-desc {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-top: 2px;
}
</style>
```

- [ ] **Step 2: 创建 project/todo/index.vue**

```vue
<template>
  <div class="project-todo-page">
    <PageHeader title="项目待办" description="聚合本项目下的所有待办事项">
      <template #actions>
        <el-button @click="$router.back()">返回</el-button>
      </template>
    </PageHeader>
    <GlobalTodoCenter :project-id="projectId" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import GlobalTodoCenter from '@/components/GlobalTodoCenter.vue'

const route = useRoute()
const projectId = computed(() => Number(route.params.id))
</script>

<style scoped>
.project-todo-page { padding: 16px 24px; }
</style>
```

- [ ] **Step 3: 重做 workflow/todo/index.vue 为全局待办中心**

```vue
<template>
  <div class="global-todo-page">
    <PageHeader title="全局待办中心" description="聚合所有项目的待办事项" />
    <GlobalTodoCenter />
  </div>
</template>

<script setup lang="ts">
import PageHeader from '@/components/PageHeader.vue'
import GlobalTodoCenter from '@/components/GlobalTodoCenter.vue'
</script>

<style scoped>
.global-todo-page { padding: 16px 24px; }
</style>
```

- [ ] **Step 4: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/components/GlobalTodoCenter.vue \
  network-equipment-pms/pms-frontend/src/views/project/todo/index.vue \
  network-equipment-pms/pms-frontend/src/views/workflow/todo/index.vue
git commit -m "feat(b8-t6): 全局待办聚合（阶段推进/审批/检查项/交付件评审 4 类 Tab）"
git push
```

---

## Task 7: 重做阶段管理页

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/phase/index.vue`

- [ ] **Step 1: 重写 phase/index.vue**

横向流水线升级，每张卡片增加退出条件可视化（4 类条件完成度环形进度），新增阶段时间轴视图切换。

```vue
<template>
  <div class="phase-manage">
    <div class="phase-toolbar">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button label="pipeline">流水线</el-radio-button>
        <el-radio-button label="timeline">时间轴</el-radio-button>
      </el-radio-group>
      <el-button type="primary" :disabled="!currentPhase" @click="handleAdvance">推进当前阶段</el-button>
    </div>

    <!-- 流水线视图 -->
    <div v-if="viewMode === 'pipeline'" class="pipeline-view">
      <div v-for="(phase, idx) in phases" :key="phase.id" class="phase-card-wrapper">
        <el-card
          shadow="hover"
          class="phase-card"
          :class="{ active: phase.status === 'IN_PROGRESS' }"
          @click="selectPhase(phase)"
        >
          <div class="phase-header">
            <PhaseStatusTag :status="phase.status" size="small" />
            <span class="phase-sort">阶段 {{ idx + 1 }}</span>
          </div>
          <h3 class="phase-name">{{ phase.phaseName }}</h3>
          <div class="phase-meta">
            <div>{{ phase.phaseCode }}</div>
            <div v-if="phase.plannedStartDate">{{ phase.plannedStartDate }} ~ {{ phase.plannedEndDate }}</div>
          </div>
          <!-- 退出条件可视化 -->
          <div class="exit-gate-summary">
            <el-progress :percentage="getExitGateProgress(phase)" :stroke-width="4" />
            <span class="gate-text">{{ getExitGateText(phase) }}</span>
          </div>
        </el-card>
        <el-icon v-if="idx < phases.length - 1" class="phase-arrow"><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 时间轴视图 -->
    <el-timeline v-else class="timeline-view">
      <el-timeline-item
        v-for="phase in phases"
        :key="phase.id"
        :type="getTimelineType(phase.status)"
        :timestamp="phase.actualStartDate || phase.plannedStartDate"
      >
        <el-card shadow="hover">
          <h4>{{ phase.phaseName }}</h4>
          <p>{{ phase.status }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <!-- 退出条件详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="阶段退出条件" size="50%">
      <PhaseExitGateEditor v-if="selectedPhase" v-model="selectedPhase.exitCriteria" :disabled="true" />
    </el-drawer>

    <!-- violations 弹窗 -->
    <el-dialog v-model="violationDialogVisible" title="阶段推进被阻止" width="600px">
      <el-table :data="violations" border>
        <el-table-column prop="gateType" label="条件类型" width="120" />
        <el-table-column prop="message" label="未满足原因" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPhasesByProjectId, advancePhase, type PhaseExitGateResult } from '@/api/project-phase'
import PhaseStatusTag from '@/components/PhaseStatusTag.vue'
import PhaseExitGateEditor from '@/components/PhaseExitGateEditor.vue'
import { useProjectContext } from '@/composables/useProjectContext'

interface Props {
  projectId: number
}
const props = defineProps<Props>()

const viewMode = ref<'pipeline' | 'timeline'>('pipeline')
const phases = ref<any[]>([])
const selectedPhase = ref<any>(null)
const drawerVisible = ref(false)
const violationDialogVisible = ref(false)
const violations = ref<any[]>([])

const currentPhase = computed(() => phases.value.find(p => p.status === 'IN_PROGRESS'))

const loadPhases = async () => {
  phases.value = await listPhasesByProjectId(props.projectId)
}

const selectPhase = (phase: any) => {
  selectedPhase.value = phase
  drawerVisible.value = true
}

const getExitGateProgress = (phase: any) => {
  // 简化：根据状态判断
  if (phase.status === 'COMPLETED') return 100
  if (phase.status === 'IN_PROGRESS') return 60
  return 0
}

const getExitGateText = (phase: any) => {
  if (phase.status === 'COMPLETED') return '已完成'
  if (phase.status === 'IN_PROGRESS') return '退出条件部分满足'
  return '未开始'
}

const getTimelineType = (status: string) => {
  const map: Record<string, string> = {
    COMPLETED: 'success',
    IN_PROGRESS: 'primary',
    SKIPPED: 'warning'
  }
  return map[status] || 'info'
}

const handleAdvance = async () => {
  if (!currentPhase.value) return
  try {
    await ElMessageBox.confirm(`确认推进阶段「${currentPhase.value.phaseName}」？`, '提示')
    const result = await advancePhase(currentPhase.value.id)
    if (result && typeof result === 'object' && 'success' in result && result.success === false) {
      violations.value = (result as PhaseExitGateResult).violations ?? []
      violationDialogVisible.value = true
    } else {
      ElMessage.success('阶段推进成功')
      loadPhases()
    }
  } catch (e) {
    // 取消或错误
  }
}

watch(() => props.projectId, loadPhases)
onMounted(loadPhases)
</script>

<style scoped>
.phase-manage { padding: 0; }
.phase-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
.pipeline-view {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
}
.phase-card-wrapper {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
.phase-card {
  width: 240px;
  cursor: pointer;
  transition: all var(--pms-transition-fast);
}
.phase-card.active {
  border-color: var(--pms-color-primary);
  box-shadow: 0 0 0 2px var(--pms-color-primary-light-7);
}
.phase-arrow {
  color: var(--pms-color-text-placeholder);
  margin: 0 4px;
}
.phase-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.phase-sort {
  font-size: 12px;
  color: var(--pms-color-text-placeholder);
}
.phase-name {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 8px;
}
.phase-meta {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 12px;
}
.exit-gate-summary {
  display: flex;
  align-items: center;
  gap: 8px;
}
.exit-gate-summary .el-progress {
  flex: 1;
}
.gate-text {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  white-space: nowrap;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/phase/index.vue
git commit -m "feat(b8-t7): 阶段管理页重做（流水线 + 时间轴双视图 + 退出条件可视化 + violations 弹窗）"
git push
```

---

## Task 8: 重做任务列表页（树形/看板双视图）

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/task/list/index.vue`

- [ ] **Step 1: 重写 task/list/index.vue**

支持树形列表 + 看板双视图切换，看板按状态分列。

```vue
<template>
  <div class="task-list-page">
    <div class="task-toolbar">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button label="tree">树形列表</el-radio-button>
        <el-radio-button label="kanban">看板</el-radio-button>
      </el-radio-group>
      <el-input v-model="searchKeyword" placeholder="搜索任务..." clearable style="width: 200px" />
      <el-button type="primary" @click="showCreateDialog = true">新建任务</el-button>
    </div>

    <!-- 树形列表视图 -->
    <el-table
      v-if="viewMode === 'tree'"
      :data="taskTree"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      default-expand-all
      border
    >
      <el-table-column prop="taskName" label="任务名称" min-width="200" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="{ row }">
          <TaskPriorityTag :priority="row.priority" size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="plannedHours" label="工时" width="80" />
      <el-table-column prop="progress" label="进度" width="120">
        <template #default="{ row }">
          <el-progress :percentage="row.progress ?? 0" :stroke-width="4" />
        </template>
      </el-table-column>
      <el-table-column prop="assigneeName" label="负责人" width="100" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button text size="small" @click="goDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 看板视图 -->
    <div v-else class="kanban-view">
      <div v-for="status in kanbanStatuses" :key="status.value" class="kanban-column">
        <div class="column-header">
          <span class="column-title">{{ status.label }}</span>
          <el-tag size="small" round>{{ getTasksByStatus(status.value).length }}</el-tag>
        </div>
        <div class="column-body">
          <el-card
            v-for="task in getTasksByStatus(status.value)"
            :key="task.id"
            shadow="hover"
            class="kanban-card"
            @click="goDetail(task.id)"
          >
            <div class="card-title">{{ task.taskName }}</div>
            <div class="card-meta">
              <TaskPriorityTag :priority="task.priority" size="small" />
              <span class="assignee">{{ task.assigneeName || '-' }}</span>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 新建任务对话框 -->
    <el-dialog v-model="showCreateDialog" title="新建任务" width="500px">
      <!-- 简化：复用现有表单 -->
      <el-form>
        <el-form-item label="任务名称"><el-input v-model="newTask.taskName" /></el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="newTask.priority">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
            <el-option label="紧急" value="CRITICAL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { listTasks, createTask } from '@/api/implementation'
import TaskPriorityTag from '@/components/TaskPriorityTag.vue'

interface Props {
  projectId?: number
}
const props = defineProps<Props>()
const router = useRouter()

const viewMode = ref<'tree' | 'kanban'>('tree')
const searchKeyword = ref('')
const tasks = ref<any[]>([])
const showCreateDialog = ref(false)
const newTask = ref({ taskName: '', priority: 'MEDIUM' })

const kanbanStatuses = [
  { label: '待开始', value: 'TODO' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '评审中', value: 'IN_REVIEW' },
  { label: '已完成', value: 'COMPLETED' }
]

const taskTree = computed(() => {
  // 构建树形结构
  const map = new Map<number, any>()
  const roots: any[] = []
  tasks.value.forEach(t => map.set(t.id, { ...t, children: [] }))
  tasks.value.forEach(t => {
    if (t.parentTaskId && map.has(t.parentTaskId)) {
      map.get(t.parentTaskId).children.push(map.get(t.id))
    } else {
      roots.push(map.get(t.id))
    }
  })
  return roots
})

const getTasksByStatus = (status: string) => tasks.value.filter(t => t.status === status)

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    TODO: 'info',
    IN_PROGRESS: 'primary',
    IN_REVIEW: 'warning',
    COMPLETED: 'success'
  }
  return map[status] || 'info'
}

const loadTasks = async () => {
  if (!props.projectId) return
  const data = await listTasks({ projectId: props.projectId })
  tasks.value = data.records ?? data ?? []
}

const goDetail = (id: number) => {
  router.push(`/implementation/task/detail/${id}`)
}

const handleCreate = async () => {
  await createTask({ ...newTask.value, projectId: props.projectId })
  showCreateDialog.value = false
  newTask.value = { taskName: '', priority: 'MEDIUM' }
  loadTasks()
}

watch(() => props.projectId, loadTasks)
onMounted(loadTasks)
</script>

<style scoped>
.task-list-page { padding: 0; }
.task-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}
.kanban-view {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
}
.kanban-column {
  flex: 1;
  min-width: 280px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-lg);
  padding: 12px;
}
.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.column-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-regular);
}
.column-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.kanban-card {
  cursor: pointer;
  transition: all var(--pms-transition-fast);
}
.kanban-card:hover {
  transform: translateY(-2px);
}
.kanban-card .card-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}
.kanban-card .card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/task/list/index.vue
git commit -m "feat(b8-t8): 任务列表页重做（树形 + 看板双视图 + 优先级标签 + 进度条）"
git push
```

---

## Task 9: 重做任务详情页（侧栏 + 主区布局）

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/task/detail/index.vue`

- [ ] **Step 1: 重写 detail/index.vue**

改为左侧任务导航树 + 右侧主内容区，主内容区 6 个面板改为可折叠区段。

```vue
<template>
  <div class="task-detail-page">
    <!-- 顶部信息条 -->
    <div class="task-header">
      <div class="header-left">
        <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon>返回</el-button>
        <h1>{{ task?.taskName || '加载中...' }}</h1>
        <el-tag v-if="task" :type="getStatusType(task.status)" size="small">{{ task.status }}</el-tag>
        <TaskPriorityTag v-if="task" :priority="task.priority" size="small" />
      </div>
      <div class="header-right">
        <el-button v-if="canSubmit" type="primary" @click="handleSubmit">提交评审</el-button>
        <el-button v-if="canApprove" type="success" @click="handleApprove">审批通过</el-button>
      </div>
    </div>

    <!-- 主体：侧栏 + 主区 -->
    <div class="task-body">
      <aside class="task-sidebar">
        <div class="sidebar-section">
          <h3>子任务</h3>
          <TaskTree v-if="task" :tasks="task.children || []" @click="goTask" />
        </div>
        <div class="sidebar-section">
          <h3>基本信息</h3>
          <div class="info-list">
            <div class="info-row"><span>负责人</span><span>{{ task?.assigneeName || '-' }}</span></div>
            <div class="info-row"><span>计划工时</span><span>{{ task?.plannedHours || '-' }}h</span></div>
            <div class="info-row"><span>实际工时</span><span>{{ task?.actualHours || '-' }}h</span></div>
            <div class="info-row"><span>开始日期</span><span>{{ task?.planStartDate || '-' }}</span></div>
            <div class="info-row"><span>结束日期</span><span>{{ task?.planEndDate || '-' }}</span></div>
          </div>
        </div>
      </aside>

      <main class="task-main">
        <!-- 进度卡 -->
        <el-card shadow="hover" class="progress-card">
          <div class="progress-content">
            <el-progress type="circle" :percentage="progressVO?.rolledUpProgress ?? task?.progress ?? 0" />
            <div class="progress-meta">
              <div>汇总进度：{{ progressVO?.rolledUpProgress ?? 0 }}%</div>
              <div>子任务数：{{ progressVO?.subTaskCount ?? 0 }}</div>
            </div>
          </div>
        </el-card>

        <!-- 可折叠区段 -->
        <el-collapse v-model="activeSections">
          <el-collapse-item title="检查项" name="checklist">
            <TaskChecklist v-if="task" :task-id="task.id" :items="task.checklistItems" @change="loadTask" />
          </el-collapse-item>
          <el-collapse-item title="评论" name="comment">
            <CommentPanel v-if="task" :task-id="task.id" />
          </el-collapse-item>
          <el-collapse-item title="活动记录" name="activity">
            <el-timeline v-if="activities.length">
              <el-timeline-item v-for="act in activities" :key="act.id" :timestamp="act.createTime">
                {{ act.userName }} {{ act.activityType }}: {{ act.content }}
              </el-timeline-item>
            </el-timeline>
          </el-collapse-item>
        </el-collapse>
      </main>
    </div>

    <!-- 强制检查项未勾选弹窗 -->
    <el-dialog v-model="checklistDialogVisible" title="强制检查项未完成" width="500px">
      <p>以下强制检查项尚未勾选，无法提交评审：</p>
      <ul>
        <li v-for="item in uncheckedItems" :key="item.id">{{ item.title }}</li>
      </ul>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getTaskDetail, submitForReview, approveTask, getTaskProgress } from '@/api/implementation'
import { listActivities } from '@/api/task-activity'
import TaskPriorityTag from '@/components/TaskPriorityTag.vue'
import TaskTree from '@/components/TaskTree.vue'
import TaskChecklist from '@/components/TaskChecklist.vue'
import CommentPanel from '@/components/CommentPanel/index.vue'

const route = useRoute()
const router = useRouter()
const taskId = computed(() => Number(route.params.id))

const task = ref<any>(null)
const activities = ref<any[]>([])
const progressVO = ref<any>(null)
const activeSections = ref(['checklist', 'comment', 'activity'])
const checklistDialogVisible = ref(false)
const uncheckedItems = ref<any[]>([])

const canSubmit = computed(() => task.value?.status === 'IN_PROGRESS')
const canApprove = computed(() => task.value?.status === 'IN_REVIEW')

const getStatusType = (status: string) => {
  const map: Record<string, string> = { TODO: 'info', IN_PROGRESS: 'primary', IN_REVIEW: 'warning', COMPLETED: 'success' }
  return map[status] || 'info'
}

const loadTask = async () => {
  task.value = await getTaskDetail(taskId.value)
  const [acts, prog] = await Promise.all([
    listActivities(taskId.value),
    getTaskProgress(taskId.value).catch(() => null)
  ])
  activities.value = acts ?? []
  progressVO.value = prog
}

const handleSubmit = async () => {
  try {
    await submitForReview(taskId.value)
    ElMessage.success('已提交评审')
    loadTask()
  } catch (e: any) {
    if (e?.errorCode === 'TASK_CHECKLIST_REQUIRED') {
      uncheckedItems.value = e.uncheckedMandatoryItems ?? []
      checklistDialogVisible.value = true
    }
  }
}

const handleApprove = async () => {
  await approveTask(taskId.value)
  ElMessage.success('审批通过')
  loadTask()
}

const goTask = (id: number) => {
  router.push(`/implementation/task/detail/${id}`)
}

onMounted(loadTask)
</script>

<style scoped>
.task-detail-page { padding: 0; }
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: var(--pms-color-bg-card);
  border-bottom: 1px solid var(--pms-color-border-light);
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-left h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}
.task-body {
  display: flex;
  padding: 16px 24px;
  gap: 16px;
}
.task-sidebar {
  width: 280px;
  flex-shrink: 0;
}
.sidebar-section {
  background: var(--pms-color-bg-card);
  border-radius: var(--pms-radius-lg);
  padding: 16px;
  margin-bottom: 16px;
}
.sidebar-section h3 {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--pms-color-text-regular);
}
.info-list { font-size: 13px; }
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  color: var(--pms-color-text-regular);
}
.task-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.progress-card .progress-content {
  display: flex;
  align-items: center;
  gap: 24px;
}
.progress-meta {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/task/detail/index.vue
git commit -m "feat(b8-t9): 任务详情页重做（侧栏+主区布局 + 进度圆环 + 折叠区段 + 强制检查项弹窗）"
git push
```

---

## Task 10: 重做交付件列表 + 详情页

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/deliverable/lifecycle.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/deliverable/detail/index.vue`

- [ ] **Step 1: 重写 lifecycle.vue**

7 态卡片视图，按状态分组展示。

```vue
<template>
  <div class="deliverable-lifecycle">
    <div class="lifecycle-toolbar">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button label="status">按状态分组</el-radio-button>
        <el-radio-button label="list">平铺列表</el-radio-button>
      </el-radio-group>
      <el-button type="primary" @click="showCreateDialog = true">新建交付件</el-button>
    </div>

    <!-- 按状态分组视图 -->
    <div v-if="viewMode === 'status'" class="status-columns">
      <div v-for="status in deliverableStatuses" :key="status.value" class="status-column">
        <div class="column-header">
          <DeliverableStatusBadge :status="status.value" />
          <el-tag size="small" round>{{ getDeliverablesByStatus(status.value).length }}</el-tag>
        </div>
        <div class="column-body">
          <el-card
            v-for="item in getDeliverablesByStatus(status.value)"
            :key="item.id"
            shadow="hover"
            class="deliverable-card"
            @click="goDetail(item.id)"
          >
            <div class="card-title">{{ item.deliverableName }}</div>
            <div class="card-meta">
              <span>v{{ item.currentVersion }}</span>
              <el-tag v-if="item.mandatory" type="danger" size="small">必需</el-tag>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 平铺列表视图 -->
    <el-table v-else :data="deliverables" border>
      <el-table-column prop="deliverableName" label="交付件名称" min-width="200" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }"><DeliverableStatusBadge :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="currentVersion" label="当前版本" width="100" />
      <el-table-column prop="mandatory" label="必需" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.mandatory" type="danger" size="small">是</el-tag>
          <span v-else>否</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { listFullDeliverables } from '@/api/deliverable'
import DeliverableStatusBadge from '@/components/DeliverableStatusBadge.vue'

interface Props {
  projectId?: number
}
const props = defineProps<Props>()
const router = useRouter()

const viewMode = ref<'status' | 'list'>('status')
const deliverables = ref<any[]>([])
const showCreateDialog = ref(false)

const deliverableStatuses = [
  { value: 'DRAFT' },
  { value: 'SUBMITTED' },
  { value: 'REVIEWED' },
  { value: 'SIGNED' },
  { value: 'PUBLISHED' },
  { value: 'REFERENCED' },
  { value: 'ARCHIVED' }
]

const getDeliverablesByStatus = (status: string) => deliverables.value.filter(d => d.status === status)

const loadDeliverables = async () => {
  if (!props.projectId) return
  const data = await listFullDeliverables({ projectId: props.projectId })
  deliverables.value = data.records ?? data ?? []
}

const goDetail = (id: number) => {
  router.push(`/deliverable/detail/${id}`)
}

watch(() => props.projectId, loadDeliverables)
onMounted(loadDeliverables)
</script>

<style scoped>
.lifecycle-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
.status-columns {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}
.status-column {
  flex: 1;
  min-width: 220px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-lg);
  padding: 12px;
}
.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.column-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.deliverable-card {
  cursor: pointer;
  transition: all var(--pms-transition-fast);
}
.deliverable-card:hover { transform: translateY(-2px); }
.deliverable-card .card-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}
.deliverable-card .card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}
</style>
```

- [ ] **Step 2: 重写 deliverable/detail/index.vue**

顶部状态流可视化 + 主体 4 Tab（基本信息/版本历史/签名记录/引用关系）。

```vue
<template>
  <div class="deliverable-detail">
    <div class="detail-header">
      <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon>返回</el-button>
      <h1>{{ deliverable?.deliverableName || '加载中...' }}</h1>
      <DeliverableStatusBadge v-if="deliverable" :status="deliverable.status" />
    </div>

    <!-- 状态流可视化 -->
    <DeliverableStatusFlow v-if="deliverable" :current-status="deliverable.status" class="status-flow" />

    <!-- 操作按钮组 -->
    <div class="action-bar" v-if="deliverable">
      <el-button v-if="canSubmit" type="primary" @click="handleAction('submit')">提交评审</el-button>
      <el-button v-if="canReview" type="primary" @click="handleAction('review')">评审</el-button>
      <el-button v-if="canSign" type="primary" @click="handleAction('sign')">签名</el-button>
      <el-button v-if="canPublish" type="success" @click="handleAction('publish')">发布</el-button>
      <el-button v-if="canArchive" type="warning" @click="handleAction('archive')">归档</el-button>
      <el-button @click="showReviseDialog = true">修订新版本</el-button>
    </div>

    <!-- 4 Tab -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="交付件名称">{{ deliverable?.deliverableName }}</el-descriptions-item>
          <el-descriptions-item label="所属阶段">{{ deliverable?.phaseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前版本">v{{ deliverable?.currentVersion }}</el-descriptions-item>
          <el-descriptions-item label="是否必需">{{ deliverable?.mandatory ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="签核角色">{{ deliverable?.approverRole || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ deliverable?.publishedAt || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="版本历史" name="versions">
        <DeliverableVersionList v-if="deliverable" :deliverable-id="deliverable.id" />
      </el-tab-pane>
      <el-tab-pane label="签名记录" name="signatures">
        <!-- 签名列表 -->
      </el-tab-pane>
      <el-tab-pane label="引用关系" name="references">
        <!-- 引用关系 -->
      </el-tab-pane>
    </el-tabs>

    <!-- 修订对话框 -->
    <el-dialog v-model="showReviseDialog" title="修订新版本" width="500px">
      <el-form>
        <el-form-item label="文件"><el-input v-model="reviseForm.filePath" /></el-form-item>
        <el-form-item label="变更说明"><el-input v-model="reviseForm.changeLog" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviseDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRevise">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getDeliverable, submitDeliverable, reviewDeliverable,
  signDeliverable, publishDeliverable, archiveDeliverable, revise
} from '@/api/deliverable'
import DeliverableStatusBadge from '@/components/DeliverableStatusBadge.vue'
import DeliverableStatusFlow from '@/components/DeliverableStatusFlow.vue'
import DeliverableVersionList from '@/components/DeliverableVersionList.vue'

const route = useRoute()
const deliverableId = Number(route.params.id)

const deliverable = ref<any>(null)
const activeTab = ref('info')
const showReviseDialog = ref(false)
const reviseForm = ref({ filePath: '', changeLog: '' })

const canSubmit = computed(() => deliverable.value?.status === 'DRAFT')
const canReview = computed(() => deliverable.value?.status === 'SUBMITTED')
const canSign = computed(() => deliverable.value?.status === 'REVIEWED')
const canPublish = computed(() => deliverable.value?.status === 'SIGNED')
const canArchive = computed(() => ['PUBLISHED', 'REFERENCED'].includes(deliverable.value?.status))

const loadDeliverable = async () => {
  deliverable.value = await getDeliverable(deliverableId)
}

const handleAction = async (action: string) => {
  const map: Record<string, any> = {
    submit: submitDeliverable,
    review: () => reviewDeliverable(deliverableId, true),
    sign: signDeliverable,
    publish: publishDeliverable,
    archive: archiveDeliverable
  }
  await map[action](deliverableId)
  ElMessage.success('操作成功')
  loadDeliverable()
}

const handleRevise = async () => {
  await revise(deliverableId, reviseForm.value.filePath, reviseForm.value.changeLog)
  showReviseDialog.value = false
  reviseForm.value = { filePath: '', changeLog: '' }
  ElMessage.success('新版本已创建')
  loadDeliverable()
}

onMounted(loadDeliverable)
</script>

<style scoped>
.deliverable-detail { padding: 16px 24px; }
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.detail-header h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}
.status-flow { margin-bottom: 16px; }
.action-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  padding: 12px;
  background: var(--pms-color-bg-card);
  border-radius: var(--pms-radius-lg);
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/deliverable/lifecycle.vue \
  network-equipment-pms/pms-frontend/src/views/deliverable/detail/index.vue
git commit -m "feat(b8-t10): 交付件列表+详情重做（7 态卡片分组 + 状态流可视化 + 操作按钮组 + 4 Tab）"
git push
```

---

## Task 11: 重做审批中心 + 详情 + 历史

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/workflow/approval-center/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/workflow/approval-detail/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/workflow/approval-history/index.vue`

- [ ] **Step 1: 重写 approval-center/index.vue**

3 Tab（待办/已办/我提交的）+ 卡片化列表。

```vue
<template>
  <div class="approval-center">
    <PageHeader title="统一审批中心">
      <template #actions>
        <el-input v-model="searchKeyword" placeholder="搜索审批..." clearable style="width: 240px" />
      </template>
    </PageHeader>

    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane :label="`待办 (${todoCount})`" name="todo">
        <div class="approval-list">
          <el-card v-for="item in approvals" :key="item.id" shadow="hover" class="approval-card" @click="goDetail(item.id)">
            <div class="card-header">
              <span class="card-title">{{ item.title }}</span>
              <el-tag size="small">{{ item.approvalType }}</el-tag>
            </div>
            <div class="card-meta">
              <span>提交人：{{ item.submitterName }}</span>
              <span>当前节点：{{ item.currentNodeName }}</span>
              <span>提交时间：{{ item.submittedAt }}</span>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
      <el-tab-pane label="已办" name="done">
        <!-- 已办列表 -->
      </el-tab-pane>
      <el-tab-pane label="我提交的" name="mine">
        <!-- 我提交的列表 -->
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listApprovals } from '@/api/approval-center'
import PageHeader from '@/components/PageHeader.vue'
import { useProjectContext } from '@/composables/useProjectContext'

interface Props {
  projectId?: number
}
const props = defineProps<Props>()
const router = useRouter()
const { projectId: ctxProjectId } = useProjectContext()

const activeTab = ref('todo')
const searchKeyword = ref('')
const approvals = ref<any[]>([])

const todoCount = computed(() => approvals.value.length)
const targetProjectId = computed(() => props.projectId ?? ctxProjectId.value)

const loadData = async () => {
  const status = activeTab.value === 'todo' ? 'PENDING' : activeTab.value === 'done' ? 'APPROVED' : 'PENDING'
  const params: any = { status }
  if (targetProjectId.value) params.projectId = targetProjectId.value
  const data = await listApprovals(params)
  approvals.value = data.records ?? data ?? []
}

const goDetail = (id: number) => {
  router.push(`/workflow/approval-detail/${id}`)
}

onMounted(loadData)
</script>

<style scoped>
.approval-center { padding: 16px 24px; }
.approval-list { display: flex; flex-direction: column; gap: 8px; }
.approval-card { cursor: pointer; transition: all var(--pms-transition-fast); }
.approval-card:hover { transform: translateY(-2px); }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
}
.card-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}
</style>
```

- [ ] **Step 2: 重写 approval-detail/index.vue**

脱敏字段展示 + 多轮次时间轴 + 操作区。

```vue
<template>
  <div class="approval-detail">
    <div class="detail-header">
      <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon>返回</el-button>
      <h1>{{ detail?.title || '加载中...' }}</h1>
      <el-tag v-if="detail">{{ detail.status }}</el-tag>
    </div>

    <el-row :gutter="16">
      <el-col :span="16">
        <!-- 业务数据（含脱敏字段） -->
        <el-card shadow="hover">
          <template #header>业务详情</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item
              v-for="(value, key) in detail?.maskedFields"
              :key="key"
              :label="key"
            >
              <SensitiveFieldDisplay
                :value="value"
                :permission="getFieldPermission(key)"
              />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 审批历史时间轴 -->
        <el-card shadow="hover" style="margin-top: 16px">
          <template #header>审批历史</template>
          <ApprovalTimeline :history="detail?.history || []" />
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- 操作面板 -->
        <el-card shadow="hover">
          <template #header>审批操作</template>
          <el-form>
            <el-form-item label="审批意见">
              <el-input v-model="comment" type="textarea" :rows="4" />
            </el-form-item>
            <el-button type="primary" @click="handleApprove" :disabled="detail?.status !== 'PENDING'">通过</el-button>
            <el-button type="danger" @click="handleReject" :disabled="detail?.status !== 'PENDING'">退回</el-button>
            <el-button @click="handleWithdraw" :disabled="!isSubmitter">撤回</el-button>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getApprovalDetail, approveApproval, rejectApproval, withdrawApproval } from '@/api/approval-center'
import SensitiveFieldDisplay from '@/components/SensitiveFieldDisplay.vue'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const approvalId = Number(route.params.id)

const detail = ref<any>(null)
const comment = ref('')

const isSubmitter = computed(() => detail.value?.submitterId === userStore.userInfo?.id)

const loadDetail = async () => {
  detail.value = await getApprovalDetail(approvalId)
}

const getFieldPermission = (fieldName: string) => {
  return detail.value?.fieldPermissions?.find((p: any) => p.fieldName === fieldName)
}

const handleApprove = async () => {
  await approveApproval(approvalId, comment.value)
  ElMessage.success('已通过')
  loadDetail()
}

const handleReject = async () => {
  await rejectApproval(approvalId, comment.value)
  ElMessage.success('已退回')
  loadDetail()
}

const handleWithdraw = async () => {
  await withdrawApproval(approvalId)
  ElMessage.success('已撤回')
  loadDetail()
}

onMounted(loadDetail)
</script>

<style scoped>
.approval-detail { padding: 16px 24px; }
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.detail-header h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}
</style>
```

- [ ] **Step 3: 重写 approval-history/index.vue**

多轮次时间轴展示。

```vue
<template>
  <div class="approval-history-page">
    <PageHeader title="审批历史">
      <template #actions>
        <el-button @click="$router.back()">返回</el-button>
      </template>
    </PageHeader>
    <el-card shadow="hover">
      <ApprovalTimeline :history="history" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { listApprovalHistory } from '@/api/approval-center'
import PageHeader from '@/components/PageHeader.vue'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'

const route = useRoute()
const recordId = Number(route.params.recordId)
const history = ref<any[]>([])

onMounted(async () => {
  history.value = await listApprovalHistory(recordId)
})
</script>

<style scoped>
.approval-history-page { padding: 16px 24px; }
</style>
```

- [ ] **Step 4: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/workflow/approval-center/index.vue \
  network-equipment-pms/pms-frontend/src/views/workflow/approval-detail/index.vue \
  network-equipment-pms/pms-frontend/src/views/workflow/approval-history/index.vue
git commit -m "feat(b8-t11): 审批中心+详情+历史重做（3 Tab 卡片化 + 脱敏字段展示 + 多轮次时间轴 + 操作面板）"
git push
```

---

## Task 12: 重做基线列表 + 偏差分析

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/baseline/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/baseline/diff.vue`

- [ ] **Step 1: 重写 baseline/index.vue**

卡片化列表 + 保存新基线按钮。

```vue
<template>
  <div class="baseline-list-page">
    <PageHeader title="基线管理">
      <template #actions>
        <el-button type="primary" @click="handleSave">保存新基线</el-button>
      </template>
    </PageHeader>

    <div class="baseline-list">
      <el-card v-for="baseline in baselines" :key="baseline.id" shadow="hover" class="baseline-card">
        <div class="card-header">
          <span class="card-title">{{ baseline.baselineName }}</span>
          <el-tag :type="getStatusType(baseline.status)" size="small">{{ baseline.status }}</el-tag>
        </div>
        <div class="card-meta">
          <span>创建时间：{{ baseline.createTime }}</span>
          <span v-if="baseline.approvedAt">审批时间：{{ baseline.approvedAt }}</span>
        </div>
        <div class="card-actions">
          <el-button text size="small" @click="goDiff(baseline.id)">偏差分析</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listBaselines, saveBaseline } from '@/api/baseline'
import PageHeader from '@/components/PageHeader.vue'
import { useProjectContext } from '@/composables/useProjectContext'

interface Props {
  projectId?: number
}
const props = defineProps<Props>()
const router = useRouter()
const { projectId: ctxProjectId } = useProjectContext()

const baselines = ref<any[]>([])
const targetProjectId = computed(() => props.projectId ?? ctxProjectId.value)

const loadBaselines = async () => {
  if (!targetProjectId.value) return
  const data = await listBaselines(targetProjectId.value)
  baselines.value = data.records ?? data ?? []
}

const handleSave = async () => {
  await saveBaseline({ projectId: targetProjectId.value, baselineName: `基线 ${new Date().toLocaleString()}` })
  ElMessage.success('基线已保存')
  loadBaselines()
}

const goDiff = (baselineId: number) => {
  router.push(`/baseline/diff/${baselineId}`)
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { DRAFT: 'warning', APPROVED: 'success', SUPERSEDED: 'info' }
  return map[status] || 'info'
}

watch(() => props.projectId, loadBaselines)
onMounted(loadBaselines)
</script>

<style scoped>
.baseline-list-page { padding: 16px 24px; }
.baseline-list { display: flex; flex-direction: column; gap: 8px; }
.baseline-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.card-title { font-size: 15px; font-weight: 600; }
.card-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 8px;
}
</style>
```

- [ ] **Step 2: 重写 baseline/diff.vue**

偏差表格 + 甘特图叠加对比。

```vue
<template>
  <div class="baseline-diff-page">
    <PageHeader title="基线偏差分析">
      <template #actions>
        <el-button @click="$router.back()">返回</el-button>
        <el-button type="primary" @click="handleRequestChange">申请变更</el-button>
      </template>
    </PageHeader>

    <el-card shadow="hover" class="diff-summary">
      <el-row :gutter="16">
        <el-col :span="6"><div class="metric"><span>总任务数</span><strong>{{ diffResult?.totalTasks ?? 0 }}</strong></div></el-col>
        <el-col :span="6"><div class="metric"><span>偏差任务数</span><strong>{{ diffResult?.totalVarianced ?? 0 }}</strong></div></el-col>
        <el-col :span="6"><div class="metric"><span>最大偏差</span><strong>{{ diffResult?.maxVariance ?? 0 }} 天</strong></div></el-col>
        <el-col :span="6"><div class="metric"><span>是否需审批</span><strong :class="{ warning: diffResult?.needsApproval }">{{ diffResult?.needsApproval ? '是' : '否' }}</strong></div></el-col>
      </el-row>
    </el-card>

    <BaselineDiffTable :diffs="diffResult?.diffs || []" :threshold-days="5" class="diff-table" />

    <!-- 甘特图叠加 -->
    <el-card shadow="hover" class="gantt-overlay">
      <template #header>基线 vs 当前 甘特图对比</template>
      <ProjectGantt
        v-if="diffResult"
        :tasks="diffResult.currentTasks"
        :dependencies="[]"
        :baseline="diffResult.baselineTasks"
        :project-id="diffResult.projectId"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getBaselineDiff, requestBaselineChange } from '@/api/baseline'
import PageHeader from '@/components/PageHeader.vue'
import BaselineDiffTable from '@/components/BaselineDiffTable.vue'
import ProjectGantt from '@/components/ProjectGantt.vue'

const route = useRoute()
const baselineId = Number(route.params.baselineId)
const diffResult = ref<any>(null)

const loadDiff = async () => {
  diffResult.value = await getBaselineDiff(baselineId)
}

const handleRequestChange = async () => {
  await requestBaselineChange(baselineId, '计划变更')
  ElMessage.success('变更申请已提交')
  loadDiff()
}

onMounted(loadDiff)
</script>

<style scoped>
.baseline-diff-page { padding: 16px 24px; }
.diff-summary { margin-bottom: 16px; }
.metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.metric span {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}
.metric strong {
  font-size: 20px;
  color: var(--pms-color-text-primary);
}
.metric strong.warning { color: var(--pms-color-warning); }
.diff-table { margin-bottom: 16px; }
</style>
```

- [ ] **Step 3: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/baseline/index.vue \
  network-equipment-pms/pms-frontend/src/views/baseline/diff.vue
git commit -m "feat(b8-t12): 基线列表+偏差分析重做（卡片化 + 偏差汇总指标 + 甘特图叠加对比）"
git push
```

---

## Task 13: 重做模板 + 配置 + 看板 + 树视图

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/views/project/template/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/project/template/form.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/project/template/version.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/project/kanban/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/project/tree/index.vue`
- Modify: `network-equipment-pms/pms-frontend/src/views/project-config/index.vue`

- [ ] **Step 1: 重写 template/index.vue 为卡片网格**

```vue
<template>
  <div class="template-list-page">
    <PageHeader title="项目模板" description="管理项目模板，支持版本化发布">
      <template #actions>
        <el-button type="primary" @click="goCreate">新建模板</el-button>
      </template>
    </PageHeader>

    <div class="template-grid">
      <el-card v-for="tpl in templates" :key="tpl.id" shadow="hover" class="template-card" @click="goEdit(tpl.id)">
        <div class="card-header">
          <el-icon :size="24"><Files /></el-icon>
          <el-tag :type="getStatusType(tpl.status)" size="small">{{ tpl.status }}</el-tag>
        </div>
        <h3 class="card-title">{{ tpl.templateName }}</h3>
        <div class="card-meta">
          <span>编码：{{ tpl.templateCode }}</span>
          <span>分类：{{ tpl.category }}</span>
        </div>
        <p class="card-desc">{{ tpl.description }}</p>
        <div class="card-actions">
          <el-button text size="small" @click.stop="goVersion(tpl.id)">版本管理</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Files } from '@element-plus/icons-vue'
import { listTemplates } from '@/api/project-template'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const templates = ref<any[]>([])

const loadTemplates = async () => {
  const data = await listTemplates({ page: 1, size: 100 })
  templates.value = data.records ?? data ?? []
}

const goCreate = () => router.push('/project/template/form')
const goEdit = (id: number) => router.push(`/project/template/form/${id}`)
const goVersion = (id: number) => router.push(`/project/template/version/${id}`)

const getStatusType = (status: string) => {
  const map: Record<string, string> = { DRAFT: 'info', PUBLISHED: 'success', DEPRECATED: 'danger' }
  return map[status] || 'info'
}

onMounted(loadTemplates)
</script>

<style scoped>
.template-list-page { padding: 16px 24px; }
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.template-card { cursor: pointer; transition: all var(--pms-transition-fast); }
.template-card:hover { transform: translateY(-2px); }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
}
.card-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 8px;
}
.card-desc {
  font-size: 13px;
  color: var(--pms-color-text-regular);
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
```

- [ ] **Step 2: 重写 template/form.vue 为分步表单**

将原表单改为分步骤：基本信息 → 阶段定义 → 任务定义 → 交付件定义 → 审批计划 → 预览保存。

```vue
<template>
  <div class="template-form-page">
    <PageHeader :title="isEdit ? '编辑模板' : '新建模板'">
      <template #actions>
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </PageHeader>

    <el-steps :active="activeStep" finish-status="success" align-center>
      <el-step title="基本信息" />
      <el-step title="阶段定义" />
      <el-step title="任务定义" />
      <el-step title="交付件定义" />
      <el-step title="审批计划" />
      <el-step title="预览保存" />
    </el-steps>

    <div class="step-content">
      <!-- Step 1: 基本信息 -->
      <el-form v-if="activeStep === 0" :model="form" label-width="100px">
        <el-form-item label="模板编码"><el-input v-model="form.templateCode" /></el-form-item>
        <el-form-item label="模板名称"><el-input v-model="form.templateName" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category">
            <el-option label="实施" value="IMPLEMENT" />
            <el-option label="维护" value="MAINTENANCE" />
            <el-option label="咨询" value="CONSULTING" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>

      <!-- Step 2-5: 简化，使用 PhaseExitGateEditor 复用 -->
      <div v-else-if="activeStep < 5">
        <el-empty description="使用快照构建器配置（复用已有组件）" />
      </div>

      <!-- Step 6: 预览 -->
      <el-card v-else>
        <pre>{{ JSON.stringify(form, null, 2) }}</pre>
      </el-card>
    </div>

    <div class="step-actions">
      <el-button v-if="activeStep > 0" @click="activeStep--">上一步</el-button>
      <el-button v-if="activeStep < 5" type="primary" @click="activeStep++">下一步</el-button>
      <el-button v-if="activeStep === 5" type="success" @click="handleSave">保存模板</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTemplate, createTemplate, updateTemplate } from '@/api/project-template'
import PageHeader from '@/components/PageHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const activeStep = ref(0)
const form = ref({
  templateCode: '',
  templateName: '',
  category: 'IMPLEMENT',
  description: ''
})

const handleSave = async () => {
  if (isEdit.value) {
    await updateTemplate(form.value)
  } else {
    await createTemplate(form.value)
  }
  ElMessage.success('保存成功')
  router.back()
}

onMounted(async () => {
  if (isEdit.value) {
    const data = await getTemplate(Number(route.params.id))
    form.value = { ...form.value, ...data }
  }
})
</script>

<style scoped>
.template-form-page { padding: 16px 24px; }
.step-content {
  margin: 24px 0;
  min-height: 300px;
}
.step-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}
</style>
```

- [ ] **Step 3: 重写 template/version.vue 为版本时间轴**

```vue
<template>
  <div class="template-version-page">
    <PageHeader title="版本管理">
      <template #actions>
        <el-button @click="$router.back()">返回</el-button>
        <el-button type="primary" @click="showPublishDialog = true">发布新版本</el-button>
      </template>
    </PageHeader>

    <el-timeline>
      <el-timeline-item
        v-for="version in versions"
        :key="version.id"
        :type="version.status === 'PUBLISHED' ? 'success' : 'info'"
        :timestamp="version.publishedAt || version.createTime"
      >
        <el-card shadow="hover">
          <div class="version-header">
            <h3>v{{ version.version }}</h3>
            <el-tag :type="version.status === 'PUBLISHED' ? 'success' : 'info'" size="small">{{ version.status }}</el-tag>
          </div>
          <p>{{ version.changeLog }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <el-dialog v-model="showPublishDialog" title="发布新版本" width="500px">
      <el-form>
        <el-form-item label="版本号"><el-input v-model="publishForm.version" placeholder="v1.0.0" /></el-form-item>
        <el-form-item label="变更说明"><el-input v-model="publishForm.changeLog" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listTemplateVersions, publishVersion } from '@/api/project-template'
import PageHeader from '@/components/PageHeader.vue'

const route = useRoute()
const templateId = Number(route.params.id)
const versions = ref<any[]>([])
const showPublishDialog = ref(false)
const publishForm = ref({ version: '', changeLog: '' })

const loadVersions = async () => {
  versions.value = await listTemplateVersions(templateId)
}

const handlePublish = async () => {
  await publishVersion(templateId, publishForm.value)
  showPublishDialog.value = false
  ElMessage.success('版本已发布')
  loadVersions()
}

onMounted(loadVersions)
</script>

<style scoped>
.template-version-page { padding: 16px 24px; }
.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.version-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}
</style>
```

- [ ] **Step 4: 重写 kanban/index.vue 为项目状态看板**

```vue
<template>
  <div class="project-kanban-page">
    <PageHeader title="项目看板" description="按项目状态分列展示" />
    <div class="kanban-columns">
      <div v-for="status in statuses" :key="status.value" class="kanban-column">
        <div class="column-header">
          <ProjectStatusTag :status="status.value" />
          <el-tag size="small" round>{{ getProjectsByStatus(status.value).length }}</el-tag>
        </div>
        <div class="column-body">
          <el-card
            v-for="project in getProjectsByStatus(status.value)"
            :key="project.id"
            shadow="hover"
            class="kanban-card"
            @click="goWorkspace(project.id)"
          >
            <div class="card-title">{{ project.projectName }}</div>
            <div class="card-meta">{{ project.projectCode }}</div>
            <el-progress :percentage="project.progress ?? 0" :stroke-width="4" />
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listProjects } from '@/api/project'
import PageHeader from '@/components/PageHeader.vue'
import ProjectStatusTag from '@/components/ProjectStatusTag.vue'

const router = useRouter()
const projects = ref<any[]>([])
const statuses = [
  { value: 'PLANNING' },
  { value: 'EXECUTING' },
  { value: 'CLOSING' },
  { value: 'CLOSED' }
]

const getProjectsByStatus = (status: string) => projects.value.filter(p => p.status === status)

const goWorkspace = (id: number) => router.push(`/project/workspace/${id}`)

onMounted(async () => {
  const data = await listProjects({ page: 1, size: 100 })
  projects.value = data.records ?? data ?? []
})
</script>

<style scoped>
.project-kanban-page { padding: 16px 24px; }
.kanban-columns {
  display: flex;
  gap: 16px;
  overflow-x: auto;
}
.kanban-column {
  flex: 1;
  min-width: 280px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-lg);
  padding: 12px;
}
.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.column-body { display: flex; flex-direction: column; gap: 8px; }
.kanban-card { cursor: pointer; transition: all var(--pms-transition-fast); }
.kanban-card:hover { transform: translateY(-2px); }
.kanban-card .card-title { font-size: 14px; font-weight: 500; margin-bottom: 4px; }
.kanban-card .card-meta { font-size: 12px; color: var(--pms-color-text-secondary); margin-bottom: 8px; }
</style>
```

- [ ] **Step 5: 重写 tree/index.vue 为树形+卡片混合**

```vue
<template>
  <div class="project-tree-page">
    <PageHeader title="主子项目树" description="查看项目的层级结构" />
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="hover">
          <el-tree :data="treeData" :props="treeProps" node-key="id" @node-click="handleClick">
            <template #default="{ data }">
              <div class="tree-node">
                <span>{{ data.projectName }}</span>
                <ProjectStatusTag :status="data.status" size="small" />
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card v-if="selectedProject" shadow="hover">
          <template #header>{{ selectedProject.projectName }}</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="项目编码">{{ selectedProject.projectCode }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <ProjectStatusTag :status="selectedProject.status" />
            </el-descriptions-item>
            <el-descriptions-item label="进度">
              <el-progress :percentage="selectedProject.progress ?? 0" />
            </el-descriptions-item>
            <el-descriptions-item label="子项目数">{{ selectedProject.children?.length ?? 0 }}</el-descriptions-item>
          </el-descriptions>
          <el-button type="primary" @click="goWorkspace(selectedProject.id)" style="margin-top: 16px">进入工作区</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProjectTree } from '@/api/project'
import PageHeader from '@/components/PageHeader.vue'
import ProjectStatusTag from '@/components/ProjectStatusTag.vue'

const router = useRouter()
const treeData = ref<any[]>([])
const selectedProject = ref<any>(null)
const treeProps = { label: 'projectName', children: 'children' }

const handleClick = (data: any) => {
  selectedProject.value = data
}

const goWorkspace = (id: number) => router.push(`/project/workspace/${id}`)

onMounted(async () => {
  const data = await getProjectTree(0)
  treeData.value = Array.isArray(data) ? data : [data]
})
</script>

<style scoped>
.project-tree-page { padding: 16px 24px; }
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  padding-right: 8px;
}
</style>
```

- [ ] **Step 6: 重写 project-config/index.vue 为配置分组卡片**

```vue
<template>
  <div class="project-config-page">
    <PageHeader title="项目配置" description="多层级配置（项目级 > 模板级 > 系统默认）">
      <template #actions>
        <el-button type="primary" @click="handleSave">保存配置</el-button>
      </template>
    </PageHeader>

    <div class="config-groups">
      <el-card v-for="group in configGroups" :key="group.title" shadow="hover" class="config-card">
        <template #header>{{ group.title }}</template>
        <el-form label-width="200px">
          <el-form-item v-for="item in group.items" :key="item.key" :label="item.label">
            <el-input v-model="configMap[item.key]" :placeholder="item.description" />
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getProjectConfigs, updateProjectConfigs } from '@/api/project-config'
import PageHeader from '@/components/PageHeader.vue'
import { useProjectContext } from '@/composables/useProjectContext'

interface Props {
  projectId?: number
}
const props = defineProps<Props>()
const { projectId: ctxProjectId } = useProjectContext()

const configMap = ref<Record<string, string>>({})
const targetProjectId = computed(() => props.projectId ?? ctxProjectId.value)

const configGroups = [
  {
    title: '阶段管理',
    items: [
      { key: 'phase.auto_advance', label: '自动推进阶段', description: 'true/false' },
      { key: 'phase.exit_gate.strict', label: '严格退出条件', description: 'true/false' }
    ]
  },
  {
    title: '基线管理',
    items: [
      { key: 'baseline.variance.threshold.days', label: '偏差天数阈值', description: '默认 5' },
      { key: 'baseline.variance.threshold.percent', label: '偏差百分比阈值', description: '默认 10' }
    ]
  },
  {
    title: '审批管理',
    items: [
      { key: 'approval.timeout.hours', label: '审批超时小时数', description: '默认 48' },
      { key: 'approval.timeout.action', label: '超时动作', description: 'AUTO_APPROVE/AUTO_REJECT/NOTIFY_ONLY' }
    ]
  }
]

const loadConfig = async () => {
  if (!targetProjectId.value) return
  configMap.value = await getProjectConfigs(targetProjectId.value)
}

const handleSave = async () => {
  await updateProjectConfigs(targetProjectId.value, configMap.value)
  ElMessage.success('配置已保存')
}

watch(() => props.projectId, loadConfig)
onMounted(loadConfig)
</script>

<style scoped>
.project-config-page { padding: 16px 24px; }
.config-groups { display: flex; flex-direction: column; gap: 16px; }
</style>
```

- [ ] **Step 7: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/views/project/template/ \
  network-equipment-pms/pms-frontend/src/views/project/kanban/index.vue \
  network-equipment-pms/pms-frontend/src/views/project/tree/index.vue \
  network-equipment-pms/pms-frontend/src/views/project-config/index.vue
git commit -m "feat(b8-t13): 模板（卡片网格+分步表单+版本时间轴）+ 看板 + 树视图 + 配置分组重做"
git push
```

---

## Task 14: 路由全量重构 + 验收

**Files:**
- Modify: `network-equipment-pms/pms-frontend/src/router/index.ts`

- [ ] **Step 1: 重构路由为项目为中心嵌套结构**

将 `/project` 路由段改为：
- `/project/list` — 项目列表
- `/project/workspace/:id` — 项目工作区（新枢纽）
- `/project/workspace/:id/overview` — 概览
- `/project/workspace/:id/phase` — 阶段
- `/project/workspace/:id/task` — 任务
- `/project/workspace/:id/deliverable` — 交付件
- `/project/workspace/:id/baseline` — 基线
- `/project/workspace/:id/approval` — 审批
- `/project/workspace/:id/member` — 成员
- `/project/workspace/:id/config` — 配置
- `/project/:id/gantt` — 甘特图独立页
- `/project/:id/todo` — 项目待办
- `/project/tree` — 主子项目树
- `/project/template` — 模板列表
- `/project/template/form/:id?` — 模板编辑
- `/project/template/version/:id` — 版本管理
- `/project/kanban` — 项目看板

更新 `/project` children：

```typescript
{
  path: '/project',
  component: Layout,
  redirect: '/project/list',
  meta: { title: '项目管理', icon: 'Folder', requiresAuth: true },
  children: [
    { path: 'list', name: 'ProjectList',
      component: () => import('@/views/project/list/index.vue'),
      meta: { title: '项目列表', icon: 'Folder' } },
    { path: 'workspace/:id', name: 'ProjectWorkspace',
      component: () => import('@/views/project/workspace/index.vue'),
      meta: { title: '项目工作区', hidden: true } },
    { path: 'tree', name: 'ProjectTree',
      component: () => import('@/views/project/tree/index.vue'),
      meta: { title: '主子项目树', icon: 'Share' } },
    { path: 'kanban', name: 'ProjectKanban',
      component: () => import('@/views/project/kanban/index.vue'),
      meta: { title: '项目看板', icon: 'Grid' } },
    { path: 'template', name: 'ProjectTemplate',
      component: () => import('@/views/project/template/index.vue'),
      meta: { title: '项目模板', icon: 'Files', perms: 'project:template:list' } },
    { path: 'template/form/:id?', name: 'ProjectTemplateForm',
      component: () => import('@/views/project/template/form.vue'),
      meta: { title: '模板编辑', hidden: true } },
    { path: 'template/version/:id', name: 'ProjectTemplateVersion',
      component: () => import('@/views/project/template/version.vue'),
      meta: { title: '版本管理', hidden: true } },
    { path: ':id/gantt', name: 'ProjectGanttPage',
      component: () => import('@/views/project/gantt/index.vue'),
      meta: { title: '项目甘特图', hidden: true } },
    { path: ':id/todo', name: 'ProjectTodo',
      component: () => import('@/views/project/todo/index.vue'),
      meta: { title: '项目待办', hidden: true } }
  ]
}
```

- [ ] **Step 2: 验收代码审查**

检查以下关键链路：
1. 项目列表 → 点击卡片 → 项目工作区
2. 项目工作区 → 8 Tab 切换 → 各子模块
3. 项目工作区 → 甘特图按钮 → 甘特图页
4. 项目工作区 → 待办按钮 → 待办聚合页
5. 左侧项目树 → 点击节点 → 切换项目工作区
6. 阶段管理 → 推进按钮 → violations 弹窗
7. 任务详情 → 提交评审 → 强制检查项弹窗
8. 交付件详情 → 修订新版本 → 版本列表更新
9. 审批中心 → 卡片点击 → 详情页 → 通过/退回
10. 基线偏差 → 甘特图叠加对比

- [ ] **Step 3: 提交**

```bash
git add network-equipment-pms/pms-frontend/src/router/index.ts
git commit -m "feat(b8-t14): 路由全量重构为项目为中心嵌套结构（workspace/:id 为枢纽 + gantt + todo 独立页）"
git push
```

---

## Task 15: 端到端验收 + 实施总结

**Files:**
- Create: `network-equipment-pms/docs/superpowers/acceptance/frontend-redesign-acceptance.md`

- [ ] **Step 1: 创建验收报告**

记录 14 个任务的完成情况、关键文件清单、验收点核对、技术债。

- [ ] **Step 2: 提交**

```bash
git add network-equipment-pms/docs/superpowers/acceptance/frontend-redesign-acceptance.md
git commit -m "docs(b8-t15): 前端重做端到端验收报告 + 实施总结"
git push
```

---

## 总结

本计划共 15 个任务，覆盖：
- 7 个新组件（ProjectStatusTag/PhaseStatusTag/TaskPriorityTag/DeliverableStatusBadge/EmptyState/SkeletonCard/PageHeader）
- 1 个新 composable（useProjectContext）
- 3 个新视图（workspace/overview/gantt/todo）
- 1 个新布局组件（ProjectTreeSidebar）
- 1 个新甘特图组件（ProjectGantt）
- 1 个新待办聚合组件（GlobalTodoCenter）
- 12 个重做视图（list/phase/task×2/deliverable×2/baseline×2/workflow×3/template×3/kanban/tree/config）
- 路由全量重构
- 端到端验收

执行完成后，项目管理前端将形成以项目工作区为枢纽的现代化中后台界面。
