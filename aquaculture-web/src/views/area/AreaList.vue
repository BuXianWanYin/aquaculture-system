<template>
  <div class="area-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>养殖区域管理</span>
          <el-button v-if="hasPermission('area:add')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增区域
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="区域名称">
          <el-input v-model="searchForm.areaName" placeholder="请输入区域名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="在用" :value="1" />
            <el-option label="闲置" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="areaCode" label="区域编号" min-width="120" />
        <el-table-column prop="areaName" label="区域名称" min-width="150" />
        <el-table-column prop="areaSize" label="面积(㎡)" min-width="120" />
        <el-table-column prop="location" label="位置" min-width="200" />
        <el-table-column prop="waterQuality" label="水质类型" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在用' : '闲置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('area:edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="hasPermission('area:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            <span v-if="!hasPermission('area:edit') && !hasPermission('area:delete')" style="color: #909399;">无操作权限</span>
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
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="areaFormRef"
        :model="areaForm"
        :rules="areaRules"
        label-width="120px"
      >
        <el-form-item label="区域编号" prop="areaCode">
          <el-input v-model="areaForm.areaCode" />
        </el-form-item>
        <el-form-item label="区域名称" prop="areaName">
          <el-input v-model="areaForm.areaName" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="areaSize">
          <el-input-number v-model="areaForm.areaSize" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="areaForm.location" />
        </el-form-item>
        <el-form-item label="水质类型">
          <el-input v-model="areaForm.waterQuality" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="areaForm.status">
            <el-radio :label="1">在用</el-radio>
            <el-radio :label="0">闲置</el-radio>
          </el-radio-group>
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
import { getAreaList, saveArea, updateArea, deleteArea } from '@/api/area'
import { formatDateTime } from '@/utils/date'
import { usePermission } from '@/composables/usePermission'

const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增区域')
const isEdit = ref(false)
const areaFormRef = ref(null)

const searchForm = reactive({
  areaName: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const areaForm = reactive({
  areaId: null,
  areaCode: '',
  areaName: '',
  areaSize: null,
  departmentId: null,
  location: '',
  waterQuality: '',
  status: 1,
  managerId: null
})

const areaRules = {
  areaCode: [
    { required: true, message: '请输入区域编号', trigger: 'blur' }
  ],
  areaName: [
    { required: true, message: '请输入区域名称', trigger: 'blur' }
  ],
  areaSize: [
    { required: true, message: '请输入面积', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAreaList({
      current: pagination.current,
      size: pagination.size,
      areaName: searchForm.areaName || undefined,
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
  searchForm.areaName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增区域'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑区域'
  Object.assign(areaForm, {
    areaId: row.areaId,
    areaCode: row.areaCode,
    areaName: row.areaName,
    areaSize: row.areaSize,
    departmentId: row.departmentId,
    location: row.location || '',
    waterQuality: row.waterQuality || '',
    status: row.status,
    managerId: row.managerId
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该区域吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteArea(row.areaId)
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
  if (!areaFormRef.value) return
  await areaFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateArea(areaForm)
        } else {
          res = await saveArea(areaForm)
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
  Object.assign(areaForm, {
    areaId: null,
    areaCode: '',
    areaName: '',
    areaSize: null,
    departmentId: null,
    location: '',
    waterQuality: '',
    status: 1,
    managerId: null
  })
  areaFormRef.value?.clearValidate()
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
.area-list {
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

