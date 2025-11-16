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
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="病害记录">
              <el-select v-model="usageForm.recordId" placeholder="请选择病害记录" style="width: 100%;" filterable @change="handleRecordChange">
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品名称" prop="medicineName">
              <el-input v-model="usageForm.medicineName" placeholder="请输入药品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药品类型" prop="medicineType">
              <el-select v-model="usageForm.medicineType" placeholder="请选择类型" style="width: 100%;">
                <el-option label="抗生素" value="抗生素" />
                <el-option label="消毒剂" value="消毒剂" />
                <el-option label="中药" value="中药" />
                <el-option label="益生菌" value="益生菌" />
                <el-option label="抗病毒药" value="抗病毒药" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="用量" prop="dosage">
              <el-input-number v-model="usageForm.dosage" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位">
              <el-select v-model="usageForm.unit" placeholder="请选择单位" style="width: 100%;">
                <el-option label="克" value="克" />
                <el-option label="毫升" value="毫升" />
                <el-option label="公斤" value="公斤" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价(元)">
              <el-input-number v-model="usageForm.unitPrice" :min="0" :precision="2" style="width: 100%;" />
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
            <el-form-item label="批次号">
              <el-input v-model="usageForm.batchNumber" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="有效期至">
          <el-date-picker 
            v-model="usageForm.expiryDate" 
            type="date" 
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;" 
          />
        </el-form-item>
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
import { getAllDiseaseRecords } from '@/api/diseaseRecord'
import { getAllPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
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
  medicineName: [
    { required: true, message: '请输入药品名称', trigger: 'blur' }
  ],
  medicineType: [
    { required: true, message: '请选择药品类型', trigger: 'change' }
  ],
  dosage: [
    { required: true, message: '请输入用量', trigger: 'blur' }
  ],
  usageDate: [
    { required: true, message: '请选择用药日期', trigger: 'change' }
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

const loadDiseaseRecordList = async () => {
  try {
    const res = await getAllDiseaseRecords()
    if (res.code === 200) {
      diseaseRecordList.value = res.data || []
    }
  } catch (error) {
    console.error('加载病害记录列表失败', error)
  }
}

const loadPlanList = async () => {
  try {
    const res = await getAllPlans()
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

const handleRecordChange = (recordId) => {
  if (recordId) {
    const record = diseaseRecordList.value.find(r => r.recordId === recordId)
    if (record) {
      usageForm.planId = record.planId
      usageForm.areaId = record.areaId
    }
  }
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
}

const handleEdit = (row) => {
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
  dialogVisible.value = true
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
  loadDiseaseRecordList()
  loadPlanList()
  loadAreaList()
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

