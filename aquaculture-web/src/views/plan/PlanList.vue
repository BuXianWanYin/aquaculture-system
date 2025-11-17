<template>
  <div class="plan-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>养殖计划管理</span>
          <el-button v-if="hasPermission('plan:add')" type="primary" @click="handleAdd">
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
        <el-table-column prop="feedBudget" label="饲料预算" min-width="150">
          <template #default="{ row }">
            <span v-if="row.feedBudget || row.feedUsedAmount !== undefined">
              {{ formatFeedBudget(row.feedUsedAmount, row.feedBudget) }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="cycleDays" label="周期(天)" width="100" />
        <el-table-column prop="startDate" label="开始日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="completionRate" label="完成率" min-width="150" sortable="custom" :sort-method="sortByCompletionRate">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-progress 
                :percentage="row.completionRate || 0" 
                :color="getProgressColor(row.completionRate || 0)"
                :stroke-width="8"
                style="flex: 1;"
              />
             
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
            <el-tag v-else-if="row.status === 3" type="primary">执行中</el-tag>
            <el-tag v-else-if="row.status === 4" type="success" effect="dark">已完成</el-tag>
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
            <el-button v-if="hasPermission('plan:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="hasPermission('plan:approve') && row.status === 0" type="success" link size="small" @click="handleApprove(row)">审批</el-button>
            <el-button v-if="hasPermission('plan:adjust:add')" type="warning" link size="small" @click="handleAdjust(row)">调整</el-button>
            <el-button v-if="hasPermission('plan:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item 
          v-if="isEdit && originalPlanStatus !== null && (originalPlanStatus === 1 || originalPlanStatus === 3 || originalPlanStatus === 4)" 
          label="状态" 
          prop="status"
        >
          <el-select v-model="planForm.status" placeholder="请选择状态" style="width: 100%;">
            <!-- 如果原状态是已通过（1），可以保持已通过，或改为执行中、已完成 -->
            <template v-if="originalPlanStatus === 1">
              <el-option label="已通过" :value="1" />
              <el-option label="执行中" :value="3" />
              <el-option label="已完成" :value="4" />
            </template>
            <!-- 如果原状态是执行中（3），可以保持执行中，或改为已完成 -->
            <template v-else-if="originalPlanStatus === 3">
              <el-option label="执行中" :value="3" />
              <el-option label="已完成" :value="4" />
            </template>
            <!-- 如果原状态是已完成（4），只能保持已完成 -->
            <template v-else-if="originalPlanStatus === 4">
              <el-option label="已完成" :value="4" />
            </template>
          </el-select>
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
        :rules="getAdjustRules()"
        label-width="120px"
      >
        <el-form-item label="调整类型" prop="adjustType">
          <el-select v-model="adjustForm.adjustType" placeholder="请选择调整类型" style="width: 100%;">
            <el-option label="延长周期" value="延长周期" />
            <el-option label="修改目标产量" value="修改目标产量" />
            <el-option label="修改投放量" value="修改投放量" />
          </el-select>
        </el-form-item>
        <el-form-item label="调整原因" prop="adjustReason">
          <el-input v-model="adjustForm.adjustReason" type="textarea" :rows="4" placeholder="请输入调整原因" />
        </el-form-item>
        <!-- 根据调整类型显示不同的输入控件 -->
        <el-form-item 
          v-if="adjustForm.adjustType === '延长周期'" 
          label="延长天数" 
          prop="cycleDays"
        >
          <el-input-number 
            v-model="adjustForm.cycleDays" 
            :min="1" 
            placeholder="请输入延长的天数" 
            style="width: 100%;" 
          />
        </el-form-item>
        <el-form-item 
          v-if="adjustForm.adjustType === '修改目标产量'" 
          label="新目标产量(kg)" 
          prop="targetYield"
        >
          <el-input-number 
            v-model="adjustForm.targetYield" 
            :min="0" 
            :precision="2" 
            placeholder="请输入新的目标产量" 
            style="width: 100%;" 
          />
        </el-form-item>
        <el-form-item 
          v-if="adjustForm.adjustType === '修改投放量'" 
          label="新投放量(kg)" 
          prop="releaseAmount"
        >
          <el-input-number 
            v-model="adjustForm.releaseAmount" 
            :min="0" 
            :precision="2" 
            placeholder="请输入新的投放量" 
            style="width: 100%;" 
          />
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
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPlanList, savePlan, updatePlan, deletePlan, approvePlan, getPlanCompletionRate } from '@/api/plan'
import { saveAdjust } from '@/api/planAdjust'
import { getAllAreas } from '@/api/area'
import { getAllBreeds } from '@/api/breed'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'
import { useUserStore } from '@/stores/user'

const { isOperator, isAdminOrManager, hasPermission } = usePermission()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const approveDialogVisible = ref(false)
const adjustDialogVisible = ref(false)
const dialogTitle = ref('新增计划')
const isEdit = ref(false)
const planFormRef = ref(null)
const adjustFormRef = ref(null)
const currentPlan = ref({})
const originalPlanStatus = ref(null) // 保存编辑时的原始状态
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
  creatorId: null,
  // 根据调整类型存储不同的参数值
  cycleDays: null,
  targetYield: null,
  releaseAmount: null
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

