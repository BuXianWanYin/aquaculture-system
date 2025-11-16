<template>
  <div class="yield-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>产量统计管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增产量统计
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="计划ID">
          <el-input-number v-model="searchForm.planId" placeholder="请输入计划ID" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="区域ID">
          <el-input-number v-model="searchForm.areaId" placeholder="请输入区域ID" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="planId" label="计划ID" width="100" />
        <el-table-column prop="areaId" label="区域ID" width="100" />
        <el-table-column prop="breedId" label="品种ID" width="100" />
        <el-table-column prop="actualYield" label="实际产量(kg)" min-width="120" />
        <el-table-column prop="specification" label="规格" min-width="120" />
        <el-table-column prop="statisticsDate" label="统计日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.statisticsDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="catchTime" label="捕捞时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.catchTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
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
            <el-button type="success" link size="small" @click="handleAudit(row)" v-if="row.status === 0">审核</el-button>
            <el-button type="info" link size="small" @click="handleViewEvidence(row)">凭证</el-button>
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
    
    <!-- 产量统计表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="yieldFormRef"
        :model="yieldForm"
        :rules="yieldRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划ID" prop="planId">
              <el-input-number v-model="yieldForm.planId" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域ID" prop="areaId">
              <el-input-number v-model="yieldForm.areaId" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品种ID" prop="breedId">
              <el-input-number v-model="yieldForm.breedId" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际产量(kg)" prop="actualYield">
              <el-input-number v-model="yieldForm.actualYield" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规格">
          <el-input v-model="yieldForm.specification" placeholder="例如：500-800g" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="统计日期" prop="statisticsDate">
              <el-date-picker 
                v-model="yieldForm.statisticsDate" 
                type="date" 
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="捕捞时间">
              <el-date-picker 
                v-model="yieldForm.catchTime" 
                type="datetime" 
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="负责人ID" prop="managerId">
          <el-input-number v-model="yieldForm.managerId" :min="1" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="产量统计审核"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="实际产量">
          <el-input v-model="currentYield.actualYield" disabled />
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input v-model="auditForm.auditOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 凭证查看对话框 -->
    <el-dialog
      v-model="evidenceDialogVisible"
      title="产量凭证"
      width="600px"
    >
      <el-table :data="evidenceList" border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="filePath" label="文件路径" min-width="200" />
        <el-table-column prop="fileType" label="文件类型" width="100" />
        <el-table-column prop="uploadTime" label="上传时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDeleteEvidence(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="evidenceDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getYieldList, saveYield, updateYield, deleteYield, auditYield } from '@/api/yield'
import { getEvidenceList, deleteEvidence } from '@/api/yieldEvidence'
import { useUserStore } from '@/stores/user'
import { formatDateTime, formatDate } from '@/utils/date'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const auditDialogVisible = ref(false)
const evidenceDialogVisible = ref(false)
const dialogTitle = ref('新增产量统计')
const isEdit = ref(false)
const yieldFormRef = ref(null)
const userStore = useUserStore()
const currentYield = ref({})
const evidenceList = ref([])

const searchForm = reactive({
  planId: null,
  areaId: null,
  breedId: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const yieldForm = reactive({
  yieldId: null,
  planId: null,
  areaId: null,
  breedId: null,
  actualYield: null,
  specification: '',
  statisticsDate: null,
  catchTime: null,
  managerId: null,
  status: 0,
  creatorId: null
})

const auditForm = reactive({
  yieldId: null,
  auditorId: null,
  auditOpinion: '',
  status: 1
})

const yieldRules = {
  planId: [
    { required: true, message: '请输入计划ID', trigger: 'blur' }
  ],
  areaId: [
    { required: true, message: '请输入区域ID', trigger: 'blur' }
  ],
  breedId: [
    { required: true, message: '请输入品种ID', trigger: 'blur' }
  ],
  actualYield: [
    { required: true, message: '请输入实际产量', trigger: 'blur' }
  ],
  statisticsDate: [
    { required: true, message: '请选择统计日期', trigger: 'change' }
  ],
  managerId: [
    { required: true, message: '请输入负责人ID', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getYieldList({
      current: pagination.current,
      size: pagination.size,
      planId: searchForm.planId || undefined,
      areaId: searchForm.areaId || undefined,
      breedId: searchForm.breedId || undefined,
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
  searchForm.planId = null
  searchForm.areaId = null
  searchForm.breedId = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增产量统计'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑产量统计'
  Object.assign(yieldForm, {
    yieldId: row.yieldId,
    planId: row.planId,
    areaId: row.areaId,
    breedId: row.breedId,
    actualYield: row.actualYield,
    specification: row.specification || '',
    statisticsDate: row.statisticsDate,
    catchTime: row.catchTime,
    managerId: row.managerId,
    status: row.status,
    creatorId: row.creatorId
  })
  dialogVisible.value = true
}

const handleAudit = (row) => {
  currentYield.value = { ...row }
  auditForm.yieldId = row.yieldId
  auditForm.auditorId = userStore.userInfo?.userId
  auditForm.auditOpinion = ''
  auditForm.status = 1
  auditDialogVisible.value = true
}

const handleViewEvidence = async (row) => {
  try {
    const res = await getEvidenceList(row.yieldId)
    if (res.code === 200) {
      evidenceList.value = res.data || []
      evidenceDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载凭证列表失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该产量统计吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteYield(row.yieldId)
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

const handleDeleteEvidence = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该凭证吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteEvidence(row.evidenceId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      handleViewEvidence({ yieldId: row.yieldId })
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!yieldFormRef.value) return
  await yieldFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        yieldForm.creatorId = userStore.userInfo?.userId
        let res
        if (isEdit.value) {
          res = await updateYield(yieldForm)
        } else {
          res = await saveYield(yieldForm)
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

const handleAuditSubmit = async () => {
  try {
    const res = await auditYield(auditForm)
    if (res.code === 200) {
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error(error.message || '审核失败')
  }
}

const resetForm = () => {
  Object.assign(yieldForm, {
    yieldId: null,
    planId: null,
    areaId: null,
    breedId: null,
    actualYield: null,
    specification: '',
    statisticsDate: null,
    catchTime: null,
    managerId: null,
    status: 0,
    creatorId: null
  })
  yieldFormRef.value?.clearValidate()
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
.yield-list {
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

