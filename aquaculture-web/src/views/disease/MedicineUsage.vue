<template>
  <div class="medicine-usage">
    <div class="card-header">
      <span>用药记录管理</span>
      <el-button v-if="hasPermission('disease:medicine:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增用药记录
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="病害记录">
        <el-select v-model="searchForm.recordId" placeholder="请选择病害记录" clearable filterable style="width: 180px;">
          <el-option 
            v-for="record in diseaseRecordList" 
            :key="record.recordId" 
            :label="`${record.diseaseName} (${formatDate(record.occurrenceDate)})`" 
            :value="record.recordId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="养殖计划">
        <el-select v-model="searchForm.planId" placeholder="请选择计划" clearable filterable style="width: 180px;">
          <el-option 
            v-for="plan in planList" 
            :key="plan.planId" 
            :label="plan.planName" 
            :value="plan.planId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="药品名称">
        <el-input v-model="searchForm.medicineName" placeholder="请输入药品名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="planId" label="养殖计划" min-width="150">
        <template #default="{ row }">
          {{ getPlanName(row.planId) }}
        </template>
      </el-table-column>
      <el-table-column prop="areaId" label="所属区域" min-width="120">
        <template #default="{ row }">
          {{ getAreaName(row.areaId) }}
        </template>
      </el-table-column>
      <el-table-column prop="medicineName" label="药品名称" min-width="150" />
      <el-table-column prop="medicineType" label="药品类型" width="120" />
      <el-table-column prop="dosage" label="用量" width="120">
        <template #default="{ row }">
          {{ row.dosage }} {{ row.unit }}
        </template>
      </el-table-column>
      <el-table-column prop="unitPrice" label="单价" width="100" />
      <el-table-column prop="totalCost" label="总成本(元)" width="120" />
      <el-table-column prop="usageDate" label="用药日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.usageDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="usageMethod" label="使用方法" width="120" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('disease:medicine:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('disease:medicine:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 用药记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="usageFormRef"
        :model="usageForm"
        :rules="usageRules"
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖计划" prop="planId">
              <el-select v-model="usageForm.planId" placeholder="请选择计划" style="width: 100%;" filterable @change="handlePlanChange">
                <el-option 
                  v-for="plan in planList" 
                  :key="plan.planId" 
                  :label="plan.planName" 
                  :value="plan.planId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaId">
              <el-select v-model="usageForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable :disabled="!!usageForm.planId">
                <el-option 
                  v-for="area in areaList" 
                  :key="area.areaId" 
                  :label="area.areaName" 
                  :value="area.areaId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="病害记录" prop="recordId">
              <el-select v-model="usageForm.recordId" placeholder="请选择病害记录" style="width: 100%;" filterable :disabled="!usageForm.planId">
                <el-option 
                  v-for="record in diseaseRecordList" 
                  :key="record.recordId" 
                  :label="`${record.diseaseName} (${formatDate(record.occurrenceDate)})`" 
                  :value="record.recordId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用药日期" prop="usageDate">
              <el-date-picker 
                v-model="usageForm.usageDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="药品名称" prop="medicineName">
          <el-select 
            v-model="usageForm.medicineName" 
            placeholder="请选择药品名称" 
            style="width: 100%;" 
            filterable
            @change="handleMedicineChange"
          >
            <el-option 
              v-for="inventory in medicineInventoryList" 
              :key="inventory.inventoryId" 
              :label="inventory.medicineName" 
              :value="inventory.medicineName"
              :disabled="inventory.currentStock <= 0"
            >
              <span>{{ inventory.medicineName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px;">库存: {{ inventory.currentStock }}{{ inventory.unit }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品类型" prop="medicineType">
              <el-input v-model="usageForm.medicineType" disabled placeholder="自动绑定" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="usageForm.unit" disabled placeholder="自动绑定" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用量" prop="dosage">
              <el-input-number v-model="usageForm.dosage" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价(元)">
              <el-input-number v-model="usageForm.unitPrice" :min="0" :precision="2" style="width: 100%;" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用方法">
              <el-select v-model="usageForm.usageMethod" placeholder="请选择使用方法" style="width: 100%;">
                <el-option label="拌料" value="拌料" />
                <el-option label="泼洒" value="泼洒" />
                <el-option label="注射" value="注射" />
                <el-option label="口服" value="口服" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次号" prop="batchNumber">
              <!-- 如果有采购记录，显示下拉框 -->
              <el-select 
                v-if="purchaseList.length > 0"
                v-model="usageForm.batchNumber" 
                placeholder="请选择批次号" 
                style="width: 100%;"
                @change="handleBatchChange"
              >
                <el-option
                  v-for="purchase in purchaseList"
                  :key="purchase.purchaseId"
                  :label="getBatchOptionLabel(purchase)"
                  :value="purchase.batchNumber || ''"
                />
              </el-select>
              <!-- 如果没有采购记录，显示提示 -->
              <el-input 
                v-else
                v-model="usageForm.batchNumber" 
                placeholder="该药品暂无采购记录，请先进行采购" 
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="usageForm.remarks" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { getMedicineUsageList, saveMedicineUsage, updateMedicineUsage, deleteMedicineUsage } from '@/api/medicineUsage'
import { getAllDiseaseRecords, getDiseaseRecordsByPlanId } from '@/api/diseaseRecord'
import { getApprovedPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { getAllMedicineInventories } from '@/api/medicineInventory'
import { getMedicinePurchasesByMedicine } from '@/api/medicinePurchase'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增用药记录')
const isEdit = ref(false)
const usageFormRef = ref(null)
const diseaseRecordList = ref([])
const planList = ref([])
const areaList = ref([])
const medicineInventoryList = ref([])
const purchaseList = ref([])

const searchForm = reactive({
  recordId: null,
  planId: null,
  medicineName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const usageForm = reactive({
  usageId: null,
  recordId: null,
  preventionId: null,
  planId: null,
  areaId: null,
  medicineName: '',
  medicineType: '',
  dosage: null,
  unit: '克',
  unitPrice: null,
  totalCost: null,
  usageDate: null,
  usageMethod: '',
  batchNumber: '',
  expiryDate: null,
  remarks: '',
  status: 1,
  creatorId: null
})

const usageRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  recordId: [
    { required: true, message: '请选择病害记录', trigger: 'change' }
  ],
  medicineName: [
    { required: true, message: '请选择药品名称', trigger: 'change' }
  ],
  dosage: [
    { required: true, message: '请输入用量', trigger: 'blur' }
  ],
  usageDate: [
    { required: true, message: '请选择用药日期', trigger: 'change' }
  ],
  batchNumber: [
    { required: true, message: '请选择批次号', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMedicineUsageList({
      current: pagination.current,
      size: pagination.size,
      recordId: searchForm.recordId || undefined,
      planId: searchForm.planId || undefined,
      medicineName: searchForm.medicineName || undefined
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

const loadDiseaseRecordList = async (planId) => {
  if (!planId) {
    diseaseRecordList.value = []
    return
  }
  try {
    const res = await getDiseaseRecordsByPlanId(planId)
    if (res.code === 200) {
      diseaseRecordList.value = res.data || []
    }
  } catch (error) {
    console.error('加载病害记录列表失败', error)
    diseaseRecordList.value = []
  }
}

const loadMedicineInventoryList = async () => {
  try {
    const res = await getAllMedicineInventories()
    if (res.code === 200) {
      medicineInventoryList.value = (res.data || []).filter(item => item.status === 1)
    }
  } catch (error) {
    console.error('加载药品库存列表失败', error)
  }
}

// 加载计划列表（只加载已审核通过的计划）
const loadPlanList = async () => {
  try {
    const res = await getApprovedPlans()
    if (res.code === 200) {
      planList.value = res.data || []
    }
  } catch (error) {
    console.error('加载计划列表失败', error)
  }
}

const loadAreaList = async () => {
  try {
    const res = await getAllAreas()
    if (res.code === 200) {
      areaList.value = res.data || []
    }
  } catch (error) {
    console.error('加载区域列表失败', error)
  }
}

const handlePlanChange = async (planId) => {
  if (planId) {
    const plan = planList.value.find(p => p.planId === planId)
    if (plan) {
      usageForm.areaId = plan.areaId
      // 加载该计划下的病害记录
      await loadDiseaseRecordList(planId)
      // 如果只有一条病害记录，自动选择
      if (diseaseRecordList.value.length === 1) {
        usageForm.recordId = diseaseRecordList.value[0].recordId
      } else {
        usageForm.recordId = null
      }
    }
  } else {
    usageForm.areaId = null
    usageForm.recordId = null
    diseaseRecordList.value = []
  }
  // 清除表单验证
  setTimeout(() => {
    usageFormRef.value?.clearValidate()
  }, 100)
}

const handleMedicineChange = async (medicineName) => {
  if (medicineName) {
    const inventory = medicineInventoryList.value.find(i => i.medicineName === medicineName)
    if (inventory) {
      usageForm.medicineType = inventory.medicineType
      usageForm.unit = inventory.unit
      usageForm.unitPrice = inventory.unitPrice
      // 加载该药品的采购记录
      await loadPurchaseList(medicineName, inventory.medicineType)
      // 如果只有一条采购记录，自动选择批次号
      if (purchaseList.value.length === 1) {
        usageForm.batchNumber = purchaseList.value[0].batchNumber || ''
        usageForm.expiryDate = purchaseList.value[0].expiryDate
      } else {
        usageForm.batchNumber = ''
        usageForm.expiryDate = null
      }
    }
  } else {
    usageForm.medicineType = ''
    usageForm.unit = '克'
    usageForm.unitPrice = null
    usageForm.batchNumber = ''
    usageForm.expiryDate = null
    purchaseList.value = []
  }
  // 清除表单验证
  setTimeout(() => {
    usageFormRef.value?.clearValidate()
  }, 100)
}

const loadPurchaseList = async (medicineName, medicineType) => {
  if (!medicineName || !medicineType) {
    purchaseList.value = []
    return
  }
  try {
    const res = await getMedicinePurchasesByMedicine(medicineName, medicineType)
    if (res.code === 200) {
      purchaseList.value = (res.data || []).filter(item => item.status === 1)
    }
  } catch (error) {
    console.error('加载采购记录失败', error)
    purchaseList.value = []
  }
}

const handleBatchChange = (batchNumber) => {
  if (batchNumber) {
    const purchase = purchaseList.value.find(p => p.batchNumber === batchNumber)
    if (purchase) {
      usageForm.expiryDate = purchase.expiryDate
    }
  } else {
    usageForm.expiryDate = null
  }
}

const getBatchOptionLabel = (purchase) => {
  if (purchase.expiryDate) {
    return `${purchase.batchNumber} (有效期至: ${formatDate(purchase.expiryDate)})`
  }
  return purchase.batchNumber || ''
}

const getPlanName = (planId) => {
  if (!planId) return '-'
  const plan = planList.value.find(p => p.planId === planId)
  return plan ? plan.planName : `计划${planId}`
}

const getAreaName = (areaId) => {
  if (!areaId) return '-'
  const area = areaList.value.find(a => a.areaId === areaId)
  return area ? area.areaName : `区域${areaId}`
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.recordId = null
  searchForm.planId = null
  searchForm.medicineName = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用药记录'
  dialogVisible.value = true
  resetForm()
  // 清除表单验证
  setTimeout(() => {
    usageFormRef.value?.clearValidate()
  }, 100)
}

const handleEdit = async (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用药记录'
  Object.assign(usageForm, {
    usageId: row.usageId,
    recordId: row.recordId,
    preventionId: row.preventionId,
    planId: row.planId,
    areaId: row.areaId,
    medicineName: row.medicineName,
    medicineType: row.medicineType,
    dosage: row.dosage,
    unit: row.unit || '克',
    unitPrice: row.unitPrice,
    totalCost: row.totalCost,
    usageDate: row.usageDate,
    usageMethod: row.usageMethod || '',
    batchNumber: row.batchNumber || '',
    expiryDate: row.expiryDate,
    remarks: row.remarks || '',
    status: row.status,
    creatorId: row.creatorId
  })
  // 加载该计划下的病害记录
  if (row.planId) {
    await loadDiseaseRecordList(row.planId)
  }
  // 加载该药品的采购记录
  if (row.medicineName && row.medicineType) {
    await loadPurchaseList(row.medicineName, row.medicineType)
  }
  dialogVisible.value = true
  // 清除表单验证
  setTimeout(() => {
    usageFormRef.value?.clearValidate()
  }, 100)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用药记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteMedicineUsage(row.usageId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
      // 刷新药品库存页面
      window.dispatchEvent(new CustomEvent('refresh-medicine-inventory'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!usageFormRef.value) return
  await usageFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateMedicineUsage(usageForm)
        } else {
          res = await saveMedicineUsage(usageForm)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
          // 刷新药品库存页面
          window.dispatchEvent(new CustomEvent('refresh-medicine-inventory'))
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(usageForm, {
    usageId: null,
    recordId: null,
    preventionId: null,
    planId: null,
    areaId: null,
    medicineName: '',
    medicineType: '',
    dosage: null,
    unit: '克',
    unitPrice: null,
    totalCost: null,
    usageDate: null,
    usageMethod: '',
    batchNumber: '',
    expiryDate: null,
    remarks: '',
    status: 1,
    creatorId: null
  })
  diseaseRecordList.value = []
  purchaseList.value = []
  usageFormRef.value?.clearValidate()
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
  loadPlanList()
  loadAreaList()
  loadMedicineInventoryList()
})
</script>

<style scoped>
.medicine-usage {
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

