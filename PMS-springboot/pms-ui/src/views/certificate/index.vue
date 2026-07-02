<template>
  <div>
    <el-card>
      <template #header><span>合格证管理</span></template>
      <el-form :inline="true" :model="queryForm" label-width="80px" style="margin-bottom:16px">
        <el-form-item label="设备序列号">
          <el-input v-model="queryForm.barcode" placeholder="请输入设备序列号" clearable style="width:300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handlePrint">打印</el-button>
        </el-form-item>
      </el-form>

      <!-- OQC信息 -->
      <div v-if="oqcInfo" style="margin-bottom:16px">
        <el-divider content-position="left">OQC信息</el-divider>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="产品编码">{{ oqcInfo.itemCode }}</el-descriptions-item>
          <el-descriptions-item label="产品名称">{{ oqcInfo.itemName }}</el-descriptions-item>
          <el-descriptions-item label="序列号">{{ oqcInfo.serialNumber }}</el-descriptions-item>
          <el-descriptions-item label="检验日期">{{ oqcInfo.inspectionDate }}</el-descriptions-item>
          <el-descriptions-item label="检验结果"><el-tag :type="oqcInfo.result==='PASS'?'success':'danger'" size="small">{{ oqcInfo.result }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="检验员">{{ oqcInfo.inspector }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 印章信息 -->
      <div v-if="sealInfo" style="margin-bottom:16px">
        <el-divider content-position="left">印章信息</el-divider>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="印章类型">{{ sealInfo.sealType }}</el-descriptions-item>
          <el-descriptions-item label="印章编码">{{ sealInfo.sealCode }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 上传印章 -->
      <el-divider content-position="left">上传印章信息</el-divider>
      <el-form :inline="true" label-width="80px">
        <el-form-item label="印章文件">
          <el-upload :action="`/api/certificate/uploadSeal`" :headers="uploadHeaders" :on-success="handleSealUpload" :show-file-list="false">
            <el-button size="small">选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getOQCInfo, getSealInfo } from '@/api/certificate'
import { ElMessage } from 'element-plus'
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const queryForm = reactive({ barcode: '' })
const oqcInfo = ref(null)
const sealInfo = ref(null)
const handleQuery = async () => {
  if (!queryForm.barcode) { ElMessage.warning('请输入设备序列号'); return }
  try {
    const r = await getOQCInfo({ barcode: queryForm.barcode })
    oqcInfo.value = r.data || null
    const s = await getSealInfo({ barcode: queryForm.barcode })
    sealInfo.value = s.data || null
  } catch (e) { ElMessage.error('查询失败') }
}
const handlePrint = () => { window.print() }
const handleSealUpload = (res) => { if (res.code === 200) ElMessage.success('上传成功') }
</script>
