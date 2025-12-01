<template>
  <div class="disease-prevention">
    <div class="card-header">
      <span>病害防治记录管理</span>
      <el-button v-if="hasPermission('disease:prevention:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增防治记录
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
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="getImageUrl(row.imageUrl)"
            :preview-src-list="[getImageUrl(row.imageUrl)]"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 4px;"
            :preview-teleported="true"
          />
          <span v-else style="color: #909399;">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="recordId" label="病害记录" min-width="150">
        <template #default="{ row }">
          {{ getDiseaseRecordName(row.recordId) }}
        </template>
      </el-table-column>
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
      <el-table-column prop="preventionMethod" label="防治方法" min-width="200" show-overflow-tooltip />
      <el-table-column prop="preventionDate" label="防治日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.preventionDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="preventionResult" label="防治结果" width="120">
        <template #default="{ row }">
          <el-tag :type="getResultTagType(row.preventionResult)">
            {{ row.preventionResult }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('disease:prevention:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('disease:prevention:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 防治记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="preventionFormRef"
        :model="preventionForm"
        :rules="preventionRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="病害记录" prop="recordId">
              <el-select v-model="preventionForm.recordId" placeholder="请选择病害记录" style="width: 100%;" filterable @change="handleRecordChange">
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
            <el-form-item label="防治日期" prop="preventionDate">
              <el-date-picker 
                v-model="preventionForm.preventionDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="防治方法" prop="preventionMethod">
          <el-input v-model="preventionForm.preventionMethod" type="textarea" :rows="3" placeholder="请输入防治方法" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="防治结果">
              <el-select v-model="preventionForm.preventionResult" placeholder="请选择防治结果" style="width: 100%;">
                <el-option label="有效" value="有效" />
                <el-option label="无效" value="无效" />
                <el-option label="待观察" value="待观察" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人">
              <el-input v-model="preventionForm.operator" placeholder="请输入操作人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="效果描述">
          <el-input v-model="preventionForm.effectDescription" type="textarea" :rows="3" placeholder="请输入效果描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="preventionForm.remarks" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-upload
              :action="uploadAction"
              :headers="uploadHeaders"
              :data="{ module: 'disease' }"
              :show-file-list="false"
              :on-success="handleImageSuccess"
              :before-upload="beforeImageUpload"
              accept="image/*"
            >
              <el-button type="primary" size="small">上传图片</el-button>
            </el-upload>
            <el-image
              v-if="preventionForm.imageUrl"
              :src="getImageUrl(preventionForm.imageUrl)"
              fit="cover"
              style="width: 100px; height: 100px; border-radius: 4px;"
              :preview-src-list="[getImageUrl(preventionForm.imageUrl)]"
              :preview-teleported="true"
            />
            <el-button
              v-if="preventionForm.imageUrl"
              type="danger"
              size="small"
              @click="handleRemoveImage"
            >
              删除图片
            </el-button>
          </div>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDiseasePreventionList, saveDiseasePrevention, updateDiseasePrevention, deleteDiseasePrevention, getDiseasePreventionsByRecordId } from '@/api/diseasePrevention'
import { getAllDiseaseRecords } from '@/api/diseaseRecord'
import { getApprovedPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增防治记录')
const isEdit = ref(false)
const preventionFormRef = ref(null)
const diseaseRecordList = ref([])
const planList = ref([])
const areaList = ref([])

const searchForm = reactive({
  recordId: null,
  planId: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const preventionForm = reactive({
  preventionId: null,
  recordId: null,
  planId: null,
  areaId: null,
  preventionMethod: '',
  preventionDate: null,
  preventionResult: '',
  effectDescription: '',
  operator: '',
  remarks: '',
  imageUrl: '',
  status: 1,
  creatorId: null
})

const uploadAction = '/api/upload/image'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

const getImageUrl = (imageUrl) => {
  if (!imageUrl) return ''
  if (imageUrl.startsWith('http')) return imageUrl
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${imageUrl}`
}

const handleImageSuccess = (response) => {
  if (response.code === 200 && response.data) {
    preventionForm.imageUrl = response.data.filePath
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleRemoveImage = () => {
  preventionForm.imageUrl = ''
}

const preventionRules = {
  recordId: [
    { required: true, message: '请选择病害记录', trigger: 'change' }
  ],
  preventionMethod: [
    { required: true, message: '请输入防治方法', trigger: 'blur' }
  ],
  preventionDate: [
    { required: true, message: '请选择防治日期', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDiseasePreventionList({
      current: pagination.current,
      size: pagination.size,
      recordId: searchForm.recordId || undefined,
      planId: searchForm.planId || undefined
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

const handleRecordChange = (recordId) => {
  if (recordId) {
    const record = diseaseRecordList.value.find(r => r.recordId === recordId)
    if (record) {
      preventionForm.planId = record.planId
      preventionForm.areaId = record.areaId
    }
  }
}

const getDiseaseRecordName = (recordId) => {
  if (!recordId) return '-'
  const record = diseaseRecordList.value.find(r => r.recordId === recordId)
  return record ? `${record.diseaseName} (${formatDate(record.occurrenceDate)})` : `记录${recordId}`
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

const getResultTagType = (result) => {
  if (result === '有效') return 'success'
  if (result === '无效') return 'danger'
  return 'warning'
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.recordId = null
  searchForm.planId = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增防治记录'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑防治记录'
  Object.assign(preventionForm, {
    preventionId: row.preventionId,
    recordId: row.recordId,
    planId: row.planId,
    areaId: row.areaId,
    preventionMethod: row.preventionMethod,
    preventionDate: row.preventionDate,
    preventionResult: row.preventionResult || '',
    effectDescription: row.effectDescription || '',
    operator: row.operator || '',
    remarks: row.remarks || '',
    imageUrl: row.imageUrl || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该防治记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteDiseasePrevention(row.preventionId)
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
  if (!preventionFormRef.value) return
  await preventionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateDiseasePrevention(preventionForm)
        } else {
          res = await saveDiseasePrevention(preventionForm)
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
  Object.assign(preventionForm, {
    preventionId: null,
    recordId: null,
    planId: null,
    areaId: null,
    preventionMethod: '',
    preventionDate: null,
    preventionResult: '',
    effectDescription: '',
    operator: '',
    remarks: '',
    imageUrl: '',
    status: 1,
    creatorId: null
  })
  preventionFormRef.value?.clearValidate()
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
.disease-prevention {
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

