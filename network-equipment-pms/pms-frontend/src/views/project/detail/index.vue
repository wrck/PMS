<script setup lang="ts">
// =============================================================================
// ProjectDetail - 兼容重定向入口
// -----------------------------------------------------------------------------
// 旧路径 /project/detail/:id 重定向到新工作区 /project/workspace/:id。
// 工作区是新的项目枢纽页（8 Tab 整合），详见 workspace/index.vue。
// =============================================================================
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

defineOptions({ name: 'ProjectDetail' })

const route = useRoute()
const router = useRouter()

onMounted(() => {
  if (route.params.id) {
    router.replace(`/project/workspace/${route.params.id}`)
  }
})
</script>

<template>
  <div class="redirect-placeholder">正在跳转到项目工作区...</div>
</template>

<style scoped>
.redirect-placeholder {
  padding: 24px;
  color: var(--pms-color-text-secondary);
  text-align: center;
}
</style>
