<template>
  <div class="disease-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>病害防控管理</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 病害记录标签页 -->
        <el-tab-pane label="病害记录" name="record">
          <DiseaseRecord />
        </el-tab-pane>
        <!-- 药品采购标签页 -->
        <el-tab-pane label="药品采购" name="purchase">
          <MedicinePurchase />
        </el-tab-pane>

        <!-- 药品库存标签页 -->
        <el-tab-pane label="药品库存" name="inventory">
          <MedicineInventory />
        </el-tab-pane>

        <!-- 用药记录标签页 -->
        <el-tab-pane label="用药记录" name="medicine">
          <MedicineUsage />
        </el-tab-pane>

        <!-- 病害防治标签页 -->
        <el-tab-pane label="病害防治" name="prevention">
          <DiseasePrevention />
        </el-tab-pane>




      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DiseaseRecord from './DiseaseRecord.vue'
import DiseasePrevention from './DiseasePrevention.vue'
import MedicineUsage from './MedicineUsage.vue'
import MedicinePurchase from '../medicine/MedicinePurchase.vue'
import MedicineInventory from '../medicine/MedicineInventory.vue'

const activeTab = ref('record')
</script>

<style scoped>
.disease-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
