<script setup lang="ts">
// =============================================================================
// ProjectOverview - 项目概览 Tab
// -----------------------------------------------------------------------------
// 在 ProjectWorkspace 的「概览」Tab 内渲染：
//   1. 基础信息卡片：项目名/状态/类型/起止日期/项目经理/客户/描述
//      （项目状态、进度已显示在工作区顶部 workspace-meta 栏，此处不再重复展示
//       状态进度、任务统计等指标卡片，避免信息冗余）
//   2. 底部标签页（仅在有相关数据时显示）：
//      a) 主子项目 — 存在父项目或子项目时显示
//      b) 设备清单 — 项目下分配的资产列表
//      c) 发货设备明细 — 项目下出库/在途的资产
//      d) 项目成员 — 简要成员列表（点击进入成员 Tab）
// =============================================================================
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  getProject,
  getProjectTree,
  type Project,
  type ProjectTreeNode
} from '@/api/project'
import { listAssets, type Asset, type AssetPageResult } from '@/api/asset'
import { listMembersByProjectId, type ProjectMember } from '@/api/project-member'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'

interface Props {
  project: Project
}
const props = defineProps<Props>()

const router = useRouter()

// ============ 主子项目 ============
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

/** 是否需要展示「主子项目」tab：有父项目 或 有子项目 */
const hasProjectHierarchy = computed(() => {
  return !!parentProject.value || childProjects.value.length > 0
})

// ============ 设备清单 / 发货设备明细 ============
const assetsLoading = ref(false)
const allAssets = ref<Asset[]>([])
const assetsTotal = ref(0)

async function loadAssets() {
  if (!props.project?.id) return
  assetsLoading.value = true
  try {
    const res: AssetPageResult = await listAssets({
      page: 1,
      size: 100,
      projectId: props.project.id
    })
    allAssets.value = res.records ?? []
    assetsTotal.value = res.total ?? allAssets.value.length
  } catch {
    allAssets.value = []
    assetsTotal.value = 0
  } finally {
    assetsLoading.value = false
  }
}

/** 设备清单：当前项目下所有 ALLOCATED 资产 */
const allocatedAssets = computed(() =>
  allAssets.value.filter((a) => a.status === 'ALLOCATED' || a.projectId === props.project?.id)
)

/** 发货设备明细：在途资产（IN_TRANSIT）或已出库但未交付的资产 */
const shippedAssets = computed(() =>
  allAssets.value.filter((a) => a.status === 'IN_TRANSIT')
)

const ASSET_STATUS_LABELS: Record<string, string> = {
  IN_STOCK: '在库',
  ALLOCATED: '已分配',
  IN_TRANSIT: '在途',
  SCRAPPED: '已报废'
}

function assetStatusLabel(s?: string): string {
  return s ? (ASSET_STATUS_LABELS[s] ?? s) : '-'
}

// ============ 项目成员简要 ============
const membersLoading = ref(false)
const members = ref<ProjectMember[]>([])

async function loadMembers() {
  if (!props.project?.id) return
  membersLoading.value = true
  try {
    members.value = (await listMembersByProjectId(props.project.id)) ?? []
  } catch {
    members.value = []
  } finally {
    membersLoading.value = false
  }
}

const MEMBER_ROLE_LABELS: Record<string, string> = {
  PROJECT_MANAGER: '项目经理',
  PROJECT_MEMBER: '项目成员',
  APPROVER: '审批人',
  VIEWER: '观察者',
  CUSTOMER: '客户'
}

function memberRoleLabel(r?: string): string {
  return r ? (MEMBER_ROLE_LABELS[r] ?? r) : '-'
}

// ============ 底部 Tab 控制 ============
/**
 * 仅展示有数据的 tab：主子项目（仅当 hasProjectHierarchy）/ 设备清单 / 发货设备明细 / 项目成员
 * 默认激活第一个可见 tab。
 */
const bottomTabVisible = computed(() => ({
  hierarchy: hasProjectHierarchy.value,
  equipment: allAssets.value.length > 0,
  shipment: shippedAssets.value.length > 0,
  member: members.value.length > 0
}))
const bottomTabEmpty = computed(() =>
  !Object.values(bottomTabVisible.value).some(Boolean)
)

const activeBottomTab = ref<'hierarchy' | 'equipment' | 'shipment' | 'member'>('hierarchy')
watch(bottomTabVisible, (v) => {
  // 切换默认 tab 到第一个可见项
  if (!v[activeBottomTab.value]) {
    if (v.hierarchy) activeBottomTab.value = 'hierarchy'
    else if (v.equipment) activeBottomTab.value = 'equipment'
    else if (v.shipment) activeBottomTab.value = 'shipment'
    else if (v.member) activeBottomTab.value = 'member'
  }
}, { immediate: true })

// ============ 生命周期 ============
onMounted(async () => {
  await Promise.all([loadTree(), loadAssets(), loadMembers()])
})

watch(
  () => props.project?.id,
  (newId) => {
    if (newId) {
      loadTree()
      loadAssets()
      loadMembers()
    }
  }
)
</script>

