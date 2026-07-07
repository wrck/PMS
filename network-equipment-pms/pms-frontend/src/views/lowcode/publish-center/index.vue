<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approvePublish,
  getPendingList,
  rejectPublish,
  rollbackPublish,
  type LowCodePublishRecord
} from '@/api/lowcode-publish'
import { useUserStore } from '@/stores/user'

defineOptions({ name: 'PublishCenterView' })

const userStore = useUserStore()
const pendingList = ref<LowCodePublishRecord[]>([])
const loading = ref(false)

async function loadPending() {
  loading.value = true
  try {
    pendingList.value = await getPendingList()
  } finally {
    loading.value = false
  }
}

async function approve(id: number) {
  try {
    await approvePublish(id, userStore.userInfo?.id || 0, userStore.userInfo?.username)
    ElMessage.success('审批通过')
    await loadPending()
  } catch {
    /* */
  }
}

async function reject(id: number) {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '审批拒绝', {
      confirmButtonText: '拒绝',
      cancelButtonText: '取消'
    })
    await rejectPublish(id, reason, userStore.userInfo?.id || 0, userStore.userInfo?.username)
    ElMessage.success('已拒绝')
    await loadPending()
  } catch {
    /* */
  }
}

async function rollback(id: number) {
  try {
    await ElMessageBox.confirm('确认回滚到该版本？', '提示', { type: 'warning' })
    await rollbackPublish(id, userStore.userInfo?.id || 0, userStore.userInfo?.username)
    ElMessage.success('回滚成功')
  } catch {
    /* */
  }
}

onMounted(loadPending)
</script>

<template>
  <div style="padding: 16px">
    <el-card shadow="never">
      <template #header>
        <span>发布审批中心</span>
      </template>
      <el-table v-loading="loading" :data="pendingList">
        <el-table-column label="配置类型" prop="configType" width="100" />
        <el-table-column label="配置 ID" prop="configId" width="80" />
        <el-table-column label="版本" prop="version" width="60" />
        <el-table-column label="申请人" prop="applicant" width="100" />
        <el-table-column label="变更说明" prop="changeLog" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="approve(row.id!)">通过</el-button>
            <el-button size="small" type="danger" @click="reject(row.id!)">拒绝</el-button>
            <el-button size="small" type="warning" :disabled="row.status !== 'PUBLISHED'" @click="rollback(row.id!)">
              回滚
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
