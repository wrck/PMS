<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { clearAllCache, clearCache, getCacheNames } from '@/api/system-cache'

defineOptions({ name: 'SystemCache' })

const loading = ref(false)
const clearing = ref(false)
const clearingName = ref<string>('')
const cacheNames = ref<string[]>([])

async function loadNames() {
  loading.value = true
  try {
    const res = await getCacheNames()
    cacheNames.value = Array.isArray(res) ? res : []
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function handleClear(name: string) {
  try {
    await ElMessageBox.confirm(`确定清除缓存「${name}」吗？`, '提示', { type: 'warning' })
  } catch {
    return // 用户取消
  }
  clearingName.value = name
  try {
    await clearCache(name)
    ElMessage.success(`缓存「${name}」已清除`)
    loadNames()
  } catch {
    /* handled by interceptor */
  } finally {
    clearingName.value = ''
  }
}

async function handleClearAll() {
  try {
    await ElMessageBox.confirm('确定清除全部缓存吗？该操作可能影响系统性能。', '危险操作', {
      type: 'error',
      confirmButtonText: '确定清除',
      cancelButtonText: '取消'
    })
  } catch {
    return // 用户取消
  }
  clearing.value = true
  try {
    await clearAllCache()
    ElMessage.success('全部缓存已清除')
    loadNames()
  } catch {
    /* handled by interceptor */
  } finally {
    clearing.value = false
  }
}

onMounted(() => {
  loadNames()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">缓存管理</span>
      <el-button
        type="danger"
        :icon="'Delete'"
        :loading="clearing"
        :disabled="cacheNames.length === 0"
        @click="handleClearAll"
      >
        清除全部缓存
      </el-button>
    </div>

    <el-card shadow="never" v-loading="loading">
      <div v-if="cacheNames.length === 0 && !loading" class="empty-wrap">
        <el-empty description="暂无缓存数据" />
      </div>
      <el-row v-else :gutter="16">
        <el-col
          v-for="name in cacheNames"
          :key="name"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card shadow="hover" class="cache-card">
            <div class="cache-card-body">
              <div class="cache-name" :title="name">
                <el-icon class="cache-icon"><Coin /></el-icon>
                <span>{{ name }}</span>
              </div>
              <el-button
                type="primary"
                plain
                size="small"
                :loading="clearingName === name"
                @click="handleClear(name)"
              >
                清除
              </el-button>
            </div>
          </el-card>
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
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.empty-wrap {
  padding: 40px 0;
}
.cache-card {
  margin-bottom: 16px;
}
.cache-card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.cache-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.cache-icon {
  color: #e6a23c;
  flex-shrink: 0;
}
</style>
