<script setup lang="ts">
// 简单流程模型设计器占位页
// 该功能集成在「流程模型」编辑器中，当前仅提供入口提示
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>简单流程模型设计器</template>
      <el-result icon="info" title="简单流程模型设计器" sub-title="此功能集成在「流程模型」编辑器中，请在流程模型页面打开模型后进行设计。">
        <template #extra>
          <el-text type="info">
            通过简单拖拽节点即可完成流程编排，支持发起人节点、审批节点、抄送节点、条件分支、并行分支等。
          </el-text>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
