<script setup lang="ts">
// Placeholder view: 项目列表
// TODO: 接入 getProjectPage 实现项目列表查询、新增、编辑、删除与审批
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const tableData = ref<unknown[]>([])
const keyword = ref('')

function handleAdd() {
  // TODO: open project create dialog
}

function viewDetail(id: number) {
  router.push(`/project/detail/${id}`)
}
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目名称">
          <el-input v-model="keyword" placeholder="项目名称/编号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'">查询</el-button>
          <el-button :icon="'Refresh'">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增项目</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="code" label="项目编号" min-width="140" />
        <el-table-column prop="name" label="项目名称" min-width="200" />
        <el-table-column prop="customerName" label="客户" min-width="160" />
        <el-table-column prop="managerName" label="项目经理" min-width="120" />
        <el-table-column prop="status" label="状态" width="110" />
        <el-table-column prop="startDate" label="开始日期" min-width="120" />
        <el-table-column prop="endDate" label="结束日期" min-width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default>
            <el-button link type="primary" @click="viewDetail(1)">详情</el-button>
            <el-button link type="primary" disabled>编辑</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据（占位页面，待接入接口）" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.toolbar {
  margin-bottom: 12px;
}
</style>
