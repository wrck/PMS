<template>
  <div class="order-container">
    <el-card>
      <template #header>设备清单</template>
      <el-table :data="orderList" stripe>
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column prop="itemCode" label="物料编码" />
        <el-table-column prop="itemName" label="物料名称" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="shipmentState" label="发货状态" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const orderList = ref([])
</script>
