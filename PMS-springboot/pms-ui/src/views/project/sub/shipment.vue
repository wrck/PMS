<template>
  <div class="shipment-container">
    <el-card>
      <template #header>发货信息</template>
      <el-table :data="shipmentList" stripe>
        <el-table-column prop="serialNumber" label="序列号" />
        <el-table-column prop="itemCode" label="物料编码" />
        <el-table-column prop="itemName" label="物料名称" />
        <el-table-column prop="shipmentDate" label="发货日期" />
        <el-table-column prop="installAddress" label="安装地址" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const shipmentList = ref([])
</script>
