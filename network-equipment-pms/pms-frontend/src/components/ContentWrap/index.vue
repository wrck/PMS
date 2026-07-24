<script lang="ts" setup>
/**
 * yudao ContentWrap 组件适配层。
 *
 * <p>包装 ElCard，提供标题栏 + 内容区域的统一布局。
 * yudao 原生页面大量使用此组件作为页面容器。</p>
 *
 * <p>用法：
 * {@code <ContentWrap title="部门管理"><el-table ... /></ContentWrap>}</p>
 */
defineOptions({ name: 'ContentWrap' })

withDefaults(
  defineProps<{
    /** 标题 */
    title?: string
    /** 标题旁边的提示信息（鼠标悬浮显示） */
    message?: string
    /** 内容区域样式 */
    bodyStyle?: Record<string, string>
  }>(),
  {
    title: '',
    message: '',
    bodyStyle: () => ({ padding: '15px', overflow: 'hidden' })
  }
)
</script>

<template>
  <el-card :body-style="bodyStyle" class="content-wrap" shadow="never">
    <template v-if="title" #header>
      <div class="content-wrap__header">
        <span class="content-wrap__title">{{ title }}</span>
        <el-tooltip v-if="message" effect="dark" placement="right">
          <template #content>
            <div style="max-width: 200px">{{ message }}</div>
          </template>
          <el-icon class="content-wrap__tip"><QuestionFilled /></el-icon>
        </el-tooltip>
        <div class="content-wrap__extra">
          <slot name="header"></slot>
        </div>
      </div>
    </template>
    <slot></slot>
  </el-card>
</template>

<style scoped>
.content-wrap {
  margin-bottom: 15px;
}
.content-wrap__header {
  display: flex;
  align-items: center;
}
.content-wrap__title {
  font-size: 16px;
  font-weight: 700;
}
.content-wrap__tip {
  margin-left: 5px;
  font-size: 14px;
}
.content-wrap__extra {
  flex: 1;
  padding-left: 20px;
}
</style>
