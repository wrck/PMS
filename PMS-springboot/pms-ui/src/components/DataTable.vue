<template>
  <div>
    <el-table :data="data" v-loading="loading" stripe border @selection-change="handleSelectionChange">
      <el-table-column v-if="showSelection" type="selection" width="55" />
      <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label"
        :width="col.width" :sortable="col.sortable" :show-overflow-tooltip="true">
        <template v-if="col.slot" #default="scope"><slot :name="col.prop" v-bind="scope" /></template>
      </el-table-column>
      <el-table-column v-if="$slots.action" label="操作" :width="actionWidth" fixed="right">
        <template #default="scope"><slot name="action" v-bind="scope" /></template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="showPagination" style="margin-top: 16px; text-align: right"
      :current-page="pageNum" :page-size="pageSize" :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="$emit('page-change', $event)"
      @size-change="$emit('size-change', $event)" />
  </div>
</template>
<script setup>
defineProps({
  data: { type: Array, default: () => [] },
  columns: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  showSelection: { type: Boolean, default: false },
  showPagination: { type: Boolean, default: true },
  pageNum: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  total: { type: Number, default: 0 },
  actionWidth: { type: String, default: '200' }
})
const emit = defineEmits(['page-change', 'size-change', 'selection-change'])
const handleSelectionChange = (val) => { emit('selection-change', val) }
</script>
