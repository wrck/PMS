<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>项目总数</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>进行中</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>待办任务</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>本月完成</template>
          <div class="stat-value">--</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top: 20px">
      <template #header>欢迎使用 PMS 项目管理系统</template>
      <p>系统正在重构中，更多功能将陆续上线。</p>
    </el-card>
  </div>
</template>

<style scoped>
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  padding: 20px 0;
}
</style>
