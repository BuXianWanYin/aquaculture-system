<template>
  <div class="medicine-purchase">
    <div class="card-header">
      <span>药品采购管理</span>
      <el-button v-if="hasPermission('medicine:purchase:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增采购
      </el-button>
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
      <el-form-item label="供应商">
        <el-input v-model="searchForm.supplier" placeholder="请输入供应商" clearable style="width: 180px;" />
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
      <el-table-column prop="supplier" label="供应商" min-width="150" />
      <el-table-column prop="purchaseAmount" label="采购数量" width="140">
        <template #default="{ row }">
          {{ row.purchaseAmount }} {{ row.unit || '克' }}
        </template>
      </el-table-column>
      <el-table-column prop="unitPrice" label="单价(元/单位)" width="140" />
      <el-table-column prop="totalPrice" label="总价(元)" width="120" />
      <el-table-column prop="purchaseDate" label="采购日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.purchaseDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="batchNumber" label="批次号" width="140" />
      <el-table-column prop="expiryDate" label="保质期至" width="120">
        <template #default="{ row }">
          {{ formatDate(row.expiryDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('medicine:purchase:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('medicine:purchase:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 采购表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="purchaseFormRef"
        :model="purchaseForm"
        :rules="getPurchaseRules()"
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="采购模式" v-if="!isEdit">
          <el-radio-group v-model="purchaseMode" @change="handlePurchaseModeChange">
            <el-radio label="new">新采购</el-radio>
            <el-radio label="existing">已有采购</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item 
          v-if="purchaseMode === 'existing' && !isEdit" 
          label="选择已有采购" 
          prop="existingPurchaseId"
        >
          <el-select 
            v-model="purchaseForm.existingPurchaseId" 
            placeholder="请选择已有采购" 
            style="width: 100%;" 
            filterable
            @change="handleExistingPurchaseChange"
          >
            <el-option 
              v-for="purchase in existingPurchaseList" 
              :key="purchase.purchaseId" 
              :label="`${purchase.medicineName} - ${purchase.supplier} - ${purchase.purchaseDate}`" 
              :value="purchase.purchaseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="药品名称" prop="medicineName">
          <el-input v-model="purchaseForm.medicineName" placeholder="请输入药品名称" :disabled="purchaseMode === 'existing' && !isEdit" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品类型" prop="medicineType">
              <el-select 
                v-model="purchaseForm.medicineType" 
                placeholder="请选择类型" 
                style="width: 100%;"
                :disabled="purchaseMode === 'existing' && !isEdit"
              >
                <el-option label="抗生素" value="抗生素" />
                <el-option label="消毒剂" value="消毒剂" />
                <el-option label="疫苗" value="疫苗" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input 
                v-model="purchaseForm.supplier" 
                placeholder="请输入供应商" 
                :disabled="purchaseMode === 'existing' && !isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购数量" prop="purchaseAmount">
              <el-input-number v-model="purchaseForm.purchaseAmount" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-select v-model="purchaseForm.unit" placeholder="请选择单位" style="width: 100%;">
                <el-option label="克" value="克" />
                <el-option label="千克" value="千克" />
                <el-option label="毫升" value="毫升" />
                <el-option label="升" value="升" />
                <el-option label="瓶" value="瓶" />
                <el-option label="盒" value="盒" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价(元/单位)" prop="unitPrice">
              <el-input-number v-model="purchaseForm.unitPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker 
                v-model="purchaseForm.purchaseDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次号">
              <el-input v-model="purchaseForm.batchNumber" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保质期至">
              <el-date-picker 
                v-model="purchaseForm.expiryDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
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
import { getMedicinePurchaseList, saveMedicinePurchase, updateMedicinePurchase, deleteMedicinePurchase, getAllMedicinePurchases } from '@/api/medicinePurchase'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增采购')
const isEdit = ref(false)
const purchaseFormRef = ref(null)
const purchaseMode = ref('new') // 'new' 新采购, 'existing' 已有采购
const existingPurchaseList = ref([])

const searchForm = reactive({
  medicineName: '',
  medicineType: '',
  supplier: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const purchaseForm = reactive({
  purchaseId: null,
  existingPurchaseId: null,
  medicineName: '',
  medicineType: '',
  supplier: '',
  purchaseAmount: null,
  unit: '克',
  unitPrice: null,
  totalPrice: null,
  purchaseDate: null,
  batchNumber: '',
  expiryDate: null,
  status: 1,
  creatorId: null
})

const getPurchaseRules = () => {
  const rules = {
    medicineName: [
      { required: true, message: '请输入药品名称', trigger: 'blur' }
    ],
    medicineType: [
      { required: true, message: '请选择药品类型', trigger: 'change' }
    ],
    purchaseAmount: [
      { required: true, message: '请输入采购数量', trigger: 'blur' }
    ],
    unitPrice: [
      { required: true, message: '请输入单价', trigger: 'blur' }
    ],
    purchaseDate: [
      { required: true, message: '请选择采购日期', trigger: 'change' }
    ]
  }
  
  if (purchaseMode.value === 'existing' && !isEdit.value) {
    rules.existingPurchaseId = [
      { required: true, message: '请选择已有采购', trigger: 'change' }
    ]
  }
  
  return rules
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMedicinePurchaseList({
      current: pagination.current,
      size: pagination.size,
      medicineName: searchForm.medicineName || undefined,
      medicineType: searchForm.medicineType || undefined,
      supplier: searchForm.supplier || undefined
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
  searchForm.supplier = ''
  handleSearch()
}

const loadExistingPurchaseList = async () => {
  try {
    const res = await getAllMedicinePurchases()
    if (res.code === 200) {
      existingPurchaseList.value = res.data || []
    }
  } catch (error) {
    console.error('加载已有采购列表失败', error)
  }
}

// 处理采购模式变化
const handlePurchaseModeChange = (mode) => {
  if (mode === 'existing') {
    purchaseForm.existingPurchaseId = null
    purchaseForm.medicineName = ''
    purchaseForm.medicineType = ''
    purchaseForm.supplier = ''
    purchaseForm.unitPrice = null
    purchaseForm.batchNumber = ''
    purchaseForm.expiryDate = null
  } else {
    purchaseForm.existingPurchaseId = null
  }
}

// 处理已有采购选择变化
const handleExistingPurchaseChange = (purchaseId) => {
  if (purchaseId) {
    const purchase = existingPurchaseList.value.find(p => p.purchaseId === purchaseId)
    if (purchase) {
      purchaseForm.medicineName = purchase.medicineName
      purchaseForm.medicineType = purchase.medicineType
      purchaseForm.supplier = purchase.supplier
      purchaseForm.unitPrice = purchase.unitPrice
      purchaseForm.unit = purchase.unit || '克'
      purchaseForm.batchNumber = purchase.batchNumber || ''
      purchaseForm.expiryDate = purchase.expiryDate
    }
  }
}

const handleAdd = () => {
  isEdit.value = false
  purchaseMode.value = 'new'
  dialogTitle.value = '新增采购'
  resetForm()
  dialogVisible.value = true
  setTimeout(() => {
    purchaseFormRef.value?.clearValidate()
  }, 100)
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑采购'
  Object.assign(purchaseForm, {
    purchaseId: row.purchaseId,
    medicineName: row.medicineName,
    medicineType: row.medicineType,
    supplier: row.supplier,
    purchaseAmount: row.purchaseAmount,
    unit: row.unit || '克',
    unitPrice: row.unitPrice,
    totalPrice: row.totalPrice,
    purchaseDate: row.purchaseDate,
    batchNumber: row.batchNumber || '',
    expiryDate: row.expiryDate,
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
  setTimeout(() => {
    purchaseFormRef.value?.clearValidate()
  }, 100)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该采购记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteMedicinePurchase(row.purchaseId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      // 刷新库存页面
      window.dispatchEvent(new CustomEvent('refresh-medicine-inventory'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!purchaseFormRef.value) return
  await purchaseFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateMedicinePurchase(purchaseForm)
        } else {
          res = await saveMedicinePurchase(purchaseForm)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
          // 刷新库存页面
          window.dispatchEvent(new CustomEvent('refresh-medicine-inventory'))
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(purchaseForm, {
    purchaseId: null,
    existingPurchaseId: null,
    medicineName: '',
    medicineType: '',
    supplier: '',
    purchaseAmount: null,
    unit: '克',
    unitPrice: null,
    totalPrice: null,
    purchaseDate: null,
    batchNumber: '',
    expiryDate: null,
    status: 1,
    creatorId: null
  })
  purchaseFormRef.value?.clearValidate()
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
  loadExistingPurchaseList()
})
</script>

<style scoped>
.medicine-purchase {
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

