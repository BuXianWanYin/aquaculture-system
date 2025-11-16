<template>
  <div class="daily-inspection">
    <div class="card-header">
      <span>日常检查记录管理</span>
      <el-button v-if="hasPermission('production:inspection:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增检查记录
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
      <el-form-item label="检查类型">
        <el-select v-model="searchForm.inspectionType" placeholder="请选择类型" clearable style="width: 150px;">
          <el-option label="日常检查" value="日常检查" />
          <el-option label="设备检查" value="设备检查" />
          <el-option label="环境检查" value="环境检查" />
        </el-select>
      </el-form-item>
      <el-form-item label="检查结果">
        <el-select v-model="searchForm.inspectionResult" placeholder="请选择结果" clearable style="width: 150px;">
          <el-option label="正常" value="正常" />
          <el-option label="异常" value="异常" />
          <el-option label="待处理" value="待处理" />
        </el-select>
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
      <el-table-column prop="inspectionDate" label="检查日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.inspectionDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="inspectionType" label="检查类型" width="120" />
      <el-table-column prop="inspectionItem" label="检查项目" min-width="200" show-overflow-tooltip />
      <el-table-column prop="inspectionResult" label="检查结果" width="120">
        <template #default="{ row }">
          <el-tag :type="getResultTagType(row.inspectionResult)">
            {{ row.inspectionResult }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handler" label="处理人" width="120" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('production:inspection:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('production:inspection:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 检查记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="inspectionFormRef"
        :model="inspectionForm"
        :rules="inspectionRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖计划" prop="planId">
              <el-select v-model="inspectionForm.planId" placeholder="请选择计划" style="width: 100%;" filterable @change="handlePlanChange">
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
              <el-select v-model="inspectionForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable :disabled="!!inspectionForm.planId">
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
            <el-form-item label="检查日期" prop="inspectionDate">
              <el-date-picker 
                v-model="inspectionForm.inspectionDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查类型" prop="inspectionType">
              <el-select v-model="inspectionForm.inspectionType" placeholder="请选择类型" style="width: 100%;">
                <el-option label="日常检查" value="日常检查" />
                <el-option label="设备检查" value="设备检查" />
                <el-option label="环境检查" value="环境检查" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="检查项目" prop="inspectionItem">
          <el-input v-model="inspectionForm.inspectionItem" placeholder="请输入检查项目" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检查结果">
              <el-select v-model="inspectionForm.inspectionResult" placeholder="请选择结果" style="width: 100%;">
                <el-option label="正常" value="正常" />
                <el-option label="异常" value="异常" />
                <el-option label="待处理" value="待处理" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理人">
              <el-input v-model="inspectionForm.handler" placeholder="请输入处理人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="问题描述">
          <el-input v-model="inspectionForm.problemDescription" type="textarea" :rows="3" placeholder="请输入问题描述" />
        </el-form-item>
        <el-form-item label="处理方法">
          <el-input v-model="inspectionForm.handlingMethod" type="textarea" :rows="3" placeholder="请输入处理方法" />
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
import { getDailyInspectionList, saveDailyInspection, updateDailyInspection, deleteDailyInspection } from '@/api/dailyInspection'
import { getAllPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增检查记录')
const isEdit = ref(false)
const inspectionFormRef = ref(null)
const planList = ref([])
const areaList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  inspectionType: '',
  inspectionResult: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const inspectionForm = reactive({
  inspectionId: null,
  planId: null,
  areaId: null,
  inspectionDate: null,
  inspectionTime: '',
  inspectionType: '',
  inspectionItem: '',
  inspectionResult: '',
  problemDescription: '',
  handlingMethod: '',
  handler: '',
  status: 1,
  creatorId: null
})

const inspectionRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  inspectionDate: [
    { required: true, message: '请选择检查日期', trigger: 'change' }
  ],
  inspectionType: [
    { required: true, message: '请选择检查类型', trigger: 'change' }
  ],
  inspectionItem: [
    { required: true, message: '请输入检查项目', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDailyInspectionList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      inspectionType: searchForm.inspectionType || undefined,
      inspectionResult: searchForm.inspectionResult || undefined
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

const handlePlanChange = (planId) => {
  if (planId) {
    const plan = planList.value.find(p => p.planId === planId)
    if (plan) {
      inspectionForm.areaId = plan.areaId
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

const getResultTagType = (result) => {
  if (result === '正常') return 'success'
  if (result === '异常') return 'danger'
  return 'warning'
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.inspectionType = ''
  searchForm.inspectionResult = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增检查记录'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑检查记录'
  Object.assign(inspectionForm, {
    inspectionId: row.inspectionId,
    planId: row.planId,
    areaId: row.areaId,
    inspectionDate: row.inspectionDate,
    inspectionTime: row.inspectionTime || '',
    inspectionType: row.inspectionType,
    inspectionItem: row.inspectionItem,
    inspectionResult: row.inspectionResult || '',
    problemDescription: row.problemDescription || '',
    handlingMethod: row.handlingMethod || '',
    handler: row.handler || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该检查记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteDailyInspection(row.inspectionId)
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
  if (!inspectionFormRef.value) return
  await inspectionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateDailyInspection(inspectionForm)
        } else {
          res = await saveDailyInspection(inspectionForm)
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
  Object.assign(inspectionForm, {
    inspectionId: null,
    planId: null,
    areaId: null,
    inspectionDate: null,
    inspectionTime: '',
    inspectionType: '',
    inspectionItem: '',
    inspectionResult: '',
    problemDescription: '',
    handlingMethod: '',
    handler: '',
    status: 1,
    creatorId: null
  })
  inspectionFormRef.value?.clearValidate()
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
.daily-inspection {
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