// 动态验证规则
const getAdjustRules = () => {
  const rules = {
    adjustType: [
      { required: true, message: '请选择调整类型', trigger: 'change' }
    ],
    adjustReason: [
      { required: true, message: '请输入调整原因', trigger: 'blur' }
    ]
  }
  
  // 根据调整类型添加相应的验证规则
  if (adjustForm.adjustType === '延长周期') {
    rules.cycleDays = [
      { required: true, message: '请输入延长天数', trigger: 'blur' }
    ]
  } else if (adjustForm.adjustType === '修改目标产量') {
    rules.targetYield = [
      { required: true, message: '请输入新目标产量', trigger: 'blur' }
    ]
  } else if (adjustForm.adjustType === '修改投放量') {
    rules.releaseAmount = [
      { required: true, message: '请输入新投放量', trigger: 'blur' }
    ]
  }
  
  return rules
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
      
      // 加载每个计划的完成率
      await loadCompletionRates()
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载所有计划的完成率
const loadCompletionRates = async () => {
  const promises = tableData.value.map(async (row) => {
    try {
      const res = await getPlanCompletionRate(row.planId)
      if (res.code === 200) {
        row.completionRate = res.data || 0
      } else {
        row.completionRate = 0
      }
    } catch (error) {
      row.completionRate = 0
    }
  })
  await Promise.all(promises)
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage >= 100) {
    return '#67c23a' // 绿色 - 已完成
  } else if (percentage >= 80) {
    return '#409eff' // 蓝色 - 接近完成
  } else if (percentage >= 50) {
    return '#e6a23c' // 橙色 - 进行中
  } else {
    return '#f56c6c' // 红色 - 进度较慢
  }
}

// 按完成率排序
const sortByCompletionRate = (a, b) => {
  const rateA = a.completionRate || 0
  const rateB = b.completionRate || 0
  return rateA - rateB
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
  originalPlanStatus.value = row.status // 保存原始状态
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
  // 重置参数值
  adjustForm.cycleDays = null
  adjustForm.targetYield = null
  adjustForm.releaseAmount = null
  adjustDialogVisible.value = true
}

// 监听调整类型变化，清除之前的输入值和验证状态
watch(() => adjustForm.adjustType, (newType, oldType) => {
  if (oldType && newType !== oldType) {
    // 清除所有参数值
    adjustForm.cycleDays = null
    adjustForm.targetYield = null
    adjustForm.releaseAmount = null
    adjustForm.newParams = ''
    // 清除验证状态
    if (adjustFormRef.value) {
      adjustFormRef.value.clearValidate()
    }
  }
})

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
        // 编辑时，如果修改了状态，需要验证状态转换的合法性
        if (isEdit.value && planForm.status !== undefined && originalPlanStatus.value !== null) {
          // 如果原状态是已通过（1），只能修改为执行中（3）或已完成（4），或者保持为1
          if (originalPlanStatus.value === 1) {
            if (planForm.status !== 1 && planForm.status !== 3 && planForm.status !== 4) {
              ElMessage.error('已通过的计划只能修改为执行中或已完成')
              return
            }
          }
          // 如果原状态是执行中（3），只能修改为已完成（4），或者保持为3
          else if (originalPlanStatus.value === 3) {
            if (planForm.status !== 3 && planForm.status !== 4) {
              ElMessage.error('执行中的计划只能修改为已完成')
              return
            }
          }
          // 如果原状态是已完成（4），只能保持为4
          else if (originalPlanStatus.value === 4) {
            if (planForm.status !== 4) {
              ElMessage.error('已完成的计划不能再修改状态')
              return
            }
          }
          // 其他状态（0待审批、2已驳回、5已取消）不允许修改状态
          else {
            if (planForm.status === 3 || planForm.status === 4) {
              ElMessage.error('只有已通过、执行中或已完成的计划才能修改状态')
              return
            }
          }
        }
        
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
        // 根据调整类型构建JSON参数
        let newParamsJson = ''
        if (adjustForm.adjustType === '延长周期') {
          newParamsJson = JSON.stringify({ cycleDays: adjustForm.cycleDays })
        } else if (adjustForm.adjustType === '修改目标产量') {
          newParamsJson = JSON.stringify({ targetYield: adjustForm.targetYield })
        } else if (adjustForm.adjustType === '修改投放量') {
          newParamsJson = JSON.stringify({ releaseAmount: adjustForm.releaseAmount })
        }
        
        adjustForm.newParams = newParamsJson
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
  originalPlanStatus.value = null
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

// 格式化饲料预算显示：已使用金额/预算金额
const formatFeedBudget = (usedAmount, budget) => {
  const used = usedAmount || 0
  const budgetValue = budget || 0
  
  // 格式化为两位小数
  const usedStr = typeof used === 'number' ? used.toFixed(2) : (parseFloat(used) || 0).toFixed(2)
  const budgetStr = typeof budgetValue === 'number' ? budgetValue.toFixed(2) : (parseFloat(budgetValue) || 0).toFixed(2)
  
  return `${usedStr}/${budgetStr}`
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

