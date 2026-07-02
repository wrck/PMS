<template>
  <el-card>
    <template #header><span>审批意见</span></template>
    <el-form :model="form" label-width="80px">
      <el-form-item label="审批结果">
        <el-radio-group v-model="form.result">
          <el-radio label="agree">同意</el-radio>
          <el-radio label="reject">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审批意见">
        <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button @click="$emit('cancel')">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { reactive } from 'vue'
const emit = defineEmits(['submit', 'cancel'])
const form = reactive({ result: 'agree', comment: '' })
const handleSubmit = () => { emit('submit', { ...form }) }
</script>
