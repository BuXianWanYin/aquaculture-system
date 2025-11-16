<template>
  <div class="plan-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>养殖计划管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增计划
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="计划名称">
          <el-input v-model="searchForm.planName" placeholder="请输入计划名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
            <el-option label="执行中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="planName" label="计划名称" min-width="180" />
        <el-table-column prop="areaId" label="所属区域" min-width="120">
          <template #default="{ row }">
            {{ getAreaName(row.areaId) }}
          </template>
        </el-table-column>
        <el-table-column prop="breedId" label="养殖品种" min-width="120">
          <template #default="{ row }">
            {{ getBreedName(row.breedId) }}
          </template>
        </el-table-column>
        <el-table-column prop="targetYield" label="目标产量(kg)" min-width="120" />
        <el-table-column prop="releaseAmount" label="投放量(kg)" min-width="120" />
        <el-table-column prop="cycleDays" label="周期(天)" width="100" />
        <el-table-column prop="startDate" label="开始日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
            <el-tag v-else-if="row.status === 3" type="primary">执行中</el-tag>
            <el-tag v-else-if="row.status === 4" type="info">已完成</el-tag>
            <el-tag v-else-if="row.status === 5">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link size="small" @click="handleApprove(row)" v-if="row.status === 0">审批</el-button>
            <el-button type="warning" link size="small" @click="handleAdjust(row)">调整</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    </el-card>
    
    <!-- 计划表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="planFormRef"
        :model="planForm"
        :rules="planRules"
        label-width="120px"
      >
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="planForm.planName" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaId">
              <el-select v-model="planForm.areaId" placeholder="请选择区域" style="width: 100%;" filterable>
                <el-option 
                  v-for="area in areaList" 
                  :key="area.areaId" 
                  :label="area.areaName" 
                  :value="area.areaId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="养殖品种" prop="breedId">
              <el-select v-model="planForm.breedId" placeholder="请选择品种" style="width: 100%;" filterable>
                <el-option 
                  v-for="breed in breedList" 
                  :key="breed.breedId" 
                  :label="breed.breedName" 
                  :value="breed.breedId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标产量(kg)" prop="targetYield">
              <el-input-number v-model="planForm.targetYield" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投放量(kg)" prop="releaseAmount">
              <el-input-number v-model="planForm.releaseAmount" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养殖周期(天)" prop="cycleDays">
              <el-input-number v-model="planForm.cycleDays" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker 
                v-model="planForm.startDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="饲料预算(元)">
          <el-input-number v-model="planForm.feedBudget" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="防疫计划">
          <el-input v-model="planForm.preventionPlan" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      title="计划审批"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="计划名称">
          <el-input v-model="currentPlan.planName" disabled />
        </el-form-item>
        <el-form-item label="审批意见" prop="approveOpinion">
          <el-input v-model="approveForm.approveOpinion" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item label="审批结果">
          <el-radio-group v-model="approveForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApproveSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 调整对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      title="计划调整"
      width="600px"
    >
      <el-form
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustRules"
        label-width="120px"
      >
        <el-form-item label="调整类型" prop="adjustType">
          <el-select v-model="adjustForm.adjustType" placeholder="请选择调整类型" style="width: 100%;">
            <el-option label="延长周期" value="延长周期" />
            <el-option label="修改目标产量" value="修改目标产量" />
            <el-option label="修改投放量" value="修改投放量" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="调整原因" prop="adjustReason">
          <el-input v-model="adjustForm.adjustReason" type="textarea" :rows="4" placeholder="请输入调整原因" />
        </el-form-item>
        <el-form-item label="新参数">
          <el-input v-model="adjustForm.newParams" type="textarea" :rows="3" placeholder="请输入新的参数（JSON格式）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdjustSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPlanList, savePlan, updatePlan, deletePlan, approvePlan } from '@/api/plan'
import { saveAdjust } from '@/api/planAdjust'
import { getAllAreas } from '@/api/area'
import { getAllBreeds } from '@/api/breed'
import { useUserStore } from '@/stores/user'
import { formatDateTime, formatDate } from '@/utils/date'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const approveDialogVisible = ref(false)
const adjustDialogVisible = ref(false)
const dialogTitle = ref('新增计划')
const isEdit = ref(false)
const planFormRef = ref(null)
const adjustFormRef = ref(null)
const userStore = useUserStore()
const currentPlan = ref({})
const areaList = ref([])
const breedList = ref([])

