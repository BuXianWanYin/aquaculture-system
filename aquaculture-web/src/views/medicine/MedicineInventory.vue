<template>
  <div class="medicine-inventory">
    <div class="card-header">
      <span>药品库存管理</span>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="药品名称">
        <el-input v-model="searchForm.medicineName" placeholder="请输入药品名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item label="药品类型">
        <el-select v-model="searchForm.medicineType" placeholder="请选择类型" clearable style="width: 150px;">
          <el-option label="抗生素" value="抗生素" />
          <el-option label="消毒剂" value="消毒剂" />
          <el-option label="疫苗" value="疫苗" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="medicineName" label="药品名称" min-width="180" />
      <el-table-column prop="medicineType" label="药品类型" width="140" />
      <el-table-column prop="currentStock" label="当前库存" width="140">
        <template #default="{ row }">
          <span :style="{ color: row.currentStock < 100 ? '#f56c6c' : '#67c23a' }">
            {{ row.currentStock }} {{ row.unit || '克' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="unitPrice" label="单价(元/单位)" width="140" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('medicine:inventory:detail')" type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 库存详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="库存详情"
      width="900px"
      @close="handleDialogClose"
    >
      <el-form
        :model="inventoryForm"
        label-width="120px"
      >
        <el-form-item label="药品名称">
          <el-input v-model="inventoryForm.medicineName" disabled />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品类型">
              <el-input v-model="inventoryForm.medicineType" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前库存">
              <el-input-number v-model="inventoryForm.currentStock" :min="0" :precision="2" style="width: 100%;" disabled />
              <span style="margin-left: 10px;">{{ inventoryForm.unit || '克' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价(元/单位)">
              <el-input-number v-model="inventoryForm.unitPrice" :min="0" :precision="2" style="width: 100%;" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input v-model="inventoryForm.createTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <el-divider content-position="left">相关采购记录</el-divider>
      <el-table :data="purchaseList" border stripe style="width: 100%; margin-top: 10px;" max-height="300">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="purchaseDate" label="采购日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.purchaseDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="supplier" label="供应商" min-width="150" />
        <el-table-column prop="purchaseAmount" label="采购数量" width="140">
          <template #default="{ row }">
            {{ row.purchaseAmount }} {{ row.unit || '克' }}
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价(元/单位)" width="140" />
        <el-table-column prop="totalPrice" label="总价(元)" width="120" />
        <el-table-column prop="batchNumber" label="批次号" width="140" />
        <el-table-column prop="expiryDate" label="保质期至" width="120">
          <template #default="{ row }">
            {{ formatDate(row.expiryDate) }}
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMedicineInventoryList } from '@/api/medicineInventory'
import { getMedicinePurchasesByMedicine } from '@/api/medicinePurchase'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const inventoryFormRef = ref(null)
const purchaseList = ref([])

const searchForm = reactive({
  medicineName: '',
  medicineType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const inventoryForm = reactive({
  inventoryId: null,
  medicineName: '',
  medicineType: '',
  currentStock: 0,
  unit: '克',
  unitPrice: null,
  status: 1,
  creatorId: null,
  createTime: null
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMedicineInventoryList({
      current: pagination.current,
      size: pagination.size,
      medicineName: searchForm.medicineName || undefined,
      medicineType: searchForm.medicineType || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.medicineName = ''
  searchForm.medicineType = ''
  handleSearch()
}

const handleDetail = async (row) => {
  Object.assign(inventoryForm, {
    inventoryId: row.inventoryId,
    medicineName: row.medicineName,
    medicineType: row.medicineType,
    currentStock: row.currentStock,
    unit: row.unit || '克',
    unitPrice: row.unitPrice,
    status: row.status,
    creatorId: row.creatorId,
    createTime: row.createTime ? formatDateTime(row.createTime) : ''
  })
  
  // 加载相关采购记录
  try {
    const res = await getMedicinePurchasesByMedicine(row.medicineName, row.medicineType)
    if (res.code === 200) {
      purchaseList.value = res.data || []
    } else {
      purchaseList.value = []
    }
  } catch (error) {
    console.error('加载采购记录失败', error)
    purchaseList.value = []
  }
  
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(inventoryForm, {
    inventoryId: null,
    medicineName: '',
    medicineType: '',
    currentStock: 0,
    unit: '克',
    unitPrice: null,
    status: 1,
    creatorId: null,
    createTime: null
  })
  purchaseList.value = []
}

const handleDialogClose = () => {
  resetForm()
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

onMounted(() => {
  loadData()
  // 监听库存刷新事件
  window.addEventListener('refresh-medicine-inventory', loadData)
})

onUnmounted(() => {
  // 组件卸载时移除事件监听
  window.removeEventListener('refresh-medicine-inventory', loadData)
})
</script>

<style scoped>
.medicine-inventory {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

