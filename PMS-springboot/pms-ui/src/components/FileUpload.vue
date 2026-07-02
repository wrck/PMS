<template>
  <el-upload :action="uploadUrl" :on-success="handleSuccess" :on-error="handleError"
    :before-upload="beforeUpload" :show-file-list="showFileList" :accept="accept"
    :headers="headers" :data="extraData">
    <slot></slot>
  </el-upload>
</template>
<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
const props = defineProps({
  action: { type: String, default: '/api/file/upload' },
  accept: { type: String, default: '*' },
  showFileList: { type: Boolean, default: false },
  extraData: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['success', 'error'])
const uploadUrl = props.action
const headers = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const beforeUpload = (file) => { if (file.size > 200 * 1024 * 1024) { ElMessage.error('文件不能超过200MB'); return false } return true }
const handleSuccess = (res) => { emit('success', res) }
const handleError = () => { ElMessage.error('上传失败'); emit('error') }
</script>
