<template>
  <div class="sales-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 客户管理标签页 -->
        <el-tab-pane label="客户管理" name="customer">
          <Customer />
        </el-tab-pane>
        
        <!-- 销售记录标签页 -->
        <el-tab-pane label="销售记录" name="record">
          <SalesRecord />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Customer from './Customer.vue'
import SalesRecord from './SalesRecord.vue'

const activeTab = ref('customer')
</script>

<style scoped>
.sales-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