const searchForm = reactive({
  planName: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const planForm = reactive({
  planId: null,
  planName: '',
  areaId: null,
  breedId: null,
  targetYield: null,
  releaseAmount: null,
  cycleDays: null,
  startDate: null,
  endDate: null,
  feedBudget: null,
  preventionPlan: '',
  status: 0,
  creatorId: null
})

const approveForm = reactive({
  planId: null,
  approverId: null,
  approveOpinion: '',
  status: 1
})

const adjustForm = reactive({
  planId: null,
  adjustReason: '',
  adjustType: '',
  newParams: '',
  creatorId: null
})

const planRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  areaId: [
    { required: true, message: '请选择所属区域', trigger: 'change' }
  ],
  breedId: [
    { required: true, message: '请选择养殖品种', trigger: 'change' }
  ],
  targetYield: [
    { required: true, message: '请输入目标产量', trigger: 'blur' }
  ],
  releaseAmount: [
    { required: true, message: '请输入投放量', trigger: 'blur' }
  ],
  cycleDays: [
    { required: true, message: '请输入养殖周期', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ]
}

const adjustRules = {
  adjustType: [
    { required: true, message: '请选择调整类型', trigger: 'change' }
  ],
  adjustReason: [
    { required: true, message: '请输入调整原因', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPlanList({
      current: pagination.current,
      size: pagination.size,
      planName: searchForm.planName || undefined,
      status: searchForm.status
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
  searchForm.planName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增计划'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑计划'
  Object.assign(planForm, {
    planId: row.planId,
    planName: row.planName,
    areaId: row.areaId,
    breedId: row.breedId,
    targetYield: row.targetYield,
    releaseAmount: row.releaseAmount,
    cycleDays: row.cycleDays,
    startDate: row.startDate,
    endDate: row.endDate,
    feedBudget: row.feedBudget,
    preventionPlan: row.preventionPlan || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleApprove = (row) => {
  currentPlan.value = { ...row }
  approveForm.planId = row.planId
  approveForm.approverId = userStore.userInfo?.userId
  approveForm.approveOpinion = ''
  approveForm.status = 1
  approveDialogVisible.value = true
}

const handleAdjust = (row) => {
  adjustForm.planId = row.planId
  adjustForm.creatorId = userStore.userInfo?.userId
  adjustForm.adjustReason = ''
  adjustForm.adjustType = ''
  adjustForm.newParams = ''
  adjustDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该计划吗？', '提示', {
      type: 'warning'
    })
    const res = await deletePlan(row.planId)
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
  if (!planFormRef.value) return
  await planFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        planForm.creatorId = userStore.userInfo?.userId
        let res
        if (isEdit.value) {
          res = await updatePlan(planForm)
        } else {
          res = await savePlan(planForm)
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

const handleApproveSubmit = async () => {
  try {
    const res = await approvePlan(approveForm)
    if (res.code === 200) {
      ElMessage.success('审批成功')
      approveDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error(error.message || '审批失败')
  }
}

const handleAdjustSubmit = async () => {
  if (!adjustFormRef.value) return
  await adjustFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await saveAdjust(adjustForm)
        if (res.code === 200) {
          ElMessage.success('调整申请提交成功')
          adjustDialogVisible.value = false
          loadData()
        }
      } catch (error) {
        ElMessage.error(error.message || '提交失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(planForm, {
    planId: null,
    planName: '',
    areaId: null,
    breedId: null,
    targetYield: null,
    releaseAmount: null,
    cycleDays: null,
    startDate: null,
    endDate: null,
    feedBudget: null,
    preventionPlan: '',
    status: 0,
    creatorId: null
  })
  planFormRef.value?.clearValidate()
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

// 加载区域列表
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

// 加载品种列表
const loadBreedList = async () => {
  try {
    const res = await getAllBreeds()
    if (res.code === 200) {
      breedList.value = res.data || []
    }
  } catch (error) {
    console.error('加载品种列表失败', error)
  }
}

// 根据ID获取区域名称
const getAreaName = (areaId) => {
  if (!areaId) return '-'
  const area = areaList.value.find(a => a.areaId === areaId)
  return area ? area.areaName : `区域${areaId}`
}

// 根据ID获取品种名称
const getBreedName = (breedId) => {
  if (!breedId) return '-'
  const breed = breedList.value.find(b => b.breedId === breedId)
  return breed ? breed.breedName : `品种${breedId}`
}

onMounted(() => {
  loadData()
  loadAreaList()
  loadBreedList()
})
</script>

<style scoped>
.plan-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

