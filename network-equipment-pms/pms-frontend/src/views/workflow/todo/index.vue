<script setup lang="ts">
// =============================================================================
// WorkflowTodo - 全局待办中心
// -----------------------------------------------------------------------------
// Task 6 Step 3：重做为基于 GlobalTodoCenter 的全局待办中心。
// 不传 projectId，显示当前用户在所有项目下的待办。
// 历史版本为基于 Activiti 的待办/已办/流程定义管理（已迁移到 Activiti designer
// 模块），本页面聚焦"聚合待办"职责。
// =============================================================================
import PageHeader from '@/components/common/PageHeader.vue'
import GlobalTodoCenter from '@/components/workflow/GlobalTodoCenter.vue'

defineOptions({ name: 'WorkflowTodo' })
</script>

<template>
  <div class="global-todo-page">
    <PageHeader
      title="全局待办中心"
      description="聚合所有项目的待办事项：审批 / 任务 / 阶段推进 / 基线告警"
    />
    <GlobalTodoCenter />
  </div>
</template>

<style scoped>
.global-todo-page {
  padding: 16px 24px;
}
</style>