<template>
  <div class="project-overview">
    <!-- 基础信息（项目状态、进度已在 workspace-meta 栏展示，此处不再重复） -->
    <el-card shadow="hover" class="info-card">
      <template #header>基础信息</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目名称">{{ project.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目编码">{{ project.projectCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目状态">
          <ProjectStatusTag v-if="project.status" :status="project.status" size="small" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="项目类型">{{ typeLabel(project.projectType) }}</el-descriptions-item>
        <el-descriptions-item label="计划开始">{{ formatDate(project.planStartDate) }}</el-descriptions-item>
        <el-descriptions-item label="计划结束">{{ formatDate(project.planEndDate) }}</el-descriptions-item>
        <el-descriptions-item label="项目经理">{{ project.projectManagerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ project.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户联系人">{{ project.customerContact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ project.customerPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ project.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">
          {{ project.contractAmount != null ? `¥${project.contractAmount}` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="项目描述" :span="2">
          {{ project.description || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 底部标签页：主子项目 / 设备清单 / 发货设备明细 / 项目成员 -->
    <el-card shadow="never" class="bottom-tabs-card" v-loading="treeLoading || assetsLoading || membersLoading">
      <el-empty
        v-if="bottomTabEmpty && !treeLoading && !assetsLoading && !membersLoading"
        description="暂无关联数据（主子项目 / 设备 / 成员）"
        :image-size="80"
      />
      <el-tabs v-else v-model="activeBottomTab" class="bottom-tabs">
        <!-- ============== 主子项目 ============== -->
        <el-tab-pane
          v-if="bottomTabVisible.hierarchy"
          name="hierarchy"
          :label="`主子项目 (${childProjects.length + (parentProject ? 1 : 0)})`"
        >
          <div class="hierarchy-block">
            <!-- 父项目 -->
            <div class="hierarchy-section">
              <div class="hierarchy-title">父项目</div>
              <template v-if="parentProject">
                <el-link type="primary" @click="goProject(parentProject.id!)">
                  {{ parentProject.projectName }}
                </el-link>
                <span class="hierarchy-meta">
                  （{{ parentProject.projectCode || '-' }}）
                </span>
              </template>
              <span v-else class="hierarchy-empty">无（当前为主项目）</span>
            </div>
            <!-- 子项目列表 -->
            <div class="hierarchy-section">
              <div class="hierarchy-title">
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
                  <span class="hierarchy-meta">（{{ child.projectCode || '-' }}）</span>
                  <ProjectStatusTag
                    v-if="child.status"
                    :status="child.status"
                    size="small"
                  />
                  <span v-if="typeof child.progress === 'number'" class="hierarchy-progress">
                    {{ child.progress }}%
                  </span>
                </li>
              </ul>
            </div>
          </div>
        </el-tab-pane>

        <!-- ============== 设备清单 ============== -->
        <el-tab-pane
          v-if="bottomTabVisible.equipment"
          name="equipment"
          :label="`设备清单 (${allocatedAssets.length})`"
        >
          <el-table :data="allocatedAssets" border stripe size="small">
            <el-table-column prop="serialNo" label="序列号" min-width="140" />
            <el-table-column prop="name" label="设备名称" min-width="160" />
            <el-table-column prop="modelName" label="型号" min-width="120" />
            <el-table-column prop="categoryName" label="分类" min-width="100" />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag size="small" type="success">{{ assetStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warehouse" label="仓库" min-width="100" />
            <el-table-column prop="location" label="位置" min-width="120" />
            <el-table-column label="入库日期" width="110" align="center">
              <template #default="{ row }">{{ formatDate(row.inboundDate) }}</template>
            </el-table-column>
            <el-table-column prop="ownerName" label="责任人" width="100" />
            <template #empty>
              <el-empty description="当前项目暂无分配设备" :image-size="60" />
            </template>
          </el-table>
        </el-tab-pane>

        <!-- ============== 发货设备明细 ============== -->
        <el-tab-pane
          v-if="bottomTabVisible.shipment"
          name="shipment"
          :label="`发货设备明细 (${shippedAssets.length})`"
        >
          <el-table :data="shippedAssets" border stripe size="small">
            <el-table-column prop="serialNo" label="序列号" min-width="140" />
            <el-table-column prop="name" label="设备名称" min-width="160" />
            <el-table-column prop="modelName" label="型号" min-width="120" />
            <el-table-column label="发货状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag size="small" type="warning">{{ assetStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warehouse" label="始发仓库" min-width="100" />
            <el-table-column prop="location" label="当前位置" min-width="120" />
            <el-table-column label="入库日期" width="110" align="center">
              <template #default="{ row }">{{ formatDate(row.inboundDate) }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="160" />
            <template #empty>
              <el-empty description="当前项目暂无在途发货设备" :image-size="60" />
            </template>
          </el-table>
        </el-tab-pane>

        <!-- ============== 项目成员（简要） ============== -->
        <el-tab-pane
          v-if="bottomTabVisible.member"
          name="member"
          :label="`项目成员 (${members.length})`"
        >
          <el-table :data="members" border stripe size="small">
            <el-table-column prop="userId" label="用户 ID" width="100" align="center" />
            <el-table-column prop="userName" label="姓名" min-width="120" />
            <el-table-column label="角色" width="120" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ memberRoleLabel(row.role) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="加入日期" width="120" align="center">
              <template #default="{ row }">{{ formatDate(row.joinDate) }}</template>
            </el-table-column>
            <el-table-column label="离开日期" width="120" align="center">
              <template #default="{ row }">{{ formatDate(row.leaveDate) }}</template>
            </el-table-column>
            <template #empty>
              <el-empty description="当前项目暂无成员" :image-size="60" />
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.project-overview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.info-card :deep(.el-descriptions) {
  font-size: 13px;
}
.bottom-tabs-card {
  min-height: 200px;
}
.bottom-tabs :deep(.el-tabs__content) {
  padding-top: 8px;
}

/* ============ 主子项目 ============ */
.hierarchy-block {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.hierarchy-section {
  padding: 4px 0;
}
.hierarchy-title {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 8px;
}
.hierarchy-empty {
  color: var(--pms-color-text-placeholder);
  font-size: 13px;
}
.hierarchy-meta {
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
.hierarchy-progress {
  margin-left: auto;
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}
</style>
