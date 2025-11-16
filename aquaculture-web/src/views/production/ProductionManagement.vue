<template>
  <div class="production-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>日常生产记录管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 投喂记录标签页 -->
        <el-tab-pane label="投喂记录" name="feeding">
          <FeedingRecord />
        </el-tab-pane>
        
        <!-- 日常检查标签页 -->
        <el-tab-pane label="日常检查" name="inspection">
          <DailyInspection />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FeedingRecord from './FeedingRecord.vue'
import DailyInspection from './DailyInspection.vue'

const activeTab = ref('feeding')
</script>

<style scoped>
.production-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

