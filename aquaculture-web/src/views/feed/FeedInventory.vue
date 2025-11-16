<template>
  <div class="feed-inventory">
    <div class="card-header">
      <span>饲料库存管理</span>
      <el-button v-if="hasPermission('feed:inventory:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增库存
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="饲料名称">
        <el-input v-model="searchForm.feedName" placeholder="请输入饲料名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item label="饲料类型">
        <el-select v-model="searchForm.feedType" placeholder="请选择类型" clearable style="width: 150px;">
          <el-option label="对虾专用饲料" value="对虾专用饲料" />
          <el-option label="淡水鱼饲料" value="淡水鱼饲料" />
          <el-option label="通用饲料" value="通用饲料" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="feedName" label="饲料名称" min-width="180" />
      <el-table-column prop="feedType" label="饲料类型" width="140" />
      <el-table-column prop="currentStock" label="当前库存(kg)" width="140">
        <template #default="{ row }">
          <span :style="{ color: row.currentStock < 1000 ? '#f56c6c' : '#67c23a' }">
            {{ row.currentStock }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="unitPrice" label="单价(元/kg)" width="120" />
      <el-table-column prop="batchNumber" label="批次号" width="140" />
      <el-table-column prop="expiryDate" label="保质期至" width="120">
        <template #default="{ row }">
          {{ formatDate(row.expiryDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="warehouseLocation" label="仓储位置" width="150" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('feed:inventory:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('feed:inventory:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 库存表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="inventoryFormRef"
        :model="inventoryForm"
        :rules="inventoryRules"
        label-width="120px"
      >
        <el-form-item label="饲料名称" prop="feedName">
          <el-input v-model="inventoryForm.feedName" placeholder="请输入饲料名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="饲料类型" prop="feedType">
              <el-select v-model="inventoryForm.feedType" placeholder="请选择类型" style="width: 100%;">
                <el-option label="对虾专用饲料" value="对虾专用饲料" />
                <el-option label="淡水鱼饲料" value="淡水鱼饲料" />
                <el-option label="通用饲料" value="通用饲料" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前库存(kg)" prop="currentStock">
              <el-input-number v-model="inventoryForm.currentStock" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价(元/kg)">
              <el-input-number v-model="inventoryForm.unitPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保质期至">
              <el-date-picker 
                v-model="inventoryForm.expiryDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="批次号">
          <el-input v-model="inventoryForm.batchNumber" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="仓储位置">
          <el-input v-model="inventoryForm.warehouseLocation" placeholder="请输入仓储位置" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getFeedInventoryList, saveFeedInventory, updateFeedInventory, deleteFeedInventory } from '@/api/feedInventory'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增库存')
const isEdit = ref(false)
const inventoryFormRef = ref(null)

const searchForm = reactive({
  feedName: '',
  feedType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const inventoryForm = reactive({
  inventoryId: null,
  feedName: '',
  feedType: '',
  currentStock: 0,
  unitPrice: null,
  batchNumber: '',
  expiryDate: null,
  warehouseLocation: '',
  status: 1,
  creatorId: null
})

const inventoryRules = {
  feedName: [
    { required: true, message: '请输入饲料名称', trigger: 'blur' }
  ],
  feedType: [
    { required: true, message: '请选择饲料类型', trigger: 'change' }
  ],
  currentStock: [
    { required: true, message: '请输入当前库存', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeedInventoryList({
      current: pagination.current,
      size: pagination.size,
      feedName: searchForm.feedName || undefined,
      feedType: searchForm.feedType || undefined
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
  searchForm.feedName = ''
  searchForm.feedType = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增库存'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑库存'
  Object.assign(inventoryForm, {
    inventoryId: row.inventoryId,
    feedName: row.feedName,
    feedType: row.feedType,
    currentStock: row.currentStock,
    unitPrice: row.unitPrice,
    batchNumber: row.batchNumber || '',
    expiryDate: row.expiryDate,
    warehouseLocation: row.warehouseLocation || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该库存记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteFeedInventory(row.inventoryId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!inventoryFormRef.value) return
  await inventoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateFeedInventory(inventoryForm)
        } else {
          res = await saveFeedInventory(inventoryForm)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(inventoryForm, {
    inventoryId: null,
    feedName: '',
    feedType: '',
    currentStock: 0,
    unitPrice: null,
    batchNumber: '',
    expiryDate: null,
    warehouseLocation: '',
    status: 1,
    creatorId: null
  })
  inventoryFormRef.value?.clearValidate()
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
})
</script>

<style scoped>
.feed-inventory {
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

