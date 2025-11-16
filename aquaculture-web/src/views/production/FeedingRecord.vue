<template>
  <div class="feeding-record">
    <div class="card-header">
      <span>投喂记录管理</span>
      <el-button v-if="hasPermission('production:feeding:add')" type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增投喂记录
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
      <el-form-item label="饲料名称">
        <el-input v-model="searchForm.feedName" placeholder="请输入饲料名称" clearable style="width: 180px;" />
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
      <el-table-column prop="feedingDate" label="投喂日期" width="120">
        <template #default="{ row }">
          {{ formatDate(row.feedingDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="feedingTime" label="投喂时间" width="120" />
      <el-table-column prop="feedName" label="饲料名称" min-width="150" />
      <el-table-column prop="feedType" label="饲料类型" width="140" />
      <el-table-column prop="feedingAmount" label="投喂量(kg)" width="120" />
      <el-table-column prop="feedingMethod" label="投喂方式" width="120" />
      <el-table-column prop="waterTemperature" label="水温(℃)" width="100" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="hasPermission('production:feeding:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="hasPermission('production:feeding:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 投喂记录表单对话框 -->
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
            <el-form-item label="投喂日期" prop="feedingDate">
              <el-date-picker 
                v-model="recordForm.feedingDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投喂时间">
              <el-select v-model="recordForm.feedingTime" placeholder="请选择投喂时间" style="width: 100%;">
                <el-option label="早" value="早" />
                <el-option label="中" value="中" />
                <el-option label="晚" value="晚" />
                <el-option label="早晚" value="早晚" />
                <el-option label="早中晚" value="早中晚" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="饲料名称" prop="feedName">
          <el-input v-model="recordForm.feedName" placeholder="请输入饲料名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="饲料类型" prop="feedType">
              <el-select v-model="recordForm.feedType" placeholder="请选择类型" style="width: 100%;">
                <el-option label="对虾专用饲料" value="对虾专用饲料" />
                <el-option label="淡水鱼饲料" value="淡水鱼饲料" />
                <el-option label="通用饲料" value="通用饲料" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投喂量(kg)" prop="feedingAmount">
              <el-input-number v-model="recordForm.feedingAmount" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="投喂方式">
              <el-select v-model="recordForm.feedingMethod" placeholder="请选择投喂方式" style="width: 100%;">
                <el-option label="人工投喂" value="人工投喂" />
                <el-option label="机械投喂" value="机械投喂" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="水温(℃)">
              <el-input-number v-model="recordForm.waterTemperature" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="recordForm.remarks" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { getFeedingRecordList, saveFeedingRecord, updateFeedingRecord, deleteFeedingRecord } from '@/api/feedingRecord'
import { getAllPlans } from '@/api/plan'
import { getAllAreas } from '@/api/area'
import { formatDateTime, formatDate } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增投喂记录')
const isEdit = ref(false)
const recordFormRef = ref(null)
const planList = ref([])
const areaList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  feedName: ''
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
  feedingDate: null,
  feedingTime: '',
  feedName: '',
  feedType: '',
  feedingAmount: null,
  feedingMethod: '',
  waterTemperature: null,
  remarks: '',
  status: 1,
  creatorId: null
})

const recordRules = {
  planId: [
    { required: true, message: '请选择养殖计划', trigger: 'change' }
  ],
  feedingDate: [
    { required: true, message: '请选择投喂日期', trigger: 'change' }
  ],
  feedName: [
    { required: true, message: '请输入饲料名称', trigger: 'blur' }
  ],
  feedType: [
    { required: true, message: '请选择饲料类型', trigger: 'change' }
  ],
  feedingAmount: [
    { required: true, message: '请输入投喂量', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeedingRecordList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      feedName: searchForm.feedName || undefined
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
      recordForm.areaId = plan.areaId
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
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.feedName = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增投喂记录'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑投喂记录'
  Object.assign(recordForm, {
    recordId: row.recordId,
    planId: row.planId,
    areaId: row.areaId,
    feedingDate: row.feedingDate,
    feedingTime: row.feedingTime || '',
    feedName: row.feedName,
    feedType: row.feedType,
    feedingAmount: row.feedingAmount,
    feedingMethod: row.feedingMethod || '',
    waterTemperature: row.waterTemperature,
    remarks: row.remarks || '',
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该投喂记录吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteFeedingRecord(row.recordId)
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
          res = await updateFeedingRecord(recordForm)
        } else {
          res = await saveFeedingRecord(recordForm)
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
    feedingDate: null,
    feedingTime: '',
    feedName: '',
    feedType: '',
    feedingAmount: null,
    feedingMethod: '',
    waterTemperature: null,
    remarks: '',
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
.feeding-record {
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

