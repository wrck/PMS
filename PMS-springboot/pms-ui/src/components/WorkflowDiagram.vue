<template>
  <div class="workflow-diagram">
    <img v-if="imageUrl" :src="imageUrl" style="max-width: 100%" />
    <el-empty v-else description="暂无流程图" />
  </div>
</template>
<script setup>
import { ref, watch } from 'vue'
import { viewImage, viewCurrentImage } from '@/api/workflow'
const props = defineProps({ deploymentId: String, processInstanceId: String })
const imageUrl = ref('')
watch(() => [props.deploymentId, props.processInstanceId], async () => {
  try {
    let res
    if (props.processInstanceId) res = await viewCurrentImage(props.processInstanceId)
    else if (props.deploymentId) res = await viewImage(props.deploymentId)
    if (res) imageUrl.value = URL.createObjectURL(res)
  } catch (e) { console.error(e) }
}, { immediate: true })
</script>
