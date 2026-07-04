<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { get, put } from '@/utils/request'

/** 通知项数据结构 */
interface Notification {
  id: number
  title: string
  content: string
  category: string
  bizType?: string
  bizId?: number
  /** 关联的业务路由（点击跳转） */
  bizUrl?: string
  readStatus: string
  createdAt: string
}

/** 分页返回结构 */
interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

const router = useRouter()

/** 未读数 */
const unreadCount = ref(0)
/** 最近通知列表 */
const recentList = ref<Notification[]>([])
/** 加载中 */
const loading = ref(false)
/** 轮询定时器 */
let timer: number | null = null

/** 拉取未读数 */
async function fetchUnread() {
  try {
    const count = await get<number>('/api/notification/unread/count')
    unreadCount.value = typeof count === 'number' ? count : 0
  } catch {
    // 静默失败，不打扰用户
  }
}

/** 拉取最近 5 条未读通知 */
async function fetchRecent() {
  loading.value = true
  try {
    const res = await get<PageResult<Notification>>('/api/notification/page', {
      page: 1,
      size: 5,
      readStatus: 'UNREAD'
    })
    recentList.value = res?.records || []
  } catch {
    recentList.value = []
  } finally {
    loading.value = false
  }
}

/** 标记单条为已读并跳转 */
async function markAsRead(item: Notification) {
  try {
    await put<void>(`/api/notification/${item.id}/read`)
    // 从列表移除
    const idx = recentList.value.findIndex((n) => n.id === item.id)
    if (idx !== -1) recentList.value.splice(idx, 1)
    if (unreadCount.value > 0) unreadCount.value--
  } catch {
    // 标记失败不阻塞跳转
  }
  if (item.bizUrl) {
    router.push(item.bizUrl)
  }
}

/** 跳转消息中心 */
function viewAll() {
  router.push('/notification')
}

/** 格式化时间 */
function formatTime(time: string): string {
  if (!time) return ''
  // 兼容各种格式，简单返回前 16 位
  return time.length >= 16 ? time.slice(0, 16).replace('T', ' ') : time
}

/** 分类标签颜色 */
function categoryType(category: string): 'success' | 'warning' | 'info' | 'danger' {
  switch (category) {
    case 'TASK':
    case 'WORKFLOW':
      return 'success'
    case 'WARN':
    case 'ALERT':
      return 'warning'
    case 'ERROR':
    case 'URGENT':
      return 'danger'
    default:
      return 'info'
  }
}

onMounted(() => {
  fetchUnread()
  fetchRecent()
  // 每 30 秒轮询未读数
  timer = window.setInterval(fetchUnread, 30000)
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<template>
  <el-popover
    placement="bottom-end"
    :width="360"
    trigger="click"
    popper-class="notification-popover"
  >
    <template #reference>
      <el-badge
        :value="unreadCount"
        :hidden="unreadCount === 0"
        :max="99"
        class="bell-badge"
      >
        <el-icon class="bell-icon" :size="20"><Bell /></el-icon>
      </el-badge>
    </template>

    <div class="notification-panel">
      <div class="panel-header">
        <span class="panel-title">通知</span>
        <span class="panel-count" v-if="unreadCount > 0">{{ unreadCount }} 条未读</span>
      </div>

      <div v-loading="loading" class="panel-body">
        <div v-if="!recentList.length && !loading" class="empty-tip">
          暂无未读通知
        </div>
        <div
          v-for="item in recentList"
          :key="item.id"
          class="notify-item"
          @click="markAsRead(item)"
        >
          <div class="notify-head">
            <el-tag size="small" :type="categoryType(item.category)" effect="plain">
              {{ item.category }}
            </el-tag>
            <span class="notify-time">{{ formatTime(item.createdAt) }}</span>
          </div>
          <div class="notify-title">{{ item.title }}</div>
          <div class="notify-content" v-if="item.content">{{ item.content }}</div>
        </div>
      </div>

      <div class="panel-footer" @click="viewAll">
        查看全部
      </div>
    </div>
  </el-popover>
</template>

<style scoped>
.bell-badge {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.bell-icon {
  color: #5a5e66;
  cursor: pointer;
}

.bell-icon:hover {
  color: #409eff;
}

.notification-panel {
  display: flex;
  flex-direction: column;
  max-height: 420px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 4px;
  border-bottom: 1px solid #ebeef5;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.panel-count {
  font-size: 12px;
  color: #f56c6c;
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

.empty-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 32px 0;
}

.notify-item {
  padding: 8px 4px;
  border-bottom: 1px solid #f2f6fc;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notify-item:hover {
  background-color: #f5f7fa;
}

.notify-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.notify-time {
  font-size: 12px;
  color: #c0c4cc;
}

.notify-title {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notify-content {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.panel-footer {
  text-align: center;
  padding: 10px 0;
  font-size: 13px;
  color: #409eff;
  cursor: pointer;
  border-top: 1px solid #ebeef5;
}

.panel-footer:hover {
  background-color: #ecf5ff;
}
</style>
