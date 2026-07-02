<template>
  <div>
    <el-card>
      <template #header><span>合格证详情</span></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="设备序列号">{{ data.serialNumber }}</el-descriptions-item>
        <el-descriptions-item label="产品编码">{{ data.itemCode }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ data.itemName }}</el-descriptions-item>
        <el-descriptions-item label="生产日期">{{ data.productionDate }}</el-descriptions-item>
        <el-descriptions-item label="检验日期">{{ data.inspectionDate }}</el-descriptions-item>
        <el-descriptions-item label="检验结果"><el-tag :type="data.inspectionResult==='PASS'?'success':'danger'" size="small">{{ data.inspectionResult }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="检验员">{{ data.inspector }}</el-descriptions-item>
        <el-descriptions-item label="合格证编号">{{ data.certificateNo }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 打印预览 -->
    <el-card style="margin-top:16px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>合格证预览</span>
          <el-button type="primary" @click="handlePrint">打印</el-button>
        </div>
      </template>
      <div ref="printRef" style="padding:20px;border:1px solid #eee;min-height:400px">
        <h2 style="text-align:center">产品合格证</h2>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="产品名称">{{ data.itemName }}</el-descriptions-item>
          <el-descriptions-item label="产品型号">{{ data.itemCode }}</el-descriptions-item>
          <el-descriptions-item label="序列号">{{ data.serialNumber }}</el-descriptions-item>
          <el-descriptions-item label="生产日期">{{ data.productionDate }}</el-descriptions-item>
          <el-descriptions-item label="检验结论">{{ data.inspectionResult }}</el-descriptions-item>
          <el-descriptions-item label="检验员">{{ data.inspector }}</el-descriptions-item>
          <el-descriptions-item label="合格证编号">{{ data.certificateNo }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCertificate } from '@/api/certificate'
const route = useRoute()
const id = route.params.id
const data = ref({})
const printRef = ref(null)
const handlePrint = () => { window.print() }
onMounted(async () => { const r = await getCertificate(id); data.value = r.data || {} })
</script>
