<template>
  <div class="feed-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>饲料管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 饲料采购标签页 -->
        <el-tab-pane label="饲料采购" name="purchase">
          <FeedPurchase />
        </el-tab-pane>
        
        <!-- 饲料库存标签页 -->
        <el-tab-pane label="饲料库存" name="inventory">
          <FeedInventory />
        </el-tab-pane>
        
        <!-- 饲料使用标签页 -->
        <el-tab-pane label="饲料使用" name="usage">
          <FeedUsage />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FeedPurchase from './FeedPurchase.vue'
import FeedInventory from './FeedInventory.vue'
import FeedUsage from './FeedUsage.vue'

const activeTab = ref('purchase')
</script>

<style scoped>
.feed-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

