<template>
  <div class="disease-record">
    <div class="card-header">
      <span>病害记录管理</span>
      <el-button v-if="hasPermission('disease:record:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增病害记录
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-form">
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
      <el-form-item label="所属区域">
        <el-select v-model="searchForm.areaId" placeholder="请选择区域" clearable filterable style="width: 180px;">
          <el-option 
            v-for="area in areaList" 
            :key="area.areaId" 
            :label="area.areaName" 
            :value="area.areaId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="病害名称">
        <el-input v-model="searchForm.diseaseName" placeholder="请输入病害名称" clearable style="width: 180px;" />
      </el-form-item>
      <el-form-item label="病害类型">
        <el-select v-model="searchForm.diseaseType" placeholder="请选择类型" clearable style="width: 150px;">
          <el-option label="病毒性疾病" value="病毒性疾病" />
          <el-option label="细菌性疾病" value="细菌性疾病" />
          <el-option label="真菌性疾病" value="真菌性疾病" />
          <el-option label="寄生虫病" value="寄生虫病" />
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
      <el-table-column prop="diseaseName" label="病害名称" min-width="150" />
      <el-table-column prop="diseaseType" label="病害类型" width="120" />
      <el-table-column prop="occurrenceDate" label="发生日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.occurrenceDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="severityLevel" label="严重程度" width="120">
        <template #default="{ row }">
          <el-tag :type="getSeverityTagType(row.severityLevel)">
            {{ row.severityLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="affectedQuantity" label="受影响数量(kg)" width="140" />
      <el-table-column prop="symptoms" label="症状描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('disease:record:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('disease:record:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 病害记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="recordFormRef"
        :model="recordForm"
        :rules="recordRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖计划" prop="planId">
              <el-select v-model="recordForm.planId" placeholder="请选择计划" style="width: 100%;" filterable @change="handlePlanChange">
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
              <el-select v-model="recordForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable :disabled="!!recordForm.planId">
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
            <el-form-item label="病害名称" prop="diseaseName">
              <el-input v-model="recordForm.diseaseName" placeholder="请输入病害名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="病害类型" prop="diseaseType">
              <el-select v-model="recordForm.diseaseType" placeholder="请选择类型" style="width: 100%;">
                <el-option label="病毒性疾病" value="病毒性疾病" />
                <el-option label="细菌性疾病" value="细菌性疾病" />
                <el-option label="真菌性疾病" value="真菌性疾病" />
                <el-option label="寄生虫病" value="寄生虫病" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发生日期" prop="occurrenceDate">
              <el-date-picker 
                v-model="recordForm.occurrenceDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="严重程度">
              <el-select v-model="recordForm.severityLevel" placeholder="请选择严重程度" style="width: 100%;">
                <el-option label="轻微" value="轻微" />
                <el-option label="中等" value="中等" />
                <el-option label="严重" value="严重" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="影响面积(㎡)">
              <el-input-number v-model="recordForm.affectedArea" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="受影响数量(kg)">
              <el-input-number v-model="recordForm.affectedQuantity" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="症状描述">
          <el-input v-model="recordForm.symptoms" type="textarea" :rows="4" placeholder="请输入症状描述" />
        </el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; flex-direction: column; gap: 10px; align-items: flex-start;">
            <el-image
              v-if="recordForm.imageUrl"
              :src="getImageUrl(recordForm.imageUrl)"
              fit="cover"
              style="width: 150px; height: 150px; border-radius: 4px; border: 1px solid #dcdfe6;"
              :preview-src-list="[getImageUrl(recordForm.imageUrl)]"
              :preview-teleported="true"
            />
            <div style="display: flex; gap: 10px;">
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
              <el-button
                v-if="recordForm.imageUrl"
                type="danger"
                size="small"
                @click="handleRemoveImage"
              >
                删除图片
              </el-button>
            </div>
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
import { getDiseaseRecordList, saveDiseaseRecord, updateDiseaseRecord, deleteDiseaseRecord } from '@/api/diseaseRecord'
import { getApprovedPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增病害记录')
const isEdit = ref(false)
const recordFormRef = ref(null)
const planList = ref([])
const areaList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  diseaseName: '',
  diseaseType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const recordForm = reactive({
  recordId: null,
  planId: null,
  areaId: null,
  breedId: null,
  diseaseName: '',
  diseaseType: '',
  occurrenceDate: null,
  affectedArea: null,
  affectedQuantity: null,
  severityLevel: '',
  symptoms: '',
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
    recordForm.imageUrl = response.data.filePath
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
  recordForm.imageUrl = ''
}

const recordRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  diseaseName: [
    { required: true, message: '请输入病害名称', trigger: 'blur' }
  ],
  diseaseType: [
    { required: true, message: '请选择病害类型', trigger: 'change' }
  ],
  occurrenceDate: [
    { required: true, message: '请选择发生日期', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDiseaseRecordList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      diseaseName: searchForm.diseaseName || undefined,
      diseaseType: searchForm.diseaseType || undefined
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

const handlePlanChange = (planId) => {
  if (planId) {
    const plan = planList.value.find(p => p.planId === planId)
    if (plan) {
      recordForm.areaId = plan.areaId
      recordForm.breedId = plan.breedId
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

const getSeverityTagType = (severity) => {
  if (severity === '严重') return 'danger'
  if (severity === '中等') return 'warning'
  return 'success'
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.diseaseName = ''
  searchForm.diseaseType = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增病害记录'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑病害记录'
  Object.assign(recordForm, {
    recordId: row.recordId,
    planId: row.planId,
    areaId: row.areaId,
    breedId: row.breedId,
    diseaseName: row.diseaseName,
    diseaseType: row.diseaseType,
    occurrenceDate: row.occurrenceDate,
    affectedArea: row.affectedArea,
    affectedQuantity: row.affectedQuantity,
    severityLevel: row.severityLevel || '',
    symptoms: row.symptoms || '',
    imageUrl: row.imageUrl || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该病害记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteDiseaseRecord(row.recordId)
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
  if (!recordFormRef.value) return
  await recordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateDiseaseRecord(recordForm)
        } else {
          res = await saveDiseaseRecord(recordForm)
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
  Object.assign(recordForm, {
    recordId: null,
    planId: null,
    areaId: null,
    breedId: null,
    diseaseName: '',
    diseaseType: '',
    occurrenceDate: null,
    affectedArea: null,
    affectedQuantity: null,
    severityLevel: '',
    symptoms: '',
    imageUrl: '',
    status: 1,
    creatorId: null
  })
  recordFormRef.value?.clearValidate()
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
})
</script>

<style scoped>
.disease-record {
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

