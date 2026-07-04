<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUnreadCount,
  listNotifications,
  markAllAsRead,
  markAsRead,
  type Notification,
  type NotificationCategory
} from '@/api/notification'

const router = useRouter()

const loading = ref(false)
const tableData = ref<Notification[]>([])
const total = ref(0)
const unreadCount = ref(0)
const activeMenu = ref('all')

const query = reactive<{ page: number; size: number; category?: string; readStatus?: string }>({
  page: 1,
  size: 10,
  category: undefined,
  readStatus: undefined
})

// 侧边栏菜单项
interface MenuItem {
  index: string
  label: string
  category?: NotificationCategory
  readStatus?: string
}

const menuItems: MenuItem[] = [
  { index: 'all', label: '全部' },
  { index: 'unread', label: '未读', readStatus: 'UNREAD' },
  { index: 'MILESTONE', label: '里程碑', category: 'MILESTONE' },
  { index: 'TASK', label: '任务', category: 'TASK' },
  { index: 'APPROVAL', label: '审批', category: 'APPROVAL' },
  { index: 'PUNCH_LIST', label: 'Punch List', category: 'PUNCH_LIST' },
  { index: 'WARRANTY', label: '质保', category: 'WARRANTY' },
  { index: 'RMA', label: 'RMA', category: 'RMA' },
  { index: 'SETTLEMENT', label: '结算', category: 'SETTLEMENT' }
]

// 分类标签文案与颜色
const categoryMeta: Record<string, { label: string; tagType: any }> = {
  MILESTONE: { label: '里程碑', tagType: 'primary' },
  TASK: { label: '任务', tagType: 'warning' },
  APPROVAL: { label: '审批', tagType: 'danger' },
  PUNCH_LIST: { label: 'Punch List', tagType: 'info' },
  WARRANTY: { label: '质保', tagType: 'success' },
  RMA: { label: 'RMA', tagType: 'danger' },
  SETTLEMENT: { label: '结算', tagType: 'info' }
}

function getCategoryMeta(category?: string) {
  return (categoryMeta as Record<string, { label: string; tagType: any }>)[category ?? ''] ?? {
    label: category ?? '-',
    tagType: 'info'
  }
}

// 时间格式化：去 T 并截取到秒
function formatDateTime(val?: string): string {
  return val?.replace('T', ' ').slice(0, 19) ?? '-'
}

// ============== 数据加载 ==============
async function loadData() {
  loading.value = true
  try {
    const params: { page: number; size: number; category?: string; readStatus?: string } = {
      page: query.page,
      size: query.size
    }
    if (query.category) params.category = query.category
    if (query.readStatus) params.readStatus = query.readStatus
    const res = await listNotifications(params)
    tableData.value = res.records ?? []
    total.value = res.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  try {
    unreadCount.value = await getUnreadCount()
  } catch {
    /* handled by interceptor */
  }
}

// 菜单切换
function handleMenuSelect(index: string) {
  activeMenu.value = index
  const item = menuItems.find((m) => m.index === index)
  query.category = item?.category
  query.readStatus = item?.readStatus
  query.page = 1
  loadData()
}

function handlePageChange(p: number) {
  query.page = p
  loadData()
}

function handleSizeChange(s: number) {
  query.size = s
  query.page = 1
  loadData()
}

// ============== 全部已读 ==============
function handleMarkAllRead() {
  ElMessageBox.confirm('确认将所有未读通知标记为已读吗？', '全部已读', { type: 'warning' })
    .then(async () => {
      await markAllAsRead()
      ElMessage.success('已全部标记为已读')
      loadData()
      loadUnreadCount()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============== 单条查看：标记已读 + 跳转 ==============
async function handleView(row: Notification) {
  // 未读则先标记已读
  if (row.readStatus === 'UNREAD') {
    try {
      await markAsRead(row.id)
      row.readStatus = 'READ'
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch {
      /* handled by interceptor */
    }
  }
  jumpToBiz(row)
}

// 根据 bizType + bizId 跳转业务详情
function jumpToBiz(notif: Notification) {
  if (!notif.bizType || !notif.bizId) return
  const routeMap: Record<string, string> = {
    PROJECT: `/project/detail/${notif.bizId}`,
    PUNCH_LIST: `/punch-list`,
    RMA: `/rma`,
    WARRANTY: `/warranty`,
    DELIVERABLE: `/deliverable`,
    MILESTONE: `/project/detail/${notif.bizId}`
  }
  const path = routeMap[notif.bizType]
  if (path) {
    router.push(path)
  }
}

onMounted(() => {
  loadUnreadCount()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">消息中心</span>
          <el-button type="primary" :icon="'Check'" @click="handleMarkAllRead">全部已读</el-button>
        </div>
      </template>

      <el-row :gutter="16">
        <!-- 左侧分类侧栏 -->
        <el-col :span="4">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item
              v-for="item in menuItems"
              :key="item.index"
              :index="item.index"
            >
              <span>{{ item.label }}</span>
              <el-badge
                v-if="item.index === 'unread' && unreadCount > 0"
                :value="unreadCount"
                class="menu-badge"
              />
            </el-menu-item>
          </el-menu>
        </el-col>

        <!-- 右侧通知列表 -->
        <el-col :span="20">
          <el-table v-loading="loading" :data="tableData" border stripe>
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="content" label="内容预览" min-width="240" show-overflow-tooltip />
            <el-table-column label="分类" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getCategoryMeta(row.category).tagType" size="small">
                  {{ getCategoryMeta(row.category).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="已读状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.readStatus === 'UNREAD' ? 'danger' : 'info'" size="small">
                  {{ row.readStatus === 'UNREAD' ? '未读' : '已读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="160" align="center">
              <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleView(row)">查看</el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无通知" />
            </template>
          </el-table>

          <el-pagination
            class="pagination"
            background
            :current-page="query.page"
            :page-size="query.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.menu-badge {
  margin-left: 8px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
